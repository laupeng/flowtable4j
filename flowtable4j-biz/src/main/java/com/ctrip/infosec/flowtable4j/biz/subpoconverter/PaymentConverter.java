package com.ctrip.infosec.flowtable4j.biz.subpoconverter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.biz.processor.Crypto;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.CtripSubOrderType;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class PaymentConverter extends ConverterBase {
    private static List<String> foreignCardType = Arrays.asList(new String[]{"6", "7", "8", "9", "10"});
    private Logger logger = LoggerFactory.getLogger(PaymentConverter.class);
    /**
     * 填充 支付信息
     *
     * @param eventBody
     * @param paymentinfo
     */
    public void fillPaymentInfo(Map<String, Object> eventBody, Map<String, Object> paymentinfo, int orderType,int checkType,int subOrderType) {

        //fill PaymentMainInfo
        setValue(paymentinfo, "paymentmaininfo", copyMap(eventBody,new String[]{"bankvalidationmethod","clientidorip","clientos","deducttype","isprepaid","paymethod","payvalidationmethod","validationfailsreason"}));

        List<Map<String, Object>> paymentInfoList = createList();
        List<Map<String, Object>> paymentInfoListSrc;

        paymentInfoListSrc = getList(eventBody, "paymentinfos");

        //大部分的Payment节点为Paymentinfos
        if(checkType==0) {
            if ( orderType == CtripOrderType.Hotel.getCode()
                    || orderType == CtripOrderType.HotelEBK.getCode()
                    || orderType == CtripOrderType.YongAnHotel.getCode() ) {
                paymentInfoListSrc = getList(eventBody, "listpaymenitems");
            }
            if (orderType == CtripOrderType.JiFen.getCode()) {
                paymentInfoListSrc = getList(eventBody, "paymentinfobyjifenlist");
            }
            if (orderType == CtripOrderType.TopShop.getCode()) {
                paymentInfoListSrc = getList(eventBody, "topshoppaymentinfolist");
            }
        }
        if (paymentInfoListSrc == null || paymentInfoListSrc.size() == 0) {
            if (orderType == CtripOrderType.Flights.getCode()
                    ||  subOrderType== CtripSubOrderType.DistributionFlight.getCode()
                    ||  subOrderType==CtripSubOrderType.TaoPanChan.getCode()
                    ||  orderType== CtripOrderType.TravelMoney.getCode() && subOrderType==0) {  //机票有可能支付信息在eventBody中
                findPaymentInfo(paymentInfoList, eventBody, orderType,subOrderType);
            }
        } else {
            for (Map<String, Object> paymentSrc : paymentInfoListSrc) {
                findPaymentInfo(paymentInfoList, paymentSrc, orderType,subOrderType);
            }
        }
        setValue(paymentinfo, "paymentinfolist", paymentInfoList);
    }

    private static List<Integer> requireIdCity = Arrays.asList(new Integer[]{CtripOrderType.Hotel.getCode(), CtripOrderType.TTD.getCode(), CtripOrderType.Flights.getCode()});
    private static List<Integer> requireBankCity = Arrays.asList(new Integer[]{CtripOrderType.CRH.getCode(),CtripOrderType.Hotel.getCode(),
            CtripOrderType.TTD.getCode(),CtripOrderType.Flights.getCode()});

    private void findPaymentInfo(List<Map<String, Object>> paymentInfoList, Map<String, Object> paymentSrc, int orderType,int subOrderType) {
        Map<String, Object> paymentInfo = createMap();

        setValue(paymentInfo, "payment", copyMap(paymentSrc,"infosecurity_paymentinfo"));

        String prepayType = getString(paymentSrc, "prepaytype", "").toUpperCase();
        String isForigin = null;

        List<Map<String, Object>> cardInfoList = createList();
        setValue(paymentInfo, "cardinfolist", cardInfoList);

        if ("CCARD".equals(prepayType) || "DCARD".equals(prepayType) || "DQPAY".equals(prepayType)) {
            Map<String, Object> cardInfo = createMap();
            Map<String, Object> creditcardinfoMap = getMap(paymentSrc, "creditcardinfo");
            if (creditcardinfoMap != null) {
                //有CreditCardInfo节点
                copyMap(creditcardinfoMap,cardInfo,"infosecurity_cardinfo");
            } else if(CtripOrderType.Flights.getCode()== orderType || CtripOrderType.TravelMoney.getCode()==orderType && subOrderType==0) {
                //没有CreditCardInfo节点，机票
                copyMap(paymentSrc,cardInfo,"infosecurity_cardinfo");
            }

            //老的支付先前做一次转换，把 cardInfoId提取到外面
            String cardInfoId = getString(creditcardinfoMap,"cardinfoid", "");
            if (Strings.isNullOrEmpty(cardInfoId) || cardInfoId.equals("0")) {
                cardInfoId = getString(paymentSrc, "cardinfoid", "");
            }
            Long cardId = 0L;
            if (StringUtils.isNumeric(cardInfoId)) {
                cardId = Long.parseLong(cardInfoId);
            }

            //调用接口获取信用卡信息
            if (cardId > 0) {
                Map<String, Object> cardInfoResult = (Map<String, Object>) esbClient.getCardInfo(cardInfoId);//从esb取出相关数据
                if (cardInfoResult != null && cardInfoResult.size() > 0) {
                    copyMap(cardInfoResult, cardInfo, "infosecurity_cardinfo");
                    copyValue(cardInfoResult, "isforeigncard", cardInfo, "isforigencard");
                    copyValueIfNotNull(cardInfoResult, "cardrisknolastcode", cardInfo, "ccardlastnocode");
                    copyValueIfNotNull(cardInfoResult, "cardrisknoprecode", cardInfo, "ccardprenocode");
                    setValue(cardInfo, "infoid", 0);
                    //中国公民，取省份
                    isForigin = getString(cardInfoResult, "isforigencard");
                    if (requireIdCity.contains(orderType) && "1".equals(getString(cardInfoResult, "idcardtype")) && "F".equals(isForigin)) {
                        Map<String, Object> id = checkRiskDAO.getIDCardProvince(getString(cardInfoResult, "idnumber"));
                        if (id != null && id.size() > 0) {
                            setValue(cardInfo, "idnumberprovince", getString(id, "provincename"));
                            setValue(cardInfo, "idnumbercity", getString(id, "cityname"));
                        }
                    }
                }
            }

            setValue(cardInfo,"cardinfoid",cardId);
            setValue(cardInfo,"infoid",0);

            //如果是外卡，获取卡发行组织、银行
            String creditCardType = getString(cardInfo, "creditcardtype", "0");
            String cardBin = getString(cardInfo, "cardbin");
            if ("T".equals(isForigin) || foreignCardType.contains(creditCardType)) { //判断外卡标志 isForgien 或者 6，7，8，9，10
                Map<String, Object> subCardInfo = checkRiskDAO.getForeignCardInfo(creditCardType, cardBin);
                if (subCardInfo != null && subCardInfo.size() > 0) {
                    setValue(cardInfo, "cardbinissue", getString(subCardInfo, "nationality"));
                    setValue(cardInfo, "cardbinbankofcardissue", getString(subCardInfo, "bankofcardissue"));
                }
            }
            //获取发卡银行城市、省份信息
            if (requireBankCity.contains(orderType)) {
                //取出branchCity 和 branchProvince
                String creditCardNumber = getString(cardInfo, "creditcardnumber");
                if ("3".equals(creditCardType) && !Strings.isNullOrEmpty(creditCardNumber))//这里只针对类型为3的卡进行处理
                {
                    String decryptText = null;
                    try {
                        decryptText = Crypto.decrypt(creditCardNumber);
                    } catch (Exception exp) {
                        logger.warn("解密卡号异常" + exp.getMessage());
                    }
                    if (!Strings.isNullOrEmpty(decryptText) && decryptText.length() > 12) {
                        String branchNo = decryptText.substring(6, 9);
                        if (!branchNo.isEmpty()) {
                            Map cardBankInfo = checkRiskDAO.getCardBankInfo(creditCardType, branchNo);
                            if (cardBankInfo != null) {
                                setValue(cardInfo, "branchcity", getString(cardBankInfo, "branchcity"));
                                setValue(cardInfo, "branchprovince", getString(cardBankInfo, "branchprovince"));
                            }
                        }
                    }
                }
            }
            cardInfoList.add(cardInfo);
        }
        paymentInfoList.add(paymentInfo);
    }

}

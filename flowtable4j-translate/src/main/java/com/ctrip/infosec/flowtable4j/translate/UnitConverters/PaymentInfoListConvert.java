package com.ctrip.infosec.flowtable4j.translate.UnitConverters;

import com.ctrip.infosec.flowtable4j.translate.dao.CommonSources;
import com.ctrip.infosec.flowtable4j.translate.dao.CommonWriteSources;
import com.ctrip.infosec.flowtable4j.translate.dao.Crypto;
import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.service.CommonOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValueMap;

/**
 * Created by lpxie on 15-6-10.
 */
public class PaymentInfoListConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(PaymentInfoListConvert.class);

    @Resource(name="allTemplates")
    private AllTemplates allTemplates;

    JdbcTemplate cardRiskDBTemplate = null;
    JdbcTemplate riskCtrlPreProcDBTemplate = null;
    JdbcTemplate cUSRATDBTemplate = null;

    @Autowired
    CommonOperation commonOperation;
    @Autowired
    CommonSources commonSources;
    @Autowired
    CommonWriteSources commonWriteSources;

    /**
     * 初始化jndi
     */
    private void init()
    {
        cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
        cUSRATDBTemplate = allTemplates.getcUSRATDBTemplate();
    }

    @Override
    public void completeData(DataFact dataFact, Map data)
    {
        //这里分checkType是1和0、2的情况
        String checkType = getValue(data, Common.CheckType);
        if(checkType.equals("0") || checkType.equals("2"))
        {
            List<Map> paymentInfos = (List<Map>)data.get(Common.PaymentInfos);//这里在转换的时候注意是否需要转换成Json格式
            if(paymentInfos == null || paymentInfos.size()<1)
                return;
            for(Map payment : paymentInfos)
            {
                Map<String,Object> subPaymentInfoList = new HashMap<String, Object>();

                Map<String,Object> PaymentInfo = new HashMap();
                List<Map> CardInfoList = new ArrayList<Map>();

                Map<String,Object> cardInfo = new HashMap<String, Object>();

                String prepayType = getValue(payment,Common.PrepayType);
                PaymentInfo.put(Common.PrepayType, prepayType);
                PaymentInfo.put(Common.Amount,getValue(payment, Common.Amount));
                if(prepayType.toUpperCase().equals("CCARD") || prepayType.toUpperCase().equals("DCARD"))
                {
                    cardInfo.put(Common.CardInfoID,getValue(payment, Common.CardInfoID));
                    cardInfo.put(Common.InfoID,"0");

                    ///从wsdl里面获取卡信息
                    String cardInfoId = getValue(payment,Common.CardInfoID);
                    if(cardInfoId.isEmpty())
                        continue;
                    Map cardInfoResult = commonOperation.getCardInfo(cardInfoId);//从esb取出相关数据
                    if(cardInfoResult != null && cardInfoResult.size()>0)
                    {
                        cardInfo.put(Common.BillingAddress,getValue(cardInfoResult,Common.BillingAddress));
                        cardInfo.put(Common.CardBin,getValue(cardInfoResult,Common.CardBin));
                        cardInfo.put(Common.CardHolder,getValue(cardInfoResult,Common.CardHolder));
                        cardInfo.put(Common.CCardLastNoCode,getValue(cardInfoResult,"CardRiskNoLastCode"));

                        cardInfo.put(Common.CCardNoCode,getValue(cardInfoResult,Common.CCardNoCode));

                        cardInfo.put("CardNoRefID",getValue(cardInfoResult,"CardNoRefID"));

                        cardInfo.put(Common.CCardPreNoCode,getValue(cardInfoResult,"CardRiskNoPreCode"));
                        cardInfo.put(Common.CreditCardType,getValue(cardInfoResult,Common.CreditCardType));

                        cardInfo.put(Common.CValidityCode,getValue(cardInfoResult,Common.CValidityCode));
                        cardInfo.put(Common.IsForigenCard,getValue(cardInfoResult,Common.IsForeignCard));
                        cardInfo.put(Common.Nationality,getValue(cardInfoResult,Common.Nationality));

                        cardInfo.put(Common.Nationalityofisuue,getValue(cardInfoResult,Common.Nationalityofisuue));
                        cardInfo.put(Common.BankOfCardIssue,getValue(cardInfoResult,Common.BankOfCardIssue));
                        cardInfo.put(Common.StateName,getValue(cardInfoResult,Common.StateName));
                        cardInfo.put("CardNoRefID",getValue(cardInfoResult,"CardNoRefID"));
                    }
                    //取出branchCity 和 branchProvince
                    String creditCardType = getValue(cardInfoResult,Common.CreditCardType);
                    String creditCardNumber = getValue(cardInfoResult,"CreditCardNumber");
                    if(creditCardType.equals("3") && !creditCardNumber.isEmpty())//这里只针对类型为3的卡进行处理
                    {
                        String decryptText = null;
                        try
                        {
                            decryptText = Crypto.decrypt(creditCardNumber);
                        }catch (Exception exp)
                        {
                            logger.warn("解密卡号异常"+exp.getMessage());
                        }
                        if(decryptText !=null && !decryptText.isEmpty()&&decryptText.length()>12)
                        {
                            String branchNo = decryptText.substring(6,9);
                            if(!branchNo.isEmpty())
                            {
                                Map cardBankInfo = commonSources.getInfo(creditCardType,branchNo);
                                if(cardBankInfo != null)
                                {
                                    cardInfo.put("BranchCity",getValue(cardBankInfo,"BranchCity"));
                                    cardInfo.put("BranchProvince",getValue(cardBankInfo,"BranchProvince"));
                                }
                            }
                        }
                    }
                    //通过卡种和卡BIN获取系统中维护的信用卡信息
                    String cardTypeId = getValue(cardInfoResult,Common.CreditCardType);
                    String cardBin = getValue(cardInfoResult,Common.CardBin);
                    Map subCardInfo = commonSources.getCardInfo(cardTypeId,cardBin);
                    if(subCardInfo != null && subCardInfo.size()>0)
                    {
                        cardInfo.put(Common.CardBinIssue,getValue(subCardInfo,"Nationality"));
                        cardInfo.put(Common.CardBinBankOfCardIssue,getValue(subCardInfo,"BankOfCardIssue"));
                    }
                    CardInfoList.add(cardInfo);
                }
                subPaymentInfoList.put(Common.PaymentInfo,PaymentInfo);
                subPaymentInfoList.put(Common.CardInfoList,CardInfoList);
                dataFact.paymentInfoList.add(subPaymentInfoList);
            }
        }else if(checkType.equals("1"))
        {
            final String lastReq = getValue(data,Common.OldReqID);
            List<Map<String, Object>> paymentInfos = commonSources.getListPaymentInfo(lastReq);
            if(paymentInfos == null || paymentInfos.size()<1)
                return;
            for(Map payment : paymentInfos)
            {
                Map subPayInfo = new HashMap();
                subPayInfo.put(Common.PaymentInfo,payment);
                String paymentInfoId = getValue(payment,"PaymentInfoID");
                subPayInfo.put(Common.CardInfoList, commonSources.getListCardInfo(paymentInfoId));
                dataFact.paymentInfoList.add(subPayInfo);
            }
        }
    }

    @Override
    public void writeData(DataFact dataFact, String reqId, boolean isWrite, boolean isCheck)
    {
        for (int i = 0; i < dataFact.paymentInfoList.size(); i++)
        {
            Map<String, Object> paymentInfo = dataFact.paymentInfoList.get(i);
            final String paymentInfoID = commonWriteSources.insertPaymentInfo(getValueMap(paymentInfo, Common.PaymentInfo), reqId, isWrite, isCheck);
            List<Map<String, Object>> cardInfos = (List<Map<String, Object>>) paymentInfo.get(Common.CardInfoList);
            for (int j = 0; j < cardInfos.size(); j++)
            {
                commonWriteSources.insertCardInfo(cardInfos.get(j), reqId, paymentInfoID, isWrite, isCheck);
            }
        }
    }
}

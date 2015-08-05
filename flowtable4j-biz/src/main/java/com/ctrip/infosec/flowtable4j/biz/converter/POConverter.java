package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.processor.InvalidOrderException;
import com.ctrip.infosec.flowtable4j.biz.subpoconverter.*;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求报文转换为PO Entity
 * PO对外访问方式为Map
 * 内部操作模式为对象类
 * Created by thyang on 2015-06-10.
 */
@Component
public class POConverter extends POConvertBase {

    @Autowired
    POConverterEx poConverterEx;

    @Autowired
    MainInfoConverter mainInfoConverter;

    @Autowired
    DIYConverter diyConverter;

    @Autowired
    PaymentConverter paymentConverter;

    @Autowired
    JiFenConverter jiFenConverter;

    @Autowired
    TraveMoneyConverter traveMoneyConverter;

    @Autowired
    TopShopConverter topShopConverter;
    /**
     * 核心过程，Request 转 PO
     *
     * @param requestBody
     * @return
     */
    private List<Integer> skipZeroOrderTypes=Arrays.asList(new Integer[]{CtripOrderType.BindingCard.getCode(),
            CtripOrderType.Coupons.getCode(),
            CtripOrderType.Marketing.getCode(),
            CtripOrderType.TieYou.getCode(),
            CtripOrderType.YongAnFlight.getCode()});

    private List<Integer> noContactInfos= Arrays.asList(new Integer[]{CtripOrderType.BindingCard.getCode(),
            CtripOrderType.CurrencyExchange.getCode(),
            CtripOrderType.Insurer.getCode(),
            CtripOrderType.Wallet.getCode()});

    private List<Integer> nopaymentInfos= Arrays.asList(new Integer[]{CtripOrderType.BindingCard.getCode(),
            CtripOrderType.Coupons.getCode(),
            CtripOrderType.CurrencyExchange.getCode()});

    public PO convert(RequestBody requestBody) {
        PO po = new PO();
        Map<String, Object> eventBody = requestBody.getEventBody();

        String orderId = getString(eventBody, "orderid");
        String orderType = getString(eventBody, "ordertype");
        String merchantOrderId = getString(eventBody, "merchantorderid");
        String checkType = getString(eventBody, "checktype");
        String suborderType = getString(eventBody, "subordertype");
        String reqId= getString(eventBody,"reqid");
        po.setPrepaytype(getString(eventBody, "orderprepaytype"));

        if (StringUtils.isNumeric(orderId)) {
            po.setOrderid(Long.parseLong(orderId));
        }
        if (StringUtils.isNumeric(orderType)) {
            po.setOrdertype(Integer.parseInt(orderType));
        }
        if (StringUtils.isNumeric(suborderType)) {
            po.setSubordertype(Integer.parseInt(suborderType));
        }
        if (StringUtils.isNumeric(checkType)) {
            po.setChecktype(Integer.parseInt(checkType));
        }
        if(StringUtils.isNumeric(reqId)){
            po.setReqid(Long.parseLong(reqId));
        }

        //绑定卡片，只有CheckType=1
        if(po.getOrdertype()== CtripOrderType.BindingCard.getCode()){
            checkType="1";
            po.setChecktype(1);
        }

        if(po.getOrdertype()== CtripOrderType.Coupons.getCode()
                || po.getOrdertype()==CtripOrderType.HHTravel.getCode()
                || po.getOrdertype()==CtripOrderType.YongChe.getCode()){
            checkType="0";
            po.setChecktype(0);
        }

        List<String> modules = mainInfoConverter.getModules(po.getOrdertype(), po.getSubordertype(),po.getChecktype());

        if (po.getOrderid().equals(0) && !skipZeroOrderTypes.contains(po.getOrdertype())) {
            throw new InvalidOrderException("ORDERID IS ZERO");
        }

        Map<String, Object> paymentInfo = null;
        Map<String, Object> productInfo = null;
        Map<String, Object> tmpPay = null;
        Map<String, Object> tmpProduct = null;

        if ("1".equals(checkType)) {
            //没有支付信息直接忽略
            if(!nopaymentInfos.contains(po.getOrdertype())) {
                tmpPay = checkRiskDAO.getLastPaymentInfo(orderId, orderType, merchantOrderId);
            }
        } else if ("2".equals(checkType)) {
            tmpProduct = checkRiskDAO.getLastProductInfo(orderId, orderType, merchantOrderId);
        }

        if (tmpProduct != null) {
            productInfo = mapper.fromJson(getString(tmpProduct, "content"), HashMap.class);
            po.setProductinfo(productInfo);
            Map<String, Object> mainInfo = getMap(productInfo, "maininfo");
            if (mainInfo != null) {
                setValue(mainInfo, "checktype", po.getChecktype());
            }
        } else {
            productInfo = new HashMap<String, Object>();
            po.setProductinfo(productInfo);
        }

        if (tmpPay != null) {
            paymentInfo = mapper.fromJson(getString(tmpPay, "content"), HashMap.class);
            po.setPaymentinfo(paymentInfo);
            if (Strings.isNullOrEmpty(po.getPrepaytype())) {
                po.setPrepaytype(getString(tmpPay, "prepaytype"));
            }
        } else {
            paymentInfo = new HashMap<String, Object>();
            po.setPaymentinfo(paymentInfo);
        }

        // 处理 Email、UserIp、MobilePhone.TelNo,ContactTel等非法
        validateData(eventBody);
        if (po.getOrdertype() == CtripOrderType.JiFen.getCode()) {
            validateData(getMap(eventBody, "contactview"));
            validateData(getMap(eventBody, "deliveryinfoview"));
        }


        // fill DealInfo
        Map<String, Object> dealInfo = new HashMap<String, Object>();
        setValue(dealInfo, "checkstatus", 0);
        setValue(dealInfo, "referenceid", getString(eventBody, "referenceno"));
        setValue(productInfo, "dealinfo", dealInfo);

        //订单校验
        if (checkType.equals("1") || checkType.equals("0")) {     //订单校验，补充上次的支付信息

            //fill MainInfo
            mainInfoConverter.fillMainInfo(eventBody,po);

            //fill User Info Info，CusCharacter、BindEmail etc.
            fillUserInfo(eventBody, productInfo, getString(eventBody, "uid"), po.getOrdertype());

            //fill IP Info, IpCity,Ip Continent
            fillIPInfo(productInfo, getString(eventBody, "userip"));

            //fill contactInfo && Mobilephone City
            //没有联系人信息，直接略过
            if(! noContactInfos.contains(po.getOrdertype())) {
                fillContactInfo(eventBody, productInfo, po.getOrdertype());
            }
            if (modules.contains("corporation")) {
                setValue(productInfo,"corporation",copyMap(eventBody,new String[]{"canaccountpay","companytype","corp_paytype","corpid","corpname"}));
            }
            if (modules.contains("proposer")) {
                setValue(productInfo,"proposer",copyMap(eventBody,"infosecurity_proposerinfo"));
            }
            if (modules.contains("appinfo")) {
                setValue(productInfo,"appinfo",copyMap(eventBody,new String[]{"clientid","clientversion","latitude","longitude"}));
            }
            if (modules.contains("hotelgroupinfolist")) {
                poConverterEx.fillHotelGroupInfoList(productInfo, eventBody);
            }
            if (modules.contains("hotelinfolist")) {
                poConverterEx.fillHotelInfoList(productInfo, eventBody);
            }
            if (modules.contains("railinfolist")) {
                poConverterEx.fillRailInfoList(productInfo, eventBody,po.getOrdertype());
            }
            if (modules.contains("flightinfolist")) {
                poConverterEx.fillFlightInfoList(productInfo, eventBody);
            }
            if (modules.contains("vacationinfolist")) {
                traveMoneyConverter.fillVacationInfoList(productInfo, eventBody, po.getOrdertype());
            }
            if(modules.contains("invoiceinfolist")){
                traveMoneyConverter.fillInvoiceInfoList(productInfo,eventBody,po.getOrdertype(),po.getSubordertype());
            }
            if(modules.contains("fncmalllist")){
                traveMoneyConverter.fillFNCMallList(productInfo,eventBody);
            }
            if (modules.contains("diy")) {
                diyConverter.fillDIYFlightInfoList(productInfo, eventBody);
                diyConverter.fillDIYVacationInfoList(productInfo, eventBody);
                setValue(productInfo, "hotelinfolist", copyList(getList(eventBody, "diyhotelsinfos"),new String[]{"orderid", "amount", "prepaytype"}));
                setValue(productInfo, "hotelotherinfolist",copyList(getList(eventBody,"diyhotelsinfos"),"infosecurity_hotelinfo"));
                setValue(productInfo, "diyresourcexlist", copyList(getList(eventBody,"diyresourcexinfos"),"infosecurity_diyresourcexinfo"));
            }
            if (modules.contains("giftitemlist")) {
                setValue(productInfo, "giftitemlist", copyList(getList(eventBody,"giftlist"),"infosecurity_giftitem"));
            }
            if (modules.contains("devoteinfoviewbyjifen")) {
                setValue(productInfo, "devoteinfoviewbyjifen",copyMap(getMap(eventBody,"devoterinfoview"),"infosecurity_devoterinfoviewbyjifen"));
            }
            if (modules.contains("distribution")) {
                jiFenConverter.fillDistribution(productInfo, eventBody, po.getOrdertype(),po.getSubordertype());
            }
            if (modules.contains("jifenorderitemlist")) {
                jiFenConverter.fillJifenOrderItemList(productInfo, eventBody);
            }
            if(modules.contains("walletwithdrawal")){
                setValue(productInfo, "walletwithdrawal", copyMap(eventBody,"infosecurity_walletwithdrawal"));
            }
            if(modules.contains("chproduct")){
                setValue(productInfo,"chproduct",copyMap(eventBody, "infosecurity_chproduct"));
            }

            if(modules.contains("coupons")){
                setValue(productInfo, "coupons", copyMap(eventBody, "infosecurity_couponsinfo"));
            }

            if(modules.contains("customer")){
                setValue(productInfo,"customer",copyMap(eventBody,new String[]{"customerusername","customeremail","customermobile"}));
            }

            if(modules.contains("smsverify")){
                setValue(productInfo, "smsverify", copyMap(eventBody, new String[]{"smsverifystatus"}));
            }
            if(modules.contains("tianhai")){
                setValue(productInfo, "tianhai", copyMap(eventBody, "infosecurity_vacationbytianhaiinfo"));
            }
            if(modules.contains("currencyexchange")){
                poConverterEx.fillCurrencyExchange(productInfo, eventBody);
            }
            if(modules.contains("insureinfolist")){
                setValue(productInfo, "insureinfolist", copyList(getList(eventBody, "insuredinfolist"), "infosecurity_insuredinfo"));
            }
            if(modules.contains("marketing")){
                setValue(productInfo, "marketing", copyMap(eventBody, "infosecurity_marketinginfo"));
            }
            if(modules.contains("marketdata")){
                poConverterEx.fillMarketData(productInfo, eventBody);
            }
            if(modules.contains("miceinfo")){
                setValue(productInfo, "miceinfo", copyMap(eventBody,"infosecurity_miceinfo"));
            }

            if(modules.contains("travemoneyretailer")){
                //BindMobileNo,AllianceID,SID,AvailAmount,DiscountRateRWX,DiscountRateRWY,RetailerType
                setValue(productInfo,"travelmoneyretailer",copyMap(eventBody,"infosecurity_travelmoneyretailerinfo"));
            }
            if(modules.contains("distributioncompany")){
                setValue(productInfo,"distributioncompany",copyMap(getMap(eventBody,"distributioncompanyitem"),new String[]{"flowcompanyname","flowremark"}));
            }
            if(modules.contains("travelmoneyproductlist")){
                setValue(productInfo, "travelmoneyproductlist", copyList(getList(eventBody, "travelmoneyproductitems"), "infosecurity_travelmoneyproductinfo"));
            }
            if(modules.contains("rechargesuborderlist")){
                 setValue(productInfo, "rechargesuborderlist", copyList(getList(eventBody, "rechargesuborderinfolist"), "infosecurity_rechargesuborderinfo"));
            }

            if(modules.contains("goodslist")){
                traveMoneyConverter.fillGoodsList(productInfo,eventBody);
            }
            if(modules.contains("fncmallorder")){
                setValue(productInfo,"fncmallorder",copyMap(getMap(eventBody,"fncmallorderinfo"),"infosecurity_fncmallorderinfo"));
            }
            if(modules.contains("invoiceinfo")){
                setValue(productInfo,"invoiceinfo",copyMap(eventBody,"infosecurity_invoiceinfo"));
            }
            if(modules.contains("passenger")){
                setValue(productInfo,"passenger",copyMap(eventBody, ImmutableMap.of("cardendtime","cardendtime","cardstarttime","cardstarttime",
                        "proposercardno","passengercardid","proposercardtype","passengercardidtype")));
            }
            if(modules.contains("fundcertificate")){
                setValue(productInfo,"fundcertificate",copyMap(eventBody,"infosecurity_fundcertificateinfo"));
            }
            if(modules.contains("travelmoneyproductplus")){
                setValue(productInfo,"travelmoneyproductplus",copyMap(eventBody,"infosecurity_travelmoneyproductinfoplus"));
            }
            if(modules.contains("employee")){
                setValue(productInfo,"employee",copyMap(eventBody,new String[]{"eid","eidip"}));
                setValue(getMap(productInfo,"employee"),"eidipvalue",ipConvertToValue(getString(eventBody,"eidip")));
            }
            if(modules.contains("vacationproduct")){
                setValue(productInfo,"vacationproduct",copyMap(getMap(eventBody,"vacationproductinfos"),"infosecurity_vacationproductinfo"));
            }
            if(modules.contains("gps")){
                setValue(productInfo, "gps", copyMap(eventBody, new String[]{"latitude","longitude"}));
            }
            if(modules.contains("yongche")){
                setValue(productInfo,"yongche",copyMap(eventBody,"infosecurity_yongcheinfo"));
            }
            if(modules.contains("topshoporderlist")){
                topShopConverter.fillTopShopOrderList(productInfo,eventBody);
            }
            if(modules.contains("topshopcatalog")){
                topShopConverter.fillTopShopCatalog(productInfo,eventBody);
            }
            //fill Other Info
            fillOtherInfo(po, eventBody);

        } else if (checkType.equals("2") || checkType.equals("0")) { //支付校验，补充订单信息
            if(!nopaymentInfos.contains(po.getOrdertype())) {
                //fill PaymentInfoList
                paymentConverter.fillPaymentInfo(eventBody,paymentInfo,po.getOrdertype(),po.getChecktype(),po.getSubordertype());
            }
            if (modules.contains("orderccard")) {
                poConverterEx.fillAuthCCardInfo(productInfo, paymentInfo);
            }
        }

        if (modules.contains("tddspecial")) {
            //处理 isprepaid标志
            poConverterEx.tddSpecial(po, eventBody);
        }
        if(modules.contains("vacationspecial")){
            poConverterEx.vacationSpecail(po,eventBody);
        }

        if (modules.contains("hotelspecial")) {
            //处理 isprepaid标志
            poConverterEx.hotelSpecial(po, eventBody);
        }

        if (modules.contains("flightspecial")) {
            poConverterEx.flightSpecial(po, eventBody);
        }

        if (modules.contains("fillprofit")) {
            poConverterEx.fillFligtProfit(productInfo, paymentInfo, po.getChecktype());
        }

        if(modules.contains("hotelebkspecial")){
            //处理 isprepaid标志
            poConverterEx.fillHotelEBKSpecial(po,eventBody);
        }

        //BindingCard没有支付信息
        if (Strings.isNullOrEmpty(po.getPrepaytype()) && !nopaymentInfos.contains(po.getOrdertype())) {
            po.setPrepaytype(getPrepayType(paymentInfo));
        }

        if (!po.getOrdertype().equals(CtripOrderType.BindingCard.getCode())) {
            fillDIDInfo(productInfo, orderId, orderType);
        }
        return po;
    }

    public void saveData4Next(PO po) {
        int checkType = po.getChecktype();
        int orderType = po.getOrdertype();
        if (checkType <= 1 && po.getProductinfo() != null) {
            checkRiskDAO.saveLastProductInfo(po.getOrderid(), po.getOrdertype(), po.getMerchantid(), po.getProductinfo());
        }

        //TDD, HotelGroup的CheckType=1 带 PaymentMainInfo的 PrepaId信息，
        if (checkType == 1 && (orderType == CtripOrderType.TTD.getCode() || orderType == CtripOrderType.Hotel.getCode())) {
            if (po.getPaymentinfo() != null) {
                checkRiskDAO.saveLastPaymentInfo(po.getOrderid(), po.getOrdertype(), po.getMerchantid(), po.getPrepaytype(), po.getPaymentinfo());
            }
        }

        if (checkType != 1 && po.getPaymentinfo() != null) {
            checkRiskDAO.saveLastPaymentInfo(po.getOrderid(), po.getOrdertype(), po.getMerchantid(), po.getPrepaytype(), po.getPaymentinfo());
        }
    }

}

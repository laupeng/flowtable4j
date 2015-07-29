package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.processor.InvalidOrderException;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    /**
     * 业务选择器
     *
     * @param orderType
     * @return
     */
    public List<String> getModules(int orderType) {
        if (orderType == CtripOrderType.HotelGroup.getCode()) {
            return Arrays.asList(new String[]{"hotelgroupinfolist", "vacationinfolist"});
        }
        if (orderType == CtripOrderType.Flights.getCode()) {
            //CheckType 0,1,2
            return Arrays.asList(new String[]{"flightinfolist", "corporation", "appinfo", "orderccard", "fillprofit", "flightspecial"});
        }
        if (orderType == CtripOrderType.CRH.getCode()) {
            return Arrays.asList(new String[]{"railinfolist", "corporation", "distribution", "vacationinfolist"});
        }

        if (orderType == CtripOrderType.TTD.getCode()) {
            return Arrays.asList(new String[]{"vacationinfolist", "tddspecial"});
        }

        if (orderType == CtripOrderType.Hotel.getCode()) {
            return Arrays.asList(new String[]{"hotelinfolist", "giftitemlist", "corporation", "hotelspecial"});
        }

        if (orderType == CtripOrderType.JiFen.getCode()) {
            return Arrays.asList(new String[]{"devoteinfoviewbyjifen", "jifenorderitemlist", "distribution",});
        }
        if (orderType == CtripOrderType.DIY.getCode()) {
            return Arrays.asList(new String[]{"diy"});
        }

        return new ArrayList<String>();
    }

    /**
     * 核心过程，Request 转 PO
     *
     * @param requestBody
     * @return
     */
    public PO convert(RequestBody requestBody) {
        PO po = new PO();
        Map<String, Object> eventBody = requestBody.getEventBody();

        String orderId = getString(eventBody, "orderid");
        String orderType = getString(eventBody, "ordertype");
        String merchantOrderId = getString(eventBody, "merchantorderid");
        String checkType = getString(eventBody, "checktype");
        String suborderType = getString(eventBody, "subordertype");

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

        List<String> modules = getModules(po.getOrdertype());

        if (po.getOrderid().equals(0)) {
            throw new InvalidOrderException("ORDERID IS ZERO");
        }

        Map<String, Object> paymentInfo = null;
        Map<String, Object> productInfo = null;
        Map<String, Object> tmpPay = null;
        Map<String, Object> tmpProduct = null;

        if ("1".equals(checkType)) {
            tmpPay = checkRiskDAO.getLastPaymentInfo(orderId, orderType, merchantOrderId);
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
            Map<String, Object> mainInfo = new HashMap<String, Object>();
            copyMap(requestBody.getEventBody(), mainInfo, "infosecurity_maininfo");
            setValue(mainInfo, "lastcheck", "T");
            setValue(mainInfo, "ordertype", orderType);
            setValue(mainInfo, "createdate", sdf.format(System.currentTimeMillis()));
            setValue(mainInfo, "corporationid", "");
            if (po.getOrdertype() == CtripOrderType.CRH.getCode() || po.getOrdertype() == CtripOrderType.Flights.getCode()
                ||po.getOrdertype() == CtripOrderType.Hotel.getCode() ) {
                setValue(mainInfo, "corporationid", getObject(eventBody, "corporationid"));
            }
            if (po.getOrdertype() != CtripOrderType.Flights.getCode()) {
                setValue(mainInfo, "amount", getString(eventBody, "orderamount"));
            }
            if (po.getOrdertype() == CtripOrderType.TTD.getCode()
                    || po.getChecktype() == CtripOrderType.DIY.getCode()
                    || po.getOrdertype() == CtripOrderType.Hotel.getCode()) {
                String refNo = getString(eventBody, "referenceno");
                if (StringUtils.isNumeric(refNo)) {
                    setValue(mainInfo, "refno", refNo);
                }
            }
            setValue(productInfo, "maininfo", mainInfo);


            //fill User Info Info，CusCharacter、BindEmail etc.
            fillUserInfo(eventBody, productInfo, getString(eventBody, "uid"), po.getOrdertype());

            //fill IP Info, IpCity,Ip Continent
            fillIPInfo(productInfo, getString(eventBody, "userip"));

            //fill contactInfo && Mobilephone City
            fillContactInfo(eventBody, productInfo, po.getOrdertype());

            //fill Corporation
            if (modules.contains("corporation")) {
                Map<String, Object> corporationInfo = new HashMap<String, Object>();
                copyMap(eventBody, corporationInfo, "infosecurity_corporationinfo");
                setValue(productInfo, "corporation", corporationInfo);
            }

            //fill AppInfo
            if (modules.contains("appinfo")) {
                Map<String, Object> appInfo = new HashMap<String, Object>();
                copyMap(eventBody, appInfo, "infosecurity_appinfo");
                setValue(productInfo, "appinfo", appInfo);
            }

            //fill Hotel Group
            if (modules.contains("hotelgroupinfolist")) {
                poConverterEx.fillHotelGroupInfoList(productInfo, eventBody);
            }

            //fill Hotel Group
            if (modules.contains("hotelinfolist")) {
                poConverterEx.fillHotelInfoList(productInfo, eventBody);
            }

            //fill Vaction Info
            if (modules.contains("vacationinfolist")) {
                poConverterEx.fillVacationInfoList(productInfo, eventBody, po.getOrdertype());
            }

            if (modules.contains("railinfolist")) {
                poConverterEx.fillRailInfoList(productInfo, eventBody);
            }

            if (modules.contains("flightinfolist")) {
                poConverterEx.fillFlightInfoList(productInfo, eventBody);
            }
            if (modules.contains("diy")) {
                poConverterEx.fillDIYFlightInfoList(productInfo, eventBody);
                poConverterEx.fillDIYHotelInfoList(productInfo, eventBody);
                poConverterEx.fillDIYVacationInfoList(productInfo, eventBody);
                poConverterEx.fillDIYResourceXInfo(productInfo, eventBody);
            }
            if (modules.contains("giftitemlist")) {
                poConverterEx.fillGiftItemList(productInfo, eventBody);
            }
            if (modules.contains("devoteinfoviewbyjifen")) {
                poConverterEx.fillDevoteInfoViewByJiFen(productInfo, eventBody);
            }
            if (modules.contains("distribution")) {
                poConverterEx.fillDistribution(productInfo, eventBody, po.getOrdertype());
            }
            if (modules.contains("jifenorderitemlist")) {
                poConverterEx.fillJifenOrderItemList(productInfo, eventBody);
            }

            //fill Other Info
            fillOtherInfo(po, eventBody);

        } else if (checkType.equals("2") || checkType.equals("0")) { //支付校验，补充订单信息

            //fill PaymentMainInfo
            Map<String, Object> paymentMainInfo = new HashMap<String, Object>();
            copyMap(requestBody.getEventBody(), paymentMainInfo, "infosecurity_paymentmaininfo");
            setValue(paymentInfo, "paymentmaininfo", paymentMainInfo);

            //fill PaymentInfoList
            fillPaymentInfo(eventBody, paymentInfo, po.getOrdertype());

            if (modules.contains("orderccard")) {
                poConverterEx.fillAuthCCardInfo(productInfo, paymentInfo);
            }

        }

        if (modules.contains("tddspecial")) {
            //处理 isprepaid标志
            poConverterEx.tddSpecial(po, eventBody);
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

        if (Strings.isNullOrEmpty(po.getPrepaytype())) {
            po.setPrepaytype(getPrepayType(paymentInfo));
        }

        if (modules.contains("didinfo")) {
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

package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求报文转换为PO Entity
 * PO对外访问方式为Map
 * 内部操作模式为对象类
 * Created by thyang on 2015-06-10.
 */
@Component
public class POConverter extends POConvertBase {

    public PO convert(RequestBody requestBody) {
        PO po = new PO();
        Map<String, Object> eventBody = requestBody.getEventBody();

        String orderId = getString(eventBody, "orderid");
        String orderType = getString(eventBody, "ordertype");
        String merchantOrderId = getString(eventBody, "merchantorderid");
        String checkType = getString(eventBody, "checktype");
        String suborderType = getString(eventBody, "subordertype");
        po.setPrepaytype(getString(eventBody, "orderprepaytype"));
        if(StringUtils.isNumeric(orderId)) {
            po.setOrderid(Long.parseLong(orderId));
        }
        if(StringUtils.isNumeric(orderType)){
           po.setOrdertype(Integer.parseInt(orderType));
        }
        if(StringUtils.isNumeric(suborderType)) {
            po.setSubordertype(Integer.parseInt(suborderType));
        }
        if(StringUtils.isNumeric(checkType)) {
            po.setChecktype(Integer.parseInt(checkType));
        }


        if (Strings.isNullOrEmpty(orderId) || "0".equals(orderId)) {
            throw new RuntimeException("ORDERID IS ZERO");
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
        // 处理 Email、UserIp、MobilePhone
        validateData(requestBody);

        // fill DealInfo
        Map<String, Object> dealInfo = new HashMap<String, Object>();
        setValue(dealInfo, "checkstatus", 0);
        setValue(dealInfo, "referenceid", getString(eventBody, "referenceno"));
        setValue(productInfo, "dealinfo", dealInfo);

        //订单校验
        if (checkType.equals("1")) {     //订单校验，补充上次的支付信息

            //fill MainInfo
            Map<String, Object> mainInfo = new HashMap<String, Object>();
            copyMap(requestBody.getEventBody(), mainInfo, "infosecurity_maininfo");
            setValue(mainInfo, "lastcheck", "T");
            setValue(mainInfo, "ordertype", orderType);
            setValue(mainInfo, "corporationid", "");
            setValue(mainInfo, "amount", getObject(eventBody, "orderamount"));
            setValue(productInfo, "maininfo", mainInfo);

            //fill contactInfo
            fillContactInfo(requestBody, productInfo);

            //fill User Info Info
            String signupDate = fillUserInfo(requestBody, productInfo, getString(eventBody, "uid"));

            //fill IP Info
            fillIPInfo(productInfo, getString(eventBody, "userip"));

            //fill Vaction Info
            fillVactionInfo(productInfo, requestBody.getEventBody());

            //fill Hotel Group
            Map<String, Object> hotelGroup = new HashMap<String, Object>();
            copyMap(requestBody.getEventBody(), hotelGroup, "infosecurity_hotelgroupinfo");
            setValue(productInfo, "hotelgroupinfo", hotelGroup);

            //fill Other Info
            fillOtherInfo(productInfo, getString(eventBody, "orderdate"), signupDate, getString(eventBody, "takeofftime"));

        } else if (checkType.equals("2")) { //支付校验，补充订单信息

            //fill PaymentMainInfo
            Map<String, Object> paymentMainInfo = new HashMap<String, Object>();
            copyMap(requestBody.getEventBody(), paymentMainInfo, "infosecurity_paymentmaininfo");
            setValue(paymentInfo, "paymentmaininfo", paymentMainInfo);

            String prepayType = fillPaymentInfo(requestBody, paymentInfo);
            if (Strings.isNullOrEmpty(po.getPrepaytype())) {
                po.setPrepaytype(prepayType);
            }
        }

        fillDIDInfo(productInfo, orderId, orderType);

        return po;
    }

    public void saveData4Next(PO po){
        if(po.getChecktype().equals(1) && po.getProductinfo()!=null){
            checkRiskDAO.saveLastProductInfo(po.getOrderid(),po.getOrdertype(),po.getMerchantid(),po.getProductinfo());
        } else if(po.getChecktype().equals(2) && po.getPaymentinfo()!=null) {
            checkRiskDAO.saveLastPaymentInfo(po.getOrderid(),po.getOrdertype(),po.getMerchantid(),po.getPrepaytype(),po.getPaymentinfo());
        }
    }

}

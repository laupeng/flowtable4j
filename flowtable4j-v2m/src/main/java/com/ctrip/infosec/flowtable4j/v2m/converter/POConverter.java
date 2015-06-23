package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.model.persist.PaymentInfo;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求报文转换为PO Entity
 * PO对外访问方式为Map
 * 内部操作模式为对象类
 * Created by thyang on 2015-06-10.
 */
public class POConverter extends ConverterBase {

    public PO convert(RequestBody requestBody) {
        PO po = new PO();
        Map<String,Object> eventBody = requestBody.getEventBody();
        String orderId = getString(eventBody, "OrderID");
        String orderType =getString(eventBody, "OrderType");
        String merchantOrderId = getString(eventBody, "MerchantOrderID");
        String checkType =  getString(eventBody, "CheckType");

        if(Strings.isNullOrEmpty(orderId)||"0".equals(orderId)){
            return null;
        }
        Map<String,Object> paymentInfo = null;
        Map<String,Object> productInfo=null;
        if ("1".equals(checkType)) {
            paymentInfo = checkRiskDAO.getLastPaymentInfo(orderId, orderType, merchantOrderId);
        } else if ("2".equals(checkType)) {
            productInfo = checkRiskDAO.getLastProductInfo(orderId, orderType, merchantOrderId);
        }

        if(productInfo==null){
            productInfo = new HashMap<String, Object>();
            po.setProductInfo(productInfo);
        }


        if(paymentInfo==null){
            paymentInfo = new HashMap<String, Object>();
            po.setPaymentInfo(paymentInfo);
        }
        // 处理 Email、UserIp、MobilePhone
        validateData(requestBody);

        // fill DealInfo
       Map<String,Object> dealInfo = new HashMap<String, Object>();
       setValue(dealInfo,"CheckStatus", 0);
       setValue(dealInfo,"ReferenceID", getString(eventBody,"ReferenceNo"));
       setValue(productInfo,"dealInfo",dealInfo);

        //订单校验
        if (checkType.equals("1")) {     //订单校验，补充上次的支付信息
            //fill MainInfo
            Map<String,Object> mainInfo = new HashMap<String, Object>();
            fillEntity(requestBody,mainInfo, "InfoSecurity_MainInfo");
            setValue(mainInfo, "LastCheck", "T");
            setValue(mainInfo, "OrderType", 14);
            setValue(mainInfo, "CorporationID", "");
            setValue(mainInfo, "Amount", getObject(eventBody, "OrderAmount"));
            setValue(productInfo,"mainInfo",mainInfo);

            //fill contactInfo
            fillContactInfo(requestBody,productInfo);

            //fill User Info Info
            String signupDate = fillUserInfo(requestBody,productInfo,getString(eventBody, "Uid"));

            //fill IP Info
            fillIPInfo(productInfo,getString(eventBody, "UserIP"));

            //fill Hotel Group
            Map<String,Object> hotelGroup = new HashMap<String, Object>();
            fillEntity(requestBody,hotelGroup, "InfoSecurity_HotelGroupInfo");
            setValue(productInfo,"hotelGroup",hotelGroup);

            //fill Other Info
            fillOtherInfo(productInfo, getString(eventBody, "OrderDate"), signupDate, getString(eventBody, "TakeOffTime"));

            checkRiskDAO.saveLastProductInfo(orderId, orderType, merchantOrderId, productInfo);

        } else if (checkType.equals("2")) { //支付校验，补充订单信息

            //fill PaymentMainInfo
            Map<String,Object> paymentMainInfo = new HashMap<String, Object>();
            fillEntity(requestBody, paymentMainInfo, "InfoSecurity_PaymentMainInfo");
            setValue(paymentInfo,"paymentMainInfo",paymentMainInfo);

            fillPaymentInfo(requestBody, paymentInfo);
            checkRiskDAO.saveLastPaymentInfo(orderId, orderType, merchantOrderId, "", paymentInfo);
        }

        fillDIDInfo(productInfo,orderId,orderType);

        return po;
    }
}

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
        String orderId =  requestBody.getString("OrderID");
        String orderType =  requestBody.getString("OrderType");
        String checkType = requestBody.getString("CheckType");
        String lastReqId = checkRiskDAO.getLastReqId(requestBody.getString("OrderID"),requestBody.getString("OrderType"),requestBody.getString("MerchantOrderID"));
        if(!Strings.isNullOrEmpty(lastReqId)){
            if("1".equals(checkType)) {
                fillLastPaymentInfo(requestBody, po);
            } else if("2".equals(checkType)){
                fillLastProductInfo(requestBody,po);
            }
        }

        requestBody.getEventBody().put("Amount",requestBody.getString("OrderAmount"));

        validateMobilePhoneUserIP(requestBody);

        // fill DealInfo
        po.dealInfo = new HashMap<String, Object>();
        po.dealInfo.put("CheckStatus",0);
        po.dealInfo.put("ReferenceID", requestBody.getString("ReferenceNo"));

        //订单校验
        if(checkType.equals("1")) {     //订单校验，补充上次的支付信息

            //fill MainInfo
            po.mainInfo = new HashMap<String, Object>();
            fillEntity(requestBody, po.mainInfo, "InfoSecurity_MainInfo");
            po.mainInfo.put("LastCheck","T");
            po.mainInfo.put("OrderType",14);
            po.mainInfo.put("CorporationID","");

            //fill contactInfo
            fillContactInfo(requestBody, po);

            //fill User Info Info
            String signupDate = fillUserInfo(requestBody, po,requestBody.getString("Uid"));

            //fill IP Info
            po.IPInfo = new HashMap<String, Object>();
            fillIPInfo(po.IPInfo, requestBody.getString("UserIP"));

            //fill Hotel Group
            po.hotelGroup = new HashMap<String, Object>();
            fillEntity(requestBody, po.hotelGroup, "InfoSecurity_HotelGroupInfo");

            //fill Other Info
            fillOtherInfo(po,requestBody.getString("OrderDate"),signupDate,requestBody.getString("TakeOffTime"));

        } else if(checkType.equals("2")) { //支付校验，补充订单信息

            //fill PaymentMainInfo
            po.paymentMainInfo = new HashMap<String, Object>();
            fillEntity(requestBody, po.paymentMainInfo, "InfoSecurity_PaymentMainInfo");

            po.paymentInfoList = new ArrayList<PaymentInfo>();
            fillPaymentInfo(requestBody, po);
        }

        fillDIDInfo(po,orderId,orderType);
        return po;
    }

    /**
     * 当CheckType = 2 时，只带支付信息，需要从上次已保存的数据中恢复
     * @param requestBody
     * @param po
     */
    public void fillLastProductInfo(RequestBody requestBody,PO po){
        //首先从Redis获取，如果没有，取最近一次的提交
    }

    public void fillLastPaymentInfo(RequestBody requestBody,PO po){
        //首先从Redis获取，如果没有，取最近一次的提交
    }

}

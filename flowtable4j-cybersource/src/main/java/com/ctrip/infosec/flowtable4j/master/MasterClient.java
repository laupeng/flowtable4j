package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.sars.util.GlobalConfig;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Created by thyang on 2015-08-19.
 */
public class MasterClient {
    private static final Logger logger = LoggerFactory.getLogger(MasterClient.class);
    static final String masterUrl ="https://accreditation.datacash.com/Transaction/cnp_a"; //GlobalConfig.getString("Visa.URL");
    static final String appId =  GlobalConfig.getString("appId");
    static final String userName="88001177"; //GlobalConfig.getString("Visa.MerchantID");
    static final String password ="hMXChZVV";
    private String requestWithSoap(String soapRequestContent) throws IOException {
        String response = Request.Post(masterUrl).body(new StringEntity(soapRequestContent.toString(), "UTF-8")).
                addHeader("Content-Type", "application/xml; charset=utf-8").connectTimeout(5000).socketTimeout(5000).
                execute().returnContent().asString();
        return response;
    }

    private MasterResponse response(String soapResponseData) throws DocumentException {
//        SAXReader reader = new SAXReader();
//        StringReader read = new StringReader(soapResponseData);
//        InputSource source = new InputSource(read);
//        Document document = reader.read(source);
//        Element rootElement = document.getRootElement();
//        Element bodyElement = rootElement.element("Body");
        MasterResponse response = new MasterResponse();
        response.setResponseBody(soapResponseData);
//        if(bodyElement!=null){
//            Element replayMessage = bodyElement.element("replyMessage");
//            if(replayMessage!=null){
//                Iterator iterator = replayMessage.elements().iterator();
//                String elementname=null;
//                while (iterator.hasNext()) {
//                    Element element = (Element) iterator.next();
//                    elementname = element.getName().toLowerCase();
//                }
//            }
//        }
        return response;
    }

    /**
     *  Request
     *     Risk (调用RSG服务信息)
     *       CustomerDetails
     *         RiskDetails  (账户、IP、邮件信息）
     *         PersonalDetails（姓名、联系方式等信息）
     *         BillingDetails（账单信息）
     *         Journey
     *            Legs(航班信息）
     *            Passengers（乘客信息）
     *     FraudOnlyTxn
     *        MasterCard（卡号、有效期）
     *        BankResponse（银行返回的信息）
     *
     * @param requestBody
     * @return
     * @throws Exception
     */

    public MasterResponse requestMaster(MasterRequest requestBody) throws Exception {
        requestBody.setClient(userName);
        requestBody.setPassword(password);
        String soapResponseData = requestWithSoap(requestBody.toXML());
        if (soapResponseData != null && !soapResponseData.isEmpty()) {
            return response(soapResponseData);
        }
        return null;
    }
}

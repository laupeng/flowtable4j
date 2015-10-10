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
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Created by thyang on 2015-08-19.
 */
@Component
public class MasterClient {
    private static final Logger logger = LoggerFactory.getLogger(MasterClient.class);
    static final String masterUrl = GlobalConfig.getString("Master.URL");
    static final String userName  = GlobalConfig.getString("Master.UID");
    static final String password  = GlobalConfig.getString("Master.PWD");

    private String requestWithSoap(String soapRequestContent) throws IOException {
        String response = Request.Post(masterUrl).body(new StringEntity(soapRequestContent.toString(), "UTF-8")).
                addHeader("Content-Type", "application/xml; charset=utf-8").connectTimeout(5000).socketTimeout(5000).
                execute().returnContent().asString();
        return response;
    }

    private void handleResponse(String soapResponseData, MasterResponse response) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader read = new StringReader(soapResponseData);
        InputSource source = new InputSource(read);
        Document document = reader.read(source);
        Element bodyElement = document.getRootElement();
        response.setResponseStr(soapResponseData);
        if (bodyElement != null) {
            Iterator iterator = bodyElement.elements().iterator();
            String elementname = null;
            String elementname2 = null;
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                elementname = element.getName().toLowerCase();
                if (elementname.equals("risk")) {
                    Iterator iterator2 = element.element("action_response").element("screening_response").elements().iterator();
                    while (iterator2.hasNext()) {
                        Element element2 = (Element) iterator2.next();
                        elementname2 = element2.getName().toLowerCase();
                        if (elementname2.equals("additional_messages")) {
                            response.setAdditionalMessage(element2.getStringValue());
                        }
                        if (elementname2.equals("cpi_value")) {
                            response.setCpi_value(element2.getStringValue());
                        }
                        if (elementname2.equals("response_code")) {
                            response.setResponse_code(element2.getStringValue());
                        }
                        if (elementname2.equals("response_message")) {
                            response.setResponse_message(element2.getStringValue());
                        }
                    }
                }
                if (elementname.equals("mode")) {
                    response.setMode(element.getStringValue());
                } else if (elementname.equals("reason")) {
                    response.setReason(element.getStringValue());
                } else if (elementname.equals("status")) {
                    response.setStatus(element.getStringValue());
                }
            }
        }
        response.setServiceStatus("OK");
    }

    /**
     * Request
     * Risk (调用RSG服务信息)
     * CustomerDetails
     * RiskDetails  (账户、IP、邮件信息）
     * PersonalDetails（姓名、联系方式等信息）
     * BillingDetails（账单信息）
     * Journey
     * Legs(航班信息）
     * Passengers（乘客信息）
     * FraudOnlyTxn
     * MasterCard（卡号、有效期）
     * BankResponse（银行返回的信息）
     *
     * @param requestBody
     * @return
     * @throws Exception
     */

    public MasterResponse requestMaster(MasterRequest requestBody) {
        MasterResponse response = new MasterResponse();
        requestBody.setClient(userName);
        requestBody.setPassword(password);
        requestBody.setMerchantreference(requestBody.getEventID());
        try {
            String soapResponseData = requestWithSoap(requestBody.toXML());
            if (soapResponseData != null && !soapResponseData.isEmpty()) {
                handleResponse(soapResponseData, response);
            }
        } catch (Exception ex) {
            logger.warn("call datacash fail", ex);
            response.setServiceError(ex.getMessage());
        }
        return response;
    }
}

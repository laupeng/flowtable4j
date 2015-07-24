package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.sars.util.GlobalConfig;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/*
 Created by lpxie on 15-3-20.
 */
@Component
public class ESBClient {

    private static final Logger logger = LoggerFactory.getLogger(ESBClient.class);
    static final String esbUrl =  GlobalConfig.getString("SOA.ESB.URL");
    static final String appId =  GlobalConfig.getString("appId");

    private String requestWithSoap(String soapRequestContent) throws IOException {
        StringBuilder soapRequestSOAPData = new StringBuilder();
        soapRequestSOAPData.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:SOAP-ENC=\"http://www.w3.org/2003/05/soap-encoding\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        soapRequestSOAPData.append("<SOAP-ENV:Body>");
        soapRequestSOAPData.append("<m:Request xmlns:m=\"http://tempuri.org/\">");
        soapRequestSOAPData.append(String.format("<m:requestXML><![CDATA[%s]]></m:requestXML>", soapRequestContent));
        soapRequestSOAPData.append("</m:Request>");
        soapRequestSOAPData.append("</SOAP-ENV:Body>");
        soapRequestSOAPData.append("</SOAP-ENV:Envelope>");

        String response = Request.Post(esbUrl).body(new StringEntity(soapRequestSOAPData.toString(), "UTF-8")).
                addHeader("Content-Type", "application/soap+xml; charset=utf-8").connectTimeout(5000).socketTimeout(5000).
                execute().returnContent().asString();
        return response;
    }

    private String response(String soapResponseData) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader read = new StringReader(soapResponseData);
        InputSource source = new InputSource(read);
        Document document = reader.read(source);
        Element rootElement = document.getRootElement();
        Element bodyElement = rootElement.element("Body");
        return bodyElement.getStringValue();
    }

    public Map requestESB(String requestType, String requestBody, String xpath) throws Exception {
        String responseBody = null;
        StringBuilder requestContent = new StringBuilder();
        requestContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        requestContent.append("<Request>");
        requestContent.append(String.format("<Header UserID=\"%s\" RequestType=\"%s\" />", appId, requestType));
        requestContent.append(requestBody);
        requestContent.append("</Request>");
        String request = requestContent.toString();
        String soapResponseData = requestWithSoap(request);
        if (soapResponseData != null && !soapResponseData.isEmpty()) {
            responseBody = response(soapResponseData);
        }
        return parseXml(responseBody, xpath);
    }

    private Map parseXml(String xml, String xpath) throws DocumentException {
        Map<String, Object> resultMap = new HashMap();
        Document document = DocumentHelper.parseText(xml);
        List<Element> list = document.selectNodes(xpath);
        if (list == null || list.isEmpty()) {
            return resultMap;
        }
        for (Element creditCard : list) {
            Iterator iterator = creditCard.elements().iterator();
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                resultMap.put(element.getName().toLowerCase(), element.getStringValue());
            }
        }
        return resultMap;
    }

    /**
     * @param cardInfoId
     * @return
     */
    public Map getCardInfo(String cardInfoId) {
        String requestType = "AccCash.CreditCard.GetCreditCardInfo";
        String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";
        StringBuffer requestBody = new StringBuffer();
        requestBody.append("<GetCreditCardInfoRequest>");
        requestBody.append("<CardInfoId>");
        requestBody.append(cardInfoId);
        requestBody.append("</CardInfoId>");
        requestBody.append("</GetCreditCardInfoRequest>");
        try {
            return requestESB(requestType, requestBody.toString(), xpath);

        } catch (Exception exp) {
            logger.warn("GetCreditCardInfo", exp);
        }
        return null;
    }


    private String creatNode(String nodeName, Object nodeValue) {
        return String.format("<%s>%s</%s>", nodeName, nodeValue, nodeName);
    }

    /**
     * @param po
     * @return
     */
    public Map postRiskLevelData(PO po) {
        try {
            String requestType = "AccCash.EasyPay.SaveRiskLevelData";
            String xpath = "/Response/SaveRiskLevelDataResponse";
            Map<String, Object> productInfo = po.getProductinfo();
            Map<String, Object> risklevelDate = MapX.getMap(productInfo, "riskleveldata");
            StringBuffer requestBody = new StringBuffer();
            requestBody.append("<SaveRiskLevelDataRequest>");
            int cardInfoId = 0;
            List<Map<String, Object>> payments = MapX.getList(po.getPaymentinfo(), "paymentinfolist");
            if (payments != null && payments.size() > 0) {
                for (Map<String, Object> paymentInfoMap : payments) {
                    List<Map<String, Object>> cardInfos = MapX.getList(paymentInfoMap, "cardinfolist");
                    if (cardInfos != null && cardInfos.size() > 0) {
                        requestBody.append(creatNode("InfoID", 0));
                        requestBody.append(creatNode("IsForigenCard", MapX.getString(cardInfos.get(0), "isforigencard", "F")));
                        String cardId = MapX.getString(cardInfos.get(0), "cardinfoid", "0");
                        if (StringUtils.isNumeric(cardId)) {
                            cardInfoId = Integer.parseInt(cardId);
                            requestBody.append(creatNode("CardInfoID", cardInfoId));
                        }
                    }
                }
            }
            if (cardInfoId > 0) {
                requestBody.append(creatNode("ReqID", po.getReqid()));
                requestBody.append(creatNode("ResID", po.getReqid()));
                requestBody.append(creatNode("RefNo", 0));
                requestBody.append(creatNode("OrderID", po.getOrderid()));
                requestBody.append(creatNode("RiskLevel", MapX.getString(risklevelDate, "originalrisklevel", "0")));
                requestBody.append(creatNode("CreateDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX").format(System.currentTimeMillis())));
                requestBody.append(creatNode("LastOper", MapX.getString(risklevelDate, "lastoper", "")));
                requestBody.append(creatNode("Remark", MapX.getString(risklevelDate, "remark", "")));
                requestBody.append(creatNode("OrderType", po.getOrdertype()));
                requestBody.append(creatNode("OriginalRiskLevel", MapX.getString(risklevelDate, "originalrisklevel", "0")));
                requestBody.append(creatNode("Dealed", "F"));
                if (po.getOrdertype().equals(CtripOrderType.Flights.getCode())) {
                    requestBody.append(creatNode("Status", MapX.getString(risklevelDate, "cmbmsgstatus", "")));
                }
                requestBody.append("</SaveRiskLevelDataRequest>");
                return requestESB(requestType, requestBody.toString(), xpath);
            } else {
                return ImmutableMap.of("retcode", 0);
            }
        } catch (Exception exp) {
            logger.warn("GetCreditCardInfo", exp);
            return null;
        }
    }


    public Map getMemberInfo(String uid) throws Exception {
        try {
            String requestType = "Customer.User.GetMemberInfo";
            String requestBody = "<MemberInfoRequest><Uid>" + uid + "</Uid><Type>M</Type></MemberInfoRequest>";
            String xpath = "/Response/MemberInfoResponse";
            return requestESB(requestType, requestBody, xpath);
        } catch (Exception exp) {
            logger.warn("GetMemberInfo", exp);
            return null;
        }
    }


    public Map getCustomerInfo(String uid) {
        String requestType = "Customer.User.GetCustomerInfo";
        String requestBody = "<GetCustomerInfoRequest><UID>" + uid + "</UID></GetCustomerInfoRequest>";
        String xpath = "/Response/GetCustomerInfoResponse";
        try {
            return requestESB(requestType, requestBody, xpath);
        } catch (Exception exp) {
            logger.warn("GetCustomerInfo", exp);
        }
        return null;
    }
}

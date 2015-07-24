package com.ctrip.infosec.flowtable4j.visa;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 <soapenv:Header>
 <wsse:Security soapenv:mustUnderstand="1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
 <wsse:UsernameToken>
 <wsse:Username>yourMerchantID</wsse:Username>
 <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">yourPassword</wsse:Password>
 </wsse:UsernameToken>
 </wsse:Security>
 </soapenv:Header>
 <soapenv:Body>
 <requestMessage xmlns="urn:schemas-cybersource-com:transaction-data-N.NN">
 <merchantID>yourMerchantID</merchantID>
 <merchantReferenceCode>MRC-123</merchantReferenceCode>
 <billTo>
 <firstName>John</firstName>
 <lastName>Doe</lastName>
 <street1>1295 Charleston Road</street1>
 <city>Mountain View</city>
 <state>CA</state>
 <postalCode>94043</postalCode>
 <country>US</country>
 <email>null@cybersource.com</email>
 </billTo>
 <item id="0">
 <unitPrice>5.00</unitPrice>
 <quantity>1</quantity>
 </item>
 <item id="1">
 <unitPrice>10.00</unitPrice>
 <quantity>2</quantity>
 </item>
 <purchaseTotals>
 <currency>USD</currency>
 </purchaseTotals>
 <card>
 <accountNumber>4111111111111111</accountNumber>
 <expirationMonth>11</expirationMonth>
 <expirationYear>2020</expirationYear>
 </card>
 <ccAuthService run="true"/>
 </requestMessage>
 </soapenv:Body>
 </soapenv:Envelope>
 */
/*
 Created by lpxie on 15-3-20.
 */
@Component
public class ESBClient {

    private static final Logger logger = LoggerFactory.getLogger(ESBClient.class);
    static final String esbUrl = "http://soa.fws.qa.nt.ctripcorp.com/SOA.ESB/Ctrip.SOA.ESB.asmx"; //GlobalConfig.getString("SOA.ESB.URL");
    static final String appId = "AccCash.WSUser";// GlobalConfig.getString("appId");

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

    private String creatNode(String nodeName, Object nodeValue) {
        return String.format("<%s>%s</%s>", nodeName, nodeValue, nodeName);
    }

}

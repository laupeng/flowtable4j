package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.sars.util.GlobalConfig;
import org.apache.commons.lang3.Validate;
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

/*
 Created by lpxie on 15-3-20.
 */
@Component
public class ESBClient {

    private static final Logger logger = LoggerFactory.getLogger(ESBClient.class);

    static final String esbUrl = GlobalConfig.getString("SOA.ESB.URL");
    static final String appId = GlobalConfig.getString("appId");

    private static String requestWithSoap(String soapRequestContent) throws IOException {
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

    private static String response(String soapResponseData) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader read = new StringReader(soapResponseData);
        InputSource source = new InputSource(read);
        Document document = reader.read(source);
        Element rootElement = document.getRootElement();
        Element bodyElement = rootElement.element("Body");
        return bodyElement.getStringValue();
    }

    public static String requestESB(String requestType, String requestBody) throws Exception {
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
        return responseBody;
    }
}

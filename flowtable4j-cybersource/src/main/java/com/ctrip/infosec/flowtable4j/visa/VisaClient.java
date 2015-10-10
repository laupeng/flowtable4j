package com.ctrip.infosec.flowtable4j.visa;

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
 * <?xml version="1.0" encoding="UTF-8"?>
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 * <soapenv:Header>
 * <wsse:Security soapenv:mustUnderstand="1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
 * <wsse:UsernameToken>
 * <wsse:Username>yourMerchantID</wsse:Username>
 * <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">yourPassword</wsse:Password>
 * </wsse:UsernameToken>
 * </wsse:Security>
 * </soapenv:Header>
 * <soapenv:Body>
 * <requestMessage xmlns="urn:schemas-cybersource-com:transaction-data-N.NN">
 * <merchantID>yourMerchantID</merchantID>
 * <merchantReferenceCode>MRC-123</merchantReferenceCode>
 * <billTo>
 * <firstName>John</firstName>
 * <lastName>Doe</lastName>
 * <street1>1295 Charleston Road</street1>
 * <city>Mountain View</city>
 * <state>CA</state>
 * <postalCode>94043</postalCode>
 * <country>US</country>
 * <email>null@cybersource.com</email>
 * </billTo>
 * <item id="0">
 * <unitPrice>5.00</unitPrice>
 * <quantity>1</quantity>
 * </item>
 * <item id="1">
 * <unitPrice>10.00</unitPrice>
 * <quantity>2</quantity>
 * </item>
 * <purchaseTotals>
 * <currency>USD</currency>
 * </purchaseTotals>
 * <card>
 * <accountNumber>4111111111111111</accountNumber>
 * <expirationMonth>11</expirationMonth>
 * <expirationYear>2020</expirationYear>
 * </card>
 * <ccAuthService run="true"/>
 * </requestMessage>
 * </soapenv:Body>
 * </soapenv:Envelope>
 */
/*
 Created by lpxie on 15-3-20.
 */
@Component
public class VisaClient {

    private static final Logger logger = LoggerFactory.getLogger(VisaClient.class);
    static final String visaUrl = GlobalConfig.getString("Visa.URL");
    static final String merchanetId = GlobalConfig.getString("Visa.MerchantID");
    static final String token = GlobalConfig.getString("Visa.Token");

    private String requestWithSoap(String soapRequestContent) throws IOException {
        StringBuilder request = new StringBuilder();
        request.append(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        " <soapenv:Header>\n" +
                        " <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n" +
                        " <wsse:UsernameToken>\n")
                .append(creatNode("wsse:Username", merchanetId))
                .append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                .append(token).append("</wsse:Password>\n" +
                " </wsse:UsernameToken>\n" +
                " </wsse:Security>\n" +
                " </soapenv:Header>\n" +
                " <soapenv:Body>\n" +
                "<requestMessage xmlns=\"urn:schemas-cybersource-com:transaction-data-1.118\">\n")
                .append(soapRequestContent)
                .append(
                        "<afsService run=\"true\"/>\n" +
                                "</requestMessage>\n" +
                                " </soapenv:Body>\n" +
                                "</soapenv:Envelope>");

        String response = Request.Post(visaUrl).body(new StringEntity(request.toString(), "UTF-8")).
                addHeader("Content-Type", "application/soap+xml; charset=utf-8").connectTimeout(5000).socketTimeout(5000).
                execute().returnContent().asString();
        return response;
    }

    private String creatNode(String nodeName, Object nodeValue) {
        return String.format("<%s>%s</%s>\n", nodeName, nodeValue, nodeName);
    }

    private void handleResponse(String soapResponseData, VisaResponse response) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader read = new StringReader(soapResponseData);
        InputSource source = new InputSource(read);
        Document document = reader.read(source);
        Element rootElement = document.getRootElement();
        Element bodyElement = rootElement.element("Body");
        response.setResponseStr(soapResponseData);
        if (bodyElement != null) {
            Element replayMessage = bodyElement.element("replyMessage");
            if (replayMessage != null) {
                Iterator iterator = replayMessage.elements().iterator();
                String elementname = null;
                String elementname2 = null;
                while (iterator.hasNext()) {
                    Element element = (Element) iterator.next();
                    elementname = element.getName().toLowerCase();
                    if (elementname.equals("afsreply")) {
                        Iterator iterator2 = element.elements().iterator();
                        while (iterator2.hasNext()) {
                            Element element2 = (Element) iterator2.next();
                            elementname2 = element2.getName().toLowerCase();
                            if (elementname2.equals("reasoncode")) {
                                response.getAfsReply().setReasonCode(element2.getStringValue());
                            }
                            if (elementname2.equals("afsresult")) {
                                response.getAfsReply().setAfsResult(element2.getStringValue());
                            }
                            if (elementname2.equals("hostseverity")) {
                                response.getAfsReply().setHostSeverity(element2.getStringValue());
                            }
                            if (elementname2.equals("afsfactorcode")) {
                                response.getAfsReply().setAfsFactorCode(element2.getStringValue());
                            }
                            if (elementname2.equals("bincountry")) {
                                response.getAfsReply().setBinCountry(element2.getStringValue());
                            }
                            if (elementname2.equals("cardscheme")) {
                                response.getAfsReply().setCardScheme(element2.getStringValue());
                            }
                            if (elementname2.equals("cardissuer")) {
                                response.getAfsReply().setCardIssuer(element2.getStringValue());
                            }
                            if (elementname2.equals("cardaccounttype")) {
                                response.getAfsReply().setCardAccountType(element2.getStringValue());
                            }
                            if (elementname2.equals("scoremodelused")) {
                                response.getAfsReply().setScoreModelUsed(element2.getStringValue());
                            }
                        }
                    }
                    if (elementname.equals("requesttoken")) {
                        response.setRequestToken(element.getStringValue());
                    } else if (elementname.equals("missingfield")) {
                        response.getMissingFields().add(element.getStringValue());
                    } else if (elementname.equals("reasoncode")) {
                        response.setReasonCode(element.getStringValue());
                    } else if (elementname.equals("requestid")) {
                        response.setRequestid(element.getStringValue());
                    } else if (elementname.equals("decision")) {
                        response.setDecision(element.getStringValue());
                    }
                }
            }
        }
        response.setServiceStatus("OK");
    }

    /**
     * Request
     * BillTo(账单信息）
     * VisaCard（卡信息）
     * PurchaseTotals（金额）
     * DecisionManager
     * DecisionManagerTravelData（航程信息）
     * List<DecisionManagerTravelLeg>
     * List<Item> （乘客信息）
     * MerchantDefinedData （自定义的附件信息）
     *
     * @param requestBody
     * @return
     * @throws Exception
     */
    public VisaResponse requestVisa(VisaRequest requestBody) {
        VisaResponse response = new VisaResponse();
        try {
            requestBody.setMerchantReferenceCode(requestBody.getEventID());
            String soapResponseData = requestWithSoap(requestBody.toXml());
            if (soapResponseData != null && !soapResponseData.isEmpty()) {
                handleResponse(soapResponseData, response);
            }
        } catch (Exception ex) {
            logger.warn("call cybersource fail", ex);
            response.setServiceError(ex.getMessage());
        }
        return response;
    }

}

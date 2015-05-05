package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.common.GlobalConfig;
import com.ctrip.infosec.flowtable4j.translate.esb.ESB;
import com.ctrip.infosec.flowtable4j.translate.esb.ESBSoap;
import org.apache.commons.lang3.Validate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-4-2.
 */
public class ESBSources
{
    private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "ESB");
    public static ESBSoap port = null;
    /*esb的url*/
    static final String serviceUrl = GlobalConfig.getString("ESB.serviceUrl");
    static final String appId = GlobalConfig.getString("appId");

    static void check() {
        Validate.notEmpty(serviceUrl, "在GlobalConfig.properties里没有找到\"ESB.serviceUrl\"配置项.");
        Validate.notEmpty(appId, "在GlobalConfig.properties里没有找到\"appId\"配置项.");
    }

    public void init()
    {
        check();
        URL wsdlURL = null;
        try
        {
            wsdlURL = new URL(serviceUrl);
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        ESB ss = new ESB(wsdlURL, SERVICE_NAME);
        port = ss.getESBSoap();
    }

    /**
     * 提供给外部使用的的接口
     * 测试这个接口的报文数据
     * @param requestXml
     * @return
     */
    public Map getResponse(String requestXml,String contentType,String xpath) throws DocumentException
    {
        String responseXml = port.request(constructXml(requestXml,contentType));
        Map responseMap = parseXml(responseXml,xpath);
        return responseMap;
    }

    /**
     * 这里构建xml需要根据不同的服务配置不同的格式
     * @param contentBody   <GetCreditCardInfoRequest><CardInfoId>" + params.get("cardInfoId") + "</CardInfoId></GetCreditCardInfoRequest>
     * @param contentType   AccCash.CreditCard.GetCreditCardInfo
     * @return
     */
    public String constructXml(String contentBody,String contentType)//FIXME 根据不同的服务配置不同的格式
    {
        StringBuilder requestContent = new StringBuilder();
        requestContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        requestContent.append("<Request>");
        requestContent.append(String.format("<Header UserID=\"%s\" RequestType=\"%s\" />", appId, contentType));
        requestContent.append(contentBody);
        requestContent.append("</Request>");
        return requestContent.toString();
    }

    public Map parseXml(String xml,String xpath) throws DocumentException
    {
        Map<String,Object> resultMap = new HashMap();
        Document document = DocumentHelper.parseText(xml);
        //String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";//FIXME xpath 应该作为参数传进来
        List<Element> list = document.selectNodes(xpath);
        if (list == null || list.isEmpty()) {
            return resultMap;
        }
        //FIXME 这里逻辑有些问题
        for (Element creditCard : list) {
            Iterator iterator = creditCard.elements().iterator();
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                resultMap.put(element.getName(), element.getStringValue());
            }
        }
        return resultMap;
    }
}

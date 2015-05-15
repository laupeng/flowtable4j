package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.flowtable4j.translate.dao.ESBSources;
import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lpxie on 15-4-7.
 */
public class ESBSourcesTest
{
    /**
     * 测试构建请求xml内容正确
     * 目前只知道CardInfoId 的请求的参数，所以只测试这个
     */
    @Test
    public void testConstructXml()
    {
        //30075005
        ESBSources esbSources = new ESBSources();
        String contentType = "AccCash.CreditCard.GetCreditCardInfo";
        String cardInfoId = "218417646";
        String contentBody = "<GetCreditCardInfoRequest><CardInfoId>" + cardInfoId + "</CardInfoId></GetCreditCardInfoRequest>";
        String requestContentXml = esbSources.constructXml(contentBody,contentType);
        Assert.assertNotNull(requestContentXml);
        Assert.assertTrue(!requestContentXml.isEmpty());
    }

    @Test
    public void testGetResponse()
    {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\n" +
                "<Request>\n" +
                "  <Header UserID=\"670203\" RequestType=\"AccCash.CreditCard.GetCreditCardInfo\"/>\n" +
                "  <GetCreditCardInfoRequest>\n" +
                "    <CardInfoId>218417646</CardInfoId>\n" +
                "  </GetCreditCardInfoRequest>\n" +
                "</Request>";
        ESBSources esbSources = new ESBSources();
        esbSources.init();
//        String contentType = "AccCash.CreditCard.GetCreditCardInfo";
//        String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";
        String responseXml = ESBSources.port.request(xmlContent);
        Assert.assertNotNull(responseXml);
        Assert.assertTrue(!responseXml.isEmpty());
    }
    /**
     * 测试解析xml文件，这里以前面构造的xml为例
     * xpath 为 /Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem
     */
    @Test
    public void testParseXml()
    {
        ESBSources esbSources = new ESBSources();
        String responseXml = "<?xml version=\"1.0\"?>\n" +
                "<Response>\n" +
                "  <Header ServerIP=\"10.2.6.47\" ShouldRecordPerformanceTime=\"false\" UserID=\"670203\" RequestID=\"f31a8f66-fcd1-4af9-8d41-1821fd44f83b\" ResultCode=\"Success\" AssemblyVersion=\"2.9.3.0\" RequestBodySize=\"0\" SerializeMode=\"Xml\" RouteStep=\"1\" Environment=\"fws\" />\n" +
                "  <GetCreditCardInfoResponse>\n" +
                "    <CreditCardItems>\n" +
                "      <CreditCardInfoResponseItem>\n" +
                "        <CardInfoId>30075005</CardInfoId>\n" +
                "        <CreditCardType>7</CreditCardType>\n" +
                "        <CardTypeName>境外发行信用卡 -- 威士(VISA)</CardTypeName>\n" +
                "        <CreditCardNumber>201502@bDI6iVQU2nwG0K3FEpa82yzI8faSZ572EHbA6UjQGw0=</CreditCardNumber>\n" +
                "        <CCardNoCode>D41D8CD98F00B204E9800998ECF8427E</CCardNoCode>\n" +
                "        <CValidityCode>D41D8CD98F00B204E9800998ECF8427E</CValidityCode>\n" +
                "        <CardBin>480000</CardBin>\n" +
                "        <Validity>201502@rJsSRy3aXiX5ChOUFPxoDQ==</Validity>\n" +
                "        <CardHolder>zhou jing</CardHolder>\n" +
                "        <IdCardType>0</IdCardType>\n" +
                "        <IdNumber />\n" +
                "        <VerifyNo>201502@Xopf0pcg6pF2YBJY6XLZlA==</VerifyNo>\n" +
                "        <CurrencyType>U</CurrencyType>\n" +
                "        <VM_Type>V</VM_Type>\n" +
                "        <IsForeignCard>T</IsForeignCard>\n" +
                "        <LocalCardType>U</LocalCardType>\n" +
                "        <AgreementCode />\n" +
                "        <Nationality>  </Nationality>\n" +
                "        <StateName />\n" +
                "        <BillingAddress />\n" +
                "        <ZipCode />\n" +
                "        <Nationalityofisuue>  </Nationalityofisuue>\n" +
                "        <BankOfCardIssue />\n" +
                "        <CreateDate>2015-02-26T14:26:49</CreateDate>\n" +
                "        <PhoneNo />\n" +
                "        <IsVerifyNoEmpty>F</IsVerifyNoEmpty>\n" +
                "        <CardNoRefID>7</CardNoRefID>\n" +
                "      </CreditCardInfoResponseItem>\n" +
                "    </CreditCardItems>\n" +
                "  </GetCreditCardInfoResponse>\n" +
                "</Response>";
        String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";
        try
        {
            Map responseMap = esbSources.parseXml(responseXml, xpath);
            Assert.assertNotNull(responseMap);
            Assert.assertTrue(responseMap.size()>0);
        } catch (DocumentException e)
        {
            e.printStackTrace();
        }

    }

    @Test
    public void testMapMerge()
    {
        final long lastReq = Long.parseLong("55555");
    }
}

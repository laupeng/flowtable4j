package com.ctrip.infosec.flowtable4j.flowrule;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ctrip.infosec.flowtable4j.flowrule.impl.RiskFlowControlFlight;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/FlowRule.test.xml"})
public class RiskFlowControlFlightTest {

	@Resource(name ="checkRiskCtripFlight")
	RiskFlowControlFlight checkRiskCtripFlight;
	
	Map orderEntity, ruleKPIEntity;// 订单实体,规则KPI实体

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		checkRiskCtripFlight = new RiskFlowControlFlight();

		orderEntity = new HashMap();// 订单实体
		Map InfoSecurity_DealInfo = new HashMap();// 主信息
		InfoSecurity_DealInfo.put("ReqID", 1);
		InfoSecurity_DealInfo.put("CheckStatus", 1);
		InfoSecurity_DealInfo.put("CheckNum", 1);
		InfoSecurity_DealInfo.put("ReferenceID", 1);
		orderEntity.put("InfoSecurity_DealInfo", InfoSecurity_DealInfo);

		Map InfoSecurity_MainInfo = new HashMap();// 订单主信息
		InfoSecurity_DealInfo.put("ReqID", 1);
		InfoSecurity_DealInfo.put("OrderType", 1);
		InfoSecurity_DealInfo.put("OrderId", 1);
		InfoSecurity_DealInfo.put("OrderDate", 1);
		InfoSecurity_DealInfo.put("Amount", 1);
		InfoSecurity_DealInfo.put("IsOnline", 1);
		InfoSecurity_DealInfo.put("Serverfrom", 1);
		InfoSecurity_DealInfo.put("CheckType", 1);
		InfoSecurity_DealInfo.put("CreateDate", 1);
		InfoSecurity_DealInfo.put("LastCheck", 1);
		InfoSecurity_DealInfo.put("RefNo", 1);
		InfoSecurity_DealInfo.put("WirelessClientNo", 1);
		InfoSecurity_DealInfo.put("CorporationID", 1);
		InfoSecurity_DealInfo.put("DataChange_LastTime", 1);
		InfoSecurity_DealInfo.put("MerchantID", 1);
		InfoSecurity_DealInfo.put("ProcessingType", 1);
		InfoSecurity_DealInfo.put("ClientID", 1);
		InfoSecurity_DealInfo.put("SubOrderType", 1);
		InfoSecurity_DealInfo.put("ApplyRemark", 1);
		InfoSecurity_DealInfo.put("ClientVersion", 1);
		InfoSecurity_DealInfo.put("MerchantOrderID", 1);
		InfoSecurity_DealInfo.put("OrderProductName", 1);
		InfoSecurity_DealInfo.put("PayExpiryDate", 1);
		InfoSecurity_DealInfo.put("PreAuthorizedAmount", 1);
		InfoSecurity_DealInfo.put("RiskCountrolDeadline", 1);
		InfoSecurity_DealInfo.put("TotalDiscountAmount", 1);
		InfoSecurity_DealInfo.put("Currency", 1);
		InfoSecurity_DealInfo.put("OriginalAmount", 1);
		orderEntity.put("InfoSecurity_MainInfo", InfoSecurity_MainInfo);

		Map InfoSecurity_ContactInfo = new HashMap();
		InfoSecurity_ContactInfo.put("ReqID", 1);
		InfoSecurity_ContactInfo.put("ContactName", 1);
		InfoSecurity_ContactInfo.put("MobilePhone", 1);
		InfoSecurity_ContactInfo.put("ContactEMail", 1);
		InfoSecurity_ContactInfo.put("ContactTel", 1);
		InfoSecurity_ContactInfo.put("ContactFax", 1);
		InfoSecurity_ContactInfo.put("ZipCode", 1);
		InfoSecurity_ContactInfo.put("TelCall", 1);
		InfoSecurity_ContactInfo.put("ForignMobilePhone", 1);
		InfoSecurity_ContactInfo.put("SendTickerAddr", 1);
		InfoSecurity_ContactInfo.put("PostAddress", 1);
		InfoSecurity_ContactInfo.put("MobilePhoneProvince", 1);
		InfoSecurity_ContactInfo.put("MobilePhoneCity", 1);
		InfoSecurity_ContactInfo.put("DataChange_LastTime", 1);
		InfoSecurity_ContactInfo.put("Remark", 1);
		orderEntity.put("InfoSecurity_ContactInfo", InfoSecurity_ContactInfo);
		
		List<Map> paymentInfos = new ArrayList<Map>();
		
		for(int i=0;i<2;i++){
			Map paymentInfo = new HashMap();
			Map InfoSecurity_PaymentInfo = new HashMap();
			InfoSecurity_PaymentInfo.put("PaymentInfoID", 1);
			InfoSecurity_PaymentInfo.put("ReqID", 1);
			InfoSecurity_PaymentInfo.put("PrepayType", 1);
			InfoSecurity_PaymentInfo.put("IsGuarantee", 1);
			InfoSecurity_PaymentInfo.put("Amount", 1);
			InfoSecurity_PaymentInfo.put("BillNo", 1);
			paymentInfo.put("InfoSecurity_PaymentInfo", InfoSecurity_PaymentInfo);
			
			List<Map> cardInfoList = new ArrayList<Map>();
			for(int j=0;j<1;j++){
				Map InfoSecurity_CardInfo = new HashMap();
				
				InfoSecurity_CardInfo.put("PaymentInfoID",1);
				InfoSecurity_CardInfo.put("CardInfoID",1);
				InfoSecurity_CardInfo.put("CreditCardType",1);
				InfoSecurity_CardInfo.put("InfoID",1);
				InfoSecurity_CardInfo.put("CValidityCode",1);
				InfoSecurity_CardInfo.put("CCardNoCode",1);
				InfoSecurity_CardInfo.put("CardHolder",1);
				InfoSecurity_CardInfo.put("CardBin",1);
				InfoSecurity_CardInfo.put("CCardLastNoCode",1);
				InfoSecurity_CardInfo.put("CCardPreNoCode",1);
				InfoSecurity_CardInfo.put("StateName",1);
				InfoSecurity_CardInfo.put("BillingAddress",1);
				InfoSecurity_CardInfo.put("Nationality",1);
				InfoSecurity_CardInfo.put("Nationalityofisuue",1);
				InfoSecurity_CardInfo.put("BankOfCardIssue",1);
				InfoSecurity_CardInfo.put("CardBinIssue",1);
				InfoSecurity_CardInfo.put("CardBinBankOfCardIssue",1);
				InfoSecurity_CardInfo.put("IsForigenCard",1);
				InfoSecurity_CardInfo.put("DataChange_LastTime",1);
				InfoSecurity_CardInfo.put("ReqID",1);
				
				cardInfoList.add(InfoSecurity_CardInfo);
			}
			paymentInfos.add(paymentInfo);
		}
		
		orderEntity.put("PaymentInfos", paymentInfos);
		
		
		Map InfoSecurity_DeviceIDInfo = new HashMap();
		
		ruleKPIEntity = new HashMap();

	}

	@Test
	public void testCheckFlowRuleList() {
		boolean isFlowRuleWhite = true;
		boolean isWhiteCheck = true;
		FlowCheckRiskResult result = checkRiskCtripFlight.CheckFlowRuleList(orderEntity, ruleKPIEntity, isFlowRuleWhite, isWhiteCheck);
		assertTrue(result.getRiskLevel()==0);
		
		isFlowRuleWhite = false;
		isWhiteCheck = true;
		result = checkRiskCtripFlight.CheckFlowRuleList(orderEntity, ruleKPIEntity, isFlowRuleWhite, isWhiteCheck);
		
	}

}

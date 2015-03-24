package com.ctrip.infosec.flowtable4j.flowrule;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.infosec.flowtable4j.flowrule.impl.CheckRiskCtripFlight;

public class CheckRiskCtripFlightTest {

	CheckRiskCtripFlight checkRiskCtripFlight;
	Map orderEntity, basicCheckRiskData;// 订单实体,规则KPI实体

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		checkRiskCtripFlight = new CheckRiskCtripFlight();

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
		
		Map InfoSecurity_DeviceIDInfo = new HashMap();
		
		basicCheckRiskData = new HashMap();

	}

	@Test
	public void testCheckFlowRuleList() {
		checkRiskCtripFlight.CheckFlowRuleList(orderEntity, basicCheckRiskData, true, true);
	}

}

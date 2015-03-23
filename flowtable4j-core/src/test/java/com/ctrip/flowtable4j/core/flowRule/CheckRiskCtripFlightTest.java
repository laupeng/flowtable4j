package com.ctrip.flowtable4j.core.flowRule;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.flowtable4j.core.flowRule.impl.CheckRiskCtripFlight;

public class CheckRiskCtripFlightTest {

	CheckRiskCtripFlight  checkRiskCtripFlight;
	Map ruleCheckEntity;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		checkRiskCtripFlight = new CheckRiskCtripFlight();
		
		ruleCheckEntity = new HashMap();
	}

	@Test
	public void testCheckFlowRuleList() {
		 
	}

}

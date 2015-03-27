package com.ctrip.infosec.flowtable4j.flowrule;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ctrip.infosec.flowtable4j.flowrule.entity.FlowRuleEntity;
import com.ctrip.infosec.flowtable4j.flowrule.impl.FlowRuleManagerImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/FlowRule.test.xml"})
public class FlowRuleManagerImplTest {

	@Resource(name = "flowRuleManager")
	FlowRuleManager flowRuleManager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFlowRuleListByOrderTypeString() {
		String strOrderType = "2";
		List<FlowRuleEntity> rules = flowRuleManager.GetFlowRuleListByOrderType(strOrderType);
		assertNotNull(rules);
		assertTrue(rules.size()>0);
	}

	@Test
	public void testGetFlowRuleListByOrderTypeStringString() {
		String strOrderType = "2";
		List<FlowRuleEntity> rules = flowRuleManager.GetFlowRuleListByOrderType(strOrderType,"CCARD");
		assertNotNull(rules);
		assertTrue(rules.size()>0);
	}

	
	
	@Test
	public void testGetFlowRuleListByOrderTypeStringStringBoolean() {
		String strOrderType = "2";
		List<FlowRuleEntity> rules = flowRuleManager.GetFlowRuleListByOrderType(strOrderType,"CCARD",false);
		assertNotNull(rules);
		assertTrue(rules.size()>0);
	}
	
	/*
	@Test
	public void testGetFlowRuleList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCheckedFlowRuleInfo() {
		fail("Not yet implemented");
	}*/

}

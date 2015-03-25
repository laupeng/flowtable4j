package com.ctrip.infosec.flowtable4j.flowrule;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring/flowtable4j-test.xml"})
public class RuleManagerTest {
	
    @Resource(name = "cardRiskDBTemplate")
    JdbcTemplate cardRiskDBTemplate;
    @Resource(name = "riskCtrlPreProcDBTemplate")
    JdbcTemplate riskCtrlPreProcDBTemplate;

	@Ignore
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Ignore
	@Before
	public void setUp() throws Exception {

	}

	@Ignore
	@Test
	public void testSetRuleEntities() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetFlowRuleListByOrderTypeString() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetFlowRuleListByOrderTypeStringString() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetFlowRuleListByOrderTypeStringBoolean() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetFlowRuleListByOrderTypeStringStringBoolean() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetFlowRuleList() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetCheckedFlowRuleInfo() {
		fail("Not yet implemented");
	}

}
package com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityFlowRuleGenDao;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityFlowRuleGen;

import junit.framework.TestCase;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/jdbcTemplate.test.xml"})
public class InfoSecurityFlowRuleGenDaoImplTest extends TestCase {


	
	@Resource(name = "infoSecurityFlowRuleGenDao")
	InfoSecurityFlowRuleGenDao infoSecurityFlowRuleGenDao;
	
	protected static void setUpBeforeClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testGetListByActive(){
		//infoSecurityFlowRuleGenDao = new com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl.InfoSecurityFlowRuleGenDaoImpl();
		List<InfoSecurityFlowRuleGen> gens = infoSecurityFlowRuleGenDao.getListByActive("T");
		assertNotNull(gens);
	}

}

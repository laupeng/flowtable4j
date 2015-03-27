package com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl;

import java.sql.SQLException;
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
import com.ctrip.infosec.flowtable4j.dal.dalmanager.CardRiskDBDalManager;

import junit.framework.TestCase;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/jdbcTemplate.test.xml"})
public class InfoSecurityFlowRuleGenDaoImplTest extends TestCase {


	
	@Resource(name = "cardRiskDBDalManager")
	CardRiskDBDalManager cardRiskDBDalManager;
	
	protected static void setUpBeforeClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testGetListByActive(){
		//infoSecurityFlowRuleGenDao = new com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl.InfoSecurityFlowRuleGenDaoImpl();
		List<InfoSecurityFlowRuleGen> gens;
		try {
			gens = cardRiskDBDalManager.getInfoSecurityFlowRuleGenDao().getListByActive("T");
			assertNotNull(gens);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

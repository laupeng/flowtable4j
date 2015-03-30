package com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityRuleStatisticGenDAO;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleStatisticGen;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/jdbcTemplate.test.xml"})
public class InfoSecurityRuleStatisticGenDAOImplTest {

	@Resource(name = "infoSecurityRuleStatisticGenDAO")
	InfoSecurityRuleStatisticGenDAO infoSecurityRuleStatisticGenDAO;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetAll() {
		try {
			List<InfoSecurityRuleStatisticGen> lst = infoSecurityRuleStatisticGenDAO.getAll();
			assertNotNull(lst);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetListByRuleId() throws SQLException {
		int flowRuleID;
		flowRuleID = 4;
		List<InfoSecurityRuleStatisticGen> lst = infoSecurityRuleStatisticGenDAO.getListByRuleId(flowRuleID);
		assertNotNull(lst);
	}

}

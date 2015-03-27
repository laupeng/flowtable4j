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

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityRuleMatchFieldGenDao;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleMatchFieldGen;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/jdbcTemplate.test.xml"})
public class InfoSecurityRuleMatchFieldGenDaoImplTest {

	@Resource(name = "infoSecurityRuleMatchFieldGenDao")
	InfoSecurityRuleMatchFieldGenDao infoSecurityRuleMatchFieldGenDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetAll() {
		try {
			List<InfoSecurityRuleMatchFieldGen> gens = infoSecurityRuleMatchFieldGenDao.getAll();
			assertNotNull(gens);
			System.out.print(gens.size());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGetListByRuleId() throws SQLException {

			int flowRuleID = 4;
			List<InfoSecurityRuleMatchFieldGen> gens = infoSecurityRuleMatchFieldGenDao.getListByRuleId(flowRuleID);
			assertNotNull(gens);
			System.out.print(gens.size());
			

	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author zhengby
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/flowtable4j*.xml"})
public class JdbcTemplateTest {

    @Resource(name = "ruleTemplate")
    JdbcTemplate CardRiskDB;
    @Resource(name = "flowTemplate")
    JdbcTemplate RiskCtrlPreProcDB;

    @Test
    public void testQueryCardRiskDB() {
        System.out.println("CardRiskDB");
        List<Map> results = CardRiskDB.queryForList("select top 100 * from InfoSecurity_FlowRule", Map.class);
        System.out.println("results: " + results.size());
    }

    @Test
    public void testQueryRiskCtrlPreProcDB() {
        System.out.println("RiskCtrlPreProcDB");
        List<Map> results = RiskCtrlPreProcDB.queryForList("select top 100 * from InfoSecurity_FlowRule", Map.class);
        System.out.println("results: " + results.size());
    }
}

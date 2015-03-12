/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author zhengby
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/flowtable4j*.xml"})
public class JdbcTemplateTest {

    @Resource(name = "ruleTemplate")
    JdbcTemplate CardRiskDB;
    @Resource(name = "flowTemplate")
    JdbcTemplate RiskCtrlPreProcDB;

    private class Rule {
        private Integer flowRuleID;
        private Integer orderType;
        private Integer riskLevel;

        public Integer getFlowRuleID() {
            return flowRuleID;
        }

        public void setFlowRuleID(Integer flowRuleID) {
            this.flowRuleID = flowRuleID;
        }

        public Integer getOrderType() {
            return orderType;
        }

        public void setOrderType(Integer orderType) {
            this.orderType = orderType;
        }

        public Integer getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(Integer riskLevel) {
            this.riskLevel = riskLevel;
        }
    }
    @Test
    public void testQueryCardRiskDB() {
        System.out.println("CardRiskDB");
        List<Map<String,Object>> results = CardRiskDB.queryForList("select top 100* from dbo.InfoSecurity_FlowRule");
        System.out.println("results: " + results.size());
    }

    @Test
    @Ignore
    public void testQueryRiskCtrlPreProcDB() {
        System.out.println("RiskCtrlPreProcDB");
        List<Map> results = RiskCtrlPreProcDB.queryForList("select top 100 * from InfoSecurity_FlowRule", Map.class);
        System.out.println("results: " + results.size());
    }
}

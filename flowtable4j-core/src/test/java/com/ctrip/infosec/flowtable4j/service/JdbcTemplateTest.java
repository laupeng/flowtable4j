/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author zhengby
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/flowtable4j*.xml"})
public class JdbcTemplateTest {

    @Resource(name = "ruleTemplate")
    JdbcTemplate CardRiskDB;
    @Resource(name = "flowTemplate")
    JdbcTemplate RiskCtrlPreProcDB;

    @Test
    @Ignore
    public void testQueryCardRiskDB() {
        System.out.println("CardRiskDB");
        List<Map<String,Object>> results = CardRiskDB.queryForList("select top 100* from dbo.InfoSecurity_FlowRule");
        System.out.println("results: " + results.size());
    }

    @Test
    @Ignore
    public void testQueryRiskCtrlPreProcDB() {
        System.out.println("RiskCtrlPreProcDB");
        List<Map> results = CardRiskDB.queryForList("select top 100 * from InfoSecurity_FlowRule", Map.class);
        System.out.println("results: " + results.size());
    }

    @Test
    @Ignore
    public void testQueryBW(){
        int size = 10;
        System.out.println(">>>print BWList begin...");
        List<Map<String,Object>> results = CardRiskDB.queryForList("select r.RuleID,r.OrderType,c.CheckName,c.CheckType,cv.CheckValue \n" +
                "from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv \n" +
                "on r.RuleID= cv.RuleID \n" +
                "Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType \n" +
                "Where r.Active='T' \n" +
                "Order by r.OrderType,r.RuleID,c.CheckType");
        for(int i=0;i<size;i++){
            for(Iterator it=results.get(i).keySet().iterator();it.hasNext();){
                Object key = it.next();
                System.out.print(key + ":" + results.get(i).get(key)+" ");
            }
            System.out.println();
        }
        System.out.println("<<<print BWList end...");
    }
}

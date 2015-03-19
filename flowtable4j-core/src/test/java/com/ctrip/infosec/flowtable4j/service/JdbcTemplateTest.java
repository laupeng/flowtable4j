/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.ctrip.flowtable4j.core.blackList.RuleStatement;
import com.ctrip.flowtable4j.core.blackList.RuleTerm;
import org.apache.commons.dbcp.BasicDataSource;
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
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring/flowtable4j*.xml"})
public class JdbcTemplateTest {

//    @Resource(name = "ruleTemplate")
//    JdbcTemplate CardRiskDB;
//    @Resource(name = "flowTemplate")
//    JdbcTemplate RiskCtrlPreProcDB;

    @Test
    @Ignore
    public void testQueryCardRiskDB() {
//        System.out.println("CardRiskDB");
//        List<Map<String,Object>> results = CardRiskDB.queryForList("select top 100* from dbo.InfoSecurity_FlowRule");
//        System.out.println("results: " + results.size());
    }

    @Test
    @Ignore
    public void testQueryRiskCtrlPreProcDB() {
//        System.out.println("RiskCtrlPreProcDB");
//        List<Map> results = CardRiskDB.queryForList("select top 100 * from InfoSecurity_FlowRule", Map.class);
//        System.out.println("results: " + results.size());
    }

    @Test
    @Ignore
    public void testQueryBW(){
//        int size = 10;
//        System.out.println(">>>print BWList begin...");
//        List<Map<String,Object>> results = CardRiskDB.queryForList("select r.RuleID,r.OrderType,c.CheckName,c.CheckType,cv.CheckValue \n" +
//                "from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv \n" +
//                "on r.RuleID= cv.RuleID \n" +
//                "Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType \n" +
//                "Where r.Active='T' \n" +
//                "Order by r.OrderType,r.RuleID,c.CheckType");
//        for(int i=0;i<size;i++){
//            for(Iterator it=results.get(i).keySet().iterator();it.hasNext();){
//                Object key = it.next();
//                System.out.print(key + ":" + results.get(i).get(key)+" ");
//            }
//            System.out.println();
//        }
//        System.out.println("<<<print BWList end...");
    }

    @Test
    public void testBWFull(){
        /**
         *         <property name="driverClassName" value="${rule.driverClass}"/>
         <property name="url" value="${rule.jdbcUrl}"/>
         <property name="username" value="${rule.userName}"/>
         <property name="password" value="${rule.password}"/>
         <property name="maxIdle" value="5" />
         <property name="maxActive" value="50" />
         */
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://devdb.dev.sh.ctriptravel.com:28747;database=CardRiskDB;integratedSecurity=false");
        dataSource.setUsername("uws_AllInOneKey_dev");
        dataSource.setPassword("!QAZ@WSX1qaz2wsx");
        dataSource.setMaxIdle(5);
        dataSource.setMaxActive(50);
        JdbcTemplate CardRiskDB = new JdbcTemplate(dataSource);


        List<Map<String,Object>> bwList = CardRiskDB.queryForList("select r.RuleID,ISNULL(r.OrderType,0) OrderType ,c.CheckName,c.CheckType,cv.CheckValue,ISNULL(r.Sdate,CONVERT(varchar(100), GETDATE(), 121)) Sdate,\n" +
                "ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark\n" +
                "                                from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv\n" +
                "                                on r.RuleID= cv.RuleID\n" +
                "                                Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType\n" +
                "                                Where r.Active='T'\n" +
                "                                Order by r.OrderType,r.RuleID,c.CheckType,c.CheckName");
        Map<Integer,List<Map<String,Object>>> map = new HashMap<Integer, List<Map<String, Object>>>();
        List<RuleStatement> bwfull = new ArrayList<RuleStatement>();
        for(Map item : bwList){
            Integer map_key = Integer.valueOf(item.get("RuleID").toString());
            if(map.containsKey(map_key)){
                map.get(map_key).add(item);
            }else{
                List<Map<String,Object>> map_value = new ArrayList<Map<String, Object>>();
                map_value.add(item);
                map.put(map_key,map_value);
            }
        }
        for(Iterator<Integer> it = map.keySet().iterator();it.hasNext();){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Integer ruleID = it.next();
            RuleStatement ruleStatement = new RuleStatement();
            bwfull.add(ruleStatement);
            ruleStatement.setRuleID(ruleID);
            List<RuleTerm> list = new ArrayList<RuleTerm>();
            ruleStatement.setRuleTerms(list);
            for(Map<String,Object> item : map.get(ruleID)){
                try {
                    ruleStatement.setEffectDate(sdf.parse(item.get("Edate").toString()));
                    ruleStatement.setExpireDate(sdf.parse(item.get("Sdate").toString()));
                } catch (ParseException e) {
//                    logger.error("",e);
                }
                ruleStatement.setOrderType(Integer.valueOf(item.get("OrderType").toString()));
                ruleStatement.setRemark(item.get("Remark").toString());
                ruleStatement.setRiskLevel(Integer.valueOf(item.get("RiskLevel").toString()));

                RuleTerm term = new RuleTerm(item.get("CheckName").toString(),item.get("CheckType").toString(),item.get("CheckValue").toString());
                list.add(term);
            }
        }

        for(RuleStatement item : bwfull){
            System.out.println(">>>");
            System.out.println("ruleid:"+item.getRuleID()+",effecteddate:"+item.getEffectDate()+",risklevel:"+item.getRiskLevel());

            for(RuleTerm term: item.getRuleTerms()){
                System.out.println("---term start---");
                System.out.println("fieldname:"+term.getFieldName()+",op:"+term.getOperator()+",matchvalue:"+term.getMatchValue());
                System.out.println("---term end---");
            }

            System.out.println("<<<");

        }
    }
}

package com.ctrip.infosec.flowtable4j.service;

import com.ctrip.flowtable4j.core.blackList.BWManager;
import com.ctrip.flowtable4j.core.blackList.RuleStatement;
import com.ctrip.flowtable4j.core.blackList.RuleTerm;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thyang on 2015/3/18 0018.
 */

public class BWRuleTest {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @Test
    public void testAddRule()
    {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://172.16.226.71:1433;database=CardRiskDB;integratedSecurity=false");
        dataSource.setUsername("tester");
        dataSource.setPassword("tester");
        dataSource.setMaxIdle(5);
        dataSource.setMaxActive(50);
        JdbcTemplate CardRiskDB = new JdbcTemplate(dataSource);

        int size = 10;
        System.out.println(">>>print BWList begin..." + new Date().toString());
        List<Map<String,Object>>  results =CardRiskDB.queryForList("select r.RuleID,ISNULL(r.OrderType,0) ,c.CheckName,c.CheckType,cv.CheckValue,ISNULL(r.Sdate,CONVERT(varchar(100), GETDATE(), 121)),ISNULL(r.Sdate,'9999-12-31 23:59:59.997'),r.RiskLevel,r.Remark\n" +
                "                from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv\n" +
                "                on r.RuleID= cv.RuleID\n" +
                "                Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType\n" +
                "                Where r.Active='T'\n" +
                "                Order by r.OrderType,r.RuleID,c.CheckType,c.CheckName");
        System.out.println(">>>print BWList end..." + new Date().toString());
        Map<Integer,List<Map<String,Object>>> map = new HashMap<Integer, List<Map<String, Object>>>();
        List<RuleStatement> bwfull = new ArrayList<RuleStatement>();
        for(Map item : results){
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

                }
                ruleStatement.setOrderType(Integer.valueOf(item.get("OrderType").toString()));
                ruleStatement.setRemark(item.get("Remark").toString());
                ruleStatement.setRiskLevel(Integer.valueOf(item.get("RiskLevel").toString()));

                RuleTerm term = new RuleTerm(item.get("CheckName").toString(),item.get("CheckType").toString(),item.get("CheckValue").toString());
                list.add(term);
            }
        }
        BWManager.addRule(bwfull);
    }

    @Test
    public void  testRemoveRule()
    {

    }

    @Test
    public void  testVerify(){

    }
}

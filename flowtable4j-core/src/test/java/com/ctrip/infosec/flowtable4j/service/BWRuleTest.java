package com.ctrip.infosec.flowtable4j.service;

import com.ctrip.flowtable4j.core.blackList.*;
import com.google.common.base.Stopwatch;
import org.apache.commons.dbcp.BasicDataSource;
import org.drools.marshalling.impl.ProtobufMessages;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        dataSource.setUrl("jdbc:sqlserver://devdb.dev.sh.ctriptravel.com:28747;database=CardRiskDB;integratedSecurity=false");
        dataSource.setUsername("uws_AllInOneKey_dev");
        dataSource.setPassword("!QAZ@WSX1qaz2wsx");

        dataSource.setMaxIdle(5);
        dataSource.setMaxActive(50);
        JdbcTemplate CardRiskDB = new JdbcTemplate(dataSource);

        Stopwatch sw=Stopwatch.createStarted();
        List<Map<String,Object>> bwList = CardRiskDB.queryForList(
                "Select r.RuleID,ISNULL(r.OrderType,0) OrderType ,c.CheckName,c.CheckType," +
                "cv.CheckValue,ISNULL(r.Sdate,CONVERT(varchar(100), GETDATE(), 121)) Sdate," +
                "ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark " +
                "From dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv " +
                "      on r.RuleID= cv.RuleID " +
                "      Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType " +
                "Where r.Active='T'" +
                "Order by r.RuleID,c.CheckType,c.CheckName");
        sw.stop();
        System.out.println("Get data from Db " + sw.elapsed(TimeUnit.MILLISECONDS));
        sw.reset();
        sw.start();
        List<RuleStatement> bwfull = new ArrayList<RuleStatement>();
        RuleStatement currentRule=null;
        Integer  prevId=-1;
        RuleTerm term;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        for (Iterator<Map<String,Object>> it = bwList.iterator();it.hasNext();){
             Map<String,Object> rule = it.next();
             Integer ruleId = Integer.valueOf(rule.get("RuleID").toString());
             term =new RuleTerm(rule.get("CheckName").toString(),rule.get("CheckType").toString(),rule.get("CheckValue").toString());
             if(prevId.equals(ruleId)){
                 currentRule.getRuleTerms().add(term);
             } else{
                 currentRule = new RuleStatement();
                 currentRule.setRuleTerms(new ArrayList<RuleTerm>());
                 try {
                     currentRule.setRuleID(ruleId);
                     currentRule.setEffectDate(sdf.parse(rule.get("Sdate").toString()));
                     currentRule.setExpireDate(sdf.parse(rule.get("Edate").toString()));
                     currentRule.setOrderType(Integer.valueOf(rule.get("OrderType").toString()));
                     currentRule.setRemark(rule.get("Remark").toString());
                     currentRule.setRiskLevel(Integer.valueOf(rule.get("RiskLevel").toString()));
                     currentRule.getRuleTerms().add(term);
                     bwfull.add(currentRule);
                     prevId = ruleId;
                 }
                 catch (ParseException ex)
                 {
                     //
                 }
             }
        }

        sw.stop();
        System.out.println("Build list from Map " + sw.elapsed(TimeUnit.MILLISECONDS));
        sw.reset();
        sw.start();
        BWManager.addRule(bwfull);
        sw.stop();
        System.out.println("Add list to Rule Engine " + sw.elapsed(TimeUnit.MILLISECONDS));
        BWFact fact = new BWFact();
        fact.setOrderType(1);
        HashMap<String,Object> content= new HashMap<String, Object>();
        content.put("MobilePhone","13826520055");
        content.put("PassengerCardID","|WH917775|WH88892|");
        content.put("UserIP","61.4.125.84");
        content.put("Uid","2880000000");
        content.put("CCardNoCode","6539E4337D56E2AA3A8F460BE54341F6");
        List<BWResult>  results= new ArrayList<BWResult>();
        fact.setContent(content);
        System.out.println("Start to check white rules.....");
        sw.reset();
        sw.start();
        if(BWManager.checkWhite(fact, results)){
            for (BWResult r:results) {
                System.out.println(r.toString());
            }
        }
        sw.stop();
        System.out.println("Check white rules finished");
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));

        results.clear();
        sw.reset();
        sw.start();
        System.out.println("Check black rules finished");
        if(BWManager.checkBlack(fact, results)){
            for (BWResult r:results) {
                System.out.println(r.toString());
            }
        }
        sw.stop();
        System.out.println("Check black rules finished");
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void  testRemoveRule()
    {

    }

    @Test
    public void  testVerify(){

    }
}

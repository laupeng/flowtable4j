package com.ctrip.infosec.flowtable4j.jobws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/3/13.
 */
@Component
public class SimpleRuleGetter implements RuleGetter {
    @Autowired
    @Qualifier("cardRiskDBTemplate")
    private JdbcTemplate cardRiskDB;

    /**
     * 获取近5分钟内改变的rule
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> bwIncrement() {
        return cardRiskDB.queryForList("select r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType,cv.CheckValue,ISNULL(r.Sdate,CONVERT(varchar(100), GETDATE(), 121)) Sdate,ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark,r.RuleIDName\n" +
                "                                from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv\n" +
                "                                on r.RuleID= cv.RuleID\n" +
                "                                Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType\n" +
                "                                Where r.DataChange_LastTime>=dateadd(MINUTE,-5,getdate()) and r.Active='T'\n" +
                "                                Order by r.OrderType,r.RuleID,c.CheckType,c.CheckName");
    }

    /**
     * 获取所有有效的 bw rule
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> bwFull() {
        return cardRiskDB.queryForList("select r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType,cv.CheckValue,ISNULL(r.Sdate,CONVERT(varchar(100), GETDATE(), 121)) Sdate,ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark,r.RuleIDName\n" +
                "                                                from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv\n" +
                "                                                on r.RuleID= cv.RuleID\n" +
                "                                                Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType\n" +
                "                                                Where r.Active='T'\n" +
                "                                                Order by r.OrderType,r.RuleID,c.CheckType,c.CheckName");
    }

    @Override
    public List<Map<String, Object>> ruleMatch() {
        return cardRiskDB.queryForList(
                "select f.FlowRuleID,f.RuleName,f.RiskLevel,f.OrderType,f.PrepayType,f.RuleDesc,d.ColumnName,r.MatchType,r.MatchValue,d.TableName\n" +
                        "   From dbo.InfoSecurity_RuleMatchField r\n" +
                        "   join dbo.Def_RuleMatchField d on r.FieldID= d.FieldID\n" +
                        "   join dbo.InfoSecurity_FlowRule f on f.FlowRuleID=r.FlowRuleID WHERE f.Active='T' ORDER BY f.FlowRuleID");
    }

    @Override
    public List<Map<String, Object>> ruleStatistic() {
        return cardRiskDB.queryForList("select f.FlowRuleID,f.RuleName,f.RiskLevel,f.OrderType,f.PrepayType,f.RuleDesc,d.ColumnName as KeyColumnName,\n" +
                "d1.ColumnName as MatchColumnName,d2.StatisticTableName as KeyTableName,r.MatchType,r.MatchValue,r.StatisticType,r.StartTimeLimit,r.TimeLimit,r.SqlValue\n" +
                "   From  dbo.InfoSecurity_RuleStatistic r \n" +
                "         join dbo.Def_RuleMatchField d on r.KeyFieldID=d.FieldID\n" +
                "         join dbo.Def_RuleMatchField d1 on r.MatchFieldID=d1.FieldID\n" +
                "         join dbo.Def_RuleStatisticTable d2 on r.StatisticTableID=d2.StatisticTableID\n" +
                "         join dbo.InfoSecurity_FlowRule f on r.FlowRuleID=f.FlowRuleID WHERE f.Active='T' ORDER BY f.FlowRuleID");
    }

    @Override
    public List<Map<String,Object>> getRuleValue(){
        return cardRiskDB.queryForList("select r.FlowRuleID,d.ColumnName,r.MatchType,r.MatchValue\n" +
                "From dbo.InfoSecurity_RuleMatchField r (NOLOCK)\n" +
                "join dbo.Def_RuleMatchField d (NOLOCK) on r.FieldID= d.FieldID\n" +
                "where CHARINDEX('F',r.MatchType)<>1 ORDER BY r.FlowRuleID ,r.MatchIndex");
    }

    @Override
    public List<Map<String,Object>> getRuleField(){
        return cardRiskDB.queryForList("select r.FlowRuleID,d.ColumnName,r.MatchType,r.MatchValue\n" +
                "From dbo.InfoSecurity_RuleMatchField r (NOLOCK)\n" +
                "join dbo.Def_RuleMatchField d (NOLOCK) on r.FieldID= d.FieldID\n" +
                "where CHARINDEX('F',r.MatchType)=1 ORDER BY r.FlowRuleID ,r.MatchIndex");
    }

    @Override
    public List<Map<String,Object>> getCountSql(){
        return cardRiskDB.queryForList("select r.FlowRuleID,d.ColumnName as KeyColumnName,\n" +
                "d1.ColumnName as MatchColumnName,r.MatchType,r.MatchValue,r.StatisticType,r.StartTimeLimit,r.TimeLimit,r.SqlValue\n" +
                "From  dbo.InfoSecurity_RuleStatistic (NOLOCK) r\n" +
                "join dbo.Def_RuleMatchField d (NOLOCK) on r.KeyFieldID=d.FieldID\n" +
                "join dbo.Def_RuleMatchField d1 (NOLOCK) on r.MatchFieldID=d1.FieldID\n" +
                "ORDER BY r.FlowRuleID,r.MatchIndex");
    }

    @Override
    public List<Map<String,Object>> getFlowRuleMaster(){
        return cardRiskDB.queryForList("SELECT FlowRuleID,RuleName,RiskLevel,OrderType,PrepayType,RuleDesc \n" +
                "FROM InfoSecurity_FlowRule (NOLOCK) where Active='T' ORDER BY FlowRuleID , MatchIndex ");
    }
}

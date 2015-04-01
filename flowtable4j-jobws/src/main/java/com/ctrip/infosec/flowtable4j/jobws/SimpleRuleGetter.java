package com.ctrip.infosec.flowtable4j.jobws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    private final int INTERVAL = 5 ;

    /**
     * 获取近5分钟内改变的黑白名单
     * 包括下线与上线的
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> bwIncrement() {
        Date date = new Date(System.currentTimeMillis() - INTERVAL * 60 * 1000- 1000);
        return cardRiskDB.queryForList(
                " Select r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType," +
                        " cv.CheckValue,ISNULL(r.Sdate,'1900-01-01 00:00:00.000') Sdate," +
                        " ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark,r.RuleIDName,r.Active \n" +
                        " From CardRisk_BlackListRule r (nolock) inner join CardRisk_BlackListRuleColumnValue cv (nolock) on r.RuleID= cv.RuleID \n" +
                        "      Inner join CardRisk_BlackListColumn c (nolock) on cv.ProcessType=c.ProcessType \n" +
                        " Where r.DataChange_LastTime>=? \n" +
                        " Order by r.OrderType,r.RuleID,c.CheckType,c.CheckName", new Object[]{date});
    }

    /**
     * 获取所有有效的黑白名单
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> bwFull() {
        return cardRiskDB.queryForList(
                " Select r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType," +
                        " cv.CheckValue,ISNULL(r.Sdate,'1900-01-01 00:00:00.000') Sdate," +
                        " ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark,r.RuleIDName \n" +
                        " From CardRisk_BlackListRule r (nolock) inner join CardRisk_BlackListRuleColumnValue cv (nolock) on r.RuleID= cv.RuleID \n" +
                        "    Inner join CardRisk_BlackListColumn c (nolock) on cv.ProcessType=c.ProcessType \n" +
                        " Where r.Active='T' \n" +
                        " Order by r.OrderType,r.RuleID,c.CheckType,c.CheckName");
    }

    /**
     * 流量规则-字段与值比较部分
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getRuleValue() {
        return cardRiskDB.queryForList(
                " Select r.FlowRuleID,d.ColumnName,r.MatchType,r.MatchValue\n" +
                        " From InfoSecurity_RuleMatchField r (NOLOCK)\n" +
                        "  inner join Def_RuleMatchField d (NOLOCK) on r.FieldID= d.FieldID\n" +
                        " Where CHARINDEX('F',r.MatchType)<>1 " +
                        " ORDER BY r.FlowRuleID ,r.MatchIndex");
    }

    /**
     * 流量规则-字段比较部分，操作符以 F开头
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getRuleField() {
        return cardRiskDB.queryForList(
                " Select r.FlowRuleID,d.ColumnName,r.MatchType,r.MatchValue\n" +
                        " From InfoSecurity_RuleMatchField r (NOLOCK)\n" +
                        "  inner join Def_RuleMatchField d (NOLOCK) on r.FieldID= d.FieldID\n" +
                        " Where CHARINDEX('F',r.MatchType)=1 " +
                        " ORDER BY r.FlowRuleID ,r.MatchIndex");
    }

    /**
     * 流量规则-流量Count部分     *
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getCountSql() {
        return cardRiskDB.queryForList(
                " Select r.FlowRuleID,d.ColumnName as KeyColumnName, \n" +
                        " d1.ColumnName as MatchColumnName,r.MatchType,r.MatchValue,r.StatisticType,r.StartTimeLimit,r.TimeLimit,r.SqlValue\n" +
                        " From  InfoSecurity_RuleStatistic (NOLOCK) r\n" +
                        "  inner join Def_RuleMatchField d (NOLOCK) on r.KeyFieldID=d.FieldID\n" +
                        "  inner join Def_RuleMatchField d1 (NOLOCK) on r.MatchFieldID=d1.FieldID\n" +
                        " ORDER BY r.FlowRuleID,r.MatchIndex");
    }

    /**
     * 获取所有Active的流量规则
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getFlowRuleMaster() {
        return cardRiskDB.queryForList(
                " SELECT FlowRuleID,RuleName,RiskLevel,OrderType,PrepayType,RuleDesc \n" +
                        " FROM InfoSecurity_FlowRule (NOLOCK)" +
                        " Where Active='T'" +
                        " ORDER BY FlowRuleID , MatchIndex ");
    }
}

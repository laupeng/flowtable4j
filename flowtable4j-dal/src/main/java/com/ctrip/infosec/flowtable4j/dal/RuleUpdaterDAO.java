package com.ctrip.infosec.flowtable4j.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/3/13.
 */
@Component
public class RuleUpdaterDAO {
    @Autowired
    private CardRiskService cardRiskService;
    private final int INTERVAL = 5;

    /**
     * 获取近5分钟内改变的黑白名单
     * 包括下线与上线的
     *
     * @return
     */
    public List<Map<String, Object>> getUpdateBWRule() {
        Date date = new Date(System.currentTimeMillis() - INTERVAL * 60 * 1000 - 1000);
        String sql = "SELECT r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType," +
                "cv.CheckValue,ISNULL(r.Sdate,'1900-01-01 00:00:00.000') Sdate," +
                "ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark,r.RuleIDName,r.Active " +
                "FROM CardRisk_BlackListRule r (nolock) " +
                " INNER JOIN CardRisk_BlackListRuleColumnValue cv (nolock) on r.RuleID= cv.RuleID " +
                " INNER JOIN CardRisk_BlackListColumn c (nolock) on cv.ProcessType=c.ProcessType " +
                "WHERE  r.DataChange_LastTime>=? " +
                "ORDER BY r.OrderType,r.RuleID,c.CheckType,c.CheckName";
        return cardRiskService.queryForList(sql, new Object[]{date});
    }

    /**
     * 获取所有有效的黑白名单
     *
     * @return
     */
    public List<Map<String, Object>> getAllBWRule() {
        String sql = "SELECT r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType," +
                "cv.CheckValue,ISNULL(r.Sdate,'1900-01-01 00:00:00.000') Sdate," +
                "ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,r.Remark,r.RuleIDName " +
                "FROM CardRisk_BlackListRule r (nolock) INNER JOIN CardRisk_BlackListRuleColumnValue cv (nolock) on r.RuleID= cv.RuleID " +
                "  INNER JOIN CardRisk_BlackListColumn c (nolock) on cv.ProcessType=c.ProcessType " +
                "WHERE  r.Active='T' " +
                "ORDER BY r.OrderType,r.RuleID,c.CheckType,c.CheckName";
        return cardRiskService.queryForList(sql);
    }

    /**
     * 流量规则-字段与值比较部分
     *
     * @return
     */

    public List<Map<String, Object>> getValueMatchTerms() {
        String sql = "SELECT r.FlowRuleID,d.ColumnName,r.MatchType,r.MatchValue " +
                "FROM InfoSecurity_RuleMatchField r (NOLOCK) " +
                " INNER JOIN Def_RuleMatchField d (NOLOCK) on r.FieldID= d.FieldID " +
                "WHERE  CHARINDEX('F',r.MatchType)<>1 " +
                "ORDER BY r.FlowRuleID ,r.MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    /**
     * 流量规则-字段比较部分，操作符以 F开头
     *
     * @return
     */
    public List<Map<String, Object>> getFieldMatchTerms() {
        String sql = "SELECT r.FlowRuleID,d.ColumnName,r.MatchType,r.MatchValue " +
                "FROM InfoSecurity_RuleMatchField r (NOLOCK) " +
                " INNER JOIN Def_RuleMatchField d (NOLOCK) on r.FieldID= d.FieldID " +
                "WHERE  CharIndex('F',r.MatchType)=1 " +
                "ORDER BY r.FlowRuleID ,r.MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    /**
     * 流量规则-流量Count部分     *
     *
     * @return
     */
    public List<Map<String, Object>> getCounterMatchTerms() {
        String sql = "SELECT r.FlowRuleID,d.ColumnName as KeyColumnName, " +
                "d1.ColumnName as MatchColumnName,r.MatchType,r.MatchValue,r.StatisticType,r.StartTimeLimit,r.TimeLimit,r.SqlValue " +
                "FROM  InfoSecurity_RuleStatistic (NOLOCK) r " +
                " INNER JOIN Def_RuleMatchField d (NOLOCK) on r.KeyFieldID=d.FieldID " +
                " INNER JOIN Def_RuleMatchField d1 (NOLOCK) on r.MatchFieldID=d1.FieldID " +
                "ORDER BY r.FlowRuleID,r.MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    /**
     * 获取所有Active的流量规则
     *
     * @return
     */
    public List<Map<String, Object>> getFlowRuleMaster() {
        String sql = "SELECT FlowRuleID,RuleName,RiskLevel,OrderType,PrepayType,RuleDesc " +
                "FROM InfoSecurity_FlowRule (NOLOCK) " +
                "WHERE  Active='T' " +
                "ORDER BY FlowRuleID , MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    public List<Map<String, Object>> getPayadaptRuleMaster() {
        String sql = "SELECT PayAdapterRuleID,RuleName,RiskLevel,OrderType,SceneType,PaymentStatus,IsCheckAccount,RuleDesc " +
                "FROM InfoSecurity_PayAdapterRule (NOLOCK) " +
                "WHERE Active='T' " +
                "ORDER BY PayAdapterRuleID,MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    public List<Map<String, Object>> getPayadaptValueMatchTerms() {
        String sql = "SELECT p.PayAdapterRuleID,d.ColumnName ,p.MatchType,p.MatchValue " +
                "FROM InfoSecurity_PayAdapterRuleMatchField p(NOLOCK) " +
                " INNER JOIN Def_RuleMatchField d (NOLOCK) on p.FieldID=d.FieldID  " +
                "WHERE  CharIndex('F',p.MatchType)<>1 ORDER BY p.PayAdapterRuleID, p.MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    public List<Map<String, Object>> getPayadaptCounterMatchTerms() {
        String sql = "SELECT r.FlowRuleID,d.ColumnName as KeyColumnName,d1.ColumnName as MatchColumnName," +
                "r.MatchType,r.MatchValue,r.StatisticType,r.StartTimeLimit,r.TimeLimit,r.SqlValue " +
                "FROM  InfoSecurity_PayAdapterRuleStatistic (NOLOCK) " +
                "  INNER JOIN Def_RuleMatchField d (NOLOCK) on r.KeyFieldID=d.FieldID " +
                "  INNER JOIN Def_RuleMatchField d1 (NOLOCK) on r.MatchFieldID=d1.FieldID " +
                "ORDER BY r.FlowRuleID,r.MatchIndex";
        return cardRiskService.queryForList(sql);
    }

    public List<Map<String, Object>> getPayadaptFieldMatchTerms() {
        String sql = "SELECT p.PayAdapterRuleID,d.ColumnName ,p.MatchType,p.MatchValue " +
                "FROM dbo.InfoSecurity_PayAdapterRuleMatchField p(NOLOCK) " +
                " INNER JOIN Def_RuleMatchField d (NOLOCK) on p.FieldID=d.FieldID  " +
                "WHERE  CHARINDEX('F',p.MatchType)=1 " +
                "ORDER BY p.PayAdapterRuleID, p.MatchIndex";
        return cardRiskService.queryForList(sql);
    }


}

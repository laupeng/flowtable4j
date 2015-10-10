package com.ctrip.infosec.flowtable4j.t3afs.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 规则更新，读取数据库配置
 * Created by zhangsx on 2015/3/13.
 */
@Component
public class RuleUpdaterDAO {
    @Autowired
    private CardRiskDbService cardRiskDbService;
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
                "ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,c.Remark,r.RuleIDName,r.Active " +
                "FROM CardRisk_BlackListRule r (nolock) " +
                " INNER JOIN CardRisk_BlackListRuleColumnValue cv (nolock) on r.RuleID= cv.RuleID " +
                " INNER JOIN CardRisk_BlackListColumn c (nolock) on cv.ProcessType=c.ProcessType " +
                "WHERE  r.DataChange_LastTime>=? " +
                "ORDER BY r.OrderType,r.RuleID,c.CheckType,c.CheckName";
        return cardRiskDbService.queryForList(sql, new Object[]{date});
    }

    /**
     * 获取所有有效的黑白名单
     *
     * @return
     */
    public List<Map<String, Object>> getAllBWRule() {
        String sql = "SELECT r.RuleID,ISNULL(r.OrderType,0) OrderType,c.CheckName,c.CheckType," +
                "cv.CheckValue,ISNULL(r.Sdate,'1900-01-01 00:00:00.000') Sdate," +
                "ISNULL(r.Edate,'9999-12-31 23:59:59.997') Edate,r.RiskLevel,c.Remark,r.RuleIDName " +
                "FROM CardRisk_BlackListRule r (nolock) INNER JOIN CardRisk_BlackListRuleColumnValue cv (nolock) on r.RuleID= cv.RuleID " +
                "  INNER JOIN CardRisk_BlackListColumn c (nolock) on cv.ProcessType=c.ProcessType " +
                "WHERE  r.Active='T' " +
                "ORDER BY r.OrderType,r.RuleID,c.CheckType,c.CheckName";
        return cardRiskDbService.queryForList(sql);
    }

}

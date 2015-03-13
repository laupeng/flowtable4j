package com.ctrip.flowtable4j.core.ruleUpdateJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;

/**
 * Created by zhangsx on 2015/3/13.
 */
public class SimpleRuleGetter implements RuleGetter {
    @Autowired
    @Qualifier("CardRiskDB")
    private JdbcTemplate cardRiskDB;

    @Override
    public Collection<?> bwIncrement() {
        return cardRiskDB.queryForList("");
    }

    @Override
    public Collection<?> bwFull() {
        return cardRiskDB.queryForList("select r.RuleID,r.OrderType,c.CheckName,c.CheckType,cv.CheckValue \n" +
                "from dbo.CardRisk_BlackListRule r inner join dbo.CardRisk_BlackListRuleColumnValue cv \n" +
                "on r.RuleID= cv.RuleID \n" +
                "Inner join dbo.CardRisk_BlackListColumn c on cv.ProcessType=c.ProcessType \n" +
                "Where r.Active='T' \n" +
                "Order by r.OrderType,r.RuleID,c.CheckType");
    }

    @Override
    public Collection<?> ruleIncrement() {
        return cardRiskDB.queryForList("");
    }

    @Override
    public Collection<?> ruleFull() {
        return cardRiskDB.queryForList("");
    }
}

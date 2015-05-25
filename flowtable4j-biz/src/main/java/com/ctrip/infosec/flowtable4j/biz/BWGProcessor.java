package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.accountsecurity.AccountBWGRuleHandle;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsx on 2015/5/19.
 */
@Component
public class BWGProcessor {

    @Autowired
    @Qualifier("pciAccountRiskDetailDBTemplate")
    private JdbcTemplate pciTemplate;

    @Autowired
    private AccountBWGRuleHandle accountBWGRuleHandle;

    private static Logger logger = LoggerFactory.getLogger(BWGProcessor.class);

    /**
     * 新增黑白名单
     * @param rules
     */
    public void setBWGRule(List<RuleContent> rules) {
        accountBWGRuleHandle.setBWGRule(rules);
    }

    /**
     * 删除黑白名单
     * @param rules
     */
    public void removeBWGRule(List<RuleContent> rules) {
        accountBWGRuleHandle.removeBWGRule(rules);
    }

    /**
     * 加载数据库中黑白名单到Redis中
     * @param recId
     */
    public void loadExistBWGRule(final long recId) {
        Long maxRecId = new Long(recId);
        int totalRecs = 0;
        final List<Long> list = new ArrayList<Long>(1);
        list.add(maxRecId);
        while (true) {
            List<RuleContent> results = pciTemplate.query(
                    "SELECT top 1000 RecId, ExpiryDate,CheckType,SceneType, CheckValue,ResultLevel " +
                            "FROM   AccountSecurity_BWGList (nolock) " +
                            "WHERE  RecID < ? AND IsActive=1  " +
                            "AND SceneType IN ('PAYMENT-CONF-LIPIN','PAYMENT-CONF-CC','PAYMENT-CONF-CCC','PAYMENT-CONF-CTRIPAY'," +
                            "'CREDIT-EXCHANGE','CTRIPAY-CASHOUT','CASH-EXCHANGE','PAYMENT-CONF-DCARD','PAYMENT-CONF-ALIPAY'," +
                            "'PAYMENT-CONF-CASH','PAYMENT-CONF-WEIXIN','PAYMENT-CONF-EBANK','CREDIT-GUARANTEE')" +
                            " ORDER by RecID desc",
                    new ResultSetExtractor<List<RuleContent>>() {
                        @Override
                        public List<RuleContent> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                            List<RuleContent> results = new ArrayList<RuleContent>();
                            while (resultSet.next()) {
                                RuleContent content = new RuleContent();
                                content.setCheckType(resultSet.getString("CheckType"));
                                content.setCheckValue(resultSet.getString("CheckValue"));
                                content.setExpiryDate(resultSet.getString("ExpiryDate"));
                                content.setResultLevel(resultSet.getInt("ResultLevel"));
                                content.setSceneType(resultSet.getString("SceneType"));
                                results.add(content);
                                list.set(0, resultSet.getLong("RecId"));
                            }
                            return results;
                        }
                    },
                    maxRecId);

            if (results != null && results.size() > 0) {
                totalRecs += results.size();
                accountBWGRuleHandle.setBWGRule4Job(results);
                maxRecId = list.get(0);
            } else {
                break;
            }
        }
        logger.info("Total load BWGRule from accountRiskControl:" + totalRecs);
    }
}

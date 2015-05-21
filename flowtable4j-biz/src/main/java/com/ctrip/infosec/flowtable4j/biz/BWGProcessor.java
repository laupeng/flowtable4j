package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private PaymentViaAccount paymentViaAccount;
    private static Logger logger = LoggerFactory.getLogger(BWGProcessor.class);
    public void setBWGRule(List<RuleContent> rules){
        paymentViaAccount.setBWGRule(rules);
    }

    public void removeBWGRule(List<RuleContent> rules){
        paymentViaAccount.removeBWGRule(rules);
    }

    public void syncBWG(final String datetime){
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final int pageSize = 500;
        for(int page=0;;page++){
            final int size = page*pageSize;
            final List<RuleContent> results = pciTemplate.query("SELECT RecId, RuleKey, ExpiryDate, DataChange_LastTime, IsActive, CreateDate, RuleContent, LastOper, CheckType, SceneType, CheckValue, RuleRemark, ResultLevel, RuleID\n" +
                    "FROM (\n" +
                    "\t\tSELECT ROW_NUMBER() OVER(ORDER BY RecId DESC) rownum,*\n" +
                    "\t\tFROM AccountSecurity_BWGList WHERE IsActive=1 AND CreateDate<? AND SceneType NOT IN \n" +
                    "('LOGIN-SITE',\n" +
                    "'LOGIN-CLIENT',\n" +
                    "'BRANDS-USE',\n" +
                    "'BRANDS-LOGIN',\n" +
                    "'CFX-BLOCKIP',\n" +
                    "'LOGIN-SITE-TRAIN',\n" +
                    "'LOGIN-SITE-EBK',\n" +
                    "'LOGIN-SITE-MICE',\n" +
                    "'CFX-BLOCKCLIENT',\n" +
                    "'XGW',\n" +
                    "'GOLF-GW',\n" +
                    "'LOGIN-SITE-WELFARE',\n" +
                    "'REGIST-SITE',\n" +
                    "'LOGIN-SITE-KZT',\n" +
                    "'LOGIN-SITE-SKYSEA',\n" +
                    "'SKYSEA_REGIST_APPLY',\n" +
                    "'OFFLINE-LOGIN')\n" +
                    "\t) a\n" +
                    "\tWHERE a.rownum>"+size+" AND a.rownum<"+(size+500), new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                    try {
                        preparedStatement.setTimestamp(1, new Timestamp(sdf.parse(datetime).getTime()));
                    } catch (ParseException e) {
                        logger.error("时间转换失败");
                        throw new RuntimeException("ParseException");
                    }
                }
            }, new ResultSetExtractor<List<RuleContent>>() {
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
                        logger.info("sync bwg recid:" + String.valueOf(resultSet.getInt("RecId")) + "success");
                        results.add(content);
                    }
                    return results;
                }
            });
            if(results==null||results.size()==0){
                return;
            }
            SimpleStaticThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    paymentViaAccount.setBWGRule4Job(results);
                }
            });
            logger.info("sync bwg size:"+results.size());
        }
    }

}


package com.ctrip.infosec.flowtable4j.flowdata.bwrule;

import com.google.common.base.Stopwatch;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thyang on 2015/3/18 0018.
 */

public class BWRuleTest {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

     @Test
     public void testSumRule()
    {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://devdb.dev.sh.ctriptravel.com:28747;database=RiskCtrlPreProcDB;integratedSecurity=false");
        dataSource.setUsername("uws_AllInOneKey_dev");
        dataSource.setPassword("!QAZ@WSX1qaz2wsx");

        dataSource.setMaxIdle(5);
        dataSource.setMaxActive(50);
        NamedParameterJdbcTemplate CardRiskDB = new NamedParameterJdbcTemplate(dataSource);

        String sqlStatement = "select Amount from CTRIP_TS_CCardNoCode_Amount (nolock) where CCardNoCode = @CCardNoCode and CreateDate>=@StartTimeLimit and CreateDate<=@TimeLimit";

        Map<String,Object> paramMap = new HashMap<String, Object>();
        Set countSet = new HashSet();
        sqlStatement = sqlStatement.replace('@', ':').toUpperCase();
        int fromOffset=-24*60*380;
        long nowMillis = System.currentTimeMillis();
        long startMills = nowMillis + (long)fromOffset * 60 * 1000;
        long timeLimit = nowMillis;
        String whereField = "CCardNoCode";
        Object whereFieldValue="84ED6A9D9A87C54DFE30BDF7B0827A04";
        Date start = new Date(startMills);
        Date limit = new Date(timeLimit);
        paramMap.put(whereField.toUpperCase(), whereFieldValue);
        paramMap.put("STARTTIMELIMIT", start);
        paramMap.put("TIMELIMIT", limit);
        Stopwatch stopwatch = Stopwatch.createStarted();

        List<Map<String, Object>> results = CardRiskDB.queryForList(sqlStatement, paramMap);
        String countType="SUM";
        Object matchFieldValue=2000;
        stopwatch.stop();
        if ("SUM".equals(countType)) {
            double sum = 0d;
            if(matchFieldValue!=null){
                sum += Double.parseDouble(String.valueOf(matchFieldValue));
            }
            if (results != null && results.size() > 0) {
                if (results != null && results.size() > 0) {
                    String key = results.get(0).keySet().iterator().next();
                    for (Map<String, Object> item : results) {
                        String val = String.valueOf(item.get(key));
                        if (!"null".equals(val)) {
                            sum += Double.parseDouble(val);
                        }
                    }
                }
            }
            System.out.println(sum);
        }
        if ("COUNT".equals(countType)) {
            if (results != null && results.size() > 0) {
                String key = results.get(0).keySet().iterator().next();
                for (Map<String, Object> item : results) {
                    String val = String.valueOf(item.get(key));
                    if (!"null".equals(val)) {
                        countSet.add(val);
                    }
                }
                if (matchFieldValue != null) {
                    countSet.add(matchFieldValue.toString());
                }
                System.out.println(String.valueOf(countSet.size()));
            }
        }

        System.out.println("Check black rules finished");
    }

    @Test
    public void testAddRule()
    {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://devdb.dev.sh.ctriptravel.com:28747;database=RiskCtrlPreProcDB;integratedSecurity=false");
        dataSource.setUsername("uws_AllInOneKey_dev");
        dataSource.setPassword("!QAZ@WSX1qaz2wsx");

        dataSource.setMaxIdle(5);
        dataSource.setMaxActive(50);
        NamedParameterJdbcTemplate CardRiskDB = new NamedParameterJdbcTemplate(dataSource);

        String sqlStatement = "select CCardNoCode from CTRIP_FLT_CCardNoCode_OrderID(nolock)  where ReqID = 992 and CreateDate <= @StartTimeLimit";

        Set countSet = new HashSet();
        sqlStatement = sqlStatement.replace('@', ':').toUpperCase();
        Object whereFieldValue=602105156;
        Date limit = new Date(System.currentTimeMillis()-24 * 24 * 60 * 60 * 1000);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("STARTTIMELIMIT", limit, Types.TIMESTAMP);

        List<Map<String, Object>> results = CardRiskDB.queryForList(sqlStatement, params);
        String countType="COUNT";
        Object matchFieldValue="XXXX";

        if ("SUM".equals(countType)) {
            double sum = 0d;
            if(matchFieldValue!=null){
                sum += Double.parseDouble(String.valueOf(matchFieldValue));
            }
            if (results != null && results.size() > 0) {
                if (results != null && results.size() > 0) {
                    String key = results.get(0).keySet().iterator().next();
                    for (Map<String, Object> item : results) {
                        String val = String.valueOf(item.get(key));
                        if (!"null".equals(val)) {
                            sum += Double.parseDouble(val);
                        }
                    }
                }
            }
        }
        if ("COUNT".equals(countType)) {
            if (results != null && results.size() > 0) {
                String key = results.get(0).keySet().iterator().next();
                for (Map<String, Object> item : results) {
                    String val = String.valueOf(item.get(key));
                    if (!"null".equals(val)) {
                        countSet.add(val);
                    }
                }
                if (matchFieldValue != null) {
                    countSet.add(matchFieldValue.toString());
                }
                System.out.println(String.valueOf(countSet.size()));
            }
        }

        System.out.println("Check black rules finished");
    }
}

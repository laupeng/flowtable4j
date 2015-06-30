package com.ctrip.infosec.flowtable4j.dal;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by thyang on 2015/3/25 0025.
 */
@Component
public class Counter {

    @Autowired
    @Qualifier("flowDbNamedTemplate")
    private  NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(Counter.class);

    public  String getCounter(String countType, String sqlStatement, String whereField,
                                    Integer fromOffset, Integer toOffset, String matchFieldValue, String whereFieldValue) {

        long nowMillis = System.currentTimeMillis();
        long startMills = nowMillis + (long) fromOffset * 60 * 1000;
        long timeLimit = nowMillis + (long) toOffset * 60 * 1000;

        Timestamp start = new Timestamp(startMills);
        Timestamp limit = new Timestamp(timeLimit);

        MapSqlParameterSource params = new MapSqlParameterSource();
        SqlParameterValue value0 = new SqlParameterValue(Types.VARCHAR, whereFieldValue);
        SqlParameterValue value1 = new SqlParameterValue(Types.TIMESTAMP, start);
        SqlParameterValue value2 = new SqlParameterValue(Types.TIMESTAMP, limit);

        params.addValue(whereField, value0);
        params.addValue("starttimelimit", value1);
        params.addValue("timelimit", value2);
        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sqlStatement, params);

        if ("SUM".equals(countType)) {
            double sum = 0d;
            if (!Strings.isNullOrEmpty(matchFieldValue)) {
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
            return String.valueOf(sum);
        }
        if ("COUNT".equals(countType)) {
            Set<String> countSet = new HashSet<String>();
            if (!Strings.isNullOrEmpty(matchFieldValue)) {
                countSet.add(matchFieldValue.toLowerCase());
            }
            if (results != null && results.size() > 0) {
                String key = results.get(0).keySet().iterator().next();
                for (Map<String, Object> item : results) {
                    String val = String.valueOf(item.get(key)).toLowerCase();
                    if (!"null".equals(val)) {
                        countSet.add(val);
                    }
                }
            }
            return String.valueOf(countSet.size());
        }
        return "0";
    }
}

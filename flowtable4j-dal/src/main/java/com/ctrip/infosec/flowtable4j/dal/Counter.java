package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.sars.util.SpringContextHolder;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by thyang on 2015/3/25 0025.
 */
public class Counter {
    // @Resource(name = "riskCtrlPreProcDBNamedTemplate")
    private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(Counter.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS");
    static {
        namedParameterJdbcTemplate = SpringContextHolder.getBean("riskCtrlPreProcDBNamedTemplate");
    }

    public static String getCounter(String countType, String sqlStatement, String whereField,
                                    Integer fromOffset, Integer toOffset, Object matchFieldValue, Object whereFieldValue) {
        Map<String,Object> paramMap = new HashMap<String, Object>();
        Set countSet = new HashSet();
        sqlStatement = sqlStatement.replace('@', ':');
        long nowMillis = System.currentTimeMillis();
        long startMills = nowMillis + (long)fromOffset * 60 * 1000;
        long timeLimit = nowMillis + (long)toOffset * 60 * 1000;

        Date start = new Date(startMills);
        Date limit = new Date(timeLimit);
        paramMap.put(whereField, whereFieldValue);
        paramMap.put("StartTimeLimit", start);
        paramMap.put("TimeLimit", limit);
        Stopwatch stopwatch = Stopwatch.createStarted();

//        if ("COUNT".equals(countType)) {
//            sqlStatement = "select top 1000 " + sqlStatement.substring(7);
//        }

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sqlStatement, paramMap);
        logger.debug("sql:"+sqlStatement+",whereField:"+whereFieldValue+",StartTimeLimit:"+sdf.format(start)+"TimeLimit"+sdf.format(limit));
        stopwatch.stop();
        logger.info("get data from db costs : " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
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
            return String.valueOf(sum);
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
                return String.valueOf(countSet.size());
            }
        }
        return "0";
    }
}

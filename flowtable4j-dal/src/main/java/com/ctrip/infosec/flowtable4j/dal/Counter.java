package com.ctrip.infosec.flowtable4j.dal;

import ch.qos.logback.classic.PatternLayout;
import com.ctrip.infosec.sars.util.SpringContextHolder;
import com.google.common.base.Stopwatch;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by thyang on 2015/3/25 0025.
 */
public class Counter {
//    @Resource(name = "riskCtrlPreProcDBNamedTemplate")
    private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(Counter.class);
    static{
        namedParameterJdbcTemplate = SpringContextHolder.getBean("riskCtrlPreProcDBNamedTemplate");
    }
    public static String getCounter(String countType,String sqlStatement,String whereField,
                                      Integer fromOffset,Integer toOffset,Object matchFieldValue,Object whereFieldValue){
        Map paramMap = new HashMap();
        Set countSet = new HashSet();
        sqlStatement = sqlStatement.replace('@',':');
        long nowMillis = System.currentTimeMillis();
        paramMap.put(whereField,whereFieldValue);
        paramMap.put("StartTimeLimit",new Date(nowMillis+fromOffset*60*1000));
        paramMap.put("TimeLimit",new Date(nowMillis+toOffset*60*1000));
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<Map<String,Object>> results = namedParameterJdbcTemplate.queryForList(sqlStatement, paramMap);
        stopwatch.stop();
        logger.info("get data from db costs : "+stopwatch.elapsed(TimeUnit.MILLISECONDS)+"ms");
        if("SUM".equals(countType)){
            if(results!=null&&results.size()>0){
                for(Iterator<String> it = results.get(0).keySet().iterator();it.hasNext();){
                    return results.get(0).get(it.next()).toString();
                }
            }
        }
        if("COUNT".equals(countType)){
            if(results!=null&&results.size()>0){
                String key = results.get(0).keySet().iterator().next();
                for(Map<String,Object> item : results){
                    String val = String.valueOf(item.get(key));
                    if(!"null".equals(val)){
                        countSet.add(val);
                    }
                }
                countSet.add(matchFieldValue);
                return String.valueOf(countSet.size());
            }
        }
        return "0";
    }
}

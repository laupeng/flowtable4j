package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class CardRiskDbService {

    @Autowired
    @Qualifier("cardriskDbTemplate")
    public JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("cardriskDbNamedTemplate")
    public NamedParameterJdbcTemplate namedJdbcTemplate;

    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)
    {
        return  jdbcTemplate.queryForMap(sql,args,argTypes);
    }

    public Map<String, Object> queryForMap(String sql, Object args, int argTypes)
    {
        return  jdbcTemplate.queryForMap(sql, new Object[]{args}, new int[]{argTypes});
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes)
    {
        return  jdbcTemplate.queryForList(sql, args, argTypes);
    }

    public List<Map<String, Object>> queryForList(String sql)
    {
        return  jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] args)
    {
        return  jdbcTemplate.queryForList(sql, args);
    }

    private String getRuleType(CheckResultLog item){
        String ruleType = item.getRuleType();
        if( CheckType.BW.toString().equals(ruleType)){
            if(item.getRiskLevel().equals(0)) {
                ruleType = "W";
            } else {
                ruleType = "B";
            }

        } else if( CheckType.FLOWRULE.toString().equals(ruleType)) {
           ruleType = "D";
        }
        return ruleType;
    }
}

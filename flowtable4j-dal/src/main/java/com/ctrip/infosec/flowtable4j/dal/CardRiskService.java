package com.ctrip.infosec.flowtable4j.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by thyang on 2015-06-10.
 */
public class CardRiskService {
    @Autowired
    JdbcTemplate cardRiskDBTemplate = null;
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)
    {
        return  cardRiskDBTemplate.queryForMap(sql,args,argTypes);
    }
}

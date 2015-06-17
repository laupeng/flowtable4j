package com.ctrip.infosec.flowtable4j.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class FlowtableService {
    @Autowired
    @Qualifier("flowDbTemplate")
    JdbcTemplate flowDbTemplate = null;
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)
    {
        return  flowDbTemplate.queryForMap(sql,args,argTypes);
    }
}

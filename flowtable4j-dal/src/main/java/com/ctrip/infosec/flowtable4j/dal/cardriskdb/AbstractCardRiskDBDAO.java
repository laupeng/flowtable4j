package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractCardRiskDBDAO {
    
	@Resource(name = "cardRiskDBTemplate")
    protected JdbcTemplate cardRiskDBTemplate;
}

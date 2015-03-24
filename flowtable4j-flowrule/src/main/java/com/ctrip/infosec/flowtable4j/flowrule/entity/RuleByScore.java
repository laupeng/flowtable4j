package com.ctrip.infosec.flowtable4j.flowrule.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分值段比较
 * 
 * @author weiyu
 * @date 2015年3月16日
 */
public class RuleByScore {
	public BigDecimal Score;
	public BigDecimal ElseScore;
	public List<RuleByScoreItem> ScoreItem;
}
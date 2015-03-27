package com.ctrip.infosec.flowtable4j.flowrule.impl;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityFlowRuleGen;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleMatchFieldGen;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleStatisticGen;
import com.ctrip.infosec.flowtable4j.dal.dalmanager.CardRiskDBDalManager;
import com.ctrip.infosec.flowtable4j.flowrule.FlowRuleManager;
import com.ctrip.infosec.flowtable4j.flowrule.entity.FlowRuleEntity;
import com.ctrip.infosec.flowtable4j.flowrule.entity.InfoSecurity_CheckResultLog;
import com.ctrip.infosec.flowtable4j.flowrule.entity.RuleMatchFieldEntity;
import com.ctrip.infosec.flowtable4j.flowrule.entity.RuleStatisticEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

/**
 * 流量规则管理
 * 
 * @author weiyu
 * @date 2015年3月27日
 */
public class FlowRuleManagerImpl implements FlowRuleManager{

	static List<FlowRuleEntity> lst;

	@Resource(name = "cardRiskDBDalManager")
	CardRiskDBDalManager cardRiskDBDalManager;
	static Timer timer;

	public FlowRuleManagerImpl() {
		try {
			timer = new Timer();
			timer.schedule(new FlowRuleTimerTask(), 0, 3 * 60 * 1000);
		} catch (Exception e) {

		}
	}

	public static void SetRuleEntities(List<FlowRuleEntity> ruleList) {
		lst = ruleList;
	}

	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType) {
		return GetFlowRuleListByOrderType(strOrderType, "ALL");
	}

	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType) {
		int orderType;
		orderType = org.apache.commons.lang.math.NumberUtils.toInt(strOrderType, 0);
		List<FlowRuleEntity> list = new ArrayList<FlowRuleEntity>();

		for (FlowRuleEntity tmp : GetFlowRuleList()) {
			if ((tmp.getOrderType() == 0 || tmp.getOrderType() == orderType) && prepayType.equalsIgnoreCase(tmp.getPrepayType())) {
				list.add(tmp);
			}
		}
		return list;
	}

	/**
	 * 根据OrderType获取所有 preType = All 的流量规则
	 * 
	 * @param strOrderType
	 * @param isWhiteCheck
	 * @return
	 */
	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, boolean isWhiteCheck) {
		return GetFlowRuleListByOrderType(strOrderType, "ALL", isWhiteCheck);
	}

	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType, boolean isWhiteCheck) {
		int orderType;
		orderType = org.apache.commons.lang.math.NumberUtils.toInt(strOrderType, 0);
		List<FlowRuleEntity> list = new ArrayList<FlowRuleEntity>();

		for (FlowRuleEntity tmp : GetFlowRuleList()) {
			if ((tmp.getOrderType() == 0 || tmp.getOrderType() == orderType) && prepayType.equalsIgnoreCase(tmp.getPrepayType())) {
				if (isWhiteCheck) {
					if (tmp.getRiskLevel() == 0)
						list.add(tmp);
				} else {
					if (tmp.getRiskLevel() > 0)
						list.add(tmp);
				}

			}
		}
		return list;
	}

	/**
	 * 获取全部流量
	 * 
	 * @return
	 */
	public List<FlowRuleEntity> GetFlowRuleList() {
		if (lst == null) {
			FlowRuleTimerTask timeTask = new FlowRuleTimerTask();
			try {
				lst = timeTask.getFlowRuleEntities();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lst;
	}

	public InfoSecurity_CheckResultLog getCheckedFlowRuleInfo(FlowRuleEntity en) {
		InfoSecurity_CheckResultLog log = null;

		try {
			if (en != null) {
				log = new InfoSecurity_CheckResultLog();
				log.setRiskLevel(en.getRiskLevel());
				log.setRuleID(en.getFlowRuleID());
				log.setRuleName(en.getRuleName());
				log.setRuleRemark(en.getRuleDesc());
				log.setRuleType("D");
			}
		} catch (Exception e) {
			// nRef =
			// (int)RetCodeDefinition.CheckRiskRetCode.SaveCheckedBlackRuleErr;
		}

		return log;
	}

	public class FlowRuleTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				List<FlowRuleEntity> flowRuleEntities = getFlowRuleEntities();
				if (flowRuleEntities != null && flowRuleEntities.size() > 0) {
					FlowRuleManagerImpl.SetRuleEntities(flowRuleEntities);
				}
			} catch (SQLException e) {

			} catch (Exception e) {

			}
		}

		public List<FlowRuleEntity> getFlowRuleEntities() throws Exception {
			List<InfoSecurityFlowRuleGen> flowRules = cardRiskDBDalManager.getInfoSecurityFlowRuleGenDao().getListByActive("T");
			if (flowRules == null || flowRules.size() == 0) {
				throw new Exception("获取 InfoSecurityFlowRule 失败");
			}

			List<FlowRuleEntity> flowRuleEntities = new ArrayList<FlowRuleEntity>();
			for (InfoSecurityFlowRuleGen flowRule : flowRules) {
				FlowRuleEntity entity = new FlowRuleEntity();
				entity.setFlowRuleID(flowRule.getFlowRuleID());
				entity.setOrderType(flowRule.getOrderType());
				entity.setPrepayType(flowRule.getPrepayType());
				entity.setRiskLevel(flowRule.getRiskLevel());
				entity.setRuleDesc(flowRule.getRuleDesc());
				entity.setRuleName(flowRule.getRuleName());

				entity.setMatchFieldListItem(getMatchFieldListItem(entity.getFlowRuleID()));
				entity.setStatisticListItem(getStatisticListItem(entity.getFlowRuleID()));

				flowRuleEntities.add(entity);
			}
			return flowRuleEntities;
		}

		private List<RuleStatisticEntity> getStatisticListItem(int flowRuleID) throws SQLException {
			// TODO Auto-generated method stub
			List<RuleStatisticEntity> entities = new ArrayList<RuleStatisticEntity>();
			List<InfoSecurityRuleStatisticGen> gens = cardRiskDBDalManager.getInfoSecurityRuleStatisticGenDAO().getListByRuleId(flowRuleID);
			if (gens != null && gens.size() >= 0) {
				for (InfoSecurityRuleStatisticGen gen : gens) {
					RuleStatisticEntity e = new RuleStatisticEntity();
					e.setKeyColumnName(gen.getKeyColumnName());
					e.setKeyTableName(gen.getKeyTableName());
					e.setMatchColumnName(gen.getMatchColumnName());
					e.setMatchTableName(gen.getMatchTableName());
					e.setMatchType(gen.getMatchType());
					e.setMatchValue(gen.getMatchValue());
					e.setSqlValue(gen.getSqlValue());
					e.setStartTimeLimit(gen.getStartTimeLimit());
					e.setStatisticType(gen.getStatisticType());
					e.setTimeLimit(gen.getTimeLimit());

					entities.add(e);
				}
			}
			return entities;
		}

		private List<RuleMatchFieldEntity> getMatchFieldListItem(int flowRuleID) throws SQLException {

			List<RuleMatchFieldEntity> entities = new ArrayList<RuleMatchFieldEntity>();
			List<InfoSecurityRuleMatchFieldGen> gens = cardRiskDBDalManager.getInfoSecurityRuleMatchFieldGenDao().getListByRuleId(flowRuleID);
			if (gens != null && gens.size() >= 0) {
				for (InfoSecurityRuleMatchFieldGen gen : gens) {
					RuleMatchFieldEntity entity = new RuleMatchFieldEntity();
					entity.setColumnName(gen.getKeyColumnName());
					entity.setTableName(gen.getKeyTableName());
					entity.setMatchType(gen.getMatchType());
					entity.setMatchValue(gen.getMatchValue());

					entities.add(entity);
				}
			}

			return entities;
		}

	}

}
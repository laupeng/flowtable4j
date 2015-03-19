package com.ctrip.flowtable4j.core.flowRule;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.flowtable4j.core.Utils.JsonMapper;
import com.ctrip.flowtable4j.core.flowRule.RuleManager.FlowStatisticType;
import com.ctrip.flowtable4j.core.flowRule.entity.*;
import com.ctrip.flowtable4j.core.flowRule.impl.FlowStatisticsDBManager;

/**
 * 流量验证抽象类
 * 
 * @author weiyu
 * @date 2015年3月17日
 */
public abstract class AbstractCheckRiskCtrip {

	/*
	 * InfoSecurity_FlowRule InfoSecurity_RuleMatchField
	 * InfoSecurity_RuleStatistic 字段定义Def_RuleMatchField
	 */

	@Autowired
	JsonMapper jsonMapper;
	
	private static Logger logger = LoggerFactory.getLogger(AbstractCheckRiskCtrip.class);

	/**
	 * 执行流量验证
	 * 
	 * @param basicCheckRiskData
	 * @param CheckEntity
	 * @param isFlowRuleWhite
	 * @param isWhiteCheck
	 * @return
	 */
	protected FlowCheckRiskResult CheckFlowRuleList(Map basicCheckRiskData, Map checkEntity, boolean isFlowRuleWhite, boolean isWhiteCheck) {
		FlowCheckRiskResult result = new FlowCheckRiskResult();
		result.setLogList(new ArrayList<InfoSecurity_CheckResultLog>());

		int originalRiskLevel = 0;
		int currentRiskLevel = 0;
		String ruleName = "";

		if (isFlowRuleWhite) {
			result.setRiskLevel(0);
			return result;
		}

		try {
			// DateTime ts = DateTime.Now;
			// CtripFlightsOrderEntity ctripFlightsOrderEntity =
			// basicCheckRiskData as CtripFlightsOrderEntity;

			Map<Integer, FlowRuleEntity> ruleMaps = new HashMap<Integer, FlowRuleEntity>();

			// && ctripFlightsOrderEntity.PaymentInfo.Count > 0
			Map mainInfo = (Map) basicCheckRiskData.get("MainInfo");
			List paymentInfos = (List) basicCheckRiskData.get("PaymentInfos");
			List<FlowRuleEntity> ruleList = null;
			if (mainInfo != null && paymentInfos != null) {
				ruleList = RuleManager.GetFlowRuleListByOrderType(mainInfo.get("OrderType").toString(), isWhiteCheck);
				for (FlowRuleEntity entity : ruleList) {
					ruleMaps.put(entity.getFlowRuleID(), entity);
				}

				for (Map paymentInfo : (List<Map>) paymentInfos) {

					ruleList = (RuleManager.GetFlowRuleListByOrderType(mainInfo.get("OrderType").toString(), paymentInfo.get("PrepayType").toString(), isWhiteCheck));
					for (FlowRuleEntity entity : ruleList) {
						if (!ruleMaps.containsKey(entity.getFlowRuleID())) {
							ruleMaps.put(entity.getFlowRuleID(), entity);
						}
					}
				}

			}

			String logMessage = "";
			for (FlowRuleEntity entity : ruleList) {
				ruleName = entity.getRuleName();

				try {
					long flowRuleTime = System.currentTimeMillis();
					// 返回值=-1:表示没有匹配到规则 =0：白名单规则 >0：正常流量规则
					currentRiskLevel = CheckFlowRuleItem(entity, checkEntity);

					long tempTime = System.currentTimeMillis() - flowRuleTime;
					if (tempTime > 50) {
						logMessage = logMessage + ruleName + "," + tempTime + "|";
					}
					// 如果当前是白名单流量校验，且返回的值为0，则表示当前中了白名单
					if (isWhiteCheck == true && currentRiskLevel == 0)
						isFlowRuleWhite = true;
				} catch (Exception e) {
					currentRiskLevel = 0;
					ruleName = mainInfo.get("OrderId").toString() + "||" + ruleName + ";" + e.getMessage();
					logger.error("风控规则校验异常：【" + ruleName + "】", e, FlowCheckLog.CheckFlowRuleItemError);
				}

				if ("AM1006".equalsIgnoreCase(ruleName) && currentRiskLevel > 0) {
					String ExTxt = jsonMapper.toJson(checkEntity);

					logger.warn("AM1006:" + checkEntity.get("OrderId").toString(), ExTxt);
				}

				if (currentRiskLevel > 0){
					InfoSecurity_CheckResultLog riskLog = RuleManager.getCheckedFlowRuleInfo(entity);
					if(riskLog !=null)
						result.getLogList().add(riskLog);
				}

				if (originalRiskLevel < currentRiskLevel)
					originalRiskLevel = currentRiskLevel;
			}
			//logMessage = logMessage.TrimEnd('|');
		} catch (Exception e) {
			String test = "";
		}

		return result;
	}

	private int CheckFlowRuleItem(FlowRuleEntity ruleEntity, Map checkEntity)
    {
        String value = "";

        //string message = "";
        //message = "规则名称：" + RuleEntity.RuleName;
        //LogManager.WriteLog2(9, RuleEntity.RuleName, 0, 14, "流量规则校验子项", message, "fang.y");
        //匹配属性
        List<RuleMatchFieldEntity> matchFields = ruleEntity.getMatchFieldListItem();
        for (RuleMatchFieldEntity ruleMatch: matchFields)
        {

            value =checkEntity.get(ruleMatch.getColumnName()).toString();
            //Convert.ToString(CheckEntity.GetType().GetProperty(ruleMatch.ColumnName).GetValue(CheckEntity, null)).Trim();
            String TmpMatchValue = "", TmpMatchType = "";
            //如果是字段相等或者不等，需要将匹配的字段转换成相关的值
            if ("FEQ".equalsIgnoreCase(ruleMatch.getMatchType()) 
            		|| "FNE".equalsIgnoreCase(ruleMatch.getMatchType())
            		|| "FIN".equalsIgnoreCase(ruleMatch.getMatchType())
            		|| "FNA".equalsIgnoreCase(ruleMatch.getMatchType())
            		|| "FGE".equalsIgnoreCase(ruleMatch.getMatchType())
            		|| "FLE".equalsIgnoreCase(ruleMatch.getMatchType())
                )
            {
                TmpMatchValue = checkEntity.get(ruleMatch.getMatchValue()).toString();
          		//Convert.ToString(CheckEntity.GetType().GetProperty(ruleMatch.MatchValue).GetValue(CheckEntity, null)).Trim();
                TmpMatchType = ruleMatch.getMatchType().toUpperCase().substring(1);

                if (org.apache.commons.lang.StringUtils.isEmpty(TmpMatchValue))
                    return -1;
            }
            else
            {
                TmpMatchValue = ruleMatch.getMatchValue();
                TmpMatchType = ruleMatch.getMatchType();
            }

            //如果匹配失败直接返回
            if (!matchResult(TmpMatchType, value, convertRegexValueCanRegex(TmpMatchType, TmpMatchValue)))
            {
                return -1;
            }
        }

        if (ruleEntity.getStatisticListItem() == null || ruleEntity.getStatisticListItem().size() == 0){
        	return ruleEntity.getRiskLevel();
        }
        
        //匹配流量

        List<RuleStatisticEntity> statisticEntities = ruleEntity.getStatisticListItem();
        for(RuleStatisticEntity ruleStatistic:statisticEntities )
        {
            //DateTime StartTimeLimit = DateTime.Now.AddMinutes(
        	long startTimeLimit = System.currentTimeMillis() + ruleStatistic.getStartTimeLimit() * -1 * 60 * 1000;
        	long timeLimit = System.currentTimeMillis() + ruleStatistic.getTimeLimit() * -1 * 60 * 1000;
        	
            List<String> valueList;
            
            valueList = GetListKeyValue(ruleStatistic.getKeyColumnName(), checkEntity);
            int RiskLevel;
            int SubRiskLevel = 0;
            for (String o : valueList)
            {
                RiskLevel = 0;
                RiskLevel = CheckFlowRuleItemStatistic(checkEntity, ruleStatistic, o, startTimeLimit, timeLimit, ruleEntity);
                SubRiskLevel += RiskLevel;
                //表示所有子项都满足的条件
                if ("ALL".equalsIgnoreCase(ruleStatistic.getStatisticType())  && RiskLevel == 0)
                {
                    return -1;
                }


            }
            if (SubRiskLevel <= 0)
                return -1;


        }
        

        return ruleEntity.getRiskLevel();
    }

    private int CheckFlowRuleItemStatistic(Map CheckEntity, RuleStatisticEntity RuleStatistic, String paramValue,
            long StartTimeLimit, long TimeLimit, FlowRuleEntity flowRuleEntity)
        {
            //如果统计项的KEY值为空的话，则跳过当前统计

            //     string matchValue = Convert.ToString(CheckEntity.GetType().GetProperty(RuleStatistic.MatchColumnName).GetValue(CheckEntity, null)).Trim();
            List<String> matchValueList = null;
            int RiskLevel = 0;
            int SubRiskLevel = 0;
            matchValueList = GetListKeyValue(RuleStatistic.getMatchColumnName(), CheckEntity);
            for(String matchValue : matchValueList)
            {
                RiskLevel = CheckFlowRuleItem(matchValue, paramValue, RuleStatistic, StartTimeLimit, TimeLimit, flowRuleEntity);
                SubRiskLevel += RiskLevel;
            }
            if (SubRiskLevel <= 0)
                return -1;
            return SubRiskLevel;

        }
    
    private int CheckFlowRuleItem(String matchValue, String paramValue, RuleStatisticEntity RuleStatistic, long startTimeLimit, long timeLimit, FlowRuleEntity RuleEntity)
    {

        //Ctrip.WS.InfoSecurityService.DataAccess.RiskCtrlPreProcDB.CommExecDAL StatisticDAL = new InfoSecurityService.DataAccess.RiskCtrlPreProcDB.CommExecDAL();

        String CurrentValue = "1";
        //获取 java使用的sql
        String sql = RuleStatistic.getSqlValue();
        
        FlowStatisticsDBManager execComm = new FlowStatisticsDBManager();
        FlowStatisticType flowStatisticType = FlowStatisticType.COUNT;
        if ("COUNT".equalsIgnoreCase(RuleStatistic.getStatisticType())
        		|| "ALL".equalsIgnoreCase(RuleStatistic.getStatisticType())){
        	flowStatisticType = FlowStatisticType.COUNT;
        }
        else if("SUM".equalsIgnoreCase(RuleStatistic.getStatisticType())){
        	flowStatisticType = FlowStatisticType.SUM;
        }
        
        
        String v = execComm.execSql(startTimeLimit, timeLimit,RuleStatistic.getKeyColumnName(), paramValue, sql,RuleStatistic.getMatchColumnName(),matchValue,flowStatisticType);


        if (!matchResult(RuleStatistic.getMatchType(), CurrentValue, RuleStatistic.getMatchValue()))
        {
            return 0;
        }

        return RuleEntity.getRiskLevel();
    }
    

    
	/**
	 * 判断是否属于子集字段
	 * @param columnName
	 * @return
	 */
	public abstract boolean isCheckEntityInnerList(String columnName);
	
	/**
	 * 获取子集所在key。
	 * @param columnName
	 * @return
	 */
	public abstract List<Map> getInnerList(String columnName, Map checkEntity);
	
	/**
	 * 获取值
	 * @param keyColumnName
	 * @param checkEntity
	 * @return
	 */
	private List<String> GetListKeyValue(String keyColumnName, Map checkEntity) {
        String value = "";
        List<String> valueList = new ArrayList<String>();
        
        if (isCheckEntityInnerList(keyColumnName)){
        	
        	List<Map> innerList = getInnerList(keyColumnName,checkEntity);
        	
			for (Map map : innerList) {
				List<String> tmp = GetListKeyValue(keyColumnName, map);
				if (tmp != null && tmp.size() > 0) {
					valueList.addAll(tmp);
				}
			}
        }
        else
        {
        	Object tmp = checkEntity.get(keyColumnName);

            if (tmp != null)
            {
            	value = tmp.toString();
            }
            //if(StringUtils.isNotEmpty(value))
            valueList.add(value);
        }
		
		return valueList;
	}


	

	/**
	 * 处理正则匹配字段
	 * 
	 * @param CheckType
	 * @param CheckValue
	 * @return
	 */
	public static String convertRegexValueCanRegex(String CheckType, String CheckValue) {
		String RegexValue = "";
		if ("EQ".equalsIgnoreCase(CheckType) || "NE".equalsIgnoreCase(CheckType))
			RegexValue = CheckValue.toUpperCase();
		else if ("IN".equalsIgnoreCase(CheckType) || "NA".equalsIgnoreCase(CheckType))
			RegexValue = "(" + CheckValue.toUpperCase() + ")+";
		else if ("LLIKE".equalsIgnoreCase(CheckType))
			RegexValue = "^(" + CheckValue.toUpperCase() + ")+";
		else if ("RLIKE".equalsIgnoreCase(CheckType))
			RegexValue = "(" + CheckValue.toUpperCase() + ")+$";
		else if ("REGEX".equalsIgnoreCase(CheckType))
			RegexValue = CheckValue;
		else
			return CheckValue;

		return RegexValue;
	}

	public boolean matchResult(String matchType, String CurrentValue, String MatchValue) {
		if (org.apache.commons.lang.StringUtils.isEmpty(CurrentValue) && "REGEX".equalsIgnoreCase(matchType))
			return false;

		if ("EQ".equalsIgnoreCase(matchType)) // 等于
			return org.apache.commons.lang.StringUtils.equalsIgnoreCase(MatchValue, CurrentValue);
		// return string.Equals(MatchValue, CurrentValue,
		// StringComparison.OrdinalIgnoreCase);
		else if ("NE".equalsIgnoreCase(matchType)) // 不等于
			return !org.apache.commons.lang.StringUtils.equalsIgnoreCase(MatchValue, CurrentValue);
		// return !string.Equals(MatchValue, CurrentValue,
		// StringComparison.OrdinalIgnoreCase);
		else if ("GE".equalsIgnoreCase(matchType)) { // 大于等于
			if (NumberUtils.toDouble(CurrentValue) < NumberUtils.toDouble(MatchValue))
				return false;
		} else if ("LE".equalsIgnoreCase(matchType)) { // 小于等于
			if (NumberUtils.toDouble(CurrentValue) > NumberUtils.toDouble(MatchValue))
				return false;
		} else if ("GREAT".equalsIgnoreCase(matchType)) { // 大于
			if (NumberUtils.toDouble(CurrentValue) <= NumberUtils.toDouble(MatchValue))
				return false;
		} else if ("LESS".equalsIgnoreCase(matchType)) { // 小于
			if (NumberUtils.toDouble(CurrentValue) >= NumberUtils.toDouble(MatchValue))
				return false;
		} else if ("NA".equalsIgnoreCase(matchType)) { // 不存在
			return !java.util.regex.Pattern.matches(MatchValue, CurrentValue);
			/*
			 * r = new Regex(MatchValue, RegexOptions.IgnoreCase); return
			 * !r.IsMatch(CurrentValue);
			 */
		} else if ("IN".equalsIgnoreCase(matchType) // 存在
				|| ("LLIKE".equalsIgnoreCase(matchType) // 右边有%
				|| "RLIKE".equalsIgnoreCase(matchType)) // 左边有%
				|| "REGEX".equalsIgnoreCase(matchType)) { // 完整的正则表达式
			return java.util.regex.Pattern.matches(MatchValue, CurrentValue);
		}

		return true;
	}
}

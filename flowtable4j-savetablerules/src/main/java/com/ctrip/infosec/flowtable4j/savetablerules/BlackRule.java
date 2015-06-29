package com.ctrip.infosec.flowtable4j.savetablerules;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.dal.FlowtableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
@Component
public class BlackRule extends BaseRule {
    @Autowired
    FlowtableService flowtableService;

    @Override
    void checkAndSave(FlowFact checkEntity) {
        List<FlowRuleStatement> rules = byOrderType.get(checkEntity.getOrderType());
        for (FlowRuleStatement rule : rules) {
            if (rule.check(checkEntity)) {
                String k1 = rule.getKeyFieldID1();
                String k2 = rule.getKeyFieldID2();
                Object v1 = checkEntity.getContent().get(k1);
                Object v2 = checkEntity.getContent().get(k2);
                if (k1 != null && k2 != null && v1 != null && v2 != null)
                    flowtableService.saveFlowTable(checkEntity.getReqId(), rule.getStatisticTableName(), k1, v1, k2, v2);
            }
        }
    }
}

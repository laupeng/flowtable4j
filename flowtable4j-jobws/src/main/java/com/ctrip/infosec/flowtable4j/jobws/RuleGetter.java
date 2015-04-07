package com.ctrip.infosec.flowtable4j.jobws;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/3/13.
 */
public interface RuleGetter {
    /**
     * 黑白名单增量更新
     * @return
     */
    List<Map<String,Object>> getUpdateBWRule();

    /**
     * 黑白名单全量更新
     * @return
     */
    List<Map<String,Object>> getAllBWRule();

    /**
     * 流量规则相关取数（全量）
     * @return
     */
    List<Map<String,Object>> getValueMatchTerms();

    List<Map<String,Object>> getFieldMatchTerms();

    List<Map<String,Object>> getCounterMatchTerms();

    List<Map<String,Object>> getFlowRuleMaster();
}

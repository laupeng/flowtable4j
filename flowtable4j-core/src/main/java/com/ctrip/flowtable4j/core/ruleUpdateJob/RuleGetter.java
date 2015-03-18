package com.ctrip.flowtable4j.core.ruleUpdateJob;

import java.util.Collection;
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
    List<Map<String,Object>> bwIncrement();

    /**
     * 黑白名单全量更新
     * @return
     */
    List<Map<String,Object>> bwFull();

    /**
     * 规则校验增量更新
     * @return
     */
    List<Map<String,Object>> ruleIncrement();

    /**
     * 规则校验全量更新
     * @return
     */
    List<Map<String,Object>> ruleFull();


}

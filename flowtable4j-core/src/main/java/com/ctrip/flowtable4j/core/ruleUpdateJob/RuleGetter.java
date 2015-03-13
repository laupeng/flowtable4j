package com.ctrip.flowtable4j.core.ruleUpdateJob;

import java.util.Collection;

/**
 * Created by zhangsx on 2015/3/13.
 */
public interface RuleGetter {
    /**
     * 黑白名单增量更新
     * @return
     */
    Collection<?> bwIncrement();

    /**
     * 黑白名单全量更新
     * @return
     */
    Collection<?> bwFull();

    /**
     * 规则校验增量更新
     * @return
     */
    Collection<?> ruleIncrement();

    /**
     * 规则校验全量更新
     * @return
     */
    Collection<?> ruleFull();


}

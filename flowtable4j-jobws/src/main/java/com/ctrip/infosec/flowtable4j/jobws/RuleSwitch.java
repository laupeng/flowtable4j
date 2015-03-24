package com.ctrip.infosec.flowtable4j.jobws;

/**
 * Created by zhangsx on 2015/3/13.
 * 规则更新开关
 */
public interface RuleSwitch {
    /**
     * 开始更新
     */
    void start();

    /**
     * 平稳关闭
     */
    void shutdown();

    /**
     * 立即关闭
     */
    void shutdownNow();
}

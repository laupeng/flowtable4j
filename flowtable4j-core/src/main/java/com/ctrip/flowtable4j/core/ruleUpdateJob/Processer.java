package com.ctrip.flowtable4j.core.ruleUpdateJob;

/**
 * Created by zhangsx on 2015/3/13.
 */
public interface Processer {
    void execute();
    enum Status{
        FIRST,
        NOTFIRST;
    }
}

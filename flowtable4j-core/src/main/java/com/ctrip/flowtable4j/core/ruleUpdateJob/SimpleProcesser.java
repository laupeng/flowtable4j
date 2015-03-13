package com.ctrip.flowtable4j.core.ruleUpdateJob;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhangsx on 2015/3/13.
 */
public class SimpleProcesser implements Processer {
    @Autowired
    private RuleGetter ruleGetter;

    Status status = Status.FIRST;

    @Override
    public void execute() {
        if (status == Status.FIRST) {
            ruleGetter.bwFull();

            ruleGetter.ruleFull();
            status = Status.NOTFIRST;
        } else {
            ruleGetter.bwIncrement();
            ruleGetter.ruleIncrement();
        }
    }
}

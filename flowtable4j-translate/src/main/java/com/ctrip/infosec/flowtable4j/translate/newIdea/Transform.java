package com.ctrip.infosec.flowtable4j.translate.newIdea;

import com.ctrip.infosec.flowtable4j.model.CheckFact;

import java.util.Map;

/**
 * Created by zhangsx on 2015/6/1.
 */
public interface Transform {
    CheckFact exec(Map data);
}

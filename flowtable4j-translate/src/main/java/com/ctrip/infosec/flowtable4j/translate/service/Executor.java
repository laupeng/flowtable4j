package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.model.DataFact;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lpxie on 15-4-30.
 */
public interface Executor
{
    public void complementData(DataFact dataFact,Map data,ThreadPoolExecutor executor);

    public void convertToBlackCheckItem(DataFact dataFact,Map data,Map bwList);

    public void convertToFlowRuleCheckItem(DataFact dataFact,Map data,Map flowData);

    public void writeData(DataFact dataFact,Map data,Map flowData,ThreadPoolExecutor writeExecutor,final boolean isWrite,final boolean isCheck);
}

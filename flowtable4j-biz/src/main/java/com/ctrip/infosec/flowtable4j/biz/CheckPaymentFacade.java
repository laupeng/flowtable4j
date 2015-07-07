package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.biz.processor.*;
import com.ctrip.infosec.flowtable4j.flowdata.TableSaveRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.TimeUnit;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class CheckPaymentFacade {
    @Autowired
    POConverter poConverter;

    @Autowired
    BlackWhiteConverter blackWhiteConverter;

    @Autowired
    FlowConverter flowConverter;

    @Autowired
    AccountConverter accountConverter;

    @Autowired
    Save2DbProcessor save2DbService;

    @Autowired
    FlowtableProcessor flowtableProcessor;

    @Autowired
    TableSaveRuleManager tableSaveRuleManager;
    private static Logger logger = LoggerFactory.getLogger(CheckPaymentFacade.class);
    public CheckFact process(RequestBody request) {
        Stopwatch stopwatch=Stopwatch.createStarted();
        CheckFact fact = new CheckFact();
        final PO po = poConverter.convert(request);
        fact.setAccountFact(accountConverter.convert(po));
        fact.setBwFact(blackWhiteConverter.convert(po));
        fact.setFlowFact(flowConverter.convert(po));
        fact.setCheckTypes(new CheckType[]{CheckType.ACCOUNT, CheckType.BW, CheckType.FLOWRULE});
        final Long  reqId =save2DbService.saveDealInfo(MapX.getMap(po.getProductinfo(), "dealinfo"));
        fact.setReqId(reqId);
        fact.getFlowFact().setReqId(fact.getReqId());
        stopwatch.stop();
        logger.warn("Construct PO elapse:" + stopwatch.elapsed(TimeUnit.MILLISECONDS));

        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        poConverter.saveData4Next(po);
                        save2DbService.save(po, reqId);
                    }
                });

        return fact;
    }

    public RiskResult checkRisk(RequestBody requestBody) {
        //数据准备
        CheckFact fact = process(requestBody);

        Stopwatch stopwatch=Stopwatch.createStarted();
        //流量校验
        RiskResult result = flowtableProcessor.handle(fact);

        stopwatch.stop();
        logger.warn("Check Risk elapse:" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        final FlowFact flowFact = fact.getFlowFact();
        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                //分流表数据落地
                tableSaveRuleManager.checkAndSave(flowFact);
            }
        });

        return result;
    }


}

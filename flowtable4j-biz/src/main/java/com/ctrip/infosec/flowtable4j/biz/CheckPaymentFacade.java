package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.biz.converter.AccountConverter;
import com.ctrip.infosec.flowtable4j.biz.converter.BlackWhiteConverter;
import com.ctrip.infosec.flowtable4j.biz.processor.FlowtableProcessor;
import com.ctrip.infosec.flowtable4j.biz.processor.Save2DbProcessor;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class CheckPaymentFacade {

    @Autowired
    BlackWhiteConverter blackWhiteConverter;

    @Autowired
    AccountConverter accountConverter;

    @Autowired
    Save2DbProcessor save2DbService;

    @Autowired
    FlowtableProcessor flowtableProcessor;


    /**
     * 数据库写入10个线程足以
     */
    private ThreadPoolExecutor executor= new ThreadPoolExecutor(20,30,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10),
            new CustomizableThreadFactory("pool-save-offline-"));

    private static Logger logger = LoggerFactory.getLogger(CheckPaymentFacade.class);

    public Long saveData4Offline(final PO po)
    {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                logger.info("start save offline:"+ po.getReqid());
                save2DbService.save(po, po.getReqid());
                logger.info("finish save offline:"+po.getReqid()+",total elapse:" + (System.currentTimeMillis()-start));
            }
        });
        return 0L;
    }

    public RiskResult checkBWGList(RequestBody checkEntity) {
        //数据准备
        long start = System.currentTimeMillis();
        CheckFact fact = new CheckFact();
        fact.setAccountFact(accountConverter.convert(checkEntity));
        fact.setBwFact(blackWhiteConverter.convert(checkEntity));
        fact.setCheckTypes( new CheckType[]{CheckType.ACCOUNT, CheckType.BW});

        //流量校验
        RiskResult result = flowtableProcessor.handle(fact);

        logger.info("handle checkBWGList request total elapse:" + (System.currentTimeMillis() - start));

        return result;
    }

}

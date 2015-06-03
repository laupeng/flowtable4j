package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.translate.service.FlightExecutor;
import com.ctrip.infosec.flowtable4j.translate.service.HotelGroupExecutor;
import com.ctrip.infosec.flowtable4j.translate.service.TieYouExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by lpxie on 15-3-31.
 */
@Component
public class PreProcessor
{
    private Logger logger = LoggerFactory.getLogger(PreProcessor.class);
    private ThreadPoolExecutor excutor = new ThreadPoolExecutor(64, 507, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private ThreadPoolExecutor writeExcutor = new ThreadPoolExecutor(2, 5, 60, TimeUnit.SECONDS, queue);
    @Autowired
    FlightExecutor flightExecutor;
    @Autowired
    HotelGroupExecutor hotelGroupExecutor;
    @Autowired
    TieYouExecutor tieYouExecutor;

    /**
     * 这里分orderType处理
     * 把不同的订单交个不同的业务执行器来处理
     * @param data
     */
    public CheckFact execute(Map data)
    {
        //订单类型(1 2 3 4 ...24)
        int orderType = Integer.parseInt(data.get("OrderType").toString());
        switch (orderType)
        {
            case 1:
                return flightExecutor.executeFlight(data,excutor,writeExcutor,true,true);
            case 2:
                break;
            case 14:
                return hotelGroupExecutor.executeHotelGroup(data,excutor,writeExcutor,true,true);//fixme 注意这里要调整 在上线后的写入和检查
            case 18:
                return tieYouExecutor.executeTieYou(data,excutor,writeExcutor,true,true);
            //...14-24
            default:
                logger.info("没有找到相关的订单类型 : "+orderType);
                return null;
        }
        return null;
    }
}

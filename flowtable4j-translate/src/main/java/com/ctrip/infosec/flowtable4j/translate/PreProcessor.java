package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.translate.service.HotelGroupExecutor;
import com.ctrip.infosec.flowtable4j.translate.service.TieYouExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by lpxie on 15-3-31.
 */

public class PreProcessor
{
    private Logger logger = LoggerFactory.getLogger(PreProcessor.class);

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
                break;
            case 2:
                break;
            case 14:
                return hotelGroupExecutor.executeHotelGroup(data);
            case 18:
                return tieYouExecutor.executeTieYou(data);
            //...14-24
            default:
                logger.info("没有找到相关的订单类型 : "+orderType);
                return null;
        }
        return null;
    }
}

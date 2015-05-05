package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.flowtable4j.translate.service.HotelGroupExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by lpxie on 15-3-31.
 */
public class Processor
{
    private Logger logger = LoggerFactory.getLogger(Processor.class);

    @Autowired
    HotelGroupExecutor hotelGroupExecutor;

    /**
     * 这里分orderType处理
     * 把不同的订单交个不同的业务执行器来处理
     * @param data
     */
    public void execute(Map data)
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
                logger.info("开始处理酒店团购 "+data.get("OrderID").toString()+" 数据");
                hotelGroupExecutor.executeHotelGroup(data);
                logger.info("酒店团购 "+data.get("OrderID").toString()+" 数据处理完毕");
                break;
            //...
            default:
                logger.info("没有找到相关的订单类型 : "+orderType);
                break;
        }
    }
}

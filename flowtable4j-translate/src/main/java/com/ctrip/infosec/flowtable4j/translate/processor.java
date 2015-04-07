package com.ctrip.infosec.flowtable4j.translate;

import java.util.Map;

/**
 * Created by lpxie on 15-3-31.
 */
public class Processor
{
    /**
     * 这里分orderType和checkType来划分处理
     * 把不同的订单交个不同的业务执行器来处理
     * @param data
     */
    public void execute(Map data)
    {
        //订单类型(1 2 3 4 ...24)
        int orderType = (int) data.get("OrderType");
        //支付类型（0 1 2）
        int CheckType = (int) data.get("CheckType");
        //FIXME 这里有24个业务线 怎么把不同的订单划分到不同的业务比较好

    }
}

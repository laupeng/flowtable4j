package com.ctrip.infosec.flowtable4j.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thyang on 2015-08-03.
 */
public enum CtripSubOrderType {
        Default (0),

        // 机票航司订单
        FlightsHangSi(1),

        // 携程汽车票
        CtripBusTickets(2),

        // 当地活动
        TTDLocalEvents(3),

        // 礼品卡分销
        TravelMoneyRetailer(4),

        // 钱包提现
        Wallet (5),

        // 分销(门票)
        TravelMoneyTTD(6),

        // 机场用车
        AirportByCar(7),

        // 分销机票
        DistributionFlight (8),

        // 全球购
        SHP (9),

        // 礼遇商城
        FNCMall(10),

        // （讨盘缠） 隶属 OrderType 17 游票销售
        TaoPanChan(11),

        // 景酒（套餐）
        TTDHTL (12),

       // 机酒（套餐）
        FTLHTL(13),

       // 存款证明
        FundCertificate (14),

        // 机+车
        FTLCar (15),

        // 铁友汽车票
        TieYouCar(16),

        // 欧铁
        OuTieCRH (17),

        // 欧铁
        MICE (18),

        // 国际网站度假订单
        VacationByInternational(19),

        //新礼域商城
        NEWFNCMall(20),

        //商旅用车
        BTravelCar (21);

    private static List<Integer> codes = new ArrayList<Integer>();

    static {
        for (CtripSubOrderType item : CtripSubOrderType.values()) {
            codes.add(item.getCode());
        }
    }
    private int code;

    private CtripSubOrderType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static boolean contain(int code) {
        return codes.contains(code);
    }
}

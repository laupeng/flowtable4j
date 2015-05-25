package com.ctrip.infosec.flowtable4j.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsx on 2015/5/6.
 */
public enum CtripOrderType {
    ALL(0),
    //机票
    Flights(1),

    //            酒店
    Hotel(2),
    //            团队游
    Vacation(4),
    //            机酒
    FlightHotel(5),
    //           积分兑换
    JiFen(12),
    //          酒店团购
    HotelGroup(14),
    //            高铁
    CRH(16),
    //            游票销售
    TravelMoney(17),
    //           铁友
    TieYou(18),
    //            套餐
    DIY(19),
    //           消费券
    Coupons(20),
    //            用车
    Car(21),
    //          邮轮
    Cruise(22),
    //            高端旅游
    HHTravel(23),
    //          地面服务
    TTD(24),
    //            易到用车
    YongChe(25),
    //            精选商户项目
    TopShop(26),
    //           主题旅游
    Fun(27),
    //            钱包
    Wallet(28),
    //
//            MICE
    Mice(29),
    //           保险
    Insurer(30),
    //           秒杀资格
    QualificationSeckilling(31),
    //           酒店EBK
    HotelEBK(32),
    //           外币汇兑
    CurrencyExchange(33),
    //           营销活动（火车票）
    MKTTravelExpense(34),
    //            邮轮(天海)
    CruiseByTianHai(35),
    //            汽车票（高铁）
    BusByCRH(36),
    //           Mice（度假）
    MiceByPKG(37),
    //            营销活动(送奖品)
    Marketing(10000),
    //            海外Booking
    HoTelBooking(10001),

    //            永安机票
    YongAnFlight(10002),

    //            永安酒店
    YongAnHotel(10003),
    //            绑卡
    BindingCard(10004);

    private static List<Integer> codes = new ArrayList<Integer>();

    static {
        for (CtripOrderType item : CtripOrderType.values()) {
            codes.add(item.getCode());
        }
    }
    private int code;

    private CtripOrderType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static boolean contain(int code) {
        return codes.contains(code);
    }
}

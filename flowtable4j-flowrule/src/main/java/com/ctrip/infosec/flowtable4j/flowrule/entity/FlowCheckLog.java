package com.ctrip.infosec.flowtable4j.flowrule.entity;

public class FlowCheckLog{

    static String CATEGORY = "4";

    /// <summary>
    /// 调用黑白名单维护服务异常
    /// </summary>
    public static BaseLog MaintainBWGError = new BaseLog("01", CATEGORY);

    /// <summary>
    /// 调用CRM踢登录异常
    /// </summary>
    public static BaseLog CRMKickOffLoginError = new BaseLog("02", CATEGORY);

    /// <summary>
    /// 回写风控结果到收银端异常
    /// </summary>
    public static BaseLog SaveCheckResultToEasyPayServicesError = new BaseLog("03", CATEGORY);
    /// <summary>
    /// 风控规则校验异常
    /// </summary>
    public static BaseLog CheckFlowRuleItemError = new BaseLog("04", CATEGORY);

    /// <summary>
    /// 回写风控结果到支付端异常
    /// </summary>
    public static BaseLog InstructionPayRequestError = new BaseLog("05", CATEGORY);

    /// <summary>
    /// 支付适配校验服务调用账户适配规则校验服务异常
    /// </summary>
    public static BaseLog CheckBWGRuleRequestError = new BaseLog("06", CATEGORY);

    /// <summary>
    /// 支付风控保存业务实体对象到数据库发生异常
    /// </summary>
    public static BaseLog SaveRiskDataError = new BaseLog("07", CATEGORY);

    /// <summary>
    /// 风控黑名单校验异常
    /// </summary>
    public static BaseLog CheckBlackListError = new BaseLog("08", CATEGORY);

    /// <summary>
    /// 缓存中获取黑名单规则异常
    /// </summary>
    public static BaseLog GetDictionaryBlackListByOrderTypeError = new BaseLog("09", CATEGORY);

    /// <summary>
    /// 支付适配规则校验异常
    /// </summary>
    public static BaseLog CheckPayAdapterRuleItemError = new BaseLog("10", CATEGORY);

    /// <summary>
    /// 风控流量过虑器异常
    /// </summary>
    public static BaseLog IsInsertToStatisticTableError = new BaseLog("11", CATEGORY);

    /// <summary>
    /// 风控分流表数据落地异常
    /// </summary>
    public static BaseLog SaveStatisticTableError = new BaseLog("12", CATEGORY);

    /// <summary>
    /// 更新订单状态异常
    /// </summary>
    public static BaseLog UpdateOrderStatusError = new BaseLog("13", CATEGORY);

    /// <summary>
    /// SMS检查服务异常
    /// </summary>
    public static BaseLog SMSVerifyError = new BaseLog("14", CATEGORY);

    /// <summary>
    /// 风控数据收集异常
    /// </summary>
    public static BaseLog DataCollectError = new BaseLog("15", CATEGORY);

    /// <summary>
    /// B2BFlt校验异常
    /// </summary>
    public static BaseLog B2BFltError = new BaseLog("16", CATEGORY);

    /// <summary>
    /// 校验风险订单异常
    /// </summary>
    public static BaseLog CheckRiskOrderError = new BaseLog("17", CATEGORY);

    /// <summary>
    /// 数据收集服务异常
    /// </summary>
    public static BaseLog CheckDataCollectError = new BaseLog("18", CATEGORY);

    /// <summary>
    /// 风控支付校验接口异常
    /// </summary>
    public static BaseLog CheckRiskPaymentError = new BaseLog("19", CATEGORY);

    /// <summary>
    /// EBKSearchOrderList服务接口异常
    /// </summary>
    public static BaseLog EBKSearchOrderListError = new BaseLog("20", CATEGORY);

    /// <summary>
    /// CheckRiskBooking服务接口异常
    /// </summary>
    public static BaseLog CheckRiskBookingError = new BaseLog("21", CATEGORY);

    /// <summary>
    /// 支付适配风控SOA2.0异常
    /// </summary>
    public static BaseLog PayAdapterV2Error = new BaseLog("22", CATEGORY);

    /// <summary>
    /// 支付风控SOA2.0异常
    /// </summary>
    public static BaseLog PayV2Error = new BaseLog("23", CATEGORY);

    /// <summary>
    /// 风控服务未处理的异常
    /// </summary>
    public static BaseLog InfoSecurityServiceError = new BaseLog("24", CATEGORY);

    /// <summary>
    /// B2B检查数据服务异常
    /// </summary>
    public static BaseLog B2BCheckDataCollectError = new BaseLog("25", CATEGORY);


    /// <summary>
    /// 风控规则引擎异常
    /// </summary>
    public static BaseLog REServiceError = new BaseLog("26", CATEGORY);

}

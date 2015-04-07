package com.ctrip.infosec.flowtable4j.translate.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lpxie on 15-3-31.
 */
public class InfoSecurity_MainInfo
{
    //InfoSecurity_DealInfo主键
    private long _reqID = Long.MIN_VALUE;
    //订单类型 1:机票 2:酒店 4:度假 14:团购 16:高铁 18:铁友
    private int _orderType = Integer.MIN_VALUE;
    //订单号
    private long _orderId = Long.MIN_VALUE;
    //预定日期
    private Date _orderDate = Calendar.getInstance().getTime();//FIXME 这里要设置最小的UTC时间
    //预定金额
    private BigDecimal _amount = BigDecimal.ZERO;
    //是否在线预订
    private String _isOnline = "";
    //订单来源
    private String _serverfrom = "";
    //是否首次校验
    private String _checkType = "";
    //创建时间
    private Date _createDate = Calendar.getInstance().getTime();
    //是否最后一次校验
    private String _lastCheck = "";
    //流水号
    private long _refNo = Long.MIN_VALUE;
    //无线设备号
    private String _wirelessClientNo = "";
    //商旅关联ID
    private String _corporationID = "";
    //最终修改时间
    private Date _dataChange_LastTime = Calendar.getInstance().getTime();
    //商户号
    private String _merchantID = "";
    //处理方式 0：无人工干预  1：人工干预
    private int _processingType = Integer.MIN_VALUE;
    //无线设备ID
    private String _clientID = "";
    //订单子类型
    private int _subOrderType = Integer.MIN_VALUE;
    //提交备注
    private String _applyRemark = "";
    //APP客服端版本
    private String _clientVersion = "";
    //商户OrderID
    private String _merchantOrderID = "";
    //订单产品名称
    private String _orderProductName = "";
    //最晚支付时间
    private Date _payExpiryDate = Calendar.getInstance().getTime();
    //预授权金额
    private BigDecimal _preAuthorizedAmount = BigDecimal.ZERO;
    //风控处理截止日期
    private Date _riskCountrolDeadline = Calendar.getInstance().getTime();
    //总优惠金额
    private BigDecimal _totalDiscountAmount = BigDecimal.ZERO;
    //币种
    private String _currency = "";
    //原始金额
    private BigDecimal _originalAmount = BigDecimal.ZERO;


    public long get_reqID()
    {
        return _reqID;
    }

    public void set_reqID(long _reqID)
    {
        this._reqID = _reqID;
    }

    public int get_orderType()
    {
        return _orderType;
    }

    public void set_orderType(int _orderType)
    {
        this._orderType = _orderType;
    }

    public long get_orderId()
    {
        return _orderId;
    }

    public void set_orderId(long _orderId)
    {
        this._orderId = _orderId;
    }

    public Date get_orderDate()
    {
        return _orderDate;
    }

    public void set_orderDate(Date _orderDate)
    {
        this._orderDate = _orderDate;
    }

    public BigDecimal get_amount()
    {
        return _amount;
    }

    public void set_amount(BigDecimal _amount)
    {
        this._amount = _amount;
    }

    public String get_isOnline()
    {
        return _isOnline;
    }

    public void set_isOnline(String _isOnline)
    {
        this._isOnline = _isOnline;
    }

    public String get_serverfrom()
    {
        return _serverfrom;
    }

    public void set_serverfrom(String _serverfrom)
    {
        this._serverfrom = _serverfrom;
    }

    public String get_checkType()
    {
        return _checkType;
    }

    public void set_checkType(String _checkType)
    {
        this._checkType = _checkType;
    }

    public Date get_createDate()
    {
        return _createDate;
    }

    public void set_createDate(Date _createDate)
    {
        this._createDate = _createDate;
    }

    public String get_lastCheck()
    {
        return _lastCheck;
    }

    public void set_lastCheck(String _lastCheck)
    {
        this._lastCheck = _lastCheck;
    }

    public long get_refNo()
    {
        return _refNo;
    }

    public void set_refNo(long _refNo)
    {
        this._refNo = _refNo;
    }

    public String get_wirelessClientNo()
    {
        return _wirelessClientNo;
    }

    public void set_wirelessClientNo(String _wirelessClientNo)
    {
        this._wirelessClientNo = _wirelessClientNo;
    }

    public String get_corporationID()
    {
        return _corporationID;
    }

    public void set_corporationID(String _corporationID)
    {
        this._corporationID = _corporationID;
    }

    public Date get_dataChange_LastTime()
    {
        return _dataChange_LastTime;
    }

    public void set_dataChange_LastTime(Date _dataChange_LastTime)
    {
        this._dataChange_LastTime = _dataChange_LastTime;
    }

    public String get_merchantID()
    {
        return _merchantID;
    }

    public void set_merchantID(String _merchantID)
    {
        this._merchantID = _merchantID;
    }

    public int get_processingType()
    {
        return _processingType;
    }

    public void set_processingType(int _processingType)
    {
        this._processingType = _processingType;
    }

    public String get_clientID()
    {
        return _clientID;
    }

    public void set_clientID(String _clientID)
    {
        this._clientID = _clientID;
    }

    public int get_subOrderType()
    {
        return _subOrderType;
    }

    public void set_subOrderType(int _subOrderType)
    {
        this._subOrderType = _subOrderType;
    }

    public String get_applyRemark()
    {
        return _applyRemark;
    }

    public void set_applyRemark(String _applyRemark)
    {
        this._applyRemark = _applyRemark;
    }

    public String get_clientVersion()
    {
        return _clientVersion;
    }

    public void set_clientVersion(String _clientVersion)
    {
        this._clientVersion = _clientVersion;
    }

    public String get_merchantOrderID()
    {
        return _merchantOrderID;
    }

    public void set_merchantOrderID(String _merchantOrderID)
    {
        this._merchantOrderID = _merchantOrderID;
    }

    public String get_orderProductName()
    {
        return _orderProductName;
    }

    public void set_orderProductName(String _orderProductName)
    {
        this._orderProductName = _orderProductName;
    }

    public Date get_payExpiryDate()
    {
        return _payExpiryDate;
    }

    public void set_payExpiryDate(Date _payExpiryDate)
    {
        this._payExpiryDate = _payExpiryDate;
    }

    public BigDecimal get_preAuthorizedAmount()
    {
        return _preAuthorizedAmount;
    }

    public void set_preAuthorizedAmount(BigDecimal _preAuthorizedAmount)
    {
        this._preAuthorizedAmount = _preAuthorizedAmount;
    }

    public Date get_riskCountrolDeadline()
    {
        return _riskCountrolDeadline;
    }

    public void set_riskCountrolDeadline(Date _riskCountrolDeadline)
    {
        this._riskCountrolDeadline = _riskCountrolDeadline;
    }

    public BigDecimal get_totalDiscountAmount()
    {
        return _totalDiscountAmount;
    }

    public void set_totalDiscountAmount(BigDecimal _totalDiscountAmount)
    {
        this._totalDiscountAmount = _totalDiscountAmount;
    }

    public String get_currency()
    {
        return _currency;
    }

    public void set_currency(String _currency)
    {
        this._currency = _currency;
    }

    public BigDecimal get_originalAmount()
    {
        return _originalAmount;
    }

    public void set_originalAmount(BigDecimal _originalAmount)
    {
        this._originalAmount = _originalAmount;
    }
}

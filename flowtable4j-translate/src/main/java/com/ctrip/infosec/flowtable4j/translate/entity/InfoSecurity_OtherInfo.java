package com.ctrip.infosec.flowtable4j.translate.entity;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lpxie on 15-3-31.
 */
public class InfoSecurity_OtherInfo
{
    private long _reqID = Long.MIN_VALUE;
    private int _orderToSignUpDate = Integer.MIN_VALUE;
    private int _takeOffToOrderDate = Integer.MIN_VALUE;
    private Date _dataChange_LastTime = Calendar.getInstance().getTime();
    private String _orderInfoExternalURL = "";
    private String _bid = "";

    public long get_reqID()
    {
        return _reqID;
    }

    public void set_reqID(long _reqID)
    {
        this._reqID = _reqID;
    }

    public int get_orderToSignUpDate()
    {
        return _orderToSignUpDate;
    }

    public void set_orderToSignUpDate(int _orderToSignUpDate)
    {
        this._orderToSignUpDate = _orderToSignUpDate;
    }

    public int get_takeOffToOrderDate()
    {
        return _takeOffToOrderDate;
    }

    public void set_takeOffToOrderDate(int _takeOffToOrderDate)
    {
        this._takeOffToOrderDate = _takeOffToOrderDate;
    }

    public Date get_dataChange_LastTime()
    {
        return _dataChange_LastTime;
    }

    public void set_dataChange_LastTime(Date _dataChange_LastTime)
    {
        this._dataChange_LastTime = _dataChange_LastTime;
    }

    public String get_orderInfoExternalURL()
    {
        return _orderInfoExternalURL;
    }

    public void set_orderInfoExternalURL(String _orderInfoExternalURL)
    {
        this._orderInfoExternalURL = _orderInfoExternalURL;
    }

    public String get_bid()
    {
        return _bid;
    }

    public void set_bid(String _bid)
    {
        this._bid = _bid;
    }
}

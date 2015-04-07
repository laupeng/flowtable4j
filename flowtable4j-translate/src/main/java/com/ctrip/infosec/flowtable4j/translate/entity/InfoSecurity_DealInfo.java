package com.ctrip.infosec.flowtable4j.translate.entity;

/**
 * Created by lpxie on 15-3-31.
 */
public class InfoSecurity_DealInfo
{
    //主键自增ID
    private long _reqID = Long.MIN_VALUE;
    //校验状态 0：开始校验  1：获取信用卡信息 2：获取用户信息  ...  100：校验成功
    private byte _checkStatus = Byte.MIN_VALUE;
    //校验次数
    private int _checkNum = Integer.MIN_VALUE;
    //唯一标识码
    private String _referenceID = "";

    public long get_reqID()
    {
        return _reqID;
    }

    public void set_reqID(long _reqID)
    {
        this._reqID = _reqID;
    }

    public byte get_checkStatus()
    {
        return _checkStatus;
    }

    public void set_checkStatus(byte _checkStatus)
    {
        this._checkStatus = _checkStatus;
    }

    public int get_checkNum()
    {
        return _checkNum;
    }

    public void set_checkNum(int _checkNum)
    {
        this._checkNum = _checkNum;
    }

    public String get_referenceID()
    {
        return _referenceID;
    }

    public void set_referenceID(String _referenceID)
    {
        this._referenceID = _referenceID;
    }
}

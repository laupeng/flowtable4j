package com.ctrip.infosec.flowtable4j.translate.entity;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lpxie on 15-3-31.
 */
public class InfoSecurity_ContactInfo
{
    private long _reqID = Long.MIN_VALUE;
    private String _contactName = "";
    private String _mobilePhone = "";
    private String _contactEMail = "";
    private String _contactTel = "";
    private String _contactFax = "";
    private String _zipCode = "";
    private String _telCall = "";
    private String _forignMobilePhone = "";
    private String _sendTickerAddr = "";
    private String _postAddress = "";
    private String _mobilePhoneProvince = "";
    private String _mobilePhoneCity = "";
    private Date _dataChange_LastTime = Calendar.getInstance().getTime();
    private String _remark = "";

    public long get_reqID()
    {
        return _reqID;
    }

    public void set_reqID(long _reqID)
    {
        this._reqID = _reqID;
    }

    public String get_contactName()
    {
        return _contactName;
    }

    public void set_contactName(String _contactName)
    {
        this._contactName = _contactName;
    }

    public String get_mobilePhone()
    {
        return _mobilePhone;
    }

    public void set_mobilePhone(String _mobilePhone)
    {
        this._mobilePhone = _mobilePhone;
    }

    public String get_contactEMail()
    {
        return _contactEMail;
    }

    public void set_contactEMail(String _contactEMail)
    {
        this._contactEMail = _contactEMail;
    }

    public String get_contactTel()
    {
        return _contactTel;
    }

    public void set_contactTel(String _contactTel)
    {
        this._contactTel = _contactTel;
    }

    public String get_contactFax()
    {
        return _contactFax;
    }

    public void set_contactFax(String _contactFax)
    {
        this._contactFax = _contactFax;
    }

    public String get_zipCode()
    {
        return _zipCode;
    }

    public void set_zipCode(String _zipCode)
    {
        this._zipCode = _zipCode;
    }

    public String get_telCall()
    {
        return _telCall;
    }

    public void set_telCall(String _telCall)
    {
        this._telCall = _telCall;
    }

    public String get_forignMobilePhone()
    {
        return _forignMobilePhone;
    }

    public void set_forignMobilePhone(String _forignMobilePhone)
    {
        this._forignMobilePhone = _forignMobilePhone;
    }

    public String get_sendTickerAddr()
    {
        return _sendTickerAddr;
    }

    public void set_sendTickerAddr(String _sendTickerAddr)
    {
        this._sendTickerAddr = _sendTickerAddr;
    }

    public String get_postAddress()
    {
        return _postAddress;
    }

    public void set_postAddress(String _postAddress)
    {
        this._postAddress = _postAddress;
    }

    public String get_mobilePhoneProvince()
    {
        return _mobilePhoneProvince;
    }

    public void set_mobilePhoneProvince(String _mobilePhoneProvince)
    {
        this._mobilePhoneProvince = _mobilePhoneProvince;
    }

    public String get_mobilePhoneCity()
    {
        return _mobilePhoneCity;
    }

    public void set_mobilePhoneCity(String _mobilePhoneCity)
    {
        this._mobilePhoneCity = _mobilePhoneCity;
    }

    public Date get_dataChange_LastTime()
    {
        return _dataChange_LastTime;
    }

    public void set_dataChange_LastTime(Date _dataChange_LastTime)
    {
        this._dataChange_LastTime = _dataChange_LastTime;
    }

    public String get_remark()
    {
        return _remark;
    }

    public void set_remark(String _remark)
    {
        this._remark = _remark;
    }
}

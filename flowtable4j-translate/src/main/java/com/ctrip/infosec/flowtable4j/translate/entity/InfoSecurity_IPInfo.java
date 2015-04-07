package com.ctrip.infosec.flowtable4j.translate.entity;

/**
 * Created by lpxie on 15-3-31.
 */
public class InfoSecurity_IPInfo
{
    private long _reqID = Long.MIN_VALUE;
    private long _userIPValue = Long.MIN_VALUE;
    private String _userIPAdd = null;
    private String _iPCountry = null;
    private int _iPCity = Integer.MIN_VALUE;
    private int _continent = Integer.MIN_VALUE;

    public long get_reqID()
    {
        return _reqID;
    }

    public void set_reqID(long _reqID)
    {
        this._reqID = _reqID;
    }

    public long get_userIPValue()
    {
        return _userIPValue;
    }

    public void set_userIPValue(long _userIPValue)
    {
        this._userIPValue = _userIPValue;
    }

    public String get_userIPAdd()
    {
        return _userIPAdd;
    }

    public void set_userIPAdd(String _userIPAdd)
    {
        this._userIPAdd = _userIPAdd;
    }

    public String get_iPCountry()
    {
        return _iPCountry;
    }

    public void set_iPCountry(String _iPCountry)
    {
        this._iPCountry = _iPCountry;
    }

    public int get_iPCity()
    {
        return _iPCity;
    }

    public void set_iPCity(int _iPCity)
    {
        this._iPCity = _iPCity;
    }

    public int get_continent()
    {
        return _continent;
    }

    public void set_continent(int _continent)
    {
        this._continent = _continent;
    }
}

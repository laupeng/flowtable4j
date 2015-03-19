package com.ctrip.flowtable4j.core.flowRule.entity;

import com.ctrip.flowtable4j.core.Utils.CommonUtils;

public class BaseLog {
    private static String PD_LINE = "RCD";
    private static String APPID; 

    private String _errorcode = "";
    private String _category = "";

    static
    {
    	APPID = CommonUtils.getAppId();
    }
    
    public BaseLog(String errorcode,String errorCategory)
    {
        _errorcode = errorcode;
        _category = errorCategory;
    }

    /// <summary>
    /// 应用写入CLog的时候真实值
    /// </summary>
    /// <returns></returns>
    @Override
    public String toString()
    {
    	
        return  PD_LINE+_category+APPID+_errorcode;
    }
}

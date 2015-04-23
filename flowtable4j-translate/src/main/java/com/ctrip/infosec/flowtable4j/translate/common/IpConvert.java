package com.ctrip.infosec.flowtable4j.translate.common;

/**
 * Created by lpxie on 15-3-31.
 */
public class IpConvert
{
    public static long ipConvertTo10(String userIp)
    {
        if(userIp==null||userIp.isEmpty())
        {
            throw new IllegalArgumentException("参数不能为空");
        }
        long ipValue = 0;
        String[] ipStr = userIp.split("[.]");
        if(ipStr.length != 4)
        {
            throw new IllegalArgumentException("用户Ip不是正常的Ip地址");
        }
        ipValue = 256*256*256*Long.parseLong(ipStr[0])+256*256*Long.parseLong(ipStr[1])+256*Long.parseLong(ipStr[2])+Long.parseLong(ipStr[3]);
        return ipValue;
    }


}

package com.ctrip.infosec.flowtable4j.translate.common;

/**
 * Created by lpxie on 15-3-31.
 */
public class IpConvert {
    public static long ipConvertTo10(String userIp) {
        if (userIp == null || userIp.isEmpty()) {
            throw new IllegalArgumentException("参数不能为空");
        }
        long ipValue = 0;
        String[] ipStr = userIp.split("[.]");
        if (ipStr.length != 4) {
            throw new IllegalArgumentException("用户Ip不是正常的Ip地址");
        }
        ipValue = 256 * 256 * 256 * Long.parseLong(ipStr[0]) + 256 * 256 * Long.parseLong(ipStr[1]) + 256 * Long.parseLong(ipStr[2]) + Long.parseLong(ipStr[3]);
        return ipValue;
    }


    public static String ipConvertToStr(long Ip) {
        long a = (Ip & 0xFF000000) >> 24;
        long b = (Ip & 0x00FF0000) >> 16;
        long c = (Ip & 0x0000FF00) >> 8;
        long d = Ip & 0x000000FF;
        return a + "." + b + "." + c + "." + d;
    }

    public static long ipConvertToValue(String ip) {
        long n_Ip = 0;
        if (ip != null && ip.length() > 7) {
            String[] arr = ip.split("[.]|[:]");
            if (arr.length >= 4) {
                long a = Long.parseLong(arr[0].toString());
                long b = Long.parseLong(arr[1].toString());
                long c = Long.parseLong(arr[2].toString());
                long d = Long.parseLong(arr[3].toString());
                n_Ip = (((((a << 8) | b) << 8) | c) << 8) | d;
            }
        }
        return n_Ip;
    }

}

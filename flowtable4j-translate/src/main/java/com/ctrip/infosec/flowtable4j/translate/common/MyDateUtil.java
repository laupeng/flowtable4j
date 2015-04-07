package com.ctrip.infosec.flowtable4j.translate.common;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

/**
 * Created by lpxie on 15-4-3.
 */
public class MyDateUtil
{
    /**
     * DateFormat
     */
    public static FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    /**
     * DateFormat, example: "2015-01-21 12:45:22.243"
     */
    public static FastDateFormat fastDateFormatInMicroSecond = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 计算绝对时间
     * @param date1
     * @param date2
     * @return
     */
    public static long getDateAbs(Date date1,Date date2)
    {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        return Math.abs(time1-time2);
    }
}

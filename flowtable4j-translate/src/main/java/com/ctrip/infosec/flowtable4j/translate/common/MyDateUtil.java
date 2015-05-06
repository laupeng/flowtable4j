package com.ctrip.infosec.flowtable4j.translate.common;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
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
     * type：0代表天，1代表小时，2代表分钟，3代表秒
     * @param date1
     * @param date2
     * @return
     */
    public static long getDateAbs(Date date1,Date date2,int type)
    {
        long result = 0;
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long absTime = Math.abs(time1-time2);
        switch (type)
        {
            //天
            case 0:
                result = absTime/86400000;
                break;
            //小时
            case 1:
                result = absTime/3600000;
                break;
            //分钟
            case 2:
                result = absTime/60000;
                break;
            //秒
            case 3:
                result = absTime/1000;
                break;
        }
        return result;
    }
}

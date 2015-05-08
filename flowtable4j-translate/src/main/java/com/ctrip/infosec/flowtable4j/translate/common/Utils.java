package com.ctrip.infosec.flowtable4j.translate.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lpxie on 15-4-3.
 */
public class Utils
{
    public static MyJSON Json = new MyJSON();

    public static String getValue(Map data,String key)
    {
        return data.get(key) == null ? "" : data.get(key).toString();
    }

    public static Map getValueMap(Map data,String key)
    {
        return data.get(key) == null ? new HashMap() : (Map)data.get(key);
    }
}

package com.ctrip.infosec.flowtable4j.translate.common;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.Json;
/**
 * Created by lpxie on 15-5-12.
 */
public class BeanMapper
{
    /**
    * 基于Jackson2将对象A的值拷贝到对象B中.
    */
    public static <T> T copy(Object source, Class<T> clazz)
    {
        return Json.parseObject(Json.toJSONString(source),clazz);
    }
}

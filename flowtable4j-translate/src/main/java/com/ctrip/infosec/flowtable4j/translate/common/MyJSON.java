package com.ctrip.infosec.flowtable4j.translate.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 * Created by lpxie on 15-4-3.
 */
public class MyJSON
{
    private ObjectMapper mapper = null;

    public MyJSON(){
        this(null);
    }

    public MyJSON(JsonInclude.Include include)
    {
        mapper = new ObjectMapper();
        if(include!=null)
        {
            mapper.setSerializationInclusion(include);
        }
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.setDateFormat(new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss")));
    }

    public static MyJSON nonEmptyMapper(){
        return new MyJSON(JsonInclude.Include.NON_EMPTY);
    }

    public static MyJSON nonDefaultMapper(){
        return new MyJSON(JsonInclude.Include.NON_DEFAULT);
    }

    /**
     *
     * @param object Object如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     * @return
     */
    public String toJSONString(Object object)
    {
        try
        {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String toPrettyJSONString(Object object)
    {
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
     * @see #parseObject(String, com.fasterxml.jackson.databind.JavaType)
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T parseObject(String jsonString,Class<T> clazz)
    {
        if(StringUtils.isEmpty(jsonString))
            return null;
        try
        {
            return mapper.readValue(jsonString,clazz);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T parseObject(String jsonString,JavaType javaType)
    {
        if(StringUtils.isEmpty(jsonString))
            return null;

        try
        {
            return (T)mapper.readValue(jsonString,javaType);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public JavaType constructCollectionType(Class<?> collectionClass,Class<?>... elementClasses )
    {
        return mapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
    }

    public JavaType constructCollectionType(Class<?> collectionClass,JavaType... javaTypes)
    {
        return mapper.getTypeFactory().constructParametricType(collectionClass,javaTypes);
    }

    public JavaType constructSimpleType(Class<?> elementClasses)
    {
        return mapper.getTypeFactory().uncheckedSimpleType(elementClasses);
    }
}

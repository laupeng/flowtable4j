package com.ctrip.infosec.flowtable4j.translate.common;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

/**
 * Created by lpxie on 15-4-2.
 */
public class GlobalConfig
{
    private static final String CONFIG_FILENAME = "/GlobalConfig.properties";
    private static CombinedConfiguration config = new CombinedConfiguration();

    static{
        try{
            InputStream in = GlobalConfig.class.getResourceAsStream(CONFIG_FILENAME);
            PropertiesConfiguration configuration = new PropertiesConfiguration();
            //不自动分割值
            configuration.setDelimiterParsingDisabled(true);
            configuration.load(in);
            config.addConfiguration(configuration);
        }catch(Exception ex)
        {
            throw new RuntimeException("Load config file["+CONFIG_FILENAME+"] exception.",ex);
        }
    }

    public static Properties getProperties(){
        return ConfigurationConverter.getProperties(config);
    }

    public static boolean containsKey(String key){
        return config.containsKey(key);
    }

    public static String getString(String key){
        return config.getString(key);
    }

    public static String getString(String key,String defaultValue){
        return config.getString(key,defaultValue);
    }

    public static Boolean getBoolean(String key) {
        return config.getBoolean(key, null);
    }

    public static Boolean getBoolean(String key,Boolean defaultValue){
        return config.getBoolean(key,defaultValue);
    }

    public static Byte getByte(String key) {
        return config.getByte(key, null);
    }

    public static Byte getByte(String key, Byte defaultValue) {
        return config.getByte(key, defaultValue);
    }

    public static Short getShort(String key) {
        return config.getShort(key, null);
    }

    public static Short getShort(String key, Short defaultValue) {
        return config.getShort(key, defaultValue);
    }

    public static Integer getInt(String key) {
        return config.getInteger(key, null);
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        return config.getInteger(key, defaultValue);
    }

    public static Long getLong(String key) {
        return config.getLong(key, null);
    }

    public static Long getLong(String key, Long defaultValue) {
        return config.getLong(key, defaultValue);
    }

    public static Float getFloat(String key) {
        return config.getFloat(key, null);
    }

    public static Float getFloat(String key, Float defaultValue) {
        return config.getFloat(key, defaultValue);
    }

    public static Double getDouble(String key) {
        return config.getDouble(key, null);
    }

    public static Double getDouble(String key, Double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    public static BigDecimal getBigDecimal(String key) {
        return config.getBigDecimal(key);
    }

    public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return config.getBigDecimal(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {
        return config.getBigInteger(key);
    }

    public static BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return config.getBigInteger(key, defaultValue);
    }

}

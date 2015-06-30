package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.dal.CheckRiskDAO;
import com.ctrip.infosec.flowtable4j.dal.ESBClient;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.persist.ColumnInfo;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 放入工具类方法
 * Created by thyang on 2015-06-10.
 */
public class ConverterBase {

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    protected static JsonMapper mapper = new JsonMapper();

    @Autowired
    protected CheckRiskDAO checkRiskDAO;

    @Autowired
    protected ESBClient esbClient;

    @Autowired
    protected Save2DbProcessor dbService;


    /**
     * 从一个Map里面取对应字段到Target
     *
     * @param root
     * @param keyPath
     * @param target
     * @param fieldMap Src -> Targe映射
     */
    protected void copyMap(Map<String, Object> root, String keyPath, Map<String, Object> target, Map<String, String> fieldMap) {
        if (root != null && target!=null) {
            Map<String, Object> src = (Map<String, Object>) MapX.getMap(root, keyPath);
            if (src != null && fieldMap!=null) {
                for (String key : fieldMap.keySet()) {
                    setValue(target, fieldMap.get(key), getString(src, key));
                }
            }
        }
    }


    /**
     * Map拷贝
     *
     * @param root
     * @param target
     * @param fieldMap src -> Targe
     */
    protected void copyMap(Map<String, Object> root, Map<String, Object> target, Map<String, String> fieldMap) {
        if (root != null && target != null && fieldMap!=null) {
            for (String key : fieldMap.keySet()) {
                setValue(target, fieldMap.get(key), getString(root, key));
            }
        }
    }

    /**
     * 从一个Map拷贝对应字段到Target
     *
     * @param root
     * @param keyPath
     * @param target
     * @param fields
     */
    protected void copyMap(Map<String, Object> root, String keyPath, Map<String, Object> target, String[] fields) {
        if (root != null && target!=null && fields!=null) {
            Map<String, Object> src = (Map<String, Object>) MapX.getMap(root, keyPath);
            if (src != null) {
                for (String key : fields) {
                    setValue(target, key, getString(src, key));
                }
            }
        }
    }

    /**
     * Map对拷指定字段
     *
     * @param src
     * @param target
     * @param fields
     */
    protected void copyMap(Map<String, Object> src, Map<String, Object> target, String[] fields) {
        if (src != null && target!=null && fields!=null) {
            for (String key : fields) {
                setValue(target, key, getString(src, key));
            }
        }
    }

    /**
     * 根据字段映射从RequestBody取数据
     *
     * @param src       原始Map
     * @param targetMap 目标Map
     * @param dbEntity  表名
     */
    protected void copyMap(Map<String, Object> src, Map<String, Object> targetMap, String dbEntity) {
        List<ColumnInfo> dbMeta = dbService.getDbMeta(dbEntity);
        if (dbMeta != null && dbMeta.size() > 0 && targetMap!=null) {
            for (ColumnInfo info : dbMeta) {
                setValue(targetMap, info.getName(), getObject(src, info.getName()));
            }
        }
    }

    public String getString(Map<String, Object> data, String key) {
        return MapX.getString(data, key);
    }

    public String getString(Map<String, Object> data, String[] key) {
        return MapX.getString(data, key);
    }

    public Object getObject(Map<String, Object> data, String key) {
        return MapX.getObject(data, key);
    }

    public Object getObject(Map<String, Object> data, String[] key) {
        return MapX.getObject(data, key);
    }

    public boolean setValue(Map<String, Object> target, String key, Object value) {
        return MapX.setValue(target, key, value);
    }

    public boolean setValueIfNotEmpty(Map<String, Object> target, String key, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }


    public boolean setValue(Map<String, Object> target, String[] key, Object value) {
        return MapX.setValue(target, key, value);
    }

    protected String ipConvertToStr(long Ip) {
        long a = (Ip & 0xFF000000) >> 24;
        long b = (Ip & 0x00FF0000) >> 16;
        long c = (Ip & 0x0000FF00) >> 8;
        long d = Ip & 0x000000FF;
        return a + "." + b + "." + c + "." + d;
    }

    protected long ipConvertToValue(String ip) {
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

    protected String dateDiffHour(String startDate, String endDate) {
        if (Strings.isNullOrEmpty(startDate) || Strings.isNullOrEmpty(endDate)) {
            return "0";
        }
        try {
            long S = sdf.parse(startDate).getTime();
            long E = sdf.parse(endDate).getTime();
            return String.valueOf(Math.abs((S - E) / 1000 / 60 / 60));
        } catch (Exception ex) {
            return "0";
        }
    }


}

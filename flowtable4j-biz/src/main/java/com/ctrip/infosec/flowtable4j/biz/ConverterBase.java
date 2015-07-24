package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.biz.processor.Save2DbProcessor;
import com.ctrip.infosec.flowtable4j.dal.CheckRiskDAO;
import com.ctrip.infosec.flowtable4j.dal.ESBClient;
import com.ctrip.infosec.flowtable4j.dal.RiskProfile;
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

    @Autowired
    protected RiskProfile riskProfile;
    /**
     * 从一个Map里面取对应字段到Target
     * 不拷贝Null值
     *
     * @param src
     * @param target
     * @param fieldMap Src -> Targe映射
     */
    protected void copyMapIfNotNull(Map<String, Object> src, Map<String, Object> target, Map<String, String> fieldMap) {
        if (src != null && target != null && fieldMap != null) {
            for (String key : fieldMap.keySet()) {
                setValueIfNotNull(target, fieldMap.get(key), getObject(src, key));
            }
        }
    }

    /**
     * Map拷贝
     *
     * @param src
     * @param target
     * @param fieldMap src -> Targe
     */
    protected void copyMap(Map<String, Object> src, Map<String, Object> target, Map<String, String> fieldMap) {
        if (src != null && target != null && fieldMap != null) {
            for (String key : fieldMap.keySet()) {
                setValueIfNotNull(target, fieldMap.get(key), getObject(src, key));
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
        if (src != null && target != null && fields != null) {
            for (String key : fields) {
                setValueIfNotNull(target, key, getObject(src, key));
            }
        }
    }

    /**
     * Map对拷指定字段
     * 不拷贝Null值
     *
     * @param src
     * @param target
     * @param fields
     */
    protected void copyMapIfNotNull(Map<String, Object> src, Map<String, Object> target, String[] fields) {
        if (src != null && target != null && fields != null) {
            for (String key : fields) {
                setValueIfNotNull(target, key, getObject(src, key));
            }
        }
    }

    /**
     * @param src       原始Map
     * @param targetMap 目标Map
     * @param dbEntity  表名
     */
    protected void copyMap(Map<String, Object> src, Map<String, Object> targetMap, String dbEntity) {
        List<ColumnInfo> dbMeta = dbService.getDbMeta(dbEntity);
        if (dbMeta != null && dbMeta.size() > 0 && targetMap != null) {
            for (ColumnInfo info : dbMeta) {
                setValueIfNotNull(targetMap, info.getName(), getObject(src, info.getName()));
            }
        }
    }

    protected Map<String, Object> getMap(Map<String, Object> data, String key) {
        return MapX.getMap(data, key);
    }

    protected Map<String, Object> getMap(Map<String, Object> data, String[] key) {
        return MapX.getMap(data, key);
    }


    protected List<Map<String, Object>> getList(Map<String, Object> data, String key) {
        return MapX.getList(data, key);
    }

    protected String getString(Map<String, Object> data, String key) {
        return MapX.getString(data, key);
    }

    protected String getString(Map<String, Object> data, String key, String defVal) {
        String obj = MapX.getString(data, key);
        if (obj == null) {
            return defVal;
        }
        return obj;
    }

    protected String getString(Map<String, Object> data, String[] key,String defVal) {
        return MapX.getString(data, key,defVal);
    }

    protected String getString(Map<String, Object> data, String[] key) {
        return MapX.getString(data, key);
    }

    protected Object getObject(Map<String, Object> data, String key) {
        return MapX.getObject(data, key);
    }

    protected boolean setValue(Map<String, Object> target, String key, Object value) {
        return MapX.setValue(target, key, value);
    }

    protected boolean copyValueIfNotNull(Map<String, Object> src, String fromkey, Map<String, Object> target, String toKey) {
        if (src != null && target != null) {
            return setValueIfNotNull(target, toKey, getObject(src, fromkey));
        }
        return false;
    }

    protected boolean copyValue(Map<String, Object> src, String fromkey, Map<String, Object> target, String toKey) {
        if (src != null && target != null) {
            return setValue(target, toKey, getObject(src, fromkey));
        }
        return false;
    }

    protected String concatValueIfNotNull(Map<String, Object> src, String key1, Map<String, Object> target, String key2) {
        if (src != null && target != null) {
            String v1 = getString(src, key1);
            String v2 = getString(target, key2);
            if (v1 != null && v2 != null) {
                return v1 + v2;
            }
        }
        return null;
    }

    protected String concatValueIfNotNull(Map<String, Object> src, String key1, String key2) {
        if (src != null) {
            String v1 = getString(src, key1);
            String v2 = getString(src, key2);
            if (v1 != null && v2 != null) {
                return v1 + v2;
            }
        }
        return null;
    }

    protected String concatValueIfNotNull(String v1, String v2) {
        if (v1 != null && v2 != null) {
            return v1 + v2;
        }
        return null;
    }

    protected boolean setValueIfNotEmpty(Map<String, Object> target, String key, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }

    protected boolean setValueIfNotEmpty(Map<String, Object> target,String key, String v1,String v2) {
        if (!Strings.isNullOrEmpty(v1) && !Strings.isNullOrEmpty(v2)) {
            return MapX.setValue(target, key, v1 + v2);
        }
        return false;
    }

    protected boolean setValueIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }

    /**
     * if v1 and v2 is not Null, then put concat(V1,V2) to target
     *
     * @param target
     * @param key
     * @param v1
     * @param v2
     * @return
     */
    protected boolean setValueIfNotNull(Map<String, Object> target, String key, String v1, String v2) {
        if (v1 != null && v2 != null) {
            return MapX.setValue(target, key, v1 + v2);
        }
        return false;
    }

    /**
     * 合并字段
     *
     * @param target
     * @param key
     * @param key1
     * @param key2
     */
    protected void concatKeys(Map<String, Object> target, String key, String key1, String key2) {
        setValueIfNotNull(target, key, getString(target, key1), getString(target, key2));
    }

    /**
     * 合并字段
     *
     * @param target
     * @param key
     * @param key1
     * @param key2
     */
    protected void concatKeys(Map<String, Object> target, String key, String key1, String key2,String key3) {
        setValueIfNotNull(target, key,concatValueIfNotNull(target,key1,key2), getString(target, key3));
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

    /**
     * 相差小时数，绝对值
     *
     * @param startDate
     * @param endDate
     * @return
     */
    protected long dateDiffHour(String startDate, String endDate) {
        if (Strings.isNullOrEmpty(startDate) || Strings.isNullOrEmpty(endDate)) {
            return 0;
        }
        try {
            long S = sdf.parse(startDate).getTime();
            long E = sdf.parse(endDate).getTime();
            return Math.abs((S - E) / 1000 / 60 / 60);
        } catch (Exception ex) {
            return 0;
        }
    }


}

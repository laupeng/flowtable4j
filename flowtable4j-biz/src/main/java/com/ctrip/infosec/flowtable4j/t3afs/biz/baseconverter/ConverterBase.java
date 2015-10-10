package com.ctrip.infosec.flowtable4j.t3afs.biz.baseconverter;

import com.ctrip.infosec.flowtable4j.t3afs.biz.processor.Save2DbProcessor;
import com.ctrip.infosec.flowtable4j.t3afs.model.MapX;
import com.ctrip.infosec.flowtable4j.t3afs.model.persist.ColumnInfo;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 放入工具类方法
 * Created by thyang on 2015-06-10.
 */
public class ConverterBase {

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    protected Save2DbProcessor dbService;
    /**
     * 创建新Map
     * @return
     */
    protected Map<String,Object> createMap(){
        return new HashMap<String, Object>();
    }

    /**
     * 创建新List
     * @return
     */
    protected List<Map<String,Object>> createList(){
        return new ArrayList<Map<String, Object>>();
    }

    /**
     * 根据SrcList，字段列表返回新的List<Map<String,Object>>
     * @param srcList
     * @param fields
     * @return
     */
    protected List<Map<String,Object>> copyList(List<Map<String,Object>> srcList,String[] fields){
        List<Map<String,Object>> targetList=createList();
        if(srcList!=null && srcList.size()>0){
            for(Map<String,Object> srcMap:srcList){
               targetList.add(copyMap(srcMap,fields));
            }
        }
        return targetList;
    }

    /**
     * 根据srcList，dbEnitity数据库Schema生产List<Map>
     * @param srcList
     * @param dbEntity
     * @return
     */
    protected List<Map<String,Object>> copyList(List<Map<String,Object>> srcList,String dbEntity){
        List<Map<String,Object>> targetList=createList();
        if(srcList!=null && srcList.size()>0){
            for(Map<String,Object> srcMap:srcList){
                targetList.add(copyMap(srcMap,dbEntity));
            }
        }
        return targetList;
    }

    /**
     * 根据srcList，字段映射关系，生成List<Map>
     * @param srcList
     * @param fieldMap
     * @return
     */
    protected List<Map<String,Object>> copyList(List<Map<String,Object>> srcList,Map<String, String> fieldMap){
        List<Map<String,Object>> targetList=createList();
        if(srcList!=null && srcList.size()>0){
            for(Map<String,Object> srcMap:srcList){
                targetList.add(copyMap(srcMap,fieldMap));
            }
        }
        return targetList;
    }

    /**
     * 从一个Map里面取对应字段到到新的Map
     * 不拷贝Null值
     * @param src
     * @param fieldMap  Src -> Targe映射
     */
    protected Map<String,Object> copyMapIfNotNull(Map<String, Object> src, Map<String, String> fieldMap) {
        Map<String,Object> target=createMap();
        if (src != null && target != null && fieldMap != null) {
            for (String key : fieldMap.keySet()) {
                setValueIfNotNull(target, fieldMap.get(key), getObject(src, key));
            }
        }
        return target;
    }

    /**
     * Map拷贝
     * 拷贝Null值
     * @param src
     * @param fieldMap src1 -> Targe
     */
    protected Map<String,Object> copyMap(Map<String, Object> src,Map<String, String> fieldMap) {
        Map<String,Object> target=createMap();
        if (src != null && target != null && fieldMap != null) {
            for (String key : fieldMap.keySet()) {
                setValueIfNotNull(target, fieldMap.get(key), getObject(src, key));
            }
        }
        return target;
    }


    /**
     * Map对拷指定字段
     *
     * @param src
     * @param fields 字段列表，表示Src、Target字段名一样
     */
    protected Map<String,Object> copyMap(Map<String, Object> src, String[] fields) {
        Map<String,Object> target=createMap();
        if (src != null && target != null && fields != null) {
            for (String key : fields) {
                setValueIfNotNull(target, key, getObject(src, key));
            }
        }
        return target;
    }

    /**
     * Map对拷指定字段
     * 不拷贝Null值
     *
     * @param src
     * @param fields
     */
    protected Map<String,Object> copyMapIfNotNull(Map<String, Object> src, String[] fields) {
        Map<String,Object> target=new HashMap<String, Object>();
        if (src != null && target != null && fields != null) {
            for (String key : fields) {
                setValueIfNotNull(target, key, getObject(src, key));
            }
        }
        return target;
    }

    /**
     * @param src        原始Map
     * @param dbEntity  表名
     */
    protected Map<String,Object> copyMap(Map<String, Object> src,String dbEntity) {
        Map<String,Object> targetMap=createMap();
        List<ColumnInfo> dbMeta = dbService.getDbMeta(dbEntity);
        if (src!=null && dbMeta != null && dbMeta.size() > 0 && targetMap != null) {
            for (ColumnInfo info : dbMeta) {
                setValueIfNotNull(targetMap, info.getName(), getObject(src, info.getName()));
            }
        }
        return targetMap;
    }


    /**
     * 从一个Map里面取对应字段到Target
     * 不拷贝Null值
     *
     * @param src
     * @param target
     * @param fieldMap  Src -> Targe映射
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
     * 拷贝Null值
     * @param src
     * @param target
     * @param fieldMap src1 -> Targe
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
     * @param fields 字段列表，表示Src、Target字段名一样
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
     * @param src        原始Map
     * @param targetMap 目标Map
     * @param dbEntity  表名
     */
    protected void copyMap(Map<String, Object> src, Map<String, Object> targetMap, String dbEntity) {
        List<ColumnInfo> dbMeta = dbService.getDbMeta(dbEntity);
        if (src!=null && dbMeta != null && dbMeta.size() > 0 && targetMap != null) {
            for (ColumnInfo info : dbMeta) {
                setValueIfNotNull(targetMap, info.getName(), getObject(src, info.getName()));
            }
        }
    }

    /**
     * 返回Map
     * @param data
     * @param key
     * @return
     */
    protected Map<String, Object> getMap(Map<String, Object> data, String key) {
        if(data!=null) {
            return MapX.getMap(data, key);
        }
        return null;
    }

    /**
     * 返回Map
     * @param data
     * @param key
     * @return
     */
    protected Map<String, Object> getMap(Map<String, Object> data, String[] key) {
        if(data!=null) {
            return MapX.getMap(data, key);
        }
        return null;
    }

    /**
     * 返回List
     * @param data
     * @param key
     * @return
     */
    protected List<Map<String, Object>> getList(Map<String, Object> data, String key) {
        if(data!=null) {
            return MapX.getList(data, key);
        }
        return null;
    }

    /**
     * Map获取值
     * @param data
     * @param key
     * @return
     */
    protected String getString(Map<String, Object> data, String key) {
        if(data!=null) {
            return MapX.getString(data, key);
        }
        return null;
    }

    /**
     * 从Map取值，如为null返回defVal
     * @param data
     * @param key
     * @param defVal
     * @return
     */
    protected String getString(Map<String, Object> data, String key, String defVal) {
        if (data != null) {
            return MapX.getString(data, key,defVal);
        }
        return defVal;
    }

    /**
     * Map中取值，如果为null返回defVal
     * @param data
     * @param key
     * @param defVal
     * @return
     */
    protected String getString(Map<String, Object> data, String[] key,String defVal) {
        if(data!=null) {
            return MapX.getString(data, key,defVal);
        }
        return defVal;
    }

    /**
     * 从Map取值
     * @param data
     * @param key
     * @return
     */
    protected String getString(Map<String, Object> data, String[] key) {
        if(data!=null) {
            return MapX.getString(data, key);
        }
        return null;
    }

    /**
     * 获取值
     * @param data
     * @param key
     * @return
     */
    protected Object getObject(Map<String, Object> data, String key) {
        if(data!=null) {
            return MapX.getObject(data, key);
        }
        return null;
    }

    /**
     * 写入Map
     * @param target
     * @param key
     * @param value
     * @return
     */
    protected boolean setValue(Map<String, Object> target, String key, Object value) {
        if(target!=null) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }

    /**
     * 取src的fromKey写入taget的toKey
     * 不写入Null值
     * @param src
     * @param fromkey
     * @param target
     * @param toKey
     * @return
     */
    protected boolean copyValueIfNotNull(Map<String, Object> src, String fromkey, Map<String, Object> target, String toKey) {
        if (src != null && target != null) {
            return setValueIfNotNull(target, toKey, getObject(src, fromkey));
        }
        return false;
    }

    /**
     * 取src的fromKey写入taget的toKey
     * @param src
     * @param fromkey
     * @param target
     * @param toKey
     * @return
     */
    protected boolean copyValue(Map<String, Object> src, String fromkey, Map<String, Object> target, String toKey) {
        if (src != null && target != null) {
            return setValue(target, toKey, getObject(src, fromkey));
        }
        return false;
    }

    /**
     * 从两个Map取两个Key的值合并，任一为null返回null
     * @param src1
     * @param key1
     * @param src2
     * @param key2
     * @return
     */
    protected String concatValueIfNotNull(Map<String, Object> src1, String key1, Map<String, Object> src2, String key2) {
        if (src1 != null && src2 != null) {
            String v1 = getString(src1, key1);
            String v2 = getString(src2, key2);
            if (v1 != null && v2 != null) {
                return v1 + v2;
            }
        }
        return null;
    }

    /**
     * src里面取两个键值合并，任一为null返回null
     * @param src
     * @param key1
     * @param key2
     * @return
     */
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

    /**
     * 返回 v1+v2,任一为null返回null
     * @param v1
     * @param v2
     * @return
     */
    protected String concatValueIfNotNull(String v1, String v2) {
        if (v1 != null && v2 != null) {
            return v1 + v2;
        }
        return null;
    }

    /**
     * 如果value非空，置于 target的key中
     * @param target
     * @param key
     * @param value
     * @return
     */
    protected boolean setValueIfNotEmpty(Map<String, Object> target, String key, String value) {
        if (target!=null && !Strings.isNullOrEmpty(value)) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }

    /**
     * v1+v2如果全部非空，置于Target的key
     * @param target
     * @param key
     * @param v1
     * @param v2
     * @return
     */
    protected boolean setValueIfNotEmpty(Map<String, Object> target,String key, String v1,String v2) {
        if (target!=null && !Strings.isNullOrEmpty(v1) && !Strings.isNullOrEmpty(v2)) {
            return MapX.setValue(target, key, v1 + v2);
        }
        return false;
    }


    /**
     * 如果Value不为null则设置
     * @param target
     * @param key
     * @param value
     * @return
     */
    protected boolean setValueIfNotNull(Map<String, Object> target, String key, Object value) {
        if (target!=null && value != null) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }

    /**
     * 把v1+v2的值写入 key，遇Null不写
     *
     * @param target
     * @param key
     * @param v1
     * @param v2
     * @return
     */
    protected boolean setValueIfNotNull(Map<String, Object> target, String key, String v1, String v2) {
        if (target!=null && v1 != null && v2 != null) {
            return MapX.setValue(target, key, v1 + v2);
        }
        return false;
    }

    /**
     * 合并字段
     * 两个字段合并写入key，遇Null则不写
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
     * 三个字段的值合并写入 key，遇Null则不写
     * @param target
     * @param key
     * @param key1
     * @param key2
     */
    protected void concatKeys(Map<String, Object> target, String key, String key1, String key2,String key3) {
        setValueIfNotNull(target, key,concatValueIfNotNull(target,key1,key2), getString(target, key3));
    }

    /**
     * IP的10进制转带点地址
     * @param Ip
     * @return
     */
    protected String ipConvertToStr(long Ip) {
        long a = (Ip & 0xFF000000) >> 24;
        long b = (Ip & 0x00FF0000) >> 16;
        long c = (Ip & 0x0000FF00) >> 8;
        long d = Ip & 0x000000FF;
        return a + "." + b + "." + c + "." + d;
    }

    /**
     * IP转long，非法则返回0
     * @param ip
     * @return
     */
    protected long ipConvertToValue(String ip) {
        long n_Ip = 0;
        try {
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
        }
        catch (Exception ex){
           //
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

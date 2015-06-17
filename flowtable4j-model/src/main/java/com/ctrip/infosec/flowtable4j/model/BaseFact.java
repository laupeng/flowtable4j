package com.ctrip.infosec.flowtable4j.model;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-15.
 */
public abstract class BaseFact {
    private boolean ignoreCase =false;
    protected abstract Map<String,Object> getRootMap();

    /**
     * 获取直接双亲
     * @param keyPath 搜索路径
     * @return 父节点Map
     */
    private Map<String, Object> getDirectParentMapNode(String[] keyPath) {
        Map<String, Object> prevMap = getRootMap();
        if (keyPath.length > 1) {
            for (int i = 0; i < keyPath.length - 1; i++) {
                if (prevMap != null) {
                    prevMap = getChildMap(prevMap, keyPath[i]);
                } else {
                    break;
                }
            }
        }
        return prevMap;
    }

    /**
     * 获取String
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public Map getMap(String keyPath) {
        return getMap(new String[]{keyPath});
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public Map getMap(String[] keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            if (parentMap.containsKey(key)) {
                return (Map) parentMap.get(key);
            } else if (ignoreCase && parentMap.containsKey(key.toUpperCase())) {
                return (Map) parentMap.get(key.toUpperCase());
            }
        }
        return null;
    }

    /**
     * 获取String
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public String getString(String keyPath) {
        return getString(new String[]{keyPath});
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public String getString(String[] keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            if (parentMap.containsKey(key)) {
                Object obj = parentMap.get(key);
                if(obj != null)
                {
                    return obj.toString();
                }
            } else if (ignoreCase && parentMap.containsKey(key.toUpperCase())) {
                Object obj = parentMap.get(key.toUpperCase());
                if(obj != null)
                {
                    return obj.toString();
                }
            }
        }
        return null;
    }

    /**
     * 获取子节点
     *
     * @param parentMapNode 父节点
     * @param childNode     子节点名称
     * @return 子节点Map
     */
    private Map<String, Object> getChildMap(Map<String, Object> parentMapNode, String childNode) {
        if (parentMapNode.containsKey(childNode)) {
            return (Map<String, Object>) parentMapNode.get(childNode);
        } else if(ignoreCase && parentMapNode.containsKey(childNode.toUpperCase())){
            return (Map<String, Object>) parentMapNode.get(childNode.toUpperCase());
        }
        return null;
    }

    /**
     * 获取List
     * @param  keyPath 搜索路径
     * @return返回 List，Java默认反序列化为ArrayList
     */
    public List getList(String  keyPath)
    {
        return getList(new String[]{keyPath});
    }

    /**
     * 获取List
     * @param  keyPath 搜索路径
     * @return返回 List，Java默认反序列化为ArrayList
     */
    public List getList(String[] keyPath)
    {
        Map<String,Object> parentMap = getDirectParentMapNode(keyPath);
        if(parentMap!=null) {
            String key = keyPath[keyPath.length-1];
            if (parentMap.containsKey(key)) {
                return (List) parentMap.get(key);
            } else if(ignoreCase && parentMap.containsKey(key.toUpperCase())){
                return (List) parentMap.get(key.toUpperCase());
            }
        }
        return null;
    }

}

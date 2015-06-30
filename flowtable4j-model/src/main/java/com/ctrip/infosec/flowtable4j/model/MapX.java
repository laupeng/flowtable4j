package com.ctrip.infosec.flowtable4j.model;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-15.
 */
public class MapX {
    private static boolean ignoreCase =false;
    /**
     * 获取直接双亲
     * @param keyPath 搜索路径
     * @return 父节点Map
     */
    private static Map<String, Object> getDirectParentMapNode(Map<String,Object> root,String[] keyPath) {
        Map<String, Object> prevMap = root;
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
    public static Map getMap(Map<String,Object> root,String keyPath) {
        if (root.containsKey(keyPath)) {
            return (Map) root.get(keyPath);
        } else if (ignoreCase && root.containsKey(keyPath.toUpperCase())) {
            return (Map) root.get(keyPath.toUpperCase());
        }
        return null;
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static Map getMap(Map<String,Object> root,String[] keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(root,keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            return getMap(parentMap,key);
        }
        return null;
    }
    /**
     * 获取String
     * @param key 搜索路径
     * @return 字符串
     */
    public static Object getObject(Map<String,Object> parentMap,String key) {
        if (parentMap.containsKey(key)) {
            return  parentMap.get(key);
        } else if (ignoreCase && parentMap.containsKey(key.toUpperCase())) {
            return parentMap.get(key.toUpperCase());
        }
        return null;
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static Object getObject(Map<String,Object> root,String[] keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(root,keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            return getObject(parentMap, key);
        }
        return null;
    }
    /**
     * 获取String
     * @param key 搜索路径
     * @return 字符串
     */
    public static String getString(Map<String,Object> parentMap,String key) {
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
        return null;
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static String getString(Map<String,Object> root,String[] keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(root,keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            return getString(parentMap,key);
        }
        return null;
    }

    /**
     * 获取String
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static String getStringEmpty(Map<String,Object> root,String keyPath) {
        return Strings.nullToEmpty(getString(root,keyPath));
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static String getStringEmpty(Map<String,Object> root,String[] keyPath) {
        return Strings.nullToEmpty(getString(root,keyPath));
    }

    /**
     * 获取子节点
     *
     * @param parentMapNode 父节点
     * @param childNode     子节点名称
     * @return 子节点Map
     */
    private static Map<String, Object> getChildMap(Map<String, Object> parentMapNode, String childNode) {
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
    public static List getList(Map<String,Object> root,String  keyPath)
    {
        return (List) root.get(keyPath);
    }

    /**
     * 获取List
     * @param  keyPath 搜索路径
     * @return返回 List，Java默认反序列化为ArrayList
     */
    public static List getList(Map<String,Object> root,String[] keyPath)
    {
        Map<String,Object> parentMap = getDirectParentMapNode(root,keyPath);
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

    /**
     * 给Map赋值
     * @param root
     * @param key
     * @param value
     * @return
     */
    public static boolean setValue(Map<String,Object> root,String key,Object value){
        root.put(key,value);
        return true;
    }

    /**
     * 给Map赋值
     * @param root
     * @param key
     * @param value
     * @return
     */
    public static boolean setValue(Map<String,Object> root,String[] key,Object value){
        if(key.length==1) {
            root.put(key[0], value);
        }
        else
        {
            Map<String,Object> parent = root;
            Map<String,Object> child =null;
            for (int i = 0; i < key.length - 1; i++) {
                child = getChildMap(parent,key[i]);
                if (child == null) {
                    child = new HashMap<String, Object>();
                    parent.put(key[i], child);
                }
                parent = child;
            }
            child.put(key[key.length-1],value);
        }
        return true;
    }
}

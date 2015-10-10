package com.ctrip.infosec.flowtable4j.t3afs.model;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-15.
 */
public class MapX {
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
         return root==null? null: (Map) root.get(keyPath);
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static Map getMap(Map<String,Object> root,String[] keyPath) {
         return root==null? null:getMap(getDirectParentMapNode(root, keyPath), keyPath[keyPath.length - 1]);
    }

    /**
     * 获取String
     * @param key 搜索路径
     * @return 字符串
     */
    public static Object getObject(Map<String,Object> parentMap,String key) {
        return parentMap==null? null:parentMap.get(key);
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static Object getObject(Map<String,Object> root,String[] keyPath) {
        return root==null? null:getObject(getDirectParentMapNode(root, keyPath), keyPath[keyPath.length - 1]);
    }
    /**
     * 获取String
     * @param key 搜索路径
     * @return 字符串
     */
    public static String getString(Map<String,Object> parentMap,String key) {
        if (parentMap!=null) {
            Object obj = parentMap.get(key);
            return obj==null? null:obj.toString();
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
        return root==null? null:getString(getDirectParentMapNode(root,keyPath),keyPath[keyPath.length - 1]);
    }

    /**
     * 获取String
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static String getString(Map<String,Object> root,String keyPath,String defVal) {
        String val =getString(root,keyPath);
        return val==null? defVal:val;
    }

    /**
     * 获取String
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public static String getString(Map<String,Object> root,String[] keyPath,String defVal) {
        String val =getString(root,keyPath);
        return val==null? defVal:val;
    }

    /**
     * 获取子节点
     *
     * @param parentMapNode 父节点
     * @param childNode     子节点名称
     * @return 子节点Map
     */
    private static Map<String, Object> getChildMap(Map<String, Object> parentMapNode, String childNode) {
        return parentMapNode==null? null:(Map<String, Object>) parentMapNode.get(childNode);
    }

    /**
     * 获取List
     * @param  keyPath 搜索路径
     * @return返回 List，Java默认反序列化为ArrayList
     */
    public static List getList(Map<String,Object> root,String  keyPath)
    {
         return root==null? null:(List) root.get(keyPath);
    }

    /**
     * 获取List
     * @param  keyPath 搜索路径
     * @return返回 List，Java默认反序列化为ArrayList
     */
    public static List getList(Map<String,Object> root,String[] keyPath)
    {
        return root==null? null:getList(getDirectParentMapNode(root, keyPath),keyPath[keyPath.length - 1]);
    }

    /**
     * 给Map赋值
     * @param root
     * @param key
     * @param value
     * @return
     */
    public static boolean setValue(Map<String,Object> root,String key,Object value){
        if( root!= null) {
            root.put(key, value);
        }
        return true;
    }
}

package com.ctrip.infosec.flowtable4j.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/13 0013.
 * 黑白名单校验实体
 */
public class FlowFact {

    /**
     * 订单类型
     */
    private Integer orderType;

    private List<String>  prepayType;

    public Map<String,String> requestCache = new HashMap<String, String>();

    /**
     * 校验内容，字典类型
     */
    private Map<String, Object> content;

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }


    /**
     * 获取直接双亲
     *
     * @param keyPath 搜索路径
     * @return 父节点Map
     */
    private Map<String, Object> getDirectParentMapNode(String... keyPath) {
        Map<String, Object> prevMap = content;
        //Skip leading param
        if (keyPath[0]==null){
            return prevMap;
        }
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
     *
     * @param keyPath 搜索路径
     * @return 字符串
     */
    public String getString(String... keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            if (parentMap.containsKey(key)) {
                Object obj = parentMap.get(key);
                if (obj instanceof String) {
                    return (String) obj;
                } else {
                    return obj.toString();
                }
            }
        }
        return null;
    }

    public Object getObject(String... keyPath) {
        Map<String, Object> parentMap = getDirectParentMapNode(keyPath);
        if (parentMap != null) {
            String key = keyPath[keyPath.length - 1];
            if (parentMap.containsKey(key)) {
                return parentMap.get(key);
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
        }
        return null;
    }

    /**
     * 获取List
     * @param  keyPath 搜索路径
     * @return返回 List，Java默认反序列化为ArrayList
     */
    public List getList(String...keyPath)
    {
        Map<String,Object> parentMap= getDirectParentMapNode(keyPath);
        if(parentMap!=null) {
            String key = keyPath[keyPath.length-1];
            if (parentMap.containsKey(key)) {
                return (List) parentMap.get(key);
            }
        }
        return null;
    }

    public List<String> getPrepayType() {
        return prepayType;
    }

    public void setPrepayType(List<String> prepayType) {
        this.prepayType = prepayType;
    }
}


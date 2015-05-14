package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.sars.util.GlobalConfig;
import com.ctrip.infosec.sars.util.SpringContextHolder;
import com.ctrip.sec.userprofile.contract.venusapi.DataProxyVenusService;
import com.ctrip.sec.userprofile.vo.content.request.DataProxyRequest;
import com.ctrip.sec.userprofile.vo.content.response.DataProxyResponse;
import com.fasterxml.jackson.databind.JavaType;
import com.meidusa.venus.annotations.Param;
import com.meidusa.venus.client.VenusServiceFactory;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.stereotype.Repository;

import javax.annotation.Resources;
import java.util.*;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.configs.utils.Utils.JSON;

/**
 * Created by lpxie on 15-4-8.
 */
@Repository
public class DataProxySources
{
    private static Logger logger = LoggerFactory.getLogger(DataProxySources.class);

    /**
     * 查询一个服务的接口
     * @param serviceName
     * @param operationName
     * @param params
     * @return
     */
    public static Map queryForMap(String serviceName, String operationName, Map<String, Object> params)
    {
        beforeInvoke();
        DataProxyResponse response = null;
        try {
            DataProxyRequest request = new DataProxyRequest();
            request.setServiceName(serviceName);
            request.setOperationName(operationName);
            request.setParams(params);
            List<DataProxyRequest> requests = new ArrayList<DataProxyRequest>();
            requests.add(request);
            DataProxyVenusService dataProxyVenusService = SpringContextHolder.getBean(DataProxyVenusService.class);
            List<DataProxyResponse> responses = dataProxyVenusService.dataproxyQueries(requests);
            if(responses == null || responses.size()<1)
            {
                return new HashMap();
            }
            response = responses.get(0);
            if(serviceName.equals("UserProfileService"))
            {
                if(request.getParams().get("tagName") != null)
                {
                    Map newResult = getNewResult(response.getResult());
                    if(newResult!=null)
                        response.setResult(newResult);
                }else if(request.getParams().get("tagNames") != null)
                {
                    Object[] oldResults = (Object[])response.getResult().get("tagNames");
                    Map newResults = new HashMap();
                    for(int j=0;j<oldResults.length;j++)
                    {
                        Map oneResult = (Map)oldResults[j];
                        Map newResult = getNewResult(oneResult);

                        if(newResult != null && newResult.size()>0)
                            newResults.putAll(newResult);
                    }
                    response.setResult(newResults);
                }
            }
        } catch (Exception ex) {
            fault();
           logger.error("invoke DataProxy.queries fault.", ex);
        } finally {
            afterInvoke("DataProxy.queries");
        }
        return response.getResult();
    }

    /**
     * 批量查询的接口
     * @param requests
     * @return
     */
    public static List<Map> queryForList( List<DataProxyRequest> requests)
    {
        beforeInvoke();
        List<Map> results = new ArrayList<Map>();
        try {
            DataProxyVenusService dataProxyVenusService = SpringContextHolder.getBean(DataProxyVenusService.class);
            List<DataProxyResponse> responses = dataProxyVenusService.dataproxyQueries(requests);
            if(responses == null || responses.size()<1)
            {
                return results;
            }
            for(int i = 0;i<responses.size();i++)
            {
                //这里得到的结果的顺序和请求的顺序是一致的
                DataProxyRequest request = requests.get(i);
                DataProxyResponse response = responses.get(i);
                if(response.getResult() == null)
                {
                    results.add(new HashMap());
                    continue;
                }
                if(request.getServiceName().equals("UserProfileService"))
                {
                    if(request.getParams().get("tagName") != null)
                    {
                        Map newResult = getNewResult(response.getResult());
                        response.setResult(newResult);
                    }else if(request.getParams().get("tagNames") != null)
                    {
                        Object[] oldResults = (Object[])response.getResult().get("tagNames");
                        List<Map> newResults = new ArrayList<Map>();
                        for(int j=0;j<oldResults.length;j++)
                        {
                            Map oneResult = (Map)oldResults[j];
                            newResults.add(getNewResult(oneResult));
                        }
                        Map finalResult = new HashMap();
                        finalResult.put("result",newResults);
                        response.setResult(finalResult);
                    }
                }
                results.add(response.getResult());
            }
        } catch (Exception ex) {
            fault();
           logger.error("invoke DataProxy.queries fault.", ex);
        } finally {
            afterInvoke("DataProxy.queries");
        }
        return results;
    }

    /**
     * 转换数据格式
     * 把从userProfile里面的数据转成Map的格式
     * @param oldValue 原来的值
     * @return
     */
    private static Map getNewResult(Map oldValue)
    {
        Map newResult = new HashMap();
        String tagDataType = oldValue.get("tagDataType") == null ? "" : oldValue.get("tagDataType").toString();
        if(tagDataType.toLowerCase().equals("int") || tagDataType.toLowerCase().equals("string") || tagDataType.toLowerCase().equals("datetime")
                || tagDataType.toLowerCase().equals("boolean"))
        {
            String tagName = oldValue.get("tagName") == null ? "" : oldValue.get("tagName").toString();
            String tagContent = oldValue.get("tagContent") == null ? "" : oldValue.get("tagContent").toString();
            newResult.put(tagName,tagContent);
        }else if(tagDataType.toLowerCase().equals("list"))
        {
            String tagName = oldValue.get("tagName") == null ? "" : oldValue.get("tagName").toString();
            if(oldValue.get("tagContent") == null)
                newResult.put(tagName,"");
            else
            {
                Object[] tagContent = (Object[])oldValue.get("tagContent");
                newResult.put(tagName,tagContent);
            }
        }
        return newResult;
    }
}

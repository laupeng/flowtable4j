//package com.ctrip.infosec.flowtable4j.translate;
//
//import com.ctrip.infosec.flowtable4j.translate.common.MyJSON;
//import com.ctrip.infosec.flowtable4j.translate.dao.DataProxySources;
////'import com.ctrip.infosec.flowtable4j.translate.model.DataProxyRequest;
//import com.ctrip.sec.userprofile.contract.venusapi.DataProxyVenusService;
//import com.ctrip.sec.userprofile.vo.content.request.DataProxyRequest;
//import com.ctrip.sec.userprofile.vo.content.response.DataProxyResponse;
//import com.google.common.collect.ImmutableMap;
//import org.apache.commons.lang3.time.DateUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.text.ParseException;
//import java.util.*;
//
///**
// * Created by lpxie on 15-4-10.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:spring/dataProxy-venus-client-test.xml"})
//public class DataProxySourcesTest
//{
//    MyJSON myJSON = new MyJSON();
//
//    @Test
//    public void testVenusDataProxy()
//    {
//        List<DataProxyRequest> requests = new ArrayList<DataProxyRequest>();
//        DataProxyRequest request;
//        Map params;
//        request = new DataProxyRequest();
//        request.setServiceName("UserProfileService");
//        request.setOperationName("DataQuery");
//        params = new HashMap();
//        params.put("uid", "wwwwww");
//        params.put("tagName", "RECENT_IP");
//        request.setParams(params);
//        requests.add(request);
//        requests.add(request);
//        //List<DataProxyResponse> responses = dataProxyVenusService.dataproxyQueries(requests);
//       Map responses = DataProxySources.queryForMap("UserProfileService", "DataQuery", params);
//    }
//
//    /**
//     * 测试获取一个response
//     */
//    @Test
//    public void testQueryForMap()
//    {
//        /*System.out.println("query");
//        String serviceName = "IpService";
//        String operationName = "getIpArea";
//        Map params = ImmutableMap.of("ip", "202.96.209.133");
//        Map result = DataProxySources.queryForOne(serviceName, operationName, params);
//        System.out.println(myJSON.toPrettyJSONString(result));*/
//
//        /*String serviceName1 = "MobilePhoneService";
//        String operationName1 = "getMobileArea";
//        Map params1 = ImmutableMap.of("mobileNumber", "13917863756");
//         Map result = DataProxySources.queryForOne(serviceName, operationName, params);
//        System.out.println(myJSON.toPrettyJSONString(result));*/
//
//        String serviceName = "CRMService";
//        String operationName = "getMemberInfo";
//        Map params = ImmutableMap.of("uid", "wwwwww");
//         Map result = DataProxySources.queryForMap(serviceName, operationName, params);
//        System.out.println(myJSON.toPrettyJSONString(result));
//
//        /*String serviceName = "AirPortService";
//        String operationName = "getAirPortCity";
//        Map params = ImmutableMap.of("airport", "PEK");
//         Map result = DataProxySources.queryForOne(serviceName, operationName, params);
//        System.out.println(myJSON.toPrettyJSONString(result));*/
//
//        /*String serviceName = "UserProfileService";
//        String operationName = "DataQuery";
//        List tagContents = new ArrayList();
//        tagContents.add("RECENT_IP");
//        tagContents.add("RECENT_IPAREA");
//        Map params = ImmutableMap.of("uid", "M00713231","tagNames",tagContents);
//         Map result = DataProxySources.queryForOne(serviceName, operationName, params);
//        System.out.println(myJSON.toPrettyJSONString(result));;
//        //change data form
//        //数据类型：int string boolean  list  datetime
//        //tagNames的情况
//        List<Map> oldResults = (List<Map>)result1.get("tagNames");
//        List<Map> newResults = new ArrayList<Map>();
//        Iterator iterator = oldResults.iterator();
//        while(iterator.hasNext())
//        {
//            Map oneResult = (Map)iterator.next();
//            newResults.add(getNewResult(oneResult));
//        }
//        Map finalResult = new HashMap();
//        finalResult.put("result",newResults);
//        result.setResult(finalResult);*/
//
//        /*String serviceName = "UserProfileService";
//        String operationName = "DataQuery";
//        //http://userprofile.infosec.ctripcorp.com/userprofileweb/;jsessionid=11099F242AD077BD1F8A53F60FA6E68B
//        Map params = ImmutableMap.of("uid", "M00713231","tagName","RECENT_IP");//STATUS  RECENT_IP
//        DataProxyResponse result = DataProxy.query(serviceName, operationName, params);
//        Map result1 = result.getResult();
//        //change data form
//        //数据类型：int string boolean  list  datetime
//        //tagName的情况
//        Map newResult = getNewResult(result1);
//        System.out.println(newResult.size());*/
//
//        /*Map params = new HashMap();
//        params.put("cardInfoId", "30075005");
//        Map map = CardInfo.query("getinfo", params);
//        System.out.println(map.size());*/
//
//       /* Map result = DataProxySources.queryForOne(serviceName, operationName, params);
//        System.out.println(myJSON.toPrettyJSONString(result));*/
//    }
//
//    @Test
//    public void testQueryForList()
//    {
//        /*DataProxyRequest request1 = new DataProxyRequest();
//        request1.setServiceName("IpService");
//        request1.setOperationName("getIpArea");
//        Map params1 = ImmutableMap.of("ip", "202.96.209.133");
//        request1.setParams(params1);
//
//        DataProxyRequest request2 = new DataProxyRequest();
//        request2.setServiceName("MobilePhoneService");
//        request2.setOperationName("getMobileArea");
//        Map params2 = ImmutableMap.of("mobileNumber", "13917863756");
//        request2.setParams(params2);
//
//        DataProxyRequest request3 = new DataProxyRequest();
//        request3.setServiceName("CRMService");
//        request3.setOperationName("getMemberInfo");
//        Map params3 = ImmutableMap.of("uid", "wwwwww");
//        request3.setParams(params3);
//
//        DataProxyRequest request4 = new DataProxyRequest();
//        request4.setServiceName("AirPortService");
//        request4.setOperationName("getAirPortCity");
//        Map params4 = ImmutableMap.of("airport", "PEK");
//        request4.setParams(params4);*/
//
//        DataProxyRequest request5 = new DataProxyRequest();
//        request5.setServiceName("UserProfileService");
//        request5.setOperationName("DataQuery");
//        List tagContents = new ArrayList();
//        tagContents.add("RECENT_IP");
//        tagContents.add("RECENT_IPAREA");
//        Map params5 = ImmutableMap.of("uid", "M00713231","tagNames",tagContents);
//        request5.setParams(params5);
//
//        /*DataProxyRequest request6 = new DataProxyRequest();
//        request6.setServiceName("UserProfileService");
//        request6.setOperationName("DataQuery");
//        Map params6 = ImmutableMap.of("uid", "M00713231","tagName","RECENT_IP");//STATUS  RECENT_IP
//        request6.setParams(params6);*/
//
//        List<DataProxyRequest> requests = new ArrayList<DataProxyRequest>();
//        /*requests.add(request1);
//        requests.add(request2);
//        requests.add(request3);
//        requests.add(request4);*/
//        requests.add(request5);
//        /*requests.add(request6);*/
//        List<Map> result = DataProxySources.queryForList(requests);
//
//        System.out.println(myJSON.toPrettyJSONString(result));
//    }
//
//    @Test
//    public void testDate()
//    {
//        String orderTime = "2015-4-27 14:41:15";
//
//        try
//        {
//            orderTime = orderTime.replace(" ","T")+".9564416+08:00";
//            Date newTime = DateUtils.parseDate(orderTime, "yyyy-MM-ddTHH:mm:ss");
//            System.out.println(orderTime);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//}

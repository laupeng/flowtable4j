package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.translate.common.BeanMapper;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
/**
 * Created by lpxie on 15-5-7.
 */
@Component
public class CommonExecutor
{
    private Logger logger = LoggerFactory.getLogger(HotelGroupExecutor.class);
    private ThreadPoolExecutor excutor = new ThreadPoolExecutor(10,30, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
    List<Callable<DataFact>> runs = Lists.newArrayList();
    List<Callable<Map>> runsF = Lists.newArrayList();
    @Autowired
    CommonSources commonSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;
    @Autowired
    CommonOperation commonOperation;

    public void complementData(DataFact dataFact,Map data)
    {
        beforeInvoke();
        try{
            logger.info("开始补充"+data.get("OrderID")+"数据");
            runs.clear();
            runsF.clear();

            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            switch (checkType)
            {
                case 0:
                    //补充公共属性信息
                    fillCommonInfo(dataFact,data);
                    //补充paymentInfoList(支付信息)
                    commonOperation.fillPaymentInfo0(dataFact,data);//支付信息（兼容混合支付）  这里是根据CardInfoID来取出相关的信息
                    //补充PaymentMainInfo（预付信息）
                    dataFact.paymentMainInfo.put(Common.BankValidationMethod,getValue(data,Common.BankValidationMethod));
                    dataFact.paymentMainInfo.put(Common.ClientIDOrIP,getValue(data,Common.ClientIDOrIP));
                    dataFact.paymentMainInfo.put(Common.ClientOS,getValue(data,Common.ClientOS));
                    dataFact.paymentMainInfo.put(Common.DeductType,getValue(data,Common.DeductType));
                    dataFact.paymentMainInfo.put(Common.IsPrepaID,getValue(data,Common.IsPrepaID));
                    dataFact.paymentMainInfo.put(Common.PayMethod,getValue(data,Common.PayMethod));
                    dataFact.paymentMainInfo.put(Common.PayValidationMethod,getValue(data,Common.PayValidationMethod));
                    dataFact.paymentMainInfo.put(Common.ValidationFailsReason,getValue(data,Common.ValidationFailsReason));
                    break;
                case 1:
                    //补充公共属性信息
                    fillCommonInfo(dataFact,data);
                    //补充支付信息 从数据库获取支付信息
                    //并发执行
                    final DataFact dataFactCopy = BeanMapper.copy(dataFact, DataFact.class);
                    final String mobilePhone = getValue(data,Common.MobilePhone);
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                long start = System.currentTimeMillis();
                                commonOperation.fillMobilePhone(dataFactCopy,mobilePhone);//补充联系人手机对应的省
                                return dataFactCopy;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                            }
                            return null;
                        }
                    });
                    commonOperation.fillPaymentInfo1(dataFact,data);
                    break;
                case 2:
                    //补充产品
                    String reqIdStr = getValue(data,Common.ReqID);
                    if(!reqIdStr.isEmpty())
                    {
                        Long reqId = Long.parseLong(reqIdStr);
                        commonOperation.fillProductInfo(data,reqId);
                    }
                    //补充支付信息
                    commonOperation.fillPaymentInfo0(dataFact,data);//和checkType = 0的补充支付信息一样
                    break;
                default:
                    break;
            }
            //这里执行并发操作
            //并发执行
            long t1 = System.currentTimeMillis();
            List<DataFact> rawResult = new ArrayList<DataFact>();
            try {
                List<Future<DataFact>> result = excutor.invokeAll(runs, 500, TimeUnit.MILLISECONDS);
                for (Future f : result) {
                    try {
                        if (f.isDone()) {
                            DataFact r = (DataFact) f.get();
                            rawResult.add(r);
                        } else {
                            f.cancel(true);
                        }
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {

            }
            logger.info("第一个线程池的时间是："+(System.currentTimeMillis()-t1));
            if (rawResult.size() > 0){
                for(DataFact item : rawResult)
                {
                    //合并里面的所有信息 注意这里面没有产品信息
                    if(item.paymentInfoList != null &&item.paymentInfoList.size()>0)
                    {
                        dataFact.paymentInfoList.addAll(item.paymentInfoList);//fixme 调试这里
                    }
                    if(item.contactInfo != null &&item.contactInfo.size()>0)
                    {
                        dataFact.contactInfo.putAll(item.contactInfo);
                    }
                    if(item.mainInfo != null &&item.mainInfo.size()>0)
                    {
                        dataFact.mainInfo.putAll(item.mainInfo);
                    }
                    if(item.userInfo != null &&item.userInfo.size()>0)
                    {
                        dataFact.userInfo.putAll(item.userInfo);
                    }
                    if(item.ipInfo != null &&item.ipInfo.size()>0)
                    {
                        dataFact.ipInfo.putAll(item.ipInfo);
                    }
                    if(item.otherInfo != null &&item.otherInfo.size()>0)
                    {
                        dataFact.otherInfo.putAll(item.otherInfo);
                    }
                    if(item.paymentMainInfo != null &&item.paymentMainInfo.size()>0)
                    {
                        dataFact.paymentMainInfo.putAll(item.paymentMainInfo);
                    }
                    if(item.dealInfo != null &&item.dealInfo.size()>0)
                    {
                        dataFact.dealInfo.putAll(item.dealInfo);
                    }
                    if(item.corporationInfo != null &&item.corporationInfo.size()>0)
                    {
                        dataFact.corporationInfo.putAll(item.corporationInfo);
                    }
                    if(item.DIDInfo != null &&item.DIDInfo.size()>0)
                    {
                        dataFact.DIDInfo.putAll(item.DIDInfo);
                    }
                }
            }

            logger.info("补充"+data.get("OrderID")+"数据完毕");
        }catch (Exception exp)
        {
            fault();
            logger.error("invoke complementData.complementData fault.",exp);
        }finally
        {
            afterInvoke("complementData.complementData");
        }
    }

    public void fillCommonInfo(DataFact dataFact,Map data)
    {
        //这里面没有绝对的顺序
        long nowTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lastReqID = -1;
        dataFact.dealInfo.put(Common.LastCheck,"T");
        dataFact.dealInfo.put(Common.CorporationID,"");
        //mainInfo
        dataFact.mainInfo.put(Common.CheckType,getValue(data,Common.CheckType));
        dataFact.mainInfo.put(Common.CorporationID,"");
        try{
            dataFact.mainInfo.put(Common.CreateDate,format.format(new Date(nowTime)));
        }catch (Exception exp)
        {
            logger.warn("fillCommonInfo解析时间格式异常");
        }
        dataFact.mainInfo.put(Common.LastCheck,"T");
        dataFact.mainInfo.put("OrderId",getValue(data,Common.OrderID));
        dataFact.mainInfo.put(Common.MerchantID,getValue(data,Common.MerchantID));
        dataFact.mainInfo.put(Common.SubOrderType,getValue(data,Common.SubOrderType));
        dataFact.mainInfo.put(Common.MerchantOrderID,getValue(data,Common.MerchantOrderID));
        dataFact.mainInfo.put(Common.ClientID,getValue(data,Common.ClientID));
        //得到lastReqId
        lastReqID = commonOperation.getLastReqID(data);
        data.put(Common.ReqID,lastReqID);//这里暂时存储起来 在后面从data里面取出来
        //公共属性的值补充
        //补充mainInfo信息
        dataFact.mainInfo.put(Common.Amount,getValue(data,Common.Amount));
        dataFact.mainInfo.put(Common.OrderDate,getValue(data,Common.OrderDate));
        dataFact.mainInfo.put(Common.IsOnline,getValue(data,Common.IsOnline));
        dataFact.mainInfo.put(Common.Serverfrom,getValue(data,Common.Serverfrom));
        dataFact.mainInfo.put(Common.CorporationID,getValue(data,Common.CorporationID));
        //补充contactInfo
        dataFact.contactInfo.put(Common.MobilePhone,getValue(data,Common.MobilePhone));
        dataFact.contactInfo.put(Common.ContactName,getValue(data,Common.ContactName));
        dataFact.contactInfo.put(Common.ContactTel,getValue(data,Common.ContactTel));
        dataFact.contactInfo.put(Common.ContactEMail,getValue(data,Common.ContactEMail));
        dataFact.contactInfo.put(Common.SendTickerAddr,getValue(data,Common.SendTickerAddr));
//        commonOperation.fillMobilePhone(dataFact,data);//补充联系人手机对应的省市 //fixme 01
        //补充userInfo  fixme 02
        /*String serviceName = "CRMService";
        String operationName = "getMemberInfo";
        String uid = data.get(Common.Uid) == null ? "" : data.get(Common.Uid).toString();
        Map params = ImmutableMap.of("uid", uid);//根据uid取值
        Map crmInfo = DataProxySources.queryForMap(serviceName, operationName, params);
        if(crmInfo !=null && crmInfo.size()>0)
            dataFact.userInfo.putAll(crmInfo);*/
        /*if(!getValue(dataFact.userInfo,"Vip").toUpperCase().equals("T"))//如果UID信息中没有标明是VIP用户，则需要从CustomerInfo中获取//fixme 确认vip是不是每个产品都是这样
        {
            commonOperation.fillUserCusCharacter(dataFact,data);//这里获取用户的用户属性（NEW,REPEAT,VIP） 这里有两个方法：1，直接调用esb，2，调用郁伟新增加的DataProxy //fixme 05
        }*/
        //补充ipInfo
//        commonOperation.fillIpInfo(dataFact, data);//fixme 03
        //补充corporationInfo
        dataFact.corporationInfo.put(Common.CanAccountPay,getValue(data,Common.CanAccountPay));
        dataFact.corporationInfo.put(Common.CompanyType,getValue(data,Common.CompanyType));
        dataFact.corporationInfo.put(Common.Corp_PayType,getValue(data,Common.Corp_PayType));
         //补充DIDInfo
        //commonOperation.getDIDInfo(dataFact, data);//通过订单id和订单类型来获取 fixme 04
        //补充主要支付方式                                           自己添加的以便于后面使用
        commonOperation.fillMainOrderType(data);//这里面加一个字段 “OrderPrepayType”

        //FIXME 这里检查
        //并发执行
        final DataFact dataFactCopy01 = BeanMapper.copy(dataFact, DataFact.class);
        final String mobilePhone = getValue(data,Common.MobilePhone);
        runs.add(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    long start = System.currentTimeMillis();
                    commonOperation.fillMobilePhone(dataFactCopy01,mobilePhone);//补充联系人手机对应的省
                    return dataFactCopy01;
                } catch (Exception e) {
                    logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                }
                return null;
            }
        });

        final DataFact dataFactCopy02 = BeanMapper.copy(dataFact, DataFact.class);
        final String uid = getValue(data,Common.Uid);
        runs.add(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    commonOperation.fillUserInfo(dataFactCopy02,uid);
                    if(!getValue(dataFactCopy02.userInfo,"Vip").toUpperCase().equals("T"))//如果UID信息中没有标明是VIP用户，则需要从CustomerInfo中获取//fixme 确认vip是不是每个产品都是这样
                    {
                        commonOperation.fillUserCusCharacter(dataFactCopy02,uid);//这里获取用户的用户属性（NEW,REPEAT,VIP） 这里有两个方法：1，直接调用esb，2，调用郁伟新增加的DataProxy
                    }
                    return dataFactCopy02;
                } catch (Exception e) {
                    logger.warn("invoke commonOperation fillUserInfo failed.: ", e);
                }
                return null;
            }
        });

        final DataFact dataFactCopy03 = BeanMapper.copy(dataFact, DataFact.class);
        final String userIp = getValue(data,Common.UserIP);
        runs.add(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    long start = System.currentTimeMillis();
                    commonOperation.fillIpInfo(dataFactCopy03, userIp);
                    return dataFactCopy03;
                } catch (Exception e) {
                    logger.warn("invoke commonOperation fillIpInfo failed.: ", e);
                }
                return null;
            }
        });

        final DataFact dataFactCopy04 = BeanMapper.copy(dataFact, DataFact.class);
        final String orderId = getValue(data,Common.OrderID);
        final String orderType = getValue(data,Common.OrderType);
        runs.add(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    long start = System.currentTimeMillis();
                    commonOperation.getDIDInfo(dataFactCopy04, orderId, orderType);
                    return dataFactCopy04;
                } catch (Exception e) {
                    logger.warn("invoke commonOperation getDIDInfo failed.: ", e);
                }
                return null;
            }
        });
    }

    public Map<String,Object> convertToBlackCheckItem(DataFact dataFact,Map data)
    {
        beforeInvoke();
        Map bwList = new HashMap<String,Object>();//定义黑白名单实体
        try{
            logger.info("开始构造"+data.get("OrderID")+"黑白名单数据");
            bwList.put(Common.IPCity, getValue(dataFact.ipInfo,Common.IPCity));
            bwList.put(Common.IPCountry,getValue(dataFact.ipInfo, Common.IPCountry));
            bwList.put(Common.UserIP,getValue(dataFact.ipInfo,Common.UserIPAdd));

            bwList.put(Common.OrderToSignUpDate,getValue(dataFact.otherInfo,Common.OrderToSignUpDate)); //预定距注册日期小时数

            //支付信息处理
            //paymentInfo
            //     PaymentInfoList
            //PaymentInfo(Map) ; CardInfoList(List)
           // if(dataFact.paymentInfoList != null)
            {
                List<Map> paymentInfos = dataFact.paymentInfoList;
                for(Map paymentInfo : paymentInfos)
                {
                    Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                    List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);

                    if((subPaymentInfo.get(Common.PrepayType).toString().toUpperCase().equals("CCARD")||
                            subPaymentInfo.get(Common.PrepayType).toString().toUpperCase().equals("DCARD")) &&
                            cardInfoList.size()>0)
                    {
                        bwList.putAll(cardInfoList.get(0));
                        break;
                    }
                }

                ////黑名单校验临时转换  订单类型(C/W/N/X/P)  当前只判断CCARD，CASH，PAYPL
                String PrepayType = data.get(Common.OrderPrepayType) == null ? "" : data.get(Common.OrderPrepayType).toString();
                for(Map paymentInfo : paymentInfos)
                {
                    Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                    List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                    if(PrepayType.toUpperCase().equals("CCARD"))
                    {
                        if(subPaymentInfo.get(Common.PrepayType).toString().toUpperCase().equals("CCARD"))
                        {
                            if(cardInfoList.get(0).get(Common.IsForeignCard) != null && cardInfoList.get(0).get(Common.IsForeignCard).toString().equals("T"))
                            {
                                bwList.put(Common.PrepayTypeDetails,"W");
                            }else
                            {
                                bwList.put(Common.PrepayTypeDetails,"N");
                            }
                        }
                    }
                }
                if(PrepayType.toUpperCase().equals("CASH"))
                {
                    bwList.put(Common.PrepayTypeDetails,"X");
                }else if(PrepayType.toUpperCase().equals("PAYPL"))
                {
                    bwList.put(Common.PrepayTypeDetails,"P");
                }else if(PrepayType.toUpperCase().equals("DCARD"))
                {
                    bwList.put(Common.PrepayTypeDetails,"D");
                }
            }

            //ContactInfo
            bwList.putAll(dataFact.contactInfo);

            //UserInfo
            bwList.putAll(dataFact.userInfo);

            //mainInfo
            bwList.put(Common.IsOnline, dataFact.mainInfo.get(Common.IsOnline));

            //Country //fixme 这里放空值的意义
            bwList.put(Common.DeviceID,"");
            bwList.put(Common.FuzzyDeviceID,"");
            bwList.put(Common.TrueIP,"");
            bwList.put(Common.TrueIPGeo,"");
            bwList.put(Common.ProxyIP,"");
            bwList.put(Common.ProxyIPGeo,"");

            //did
            bwList.put(Common.DID, dataFact.DIDInfo.get(Common.DID));

            //serverForm
            bwList.put(Common.Serverfrom, dataFact.mainInfo.get(Common.Serverfrom)); //黑m字段

            logger.info("构造"+data.get("OrderID")+"黑白名单数据完毕");
        }catch (Exception exp)
        {
            fault();
            logger.error("invoke CommonExecutor.convertToBlackCheckItem fault.",exp);
        }finally
        {
            afterInvoke("CommonExecutor.convertToBlackCheckItem");
        }
        return bwList;
    }

    public Map<String,Object> convertToFlowRuleCheckItem(DataFact dataFact,Map data)
    {
        beforeInvoke();
        Map<String,Object> flowData = new HashMap();
        try{
            logger.info("开始构造"+data.get("OrderID")+"流量表数据");
            //InfoSecurity_MainInfo
            flowData.putAll(dataFact.mainInfo);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
            String orderDateStr = getValue(dataFact.mainInfo,Common.OrderDate);
            try
            {
                Date orderDate = format.parse(orderDateStr);
                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
                String mergerOrderDate = format2.format(orderDate);
                flowData.put(Common.MergerOrderDate,mergerOrderDate);
                int hours = orderDate.getHours();//fixme 这个方法回头改下
                flowData.put(Common.OrderDateHour,hours);
            } catch (ParseException e)
            {
                logger.warn("转换时间格式为yyyyMMdd异常："+e.getMessage());
            }

            //处理卡面信息
            //InfoSecurity_CardInfo
            //     PaymentInfoList
            //PaymentInfo(Map) ; CardInfoList(List)
            List<Map> paymentInfos = (List<Map>)data.get(Common.PaymentInfoList);
            if(paymentInfos !=null && paymentInfos.size()>0)
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                if((getValue(subPaymentInfo,Common.PrepayType).toUpperCase().equals("CCARD")||
                        getValue(subPaymentInfo,Common.PrepayType).toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    Map cardInfoFirst = cardInfoList.get(0);
                    flowData.putAll(cardInfoFirst);
                    break;
                }
            }

            //InfoSecurity_ContactInfo
            flowData.putAll(dataFact.contactInfo);

            //InfoSecurity_OtherInfo
            flowData.putAll(dataFact.otherInfo);

            //InfoSecurity_UserInfo
            flowData.putAll(dataFact.userInfo);
            //fixme 06
            /*if(dataFact.userInfo.get(Common.BindedMobilePhone) != null && dataFact.userInfo.get(Common.BindedMobilePhone).toString().length()>7)
            {
                Map cityInfo = commonSources.getCityAndProv(dataFact.userInfo.get(Common.BindedMobilePhone).toString());
                if(cityInfo != null)
                {
                    flowData.putAll(cityInfo);
                }
            }
            if(dataFact.userInfo.get(Common.RelatedMobilephone) != null && dataFact.userInfo.get(Common.RelatedMobilephone).toString().length()>7)
            {
                Map cityInfo = commonSources.getCityAndProv(data.get(Common.RelatedMobilephone).toString());
                if(cityInfo != null)
                {
                    flowData.putAll(cityInfo);
                }
            }*/

            //InfoSecurity_IPInfo
            flowData.putAll(dataFact.ipInfo);
            if(!getValue(dataFact.ipInfo,Common.IPCity).isEmpty())
            {
                Map ipCityInfo = commonSources.getCityInfo(getValue(dataFact.ipInfo, Common.IPCity));
                if(ipCityInfo != null && ipCityInfo.size()>0)
                    flowData.putAll(ipCityInfo);
            }

            //DID
            flowData.put(Common.DID, getValue(dataFact.DIDInfo,Common.DID));
            //fixme 07
            /*Map leakInfo = commonSources.getLeakedInfo(getValue(dataFact.userInfo, Common.Uid));
            if(leakInfo != null && leakInfo.size()>0)
            {
                flowData.put(Common.UidActive,leakInfo.get("Active"));
            }*/

            //并发执行
            runs.clear();
            final String bindedMobiePhone = getValue(dataFact.userInfo,Common.BindedMobilePhone);
            final String relatedMobiePhone = getValue(data,Common.RelatedMobilephone);
            final Map flowDataCopy01 = BeanMapper.copy(flowData,Map.class);
            runsF.add(new Callable<Map>() {
                @Override
                public Map call() throws Exception {
                    try {
                        Map cityInfo = commonSources.getCityAndProv(bindedMobiePhone);
                        if(cityInfo != null)
                        {
                            flowDataCopy01.putAll(cityInfo);
                        }
                        Map cityInfo1 = commonSources.getCityAndProv(relatedMobiePhone);
                        if(cityInfo1 != null)
                        {
                            flowDataCopy01.putAll(cityInfo1);
                        }
                        return flowDataCopy01;
                    } catch (Exception e) {
                        logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                    }
                    return null;
                }
            });

            final String uid = getValue(dataFact.userInfo,Common.Uid);
            final Map flowDataCopy02 = BeanMapper.copy(flowData,Map.class);
            runsF.add(new Callable<Map>() {
                @Override
                public Map call() throws Exception {
                    try {
                        long start = System.currentTimeMillis();
                        Map leakInfo = commonSources.getLeakedInfo(uid);
                        if(leakInfo != null && leakInfo.size()>0)
                        {
                            flowDataCopy02.put(Common.UidActive,leakInfo.get("Active"));
                        }
                        return flowDataCopy02;
                    } catch (Exception e) {
                        logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                    }
                    return null;
                }
            });

            final Map flowDataCopy03 = BeanMapper.copy(flowData,Map.class);
            final Map<String,Object> temp = new HashMap();
            temp.put("Uid",getValue(dataFact.userInfo,Common.Uid));
            temp.put("ContactEMail",getValue(dataFact.contactInfo,Common.ContactEMail));
            temp.put("MobilePhone",getValue(dataFact.contactInfo, Common.MobilePhone));
            temp.put("CCardNoCode",getValue(flowDataCopy03,Common.CCardNoCode));
            runsF.add(new Callable<Map>() {
                @Override
                public Map call() throws Exception {
                    try {
                        long toCompute = System.currentTimeMillis();
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                        String nowTimeStr = format1.format(date);

                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        calendar.add(calendar.MINUTE,-720);//往前720分钟
                        String timeLimitStr = format1.format(calendar.getTime());

                        int count = commonSources.getOriginalRisklevel(temp,timeLimitStr,nowTimeStr);
                        flowDataCopy03.put(Common.OriginalRisklevelCount,count);
                        return flowDataCopy03;
                    } catch (Exception e) {
                        logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                    }
                    return null;
                }
            });
            //并发执行
            long t2 = System.currentTimeMillis();
            List<Map> rawResult = new ArrayList<Map>();
            try {
                List<Future<Map>> result = excutor.invokeAll(runsF, 500, TimeUnit.MILLISECONDS);
                for (Future f : result) {
                    try {
                        if (f.isDone()) {
                            Map r = (Map) f.get();
                            rawResult.add(r);
                        } else {
                            f.cancel(true);
                        }
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {

            }
            logger.info("第二个线程池执行的时间是："+(System.currentTimeMillis()-t2));
            if (rawResult.size() > 0){
                for(Map item: rawResult)
                {
                    flowData.putAll(item);//fixme 调试这里的
                }
            }

            //统计分值大于195的数据 fixme 08
            /*long toCompute = System.currentTimeMillis();
            Map<String,Object> temp = new HashMap();
            temp.put("Uid",getValue(dataFact.userInfo,Common.Uid));
            temp.put("ContactEMail",getValue(dataFact.contactInfo,Common.ContactEMail));
            temp.put("MobilePhone",getValue(dataFact.contactInfo, Common.MobilePhone));
            temp.put("CCardNoCode",getValue(flowData,Common.CCardNoCode));

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
            String nowTimeStr = format1.format(date);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.MINUTE,-720);//往前720分钟
            String timeLimitStr = format1.format(calendar.getTime());

            int count = commonSources.getOriginalRisklevel(temp,timeLimitStr,nowTimeStr);
            flowData.put(Common.OriginalRisklevelCount,count);
            logger.info("统计分值大于195的时间是："+(System.currentTimeMillis()-toCompute));*/
            logger.info("构造"+data.get("OrderID")+"流量表数据完毕");
        }catch (Exception exp)
        {
            fault();
            logger.error("invoke CommonExecutor.convertToFlowRuleCheckItem fault.",exp);
        }finally
        {
            afterInvoke("CommonExecutor.convertToFlowRuleCheckItem");
        }
        return flowData;
    }
}

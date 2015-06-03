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
import com.sun.java.swing.plaf.motif.resources.motif;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.CommandMap;
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
//@Component
public class CommonExecutor
{
    private Logger logger = LoggerFactory.getLogger(HotelGroupExecutor.class);
    private ThreadPoolExecutor executor = null;
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

    public void complementData(DataFact dataFact,Map data,ThreadPoolExecutor excutor)
    {
        this.executor = excutor;
        beforeInvoke();
        try{
            logger.info("开始补充"+data.get("OrderID")+"数据");
            runs.clear();
            runsF.clear();
            String mobilePhone = getValue(data,Common.MobilePhone);
            if(mobilePhone.length()>0&&mobilePhone.startsWith("0"))//去掉手机号的第0位的0
            {
                mobilePhone = mobilePhone.substring(1,mobilePhone.length());
                data.put(Common.MobilePhone,mobilePhone);
            }
            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            switch (checkType)
            {
                case 0:
                    //补充公共属性信息
                    fillCommonInfo(dataFact,data);
                    //补充paymentInfoList(支付信息)
                    commonOperation.fillPaymentInfo0(dataFact,data);//支付信息（兼容混合支付）  这里是根据CardInfoID来取出相关的信息
                    //region Description  补充PaymentMainInfo（预付信息）
                    dataFact.paymentMainInfo.put(Common.BankValidationMethod,getValue(data,Common.BankValidationMethod));
                    dataFact.paymentMainInfo.put(Common.ClientIDOrIP,getValue(data,Common.ClientIDOrIP));
                    dataFact.paymentMainInfo.put(Common.ClientOS,getValue(data,Common.ClientOS));
                    dataFact.paymentMainInfo.put(Common.DeductType,getValue(data,Common.DeductType));
                    dataFact.paymentMainInfo.put(Common.IsPrepaID,getValue(data,Common.IsPrepaID));
                    dataFact.paymentMainInfo.put(Common.PayMethod,getValue(data,Common.PayMethod));
                    dataFact.paymentMainInfo.put(Common.PayValidationMethod,getValue(data,Common.PayValidationMethod));
                    dataFact.paymentMainInfo.put(Common.ValidationFailsReason,getValue(data,Common.ValidationFailsReason));
                    //endregion
                    break;
                case 1:
                    //补充公共属性信息
                    fillCommonInfo(dataFact,data);
                    //补充支付信息 从数据库获取支付信息
                    //并发执行
                    final DataFact dataFactCopy01 = new DataFact();
                    commonOperation.getLastReqID(data);
                    final String lastReq = getValue(data,Common.OldReqID);
                    if(lastReq!=null && !lastReq.isEmpty())
                    {
                        runs.add(new Callable<DataFact>() {
                            @Override
                            public DataFact call() throws Exception {
                                try {
                                    commonOperation.fillPaymentInfo1(dataFactCopy01, lastReq);
                                    return dataFactCopy01;
                                } catch (Exception e) {
                                    logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                                }
                                return null;
                            }
                        });
                        final DataFact dataFactCopy02 = new DataFact();
                        runs.add(new Callable<DataFact>() {
                            @Override
                            public DataFact call() throws Exception {
                                try {
                                    commonOperation.fillPaymentMainInfo(dataFactCopy02, lastReq);
                                    return dataFactCopy02;
                                } catch (Exception e) {
                                    logger.warn("invoke commonOperation.fillPaymentMainInfo failed.: ", e);
                                }
                                return null;
                            }
                        });
                    }
                    break;

                case 2:
                    //region Description       补充产品
                    Map mainInfo = commonOperation.getLastReqID(data);
                    if(mainInfo!=null)
                        dataFact.mainInfo.putAll(mainInfo);//这里暂时存储OldReqID 在后面从data里面取出来   添加mainInfo信息

                    dataFact.mainInfo.put(Common.OrderID,getValue(data,Common.OrderID));//添加订单id
                    dataFact.mainInfo.put(Common.CheckType,getValue(data,Common.CheckType));//要改成2
                    final String reqIdStr = getValue(data,Common.OldReqID);
                    final DataFact dataFactCopy001 = new DataFact();
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillProductContact(dataFactCopy001, reqIdStr);
                                return dataFactCopy001;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillProductContact failed.: ", e);
                            }
                            return null;
                        }
                    });

                    final DataFact dataFactCopy_Corpo = new DataFact();
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillCorporationInfo(dataFactCopy_Corpo, reqIdStr);
                                return dataFactCopy_Corpo;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillCorporationInfo failed.: ", e);
                            }
                            return null;
                        }
                    });

                    final DataFact dataFactCopy002 = new DataFact();
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillProductUser(dataFactCopy002, reqIdStr);
                                return dataFactCopy002;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillProductUser failed.: ", e);
                            }
                            return null;
                        }
                    });

                    final DataFact dataFactCopy003 = new DataFact();
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillProductIp(dataFactCopy003, reqIdStr);
                                return dataFactCopy003;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillProductIp failed.: ", e);
                            }
                            return null;
                        }
                    });

                    final DataFact dataFactCopy004 = new DataFact();
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillProductOther(dataFactCopy004, reqIdStr);
                                return dataFactCopy004;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillProductOther failed.: ", e);
                            }
                            return null;
                        }
                    });

                    final DataFact dataFactCopy005 = new DataFact();
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillProductAppInfo(dataFactCopy005, reqIdStr);
                                return dataFactCopy005;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillProductOther failed.: ", e);
                            }
                            return null;
                        }
                    });
                    //endregion

                    //补充支付信息
                    commonOperation.fillPaymentInfo0(dataFact,data);//和checkType = 0的补充支付信息一样

                    dataFact.paymentMainInfo.put(Common.BankValidationMethod,getValue(data,Common.BankValidationMethod));
                    dataFact.paymentMainInfo.put(Common.ClientIDOrIP,getValue(data,Common.ClientIDOrIP));
                    dataFact.paymentMainInfo.put(Common.ClientOS,getValue(data,Common.ClientOS));
                    dataFact.paymentMainInfo.put(Common.DeductType,getValue(data,Common.DeductType));
                    dataFact.paymentMainInfo.put(Common.IsPrepaID,getValue(data,Common.IsPrepaID));
                    dataFact.paymentMainInfo.put(Common.PayMethod,getValue(data,Common.PayMethod));
                    dataFact.paymentMainInfo.put(Common.PayValidationMethod,getValue(data,Common.PayValidationMethod));
                    dataFact.paymentMainInfo.put(Common.ValidationFailsReason,getValue(data,Common.ValidationFailsReason));
                    //paymentMainInfo
                   /* final DataFact dataFactCopy_M = new DataFact();//fixme 当checkType=2的时候不需要重数据库读取信息
                    runs.add(new Callable<DataFact>() {
                        @Override
                        public DataFact call() throws Exception {
                            try {
                                commonOperation.fillPaymentMainInfo(dataFactCopy_M, reqIdStr);
                                return dataFactCopy_M;
                            } catch (Exception e) {
                                logger.warn("invoke commonOperation.fillPaymentMainInfo failed.: ", e);
                            }
                            return null;
                        }
                    });*/
                    break;
                default:
                    break;
            }

            //DID信息
            final DataFact dataFactCopy04 = new DataFact();
            final String orderId = getValue(data,Common.OrderID);
            final String orderType = getValue(data,Common.OrderType);
            runs.add(new Callable<DataFact>() {
                @Override
                public DataFact call() throws Exception {
                    try {
                        commonOperation.getDIDInfo(dataFactCopy04, orderId, orderType);
                        return dataFactCopy04;
                    } catch (Exception e) {
                        logger.warn("invoke commonOperation getDIDInfo failed.: ", e);
                    }
                    return null;
                }
            });
            //这里执行并发操作
            //并发执行
            long t1 = System.currentTimeMillis();
            List<DataFact> rawResult = new ArrayList<DataFact>();
            try {
                List<Future<DataFact>> result = excutor.invokeAll(runs, 2000, TimeUnit.MILLISECONDS);
                for (Future f : result) {
                    try {
                        if (f.isDone()) {
                            DataFact r = (DataFact) f.get();
                            rawResult.add(r);
                        } else {
                            f.cancel(true);
                        }
                    } catch (Exception e) {
                        logger.warn("runs....f.get()执行异常",e);
                    }
                }
            } catch (Exception e) {

            }
            logger.info("第一个线程池的时间是："+(System.currentTimeMillis()-t1));
            if (rawResult!=null && rawResult.size() > 0){
                //region Description          合并里面的所有信息 注意这里面没有产品信息
                for(DataFact item : rawResult)
                {
                    if(item.paymentInfoList != null &&item.paymentInfoList.size()>0)//fixme 调试这里
                    {
                        dataFact.paymentInfoList.addAll(item.paymentInfoList);
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
                    if(item.tempInfo != null &&item.tempInfo.size()>0)
                    {
                        dataFact.tempInfo.putAll(item.tempInfo);
                    }
                }
                //endregion
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
        //long lastReqID = -1;
        dataFact.dealInfo.put(Common.LastCheck,"T");
        dataFact.dealInfo.put(Common.CorporationID,getValue(data,Common.CorporationID));
        dataFact.dealInfo.put(Common.ReferenceNo,getValue(data,Common.ReferenceNo));
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
        dataFact.mainInfo.put(Common.OrderID,getValue(data,Common.OrderID));//OrderId
        dataFact.mainInfo.put(Common.MerchantID,getValue(data,Common.MerchantID));
        dataFact.mainInfo.put(Common.SubOrderType,getValue(data,Common.SubOrderType));
        dataFact.mainInfo.put(Common.MerchantOrderID,getValue(data,Common.MerchantOrderID));
        dataFact.mainInfo.put(Common.ClientID,getValue(data,Common.ClientID));
        //得到lastReqId
       // commonOperation.getLastReqID(data);//这里暂时存储起来 在后面从data里面取出来
        //公共属性的值补充
        //补充mainInfo信息
        dataFact.mainInfo.put(Common.Amount,getValue(data,"OrderAmount"));
        String subOrderType = getValue(data, Common.SubOrderType);
        if(subOrderType.isEmpty())
            subOrderType = "0";
        dataFact.mainInfo.put(Common.SubOrderType,subOrderType);
        dataFact.mainInfo.put(Common.OrderDate,getValue(data,Common.OrderDate));
        dataFact.mainInfo.put(Common.IsOnline,getValue(data,Common.IsOnline));
        dataFact.mainInfo.put(Common.OrderType,getValue(data,Common.OrderType));
        dataFact.mainInfo.put(Common.Serverfrom,getValue(data,Common.Serverfrom));
        dataFact.mainInfo.put(Common.CorporationID,getValue(data,Common.CorporationID));
        //补充contactInfo
        dataFact.contactInfo.put(Common.MobilePhone,getValue(data,Common.MobilePhone));
        dataFact.contactInfo.put(Common.ContactName,getValue(data,Common.ContactName));
        dataFact.contactInfo.put(Common.ContactTel,getValue(data,Common.ContactTel));
        dataFact.contactInfo.put(Common.ContactEMail,getValue(data,Common.ContactEMail));
        dataFact.contactInfo.put(Common.SendTickerAddr,getValue(data,Common.SendTickerAddr));
        //补充corporationInfo
        dataFact.corporationInfo.put(Common.CanAccountPay,getValue(data,Common.CanAccountPay));
        dataFact.corporationInfo.put(Common.CompanyType,getValue(data,Common.CompanyType));
        dataFact.corporationInfo.put(Common.Corp_PayType,getValue(data,Common.Corp_PayType));
        //补充userInfo
        dataFact.userInfo.put(Common.Uid,getValue(data,Common.Uid));
        //补充主要支付方式                                           自己添加的以便于后面使用
        commonOperation.fillMainOrderType(data);//这里面加一个字段 “OrderPrepayType”

        //appInfo
        dataFact.appInfo.put("ClientID",getValue(data,"ClientID"));
        dataFact.appInfo.put("ClientVersion",getValue(data,"ClientVersion"));
        dataFact.appInfo.put("Latitude",getValue(data,"Latitude"));
        dataFact.appInfo.put("Longitude",getValue(data,"Longitude"));
        //FIXME 这里检查
        //并发执行
        final DataFact dataFactCopy01 = new DataFact();
        final String mobilePhone = getValue(data,Common.MobilePhone);
        runs.add(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    commonOperation.fillMobilePhone(dataFactCopy01,mobilePhone);//补充联系人手机对应的省
                    return dataFactCopy01;
                } catch (Exception e) {
                    logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                }
                return null;
            }
        });

        final DataFact dataFactCopy02 = new DataFact();
        final String uid = getValue(data,Common.Uid);
        runs.add(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    commonOperation.fillUserInfo(dataFactCopy02,uid);
                    String vip = getValue(dataFactCopy02.userInfo,"vip");
                    if(!vip.toUpperCase().equals("T"))//如果UID信息中没有标明是VIP用户，则需要从CustomerInfo中获取//fixme 确认vip是不是每个产品都是这样
                    {
                        commonOperation.fillUserCusCharacter(dataFactCopy02, uid,vip);//这里获取用户的用户属性（NEW,REPEAT,VIP） 这里有两个方法：1，直接调用esb，2，调用郁伟新增加的DataProxy
                    }
                    return dataFactCopy02;
                } catch (Exception e) {
                    logger.warn("invoke commonOperation fillUserInfo and fillUserCusCharacter failed.: ", e);
                }
                return null;
            }
        });

        final DataFact dataFactCopy03 = new DataFact();
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


            //region Description   支付信息处理paymentInfo   PaymentInfoList   PaymentInfo(Map) ; CardInfoList(List)
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
                            if(getValue(cardInfoList.get(0),Common.IsForigenCard).equals("T"))
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
            //endregion

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
            bwList.put(Common.ClientID, dataFact.mainInfo.get(Common.ClientID));
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
            //公共属性赋值
            flowData.put("ClientID",getValue(dataFact.mainInfo,"ClientID"));
            flowData.put("OrderType",getValue(dataFact.mainInfo,"OrderType"));
            flowData.put("SubOrderType",getValue(dataFact.mainInfo,"SubOrderType"));
            //InfoSecurity_MainInfo
            flowData.put("OrderId",getValue(dataFact.mainInfo, Common.OrderID));
            flowData.put("Amount",getValue(dataFact.mainInfo,Common.Amount));
            flowData.put("CheckType",getValue(dataFact.mainInfo,Common.CheckType));
            flowData.put("Serverfrom",getValue(dataFact.mainInfo,Common.Serverfrom));
            flowData.put("OrderDate",getValue(dataFact.mainInfo,Common.OrderDate));

            String orderDateStr = getValue(dataFact.mainInfo,Common.OrderDate);
            try
            {
                Date orderDate = DateUtils.parseDate(orderDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.sss");//format.parse(orderDateStr);
                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
                String mergerOrderDate = format2.format(orderDate);
                flowData.put(Common.MergerOrderDate,mergerOrderDate);
                int hours = orderDate.getHours();//fixme 这个方法回头改下
                flowData.put(Common.OrderDateHour,hours);
            } catch (ParseException e)
            {
                logger.warn("转换时间格式为yyyyMMdd异常："+e.getMessage());
            }

            //处理卡面信息(cardInfo)
            //InfoSecurity_CardInfo
            //     PaymentInfoList
            //PaymentInfo(Map) ; CardInfoList(List)
            List<Map> paymentInfos = dataFact.paymentInfoList;
            String  MergerOrderPrepayType = "|";
            if(paymentInfos !=null && paymentInfos.size()>0)
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                MergerOrderPrepayType += subPaymentInfo.get(Common.PrepayType)+"|";
                if((getValue(subPaymentInfo,Common.PrepayType).toUpperCase().equals("CCARD")||
                        getValue(subPaymentInfo,Common.PrepayType).toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    Map cardInfoFirst = cardInfoList.get(0);
                    flowData.put("CCardNoCode",getValue(cardInfoFirst,Common.CCardNoCode));
                    flowData.put("CardNoRefID",getValue(cardInfoFirst,"CardNoRefID"));
                    flowData.put("CValidityCode",getValue(cardInfoFirst,Common.CValidityCode));
                    flowData.put("CreditCardType",getValue(cardInfoFirst,Common.CreditCardType));
                    flowData.put("IsForigenCard",getValue(cardInfoFirst,Common.IsForigenCard));
                    flowData.put("CardBinIssue",getValue(cardInfoFirst,Common.CardBinIssue));
                    flowData.put("CardBin",getValue(cardInfoFirst,Common.CardBin));
                    flowData.put("CardHolder",getValue(cardInfoFirst,Common.CardHolder));
                    break;
                }
            }
            flowData.put("MergerOrderPrepayType",MergerOrderPrepayType);
            //InfoSecurity_ContactInfo
            flowData.put("MobilePhone",getValue(dataFact.contactInfo,Common.MobilePhone));
            flowData.put("MobilePhoneCity",getValue(dataFact.contactInfo,Common.MobilePhoneCity));
            flowData.put("ContactEMail",getValue(dataFact.contactInfo,Common.ContactEMail));
            flowData.put("MobilePhoneProvince",getValue(dataFact.contactInfo,Common.MobilePhoneProvince));

            flowData.put(Common.RelatedMobilePhoneCity,getValue(dataFact.contactInfo,Common.MobilePhoneCity));
            flowData.put(Common.RelatedMobilePhoneProvince,getValue(dataFact.contactInfo,Common.MobilePhoneProvince));

            //InfoSecurity_OtherInfo
            flowData.put("OrderToSignUpDate",getValue(dataFact.otherInfo,Common.OrderToSignUpDate));

            //InfoSecurity_UserInfo
            flowData.put("CusCharacter",getValue(dataFact.userInfo,Common.CusCharacter));
            flowData.put("BindedMobilePhone",getValue(dataFact.userInfo,Common.BindedMobilePhone));
            flowData.put("UserPassword",getValue(dataFact.userInfo,Common.UserPassword));
            flowData.put("Experience",getValue(dataFact.userInfo,Common.Experience));
            flowData.put("BindedEmail",getValue(dataFact.userInfo,Common.BindedEmail));
            flowData.put("Uid",getValue(dataFact.userInfo,Common.Uid));
            flowData.put("VipGrade",getValue(dataFact.userInfo,Common.VipGrade));

            //InfoSecurity_IPInfo
            flowData.put("UserIPAdd",getValue(dataFact.ipInfo,Common.UserIPAdd));
            flowData.put("UserIPValue",getValue(dataFact.ipInfo,Common.UserIPValue));
            flowData.put("IPCity",getValue(dataFact.ipInfo,Common.IPCity));
            flowData.put("IPCountry",getValue(dataFact.ipInfo,Common.IPCountry));
            if(!getValue(dataFact.ipInfo,Common.IPCity).isEmpty())
            {
                Map ipCityInfo = commonSources.getCityInfo(getValue(dataFact.ipInfo, Common.IPCity));
                if(ipCityInfo != null && ipCityInfo.size()>0)
                {
                    flowData.put("IPCityName",getValue(ipCityInfo,"CityName"));
                    flowData.put("IPProvince",getValue(ipCityInfo,"ProvinceName"));
                }
            }

            //DID
            flowData.put(Common.DID, getValue(dataFact.DIDInfo,Common.DID));


            //并发执行
            final String bindedMobiePhone = getValue(dataFact.userInfo,Common.BindedMobilePhone);
            final Map flowDataCopy01 = BeanMapper.copy(flowData,Map.class);
            runsF.add(new Callable<Map>() {
                @Override
                public Map call() throws Exception {
                    try {
                        Map cityInfo = commonSources.getCityAndProv(bindedMobiePhone);
                        if(cityInfo != null)
                        {
                            flowDataCopy01.put(Common.RelatedMobilePhoneCity,getValue(cityInfo,Common.BindedMobilePhoneCity));
                            flowDataCopy01.put(Common.RelatedMobilePhoneProvince,getValue(cityInfo,Common.BindedMobilePhoneProvince));
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

            //region Description    这里面是计算一些属性的风险值 这个操作比较耗时，所以改进执行方式
            final Map flowDataCopy03 = new HashMap();
            final String ccardNoCode = getValue(flowData,Common.CCardNoCode);
            final String cardNoRefID = getValue(flowData,"CardNoRefID");
            final Map<String,Object> temp = new HashMap();
            final String orderType = getValue(data,Common.OrderType);
            temp.put("Uid",getValue(dataFact.userInfo,Common.Uid));
            temp.put("ContactEMail",getValue(dataFact.contactInfo,Common.ContactEMail));
            temp.put("MobilePhone",getValue(dataFact.contactInfo, Common.MobilePhone));
            temp.put("CCardNoCode",ccardNoCode);
            temp.put("cardNoRefID",cardNoRefID);
            runsF.add(new Callable<Map>() {
                @Override
                public Map call() throws Exception {
                    try {
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                        String nowTimeStr = format1.format(date);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        calendar.add(calendar.MINUTE, -720);//往前720分钟
                        String timeLimitStr = format1.format(calendar.getTime());
                        int count = commonSources.getOriginalRisklevel(temp,timeLimitStr,nowTimeStr,orderType);
                        flowDataCopy03.put(Common.OriginalRisklevelCount,count);
                        return flowDataCopy03;
                    } catch (Exception e) {
                        logger.warn("invoke commonOperation fillMobilePhone failed.: ", e);
                    }
                    return null;
                }
            });
            //endregion

            //并发执行
            long t2 = System.currentTimeMillis();
            List<Map> rawResult = new ArrayList<Map>();
            try {
                List<Future<Map>> result = executor.invokeAll(runsF, 1000, TimeUnit.MILLISECONDS);
                for (Future f : result) {
                    try {
                        if (f.isDone()) {
                            Map r = (Map) f.get();
                            rawResult.add(r);
                        } else {
                            f.cancel(true);
                        }
                    } catch (Exception e) {
                        logger.warn("runsF...f.get()执行异常",e);
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

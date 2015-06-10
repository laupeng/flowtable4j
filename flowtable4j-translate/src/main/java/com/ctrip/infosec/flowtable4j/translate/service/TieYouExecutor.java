package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.common.BeanMapper;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.ctrip.infosec.flowtable4j.translate.model.TieYou;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.Json;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValueMap;

/**
 * Created by lpxie on 15-5-8.
 */
@Component
public class TieYouExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(TieYouExecutor.class);
    @Autowired
    CommonSources commonSources;
    @Autowired
    CommonWriteSources commonWriteSources;
    @Autowired
    TieYouSources tieYouSources;
    @Autowired
    TieYouWriteSources tieYouWriteSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;
    @Autowired
    CommonExecutor commonExecutor;
    @Autowired
    CommonOperation commonOperation;

    @Override
    public void complementData(DataFact dataFact, Map data, ThreadPoolExecutor executor)
    {
        try{
            logger.info("开始处理铁友 "+data.get("OrderID").toString()+" 数据");
            //一：补充数据
            commonExecutor.complementData(dataFact,data,executor);
            dataFact.mainInfo.put(Common.OrderType,18);//添加订单类型 铁友是18
            getOtherInfo(dataFact, data);
            //这里分checkType 0、1和2两种情况
            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            if(checkType == 0 ||checkType == 1)
            {
                getTieYouProductInfo0(dataFact, data);
            }else if(checkType == 2)
            {
                getTieYouProductInfo1(dataFact, data);
            }
        }catch (Exception exp)
        {
            logger.warn(data.get("OrderID").toString()+"的铁又数据处理异常"+exp.getMessage());
        }
    }

    @Override
    public void convertToBlackCheckItem(DataFact dataFact, Map data, Map bwList)
    {
        //二：黑白名单数据
        bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
        String TieYouPassengerName = "";
        String TieYouPassengerCardIDType = "";
        String TieYouPassengerCardID = "";
        if(dataFact.productInfoL != null && dataFact.productInfoL.size()>0)
        {
            bwList.put(Common.DCity,getValue(getValueMap(dataFact.productInfoL.get(0), TieYou.ExRailInfo),Common.DCity));
            bwList.put(Common.ACity,getValue(getValueMap(dataFact.productInfoL.get(0), TieYou.ExRailInfo),Common.ACity));
            for(Map tempInfo : dataFact.productInfoL)
            {
                Map ExRailUserInfo = getValueMap(tempInfo,TieYou.ExRailUserInfo);
                TieYouPassengerName += getValue(ExRailUserInfo,TieYou.PassengerName)+"|";
                TieYouPassengerCardIDType += getValue(ExRailUserInfo,TieYou.PassengerIDType)+"|";
                TieYouPassengerCardID += getValue(ExRailUserInfo,TieYou.PassengerIDCode)+"|";
            }
        }
        bwList.put("TieYouPassengerName",TieYouPassengerName);
        bwList.put("TieYouPassengerCardIDType",TieYouPassengerCardIDType);
        bwList.put("TieYouPassengerCardID",TieYouPassengerCardID);
    }

    @Override
    public void convertToFlowRuleCheckItem(DataFact dataFact, Map data, Map flowData)
    {
        flowData = commonExecutor.convertToFlowRuleCheckItem(dataFact,data);
        //支付衍生字段
        List<Map> paymentInfos = dataFact.paymentInfoList;
        for(Map paymentInfo : paymentInfos)
        {
            Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
            List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
            if((getValue(subPaymentInfo,Common.PrepayType).toUpperCase().equals("CCARD")||
                    getValue(subPaymentInfo,Common.PrepayType).toUpperCase().equals("DCARD")) &&
                    cardInfoList.size()>0)
            {
                Map cardInfoFirst = cardInfoList.get(0);
                flowData.put(TieYou.OrderIdCardBin,getValue(dataFact.mainInfo,Common.OrderID)+getValue(cardInfoFirst,Common.CardBin));
                flowData.put(TieYou.CardBinMobilePhone,getValue(dataFact.contactInfo,Common.MobilePhone)+getValue(cardInfoFirst,Common.CardBin));
                flowData.put(TieYou.CardBinUserIPAdd,getValue(dataFact.ipInfo,Common.UserIPAdd)+getValue(cardInfoFirst,Common.CardBin));
                break;
            }
        }
        //产品信息加到流量实体
        String MergePassengerIDType = "";
        List<Map> PassengerList = new ArrayList<Map>();
        if(dataFact.productInfoL != null && dataFact.productInfoL.size()>0)
        {
            flowData.put(Common.DCity,getValue(getValueMap(dataFact.productInfoL.get(0), TieYou.ExRailInfo),Common.DCity));
            flowData.put(Common.ACity,getValue(getValueMap(dataFact.productInfoL.get(0), TieYou.ExRailInfo),Common.ACity));

            String departureDateStr = getValue((Map)dataFact.productInfoL.get(0).get(TieYou.ExRailInfo),TieYou.DepartureDate);
            String orderDateStr = getValue(dataFact.mainInfo,Common.OrderDate);
            try{
                Date departureDate = DateUtils.parseDate(departureDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
                Date orderDate = DateUtils.parseDate(orderDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");
                flowData.put("OrderToDepartureDate",getDateAbs(departureDate, orderDate,1));
            }catch (Exception exp)
            {
                logger.warn("解析时间格式出错:departureDateStr,orderDateStr "+departureDateStr+"\t"+orderDateStr,exp);
            }

            for(Map tempInfo : dataFact.productInfoL)
            {
                Map ExRailUserInfo = getValueMap(tempInfo,TieYou.ExRailUserInfo);
                if(ExRailUserInfo == null)
                    continue;
                Map<String,Object> tempPassenger = new HashMap();
                tempPassenger.put(TieYou.PassengerIDCode,getValue(ExRailUserInfo,TieYou.PassengerIDCode));
                PassengerList.add(tempPassenger);
                MergePassengerIDType = MergePassengerIDType + getValue(ExRailUserInfo,TieYou.PassengerIDCode)+"|";
            }
            flowData.put("MergePassengerIDType",MergePassengerIDType.substring(0,MergePassengerIDType.length()-1));
        }
    }

    @Override
    public void writeData(DataFact dataFact, Map data, Map flowData, ThreadPoolExecutor writeExecutor,final boolean isWrite,final boolean isCheck)
    {
        flowData.put(Common.OrderType,data.get(Common.OrderType));
        logger.info("mainInfo\t"+ Json.toPrettyJSONString(dataFact.mainInfo));
        logger.info("contactInfo\t"+ Json.toPrettyJSONString(dataFact.contactInfo));
        logger.info("userInfo\t"+ Json.toPrettyJSONString(dataFact.userInfo));
        logger.info("ipInfo\t"+ Json.toPrettyJSONString(dataFact.ipInfo));
        logger.info("corporationInfo\t"+ Json.toPrettyJSONString(dataFact.corporationInfo));
        if(dataFact.productInfoL != null && dataFact.productInfoL.size()>0)//铁友产品信息是list
            for (int i = 0; i < dataFact.productInfoL.size(); i++)
            {
                Map<String, Object> tieYouInfo = dataFact.productInfoL.get(i);
                Map exRailInfo = getValueMap(tieYouInfo, TieYou.ExRailInfo);
                logger.info(i + "\texRailInfo\t" + Json.toPrettyJSONString(exRailInfo));
                Map exRailUserInfo = getValueMap(tieYouInfo, TieYou.ExRailUserInfo);
                logger.info(i + "\texRailUserInfo\t" + Json.toPrettyJSONString(exRailUserInfo));
            }
        logger.info("otherInfo\t"+ Json.toPrettyJSONString(dataFact.otherInfo));
        logger.info("DIDInfo\t"+ Json.toPrettyJSONString(dataFact.DIDInfo));

        if(dataFact.paymentInfoList != null && dataFact.paymentInfoList.size()>0)
            for(int i=0;i<dataFact.paymentInfoList.size();i++)
            {
                Map<String,Object> paymentInfo = dataFact.paymentInfoList.get(i);
                logger.info(i + "\tpaymentInfo\t" + Json.toPrettyJSONString(paymentInfo.get("PaymentInfo")));
                List<Map<String,Object>> cardInfos = (List<Map<String,Object>>)paymentInfo.get(Common.CardInfoList);
                for(int j=0;j<cardInfos.size();j++)
                {
                    logger.info(i + "\t" + j + "\tcardInfo\t" + Json.toPrettyJSONString(cardInfos.get(j)));
                }
            }
        logger.info("paymentMainInfo\t"+ Json.toPrettyJSONString(dataFact.paymentMainInfo));


        logger.info(getValue(dataFact.mainInfo,Common.OrderID)+"开始写预处理数据和流量表数据到数据库");
        //下面输出当前订单的预处理数据到日志 给测试用 方便他们做对比
        final String reqId = getValue(data,Common.ReqID);
        flowData.put(Common.ReqID,reqId);

        final DataFact dataFactCopy = BeanMapper.copy(dataFact, DataFact.class);
        writeExecutor.submit(new Callable<DataFact>() {
            @Override
            public DataFact call() throws Exception {
                try {
                    commonWriteSources.insertMainInfo(dataFactCopy.mainInfo,reqId,isWrite,isCheck);
                } catch (Exception e) {
                    logger.warn("invoke commonWriteSources.insertMainInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertContactInfo(dataFactCopy.contactInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertContactInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertUserInfo(dataFactCopy.userInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertContactInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertIpInfo(dataFactCopy.ipInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertIpInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertCorporationInfo(dataFactCopy.corporationInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.corporationInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    for (int i = 0; i < dataFactCopy.productInfoL.size(); i++)
                    {
                        Map<String, Object> tieYouInfo = dataFactCopy.productInfoL.get(i);
                        String ExRailInfoID = "";
                        Map exRailInfo = getValueMap(tieYouInfo, TieYou.ExRailInfo);
                        if(exRailInfo.size()>0)
                        {ExRailInfoID = tieYouWriteSources.insertTieYouExRailInfo(exRailInfo,reqId, isWrite, isCheck);}

                        Map exRailUserInfo = getValueMap(tieYouInfo, TieYou.ExRailUserInfo);
                        exRailUserInfo.put("ExRailInfoID",ExRailInfoID);
                        if(exRailUserInfo.size()>0 && !ExRailInfoID.isEmpty())
                        {tieYouWriteSources.insertTieYouExRailInfo(exRailUserInfo, reqId, isWrite, isCheck);}
                    }
                } catch (Exception e)
                {
                    logger.warn("invoke  tieYouWriteSources.insertTieInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertOtherInfo(dataFactCopy.otherInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertOtherInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertDeviceIDInfo(dataFactCopy.DIDInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertDeviceIDInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    commonWriteSources.insertPaymentMainInfo(dataFactCopy.paymentMainInfo, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertIpInfo failed.: ", e);
                }
                return null;
            }
        });

        writeExecutor.submit(new Callable<DataFact>()
        {
            @Override
            public DataFact call() throws Exception
            {
                try
                {
                    for (int i = 0; i < dataFactCopy.paymentInfoList.size(); i++)
                    {
                        Map<String, Object> paymentInfo = dataFactCopy.paymentInfoList.get(i);
                        final String paymentInfoID = commonWriteSources.insertPaymentInfo(getValueMap(paymentInfo, Common.PaymentInfo), reqId, isWrite, isCheck);
                        List<Map<String, Object>> cardInfos = (List<Map<String, Object>>) paymentInfo.get(Common.CardInfoList);
                        for (int j = 0; j < cardInfos.size(); j++)
                        {
                            commonWriteSources.insertCardInfo(cardInfos.get(j), reqId, paymentInfoID, isWrite, isCheck);
                        }
                    }
                } catch (Exception e)
                {
                    logger.warn("invoke  commonWriteSources.insertCardInfo failed.: ", e);
                }
                return null;
            }
        });

        //流量数据
        final Map flowDataCopy = BeanMapper.copy(flowData,Map.class);
        commonOperation.writeFlowData(flowDataCopy,writeExecutor,isWrite,isCheck);
    }

    public CheckFact executeTieYou(Map data,ThreadPoolExecutor executor,ThreadPoolExecutor writeExecutor,boolean isWrite,boolean isCheck)
    {
        beforeInvoke();
        CheckFact checkFact = new CheckFact();
        try{
            logger.info("开始处理铁又 "+data.get("OrderID").toString()+" 数据");

            DataFact dataFact = new DataFact();
            complementData(dataFact,data,executor);

            Map<String,Object> bwList = new HashMap();
            convertToBlackCheckItem(dataFact,data,bwList);

            Map<String,Object> flowData = new HashMap<String, Object>();
            convertToFlowRuleCheckItem(dataFact,data,flowData);

            logger.info(data.get("OrderID").toString()+" 数据处理完毕");
            //构造规则引擎的数据类型CheckFact
            CheckType[] checkTypes = {CheckType.BW,CheckType.FLOWRULE};

            BWFact bwFact = new BWFact();
            bwFact.setOrderType(Integer.parseInt(data.get(Common.OrderType).toString()));
            bwFact.setContent(bwList);

            FlowFact flowFact = new FlowFact();
            flowFact.setContent(flowData);
            flowFact.setOrderType(Integer.parseInt(data.get(Common.OrderType).toString()));

            checkFact.setBwFact(bwFact);
            checkFact.setFlowFact(flowFact);
            checkFact.setCheckTypes(checkTypes);
            if(data.get(Common.ReqID)!=null)
                checkFact.setReqId(Long.parseLong(data.get(Common.ReqID).toString()));//reqId如何获取
            logger.info(data.get("OrderID").toString()+" 数据处理完毕");

            //预处理数据写到数据库
            //判断是否写入数据
            // boolean isWrite = commonSources.getIsWrite("TieYouPreByNewSystem");//fixme 这个在上线后把注释去掉
            logger.info("流量表数据\t"+ Json.toPrettyJSONString(flowData));
            writeData(dataFact,data,flowData,writeExecutor,isWrite,isCheck);
        }catch (Exception exp)
        {
            fault();
            logger.error("invoke TieYouExecutor.executeHotelGroup fault.",exp);
        }finally
        {
            afterInvoke("TieYouExecutor.executeHotelGroup");
        }
        return checkFact;
    }

    /**
     * 添加订单日期到注册日期的差值
     * 添加订单日期到起飞日期的差值
     * @param data
     * @throws java.text.ParseException
     */
    public void getOtherInfo(DataFact dataFact,Map data) throws ParseException
    {
        logger.info(data.get("OrderID")+"获取时间的差值相关信息");
        try{
            //订单日期
            String orderDateStr = getValue(data,Common.OrderDate);
            Date orderDate = DateUtils.parseDate(orderDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
            //注册日期
            String signUpDateStr = getValue(data,Common.SignUpDate);
            Date signUpDate = DateUtils.parseDate(signUpDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
            dataFact.otherInfo.put(Common.OrderToSignUpDate,getDateAbs(signUpDate, orderDate,1));
        }catch (Exception exp)
        {
            logger.warn(data.get("OrderID")+"获取时间的差值相关信息异常"+exp.getMessage());
        }
    }

    /**
     * 获取铁友产品信息当checkType是0或1的时候
     * @param dataFact
     * @param data
     */
    public void getTieYouProductInfo0(DataFact dataFact,Map data)
    {
        List<Map> tieYouOrderInfos = data.get(TieYou.TieYouOrderInfos) == null ? new ArrayList<Map>() : (List<Map>)data.get(TieYou.TieYouOrderInfos);
        for(Map tempInfo : tieYouOrderInfos)
        {
            Map<String,Object> subProductInfo = new HashMap<String, Object>();
            Map<String,Object> exRailInfo = new HashMap<String, Object>();
            Map<String,Object> exRailUserInfo = new HashMap<String, Object>();

            exRailInfo.put(TieYou.Acity,getValue(tempInfo, TieYou.Acity));
            exRailInfo.put(TieYou.Dcity,getValue(tempInfo, TieYou.Dcity));
            exRailInfo.put(TieYou.DepartureDate,getValue(tempInfo, TieYou.DepartureDate));
            exRailInfo.put(TieYou.SeatClass,getValue(tempInfo, TieYou.SeatClass));
            exRailInfo.put(TieYou.TrainNo,getValue(tempInfo, TieYou.TrainNo));
            exRailInfo.put(TieYou.FromStationName,getValue(tempInfo, TieYou.FromStationName));

            exRailUserInfo.put(TieYou.PassengerIDCode,getValue(tempInfo, TieYou.PassengerIDCode));
            exRailUserInfo.put(TieYou.PassengerIDType,getValue(tempInfo, TieYou.PassengerIDType));
            exRailUserInfo.put(TieYou.PassengerName,getValue(tempInfo, TieYou.PassengerName));
            exRailUserInfo.put(TieYou.InsuranceType,getValue(tempInfo, TieYou.InsuranceType));

            subProductInfo.put(TieYou.ExRailInfo,exRailInfo);
            subProductInfo.put(TieYou.ExRailUserInfo,exRailUserInfo);
            dataFact.productInfoL.add(subProductInfo);
        }
    }

    /**
     * 获取铁友产品信息当checkType是2的时候
     * @param dataFact
     * @param data
     */
    public void getTieYouProductInfo1(DataFact dataFact,Map data)
    {
        //通过lastReqID查询所有订单相关的信息
        String lastReqID = getValue(data,Common.ReqID);
        List<Map<String,Object>> tieYouOrderInfos = commonSources.getProductInfo(lastReqID);
        if(tieYouOrderInfos == null || tieYouOrderInfos.isEmpty())
            return;
        for(Map tempInfo : tieYouOrderInfos)
        {
            Map<String,Object> exRailInfo = tempInfo;
            String exRailInfoID = getValue(tempInfo,"ExRailInfoID");
            Map<String,Object> exRailUserInfo = tieYouSources.getExRailUserInfo(exRailInfoID);
            Map<String,Object> subProductInfo = new HashMap<String, Object>();

            subProductInfo.put(TieYou.ExRailInfo,exRailInfo);
            subProductInfo.put(TieYou.ExRailUserInfo,exRailUserInfo);
            dataFact.productInfoL.add(subProductInfo);
        }
    }
}

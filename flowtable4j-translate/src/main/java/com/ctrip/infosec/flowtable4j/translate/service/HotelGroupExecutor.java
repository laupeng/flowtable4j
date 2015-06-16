package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.UnitConverters.HotelGroupConvert;
import com.ctrip.infosec.flowtable4j.translate.UnitConverters.OtherInfoConvert;
import com.ctrip.infosec.flowtable4j.translate.common.BeanMapper;
import com.ctrip.infosec.flowtable4j.translate.common.MyJSON;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateUtils;
import org.omg.CORBA.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.OrientationRequested;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.Json;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValueMap;

/**
 * Created by lpxie on 15-4-20.
 */
@Component
public class HotelGroupExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(HotelGroupExecutor.class);
    @Autowired
    HotelGroupSources hotelGroupSources;
    @Autowired
    HotelGroupWriteSources hotelGroupWriteSources;
    @Autowired
    CommonSources commonSources;
    @Autowired
    CommonWriteSources commonWriteSources;
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
    @Autowired
    HotelGroupConvert hotelGroupConvert;

    @Override
    public void complementData(DataFact dataFact, Map data,ThreadPoolExecutor executor)
    {
        beforeInvoke();
        try{
            logger.info("开始处理酒店团购 "+data.get("OrderID").toString()+" 数据");
            //一：补充数据
            long now = System.currentTimeMillis();
            commonExecutor.complementData(dataFact,data,executor);
            logger.info("complementData公共数据补充的时间是:"+(System.currentTimeMillis()-now));
            //产品
            hotelGroupConvert.completeData(dataFact,data);
            //这里分checkType 0、1和2两种情况
            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            if(checkType == 0 || checkType ==1)
            {
                getOtherInfo0(dataFact, data);
            }
            else if(checkType == 2)
            {
                getOtherInfo1(dataFact, data);
            }
            logger.info("一：公共补充数据的时间是:"+(System.currentTimeMillis()-now));
        }catch (Exception exp)
        {
            logger.warn("补充酒店团购数据异常"+exp.getMessage());
        }
    }

    @Override
    public void convertToBlackCheckItem(DataFact dataFact, Map data,Map bwList)
    {
        try
        {
            //二：黑白名单数据
            long now = System.currentTimeMillis();
            commonExecutor.convertToBlackCheckItem(dataFact,data,bwList);
            bwList.putAll(dataFact.productInfoM);
            logger.info("补充黑白名单数据的时间是："+(System.currentTimeMillis()-now));
        }catch (Exception exp)
        {
            logger.warn("补充酒店黑白名单异常"+exp.getMessage());
        }
    }

    @Override
    public void convertToFlowRuleCheckItem(DataFact dataFact, Map data,Map flowData)
    {
        try{
            //三：流量实体数据
            long now = System.currentTimeMillis();
            commonExecutor.convertToFlowRuleCheckItem(dataFact,data,flowData);
            logger.info("通用流量实体执行时间:"+(System.currentTimeMillis()-now));
            hotelGroupConvert.convertToFlowRuleCheckItem(dataFact,data,flowData);
        }catch (Exception exp)
        {
            logger.warn("转换酒店团购流量实体数据异常"+exp.getMessage());
        }
    }

    @Override
    public void writeData(DataFact dataFact, Map data,Map flowData,ThreadPoolExecutor writeExecutor,final boolean isWrite,final boolean isCheck)
    {
        //预处理数据写到数据库
        flowData.put(Common.OrderType,data.get(Common.OrderType));
        logger.info("mainInfo\t"+ Json.toPrettyJSONString(dataFact.mainInfo));
        logger.info("contactInfo\t"+ Json.toPrettyJSONString(dataFact.contactInfo));
        logger.info("userInfo\t"+ Json.toPrettyJSONString(dataFact.userInfo));
        logger.info("ipInfo\t"+ Json.toPrettyJSONString(dataFact.ipInfo));
        logger.info("hotelGroupInfo\t"+ Json.toPrettyJSONString(dataFact.productInfoM));
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

        final DataFact dataFactCopy = BeanMapper.copy(dataFact,DataFact.class);
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
                    hotelGroupWriteSources.insertHotelGroupInfo(dataFactCopy.productInfoM, reqId, isWrite, isCheck);
                } catch (Exception e)
                {
                    logger.warn("invoke commonWriteSources.insertHotelGroupInfo failed.: ", e);
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


    public CheckFact executeHotelGroup(Map data,ThreadPoolExecutor executor,ThreadPoolExecutor writeExecutor,boolean isWrite,boolean isCheck)
    {
        CheckFact checkFact = new CheckFact();
        beforeInvoke();
        try{
            logger.info("开始处理酒店团购 "+data.get("OrderID").toString()+" 数据");

            DataFact dataFact = new DataFact();
            complementData(dataFact,data,executor);

            Map<String,Object> bwList = new HashMap<String, Object>();;
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
            if(data.get(HotelGroup.ReqID)!=null)
                checkFact.setReqId(Long.parseLong(data.get(Common.ReqID).toString()));//reqId如何获取

            //判断是否写入数据
            // boolean isWrite = commonSources.getIsWrite("HotelGroupPreByNewSystem");//fixme 这个在上线后把注释去掉
            writeData(dataFact,data,flowData,writeExecutor,isWrite,isCheck);//fixme 第一次测试先不写数据库
        }catch (Exception exp)
        {
            fault();
            logger.error("invoke HotelGroupExecutor.executeHotelGroup fault.",exp);
        }finally
        {
            afterInvoke("HotelGroupExecutor.executeHotelGroup");
        }
        return checkFact;
    }

    /**
     * 添加订单日期到注册日期的差值
     * 添加订单日期到起飞日期的差值
     * @param data
     * @throws java.text.ParseException
     */
    public void getOtherInfo0(DataFact dataFact,Map data) throws ParseException
    {
        logger.info(data.get("OrderID")+"获取时间的差值相关信息");
        //订单日期
        String orderDateStr = getValue(data,Common.OrderDate);
        Date orderDate = DateUtils.parseDate(orderDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
        //注册日期
        String signUpDateStr = getValue(data,Common.SignUpDate);
        Date signUpDate = DateUtils.parseDate(signUpDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        dataFact.otherInfo.put(Common.OrderToSignUpDate,getDateAbs(signUpDate, orderDate,1));

        dataFact.otherInfo.put(Common.TakeOffToOrderDate,"0");
    }

    public void getOtherInfo1(DataFact dataFact,Map data)
    {
        String reqIdStr = getValue(data,Common.ReqID);
        Map otherInfo = commonSources.getOtherInfo(reqIdStr);
        if(otherInfo != null && otherInfo.size()>0)
            dataFact.otherInfo.putAll(otherInfo);
    }
}

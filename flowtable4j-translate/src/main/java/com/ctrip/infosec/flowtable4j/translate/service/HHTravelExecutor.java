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
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
 * Created by lpxie on 15-6-9.
 */
public class HHTravelExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(HotelGroupExecutor.class);

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

    @Override
    public void complementData(DataFact dataFact, Map data, ThreadPoolExecutor executor)
    {
        beforeInvoke();
        try{
            logger.info("开始处理酒店团购 "+data.get("OrderID").toString()+" 数据");
            //一：补充数据
            long now5 = System.currentTimeMillis();
            data.put(Common.CheckType,"0");
            commonExecutor.complementData(dataFact, data, executor);
            logger.info("complementData公共补充数据的时间是:" + (System.currentTimeMillis() - now5));
            getOtherInfo0(dataFact, data);
            //产品信息添加
            getVacationProductInfo(dataFact,data);
            logger.info("一：公共补充数据的时间是:"+(System.currentTimeMillis()-now5));
        }catch (Exception exp)
        {
            logger.warn(data.get("OrderID").toString()+" 鸿鹄旅游数据补充异常"+exp.getMessage());
        }
    }

    @Override
    public void convertToBlackCheckItem(DataFact dataFact, Map data, Map bwList)
    {
        long now = System.currentTimeMillis();
        bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
        //产品信息
        List<Map<String,Object>> vacationUserInfo = (List<Map<String,Object>>)data.get("VacationUserInfo");
        if(vacationUserInfo != null && vacationUserInfo.size()>0)
        {
            String PassengerName = "";
            String PassengerNationality = "";
            String PassengerCardID = "";
            String VisitorContactInfo = "";
            for(Map item : vacationUserInfo)
            {
                PassengerName += getValue(item,"VisitorName") +"|"; //	出行人姓名(精确)
                PassengerNationality += getValue(item,"VisitorNationality") +"|";   //	出行人国籍
                PassengerCardID += getValue(item,"VisitorIDCode") +"|";//	出行人证件号码
                VisitorContactInfo += getValue(item,"VisitorContactInfo") +"|";////	出行人联系方式
            }
            bwList.put("PassengerName","|"+PassengerName);
            bwList.put("PassengerNationality","|"+PassengerNationality);
            bwList.put("PassengerCardID","|"+PassengerCardID);
            bwList.put("VisitorContactInfo","|"+VisitorContactInfo);
        }
        List<Map<String,Object>> vacationOptionInfo = (List<Map<String,Object>>)data.get("VacationOptionInfo");
        if(vacationOptionInfo != null && vacationOptionInfo.size()>0)
        {
            String VacationOptionID = "";
            String VacationOptionName = "";
            for(Map item : vacationOptionInfo)
            {
                VacationOptionID += getValue(item,"OptionID") +"|"; //	度假子项ID
                VacationOptionName += getValue(item,"OptionName") +"|";      //	度假子项名称
            }
            bwList.put("VacationOptionID","|"+VacationOptionID);
            bwList.put("VacationOptionName","|"+VacationOptionName);
        }
        logger.info("补充黑白名单数据的时间是："+(System.currentTimeMillis()-now));
    }

    @Override
    public void convertToFlowRuleCheckItem(DataFact dataFact, Map data, Map flowData)
    {
        long now = System.currentTimeMillis();
        flowData = null;//鸿鹄旅游不需要这个
        logger.info("通用流量实体执行时间:"+(System.currentTimeMillis()-now));
    }

    @Override
    public void writeData(DataFact dataFact, Map data, Map flowData, ThreadPoolExecutor writeExecutor,final boolean isWrite,final boolean isCheck)
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
                    //hotelGroupWriteSources.insertHotelGroupInfo(dataFactCopy.productInfoM, reqId, isWrite, isCheck);
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

    /**
     * 鸿鹄旅游只有checkType=0的情况
     * @param data
     * @param executor
     * @param writeExecutor
     * @param isWrite
     * @param isCheck
     * @return
     */
    public CheckFact executeHHTravel(Map data,ThreadPoolExecutor executor,ThreadPoolExecutor writeExecutor,boolean isWrite,boolean isCheck)
    {
        CheckFact checkFact = new CheckFact();
        beforeInvoke();
        try{
            logger.info("开始处理酒店团购 "+data.get("OrderID").toString()+" 数据");

            DataFact dataFact = new DataFact();
            complementData(dataFact,data,executor);

            Map<String,Object> bwList = new HashMap();
            convertToBlackCheckItem(dataFact,data,bwList);

            Map<String,Object> flowData = new HashMap<String, Object>();
            convertToFlowRuleCheckItem(dataFact,data,flowData);

            logger.info(data.get("OrderID").toString()+" 数据处理完毕");

            //四：构造规则引擎的数据类型CheckFact
            CheckType[] checkTypes = {CheckType.BW};//鸿鹄旅游不需要检查流量实体
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

        dataFact.otherInfo.put("OrderInfoExternalURL",getValue(data,"OrderInfoExternalURL"));
    }

    public void getVacationProductInfo(DataFact dataFact,Map data)
    {
        Map<String,Object> vacationOrderInfo = new HashMap<String, Object>();
        vacationOrderInfo.put("DCity",getValue(data,"DCity"));
        vacationOrderInfo.put("DepartureDate",getValue(data,"DepartureDate"));
        vacationOrderInfo.put("ProductName",getValue(data,"ProductName"));

        List<Map<String,Object>> vacationOptionInfoList = new ArrayList<Map<String, Object>>();
        Map<String,Object> vacationOptionInfo = new HashMap<String, Object>();
        List<Map<String,Object>> oldVacationOptionInfo = (List<Map<String,Object>>)data.get("OptionItems");
        if(oldVacationOptionInfo != null)
            for(Map item : oldVacationOptionInfo)
            {
                vacationOptionInfo.put("OptionID",getValue(item,"OptionID"));
                vacationOptionInfo.put("OptionName",getValue(item,"OptionName"));
                vacationOptionInfo.put("OptionQty",getValue(item,"OptionQty"));
                vacationOptionInfoList.add(vacationOptionInfo);
            }

        List<Map<String,Object>> vacationUserInfoList = new ArrayList<Map<String, Object>>();
        Map<String,Object> vacationUserInfo = new HashMap<String, Object>();
        List<Map<String,Object>> oldVacationUserInfo = (List<Map<String,Object>>)data.get("UserInfos");//出行人信息
        if(oldVacationUserInfo != null)
            for(Map item : oldVacationUserInfo)
            {
                vacationUserInfo.put("VisitorContactInfo",getValue(item,"VisitorContactInfo"));
                vacationUserInfo.put("VisitorCardNo",getValue(item,"VisitorCardNo"));
                vacationUserInfo.put("VisitorName",getValue(item,"VisitorName"));
                vacationUserInfo.put("VisitorNationality",getValue(item,"VisitorNationality"));
                vacationUserInfoList.add(vacationUserInfo);
            }

        dataFact.productInfoM.put("VacationOrderInfo",vacationOrderInfo);//订单信息
        dataFact.productInfoM.put("VacationOptionInfo",vacationOptionInfoList);//选项信息
        dataFact.productInfoM.put("VacationUserInfo",vacationUserInfoList);//出行人信息
    }
}

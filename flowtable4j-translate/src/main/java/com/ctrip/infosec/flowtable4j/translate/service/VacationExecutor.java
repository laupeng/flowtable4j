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
public class VacationExecutor
{
    private Logger logger = LoggerFactory.getLogger(VacationExecutor.class);
    private ThreadPoolExecutor writeExecutor = null;
    @Autowired
    VacationSources vacationSources;
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

    public CheckFact executeVacation(Map data,ThreadPoolExecutor executor,ThreadPoolExecutor writeExecutor,boolean isWrite,boolean isCheck)
    {
        this.writeExecutor = writeExecutor;
        beforeInvoke();
        DataFact dataFact = new DataFact();
        CheckFact checkFact = new CheckFact();
        try{
            logger.info("开始处理度假 "+data.get("OrderID").toString()+" 数据");
            //一：补充数据
            long now5 = System.currentTimeMillis();
            commonExecutor.complementData(dataFact,data,executor);
            logger.info("complementData公共补充数据的时间是:"+(System.currentTimeMillis()-now5));
            //添加miceInfo的信息
            String subOrderType = getValue(data,Common.SubOrderType);
            if(subOrderType.equals("18"))//18是欧铁
            {
                dataFact.miceInfo.put("AccountBook",getValue(data,"AccountBook"));
                dataFact.miceInfo.put("BookingName",getValue(data,"BookingName"));
            }
            //这里分checkType 0、1和2两种情况
            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            if(checkType == 0 )
            {
                getOtherInfo0(dataFact, data);
            }else if(checkType == 1)
            {
                getOtherInfo0(dataFact, data);
                getVacationProductInfo0(dataFact, data);
            }
            else if(checkType == 2)
            {
                getOtherInfo1(dataFact, data);
                getVacationProductInfo1(dataFact, data);
                getMiceInfo(dataFact,data);//获取欧铁信息
            }
            logger.info("一：公共补充数据的时间是:"+(System.currentTimeMillis()-now5));
            //二：黑白名单数据
            long now1 = System.currentTimeMillis();
            Map<String,Object> bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
            //产品信息
            Map vacationOrderInfo = getValueMap(dataFact.productInfoM,"VacationOrderInfo");
            if(vacationOrderInfo != null && vacationOrderInfo.size()>0)
                bwList.put("ProductName",getValue(vacationOrderInfo,"ProductName"));
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

            //bwList.putAll(dataFact.productInfoM);
            logger.info("补充黑白名单数据的时间是："+(System.currentTimeMillis()-now1));
            logger.info("二：到黑白名单数据的时间是："+(System.currentTimeMillis()-now5));
            //三：流量实体数据
            long now2 = System.currentTimeMillis();
            Map<String,Object> flowData = commonExecutor.convertToFlowRuleCheckItem(dataFact,data);
            logger.info("通用流量实体执行时间:"+(System.currentTimeMillis()-now2));
            //支付衍生字段
            List<Map> paymentInfos = dataFact.paymentInfoList;
            for(Map paymentInfo : paymentInfos)
            {
                //Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                Map cardInfoFirst = cardInfoList.get(0);
                flowData.put(Common.CardBinOrderID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.mainInfo,Common.OrderID));
                flowData.put("CCardPreNoCodeContactEMail",getValue(cardInfoFirst,Common.CCardPreNoCode)+getValue(dataFact.contactInfo,Common.ContactEMail));
                flowData.put("CCardPreNoCodeMobilePhone",getValue(cardInfoFirst,Common.CCardPreNoCode)+getValue(dataFact.contactInfo,Common.MobilePhone));
                flowData.put("CCardPreNoCodeUid",getValue(cardInfoFirst,Common.CCardPreNoCode)+getValue(dataFact.userInfo,Common.Uid));

                flowData.put("UidCCardNoCode",getValue(dataFact.userInfo,Common.Uid)+getValue(cardInfoFirst,Common.CCardNoCode));
                flowData.put("UidCardNoRefID",getValue(dataFact.userInfo,Common.Uid)+getValue(cardInfoFirst,"CardNoRefID"));
                flowData.put("CCardNoCodeSupplierID",getValue(cardInfoFirst,Common.CCardNoCode)+getValue(flowData,"SupplierID"));
                flowData.put("MobilePhoneSupplierID",getValue(dataFact.contactInfo,Common.MobilePhone)+getValue(flowData,"SupplierID"));
                flowData.put("ContactEMailSupplierID",getValue(dataFact.contactInfo,Common.ContactEMail)+getValue(flowData,"SupplierID"));
                flowData.put("ServerfromSupplierID",getValue(flowData,"Serverfrom")+getValue(flowData,"SupplierID"));
                break;
            }
            //vacationOrderInfo  //产品信息加到流量实体
            if(vacationOrderInfo != null && vacationOrderInfo.size()>0)
            {
                flowData.put("DCity",getValue(vacationOrderInfo,"DCity"));
                flowData.put("SupplierID",getValue(vacationOrderInfo,"SupplierID"));
                flowData.put("SaleMode",getValue(vacationOrderInfo,"SaleMode"));
                flowData.put("DCityName",getValue(vacationOrderInfo,"DCityName"));//fixme 这里有点问题
                flowData.put("ProductName",getValue(vacationOrderInfo,"ProductName"));
            }
            //vacationOptionInfo
            if(vacationOptionInfo != null && vacationOptionInfo.size()>0)
            {
                int optionQty = 0;
                for (Map item : vacationOptionInfo)
                {
                    optionQty += Integer.parseInt(getValue(item,"OptionQty"));
                }
                flowData.put("OptionQty",optionQty);
            }
            //vacationUserInfo
            if(vacationUserInfo != null && vacationUserInfo.size()>0)
            {
                String visitorContactInfo = "";
                int visitorCount = vacationUserInfo.size();
                for (Map item : vacationUserInfo)
                {
                    if(!getValue(item,"VisitorContactInfo").isEmpty())
                    {
                        visitorContactInfo = getValue(item,"VisitorContactInfo");
                        break;
                    }
                }
                if(!visitorContactInfo.isEmpty())
                {
                    flowData.put("VisitorContactInfo",visitorContactInfo);
                    flowData.put("MergedContactInfo",visitorContactInfo);
                }
                flowData.put("VistorCount",visitorCount);
            }
            /*flowData.put("Quantity",getValue(dataFact.productInfoM,Common.Quantity));
            flowData.put("City",getValue(dataFact.productInfoM,Common.City));
            flowData.put("ProductID",getValue(dataFact.productInfoM,Common.ProductID));
            flowData.put("ProductName",getValue(dataFact.productInfoM,Common.ProductName));
            flowData.put("ProductType",getValue(dataFact.productInfoM,Common.ProductType));
            flowData.put("Price",getValue(dataFact.productInfoM,Common.Price));*/
            logger.info("三：到补充流量数据的时间是："+(System.currentTimeMillis()-now5));
            logger.info(data.get("OrderID").toString()+" 数据处理完毕");

            //四：构造规则引擎的数据类型CheckFact
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

            //判断是否写入数据
            // boolean isWrite = commonSources.getIsWrite("HotelGroupPreByNewSystem");//fixme 这个在上线后把注释去掉

            logger.info("流量表数据\t"+ Json.toPrettyJSONString(flowData));
            writeDB(data,dataFact, flowData,isWrite,isCheck);//fixme 第一次测试先不写数据库
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
    /**
     * 获取铁友产品信息当checkType是0或1的时候
     * @param dataFact
     * @param data
     */
    public void getVacationProductInfo0(DataFact dataFact,Map data)
    {
        Map<String,Object> vacationOrderInfo = new HashMap<String, Object>();
        vacationOrderInfo.put("DCity",getValue(data,"DCity"));
        vacationOrderInfo.put("ACity",getValue(data,"ACity"));
        vacationOrderInfo.put("DepartureDate",getValue(data,"DepartureDate"));
        vacationOrderInfo.put("ProductName",getValue(data,"ProductName"));
        vacationOrderInfo.put("SaleMode",getValue(data,"SaleMode"));
        vacationOrderInfo.put("SupplierID",getValue(data,"SupplierID"));
        vacationOrderInfo.put("SupplierName",getValue(data,"SupplierName"));

        List<Map<String,Object>> vacationOptionInfoList = new ArrayList<Map<String, Object>>();
        Map<String,Object> vacationOptionInfo = new HashMap<String, Object>();
        List<Map<String,Object>> oldVacationOptionInfo = (List<Map<String,Object>>)data.get("OptionItems");
        if(oldVacationOptionInfo != null)
        for(Map item : oldVacationOptionInfo)
        {
            vacationOptionInfo.put("OptionID",getValue(item,"OptionID"));
            vacationOptionInfo.put("OptionName",getValue(item,"OptionName"));
            vacationOptionInfo.put("OptionQty",getValue(item,"OptionQty"));
            vacationOptionInfo.put("SupplierID",getValue(item,"SupplierID"));
            vacationOptionInfo.put("SupplierName",getValue(item,"SupplierName"));
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

    /**
     * 获取铁友产品信息当checkType是2的时候
     * @param dataFact
     * @param data
     */
    public void getVacationProductInfo1(DataFact dataFact,Map data)
    {
        //通过lastReqID查询所有订单相关的信息 注意这里是上一次的reqid(当checkType=1的时候)
        String reqIdStr = getValue(data,Common.OldReqID);
        if(reqIdStr.isEmpty())
            return;
        try{
            Map vacationOrderInfo = vacationSources.getVacationOrderInfo(reqIdStr);
            if(vacationOrderInfo != null && vacationOrderInfo.size()>0)
                dataFact.productInfoM.put("VacationOrderInfo",vacationOrderInfo);
            String vacationInfoID = getValue(vacationOrderInfo,"VacationInfoID");
            if(!vacationInfoID.isEmpty())
            {
                List<Map<String,Object>> vacationOptionInfoList = vacationSources.getVacationOptionInfoList(vacationInfoID);
                if(vacationOptionInfoList != null && vacationOptionInfoList.size()>0)
                    dataFact.productInfoM.put("VacationOptionInfo",vacationOrderInfo);
                List<Map<String,Object>> vacationUserInfoList = vacationSources.getVacationUserInfoList(vacationInfoID);
                if(vacationUserInfoList != null && vacationUserInfoList.size()>0)
                    dataFact.productInfoM.put("VacationUserInfo",vacationUserInfoList);
            }
        }catch (Exception exp)
        {
            logger.warn("获取HotelGroupProductInfo异常:",exp);
        }
    }

    //获取欧铁信息
    public void getMiceInfo(DataFact dataFact,Map data)
    {
        //通过lastReqID查询所有订单相关的信息 注意这里是上一次的reqid(当checkType=1的时候)
        String reqIdStr = getValue(data,Common.OldReqID);
        if(reqIdStr.isEmpty())
            return;
        try{
            Map miceInfo = vacationSources.getMiceInfo(reqIdStr);
            if(miceInfo != null && miceInfo.size()>0)
                dataFact.productInfoM.put("MiceInfo",miceInfo);
        }catch (Exception exp)
        {
            logger.warn("获取MiceInfo异常:",exp);
        }

    }

    public void writeDB(Map data,DataFact dataFact,Map flowData,final boolean isWrite,final boolean isCheck)
    {
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
}

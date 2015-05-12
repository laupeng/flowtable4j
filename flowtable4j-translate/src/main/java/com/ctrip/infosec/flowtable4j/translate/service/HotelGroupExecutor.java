package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.OrientationRequested;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
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
    CommonSources commonSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;
    @Autowired
    CommonExecutor commonExecutor;

    public CheckFact executeHotelGroup(Map data)
    {
        beforeInvoke();
        DataFact dataFact = new DataFact();
        CheckFact checkFact = new CheckFact();
        try{
            logger.info("开始处理酒店团购 "+data.get("OrderID").toString()+" 数据");
            long now = System.currentTimeMillis();
            //一：补充数据
            long now5 = System.currentTimeMillis();
            commonExecutor.complementData(dataFact,data);
            logger.info("公共补充数据的时间是:"+(System.currentTimeMillis()-now5));

            //这里分checkType 0、1和2两种情况
            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            if(checkType == 0 ||checkType == 1)
            {
                getOtherInfo0(dataFact, data);
                getHotelGroupProductInfo0(dataFact, data);
            }else if(checkType == 2)
            {
                getOtherInfo1(dataFact, data);
                getHotelGroupProductInfo1(dataFact, data);
            }
            logger.info("补充数据的时间是："+(System.currentTimeMillis()-now));
            //二：黑白名单数据
            long now1 = System.currentTimeMillis();
            Map<String,Object> bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
            bwList.putAll(dataFact.productInfoM);
            logger.info("补充数据的时间是："+(System.currentTimeMillis()-now1));
            //三：流量实体数据
            long now2 = System.currentTimeMillis();
            Map<String,Object> flowData = commonExecutor.convertToFlowRuleCheckItem(dataFact,data);
            logger.info("通用流量实体执行时间:"+(System.currentTimeMillis()-now2));
            //支付衍生字段
            List<Map> paymentInfos = dataFact.paymentInfoList;
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                Map cardInfoFirst = cardInfoList.get(0);

                flowData.put(HotelGroup.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.userInfo,Common.Uid));
                flowData.put(HotelGroup.CardBinMobilePhone,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.contactInfo,Common.MobilePhone));
                flowData.put(HotelGroup.CardBinUserIPAdd,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.ipInfo,Common.UserIPAdd));
                flowData.put(HotelGroup.ContactEMailCardBin,getValue(dataFact.contactInfo,Common.ContactEMail)+getValue(cardInfoFirst,Common.CardBin));
                if(getValue(dataFact.contactInfo,Common.MobilePhone).length()>=7)
                {
                    flowData.put(HotelGroup.UserIPAddMobileNumber,getValue(dataFact.ipInfo,Common.UserIPAdd)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
                    flowData.put(HotelGroup.UIDMobileNumber,getValue(dataFact.contactInfo,Common.Uid)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
                }
                break;
            }
            logger.info("补充数据的时间是："+(System.currentTimeMillis()-now2));
            //产品信息加到流量实体
            flowData.putAll(dataFact.productInfoM);

            //构造规则引擎的数据类型CheckFact
            CheckType[] checkTypes = {CheckType.BW,CheckType.FLOWRULE};
            BWFact bwFact = new BWFact();
            bwFact.setOrderType(Integer.parseInt(data.get(HotelGroup.OrderType).toString()));
            bwFact.setContent(bwList);
            FlowFact flowFact = new FlowFact();
            flowFact.setContent(flowData);
            flowFact.setOrderType(Integer.parseInt(data.get(HotelGroup.OrderType).toString()));
            checkFact.setBwFact(bwFact);
            checkFact.setFlowFact(flowFact);
            checkFact.setCheckTypes(checkTypes);
            if(data.get(HotelGroup.ReqID)!=null)
                checkFact.setReqId(Long.parseLong(data.get(HotelGroup.ReqID).toString()));//reqId如何获取
            logger.info(data.get("OrderID").toString()+" 数据处理完毕");
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
    }

    public void getOtherInfo1(DataFact dataFact,Map data)
    {
        String reqIdStr = getValue(data,Common.ReqID);
        if(reqIdStr.isEmpty())
        {
            return;
        }
        long lastReqID= Long.parseLong(reqIdStr);
        dataFact.otherInfo.putAll(commonSources.getOtherInfo(lastReqID));
    }
    /**
     * 获取铁友产品信息当checkType是0或1的时候
     * @param dataFact
     * @param data
     */
    public void getHotelGroupProductInfo0(DataFact dataFact,Map data)
    {
        dataFact.productInfoM.put(HotelGroup.City,getValue(data,HotelGroup.City));
        dataFact.productInfoM.put(HotelGroup.Price,getValue(data,HotelGroup.Price));//fixme 转成decimal
        dataFact.productInfoM.put(HotelGroup.ProductID,getValue(data,HotelGroup.ProductID));
        dataFact.productInfoM.put(HotelGroup.ProductName,getValue(data,HotelGroup.ProductName));
        dataFact.productInfoM.put(HotelGroup.Quantity,getValue(data,HotelGroup.Quantity));
        dataFact.productInfoM.put(HotelGroup.ProductType,getValue(data,HotelGroup.ProductType));
    }

    /**
     * 获取铁友产品信息当checkType是2的时候
     * @param dataFact
     * @param data
     */
    public void getHotelGroupProductInfo1(DataFact dataFact,Map data)
    {
        //通过lastReqID查询所有订单相关的信息
        String reqIdStr = getValue(data, Common.ReqID);
        if(reqIdStr.isEmpty())
            return;
        try{
            long reqId= Long.parseLong(reqIdStr);
            Map hotelGroupProduct = hotelGroupSources.getHotelGroupInfo(reqId);
            if(hotelGroupProduct != null && hotelGroupProduct.size()>0)
                dataFact.productInfoM.putAll(hotelGroupProduct);
        }catch (Exception exp)
        {
            logger.warn("获取HotelGroupProductInfo1异常:",exp);
        }
    }
}
package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.dao.CommonSources;
import com.ctrip.infosec.flowtable4j.translate.dao.TieYouSources;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.ctrip.infosec.flowtable4j.translate.model.TieYou;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.*;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValueMap;

/**
 * Created by lpxie on 15-5-8.
 */
public class TieYouExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(TieYouExecutor.class);

    @Autowired
    CommonExecutor commonExecutor;
    @Autowired
    CommonSources commonSources;
    @Autowired
    TieYouSources tieYouSources;

    public CheckFact executeTieYou(Map data)
    {
        beforeInvoke();
        DataFact dataFact = new DataFact();
        CheckFact checkFact = new CheckFact();
        try{
            logger.info("开始处理铁友 "+data.get("OrderID").toString()+" 数据");

            //一：补充数据
            commonExecutor.complementData(dataFact,data);
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

            //二：黑白名单数据
            Map<String,Object> bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
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

            //三：流量实体数据
            Map<String,Object> flowData = commonExecutor.convertToFlowRuleCheckItem(dataFact,data);
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
                    flowData.put(Common.OrderToSignUpDate,getDateAbs(departureDate, orderDate,1));
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
                flowData.put("MergePassengerIDType",MergePassengerIDType);
            }

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
        //订单日期
        String orderDateStr = getValue(data,Common.OrderDate);
        Date orderDate = DateUtils.parseDate(orderDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
        //注册日期
        String signUpDateStr = getValue(data,Common.SignUpDate);
        Date signUpDate = DateUtils.parseDate(signUpDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        dataFact.otherInfo.put(Common.OrderToSignUpDate,getDateAbs(signUpDate, orderDate,1));
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
        long lastReqID = Long.parseLong(getValue(data,Common.ReqID));
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

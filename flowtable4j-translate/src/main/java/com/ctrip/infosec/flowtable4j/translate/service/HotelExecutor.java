package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.common.BeanMapper;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.Hotel;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.net.www.content.image.gif;
import sun.net.www.content.text.plain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
 * Created by lpxie on 15-6-4.
 */
public class HotelExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(HotelExecutor.class);
    private ThreadPoolExecutor writeExecutor = null;
    @Autowired
    HotelSources hotelSources;
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

    public CheckFact executeHotelGroup(Map data,ThreadPoolExecutor excutor,ThreadPoolExecutor writeExecutor,boolean isWrite,boolean isCheck)
    {
        this.writeExecutor = writeExecutor;
        beforeInvoke();
        DataFact dataFact = new DataFact();
        CheckFact checkFact = new CheckFact();
        try{
            logger.info("开始处理酒店 "+data.get("OrderID").toString()+" 数据");
            //一：补充数据
            long now = System.currentTimeMillis();
            commonExecutor.complementData(dataFact,data,excutor);
            logger.info("complementData公共补充数据的时间是:"+(System.currentTimeMillis()-now));

            //这里分checkType 0、1和2两种情况
            int checkType = Integer.parseInt(getValue(data, Common.CheckType));
            if(checkType == 0 )
            {
                getOtherInfo0(dataFact, data);
            }else if(checkType == 1)
            {
                getOtherInfo0(dataFact, data);
                getHotelProductInfo0(dataFact, data);
            }
            else if(checkType == 2)
            {
                getOtherInfo1(dataFact, data);
                getHotelProductInfo1(dataFact, data);
            }
            logger.info("一：公共补充数据的时间是:"+(System.currentTimeMillis()-now));

            //二：黑白名单数据
            long now1 = System.currentTimeMillis();
            Map<String,Object> bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
            //bwList.putAll(dataFact.productInfoM);
            bwList.put("HotelLabel",getValue(dataFact.productInfoM,"HotelLabel"));
            bwList.put("HotelName",getValue(dataFact.productInfoM,"HotelName"));
            logger.info("补充黑白名单数据的时间是："+(System.currentTimeMillis()-now1));
            logger.info("二：到黑白名单数据的时间是："+(System.currentTimeMillis()-now));


            //三：流量实体数据
            long now2 = System.currentTimeMillis();
            Map<String,Object> flowData = commonExecutor.convertToFlowRuleCheckItem(dataFact,data);
            logger.info("通用流量实体执行时间:"+(System.currentTimeMillis()-now2));
            //是否外网ip
            String userIpAdd = getValue(dataFact.ipInfo,"UserIPAdd");
            if(userIpAdd.startsWith("192.") || userIpAdd.startsWith("172.") || userIpAdd.startsWith("10."))
            {
                flowData.put("IsExtranetIP","0");
            }
            flowData.put("IsExtranetIP","1");

            {//产品信息
                flowData.put("ArrivalToOrderDate",getArrivalTimeOrderDate(dataFact,data));
                flowData.put("AtNightCount",Integer.parseInt(getValue(dataFact.productInfoM,"RoomQuantity"))*Integer.parseInt(getValue(dataFact.productInfoM,"NumOfDays")));
                flowData.put("BalanceType",getValue(dataFact.productInfoM,"BalanceType"));
                flowData.put("HotelID",getValue(dataFact.productInfoM,"HotelID"));
                flowData.put("HotelCity",getValue(dataFact.productInfoM,"HotelCity"));
                Map cityInfo = commonSources.getCityInfo(getValue(dataFact.productInfoM,"City"));//根据City获取城市地址信息
                if(cityInfo != null && cityInfo.size()>0)
                {
                    flowData.put("HotelCityName",getValue(cityInfo,"CityName"));
                    flowData.put("ACountryCode",getValue(cityInfo,"Country"));
                    flowData.put("ACountryNationality",getValue(cityInfo,"Country"));
                    Map countryInfo = commonSources.getCountryNameNationality(getValue(cityInfo,"Country"));
                    if(countryInfo != null && countryInfo.size()>0)
                        flowData.put("ACountryNationality",getValue(countryInfo,"Nationality"));
                }else
                {
                    flowData.put("HotelCityName",getValue(dataFact.productInfoM,"City"));
                }
                flowData.put("OrderSource",getValue(dataFact.productInfoM,"OrderSource"));
                flowData.put("HotelLabel",getValue(dataFact.productInfoM,"HotelLabel"));
                flowData.put("HotelName",getValue(dataFact.productInfoM,"HotelName"));

                flowData.put("Supplierid",getValue(dataFact.productInfoM,"Supplierid"));
                flowData.put("GuestName",getValue(dataFact.productInfoM,"GuestName"));
                flowData.put("ConfirmType",getValue(dataFact.productInfoM,"ConfirmType"));

                flowData.put("Supplierid",getValue(dataFact.productInfoM,"Supplierid"));
                flowData.put("GuestName",getValue(dataFact.productInfoM,"GuestName"));
                flowData.put("ConfirmType",getValue(dataFact.productInfoM,"ConfirmType"));

                //en.GuestNameList = GetSingleGuestName(OrderEntity.HotelInfo.GuestName);//fixme 解决
                flowData.put("ActiveDesc",getValue(dataFact.productInfoM,"ActiveDesc"));
                flowData.put("NumOfDays",getValue(dataFact.productInfoM,"NumOfDays"));
                flowData.put("Remark",getValue(dataFact.productInfoM,"Remark"));
                //住酒店的房间的天数的均值
                int numOfDays = Integer.parseInt(getValue(dataFact.productInfoM,"NumOfDays"));
                int amount = Integer.parseInt(getValue(flowData,"Amount"));
                flowData.put("OneRoomPrice",amount/numOfDays);
                flowData.put("HotelIDServerfrom",getValue(dataFact.productInfoM,"HotelID")+getValue(dataFact.mainInfo,"Serverfrom"));
                flowData.put("ATimeToSignUpDate",getArrivalTimeSignUpDate(dataFact,data));
                flowData.put("GuestNameIssue",getValue(dataFact.productInfoM,"GuestName").getBytes()[0]);//fixme 这里有点问题 要测试

                flowData.put("Persons",getValue(dataFact.productInfoM, "Persons"));
                flowData.put("ConfirmType",getValue(dataFact.productInfoM, "ConfirmType"));

                flowData.put("GuestNameHotelNameArrivalTime",getValue(dataFact.productInfoM, "GuestName")+getValue(dataFact.productInfoM, "HotelName")
                +getValue(dataFact.productInfoM, "ArrivalTime"));

                flowData.put("GuestNameHotelNameArrivalTimeUid",getValue(dataFact.productInfoM, "GuestName")+getValue(dataFact.productInfoM, "HotelName")
                        +getValue(dataFact.productInfoM, "ArrivalTime")+getValue(dataFact.userInfo,"Uid"));

                flowData.put("GuestNameHotelNameArrivalTimeMobilePhone",getValue(dataFact.productInfoM, "GuestName")+getValue(dataFact.productInfoM, "HotelName")
                        +getValue(dataFact.productInfoM, "ArrivalTime")+getValue(dataFact.contactInfo,"MobilePhone"));

                String arrivalTime = getValue(dataFact.productInfoM,"ArrivalTime");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date arrivalDate = formatter.parse(arrivalTime);
                String dateStr = formatter.format(arrivalDate);
                flowData.put("HotelNameArrivalTime",getValue(dataFact.productInfoM, "HotelName")+dateStr);


            }
            if(data.get("CorporationInfo") == null)
                flowData.put("IsCorporationInfo","F");
            else
                flowData.put("IsCorporationInfo","T");


            //支付衍生字段
            List<Map> paymentInfos = dataFact.paymentInfoList;
            for(Map paymentInfo : paymentInfos)
            {
                //Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                Map cardInfoFirst = cardInfoList.get(0);
                flowData.put(Common.CardBinOrderID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.mainInfo,Common.OrderID));
                flowData.put(Common.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.userInfo,Common.Uid));
                flowData.put(Common.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.ipInfo,Common.UserIPValue));
                flowData.put(Common.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.contactInfo,Common.ContactEMail));
                flowData.put(Common.CardBinMobilePhone,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.contactInfo,Common.MobilePhone));
                flowData.put("MobileNumberCCardNoCode",getValue(flowData,Common.MobilePhone).substring(0,7)+getValue(cardInfoFirst,Common.CCardNoCode));
                flowData.put("MobileNumberCardNoRefID",getValue(flowData,Common.MobilePhone).substring(0,7)+getValue(cardInfoFirst,"CardNoRefID"));
                break;
            }
            flowData.put("UIDMobileNumber",getValue(flowData,"MobilePhone").substring(0,7)+getValue(flowData,"Uid"));
            flowData.put("EMailMobileNumber",getValue(dataFact.contactInfo, Common.ContactEMail)+getValue(flowData,"MobilePhone").substring(0, 7));
            flowData.put("IPMobileNumber",getValue(dataFact.ipInfo,Common.UserIPValue)+getValue(flowData,"MobilePhone").substring(0,7));
            if(getValue(dataFact.contactInfo,Common.MobilePhone).length()>=7)//fixme 看看下面这段是不是都有用到，可以拿到common里面去
            {
                flowData.put(Common.UserIPAddMobileNumber,getValue(dataFact.ipInfo,Common.UserIPAdd)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
                flowData.put(Common.UIDMobileNumber,getValue(dataFact.userInfo,Common.Uid)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
            }

            //<editor-fold desc="产品信息加到流量实体">
            flowData.put("Quantity",getValue(dataFact.productInfoM,Common.Quantity));
            flowData.put("City",getValue(dataFact.productInfoM,Common.City));
            flowData.put("ProductID",getValue(dataFact.productInfoM,Common.ProductID));
            flowData.put("ProductName",getValue(dataFact.productInfoM,Common.ProductName));
            flowData.put("ProductType",getValue(dataFact.productInfoM,Common.ProductType));
            flowData.put("Price",getValue(dataFact.productInfoM,Common.Price));
            //</editor-fold>

            //酒店反养蜂KPI
            {
                List<Map<String,Object>> guestNameList = (List<Map<String,Object>>)flowData.get("GuestNameList");
                if(guestNameList != null && guestNameList.size()>0)
                {
                    String firstSingleGuestName = getValue(guestNameList.get(0),"SingleGuestName");
                    int guestNameCount = guestNameList.size();
                    String sqlValue = "";
                    flowData.put("AntiBeekeepingHotel_ZJJ","");
                    Map<String,Map<String,Object>> AntiBeekeepingHotels = new HashMap<String,Map<String,Object>>();

                    for(Map singleGuestName : guestNameList)
                    {
                        Map<String,Object> result = new HashMap<String, Object>();
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                        String timeLimitStr = format1.format(date);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        calendar.add(calendar.MINUTE, -43200);//往前720分钟
                        String nowTimeStr = format1.format(calendar.getTime());
                        String uid = getValue(dataFact.userInfo,Common.Uid);
                        String mobilePhone = getValue(dataFact.contactInfo,Common.MobilePhone);
                        String singleGuestNameStr = getValue(singleGuestName,"SingleGuestName");
                        String hotelNameArrivalTime = getValue(flowData,"HotelNameArrivalTime");
                        List<Map<String,Object>> sumReqIdInfo = hotelSources.getSumReqIdInfo(singleGuestNameStr,hotelNameArrivalTime,uid,mobilePhone,nowTimeStr,timeLimitStr);
                        if(sumReqIdInfo != null && sumReqIdInfo.size()>0)
                        {
                            for(Map reqIdInfo : sumReqIdInfo)
                            {
                                result.put(getValue(reqIdInfo,"reqid"),getValue(reqIdInfo,"SumSameReqidCount"));
                            }
                            AntiBeekeepingHotels.put(singleGuestNameStr,result);
                        }else
                        {
                            flowData.put("AntiBeekeepingHotel_ZJJ","F");
                            break;
                        }
                    }
                    //如果入住人存在，而且都有reqid列表，执行下面代码
                    if(AntiBeekeepingHotels != null && AntiBeekeepingHotels.size()>0 && getValue(flowData,"AntiBeekeepingHotel_ZJJ").equals(""))
                    {
                        Map first = getValueMap(AntiBeekeepingHotels,firstSingleGuestName);
                        boolean HasSameReqid = false;
                        Set<Map.Entry> entries =  first.entrySet();
                        for(Map.Entry<String,Object> entry : entries)
                        {
                            if(Integer.parseInt(entry.getValue().toString()) == guestNameCount)
                            {
                                if(guestNameCount == 1)
                                {
                                    HasSameReqid = true;
                                }else
                                {
                                    for(Map item : AntiBeekeepingHotels.values())
                                    {
                                        HasSameReqid = true;
                                        if(!item.containsKey(entry.getKey()))
                                        {
                                            HasSameReqid = false;
                                            break;
                                        }
                                    }
                                }
                            }

                            if(HasSameReqid)
                                break;
                        }

                        if(HasSameReqid)
                            flowData.put("AntiBeekeepingHotel_ZJJ","T");
                        else
                            flowData.put("AntiBeekeepingHotel_ZJJ","F");
                    }
                }
            }
            logger.info("三：到补充流量数据的时间是："+(System.currentTimeMillis()-now));
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
     * 获取酒店产品信息当checkType是0或1的时候
     * @param dataFact
     * @param data
     */
    public void getHotelProductInfo0(DataFact dataFact,Map data)
    {
        dataFact.productInfoM.put(Hotel.ActiveDesc,getValue(data,Hotel.ActiveDesc));
        dataFact.productInfoM.put(Hotel.AddBreakfast,getValue(data,Hotel.AddBreakfast));
        dataFact.productInfoM.put(Hotel.ArrivalTime,getValue(data,Hotel.ArrivalTime));
        dataFact.productInfoM.put(Hotel.Breakfast,getValue(data,Hotel.Breakfast));
        dataFact.productInfoM.put(Hotel.CheckInDate,getValue(data,Hotel.CheckInDate));
        dataFact.productInfoM.put(Hotel.CheckOutDate,getValue(data,Hotel.CheckOutDate));
        dataFact.productInfoM.put(Hotel.ConfirmType,getValue(data,Hotel.ConfirmType));
        dataFact.productInfoM.put(Hotel.CutOffTime,getValue(data,Hotel.CutOffTime));
        dataFact.productInfoM.put(Hotel.FlightOrTrainNo,getValue(data,Hotel.FlightOrTrainNo));
        dataFact.productInfoM.put(Hotel.GuestName,getValue(data,Hotel.GuestName));
        dataFact.productInfoM.put(Hotel.HotelContactPerson,getValue(data,Hotel.HotelContactPerson));
        dataFact.productInfoM.put(Hotel.HotelID,getValue(data,Hotel.HotelID));
        dataFact.productInfoM.put(Hotel.HotelLabel,getValue(data,Hotel.HotelLabel));
        dataFact.productInfoM.put(Hotel.HotelName,getValue(data,Hotel.HotelName));
        dataFact.productInfoM.put(Hotel.HotelOrderID,getValue(data,Hotel.HotelOrderID));
        dataFact.productInfoM.put("HotelTel",getValue(data,"HotelTel"));
        dataFact.productInfoM.put(Hotel.LateReserveTime,getValue(data,Hotel.LateReserveTime));
        dataFact.productInfoM.put(Hotel.Level,getValue(data,Hotel.Level));
        dataFact.productInfoM.put(Hotel.NoShowRate,getValue(data,Hotel.NoShowRate));
        dataFact.productInfoM.put(Hotel.NumOfDays,getValue(data,Hotel.NumOfDays));
        dataFact.productInfoM.put(Hotel.OrderAmendRemark,getValue(data,Hotel.OrderAmendRemark));
        dataFact.productInfoM.put(Hotel.OrderSource,getValue(data,Hotel.OrderSource));
        dataFact.productInfoM.put(Hotel.PaymentAddress,getValue(data,Hotel.PaymentAddress));
        dataFact.productInfoM.put(Hotel.PromptNum,getValue(data,Hotel.PromptNum));
        dataFact.productInfoM.put(Hotel.Remark,getValue(data,Hotel.Remark));
        dataFact.productInfoM.put(Hotel.RoomPrice,getValue(data,Hotel.RoomPrice));
        dataFact.productInfoM.put(Hotel.RoomStatus,getValue(data,Hotel.RoomStatus));
        dataFact.productInfoM.put(Hotel.RoomType,getValue(data,Hotel.RoomType));

        dataFact.productInfoM.put(Hotel.CashBack,getValue(data,Hotel.CashBack));
        dataFact.productInfoM.put(Hotel.BalanceType,getValue(data,Hotel.BalanceType));
        dataFact.productInfoM.put(Hotel.IsHoldRoom,getValue(data,Hotel.IsHoldRoom));
        dataFact.productInfoM.put(Hotel.Guarantee,getValue(data,Hotel.Guarantee));
        dataFact.productInfoM.put(Hotel.Persons,getValue(data,Hotel.Persons));
        dataFact.productInfoM.put(Hotel.RoomQuantity,getValue(data,Hotel.RoomQuantity));
        dataFact.productInfoM.put(Hotel.City,getValue(data,Hotel.City));
        dataFact.productInfoM.put(Hotel.IsSupplierOrder,getValue(data,Hotel.IsSupplierOrder));
        dataFact.productInfoM.put(Hotel.Star,getValue(data,Hotel.Star));
        dataFact.productInfoM.put(Hotel.Supplierid,getValue(data,Hotel.Supplierid));
        dataFact.productInfoM.put(Hotel.Countryname,getValue(data,Hotel.Countryname));

        dataFact.productInfoM.put(Hotel.AllNeedGuarantee,getValue(data,Hotel.AllNeedGuarantee));
        dataFact.productInfoM.put(Hotel.GuaranteeWay,getValue(data,Hotel.GuaranteeWay));
        dataFact.productInfoM.put(Hotel.VendorID,getValue(data,Hotel.VendorID));
        dataFact.productInfoM.put(Hotel.SourceName,getValue(data,Hotel.SourceName));
        dataFact.productInfoM.put(Hotel.AID,getValue(data,Hotel.AID));
        dataFact.productInfoM.put(Hotel.SID,getValue(data,Hotel.SID));
        dataFact.productInfoM.put(Hotel.GuaranteePolicy,getValue(data,Hotel.GuaranteePolicy));

        try{
            List<Map<String,Object>> giftList = (List<Map<String,Object>>)data.get(Hotel.GiftList);
            dataFact.productInfoM.put(Hotel.GiftList,giftList);
            /*if(giftList != null && giftList.size()>0)
            {
                for(Map<String,Object> gift : giftList)
                {

                }
            }*/
        }catch(Exception exp)
        {
            logger.warn("获取酒店giftList字段信息异常:",exp);
        }

    }

    /**
     * 获取酒店产品信息当checkType是2的时候
     * @param dataFact
     * @param data
     */
    public void getHotelProductInfo1(DataFact dataFact,Map data)
    {
        //通过lastReqID查询所有订单相关的信息 注意这里是上一次的reqid(当checkType=1的时候)
        String reqIdStr = getValue(data,Common.OldReqID);
        if(reqIdStr.isEmpty())
            return;
        try{
            Map hotelProduct = hotelSources.getHotelInfo(reqIdStr);
            if(hotelProduct != null && hotelProduct.size()>0)
                dataFact.productInfoM.putAll(hotelProduct);
        }catch (Exception exp)
        {
            logger.warn("获取HotelProductInfo异常:",exp);
        }
    }

    //获取到达酒店到订单时间的差值
    public long getArrivalTimeOrderDate(DataFact dataFact,Map data) throws ParseException
    {
        logger.info(data.get("OrderID")+"获取时间的差值相关信息");
        //订单日期
        String orderDateStr = getValue(data,Common.OrderDate);
        Date orderDate = DateUtils.parseDate(orderDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
        //注册日期
        String arrivalTimeStr = getValue(data,"ArrivalTime");
        Date arrivalTime = DateUtils.parseDate(arrivalTimeStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        return getDateAbs(arrivalTime, orderDate,1);
    }

    //获取到达酒店到用户注册时间的差值
    public long getArrivalTimeSignUpDate(DataFact dataFact,Map data) throws ParseException
    {
        logger.info(data.get("OrderID")+"获取时间的差值相关信息");
        //订单日期
        String signUpDateStr = getValue(dataFact.userInfo,Common.SignUpDate);
        Date signUpDate = DateUtils.parseDate(signUpDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
        //注册日期
        String arrivalTimeStr = getValue(data,"ArrivalTime");
        Date arrivalTime = DateUtils.parseDate(arrivalTimeStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        return getDateAbs(arrivalTime, signUpDate,1);
    }
}

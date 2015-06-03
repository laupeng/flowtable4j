package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.common.MyJSON;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.Flight;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static com.ctrip.infosec.common.SarsMonitorWrapper.afterInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.beforeInvoke;
import static com.ctrip.infosec.common.SarsMonitorWrapper.fault;
import static com.ctrip.infosec.flowtable4j.translate.common.IpConvert.ipConvertTo10;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.Json;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValueMap;

/**
 * Created by lpxie on 15-3-31.
 * 两个模块：1，数据补充。2，转换成实体
 */

@Service
public class FlightExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(FlightExecutor.class);
    private ThreadPoolExecutor writeExecutor = null;
    @Autowired
    FlightSources flightSources;
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

    public CheckFact executeFlight(Map data,ThreadPoolExecutor excutor,ThreadPoolExecutor writeExecutor,boolean isWrite,boolean isCheck)
    {
        this.writeExecutor = writeExecutor;
        beforeInvoke();
        DataFact dataFact = new DataFact();
        CheckFact checkFact = new CheckFact();
        try{
            logger.info("开始处理机票 "+data.get("OrderID").toString()+" 数据");
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
                getFlightProductInfo0(dataFact, data);
            }
            else if(checkType == 2)
            {
                getOtherInfo1(dataFact, data);
                getFlightProductInfo1(dataFact, data);
            }
            //机票利润值计算
            fillFightsOrderProfit(data,dataFact);
            logger.info("一：公共补充数据的时间是:"+(System.currentTimeMillis()-now));

            //二：黑白名单数据
            long now1 = System.currentTimeMillis();
            Map<String,Object> bwList = commonExecutor.convertToBlackCheckItem(dataFact,data);
            bwList.put("TakeOffToOrderDate",getValue(dataFact.otherInfo,"TakeOffToOrderDate"));
            Map flightsOrderInfo = getValueMap(dataFact.productInfoM,Flight.FlightsOrderInfo);
            //机票产品信息
            if(flightsOrderInfo!=null && flightsOrderInfo.size()>0)
            {
                bwList.put(Flight.AAirPort,getValue(flightsOrderInfo,Flight.AAirPort));
                bwList.put(Flight.EAirPort,getValue(flightsOrderInfo,Flight.EAirPort));
                bwList.put(Flight.DAirPort,getValue(flightsOrderInfo,Flight.DAirPort));
                bwList.put("Acity",getValue(flightsOrderInfo,"Acity"));
                bwList.put("Dcity",getValue(flightsOrderInfo,"Dcity"));
            }
            List<Map<String,Object>> passengerInfoList = (List<Map<String,Object>>)dataFact.productInfoM.get(Flight.PassengerInfoList);
            if(passengerInfoList != null && passengerInfoList.size()>0)
            {
                String passengerName = "", passengerNationality = "",passengerCardID = "";
                for(Map passengerInfo : passengerInfoList)
                {
                    passengerName += getValue(passengerInfo,"PassengerName") + "|";
                    passengerNationality += getValue(passengerInfo,"PassengerNationality") + "|";
                    passengerCardID += getValue(passengerInfo,"PassengerCardID") + "|";
                }
                bwList.put("PassengerName","|"+passengerName);
                bwList.put("PassengerNationality","|"+passengerNationality);
                bwList.put("PassengerCardID","|"+passengerCardID);
            }
            logger.info("补充黑白名单数据的时间是："+(System.currentTimeMillis()-now1));
            logger.info("二：到黑白名单数据的时间是："+(System.currentTimeMillis()-now));

            //三：流量实体数据
            long now2 = System.currentTimeMillis();
            Map<String,Object> flowData = commonExecutor.convertToFlowRuleCheckItem(dataFact,data);
            logger.info("通用流量实体执行时间:"+(System.currentTimeMillis()-now2));
            //支付衍生字段
            List<Map> paymentInfos = dataFact.paymentInfoList;
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                String prepayType = getValue(subPaymentInfo,Common.PrepayType);
                if(prepayType.equals("CCARD")||prepayType.equals("DCARD")||prepayType.equals("DQPAY"))
                {
                    List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
                    Map cardInfoFirst = cardInfoList.get(0);
                    flowData.put("BranchCity",getValue(cardInfoFirst,"BranchCity"));
                    flowData.put("BranchProvince",getValue(cardInfoFirst,"BranchProvince"));
                    flowData.put("MobilePhoneCardBin",getValue(dataFact.contactInfo,Common.MobilePhone)+getValue(cardInfoFirst,Common.CardBin));
                    flowData.put("UserIPAddCardBin",getValue(dataFact.ipInfo,Common.UserIPAdd)+getValue(cardInfoFirst,Common.CardBin));
                    /*flowData.put(Common.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.userInfo,Common.Uid));
                    flowData.put(Common.CardBinOrderID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.mainInfo,Common.OrderID));
                    flowData.put(Common.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.userInfo,Common.Uid));
                    flowData.put(Common.CardBinMobilePhone,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.contactInfo,Common.MobilePhone));
                    flowData.put(Common.CardBinUserIPAdd,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.ipInfo,Common.UserIPAdd));
                    flowData.put(Common.ContactEMailCardBin,getValue(dataFact.contactInfo,Common.ContactEMail)+getValue(cardInfoFirst,Common.CardBin));*/
                    break;
                }
                else if(prepayType.equals("TMPAY"))
                {
                    flowData.put("TmpayAmount",getValue(subPaymentInfo,"Amount"));
                    break;
                }
            }
            if(getValue(dataFact.contactInfo,Common.MobilePhone).length()>=7)
            {
                flowData.put(Common.UIDMobileNumber,getValue(dataFact.userInfo,Common.Uid)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
                flowData.put("UidMergerMobilePhone7",getValue(dataFact.userInfo,Common.Uid)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
                flowData.put("UserIPAddMergerMobilePhone7",getValue(dataFact.ipInfo,Common.UserIPAdd)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
            }

            String aCityProvince = "", aCityName = "",aCountryCode="";
            Map aCnpInfo = flightSources.getCityNameProvince(getValue(flightsOrderInfo,"ACity"));
            if(aCnpInfo!=null&&aCnpInfo.size()>0)
            {
                aCityProvince = getValue(aCnpInfo,"ProvinceName");
                aCityName = getValue(aCnpInfo,"CityName");
                aCountryCode = getValue(aCnpInfo,"Country");
            }
            String dCityProvince = "", dCityName = "",dCountryCode="";
            Map dCnpInfo = flightSources.getCityNameProvince(getValue(flightsOrderInfo,"DCity"));
            if(dCnpInfo!=null&&dCnpInfo.size()>0)
            {
                dCityProvince = getValue(dCnpInfo,"ProvinceName");
                dCityName = getValue(dCnpInfo,"CityName");
                dCountryCode = getValue(dCnpInfo,"Country");
            }
            flowData.put("Agencyid",getValue(flightsOrderInfo,"Agencyid"));
            flowData.put("Insurance_fee",getValue(flightsOrderInfo,"Insurance_fee"));
            flowData.put("MergedDACityName","|"+aCityName+"|"+dCityName+"|");

            flowData.put("ACityName",aCityName);
            flowData.put("ACityProvince",aCityProvince);
            flowData.put("ACountryCode",aCountryCode);

            flowData.put("DCityName",dCityName);
            flowData.put("DCityProvince",dCityProvince);
            flowData.put("DCountryCode",dCountryCode);

            //国家相关信息
            if(!dCountryCode.isEmpty())
            {
                Map countryInfo = flightSources.getCountryNameNationality(dCountryCode);
                flowData.put("DCountryNationality",getValue(countryInfo,"Nationality"));
            }
            if(!aCountryCode.isEmpty())
            {
                Map countryInfo = flightSources.getCountryNameNationality(aCountryCode);
                flowData.put("ACountryNationality",getValue(countryInfo,"Nationality"));
            }

            //获取IP省
            /*GetCityNameProvince(en.IPCity, ref dCityName, ref dCityProvince, ref countryCode);//fixme 这段代码的逻辑有问题
            en.IPProvince = dCityProvince;*/


            //产品信息加到流量实体
            if(flightsOrderInfo!=null && flightsOrderInfo.size()>0)
            {
                flowData.put("Profit",getValue(flightsOrderInfo,"Profit"));
                flowData.put("FlightCostRate",getValue(flightsOrderInfo,"FlightCostRate"));
                flowData.put("AgencyName",getValue(flightsOrderInfo,"AgencyName"));
            }
            List<Map<String,Object>> segmentInfoList = (List<Map<String,Object>>)dataFact.productInfoM.get(Flight.SegmentInfoList);
            if(segmentInfoList != null && segmentInfoList.size()>0)
            {
                flowData.put("SegmentInfoCount",segmentInfoList.size());
                flowData.put("SegmentInfoCount",getValue(segmentInfoList.get(0),"SeatClass"));
                flowData.put("SegmentInfoCount",getValue(segmentInfoList.get(0), "Sequence"));
            }

            String contactEMail = getValue(dataFact.contactInfo,"ContactEMail");
            if(dataFact.contactInfo != null && !contactEMail.isEmpty())
            {
                String tmpContactEMail = contactEMail.replace(".","").replace("_","").replace("@","");
                if(tmpContactEMail.length()>=7)
                {
                    flowData.put("ContactEMailToConvert7",tmpContactEMail.substring(0,7));
                }
            }

            //乘客信息
            if(passengerInfoList != null && passengerInfoList.size()>0)
            {
                String passengerName = "|",passengerNationality = "|",passengerCardID = "|",passengerCardIDLength = "|",mergerPassengerCardIDLength = "|";
                passengerCardIDLength = "F";
                List<Map<String,Object>> passengerList = new ArrayList<Map<String, Object>>();
                flowData.put("PassengerCount",passengerInfoList.size());

                String PassengerNationality1 = "";
                flowData.put("IsSamePassengerNationality","");
                if(passengerInfoList.size()>=1)
                {
                    flowData.put("IsSamePassengerNationality","T");
                }

                List<Map<String,Object>> flowPassengerList = new ArrayList<Map<String, Object>>();
                Map<Integer,String> dic3 = new HashMap<Integer, String>();
                Map<Integer,String> dic6 = new HashMap<Integer, String>();
                for(Map passengerInfo : passengerInfoList)
                {
                    passengerName += getValue(passengerInfo,"PassengerName") + "|";
                    passengerNationality += getValue(passengerInfo,"PassengerNationality") + "|";
                    passengerCardID += getValue(passengerInfo,"PassengerCardID") + "|";
                    if(!passengerCardID.isEmpty())
                    {
                        mergerPassengerCardIDLength += passengerCardID.length()+"|";
                    }

                    String PassengerCardID = getValue(passengerInfo,"PassengerCardID");
                    if(dic3.size() == 0 || (!PassengerCardID.isEmpty()&&PassengerCardID.length()>3&&!dic3.containsValue(PassengerCardID.substring(0,3))))
                    {
                        dic3.put(dic3.size(),PassengerCardID.substring(0,3));
                    }
                    if(dic6.size() == 0 || (!PassengerCardID.isEmpty()&&PassengerCardID.length()>6&&!dic6.containsValue(PassengerCardID.substring(0,6))))
                    {
                        dic6.put(dic6.size(),PassengerCardID.substring(0,6));
                    }

                    if(passengerCardIDLength.equals("F"))
                    {
                        if(PassengerCardID.length() != 15 && PassengerCardID.length() != 18)//登机人证件长度存在不等于15位和18位的
                        {
                            passengerCardIDLength = "T";
                        }
                    }

                    Map<String,Object> flightPassenger = new HashMap();
                    flightPassenger.put("PassengerName",getValue(passengerInfo,"PassengerName"));
                    flightPassenger.put("PassengerNationality",getValue(passengerInfo,"PassengerNationality"));
                    flightPassenger.put("PassengerCardID",getValue(passengerInfo,"PassengerCardID"));
                    flightPassenger.put("PassengerNameCardID",getValue(passengerInfo,"PassengerName")+getValue(passengerInfo,"PassengerCardID"));
                    //PassengerCardID6 += getValue(passengerInfo,PassengerCardID);//fixme 原来的代码逻辑有问题
                    String PassengerCardID6 = getValue(passengerInfo,PassengerCardID).substring(0,6);
                    flightPassenger.put("PassengerCardID6",PassengerCardID6);//乘机人证件前6位
                    flightPassenger.put("PassengerCardIDLengthOne",getValue(passengerInfo,"PassengerCardID").length());

                    String IDCardNumberProvinceName = "",IDCardNumberProvinceNameToDACity = "";
                    Map<String,Object> iDCardInfo = flightSources.getIDCardProvince(PassengerCardID6);
                    String provinceName = "";
                    if(iDCardInfo != null && iDCardInfo.size()>0)
                    {
                        provinceName = getValue(iDCardInfo,"ProvinceName");
                        String mobilePhoneProvince = getValue(dataFact.contactInfo,"MobilePhoneProvince");
                        //aCityProvince dCityProvince  IDCardNumberProvinceName  IDCardNumberProvinceNameToDACity
                        if(!provinceName.equals(mobilePhoneProvince))
                        {
                            IDCardNumberProvinceName = "F";
                        }else
                        {
                            IDCardNumberProvinceName = "T";
                        }
                        if(!provinceName.equals(aCityProvince) && !provinceName.equals(dCityProvince))
                        {
                            IDCardNumberProvinceNameToDACity = "F";
                        }else
                        {
                            IDCardNumberProvinceNameToDACity = "T";
                        }
                    }
                    if(!getValue(flowData,"IDCardNumberProvinceNameToMobilePhone").equals("T"))
                    {
                        flowData.put("IDCardNumberProvinceNameToMobilePhone",IDCardNumberProvinceName);
                    }
                    if(!getValue(flowData,"IDCardNumberProvinceNameToDACity").equals("T"))
                    {
                        flowData.put("IDCardNumberProvinceNameToDACity",IDCardNumberProvinceNameToDACity);
                    }

                    flightPassenger.put("UidPassengerName",passengerName+getValue(dataFact.userInfo,"Uid"));
                    flightPassenger.put("UidPassengerNameCardID",getValue(dataFact.userInfo,"Uid")+passengerCardID);
                    flightPassenger.put("MobilePhonePassengerCardID",getValue(dataFact.contactInfo,"MobilePhone")+passengerCardID);
                    flightPassenger.put("EMailPassengerNameCardID",getValue(dataFact.contactInfo,"ContactEMail")+passengerCardID);
                    flightPassenger.put("CCardNoCodePassengerNameCardID",getValue(flowData,"CCardNoCode")+passengerCardID);
                    flightPassenger.put("CardNoRefIDPassengerNameCardID",getValue(flowData,"CardNoRefID")+passengerCardID);

                    //判断多个乘机人国籍是否相同
                    if(getValue(flowData,"IsSamePassengerNationality").equals("T"))
                    {
                        if(PassengerNationality1.isEmpty())
                            PassengerNationality1 = passengerNationality;
                        else
                        {
                            if(PassengerNationality1.equals(passengerNationality))
                            {
                                flowData.put("IsSamePassengerNationality","F");
                            }
                        }
                    }
                    flowPassengerList.add(flightPassenger);
                }

                int count = Integer.parseInt(getValue(flowData,"PassengerCount"));
                if(count>0)
                {
                    long amount = Integer.parseInt(getValue(dataFact.mainInfo,"Amount"));
                    flowData.put("LeafletAmount",amount/count);
                }

                flowData.put("MergerPassengerName",passengerName.toUpperCase());
                flowData.put("MergerPassengerNationality",passengerNationality.toUpperCase());
                flowData.put("MergerPassengerCardID",passengerCardID.toUpperCase());
                flowData.put("PassengerCardIDLength",passengerCardIDLength.toUpperCase());
                flowData.put("MergerPassengerCardIDLength",mergerPassengerCardIDLength.toUpperCase());
                flowData.put("DCountPassengerCardID3",dic3.size());
                flowData.put("DCountPassengerCardID6",dic6.size());

                //计算机票负利润
                //List<Map> paymentInfos = dataFact.paymentInfoList;
                for(Map paymentInfo : paymentInfos)
                {
                    Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
                    String prepayType = getValue(subPaymentInfo,Common.PrepayType);
                    if(prepayType.equals("TMPAY"))
                    {
                        try{
                            Map flightOrderInfo = getValueMap(dataFact.productInfoM,"FlightsOrderInfo");
                            if(flightOrderInfo.size()>0)
                            {
                                long flightPrice = Long.parseLong(getValue(flightOrderInfo,"Flightprice"));
                                long flightCost = Long.parseLong(getValue(flightOrderInfo,"FlightCost"));
                                long packageAttachFee = Long.parseLong(getValue(flightOrderInfo,"PackageAttachFee"));
                                long insurance_fee = Long.parseLong(getValue(flightOrderInfo,"Insurance_fee"));
                                long amount = Long.parseLong(getValue(subPaymentInfo,"Amount"));

                                double onePointFivePercentProfit = (flightPrice- flightCost)+packageAttachFee*0.7+insurance_fee*0.9-amount*0.15;
                                double twoPointFivePercentProfit = (flightPrice- flightCost)+packageAttachFee*0.7+insurance_fee*0.9-amount*0.02;
                                double threePointFivePercentProfit = (flightPrice- flightCost)+packageAttachFee*0.7+insurance_fee*0.9-amount*0.03;
                                double fourPointFivePercentProfit = (flightPrice- flightCost)+packageAttachFee*0.7+insurance_fee*0.9-amount*0.04;
                                double fivePointFivePercentProfit = (flightPrice- flightCost)+packageAttachFee*0.7+insurance_fee*0.9-amount*0.05;

                                double onePointFivePercentProfitNotIncludeBinded = (flightPrice- flightCost)+insurance_fee*0.9-amount*0.15;
                                double twoPointFivePercentProfitNotIncludeBinded = (flightPrice- flightCost)+insurance_fee*0.9-amount*0.02;
                                double threePointFivePercentProfitNotIncludeBinded = (flightPrice- flightCost)+insurance_fee*0.9-amount*0.03;
                                double fourPointFivePercentProfitNotIncludeBinded = (flightPrice- flightCost)+insurance_fee*0.9-amount*0.04;
                                double fivePointFivePercentProfitNotIncludeBinded = (flightPrice- flightCost)+insurance_fee*0.9-amount*0.05;

                                flowData.put("OnePointFivePercentProfit",onePointFivePercentProfit);
                                flowData.put("TwoPercentProfit",twoPointFivePercentProfit);
                                flowData.put("Threepercentprofit",threePointFivePercentProfit);
                                flowData.put("Fourpercentprofit",fourPointFivePercentProfit);
                                flowData.put("Fivepercentprofit",fivePointFivePercentProfit);
                                flowData.put("OnePointFivePercentProfitNotIncludeBinded",onePointFivePercentProfitNotIncludeBinded);
                                flowData.put("TwoPercentProfitNotIncludeBinded",twoPointFivePercentProfitNotIncludeBinded);
                                flowData.put("ThreepercentProfitnotincludebinded",threePointFivePercentProfitNotIncludeBinded);
                                flowData.put("FourpercentProfitnotincludebinded",fourPointFivePercentProfitNotIncludeBinded);
                                flowData.put("FivepercentProfitnotincludebinded",fivePointFivePercentProfitNotIncludeBinded);

                                flowData.put("ActualAmount",amount+insurance_fee+packageAttachFee);
                            }
                        }catch (Exception exp)
                        {
                            logger.warn("机票负利润计算子项异常:",exp.getMessage());
                        }
                    }
                }
            }

            //同一手机，相同卡号前12位  同一Email，相同卡号前12位  同一UID，相同卡号前12位
            flowData.put("CCardPreNoCodeMobilePhone",getValue(flowData,"CCardPreNoCode")+getValue(dataFact.contactInfo,"MobilePhone"));
            flowData.put("CCardNoCodeMobilePhone7",getValue(flowData,"CCardNoCode")+getValue(dataFact.contactInfo,"MobilePhone").substring(0,7));
            flowData.put("CCardPreNoCodeContactEMail",getValue(flowData,"CCardPreNoCode")+getValue(dataFact.contactInfo, "ContactEMail"));
            flowData.put("CCardPreNoCodeUid",getValue(flowData,"CCardPreNoCode")+getValue(dataFact.userInfo,"Uid"));

            flowData.put("CardBinOrderID",getValue(flowData,"CardBin")+getValue(dataFact.mainInfo,"OrderId"));
            flowData.put("CardBinMobilePhone",getValue(flowData,"CardBin")+getValue(dataFact.contactInfo, "MobilePhone"));
            flowData.put("ContactEMailCardBin",getValue(flowData,"CCardPreNoCode")+getValue(dataFact.contactInfo, "ContactEMail"));
            flowData.put("CCardPreNoCodeUid",getValue(dataFact.contactInfo, "ContactEMail")+getValue(flowData,"CardBin"));

            flowData.put("CardBinUID",getValue(flowData,"CardBin")+getValue(dataFact.userInfo,"Uid"));

            flowData.put("MobilePhoneContactEMail",getValue(dataFact.contactInfo, "MobilePhone")+getValue(dataFact.contactInfo, "ContactEMail"));

            flowData.put("MobilePhoneUID",getValue(dataFact.contactInfo, "MobilePhone")+getValue(dataFact.userInfo,"Uid"));
            flowData.put("UidUserIPValue",getValue(dataFact.userInfo,"Uid")+getValue(dataFact.ipInfo,"UserIPValue"));

            flowData.put("CCardNoCodeContactEMailUserIPValue",getValue(flowData,"CCardNoCode")+getValue(dataFact.contactInfo, "ContactEMail")+getValue(dataFact.ipInfo,"UserIPValue"));
            flowData.put("CardNoRefIDContactEMailUserIPValue",getValue(flowData,"CardNoRefID")+getValue(dataFact.contactInfo, "ContactEMail")+getValue(dataFact.ipInfo,"UserIPValue"));

            flowData.put("CardNoRefIDMobilePhone7",getValue(flowData,"CardNoRefID")+getValue(dataFact.contactInfo,"MobilePhone").substring(0,7));


            String contactEmail = getValue(flowData,"ContactEMail");
            if(!contactEmail.isEmpty()&&contactEmail.contains("@"))
            {
                contactEMail = contactEmail.substring(contactEmail.indexOf("@")+1);
                flowData.put("ContactEMailToPassengerNationality","T");
                String[] passengerNationalitylist = getValue(flowData,"MergerPassengerNationality").split("|");
                for(String item : passengerNationalitylist)
                {
                    if(item.isEmpty())
                        continue;
                    if(contactEmail.contains(item))
                    {
                        flowData.put("ContactEMailToPassengerNationality","F");
                        break;
                    }
                }
            }
            flowData.put("DAirPortAAirPort",getValue(flightsOrderInfo,"DAirPort")+getValue(flightsOrderInfo,"AAirPort"));
            flowData.put("ContactEMailMergerMobilePhone7",getValue(dataFact.contactInfo, "ContactEMail")+getValue(dataFact.contactInfo, "MobilePhone").substring(0,7));

            boolean isIPProvince = commonOperation.isMatch("EQ",getValue(flowData,"IPProvince"),getValue(flowData,"DCityProvince"));
            if(isIPProvince)
            {
                flowData.put("IPProvinceCompareDCityProvince","T");
            }else
            {
                flowData.put("IPProvinceCompareDCityProvince","F");
            }
            boolean isMobilePhoneProvince = commonOperation.isMatch("EQ",getValue(flowData,"MobilePhoneProvince"),getValue(flowData,"DCityProvince"));
            if(isMobilePhoneProvince)
            {
                flowData.put("MobilePhoneProvinceCompareDCityProvince","T");
            }else
            {
                flowData.put("MobilePhoneProvinceCompareDCityProvince","F");
            }
            boolean isACityProvince = commonOperation.isMatch("EQ",getValue(flowData,"IPProvince"),getValue(flowData,"ACityProvince"));
            if(isACityProvince)
            {
                flowData.put("IPProvinceCompareACityProvince","T");
            }else
            {
                flowData.put("IPProvinceCompareACityProvince","F");
            }
            boolean MobilePhoneProvinceCompareACityProvince = commonOperation.isMatch("EQ",getValue(flowData,"MobilePhoneProvince"),getValue(flowData,"ACityProvince"));
            if(MobilePhoneProvinceCompareACityProvince)
            {
                flowData.put("MobilePhoneProvinceCompareACityProvince","T");
            }else
            {
                flowData.put("MobilePhoneProvinceCompareACityProvince","F");
            }

            String uid = getValue(dataFact.userInfo,"Uid");
            //该uid在最近一年内的第一次订单时间
            try
            {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                String timeLimitStr = format.format(date);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(calendar.MINUTE, -525600);//往前525600分钟
                String startTimeStr = format.format(calendar.getTime());
                String firstOrderStr = flightSources.getUidOrderDate(uid,startTimeStr,timeLimitStr);//fixme 这里提取的日期应该是.net的日期格式 要改
                firstOrderStr = firstOrderStr.replace("T"," ");
                String currentOrderStr = getValue(dataFact.mainInfo,"OrderDate");
                Date firstOrder = format.parse(firstOrderStr);
                Date currentOrder = format.parse(currentOrderStr);
                flowData.put("orderDateToOrderDate1YByUid",getDateAbs(firstOrder,currentOrder,1));

                // 本单金额与7天内均值消费金额之间的差值的绝对值
                calendar.add(calendar.MINUTE, -10080);//往前10080分钟
                String startTimeStr1 = format.format(calendar.getTime());
                String avgAmount7 = flightSources.getAvgAmount7(getValue(flowData,"CCardNoCode"),startTimeStr1,timeLimitStr);
                long avgAmount = Long.parseLong(avgAmount7);
                long currentAmount = Long.parseLong(getValue(dataFact.mainInfo,"Amount"));
                flowData.put("AmountToAvgAmount7",currentAmount-avgAmount);
            }catch (Exception exp)
            {
                flowData.put("orderDateToOrderDate1YByUid","-1");
            }

            flowData.put("uidIPCityLastYear","T");


            if(dataFact.userInfo != null && !uid.isEmpty())
            {
                if(uid.length()>=10)
                {
                    flowData.put("UID3To7",uid.substring(2,9));
                    flowData.put("Uid1",uid.substring(0,1));
                }
            }

            flowData.put("TakeOffToOrderDate",getValue(dataFact.otherInfo,"TakeOffToOrderDate"));
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
            if(data.get(Common.ReqID)!=null)
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
        }catch (Exception exp)
        {
            fault();
            logger.error("invoke FlightExecutor.executeFlight fault.",exp);
        }finally
        {
            afterInvoke("FlightExecutor.executeFlight");
        }
        return checkFact;
    }


    /**
     * 计算机票利润
     * @param data
     */
    public void fillFightsOrderProfit(Map data,DataFact dataFact)
    {
        List<Map<String,Object>> paymentInfoList = (List<Map<String,Object>>)data.get(Common.PaymentInfos);
        if(paymentInfoList == null || paymentInfoList.size()<1)
            return;

        Map flightsInfo = getValueMap(dataFact.productInfoM,Flight.FlightsOrderInfo);
        if(flightsInfo == null || flightsInfo.size()<1)
            return;
        for(Map payInfo : paymentInfoList)
        {
            if(!getValue(payInfo,"TMPAY").equals("TMPAY"))
            {
                continue;
            }
            ////利润=（机票售价-票面底价）*人数+绑定产品（礼品卡，租车券）*0.7*人数+保险*0.9-机票中礼品卡支付的部分*0.01
            //新公式：利润=（机票卖价-票面底价）+绑定产品（礼品卡，租车券）*0.7+保险*0.9-机票中礼品卡支付的部分*0.01
            String flightPrice = getValue(flightsInfo,"Flightprice");
            String flightCost = getValue(flightsInfo,"FlightCost");
            String packageAttachFee = getValue(flightsInfo,"PackageAttachFee");
            String insurance_fee = getValue(flightsInfo,"Insurance_fee");
            if(Integer.parseInt(flightPrice) < Integer.parseInt(flightCost))
                continue;

            Map subPaymentInfo = (Map)payInfo.get(Common.PaymentInfo);
            String amount = getValue(subPaymentInfo,Common.Amount);
            double profit = (Long.parseLong(flightPrice)-Long.parseLong(flightCost))+Long.parseLong(packageAttachFee)*0.7+
                    Long.parseLong(insurance_fee)*0.9-Long.parseLong(amount)*0.01;
            flightsInfo.put("Profit", profit);//添加利润值
        }
    }

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
        //起飞日期
        String takeOffDateStr = getValue(data,Common.TakeOffTime);
        Date takeOffDate = DateUtils.parseDate(takeOffDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        dataFact.otherInfo.put(Common.TakeOffToOrderDate,getDateAbs(takeOffDate, orderDate,1));
    }

    public void getOtherInfo1(DataFact dataFact,Map data)
    {
        String reqIdStr = getValue(data,Common.ReqID);
        Map otherInfo = commonSources.getOtherInfo(reqIdStr);
        if(otherInfo != null && otherInfo.size()>0)
            dataFact.otherInfo.putAll(otherInfo);
    }

/**
     * 获取机票产品信息当checkType是0或1的时候
     * @param dataFact
     * @param data*/
    public void getFlightProductInfo0(DataFact dataFact,Map data)
    {
        //机票产品
        Map<String,Object> flightsOrderInfo = new HashMap();
        flightsOrderInfo.put(Flight.DAirPort,getValue(data,Flight.DAirPort));
        flightsOrderInfo.put(Flight.EAirPort,getValue(data,Flight.EAirPort));
        flightsOrderInfo.put(Flight.AAirPort,getValue(data,Flight.AAirPort));
        flightsOrderInfo.put(Flight.DCity,getValue(data,Flight.DCity));
        flightsOrderInfo.put(Flight.ACity,getValue(data,Flight.ACity));
        flightsOrderInfo.put(Flight.FlightClass,getValue(data,Flight.FlightClass));
        flightsOrderInfo.put(Flight.IsClient,getValue(data,Flight.IsClient));
        flightsOrderInfo.put(Flight.SubOrderType,getValue(data,Flight.SubOrderType));
        flightsOrderInfo.put(Flight.TakeOffTime,getValue(data,Flight.TakeOffTime));
        flightsOrderInfo.put(Flight.Remark,getValue(data,Flight.Remark));
        flightsOrderInfo.put(Flight.SalesType,getValue(data,Flight.SalesType));
        flightsOrderInfo.put(Flight.FlightCost,getValue(data,Flight.FlightCost));
        flightsOrderInfo.put(Flight.InsuranceCost,getValue(data,Flight.InsuranceCost));
        flightsOrderInfo.put(Flight.AgencyName,getValue(data,Flight.AgencyName));
        flightsOrderInfo.put(Flight.Agencyid,getValue(data,Flight.Agencyid));

        flightsOrderInfo.put(Flight.FlightCostRate,getValue(data,Flight.FlightCostRate));
        flightsOrderInfo.put(Flight.Insurance_fee,getValue(data,Flight.Insurance_fee));
        flightsOrderInfo.put(Flight.Flightprice,getValue(data,Flight.Flightprice));
        flightsOrderInfo.put(Flight.PackageAttachFee,getValue(data,Flight.PackageAttachFee));

        flightsOrderInfo.put(Flight.Persons,getValue(data,Flight.Persons));
        flightsOrderInfo.put(Flight.Tot_Oilfee,getValue(data,Flight.Tot_Oilfee));

        flightsOrderInfo.put(Flight.Tot_Tax,getValue(data,Flight.Tot_Tax));
        flightsOrderInfo.put(Flight.UrgencyLevel,getValue(data,Flight.UrgencyLevel));

        //乘客
        List<Map<String,Object>> newPassengerInfoList = new ArrayList<Map<String, Object>>();
        Object passengerInfoOb = data.get("PassengerInfoList");
        if(passengerInfoOb != null)
        {
            String passengerInfoStr = Json.toPrettyJSONString(passengerInfoOb);//fixme 调试这里的代码
            List<Map> passengerInfoList = Json.parseObject(passengerInfoStr,List.class);
            if(passengerInfoList != null && passengerInfoList.size()>0)
            {
                for(Map pInfo : passengerInfoList)
                {
                    Map<String,Object> passengerInfo = new HashMap();
                    passengerInfo.put(Flight.PassengerBirthday,getValue(pInfo,Flight.PassengerBirthday));
                    passengerInfo.put(Flight.PassengerCardID,getValue(pInfo,Flight.PassengerCardID));
                    passengerInfo.put(Flight.PassengerGender,getValue(pInfo,Flight.PassengerGender));
                    passengerInfo.put(Flight.PassengerName,getValue(pInfo,Flight.PassengerName));

                    passengerInfo.put(Flight.PassengerNationality,getValue(pInfo,Flight.PassengerNationality));
                    passengerInfo.put(Flight.PassengerCardIDType,getValue(pInfo,Flight.PassengerCardIDType));

                    passengerInfo.put(Flight.PassengerAgeType,getValue(pInfo,Flight.PassengerAgeType));
                    passengerInfo.put(Flight.PassengerGender,getValue(pInfo,Flight.PassengerGender));

                    newPassengerInfoList.add(passengerInfo);
                }
            }
        }
        //航程段
        List<Map<String,Object>> newSegmentInfoList = new ArrayList<Map<String, Object>>();
        Object segmentInfoOb = data.get("SegmentInfoList");
        if(passengerInfoOb != null)
        {
            String segmentInfoStr = Json.toPrettyJSONString(segmentInfoOb);//fixme 调试这里的代码
            List<Map> segmentInfoList = Json.parseObject(segmentInfoStr,List.class);
            if(segmentInfoList != null && segmentInfoList.size()>0)
            {
                for(Map sInfo : segmentInfoList)
                {
                    Map segmentInfo = new HashMap();
                    segmentInfo.put(Flight.AAirPort,getValue(sInfo,Flight.AAirPort));
                    segmentInfo.put(Flight.ACity,getValue(sInfo,getCityCodeByAirPort(Flight.AAirPort)));
                    segmentInfo.put(Flight.DAirPort,getValue(sInfo,Flight.DAirPort));
                    segmentInfo.put(Flight.DCity,getValue(sInfo,getCityCodeByAirPort(Flight.DAirPort)));
                    segmentInfo.put("SeatClass",getValue(sInfo,"SeatClass"));
                    segmentInfo.put("Sequence",getValue(sInfo,"Sequence"));
                    segmentInfo.put("SubClass",getValue(sInfo,"SubClass"));
                    segmentInfo.put("Takeofftime",getValue(sInfo,"Takeofftime"));
                    segmentInfo.put("Arrivaltime",getValue(sInfo,"Arrivaltime"));
                    newSegmentInfoList.add(segmentInfo);
                }
            }
        }
        dataFact.productInfoM.put(Flight.FlightsOrderInfo,flightsOrderInfo);//FlightsOrderInfo  PassengerInfoList  SegmentInfoList
        dataFact.productInfoM.put(Flight.PassengerInfoList,newPassengerInfoList);
        dataFact.productInfoM.put(Flight.SegmentInfoList,newSegmentInfoList);
    }

    public void getFlightProductInfo1(DataFact dataFact,Map data)
    {
        //通过lastReqID查询所有订单相关的信息 注意这里是上一次的reqId(当checkType=1的时候)
        String reqIdStr = getValue(data,Common.OldReqID);
        if(reqIdStr.isEmpty())
            return;
        try{
            Map flightsOrderInfo = flightSources.getFlightsOrderInfo(reqIdStr);
            String flightsOrderID = "";
            if(flightsOrderInfo != null && flightsOrderInfo.size()>0)
            {
                dataFact.productInfoM.put(Flight.FlightsOrderInfo, flightsOrderInfo);
                flightsOrderID = getValue(flightsOrderInfo,"FlightsOrderID");
            }

            if(!flightsOrderID.isEmpty())
            {
                List<Map<String,Object>> passengerInfo = flightSources.getPassengerInfo(flightsOrderID);
                if(passengerInfo != null && passengerInfo.size()>0)
                    dataFact.productInfoM.put(Flight.PassengerInfoList,flightsOrderInfo);
                List<Map<String,Object>> segmentInfo = flightSources.getSegmentInfo(flightsOrderID);
                if(segmentInfo != null && segmentInfo.size()>0)
                    dataFact.productInfoM.put(Flight.SegmentInfoList,flightsOrderInfo);
            }
        }catch (Exception exp)
        {
            logger.warn("获取FlightsOrderInfo异常:",exp);
        }
    }

    public String getCityCodeByAirPort(String airPort)
    {
        if(CacheFlowRuleData.airPortCache.containsKey(airPort))
        {
            return getValue(CacheFlowRuleData.airPortCache,airPort);
        }
        else//从数据库读取数据并写到缓存中去
        {
            String cityCode = flightSources.getCityCode(airPort);
            CacheFlowRuleData.airPortCache.put(airPort,cityCode);
            return cityCode;
        }
    }
}

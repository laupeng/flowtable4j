package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.common.MyJSON;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Flight;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

import static com.ctrip.infosec.flowtable4j.translate.common.IpConvert.ipConvertTo10;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.Json;
/**
 * Created by lpxie on 15-3-31.
 * 两个模块：1，数据补充。2，转换成实体
 */
@Service
public class FlightExecutor implements Executor
{
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
    FlightSources flightSources;

    /**
     * 补充订单信息
     * @param data
     * @param checkType
     * @return 返回处理后的结果
     */
    public Map complementData(Map<String,Object> data,int checkType)
    {
        //当数据进来的时候先执行规则引擎的数据标准化
//        rulesExecutorService.executeSyncRules();
        //携程内订单验证类型(0--默认（产品+支付），1--产品信息校验，2--支付信息校验)

        //判断异常的CheckType
        if(checkType<=0)
        {
            checkType = 0;
        }
        //添加默认信息
        data.put(Flight.LastCheck,"T");//FIXME 这里咨询徐洪LastCheck字段的含义

        if(checkType == 0 || checkType == 1)
        {
            //补充手机对应的城和省（MobilePhone）
            fillMobilePhoneInfo(data);
            //补充用户信息(uid)
            // 这里是通过调用ESB来获取数据（先跟徐洪确认接口参数再通过cfx生成client代码直接调用）
            //这里的数据是从DataProxy里面读取的，这里跟徐洪确认
            fillUidInfo(data);
            //补充IP信息 先转换成10进制在通过10进制的数据查询IP对应的城市
            fillIpInfo(data);
            //补充航程段信息
            fillCityCode(data);
            //计算用户注册时间和用户预定的时间差
            try
            {
                getTimeAbs(data);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            //计算用户起飞和预定的时间差
            //TODO
        }else if(checkType == 2)
        {
            //标识过来的是支付报文,补充产品信息
            fillMainInfo(data,checkType);//Fixme 这个方法有点问题  里面的数据应该是取嵌套在里面的
        }

        //携程内订单验证类型(0--默认（产品+支付），1--产品信息校验，2--支付信息校验)
        if(checkType == 0 || checkType == 2)//补充支付信息
        {
            fillPaymentInfo(data);//2014.4.17 下周这里继续
        }else if(checkType == 1)
        {
            //补充订单支付（包括带订单支付信息和订单主要支付）信息
            fillOrderInfo(data);
        }

        //计算机票利润
        if(checkType == 0)
        {
            //补充机票订单的信息便于在后面计算利润 这个只是针对机票这个业务而言的
            fillFlightsOrderInfo(data);
        }

        //利润=（机票售价-票面底价）*人数+绑定产品（礼品卡，租车券）*0.7*人数+保险*0.9-机票中礼品卡支付的部分*0.01
        fillFightsOrderProfit(data);

        //did逻辑 添加did信息
        fillDID(data);

        //获取userProfile信息
        //TODO 读取userProfile信息  根据uid把相关的信息从userProfile service 里面取出来

        //保存当前订单信息到redis

        return data;
    }

    /**
     * 转成黑白名单实体数据
     * 内容是data的数据
     * 这里两种方案：1，新生成一个Map,2，用原来的data作为一个完整信息
     * @param data
     */
    public void convertToBWEntity(Map data)
    {
        if(data.get("PassengerInfoList") !=null)
        {
            List<Map> passengerInfoList = (List)data.get("PassengerInfoList");
            if(passengerInfoList.size()>0)
            {
                String passengerName = "";
                String PassengerNationality = "";
                String PassengerCardID = "";
                for(Map passenger : passengerInfoList)
                {
                    if(passenger.get("PassengerName")!=null)
                        passengerName += passenger.get("PassengerName") +"|";

                    if(passenger.get("PassengerNationality")!=null)
                        PassengerNationality += passenger.get("PassengerNationality") +"|";

                    if(passenger.get("PassengerCardID")!=null)
                        PassengerCardID += passenger.get("PassengerCardID") +"|";
                }
                data.put("PassengerName","|"+passengerName);
                data.put("PassengerNationality","|"+PassengerNationality);
                data.put("PassengerCardID","|"+PassengerCardID);
            }
        }


        if(data.get("PaymentInfos")!=null)
        {
            List<Map> paymentInfos = (List)data.get("PaymentInfos");
            if(paymentInfos.size()>0)
            {
                Map paymentInfo = paymentInfos.get(0);
                if(paymentInfo.get("CardInfoList")!=null)
                {
                    Map cardInfo = (Map)paymentInfo.get("CardInfoList");
                    if(cardInfo.size()>0)
                    {
                        String prepayType = paymentInfo.get("PrepayType") == null ? "" : paymentInfo.get("PrepayType").toString();
                        String isForigened = cardInfo.get("IsForigenCard") == null ? "" : cardInfo.get("IsForigenCard").toString();
                        if(prepayType.equals("CCARD") )
                        {
                            if(isForigened.equals("T"))
                            {
                                data.put("PrepayTypeDetails","W");
                            }else
                                data.put("PrepayTypeDetails","N");
                        }else if(prepayType.equals("CASH") )
                        {
                            data.put("PrepayTypeDetails","X");
                        }else if(prepayType.equals("PAYPL"))
                        {
                            data.put("PrepayTypeDetails","P");
                        }else if(prepayType.equals("DCARD") || prepayType.equals("DQPAY"))
                        {
                            data.put("PrepayTypeDetails","D");
                        }
                    }
                }
            }
        }



    }

    /**
     * 转成流量实体数据
     * 内容是data的数据
     * @param data
     */
    public void convertToFlowEntity(Map data)
    {
        Map<String,Object> flowValue = new HashMap();
        //把支付类型提取到第一层 （prepayType）
        if(data.get("PaymentInfos") != null)
        {
            List<Map> paymentInfo = (List<Map>)data.get("PaymentInfos");//FIXME 这里找徐洪确认下字段的名称和格式
            if(paymentInfo!=null && paymentInfo.size()>0)
            {
                String MergerOrderPrepayType = "|";
                for(Map payment:paymentInfo)
                {
                    String PrepayType = payment.get("PrepayType") == null ? "" : payment.get("PrepayType").toString();
                    MergerOrderPrepayType += PrepayType+"|";
                    if(PrepayType.equals("CCARD") || PrepayType.equals("DCARD") ||PrepayType.equals("DQPAY"))
                    {
                        List<Map> cardInfos = payment.get("CreditCardInfo") == null ? null : (List<Map>)payment.get("CreditCardInfo");
                        flowValue.put("CValidityCode",cardInfos.get(0).get("CValidityCode"));
                        flowValue.put("IsForigenCard",cardInfos.get(0).get("IsForigenCard"));
                        flowValue.put("CCardPreNoCode",cardInfos.get(0).get("CCardPreNoCode"));
                        flowValue.put("CardBinIssue",cardInfos.get(0).get("CardBinIssue"));
                        flowValue.put("CreditCardType",cardInfos.get(0).get("CreditCardType"));
                        flowValue.put("CardHolder",cardInfos.get(0).get("CardHolder"));
                        flowValue.put("CardBin",cardInfos.get(0).get("CardBin"));
                    }else if(PrepayType.equals("TMPAY"))
                    {
                        flowValue.put("TmpayAmount",payment.get("Amount"));
                    }

                }
                flowValue.put("MergerOrderPrepayType",MergerOrderPrepayType);//FIXME 这里和原来的.net逻辑不一样
            }
        }

        //
        if(data.get("FlightsInfo")!=null && data.get("FlightsOrderInfo")!=null)
        {
            //FIXME 这里找到之前Profit字段的位置
            Map flightsInfo = (Map)data.get("FlightsInfo");
            String profit = flightsInfo.get("Profit") == null ? "" : flightsInfo.get("Profit").toString();
            String flightCostRate = flightsInfo.get("FlightCostRate") == null ? "" : flightsInfo.get("FlightCostRate").toString();
            flowValue.put("Profit",profit);
            flowValue.put("Profit",flightCostRate);
            String agencyName = flightsInfo.get("AgencyName") == null ? "" : flightsInfo.get("AgencyName").toString();
            if(!agencyName.isEmpty())
            {
                flowValue.put("AgencyName",agencyName);
            }
        }

        //
        String CusCharacter = data.get("CusCharacter") == null ? "" : data.get("CusCharacter").toString();
        String VipGrade = data.get("VipGrade") == null ? "" : data.get("VipGrade").toString();
        String Uid = data.get("Uid") == null ? "" : data.get("Uid").toString();
        String IsTempUser = data.get("IsTempUser") == null ? "" : data.get("IsTempUser").toString();
        String Experience = data.get("Experience") == null ? "" : data.get("Experience").toString();
        String BindedEmail = data.get("BindedEmail") == null ? "" : data.get("BindedEmail").toString();
        String UserPassword = data.get("UserPassword") == null ? "" : data.get("UserPassword").toString();
        flowValue.put("CusCharacter",CusCharacter);
        flowValue.put("VipGrade",VipGrade);
        flowValue.put("Uid",Uid);
        flowValue.put("IsTempUser",IsTempUser);
        flowValue.put("Experience",Experience);
        flowValue.put("BindedEmail",BindedEmail);
        flowValue.put("UserPassword",UserPassword);

        //
        String RelatedMobilephone = data.get("RelatedMobilephone") == null ? "" : data.get("RelatedMobilephone").toString();
        if(!RelatedMobilephone.isEmpty())
        {
            flowValue.put("RelatedMobilephone",RelatedMobilephone);
            if(RelatedMobilephone.length()>=7)
            {
                String moblieNumber = RelatedMobilephone.substring(0,7);
                //...
                String MobilePhoneCity = data.get("MobilePhoneCity") == null ? "" : data.get("MobilePhoneCity").toString();
                String MobilePhoneProvince = data.get("MobilePhoneProvince") == null ? "" : data.get("MobilePhoneProvince").toString();

                flowValue.put("RelatedMobilePhoneCity",MobilePhoneCity);
                flowValue.put("RelatedMobilePhoneProvince",MobilePhoneProvince);
            }
        }

        //
        String BindedMobilePhone = data.get("BindedMobilePhone") == null ? "" : data.get("BindedMobilePhone").toString();
        if(!BindedMobilePhone.isEmpty())
        {
            flowValue.put("BindedMobilePhone",BindedMobilePhone);
            if(RelatedMobilephone.length()>=7)
            {
                String moblieNumber = BindedMobilePhone.substring(0,7);
                //...
                Map binded = commonSources.getCityAndProv(BindedMobilePhone);//CityName 和 ProvinceName
                String MobilePhoneCity = binded.get("CityName") == null ? "" : binded.get("CityName").toString();
                String MobilePhoneProvince = binded.get("ProvinceName") == null ? "" : binded.get("ProvinceName").toString();

                flowValue.put("MobilePhoneCity",MobilePhoneCity);
                flowValue.put("MobilePhoneProvince",MobilePhoneProvince);
            }
        }

        //
        String RelatedEMail = data.get("RelatedEMail") == null ? "" : data.get("RelatedEMail").toString();
        flowValue.put("RelatedEMail",RelatedEMail);

        //
        String IsClient = data.get("IsClient") == null ? "" : data.get("IsClient").toString();
        String FlightClass = data.get("FlightClass") == null ? "" : data.get("FlightClass").toString();
        String DAirPort = data.get("DAirPort") == null ? "" : data.get("DAirPort").toString();
        String AAirPort = data.get("AAirPort") == null ? "" : data.get("AAirPort").toString();
        String EAirPort = data.get("EAirPort") == null ? "" : data.get("EAirPort").toString();
        String Remark = data.get("Remark") == null ? "" : data.get("Remark").toString();
        flowValue.put("IsClient",IsClient);
        flowValue.put("FlightClass",FlightClass);
        flowValue.put("DAirPort",DAirPort);
        flowValue.put("AAirPort",AAirPort);
        flowValue.put("EAirPort",EAirPort);
        flowValue.put("Remark",Remark);

        //
        String OrderToSignUpDate = data.get("OrderToSignUpDate") == null ? "" : data.get("OrderToSignUpDate").toString();
        String TakeOffToOrderDate = data.get("TakeOffToOrderDate") == null ? "" : data.get("TakeOffToOrderDate").toString();
        flowValue.put("TakeOffToOrderDate",TakeOffToOrderDate);
        flowValue.put("OrderToSignUpDate",OrderToSignUpDate);

        //
        String OrderId = data.get("OrderId") == null ? "" : data.get("OrderId").toString();
        String Amount = data.get("Amount") == null ? "" : data.get("Amount").toString();
        String ClientID = data.get("ClientID") == null ? "" : data.get("ClientID").toString();
        String OrderDate = data.get("OrderDate") == null ? "" : data.get("OrderDate").toString();
        String OrderDateHour = data.get("OrderDateHour") == null ? "" : data.get("OrderDateHour").toString();
        flowValue.put("OrderId",OrderId);
        flowValue.put("Amount",Amount);
        flowValue.put("ClientID",ClientID);
        flowValue.put("OrderDate",OrderDate);
        flowValue.put("OrderDateHour",OrderDateHour);

        //
        String IsOnline = data.get("IsOnline") == null ? "" : data.get("IsOnline").toString();
        String Serverfrom = data.get("Serverfrom") == null ? "" : data.get("Serverfrom").toString();
        String WirelessClientNo = data.get("WirelessClientNo") == null ? "" : data.get("WirelessClientNo").toString();
        flowValue.put("IsOnline",IsOnline);
        flowValue.put("Serverfrom",Serverfrom);
        flowValue.put("WirelessClientNo",WirelessClientNo);

        //
        String MobilePhone = data.get("MobilePhone") == null ? "" : data.get("MobilePhone").toString();
        String MobilePhone4Count = data.get("MobilePhone4Count") == null ? "" : data.get("MobilePhone4Count").toString();
        String MobilePhoneCity = data.get("MobilePhoneCity") == null ? "" : data.get("MobilePhoneCity").toString();
        String MobilePhoneProvince = data.get("MobilePhoneProvince") == null ? "" : data.get("MobilePhoneProvince").toString();
        String ContactEMail = data.get("ContactEMail") == null ? "" : data.get("ContactEMail").toString();
        String ContactName = data.get("ContactName") == null ? "" : data.get("ContactName").toString();
        String ContactTel = data.get("ContactTel") == null ? "" : data.get("ContactTel").toString();
        String ForignMobilePhone = data.get("ForignMobilePhone") == null ? "" : data.get("ForignMobilePhone").toString();
        String TelCall = data.get("TelCall") == null ? "" : data.get("TelCall").toString();
        String SendTickerAddr = data.get("SendTickerAddr") == null ? "" : data.get("SendTickerAddr").toString();
        String PostAddress = data.get("PostAddress") == null ? "" : data.get("PostAddress").toString();
        flowValue.put("MobilePhone",MobilePhone);
        flowValue.put("MobilePhone4Count",MobilePhone4Count);
        flowValue.put("MobilePhoneCity",MobilePhoneCity);
        flowValue.put("MobilePhoneProvince",MobilePhoneProvince);
        flowValue.put("ContactEMail",ContactEMail);
        flowValue.put("ContactName",ContactName);
        flowValue.put("ContactTel",ContactTel);
        flowValue.put("ForignMobilePhone",ForignMobilePhone);
        flowValue.put("TelCall",TelCall);
        flowValue.put("SendTickerAddr",SendTickerAddr);
        flowValue.put("PostAddress",PostAddress);

        //
        String UserIPValue = data.get("UserIPValue") == null ? "" : data.get("UserIPValue").toString();
        String IPCountry = data.get("IPCountry") == null ? "" : data.get("IPCountry").toString();
        String Continent = data.get("Continent") == null ? "" : data.get("Continent").toString();
        String IPCity = data.get("IPCity") == null ? "" : data.get("IPCity").toString();
        String UserIPAdd = data.get("UserIPAdd") == null ? "" : data.get("UserIPAdd").toString();
        flowValue.put("UserIPValue",UserIPValue);
        flowValue.put("IPCountry",IPCountry);
        flowValue.put("Continent",Continent);
        flowValue.put("IPCity",IPCity);
        flowValue.put("UserIPAdd",UserIPAdd);

        String IPCityName = data.get("IPCityName") == null ? "" : data.get("IPCityName").toString();
        String IPProvince = data.get("IPProvince") == null ? "" : data.get("IPProvince").toString();
        flowValue.put("IPCityName",IPCityName);
        flowValue.put("IPProvince",IPProvince);

        //
        String DID = data.get("DID") == null ? "" : data.get("DID").toString();
        flowValue.put("DID",DID);

        //
        List<Map> segmentInfos = (List<Map>)data.get("SegmentInfoList");
        if(segmentInfos != null && segmentInfos.size()>0)
        {
            for(Map segmentInfo : segmentInfos)
            {
                //FIXME 这里添加Count字段到流量实体里面 咨询徐洪（flowValue.put("SegmentInfoCount",data.get("Count"))）
                String SeatClass = segmentInfo.get("SeatClass") == null ? "" : segmentInfo.get("SeatClass").toString();
                String Sequence = segmentInfo.get("Sequence") == null ? "" : segmentInfo.get("Sequence").toString();
                flowValue.put("SeatClass",SeatClass);
                flowValue.put("Sequence",Sequence);
                //FIXME 这里只取第一个值 不知道问什么 咨询张应洪
                break;
            }
        }

        //
        String mobilePhone =  data.get("MobilePhone") == null ? "" : data.get("MobilePhone").toString();
        flowValue.put("UIDMobileNumber",Uid+mobilePhone.substring(0,7));
        flowValue.put("UidMergerMobilePhone7",Uid+mobilePhone.substring(0,7));

        //
        String CardBin =  data.get("CardBin") == null ? "" : data.get("CardBin").toString();
        flowValue.put("MobilePhoneCardBin",mobilePhone+CardBin);

        //
        String usrIp = data.get("UserIPAdd") == null ? "" : data.get("UserIPAdd").toString();
        flowValue.put("UserIPAddMergerMobilePhone7",usrIp+mobilePhone.substring(0,7));
        flowValue.put("UserIPAddCardBin",usrIp+CardBin);
        /*以上都是原生字段的处理*/

        /*下面是衍生字段的处理*/
        String ACityName = data.get("ACityName") == null ? "" : data.get("ACityName").toString();
        String DCityName = data.get("DCityName") == null ? "" : data.get("DCityName").toString();
        String ACityProvince = data.get("ACityProvince") == null ? "" : data.get("ACityProvince").toString();
        String DCityProvince = data.get("DCityProvince") == null ? "" : data.get("DCityProvince").toString();
        String ACountryCode = data.get("ACountryCode") == null ? "" : data.get("ACountryCode").toString();
        String DCountryCode = data.get("DCountryCode") == null ? "" : data.get("DCountryCode").toString();
        String Agencyid = data.get("Agencyid") == null ? ""  : data.get("Agencyid").toString();
        String Insurance_fee = data.get("Insurance_fee") == null ? ""  : data.get("Insurance_fee").toString();
        flowValue.put("ACityName",ACityName);
        flowValue.put("DCityName",DCityName);
        flowValue.put("ACityProvince",ACityProvince);
        flowValue.put("DCityProvince",DCityProvince);
        flowValue.put("ACountryCode",ACountryCode);
        flowValue.put("DCountryCode",DCountryCode);
        flowValue.put("Agencyid",Agencyid);
        flowValue.put("Insurance_fee",Insurance_fee);

        //
        //String IPProvince = data.get("IPProvince") == null ? ""  : data.get("IPProvince").toString();
        flowValue.put("IPProvince",IPProvince);

        //
        //String ContactEMail = data.get("ContactEMail") == null ? ""  : data.get("ContactEMail").toString();
        String TmpContactEMail = ContactEMail.replace(".","").replace("_","").replace("@","");
        flowValue.put("ContactEMailToConvert7",TmpContactEMail.substring(0,7));

        //UID从第三位开始取7位
        if(Uid.length()>=10)
        {
            flowValue.put("UID3To7",Uid.substring(2,7));
            flowValue.put("Uid1", Uid.substring(0, 1));

        }
        //
        flowValue.put("DID",DID);

        //
        List<Map> PassengerInfoList = (List<Map>)data.get("PassengerInfoList");
        if(PassengerInfoList != null && PassengerInfoList.size()>0)
        {
            String PassengerName = "|";
            String PassengerNationality = "|";
            String PassengerCardID = "|";
            String PassengerCardIDLength = "|";
            String MergerPassengerCardIDLength = "|";
            PassengerCardIDLength = "F";
            Map passengerCards = new HashMap();
            List<Map> mapList = new ArrayList<Map>();
            for(Map passengerInfo : PassengerInfoList)
            {
                PassengerName += passengerInfo.get("PassengerName") + "|";
                PassengerNationality += passengerInfo.get("PassengerNationality") + "|";     //	登机人国籍
                PassengerCardID += passengerInfo.get("PassengerCardID") + "|";
                MergerPassengerCardIDLength += passengerInfo.get("PassengerCardID").toString().length() + "|";
                passengerCards.put("before3",passengerInfo.get("PassengerCardID").toString().substring(0,3));
                passengerCards.put("before6",passengerInfo.get("PassengerCardID").toString().substring(0,6));
                if(PassengerCardIDLength.equals("F"))
                {
                    if(PassengerCardID.length() != 15 && PassengerCardID.length() != 18)
                    {
                        PassengerCardIDLength = "T";
                    }
                }
                Map passengers = new HashMap();
                passengers.put("PassengerName",passengerInfo.get("PassengerName"));
                passengers.put("PassengerNationality",passengerInfo.get("PassengerNationality"));
                passengers.put("PassengerCardID",passengerInfo.get("PassengerCardID"));

                passengers.put("PassengerNameCardID",PassengerName+PassengerCardID);
                passengers.put("PassengerCardID6",PassengerCardID.substring(0,6));
                passengers.put("PassengerCardIDLengthOne",PassengerCardID.length());

                String IDCardNumberProvinceName = "";
                String IDCardNumberProvinceNameToDACity = "";

                long i = Long.parseLong(PassengerCardID.substring(0,6));
                //TODO 通过十进制的乘客号码获取用户对应的 省 市
                if(!flowValue.get("IDCardNumberProvinceNameToMobilePhone").toString().equals("T"))
                {
                    flowValue.put("IDCardNumberProvinceNameToMobilePhone",IDCardNumberProvinceName);
                }
                if(!flowValue.get("IDCardNumberProvinceNameToDACity").toString().equals("T"))
                {
                    flowValue.put("IDCardNumberProvinceNameToDACity",IDCardNumberProvinceNameToDACity);
                }
                if(data.get("Uid") != null)
                {
                    passengers.put("UidPassengerName",PassengerName+data.get("Uid"));
                    passengers.put("UidPassengerNameCardID",data.get("Uid")+PassengerCardID);
                }
                if(data.get("MobilePhone")!=null)
                    passengers.put("MobilePhonePassengerCardID",data.get("MobilePhone")+PassengerCardID);
                if(data.get("ContactEMail") != null)
                    passengers.put("EMailPassengerNameCardID",data.get("ContactEMail")+PassengerCardID);
                if(flowValue.get("CCardNoCode") != null)
                    passengers.put("CCardNoCodePassengerNameCardID",flowValue.get("CCardNoCode")+PassengerCardID);

                if(flowValue.get("IsSamePassengerNationality").equals("T"))
                {
                    //Todo  判断多个乘机人国籍是否相同
                }
                mapList.add(passengers);
            }
            flowValue.put("PassengerList",mapList);
            if(Integer.parseInt(flowValue.get("PassengerCount").toString()) > 0)
            {
                flowValue.put("LeafletAmount",Integer.parseInt(data.get("Amount").toString())/Integer.parseInt(flowValue.get("PassengerCount").toString()));
            }

            flowValue.put("MergerPassengerName",PassengerName.toUpperCase());
            flowValue.put("MergerPassengerNationality",PassengerNationality.toUpperCase());
            flowValue.put("MergerPassengerCardID",PassengerCardID.toUpperCase());
            flowValue.put("PassengerCardIDLength",PassengerCardIDLength);
            flowValue.put("MergerPassengerCardIDLength",MergerPassengerCardIDLength);
            flowValue.put("DCountPassengerCardID3",passengerCards.get("before3"));
            flowValue.put("DCountPassengerCardID6",passengerCards.get("before6"));


        }
        //这里判断支付方式是TMPAY的时候会计算负利润
        if(data.get("PaymentInfos") != null)
        {
            List<Map> paymentInfo = (List<Map>)data.get("PaymentInfos");//FIXME 这里找徐洪确认下字段的名称和格式
            if(paymentInfo!=null && paymentInfo.size()>0)
            {
                for(Map payment:paymentInfo)
                {
                    if(payment.get("PrepayType") != null && payment.get("PrepayType").toString().equals("TMPAY"))
                    {
                        //TODO 计算负利润
                    }
                }
            }
        }

        //同一手机，相同卡号前12位  同一Email，相同卡号前12位  同一UID，相同卡号前12位
        if(flowValue.get("CCardPreNoCode") != null)
        {
            if(data.get("MobilePhone") != null)
            {
                flowValue.put("CCardPreNoCodeMobilePhone",flowValue.get("CCardPreNoCode").toString()+data.get("MobilePhone"));
                flowValue.put("CCardNoCodeMobilePhone7",flowValue.get("CCardPreNoCode").toString()+data.get("MobilePhone").toString().substring(0,7));
            }
            if(data.get("ContactEMail") != null)
            {
                flowValue.put("CCardPreNoCodeContactEMail",flowValue.get("CCardPreNoCode").toString()+data.get("ContactEMail"));
            }

            if(data.get("Uid") != null)
            {
                flowValue.put("CCardPreNoCodeUid",flowValue.get("CCardPreNoCode").toString() + data.get("Uid"));
            }
        }

        if(flowValue.get("CardBin") != null)
        {
            flowValue.put("CardBinOrderID",flowValue.get("CardBin").toString()+data.get("OrderId"));
            flowValue.put("CardBinMobilePhone",flowValue.get("CardBin").toString()+data.get("MobilePhone"));
            flowValue.put("ContactEMailCardBin",data.get("ContactEMail")+flowValue.get("CardBin").toString());
            flowValue.put("CardBinUID",flowValue.get("CardBin").toString()+data.get("Uid"));
        }

        //同一手机号，同一邮箱
        flowValue.put("MobilePhoneContactEMail",data.get("MobilePhone").toString() + data.get("ContactEMail"));
        flowValue.put("MobilePhoneUID",data.get("MobilePhone").toString() + data.get("Uid"));
        flowValue.put("UidUserIPValue",data.get("Uid").toString() + data.get("UserIPValue"));
        flowValue.put("CCardNoCodeContactEMailUserIPValue",flowValue.get("CCardNoCode").toString()+data.get("ContactEMail")+data.get("UserIPValue"));
        flowValue.put("DAirPortAAirPort",data.get("DAirPort").toString() + data.get("AAirPort"));
        flowValue.put("ContactEMailMergerMobilePhone7",data.get("ContactEMail").toString() + data.get("MobilePhone").toString().substring(0,7));
        /*以上是处理衍生字段*/

        //ip和city转换
        if(flowValue.get("DCityProvince") != null)
        {
            if(flowValue.get("IPProvince") != null)
            {
                //通过枚举：EQ: 等于 NE: 不等于 IN：存在 NA：不存在 LLike: 右边% RLike: 左边% GE:大
                /// 于等于 LE:小于等于 Less:小于 Great:大于 外部整理成EQ或者NE[FEQ: 字段相等 FNE: 字段不等 FIN:字段存在 FNA：字段不存在
                //来对比结果
                //TODO 这里想把结果配置在规则里面
            }
        }

        //该uid在最近一年内的第一次订单时间
        if(data.get("Uid") != null)
        {
            String myTmpUid = data.get("Uid").toString();
            //Todo 从RiskCtrlPreProcDB表里查询数据
            flowValue.put("orderDateToOrderDate1YByUid",-1);//替换-1值
        }
        //第一次IP城市,
        if(data.get("IPCity") != null)
        {
            flowValue.put("uidIPCityLastYear","F");
            //Fixme 这里老的系统逻辑有问题
        }
        //第一次订单金额
        //Todo 从数据库表里查询

        //
        flowValue.put("ReqId",data.get("ReqId"));

        //统计分值大于195的数据
        //Todo 从数据库里面查询

        //检查userProfile数据


    }
    /**
     * 补充手机对应的城市和省
     * @param data
     */
    public void fillMobilePhoneInfo(Map data)
    {
        String mobilePhone = data.get(Flight.MobilePhone) == null ? "" : data.get(Flight.MobilePhone).toString();
        if(mobilePhone.length()<=6)
            return;
        Map mobileInfo = commonSources.getCityAndProv(mobilePhone);
        if(mobileInfo == null || mobileInfo.size()<1)
        {
            return;
        }
        String CityName = mobileInfo.get("CityName") == null ? "" : mobileInfo.get("CityName").toString();
        String ProvinceName = mobileInfo.get("ProvinceName") == null ? "" : mobileInfo.get("ProvinceName").toString();
        data.put(Flight.MobilePhoneCity,CityName);
        data.put(Flight.MobilePhoneProvince,ProvinceName);
    }

    public void fillUidInfo(Map data)
    {
        //从DataProxy获取数据 根据Uid取出用户的信息
        String uid = data.get(Flight.Uid)==null?"":data.get(Flight.Uid).toString();
        Map params = new HashMap();
        params.put("uid",uid);
        Map uidInfo = DataProxySources.queryForMap("CRMService","getMemberInfo",params);
        String BindedEmail = uidInfo.get(Flight.BindedEmail)==null?"":uidInfo.get(Flight.BindedEmail).toString();
        String BindedMobilePhone = uidInfo.get(Flight.BindedMobilePhone)==null?"":uidInfo.get(Flight.BindedMobilePhone).toString();
        String RelatedEMail = uidInfo.get(Flight.RelatedEMail)==null?"":uidInfo.get(Flight.RelatedEMail).toString();
        String RelatedMobilephone = uidInfo.get(Flight.RelatedMobilephone)==null?"":uidInfo.get(Flight.RelatedMobilephone).toString();
        data.put(Flight.BindedEmail,BindedEmail);
        data.put(Flight.BindedMobilePhone,BindedMobilePhone);
        data.put(Flight.RelatedEMail,RelatedEMail);
        data.put(Flight.RelatedMobilephone,RelatedMobilephone);

        if(data.get(Flight.CusCharacter) == null || data.get(Flight.CusCharacter).toString().isEmpty())
        {
            if(!uidInfo.get("Vip").toString().equalsIgnoreCase("T"))
            {
                //这里从ESB获取CustomerInfo信息来判断用户的等级

                String requestType = "Customer.User.GetCustomerInfo";
                String xpath = "/Response/GetCustomerInfoResponse";
                StringBuffer requestXml = new StringBuffer();
                requestXml.append("<GetCustomerInfoRequest>");
                requestXml.append("<UID>");
                requestXml.append(uid+"");
                requestXml.append("</UID>");
                requestXml.append("</GetCustomerInfoRequest>");
                try
                {
                    Map customerInfo = esbSources.getResponse(requestXml.toString(),requestType,xpath);
                    if(customerInfo != null)
                    {
                        if(customerInfo.get("CustomerDate").toString().equals("0001-01-01T00:00:00"))//Fixme 这里是时间的最小值 在net里面是DateTime.MinValue
                        {
                            data.put(Flight.CusCharacter, "NEW");
                        }else
                        {
                            data.put(Flight.CusCharacter,"REPEAT");
                        }
                    }
                } catch (DocumentException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                data.put(Flight.CusCharacter,"VIP");
            }
        }
    }
    /**
     * 补充IP信息 先转换成10进制在通过10进制的数据查询IP对应的城市
     * @param data
     */
    public void fillIpInfo(Map data)
    {
        String usrIp = data.get(Flight.UserIP) == null ? "" : data.get(Flight.UserIP).toString();
        //这里添加了一个字段UserIPAdd //FIXME 这里问问徐洪为什么
        data.put(Flight.UserIPAdd,data.get(Flight.UserIP));
        long ipValue = ipConvertTo10(usrIp);//转成十进制
        data.put(Flight.UserIPValue,ipValue);

        Map ipInfo = flightSources.getIpCountryCity(ipValue);
        if(ipInfo == null || ipInfo.size()<=0)
            return;
        String ContinentID = ipInfo.get("ContinentID") == null ? "" : ipInfo.get("ContinentID").toString();
        String CityID = ipInfo.get("CityID") == null ? "" : ipInfo.get("CityID").toString();
        String CountryCode = ipInfo.get("CountryCode") == null ? "" : ipInfo.get("CountryCode").toString();
        data.put(Flight.Continent,ContinentID);
        data.put(Flight.IPCity,CityID);
        data.put(Flight.IPCountry,CountryCode);
    }

    /**
     * 补充航程段信息
     * 获取逻辑：用AAirPort获取对应的City的id三位码
     * 获取逻辑：用DAirPort获取对应的City的id三位码
     * @param data
     */
    public void fillCityCode(Map data)
    {
        if(data.get(Flight.SegmentInfoList) == null)
        {
            return;
        }
        List<Map> segmentInfo = (List<Map>)data.get(Flight.SegmentInfoList);

        for(Map map : segmentInfo)
        {
            //这里首先从redis获取数据如果redis没有就从数据库获取
            String aairPort = map.get(Flight.SAAirPort) == null ? "" : map.get(Flight.SAAirPort).toString();
            String dairPort = map.get(Flight.SDAirPort) == null ? "" : map.get(Flight.SDAirPort).toString();
            //TODO 从redis获取数据
            Object object = redisSources.getValue("AirportCache");
            boolean existAAirPort  = false;
            boolean existDAirPort  = false;
            if(object != null)
            {
                Map ports = Json.parseObject(object.toString(),Map.class);
                if(ports.get(aairPort) != null)
                {
                    existAAirPort = true;
                    map.put(Flight.ACity,ports.get(aairPort));
                }
                if(ports.get(dairPort) != null)
                {
                    existDAirPort = true;
                    map.put(Flight.DCity,ports.get(dairPort));
                }
            }

            Map<String,Object> airPortCache = new HashMap();
            //从数据库读取数据
            if(!existAAirPort)
            {
                int acityCode = flightSources.getCityCode(aairPort);
                map.put(Flight.ACity,acityCode);
                airPortCache.put(Flight.AAirPort,acityCode);
            }
            if(!existDAirPort)
            {
                int dcityCode = flightSources.getCityCode(dairPort);
                map.put(Flight.DCity,dcityCode);
                airPortCache.put("DAirPort",dcityCode);
            }
            String value = new MyJSON().toJSONString(airPortCache);
            //存放到redis中
            redisSources.setKeyValue("AirportCache",value);
        }
    }

    /**
     * 补充订单主信息
     * @param data
     * @param checkType
     */
    public void fillMainInfo(Map data,int checkType)
    {
        if(data.get(Flight.OrderType)==null||data.get(Flight.OrderID)==null)
            return;
        String orderType = data.get(Flight.OrderType)+"";
        String orderId = data.get(Flight.OrderID)+"";
        //读取订单信息 先从redis读取如果redis没有则从数据库读取再添加到redis
        String key = "OrderEntityInfo_"+orderType+"_"+orderId;
        String value = redisSources.getValue(key);
        if(value != null && !value.isEmpty())
        {
            Map orderEntity = Json.parseObject(value,Map.class);
            data.putAll(orderEntity);//FIXME 这里读取的数据orderEntity是什么类型 里面是否包含：MainInfo、ContactInfo 这种net类型
        }else
        {
            //从数据库读取
            if(checkType == 0)
            {
                return;
            }
            Map<String,Object> newData = new HashMap();
            Map mainInfo = flightSources.getMainInfo(orderType,orderId);
            if(mainInfo == null|mainInfo.get("ReqID")==null)
                return;
            long lastReqID = Long.parseLong(mainInfo.get("ReqID") + "");
            Map contactInfo = flightSources.getContactInfo(lastReqID);
            newData.putAll(contactInfo);
            Map userInfo = flightSources.getUserInfo(lastReqID);
            newData.putAll(userInfo);
            Map ipInfo = flightSources.getIpInfo(lastReqID);
            newData.putAll(ipInfo);
            //CtripTTDInfo
            Map flightsOrderInfo = flightSources.getFlightsOrderInfo(lastReqID);
            if(flightsOrderInfo != null )
            {
                String flightOrderID = flightsOrderInfo.get("FlightsOrderID") == null ? "" : flightsOrderInfo.get("FlightsOrderID").toString();
                int flightOrderId = Integer.parseInt(flightOrderID);
                //添加机票乘客信息
                Map passengerInfo = flightSources.getPassengerInfo(flightOrderId);
                newData.putAll(passengerInfo);
                //添加机票段信息
                Map segmentInfo = flightSources.getSegmentInfo(flightOrderId);
                newData.putAll(segmentInfo);
            }
            Map otherInfo = flightSources.getOtherInfo(lastReqID);
            newData.putAll(otherInfo);
            //corporationInfo
            Map corporationInfo = flightSources.getCorporationInfo(lastReqID);
            newData.putAll(corporationInfo);
            //AppInfo
            Map appInfo = flightSources.getAppInfo(lastReqID);
            newData.putAll(appInfo);

            data.putAll(newData);
            //这里补充一点把从数据库获取的数据存入redis
            String newValue = Json.toJSONString(newData);
            redisSources.setKeyValue(key,newValue);
        }
    }

    /**
     * 补充支付信息
     * @param data
     */
    public void fillPaymentInfo(Map data)
    {
        Object object = data.get(Flight.PaymentInfos);
        List<Map> PaymentInfo = new ArrayList<Map>();
        List<Map> paymentInfos = (List<Map>)object;
        if(paymentInfos != null && paymentInfos.size()>0)
        {
            //补充卡信息 1，通过CardInfoID从ESB里面获取 2，通过1得到的CreditCardType和CardBin从数据库里面外卡信息
            for(Map subPayment : paymentInfos)
            {
                if(!subPayment.get(Flight.PrepayType).equals("CCARD") && !subPayment.get("PrepayType").equals("DCARD") && !subPayment.get(Flight.PrepayType).equals("DQPAY"))
                {
                    continue;
                }

                int cardInfoId = subPayment.get(Flight.CardInfoID)==null ? 0 : Integer.parseInt(subPayment.get(Flight.CardInfoID).toString());
                // 从ESB获取数据
                 //从esb获取数据 根据CardInfoID取出卡的信息
                String requestType = "AccCash.CreditCard.GetCreditCardInfo";
                String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";
                StringBuffer requestXml = new StringBuffer();
                requestXml.append("<GetCreditCardInfoRequest>");
                requestXml.append("<CardInfoId>");
                requestXml.append(cardInfoId+"");
                requestXml.append("</CardInfoId>");
                requestXml.append("</GetCreditCardInfoRequest>");
                try
                {
                    Map cardInfo = esbSources.getResponse(requestXml.toString(),requestType,xpath);

                    int cardTypeId = subPayment.get(Flight.CreditCardType) == null ? 0 : Integer.parseInt(subPayment.get(Flight.CreditCardType).toString());
                    String cardBin = subPayment.get(Flight.CardBin) == null ? "" : subPayment.get(Flight.CardBin).toString();
                    Map anotherCardInfo = flightSources.getCardInfo(cardTypeId,cardBin);
                    if(cardInfo != null)
                    {
                        cardInfo.put("CardBinIssue", anotherCardInfo.get("Nationality"));
                        cardInfo.put("CardBinBankOfCardIssue",anotherCardInfo.get("BankOfCardIssue"));
                    }

                    if(cardInfo != null && cardInfo.size()>0)
                    {
                        cardInfo.putAll(cardInfo);
                    }
                    PaymentInfo.add(cardInfo);
                } catch (DocumentException e)
                {
                    e.printStackTrace();
                }
            }
            data.put(Flight.PaymentInfo,PaymentInfo);//把支付信息列表添加进源数据 //Fixme 这里充分测试下
        }else
        {
            Map subPaymentInfo = new HashMap();
            subPaymentInfo.put(Flight.Amount,data.get(Flight.Amount));
            subPaymentInfo.put(Flight.IsGuarantee,data.get(Flight.IsGuarantee));
            subPaymentInfo.put(Flight.PrepayType,data.get(Flight.PrepayType));
            //如果报文里面没有支付的信息（指没有paymentInfo标签信息），这时用第一层的CardInfoID和CCardNoCode来获得信息
            Object cardInfoId = data.get(Flight.CardInfoID);
            Object ccardNoCode = data.get(Flight.CCardNoCode);
            Map cardInfo = null;
            if(cardInfoId!=null&&ccardNoCode!=null)
            {
                //从esb获取数据 根据CardInfoID取出卡的信息
                String requestType = "AccCash.CreditCard.GetCreditCardInfo";
                String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";
                StringBuffer requestXml = new StringBuffer();
                requestXml.append("<GetCreditCardInfoRequest>");
                requestXml.append("<CardInfoId>");
                requestXml.append(cardInfoId+"");
                requestXml.append("</CardInfoId>");
                requestXml.append("</GetCreditCardInfoRequest>");
                try
                {
                    cardInfo = esbSources.getResponse(requestXml.toString(),requestType,xpath);

                } catch (DocumentException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                if(cardInfo == null)
                {
                    cardInfo = new HashMap();
                    cardInfo.put(Flight.CardInfoID,data.get(Flight.CardInfoID));
                    cardInfo.put(Flight.InfoID,data.get(Flight.InfoID));
                    cardInfo.put(Flight.BillingAddress,data.get(Flight.BillingAddress));
                    cardInfo.put(Flight.CardBin,data.get(Flight.CardBin));
                    cardInfo.put(Flight.CardHolder,data.get(Flight.CardHolder));
                    cardInfo.put(Flight.CCardLastNoCode,data.get(Flight.CCardLastNoCode));
                    cardInfo.put(Flight.CCardNoCode,data.get(Flight.CCardNoCode));
                    cardInfo.put(Flight.CCardPreNoCode,data.get(Flight.CCardPreNoCode));
                    cardInfo.put(Flight.CreditCardType,data.get(Flight.CreditCardType));
                    cardInfo.put(Flight.CValidityCode,data.get(Flight.CValidityCode));
                    cardInfo.put(Flight.IsForigenCard,data.get(Flight.IsForigenCard));
                    cardInfo.put(Flight.Nationality,data.get(Flight.Nationality));
                    cardInfo.put(Flight.Nationalityofisuue,data.get(Flight.Nationalityofisuue));
                    cardInfo.put(Flight.BankOfCardIssue,data.get(Flight.BankOfCardIssue));
                    cardInfo.put(Flight.StateName,data.get(Flight.StateName));
                    cardInfo.put(Flight.InfoID,data.get(Flight.InfoID));
                }
            }

            int cardTypeId = data.get("CreditCardType") == null ? 0 : Integer.parseInt(data.get("CreditCardType").toString());
            String cardBin = data.get("CardBin") == null ? "" : data.get("CardBin").toString();
            Map anotherCardInfo = flightSources.getCardInfo(cardTypeId,cardBin);
            if(cardInfo != null)
            {
                cardInfo.put("CardBinIssue", anotherCardInfo.get("Nationality"));
                cardInfo.put("CardBinBankOfCardIssue",anotherCardInfo.get("BankOfCardIssue"));
            }
            subPaymentInfo.putAll(cardInfo);
            data.put(Flight.PaymentInfo,subPaymentInfo);//把支付信息列表添加进源数据 //Fixme 这里充分测试下
        }

        //添加是否外卡信息
        List<Map> payments = (List<Map>)data.get(Flight.PaymentInfo);
        if(payments != null && paymentInfos.size()>0)
        for(Map payment : payments)
        {
            data.put(Flight.PrepayType,payment.get(Flight.PrepayType));//把支付类型放在第一层 //FIXME 原因？
            if(!payment.get(Flight.PrepayType).equals("CCARD") && !payment.get(Flight.PrepayType).equals("DCARD") && !payment.get(Flight.PrepayType).equals("DQPAY"))
                continue;
            Object cardInfoList = payment.get(Flight.CardInfoList);
            if(cardInfoList != null)
            {
                List listCard = (List)cardInfoList;
                if(listCard.size()>0)
                {
                    data.put(Flight.IsForeignCard,listCard.get(0));//FIXME 这里有待商榷！！！
                    break;
                }
            }
        }
    }

    /**
     * 补充订单支付信息（包括订单的支付和主支付信息）
     * @param data
     */
    public void fillOrderInfo(Map data)
    {
        String orderType = data.get(Flight.OrderType) == null ? "" : data.get(Flight.OrderType).toString();
        String orderId = data.get(Flight.OrderID) == null ? "" : data.get(Flight.OrderID).toString();
        String key = "OrderEntityInfo_"+orderType+"_"+orderId;//设置redies的key
        String value = redisSources.getValue(key);
        if(value != null && !value.isEmpty())
        {
            List<Map> orderInfo = Json.parseObject(value,List.class);
            for(Map map : orderInfo)
            {
                data.putAll(map);//Fixme 这里添加支付信息

                {
                    data.put(Flight.PaymentInfo,map.get(Flight.PaymentInfo));
                    data.put(Flight.PaymentMainInfo,map.get(Flight.PaymentMainInfo));
                }
            }
        }else
        {
            //从数据库读取
            Map mainInfo = flightSources.getMainInfo(orderType,orderId);
            long lastReqID = mainInfo.get("ReqID") == null ? 0 : Long.parseLong(mainInfo.get("ReqID").toString());
            List<Map<String,Object>> paymentInfo = flightSources.getPaymentInfo(lastReqID);
            if(paymentInfo != null)
            {
                for(Map map : paymentInfo)
                {
                    long paymentInfoId = map.get("PaymentInfoID") == null ? 0 : Long.parseLong(map.get("PaymentInfoID").toString());
                    List<Map<String,Object>> temPaymentInfo = flightSources.getTemPayInfo(paymentInfoId);
                    data.put(Flight.PaymentInfo,temPaymentInfo);//把从数据库支付信息添加到支付里面  //Fixme 这里是否正确？
                }
            }
            Map paymentMainInfo = flightSources.getPaymentMainInfo(lastReqID);
            data.put(Flight.PaymentMainInfo,paymentMainInfo);
//            data.putAll(paymentMainInfo);
            //FIXME 这里应该要把从数据库读取的数据存放到redis
            //Todo 把数据放进redis
        }
    }

    /**
     * 补充机票订单信息
     * @param data
     */
    public void fillFlightsOrderInfo(Map data)
    {
        Map mainInfo = flightSources.getMainInfo(data.get(Flight.OrderType).toString(),data.get(Flight.OrderID).toString());
        if(mainInfo == null)
            return;
        long lastReqID = mainInfo.get("ReqID") == null ? 0 : Long.parseLong(mainInfo.get("ReqID").toString());
        Map flightsOrderInfo = flightSources.getFlightsOrderInfo(lastReqID);
        data.put(Flight.FlightsOrderInfo,flightsOrderInfo);

        Map paymentMainInfo = flightSources.getPaymentMainInfo(lastReqID);
        data.put(Flight.PaymentMainInfo,paymentMainInfo);
    }

    /**
     * 计算机票利润
     * @param data
     */
    public void fillFightsOrderProfit(Map data)
    {
        if(data.get(Flight.PaymentInfo) == null || Integer.parseInt(data.get(Flight.CheckType).toString()) == 0)
            return;

        List<Map<String,Object>> paymentInfo = (List<Map<String,Object>>)data.get(Flight.PaymentInfo);
        for(Map payInfo : paymentInfo)
        {
            if(!payInfo.get(Flight.PrepayType).equals("TMPAY"))
            {
                continue;
            }

            ////利润=（机票售价-票面底价）*人数+绑定产品（礼品卡，租车券）*0.7*人数+保险*0.9-机票中礼品卡支付的部分*0.01

            //新公式：利润=（机票卖价-票面底价）+绑定产品（礼品卡，租车券）*0.7+保险*0.9-机票中礼品卡支付的部分*0.01
            if(data.get(Flight.FlightsInfo)==null)
                continue;
            Map flightsInfo = (Map)data.get(Flight.FlightsInfo);

            if(flightsInfo.get(Flight.FlightsOrderInfo) == null || flightsInfo.get(Flight.FlightsOrderInfo) == null)
                continue;

            Map flightsOrderInfo = (Map)flightsInfo.get(Flight.FlightsOrderInfo);
            if(Integer.parseInt(flightsOrderInfo.get("Flightprice").toString()) < Integer.parseInt(flightsOrderInfo.get("FlightCost").toString()))
                continue;
            String flightPrice = flightsOrderInfo.get("Flightprice") == null ? "" : flightsOrderInfo.get("Flightprice").toString();
            String flightCost = flightsOrderInfo.get("FlightCost") == null ? "" : flightsOrderInfo.get("FlightCost").toString();
            String packageAttachFee = flightsOrderInfo.get("PackageAttachFee") == null ? "" : flightsOrderInfo.get("PackageAttachFee").toString();
            String insurance_fee = flightsOrderInfo.get("Insurance_fee") == null ? "" : flightsOrderInfo.get("Insurance_fee").toString();

            Map subPaymentInfo = (Map)payInfo.get(Flight.PaymentInfo);
            String amount = subPaymentInfo.get(Flight.Amount) == null ? "" : subPaymentInfo.get(Flight.Amount).toString();

            double profit = (Long.parseLong(flightPrice)-Long.parseLong(flightCost))+Long.parseLong(packageAttachFee)*0.7+
                    Long.parseLong(insurance_fee)*0.9-Long.parseLong(amount);
            flightsOrderInfo.put("Profit", profit);//添加利润
        }
    }

    /**
     * 补充did信息
     * @param data
     */
    public void fillDID(Map data)
    {
        String orderType = data.get(Flight.OrderType) == null ? "" : data.get(Flight.OrderType).toString();
        String orderId = data.get(Flight.OrderID) == null ? "" : data.get(Flight.OrderID).toString();
        if(orderType.equals("1") && !orderId.isEmpty())
        {
            String key = "DIDInfo_"+orderType+"_"+orderId;
            String value = redisSources.getValue(key);
            Map didInfo = Json.parseObject(value,Map.class);
            if(value != null && !value.isEmpty())
            {
                data.put(Flight.DIDInfo,didInfo);
            }
            //FIXME 这里有个问题什么时候把DID的信息放入redis的？
            //Todo 把上次的信息放入redis
        }

    }

    /**
     * 添加订单日期到注册日期的差值
     * 添加订单日期到起飞日期的差值
     * @param data
     * @throws ParseException
     */
    public void getTimeAbs(Map data) throws ParseException
    {
        //订单日期
        String orderDateStr = data.get(Flight.OrderDate) == null ? "": data.get(Flight.OrderDate).toString();
        Date orderDate = DateUtils.parseDate(orderDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
        //注册日期
        String signUpDateStr = data.get(Flight.SignUpDate) == null ? "": data.get(Flight.SignUpDate).toString();
        Date signUpDate = DateUtils.parseDate(signUpDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        data.put(Flight.OrderToSignUpDate,getDateAbs(signUpDate, orderDate,1));
        //起飞日期
        String takeOffTimeDateStr = data.get(Flight.TakeOffTime) == null ? "": data.get(Flight.TakeOffTime).toString();
        Date takeOffTimeDate =  DateUtils.parseDate(takeOffTimeDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");
        data.put(Flight.TakeOffToOrderDate,getDateAbs(takeOffTimeDate, orderDate,1));
    }
}

package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.print.attribute.standard.OrientationRequested;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupExecutor implements Executor
{
    private Logger logger = LoggerFactory.getLogger(HotelGroupExecutor.class);

    @Autowired
    HotelGroupSources hotelGroupSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;
    @Autowired
    HotelGroupOperation hotelGroupOperation;

    public CheckFact executeHotelGroup(Map data)
    {
        try
        {
            complementData(data);
        } catch (ParseException e)
        {
            logger.warn("补充酒店团购 "+data.get("OrderID")+" 数据异常"+e.getMessage());
        }
        Map<String,Object> bwList = convertToBlackCheckItem(data);
        Map<String,Object> flowData = convertToFlowRuleCheckItem(data);

        //构造规则引擎的数据类型
        CheckFact checkFact = new CheckFact();
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

        return checkFact;
    }
    /**
     * 补充订单数据
     * @param data
     * @throws ParseException
     */
    public void complementData(Map data) throws ParseException
    {
        logger.info("开始补充酒店团购"+data.get("OrderID")+"数据");

        long lastReqID = Long.MIN_VALUE;
        data.put(HotelGroup.LastCheck,"T");
        data.put(HotelGroup.CorporationID,"");

        //根据uid取出crm信息
        String serviceName = "CRMService";
        String operationName = "getMemberInfo";
        String uid = data.get(HotelGroup.Uid) == null ? "" : data.get(HotelGroup.Uid).toString();
        Map params = ImmutableMap.of("uid", uid);
        Map crmInfo = DataProxySources.queryForMap(serviceName, operationName, params);
        data.putAll(crmInfo);

        //得到mainInfo信息
        lastReqID = hotelGroupOperation.getLastReqID(data);
        data.put(HotelGroup.ReqID,lastReqID);
        int checkType = data.get(HotelGroup.CheckType) == null ? Integer.MIN_VALUE : Integer.parseInt(data.get(HotelGroup.CheckType).toString());
        switch (checkType)
        {
            case 0:
                //补充联系人手机对应的省市
                hotelGroupOperation.fillMobilePhone(data);
                //这里获取用户的用户属性（NEW,REPEAT,VIP） 这里有两个方法：1，直接调用esb，2，调用郁伟新增加的DataProxy
                hotelGroupOperation.fillUserCusCharacter(data);
                //处理ip相关的信息
                hotelGroupOperation.fillIpInfo(data);
                //支付信息（兼容混合支付）  这里是根据CardInfoID来取出相关的信息
                hotelGroupOperation.fillPaymentInfo0(data);
                //注册日期和订单日期的差值
                hotelGroupOperation.getTimeAbs(data);
                break;
            case 1:
                hotelGroupOperation.fillMobilePhone(data);
                hotelGroupOperation.fillUserCusCharacter(data);
                hotelGroupOperation.fillIpInfo(data);//处理ip相关的信息
                //补充支付信息 从数据库获取支付信息
                hotelGroupOperation.fillPaymentInfo1(data,lastReqID);
                //注册日期和订单日期的差值
                hotelGroupOperation.getTimeAbs(data);
                break;
            case 2:
                //补充支付信息
                hotelGroupOperation.fillPaymentInfo0(data);//和checkType = 0 的补充支付信息一样
                //通过lastReqID查询所有订单相关的信息
                hotelGroupOperation.fillProductInfo(data,lastReqID);
                break;
            default:
                break;
        }
        //补充DID信息 通过订单id和订单类型来获取
        hotelGroupOperation.getDIDInfo(data);

        //补充主要支付方式
        hotelGroupOperation.fillMainOrderType(data);//这里面加一个字段 “OrderPrepayType”
    }

    //如果，主要支付方式为空，则用订单号和订单类型到risk_levelData取上次的主要支付方式

    //补充主要支付方式自动判断逻辑

    public Map<String,Object> convertToBlackCheckItem(Map data)
    {
        logger.info("开始构造酒店团购"+data.get("OrderID")+"黑白名单数据");
        Map bwList = new HashMap<String,Object>();//定义黑白名单实体

        bwList.put(HotelGroup.IPCity, data.get(HotelGroup.IPCity));
        bwList.put(HotelGroup.IPCountry,data.get(HotelGroup.IPCountry));
        bwList.put(HotelGroup.UserIP,data.get(HotelGroup.UserIPAdd));//fixme 这里为什么是UserIPAdd
        
        bwList.put(HotelGroup.OrderToSignUpDate,data.get(HotelGroup.OrderToSignUpDate));

        //paymentInfo
        //     PaymentInfoList
        //PaymentInfo(Map) ; CardInfoList(List)
        //todo 这块的代码一定要调试一下
        if(data.get(HotelGroup.PaymentInfoList) != null)
         {
            List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(HotelGroup.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(HotelGroup.CardInfoList);

                if((subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD")||
                        subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    bwList.put(HotelGroup.BankOfCardIssue,cardInfoList.get(0).get(HotelGroup.BankOfCardIssue));//why it is the first one
                    bwList.put(HotelGroup.CardBin,cardInfoList.get(0).get(HotelGroup.CardBin));
                    bwList.put(HotelGroup.CardHolder,cardInfoList.get(0).get(HotelGroup.CardHolder));
                    bwList.put(HotelGroup.CCardNoCode,cardInfoList.get(0).get(HotelGroup.CCardNoCode));
                    bwList.put(HotelGroup.Nationality,cardInfoList.get(0).get(HotelGroup.Nationality));
                    bwList.put(HotelGroup.Nationalityofisuue,cardInfoList.get(0).get(HotelGroup.Nationalityofisuue));
                    bwList.put(HotelGroup.CCardPreNoCode,cardInfoList.get(0).get(HotelGroup.CCardPreNoCode));

                    //这里和下面的是一起的  //黑名单校验临时转换  订单类型(C/W/N/X/P)  当前只判断CCARD，CASH，PAYPL
                    String mainOrderPay = data.get(HotelGroup.OrderPrepayType) == null ? "" : data.get(HotelGroup.OrderPrepayType).toString();
                    if(mainOrderPay.toUpperCase().equals("CCARD"))
                    {
                        if(subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD"))
                        {

                            if(cardInfoList.get(0).get(HotelGroup.IsForeignCard) != null && cardInfoList.get(0).get(HotelGroup.IsForeignCard).toString().equals("T"))
                            {
                                bwList.put(HotelGroup.PrepayTypeDetails,"W");
                            }else
                            {
                                bwList.put(HotelGroup.PrepayTypeDetails,"N");
                            }
                        }
                    }
                }
            }

            //黑名单校验临时转换  订单类型(C/W/N/X/P)  当前只判断CCARD，CASH，PAYPL
            String mainOrderPay = data.get(HotelGroup.OrderPrepayType) == null ? "" : data.get(HotelGroup.OrderPrepayType).toString();
            if(mainOrderPay.toUpperCase().equals("CASH"))
            {
                bwList.put(HotelGroup.PrepayTypeDetails,"X");
            }else if(mainOrderPay.toUpperCase().equals("PAYPL"))
            {
                bwList.put(HotelGroup.PrepayTypeDetails,"P");
            }else if(mainOrderPay.toUpperCase().equals("DCARD"))
            {
                bwList.put(HotelGroup.PrepayTypeDetails,"D");
            }
        }

        //ContactInfo
        bwList.put(HotelGroup.ContactEmail,data.get(HotelGroup.ContactEmail));
        bwList.put(HotelGroup.ContactFax,data.get(HotelGroup.ContactFax));
        bwList.put(HotelGroup.ContactName,data.get(HotelGroup.ContactName));
        bwList.put(HotelGroup.ContactTel,data.get(HotelGroup.ContactTel));
        bwList.put(HotelGroup.MobilePhone,data.get(HotelGroup.MobilePhone));
        bwList.put(HotelGroup.TelCall,data.get(HotelGroup.TelCall));
        bwList.put(HotelGroup.SendTicketAddr,data.get(HotelGroup.SendTicketAddr));
        bwList.put(HotelGroup.ForignMobilePhone,data.get(HotelGroup.ForignMobilePhone));

        //UserInfo
        bwList.put(HotelGroup.IsTempUser,data.get(HotelGroup.IsTempUser));
        bwList.put(HotelGroup.Uid,data.get(HotelGroup.Uid));
        bwList.put(HotelGroup.UserPassword,data.get(HotelGroup.UserPassword));
        bwList.put(HotelGroup.TotalPenalty,data.get(HotelGroup.TotalPenalty));

        //mainInfo
        bwList.put(HotelGroup.IsOnline,data.get(HotelGroup.IsOnline));

        //HotelGroupInfo
        bwList.put(HotelGroup.ProductID,data.get(HotelGroup.ProductID));//	产品编号(酒店团购)
        bwList.put(HotelGroup.ProductNameD,data.get(HotelGroup.ProductNameD));//	产品名称(酒店团购)

        //Country //fixme
        bwList.put(HotelGroup.DeviceID,"");
        bwList.put(HotelGroup.FuzzyDeviceID,"");
        bwList.put(HotelGroup.TrueIP,"");
        bwList.put(HotelGroup.TrueIPGeo,"");
        bwList.put(HotelGroup.ProxyIP,"");
        bwList.put(HotelGroup.ProxyIPGeo,"");

        //did
        bwList.put(HotelGroup.DID,data.get(HotelGroup.DID));

        //serverForm
        bwList.put(HotelGroup.Serverfrom,data.get(HotelGroup.Serverfrom));

        return bwList;
    }

    public Map<String,Object> convertToFlowRuleCheckItem(Map data)
    {
        logger.info("开始构造酒店团购"+data.get("OrderID")+"流量表数据");
        Map<String,Object> flowData = new HashMap();
        //common properties
        //nothing done ?

        //InfoSecurity_MainInfo
        flowData.put(HotelGroup.OrderID,data.get(HotelGroup.OrderID));
        flowData.put(HotelGroup.Amount,data.get(HotelGroup.Amount));
        flowData.put(HotelGroup.CheckType,data.get(HotelGroup.CheckType));
        flowData.put(HotelGroup.Serverfrom,data.get(HotelGroup.Serverfrom));
        flowData.put(HotelGroup.OrderDate,data.get(HotelGroup.OrderDate));
        flowData.put(HotelGroup.MergerOrderDate,"yyyyMMdd");//fixme change time form 这里到时候写个DateUtil类
        flowData.put(HotelGroup.OrderDateHour,"hour");//fixme time to hour ?

        //InfoSecurity_CardInfo
        //     PaymentInfoList
        //PaymentInfo(Map) ; CardInfoList(List)
        if(data.get(HotelGroup.PaymentInfoList) != null)
        {
            List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(HotelGroup.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(HotelGroup.CardInfoList);
                if((subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD")||
                        subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    flowData.put(HotelGroup.CCardNoCode,cardInfoList.get(0).get(HotelGroup.CCardNoCode));//why it is the first one
                    flowData.put(HotelGroup.CValidityCode,cardInfoList.get(0).get(HotelGroup.CValidityCode));
                    flowData.put(HotelGroup.CreditCardType,cardInfoList.get(0).get(HotelGroup.CreditCardType));
                    flowData.put(HotelGroup.IsForigenCard,cardInfoList.get(0).get(HotelGroup.IsForigenCard));
                    flowData.put(HotelGroup.CardBinIssue,cardInfoList.get(0).get(HotelGroup.CardBinIssue));
                    flowData.put(HotelGroup.CardBin,cardInfoList.get(0).get(HotelGroup.CardBin));
                    flowData.put(HotelGroup.CardHolder,cardInfoList.get(0).get(HotelGroup.CardHolder));
                    flowData.put(HotelGroup.CardBinOrderID,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.OrderID));

                    break; //fixme ??? 如果这里遍历下去会覆盖
                }
            }
        }

        //InfoSecurity_ContactInfo
        flowData.put(HotelGroup.MobilePhone,data.get(HotelGroup.MobilePhone));
        flowData.put(HotelGroup.MobilePhoneCity,data.get(HotelGroup.MobilePhoneCity));
        flowData.put(HotelGroup.ContactEMail,data.get(HotelGroup.ContactEMail));
        flowData.put(HotelGroup.MobilePhoneProvince,data.get(HotelGroup.MobilePhoneProvince));

        //InfoSecurity_OtherInfo
        flowData.put(HotelGroup.OrderToSignUpDate,data.get(HotelGroup.OrderToSignUpDate));

        //InfoSecurity_HotelGroupInfo
        flowData.put(HotelGroup.Quantity,data.get(HotelGroup.Quantity));//Quantity  City   ProductName   ProductType  Price
        flowData.put(HotelGroup.City,data.get(HotelGroup.City));
        flowData.put(HotelGroup.ProductID,data.get(HotelGroup.ProductID));
        flowData.put(HotelGroup.ProductName,data.get(HotelGroup.ProductName));
        flowData.put(HotelGroup.ProductType,data.get(HotelGroup.ProductType));
        flowData.put(HotelGroup.Price,data.get(HotelGroup.Price));//fixme 这段信息的添加是干嘛的 ，，有的里面没有

        //InfoSecurity_UserInfo
        flowData.put(HotelGroup.CusCharacter,data.get(HotelGroup.CusCharacter));
        flowData.put(HotelGroup.BindedMobilePhone,data.get(HotelGroup.BindedMobilePhone));
        flowData.put(HotelGroup.UserPassword,data.get(HotelGroup.UserPassword));
        flowData.put(HotelGroup.Experience,data.get(HotelGroup.Experience));
        flowData.put(HotelGroup.BindedEmail,data.get(HotelGroup.BindedEmail));
        if(data.get(HotelGroup.BindedMobilePhone) != null && data.get(HotelGroup.BindedMobilePhone).toString().length()>7)
        {
            Map cityInfo = hotelGroupSources.getCityAndProv(data.get(HotelGroup.BindedMobilePhone).toString());
            if(cityInfo != null)
            {
                flowData.put(HotelGroup.BindedMobilePhoneCity,cityInfo.get("CityName"));
                flowData.put(HotelGroup.BindedMobilePhoneProvince,cityInfo.get("ProvinceName"));//fixme todo
            }
        }

        flowData.put(HotelGroup.Uid,data.get(HotelGroup.Uid));
        flowData.put(HotelGroup.VipGrade,data.get(HotelGroup.VipGrade));
        flowData.put(HotelGroup.RelatedMobilephone,data.get(HotelGroup.RelatedMobilephone));
        flowData.put(HotelGroup.RelatedEMail,data.get(HotelGroup.RelatedEMail));
        if(data.get(HotelGroup.RelatedMobilephone) != null && data.get(HotelGroup.RelatedMobilephone).toString().length()>7)
        {
            Map cityInfo = hotelGroupSources.getCityAndProv(data.get(HotelGroup.BindedMobilePhone).toString());
            if(cityInfo != null)
            {
                flowData.put(HotelGroup.RelatedMobilePhoneCity,cityInfo.get("CityName"));
                flowData.put(HotelGroup.RelatedMobilePhoneProvince,cityInfo.get("ProvinceName"));//fixme todo
            }
        }

        //InfoSecurity_IPInfo
        flowData.put(HotelGroup.UserIPAdd,data.get(HotelGroup.UserIPAdd));
        flowData.put(HotelGroup.UserIPValue,data.get(HotelGroup.UserIPValue));
        flowData.put(HotelGroup.IPCity,data.get(HotelGroup.IPCity));
        Map ipCityInfo = hotelGroupSources.getCityInfo(data.get(HotelGroup.IPCity).toString());
        if(ipCityInfo != null && ipCityInfo.size()>0)
        {
            flowData.put(HotelGroup.IPCityName,ipCityInfo.get("CityName"));
            flowData.put(HotelGroup.IPProvince,ipCityInfo.get("ProvinceName"));
        }
        flowData.put(HotelGroup.IPCountry,data.get(HotelGroup.IPCountry));

        //DID
        flowData.put(HotelGroup.DID,data.get(HotelGroup.DID));//fixme 这里在添加的时候需要把did值直接添加在data里面

        //场景  下面这段是用来判断账户风控结果的 这里在通辉的服务里面去做
        /*List<String> sceneTypeList = new ArrayList<String>();
        sceneTypeList.add("PAYMENT-REALTIME-CASH");
        sceneTypeList.add("PAYMENT-REALTIME-CC");
        sceneTypeList.add("PAYMENT-REALTIME-LIPIN");
        Map<String,Object> infos = new HashMap();
        infos.put("UID",data.get(HotelGroup.Uid));
        infos.put("IP",data.get(HotelGroup.UserIPAdd));
        infos.put("DID",data.get(HotelGroup.DID));*/
        //得到账户风控结果 //todo ...这里到时候咨询徐洪  里面调用的是外部服务 AccountSecurityService.Client.AccountSecurityServiceClient.GetInstance();


        //衍生字段
        if(data.get(HotelGroup.PaymentInfoList) != null)
        {
            List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfo : paymentInfos)
            {
                Map subPaymentInfo = (Map)paymentInfo.get(HotelGroup.PaymentInfo);
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(HotelGroup.CardInfoList);
                if((subPaymentInfo.get(HotelGroup.PrepayType) != null && subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD")||
                        subPaymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    flowData.put(HotelGroup.CardBinUID,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.Uid));//why it is the first one
                    flowData.put(HotelGroup.CardBinMobilePhone,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.MobilePhone));
                    flowData.put(HotelGroup.CardBinUserIPAdd,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.UserIPAdd));
                    flowData.put(HotelGroup.ContactEMailCardBin,data.get(HotelGroup.ContactEMail)+""+cardInfoList.get(0).get(HotelGroup.CardBin));

                    flowData.put(HotelGroup.UserIPAddMobileNumber,data.get(HotelGroup.UserIPAdd)+""+data.get(HotelGroup.UserIPAdd).toString().substring(0,7));
                    flowData.put(HotelGroup.UIDMobileNumber,data.get(HotelGroup.Uid)+""+data.get(HotelGroup.UserIPAdd).toString().substring(0, 7));
                }
            }
        }
        //todo
        Map leakInfo = hotelGroupSources.getLeakedInfo(data.get(HotelGroup.Uid).toString());
        if(leakInfo != null && leakInfo.size()>0)
        {
            flowData.put(HotelGroup.UidActive,leakInfo.get("Active"));
        }


        //统计分值大于195的数据
        Map<String,Object> temp = new HashMap();
        temp.put("Uid",data.get(HotelGroup.Uid));
        temp.put("ContactEMail",data.get(HotelGroup.ContactEMail));
        temp.put("MobilePhone",data.get(HotelGroup.MobilePhone));
        temp.put("CCardNoCode",data.get(HotelGroup.CCardNoCode));
        //统计
        //todo 这里不是很清楚，找徐洪确认（是查数据库还是）！！！

        return flowData;
    }
}

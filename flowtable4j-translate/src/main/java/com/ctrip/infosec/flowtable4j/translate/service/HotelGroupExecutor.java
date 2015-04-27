package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.google.common.collect.ImmutableMap;
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
public class HotelGroupExecutor
{
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

    public void complementData(Map data) throws ParseException
    {
        long lastReqID = Long.MIN_VALUE;
        data.put(HotelGroup.LastCheck,"T");
        data.put(HotelGroup.CorporationID,"");

        //根据uid取出crm信息
        String serviceName = "CRMService";
        String operationName = "getMemberInfo";
        String uid = data.get(HotelGroup.Uid) == null ? "" : data.get(HotelGroup.Uid).toString();
        Map params = ImmutableMap.of("uid", uid);
        Map crmInfo = DataProxySources.queryForMap(serviceName, operationName, params);

        //得到mainInfo信息
        lastReqID = hotelGroupOperation.getLastReqID(data);

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
    }

    //如果，主要支付方式为空，则用订单号和订单类型到risk_levelData取上次的主要支付方式

    //补充主要支付方式自动判断逻辑

    public void convertToBlackCheckItem(Map data)
    {
        Map bwList = new HashMap<String,Object>();
        bwList.put(HotelGroup.IPCity, data.get(HotelGroup.IPCity));
        bwList.put(HotelGroup.IPCountry,data.get(HotelGroup.IPCountry));
        bwList.put(HotelGroup.UserIP,data.get(HotelGroup.UserIP)); 
        
        bwList.put(HotelGroup.OrderToSignUpDate,data.get(HotelGroup.OrderToSignUpDate));

        //paymentInfo
        if(data.get(HotelGroup.PaymentInfoList) != null)
        {
            List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfo : paymentInfos)
            {
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(HotelGroup.CardInfoList);
                if((paymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD")||
                        paymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    bwList.put(HotelGroup.BankOfCardIssue,cardInfoList.get(0).get(HotelGroup.BankOfCardIssue));//why it is the first one
                    bwList.put(HotelGroup.CardBin,cardInfoList.get(0).get(HotelGroup.CardBin));
                    bwList.put(HotelGroup.CardHolder,cardInfoList.get(0).get(HotelGroup.CardHolder));
                    bwList.put(HotelGroup.CCardNoCode,cardInfoList.get(0).get(HotelGroup.CCardNoCode));
                    bwList.put(HotelGroup.Nationality,cardInfoList.get(0).get(HotelGroup.Nationality));
                    bwList.put(HotelGroup.Nationalityofisuue,cardInfoList.get(0).get(HotelGroup.Nationalityofisuue));
                    bwList.put(HotelGroup.CCardPreNoCode,cardInfoList.get(0).get(HotelGroup.CCardPreNoCode));
                }
            }

            //黑名单校验临时转换  订单类型(C/W/N/X/P)  当前只判断CCARD，CASH，PAYPL
            //todo
            bwList.put(HotelGroup.PrepayTypeDetails,"Todo");
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
        //bwList.put(HotelGroup.City,data.get(HotelGroup.City));//	产品名称(酒店团购)

        //Country //fixme
        bwList.put(HotelGroup.DeviceID,data.get(HotelGroup.DeviceID));
        bwList.put(HotelGroup.FuzzyDeviceID,data.get(HotelGroup.FuzzyDeviceID));
        bwList.put(HotelGroup.TrueIP,data.get(HotelGroup.TrueIP));
        bwList.put(HotelGroup.TrueIPGeo,data.get(HotelGroup.TrueIPGeo));
        bwList.put(HotelGroup.ProxyIP,data.get(HotelGroup.ProxyIP));
        bwList.put(HotelGroup.ProxyIPGeo,data.get(HotelGroup.ProxyIPGeo));

        //did
        bwList.put(HotelGroup.DID,data.get(HotelGroup.DID));

        //serverForm
        bwList.put(HotelGroup.Serverfrom,data.get(HotelGroup.Serverfrom));
    }

    public void convertToFlowRuleCheckItem(Map data)
    {
        Map<String,Object> flowData = new HashMap();
        //common properties
        //nothing done

        //InfoSecurity_MainInfo
        flowData.put(HotelGroup.OrderID,data.get(HotelGroup.OrderID));
        flowData.put(HotelGroup.Amount,data.get(HotelGroup.Amount));
        flowData.put(HotelGroup.CheckType,data.get(HotelGroup.CheckType));
        flowData.put(HotelGroup.Serverfrom,data.get(HotelGroup.Serverfrom));
        flowData.put(HotelGroup.OrderDate,data.get(HotelGroup.OrderDate));
        flowData.put(HotelGroup.MergerOrderDate,"yyyyMMdd");//fixme change time form
        flowData.put(HotelGroup.OrderDateHour,"hour");//fixme time to hour ?

        //InfoSecurity_CardInfo
        //paymentInfo
        if(data.get(HotelGroup.PaymentInfoList) != null)
        {
            List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfo : paymentInfos)
            {
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(HotelGroup.CardInfoList);
                if((paymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD")||
                        paymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD")) &&
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

                    //break //fixme ???
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
        /*flowData.put(HotelGroup.Quantity,data.get(HotelGroup.Quantity));
        flowData.put(HotelGroup.City,data.get(HotelGroup.City));
        flowData.put(HotelGroup.ProductID,data.get(HotelGroup.ProductID));
        flowData.put(HotelGroup.ProductName,data.get(HotelGroup.ProductName));
        flowData.put(HotelGroup.ProductType,data.get(HotelGroup.ProductType));
        flowData.put(HotelGroup.Price,data.get(HotelGroup.Price));*///fixme

        //InfoSecurity_UserInfo
        flowData.put(HotelGroup.CusCharacter,data.get(HotelGroup.CusCharacter));
        flowData.put(HotelGroup.BindedMobilePhone,data.get(HotelGroup.BindedMobilePhone));
        flowData.put(HotelGroup.UserPassword,data.get(HotelGroup.UserPassword));
        flowData.put(HotelGroup.Experience,data.get(HotelGroup.Experience));
        flowData.put(HotelGroup.BindedEmail,data.get(HotelGroup.BindedEmail));
        /*flowData.put(HotelGroup.BindedMobilePhoneCity,data.get(HotelGroup.BindedMobilePhoneCity));
        flowData.put(HotelGroup.BindedMobilePhoneProvince,data.get(HotelGroup.BindedMobilePhoneProvince));*///fixme todo
        flowData.put(HotelGroup.Uid,data.get(HotelGroup.Uid));
        flowData.put(HotelGroup.VipGrade,data.get(HotelGroup.VipGrade));
        flowData.put(HotelGroup.RelatedMobilephone,data.get(HotelGroup.RelatedMobilephone));
        flowData.put(HotelGroup.RelatedEMail,data.get(HotelGroup.RelatedEMail));
        /*flowData.put(HotelGroup.RelatedMobilePhoneCity,data.get(HotelGroup.RelatedMobilePhoneCity));
        flowData.put(HotelGroup.RelatedMobilePhoneProvince,data.get(HotelGroup.RelatedMobilePhoneProvince));*///fixme todo

        //InfoSecurity_IPInfo
        flowData.put(HotelGroup.UserIPAdd,data.get(HotelGroup.UserIPAdd));
        flowData.put(HotelGroup.UserIPValue,data.get(HotelGroup.UserIPValue));
        flowData.put(HotelGroup.IPCity,data.get(HotelGroup.IPCity));
        /*flowData.put(HotelGroup.IPCityName,data.get(HotelGroup.IPCityName));
        flowData.put(HotelGroup.IPProvince,data.get(HotelGroup.IPProvince));*///fixme todo
        flowData.put(HotelGroup.IPCountry,data.get(HotelGroup.IPCountry));

        //DID
        flowData.put(HotelGroup.DID,data.get(HotelGroup.DID));

        //场景
        List<String> sceneTypeList = new ArrayList<String>();
        sceneTypeList.add("PAYMENT-REALTIME-CASH");
        sceneTypeList.add("PAYMENT-REALTIME-CC");
        sceneTypeList.add("PAYMENT-REALTIME-LIPIN");
        //todo ...

        //衍生字段
        if(data.get(HotelGroup.PaymentInfoList) != null)
        {
            List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfo : paymentInfos)
            {
                List<Map> cardInfoList = (List<Map>)paymentInfo.get(HotelGroup.CardInfoList);
                if((paymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD")||
                        paymentInfo.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD")) &&
                        cardInfoList.size()>0)
                {
                    flowData.put(HotelGroup.CardBinUID,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.Uid));//why it is the first one
                    flowData.put(HotelGroup.CardBinMobilePhone,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.MobilePhone));
                    flowData.put(HotelGroup.CardBinUserIPAdd,cardInfoList.get(0).get(HotelGroup.CardBin)+""+data.get(HotelGroup.UserIPAdd));
                    flowData.put(HotelGroup.ContactEMailCardBin,data.get(HotelGroup.ContactEMail)+""+cardInfoList.get(0).get(HotelGroup.CardBin));

                    flowData.put(HotelGroup.UserIPAddMobileNumber,data.get(HotelGroup.UserIPAdd)+""+data.get(HotelGroup.UserIPAdd).toString().substring(0,7));
                    flowData.put(HotelGroup.UIDMobileNumber,data.get(HotelGroup.Uid)+""+data.get(HotelGroup.UserIPAdd).toString().substring(0, 7));

                    //todo
                    //flowData.put(HotelGroup.UidActive,data.get(HotelGroup.Uid)+""+data.get(HotelGroup.UserIPAdd).toString().substring(0, 7));
                }
            }
        }

        //统计分值大于195的数据
        Map<String,Object> temp = new HashMap();
        temp.put("Uid",data.get(HotelGroup.Uid));
        temp.put("ContactEMail",data.get(HotelGroup.ContactEMail));
        temp.put("MobilePhone",data.get(HotelGroup.MobilePhone));
        temp.put("CCardNoCode",data.get(HotelGroup.CCardNoCode));
        //统计\
        //todo

    }
}

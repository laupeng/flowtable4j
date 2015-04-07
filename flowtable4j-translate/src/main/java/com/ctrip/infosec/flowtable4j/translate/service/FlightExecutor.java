package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.common.MyJSON;
import com.ctrip.infosec.flowtable4j.translate.core.executor.RulesExecutorService;
import com.ctrip.infosec.flowtable4j.translate.dao.FlightSources;
import com.ctrip.infosec.flowtable4j.translate.dao.RedisSources;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ctrip.infosec.flowtable4j.translate.common.IpConvert.ipConvertTo10;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.fastDateFormat;
import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.Json;
/**
 * Created by lpxie on 15-3-31.
 * 两个模块：1，数据补充。2，转换成实体
 */
public class FlightExecutor
{
    @Autowired
    RulesExecutorService rulesExecutorService;
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
        if(checkType == 0 || checkType == 1)
        {
            //补充手机对应的城和省（MobilePhone）
            fillMobilePhoneInfo(data);
            //补充用户信息(uid)
            //TODO 这里是通过调用ESB来获取数据（先跟徐洪确认接口参数再通过cfx生成client代码直接调用）

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
        }else if(checkType == 2)//标识过来的是支付报文
        {
            //补充产品信息
            fillMainInfo(data,checkType);
        }

        //携程内订单验证类型(0--默认（产品+支付），1--产品信息校验，2--支付信息校验)
        if(checkType == 0 || checkType == 2)//补充支付信息
        {
            fillPaymentInfo(data);
        }else if(checkType == 1)
        {
            //补充订单支付（包括带订单支付信息和订单主要支付）信息
            fillOrderInfo(data);
        }

        //计算机票利润
        if(checkType == 0)
        {
            //补充订单信息
            fillFlightsOrderInfo(data);
        }

        //利润=（机票售价-票面底价）*人数+绑定产品（礼品卡，租车券）*0.7*人数+保险*0.9-机票中礼品卡支付的部分*0.01
        fillFightsOrderProfit(data);

        //did逻辑 添加did信息
        fillDID(data);
        //获取userProfile信息
        //TODO 读取userProfile信息
        //保存当前订单信息到redis

        return data;
    }

    /**
     * 转成流量实体
     * 内容是data的数据
     * @param data
     */
    public void convertToFlowEntity(Map data)
    {

    }

    /**
     * 转成黑白名单实体
     * 内容是data的数据
     * @param data
     */
    public void convertToBWEntity(Map data)
    {

    }
    /**
     * 补充手机对应的城市和省
     * @param data
     */
    public void fillMobilePhoneInfo(Map data)
    {
        String mobilePhone = data.get("MobilePhone") == null ? "" : data.get("MobilePhone").toString();
        Map mobileInfo = FlightSources.getCityAndProv(mobilePhone);
        data.put("MobilePhoneCity",mobileInfo.get("CityName"));
        data.put("MobilePhoneProvince",mobileInfo.get("ProvinceName"));
    }

    /**
     * 补充IP信息 先转换成10进制在通过10进制的数据查询IP对应的城市
     * @param data
     */
    public void fillIpInfo(Map data)
    {
        String usrIp = data.get("UserIP") == null ? "" : data.get("UserIP").toString();
        long ipValue = ipConvertTo10(usrIp);
        Map ipInfo = FlightSources.getIpCountryCity(ipValue);
        if(ipInfo == null || ipInfo.size()<=0)
            return;
        data.put("Continent",ipInfo.get("ContinentID"));
        data.put("IPCity",ipInfo.get("CityID"));
        data.put("IPCountry",ipInfo.get("CountryCode"));
    }

    /**
     * 补充航程段信息
     * 获取逻辑：用AAirPort获取对应的City的id三位码
     * 获取逻辑：用DAirPort获取对应的City的id三位码
     * @param data
     */
    public void fillCityCode(Map data)
    {
        if(data.get("SegmentInfoList") == null)
        {
            return;
        }
        List<Map> segmentInfo = (List<Map>)data.get("SegmentInfoList");

        for(Map map : segmentInfo)
        {
            //这里首先从redis获取数据如果redis没有就从数据库获取
            String aairPort = map.get("AAirPort") == null ? "" : map.get("AAirPort").toString();
            String dairPort = map.get("DAirPort") == null ? "" : map.get("DAirPort").toString();
            //TODO 从redis获取数据
            Object object = RedisSources.getValue("AirportCache");
            boolean existAAirPort  = false;
            boolean existDAirPort  = false;
            if(object != null)
            {
                Map ports = (Map)object;
                if(ports.get(aairPort) != null)
                {
                    existAAirPort = true;
                    map.put("ACity",ports.get(aairPort));
                }
                if(ports.get(dairPort) != null)
                {
                    existDAirPort = true;
                    map.put("DCity",ports.get(dairPort));
                }
            }

            Map<String,Object> airPortCache = new HashMap();
            //从数据库读取数据
            if(!existAAirPort)
            {
                int acityCode = FlightSources.getCityCode(aairPort);
                map.put("ACity",acityCode);
                airPortCache.put("AAirPort",acityCode);
            }
            if(!existDAirPort)
            {
                int dcityCode = FlightSources.getCityCode(dairPort);
                map.put("DCity",dcityCode);
                airPortCache.put("DAirPort",dcityCode);
            }
            String value = new MyJSON().toJSONString(airPortCache);
            //存放到redis中
            RedisSources.setKeyValue("AirportCache",value);
        }
    }

    /**
     * 补充订单主信息
     * @param data
     * @param checkType
     */
    public void fillMainInfo(Map data,int checkType)
    {
        if(data.get("OrderType")==null||data.get("OrderId")==null)
            return;
        String orderType = data.get("OrderType")+"";
        String orderId = data.get("orderId")+"";
        //读取订单信息 先从redis读取如果redis没有则从数据库读取再添加到redis
        String key = "OrderEntityInfo_"+orderType+"_"+orderId;
        String value = RedisSources.getValue(key);
        if(value != null && !value.isEmpty())
        {
            Map orderEntity = Json.parseObject(value,Map.class);
            data.putAll(orderEntity);
        }else
        {
            //从数据库读取
            if(checkType == 0)
            {
                return;
            }
            Map<String,Object> newData = new HashMap();
            Map mainInfo = FlightSources.getMainInfo(orderType,orderId);
            if(mainInfo == null|mainInfo.get("ReqID")==null)
                return;
            long lastReqID = Long.parseLong(mainInfo.get("ReqID") + "");
            Map contactInfo = FlightSources.getContactInfo(lastReqID);
            newData.putAll(contactInfo);
            Map userInfo = FlightSources.getUserInfo(lastReqID);
            newData.putAll(userInfo);
            Map ipInfo = FlightSources.getIpInfo(lastReqID);
            newData.putAll(ipInfo);
            //CtripTTDInfo
            Map flightsOrderInfo = FlightSources.getFlightsOrderInfo(lastReqID);
            if(flightsOrderInfo != null )
            {
                String flightOrderID = flightsOrderInfo.get("FlightsOrderID") == null ? "" : flightsOrderInfo.get("FlightsOrderID").toString();
                int flightOrderId = Integer.parseInt(flightOrderID);
                //添加机票乘客信息
                Map passengerInfo = FlightSources.getPassengerInfo(flightOrderId);
                newData.putAll(passengerInfo);
                //添加机票段信息
                Map segmentInfo = FlightSources.getSegmentInfo(flightOrderId);
                newData.putAll(segmentInfo);
            }
            Map otherInfo = FlightSources.getOtherInfo(lastReqID);
            newData.putAll(otherInfo);
            //corporationInfo
            Map corporationInfo = FlightSources.getCorporationInfo(lastReqID);
            newData.putAll(corporationInfo);
            //AppInfo
            Map appInfo = FlightSources.getAppInfo(lastReqID);
            newData.putAll(appInfo);

            data.putAll(newData);
            //这里补充一点把从数据库获取的数据存入redis
            String newValue = Json.toJSONString(newData);
            RedisSources.setKeyValue(key,newValue);
        }
    }

    /**
     * 补充支付信息
     * @param data
     */
    public void fillPaymentInfo(Map data)
    {
        Object object = data.get("PaymentInfos");
        if(object != null)
        {
            List<Map> paymentInfo = (List<Map>)object;
            //补充卡信息 1，通过CardInfoID从ESB里面获取 2，通过1得到的CreditCardType和CardBin从数据库里面外卡信息
            for(Map payment : paymentInfo)
            {
                if(!payment.get("PrepayType").equals("CCARD") && !payment.get("PrepayType").equals("DCARD") && !payment.get("PrepayType").equals("DQPAY"))
                {
                    continue;
                }

                int cardInfoId = payment.get("CardInfoID")==null ? 0 : Integer.parseInt(payment.get("CardInfoID").toString());
                //TODO 从ESB获取数据

                int cardTypeId = payment.get("CreditCardType") == null ? 0 : Integer.parseInt(payment.get("CreditCardType").toString());
                String cardBin = payment.get("CardBin") == null ? "" : payment.get("CardBin").toString();
                Map cardInfo = FlightSources.getCardInfo(cardTypeId,cardBin);
                if(cardInfo == null)
                    continue;
                payment.put("CardBinIssue", cardInfo.get("Nationality"));
                payment.put("CardBinBankOfCardIssue",cardInfo.get("BankOfCardIssue"));
            }

        }else
        {
            //如果报文里面没有支付的信息（指没有paymentInfo标签信息），这时用第一层的CardInfoID和CCardNoCode来获得信息
            Object cardInfoId = data.get("CardInfoID");
            Object ccardNoCode = data.get("CCardNoCode");
            if(cardInfoId!=null&&ccardNoCode!=null)
            {
                //TODO 从esb获取数据
                data.putAll(new HashMap());
            }else
            {
                //nothing to do
                //FIXME 这里没看到 感觉需要任何处理
            }
            int cardTypeId = data.get("CreditCardType") == null ? 0 : Integer.parseInt(data.get("CreditCardType").toString());
            String cardBin = data.get("CardBin") == null ? "" : data.get("CardBin").toString();
            Map cardInfo = FlightSources.getCardInfo(cardTypeId,cardBin);
            if(cardInfo != null)
            {
                data.put("CardBinIssue", cardInfo.get("Nationality"));
                data.put("CardBinBankOfCardIssue",cardInfo.get("BankOfCardIssue"));
            }
        }
        //添加是否外卡信息
        List<Map> payments = (List<Map>)data.get("PaymentInfo");
        for(Map payment : payments)
        {
            if(!payment.get("PrepayType").equals("CCARD") && !payment.get("PrepayType").equals("DCARD") && !payment.get("PrepayType").equals("DQPAY"))
                continue;
            Object cardInfoList = payment.get("CardInfoList");
            if(cardInfoList != null)
            {
                List listCard = (List)cardInfoList;
                if(listCard.size()>0)
                    data.put("IsForeignCard",listCard.get(0));//FIXME 这里有待商榷！！！
            }
        }
    }

    /**
     * 补充订单支付信息（包括订单的支付和主支付信息）
     * @param data
     */
    public void fillOrderInfo(Map data)
    {
        String orderType = data.get("OrderType") == null ? "" : data.get("OrderType").toString();
        String orderId = data.get("OrderID") == null ? "" : data.get("OrderType").toString();
        String key = "OrderEntityInfo_"+orderType+"_"+orderId;
        String value = RedisSources.getValue(key);
        if(value != null && !value.isEmpty())
        {
            List<Map> orderInfo = Json.parseObject(value,List.class);
            for(Map map : orderInfo)
            {
                data.putAll(map);
            }
        }else
        {
            //从数据库读取
            Map mainInfo = FlightSources.getMainInfo(orderType,orderId);
            long lastReqID = mainInfo.get("ReqID") == null ? 0 : Long.parseLong(mainInfo.get("ReqID").toString());
            List<Map<String,Object>> paymentInfo = FlightSources.getPaymentInfo(lastReqID);
            if(paymentInfo != null)
            {
                for(Map map : paymentInfo)
                {
                    long paymentInfoId = map.get("PaymentInfoID") == null ? 0 : Long.parseLong(map.get("PaymentInfoID").toString());
                    List<Map<String,Object>> temPaymentInfo = FlightSources.getTemPayInfo(paymentInfoId);
                    data.put("tempPay",temPaymentInfo);
                }
            }
            Map paymentMainInfo = FlightSources.getPaymentMainInfo(lastReqID);
            data.putAll(paymentMainInfo);
            //FIXME 这里应该要把从数据库读取的数据存放到redis
        }
    }

    /**
     * 补充机票订单信息
     * @param data
     */
    public void fillFlightsOrderInfo(Map data)
    {
        Map mainInfo = FlightSources.getMainInfo(data.get("OrderType").toString(),data.get("OrderID").toString());
        if(mainInfo == null)
            return;
        long lastReqID = mainInfo.get("ReqID") == null ? 0 : Long.parseLong(mainInfo.get("ReqID").toString());
        Map flightsOrderInfo = FlightSources.getFlightsOrderInfo(lastReqID);
        data.put("FlightsOrderInfo",flightsOrderInfo);
        Map paymentMainInfo = FlightSources.getPaymentMainInfo(lastReqID);
        data.put("PaymentMainInfo",paymentMainInfo);
    }

    /**
     * 计算机票利润
     * @param data
     */
    public void fillFightsOrderProfit(Map data)
    {
        if(data.get("PaymentInfo") == null || Integer.parseInt(data.get("CheckType").toString()) == 0)
            return;

        List<Map<String,Object>> paymentInfo = (List<Map<String,Object>>)data.get("PaymentInfo");
        for(Map payInfo : paymentInfo)
        {
            if(!payInfo.get("PrepayType").equals("TMPAY"))
            {
                continue;
            }

            ////利润=（机票售价-票面底价）*人数+绑定产品（礼品卡，租车券）*0.7*人数+保险*0.9-机票中礼品卡支付的部分*0.01

            //新公式：利润=（机票卖价-票面底价）+绑定产品（礼品卡，租车券）*0.7+保险*0.9-机票中礼品卡支付的部分*0.01
            if(data.get("FlightsInfo")==null)
                continue;
            Map flightsInfo = (Map)data.get("FlightsInfo");
            if(flightsInfo.get("FlightsOrderInfo") == null)
                continue;
            Map flightsOrderInfo = (Map)flightsInfo.get("FlightsOrderInfo");
            if(Integer.parseInt(flightsOrderInfo.get("Flightprice").toString()) < Integer.parseInt(flightsOrderInfo.get("FlightCost").toString()))
                continue;
            String flightPrice = flightsOrderInfo.get("Flightprice") == null ? "" : flightsOrderInfo.get("Flightprice").toString();
            String flightCost = flightsOrderInfo.get("FlightCost") == null ? "" : flightsOrderInfo.get("FlightCost").toString();
            String packageAttachFee = flightsOrderInfo.get("PackageAttachFee") == null ? "" : flightsOrderInfo.get("PackageAttachFee").toString();
            String insurance_fee = flightsOrderInfo.get("Insurance_fee") == null ? "" : flightsOrderInfo.get("Insurance_fee").toString();
            Map subPaymentInfo = (Map)payInfo.get("PaymentInfo");
            String amount = subPaymentInfo.get("Amount") == null ? "" : subPaymentInfo.get("Amount").toString();

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
        String orderType = data.get("OrderType") == null ? "" : data.get("OrderType").toString();
        String orderId = data.get("OrderID") == null ? "" : data.get("OrderID").toString();
        String key = "DIDInfo_"+orderType+"_"+orderId;
        String value = RedisSources.getValue(key);
        if(value != null && !value.isEmpty())
        {
            data.put("DID",value);
        }
        //FIXME 这里有个问题什么时候把DID的信息放入redis的？
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
        String orderDate = data.get("OrderDate") == null ? "": data.get("OrderDate").toString();
        Date date1 = (Date)fastDateFormat.parseObject(orderDate);
        //注册日期
        String time1 = data.get("SignUpDate") == null ? "": data.get("SignUpDate").toString();
        Date dateOrder = (Date)fastDateFormat.parseObject(time1);
        data.put("OrderToSignUpDate",getDateAbs(dateOrder, date1));
        //起飞日期
        String time3 = data.get("TakeOffTime") == null ? "": data.get("TakeOffTime").toString();
        Date date2 = (Date)fastDateFormat.parseObject(time3);
        data.put("TakeOffToOrderDate",getDateAbs(date2, dateOrder));
    }
}

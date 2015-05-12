package com.ctrip.infosec.flowtable4j.translate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-5-6.
 */
public class DataFact
{
    //一：下面这些是公共属性
    //订单主要信息
    public Map<String,Object> mainInfo = new HashMap<String, Object>();
    //联系人信息
    public Map<String,Object> contactInfo = new HashMap<String, Object>();
    //账户信息
    public Map<String,Object> userInfo = new HashMap<String, Object>();
    //ip相关信息
    public Map<String,Object> ipInfo = new HashMap<String, Object>();
    //其他信息
    public Map<String,Object> otherInfo = new HashMap<String, Object>();

    //二：支付信息
    public List<Map> paymentInfoList = new ArrayList<Map>();

   /* public Map<String,Object> paymentInfo = new HashMap<String, Object>();//感觉没有必要
    public List<Object> cardInfoList = new ArrayList<Object>();//感觉没有必要

    public Map<String,Object> cardInfo = new HashMap<String, Object>();//感觉没有必要*/

    //预付信息
    public Map<String,Object>  paymentMainInfo = new HashMap<String, Object>();

    //三：这个是产品信息，根据具体的产品来 这里一个类搞不定，用两个类型，一个Map一个List
    public Map<String,Object> productInfoM = new HashMap<String, Object>();
    public List<Map> productInfoL = new ArrayList<Map>();

    //四:dealInfo信息
    public Map<String,Object> dealInfo = new HashMap<String, Object>();

    //五：corporationInfo信息 商旅信息
    public Map<String,Object> corporationInfo = new HashMap<String, Object>();

    //六：DIDInfo信息
    public Map<String,Object> DIDInfo = new HashMap<String, Object>();


    //七：临时信息


}

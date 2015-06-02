package com.ctrip.infosec.flowtable4j.translate.model;

/**
 * Created by lpxie on 15-4-16.
 * checkType=0的报文字段标签
 * 注意里面有两个字段的名称一样,
 * 在嵌套里面的字段名称前面加了一个S字母区分
 */
public class Flight
{
    //机票产品信息
    public static String DAirPort = "DAirPort";
    public static String EAirPort = "EAirPort";
    public static String AAirPort = "AAirPort";

    public static String DCity = "DCity";
    public static String ACity = "ACity";
    public static String FlightClass = "FlightClass";

    public static String IsClient = "IsClient";
    public static String SubOrderType = "SubOrderType";
    public static String TakeOffTime = "TakeOffTime";

    public static String Remark = "Remark";
    public static String SalesType = "SalesType";
    public static String FlightCost = "FlightCost";


    public static String InsuranceCost = "InsuranceCost";
    public static String AgencyName = "AgencyName";
    public static String Agencyid = "Agencyid";

    public static String FlightCostRate = "FlightCostRate";
    public static String Insurance_fee = "Insurance_fee";
    public static String Flightprice = "Flightprice";

    public static String PackageAttachFee = "PackageAttachFee";
    public static String Persons = "Persons";
    public static String Tot_Oilfee = "Tot_Oilfee";

    public static String Tot_Tax = "Tot_Tax";
    public static String UrgencyLevel = "UrgencyLevel";
    //机票乘客信息
    public static String PassengerBirthday = "PassengerBirthday";
    public static String PassengerCardID = "PassengerCardID";

    public static String PassengerName = "PassengerName";
    public static String PassengerNationality = "PassengerNationality";


    public static String PassengerCardIDType = "PassengerCardIDType";
    public static String PassengerAgeType = "PassengerAgeType";
    public static String PassengerGender = "PassengerGender";
    //航程段信息

    //FlightsOrderInfo  PassengerInfoList  SegmentInfoList
    public static String FlightsOrderInfo = "FlightsOrderInfo";
    public static String PassengerInfoList = "PassengerInfoList";
    public static String SegmentInfoList = "SegmentInfoList";
}

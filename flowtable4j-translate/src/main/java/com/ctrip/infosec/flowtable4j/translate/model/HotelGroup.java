package com.ctrip.infosec.flowtable4j.translate.model;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroup
{

    public static String OrderType = "OrderType";
    public static String OrderID = "OrderID";
    public static String MerchantID = "MerchantID";
    public static String SalesType = "SalesType";
    public static String CheckType = "CheckType";
    public static String ContactEMail = "ContactEMail";
    public static String ContactFax = "ContactFax";
    public static String ContactName = "ContactName";
    public static String ContactTel = "ContactTel";
    public static String SendTickerAddr = "SendTickerAddr";
    public static String PostAddress = "PostAddress";
    public static String MobilePhone = "MobilePhone";
    public static String ForeignMobilePhone = "ForeignMobilePhone";
    public static String ZipCode = "ZipCode";
    public static String TelCall = "TelCall";
    public static String DAirPort = "DAirPort";
    public static String EAirPort = "EAirPort";
    public static String AAirPort = "AAirPort";
    public static String DCity = "DCity";
    public static String ACity = "ACity";
    public static String IsClient = "IsClient";
    public static String Remark = "Remark";
    public static String FlightClass = "FlightClass";
    public static String TakeOffTime = "TakeOffTime";
    public static String SubOrderType = "SubOrderType";
    public static String PrepayType = "PrepayType";
    public static String IsGuarantee = "IsGuarantee";
    public static String Insurance_fee = "Insurance_fee";
    public static String Flightprice = "Flightprice";
    public static String PackageAttachFee = "PackageAttachFee";
    public static String Persons = "Persons";
    public static String Tot_Oilfee = "Tot_Oilfee";
    public static String Tot_Tax = "Tot_Tax";
    public static String FlightCost = "FlightCost";
    public static String InsuranceCost = "InsuranceCost";
    public static String AgencyName = "AgencyName";
    public static String Agencyid = "Agencyid";
    public static String FlightCostRate = "FlightCostRate";
    public static String Serverfrom = "Serverfrom";
    public static String WirelessClientNo = "WirelessClientNo";
    public static String IsOnline = "IsOnline";
    public static String OrderDate = "OrderDate";
    public static String Amount = "Amount";
    public static String CardInfoID = "CardInfoID";
    public static String InfoID = "InfoID";
    public static String ClientID = "ClientID";
    public static String Latitude = "Latitude";
    public static String Longitude = "Longitude";
    public static String ClientVersion = "ClientVersion";
    public static String RefNo = "RefNo";
    public static String BillingAddress = "BillingAddress";
    public static String CardBin = "CardBin";
    public static String CardHolder = "CardHolder";
    public static String CCardLastNoCode = "CCardLastNoCode";
    public static String CCardPreNoCode = "CCardPreNoCode";
    public static String CCardNoCode = "CCardNoCode";
    public static String CreditCardType = "CreditCardType";
    public static String CValidityCode = "CValidityCode";
    public static String IsForeignCard = "IsForeignCard";
    public static String Nationality = "Nationality";
    public static String StateName = "StateName";
    public static String Nationalityofisuue = "Nationalityofisuue";
    public static String BankOfCardIssue = "BankOfCardIssue";
    public static String SignUpDate = "SignUpDate";
    public static String Uid = "Uid";
    public static String IsUidHasBlackCard = "IsUidHasBlackCard";
    public static String CusCharacter = "CusCharacter";
    public static String IsTempUser = "IsTempUser";
    public static String TotalPenalty = "TotalPenalty";
    public static String Experience = "Experience";
    public static String MD5Password = "MD5Password";
    public static String VipGrade = "VipGrade";
    public static String UserIP = "UserIP";
    public static String ReferenceNo = "ReferenceNo";
    public static String NeedCheckBlackList = "NeedCheckBlackList";
    public static String IsNeedCheckRisk = "IsNeedCheckRisk";
    public static String FltRiskLevel = "FltRiskLevel";
    /**
     * 乘客信息
     * 对应的内容是List类型
     */
    public static String PassengerInfoList = "PassengerInfoList";
    /**
     * 乘客信息,PassengerInfoList的子类型
     * 对应的内容是Map
     */
    public static String PassengerInfo = "PassengerInfo";
    public static String PassengerGender = "PassengerGender";
    public static String PassengerBirthday = "PassengerBirthday";
    public static String PassengerName = "PassengerName";
    public static String PassengerNationality = "PassengerNationality";
    public static String PassengerCardNo = "PassengerCardNo";
    public static String PassengerCardIDType = "PassengerCardIDType";
    public static String PassengerAgeType = "PassengerAgeType";

    /**
     *航程段信息
     * 表示飞机的飞行路线信息
     */
    public static String SegmentInfoList = "SegmentInfoList";
    /**
     * 航程段信息 SegmentInfoList的子类型
     */
    public static String SegmentInfo = "SegmentInfo";
    public static String Sequence = "Sequence";
    public static String SDAirPort = "DAirPort";
    public static String SAAirPort = "AAirPort";
    public static String SubClass = "SubClass";
    public static String SeatClass = "SeatClass";
    public static String Takeofftime = "Takeofftime";
    public static String Arrivaltime = "Arrivaltime";

    public static String CorporationID = "CorporationID";
    public static String Corp_PayType = "Corp_PayType";
    public static String CanAccountPay = "CanAccountPay";
    public static String CompanyType = "CompanyType";
    public static String BusinessItem = "BusinessItem";

    /*支付相关的信息*/
    public static String PayMethod = "PayMethod";
    public static String PayValidationMethod = "PayValidationMethod";
    public static String BankValidationMethod = "BankValidationMethod";
    public static String ValidationFailsReason = "ValidationFailsReason";

    /**
     * 对应的内容类型是List
     */
    public static String PaymentInfos = "PaymentInfos";
    /**
     * PaymentInfos的子类型
     * 对应的内容类型是Map
     */
    public static String PaymentInfoByDefault = "PaymentInfoByDefault";
    //public static String PrepayType = "PrepayType";
    //public static String Amount = "Amount";
    //public static String RefNo = "RefNo";
    /**
     * 支付信息（PaymentInfoByDefault）的子类
     * 表示当前支付的卡的信息
     */
    /*public static String CreditCardInfo = "CreditCardInfo";
    public static String CardInfoID = "CardInfoID";
    public static String CreditCardType = "CreditCardType";
    public static String InfoID = "InfoID";
    public static String CValidityCode = "CValidityCode";
    public static String CCardNoCode = "CCardNoCode";
    public static String CardHolder = "CardHolder";
    public static String CardBin = "CardBin";
    public static String CCardLastNoCode = "CCardLastNoCode";
    public static String CCardPreNoCode = "CCardPreNoCode";
    public static String StateName = "StateName";
    public static String BillingAddress = "BillingAddress";
    public static String Nationality = "Nationality";
    public static String Nationalityofisuue = "Nationalityofisuue";
    public static String BankOfCardIssue = "BankOfCardIssue";
    public static String IsForigenCard = "IsForigenCard";*/


    /*下面是衍生字段 主要是给黑白名单统计使用 和给流量统计使用*/
    public static String LastCheck = "LastCheck";

    //通过手机号取出对应的城市和省名称
    public static String MobilePhoneCity = "CityName";
    public static String MobilePhoneProvince = "ProvinceName";

    //通过uid获取的用户的crm的信息
    public static String BindedEmail= "BindedEmail";
    public static String BindedMobilePhone= "BindedMobilePhone";
    public static String RelatedEMail= "RelatedEMail";
    public static String RelatedMobilephone= "RelatedMobilephone";

    //添加了UserIPAdd这个字段 值和userIp是一样的
    public static String UserIPAdd = "UserIPAdd";
    public static String UserIPValue = "UserIPValue";
    //根据ip的十进制数获取的相关信息
    public static String Continent = "Continent";
    public static String IPCity = "IPCity";
    public static String IPCountry = "IPCountry";

    //通过订单时间 和 “”计算出来的差值
    public static String OrderToSignUpDate = "OrderToSignUpDate";
    public static String TakeOffToOrderDate = "TakeOffToOrderDate";

    //存储cardInfo的卡信息字段
    public static String PaymentInfo = "PaymentInfo";

    //是否外卡
    public static String IsForigenCard = "IsForigenCard";

    //卡信息
    public static String CardInfoList = "CardInfoList";

    //从redis取出的衍生字段
    public static String PaymentMainInfo = "PaymentMainInfo";

    //添加支付信息的时候的衍生字段
    public static String tempPay = "tempPay";

    //补充的机票订单的信息
    public static String FlightsOrderInfo = "FlightsOrderInfo";

    //机票衍生信息
    public static String FlightsInfo = "FlightsInfo";

    //DID相关相关信息
    public static String DIDInfo = "DIDInfo";

    //paymentInfoList
    public static String PaymentInfoList = "PaymentInfoList";

    //CardBinIssue
    public static String CardBinIssue = "CardBinIssue";

    //PrepayTypeDetails
    public static String PrepayTypeDetails = "PrepayTypeDetails";

    //ContactEmail
    public static String ContactEmail = "ContactEmail";

    //ForignMobilePhone
    public static String ForignMobilePhone = "ForignMobilePhone";

    //SendTicketAddr
    public static String SendTicketAddr = "SendTicketAddr";

    //UserPassword
    public static String UserPassword = "UserPassword";

    //ProductID
    public static String ProductID = "ProductID";

    //ProductNameD
    public static String ProductNameD = "ProductNameD";
    //coutry id ...
    public static String DeviceID = "DeviceID";
    public static String FuzzyDeviceID = "FuzzyDeviceID";
    public static String TrueIP = "TrueIP";
    public static String TrueIPGeo = "TrueIPGeo";
    public static String ProxyIP = "ProxyIP";
    public static String ProxyIPGeo = "ProxyIPGeo";

    //DID
    public static String DID = "DID";

    //MergerOrderDate
    public static String MergerOrderDate = "MergerOrderDate";

    //OrderDateHour
    public static String OrderDateHour = "OrderDateHour";

    //CardBinOrderID
    public static String CardBinOrderID = "CardBinOrderID";

    //CardBinUID
    public static String CardBinUID = "CardBinUID";
    public static String CardBinMobilePhone = "CardBinMobilePhone";
    public static String CardBinUserIPAdd = "CardBinUserIPAdd";
    public static String ContactEMailCardBin = "ContactEMailCardBin";

    //UserIPAddMobileNumber
    public static String UserIPAddMobileNumber = "UserIPAddMobileNumber";
    public static String UIDMobileNumber = "UIDMobileNumber";

    //主键表示
    public static String ReqID = "ReqID";

    //系统发行卡名称
    public static String CardBinBankOfCardIssue = "CardBinBankOfCardIssue";

    //主要支付方式
    public static String OrderPrepayType = "OrderPrepayType";

    //根据绑定的手机号获取对应的城市
    public static String BindedMobilePhoneCity = "BindedMobilePhoneCity";
    public static String BindedMobilePhoneProvince = "BindedMobilePhoneProvince";
    //根据联系的手机号获取对应的城市
    public static String RelatedMobilePhoneCity = "RelatedMobilePhoneCity";
    public static String RelatedMobilePhoneProvince = "RelatedMobilePhoneProvince";

    //ip补充信息 //IPCityName  IPProvince 用在流量表里面
    public static String IPCityName = "IPCityName";
    public static String IPProvince = "IPProvince";

    //uid是否激活标识 UidActive
    public static String UidActive = "UidActive";

    //Quantity  City   ProductName   ProductType  Price  酒店团购产品信息
    public static String Quantity = "Quantity";
    public static String City = "City";
    public static String ProductName = "ProductName";
    public static String ProductType = "ProductType";
    public static String Price = "Price";
}

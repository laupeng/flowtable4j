package com.ctrip.infosec.flowtable4j.v2m.service;

import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.RequestBody;

/**
 * 支付适配订单信息
 * 暂只有 CRH/HOTELGROUP/FLIGHTS/TTD有
 * Created by thyang on 2015-06-15.
 */
public class Order4PayAdaptService {
      public void saveOrder2Redis(RequestBody requestBody){
            // CRH
            // MobilePhone-> MobilePhone
            // MobilePhone-->MobilePhoneCity
            // ACity --> ACtity, DCity --> DCity
            // Serverfrom -> Serverfrom
            // OrderAmount -> Amount
            // ContactEMail -> ContactEMail
            // Uid --> VipGrade,BindedMobilePhone,RelatedMobilephone,RelatedEMail,CusCharacter
            // OrderDate,Uid -> OrderToSignUpDate

            //HotelGroup
            // Uid --> BindedMobilePhone,RelatedMobilephone,RelatedEMail,BindedEmail,CusCharacter,VipGrade
            // Serverfrom -> Serverfrom
            // City -> City
            // MobilePhone-> MobilePhone
            // MobilePhone-->MobilePhoneCity
            // ContactEMail -> ContactEMail
            // UserIP -> UserIPValue
            // OrderAmount -> Amount
            // OrderDate -> OrderToSignUpDate


            //Flights
            // ACity --> ACtity,ACityProvince, DCity --> DCity,DCityProvince
            // Uid --> BindedMobilePhone,RelatedMobilephone,RelatedEMail,BindedEmail,CusCharacter,VipGrade,SignUpDate
            // OrderDate,Uid -> OrderToSignUpDate
            // TakeOffTime,OrderDate--> TakeOffToOrderDate
            // UserIP -> UserIPValue,Continent
            // Serverfrom -> Serverfrom
            // SendTickerAddr -> SendTickerAddr
            // FlightClass -> FlightClass
            // Amount-> Amount
            // ContactEMail,MobilePhone -> ContactEMail,MobilePhone,MobilePhoneCity,MobilePhoneProvince

            //TTD
            // Uid --> BindedMobilePhone,BindedMobilePhoneCity,RelatedMobilephone,RelatedMobilephoneCity,RelatedEMail,BindedEmail,CusCharacter,VipGrade,SignUpDate
            // Serverfrom -> Serverfrom
            // MobilePhone -> MobilePhone,MobilePhoneCity,MobilePhoneProvince
            // OrderDate -> OrderToSignUpDate
            // ProductName -> ProductName

      }

}

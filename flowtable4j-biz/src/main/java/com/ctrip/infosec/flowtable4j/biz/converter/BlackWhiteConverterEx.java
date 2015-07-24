package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.MapX;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-07-04.
 */
@Component
public class BlackWhiteConverterEx extends ConverterBase {

    /**
     * 实际应用中，paymentinfolist 只有一条记录
     * 同样 cardInfoList 也只有一条记录
     *
     * @param paymentInfo
     * @param target
     */
    public void fillPaymentInfoToMap(Map<String, Object> paymentInfo, Map<String, Object> target) {
        List<Map<String, Object>> paymentinfolist = getList(paymentInfo, "paymentinfolist");
        if (paymentinfolist != null && paymentinfolist.size() > 0) {
            for (Map<String, Object> paymentItem : paymentinfolist) {
                Map<String, Object> payment = getMap(paymentItem, "payment");
                if (payment != null) {
                    String prepayType = MapX.getString(payment, "prepaytype", "").toUpperCase();
                    if ("CASH".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "X");
                    } else if ("PAYPL".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "P");
                    } else if ("DCARD".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "D");
                    } else if ("DQPAY".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "D");
                    }

                    if (prepayType.equals("CCARD") || prepayType.equals("DCARD") || prepayType.equals("DQPAY")) {
                        List<Map<String, Object>> cardInfoList = getList(paymentItem, "cardinfolist");
                        if (cardInfoList != null && cardInfoList.size() > 0) {
                            Map<String, Object> cardInfo0 = cardInfoList.get(0);
                            copyMapIfNotNull(cardInfo0, target, new String[]{
                                    "bankofcardissue", "cardbin", "cardbinissue", "cardholder",
                                    "ccardnocode", "cardnorefid", "nationality", "nationalityofisuue",
                                    "ccardprenocode"
                            });

                            if (prepayType.equals("CCARD")) {
                                if ("T".equals(getString(cardInfo0, "isforigencard", "").toUpperCase())) {
                                    setValue(target, "prepaytypedetails", "W");
                                } else {
                                    setValue(target, "prepaytypedetails", "N");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void fillRailInfoList(Map<String, Object> productInfo, Map<String, Object> content) {
        List<Map<String, Object>> railInfoListMap = getList(productInfo, "railinfolist");
        if (railInfoListMap != null && railInfoListMap.size() > 0) {
            copyMapIfNotNull(getMap(railInfoListMap.get(0), "rail"), content, new String[]{"dcity", "acity"});
            StringBuilder names = new StringBuilder("|");
            StringBuilder cardType = new StringBuilder("|");
            StringBuilder cards = new StringBuilder("|");
            for (Map<String, Object> railMap : railInfoListMap) {
                Map<String, Object> user = getMap(railMap, "user");
                if (user != null && user.size() > 0) {
                    names.append(getString(user, "passengername")).append("|");
                    cardType.append(getString(user, "passengeridtype")).append("|");
                    cards.append(getString(user, "passengeridcode")).append("|");
                }
            }
            setValue(content, "crhpassengername", names.toString());
            setValue(content, "crhpassengercardidtype", cardType.toString());
            setValue(content, "crhpassengercardid", cards.toString());
        }
    }

    public void fillVacationInfoList(Map<String, Object> productinfo, Map<String, Object> content) {
        List<Map<String, Object>> vacationinfolistMap = getList(productinfo, "vacationinfolist");
        if (vacationinfolistMap != null && vacationinfolistMap.size() > 0) {
            for (Map<String, Object> vacationinfoMap : vacationinfolistMap) {
                copyValueIfNotNull(getMap(vacationinfoMap, "order"), "productname", content, "productname");
                List<Map<String, Object>> userlistMap = getList(vacationinfoMap, "userlist");
                if (userlistMap != null && userlistMap.size() > 0) {
                    StringBuilder names = new StringBuilder("|");
                    StringBuilder nations = new StringBuilder("|");
                    StringBuilder cards = new StringBuilder("|");
                    StringBuilder contact = new StringBuilder("|");
                    for (Map<String, Object> user : userlistMap) {
                        names.append(getString(user, "passengername", "")).append("|");
                        nations.append(getString(user, "visitornationality", "")).append("|");
                        cards.append(getString(user, "visitoridcode", "")).append("|");
                        contact.append(getString(user, "visitorcontactinfo", "")).append("|");
                    }
                    setValue(content, "passengername", names.toString());
                    setValue(content, "passengernationality", nations.toString());
                    setValue(content, "passengercardid", cards.toString());
                    setValue(content, "visitorcontactinfo", contact.toString());
                }
                List<Map<String, Object>> optlistMap = getList(vacationinfoMap, "optionlist");
                if (optlistMap != null && optlistMap.size() > 0) {
                    StringBuilder optid = new StringBuilder("|");
                    StringBuilder optName = new StringBuilder("|");
                    for (Map<String, Object> opt : optlistMap) {
                        optid.append(getString(opt, "optionid", "")).append("|");
                        optName.append(getString(opt, "optionname", "")).append("|");
                    }
                    setValue(content, "vacationoptionid", optid.toString());
                    setValue(content, "vacationoptionname", optName.toString());
                }
            }
        }
    }

    public void fillFlightInfoList(Map<String, Object> productinfo, Map<String, Object> content) {
        List<Map<String, Object>> flightinfolistMap = getList(productinfo, "flightinfolist");
        if (flightinfolistMap != null && flightinfolistMap.size() > 0) {
            for (Map<String, Object> flightInfo : flightinfolistMap) {
                //复制 FlightOrder信息
                copyMapIfNotNull(getMap(flightInfo, "order"), content, new String[]{"aairport", "eairport", "dairport", "acity", "dcity"});

                //合并PassengerInfo信息
                List<Map<String, Object>> passengers = getList(flightInfo, "passengerlist");
                if (passengers != null && passengers.size() > 0) {
                    StringBuilder names = new StringBuilder("|");
                    StringBuilder nations = new StringBuilder("|");
                    StringBuilder cards = new StringBuilder("|");
                    for (Map<String, Object> p : passengers) {
                        if (getString(p, "passengername") != null) {
                            names.append(getString(p, "passengername")).append("|");
                        }
                        if (getString(p, "passengernationality") != null) {
                            nations.append(getString(p, "passengernationality")).append("|");
                        }
                        if (getString(p, "passengercardid") != null) {
                            cards.append(getString(p, "passengercardid")).append("|");
                        }
                    }
                    setValue(content, "passengername", names.toString());
                    setValue(content, "passengernationality", nations.toString());
                    setValue(content, "passengercardid", cards.toString());
                }
            }
        }
    }
}

package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by thyang on 2015-07-06.
 */
@Component
public class POConverterEx extends ConverterBase {

    private void fillVacationInfo(List<Map<String, Object>> vacationinfolist, List<Map<String, Object>> vacationotherinfolist, List<Map<String, Object>> vactioninfolistMap, String vacationType) {
        if (vactioninfolistMap != null && vactioninfolistMap.size() > 0) {
            for (Map<String, Object> vacationMap : vactioninfolistMap) {
                Map<String, Object> vacation = new HashMap<String, Object>();
                Map<String, Object> order = new HashMap<String, Object>();
                copyMap(vacationMap, order, "infosecurity_vacationinfo");
                setValue(vacationMap, "vacationtype", vacationType);
                setValue(vacation, "order", order);

                //机票订单附加信息
                Map<String, Object> otherinfo = new HashMap<String, Object>();
                copyMap(vacationMap, otherinfo, new String[]{"orderid", "amount", "prepaytype"});
                vacationotherinfolist.add(otherinfo);

                List<Map<String, Object>> userlist = new ArrayList<Map<String, Object>>();
                List<Map<String, Object>> userInfolistMap = getList(vacationMap, "vacationuserinfolist");

                if (userInfolistMap != null && userInfolistMap.size() > 0) {
                    for (Map<String, Object> userMap : userInfolistMap) {
                        Map<String, Object> vu = new HashMap<String, Object>();
                        copyMap(userMap, vu, "infosecurity_vacationuserinfo");
                        userlist.add(vu);
                    }
                }
                setValue(vacation, "userlist", userlist);

                List<Map<String, Object>> optionListMap = getList(vacationMap, "optionitems");
                List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
                if (optionListMap != null && optionListMap.size() > 0) {
                    for (Map<String, Object> optMap : optionListMap) {
                        Map<String, Object> opt = new HashMap<String, Object>();
                        copyMap(optMap, opt, "infosecurity_vacationoptioninfo");
                        optlist.add(opt);
                    }
                }
                setValue(vacation, "optionlist", optlist);
                vacationinfolist.add(vacation);
            }
        }
    }

    /**
     * 填充DIY TDD信息，TDD信息置于 VacationInfoList
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillDIYVacationInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> vacationinfolist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> vacationotherinfolist = new ArrayList<Map<String, Object>>();
        fillVacationInfo(vacationinfolist, vacationotherinfolist, getList(eventBody, "diyticketsinfos"), "TKT");
        fillVacationInfo(vacationinfolist, vacationotherinfolist, getList(eventBody, "diyinsuranceinfos"), "IRE");
        fillVacationInfo(vacationinfolist, vacationotherinfolist, getList(eventBody, "diyvisainfos"), "VIA");
        setValue(productInfo, "vacationinfolist", vacationinfolist);
        setValue(productInfo, "vacationotherinfolist", vacationotherinfolist);
    }


    /**
     * 填充VactionInfoList
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillVacationInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody, int orderType) {
        List<Map<String, Object>> vacationinfolist = new ArrayList<Map<String, Object>>();
        Map<String, Object> order = new HashMap<String, Object>();
        copyMap(eventBody, order, "infosecurity_vacationinfo");

        List<Map<String, Object>> userlist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> userInfolistMap = getList(eventBody, "userinfos");

        if (userInfolistMap != null && userInfolistMap.size() > 0) {
            for (Map<String, Object> userMap : userInfolistMap) {
                Map<String, Object> vu = new HashMap<String, Object>();
                if(CtripOrderType.Car.getCode()==orderType){
                    copyValue(userMap, "visitorcontactinfo", vu, "visitorcontactinfo");
                    copyValue(userMap, "visitorcardno", vu, "visitoridcode");
                    copyValue(userMap, "visitorname", vu, "visitorname");
                    copyValue(userMap, "visitornationality", vu, "visitornationality");
                    copyValue(userMap, "idtype", vu, "visitoridcardtype");
                    copyValue(userMap, "idtypename", vu, "visitoridcardtypename");
                    copyValue(userMap, "cityid", vu, "cityid");
                    copyValue(userMap, "cityname", vu, "cityname");
                    copyValue(userMap, "corpuserid", vu, "corpuserid");
                    copyValue(userMap, "passengertype", vu, "visitortype");
                } else {
                    copyMapIfNotNull(userMap, vu, new String[]{"visitorname", "visitorcontactinfo", "visitornationality", "visitoridcardtype"});
                    if (CtripOrderType.HotelGroup.getCode() == orderType) {
                        copyValue(userMap, "idtype", vu, "visitoridcardtype");
                    }
                    if (CtripOrderType.TTD.getCode() == orderType
                            || CtripOrderType.CRH.getCode() == orderType
                            || CtripOrderType.HotelGroup.getCode() == orderType
                            || CtripOrderType.Cruise.getCode()==orderType
                            || CtripOrderType.CruiseByTianHai.getCode()==orderType
                            || CtripOrderType.HHTravel.getCode()==orderType) {
                        copyValue(userMap, "visitorcardno", vu, "visitoridcode");
                    }
                }
                userlist.add(vu);
            }
        }
        List<Map<String, Object>> optionListMap = getList(eventBody, "optionitems");
        List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
        if (optionListMap != null && optionListMap.size() > 0) {
            for (Map<String, Object> optMap : optionListMap) {
                Map<String, Object> opt = new HashMap<String, Object>();
                copyMap(optMap, opt, "infosecurity_vacationoptioninfo");
                optlist.add(opt);
            }
        }
        vacationinfolist.add(ImmutableMap.of("order", order, "userlist", userlist, "optionlist", optlist));

        setValue(productInfo, "vacationinfolist", vacationinfolist);
    }

    public void fillDIYResourceXInfo(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> resourceList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> resourceListMap = getList(eventBody, "diyresourcexinfos");
        if (resourceListMap != null && resourceListMap.size() > 0) {
            for (Map<String, Object> resouceMap : resourceListMap) {
                Map<String, Object> resource = new HashMap<String, Object>();
                copyMap(resouceMap, resource, "infosecurity_diyresourcexinfo");
            }
        }
        setValue(productInfo, "diyresourcexlist", resourceList);
    }

    private void fillADCityNameProvince(Map<String, Object> target, String acity, String dcity) {
        Map<String, Object> map = checkRiskDAO.getCityNameProvince(acity);
        if (map != null) {
            setValue(target, "acityname", getString(map, "cityname"));
            setValue(target, "acityprovince", getString(map, "provincename"));
        }
        if (!StringUtils.equals(acity, dcity)) {
            map = checkRiskDAO.getCityNameProvince(dcity);
        }
        if (map != null) {
            setValue(target, "dcityname", getString(map, "cityname"));
            setValue(target, "dcityprovince", getString(map, "provincename"));
        }
    }

    /**
     * DIY的机票信息
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillDIYFlightInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> flightinfolist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> flightotherinfolist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> flightinfolistMap = getList(eventBody, "diyflightsinfos");
        if (flightinfolistMap != null && flightinfolistMap.size() > 0) {
            for (Map<String, Object> flightMap : flightinfolistMap) {
                Map<String, Object> flightinfo = new HashMap<String, Object>();

                //订单信息
                Map<String, Object> order = new HashMap<String, Object>();
                copyMap(flightMap, order, "infosecurity_flightsorderinfo");
                fillADCityNameProvince(order, getString(order, "acity"), getString(order, "dcity"));
                setValue(flightinfo, "order", order);

                //机票订单附加信息
                Map<String, Object> otherinfo = new HashMap<String, Object>();
                copyMap(flightMap, otherinfo, new String[]{"orderid", "amount", "prepaytype"});
                flightotherinfolist.add(otherinfo);

                //乘客信息
                List<Map<String, Object>> passengerList = new ArrayList<Map<String, Object>>();
                setValue(flightinfo, "passengerlist", passengerList);
                List<Map<String, Object>> passengerListMap = getList(flightMap, "passengerinfolist");
                if (passengerListMap != null && passengerListMap.size() > 0) {
                    for (Map<String, Object> pMap : passengerListMap) {
                        Map<String, Object> p = new HashMap<String, Object>();
                        copyMap(pMap, p, "infosecurity_passengerinfo");
                        copyValue(pMap, "passengercardno", p, "passengercardid");
                        copyValue(pMap, "passengercardnotype", p, "passengercardidtype");
                        passengerList.add(p);
                    }
                }

                //航程段信息
                List<Map<String, Object>> segmentList = new ArrayList<Map<String, Object>>();
                setValue(flightinfo, "segmentlist", segmentList);
                List<Map<String, Object>> segmentListMap = getList(flightMap, "segmentinfolist");
                if (segmentListMap != null && segmentListMap.size() > 0) {
                    for (Map<String, Object> sMap : segmentListMap) {
                        Map<String, Object> s = new HashMap<String, Object>();
                        copyMap(sMap, s, "infosecurity_segmentinfo");
                        setValue(s, "acity", checkRiskDAO.getCityCode(getString(s, "aairport")));
                        setValue(s, "dcity", checkRiskDAO.getCityCode(getString(s, "dairport")));
                        segmentList.add(s);
                    }
                }
                flightinfolist.add(flightinfo);
            }
        }
        setValue(productInfo, "flightinfolist", flightinfolist);
        setValue(productInfo, "flightotherinfolist", flightotherinfolist);
    }

    /**
     * 填充FlightInfoList
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillFlightInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> flightinfolist = new ArrayList<Map<String, Object>>();
        Map<String, Object> flightinfo = new HashMap<String, Object>();
        Map<String, Object> order = new HashMap<String, Object>();
        copyMap(eventBody, order, "infosecurity_flightsorderinfo");
        fillADCityNameProvince(order, getString(order, "acity"), getString(order, "dcity"));
        setValue(flightinfo, "order", order);

        List<Map<String, Object>> passengerList = new ArrayList<Map<String, Object>>();
        setValue(flightinfo, "passengerlist", passengerList);

        List<Map<String, Object>> passengerListMap = getList(eventBody, "passengerinfolist");
        if (passengerListMap != null && passengerListMap.size() > 0) {
            for (Map<String, Object> pMap : passengerListMap) {
                Map<String, Object> p = new HashMap<String, Object>();
                copyMap(pMap, p, "infosecurity_passengerinfo");
                copyValue(pMap, "passengercardno", p, "passengercardid");
                passengerList.add(p);
            }
        }

        List<Map<String, Object>> segmentList = new ArrayList<Map<String, Object>>();
        setValue(flightinfo, "segmentlist", segmentList);
        List<Map<String, Object>> segmentListMap = getList(eventBody, "segmentinfolist");
        if (segmentListMap != null && segmentListMap.size() > 0) {
            for (Map<String, Object> sMap : segmentListMap) {
                Map<String, Object> s = new HashMap<String, Object>();
                copyMap(sMap, s, "infosecurity_segmentinfo");
                setValue(s, "acity", checkRiskDAO.getCityCode(getString(s, "aairport")));
                setValue(s, "dcity", checkRiskDAO.getCityCode(getString(s, "dairport")));
                segmentList.add(s);
            }
        }
        flightinfolist.add(flightinfo);
        setValue(productInfo, "flightinfolist", flightinfolist);
    }

    /**
     * 机票记录 Auth_CCARD_Order
     *
     * @param product
     * @param paymentInfo
     */
    public void fillAuthCCardInfo(Map<String, Object> product, Map<String, Object> paymentInfo) {
        Map<String, Object> auth_ccard = new HashMap<String, Object>();
        setValue(auth_ccard, "orderid", getString(product, new String[]{"maininfo", "orderid"}));
        setValue(auth_ccard, "ordertype", getString(product, new String[]{"maininfo", "ordertype"}));
        setValue(auth_ccard, "createdate", getString(product, new String[]{"maininfo", "createdate"}));
        setValue(auth_ccard, "authstatus", 0);
        List<Map<String, Object>> paymentInfoList = getList(paymentInfo, "paymentinfolist");
        if (paymentInfoList != null && paymentInfoList.size() > 0) {
            for (Map<String, Object> pay : paymentInfoList) {
                String prepayType = getString(pay, new String[]{"payment", "prepaytype"});
                setValue(auth_ccard, "prepaytype", prepayType);
                if ("CCARD".equals(prepayType) || "DCARD".equals(prepayType) || "DQPAY".equals(prepayType)) {
                    List<Map<String, Object>> cards = getList(pay, "cardinfolist");
                    if (cards != null && cards.size() > 0) {
                        copyValue(cards.get(0), "isforeigncard", auth_ccard, "isforeigncard");
                        break;
                    }
                }
            }
        }
        setValue(product, "orderccard", auth_ccard);
    }

    /**
     * 计算机票负利润
     *
     * @param productInfo
     * @param paymentInfo
     */
    public void fillFligtProfit(Map<String, Object> productInfo, Map<String, Object> paymentInfo, int checkType) {
        //CheckType!=0才计算利润
        if (checkType == 0) {
            return;
        }
        List<Map<String, Object>> paymentInfoList = getList(paymentInfo, "paymentinfolist");
        List<Map<String, Object>> flightInfoList = getList(productInfo, "flightinfolist");
        Map<String, Object> maininfo = getMap(productInfo, "maininfo");
        if (paymentInfoList != null && paymentInfoList.size() > 0) {
            for (Map<String, Object> p : paymentInfoList) {
                Map<String, Object> payment = getMap(p, "payment");
                if (payment != null && getString(payment, "prepaytype", "").toUpperCase().equals("TMPAY")) {
                    double paymentAmount = Double.parseDouble(getString(payment, "amount", "0"));
                    if (flightInfoList != null && flightInfoList.size() > 0) {
                        Map<String, Object> flightOrder = getMap(flightInfoList.get(0), "order");
                        if (flightOrder != null && flightOrder.size() > 0) {
                            double flightPrice = Double.parseDouble(getString(flightOrder, "flightprice", "0"));
                            double flightCost = Double.parseDouble(getString(flightOrder, "flightcost", "0"));
                            double packageattachfee = Double.parseDouble(getString(flightOrder, "packageattachfee", "0"));
                            double insurance_fee = Double.parseDouble(getString(flightOrder, "insurance_fee", "0"));
                            if (flightPrice >= flightCost) {
                                setValue(flightOrder, "profit", flightPrice - flightCost + packageattachfee * 0.7 + insurance_fee * 0.9 - paymentAmount * 0.01);
                                setValue(flightOrder, "twopercentprofit", flightPrice - flightCost + packageattachfee * 0.7 + insurance_fee * 0.9 - paymentAmount * 0.02);
                                if (maininfo != null) {
                                    setValue(flightOrder, "actualamount", Double.parseDouble(Objects.toString(getString(maininfo, "amount"), "0")) + insurance_fee + packageattachfee);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public void fillRailInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> railInfos = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> railInfoListMap = getList(eventBody, "crhorderinfos");
        if (railInfoListMap != null && railInfoListMap.size() > 0) {
            for (Map<String, Object> railMap : railInfoListMap) {
                Map<String, Object> rail = new HashMap<String, Object>();
                Map<String, Object> user = new HashMap<String, Object>();
                copyMap(railMap, rail, "infosecurity_exrailinfo");
                copyMap(railMap, user, "infosecurity_exrailuserinfo");
                Map<String, Object> railInfo = new HashMap<String, Object>();
                setValue(railInfo, "rail", rail);
                setValue(railInfo, "user", user);
                railInfos.add(railInfo);
            }
        }
        setValue(productInfo, "railinfolist", railInfos);
    }

    public void fillHotelGroupInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> hotelGroupInfos = new ArrayList<Map<String, Object>>();
        //List报文,新业务为List
        List<Map<String, Object>> hoteGroupInfoListMap = getList(eventBody, "groupinfos");
        if (hoteGroupInfoListMap != null && hoteGroupInfoListMap.size() > 0) {
            for (Map<String, Object> hotelGroupMap : hoteGroupInfoListMap) {
                Map<String, Object> hotelgroupInfo = new HashMap<String, Object>();
                copyMap(hotelGroupMap, hotelgroupInfo, "infosecurity_hotelgroupinfo");
                hotelGroupInfos.add(hotelgroupInfo);
            }
        } else {
            //老报文，在eventBody里面
            Map<String, Object> hotelGroup = new HashMap<String, Object>();
            copyMap(eventBody, hotelGroup, "infosecurity_hotelgroupinfo");
            setValue(hotelGroup, "ismainproduct", "T");
            hotelGroupInfos.add(hotelGroup);
        }
        setValue(productInfo, "hotelgroupinfolist", hotelGroupInfos);
    }


    /**
     * TDD 特殊处理
     *
     * @param po
     * @param eventBody
     */
    public void flightSpecial(PO po, Map<String, Object> eventBody) {

        Map<String, Object> productInfo = po.getProductinfo();
        Map<String, Object> paymentInfo = po.getPaymentinfo();

        //CheckType=0, PaymentMainInfo取上次，FlightOrderInfo取上次
        if (po.getChecktype() == 0) {
            Map<String, Object> tmpPay = checkRiskDAO.getLastPaymentInfo(getString(eventBody, "orderid"), getString(eventBody, "ordertype"), getString(eventBody, "merchantorderid"));
            if (tmpPay != null) {
                Map<String, Object> paymentInfoOld = mapper.fromJson(getString(tmpPay, "content"), HashMap.class);
                if (paymentInfoOld != null) {
                    setValue(paymentInfo, "paymentmaininfo", getMap(paymentInfoOld, "paymentmaininfo"));
                }
            }
            Map<String, Object> tmpProduct = checkRiskDAO.getLastProductInfo(getString(eventBody, "orderid"), getString(eventBody, "ordertype"), getString(eventBody, "merchantorderid"));
            if (tmpProduct != null) {
                Map<String, Object> productInfoOld = mapper.fromJson(getString(tmpProduct, "content"), HashMap.class);
                if (productInfoOld != null) {
                    List<Map<String, Object>> flightinfolistOld = getList(productInfoOld, "flightinfolist");
                    Map<String, Object> orderOld = null;
                    if (flightinfolistOld != null && flightinfolistOld.size() > 0) {
                        orderOld = getMap(flightinfolistOld.get(0), "order");
                    }
                    List<Map<String, Object>> flightsCurrent = getList(productInfo, "flightinfolist");
                    if (flightsCurrent != null && flightsCurrent.size() > 0 && orderOld != null) {
                        setValue(flightsCurrent.get(0), "order", orderOld);
                    }
                }
            }
        }
        /**
         * 计算 SignupToOrderDate
         */
        List<Map<String, Object>> flightinfolist = getList(productInfo, "flightinfolist");
        if (flightinfolist != null && flightinfolist.size() > 0) {
            Map<String, Object> order = getMap(flightinfolist.get(0), "order");
            setValue(getMap(productInfo, "otherinfo"), "takeofftoorderdate",
                    dateDiffHour(getString(order, "takeofftime"), getString(productInfo, new String[]{"maininfo", "orderdate"})));
            String amt = getString(productInfo, new String[]{"maininfo", "amount"});
            List<Map<String, Object>> passengers = getList(flightinfolist.get(0), "passengerlist");
            if (NumberUtils.isNumber(amt) && passengers != null && passengers.size() > 0) {
                setValue(order, "leafletamount", Double.parseDouble(amt) / passengers.size());
            }
        }
    }

    /**
     * TDD 特殊处理
     *
     * @param po
     * @param eventBody
     */
    public void tddSpecial(PO po, Map<String, Object> eventBody) {
        processIsprepaId(po, eventBody);
    }

    private void processIsprepaId(PO po, Map<String, Object> eventBody) {
        // CheckType =1时，带有是否预付 IsPrepaID
        // CheckType =2时，支付时带有其它信息，从上次取 IsPrepaID
        Map<String, Object> paymentMainInfo = getMap(po.getPaymentinfo(), "paymentmaininfo");
        if (paymentMainInfo == null) {
            paymentMainInfo = new HashMap<String, Object>();
            setValue(po.getPaymentinfo(), "paymentmaininfo", paymentMainInfo);
        }
        if (po.getChecktype() == 1) {
            copyValueIfNotNull(eventBody, "isprepaid", paymentMainInfo, "isprepaid"); //取当前isprepaid
        } else if (po.getChecktype() == 2) {
            Map<String, Object> tmpPay = checkRiskDAO.getLastPaymentInfo(getString(eventBody, "orderid"), getString(eventBody, "ordertype"), getString(eventBody, "merchantorderid"));
            if (tmpPay != null) {
                Map<String, Object> paymentInfo = mapper.fromJson(getString(tmpPay, "content"), HashMap.class);
                copyValueIfNotNull(getMap(paymentInfo, "paymentmaininfo"), "isprepaid", paymentMainInfo, "isprepaid"); //取上次的 isprepaid
            }
        }
    }

    /**
     * 主要处理预付标志 isprepaId
     *
     * @param po
     * @param eventBody
     */
    public void hotelSpecial(PO po, Map<String, Object> eventBody) {
        processIsprepaId(po, eventBody);
    }


    public void fillHotelInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> hotelinfolist = new ArrayList<Map<String, Object>>();
        Map<String, Object> hotel = new HashMap<String, Object>();
        //酒店信息，单条记录
        copyMap(eventBody, hotel, "infosecurity_hotelinfo");
        copyValue(eventBody,"orderid",hotel,"hotelorderid");
        hotelinfolist.add(hotel);
        setValue(productInfo, "hotelinfolist", hotelinfolist);
    }

    public void fillDIYHotelInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> hotelinfolist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> hotelotherinfolist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> hotellistMap = getList(eventBody, "diyhotelsinfos");
        if (hotellistMap != null && hotellistMap.size() > 0) {
            for (Map<String, Object> hotelMap : hotellistMap) {
                //酒店信息
                Map<String, Object> hotel = new HashMap<String, Object>();
                copyMap(hotelMap, hotel, "infosecurity_hotelinfo");
                hotelinfolist.add(hotel);
                //酒店其它信息
                Map<String, Object> otherinfo = new HashMap<String, Object>();
                copyMap(hotelMap, otherinfo, new String[]{"orderid", "amount", "prepaytype"});
                hotelotherinfolist.add(otherinfo);
            }
        }
        setValue(productInfo, "hotelinfolist", hotelinfolist);
        setValue(productInfo, "hotelotherinfolist", hotelotherinfolist);
    }


    public void fillGiftItemList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> giftList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> giftListMap = getList(eventBody, "giftlist");
        if (giftListMap != null && giftListMap.size() > 0) {
            for (Map<String, Object> giftMap : giftListMap) {
                Map<String, Object> gift = new HashMap<String, Object>();
                copyMap(giftMap, gift, "infosecurity_giftitem");
                giftList.add(gift);
            }
        }
        setValue(productInfo, "giftitemlist", giftList);
    }

    public void fillDevoteInfoViewByJiFen(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String, Object> devoteInfoMap = getMap(eventBody, "devoterinfoview");
        Map<String, Object> devote = new HashMap<String, Object>();
        copyMap(devoteInfoMap, devote, "infosecurity_devoterinfoviewbyjifen");
        setValue(productInfo, "devoteinfoviewbyjifen", devote);
    }

    public void fillDistribution(Map<String, Object> productInfo, Map<String, Object> eventBody, int orderType) {
        Map<String, Object> distribut = new HashMap<String, Object>();
        if (orderType == CtripOrderType.CRH.getCode()) {
            copyMap(getMap(eventBody, "distributioninfo"), distribut, "infosecurity_distributioninfo");
        } else if (orderType == CtripOrderType.JiFen.getCode()) {
            Map<String, Object> contactMap = getMap(eventBody, "deliveryinfoview");
            if (contactMap != null && contactMap.size() > 0) {
                copyValue(contactMap, "city", distribut, "city");
                copyValue(contactMap, "address", distribut, "distributionadress");
                copyValue(contactMap, "recipient", distribut, "distributionrecipient");
                copyValue(contactMap, "zipcode", distribut, "distributionzipcode");
                copyValue(contactMap, "district", distribut, "district");
                copyValue(contactMap, "province", distribut, "province");
                copyValue(contactMap, "telno", distribut, "telno");
                copyValue(contactMap, "ishistoryaddress", distribut, "ishistoryaddress");
            }
        }
        setValue(productInfo, "distribution", distribut);
    }

    public void fillJifenOrderItemList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> jifenList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> jifenListMap = getList(eventBody, "suborderlist");
        if (jifenListMap != null && jifenListMap.size() > 0) {
            for (Map<String, Object> jifenMap : jifenListMap) {
                Map<String, Object> jifen = new HashMap<String, Object>();
                //SubOrder
                Map<String, Object> order = new HashMap<String, Object>();
                copyValue(jifenMap, "quantity", jifen, "quantity");
                setValue(jifen, "order", order);
                //Greeting Card
                Map<String, Object> greetMap = getMap(jifenMap, "greetingcardinfoview");
                if (greetMap != null && greetMap.size() > 0) {
                    Map<String, Object> greet = new HashMap<String, Object>();
                    copyMap(greetMap, greet, "infosecurity_greetingcardinfoviewbyjifen");
                    setValue(jifen, "greetingcard", greet);
                }
                //Prize Detail
                Map<String, Object> prizeMap = getMap(jifenMap, "prizedetail");
                if (prizeMap != null && prizeMap.size() > 0) {
                    Map<String, Object> prize = new HashMap<String, Object>();
                    copyMap(prizeMap, prize, "infosecurity_prizedetailitembyjifen");
                    copyValue(prizeMap,"categoryid",prize,"prizecategoryid");
                    copyValue(prizeMap,"categoryname",prize,"prizecategoryname");
                    copyValue(prizeMap,"name",prize,"prizename");
                    copyValue(prizeMap,"permission",prize,"prizepermission");
                    copyValue(prizeMap,"status",prize,"prizestatus");
                    copyValue(prizeMap,"storage",prize,"prizestorage");
                    copyValue(prizeMap,"storagemanagetype",prize,"prizestoragemanagetype");
                    copyValue(prizeMap,"subcategoryid",prize,"prizesubcategoryid");
                    copyValue(prizeMap,"subtypecode",prize,"prizesubtypecode");
                    copyValue(prizeMap,"subtypevalue",prize,"prizesubtypevalue");
                    copyValue(prizeMap,"zonecode",prize,"prizezonecode");
                    copyValue(prizeMap,"zoneid",prize,"prizezoneid");
                    setValue(jifen, "prizedetail", prize);
                }
                Map<String, Object> paymentMap = getMap(jifenMap, "paymentwayview");
                if (paymentMap != null && paymentMap.size() > 0) {
                    Map<String, Object> payment = new HashMap<String, Object>();
                    copyMap(paymentMap, payment, "infosecurity_paymentitemviewbyjifen");
                    setValue(jifen, "paymentitem", payment);
                }
                jifenList.add(jifen);

            }
        }
        setValue(productInfo, "jifenorderitemlist", jifenList);
    }


    public void fillWalletWithdrawal(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String,Object> withdrawal=new HashMap<String, Object>();
        copyMap(eventBody,withdrawal,"infosecurity_walletwithdrawal");
        setValue(productInfo,"walletwithdrawal",withdrawal);
    }

    public void fillCHProduct(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String,Object> chproduct=new HashMap<String, Object>();
        copyMap(eventBody,chproduct,"infosecurity_chproduct");
        setValue(productInfo,"chproduct",chproduct);
    }

    public void fillInvoiceInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String,Object>> invoiceinfolist=new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> invoiceinfolistMap=getList(eventBody,"invoicelistinfos");
        if(invoiceinfolistMap!=null && invoiceinfolistMap.size()>0){
            for(Map<String,Object> invoiceMap:invoiceinfolistMap){
                Map<String,Object> invoice=new HashMap<String, Object>();
                copyValue(invoiceMap,"detail",invoice,"invoicecontent");
                copyValue(invoiceMap,"title",invoice,"invoicehead");
                copyValue(invoiceMap,"receivertel",invoice,"invoicephone");
                copyValue(invoiceMap,"receivername",invoice,"fullname");
                copyValue(invoiceMap,"zipcode",invoice,"zipcode");
                copyValue(invoiceMap,"province",invoice,"deliveryprovince");
                copyValue(invoiceMap,"city",invoice,"deliverycity");
                copyValue(invoiceMap,"addressdetail",invoice,"deliverydistrict");
                copyValue(invoiceMap,"costtype",invoice,"costtype");
                copyValue(invoiceMap,"costvalue",invoice,"costvalue");
                copyValue(invoiceMap,"area",invoice,"deliveryarea");
                invoiceinfolist.add(invoice);
            }
        }
       setValue(productInfo,"invoiceinfolist",invoiceinfolist);
    }

    public void fillCoupons(Map<String, Object> productInfo, Map<String, Object> eventBody) {
         Map<String,Object> coupons=new HashMap<String, Object>();
        copyMap(eventBody,coupons,"infosecurity_couponsinfo");
        setValue(productInfo,"coupons",coupons);
    }

    public void fillCustomer(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String,Object> customer=new HashMap<String, Object>();
        copyMap(eventBody,customer,"infosecurity_customerinfo");
        setValue(productInfo,"customer",customer);
    }

    public void fillSMSVerify(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String,Object> sms=new HashMap<String, Object>();
        copyValue(eventBody,"smsverifystatus",sms,"smsverifystatus");
        setValue(productInfo, "smsverify",sms);
    }

    public void fillTianHai(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String,Object> tianhai=new HashMap<String, Object>();
        copyMap(eventBody, tianhai, "infosecurity_vacationbytianhaiinfo");
        setValue(productInfo, "tianhai",tianhai);
    }

    public void fillCurrencyExchange(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        Map<String,Object> currency=new HashMap<String, Object>();
        copyMap(eventBody,currency,"infosecurity_currencyexchange");
        copyMap(getMap(eventBody,"acurrencyinfo"),currency,ImmutableMap.of("amount","acamount","currency","acurrency"));
        copyMap(getMap(eventBody,"dcurrencyinfo"),currency,ImmutableMap.of("amount","dcamount","currency","dcurrency"));
        copyMap(getMap(eventBody,"ecurrencyinfo"),currency,ImmutableMap.of("amount","ecamount","currency","ecurrency"));
        setValue(productInfo,"currencyexchange",currency);
    }
}

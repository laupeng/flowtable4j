package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by thyang on 2015-07-04.
 */
@Component
public class FlowConverterEx extends ConverterBase {

    public void fillFlightInfoList(Map<String,Object> productInfo,Map<String,Object> target){
        List<Map<String,Object>> flightInfolistMap =getList(productInfo, "flightinfolist");
        if(flightInfolistMap!=null && flightInfolistMap.size()>0){
            for(Map<String,Object> flightInfoMap :flightInfolistMap){
                Map<String,Object> order= getMap(flightInfoMap, "order");
                if(order!=null && order.size()>0){
                    copyMapIfNotNull(order, target, new String[]{
                            "profit", "flightcostrate", "agencyname",
                            "isclient", "flightclass", "dairport", "aairport", "eairport", "remark",
                            "balancetype", "reservationtype", "onlysingleway", "pricetype", "bookingchannel",
                            "isenglish", "isabacus", "realreservationtype", "targetorder", "c_language", "ispartial",
                            "needemail", "needfax", "needmorebagage", "needhotel", "trackidcard", "remittance",
                            "sendemailinfo", "sendfaxinfo", "exceedlimit", "withotherorders", "withinfantticket",
                            "agencyid", "insurance_fee"
                    });

                    concatKeys(target,"dairportaairport","dairport","aairport");

                    //获取起飞、落地城市、省、国家
                    Map<String,Object> actityMap = checkRiskDAO.getCityNameProvince(getString(order,"acity"));
                    if(actityMap!=null && actityMap.size()>0){
                        setValueIfNotEmpty(target,"acityname",getString(actityMap,"cityname"));
                        setValueIfNotEmpty(target,"acityprovince",getString(actityMap,"provincename"));
                        String aCountry = getString(actityMap, "country");
                        setValueIfNotEmpty(target,"acountrycode",aCountry);
                        if(!Strings.isNullOrEmpty(aCountry)){
                            Map<String,Object> nationMap= checkRiskDAO.getCountryNameNationality(aCountry);
                            if(nationMap!=null && nationMap.size()>0){
                                setValueIfNotEmpty(target,"acountrynationality",getString(nationMap,"nationality"));
                            }
                        }
                    }
                    if(!StringUtils.equals(getString(order,"acity"),getString(order,"dcity"))) {
                        actityMap = checkRiskDAO.getCityNameProvince(getString(order, "dcity"));
                    }
                    if(actityMap!=null && actityMap.size()>0){
                        setValueIfNotEmpty(target,"dcityname",getString(actityMap,"cityname"));
                        setValueIfNotEmpty(target,"dcityprovince",getString(actityMap,"provincename"));
                        String aCountry = getString(actityMap, "country");
                        setValueIfNotEmpty(target,"dcountrycode",aCountry);
                        if(!Strings.isNullOrEmpty(aCountry)){
                            Map<String,Object> nationMap= checkRiskDAO.getCountryNameNationality(aCountry);
                            if(nationMap!=null && nationMap.size()>0){
                                setValueIfNotEmpty(target,"dcountrynationality",getString(nationMap,"nationality"));
                            }
                        }
                    }
                    setValueIfNotEmpty(target,"mergeddacityname",String.format("|%s|%s|",getString(target,"acityname"),getString(target,"dcityname")));
                }

                List<Map<String,Object>>  segments= getList(flightInfoMap,"segmentlist");
                if (segments != null && segments.size() > 0) {
                    setValue(target, "segmentinfocount", segments.size());
                    copyMapIfNotNull(segments.get(0), target, new String[]{"seatclass", "sequence", "flight", "pataresult", "isshared", "vehicletype"});
                }

                List<Map<String,Object>>  passengerlistMap= getList(flightInfoMap,"passengerlist");

                if(passengerlistMap!=null && passengerlistMap.size()>0){
                    List<Map<String,Object>> passengerlist = new ArrayList<Map<String, Object>>();
                    setValue(target,"passengerlist",passengerlist);
                    setValue(target,"passengercount",passengerlistMap.size());
                    StringBuilder mergeName =new StringBuilder("|");
                    StringBuilder mergeNationality = new StringBuilder("|");
                    StringBuilder mergeCardId = new StringBuilder("|");
                    StringBuilder mergeCardIdLength = new StringBuilder("|");

                    //含有非15、16位的证件
                    String cardIdNotCN ="F";
                    //证件省份与手机、起飞到达城市相比
                    String idProvinceSameAsMobile ="F";
                    String idProvinceSameAsDACity ="F";
                    //身份证前3位、前6位
                    Set<String> id3Set = new HashSet<String>();
                    Set<String> id6Set = new HashSet<String>();
                    Set<String> nationalitySet = new HashSet<String>();
                    String uid = getString(productInfo,new String[]{"userinfo","uid"});
                    for(Map<String,Object> passengerMap :passengerlistMap){

                        Map<String,Object> passenger = new HashMap<String, Object>();

                        String cardId = getString(passengerMap,"passengercardid","");
                        mergeCardId.append(cardId).append("|");
                        mergeCardIdLength.append(cardId.length()).append("|");
                        if(cardId.length()>3)
                        {
                            id3Set.add(cardId.substring(0, 3));
                        }
                        if(cardId.length()>6) {
                            String id6=cardId.substring(0, 6);
                            id6Set.add(id6);
                            setValue(passenger,"passengercardid6",id6);
                        }
                        if(cardId.length()!=15 && cardId.length()!=18){
                            cardIdNotCN ="T";
                        }

                        mergeName.append(getString(passengerMap, "passengername")).append("|");
                        String nationality = getString(passengerMap,"passengernationality");
                        mergeNationality.append(nationality).append("|");

                        if(!Strings.isNullOrEmpty(nationality)){
                            nationalitySet.add(nationality);
                        }

                        copyMapIfNotNull(passengerMap, passenger, new String[]{
                                "passengername", "passengernationality", "passengercardid",
                                "seatremark","mealremark","credleremark","wheelchairremark"});
                        setValue(passenger,"passengercardidlengthone",cardId.length());
                        concatKeys(passenger, "passengernamecardid", "passengername", "passengercardid");
                        Map<String,Object> idProvinceMap = checkRiskDAO.getIDCardProvince(cardId);
                        if(idProvinceMap!=null && idProvinceMap.size()>0){
                            String idProvince = getString(idProvinceMap,"provincename");
                            if(StringUtils.equals(idProvince, getString(target, "mobilephoneprovince"))){
                                idProvinceSameAsMobile="T";
                            }
                            if(StringUtils.equals(idProvince,getString(target,"acityprovince"))||StringUtils.equals(idProvince,getString(target,"dcityprovince"))){
                                idProvinceSameAsDACity ="T";
                            }
                        }

                        if(!Strings.isNullOrEmpty(uid)){
                            setValueIfNotNull(passenger, "uidpassengername",getString(passengerMap, "passengername"), uid);
                            setValueIfNotNull(passenger, "uidpassengernamecardid",uid,getString(passengerMap, "passengercardid"));
                        }

                        setValueIfNotNull(passenger,"mobilephonepassengercardid",concatValueIfNotNull(target,"mobilephone", passengerMap,"passengercardid"));
                        setValueIfNotNull(passenger,"emailpassengernamecardid",concatValueIfNotNull(target,"contactemail", passengerMap,"passengercardid"));
                        setValueIfNotNull(passenger,"ccardnocodepassengernamecardid",concatValueIfNotNull(target,"ccardnocode", passengerMap,"passengercardid"));
                        setValueIfNotNull(passenger,"cardnorefidpassengernamecardid",concatValueIfNotNull(target,"cardnorefid", passengerMap,"passengercardid"));

                        passengerlist.add(passenger);


                    }
                    String amount = getString(productInfo,new String[]{"maininfo","amount"});
                    if(NumberUtils.isNumber(amount) && passengerlistMap.size()>0){
                        setValue(target, "leafletamount", Double.parseDouble(amount) /1.00/passengerlistMap.size());
                    }

                    setValue(target,"issamepassengernationality",nationalitySet.size()<= 1? "T":"F");
                    setValue(target,"idcardnumberprovincenametomobilephone",idProvinceSameAsMobile);
                    setValue(target,"idcardnumberprovincenametodacity",idProvinceSameAsDACity);

                    setValue(target, "mergerpassengername", mergeName.toString().toUpperCase());
                    setValue(target, "mergerpassengernationality", mergeNationality.toString().toUpperCase());
                    setValue(target, "mergerpassengercardid", mergeCardIdLength.toString().toUpperCase());

                    setValue(target,"passengercardidlength",cardIdNotCN);
                    setValue(target,"mergerpassengercardidlength", mergeCardIdLength.toString());
                    setValue(target,"dcountpassengercardid3",id3Set.size());
                    setValue(target,"dcountpassengercardid6",id6Set.size());
                }
            }
        }
    }

    public void fillFlight(Map<String,Object> target){
            String uid = getString(target, "uid");
            if (!Strings.isNullOrEmpty(uid) && uid.length() >= 10) {
                setValue(target, "uid3to7", uid.substring(2, 9));
                setValue(target, "uid1", uid.substring(0, 1));
            }
            String contactEmail = getString(target, "contactemail","");
            if (!Strings.isNullOrEmpty(contactEmail) && contactEmail.length() > 7) {
                contactEmail = contactEmail.replace(".", "").replace("@", "").replace("_", "");
                if (contactEmail.length() >= 7) {
                    setValue(target, "contactemailtoconvert7", contactEmail.substring(0,7));
                }
            }
            //Email与乘客国籍不一致
            if(contactEmail.contains("@")){
                setValue(target,"contactemailtopassengernationality","T");
                String[] nationality= getString(target,"mergerpassengernationality","").split("[|]");
                String mailSuffix= contactEmail.substring(contactEmail.indexOf("@"));
                for(String s:nationality ){
                    if(mailSuffix.contains(s)){
                        setValue(target,"contactemailtopassengernationality","F");
                        break;
                    }
                }
            }
            setValueIfNotNull(target,"ipprovincecomparedcityprovince",StringUtils.equals(getString(target,"ipprovince"),getString(target,"dcityprovince"))? "T":"F");
            setValueIfNotNull(target,"mobilephoneprovincecomparedcityprovince",StringUtils.equals(getString(target,"mobilephoneprovince"),getString(target,"dcityprovince"))? "T":"F");

            setValueIfNotNull(target,"ipprovincecompareacityprovince",StringUtils.equals(getString(target,"ipprovince"),getString(target,"acityprovince"))? "T":"F");
            setValueIfNotNull(target,"mobilephoneprovincecompareacityprovince",StringUtils.equals(getString(target,"mobilephoneprovince"),getString(target,"acityprovince"))? "T":"F");

            Date now= new Date(System.currentTimeMillis());
            Date from =new Date(System.currentTimeMillis() - 525600 * 60 * 1000);
            setValue(target, "orderdatetoorderdate1ybyuid", -1);
            String firstOrderDate = checkRiskDAO.getUidOrderDate(getString(target,"uid"),sdf.format(from),sdf.format(now));
            if(!Strings.isNullOrEmpty(firstOrderDate)) {
                long span = dateDiffHour(getString(target, "orderdate"), firstOrderDate);
                setValueIfNotNull(target, "orderdatetoorderdate1ybyuid", span);
                setValueIfNotNull(target, "uidipcitylastyear", "F");
            }

            from = new Date(System.currentTimeMillis() - 10080 * 60 * 1000);
            setValueIfNotNull(target, "amounttoavgamount7",checkRiskDAO.getAvgAmount7(getString(target," cardnorefid"),sdf.format(from),sdf.format(now)));
    }

    public void fillFlightProfit(Map<String,Object> paymentInfo,Map<String,Object> productInfo,Map<String,Object> target){
        List<Map<String,Object>> paymentlistMap = getList(paymentInfo,"paymeninfolist");
        if(paymentlistMap!=null && paymentlistMap.size()>0){
            for(Map<String,Object> paymentMap:paymentlistMap) {
                Map<String, Object> payment = getMap(paymentMap, "payment");
                if (payment != null && payment.size() > 0) {
                    double paymentAmount = Double.parseDouble(getString(payment, "amount", "0"));
                    List<Map<String, Object>> flightOrderList = getList(productInfo, "flightinfolist");
                    if (flightOrderList != null && flightOrderList.size() > 0) {
                        Map<String, Object> order = getMap(flightOrderList.get(0), "order");
                        if (order != null && order.size() > 0) {
                            double flightPrice = Double.parseDouble(getString(order, "flightprice", "0"));
                            double flightCost = Double.parseDouble(getString(order, "flightcost", "0"));
                            double packageattachfee = Double.parseDouble(getString(order, "packageattachfee", "0"));
                            double insurance_fee = Double.parseDouble(getString(order, "insurance_fee", "0"));
                            if (flightPrice >= flightCost) {
                                double d = flightPrice - flightCost + packageattachfee * 0.7 + insurance_fee * 0.9;
                                if (d > 0) {
                                    setValue(target, "onepointfivepercentprofit", d - paymentAmount * 0.015);
                                    setValue(target, "twopercentprofit", d - paymentAmount * 0.02);
                                    setValue(target, "threepercentprofit", d - paymentAmount * 0.03);
                                    setValue(target, "fourpercentprofit", d - paymentAmount * 0.04);
                                    setValue(target, "fivepercentprofit", d - paymentAmount * 0.05);

                                    d = flightPrice - flightCost + insurance_fee * 0.9;
                                    setValue(target, "onepointfivepercentprofitnotincludebinded", d - paymentAmount * 0.015);
                                    setValue(target, "twopercentprofitnotincludebinded", d - paymentAmount * 0.02);
                                    setValue(target, "threepercentprofitnotincludebinded", d - paymentAmount * 0.03);
                                    setValue(target, "fourpercentprofitnotincludebinded", d - paymentAmount * 0.04);
                                    setValue(target, "fivepercentprofitnotincludebinded", d - paymentAmount * 0.05);
                                    //maininfo已经处理过
                                    setValue(target, "actualamount", Double.parseDouble(getString(target,"amount", "0")) + insurance_fee + packageattachfee);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void fillRailInfoList(Map<String, Object> productInfo, Map<String, Object> target) {
        List<Map<String,Object>> railListMap= getList(productInfo,"railinfolist");
        if(railListMap!=null && railListMap.size()>0){
            List<Map<String,Object>> passengerlist = new ArrayList<Map<String, Object>>();
            StringBuilder idLength= new StringBuilder("|");
            StringBuilder name = new StringBuilder("|");
            StringBuilder idType = new StringBuilder("|");
            Set<String> id6Set = new HashSet<String>();
            Map<String,Object> rail =null;
            for(Map<String,Object> railMap:railListMap){
                if(rail==null) {
                     rail = getMap(railMap, "rail");
                }
                Map<String,Object> user = getMap(railMap,"user");
                if(user!=null && user.size()>0){
                    Map<String,Object> passgener = new HashMap<String, Object>();
                    copyValueIfNotNull(user,"passengeridcode",passgener,"passengeridcode");
                    String id6 = getString(user,"passengeridcode","");
                    if(id6.length()>6){
                        setValue(passgener,"mergepassengeridcode6",id6.substring(0,6));
                        idLength.append(id6.length()).append("|");
                        id6Set.add(id6.substring(0,6));
                    }
                    String passengerName = getString(user,"passengername");
                    if(passengerName!=null){
                        name.append(passengerName).append("|");
                    }
                    idType.append(getString(user,"passengeridtype","")).append("|");
                    passengerlist.add(passgener);
                }
            }

            copyMap(rail,target,new String[]{"dcity","acity","seatclass"});
            concatKeys(target,"dcityandacity","dcity","acity");
            setValue(target,"ordertodeparturedate",dateDiffHour(getString(rail,"departuredate"),getString(target,"orderdate")));
            setValueIfNotEmpty(target,"acitybysite", riskProfile.getCityByCRHSite(getString(target,"acity")));
            setValueIfNotEmpty(target,"dcitybysite", riskProfile.getCityByCRHSite(getString(target,"dcity")));
            if(id6Set.size()>=4){
                setValue(target,"ispassengeridcount",1);
            }
            setValue(target, "mergerpassengercardidlength",idLength.toString());
            setValue(target,"mergepassengeridtype", idType.toString());
            setValue(target,"mergerpassengername",name.toString());
        }
    }
}

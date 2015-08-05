package com.ctrip.infosec.flowtable4j.biz.subpoconverter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class DIYConverter extends ConverterBase {

    /**
     * DIY的机票信息
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillDIYFlightInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> flightinfolist = createList();
        List<Map<String, Object>> flightotherinfolist = createList();
        List<Map<String, Object>> flightinfolistMap = getList(eventBody, "diyflightsinfos");
        if (flightinfolistMap != null && flightinfolistMap.size() > 0) {
            for (Map<String, Object> flightMap : flightinfolistMap) {
                Map<String, Object> flightinfo =createMap();
                //订单信息
                Map<String, Object> order = copyMap(flightMap, "infosecurity_flightsorderinfo");
                fillADCityNameProvince(order, getString(order, "acity"), getString(order, "dcity"));
                setValue(flightinfo, "order", order);

                //机票订单附加信息
                flightotherinfolist.add(copyMap(flightMap, new String[]{"orderid", "amount", "prepaytype"}));

                //乘客信息
                List<Map<String, Object>> passengerList = createList();
                setValue(flightinfo, "passengerlist", passengerList);
                List<Map<String, Object>> passengerListMap = getList(flightMap, "passengerinfolist");
                if (passengerListMap != null && passengerListMap.size() > 0) {
                    for (Map<String, Object> pMap : passengerListMap) {
                        Map<String, Object> p = createMap();
                        copyMap(pMap, p, "infosecurity_passengerinfo");
                        copyValue(pMap, "passengercardno", p, "passengercardid");
                        copyValue(pMap, "passengercardnotype", p, "passengercardidtype");
                        passengerList.add(p);
                    }
                }

                //航程段信息
                List<Map<String, Object>> segmentList =createList();
                setValue(flightinfo, "segmentlist", segmentList);
                List<Map<String, Object>> segmentListMap = getList(flightMap, "segmentinfolist");
                if (segmentListMap != null && segmentListMap.size() > 0) {
                    for (Map<String, Object> sMap : segmentListMap) {
                        Map<String, Object> s = createMap();
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


    private void fillVacationInfo(List<Map<String, Object>> vacationinfolist, List<Map<String, Object>> vacationotherinfolist, List<Map<String, Object>> vactioninfolistMap, String vacationType) {
        if (vactioninfolistMap != null && vactioninfolistMap.size() > 0) {
            for (Map<String, Object> vacationMap : vactioninfolistMap) {
                Map<String, Object> vacation = createMap();
                Map<String, Object> order = copyMap(vacationMap,"infosecurity_vacationinfo");
                setValue(order,"vacationtype", vacationType);
                setValue(vacation,"order", order);
                //机票订单附加信息
                vacationotherinfolist.add(copyMap(vacationMap, new String[]{"orderid", "amount", "prepaytype"}));
                setValue(vacation, "userlist", copyList(getList(vacationMap,"vacationuserinfolist"),"infosecurity_vacationuserinfo"));
                setValue(vacation, "optionlist", copyList(getList(vacationMap,"optionitems"),"infosecurity_vacationoptioninfo"));
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
        List<Map<String, Object>> vacationinfolist =createList();
        List<Map<String, Object>> vacationotherinfolist =createList();
        fillVacationInfo(vacationinfolist, vacationotherinfolist, getList(eventBody, "diyticketsinfos"), "TKT");
        fillVacationInfo(vacationinfolist, vacationotherinfolist, getList(eventBody, "diyinsuranceinfos"), "IRE");
        fillVacationInfo(vacationinfolist, vacationotherinfolist, getList(eventBody, "diyvisainfos"), "VIA");
        setValue(productInfo, "vacationinfolist", vacationinfolist);
        setValue(productInfo, "vacationotherinfolist", vacationotherinfolist);
    }

}

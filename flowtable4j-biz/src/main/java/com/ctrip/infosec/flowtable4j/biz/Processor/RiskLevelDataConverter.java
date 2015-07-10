package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.accountrule.AccountBWGManager;
import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thyang on 2015-07-10.
 */
@Component
public class RiskLevelDataConverter extends ConverterBase {

    protected static SimpleDateFormat redisSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    AccountBWGManager accountBWGManager;

    private int processUWL(List<CheckResultLog> resultLogs, int maxriskLevel) {
        int level = maxriskLevel;
        boolean startUWL = false;
        if (resultLogs != null && resultLogs.size() > 0) {
            for (CheckResultLog r : resultLogs) {
                if (r.getRuleName() != null && r.getRuleName().startsWith("UWL")) {
                    startUWL = true;
                    break;
                }
            }
        }
        if (startUWL && maxriskLevel <= 194) {
            level = 99;
        }
        return level;
    }

    public void convertRiskLevelData(PO po, RiskResult riskResult, long reqid) {
        Integer maxlevel = 0;
        List<CheckResultLog> results = riskResult.getResults();
        Map<String, Object> riskleveldata = new HashMap<String, Object>();
        setValue(riskleveldata, "reqid", reqid);
        setValue(riskleveldata, "transflag", reqid);
        setValue(riskleveldata, "createdate", sdf.format(System.currentTimeMillis()));
        setValue(riskleveldata, "orderid", po.getOrderid());
        setValue(riskleveldata, "ordertype", po.getOrdertype());
        setValue(riskleveldata, "subordertype", 0);
        setValue(riskleveldata, "originalrisklevel", 0);
        setValue(riskleveldata, "risklevel", 0);
        setValue(riskleveldata, "referenceno", getString(po.getProductinfo(), new String[]{"dealinfo", "referenceid"}));
        setValue(po.getProductinfo(), "riskleveldata", riskleveldata);
        po.setRisklevel(0);

        StringBuilder remark = new StringBuilder();
        if (results != null && results.size() > 0) {
            for (CheckResultLog r : results) {
                int risklevel = r.getRiskLevel();
                maxlevel = Math.max(maxlevel, risklevel);
                if (CheckType.FLOWRULE.toString().equals(r.getRuleType())) {
                    remark.append(r.getRuleName()).append(";");
                } else {
                    remark.append(r.getRuleRemark()).append(";");
                }
            }

            setValue(riskleveldata, "originalrisklevel", maxlevel);
            setValue(riskleveldata, "risklevel", maxlevel);
            setValue(riskleveldata, "remark", remark.toString());
            po.setRisklevel(maxlevel);
        }

        if (po.getOrdertype().equals(CtripOrderType.HotelGroup.getCode())) {
            processHotelGroup(po, riskleveldata, maxlevel, riskResult);
        }

        if (po.getOrdertype().equals(CtripOrderType.Flights.getCode())) {
            processFlight(po, riskleveldata, maxlevel, riskResult);
        }
    }

    private void processFlight(PO po, Map<String, Object> riskleveldata, Integer maxlevel, RiskResult riskResult) {
        List<Map<String, Object>> flightinfoListMap = getList(po.getProductinfo(), "flightinfolist");
         if (flightinfoListMap != null && flightinfoListMap.size() > 0) {
                for (Map<String, Object> flightInfoMap : flightinfoListMap) {
                    Map<String, Object> order = getMap(flightInfoMap, "order");
                    setValue(riskleveldata, "subordertype", getString(order, "subordertype", "0"));
                }
            }
        int finallevel = 0 ;

        String creditCardType = getCreditCardType(po.getPaymentinfo());
        if("11".equals(creditCardType) && maxlevel > 99 && maxlevel < 195){
            finallevel = 99;
            setValue(riskleveldata,"cmbmsgstatus","T");
        }

        String remark = getString(riskleveldata,"remark","");
        String uid =MapX.getString(po.getProductinfo(),new String[]{"userinfo","uid"},"");
        if(remark.contains("UFTMH030004") &&  !uid.equals("")){
            RuleContent rc = new RuleContent();
            rc.setCheckType("UID");
            rc.setCheckValue(uid);
            rc.setResultLevel(295);
            rc.setSceneType("PAYMENT-CONF-LIPIN");
            rc.setExpiryDate(redisSdf.format(System.currentTimeMillis() + 50 * 365 * 24 * 60 * 60 * 1000));
            List<RuleContent> rules = new ArrayList<RuleContent>();
            rules.add(rc);
            accountBWGManager.setBWGRule(rules);
        }

        if(finallevel > 99 ){
            finallevel = processUWL(riskResult.getResults(),maxlevel);
            finallevel = process90_100BWResult(riskResult.getResults(),finallevel);
        }

        if(finallevel>=100 && finallevel <200){
            flightinfoListMap = getList(po.getProductinfo(), "flightinfolist");
            if (flightinfoListMap != null && flightinfoListMap.size() > 0) {
                for (Map<String, Object> flightInfoMap : flightinfoListMap) {
                    Map<String, Object> order = getMap(flightInfoMap, "order");
                    String flightClass = getString(order,"flightclass","");
                    String subOrderType = getString(order,"subordertype","");
                    String urgencyLevel = getString(order,"urgencylevel","");
                    String salesType = getString(order,"salestype","");
                    if(subOrderType.equals("1") && urgencyLevel.equals("5")){
                        List<String> sales1 = Arrays.asList(new String[]{"2","32"});
                        List<String> sales2 = Arrays.asList(new String[]{"4","6","7","16"});
                        if(flightClass.equals("I") && !sales1.contains(salesType)
                           || flightClass.equals("N") && !sales2.contains(salesType)){
                            if(getString(po.getProductinfo(),new String[]{"maininfo","isonline"},"").equals("F"))
                            {
                                finallevel = 211;
                            } else {
                                finallevel = 205;
                            }
                        }
                    }
                }
            }
        }
        po.setRisklevel(finallevel);
        setValue(riskleveldata,"risklevel",finallevel);
        setValue(riskleveldata,"originalrisklevel",finallevel);
    }

    /**
     * 过滤原来黑白名单 90~99、95分数据
     * @param results
     * @param originLevel
     * @return
     */
    private int process90_100BWResult( List<CheckResultLog> results,int originLevel){
        int finalRisklevel = originLevel;
        boolean black95 = false;
        boolean black90 = false;
        if (results != null && results.size() > 0) {
            for (CheckResultLog r : results) {
                if(r.getRuleType().equals(CheckType.BW.toString())){
                    int risklevel = r.getRiskLevel();
                    if(risklevel==95){
                        black95 = true;
                    } else if (risklevel>=90 && risklevel< 100){
                        black90 = true;
                    }
                }
            }
        }
        if(black95){
            finalRisklevel = 95;
        } else if(black90 && originLevel < 195){
            finalRisklevel = 97;
        }
        return finalRisklevel;
    }

    private void processHotelGroup(PO po, Map<String, Object> riskleveldata, Integer maxlevel,RiskResult riskResult) {

        setValue(riskleveldata, "creditcardtype", getCreditCardType(po.getPaymentinfo()));
        List<CheckResultLog> results = riskResult.getResults();
        int finalRisklevel =processUWL(results,maxlevel);
        finalRisklevel = process90_100BWResult(results,finalRisklevel);
        po.setRisklevel(finalRisklevel);
        setValue(riskleveldata,"risklevel",finalRisklevel);
        setValue(riskleveldata,"originalrisklevel",finalRisklevel);

    }

    /**
     * 获取主要支付方式
     *
     * @param paymentInfos
     * @return
     */
    public String getCreditCardType(Map<String, Object> paymentInfos) {
        List<Map<String, Object>> paymentInfoList = getList(paymentInfos, "paymentinfolist");
        String prePay = "";
        if (paymentInfoList != null) {
            for (Map<String, Object> p : paymentInfoList) {
                String tmpprePay = MapX.getString(p, new String[]{"payment", "prepaytype"}, "").toUpperCase();
                if (tmpprePay.equals("CCARD") || tmpprePay.equals("DCARD") || tmpprePay.equals("DQPAY")) {
                    List<Map<String, Object>> cards = getList(p, "cardinfolist");
                    if (cards != null && cards.size() > 0) {
                        return getString(cards.get(0), "creditcardtype", "");
                    }
                    break;
                }
            }
        }
        return prePay;
    }

    public void postToEasyPay(PO po){
        boolean next = false;
        if(po.getOrdertype()==1 && (po.getSubordertype()==1 || po.getRisklevel()>100 || po.getRisklevel()==99 && getString(po.getProductinfo(),new String[]{"riskleveldata","cmbmsgstatus"},"").equals("T"))){
             next = true;
        }
        if(CtripOrderType.HotelGroup.getCode()== po.getOrdertype()){
            next = true;
        }
        if (next) {
            Map<String, Object> result = esbClient.postRiskLevelData(po);
            if (result != null && getString(result, "retcode", "") == "0") {
                Map<String, Object> risklevelDate = MapX.getMap(po.getProductinfo(), "riskleveldata");
                if (risklevelDate != null) {
                    setValue(risklevelDate, "transflag", 32);
                }
            }
        }
    }
}

package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.dal.CardRiskService;
import com.ctrip.infosec.flowtable4j.jobws.TableInfoService;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.model.persist.TableInfo;
import org.bouncycastle.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class Save2DbProcessor {
    @Autowired
    private CardRiskService cardRiskService;

    @Autowired
    private TableInfoService tableInfoService;

    public void save(PO po) {
        Map<String, Object> dealInfo = MapX.getMap(po.getProductinfo(), "dealinfo");
        if (dealInfo != null) {
            long reqId = saveDealInfo(dealInfo);
            loopList(po.getPaymentinfo(), "", reqId);
            loopList(po.getProductinfo(), "", reqId);
        }
    }

    public List<TableInfo> getDbMeta(String tableName){
        return  tableInfoService.getTableInfo(tableName);
    }

    public void loopList(Map<String, Object> toSave, String name,long reqId) {
        if ("goodsList~.".equals(name)) {
            saveGoodsInfo(toSave, reqId);
        } else if ("flightinfolist~.".equals(name)) {
            saveFlightInfo(toSave,reqId);
        } else if ("flightotherinfolist~.".equals(name)) {
            saveOtherInfo(toSave,reqId);
        } else if ("hotelotherinfolist~.".equals(name)) {
            saveOtherInfo(toSave,reqId);
        } else if ("ireotherinfolist~.".equals(name)) {
            saveOtherInfo(toSave,reqId);
        } else if ("jifenorderitemlist~.".equals(name)) {
            saveJiFenOrderItem(toSave,reqId);
        } else if ("paymentinfolist~.".equals(name)) {
            savePaymentInfo(toSave,reqId);
        } else if ("railinfolist~.".equals(name)) {
            saveRailInfo(toSave,reqId);
        } else if ("topshopmerchantlist~.".equals(name)) {
            saveTopShopMerchantItem(toSave,reqId);
        } else if ("viaotherinfolist~.".equals(name)) {
            saveVacationInfo(toSave,reqId);
        } else if ("vacationinfolist~.".equals(name)) {
            saveVacationInfo(toSave,reqId);
        } else
            for (Iterator<String> it = toSave.keySet().iterator(); it.hasNext(); ) {
                String k = it.next();
                Object v = toSave.get(k);
                if (v instanceof List) {
                    for (Iterator<Map<String, Object>> itM = ((List<Map<String, Object>>) v).iterator(); itM.hasNext(); ) {
                        loopList(itM.next(), name.length() == 0 ? k + "~." : name + k + "~.",reqId);
                    }
                } else {
                    saveMap((Map<String, Object>) v, PO.getProp2Table().get(name.length() == 0 ? k : name + k),reqId);
                }
            }
    }

    private void saveMap(Map<String, Object> toSave, String tableName,long reqId) {
        List<TableInfo> tableInfo = tableInfoService.getTableInfo(tableName);
        cardRiskService.saveImpl(toSave, tableInfo, tableName,reqId);
    }

    private void saveFlightInfo(Map<String, Object> toSave,long reqId) {
        String table_order = "infosecurity_flightsorderinfo";
        String table_passenger = "infosecurity_passengerinfo";
        String table_segment = "infosecurity_segmentinfo";
        List<TableInfo> tableInfo_order = tableInfoService.getTableInfo(table_order);
        List<TableInfo> tableInfo_passenger = tableInfoService.getTableInfo(table_passenger);
        List<TableInfo> tableInfo_segment = tableInfoService.getTableInfo(table_segment);
        Map<String, Object> order = (Map) toSave.get("order");
        List<Map<String, Object>> passengerList = (List) toSave.get("passengerlist");
        List<Map<String, Object>> segmentList = (List) toSave.get("segmentlist");

        long id = cardRiskService.saveImpl(order, tableInfo_order, table_order,reqId);
        if(passengerList!=null)
        for (Map<String, Object> passenger : passengerList) {
            passenger.put("flightsorderid",id);
            cardRiskService.saveImpl(passenger, tableInfo_passenger, table_passenger,reqId);
        }

        if(segmentList!=null)
        for (Map<String, Object> segment : segmentList) {
            segment.put("flightsorderid",id);
            cardRiskService.saveImpl(segment, tableInfo_segment, table_segment,reqId);
        }

    }

    private void saveGoodsInfo(Map<String, Object> toSave,long reqId) {
        String table_goods = "infosecurity_goodslistinfo";
        String table_goodsItem = "infosecurity_goodsiteminfo";
        List<TableInfo> tableInfo_goods = tableInfoService.getTableInfo(table_goods);
        List<TableInfo> tableInfo_goodsItem = tableInfoService.getTableInfo(table_goodsItem);
        Map<String, Object> goods = (Map) toSave.get("goods");
        List<Map<String, Object>> goodsItemList = (List) toSave.get("goodsitemlist");

        long id = cardRiskService.saveImpl(goods, tableInfo_goods, table_goods, reqId);
        if(goodsItemList!=null)
        for (Map<String, Object> passenger : goodsItemList) {
            passenger.put("goodslistinfoid",id);
            cardRiskService.saveImpl(passenger, tableInfo_goodsItem, table_goodsItem,reqId);
        }
    }

    private void saveJiFenOrderItem(Map<String, Object> toSave,long reqId) {
        String table_order = "infosecurity_suborderitermbyjifen";
        String table_greetingCard = "infosecurity_greetingcardinfoviewbyjifen";
        String table_prizeDetail = "infosecurity_prizedetailitembyjifen";
        String table_paymentItem = "infosecurity_paymentitemviewbyjifen";

        List<TableInfo> tableInfo_order = tableInfoService.getTableInfo(table_order);
        List<TableInfo> tableInfo_greetingCard = tableInfoService.getTableInfo(table_greetingCard);
        List<TableInfo> tableInfo_prizeDetail = tableInfoService.getTableInfo(table_prizeDetail);
        List<TableInfo> tableInfo_paymentItem = tableInfoService.getTableInfo(table_paymentItem);
        Map<String, Object> order = (Map) toSave.get("order");
        Map<String, Object> greetingCard = (Map) toSave.get("greetingcard");
        Map<String, Object> prizeDetail = (Map) toSave.get("prizedetail");
        Map<String, Object> paymentItem = (Map) toSave.get("paymentitem");

        long id = cardRiskService.saveImpl(order, tableInfo_order, table_order,reqId);

        cardRiskService.saveImpl(greetingCard, tableInfo_greetingCard, table_greetingCard,reqId);
        cardRiskService.saveImpl(prizeDetail, tableInfo_prizeDetail, table_prizeDetail,reqId);
        cardRiskService.saveImpl(paymentItem, tableInfo_paymentItem, table_paymentItem,reqId);
    }

    private void saveOtherInfo(Map<String, Object> toSave,long reqId) {

    }

    private void savePaymentInfo(Map<String, Object> toSave,long reqId) {
        String table_payment = "infosecurity_paymentinfo";
        String table_cardInfo = "infosecurity_cardinfo";
        List<TableInfo> tableInfo_payment = tableInfoService.getTableInfo(table_payment);
        List<TableInfo> tableInfo_cardInfo = tableInfoService.getTableInfo(table_cardInfo);
        Map<String, Object> payment = (Map) toSave.get("rail");
        List<Map<String, Object>> cardInfoList = (List) toSave.get("user");

        long id = cardRiskService.saveImpl(payment, tableInfo_payment, table_payment,reqId);
        if(cardInfoList!=null)
        for (Map<String, Object> passenger : cardInfoList) {
            cardRiskService.saveImpl(passenger, tableInfo_cardInfo, table_cardInfo,reqId);
        }
    }

    private void saveRailInfo(Map<String, Object> toSave,long reqId) {
        String table_rail = "infosecurity_exrailinfo";
        String table_user = "infosecurity_exrailuserinfo";
        List<TableInfo> tableInfo_rail = tableInfoService.getTableInfo(table_rail);
        List<TableInfo> tableInfo_user = tableInfoService.getTableInfo(table_user);
        Map<String, Object> rail = (Map) toSave.get("rail");
        Map<String, Object> user = (Map) toSave.get("user");

        long id = cardRiskService.saveImpl(rail, tableInfo_rail, table_rail,reqId);

        cardRiskService.saveImpl(user, tableInfo_user, table_user,reqId);
    }

    private void saveTopShopMerchantItem(Map<String, Object> toSave,long reqId) {
        String table_topShopMerchant = "infosecurity_topshopmerchantitem";
        String table_product = "infosecurity_topshopproductioninfo";
        List<TableInfo> tableInfo_topShopMerchant = tableInfoService.getTableInfo(table_topShopMerchant);
        List<TableInfo> tableInfo_product = tableInfoService.getTableInfo(table_product);
        Map<String, Object> topShopMerchant = (Map) toSave.get("topShopMerchant");
        List<Map<String, Object>> productList = (List) toSave.get("productList");

        long id = cardRiskService.saveImpl(topShopMerchant, tableInfo_topShopMerchant, table_topShopMerchant,reqId);
        if(productList!=null)
        for (Map<String, Object> passenger : productList) {
            cardRiskService.saveImpl(passenger, tableInfo_product, table_product,reqId);
        }
    }

    private void saveVacationInfo(Map<String, Object> toSave,long reqId) {
        String table_order = "infosecurity_vacationinfo";
        String table_user = "infosecurity_vacationuserinfo";
        String table_option = "infosecurity_vacationoptioninfo";
        List<TableInfo> tableInfo_order = tableInfoService.getTableInfo(table_order);
        List<TableInfo> tableInfo_user = tableInfoService.getTableInfo(table_user);
        List<TableInfo> tableInfo_option = tableInfoService.getTableInfo(table_option);

        Map<String, Object> order = (Map) toSave.get("order");
        List<Map<String, Object>> userList = (List) toSave.get("userList");
        List<Map<String, Object>> optionList = (List) toSave.get("optionList");

        long id = cardRiskService.saveImpl(order, tableInfo_order, table_order,reqId);
        if(userList!=null)
        for (Map<String, Object> user : userList) {
            cardRiskService.saveImpl(user, tableInfo_user, table_user,reqId);
        }

        if(optionList!=null)
        for (Map<String, Object> option : optionList) {
            cardRiskService.saveImpl(option, tableInfo_option, table_option,reqId);
        }
    }

    public long saveDealInfo(Map<String, Object> dealinfo) {
        return cardRiskService.saveImpl(dealinfo, tableInfoService.getTableInfo("infosecurity_dealinfo"), "infosecurity_dealinfo",0);
    }
}

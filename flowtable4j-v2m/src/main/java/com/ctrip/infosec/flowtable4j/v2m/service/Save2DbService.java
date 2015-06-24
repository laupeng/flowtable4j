package com.ctrip.infosec.flowtable4j.v2m.service;

import com.ctrip.infosec.flowtable4j.dal.CardRiskService;
import com.ctrip.infosec.flowtable4j.jobws.TableInfoService;
import com.ctrip.infosec.flowtable4j.model.persist.MMap;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by thyang on 2015-06-12.
 */
public class Save2DbService {
    @Autowired
    private CardRiskService cardRiskService;

    @Autowired
    private TableInfoService tableInfoService;

    //local 待存储的map 依赖于localbase
    //localbase key：token 依赖与被依赖之间有一致的token
//    ThreadLocal<List<MMap>> local = new ThreadLocal();
//    ThreadLocal<Map<String,MMap>> localBase = new ThreadLocal();

    public long saveDealInfo(PO po){
        return 10000;
    }

    public Map<String,String> getDbMeta(String tableName){
        return  null;
    }

    public void loopList(Map<String, Object> toSave, String name) {
        if ("goodsList~.".equals(name)) {
            saveGoodsInfo(toSave);
        } else if ("flightInfoList~.".equals(name)) {
            saveFlightInfo(toSave);
        } else if ("flightOtherInfoList~.".equals(name)) {
            saveOtherInfo(toSave);
        } else if ("hotelOtherInfoList~.".equals(name)) {
            saveOtherInfo(toSave);
        } else if ("IREOtherInfoList~.".equals(name)) {
            saveOtherInfo(toSave);
        } else if ("jiFenOrderItemList~.".equals(name)) {
            saveJiFenOrderItem(toSave);
        } else if ("paymentInfoList~.".equals(name)) {
            savePaymentInfo(toSave);
        } else if ("railInfoList~.".equals(name)) {
            saveRailInfo(toSave);
        } else if ("topShopMerchantList~.".equals(name)) {
            saveTopShopMerchantItem(toSave);
        } else if ("VIAOtherInfoList~.".equals(name)) {
            saveVacationInfo(toSave);
        } else if ("vacationInfoList~.".equals(name)) {
            saveVacationInfo(toSave);
        } else
            for (Iterator<String> it = toSave.keySet().iterator(); it.hasNext(); ) {
                String k = it.next();
                Object v = toSave.get(k);
                if (v instanceof List) {
                    for (Iterator<Map<String, Object>> itM = ((List<Map<String, Object>>) v).iterator(); itM.hasNext(); ) {
                        loopList(itM.next(), name.length() == 0 ? k + "~." : name + k + "~.");
                    }
                } else {
                    saveMap((Map<String, Object>) v, PO.getProp2Table().get(name.length() == 0 ? k : name + k));
                }
            }
//        finishSave();
    }

    private void saveMap(Map<String, Object> toSave, String tableName) {
//        cardRiskService.saveByTableInfo(toSave, tableName);
        Map<String, String> tableInfo = tableInfoService.getTableInfo(tableName);
//        if (toSave instanceof MMap) {
//            MMap m = ((MMap) toSave);
//            if (m.isBase()) {
//                if (localBase.get() == null) {
//                    localBase.set(new HashMap<String, MMap>());
//                }
////                DepB b = new DepB();
////                b.setFieldName(((MMap) toSave).getTableNamesAndFields()[0]);
////                b.setTableName(tableName);
////                b.setId(saveImpl(toSave, tableName));
////                localBase.get().add(b);
//                m.setReturnId(cardRiskService.saveImpl(toSave, tableInfo, tableName));
//                localBase.get().put(m.getToken(), m);
//            } else {
//                if (local.get() == null) {
//                    local.set(new ArrayList<MMap>());
//                }
//                local.get().add(m);
//            }
//        } else {
        //TODO save the map
        cardRiskService.saveImpl(toSave, tableInfo, tableName);
//        }
    }

//    private void finishSave() {
//        List<MMap> toFinishMaps = local.get();
//        Map<String, MMap> baseMaps = localBase.get();
//        if (toFinishMaps != null && toFinishMaps.size() > 0 && baseMaps != null && baseMaps.size() > 0) {
//            for (MMap mMap : toFinishMaps) {
////                boolean find = false;
////                long value = 0;
////                String[] tableNameAndField = mMap.getTableNamesAndFields();
////                for (DepB depB : depBs) {
////                    if (depB.getTableName().equals(tableNameAndField[0]) && depB.getFieldName().equals(tableNameAndField[1])) {
////                        find = true;
////                        value = depB.getId();
////                        mMap.put(tableNameAndField[2], value);
////                        break;
////                    }
////                }
//                MMap baseMap = baseMaps.get(mMap.getToken());
//                if (baseMap != null) {
//                    mMap.put(mMap.getFieldName(), baseMap.getReturnId());
//                    //TODO save 需完善
//                    cardRiskService.saveImpl(mMap, null,"");
//                }
//            }
//        }
//    }


    private void saveFlightInfo(Map<String, Object> toSave) {
        String table_order = "infosecurity_flightsorderinfo";
        String table_passenger = "infosecurity_passengerinfo";
        String table_segment = "infosecurity_segmentinfo";
        Map<String, String> tableInfo_order = tableInfoService.getTableInfo(table_order);
        Map<String, String> tableInfo_passenger = tableInfoService.getTableInfo(table_passenger);
        Map<String, String> tableInfo_segment = tableInfoService.getTableInfo(table_segment);
        Map<String, Object> order = (Map) toSave.get("order");
        List<Map<String, Object>> passengerList = (List) toSave.get("passengerlist");
        List<Map<String, Object>> segmentList = (List) toSave.get("segmentlist");

        long id = cardRiskService.saveImpl(order, tableInfo_order, table_order);
        for (Map<String, Object> passenger : passengerList) {
            passenger.put("FlightsOrderID",id);
            cardRiskService.saveImpl(passenger, tableInfo_passenger, table_passenger);
        }

        for (Map<String, Object> segment : segmentList) {
            segment.put("FlightsOrderID",id);
            cardRiskService.saveImpl(segment, tableInfo_segment, table_segment);
        }

    }

    private void saveGoodsInfo(Map<String, Object> toSave) {
        String table_goods = "infosecurity_goodslistinfo";
        String table_goodsItem = "infosecurity_goodsiteminfo";
        Map<String, String> tableInfo_goods = tableInfoService.getTableInfo(table_goods);
        Map<String, String> tableInfo_goodsItem = tableInfoService.getTableInfo(table_goodsItem);
        Map<String, Object> goods = (Map) toSave.get("goods");
        List<Map<String, Object>> goodsItemList = (List) toSave.get("goodsitemlist");

        long id = cardRiskService.saveImpl(goods, tableInfo_goods, table_goods);
        for (Map<String, Object> passenger : goodsItemList) {
            passenger.put("goodslistinfoid",id);
            cardRiskService.saveImpl(passenger, tableInfo_goodsItem, table_goodsItem);
        }
    }

    private void saveJiFenOrderItem(Map<String, Object> toSave) {
        String table_order = "infosecurity_suborderitermbyjifen";
        String table_greetingCard = "infosecurity_greetingcardinfoviewbyjifen";
        String table_prizeDetail = "infosecurity_prizedetailitembyjifen";
        String table_paymentItem = "infosecurity_paymentitemviewbyjifen";

        Map<String, String> tableInfo_order = tableInfoService.getTableInfo(table_order);
        Map<String, String> tableInfo_greetingCard = tableInfoService.getTableInfo(table_greetingCard);
        Map<String, String> tableInfo_prizeDetail = tableInfoService.getTableInfo(table_prizeDetail);
        Map<String, String> tableInfo_paymentItem = tableInfoService.getTableInfo(table_paymentItem);
        Map<String, Object> order = (Map) toSave.get("order");
        Map<String, Object> greetingCard = (Map) toSave.get("greetingcard");
        Map<String, Object> prizeDetail = (Map) toSave.get("prizedetail");
        Map<String, Object> paymentItem = (Map) toSave.get("paymentitem");

        long id = cardRiskService.saveImpl(order, tableInfo_order, table_order);

        cardRiskService.saveImpl(greetingCard, tableInfo_greetingCard, table_greetingCard);
        cardRiskService.saveImpl(prizeDetail, tableInfo_prizeDetail, table_prizeDetail);
        cardRiskService.saveImpl(paymentItem, tableInfo_paymentItem, table_paymentItem);
    }

    private void saveOtherInfo(Map<String, Object> toSave) {

    }

    private void savePaymentInfo(Map<String, Object> toSave) {
        String table_payment = "infosecurity_paymentinfo";
        String table_cardInfo = "infosecurity_cardinfo";
        Map<String, String> tableInfo_payment = tableInfoService.getTableInfo(table_payment);
        Map<String, String> tableInfo_cardInfo = tableInfoService.getTableInfo(table_cardInfo);
        Map<String, Object> payment = (Map) toSave.get("rail");
        List<Map<String, Object>> cardInfoList = (List) toSave.get("user");

        long id = cardRiskService.saveImpl(payment, tableInfo_payment, table_payment);
        for (Map<String, Object> passenger : cardInfoList) {
            cardRiskService.saveImpl(passenger, tableInfo_cardInfo, table_cardInfo);
        }
    }

    private void saveRailInfo(Map<String, Object> toSave) {
        String table_rail = "infosecurity_exrailinfo";
        String table_user = "infosecurity_exrailuserinfo";
        Map<String, String> tableInfo_rail = tableInfoService.getTableInfo(table_rail);
        Map<String, String> tableInfo_user = tableInfoService.getTableInfo(table_user);
        Map<String, Object> rail = (Map) toSave.get("rail");
        Map<String, Object> user = (Map) toSave.get("user");

        long id = cardRiskService.saveImpl(rail, tableInfo_rail, table_rail);

        cardRiskService.saveImpl(user, tableInfo_user, table_user);
    }

    private void saveTopShopMerchantItem(Map<String, Object> toSave) {
        String table_topShopMerchant = "infosecurity_topshopmerchantitem";
        String table_product = "infosecurity_topshopproductioninfo";
        Map<String, String> tableInfo_topShopMerchant = tableInfoService.getTableInfo(table_topShopMerchant);
        Map<String, String> tableInfo_product = tableInfoService.getTableInfo(table_product);
        Map<String, Object> topShopMerchant = (Map) toSave.get("topShopMerchant");
        List<Map<String, Object>> productList = (List) toSave.get("productList");

        long id = cardRiskService.saveImpl(topShopMerchant, tableInfo_topShopMerchant, table_topShopMerchant);
        for (Map<String, Object> passenger : productList) {
            cardRiskService.saveImpl(passenger, tableInfo_product, table_product);
        }
    }

    private void saveVacationInfo(Map<String, Object> toSave) {
        String table_order = "infosecurity_vacationinfo";
        String table_user = "infosecurity_vacationuserinfo";
        String table_option = "infosecurity_vacationoptioninfo";
        Map<String, String> tableInfo_order = tableInfoService.getTableInfo(table_order);
        Map<String, String> tableInfo_user = tableInfoService.getTableInfo(table_user);
        Map<String, String> tableInfo_option = tableInfoService.getTableInfo(table_option);

        Map<String, Object> order = (Map) toSave.get("order");
        List<Map<String, Object>> userList = (List) toSave.get("userList");
        List<Map<String, Object>> optionList = (List) toSave.get("optionList");

        long id = cardRiskService.saveImpl(order, tableInfo_order, table_order);
        for (Map<String, Object> user : userList) {
            cardRiskService.saveImpl(user, tableInfo_user, table_user);
        }

        for (Map<String, Object> option : optionList) {
            cardRiskService.saveImpl(option, tableInfo_option, table_option);
        }

        JdbcTemplate template = null;
        template.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                connection.prepareStatement("",new String[1]);
                return null;
            }
        }, new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                return null;
            }
        });
    }
}

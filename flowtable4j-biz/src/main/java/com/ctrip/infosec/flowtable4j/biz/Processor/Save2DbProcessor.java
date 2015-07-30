package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.dal.CardRiskDbService;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.persist.ColumnInfo;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class Save2DbProcessor {
    @Autowired
    private CardRiskDbService cardRiskDbService;

    @Autowired
    private TableInfoService tableInfoService;

    private Logger logger = LoggerFactory.getLogger(Save2DbProcessor.class);
    protected static JsonMapper mapper = new JsonMapper();

    public Long saveDealInfo(Map<String, Object> map) {

        Map<String, Long> keys = new HashMap<String, Long>();
        keys.put("reqid", 0L);
        saveMap(map, PO.getProp2Table().get("dealinfo"), keys);
        return keys.get("reqid");
    }

    public void save(PO po, Long reqid) {
        //TODO 开启多线程保存
        save(po.getProductinfo(), reqid);
        save(po.getPaymentinfo(), reqid);
    }

    public void save(Map<String, Object> src, long reqId) {
        if (src != null && src.size() > 0) {
            for (String key : src.keySet()) {
                Map<String, Long> keys = new HashMap<String, Long>();
                keys.put("reqid", reqId);
                if ("diyresourcexlist".equals(key) || "giftitemlist".equals(key) || "hotelinfolist".equals(key)
                        || "insureinfolist".equals(key) || "invoiceinfolist".equals(key) || "rechargesuborderlist".equals(key)
                        || "vacationproductlist".equals(key) || "hotegroupinfolist".equals(key)) {
                    saveList(key, src, reqId);
                } else if ("flightinfolist".equals(key)) {
                    keys.put("flightsorderid", 0L);
                    saveList(key, src, keys, new String[]{"order"}, new String[]{"passengerlist", "segmentlist"});
                } else if ("fncmalllist".equals(key)) {
                    keys.put("fncmallid", 0L);
                    saveList(key, src, keys, new String[]{"travelmoneyfncmall"}, new String[]{"suborderitemlist"});
                } else if ("goodslist".equals(key)) {
                    keys.put("goodslistinfoid", 0L);
                    saveList(key, src, keys, new String[]{"goods"}, new String[]{"goodsitemlist"});
                } else if ("jifenorderitemlist".equals(key)) {
                    keys.put("orderitemid", 0L);
                    keys.put("detailitemid", 0L);
                    saveList(key, src, keys, new String[]{"order", "greetingcard", "prizedetail", "paymentitem"}, null);
                } else if ("paymentinfolist".equals(key)) {
                    keys.put("paymentinfoid", 0L);
                    saveList(key, src, keys, new String[]{"payment"}, new String[]{"cardinfolist"});
                } else if ("railinfolist".equals(key)) {
                    keys.put("exrailinfoid", 0L);
                    saveList(key, src, keys, new String[]{"rail"}, new String[]{"user"});
                } else if ("topshopcatalog".equals(key)) {
                    keys.put("cataloginfoid", 0L);
                    saveList(key, src, keys, new String[]{"cataloginfo"}, new String[]{"itemlist"});
                } else if ("travelmoneyproductlist".equals(key)) {
                    saveTopShopOrderList(src, reqId);
                } else if ("vacationinfolist".equals(key)) {
                    keys.put("vacationinfoid", 0L);
                    saveList(key, src, keys, new String[]{"order"}, new String[]{"userlist", "optionlist"});
                } else if (!"dealinfo".equals(key)) {
                    saveMap(MapX.getMap(src, key), PO.getProp2Table().get(key), ImmutableMap.of("reqid", reqId));
                }
            }
        }
    }

    private void saveTopShopOrderList(Map<String, Object> src, long reqid) {
        List<Map<String, Object>> list2Save = MapX.getList(src, "topshoporderlist");
        Map<String, Long> keys = ImmutableMap.of("topshoporderid", 0L, "merchantitemid", 0L, "reqid", reqid);
        if (list2Save != null && list2Save.size() > 0) {
            for (Map<String, Object> toSave : list2Save) {
                Map<String, Object> order = MapX.getMap(toSave, "order");
                List<Map<String, Object>> productitemlist = MapX.getList(toSave, "productitemlist");
                List<Map<String, Object>> merchantlist = MapX.getList(toSave, "merchantlist");
                //greetingcard,prizedetail ref --> order
                saveMap(order, PO.getProp2Table().get("topshoporderlist~.order"), keys);
                for (Map<String, Object> prod : productitemlist) {
                    saveMap(prod, PO.getProp2Table().get("topshoporderlist~.productitemlist~"), keys);
                }
                for (Map<String, Object> merchant : merchantlist) {
                    Map<String, Object> item = MapX.getMap(merchant, "merchant");
                    List<Map<String, Object>> productionlist = MapX.getList(merchant, "productionlist");
                    saveMap(item, PO.getProp2Table().get("topshoporderlist~.merchantlist~.merchant"), keys);
                    for (Map<String, Object> prodItem : productionlist) {
                        saveMap(prodItem, PO.getProp2Table().get("topshoporderlist~.merchantlist~.productionlist~"), keys);
                    }
                }
            }
        }
    }

    public List<ColumnInfo> getDbMeta(String tableName) {
        return tableInfoService.getTableInfo(tableName);
    }

    private void saveList(String prefix, Map<String, Object> src, Map<String, Long> keys, String[] singleTables, String[] listTables) {
        List<Map<String, Object>> list2Save = MapX.getList(src, prefix);
        if (list2Save != null && list2Save.size() > 0) {
            for (Map<String, Object> toSave : list2Save) {
                if (singleTables != null && singleTables.length > 0) {
                    for (String s : singleTables) {
                        String tableName = PO.getProp2Table().get(prefix + "~." + s);
                        if (!Strings.isNullOrEmpty(tableName)) {
                            Map<String, Object> obj = MapX.getMap(toSave, s);
                            saveMap(obj, tableName, keys);
                        }
                    }
                }
                if (listTables != null && listTables.length > 0) {
                    for (String s : listTables) {
                        String tableName = PO.getProp2Table().get(prefix + "~." + s + "~");
                        if (!Strings.isNullOrEmpty(tableName)) {
                            List<Map<String, Object>> objs = MapX.getList(toSave, s);
                            if (objs != null && objs.size() > 0) {
                                for (Map<String, Object> obj : objs) {
                                    saveMap(obj, tableName, keys);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存List元素
     * vacationproductlist，giftItemlist，hotelinfolist
     *
     * @param prefix
     * @param src
     * @param reqId
     */
    private void saveList(String prefix, Map<String, Object> src, long reqId) {
        String tableName = PO.getProp2Table().get(prefix + "~");
        if (!Strings.isNullOrEmpty(tableName) && src != null && src.size() > 0) {
            List<Map<String, Object>> list2Save = MapX.getList(src, prefix);
            if (list2Save != null && list2Save.size() > 0) {
                for (Map<String, Object> obj : list2Save) {
                    saveMap(obj, tableName, ImmutableMap.of("reqid", reqId));
                }
            }
        }
    }

    /**
     * 保存Map数据到数据库中
     *
     * @param src       需要保存的Map
     * @param tableName 表名称，小写
     * @param keys      需要传递的参数、或者外传的ID
     * @return
     */
    public void saveMap(final Map<String, Object> src, final String tableName, final Map<String, Long> keys) {
        try {
            if (src != null && src.size() > 0 && !Strings.isNullOrEmpty(tableName)) {
                final List<ColumnInfo> columnInfos = tableInfoService.getTableInfo(tableName);
                if (columnInfos != null && columnInfos.size() > 0) {
                    final String[] outField = new String[]{""};
                    final Integer[] outFieldIndex = new Integer[]{0};
                    cardRiskDbService.jdbcTemplate.<Long>execute(
                            new CallableStatementCreator() {
                                @Override
                                public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                                    String storedProc = "{call spA_%s_i (%s)}";
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < columnInfos.size(); i++) {
                                        sb.append(",@").append(columnInfos.get(i).getName()).append("=?");
                                    }
                                    storedProc = String.format(storedProc, tableName, sb.toString().substring(1));
                                    CallableStatement callableStatement = connection.prepareCall(storedProc);
                                    for (int i = 1; i <= columnInfos.size(); i++) {
                                        ColumnInfo t = columnInfos.get(i - 1);
                                        if (t.getIs_identity() == 1) {
                                            callableStatement.registerOutParameter(i, Types.BIGINT);
                                            outField[0] = t.getName();
                                            outFieldIndex[0] = i;
                                        } else {
                                            callableStatement.setObject(i, t.getValue(src, keys));
                                        }
                                    }
                                    return callableStatement;
                                }
                            }, new CallableStatementCallback() {
                                @Override
                                public Void doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                                    callableStatement.execute();
                                    if (!Strings.isNullOrEmpty(outField[0]) && keys.containsKey(outField[0])) {
                                        keys.put(outField[0], callableStatement.getLong(outFieldIndex[0]));
                                    }
                                    return null;
                                }
                            });
                }
            }
        } catch (Exception ex) {
            logger.warn(String.format("Save %s fail:{%s}", tableName,mapper.toJson(src)), ex);
        }

    }
}

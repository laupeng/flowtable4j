package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.biz.processor.TableInfoService;
import com.ctrip.infosec.flowtable4j.dal.CardRiskDbService;
import com.ctrip.infosec.flowtable4j.dal.FlowDbService;
import com.ctrip.infosec.flowtable4j.model.VerifyData;
import com.ctrip.infosec.flowtable4j.model.persist.ColumnInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created by thyang on 2015-09-14.
 */
@Component
public class ResultCompare {
    @Autowired
    CardRiskDbService cardRiskDbService;

    @Autowired
    TableInfoService tableInfoService;

    @Autowired
    FlowDbService flowDbService;
    private static Logger logger = LoggerFactory.getLogger(ResultCompare.class);

    private static ObjectMapper mapper =null;

    static {
        mapper = new ObjectMapper();
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);

        // 忽略EMPTY_BEANS
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 排序
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

        // DateFormat
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException var3) {
            logger.warn("write to json string error:" + object, var3);
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static InetAddress getInetAddress() {
        try {
            Enumeration allNetInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
                        .nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        if ("127.0.0.1".equals(ip.getHostAddress())) {
                            continue;
                        } else {
                            return ip;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            //
        }
        return null;
    }

    public static String getHostIp() {
        InetAddress netAddress = getInetAddress();
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); // get the ip address
        return ip;
    }

    String[] tables = new String[]{
            "infosecurity_appinfo",
            "infosecurity_cardinfo",
//            "infosecurity_chproduct",
            "infosecurity_contactinfo",
            "infosecurity_corporationinfo",
//            "infosecurity_couponsinfo",
//            "infosecurity_currencyexchange",
//            "infosecurity_customerinfo",
            "infosecurity_deviceidinfo",
//            "infosecurity_devoterinfoviewbyjifen",
//            "infosecurity_distributioncompany",
//            "infosecurity_distributioninfo",
//            "infosecurity_diyresourcexinfo",
//            "infosecurity_employeeinfo",
//            "infosecurity_exrailinfo",
//            "infosecurity_exrailuserinfo",
            "infosecurity_flightsorderinfo",
//            "infosecurity_fncmallorderinfo",
//            "infosecurity_fncmallsuborderitem",
//            "infosecurity_fundcertificateinfo",
//            "infosecurity_giftitem",
//            "infosecurity_goodsiteminfo",
//            "infosecurity_goodslistinfo",
//            "infosecurity_gpsinfo",
//            "infosecurity_greetingcardinfoviewbyjifen",
//            "infosecurity_hotelgroupinfo",
//            "infosecurity_hotelinfo",
//            "infosecurity_insuredinfo",
//            "infosecurity_invoiceinfo",
//            "infosecurity_invoicelistinfo",
            "infosecurity_ipinfo",
            "infosecurity_maininfo",
//            "infosecurity_marketdatainfo",
//            "infosecurity_miceinfo",
            "infosecurity_otherinfo",
            "infosecurity_passengerinfo",
            "infosecurity_paymentinfo",
//            "infosecurity_paymentitemviewbyjifen",
            "infosecurity_paymentmaininfo",
//            "infosecurity_prizedetailitembyjifen",
//            "infosecurity_proposerinfo",
//            "infosecurity_rechargesuborderinfo",
            "infosecurity_riskleveldata",
            "infosecurity_segmentinfo",
//            "infosecurity_smsverifyinfo",
//            "infosecurity_suborderitermbyjifen",
//            "infosecurity_topshopcataloginfo",
//            "infosecurity_topshopcataloginfoitem",
//            "infosecurity_topshopmerchantitem",
//            "infosecurity_topshopproductioninfo",
//            "infosecurity_topshopproductitem",
//            "infosecurity_travelmoneyfncmall",
//            "infosecurity_travelmoneyproductinfo",
//            "infosecurity_travelmoneyproductinfoplus",
//            "infosecurity_travelmoneyretailerinfo",
            "infosecurity_userinfo"
//            "infosecurity_vacationbytianhaiinfo",
//            "infosecurity_vacationinfo",
//            "infosecurity_vacationoptioninfo",
//            "infosecurity_vacationproductinfo",
//            "infosecurity_vacationuserinfo",
//            "infosecurity_walletwithdrawals",
//            "infosecurity_yongcheinfo"
    };

    List<String> exfields = Arrays.asList(new String[]{"reqid", "couponsid", "customerid", "dcompanyid", "resourcexinfoid", "exrailinfoid",
            "exrailuserid", "flightsorderid", "subitemid", "giftitemid", "goodsiteminfoid", "goodslistinfoid",
            "orderitemid", "hotelgrouporderid", "hotelorderid", "insuredinfoid", "invoicelistinfoid",
            "marketid", "datainfoid", "flightsuserid", "flightsorderid", "paymentinfoid", "detailitemid",
            "rechargesuborderinfoinfo", "segmentinfoid", "smsverifyid", "orderitemid", "cataloginfoid",
            "citemid", "merchantitemid", "topshoporderid", "productionid", "productitemid", "fncmallid",
            "travelmoneyid", "plusid", "travelmoneyretailerinfoid", "vacationinfoid", "vacationoption",
            "vacationuserid", "datachange_createtime", "datachange_lasttime","createdate"});

    private void buildSql(Map<String, List<ColumnInfo>> src, Map<String, String> target) {
        for (String tableName : tables) {
            List<ColumnInfo> columnInfos = src.get(tableName);
            if (columnInfos != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("select ");
                int columnCount = 0;
                for (ColumnInfo col : columnInfos) {
                    if (!exfields.contains(col.getName())) {
                        sb.append(col.getName()).append(",");
                        columnCount++;
                    }
                }
                sb.deleteCharAt(sb.length() - 1).append(" from ").append(tableName).append(" with(nolock) ");
                if (tableName.equals("infosecurity_exrailuserinfo")) {
                    sb.append(" where exrailinfoid in (select exrailinfoid from infosecurity_exrailinfo with(nolock) where reqid=?) order by 1,2");
                } else if (tableName.equals("infosecurity_passengerinfo")) {
                    sb.append(" where flightsorderid in (select flightsorderid from infosecurity_flightsorderinfo with(nolock) where reqid=?) order by 1,2");
                } else if (tableName.equals("infosecurity_segmentinfo")) {
                    sb.append(" where flightsorderid in (select flightsorderid from infosecurity_flightsorderinfo with(nolock) where reqid=?) order by 1,2");
                } else if (tableName.equals("infosecurity_vacationoptioninfo")) {
                    sb.append(" where vacationinfoid in (select vacationinfoid from infosecurity_vacationinfo with(nolock) where reqid=?) order by 1,2");
                } else if (tableName.equals("infosecurity_vacationuserinfo")) {
                    sb.append(" where vacationinfoid in (select vacationinfoid from infosecurity_vacationinfo with(nolock) where reqid=?) order by 1,2");
                } else if (tableName.equals("infosecurity_cardinfo")) {
                    sb.append(" where paymentinfoid in (select paymentinfoid from infosecurity_paymentinfo with(nolock) where reqid=?) order by 1,2");
                }else {
                    if (columnCount >= 2) {
                        sb.append("where reqid=? order by 1,2");
                    } else {
                        sb.append("where reqid=? order by 1");
                    }
                }
                target.put(tableName, sb.toString());
            }
        }
    }

    private List<Map<String,Object>> queryOld(String sql,Long reqId){
//          try {
              return cardRiskDbService.jdbcTemplate.queryForList(sql, new Object[]{reqId}, new int[]{Types.BIGINT});
//          } catch (EmptyResultDataAccessException ex){
//              return new ArrayList<Map<String, Object>>();
//          }
    }

    private List<Map<String,Object>> queryNew(String sql,Long reqId){
//       try {
            return flowDbService.jdbcTemplate2.queryForList(sql, new Object[]{reqId}, new int[]{Types.BIGINT});
//        } catch (EmptyResultDataAccessException ex){
//            return new ArrayList<Map<String, Object>>();
//        }
    }


    private void compareData(VerifyData data, String fileOld, String fileNew, String fileDiff) {
        FileWriter writer1 = null, writer2 = null, writer3 = null;
        try {
            Map<String, List<ColumnInfo>> tableinfo = tableInfoService.getTableInfos();
            if (tableinfo == null) {
                Thread.sleep(20L); //
                tableinfo = tableInfoService.getTableInfos();
            }
            Map<String, String> buildingSql = new HashMap<String, String>();
            buildSql(tableinfo, buildingSql);
            Long startReqId= data.getStartReq();
            Long lastReqId = data.getLastReq();
            writer1 = new FileWriter(fileDiff);
            writer2 = new FileWriter(fileOld);
            writer3 = new FileWriter(fileNew);
            String oldJson,newJson;
            while(startReqId<lastReqId) {
                String sql = "select top 100 reqId from infosecurity_maininfo with(nolock) where reqid>? and reqid<? and ordertype=? order by reqid";
                List<Long> reqIds = cardRiskDbService.jdbcTemplate.queryForList(sql, new Object[]{startReqId, lastReqId, data.getOrderType()}, new int[]{
                        Types.BIGINT, Types.BIGINT, Types.INTEGER}, Long.class);
                //区间内无数据，退出
                if(reqIds.size()==0){
                    break;
                }
                for(Long reqid:reqIds){
                    writer1.append("REQ:").append(reqid.toString()).append("\n");
                    writer2.append("REQ:").append(reqid.toString()).append("\n");
                    writer3.append("REQ:").append(reqid.toString()).append("\n");
                    for(String table:tables){
                        sql = buildingSql.get(table);
                        List<Map<String,Object>>  mapOld= queryOld(sql,reqid);
                        List<Map<String,Object>>  mapNew= queryNew(sql,reqid);
                        oldJson = toJson(mapOld);
                        newJson = toJson(mapNew);
                        writer1.append(table).append(":").append(String.valueOf(oldJson.equals(newJson))).append("\n");
                        writer2.append(table).append("\n");
                        writer2.append(oldJson).append("\n");
                        writer3.append(table).append("\n");
                        writer3.append(newJson).append("\n");
                        writer1.flush();
                        writer2.flush();
                        writer3.flush();
                    }
                    startReqId =Math.max(startReqId,reqid);
                    Thread.sleep(20L);
                    logger.debug("compare " + reqid);
                }
            }

        } catch (Exception ex) {
            logger.warn("compare result error", ex);
        } finally {
            try {
                if (writer1 != null) {
                    writer1.close();
                }
                if (writer2 != null) {
                    writer2.close();
                }
                if (writer3 != null) {
                    writer3.close();
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public String checkOrderData(final VerifyData data) {
          final String fileName1 = String.format("/opt/logs/tomcat/req_[%s_%s]_[%s]_old.log", data.getStartReq(), data.getLastReq(), data.getOrderType());
          final String fileName2 = String.format("/opt/logs/tomcat/req_[%s_%s]_[%s]_new.log", data.getStartReq(), data.getLastReq(), data.getOrderType());
          final String fileName3 = String.format("/opt/logs/tomcat/req_[%s_%s]_[%s]_cmp.log", data.getStartReq(), data.getLastReq(), data.getOrderType());
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                compareData(data, fileName1, fileName2, fileName3);
            }
        });
        return String.format("job submitted,please login to %s, check files %s", getHostIp(), fileName3);
    }

}

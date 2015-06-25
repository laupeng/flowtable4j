import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.v2m.converter.POConverter;

/**
 * Created by zhangsx on 2015/6/17.
 */
public class MainClass {
    public static void main(String[] args) {
//        PO po = new PO();
//        po.goodsList = new ArrayList<GoodsInfo>();
//
//        GoodsInfo info = new GoodsInfo();
//        List<Map<String,Object>> goodsItemList = new ArrayList<Map<String, Object>>();
//        Map<String,Object> goodsItem = new HashMap<String, Object>();
//        goodsItem.put("item1","2");
//        goodsItemList.add(goodsItem);
//        Map<String, Object> goods = new HashMap<String, Object>();
//        goods.put("good1", "1");
//        info.setGoodsItemList(goodsItemList);
//        info.setGoods(goods);
//
//        po.goodsList.add(info);
//        Save2DbService save2DbService = new Save2DbService();
//        save2DbService.save(po);

//        SQLServerConnectionPoolDataSource ds = new SQLServerConnectionPoolDataSource();
//        ds.setUser("uws_AllInOneKey_dev");
//        ds.setPassword("!QAZ@WSX1qaz2wsx");
//        ds.setDatabaseName("CardRiskDB");
//        ds.setServerName("devdb.dev.sh.ctriptravel.com");
//        ds.setPortNumber(28747);
//
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
//        long id = jdbcTemplate.<Long>execute(new CallableStatementCreator() {
//            @Override
//            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
//                String storedProc = "{call spA_InfoSecurity_CounterDimension_i (?,?,?,?)}";
//
//                CallableStatement callableStatement = connection.prepareCall(storedProc);
//                callableStatement.registerOutParameter(1, Types.BIGINT);
//                callableStatement.setObject("Cid","");
//                callableStatement.setObject("DimensionName","");
//                callableStatement.setObject("DataChange_LastTime","");
//
//
////                callableStatement.registerOutParameter(1, Types.BIGINT);
////                callableStatement.setObject(2,"");
////                callableStatement.setObject(3,"");
////                callableStatement.setObject(4,"");
////                callableStatement.setObject(5,"");
////                callableStatement.setObject(6,"");
////                callableStatement.setObject(7,"");
////                callableStatement.setObject(8,"");
////                callableStatement.setObject(9,"");
////                callableStatement.setObject(10,"");
////                callableStatement.setObject(11,"");
////                callableStatement.setObject(12,"");
////                callableStatement.setObject(13,"");
////                callableStatement.setObject(14,"");
////                callableStatement.setObject(15,"");
//                return callableStatement;
//            }
//        }, new CallableStatementCallback<Long>() {
//            @Override
//            public Long doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
//                callableStatement.execute();
//                return callableStatement.getLong(1);
//            }
//        });
//
//
//        System.out.println(id);
        com.ctrip.infosec.flowtable4j.model.RequestBody requestBody = new com.ctrip.infosec.flowtable4j.model.RequestBody();
        POConverter poConverter = new POConverter();
        PO po = poConverter.convert(requestBody);

    }
}

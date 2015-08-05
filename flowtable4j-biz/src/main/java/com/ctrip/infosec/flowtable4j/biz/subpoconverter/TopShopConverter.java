package com.ctrip.infosec.flowtable4j.biz.subpoconverter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class TopShopConverter extends ConverterBase {

    public void fillTopShopOrderList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String,Object>> orderlist = createList();
        Map<String,Object> order=createMap();
        orderlist.add(order);
        setValue(productInfo,"topshoporderlist",orderlist);
        setValue(order, "order", copyMap(eventBody, new String[]{"mobilenumber","salestype"}));
        if(getString(eventBody,"salestype","").equals("1")){
            List<Map<String,Object>> productListMap= getList(eventBody,"topshopproductlist");
            List<Map<String,Object>> merchentlist=createList();
            if(productListMap!=null && productListMap.size()>0){
                for(Map<String,Object> productMap:productListMap){
                    Map<String,Object> merchant =createMap();
                    Map<String,Object> query =soa2Client.getMerchantDetail(getString(productMap,"productid"));
                    if(query!=null && query.size()>0) {
                        Map<String, Object> merchanetItem = createMap();
                        setValue(merchant, "merchant", merchanetItem);
                        copyMap(productMap, merchanetItem, ImmutableMap.of("productid", "merchantid", "salespolicyid", "rebateid"));
                        copyMap(getMap(query, "rebatedetailinfo"), merchanetItem, ImmutableMap.of("expirydate", "expirydate", "id", "rebateid", "type", "rebatetype", "gift", "gift"));
                        copyMap(getMap(query, new String[]{"rebatedetailinfo", "rebatebyamountinfo"}), merchanetItem,
                                ImmutableMap.of("flag", "rebatebyamountinfoflag", "type", "rebatebyamountinfotype", "amount", "rebatebyamountinfoamount"));
                        copyMap(getMap(query, new String[]{"rebatedetailinfo", "rebatebypercentinfo"}), merchanetItem,
                                ImmutableMap.of("rebatebypercent", "rebatebypercentinfopercent", "type", "rebatebypercentinfotype"));
                        copyMap(query, merchanetItem, new String[]{"priceperperson", "longitude", "latitude", "remark", "openingtime"});
                        copyMap(query, merchanetItem, ImmutableMap.of("name", "merchantname", "status", "merchantstatus"));
                        List<Map<String, Object>> productInfoListMap = getList(query, "productioninfolist");
                        if (productInfoListMap != null && productInfoListMap.size() > 0) {
                            setValue(merchant, "productionlist", copyList(productInfoListMap, ImmutableMap.of("price", "price", "id", "productioninfoid", "name", "productioninfoname")));
                        }
                    }
                    merchentlist.add(merchant);
                }
            }
            setValue(order,"merchantlist",merchentlist);
        } else {
            List<Map<String,Object>> productListMap= getList(eventBody,"topshopproductlist");
            List<Map<String,Object>> productlist=createList();
            if(productListMap!=null && productListMap.size()>0){
                for(Map<String,Object> productMap:productListMap){
                    Map<String,Object> productItem =createMap();
                    Map<String,Object> query =soa2Client.getProductDetailSearch(getString(productMap, "productid"));
                    if(query!=null && query.size()>0) {
                        copyMap(productMap, productItem, ImmutableMap.of("productid", "productid", "productamount", "price", "salespolicyid", "salespolicyid"));
                        copyMap(getMap(query, "basicinfo"), productItem, ImmutableMap.of("introduction", "introduction", "type", "producttype", "name", "productname", "summary", "summary"));
                        copyMap(getMap(query, "salespolicy"), productItem, new String[]{"validendtime", "validstarttime", "marketprice", "price", "rebatetype", "rebate"});
                        copyValue(getMap(query, "salespolicy"), "id", productItem, "salespolicyid");
                    }
                    productlist.add(productItem);
                }
            }
            setValue(order,"productitemlist",productlist);
        }
    }

    public void fillTopShopCatalog(Map<String, Object> productInfo, Map<String, Object> eventBody) {
         List<Map<String,Object>> catalogList=createList();
         Map<String,Object> catalog = createMap();
         Map<String,Object> catalogitem = getMap(eventBody, "catalogitem");
         catalogList.add(catalog);
         if(catalogitem!=null && catalogitem.size()>0) {
            setValue(catalog, "cataloginfo", copyMap(catalogitem,"infosecurity_topshopcataloginfo"));
            setValue(catalog,"itemlist",copyList(getList(catalogitem,"cataloginfoitems"),"infosecurity_topshopcataloginfoitem"));
         }
         setValue(productInfo,"topshopcatalog",catalogList);
    }
}

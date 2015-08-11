package com.ctrip.infosec.flowtable4j.dal;
import com.ctrip.infosec.sars.util.GlobalConfig;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class SOA2Client {
    static final String esbUrl = GlobalConfig.getString("Selected.Service.Url");//"http://ws.selected.uat.qa.nt.ctripcorp.com/PublicApi/json/";
    private static final Logger logger = LoggerFactory.getLogger(SOA2Client.class);
    JsonMapper mapper = new JsonMapper();
    private static String productRequest="{\"RequestHead\":{\"Channel\":\"0000\",\"ExternalChannel\":\"String\",\"Auth\":\"String\",\"Culture\":\"zh-CN\",\"SessionId\":\"String\",\"ClientIP\":\"String\"},\"Body\":{\"ProductId\":%s,\"MerchantId\":0,\"MapPosition\":{\"Longitude\":0,\"Latitude\":0},\"ImageQuality\":0}}";
    private static String merchantRequest="{\"RequestHead\":{\"Channel\":\"0000\",\"ExternalChannel\":\"String\",\"Auth\":\"String\",\"Culture\":\"zh-CN\",\"SessionId\":\"String\",\"ClientIP\":\"String\"},\"Body\":{\"MerchantId\":%s,\"ImageQuality\":0}}";
    public Map<String,Object> getMerchantDetail(String merchantId){
        if(StringUtils.isNumeric(merchantId) && !"0".equals(merchantId)) {
            try {
                String response = Request.Post(esbUrl + "appmerchantdetailsearch").body(new StringEntity(String.format(merchantRequest, merchantId), "UTF-8")).
                        addHeader("Content-Type", "application/json; charset=utf-8").connectTimeout(1000).socketTimeout(1000).
                        execute().returnContent().asString().toLowerCase();
                Map<String, Object> result = mapper.fromJson(response, HashMap.class);
                if (result != null && result.size() > 0) {
                    return (Map<String, Object>) result.get("body");
                }
            } catch (IOException e) {
                logger.warn("AppMerchantDetailSearch异常", e);
            }
        }
        return null;
    }

    public Map<String,Object> getProductDetailSearch(String productId){
        if(StringUtils.isNumeric(productId) && !"0".equals(productId)) {
            try {
                String response = Request.Post(esbUrl + "appproductdetailsearch").body(new StringEntity(String.format(productRequest, productId), "UTF-8")).
                        addHeader("Content-Type", "application/json; charset=utf-8").connectTimeout(1000).socketTimeout(1000).
                        execute().returnContent().asString().toLowerCase();
                Map<String, Object> result = mapper.fromJson(response, HashMap.class);
                if (result != null && result.size() > 0) {
                    return (Map<String, Object>) result.get("body");
                }
            } catch (IOException e) {
                logger.warn("AppProductDetailSearch异常", e);
            }
        }
        return null;
    }
}

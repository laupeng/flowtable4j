package com.ctrip.infosec.flowtable4j.translate.UnitConverters;

import com.ctrip.infosec.flowtable4j.translate.dao.HotelGroupSources;
import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.ctrip.infosec.flowtable4j.translate.service.CommonOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;
/**
 * Created by lpxie on 15-6-10.
 */
public class HotelGroupConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(HotelGroupConvert.class);

    @Resource(name="allTemplates")
    private AllTemplates allTemplates;

    JdbcTemplate cardRiskDBTemplate = null;
    JdbcTemplate riskCtrlPreProcDBTemplate = null;
    JdbcTemplate cUSRATDBTemplate = null;

    @Autowired
    CommonOperation commonOperation;

    @Autowired
    HotelGroupSources hotelGroupSources;
    /**
     * 初始化jndi
     */
    private void init()
    {
        cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
        cUSRATDBTemplate = allTemplates.getcUSRATDBTemplate();
    }

    @Override
    public void completeData(DataFact dataFact, Map data)
    {
        String checkType = getValue(data, Common.CheckType);
        if(checkType.equals("0") || checkType.equals("1"))
        {
            dataFact.productInfoM.put(HotelGroup.City,getValue(data,HotelGroup.City));
            dataFact.productInfoM.put(HotelGroup.Price,getValue(data,HotelGroup.Price));//fixme 转成decimal
            dataFact.productInfoM.put(HotelGroup.ProductID,getValue(data,HotelGroup.ProductID));
            dataFact.productInfoM.put(HotelGroup.ProductName,getValue(data,HotelGroup.ProductName));
            dataFact.productInfoM.put(HotelGroup.Quantity,getValue(data,HotelGroup.Quantity));
            dataFact.productInfoM.put(HotelGroup.ProductType,getValue(data,HotelGroup.ProductType));
        }
        else if(checkType.equals("2"))
        {
            //通过lastReqID查询所有订单相关的信息 注意这里是上一次的reqid(当checkType=1的时候)
            String reqIdStr = getValue(data,Common.OldReqID);
            if(reqIdStr.isEmpty())
                return;
            try{
                Map hotelGroupProduct = hotelGroupSources.getHotelGroupInfo(reqIdStr);
                if(hotelGroupProduct != null && hotelGroupProduct.size()>0)
                    dataFact.productInfoM.putAll(hotelGroupProduct);
            }catch (Exception exp)
            {
                logger.warn("获取HotelGroupProductInfo异常:",exp);
            }
        }
    }

    public void convertToFlowRuleCheckItem(DataFact dataFact, Map data,Map flowData)
    {
        List<Map> paymentInfos = dataFact.paymentInfoList;
        for(Map paymentInfo : paymentInfos)
        {
            //Map subPaymentInfo = (Map)paymentInfo.get(Common.PaymentInfo);
            List<Map> cardInfoList = (List<Map>)paymentInfo.get(Common.CardInfoList);
            Map cardInfoFirst = cardInfoList.get(0);
            flowData.put(Common.CardBinOrderID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.mainInfo,Common.OrderID));
            flowData.put(Common.CardBinUID,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.userInfo,Common.Uid));
            flowData.put(Common.CardBinMobilePhone,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.contactInfo,Common.MobilePhone));
            flowData.put(Common.CardBinUserIPAdd,getValue(cardInfoFirst,Common.CardBin)+getValue(dataFact.ipInfo,Common.UserIPAdd));
            flowData.put(Common.ContactEMailCardBin,getValue(dataFact.contactInfo,Common.ContactEMail)+getValue(cardInfoFirst,Common.CardBin));
            break;
        }
        if(getValue(dataFact.contactInfo,Common.MobilePhone).length()>=7)//fixme 看看下面这段是不是都有用到，可以拿到common里面去
        {
            flowData.put(Common.UserIPAddMobileNumber,getValue(dataFact.ipInfo,Common.UserIPAdd)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
            flowData.put(Common.UIDMobileNumber,getValue(dataFact.userInfo,Common.Uid)+getValue(dataFact.contactInfo,Common.MobilePhone).substring(0,7));
        }

        //产品信息加到流量实体
        flowData.put("Quantity",getValue(dataFact.productInfoM,Common.Quantity));
        flowData.put("City",getValue(dataFact.productInfoM,Common.City));
        flowData.put("ProductID",getValue(dataFact.productInfoM,Common.ProductID));
        flowData.put("ProductName",getValue(dataFact.productInfoM,Common.ProductName));
        flowData.put("ProductType",getValue(dataFact.productInfoM,Common.ProductType));
        flowData.put("Price",getValue(dataFact.productInfoM,Common.Price));
    }

    @Override
    public void writeData(DataFact dataFact, String reqId, boolean isWrite, boolean isCheck)
    {

    }
}

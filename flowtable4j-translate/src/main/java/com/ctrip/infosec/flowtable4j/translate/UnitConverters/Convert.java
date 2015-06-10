package com.ctrip.infosec.flowtable4j.translate.UnitConverters;

import com.ctrip.infosec.flowtable4j.translate.model.DataFact;

import java.util.Map;

/**
 * Created by lpxie on 15-6-10.
 */
public interface Convert
{
    /**
     * 补充当前转换单元的数据
     * 这里可以根据具体实现类的配置来填充相应的数据
     */
    public void completeData(DataFact dataFact,Map data);

    /**
     * 把数据写到数据库
     * 这里可以根据具体实现类的配置来写相应字段到数据库
     */
    public void writeData(DataFact dataFact,final String reqId,boolean isWrite,boolean isCheck);
}

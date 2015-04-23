package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.common.model.RiskFact;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;

import static com.ctrip.infosec.configs.utils.Utils.JSON;

/**
 * Created by lpxie on 15-3-23.
 */
public class ReadFactFile
{
    public static Map getData(String jsonPath)
    {
        String str = null;
        try
        {
            str = IOUtils.toString(ReadFactFile.class.getClassLoader().getResourceAsStream(jsonPath), "utf-8");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Map fact = JSON.parseObject(str,Map.class);
        return fact;
    }
}

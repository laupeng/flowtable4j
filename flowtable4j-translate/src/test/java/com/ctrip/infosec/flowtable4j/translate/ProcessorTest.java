package com.ctrip.infosec.flowtable4j.translate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by lpxie on 15-4-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/preprocess-datasource.xml","classpath*:spring/dataProxy-venus-client.xml"})
public class ProcessorTest
{
    @Autowired
    Processor processor;

    @Test
    public void testHoteGroup()
    {
        Map data = ReadFactFile.getData("hotelGroup1.json");
        processor.execute(data);
    }
}

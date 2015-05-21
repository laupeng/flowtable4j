//package com.ctrip.infosec.flowtable4j.translate.flight;
//
//import com.ctrip.infosec.flowtable4j.translate.ReadFactFile;
//import com.ctrip.infosec.flowtable4j.translate.service.FlightExecutor;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.Map;
//
///**
// * Created by lpxie on 15-4-13.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:spring/preprocess-datasource-test.xml"})
//public class FlightExecutorTest
//{
//    @Autowired
//    private FlightExecutor flightExecutor;
//    @Test
//    @Ignore
//    public void testComplementData()
//    {
//        int checkType = 0;
//        Map data = ReadFactFile.getData("flight0.json");
//        flightExecutor.complementData(data,checkType);
//
//    }
//}

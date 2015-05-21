//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.ctrip.infosec.flowtable4j.translate;
//
//import org.apache.commons.dbcp.BasicDataSource;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// *
// * @author zhengby
// */
//
////deprecate 这个类不再使用
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:spring/preprocess-datasource-test.xml",
//        "classpath*:spring/preprocess-datasource-test.xml"})
//public class JdbcTemplateTest
//{
//    @Resource(name = "CardRiskDB")
//    JdbcTemplate cardRiskDBTemplate;
//    @Resource(name = "RiskCtrlPreProcDB")
//    JdbcTemplate riskCtrlPreProcDBTemplate;
//
//    @Test
//   // @Ignore
//    public void testQueryCardRiskDB() {
//        System.out.println("CardRiskDB");
//        List<Map<String, Object>> results = cardRiskDBTemplate.queryForList("select top 100* from dbo.InfoSecurity_FlowRule");
//        System.out.println("results: " + results.size());
//    }
//}

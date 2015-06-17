package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.accountsecurity.AccountBWGHandler;
import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountResult;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhangsx on 2015/5/19.
 */
@Component
public class BWGProcessor {

    @Autowired
    private AccountBWGHandler accountBWGHandler;

    private static Logger logger = LoggerFactory.getLogger(BWGProcessor.class);

    /**
     * 新增黑白名单
     * @param rules
     */
    public String setBWGRule(List<RuleContent> rules) {
        return accountBWGHandler.setBWGRule(rules);
    }

    /**
     * 删除黑白名单
     * @param rules
     */
    public String removeBWGRule(List<RuleContent> rules) {
        return accountBWGHandler.removeBWGRule(rules);
    }

    /**
     * 调用账户风控黑白名单
     * @param fact
     * @param result
     */
    public void checkBWGRule(AccountFact fact,AccountResult result){
        try {
            accountBWGHandler.checkBWGRule(fact, result.getResult());
        }
        catch (Exception ex){
            result.setStatus("FAIL");
            logger.warn("Call checkBWGRule fail",ex);
        }
    }
}

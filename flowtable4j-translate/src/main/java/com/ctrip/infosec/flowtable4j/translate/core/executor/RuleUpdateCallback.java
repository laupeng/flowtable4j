/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.translate.core.executor;

import com.ctrip.infosec.configs.ConfigsLoadedCallback;
import com.ctrip.infosec.flowtable4j.translate.core.engine.StatelessRuleEngine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author zhengby
 */
public class RuleUpdateCallback implements ConfigsLoadedCallback {

    @Autowired
    private StatelessRuleEngine statelessRuleEngine;

    @Override
    public void onConfigsLoaded() {
        statelessRuleEngine.updateRules();
    }

}

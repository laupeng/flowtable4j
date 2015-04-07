package com.ctrip.infosec.flowtable4j.translate.core.engine;

import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.configs.Configs;
import com.ctrip.infosec.configs.event.Rule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.AgendaFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.ctrip.infosec.configs.utils.Utils.JSON;

/**
 * Stateless引擎
 *
 * @author zhengbaiyun
 */
@ManagedResource
public class StatelessRuleEngine extends RuleEngine {

    private static final Logger logger = LoggerFactory.getLogger(StatelessRuleEngine.class);
    private StatelessKnowledgeSession statelessKSession;
    /**
     * Cache, Key为: packageName
     */
    Map<String, Rule> rulesInKBase = Maps.newHashMap();

    /**
     * 使用JMX查询规则集
     */
    @ManagedAttribute
    public String getPackageNamesInKBase() {
        return JSON.toPrettyJSONString(rulesInKBase.keySet());
    }

    public void execute(String packageName, RiskFact fact) {
        if (!isRuleInKBase(packageName)) {
//            logger.warn("\"" + packageName + "\" is not exists, ignored.");
            return;
        }
        try {
            AgendaFilter filter = new PackageAgendaFilter(packageName);
            execStatelessRule(filter, fact);
        } catch (Exception ex) {
            logger.error("execute \"" + packageName + "\" exception.", ex);
        }
    }

    protected void execStatelessRule(AgendaFilter filter, RiskFact fact) {
        ArrayList<Command> cmds = Lists.newArrayList();
        cmds.add(CommandFactory.newInsert(fact));
        cmds.add(CommandFactory.newSetGlobal("logger", logger));
        cmds.add(new FireAllRulesCommand(filter));
        statelessKSession.execute(CommandFactory.newBatchExecution(cmds));
    }

    /**
     * Init RuleEngine
     */
    protected void initKnowledgeBaseAndSession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Collection<KnowledgePackage> kpackage = kbuilder.getKnowledgePackages();
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbConf.setProperty("org.drools.sequential", "true");
        kbConf.setOption(EventProcessingOption.STREAM);
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(kbConf);
        knowledgeBase.addKnowledgePackages(kpackage);
        this.statelessKSession = knowledgeBase.newStatelessKnowledgeSession();
    }

    @Override
    public void initEngine() {
        logger.warn("exec initEngine() start.");
        initKnowledgeBaseAndSession();
        logger.warn("exec initEngine() end.");
    }

    /**
     * 规则更新
     */
    @Override
    public void updateRules() {
        logger.warn("exec updateRules() start.");
        Map<String, Rule> newRulesInKBase = Maps.newHashMap();

        // 删除&更新Route规则
        for (Rule rule : Configs.getRules()) {
            String packageName = rule.getRuleNo();
            Rule ruleInKBase = this.rulesInKBase.get(packageName);
            if (!rule.isEnabled()) {
                if (ruleInKBase != null) {
                    try {
                        logger.warn("remove rule: " + packageName);
                        Collection<KnowledgePackage> kpackagesInBase = this.getKnowledgePackagesFromString(ruleInKBase.getRuleContent());
                        this.removeKnowledgePackages(kpackagesInBase);
                    } catch (Exception ex) {
                        logger.error("remove rule failed.", ex);
                    }
                }
            } else {
                if (ruleInKBase == null
                        || ruleInKBase.getUpdatedAt().before(rule.getUpdatedAt())) {
                    try {
                        logger.warn("update rule: " + packageName);
                        Collection<KnowledgePackage> kpackages = this.getKnowledgePackagesFromString(rule.getRuleContent());
                        this.addKnowledgePackages(kpackages);
                    } catch (Exception ex) {
                        logger.error("update rule failed.", ex);
                    }
                }

                newRulesInKBase.put(packageName, rule);
            }
        }
        logger.warn("exec updateRules() end.");

        // 最后更新缓存
        this.rulesInKBase = newRulesInKBase;
    }

    /**
     * 判断一个规则是否在KnowledgeBase中
     */
    public boolean isRuleInKBase(String packageName) {
        return rulesInKBase.containsKey(packageName);
    }

}

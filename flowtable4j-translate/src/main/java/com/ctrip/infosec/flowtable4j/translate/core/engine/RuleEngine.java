package com.ctrip.infosec.flowtable4j.translate.core.engine;

import org.drools.KnowledgeBase;
import org.drools.builder.*;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;

import java.util.Collection;

/**
 * Created by lpxie on 15-4-2.
 */
public abstract class RuleEngine
{
    /*规则引擎当前编译的规则集*/
    protected KnowledgeBase knowledgeBase;
    /*初始化规则引擎*/
    public abstract void initEngine();
    /*规则更新*/
    public abstract void updateRules();

    protected void addKnowledgePackages(Collection<KnowledgePackage> kpackages)
    {this.knowledgeBase.addKnowledgePackages(kpackages);}

    protected void removeKnowledgePackages(Collection<KnowledgePackage> kpackages)
    {
        for(KnowledgePackage knowledgePackage:kpackages)
        {
            this.knowledgeBase.removeKnowledgePackage(knowledgePackage.getName());
        }
    }

    protected Collection<KnowledgePackage> getKnowledgePackagesFromString(String ruleContent) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(ruleContent.getBytes("UTF-8")), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            String errorMessage = "compile rule failed: " + "\n";
            for (KnowledgeBuilderError error : errors) {
                errorMessage += error.toString() + "\n";
            }
            throw new RuntimeException(errorMessage);
        }
        Collection<KnowledgePackage> kpackages = kbuilder.getKnowledgePackages();
        return kpackages;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.translate.core.engine;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

import java.util.List;

public class MultiPackageAgendaFilter implements AgendaFilter {

    private List<String> packageNames;

    public MultiPackageAgendaFilter(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    @Override
    public boolean accept(Activation activation) {
        String pkgName = (String) activation.getRule().getPackageName();
        return packageNames.contains(pkgName);
    }
}

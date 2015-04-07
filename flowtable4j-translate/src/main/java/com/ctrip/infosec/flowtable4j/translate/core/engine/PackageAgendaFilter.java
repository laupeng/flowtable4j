/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.translate.core.engine;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

public class PackageAgendaFilter implements AgendaFilter {

    private String packageName;

    public PackageAgendaFilter(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean accept(Activation activation) {
        String pkgName = (String) activation.getRule().getPackageName();
        return packageName.equals(pkgName);
    }
}

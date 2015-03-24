package com.ctrip.infosec.flowtable4j.model.check;

import com.ctrip.infosec.flowtable4j.model.account.AccountCheckItem;
import com.ctrip.infosec.flowtable4j.model.bw.BWFact;

/**
* Created by zhangsx on 2015/3/24.
*/
public class CheckEntity {
    private CheckType[] checkTypes;
    private AccountCheckItem accountCheckItem;
    private BWFact bwFact;

    public CheckType[] getCheckTypes() {
        return checkTypes;
    }

    public void setCheckTypes(CheckType[] checkTypes) {
        this.checkTypes = checkTypes;
    }

    public AccountCheckItem getAccountCheckItem() {
        return accountCheckItem;
    }

    public void setAccountCheckItem(AccountCheckItem accountCheckItem) {
        this.accountCheckItem = accountCheckItem;
    }

    public BWFact getBwFact() {
        return bwFact;
    }

    public void setBwFact(BWFact bwFact) {
        this.bwFact = bwFact;
    }
}

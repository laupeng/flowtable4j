package com.ctrip.infosec.flowtable4j.model;

import java.util.List;

/**
 * Created by thyang on 2015/3/27 0027.
 */
public class AccountFact {

    private List<AccountItem> checkItems;

    public List<AccountItem> getCheckItems() {
        return checkItems;
    }

    public void setCheckItems(List<AccountItem> checkItems) {
        this.checkItems = checkItems;
    }
}

package com.ctrip.infosec.flowtable4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by thyang on 2015/3/27 0027.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountFact {

    private List<AccountItem> checkItems;

    public List<AccountItem> getCheckItems() {
        return checkItems;
    }

    public void setCheckItems(List<AccountItem> checkItems) {
        this.checkItems = checkItems;
    }
}

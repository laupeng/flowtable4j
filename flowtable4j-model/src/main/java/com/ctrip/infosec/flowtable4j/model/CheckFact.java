package com.ctrip.infosec.flowtable4j.model;

/**
 * Created by zhangsx on 2015/3/24.
 */
public class CheckFact {
    private CheckType[] checkTypes;
    private AccountFact accountFact;
    private BWFact bwFact;
    private FlowFact flowFact;
    private long reqId;

    public long getReqId() {
        return reqId;
    }

    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    public CheckType[] getCheckTypes() {
        return checkTypes;
    }

    public void setCheckTypes(CheckType[] checkTypes) {
        this.checkTypes = checkTypes;
    }

    public BWFact getBwFact() {
        return bwFact;
    }

    public void setBwFact(BWFact bwFact) {
        this.bwFact = bwFact;
    }

    public FlowFact getFlowFact() {
        return flowFact;
    }

    public void setFlowFact(FlowFact flowFact) {
        this.flowFact = flowFact;
    }

    public AccountFact getAccountFact() {
        return accountFact;
    }

    public void setAccountFact(AccountFact accountFact) {
        this.accountFact = accountFact;
    }
}

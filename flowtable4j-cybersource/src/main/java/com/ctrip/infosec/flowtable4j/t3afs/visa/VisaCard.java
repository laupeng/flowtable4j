package com.ctrip.infosec.flowtable4j.t3afs.visa;
import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;

import java.math.BigInteger;

public class VisaCard extends BaseNode {
    private String accountNumber;
    private BigInteger expirationMonth;
    private BigInteger expirationYear;
    private String cardType;
    @Override
    public String toXML(){
        StringBuilder sb = new StringBuilder();
        sb.append("<card>\n");
        createNode(sb,"accountNumber",accountNumber);
        createNode(sb,"expirationMonth",expirationMonth.toString());
        createNode(sb,"expirationYear",expirationYear.toString());
        createNode(sb,"cardType",cardType);
        sb.append("</card>\n");
        return sb.toString();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigInteger getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(BigInteger expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public BigInteger getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(BigInteger expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}

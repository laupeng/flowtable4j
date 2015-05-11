package com.ctrip.infosec.flowtable4j.model;

/**
 * Created by zhangsx on 2015/5/7.
 */
public class InfoSecurity_VacationOptionInfo {
    public int vacationOption;
    public int vacationInfoID;
    public int optionID;
    public String optionName;
    public int  optionQty;
    public String funSubOrderType;
    public String supplierID;
    public String supplierName;

    public int getVacationOption() {
        return vacationOption;
    }

    public void setVacationOption(int vacationOption) {
        this.vacationOption = vacationOption;
    }

    public int getVacationInfoID() {
        return vacationInfoID;
    }

    public void setVacationInfoID(int vacationInfoID) {
        this.vacationInfoID = vacationInfoID;
    }

    public int getOptionID() {
        return optionID;
    }

    public void setOptionID(int optionID) {
        this.optionID = optionID;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public int getOptionQty() {
        return optionQty;
    }

    public void setOptionQty(int optionQty) {
        this.optionQty = optionQty;
    }

    public String getFunSubOrderType() {
        return funSubOrderType;
    }

    public void setFunSubOrderType(String funSubOrderType) {
        this.funSubOrderType = funSubOrderType;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}

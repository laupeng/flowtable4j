package com.ctrip.infosec.flowtable4j.model;

import java.util.Date;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class BaseData_IPInfo {
    private long startAddr;
    private long endAddr;
    private String type_Company;
    private String country;
    private String province;
    private String city;
    private int countryId;
    private int provinceId;
    private int cityId;
    private String latitude;
    private String longitude;
    private String area;
    private String remark;
    private Date dataChange_LastTime;
    private String nationCode;
    private int continentID;

    public long getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(long startAddr) {
        this.startAddr = startAddr;
    }

    public long getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(long endAddr) {
        this.endAddr = endAddr;
    }

    public String getType_Company() {
        return type_Company;
    }

    public void setType_Company(String type_Company) {
        this.type_Company = type_Company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDataChange_LastTime() {
        return dataChange_LastTime;
    }

    public void setDataChange_LastTime(Date dataChange_LastTime) {
        this.dataChange_LastTime = dataChange_LastTime;
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    public int getContinentID() {
        return continentID;
    }

    public void setContinentID(int continentID) {
        this.continentID = continentID;
    }
}

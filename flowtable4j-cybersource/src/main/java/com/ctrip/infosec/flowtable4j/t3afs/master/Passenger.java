package com.ctrip.infosec.flowtable4j.t3afs.master;

import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class Passenger extends BaseNode {
    private String first_name;
    private String surname;
    private String passenger_type;
    private String nationality;
    private String id_number;
    private String loyalty_number;
    private String loyalty_type;
    private String loyalty_tier;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<Passenger>\n");
        createNode(sb,"first_name", first_name);
        createNode(sb,"surname", surname);
        createNode(sb,"passenger_type",passenger_type);
        createNode(sb,"nationality",nationality);
        createNode(sb,"id_number", id_number);
        createNode(sb,"loyalty_number", loyalty_number);
        createNode(sb,"loyalty_type", loyalty_type);
        createNode(sb,"loyalty_tier", loyalty_tier);
        sb.append("</Passenger>\n");
        return sb.toString();
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassenger_type(String passenger_type) {
        this.passenger_type = passenger_type;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public void setLoyalty_number(String loyalty_number) {
        this.loyalty_number = loyalty_number;
    }

    public void setLoyalty_type(String loyalty_type) {
        this.loyalty_type = loyalty_type;
    }

    public void setLoyalty_tier(String loyalty_tier) {
        this.loyalty_tier = loyalty_tier;
    }
}

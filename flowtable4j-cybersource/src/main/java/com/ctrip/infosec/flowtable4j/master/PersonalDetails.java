package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class PersonalDetails extends BaseNode {
    private String first_name;
    private String  surname;
    private String  telephone;
    private String  telephone_2;
    private String  date_of_birth;
    private String  nationality;
    private String  id_number;
    private String  id_type;
    private String  ssn;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<PersonalDetails>\n");
        createNode(sb,"first_name", first_name);
        createNode(sb,"surname", surname);
        createNode(sb,"telephone",telephone);
        createNode(sb,"telephone_2", telephone_2);
        createNode(sb,"date_of_birth", date_of_birth);
        createNode(sb,"nationality",nationality);
        createNode(sb,"id_number", id_number);
        createNode(sb,"id_type", id_type);
        createNode(sb,"ssn", ssn);
        sb.append("</PersonalDetails>\n");
        return sb.toString();
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setTelephone_2(String telephone_2) {
        this.telephone_2 = telephone_2;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
}

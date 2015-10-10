package com.ctrip.infosec.flowtable4j.t3afs.master;

import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class Leg extends BaseNode {
    private String depart_airport;
    private String depart_country;
    private String depart_datetime;
    private String depart_airport_timezone;
    private String arrival_airport;
    private String carrier;
    private String flight_number;
    private String fare_basiscode;
    private String fare_class;
    private String base_fare;
    private String currency_code;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<Leg>\n");
        createNode(sb,"depart_airport", depart_airport);
        createNode(sb,"depart_country", depart_country);
        createNode(sb,"depart_datetime",depart_datetime);
        createNode(sb,"depart_airport_timezone",depart_airport_timezone);
        createNode(sb,"arrival_airport", arrival_airport);
        createNode(sb,"carrier", carrier);
        createNode(sb,"flight_number", flight_number);
        createNode(sb,"fare_basiscode", fare_basiscode);
        createNode(sb,"fare_class", fare_class);
        createNode(sb,"base_fare", base_fare);
        createNode(sb,"currency_code", currency_code);
        sb.append("</Leg>\n");
        return sb.toString();
    }

    public void setDepart_airport(String depart_airport) {
        this.depart_airport = depart_airport;
    }

    public void setDepart_country(String depart_country) {
        this.depart_country = depart_country;
    }

    public void setDepart_datetime(String depart_datetime) {
        this.depart_datetime = depart_datetime;
    }

    public void setDepart_airport_timezone(String depart_airport_timezone) {
        this.depart_airport_timezone = depart_airport_timezone;
    }

    public void setArrival_airport(String arrival_airport) {
        this.arrival_airport = arrival_airport;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setFlight_number(String flight_number) {
        this.flight_number = flight_number;
    }

    public void setFare_basiscode(String fare_basiscode) {
        this.fare_basiscode = fare_basiscode;
    }

    public void setFare_class(String fare_class) {
        this.fare_class = fare_class;
    }

    public void setBase_fare(String base_fare) {
        this.base_fare = base_fare;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }
}

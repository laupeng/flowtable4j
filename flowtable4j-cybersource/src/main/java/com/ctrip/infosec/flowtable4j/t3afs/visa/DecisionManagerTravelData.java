package com.ctrip.infosec.flowtable4j.t3afs.visa;

import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;

public class DecisionManagerTravelData extends BaseNode {
    protected String departureDateTime;
    protected String completeRoute;
    protected String journeyType;

    @Override
    public String toXML(){
        StringBuilder sb=new StringBuilder();
        sb.append("<travelData>\n");
        createNode(sb,"departureDateTime",departureDateTime);
        createNode(sb,"completeRoute",completeRoute);
        createNode(sb,"journeyType",journeyType);
        sb.append("</travelData>\n");
        return sb.toString();
    }


    public String getDepartureDateTime() {
        return departureDateTime;
    }


    public void setDepartureDateTime(String value) {
        this.departureDateTime = value;
    }


    public String getCompleteRoute() {
        return completeRoute;
    }


    public void setCompleteRoute(String value) {
        this.completeRoute = value;
    }


    public String getJourneyType() {
        return journeyType;
    }


    public void setJourneyType(String value) {
        this.journeyType = value;
    }

}

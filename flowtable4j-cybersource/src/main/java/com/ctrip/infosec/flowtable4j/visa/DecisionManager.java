package com.ctrip.infosec.flowtable4j.visa;
import com.ctrip.infosec.flowtable4j.Common.BaseNode;
public class DecisionManager extends BaseNode {
    private DecisionManagerTravelData travelData;
    @Override
    public String toXML(){
        if(travelData!=null){
            StringBuilder sb=new StringBuilder();
            sb.append("<decisionManager>\n");
            createNode(sb,"enabled","true");
            sb.append(travelData.toXML());
            sb.append("</decisionManager>\n");
            return sb.toString();
        }
        return "";
    }

    public DecisionManagerTravelData getTravelData() {
        return travelData;
    }

    public void setTravelData(DecisionManagerTravelData value) {
        this.travelData = value;
    }

}

package com.ctrip.infosec.flowtable4j.t3afs.visa;
import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;
public class AFSService extends BaseNode {
    protected String avsCode;
    protected String cvCode;
    protected String disableAVSScoring;
    protected String customRiskModel;

    public String toXML(){
        StringBuilder sb=new StringBuilder("<afsService run=\"true\">");
            createNode(sb,"avsCode",avsCode);
            createNode(sb,"cvCode",cvCode);
            createNode(sb,"disableAVSScoring",disableAVSScoring);
            createNode(sb,"customRiskModel",customRiskModel);
            sb.append("</afsService>");
        return sb.toString();
    }


    public String getAvsCode() {
        return avsCode;
    }

    public void setAvsCode(String value) {
        this.avsCode = value;
    }

    public String getCvCode() {
        return cvCode;
    }

    public void setCvCode(String value) {
        this.cvCode = value;
    }

    public String getDisableAVSScoring() {
        return disableAVSScoring;
    }

    public void setDisableAVSScoring(String value) {
        this.disableAVSScoring = value;
    }

    public String getCustomRiskModel() {
        return customRiskModel;
    }

    public void setCustomRiskModel(String value) {
        this.customRiskModel = value;
    }

}

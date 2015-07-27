package com.ctrip.infosec.flowtable4j.visa;

import com.google.common.base.Strings;

/**
 * Created by thyang on 2015-07-23.
 */
public class BaseNode {
    public void createNode(StringBuilder sb,String nodeName,String value){
        if(!Strings.isNullOrEmpty(value)){
            sb.append("<").append(nodeName).append(">").append(value).append("</").append(nodeName).append(">\n");
        }
    }
    public String toXML(){
        return "";
    }
    public String toXML(int sequence){
        return "";
    }
}

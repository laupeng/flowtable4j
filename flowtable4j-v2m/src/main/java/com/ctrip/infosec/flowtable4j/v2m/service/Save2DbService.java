package com.ctrip.infosec.flowtable4j.v2m.service;

import com.ctrip.infosec.flowtable4j.model.persist.PO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class Save2DbService {
    public Map<String,String> getDbMeta(String tableName){
        return new HashMap<String, String>();
    }
    public void save(List<String> propertiesToSave,PO po)
    {
        for(String s: po.getProp2Table().keySet()) {
            if(propertiesToSave.contains(s)){
                //To Do
            }
        }
    }
}

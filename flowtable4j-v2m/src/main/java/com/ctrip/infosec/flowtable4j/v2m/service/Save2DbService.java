package com.ctrip.infosec.flowtable4j.v2m.service;

import com.ctrip.infosec.flowtable4j.model.persist.PO;

import java.util.List;

/**
 * Created by thyang on 2015-06-12.
 */
public class Save2DbService {
    public void save(List<String> propertiesToSave,PO po)
    {
        for(String s: po.getProp2Table().keySet()) {
            if(propertiesToSave.contains(s)){
                //To Do
            }
        }
    }
}

package com.ctrip.infosec.flowtable4j.model.persist;

import java.util.Map;

/**
 * Created by thyang on 2015-06-09.
 */
public interface IMapAble {
    Map<String,Object> toMap();
}

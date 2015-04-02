package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class ParameterDeamon {
    /**
     * 开启监听
     */
    private Logger logger = LoggerFactory.getLogger(ParameterDeamon.class);
    private Map checkType2Int = new HashMap();
    private Map sceneType2Int = new HashMap();
    //TODO template PCIDB 实现
    @Autowired
    @Qualifier("pciAccountRiskDetailDBTemplate")
    private JdbcTemplate template;

    @Scheduled(fixedDelay = 5*60*1000)
    public void startWatch() {
        String sql = "Select ParamType,CheckType,SceneType,ResultLevel,ParamValue From AccountSecurity_Param with (nolock) Where ParamType <= 2";
        List<Map<String, Object>> ips = template.queryForList(sql);
        if (ips != null && ips.size() > 0) {
            String key = "";
            String pType, checkType, stype, resultlv, paraVal;
            for (Map p : ips) {
                pType = Objects.toString(p.get("ParamType"), "");
                checkType = Objects.toString(p.get("CheckType"), "");
                stype = Objects.toString(p.get("SceneType"), "");
                resultlv = Objects.toString(p.get("ResultLevel"), "");
                paraVal = Objects.toString(p.get("ParamValue"), "");

                Objects.toString("", "");
                if ("1".equals(pType)) {
                    if (!checkType2Int.containsKey(checkType)) {
                        checkType2Int.put(checkType, resultlv);
                    }
                } else if ("2".equals(pType)) {
                    if (!sceneType2Int.containsKey(stype)) {
                        sceneType2Int.put(stype, resultlv);
                    }
                }
            }
        }
    }


    /**
     * 获取检验类型 int
     *
     * @param key
     * @return
     */
    public int getCheckType(String key) {
        if (!Strings.isNullOrEmpty(key)) {
            key = key.toUpperCase();
            if (checkType2Int.containsKey(key)) {
                return Integer.parseInt(checkType2Int.get(key).toString());
            }
        }
        return 0;
    }

    /**
     * 获取场景类型 int
     *
     * @param key
     * @return
     */
    public int getSceneType(String key) {
        if (!Strings.isNullOrEmpty(key)) {
            key = key.toUpperCase();
            if (sceneType2Int.containsKey(key)) {
                return Integer.parseInt(sceneType2Int.get(key).toString());
            }
        }
        return 0;
    }
}

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

    private Logger logger = LoggerFactory.getLogger(ParameterDeamon.class);
    private Map<String,String> checkType2Int = new HashMap<String,String>();
    private Map<String,String> sceneType2Int = new HashMap<String,String>();

    @Autowired
    @Qualifier("pciAccountRiskDetailDBTemplate")
    private JdbcTemplate template;

    @Scheduled(fixedDelay = 5*60*1000)
    public void startWatch() {
        String sql = "Select ParamType,CheckType,SceneType,ResultLevel " +
                     "From AccountSecurity_Param with (nolock) " +
                     "Where ParamType <= 2";
        List<Map<String, Object>> ips = template.queryForList(sql);
        if (ips != null && ips.size() > 0) {
            String pType, checkType, sceneType, resultLevel;
            for (Map p : ips) {
                pType = Objects.toString(p.get("ParamType"), "");
                checkType = Objects.toString(p.get("CheckType"), "").toUpperCase();
                sceneType = Objects.toString(p.get("SceneType"), "").toUpperCase();
                resultLevel = Objects.toString(p.get("ResultLevel"), "0");
                if ("1".equals(pType)) {
                    if (!checkType2Int.containsKey(checkType)) {
                        checkType2Int.put(checkType, resultLevel);
                    }
                } else if ("2".equals(pType)) {
                    if (!sceneType2Int.containsKey(sceneType)) {
                        sceneType2Int.put(sceneType, resultLevel);
                    }
                }
            }
        }
    }


    /**
     * 获取检验类型 int
     * @param key
     * @return
     */
    public int getCheckType(String key) {
        if (!Strings.isNullOrEmpty(key)) {
            key = key.toUpperCase();
            if (checkType2Int.containsKey(key)) {
                return Integer.parseInt(checkType2Int.get(key));
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
                return Integer.parseInt(sceneType2Int.get(key));
            }
        }
        return 0;
    }
}

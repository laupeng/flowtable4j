package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.Map;

/**
 * Created by thyang on 2015-07-27.
 */
@Component
public class FltPubDbService {
    protected static JsonMapper mapper = new JsonMapper();
    @Autowired
    @Qualifier("fltPubDbTemplate")
    public JdbcTemplate jdbcTemplate;

    public int getCityCodeByAirPort(String airPort) {
        String sql = "SELECT City FROM AirPort(nolock) WHERE AirPort=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, new Object[]{airPort}, new int[]{Types.VARCHAR});
        if (map != null && map.size() > 0) {
            return Integer.parseInt(MapX.getString(map, "City"));
        }
        return 0;
    }

}

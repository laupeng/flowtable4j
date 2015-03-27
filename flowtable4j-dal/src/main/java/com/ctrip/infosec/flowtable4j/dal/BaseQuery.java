package com.ctrip.infosec.flowtable4j.dal;

import java.sql.SQLException;
import java.util.List;

public interface BaseQuery<T> {
	List<T> getAll() throws SQLException;
	
}

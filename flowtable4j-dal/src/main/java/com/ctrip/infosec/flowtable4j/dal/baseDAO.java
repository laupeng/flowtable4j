package com.ctrip.infosec.flowtable4j.dal;

import java.util.List;


public interface baseDAO<T> {
	int save(T entity);
	int update(T entity);
	int delete(T entity);
	List<T> getList(String sql);
}

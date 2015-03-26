package com.ctrip.infosec.flowtable4j.dal;

import java.sql.SQLException;
import java.util.List;




public interface baseDAO<T,TKey> {
	int insert(T entity) throws SQLException;
	int update(T entity) throws SQLException;
	int delete(T entity) throws SQLException;
	List<T> getAll() throws SQLException;
	public T queryByPk(TKey id) throws SQLException;
}

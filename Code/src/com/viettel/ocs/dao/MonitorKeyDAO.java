package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.MonitorKey;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.Qos;

public class MonitorKeyDAO extends BaseDAO<MonitorKey> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<MonitorKey> loadMonitorKeyByMonitorKey(Long monitorKeyId ,Long monitorKey) {
		String[] col = {"monitorKeyId" ,"monitorKey" };
		Operator[] ope = {Operator.NOTEQ , Operator.EQ };
		Object[] val = {monitorKeyId, monitorKey };
		return this.findByConditionsWithoutDomain(col, ope, val, "");
	}
	
	public int countMonitorKey() {
		String hql = "SELECT COUNT(a) FROM MonitorKey a ";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	@Override
	protected Class<MonitorKey> getEntityClass() {
		return MonitorKey.class;
	}
}

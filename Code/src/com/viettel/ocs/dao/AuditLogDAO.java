package com.viettel.ocs.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.AuditLog;
import com.viettel.ocs.entity.SysUser;

/***
 * 
 * @author Nampv
 *
 */
public class AuditLogDAO extends BaseDAO<AuditLog> implements Serializable {

	public List<AuditLog> findAuditLogByUserIdAndCreateDate(String userName, Date startDate, Date endDate) {
		List<AuditLog> lst = null;
		String hql = "SELECT a FROM AuditLog a ";
		hql += " WHERE DATE(a.createDate) BETWEEN :startDate AND :endDate";
		if (userName != null && !userName.equals("")) {
			hql += " AND a.userName =:userName";
		}
		hql += " ORDER BY createDate DESC";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			if (userName != null && !userName.equals("")) {
				query.setParameter("userName", userName);
			}
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}

	@Override
	protected Class<AuditLog> getEntityClass() {
		return AuditLog.class;
	}
	
}

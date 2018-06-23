package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.Qos;
import com.viettel.ocs.entity.RateTable;

public class QosDAO extends BaseDAO<Qos> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected Class<Qos> getEntityClass() {
		return Qos.class;
	}
	
	public List<Qos> loadProfilePepByPPName(Long qosId ,String qosName) {
		String[] col = {"qosId" ,"qosName" };
		Operator[] ope = {Operator.NOTEQ , Operator.EQ };
		Object[] val = {qosId, qosName };
		String oder = "qosName";
		return this.findByConditionsWithoutDomain(col, ope, val, oder);
	}
	
	public int countQos() {
		String hql = "SELECT COUNT(a) FROM Qos a ";
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
}
package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.Qos;
import com.viettel.ocs.entity.QosClass;

public class QosClassDAO extends BaseDAO<QosClass> implements Serializable {

	/**
	 * @author Namkaka
	 */
	private static final long serialVersionUID = -2920544893602743201L;

	@Override
	protected Class<QosClass> getEntityClass() {
		return QosClass.class;
	}

	public List<QosClass> findAllQosClass() {
		List<QosClass> lstQosClass = null;
		String hql = "SELECT qc FROM QosClass qc";
		//hql += " WHERE qc.qci < :qci order by qci ASC";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			//query.setParameter("qci", 10L);
			lstQosClass = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lstQosClass;
	}

}

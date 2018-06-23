package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.ObjectField;

public class ObjectFieldDAO extends BaseDAO<ObjectField> {

	public List<ObjectField> findObjectFieldChild(Long objectFieldParent) {
		List<ObjectField> lstChildren = null;
		String hql = "SELECT obj FROM ObjectField obj";
		hql += " WHERE obj.objectFieldParent = :objectFieldParent ORDER BY objectFieldId ASC";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<ObjectField> query = session.createQuery(hql);
			query.setParameter("objectFieldParent", objectFieldParent);
			lstChildren = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lstChildren;
	}

	@Override
	protected Class<ObjectField> getEntityClass() {
		return ObjectField.class;
	}

}

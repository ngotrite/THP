package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.TriggerDestination;

public class TriggerDestinationDAO extends BaseDAO<TriggerDestination> implements Serializable {

	public List<TriggerDestination> findTriggerDesByCategoryId(Long categoryId) {
		List<TriggerDestination> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index");
		return lst;

	}

	public boolean checkName(TriggerDestination triggerDestination, String destName, boolean edit) {
		List<TriggerDestination> lst = null;
		String[] cols = { "destName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { destName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getDestinationId() == triggerDestination.getDestinationId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	@Override
	protected Class<TriggerDestination> getEntityClass() {
		return TriggerDestination.class;
	}

	public int countTriggerDestination() {
		String hql = "SELECT COUNT(a) FROM TriggerDestination a WHERE  a.domainId =:domainId ";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Long getMaxIndex() {
		return getMaxIndex("index");
	}

	public boolean saveTrigerDes(TriggerDestination triggerDestinationUI) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			triggerDestinationUI.setDomainId(getDomainId());
			generateIndexByCat(triggerDestinationUI, "destinationId", session);
			session.saveOrUpdate(triggerDestinationUI);
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

}

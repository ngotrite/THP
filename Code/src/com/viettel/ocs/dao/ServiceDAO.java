package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.Service;
import com.viettel.ocs.entity.UnitType;

public class ServiceDAO extends BaseDAO<Service> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Service> findServiceByConditions(Long categoryId) {
		List<Service> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index ASC");
		return lst;
	}

	@Override
	protected Class<Service> getEntityClass() {
		return Service.class;
	}

	public boolean checkName(Service service, String serviceName, boolean edit) {
		List<Service> lst = null;
		String[] cols = { "serviceName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { serviceName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getServiceId() == service.getServiceId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public int countService() {
		String hql = "SELECT COUNT(a) FROM Service a WHERE  a.domainId =:domainId ";
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

	public long getMaxIndex() {
		return getMaxIndex("index");
	}
	
	public boolean saveService(Service service) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			service.setDomainId(getDomainId());
			generateIndexByCat(service, "serviceId", session);
			session.saveOrUpdate(service);
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

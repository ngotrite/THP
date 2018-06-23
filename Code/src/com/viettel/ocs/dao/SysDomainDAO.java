package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.SysDomain;
import com.viettel.ocs.util.CommonUtil;

public class SysDomainDAO extends BaseDAO<SysDomain> {

	@Override
	protected Class<SysDomain> getEntityClass() {
		return SysDomain.class;
	}
	
	@Override
	public List<SysDomain> findAll(String orders) { 
		
		return super.findAllWithoutDomain(orders);
	}
	
	public List<SysDomain> findAllActive(boolean isActive) { 
		
		Session session = HibernateUtil.getOpenSession();
		try {
			
			String hql = "SELECT a FROM SysDomain a WHERE a.isActive = :isActive";
			Query<SysDomain> query = session.createQuery(hql);
			query.setParameter("isActive", isActive);
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	/***
	 * 
	 * @param name
	 * @param notId: < 0: bo qua; >=0: tim khac id truyen vao
	 * @return
	 */
	public SysDomain findByName(String name, long notId) {
		
		if(CommonUtil.isEmpty(name))
			return null;
		
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT a FROM SysDomain a WHERE a.name LIKE ?";
		params.add(name.toLowerCase());
		if(notId >= 0) {
			hql += " AND a.id <> ?";
			params.add(notId);
		}
		
		List<SysDomain> lst;
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query<SysDomain> query = session.createQuery(hql);
			int i = 0;
			for(Object obj: params) {
				query.setParameter(i, obj);
				i++;
			}

			lst = query.getResultList();
			if(lst.size() > 0)
				return lst.get(0);			
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		
		return null;
	}
	
	public SysDomain cloneSysDomain(SysDomain sysDomain, String suffix) throws CloneNotSupportedException {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			SysDomain sysDomainCloned = this.cloneSysDomain(sysDomain, session, suffix);
			session.getTransaction().commit();
			return sysDomainCloned;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	private SysDomain cloneSysDomain(SysDomain sysDomain, Session session, String suffix) throws CloneNotSupportedException {
		try {
			SysDomain sysDomainToClone = sysDomain.clone();
			sysDomainToClone.setId(this.getNewId(session) + 1L);
			sysDomainToClone.setName(generateNameObjectWithoutDomain("name", sysDomainToClone.getName() + suffix, 0, session));
			session.save(sysDomainToClone);
			return sysDomainToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}
	
	private Long getNewId(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a.id FROM SysDomain a");
			hql.append(" ORDER BY a.id DESC");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setMaxResults(1);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}
}

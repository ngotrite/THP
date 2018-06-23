package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.MapAcmbalBal;
import com.viettel.ocs.entity.MapSharebalBal;
import com.viettel.ocs.entity.UnitType;

@SuppressWarnings("serial")
public class MapAcmbalBalDAO extends BaseDAO<MapAcmbalBal> implements Serializable {
	@Override
	protected Class<MapAcmbalBal> getEntityClass() {
		return MapAcmbalBal.class;
	}
	
	public List<MapAcmbalBal> findMABBByConditions(long categoryId) {

		List<MapAcmbalBal> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "index";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}
		
	public boolean checkName(MapAcmbalBal mapAcmBal, String name) {
		List<MapAcmbalBal> lst = null;
		String[] cols = { "name", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { name, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getMapAcmBaltypeId() == mapAcmBal.getMapAcmBaltypeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}
	
	
	public boolean checkBalTypeInMABBF(Long baltypeId, Long acmBaltypeId) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM MapAcmbalBal a");
		hql.append(" WHERE a.acmBaltypeId = :acmBaltypeId OR a.baltypeId = :baltypeId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("baltypeId", baltypeId);
			query.setParameter("acmBaltypeId", acmBaltypeId);
			query.setParameter("domainId", getDomainId());
			if (query.getSingleResult() > 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public int countAcmBalance() {
		String hql = "SELECT COUNT(a) FROM MapAcmbalBal a WHERE  a.domainId =:domainId";
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
	
	
	public boolean saveMapAcm(MapAcmbalBal mapAcmBal) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			mapAcmBal.setDomainId(getDomainId());
			generateIndexByCat(mapAcmBal, "mapAcmBaltypeId", session);
			session.saveOrUpdate(mapAcmBal);
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

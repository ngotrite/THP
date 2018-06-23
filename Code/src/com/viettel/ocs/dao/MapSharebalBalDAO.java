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

@SuppressWarnings("serial")
public class MapSharebalBalDAO extends BaseDAO<MapSharebalBal> implements Serializable {
	@Override
	protected Class<MapSharebalBal> getEntityClass() {
		return MapSharebalBal.class;
	}

	public List<MapSharebalBal> findMSBBByConditions(long categoryId) {

		List<MapSharebalBal> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "index";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public boolean checkName(MapSharebalBal mapShareBal, String name) {
		List<MapSharebalBal> lst = null;
		String[] cols = { "name", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { name, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getMapSharebalBalId() == mapShareBal.getMapSharebalBalId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public boolean checkBalTypeInMSBBF(Long baltypeId, Long shareType) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM MapSharebalBal a");
		hql.append(" WHERE a.shareType = :shareType OR a.baltypeId = :baltypeId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);

			query.setParameter("shareType", shareType);
			query.setParameter("baltypeId", baltypeId);
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

	public Long getMaxIndex() {
		return getMaxIndex("index");
	}

	public boolean saveMapShare(MapSharebalBal mapShareBal) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			mapShareBal.setDomainId(getDomainId());
			generateIndexByCat(mapShareBal, "mapSharebalBalId", session);
			session.saveOrUpdate(mapShareBal);
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

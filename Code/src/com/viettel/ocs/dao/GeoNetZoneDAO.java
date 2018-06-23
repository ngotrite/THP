package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BillingCycle;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.GeoNetZone;

public class GeoNetZoneDAO extends BaseDAO<GeoNetZone> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<GeoNetZone> findGeoNetZoneByConditions(Long geoHomeZoneId) {
		List<GeoNetZone> lst = null;
		String[] cols = { "geoHomeZoneId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { geoHomeZoneId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}

	public List<GeoNetZone> findByGeoHomeZoneId(GeoHomeZone geoHomeZone) {
		String hql = " FROM GeoNetZone d WHERE d.geoHomeZoneId = :geoHomeZoneId AND d.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			@SuppressWarnings("unchecked")
			Query<GeoNetZone> query = session.createQuery(hql.toString());
			query.setParameter("geoHomeZoneId", geoHomeZone.getGeoHomeZoneId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Boolean checkFieldIsExist(String col, String value, GeoNetZone geoNetZone) {
		boolean result = false;

		int count = 0;

		if (geoNetZone == null || geoNetZone.getGeoNetZoneId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "geoNetZoneId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, geoNetZone.getGeoNetZoneId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Boolean checkFieldIsExist(String col, Date value, GeoNetZone geoNetZone) {
		boolean result = false;

		int count = 0;

		if (geoNetZone == null || geoNetZone.getGeoNetZoneId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "geoNetZoneId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, geoNetZone.getGeoNetZoneId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Boolean checkFieldIsExist(String col, Long value, GeoNetZone geoNetZone) {
		boolean result = false;

		int count = 0;

		if (geoNetZone == null || geoNetZone.getGeoNetZoneId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "geoNetZoneId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, geoNetZone.getGeoNetZoneId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public boolean checkNameCellID(GeoNetZone geoNetZone, Long cellId) {
		List<GeoNetZone> lst = null;
		String[] cols = { "cellId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { cellId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getGeoNetZoneId() == geoNetZone.getGeoNetZoneId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}
	
	public boolean checkNameCellIDNew(GeoNetZone geoNetZone, Long cellId, Long geoHomeZoneId) {
		List<GeoNetZone> lst = null;
		String[] cols = { "cellId", "domainId", "geoHomeZoneId" };
		Operator[] operators = { Operator.EQ, Operator.EQ, Operator.EQ };
		Object[] values = { cellId, getDomainId(), geoHomeZoneId };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getGeoNetZoneId() == geoNetZone.getGeoNetZoneId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public boolean checkGNInGH(Long geoHomeZoneId) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM GeoNetZone a");
		hql.append(" WHERE a.geoHomeZoneId = :geoHomeZoneId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("geoHomeZoneId", geoHomeZoneId);
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
	
	public boolean delListGeoNet(List<GeoNetZone> listGeoNet) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		List<Long> listGeoNetId = new ArrayList<Long>();
		if (listGeoNet.size() > 0) {
			for (GeoNetZone geoNetZone : listGeoNet) {
				listGeoNetId.add(geoNetZone.getGeoNetZoneId());
			}
		}
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM GeoNetZone b");
			hql.append(" WHERE b.domainId = :domainId");
			hql.append(" AND b.geoNetZoneId IN (:listGeoNetId)");

			Query<GeoNetZone> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameterList("listGeoNetId", listGeoNetId);
			query.executeUpdate();
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
	

	@Override
	protected Class<GeoNetZone> getEntityClass() {
		// TODO Auto-generated method stub
		return GeoNetZone.class;
	}

}

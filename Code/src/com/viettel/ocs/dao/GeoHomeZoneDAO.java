package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.GeoNetZone;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneMap;

public class GeoHomeZoneDAO extends BaseDAO<GeoHomeZone> implements Serializable {

	public List<GeoHomeZone> findGHZByConditions(Long categoryId) {

		List<GeoHomeZone> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "index";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public List<GeoHomeZone> findGeoHomeZoneByCatId(List<Long> lstCatId) {
		List<GeoHomeZone> lstGeoHomeZone = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		lstGeoHomeZone = findByConditions(cols, operators, values, "");
		return lstGeoHomeZone;
	}

	public List<GeoHomeZone> findByCategory(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return this.findGeoHomeZoneByCatId(categoryIds);
	}

	public boolean checkName(GeoHomeZone geoHomeZone) {
		String hql = "SELECT COUNT(a) FROM GeoHomeZone a WHERE  a.domainId =:domainId";
		hql += " AND a.geoHomeZoneName LIKE :geoHomeZoneName";
		hql += " AND a.geoHomeZoneId != :geoHomeZoneId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("geoHomeZoneName", geoHomeZone.getGeoHomeZoneName());
			query.setParameter("geoHomeZoneId", geoHomeZone.getGeoHomeZoneId());
			return query.getSingleResult() > 0;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

	}

	public void saveGeoNetAndGeoHome(GeoHomeZone geoHomeZone, List<GeoNetZone> listGeoNetByGeoHome) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			geoHomeZone.setDomainId(getDomainId());
			generateIndexByCat(geoHomeZone, "geoHomeZoneId", session);
			session.saveOrUpdate(geoHomeZone);

			for (GeoNetZone geoNetZone : listGeoNetByGeoHome) {
				geoNetZone.setDomainId(getDomainId());
				geoNetZone.setGeoHomeZoneId(geoHomeZone.getGeoHomeZoneId());
				if (geoNetZone.getGeoNetZoneId() == 0L) {
					geoNetZone.setUpdateDate(new Date());
				}
				session.saveOrUpdate(geoNetZone);
			}

			session.getTransaction().commit();
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	protected Class<GeoHomeZone> getEntityClass() {
		return GeoHomeZone.class;
	}

	public int countGeoHomeZone() {
		String hql = "SELECT COUNT(a) FROM GeoHomeZone a WHERE  a.domainId =:domainId ";
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
}

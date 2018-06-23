package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.GeoNetZone;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneData;
import com.viettel.ocs.entity.ZoneMap;

public class ZoneDAO extends BaseDAO<Zone> implements Serializable {

	public List<Zone> findZoneByConditions(Long zoneMapId) {

		List<Zone> lst = null;
		String[] cols = { "zoneMapId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { zoneMapId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}

	public ZoneMap getZoneMap(Zone zone) {
		String hql = " FROM ZoneMap zm WHERE zm.zoneMapId = :zoneMapId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("zoneMapId", zone.getZoneMapId());
			return (ZoneMap) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	public List<Zone> findByZoneMapId(ZoneMap zoneMap) {
		String hql = " FROM Zone d WHERE d.zoneMapId = :zoneMapId AND d.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			@SuppressWarnings("unchecked")
			Query<Zone> query = session.createQuery(hql.toString());
			query.setParameter("zoneMapId", zoneMap.getZoneMapId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean checkName(Zone zone, String zoneName) {
		List<Zone> lst = null;
		String[] cols = { "zoneName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { zoneName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getZoneId() == zone.getZoneId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}
	
	public boolean checkName(Zone zone, String zoneName, Long zoneMapId) {
		List<Zone> lst = null;
		String[] cols = { "zoneName", "domainId", "zoneMapId" };
		Operator[] operators = { Operator.EQ, Operator.EQ, Operator.EQ };
		Object[] values = { zoneName, getDomainId(), zoneMapId};
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getZoneId() == zone.getZoneId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}
	

	public Boolean checkFieldIsExist(String col, String value, Zone zone) {
		boolean result = false;

		int count = 0;

		if (zone == null || zone.getZoneId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "zoneId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, zone.getZoneId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public boolean checkZoneByZM(long zoneMapId) {

		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM Zone a");
		hql.append(" WHERE a.zoneMapId = :zoneMapId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("zoneMapId", zoneMapId);
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

	@Override
	protected Class<Zone> getEntityClass() {
		return Zone.class;
	}

	public void saveZoneDataAndZone(Zone zone, List<ZoneData> listZoneDataByZone) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			zone.setDomainId(getDomainId());
			session.saveOrUpdate(zone);
			List<Long> ids = new ArrayList<Long>();
			ids.add(-1L);
			for (ZoneData zoneData : listZoneDataByZone) {
				if (zoneData.getZoneDataId() != 0L) {
					ids.add(zoneData.getZoneDataId());
				}
			}

			this.deleteZoneDataNotInList(zone, ids, session);

			for (ZoneData zoneData : listZoneDataByZone) {
				zoneData.setDomainId(getDomainId());
				zoneData.setZoneId(zone.getZoneId());
				if (zoneData.getZoneDataId() == 0l) {
					zoneData.setUpdateDate(new Date());
				}
				session.saveOrUpdate(zoneData);
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

	private void deleteZoneDataNotInList(Zone zone, List<Long> ids, Session session) {
		try {

			StringBuffer hql = new StringBuffer();
			hql.append("DELETE ZoneData a WHERE a.zoneId =:zoneId");
			hql.append(" AND a.zoneDataId NOT IN (:zoneDataIds)");
			hql.append(" AND a.domainId = :domainId");
			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("zoneId", zone.getZoneId());
			query.setParameter("zoneDataIds", ids);
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

}

package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ZoneData;

public class ZoneDataDAO extends BaseDAO<ZoneData> implements Serializable{
	@Override
	protected Class<ZoneData> getEntityClass() {
		return ZoneData.class;
	}
	
	public List<ZoneData> findZoneDataByConditions(Long zoneId) {

		List<ZoneData> lst = null;

		String[] cols = { "zoneId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { zoneId };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}
	
	
	public Boolean checkFieldIsExist(String col, String value, ZoneData zoneData){
		boolean result = false;
		
		int count =  0;
		
		if (zoneData == null || zoneData.getZoneDataId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count =  this.countByConditions(column, ope, val);
		} else {
			String[] column = { col , "zoneDataId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value , zoneData.getZoneDataId() };
			count =  this.countByConditions(column, ope, val);
		}
		
		if(count > 0){
			result = true;
		}
		
		return result;
	}
	
	public boolean checkZDByZone(long zoneId) {

		StringBuffer hql = new StringBuffer("SELECT COUNT(a) FROM ZoneData a");
		hql.append(" WHERE a.zoneId = :zoneId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("zoneId", zoneId);
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
		
	public boolean checkValue(ZoneData zoneData, String zoneDataValue) {
		List<ZoneData> lst = null;
		String[] cols = { "zoneDataValue", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { zoneDataValue, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getZoneDataId() == zoneData.getZoneDataId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean delListZD(List<ZoneData> listZD) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		List<Long> listZDId = new ArrayList<Long>();
		if (listZD.size() > 0) {
			for (ZoneData zD : listZD) {
				listZDId.add(zD.getZoneDataId());
			}
		}
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM ZoneData b");
			hql.append(" WHERE b.domainId = :domainId");
			hql.append(" AND b.zoneDataId IN (:listZDId)");

			Query<ZoneData> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameterList("listZDId", listZDId);
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
	
	public int countZoneData() {
		String hql = "SELECT COUNT(a) FROM ZoneData a WHERE  a.domainId =:domainId ";
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


}

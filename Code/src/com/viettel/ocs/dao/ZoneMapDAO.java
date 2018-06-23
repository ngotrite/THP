package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneMap;

@SuppressWarnings("serial")
public class ZoneMapDAO extends BaseDAO<ZoneMap> implements Serializable {
	public List<ZoneMap> findZMByConditions(Long categoryId) {

		List<ZoneMap> lst = null;		
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "index";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public List<ZoneMap> findZoneMapByCatId(List<Long> lstCatId) {
		List<ZoneMap> lstZoneMap = null;		
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		
		lstZoneMap = findByConditions(cols, operators, values, "");
		return lstZoneMap;
	}

	public List<ZoneMap> findByCategory(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return this.findZoneMapByCatId(categoryIds);
	}
	
	public boolean checkName(ZoneMap zoneMap, String zoneMapName) {
		List<ZoneMap> lst = null;
		String[] cols = { "zoneMapName" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { zoneMapName};
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getZoneMapId() == zoneMap.getZoneMapId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	@Override
	protected Class<ZoneMap> getEntityClass() {
		return ZoneMap.class;
	}

	public boolean saveZoneAndZomeMap(ZoneMap zoneMap, List<Zone> listZoneByZoneMap) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			zoneMap.setDomainId(getDomainId());
			generateIndexByCat(zoneMap, "zoneMapId", session);
			session.saveOrUpdate(zoneMap);

			for (Zone zone : listZoneByZoneMap) {
				zone.setDomainId(getDomainId());
				zone.setZoneMapId(zoneMap.getZoneMapId());
				session.saveOrUpdate(zone);
			}
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
	
	public Long getMaxIndex() {
		return getMaxIndex("index");
	}

}

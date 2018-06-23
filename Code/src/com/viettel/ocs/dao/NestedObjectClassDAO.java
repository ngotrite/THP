package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.NestedObject;
import com.viettel.ocs.entity.NestedObjectClass;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.NormalizerNormValueMap;

public class NestedObjectClassDAO extends BaseDAO<NestedObjectClass> {

	public List<NestedObjectClass> findObjectClassByObjClassID(Long objClassId) {
		List<NestedObjectClass> lstObjectClass = null;
		String[] cols = { "objClassId" };
		Operator[] operators = { Operator.GE };
		Object[] values = { objClassId };
		lstObjectClass = findByConditions(cols, operators, values, "");
		return lstObjectClass;
	}
	
	public List<NestedObjectClass> findByListObjectID(List<Long> lstObjClassId) {

		String[] cols = { "objClassId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstObjIDParam = new ArrayList<Long>();
		lstObjIDParam.addAll(lstObjClassId);
		if (lstObjIDParam.size() == 0)
			lstObjIDParam.add(-1L);
		Object[] values = { lstObjIDParam };
		return super.findByConditions(cols, operators, values, "");
	}
	
	public NestedObjectClass findNestedObjectClassByName(String name) {
		String hql = " FROM NestedObjectClass noc WHERE noc.objClassName=:objClassName";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("objClassName", name);
			return (NestedObjectClass) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	protected Class<NestedObjectClass> getEntityClass() {
		return NestedObjectClass.class;
	}

}

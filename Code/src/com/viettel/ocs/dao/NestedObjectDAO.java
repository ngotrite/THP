package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.NestedObject;
import com.viettel.ocs.entity.NestedObjectClass;

public class NestedObjectDAO extends BaseDAO<NestedObject> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<NestedObject> findNestedChildObjectByObjClassID(Long objClassId) {
		List<NestedObject> lstParent = null;
		List<NestedObject> lstChildren = new ArrayList<NestedObject>();
		String[] cols = { "objClassId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { objClassId };
		lstParent = findByConditionsWithoutDomain(cols, operators, values, "");

		for (int i = 0; i < lstParent.size(); i++) {
			// lstChildren.add(lstParent.get(i));
			String[] colsChld = { "parentClassId" };
			Operator[] operatorsChld = { Operator.EQ };
			Object[] valuesChld = { lstParent.get(i).getObjId() };
			lstChildren.addAll(findByConditionsWithoutDomain(colsChld, operatorsChld,
					valuesChld, ""));
		}

		return lstChildren;
	}
	
	public List<NestedObject> findAllNestedObject() {
		List<NestedObject> lstNestedObject = new ArrayList<NestedObject>();
		String[] cols = { "objClassId" };
		Operator[] operators = { Operator.GT };
		Object[] values = { (long)0 };
		lstNestedObject = findByConditionsWithoutDomain(cols, operators, values, "");
		return lstNestedObject;
	}
	
	public List<NestedObject> findObjectByObjParentNull() {
		List<NestedObject> lstChildren = new ArrayList<NestedObject>();
			String[] colsChld = { "parentClassId" };
			Operator[] operatorsChld = { Operator.EQ };
			Object[] valuesChld = { 100l };
			lstChildren.addAll(findByConditionsWithoutDomain(colsChld, operatorsChld,
					valuesChld, ""));

		return lstChildren;
	}
	
	public List<NestedObject> findObjectByObjParentID(Long parentId) {
		List<NestedObject> lstChildren = new ArrayList<NestedObject>();
			String[] colsChld = { "parentClassId" };
			Operator[] operatorsChld = { Operator.EQ };
			Object[] valuesChld = { parentId };
			lstChildren.addAll(findByConditionsWithoutDomain(colsChld, operatorsChld,
					valuesChld, ""));

		return lstChildren;
	}
	
	public int countChildObjectByObjId(Long objID) {
		
		String[] colsChld = { "parentClassId" };
		Operator[] operatorsChld = { Operator.EQ };
		Object[] valuesChld = { objID };
		return countByConditionsWithoutDomain(colsChld, operatorsChld, valuesChld);
	}

	public List<NestedObject> findChildObjectByObjId(Long objID) {
		List<NestedObject> lstChildren = new ArrayList<NestedObject>();
		String[] colsChld = { "parentClassId" };
		Operator[] operatorsChld = { Operator.EQ };
		Object[] valuesChld = { objID };
		lstChildren.addAll(findByConditionsWithoutDomain(colsChld, operatorsChld,
				valuesChld, ""));
		return lstChildren;
	}
	
	public NestedObject findNestedObjectByNameAndClass(String name, Long ClassID) {
		String hql = " FROM NestedObject no WHERE no.objName=:objName AND no.objClassId=:objClassId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("objName", name);
			query.setParameter("objClassId", ClassID);
			return (NestedObject) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public NestedObject findNestedObjectByNameAndParentID(String name, Long ParentID) {
		String hql = " FROM NestedObject no WHERE no.objName=:objName AND no.parentClassId=:parentClassId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("objName", name);
			query.setParameter("parentClassId", ParentID);
			return (NestedObject) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public NestedObject findNestedObjectParent(Long ParentID) {
		String hql = " FROM NestedObject no WHERE no.parentClassId=:parentClassId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("parentClassId", ParentID);
			return (NestedObject) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public NestedObject findNestedObjectByNameRoot(String name) {
		String hql = " FROM NestedObject no WHERE no.objName=:objName AND no.parentClassId = 100";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("objName", name);
			return (NestedObject) query.uniqueResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	protected Class<NestedObject> getEntityClass() {
		return NestedObject.class;
	}

}

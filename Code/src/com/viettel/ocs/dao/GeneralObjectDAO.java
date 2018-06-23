package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.GeneralObject;
import com.viettel.ocs.entity.GeneralObjectDumpV;
import com.viettel.ocs.entity.GeneralObjectV;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.util.CommonUtil;

public class GeneralObjectDAO extends BaseDAO<GeneralObject> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3737101942000396141L;

	@Override
	protected Class<GeneralObject> getEntityClass() {
		// TODO Auto-generated method stub
		return GeneralObject.class;
	}

	public List<GeneralObject> findByConditions(boolean findDump, String type, Long objId, String name, String desc) {
		
		String hql = "SELECT cat FROM GeneralObjectV cat ";
		if(findDump)
			hql = "SELECT cat FROM GeneralObjectDumpV cat ";
		hql += " WHERE domainId = :domainId AND type = :type";
		if(objId != null && objId > 0) {
			hql += " AND objId = :objId";
		}
		if(!CommonUtil.isEmpty(name)) {
			hql += " AND LOWER(name) LIKE LOWER(:name)";
		}
		if(!CommonUtil.isEmpty(desc)) {
			hql += " AND LOWER(description) LIKE LOWER(:description)";
		}
		
		Session session = HibernateUtil.getOpenSession();
		List<GeneralObject> lst;
		try {
			Query query;
			if(findDump) {
				query = session.createQuery(hql, GeneralObjectDumpV.class);
			} else {
				query = session.createQuery(hql, GeneralObjectV.class);
			}
			
			query.setParameter("domainId", super.getDomainId());
			query.setParameter("type", type);
			if(objId != null && objId > 0) {
				query.setParameter("objId", objId);
			}
			if(!CommonUtil.isEmpty(name)) {
				query.setParameter("name", "%" + name.trim() + "%");
			}
			if(!CommonUtil.isEmpty(desc)) {
				query.setParameter("description", "%" + desc.trim() + "%");
			}
			
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;		
	}
	
	public BaseEntity findActualObject(GeneralObject obj) {
		
		if(obj == null)
			return null;
		
		if("Offer".equals(obj.getType())) {
			return super.get(obj.getObjId(), Offer.class);
		} else if("Action".equals(obj.getType())) {
			return super.get(obj.getObjId(), Action.class);
		} else if("DR".equals(obj.getType())) {
			return super.get(obj.getObjId(), DynamicReserve.class);
		} else if("SPC".equals(obj.getType())) {
			return super.get(obj.getObjId(), SortPriceComponent.class);
		} else if("PC".equals(obj.getType())) {
			return super.get(obj.getObjId(), PriceComponent.class);
		} else if("Block".equals(obj.getType())) {
			return super.get(obj.getObjId(), Block.class);
		} else if("RateTable".equals(obj.getType())) {
			return super.get(obj.getObjId(), RateTable.class);
		} else if("DecisionTable".equals(obj.getType())) {
			return super.get(obj.getObjId(), DecisionTable.class);
		} else if("Normalizer".equals(obj.getType())) {
			return super.get(obj.getObjId(), Normalizer.class);
		} else if("Formula".equals(obj.getType())) {
			return super.get(obj.getObjId(), Formula.class);
		} else if("ColumnDt".equals(obj.getType())) {
			return super.get(obj.getObjId(), ColumnDt.class);
		} else if("NormValue".equals(obj.getType())) {
			return super.get(obj.getObjId(), NormValue.class);
		} else if("NormParam".equals(obj.getType())) {
			return super.get(obj.getObjId(), NormParam.class);
		}
		
		return null;
	}
	
	public BaseEntity deleteActualObject(GeneralObject obj) {

		BaseEntity actualObject = this.findActualObject(obj);
		if(actualObject == null)
			return actualObject;
		
		Session session = HibernateUtil.getOpenSession();
		try {
			session.beginTransaction();
			session.delete(actualObject);
			session.getTransaction().commit();
			return actualObject;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.UnitType;

public class BillingCycleTypeDAO extends BaseDAO<BillingCycleType> {

	public List<BillingCycleType> findBillingCycleTypeByConditions(Long categoryId) {
		List<BillingCycleType> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, "index");
		return lst;
	}

	public List<BillingCycleType> findBillingCycleTypeByCategoryId(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] val = { lstCatIDParam };
		String oder = "billingCycleTypeName";
		return this.findByConditions(col, ope, val, oder);
	}

	@Override
	protected Class<BillingCycleType> getEntityClass() {
		return BillingCycleType.class;
	}

	public boolean checkName(BillingCycleType billingCycleType, String billingCycleTypeName, boolean edit) {
		List<BillingCycleType> lst = null;
		String[] cols = { "billingCycleTypeName" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { billingCycleTypeName };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getBillingCycleTypeId() == billingCycleType.getBillingCycleTypeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public int countBillingCycleType() {
		String hql = "SELECT COUNT(a) FROM BillingCycleType a WHERE  a.domainId =:domainId ";
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
	
	public boolean saveBillingCycleType(BillingCycleType billingCycleType) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			billingCycleType.setDomainId(getDomainId());
			generateIndexByCat(billingCycleType, "billingCycleTypeId", session);
			session.saveOrUpdate(billingCycleType);
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

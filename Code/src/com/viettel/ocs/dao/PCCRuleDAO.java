package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.UnitType;

public class PCCRuleDAO extends BaseDAO<PccRule> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<PccRule> getEntityClass() {
		return PccRule.class;
	}

	public List<PccRule> findPccRuleByCategoryId(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] val = { lstCatIDParam };
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<PccRule> getPccRuleByCategoryId(Long categoryId) {
		List<PccRule> lst = null;
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { categoryId };
		lst = findByConditions(cols, operators, values, "index");
		return lst;
	}

	public List<PccRule> findPccRuleByPoliciProfile(Long policiProfileId) {
		List<PccRule> lst = null;
		String hql = "SELECT pr FROM PccRule pr JOIN ProfilePepPcc map ON map.pccRuleId = pr.pccRuleId ";
		hql += " WHERE map.profilePepId = :profilePepId";
		hql += " ORDER BY pr.index";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("profilePepId", policiProfileId);
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return lst;
	}

	public List<PccRule> loadPccRuleByPCCName(Long pccRuleId, String pccBaseName) {
		String[] col = { "pccRuleId", "pccBaseName" };
		Operator[] ope = { Operator.NOTEQ, Operator.EQ };
		Object[] val = { pccRuleId, pccBaseName };
		return this.findByConditions(col, ope, val, "");
	}

	public int countPccRule(Long pccRuleId, Long profilePepId) {
		String hql = "SELECT COUNT(pr) FROM ProfilePepPcc pr WHERE pr.pccRuleId =:pccRuleId AND pr.profilePepId !=:profilePepId  AND  pr.domainId =:domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("pccRuleId", pccRuleId);
			query.setParameter("profilePepId", profilePepId);
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countPCCRule() {
		String hql = "SELECT COUNT(a) FROM ProfilePepPcc a WHERE  a.domainId =:domainId ";
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

	public long getMaxIndex() {
		return getMaxIndex("index");
	}

	public boolean isDependency(Long pccRuleId) {
		String hql = "SELECT COUNT(a) FROM ProfilePepPcc a WHERE  a.domainId =:domainId AND a.pccRuleId = :pccRuleId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("pccRuleId", pccRuleId);
			return query.getSingleResult() > 0;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public void savePccRule(PccRule pccRule) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			pccRule.setDomainId(getDomainId());
			generateIndexByCat(pccRule, "pccRuleId", session);
			session.saveOrUpdate(pccRule);
			session.getTransaction().commit();
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	
}

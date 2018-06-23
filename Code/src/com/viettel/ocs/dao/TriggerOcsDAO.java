package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.ThresholdTriggerMap;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.TriggerType;

@SuppressWarnings("serial")
public class TriggerOcsDAO extends BaseDAO<TriggerOcs> implements Serializable {

	public List<TriggerOcs> findTOcsByCategoryId(Long categoryId) {

		List<TriggerOcs> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index");
		return lst;

	}

	public List<TriggerOcs> findTOcsByTriggerIds(String triggerIds) {

		List<TriggerOcs> lst = null;
		String[] cols = { "triggerIds", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { triggerIds, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		return lst;

	}

	public List<TriggerOcs> findTriggerOcsByListCatId(List<Long> lstCatId) {
		List<TriggerOcs> lstTriggerOcs = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		lstTriggerOcs = findByConditions(cols, operators, values, "");
		return lstTriggerOcs;
	}

	public List<TriggerOcs> findTriggerOcsByListTriggerId(List<Long> lstTriggerId) {
		List<TriggerOcs> lstTriggerOcs = null;
		String[] cols = { "triggerOcsId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstTriggerId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		lstTriggerOcs = findByConditions(cols, operators, values, "");
		return lstTriggerOcs;
	}

	public List<TriggerOcs> findTriggerOcsByThreshold(Long thresholdId) {

		List<TriggerOcs> lst = null;
		String hql = "SELECT rt FROM TriggerOcs rt JOIN ThresholdTriggerMap map ON map.triggerOcsId = rt.triggerOcsId ";
		hql += " WHERE map.thresholdId = :thresholdId";
		hql += " AND map.domainId = :domainId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("thresholdId", thresholdId);
			query.setParameter("domainId", getDomainId());
			lst = query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	@Override
	protected Class<TriggerOcs> getEntityClass() {
		return TriggerOcs.class;
	}

	public boolean checkName(TriggerOcs triggerOcs, String triggerOcsName, boolean edit) {
		List<TriggerOcs> lst = null;
		String[] cols = { "triggerName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { triggerOcsName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getTriggerOcsId() == triggerOcs.getTriggerOcsId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public boolean checkTOcsByType(long triggerType) {

		List<TriggerOcs> lst = null;
		String[] cols = { "triggerType", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { triggerType, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			return true;
		}
		return false;
	}

	// Check delete
	public boolean checkTrigger(TriggerOcs triggerOcs) {
		List<ThresholdTriggerMap> lstMap = null;
		// Check ThresholdTriggerMap
		String hqlThresholdTriggerMap = "SELECT p FROM ThresholdTriggerMap p";
		hqlThresholdTriggerMap += " WHERE p.triggerOcsId = :triggerOcsId ";
		hqlThresholdTriggerMap += " AND p.domainId = :domainId";

		Session session = HibernateUtil.getOpenSession();
		try {
			// Check ThresholdTriggerMap
			Query<ThresholdTriggerMap> queryThresholdTriggerMap = session.createQuery(hqlThresholdTriggerMap);
			queryThresholdTriggerMap.setParameter("triggerOcsId", triggerOcs.getTriggerOcsId());
			queryThresholdTriggerMap.setParameter("domainId", getDomainId());
			lstMap = queryThresholdTriggerMap.getResultList();
			if (lstMap.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return false;
	}

	public int countTriggerOcs() {
		String hql = "SELECT COUNT(a) FROM TriggerOcs a WHERE  a.domainId =:domainId ";
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

	public int checkTriggerDpdFomula(TriggerOcs triggerOcsUI) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(a) FROM Formula a");
		hql.append(" WHERE (a.triggerIds LIKE :triggerOcsIdFisrt");
		hql.append(" OR a.triggerIds LIKE :triggerOcsIdBetween");
		hql.append(" OR a.triggerIds LIKE :triggerOcsIdEnd");
		hql.append(" OR a.triggerIds LIKE :triggerOcsId)");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("triggerOcsIdFisrt", String.valueOf(triggerOcsUI.getTriggerOcsId() + ",%"));
			query.setParameter("triggerOcsIdBetween", String.valueOf("%," + triggerOcsUI.getTriggerOcsId() + ",%"));
			query.setParameter("triggerOcsIdEnd", String.valueOf("%," + triggerOcsUI.getTriggerOcsId()));
			query.setParameter("triggerOcsId", String.valueOf(triggerOcsUI.getTriggerOcsId()));

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

	public boolean saveTriggerOcs(TriggerOcs triggerOcsUI) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			triggerOcsUI.setDomainId(getDomainId());
			generateIndexByCat(triggerOcsUI, "triggerOcsId", session);
			session.saveOrUpdate(triggerOcsUI);
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

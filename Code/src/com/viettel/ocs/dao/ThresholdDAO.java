package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.ThresholdBaltypeMap;
import com.viettel.ocs.entity.ThresholdTriggerMap;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.model.ThresHoldTrigerModel;

@SuppressWarnings("serial")
public class ThresholdDAO extends BaseDAO<Threshold> implements Serializable {
	@Override
	protected Class<Threshold> getEntityClass() {
		return Threshold.class;
	}

	@SuppressWarnings("unchecked")
	public List<ThresHoldTrigerModel> findThresholdByBalType(Long balTypeId) {

		List<ThresHoldTrigerModel> lst = null;
		String hql = "SELECT new com.viettel.ocs.model.ThresHoldTrigerModel(th) FROM Threshold th JOIN ThresholdBaltypeMap map ON map.thresholdId = th.thresholdId ";
		hql += " WHERE map.balTypeId = :balTypeId";
		hql += " AND th.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<ThresHoldTrigerModel> query = session.createQuery(hql);
			query.setParameter("balTypeId", balTypeId);
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

	public boolean checkExisted(ThresHoldTrigerModel threshold) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(*) FROM Threshold a");
		hql.append(" WHERE (a.thresholdName LIKE :thresholdName");
		hql.append(" OR a.externalId LIKE :externalId)");
		hql.append(" AND a.thresholdId != :thresholdId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("thresholdName", threshold.getThreshold().getThresholdName());
			query.setParameter("externalId", threshold.getThreshold().getExternalId());
			query.setParameter("thresholdId", threshold.getThreshold().getThresholdId());
			query.setParameter("domainId", getDomainId());
			if (query.getSingleResult() > 0L) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

	}
	
	public boolean checkExistedName(ThresHoldTrigerModel threshold, Long balTypeId) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(*) FROM Threshold a JOIN ThresholdBaltypeMap b ON a.thresholdId = b.thresholdId");
		hql.append(" WHERE a.thresholdName LIKE :thresholdName");
		hql.append(" AND a.thresholdId != :thresholdId");
		hql.append(" AND b.balTypeId = :balTypeId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("thresholdName", threshold.getThreshold().getThresholdName());
			query.setParameter("thresholdId", threshold.getThreshold().getThresholdId());
			query.setParameter("balTypeId", balTypeId);
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
	
	public boolean checkExistedEx(ThresHoldTrigerModel threshold , Long balTypeId) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(*) FROM Threshold a JOIN ThresholdBaltypeMap b ON a.thresholdId = b.thresholdId");
		hql.append(" WHERE a.externalId LIKE :externalId");
		hql.append(" AND a.thresholdId != :thresholdId");
		hql.append(" AND b.balTypeId = :balTypeId");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("externalId", threshold.getThreshold().getExternalId());
			query.setParameter("thresholdId", threshold.getThreshold().getThresholdId());
			query.setParameter("balTypeId", balTypeId);
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

	public Threshold cloneThreshold(Threshold threshold, Session session, String suffix)
			throws CloneNotSupportedException {
		
		try {

			Threshold thresholdToClone = threshold.clone();

			// Find Trigger of Threshold
			List<TriggerOcs> lstTriggerOcs = this.findTByThreshold(thresholdToClone, session);

			// Save ThresHold
			thresholdToClone.setThresholdId(0L);
//			thresholdToClone.setThresholdName(
//					generateNameObject("thresholdName", thresholdToClone.getThresholdName() + suffix, 0, session));
//			thresholdToClone.setExternalId(
//					generateNameObject("externalId", thresholdToClone.getExternalId() + suffix, 0, session));
			thresholdToClone.setDomainId(getDomainId());
			session.save(thresholdToClone);

			for (TriggerOcs triggerOcs : lstTriggerOcs) {
				ThresholdTriggerMap thresholdTriggerMap = new ThresholdTriggerMap(thresholdToClone.getThresholdId(),
						triggerOcs.getTriggerOcsId(), getDomainId());
				session.save(thresholdTriggerMap);
			}

			return thresholdToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private List<TriggerOcs> findTByThreshold(Threshold threshold, Session session) {
		try {

			String hql = "SELECT a FROM TriggerOcs a JOIN ThresholdTriggerMap map ON map.triggerOcsId = a.triggerOcsId ";
			hql += " WHERE map.thresholdId = :thresholdId ";
			hql += " AND a.domainId = :domainId ";
			Query<TriggerOcs> query = session.createQuery(hql);
			query.setParameter("thresholdId", threshold.getThresholdId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<TriggerOcs> findTriggerOcsByTH(long thresholdId) {
		List<TriggerOcs> lst = null;
		String hql = "SELECT a FROM TriggerOcs a JOIN ThresholdTriggerMap map ON map.triggerOcsId = a.triggerOcsId ";
		hql += " WHERE map.thresholdId = :thresholdId ";
		hql += " AND a.domainId = :domainId ";
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

}

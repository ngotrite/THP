package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.ThresholdBaltypeMap;
import com.viettel.ocs.entity.ThresholdTriggerMap;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.model.ThresHoldTrigerModel;

@SuppressWarnings("serial")
public class BalTypeDAO extends BaseDAO<BalType> implements Serializable {

	public List<BalType> findBTByConditions(long categoryId) {

		List<BalType> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "index";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public List<BalType> findBalTypeByCatId(List<Long> lstCatId) {
		List<BalType> lstBalType = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };

		lstBalType = findByConditions(cols, operators, values, "");
		return lstBalType;
	}

	public List<BalType> findByCategory(Category category) {
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(category.getCategoryId());
		return this.findBalTypeByCatId(categoryIds);
	}

	@Override
	protected Class<BalType> getEntityClass() {
		return BalType.class;
	}

	public Long getMaxId(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.balTypeId), 0) FROM BalType a");
			hql.append(" WHERE a.domainId = :domainId");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Long getMaxIndex(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.index), 0) FROM BalType a");
			hql.append(" WHERE a.domainId = :domainId");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public boolean saveAll(BalType balType, List<ThresHoldTrigerModel> thresholds) {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			// SAVE BALTYPE
			balType.setDomainId(getDomainId());
			generateIndexByCat(balType, "balTypeId", session);

			session.saveOrUpdate(balType);

			List<Long> ids = new ArrayList<Long>();
			ids.add(-1L);

			for (ThresHoldTrigerModel thresHoldTrigerModel : thresholds) {
				long thresholdId = thresHoldTrigerModel.getThreshold().getThresholdId();
				if (thresholdId != 0L) {
					ids.add(thresHoldTrigerModel.getThreshold().getThresholdId());
				}
			}
			// DELETE THRESHOLD NOT IN LIST
			this.deleteThresholdNotInList(ids, balType, session);

			// SAVE OR UPDATE ThresHold MAP
			for (int i = 0; i < thresholds.size(); i++) {
				Threshold threshold = thresholds.get(i).getThreshold();
				List<TriggerOcs> triggerOcsOfThreshold = thresholds.get(i).getTriggerOcsofThresHold();

				if (threshold.getThresholdId() != 0L) {
					session.update(threshold);

					// DELETE TRIGGER OCS MAP NOT IN LIST
					ids.clear();
					ids.add(-1L);
					for (TriggerOcs triggerOcs : triggerOcsOfThreshold) {
						long triggerOcsId = triggerOcs.getTriggerOcsId();
						ids.add(triggerOcsId);
					}
					this.deleteTriggerNotInList(ids, threshold, session);

					// SAVE TRIGGER MAP
					for (TriggerOcs triggerOcs : triggerOcsOfThreshold) {
						ThresholdTriggerMap thresholdTriggerMap = this.findThresholdTriggerOcsMapByRelation(threshold,
								triggerOcs, session);
						if (thresholdTriggerMap.getThresholdTriggerMapId() == 0L) {
							session.save(thresholdTriggerMap);
						}
					}

				} else {// IF NEW
					threshold.setDomainId(getDomainId());
					session.save(threshold);

					// SAVE TRIGGER MAP
					for (TriggerOcs triggerOcs : triggerOcsOfThreshold) {
						session.save(new ThresholdTriggerMap(threshold.getThresholdId(), triggerOcs.getTriggerOcsId(),
								getDomainId()));
					}

					// CREATE RELATIONSHIP
					ThresholdBaltypeMap thresholdBaltypeMap = new ThresholdBaltypeMap(threshold.getThresholdId(),
							balType.getBalTypeId(), getDomainId());

					session.save(thresholdBaltypeMap);

				}
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

	public boolean deleteBalTypeAndThresHold(BalType balType) {
		if (balType != null) {
			Session session = HibernateUtil.getOpenSession();
			session.getTransaction().begin();
			try {
				deleteThresHoldTable(balType, session);
				session.delete(balType);
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
		return false;
	}

	public void deleteThresHoldTable(BalType balType, Session session) {
		try {
			StringBuffer hql = new StringBuffer(
					"DELETE FROM Threshold a WHERE a.thresholdId IN (SELECT b.thresholdId FROM ThresholdBaltypeMap b");
			hql.append(" JOIN BalType c ON c.balTypeId = b.balTypeId");
			hql.append(" WHERE a.domainId =:domainId");
			hql.append(" AND c.balTypeId =:balTypeId)");

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("balTypeId", balType.getBalTypeId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings({ "unchecked" })
	private ThresholdTriggerMap findThresholdTriggerOcsMapByRelation(Threshold threshold, TriggerOcs triggerOcs,
			Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM ThresholdTriggerMap a");
			hql.append(" WHERE a.thresholdId = :thresholdId");
			hql.append(" AND a.triggerOcsId = :triggerOcsId");
			hql.append(" AND a.domainId = :domainId");

			Query<ThresholdTriggerMap> query = session.createQuery(hql.toString());
			query.setParameter("triggerOcsId", triggerOcs.getTriggerOcsId());
			query.setParameter("thresholdId", threshold.getThresholdId());
			query.setParameter("domainId", getDomainId());
			List<ThresholdTriggerMap> thresholdTriggerMaps = query.getResultList();
			if (thresholdTriggerMaps != null && thresholdTriggerMaps.size() > 0) {
				return thresholdTriggerMaps.get(0);
			} else {
				return new ThresholdTriggerMap(threshold.getThresholdId(), triggerOcs.getTriggerOcsId(), getDomainId());
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	private void deleteThresholdNotInList(List<Long> ids, BalType balType, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a.thresholdId FROM ThresholdBaltypeMap a");
		hql.append(" WHERE a.thresholdId NOT IN (:thresholdIds)");
		hql.append(" AND a.balTypeId = :balTypeId");
		hql.append(" AND a.domainId = :domainId");
		try {
			Query<Long> getQuery = session.createQuery(hql.toString(), Long.class);
			getQuery.setParameterList("thresholdIds", ids);
			getQuery.setParameter("balTypeId", balType.getBalTypeId());
			getQuery.setParameter("domainId", getDomainId());
			List<Long> thresholdIds = getQuery.getResultList();
			if (thresholdIds.size() > 0) {
				hql = new StringBuffer("DELETE Threshold a WHERE a.thresholdId IN (:thresholdIds)");
				Query<?> deleteQuery = session.createQuery(hql.toString());
				deleteQuery.setParameterList("thresholdIds", thresholdIds);
				deleteQuery.executeUpdate();
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings({ "unchecked" })
	private void deleteTriggerNotInList(List<Long> ids, Threshold threshold, Session session) {
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM ThresholdTriggerMap a");
			hql.append(" WHERE a.thresholdId = :thresholdId");
			hql.append(" AND a.triggerOcsId NOT IN  (:triggerOcsIds)");
			hql.append(" AND a.domainId = :domainId");

			Query<ThresholdTriggerMap> query = session.createQuery(hql.toString());
			query.setParameterList("triggerOcsIds", ids);
			query.setParameter("thresholdId", threshold.getThresholdId());
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public boolean checkName(BalType balType, String balTypeName) {
		List<BalType> lst = null;
		String[] cols = { "balTypeName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { balTypeName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getBalTypeId() == balType.getBalTypeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public boolean checkUnitTypeInBT(Long unitTypeId) {
		List<BalType> lst = null;
		String[] cols = { "unitTypeId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { unitTypeId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean checkBillingCycleInBT(Long billingCycleTypeId) {
		List<BalType> lst = null;
		String[] cols = { "billingCycleTypeId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { billingCycleTypeId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public BalType findBalTypeLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM BalType a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.balTypeId DESC");

			Query<BalType> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setMaxResults(1);
			List<BalType> balTypes = query.getResultList();
			if (balTypes.size() > 0) {
				return balTypes.get(0);
			}
			return null;

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	private Long getNewId(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT a.balTypeId FROM BalType a");
			hql.append(" ORDER BY a.balTypeId DESC");

			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setMaxResults(1);
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	@SuppressWarnings("unchecked")
	public boolean checkId(BalType balType, boolean edit) {
		List<BalType> lst = null;
		String hql = "SELECT p FROM BalType p";
		hql += " WHERE p.balTypeId = :balTypeId";
		hql += " AND p.domainId = :domainId";

		Session session = HibernateUtil.getOpenSession();
		try {
			Query<BalType> query = session.createQuery(hql);
			query.setParameter("balTypeId", balType.getBalTypeId());
			query.setParameter("domainId", getDomainId());
			lst = query.getResultList();
			if (lst.size() > 0 && !edit) {
				return true;
			}
			return false;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public BalType cloneBalType(BalType balType, String suffix) throws CloneNotSupportedException {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			BalType balTypeCloned = this.cloneBalType(balType, session, suffix);
			session.getTransaction().commit();
			return balTypeCloned;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	private BalType cloneBalType(BalType balType, Session session, String suffix) throws CloneNotSupportedException {
		try {
			BalType balTypeToClone = balType.clone();
			// Find ThresHold of BalType
			List<Threshold> thresholds = this.findTHByBalType(balTypeToClone, session);

			// Save BalType
			balTypeToClone.setBalTypeId(this.getNewId(session) + 1L);
			balTypeToClone.setBalTypeName(
					generateNameObject("balTypeName", balTypeToClone.getBalTypeName() + suffix, 0, session));
			balTypeToClone.setDomainId(getDomainId());
			balTypeToClone.setIndex(this.getMaxIndex(session) + 1L);
			session.save(balTypeToClone);

			for (Threshold threshold : thresholds) {
				ThresholdBaltypeMap thresholdBaltypeMap = new ThresholdBaltypeMap(threshold.getThresholdId(),
						balTypeToClone.getBalTypeId(), getDomainId());

				ThresholdDAO thresholdDAO = new ThresholdDAO();
				thresholdBaltypeMap
						.setThresholdId(thresholdDAO.cloneThreshold(threshold, session, suffix).getThresholdId());

				session.save(thresholdBaltypeMap);
			}
			return balTypeToClone;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<Threshold> findTHByBalType(BalType balType, Session session) {
		try {
			String hql = "SELECT a FROM Threshold a JOIN ThresholdBaltypeMap map ON map.thresholdId = a.thresholdId ";
			hql += " WHERE map.balTypeId = :balTypeId ";
			hql += " AND a.domainId = :domainId ";
			Query<Threshold> query = session.createQuery(hql, Threshold.class);
			query.setParameter("balTypeId", balType.getBalTypeId());
			query.setParameter("domainId", getDomainId());
			return query.getResultList();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public List<Threshold> findThresholdByBalType(long balTypeId) {
		List<Threshold> lst = null;
		String hql = "SELECT a FROM Threshold a JOIN ThresholdBaltypeMap map ON map.thresholdId = a.thresholdId ";
		hql += " WHERE map.balTypeId = :balTypeId ";
		hql += " AND a.domainId = :domainId ";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Threshold> query = session.createQuery(hql, Threshold.class);
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

	public int countBalance() {
		String hql = "SELECT COUNT(a) FROM BalType a WHERE  a.domainId =:domainId AND a.isAcm =:isAcm";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("isAcm", false);
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

}

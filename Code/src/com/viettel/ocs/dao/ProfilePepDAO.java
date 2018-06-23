package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.ProfilePepPcc;
import com.viettel.ocs.entity.Qos;
import com.viettel.ocs.entity.RateTable;

public class ProfilePepDAO extends BaseDAO<ProfilePep> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<ProfilePep> getEntityClass() {

		return ProfilePep.class;
	}

	public List<ProfilePep> findProfilePepByCategoryId(List<Long> lstCatID) {

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

	public List<ProfilePep> findProfilePepByMonitorID(Long ID) {

		String[] col = { "monitorKey" };
		Operator[] ope = { Operator.EQ };
		;
		Object[] val = { ID };
		String order = "profilePepName";
		return this.findByConditions(col, ope, val, order);
	}

	public List<ProfilePep> getProfilePepByCategoryId(Long catID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { catID };
		String order = "index";
		return this.findByConditions(col, ope, val, order);
	}

	public boolean saveProfilePepDetail(ProfilePep profilePep, List<PccRule> listPccRule) {
		List<Long> pccRuleIds = new ArrayList<Long>();
		pccRuleIds.add(-1L);

		for (PccRule rule : listPccRule) {
			pccRuleIds.add(rule.getPccRuleId());
		}

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			// SAVE PROFILEPEP
			profilePep.setDomainId(getDomainId());

//			if (profilePep.getProfilePepId() == 0L) {
//				profilePep.setIndex(getMaxIndex("index") + 1);
//			}
			
			generateIndexByCat(profilePep, "profilePepId", session);

			session.saveOrUpdate(profilePep);

			// DELETE PROFILEPEP PCCRULE MAP
			this.deleteProfilePepPcc(pccRuleIds, profilePep, session);

			// SAVE OR UPDATE RATETABLE MAP
			for (int i = 0; i < listPccRule.size(); i++) {
				ProfilePepPcc profilePepPcc = this.findProfilePepPccByRelations(profilePep, listPccRule.get(i),
						session);
				profilePepPcc.setDomainId(getDomainId());
				session.saveOrUpdate(profilePepPcc);
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

	private void deleteProfilePepPcc(List<Long> listPccRuleIds, ProfilePep profilePep, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM ProfilePepPcc a");
		hql.append(" WHERE a.pccRuleId NOT IN (:pccRuleIds)");
		hql.append(" AND a.profilePepId = :profilePepId");
		hql.append(" AND a.domainId = :domainId");
		try {

			Query<ActionPriceComponentMap> query = session.createQuery(hql.toString());
			query.setParameterList("pccRuleIds", listPccRuleIds);
			query.setParameter("profilePepId", profilePep.getProfilePepId());
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	private ProfilePepPcc findProfilePepPccByRelations(ProfilePep profilePep, PccRule pccRule, Session session) {
		StringBuffer hql = new StringBuffer("SELECT a FROM ProfilePepPcc a");
		hql.append(" WHERE a.pccRuleId = :pccRuleId");
		hql.append(" AND a.profilePepId = :profilePepId");
		hql.append(" AND a.domainId = :domainId");
		try {

			Query<ProfilePepPcc> query = session.createQuery(hql.toString());
			query.setParameter("profilePepId", profilePep.getProfilePepId());
			query.setParameter("pccRuleId", pccRule.getPccRuleId());
			query.setParameter("domainId", getDomainId());

			List<ProfilePepPcc> ProfilePepPccs = query.getResultList();

			if (ProfilePepPccs != null && ProfilePepPccs.size() > 0) {
				return ProfilePepPccs.get(0);
			}
			ProfilePepPcc profilePepPcc = new ProfilePepPcc(pccRule.getPccRuleId(), profilePep.getProfilePepId());
			return profilePepPcc;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}
	}

	public List<ProfilePep> loadProfilePepByPPName(Long ppID, String ppName) {
		String[] col = { "profilePepId", "profilePepName" };
		Operator[] ope = { Operator.NOTEQ, Operator.EQ };
		Object[] val = { ppID, ppName };
		String oder = "profilePepName";
		return this.findByConditions(col, ope, val, oder);
	}

	public int countProfilePep(Long qosId) {
		String hql = "SELECT COUNT(a) FROM ProfilePep a WHERE a.domainId =:domainId";
		hql += " AND qosId =:qosId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("qosId", qosId);
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int countProfilePep() {
		String hql = "SELECT COUNT(a) FROM ProfilePep a WHERE  a.domainId =:domainId ";
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
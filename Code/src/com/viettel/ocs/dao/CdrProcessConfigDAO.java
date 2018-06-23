package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.CdrProcessConfig;

public class CdrProcessConfigDAO extends BaseDAO<CdrProcessConfig> {
	public CdrProcessConfigDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<CdrProcessConfig> findCdrProcessConfigByConditions(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	/**
	 * load List ReserveInfo by domain
	 */
	public List<CdrProcessConfig> loadListCdrProcessConfig(Long categoryId) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryId };
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	public Boolean checkFieldIsExist(String col, String value, CdrProcessConfig CdrProcessConfig) {
		boolean result = false;
		int count = 0;
		String[] column = { col, "cdrProcessConfigId" };
		Operator[] ope = { Operator.EQ, Operator.NOTEQ };
		Object[] val = { value, CdrProcessConfig.getCdrProcessConfigId() };
		count = this.countByConditions(column, ope, val);
		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Boolean deleteCdrProcessConfig(CdrProcessConfig item) {
		if (!checkInUsedCdrProcessConfig(item.getCdrProcessConfigId())) {
			this.delete(item);
			return true;
		}

		return false;
	}

	public Boolean checkInUsedCdrProcessConfig(Long cpID) {
		boolean result = false;
		// List<CdrProcessConfig> csLst = new ArrayList<>();
		// Session session = HibernateUtil.getOpenSession();
		// try {
		// String hql = " FROM CdrProcessConfigProp cps WHERE
		// cps.CdrProcessConfigPropId = :CdrProcessConfigId AND
		// cps.domainId=:domainId";
		// Query query = session.createQuery(hql);
		// query.setParameter("CdrProcessConfigId", cpID);
		// query.setParameter("domainId", getDomainId());
		// csLst = query.getResultList();
		//
		// if(csLst.size() > 0){

		// result = true;

		// }

		// } catch (Exception e) {
		// 
		// } finally {
		// session.close();
		// }
		return result;
	}

	@Override
	protected Class<CdrProcessConfig> getEntityClass() {
		return CdrProcessConfig.class;
	}

	public int countCdrProcessConfig() {
		String hql = "SELECT COUNT(a) FROM CdrProcessConfig a WHERE  a.domainId =:domainId ";
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

	public CdrProcessConfig cloneCdrProcessConfig(CdrProcessConfig object) {
		CdrProcessConfig cdrProcessConfig = object.clone();
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			cdrProcessConfig.setCdrProcessConfigId(0L);
			cdrProcessConfig.setProcessName(
					this.generateNameObject("processName", cdrProcessConfig.getProcessName() + "_clone", 0, session));
			cdrProcessConfig.setIndex(getMaxIndex() + 1L);
			session.save(cdrProcessConfig);
			session.getTransaction().commit();
			return cdrProcessConfig;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean saveCdrProcess(CdrProcessConfig cdrProcessConfig) {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			cdrProcessConfig.setDomainId(getDomainId());
			generateIndexByCat(cdrProcessConfig, "cdrProcessConfigId", session);
			session.saveOrUpdate(cdrProcessConfig);
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

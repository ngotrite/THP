package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.NomalizerNormParamMap;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.ReserveInfo;

public class CdrServiceDAO extends BaseDAO<CdrService> {
	public CdrServiceDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<CdrService> findCdrServiceByConditions(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	/**
	 * load List Category by categoryType
	 */
	public List<Category> loadListCategory() {
		CategoryDAO categoryDAO = new CategoryDAO();
		return categoryDAO.loadListCategoryByType(CategoryType.CTL_CDR_SERVICE);
	}

	/**
	 * load List ReserveInfo by domain
	 */
	public List<CdrService> loadListCdrService(Long categoryId) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryId };
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	public Boolean checkFieldIsExist(String col, String value, CdrService cdrService) {
		boolean result = false;

		int count = 0;

		if (cdrService == null || cdrService.getCdrServiceId() == null || cdrService.getCdrServiceId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "cdrServiceId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, cdrService.getCdrServiceId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Boolean checkVisibleService() {
		boolean result = false;
		Session session = HibernateUtil.getOpenSession();
		List lst = null;
		try {

			// Criteria cri = session.createCriteria(clazz);
			String queryString = "SELECT COUNT(obj) FROM CdrService obj WHERE domainId = :domainId ";

			Query query = session.createQuery(queryString);
			query.setParameter("domainId", getDomainId());

			lst = query.getResultList();
			if (lst.size() > 0)
				result = true;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return result;
	}

	public List<CdrService> loadAllListReserveInfo(Long categoryId) {
		return this.findAll("");
	}

	public Boolean deleteCdrService(CdrService item) {
		if (!checkInUsedCdrService(item.getCdrServiceId())) {
			this.delete(item);
			return true;
		}

		return false;
	}

	public Boolean checkInUsedCdrService(Long csID) {
		boolean result = false;
		List<CdrService> csLst = new ArrayList<>();
		Session session = HibernateUtil.getOpenSession();
		try {
			String hql = " FROM CdrProcessConfig cps WHERE cps.cdrServiceId = :cdrServiceId AND cps.domainId=:domainId";
			Query query = session.createQuery(hql);
			query.setParameter("cdrServiceId", csID);
			query.setParameter("domainId", getDomainId());
			csLst = query.getResultList();

			if (csLst.size() > 0) {

				result = true;

			} else {

				hql = " FROM CdrTemplate ct WHERE ct.cdrServiceId = :cdrServiceId AND ct.domainId=:domainId";
				query = session.createQuery(hql);
				query.setParameter("cdrServiceId", csID);
				query.setParameter("domainId", getDomainId());
				csLst = query.getResultList();

				if (csLst.size() > 0) {
					result = true;
				}

			}

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return result;
	}

	@Override
	protected Class<CdrService> getEntityClass() {
		return CdrService.class;
	}

	public long getMaxIndex() {
		return getMaxIndex("index");
	}

	public boolean saveCdrService(CdrService cdrService) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			cdrService.setDomainId(getDomainId());
			generateIndexByCat(cdrService, "cdrServiceId", session);
			session.saveOrUpdate(cdrService);
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
	
	public int countCdrService() {
		String hql = "SELECT COUNT(a) FROM CdrService a WHERE  a.domainId =:domainId";
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

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
import com.viettel.ocs.entity.CdrGenFilename;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.Parameter;

public class CdrGenFilenameDAO extends BaseDAO<CdrGenFilename> {
	public CdrGenFilenameDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<CdrGenFilename> findCdrGenFilenameByConditions(List<Long> lstCatID) {

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
		return categoryDAO.loadListCategoryByType(CategoryType.CTL_CDR_GEN_FILENAME);
	}

	/**
	 * load List CDR Genfile Name by domain
	 */
	public List<CdrGenFilename> loadListCdrGenFileName(Long categoryId) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryId };
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	public Boolean checkFieldIsExist(String col, String value, CdrGenFilename CdrGenFilename) {
		boolean result = false;

		int count = 0;

		if (CdrGenFilename == null || CdrGenFilename.getCdrGenFilenameId() == 0L) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "cdrGenFilenameId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, CdrGenFilename.getCdrGenFilenameId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Boolean deleteCdrGenFilename(CdrGenFilename item) {
		if (!checkInUsedCdrGenFilename(item.getCdrGenFilenameId())) {
			this.delete(item);
			return true;
		}

		return false;
	}

	public Boolean checkInUsedCdrGenFilename(Long cpID) {
		boolean result = false;
		List<CdrGenFilename> csLst = new ArrayList<>();
		Session session = HibernateUtil.getOpenSession();
		try {
			String hql = " FROM CdrProcessConfig cps WHERE cps.filenamePatternId = :cdrGenFilenameId AND cps.domainId=:domainId";
			Query query = session.createQuery(hql);
			query.setParameter("cdrGenFilenameId", cpID);
			query.setParameter("domainId", getDomainId());
			csLst = query.getResultList();

			if (csLst.size() > 0) {

				result = true;

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
	protected Class<CdrGenFilename> getEntityClass() {
		return CdrGenFilename.class;
	}

	public long getMaxIndex() {
		return getMaxIndex("index");
	}

	public CdrGenFilename cloneCdrGenFileName(CdrGenFilename object) {
		CdrGenFilename cdrGenFilename = object.clone();
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			cdrGenFilename.setCdrGenFilenameId(0L);
			cdrGenFilename.setName(this.generateNameObject("name", cdrGenFilename.getName() + "_clone", 0, session));
			cdrGenFilename.setIndex(getMaxIndex() + 1L);
			session.save(cdrGenFilename);
			session.getTransaction().commit();
			return cdrGenFilename;
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean saveGenFileName(CdrGenFilename cdrGenFileName) {
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			cdrGenFileName.setDomainId(getDomainId());
			generateIndexByCat(cdrGenFileName, "cdrGenFilenameId", session);
			session.saveOrUpdate(cdrGenFileName);
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
	
	public int countCdrGenFilename() {
		String hql = "SELECT COUNT(a) FROM CdrGenFilename a WHERE  a.domainId =:domainId";
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

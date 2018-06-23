package com.viettel.ocs.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.CdrTemplateProp;

public class CdrTemplateDAO extends BaseDAO<CdrTemplate> {
	public CdrTemplateDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<CdrTemplate> findCdrTemplateByConditions(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		return this.findByConditions(col, ope, val, "index");
	}

	/**
	 * load List Category by categoryType
	 */
	public List<Category> loadListCategory() {
		CategoryDAO categoryDAO = new CategoryDAO();
		return categoryDAO.loadListCategoryByType(CategoryType.CTL_CDR_TEMPLATE);
	}

	/**
	 * load List Cdr Template by domain
	 */
	public List<CdrTemplate> loadListCdrTemplate(Long categoryId) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryId };
		String oder = "index";
		return this.findByConditions(col, ope, val, oder);
	}

	public Boolean checkFieldIsExist(String value, CdrTemplate CdrTemplate) {
		boolean result = true;
		int count = 0;
		if (CdrTemplate != null) {
			String[] column = { "templateCode", "cdrTemplateId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, CdrTemplate.getCdrTemplateId() };
			count = this.countByConditions(column, ope, val);
		}
		if (count > 0) {
			result = false;
		}
		return result;
	}

	public Boolean deleteCdrTemplate(CdrTemplate item) {
		if (!checkInUsedCdrTemplate(item.getCdrTemplateId())) {
			this.delete(item);
			return true;
		}

		return false;
	}

	public Boolean checkInUsedCdrTemplate(Long cpID) {
		boolean result = false;
		Session session = HibernateUtil.getOpenSession();
		try {
			String hql = "SELECT COUNT(cps) FROM CdrGenFilename cps WHERE cps.templateId = :cdrTemplateId AND cps.domainId=:domainId";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("cdrTemplateId", cpID);
			query.setParameter("domainId", getDomainId());
			if (query.getSingleResult() > 0L) {
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
	protected Class<CdrTemplate> getEntityClass() {
		return CdrTemplate.class;
	}

	public int countCdrTemplate(CdrTemplate cdrTemplate) {
		String hql = "SELECT COUNT(a) FROM CdrTemplate a WHERE  a.domainId =:domainId AND a.templateCode LIKE :templateCode And cdrTemplateId !=:cdrTemplateId ";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("domainId", getDomainId());
			query.setParameter("templateCode", cdrTemplate.getTemplateCode());
			query.setParameter("cdrTemplateId", cdrTemplate.getCdrTemplateId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean saveCdrTemplateDetail(CdrTemplate cdrTemplate, List<CdrTemplateProp> listCdrTemplateProp) {
		List<Long> cdrTemplatePropIds = new ArrayList<Long>();
		cdrTemplatePropIds.add(-1L);

		for (CdrTemplateProp rule : listCdrTemplateProp) {
			if (rule.getCdrTemplatePropId() != null) {
				cdrTemplatePropIds.add(rule.getCdrTemplatePropId());
			}
		}

		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {

			// SAVE TEMPLATE
			cdrTemplate.setDomainId(getDomainId());
			generateIndexByCat(cdrTemplate, "cdrTemplateId", session);
			session.saveOrUpdate(cdrTemplate);

			// DELETE MAP
			this.deleteCdrTemplatePropMap(cdrTemplatePropIds, cdrTemplate, session);

			// SAVE OR UPDATE RATETABLE MAP
			if (listCdrTemplateProp != null) {
				for (int i = 0; i < listCdrTemplateProp.size(); i++) {
					listCdrTemplateProp.get(i).setDomainId(getDomainId());
					listCdrTemplateProp.get(i).setCdrTemplateId(cdrTemplate.getCdrTemplateId());
					listCdrTemplateProp.get(i).setOrderProp(Long.valueOf(i));
					session.saveOrUpdate(listCdrTemplateProp.get(i));
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

	private void deleteCdrTemplatePropMap(List<Long> cdrTemplatePropIds, CdrTemplate cdrTemplate, Session session) {
		StringBuffer hql = new StringBuffer("DELETE FROM CdrTemplateProp a");
		hql.append(" WHERE a.cdrTemplatePropId NOT IN (:cdrTemplatePropId)");
		hql.append(" AND a.cdrTemplateId = :cdrTemplateId");
//		hql.append(" AND a.domainId = :domainId");
		try {
			Query<?> query = session.createQuery(hql.toString());
			query.setParameterList("cdrTemplatePropId", cdrTemplatePropIds);
			query.setParameter("cdrTemplateId", cdrTemplate.getCdrTemplateId());
//			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public Long getMaxOrderProp(Session session) {
		try {
			StringBuffer hql = new StringBuffer("SELECT COALESCE(MAX(a.orderProp), 0) FROM CdrTemplateProp a");
			hql.append(" WHERE a.domainId = :domainId");
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("domainId", getDomainId());
			return query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		}		
	}

	public long getMaxIndex() {
		return getMaxIndex("index");
	}
	
	public int countCdrTemplate() {
		String hql = "SELECT COUNT(a) FROM CdrTemplate a WHERE  a.domainId =:domainId";
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

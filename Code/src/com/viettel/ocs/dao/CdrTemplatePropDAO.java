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
import com.viettel.ocs.entity.CdrProp;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.CdrTemplateProp;

public class CdrTemplatePropDAO extends BaseDAO<CdrTemplateProp> {
	public CdrTemplatePropDAO() {
		domainId = SessionUtils.getDomainId();
	}

	private long domainId;

	public long getDomainId() {
		return domainId;
	}

	/**
	 * categoryId domainId
	 */
	public List<CdrTemplateProp> findCdrServiceByConditions(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "name";
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
	public List<CdrTemplateProp> loadListCdrService(Long categoryId) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { categoryId };
		String oder = "name";
		return this.findByConditions(col, ope, val, oder);
	}

	public Boolean checkFieldIsExist(String col, String value, CdrTemplateProp CdrTemplateProp) {
		boolean result = false;

		int count = 0;

		if (CdrTemplateProp == null || CdrTemplateProp.getCdrTemplatePropId() == null
				|| CdrTemplateProp.getCdrTemplatePropId() == 0) {
			String[] column = { col };
			Operator[] ope = { Operator.EQ };
			Object[] val = { value };
			count = this.countByConditions(column, ope, val);
		} else {
			String[] column = { col, "CdrTemplatePropId" };
			Operator[] ope = { Operator.EQ, Operator.NOTEQ };
			Object[] val = { value, CdrTemplateProp.getCdrTemplatePropId() };
			count = this.countByConditions(column, ope, val);
		}

		if (count > 0) {
			result = true;
		}

		return result;
	}

	public Boolean deleteCdrTemplateProp(CdrTemplateProp item) {
		if (!checkInUsedCdrTemplateProp(item.getCdrTemplatePropId())) {
			this.delete(item);
			return true;
		}

		return false;
	}

	public Boolean checkInUsedCdrTemplateProp(Long cpID) {
		boolean result = false;
		List<CdrTemplateProp> csLst = new ArrayList<>();
		Session session = HibernateUtil.getOpenSession();
		try {
			String hql = " FROM CdrTemplatePropProp cps WHERE cps.CdrTemplatePropPropId = :CdrTemplatePropId AND cps.domainId=:domainId";
			Query query = session.createQuery(hql);
			query.setParameter("CdrTemplatePropId", cpID);
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

	// get new order
	public Long findNewIndexByTemplateId(Long templateId) {
		Session session = HibernateUtil.getOpenSession();
		try {
			StringBuffer hql = new StringBuffer("SELECT a FROM CdrTemplateProp a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" AND a.cdrTemplateId = :cdrTemplateId");
			hql.append(" ORDER BY a.orderProp DESC");

			Query<CdrTemplateProp> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameter("cdrTemplateId", templateId);

			List<CdrTemplateProp> cdrTemplateProps = query.getResultList();
			if (cdrTemplateProps != null && cdrTemplateProps.size() > 0) {
				return (cdrTemplateProps.get(0).getOrderProp() + 1);
			}
			return 1l;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	// save change move updown
	public Boolean saveChangeMoveUpDown(CdrTemplateProp item1, CdrTemplateProp item2) {
		boolean result = true;
		Session session = HibernateUtil.getOpenSession();
		try {
			session.getTransaction().begin();
			session.saveOrUpdate(item1);
			session.saveOrUpdate(item2);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			result = false;
		} finally {
			session.close();
		}
		return result;
	}

	// find all by templateId
	public List<CdrTemplateProp> findByTemplateID(Long templateId) {
		List<CdrTemplateProp> lst = null;
		String[] col = { "cdrTemplateId" };
		Operator[] ope = { Operator.EQ };
		Object[] val = { templateId };
		String oder = "orderProp ASC";
		lst = this.findByConditions(col, ope, val, oder);
		if (lst != null) {
			CdrPropDAO propDAO = new CdrPropDAO();
			for (int i = 0; i < lst.size(); i++) {
				lst.get(i).setEditRow(false);
				if (lst.get(i) != null) {
					CdrProp cdrProp = propDAO.get(lst.get(i).getCdrPropId());
					if (cdrProp != null) {
						lst.get(i).setPropName(cdrProp.getPropName());
					}
				}
			}
		}
		return lst;
	}

	@Override
	protected Class<CdrTemplateProp> getEntityClass() {
		return CdrTemplateProp.class;
	}

}

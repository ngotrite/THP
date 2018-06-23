package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.CssUssdResponse;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.UnitType;

@SuppressWarnings("serial")
public class CssUssdResponseDAO extends BaseDAO<CssUssdResponse> implements Serializable {
	@Override
	protected Class<CssUssdResponse> getEntityClass() {
		// TODO Auto-generated method stub
		return CssUssdResponse.class;
	}

	public List<CssUssdResponse> findByListCat(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "posIndex";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<CssUssdResponse> findCSSByCategoryId(long categoryId) {
		List<CssUssdResponse> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "posIndex";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public boolean saveCssUssdResponse(CssUssdResponse cssUssdResponse) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			cssUssdResponse.setDomainId(getDomainId());
			generateIndexByCat(cssUssdResponse, "posIndex", "cssUssdResponseId", session);
			session.saveOrUpdate(cssUssdResponse);
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

	public boolean checkName(CssUssdResponse cssUssdResponse, String name) {
		List<CssUssdResponse> lst = null;
		String[] cols = { "name", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { name, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getCssUssdResponseId() == cssUssdResponse.getCssUssdResponseId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public int countCssUssdResponse() {
		String hql = "SELECT COUNT(a) FROM CssUssdResponse a WHERE  a.domainId =:domainId";
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

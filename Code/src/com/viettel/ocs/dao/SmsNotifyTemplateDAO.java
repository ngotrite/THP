package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.SmsNotifyTemplate;
import com.viettel.ocs.entity.StatisticItem;

@SuppressWarnings("serial")
public class SmsNotifyTemplateDAO extends BaseDAO<SmsNotifyTemplate> implements Serializable{
	@Override
	protected Class<SmsNotifyTemplate> getEntityClass() {
		// TODO Auto-generated method stub
		return SmsNotifyTemplate.class;
	}

	public List<SmsNotifyTemplate> findByListCat(List<Long> lstCatID) {
		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "posIndex";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<SmsNotifyTemplate> findSMSByCategoryId(long categoryId) {
		List<SmsNotifyTemplate> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "posIndex";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public boolean saveSMS(SmsNotifyTemplate smsNotiTem) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			smsNotiTem.setDomainId(getDomainId());
			generateIndexByCat(smsNotiTem, "posIndex" , "smsNotifyTemplateId", session);
			session.saveOrUpdate(smsNotiTem);
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

	public boolean checkName(SmsNotifyTemplate smsNotiTem, String name) {
		List<SmsNotifyTemplate> lst = null;
		String[] cols = { "name", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { name, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getSmsNotifyTemplateId() == smsNotiTem.getSmsNotifyTemplateId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public int countSmsNotifyTemplate() {
		String hql = "SELECT COUNT(a) FROM SmsNotifyTemplate a WHERE  a.domainId =:domainId";
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

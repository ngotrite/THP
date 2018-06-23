package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.entity.TriggerMsg;
import com.viettel.ocs.entity.TriggerType;

public class TriggerMsgDAO extends BaseDAO<TriggerMsg> implements Serializable {

	public List<TriggerMsg> findTMByCategoryId(Long categoryId) {

		List<TriggerMsg> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index");
		return lst;

	}

	@Override
	protected Class<TriggerMsg> getEntityClass() {
		return TriggerMsg.class;
	}

	public boolean checkName(TriggerMsg triggerMsg, String triggerMsgName, boolean edit) {
		List<TriggerMsg> lst = null;
		String[] cols = { "msgName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { triggerMsgName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getMsgId() == triggerMsg.getMsgId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public int countTriggerMsg() {
		String hql = "SELECT COUNT(a) FROM TriggerMsg a WHERE  a.domainId =:domainId ";
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

	public Long getMaxIndex() {
		return getMaxIndex("index");
	}

	public boolean saveTriggerMsg(TriggerMsg triggerMsgUI) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			triggerMsgUI.setDomainId(getDomainId());
			generateIndexByCat(triggerMsgUI, "msgId", session);
			session.saveOrUpdate(triggerMsgUI);
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

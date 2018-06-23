package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.entity.TriggerType;

@SuppressWarnings("serial")
public class TriggerTypeDAO extends BaseDAO<TriggerType> implements Serializable {

	public List<TriggerType> findListTriggerTypeByCategoryId(Long categoryId) {
		List<TriggerType> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index");
		return lst;
	}

	@Override
	protected Class<TriggerType> getEntityClass() {
		return TriggerType.class;
	}

	public boolean checkName(TriggerType triggerType, String triggerTypeName, boolean edit) {
		List<TriggerType> lst = null;
		String[] cols = { "triggerTypeName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { triggerTypeName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getTriggerType() == triggerType.getTriggerType()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public Long getMaxIndex() {
		return getMaxIndex("index");
	}

	public boolean saveTriggerType(TriggerType triggerTypeUI) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			triggerTypeUI.setDomainId(getDomainId());
			generateIndexByCat(triggerTypeUI, "triggerType", session);
			session.saveOrUpdate(triggerTypeUI);
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

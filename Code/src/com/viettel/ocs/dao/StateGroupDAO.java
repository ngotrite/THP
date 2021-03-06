package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.StateGroup;

public class StateGroupDAO extends BaseDAO<StateGroup> {

	public List<StateGroup> findParamByConditions(Long categoryId) {
		List<StateGroup> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index");
		return lst;
	}

	@Override
	protected Class<StateGroup> getEntityClass() {
		return StateGroup.class;
	}

	public boolean checkName(StateGroup stateGroup, String stateGroupName, boolean edit) {
		List<StateGroup> lst = null;
		String[] cols = { "stateGroupName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { stateGroupName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getStateGroupId() == stateGroup.getStateGroupId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public int countStateGroup() {
		String hql = "SELECT COUNT(a) FROM StateGroup a WHERE  a.domainId =:domainId ";
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
}

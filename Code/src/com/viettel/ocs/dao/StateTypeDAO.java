package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.StateType;

public class StateTypeDAO extends BaseDAO<StateType> {

	public List<StateType> findListStateTypeByIndex() {
		List<StateType> lst = null;
		String[] cols = { "domainId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { getDomainId() };
		lst = findByConditions(cols, operators, values, "index ASC");
		return lst;
	}

	public List<StateType> findStateTypeByConditions(Long categoryId) {
		List<StateType> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		lst = findByConditions(cols, operators, values, "index ASC");
		return lst;
	}

	public List<StateType> findStateTypeBySG(Long stateGroup) {

		List<StateType> lst = null;
		String hql = " FROM StateType st JOIN StateGroup sg ON sg.stateGroupId = st.stateGroupId ";
		hql += " WHERE sg.stateGroupId = :stateGroup";
		hql += " AND sg.domainId = :domainId";

		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createQuery(hql);
			query.setParameter("stateGroup", stateGroup);
			query.setParameter("domainId", getDomainId());
			lst = query.list();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}

		return lst;
	}

	@Override
	protected Class<StateType> getEntityClass() {
		return StateType.class;
	}

	public boolean checkName(StateType stateType, String stateTypeName, boolean edit) {
		List<StateType> lst = null;
		String[] cols = { "stateName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { stateTypeName, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (edit && lst.get(0).getStateTypeId() == stateType.getStateTypeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}

	public boolean checkId(StateType stateType, boolean edit) {
		List<StateType> lst = null;
		String hql = "SELECT p FROM StateType p";
		hql += " WHERE p.stateTypeId = :stateTypeId";
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<StateType> query = session.createQuery(hql);
			query.setParameter("stateTypeId", stateType.getStateTypeId());
			lst = query.getResultList();
			if (lst.size() > 0 && !edit) {
				return true;
			}
			return false;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean checkTypeByGroup(long stateGroupId) {

		List<StateType> lst = null;
		String[] cols = { "stateGroupId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { stateGroupId };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			return true;
		}
		return false;
	}

	public StateType findStateTypeLastID() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM StateType a");
			hql.append(" ORDER BY a.stateTypeId DESC");
			Query<StateType> query = session.createQuery(hql.toString());
			query.setMaxResults(1);
			List<StateType> stateTypes = query.getResultList();
			if (stateTypes.size() > 0) {
				return stateTypes.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public StateType findStateTypeLastIndex() {
		Session session = HibernateUtil.getOpenSession();
		try {

			StringBuffer hql = new StringBuffer("SELECT a FROM StateType a");
			hql.append(" WHERE a.domainId = :domainId");
			hql.append(" ORDER BY a.index DESC");

			Query<StateType> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setMaxResults(1);
			List<StateType> states = query.getResultList();
			if (states.size() > 0) {
				return states.get(0);
			}
			return null;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public Boolean processMoveUpDown(StateType moveItem, StateType item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			Long tmp = moveItem.getIndex();
			moveItem.setIndex(item.getIndex());
			item.setIndex(tmp);
			session.saveOrUpdate(moveItem);
			session.saveOrUpdate(item);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return true;
	}
	
	public int countStateType() {
		String hql = "SELECT COUNT(a) FROM StateType a WHERE  a.domainId =:domainId ";
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

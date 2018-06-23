package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.PriceComponentBlockMap;

public class ActionPriceComponentMapDAO extends BaseDAO<ActionPriceComponentMap> {

	@Override
	protected Class<ActionPriceComponentMap> getEntityClass() {
		return ActionPriceComponentMap.class;
	}

	public boolean checkPCInAction(Long priceComponentId) {
		List<ActionPriceComponentMap> lst = null;
		String[] cols = { "priceComponentId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { priceComponentId };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public List<ActionPriceComponentMap> findActionPriceComponentMapByAction(Long actionId) {
		List<ActionPriceComponentMap> lst = null;
		String[] cols = { "actionId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { actionId };
		String oder = " pcIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public void saveMapActionPC(ActionPriceComponentMap itemUp, ActionPriceComponentMap item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			long indexUp = itemUp.getPcIndex();
			itemUp.setPcIndex(item.getPcIndex());
			session.saveOrUpdate(itemUp);

			item.setPcIndex(indexUp);
			session.saveOrUpdate(item);

			session.getTransaction().commit();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public ActionPriceComponentMap findActionPriceComponentMap(Long actionId, Long priceComponentId) {
		List<ActionPriceComponentMap> lst = null;
		String[] cols = { "actionId", "priceComponentId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { actionId, priceComponentId };
		String oder = " pcIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (!lst.isEmpty()) {
			return lst.get(0);
		}
		return null;
	}

	public boolean deleteActionPCByAction(Action action) {
		String hql = "DELETE FROM ActionPriceComponentMap map";
		hql += " WHERE map.actionId = :actionId";
		hql += " AND map.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(action);
			Query<PriceComponentBlockMap> query = session.createQuery(hql);
			query.setParameter("actionId", action.getActionId());
			query.setParameter("domainId", getDomainId());
			query.executeUpdate();
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

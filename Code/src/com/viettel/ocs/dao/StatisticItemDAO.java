package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.ThresholdTriggerMap;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;

@SuppressWarnings("serial")
public class StatisticItemDAO extends BaseDAO<StatisticItem> implements Serializable{
	@Override
	protected Class<StatisticItem> getEntityClass() {
		// TODO Auto-generated method stub
		return StatisticItem.class;
	}
	
	public List<StatisticItem> findByListCat(List<Long> lstCatID) {

		String[] col = { "categoryId" };
		Operator[] ope = { Operator.IN };
		Object[] val = { lstCatID };
		if (lstCatID == null || lstCatID.size() == 0)
			lstCatID.add(-1L);
		String oder = "pos_Idx";
		return this.findByConditions(col, ope, val, oder);
	}

	public List<StatisticItem> findSIByCategoryId(long categoryId) {
		List<StatisticItem> lst = null;
		String[] cols = { "categoryId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { categoryId, getDomainId() };
		String oder = "pos_Idx";
		lst = findByConditions(cols, operators, values, oder);
		return lst;

	}

	public boolean saveStatistic(StatisticItem statisticItem) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			statisticItem.setDomainId(getDomainId());
			generateIndexByCat(statisticItem, "pos_Idx" , "statisticItemId", session);
			session.saveOrUpdate(statisticItem);
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
	
	public boolean checkName(StatisticItem statisticItem, String name) {
		List<StatisticItem> lst = null;
		String[] cols = { "statisticItemName", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { name, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getStatisticItemId() == statisticItem.getStatisticItemId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public List<StatisticItem> findSIByListStatisticItemId(List<Long> lstStatisticItemId) {
		List<StatisticItem> lstStatisticItem = null;
		String[] cols = { "statisticItemId", "domainId" };
		Operator[] operators = { Operator.IN, Operator.EQ };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstStatisticItemId);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam, getDomainId() };
		lstStatisticItem = findByConditions(cols, operators, values, "");
		return lstStatisticItem;
	}
	
	// Check delete
	public int checkStatisticItemDpdFomula(StatisticItem statisticItem) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(a) FROM Formula a");
		hql.append(" WHERE (a.statisticItems LIKE :statisticItemIdFisrt");
		hql.append(" OR a.statisticItems LIKE :statisticItemIdBetween");
		hql.append(" OR a.statisticItems LIKE :statisticItemIdEnd");
		hql.append(" OR a.statisticItems LIKE :statisticItemId)");
		hql.append(" AND a.domainId = :domainId");
		Session session = HibernateUtil.getOpenSession();
		try {
			Query<Long> query = session.createQuery(hql.toString(), Long.class);
			query.setParameter("statisticItemIdFisrt", String.valueOf(statisticItem.getStatisticItemId() + ",%"));
			query.setParameter("statisticItemIdBetween", String.valueOf("%," + statisticItem.getStatisticItemId() + ",%"));
			query.setParameter("statisticItemIdEnd", String.valueOf("%," + statisticItem.getStatisticItemId()));
			query.setParameter("statisticItemId", String.valueOf(statisticItem.getStatisticItemId()));

			query.setParameter("domainId", getDomainId());
			return query.getSingleResult().intValue();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public int countStatisticItem() {
		String hql = "SELECT COUNT(a) FROM StatisticItem a WHERE  a.domainId =:domainId";
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

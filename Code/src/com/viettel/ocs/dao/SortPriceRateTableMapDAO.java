package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.PriceComponentBlockMap;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.SortPriceRateTableMap;

public class SortPriceRateTableMapDAO extends BaseDAO<SortPriceRateTableMap> {

	public List<SortPriceRateTableMap> findSortPriceRateTableMapBySPCId(long sortPriceComponentId) {
		List<SortPriceRateTableMap> lst = null;
		String[] cols = { "sortPriceComponentId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { sortPriceComponentId };
		String oder = " rateTableIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	public boolean deleteSortPriceRateTableMapBySPCId(SortPriceComponent sortPriceComponent) {
		String hql = "DELETE FROM SortPriceRateTableMap map";
		hql += " WHERE map.sortPriceComponentId = :sortPriceComponentId";
		hql += " AND map.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(sortPriceComponent);
			Query<SortPriceRateTableMap> query = session.createQuery(hql);
			query.setParameter("sortPriceComponentId", sortPriceComponent.getSortPriceComponentId());
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

	public void saveMapSPCRate(SortPriceRateTableMap itemUp, SortPriceRateTableMap item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			long indexUp = itemUp.getRateTableIndex();
			itemUp.setRateTableIndex(item.getRateTableIndex());
			session.saveOrUpdate(itemUp);

			item.setRateTableIndex(indexUp);
			session.saveOrUpdate(item);

			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public boolean checkRTinSP(Long rateTableId) {
		List<SortPriceRateTableMap> lst = null;
		String[] cols = { "rateTableId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { rateTableId };
		String oder = " rateTableIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public SortPriceRateTableMap findSortPriceRT(Long sortPriceComponentId, Long rateTableId) {
		List<SortPriceRateTableMap> lst = null;
		String[] cols = { "sortPriceComponentId", "rateTableId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { sortPriceComponentId, rateTableId };
		String oder = " rateTableIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (!lst.isEmpty()) {
			return lst.get(0);
		}
		return null;
	}

	@Override
	protected Class<SortPriceRateTableMap> getEntityClass() {
		return SortPriceRateTableMap.class;
	}

}

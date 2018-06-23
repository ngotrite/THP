package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.PriceComponentBlockMap;
import com.viettel.ocs.entity.RateTable;

public class PriceComponentBlockMapDAO extends BaseDAO<PriceComponentBlockMap> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1703404204573558588L;

	public List<PriceComponentBlockMap> findPriceComponentBlockMapByPC(Long priceComponentId) {
		List<PriceComponentBlockMap> lst = null;
		String[] cols = { "priceComponentId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { priceComponentId };
		String oder = " blockIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}

	@Override
	protected Class<PriceComponentBlockMap> getEntityClass() {
		return PriceComponentBlockMap.class;
	}

	public void saveMapPCBlock(PriceComponentBlockMap itemUp, PriceComponentBlockMap item) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			long indexUp = itemUp.getBlockIndex();
			itemUp.setBlockIndex(item.getBlockIndex());
			session.saveOrUpdate(itemUp);

			item.setBlockIndex(indexUp);
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

	public boolean checkBlockInPC(Long blockId) {
		List<PriceComponentBlockMap> lst = null;
		String[] cols = { "blockId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { blockId };
		String oder = " blockIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public PriceComponentBlockMap findPriceComponentBlock(Long priceComponentId, Long blockId) {
		List<PriceComponentBlockMap> lst = null;
		String[] cols = { "priceComponentId", "blockId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { priceComponentId, blockId };
		String oder = " blockIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (!lst.isEmpty()) {
			return lst.get(0);
		}
		return null;
	}

	public boolean deletePCBlockByPCId(PriceComponent priceComponent) {
		String hql = "DELETE FROM PriceComponentBlockMap map";
		hql += " WHERE map.priceComponentId = :priceComponentId";
		hql += " AND map.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(priceComponent);
			Query<PriceComponentBlockMap> query = session.createQuery(hql);
			query.setParameter("priceComponentId", priceComponent.getPriceComponentId());
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

package com.viettel.ocs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.PriceComponentBlockMap;

public class BlockRateTableMapDAO extends BaseDAO<BlockRateTableMap> {

	public List<BlockRateTableMap> findListBlockRateTableMapByBlockIdType(Long blockId, long type) {
		List<BlockRateTableMap> lst = null;
		String[] cols = { "blockId", "componentType" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { blockId, type };
		lst = findByConditions(cols, operators, values, "rateTableIndex ASC");
		return lst;
	}

	public List<BlockRateTableMap> findListBlockRateTableMapByBlockId(Long blockId) {
		List<BlockRateTableMap> lst = null;
		String[] cols = { "blockId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { blockId };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}

	public boolean deleteBlockRateTableMapByBlockId(Block block) {
		String hql = "DELETE FROM BlockRateTableMap map";
		hql += " WHERE map.blockId = :blockId";
		hql += " AND map.domainId = :domainId";
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			session.delete(block);
			Query<Block> query = session.createQuery(hql);
			query.setParameter("blockId", block.getBlockId());
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

	public long getMaxIndexRateTable(Long blockId, long type) {
		long index = 0;
		List<BlockRateTableMap> lst = null;
		String[] cols = { "blockId", "componentType" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { blockId, type };
		lst = findByConditions(cols, operators, values, "rateTableIndex ASC");
		if (!lst.isEmpty() && lst.size() > 0) {
			index = lst.get(lst.size() - 1).getRateTableIndex();
		}
		return index;
	}

	public boolean checkRTinBL(Long rateTableId) {
		List<BlockRateTableMap> lst = null;
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

	public BlockRateTableMap findBlockRT(Long blockId, Long rateTableId, Long componentType) {
		List<BlockRateTableMap> lst = null;
		String[] cols = { "blockId", "rateTableId", "componentType"};
		Operator[] operators = { Operator.EQ, Operator.EQ, Operator.EQ };
		Object[] values = { blockId, rateTableId, componentType };
		String oder = " rateTableIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (!lst.isEmpty()) {
			return lst.get(0);
		}
		return null;
	}

	@Override
	protected Class<BlockRateTableMap> getEntityClass() {
		return BlockRateTableMap.class;
	}
}

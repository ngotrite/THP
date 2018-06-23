package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.PriceComponentBlockMap;
import com.viettel.ocs.entity.SortPriceRateTableMap;
import com.viettel.ocs.entity.UnitType;

@SuppressWarnings("serial")
public class DynamicReserveRateTableMapDAO extends BaseDAO<DynamicReserveRateTableMap> implements Serializable {

	@Override
	protected Class<DynamicReserveRateTableMap> getEntityClass() {
		// TODO Auto-generated method stub
		return DynamicReserveRateTableMap.class;
	}
	
	public List<DynamicReserveRateTableMap> findDynamicReserveRateTableMapByRT(Long dynamicReserveId) {
		List<DynamicReserveRateTableMap> lst = null;
		String[] cols = { "dynamicReserveId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { dynamicReserveId };
		String oder = " rateTableIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		return lst;
	}
	

	public void saveMapDynamicRateTable(DynamicReserveRateTableMap itemUp, DynamicReserveRateTableMap item) {
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
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
	
	public DynamicReserveRateTableMap findDynamicReserveRT(Long dynamicReserveId, Long rateTableId) {
		List<DynamicReserveRateTableMap> lst = null;
		String[] cols = { "dynamicReserveId", "rateTableId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { dynamicReserveId, rateTableId };
		String oder = " rateTableIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (!lst.isEmpty()) {
			return lst.get(0);
		}
		return null;
	}
	
	
	public boolean checkRTinDR(Long rateTableId) {
		List<DynamicReserveRateTableMap> lst = null;
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


}

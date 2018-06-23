package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.OfferActionMap;
import com.viettel.ocs.entity.PriceComponentBlockMap;

public class OfferActionMapDAO extends BaseDAO<OfferActionMap> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean checkActionInOffer(Long actionId) {
		List<OfferActionMap> lst = null;
		String[] cols = { "actionId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { actionId };
		String oder = " mapIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	protected Class<OfferActionMap> getEntityClass() {
		// TODO Auto-generated method stub
		return OfferActionMap.class;
	}

	public OfferActionMap findOfferActionMap(Long offerId, Long actionId) {
		List<OfferActionMap> lst = null;
		String[] cols = { "offerId", "actionId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { offerId, actionId };
		String oder = " mapIndex ASC";
		lst = findByConditions(cols, operators, values, oder);
		if (!lst.isEmpty()) {
			return lst.get(0);
		}
		return null;
	}
}

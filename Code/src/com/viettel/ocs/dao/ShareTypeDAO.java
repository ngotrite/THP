package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ShareType;

public class ShareTypeDAO extends BaseDAO<ShareType> implements Serializable {
	@Override
	protected Class<ShareType> getEntityClass() {
		return ShareType.class;
	}
	
	public List<ShareType> findSTByMapShareBal(Long mapSharebalBalId) {

		List<ShareType> lst = null;
		String[] cols = { "mapSharebalBalId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { mapSharebalBalId };
		lst = findByConditions(cols, operators, values, "");
		return lst;

	}
	
	public List<ShareType> findAllShareType() {
		List<ShareType> lstShareType = new ArrayList<ShareType>();
		String[] cols = {};
		Operator[] operators = {};
		Object[] values = {};
		lstShareType = findByConditionsWithoutDomain(cols, operators, values, "");
		return lstShareType;
	}
}

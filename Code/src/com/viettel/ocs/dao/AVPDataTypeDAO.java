package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.AVPDataType;

@SuppressWarnings("serial")
public class AVPDataTypeDAO extends BaseDAO<AVPDataType> implements Serializable{

	@Override
	protected Class<AVPDataType> getEntityClass() {
		return AVPDataType.class;
	}
	
	public boolean checkName(AVPDataType avpDataType, String avpDataTypeName) {
		List<AVPDataType> lst = null;
		String[] cols = { "avpDataTypeName" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { avpDataTypeName };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getAvpDataTypeId() == avpDataType.getAvpDataTypeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
}

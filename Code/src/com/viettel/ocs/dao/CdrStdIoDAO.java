package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.CdrStdIo;

@SuppressWarnings("serial")
public class CdrStdIoDAO extends BaseDAO<CdrStdIo> implements Serializable{
	@Override
	protected Class<CdrStdIo> getEntityClass() {
		return CdrStdIo.class;
	}

	public boolean checkCdrStdConfigId(CdrStdIo cdrStdIo, Long cdrStdConfigId) {
		List<CdrStdIo> lst = null;
		String[] cols = { "cdrStdConfigId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { cdrStdConfigId };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getCdrStdIoPropId() == cdrStdIo.getCdrStdIoPropId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean checkPropertyId(CdrStdIo cdrStdIo, Long propertyId) {
		List<CdrStdIo> lst = null;
		String[] cols = { "propertyId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { propertyId };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getCdrStdIoPropId() == cdrStdIo.getCdrStdIoPropId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	

}

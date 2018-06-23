package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.AVPDataType;
import com.viettel.ocs.entity.AVPDefine;

@SuppressWarnings("serial")
public class AVPDefineDAO extends BaseDAO<AVPDefine> implements Serializable{
	@Override
	protected Class<AVPDefine> getEntityClass() {
		return AVPDefine.class;
	}
	
	public boolean checkAVPDataType(Long avpDataTypeId) {
		List<AVPDefine> lst = null;
		String[] cols = { "avpDataTypeId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { avpDataTypeId };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean checkName(AVPDefine avpDefine, String avpDefineName) {
		List<AVPDefine> lst = null;
		String[] cols = { "avpDefineName" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { avpDefineName };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getAvpDefineId() == avpDefine.getAvpDefineId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;

	}
}

package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.CdrStdConfig;

@SuppressWarnings("serial")
public class CdrStdConfigDAO extends BaseDAO<CdrStdConfig> implements Serializable{
	@Override
	protected Class<CdrStdConfig> getEntityClass() {
		return CdrStdConfig.class;
	}

	public boolean checkCdrServiceId(CdrStdConfig cdrStdConfig, Long cdrServiceId) {
		List<CdrStdConfig> lst = null;
		String[] cols = { "cdrServiceId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { cdrServiceId };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getCdrStdConfigId() == cdrStdConfig.getCdrStdConfigId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkcdrTemplatedId(CdrStdConfig cdrStdConfig, Long cdrTemplatedId) {
		List<CdrStdConfig> lst = null;
		String[] cols = { "cdrTemplatedId" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { cdrTemplatedId };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getCdrStdConfigId() == cdrStdConfig.getCdrStdConfigId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean checkCdrStdConfigName(CdrStdConfig cdrStdConfig, String cdrStdConfigName) {
		List<CdrStdConfig> lst = null;
		String[] cols = { "cdrStdConfigName" };
		Operator[] operators = { Operator.EQ };
		Object[] values = { cdrStdConfigName };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getCdrStdConfigId() == cdrStdConfig.getCdrStdConfigId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
}

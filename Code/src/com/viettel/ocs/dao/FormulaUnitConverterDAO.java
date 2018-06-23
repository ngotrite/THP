package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.FormulaUnitConverter;

public class FormulaUnitConverterDAO extends BaseDAO<FormulaUnitConverter> implements Serializable{

	
	public List<FormulaUnitConverter> findAllFormulaUnitConverter() {
		List<FormulaUnitConverter> lstFormulaUnitConverter = new ArrayList<FormulaUnitConverter>();
		String[] cols = {};
		Operator[] operators = {};
		Object[] values = {};
		lstFormulaUnitConverter = findByConditionsWithoutDomain(cols, operators, values, "");
		return lstFormulaUnitConverter;
	}
	
	@Override
	protected Class<FormulaUnitConverter> getEntityClass() {
		return FormulaUnitConverter.class;
	}
}

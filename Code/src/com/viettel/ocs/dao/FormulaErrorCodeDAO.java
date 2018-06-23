package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.FormulaErrorCode;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.UnitType;



public class FormulaErrorCodeDAO extends BaseDAO<FormulaErrorCode> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<FormulaErrorCode> findAllFormulaErrorCode() {
		List<FormulaErrorCode> lstFormulaErrorCode = new ArrayList<FormulaErrorCode>();
		String[] cols = {};
		Operator[] operators = {};
		Object[] values = {};
		lstFormulaErrorCode = findByConditionsWithoutDomain(cols, operators, values, "");
		return lstFormulaErrorCode;
	}
	
	public boolean checkName(FormulaErrorCode fErrorCode, Long errorCode) {
		List<FormulaErrorCode> lst = null;
		String[] cols = { "errorCode"};
		Operator[] operators = { Operator.EQ};
		Object[] values = { errorCode };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getFormulaErrorCodeId() == fErrorCode.getFormulaErrorCodeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean checkNameDes(FormulaErrorCode fErrorCode, String description) {
		List<FormulaErrorCode> lst = null;
		String[] cols = { "description"};
		Operator[] operators = { Operator.EQ};
		Object[] values = { description };
		lst = findByConditionsWithoutDomain(cols, operators, values, "");
		if (lst.size() > 0) {
			if (lst.get(0).getFormulaErrorCodeId() == fErrorCode.getFormulaErrorCodeId()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Class<FormulaErrorCode> getEntityClass() {
		return FormulaErrorCode.class;
	}
}

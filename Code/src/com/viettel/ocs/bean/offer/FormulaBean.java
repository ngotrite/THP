package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;


import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.Normalizer;
import com.viettel.ocs.dao.FormulaDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.TriggerOcs;

@SuppressWarnings("serial")
@ManagedBean(name = "formulaBean", eager = true)
@ViewScoped
public class FormulaBean extends BaseController implements Serializable {
	private Formula formula = new Formula();
	private FormulaDAO formulaDAO = new FormulaDAO();
	
	private List<SelectItem> listNormalizingValueType;
	private List<SelectItem> listFormulaType;
	private List<SelectItem> listComboParameter;

	private boolean isEditting;
	
	@PostConstruct
	public void init() {
		this.isEditting = false;
	}
	
	// Load list Formula Type===============================
	public List<SelectItem> loadComboFormulaType() {
		listFormulaType = new ArrayList<SelectItem>();
		listFormulaType
				.add(new SelectItem(Normalizer.FormulaType.NORMAL_TYPE, Normalizer.FormulaType.NORMAL_TYPE_NAME));
		listFormulaType.add(new SelectItem(Normalizer.FormulaType.SKIP, Normalizer.FormulaType.SKIP_NAME));
		listFormulaType.add(new SelectItem(Normalizer.FormulaType.DENY, Normalizer.FormulaType.DENY_NAME));
		listFormulaType.add(new SelectItem(Normalizer.FormulaType.SORT_TYPE, Normalizer.FormulaType.SORT_TYPE_NAME));
		listFormulaType.add(
				new SelectItem(Normalizer.FormulaType.DYNAMIC_RESERVE, Normalizer.FormulaType.DYNAMIC_RESERVE_NAME));

		return listFormulaType;
	}

	// Load list Normalizing Value Type===============================
	public List<SelectItem> loadComboNormalizingValueType() {
		listNormalizingValueType = new ArrayList<SelectItem>();
		listNormalizingValueType
				.add(new SelectItem(Normalizer.NormalizingValueType.NONE, Normalizer.NormalizingValueType.NONE_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.MINUTE_TO_SECOND,
				Normalizer.NormalizingValueType.MINUTE_TO_SECOND_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.HOUR_TO_SECOND,
				Normalizer.NormalizingValueType.HOUR_TO_SECOND_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.DAY_TO_SECOND,
				Normalizer.NormalizingValueType.DAY_TO_SECOND_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.WEEK_TO_SECOND,
				Normalizer.NormalizingValueType.WEEK_TO_SECOND_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.END_OF_MIN,
				Normalizer.NormalizingValueType.END_OF_MIN_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.END_OF_HOUR,
				Normalizer.NormalizingValueType.END_OF_HOUR_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.END_OF_DAY,
				Normalizer.NormalizingValueType.END_OF_DAY_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.END_OF_WEEK,
				Normalizer.NormalizingValueType.END_OF_WEEK_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.END_OF_MONTH,
				Normalizer.NormalizingValueType.END_OF_MONTH_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.MBYTE_TO_KBYTE,
				Normalizer.NormalizingValueType.MBYTE_TO_KBYTE_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.GBYTE_TO_KBYTE,
				Normalizer.NormalizingValueType.GBYTE_TO_KBYTE_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.CURR_TIME,
				Normalizer.NormalizingValueType.CURR_TIME_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.HOURS_FROM_CURR_TIME,
				Normalizer.NormalizingValueType.HOURS_FROM_CURR_TIME_NAME));
		listNormalizingValueType.add(new SelectItem(Normalizer.NormalizingValueType.HOURS_FROM_START_OF_DAY,
				Normalizer.NormalizingValueType.HOURS_FROM_START_OF_DAY_NAME));

		return listNormalizingValueType;
	}

	public List<SelectItem> loadComboListParameter() {
		listComboParameter = new ArrayList<SelectItem>();
		ParameterDAO parameterDAO = new ParameterDAO();
		List<Parameter> listParameterType = parameterDAO.findAll("");
		if (listComboParameter != null && !listParameterType.isEmpty()) {
			for (Parameter parameterType : listParameterType) {
				listComboParameter.add(
						new SelectItem(parameterType.getParameterId(), parameterType.getParameterName()));
			}
		}
		return listComboParameter;
	}
	

	
	
	
	
	
	public void commandApply() {
		formulaDAO.saveOrUpdate(formula);

	}
	

	// ***** GET SET *****//
	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public List<SelectItem> getListNormalizingValueType() {
		return listNormalizingValueType;
	}

	public void setListNormalizingValueType(List<SelectItem> listNormalizingValueType) {
		this.listNormalizingValueType = listNormalizingValueType;
	}

	public List<SelectItem> getListFormulaType() {
		return listFormulaType;
	}

	public void setListFormulaType(List<SelectItem> listFormulaType) {
		this.listFormulaType = listFormulaType;
	}

	public List<SelectItem> getListComboParameter() {
		return listComboParameter;
	}

	public void setListComboParameter(List<SelectItem> listComboParameter) {
		this.listComboParameter = listComboParameter;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public FormulaDAO getFormulaDAO() {
		return formulaDAO;
	}

	public void setFormulaDAO(FormulaDAO formulaDAO) {
		this.formulaDAO = formulaDAO;
	}

	
	

}

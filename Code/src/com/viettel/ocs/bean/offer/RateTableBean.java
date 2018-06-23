package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.Normalizer.FormulaType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.BlockRateTableMapDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveRateTableMapDAO;
import com.viettel.ocs.dao.FormulaDAO;
import com.viettel.ocs.dao.FormulaErrorCodeDAO;
import com.viettel.ocs.dao.FormulaUnitConverterDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.SortPriceRateTableMapDAO;
import com.viettel.ocs.dao.StatisticItemDAO;
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.BlockRateTableMap;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.DynamicReserveRateTableMap;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.FormulaErrorCode;
import com.viettel.ocs.entity.FormulaUnitConverter;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RateTableDump;
import com.viettel.ocs.entity.RateTableResult;
import com.viettel.ocs.entity.RowDt;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.SortPriceRateTableMap;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.model.ColumnModel;
import com.viettel.ocs.model.FormulaViewModel;
import com.viettel.ocs.util.CommonUtil;
import com.viettel.ocs.util.ValidateUtil;

@SuppressWarnings("serial")
@ManagedBean(name = "rateTableBean")
@ViewScoped
public class RateTableBean extends BaseController implements Serializable {

	private String formType = "";
	private RateTable rateTable;
	private RateTableDAO rateTableDAO = new RateTableDAO();
	private DecisionTableDAO decisionTableDAO;
	private List<RateTable> listRateTableByCategory;
	private List<SelectItem> listComboCategory;
	private List<SelectItem> listState;
	private List<Formula> formulas;
	private List<UnitType> listComboUnitType;
	private DecisionTable decisionTable;
	private CategoryDAO categoryDAO;
	private FormulaDAO formulaDAO;
	private UnitTypeDAO unitTypeDAO;
	private Category category;
	private List<SelectItem> listComboDecisionTable;
	private boolean isEditting;
	private UnitType unitType;
	private List<Category> categoriesOfRT;

	private List<SelectItem> listNormalizingValueType;
	private List<Parameter> listComboParameter;
	private List<FormulaErrorCode> listComboFormulaErrorCode;
	private List<FormulaUnitConverter> listFormulaUnitConverter;
	private List<SelectItem> listComboTriggerOcs;

	private FormulaViewModel genericFormulaModelForCancel;

	private FormulaViewModel genericFormulaModel;

	private FormulaViewModel defaultFormulaModel;

	private FormulaViewModel stateFormulaModel;

	private Map<Long, FormulaViewModel> tableFormulaModels;

	private List<RateTableResult> listRateTableResult;
	private RateTableResult rateTableResult;

	private List<TriggerOcs> listTriggerOcs;
	private TriggerOcs triggerOcs;
	
	private List<StatisticItem> lstStatisticItem;
	private StatisticItem statisticItem;

	// DECISION TABLE
	private List<ColumnDt> columnDts;
	private HashMap<Long, List<NormValue>> normValuesMap;
	private List<ColumnModel> columns;
	private List<RowDt> rowDts;

	private HashMap<String, Long> error;

	private FormulaErrorCode fErrorCode;
	private FormulaErrorCodeDAO fErrorCodeDAO;

	@PostConstruct
	public void init() {
		category = new Category();
		decisionTable = new DecisionTable();
		categoryDAO = new CategoryDAO();
		this.formulaDAO = new FormulaDAO();
		decisionTableDAO = new DecisionTableDAO();
		formulas = new ArrayList<Formula>();
		this.columnDts = new ArrayList<ColumnDt>();
		this.normValuesMap = new HashMap<Long, List<NormValue>>();
		this.columns = new ArrayList<ColumnModel>();
		this.rowDts = new ArrayList<RowDt>();
		this.genericFormulaModelForCancel = new FormulaViewModel();
		this.genericFormulaModel = new FormulaViewModel();
		this.defaultFormulaModel = new FormulaViewModel();
		this.stateFormulaModel = new FormulaViewModel();
		this.tableFormulaModels = new HashMap<Long, FormulaViewModel>();
		listTriggerOcs = new ArrayList<TriggerOcs>();
		this.isEditting = true;
		this.listComboFormulaErrorCode = new ArrayList<FormulaErrorCode>();
		error = new HashMap<String, Long>();
		this.listFormulaUnitConverter = new ArrayList<FormulaUnitConverter>();
		this.fErrorCode = new FormulaErrorCode();
		this.fErrorCodeDAO = new FormulaErrorCodeDAO();
		this.listComboUnitType = new ArrayList<UnitType>();
		this.unitTypeDAO = new UnitTypeDAO();
		this.triggerOcs = new TriggerOcs();
		this.categoriesOfRT = new ArrayList<Category>();
		this.lstStatisticItem = new ArrayList<StatisticItem>();
		this.statisticItem = new StatisticItem();
	}

	public void showDialogDecision() {
		super.openTreeOfferDialog(TreeType.OFFER_DECISION_TABLE, CategoryType.OFF_DT_DECISION_TABLE_NAME, 0, false, null);
	}

	private Long previousRowId = -1L;

	public void showDialogFormula(String type, Long rowId) throws CloneNotSupportedException {

		if (rateTable.getUnitTypeId() != null) {
			unitType = unitTypeDAO.get(rateTable.getUnitTypeId());
		} else {
			unitType = null;
		}
		switch (type) {
		case "default_formula":
			genericFormulaModel = defaultFormulaModel;
			break;
		case "state_formula":
			genericFormulaModel = stateFormulaModel;
			break;
		case "row_formula":
			if (tableFormulaModels.get(rowId) == null) {
				FormulaViewModel formulaViewModel = new FormulaViewModel();

				if (previousRowId != -1L) {
					FormulaViewModel preFormulaViewModel = tableFormulaModels.get(previousRowId);
					if (preFormulaViewModel != null) {
						formulaViewModel = preFormulaViewModel.clone();
					}
				}
				tableFormulaModels.put(rowId, formulaViewModel);
			}
			genericFormulaModel = tableFormulaModels.get(rowId);
			previousRowId = rowId;
			break;
		default:
			break;
		}

		if (validateConvert(genericFormulaModel)) {
			genericFormulaModel.setUnitType(unitType);
		} else {
			genericFormulaModel.setUnitType(null);
		}

		genericFormulaModel.cal();
		splitTriggerIdsValue(genericFormulaModel.getFormula());
		splitStatisticItemIdsValue(genericFormulaModel.getFormula());
		genericFormulaModelForCancel = genericFormulaModel.clone();
	}

	public boolean validateConvert(FormulaViewModel formulaViewModel) {
		boolean result = false;
		if ((rateTable.getUnitTypeId() != null && rateTable.getUnitTypeId() != 0L)
				&& (formulaViewModel.getFormula().getIsPercentage() != null
						&& formulaViewModel.getFormula().getIsPercentage() == false)
				&& (formulaViewModel.getFormula().getNormalizingValueType() == null || formulaViewModel.getFormula()
						.getNormalizingValueType() == com.viettel.ocs.constant.Normalizer.NormalizingValueType.NONE)
				&& (formulaViewModel.getFormula().getFormulaType() != null && formulaViewModel.getFormula()
						.getFormulaType() == com.viettel.ocs.constant.Normalizer.FormulaType.NORMAL_TYPE)) {
			result = true;
		}
		return result;
	}

	public boolean validateConvertXhtml() {
		genericFormulaModel.setFormulaType();
		boolean result = false;
		if ((rateTable.getUnitTypeId() != null && rateTable.getUnitTypeId() != 0L)
				&& (genericFormulaModel.getFormula().getIsPercentage() != null
						&& genericFormulaModel.getFormula().getIsPercentage() == false)
				&& (genericFormulaModel.getFormula().getNormalizingValueType() == null
						|| genericFormulaModel.getFormula()
								.getNormalizingValueType() == com.viettel.ocs.constant.Normalizer.NormalizingValueType.NONE)
				&& (genericFormulaModel.getFormula().getFormulaType() != null && genericFormulaModel.getFormula()
						.getFormulaType() == com.viettel.ocs.constant.Normalizer.FormulaType.NORMAL_TYPE)) {
			result = true;
		}
		return result;
	}
	
	public String getAbbreviation(Long unitTypeId) {		
		if (rateTable.getUnitTypeId() != null) {
			unitType = unitTypeDAO.get(rateTable.getUnitTypeId());
			unitTypeId = rateTable.getUnitTypeId();
			String abbreviation = unitTypeDAO.get(unitTypeId).getAbbreviation();
			return abbreviation;
		}
		return null;
	}
	
	public String getAbbreviationNew(Long unitTypeId, FormulaViewModel formulaViewModel) {		
		if ((rateTable.getUnitTypeId() != null && rateTable.getUnitTypeId() != 0L)
				&& (formulaViewModel.getFormula().getIsPercentage() != null)
		&& (formulaViewModel.getFormula().getNormalizingValueType() == null || formulaViewModel.getFormula()
				.getNormalizingValueType() != null)
		&& (formulaViewModel.getFormula().getFormulaType() != null && formulaViewModel.getFormula()
				.getFormulaType() == com.viettel.ocs.constant.Normalizer.FormulaType.NORMAL_TYPE)) {
			unitType = unitTypeDAO.get(rateTable.getUnitTypeId());
			unitTypeId = rateTable.getUnitTypeId();
			String abbreviation = unitTypeDAO.get(unitTypeId).getAbbreviation();
			return abbreviation;
		}
		return null;
	}

	public void showDialogParameter(String for_variable) {
		super.openTreeCommonDialog(TreeType.CATALOG_PARAMETER, CategoryType.CTL_PARAMETER_NAME, 0, false, null);
		this.for_variable = for_variable;
	}

	String for_variable = "";

	public void commandAddNewStatistic() {
		StatisticItem itemNew = new StatisticItem();
		chooseStatisticItem(itemNew);

	}
	
	private void chooseStatisticItem(StatisticItem item) {
		List<Long> lstId = new ArrayList<Long>();
		if(lstStatisticItem != null) {
			for (StatisticItem si : lstStatisticItem) {
				lstId.add(si.getStatisticItemId());
			}	
		}
		
		if (item == null || item.getStatisticItemId() <= 0){			
			super.openTreeCommonDialog(TreeType.CATALOG_STATISTIC_ITEM, readProperties("statisticItem.title"),CategoryType.CTL_STATISTIC_ITEM, true, lstId);
		} else {
			super.openTreeCommonDialog(TreeType.CATALOG_STATISTIC_ITEM, readProperties("statisticItem.title"),
					CategoryType.CTL_STATISTIC_ITEM, false, lstId);
		}
		statisticItem = item;
	}
	
	public void onDialogStatisticReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}
		for (Object obj : objArr) {
			if (obj instanceof StatisticItem) {
				StatisticItem statisticItem = (StatisticItem) obj;
				if (!exitStatisticItem(statisticItem)) {
					lstStatisticItem.add(statisticItem);
				} else {
					this.showMessageWARN("", "Statistic Item", this.readProperties("common.nameAlreadyExists"));
				}
			}
		}

	}
	
	private boolean exitStatisticItem(StatisticItem StatisticItemInput) {
		for (StatisticItem statisticItem : lstStatisticItem) {
			if (statisticItem.getStatisticItemId() == StatisticItemInput.getStatisticItemId()) {
				return true;
			}
		}
		return false;
	}


	public void chooseTrigger(TriggerOcs item) {
		List<Long> lstId = new ArrayList<Long>();
		if(listTriggerOcs != null) {
			for (TriggerOcs to : listTriggerOcs) {
				lstId.add(to.getTriggerOcsId());
			}	
		}
		
		if (item == null || item.getTriggerOcsId() <= 0){			
			super.openTreeCommonDialog(TreeType.CATALOG_TRIGGER_OCS, readProperties("triggerOcs"),CategoryType.CTL_TO_TRIGGER_OCS, true, lstId);
		} else {
			super.openTreeCommonDialog(TreeType.CATALOG_TRIGGER_OCS, readProperties("triggerOcs"),
					CategoryType.CTL_TO_TRIGGER_OCS, false, lstId);
		}
		triggerOcs = item;
	}

	public void commandAddNewTriggerOcs() {
		TriggerOcs itemNew = new TriggerOcs();
		chooseTrigger(itemNew);
	}

	public void onDialogTriggerReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}
		for (Object obj : objArr) {
			if (obj instanceof TriggerOcs) {
				TriggerOcs triggerOcs = (TriggerOcs) obj;
				if (!exitTrigger(triggerOcs)) {
					listTriggerOcs.add(triggerOcs);
				} else {
					this.showMessageWARN("", "Trigger", this.readProperties("common.nameAlreadyExists"));
				}
			}
		}
	}

	public void onDialogReturn(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof DecisionTable) {
			this.decisionTable = (DecisionTable) obj;
			rateTable.setDecisionTableId(decisionTable.getDecisionTableId());
			initDecisionTable(decisionTable);
		} else if (obj instanceof TriggerOcs) {
			TriggerOcs triggerOcs = (TriggerOcs) obj;
			if (!exitTrigger(triggerOcs)) {
				listTriggerOcs.add(triggerOcs);
			} else {
				this.showMessageWARN("", "Trigger", this.readProperties("common.nameAlreadyExists"));
			}
		} else if (obj instanceof Parameter) {
			Parameter param = (Parameter) obj;
			if (for_variable.equalsIgnoreCase("A")) {
				genericFormulaModel.setPramA(param.getParameterId());
			} else if (for_variable.equalsIgnoreCase("B")) {
				genericFormulaModel.setPramB(param.getParameterId());
			} else if (for_variable.equalsIgnoreCase("TYPE")) {
				genericFormulaModel.setPramType(param.getParameterId());
			} else if (for_variable.equalsIgnoreCase("PER")) {
				genericFormulaModel.setPramPer(param.getParameterId());
			}
		} else if (obj instanceof RateTable) {
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			List<DecisionTable> decisionTables = new ArrayList<DecisionTable>();
			DecisionTable dt = decisionTableDAO.get(((RateTable) obj).getDecisionTableId());
			if (dt != null)
				decisionTables.add(dt);
			treeOfferBean.updateTreeNode((RateTable) obj, categoryDAO.get(((RateTable) obj).getCategoryId()),
					decisionTables);
			commandEditTable((RateTable) obj);
			this.showMessageINFO("validate.cloneSuccess", super.readProperties(""));
		}
	}

	private boolean exitTrigger(TriggerOcs triggerOcsInput) {
		for (TriggerOcs triggerOcs : listTriggerOcs) {
			if (triggerOcs.getTriggerOcsId() == triggerOcsInput.getTriggerOcsId()) {
				return true;
			}
		}
		return false;
	}

	public void setGetRecipe(String dump) {

	}

	public String getRecipe(FormulaViewModel formulaViewModel) {
		formulaViewModel.cal();
		StringBuffer recip = new StringBuffer("");
		Formula formula = formulaViewModel.getFormula();
		if (!formulaViewModel.isApplied()) {
			return "";
		} else if (formula.getFormulaType() == FormulaType.SKIP) {
			return FormulaType.SKIP_NAME;
		} else if (formula.getFormulaType() == FormulaType.DENY) {
			return FormulaType.DENY_NAME;
		} else if (formula.getFormulaType() == FormulaType.EXIT) {
			return FormulaType.EXIT_NAME;

		} else {
			if (formulaViewModel.getA() == null ? false : formulaViewModel.getA()) {
				for (Parameter parameter : listComboParameter) {
					if (formulaViewModel.getFormula().getA() != null
							&& parameter.getParameterId() == formulaViewModel.getFormula().getA()) {
						recip.append(parameter.getParameterName());
						recip.append("x + ");
						break;
					}
				}
			} else {
				if (formulaViewModel.getFormula().getA() != null) {
					if (validateConvert(formulaViewModel)) {
						recip.append(formulaViewModel.getTemA());
						recip.append("x + ");
					} else {
						recip.append(formulaViewModel.getFormula().getA());
						recip.append("x + ");
					}
//					recip.append(formulaViewModel.getTemA());
//					recip.append("x + ");
				}

			}

			if (formulaViewModel.getB() == null ? false : formulaViewModel.getB()) {
				for (Parameter parameter : listComboParameter) {
					if (formulaViewModel.getFormula().getB() != null
							&& parameter.getParameterId() == formulaViewModel.getFormula().getB()) {
						recip.append(parameter.getParameterName());
						String abbr = getAbbreviation(rateTable.getUnitTypeId());
						if(!CommonUtil.isEmpty(abbr)) {
							recip.append("(");
							if (rateTable.getUnitTypeId() != null) {
								recip.append(abbr);
							}
							recip.append(")");	
						}
						
						recip.append("; per = ");
						break;
					}
				}
			} else {
				if (formulaViewModel.getFormula().getB() != null) {
					if(formulaViewModel.getFormula().getB() < 0) {
						String replaceAdd = "x + ";
						recip = recip.replace(recip.indexOf(replaceAdd), recip.indexOf(replaceAdd) + replaceAdd.length(), "x ");
					}
					
					if (validateConvert(formulaViewModel)) {
						recip.append(formulaViewModel.getTemB());
					} else {
						recip.append(formulaViewModel.getFormula().getB());
					}
//					recip.append(formulaViewModel.getTemB());
					String abbr = getAbbreviation(rateTable.getUnitTypeId());
					if(!CommonUtil.isEmpty(abbr)) { 
						recip.append("(");
						if (rateTable.getUnitTypeId() != null) {
							recip.append(abbr);
						}
						recip.append(")");	
					}					
					recip.append("; per = ");
				}

			}

			if (formulaViewModel.getPer() == null ? false : formulaViewModel.getPer()) {
				for (Parameter parameter : listComboParameter) {
					if (formulaViewModel.getFormula().getPer() != null
							&& parameter.getParameterId() == formulaViewModel.getFormula().getPer()) {
						recip.append(parameter.getParameterName());
						break;
					}
				}
			} else {
				if (formulaViewModel.getFormula().getPer() != null) {
					recip.append(formulaViewModel.getFormula().getPer());
				}
			}

			return recip.toString();
		}
	}

	public void splitTriggerIdsValue(Formula formula) {
		if (formula.getTriggerIds() != null) {
			List<Long> ids = new ArrayList<Long>();
			String[] f = formula.getTriggerIds().split(",");
			for (String string : f) {
				if (!string.isEmpty()) {
					ids.add(Long.valueOf(string));
				}
			}
			TriggerOcsDAO triggerOcsDAO = new TriggerOcsDAO();
			listTriggerOcs = triggerOcsDAO.findTriggerOcsByListTriggerId(ids);
		} else {
			listTriggerOcs.clear();
		}
	}
	
	public void splitStatisticItemIdsValue(Formula formula) {
		if (formula.getStatisticItems() != null) {
			List<Long> ids = new ArrayList<Long>();
			String[] f = formula.getStatisticItems().split(",");
			for (String string : f) {
				if (!string.isEmpty()) {
					ids.add(Long.valueOf(string));
				}
			}
			StatisticItemDAO statisticItemDAO = new StatisticItemDAO();
			lstStatisticItem = statisticItemDAO.findSIByListStatisticItemId(ids);
		} else {
			lstStatisticItem.clear();
		}
	}

	public void clearDefaultFormula() {
		defaultFormulaModel = new FormulaViewModel(defaultFormulaModel.getFormula(), false, unitType);
	}

	public void clearStateFormula() {
		stateFormulaModel = new FormulaViewModel(stateFormulaModel.getFormula(), false, unitType);
	}

	public void commandAddNew() {
		RateTable rateTable = new RateTable();
		rateTable.setCategoryId(category.getCategoryId());
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.setContentTitle(super.readProperties("title.rateTable"));
		treeOfferBean.hideCategory();
		rateTable.setState(1l);
		refreshRateTable(rateTable);
		initDecisionTable(decisionTable);
		if(this.rateTable.getUnitTypeId() == null && !this.listComboUnitType.isEmpty()) {
			this.unitType = this.listComboUnitType.get(0);
			this.rateTable.setUnitTypeId(unitType.getUnitTypeId());
		}
		this.isEditting = true;
	}

	public void commandAddNewContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		cancelRateTable();
		RateTable item = (RateTable) nodeSelectEvent.getTreeNode().getData();
		if (item != null) {
			rateTable.setCategoryId(item.getCategoryId());
		}
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.setContentTitle(super.readProperties("title.rateTable"));
		treeOfferBean.hideCategory();
		rateTable.setState(1l);
		refreshRateTable(rateTable);
		initDecisionTable(decisionTable);
		this.isEditting = true;

	}

	public void addChildren(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		this.isEditting = true;
	}

	// load list RateTable by Category
	public List<RateTable> loadRateTableByCategory(Long categoryId) {
		listRateTableByCategory = new ArrayList<RateTable>();
		listRateTableByCategory = rateTableDAO.findRateTableByCategoryId(categoryId);
		return listRateTableByCategory;
	}

	public void loadCategoriesOfRT() {
		categoriesOfRT = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_RT_RATE_TABLE);
	}

	// load combo Category==============================================
	public List<SelectItem> loadComboListCategory() {
		listComboCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_RT_RATE_TABLE);
		if (listComboCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listComboCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listComboCategory;
	}

	// load combo Payment Type==========================================
	public List<SelectItem> loadComboState() {
		listState = new ArrayList<SelectItem>();
		listState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.RateTableState.SKIP,
				com.viettel.ocs.constant.Normalizer.RateTableState.SKIP_NAME));
		listState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.RateTableState.DENY,
				com.viettel.ocs.constant.Normalizer.RateTableState.DENY_NAME));
		if (rateTable.getDecisionTableId() != null) {
			listState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.RateTableState.FORMULA,
					com.viettel.ocs.constant.Normalizer.RateTableState.FORMULA_NAME));
		}
		listState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.RateTableState.EXIT,
				com.viettel.ocs.constant.Normalizer.RateTableState.EXIT_NAME));
		return listState;
	}

	// load combo DecisionTable=========================================

	public List<SelectItem> loadComboListDecisionTable() {
		listComboDecisionTable = new ArrayList<SelectItem>();
		DecisionTableDAO decisionTableDAO = new DecisionTableDAO();
		List<DecisionTable> listDecisionTable = decisionTableDAO.findAll("");
		if (listDecisionTable != null && !listDecisionTable.isEmpty()) {
			for (DecisionTable decisionTable : listDecisionTable) {
				listComboDecisionTable
						.add(new SelectItem(decisionTable.getDecisionTableId(), decisionTable.getDecisionTableName()));
			}
		}
		return listComboDecisionTable;
	}

	public void commandApplyRateTable() {
		// setIndexForRT();
		if (validateRateTable()) {
			if (rateTableDAO.saveRateTableAndMap(rateTable, defaultFormulaModel, stateFormulaModel, rowDts,
					tableFormulaModels)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				Category cat = categoryDAO.get(rateTable.getCategoryId());
				List<DecisionTable> lst = new ArrayList<DecisionTable>();
				if (rateTable.getDecisionTableId() != null && rateTable.getDecisionTableId() != 0L) {
					lst.add(decisionTable);
				} else {
					lst.clear();
				}
				treeOfferBean.updateTreeNode(rateTable, cat, lst);
				this.showMessageINFO("common.save", " RateTable ");
				this.isEditting = false;
			}
		}
	}

	// Validate
	private boolean validateRateTable() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(rateTable.getRateTableName())) {
			this.showMessageWARN("ratetable.title", super.readProperties("validate.checkValueNameNull"));
			result = false;
		} else if (rateTableDAO.checkName(rateTable)) {
			this.showMessageWARN("ratetable.title", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		if (rateTable.getDecisionTableId() == null) {
			if (!defaultFormulaModel.isApplied()) {
				this.showMessageWARN("ratetable.defaultFormulaId", super.readProperties("validate.checkValueNameNull"));
				result = false;
			}
		}

		if ((rateTable.getDecisionTableId() == null)
				&& (rateTable.getState() == com.viettel.ocs.constant.Normalizer.RateTableState.FORMULA)) {
			this.showMessageWARN("ratetable.formulaId", super.readProperties("validate.stateFormula"));
			result = false;
		}

		if (rateTable.getState() == com.viettel.ocs.constant.Normalizer.RateTableState.FORMULA) {
			if (rateTable.getDecisionTableId() != null) {
				if (getRecipe(stateFormulaModel) == "" || getRecipe(stateFormulaModel).isEmpty()) {
					this.showMessageWARN("ratetable.formulaId", super.readProperties("validate.checkValueNameNull"));
					result = false;
				}
			}
		}

		return result;
	}

	public void deleteRateTable(RateTable item) {
		rateTableDAO.delete(item);
		listRateTableByCategory.remove(item);
		super.showMessageINFO("common.delete", " Rate Table ");

	}

	public void cancelRateTable() {
		rateTable = new RateTable();
	}

	public void commandEditTable(RateTable rateTable) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.setContentTitle(super.readProperties("title.rateTable"));
		treeOfferBean.hideCategory();
		this.isEditting = true;
		refreshRateTable(rateTable);
	}

	// Edit ContextMenu
	public void commandEditContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.setContentTitle(super.readProperties("title.rateTable"));
		treeOfferBean.hideCategory();
		refreshRateTable(rateTable);
		this.isEditting = true;
	}

	// Command Clone Table
	public void commandCloneTable(RateTable item) {
		RateTable rateTable = item;

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 500);
		options.put("height", 450);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		lstPara.add("rateTable;" + rateTable.getRateTableId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("9");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);

	}

	public void commandRemoveTable(RateTable item) {
		rateTableDAO.deleteRateTableAndResult(item);
		// rateTableDAO.delete(item);
		// listRateTableByCategory.remove(item);

		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		Category cat = categoryDAO.get(item.getCategoryId());
		refreshCategories(cat);
		treeOfferBean.removeTreeNodeAll(item);
		this.showMessageINFO("common.delete", "Rate Table");
	}

	// Remove ContextMenu
	public void commandRemoveContextMenu(NodeSelectEvent nodeSelectEvent) {

		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);

		BlockRateTableMapDAO blockRTMapDAO = new BlockRateTableMapDAO();
		DynamicReserveRateTableMapDAO dynamicReserveRTMapDAO = new DynamicReserveRateTableMapDAO();
		SortPriceRateTableMapDAO sortPriceRTMapDAO = new SortPriceRateTableMapDAO();
		BlockDAO blockDAO = new BlockDAO();

		if (nodeSelectEvent != null) {
			RateTable item = (RateTable) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			if (object instanceof Category) {

				if (!blockRTMapDAO.checkRTinBL(this.rateTable.getRateTableId())
						&& !dynamicReserveRTMapDAO.checkRTinDR(this.rateTable.getRateTableId())
						&& !sortPriceRTMapDAO.checkRTinSP(this.rateTable.getRateTableId())) {

					rateTableDAO.deleteRateTableAndResult(item);

					category.setCategoryId(rateTable.getCategoryId());
					refreshCategories(category);

					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.removeTreeNodeAll(item);
					this.showMessageINFO("common.delete", " RateTable ");
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("ratetable.objectUser"));
				}
			} else if (object instanceof DynamicReserve) {
				DynamicReserve dynamicReserve = (DynamicReserve) object;
				DynamicReserveRateTableMap dynamicReserveRTMap = dynamicReserveRTMapDAO
						.findDynamicReserveRT(dynamicReserve.getDynamicReserveId(), item.getRateTableId());
				if (dynamicReserveRTMap != null) {
					dynamicReserveRTMapDAO.delete(dynamicReserveRTMap);
					this.showMessageINFO("common.delete", " RateTable ");
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.removeTreeNode(item, dynamicReserve);
					setFormType("");
				}

			} else if (object instanceof SortPriceComponent) {
				SortPriceComponent sortPC = (SortPriceComponent) object;
				SortPriceRateTableMap sortPriceRTMap = sortPriceRTMapDAO
						.findSortPriceRT(sortPC.getSortPriceComponentId(), item.getRateTableId());
				if (sortPriceRTMap != null) {
					sortPriceRTMapDAO.delete(sortPriceRTMap);
					this.showMessageINFO("common.delete", " RateTable ");
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.removeTreeNode(item, sortPC);
					setFormType("");
				}

			} else if (object instanceof RateTableDump) {
				RateTableDump rateTableDump = (RateTableDump) object;
				BlockRateTableMap blockRTMap = blockRTMapDAO.findBlockRT(rateTableDump.getBlockId(),
						item.getRateTableId(), rateTableDump.getComponentType());
				if (blockRTMap != null) {
					if (rateTableDump.getComponentType() == 1L
							&& blockDAO.countRatetableByBlockIdType(rateTableDump.getBlockId(),
									rateTableDump.getComponentType()) == 1L) {
						this.showMessageWARN("common.summary.warning", super.readProperties(""));
					} else {
						blockRTMapDAO.delete(blockRTMap);
						this.showMessageINFO("common.delete", " RateTable ");
						Block block = new BlockDAO().get(rateTableDump.getBlockId());
						TreeOfferBean treeOfferBean = super.getTreeOfferBean();
						treeOfferBean.removeTreeNodeRateTableBlock(item, block, rateTableDump.getComponentType());
						setFormType("");
					}
				}
			}
		}

	}

	public void setIndexForRT() {
		if (this.rateTable == null || this.rateTable.getRateTableId() == 0) {
			RateTable lastItem = rateTableDAO.findRateTableLastIndex();
			if (lastItem != null) {
				this.rateTable.setIndex(lastItem.getIndex() + 1);
			} else {
				this.rateTable.setIndex(0l);
			}
		}
	}

	// CommandUp ContextMenu
	public void moveUpRT(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			RateTable item = (RateTable) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				if (rateTableDAO.processMoveUpDown(item, true)) {
					RateTable itemAfterMove = rateTableDAO.get(item.getRateTableId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveUpTreeNode(itemAfterMove, objParent);
					loadRateTableByCategory(itemAfterMove.getCategoryId());
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
				}
			} else if (objParent instanceof SortPriceComponent) {
				SortPriceComponent sortPriceComponent = (SortPriceComponent) objParent;

				SortPriceRateTableMapDAO sortPriceRateTableMapDAO = new SortPriceRateTableMapDAO();
				List<SortPriceRateTableMap> sortPriceRateTableMaps = sortPriceRateTableMapDAO
						.findSortPriceRateTableMapBySPCId(sortPriceComponent.getSortPriceComponentId());
				if (!sortPriceRateTableMaps.isEmpty() && sortPriceRateTableMaps.size() > 1) {
					for (int i = 1; i < sortPriceRateTableMaps.size(); i++) {
						if (sortPriceRateTableMaps.get(i).getRateTableId() == item.getRateTableId()) {
							sortPriceRateTableMapDAO.saveMapSPCRate(sortPriceRateTableMaps.get(i - 1),
									sortPriceRateTableMaps.get(i));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveUpTreeNode(item, objParent);
							treeOfferBean.setSortPriceCompornentProperties(false, sortPriceComponent.getCategoryId(),
									sortPriceComponent, false);
							this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));

						} else {

						}
					}

				}

			}

			else if (objParent instanceof DynamicReserve) {
				DynamicReserve dynamicReserve = (DynamicReserve) objParent;
				DynamicReserveRateTableMapDAO dynamicReserveRateTableMapDAO = new DynamicReserveRateTableMapDAO();
				List<DynamicReserveRateTableMap> dynamicReserveRateTableMaps = dynamicReserveRateTableMapDAO
						.findDynamicReserveRateTableMapByRT(dynamicReserve.getDynamicReserveId());
				if (!dynamicReserveRateTableMaps.isEmpty() && dynamicReserveRateTableMaps.size() > 1) {
					for (int i = 1; i < dynamicReserveRateTableMaps.size(); i++) {
						if (dynamicReserveRateTableMaps.get(i).getRateTableId() == item.getRateTableId()) {
							dynamicReserveRateTableMapDAO.saveMapDynamicRateTable(
									dynamicReserveRateTableMaps.get(i - 1), dynamicReserveRateTableMaps.get(i));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveUpTreeNode(item, objParent);
							treeOfferBean.setDynamicReserveProperties(false, dynamicReserve.getCategoryId(),
									dynamicReserve, false);
							this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
						} else {

						}
					}
				}
			} else if (objParent instanceof RateTableDump) {
				RateTableDump rateTableDump = (RateTableDump) objParent;

				if (rateTableDAO.processMoveUpDownMap(item, rateTableDump, true)) {
					RateTable itemAfterMove = rateTableDAO.get(item.getRateTableId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveUpTreeNode(itemAfterMove, objParent);
					loadRateTableByCategory(itemAfterMove.getCategoryId());
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
					Block block = (Block) nodeSelectEvent.getTreeNode().getParent().getParent().getData();
					treeOfferBean.setBlockProperties(false, null, block, false, false);

				}

			}
		}
	}

	// CommandDown ContextMenu
	public void moveDownRT(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			RateTable item = (RateTable) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				if (rateTableDAO.processMoveUpDown(item, false)) {
					RateTable itemAfterMove = rateTableDAO.get(item.getRateTableId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveDownTreeNode(itemAfterMove, objParent);
					loadRateTableByCategory(itemAfterMove.getCategoryId());
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("validate.notMove"));
				}
			} else if (objParent instanceof SortPriceComponent) {
				SortPriceComponent sortPriceComponent = (SortPriceComponent) objParent;

				SortPriceRateTableMapDAO sortPriceRateTableMapDAO = new SortPriceRateTableMapDAO();
				List<SortPriceRateTableMap> sortPriceRateTableMaps = sortPriceRateTableMapDAO
						.findSortPriceRateTableMapBySPCId(sortPriceComponent.getSortPriceComponentId());
				if (!sortPriceRateTableMaps.isEmpty()) {
					for (int i = 0; i < sortPriceRateTableMaps.size(); i++) {
						if (sortPriceRateTableMaps.get(i).getRateTableId() == item.getRateTableId()
								&& i != (sortPriceRateTableMaps.size() - 1)) {
							sortPriceRateTableMapDAO.saveMapSPCRate(sortPriceRateTableMaps.get(i),
									sortPriceRateTableMaps.get(i + 1));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveDownTreeNode(item, objParent);
							treeOfferBean.setSortPriceCompornentProperties(false, sortPriceComponent.getCategoryId(),
									sortPriceComponent, false);
							this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
						} else {

						}
					}
				}

			} else if (objParent instanceof DynamicReserve) {
				DynamicReserve dynamicReserve = (DynamicReserve) objParent;
				DynamicReserveRateTableMapDAO dynamicReserveRateTableMapDAO = new DynamicReserveRateTableMapDAO();
				List<DynamicReserveRateTableMap> dynamicReserveRateTableMaps = dynamicReserveRateTableMapDAO
						.findDynamicReserveRateTableMapByRT(dynamicReserve.getDynamicReserveId());
				if (!dynamicReserveRateTableMaps.isEmpty()) {
					for (int i = 0; i < dynamicReserveRateTableMaps.size(); i++) {
						if (dynamicReserveRateTableMaps.get(i).getRateTableId() == item.getRateTableId()
								&& i != (dynamicReserveRateTableMaps.size() - 1)) {
							dynamicReserveRateTableMapDAO.saveMapDynamicRateTable(dynamicReserveRateTableMaps.get(i),
									dynamicReserveRateTableMaps.get(i + 1));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveDownTreeNode(item, objParent);
							treeOfferBean.setDynamicReserveProperties(false, dynamicReserve.getCategoryId(),
									dynamicReserve, false);
							this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
						}
					}
				}
			} else if (objParent instanceof RateTableDump) {
				RateTableDump rateTableDump = (RateTableDump) objParent;

				if (rateTableDAO.processMoveUpDownMap(item, rateTableDump, false)) {
					RateTable itemAfterMove = rateTableDAO.get(item.getRateTableId());
					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveDownTreeNode(itemAfterMove, objParent);
					loadRateTableByCategory(itemAfterMove.getCategoryId());
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
					Block block = (Block) nodeSelectEvent.getTreeNode().getParent().getParent().getData();
					treeOfferBean.setBlockProperties(false, null, block, false, false);
				}
			}
		}
	}

	// CommandCheckDependencies ContextMenu
	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			RateTable item = (RateTable) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getRateTableId(), RateTable.class);
		}
	}

	// Command Clone ContextMenu
	public void commandCloneContextMenu() {
		// super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		// RateTable rateTable = (RateTable)
		// nodeSelectEvent.getTreeNode().getData();
		RateTable rateTable = this.rateTable;

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("widgetVar", "dlgTree");
		options.put("width", 500);
		options.put("height", 300);
		options.put("resizable", false);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		Map<String, List<String>> mapPara = new HashMap<String, List<String>>();
		List<String> lstPara = new ArrayList<String>();
		lstPara.add("rateTable;" + rateTable.getRateTableId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("9");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);

	}

	// Move to Category

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_RATE_TABLE, "RateTable", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.rateTable.setCategoryId(cate.getCategoryId());
			if (rateTableDAO.moveToCate(rateTable)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.rateTable, cate, null);
				this.showMessageINFO("common.moveCate", " Success ");
				this.isEditting = false;
			}
		}
	}

	public void refreshCategories(Category category) {
		formType = "list-ratetable-by-category";
		this.category = category;
		loadRateTableByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.rateTableClearFilter').click();");
		loadCategoriesOfRT();
	}

	public void refreshRateTable(RateTable rateTable) {
		formType = "ratetable-detail";
		try {
			this.rateTable = rateTable.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}

		if (rateTable.getUnitTypeId() != null) {
			unitType = unitTypeDAO.get(rateTable.getUnitTypeId());
		} else {
			unitType = null;
		}
		
//		if (validateConvert(genericFormulaModel)) {
//			genericFormulaModel.setUnitType(unitType);
//		}
//		else {
//			genericFormulaModel.setUnitType(null);
//		}

		if (rateTable.getDecisionTableId() != null && rateTable.getDecisionTableId() != 0L) {
			decisionTable = decisionTableDAO.get(rateTable.getDecisionTableId());

		} else {
			decisionTable = new DecisionTable();
		}

		initDecisionTable(decisionTable);

		if (rateTable.getDefaultFormulaId() != null && rateTable.getDefaultFormulaId() != 0L) {
			defaultFormulaModel = new FormulaViewModel(formulaDAO.get(rateTable.getDefaultFormulaId()), true, unitType);
		} else {
			defaultFormulaModel = new FormulaViewModel();
		}

		if (rateTable.getFormulaId() != null && rateTable.getFormulaId() != 0L) {
			stateFormulaModel = new FormulaViewModel(formulaDAO.get(rateTable.getFormulaId()), true, unitType);
		} else {
			stateFormulaModel = new FormulaViewModel();
		}

		List<FormulaViewModel> formulaViewModels = rateTableDAO.findFormulaTable(rateTable);
		tableFormulaModels.clear();
		for (FormulaViewModel formulaViewModel : formulaViewModels) {
			this.tableFormulaModels.put(this.getRowIdByRowIndex(formulaViewModel.getRowIndex()), formulaViewModel);
		}
		previousRowId = -1L;
		loadComboListParameter();
		loadComboListFormulaErrorCode();
		loadComboFormulaUnitConverter();
		loadComboListUnitType();
		loadCategoriesOfRT();
	}

	private Long getRowIdByRowIndex(Long rowIndex) {
		for (RowDt row : rowDts) {
			if (Long.valueOf(row.getRowIndex()) == rowIndex) {
				return row.getRowId();
			}
		}
		return -1L;
	}

	private void initDecisionTable(DecisionTable decisionTable) {
		this.decisionTable = decisionTable;
		initColumns();
		createDynamicColumns();
		initNormValuesMap();
		initRows();
	}

	/**** DECISION TABLE ****/
	private void initColumns() {
		this.columnDts = decisionTableDAO.findColumns(decisionTable);
	}

	private boolean createDynamicColumns() {
		columns.clear();
		for (ColumnDt columnDt : columnDts) {
			if (columnDt.getNormalizerId() == null) {
				return false;
			}
			columns.add(new ColumnModel(columnDt.getColumnName(), columnDt.getNormalizerId()));
		}
		initNormValuesMap();
		return true;
	}

	private void initNormValuesMap() {
		normValuesMap.clear();
		for (ColumnDt columnDt : columnDts) {
			List<NormValue> normValues = decisionTableDAO.findNormValueByNormalizer(columnDt.getNormalizerId());
			normValuesMap.put(columnDt.getNormalizerId(), normValues);
		}
	}

	public void onChangedDecisionTable(ValueChangeEvent event) {
		DecisionTable decisionTable = decisionTableDAO.get((int) event.getNewValue());
		this.initDecisionTable(decisionTable);
	}

	public void clearDecisison() {
		rateTable.setDecisionTableId(null);

		// onChangedDecisionTable(rateTable.getDecisionTableId());
		this.initDecisionTable(new DecisionTable());
	}

	private void initRows() {
		rowDts = decisionTableDAO.findRowByDecisionTable(decisionTable);
		for (int i = 0; i < rowDts.size(); i++) {
			if (decisionTable.getDefaultFormulaIndex() == null) {
				break;
			} else if (decisionTable.getDefaultFormulaIndex() == i) {
				rowDts.get(i).setDefaultValue(true);
				break;
			}
		}
	}

	public void onChangeCheckbox(RowDt rowDt) {
		rowDt.setDefaultValue(true);
		for (RowDt rowDtItem : rowDts) {
			if (rowDtItem != rowDt) {
				rowDtItem.setDefaultValue(false);
			}
		}
	}

	/***** FORMULA *****/

	// Load list Formula Type===============================
	public List<SelectItem> loadComboFormulaType() {
		List<SelectItem> listFormulaType = new ArrayList<SelectItem>();
		listFormulaType = new ArrayList<SelectItem>();
		listFormulaType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.FormulaType.NORMAL_TYPE,
				com.viettel.ocs.constant.Normalizer.FormulaType.NORMAL_TYPE_NAME));
		listFormulaType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.FormulaType.SKIP,
				com.viettel.ocs.constant.Normalizer.FormulaType.SKIP_NAME));
		listFormulaType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.FormulaType.EXIT,
				com.viettel.ocs.constant.Normalizer.FormulaType.EXIT_NAME));
		listFormulaType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.FormulaType.DENY,
				com.viettel.ocs.constant.Normalizer.FormulaType.DENY_NAME));
		listFormulaType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.FormulaType.SORT_TYPE,
				com.viettel.ocs.constant.Normalizer.FormulaType.SORT_TYPE_NAME));
		listFormulaType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.FormulaType.DYNAMIC_RESERVE,
				com.viettel.ocs.constant.Normalizer.FormulaType.DYNAMIC_RESERVE_NAME));
		return listFormulaType;
	}

	// Load list Normalizing Value Type=====================
	public List<SelectItem> loadComboNormalizingValueType() {
		listNormalizingValueType = new ArrayList<SelectItem>();
		listNormalizingValueType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.NONE,
				com.viettel.ocs.constant.Normalizer.NormalizingValueType.NONE_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.MINUTE_TO_SECOND,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.MINUTE_TO_SECOND_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.HOUR_TO_SECOND,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.HOUR_TO_SECOND_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.DAY_TO_SECOND,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.DAY_TO_SECOND_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.WEEK_TO_SECOND,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.WEEK_TO_SECOND_NAME));
		listNormalizingValueType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_MIN,
				com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_MIN_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_HOUR,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_HOUR_NAME));
		listNormalizingValueType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_DAY,
				com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_DAY_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_WEEK,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_WEEK_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_MONTH,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.END_OF_MONTH_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.MBYTE_TO_KBYTE,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.MBYTE_TO_KBYTE_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.GBYTE_TO_KBYTE,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.GBYTE_TO_KBYTE_NAME));
		listNormalizingValueType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.CURR_TIME,
				com.viettel.ocs.constant.Normalizer.NormalizingValueType.CURR_TIME_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.HOURS_FROM_CURR_TIME,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.HOURS_FROM_CURR_TIME_NAME));
		listNormalizingValueType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizingValueType.HOURS_FROM_START_OF_DAY,
						com.viettel.ocs.constant.Normalizer.NormalizingValueType.HOURS_FROM_START_OF_DAY_NAME));

		return listNormalizingValueType;
	}

	// Load list Parameter========================
	private void loadComboListParameter() {
		ParameterDAO parameterDAO = new ParameterDAO();
		listComboParameter = parameterDAO.findAll("");
	}

	private void loadComboListUnitType() {
		UnitTypeDAO unitTypeDAO = new UnitTypeDAO();
		listComboUnitType = unitTypeDAO.findAll("");
	}

	public void onChangeUnitType() {
		unitType = unitTypeDAO.get(rateTable.getUnitTypeId());
	}

	private void loadComboFormulaUnitConverter() {
		FormulaUnitConverterDAO formulaUnitConverterDAO = new FormulaUnitConverterDAO();
		listFormulaUnitConverter = formulaUnitConverterDAO.findAllFormulaUnitConverter();
	}

	public void commandApplyFormula() {
		if (validateFormula()) {
			if (validateConvert(genericFormulaModel)) {
				genericFormulaModel.setUnitType(unitType);
			}
			else {
				genericFormulaModel.setUnitType(null);
			}
			genericFormulaModel.doChooseValue();
			genericFormulaModel.convertToTemplateBits();
			genericFormulaModel.getFormula().setTriggerIds(joinIdTrigger());
			genericFormulaModel.getFormula().setStatisticItems(joinIdStatisticItem());
			genericFormulaModel.setApplied(true);

			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("$('.ui-dialog-titlebar-close').click();");
			context.update("form-ratetable-detail:defaultFormula");
			context.update("form-ratetable-detail:dtRows");
			context.update("form-ratetable-detail:pnStateFormula");
			
			
//			context.update("form-ratetable-detail:pnStateFormula form-ratetable-detail:dtRows form-ratetable-detail:defaultFormula");
		}
	}

	public void commandCancelFormula() {
		try {
			if (!genericFormulaModel.isApplied()) {
				tableFormulaModels.remove(previousRowId);
			} else {
				genericFormulaModel = genericFormulaModelForCancel.clone();
			}
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	private boolean validateFormula() {
		boolean result = true;
		if (genericFormulaModel.getType() == true
				&& (genericFormulaModel.getPramType() == null || genericFormulaModel.getPramType() == 0L)) {
			this.showMessageWARN("", "", this.readProperties("common.chooseParameter"));
			result = false;
		}
		if (genericFormulaModel.getA() == true
				&& (genericFormulaModel.getPramA() == null || genericFormulaModel.getPramA() == 0L)) {
			this.showMessageWARN("", "", this.readProperties("common.chooseParameter"));
			result = false;
		}
		if (genericFormulaModel.getB() == true
				&& (genericFormulaModel.getPramB() == null || genericFormulaModel.getPramB() == 0L)) {
			this.showMessageWARN("", "", this.readProperties("common.chooseParameter"));
			result = false;
		}
		if (genericFormulaModel.getPer() == true
				&& (genericFormulaModel.getPramPer() == null || genericFormulaModel.getPramPer() == 0L)) {
			this.showMessageWARN("", "", this.readProperties("common.chooseParameter"));
			result = false;
		}

		if (genericFormulaModel.getA() == false && genericFormulaModel.getTemA() == null) {
			this.showMessageWARN(" A ", this.readProperties("validate.fieldNull"));
			result = false;
		}
		if (genericFormulaModel.getB() == false && genericFormulaModel.getTemB() == null) {
			this.showMessageWARN(" B ", this.readProperties("validate.fieldNull"));
			result = false;
		}

		if (genericFormulaModel.getPer() == false && genericFormulaModel.getTemPer() == null) {
			this.showMessageWARN(" Per ", this.readProperties("validate.fieldNull"));
			result = false;
		}

		if (genericFormulaModel.getPer() == false && genericFormulaModel.getTemPer() != null
				&& genericFormulaModel.getTemPer() <= 0) {
			this.showMessageWARN(" Per ", this.readProperties("validate.checkValueLess"));
			result = false;
		}

		return result;
	}
	
//	public void notChooseParameter() {
//		listComboParameter.clear();
//		genericFormulaModel.setPramA(null);
//		RequestContext.getCurrentInstance().update("form-ratetable-detail:parameter_a");
//	}

	private String joinIdTrigger() {
		StringBuffer ids = new StringBuffer();
		for (TriggerOcs item : listTriggerOcs) {
			ids.append("," + item.getTriggerOcsId());
		}
		if (listTriggerOcs.size() > 0) {
			ids = new StringBuffer(ids.substring(1));
		}
		return ids.toString();
	}
	
	private String joinIdStatisticItem() {
		StringBuffer ids = new StringBuffer();
		for (StatisticItem item : lstStatisticItem) {
			ids.append("," + item.getStatisticItemId());
		}
		if (lstStatisticItem.size() > 0) {
			ids = new StringBuffer(ids.substring(1));
		}
		return ids.toString();
	}

	int rowIndex;

	public String getStringColorNormValue(String value, Long normalizerId) {
		List<NormValue> normValues = normValuesMap.get(normalizerId);
		for (NormValue normValue : normValues) {
			if (value != null && !value.isEmpty() && !value.equals("-")
					&& normValue.getValueId() == Long.valueOf(value)) {
				return "color: " + normValue.getColor() + "; background-color: " + normValue.getColorBG();
			}
		}
		return "";
	}

	public String getNormName(String value, Long normalizerId) {
		List<NormValue> normValues = normValuesMap.get(normalizerId);
		for (NormValue normValue : normValues) {
			if (value != null && !value.equals("-") && normValue.getValueId() == Long.valueOf(value)) {
				return normValue.getValueName();
			}
		}
		return "(" + readProperties("common.none") + ")";
	}

	/***** TRIGGER OCS *****/
	public void deleteTriggerOcs(TriggerOcs item) {
		listTriggerOcs.remove(item);
	}
	
	public void deleteStatisticItem(StatisticItem item) {
		lstStatisticItem.remove(item);
	}

	/***** FORMULA ERROR CODE *****/
	public void showDialogFEC() {
		this.fErrorCode = new FormulaErrorCode();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgFECWR').show();");
	}

	public void closeDialogFEC() {
		this.fErrorCode = new FormulaErrorCode();
	}

	// Load list Formula Error Code========================
	private void loadComboListFormulaErrorCode() {
		FormulaErrorCodeDAO formulaErrorCodeDAO = new FormulaErrorCodeDAO();
		listComboFormulaErrorCode = formulaErrorCodeDAO.findAllFormulaErrorCode();
		// for (FormulaErrorCode errorCode : listComboFormulaErrorCode) {
		// error.put(errorCode.getDescription(), errorCode.getErrorCode());
		// }
	}

	public void cmdApplyFEC() {
		if (validateFEC()) {
			fErrorCodeDAO.saveOrUpdate(fErrorCode);
			genericFormulaModel.getFormula().setFormulaErrorCode(fErrorCode.getFormulaErrorCodeId());
			listComboFormulaErrorCode.add(fErrorCode);
			this.showMessageINFO("common.save", "Formula Error Code");
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgFECWR').hide();");
			loadComboListFormulaErrorCode();
		}
	}

	// Validate
	private boolean validateFEC() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(fErrorCode.getDescription())) {
			this.showMessageWARN("baltype.balTypeName", super.readProperties("validate.fieldNull"));
			result = false;
		} else if (fErrorCode.getErrorCode() == null) {
			this.showMessageWARN("fErrorCode.errorCode", super.readProperties("validate.fieldNull"));
			result = false;
		} else if (fErrorCodeDAO.checkName(fErrorCode, fErrorCode.getErrorCode())) {
			this.showMessageWARN("fErrorCode.errorCode", super.readProperties("validate.checkValueExist"));
			result = false;
		} else if (fErrorCodeDAO.checkNameDes(fErrorCode, fErrorCode.getDescription())) {
			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	/***** GET SET *****/
	public RateTable getRateTable() {
		return rateTable;
	}

	public void setRateTable(RateTable rateTable) {
		this.rateTable = rateTable;
	}

	public List<RateTable> getListRateTableByCategory() {
		return listRateTableByCategory;
	}

	public void setListRateTableByCategory(List<RateTable> listRateTableByCategory) {
		this.listRateTableByCategory = listRateTableByCategory;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<SelectItem> getListState() {
		return listState;
	}

	public void setListState(List<SelectItem> listState) {
		this.listState = listState;
	}

	public DecisionTable getDecisionTable() {
		return decisionTable;
	}

	public void setDecisionTable(DecisionTable decisionTable) {
		this.decisionTable = decisionTable;
	}

	public RateTableDAO getRateTableDAO() {
		return rateTableDAO;
	}

	public void setRateTableDAO(RateTableDAO rateTableDAO) {
		this.rateTableDAO = rateTableDAO;
	}

	public List<SelectItem> getListComboDecisionTable() {
		return listComboDecisionTable;
	}

	public void setListComboDecisionTable(List<SelectItem> listComboDecisionTable) {
		this.listComboDecisionTable = listComboDecisionTable;
	}

	public List<SelectItem> getListNormalizingValueType() {
		return listNormalizingValueType;
	}

	public void setListNormalizingValueType(List<SelectItem> listNormalizingValueType) {
		this.listNormalizingValueType = listNormalizingValueType;
	}

	public List<FormulaErrorCode> getListComboFormulaErrorCode() {
		return listComboFormulaErrorCode;
	}

	public void setListComboFormulaErrorCode(List<FormulaErrorCode> listComboFormulaErrorCode) {
		this.listComboFormulaErrorCode = listComboFormulaErrorCode;
	}

	public List<SelectItem> getListComboTriggerOcs() {
		return listComboTriggerOcs;
	}

	public void setListComboTriggerOcs(List<SelectItem> listComboTriggerOcs) {
		this.listComboTriggerOcs = listComboTriggerOcs;
	}

	public List<RateTableResult> getListRateTableResult() {
		return listRateTableResult;
	}

	public void setListRateTableResult(List<RateTableResult> listRateTableResult) {
		this.listRateTableResult = listRateTableResult;
	}

	public RateTableResult getRateTableResult() {
		return rateTableResult;
	}

	public void setRateTableResult(RateTableResult rateTableResult) {
		this.rateTableResult = rateTableResult;
	}

	// CONST

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public List<Parameter> getListComboParameter() {
		return listComboParameter;
	}

	public void setListComboParameter(List<Parameter> listComboParameter) {
		this.listComboParameter = listComboParameter;
	}

	public FormulaViewModel getGenericFormulaModel() {
		return genericFormulaModel;
	}

	public void setGenericFormulaModel(FormulaViewModel genericFormulaModel) {
		this.genericFormulaModel = genericFormulaModel;
	}

	public FormulaViewModel getDefaultFormulaModel() {
		return defaultFormulaModel;
	}

	public void setDefaultFormulaModel(FormulaViewModel defaultFormulaModel) {
		this.defaultFormulaModel = defaultFormulaModel;
	}

	public FormulaViewModel getStateFormulaModel() {
		return stateFormulaModel;
	}

	public void setStateFormulaModel(FormulaViewModel stateFormulaModel) {
		this.stateFormulaModel = stateFormulaModel;
	}

	public Map<Long, FormulaViewModel> getTableFormulaModels() {
		return tableFormulaModels;
	}

	public void setTableFormulaModels(Map<Long, FormulaViewModel> tableFormulaModels) {
		this.tableFormulaModels = tableFormulaModels;
	}

	public HashMap<Long, List<NormValue>> getNormValuesMap() {
		return normValuesMap;
	}

	public void setNormValuesMap(HashMap<Long, List<NormValue>> normValuesMap) {
		this.normValuesMap = normValuesMap;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

	public List<RowDt> getRowDts() {
		return rowDts;
	}

	public void setRowDts(List<RowDt> rowDts) {
		this.rowDts = rowDts;
	}

	public List<TriggerOcs> getListTriggerOcs() {
		return listTriggerOcs;
	}

	public void setListTriggerOcs(List<TriggerOcs> listTriggerOcs) {
		this.listTriggerOcs = listTriggerOcs;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public HashMap<String, Long> getError() {
		return error;
	}

	public void setError(HashMap<String, Long> error) {
		this.error = error;
	}

	public List<FormulaUnitConverter> getListFormulaUnitConverter() {
		return listFormulaUnitConverter;
	}

	public void setListFormulaUnitConverter(List<FormulaUnitConverter> listFormulaUnitConverter) {
		this.listFormulaUnitConverter = listFormulaUnitConverter;
	}

	public FormulaErrorCode getfErrorCode() {
		return fErrorCode;
	}

	public void setfErrorCode(FormulaErrorCode fErrorCode) {
		this.fErrorCode = fErrorCode;
	}

	public List<UnitType> getListComboUnitType() {
		return listComboUnitType;
	}

	public void setListComboUnitType(List<UnitType> listComboUnitType) {
		this.listComboUnitType = listComboUnitType;
	}

	public List<Category> getCategoriesOfRT() {
		return categoriesOfRT;
	}

	public void setCategoriesOfRT(List<Category> categoriesOfRT) {
		this.categoriesOfRT = categoriesOfRT;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public List<StatisticItem> getLstStatisticItem() {
		return lstStatisticItem;
	}

	public void setLstStatisticItem(List<StatisticItem> lstStatisticItem) {
		this.lstStatisticItem = lstStatisticItem;
	}
	
	
}

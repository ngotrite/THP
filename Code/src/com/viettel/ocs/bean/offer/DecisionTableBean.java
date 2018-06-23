package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.ColumnDtDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.ColumnDt;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RowDt;
import com.viettel.ocs.model.ColumnModel;
import com.viettel.ocs.model.ComboboxModel;
import com.viettel.ocs.util.CommonUtil;

/**
 * Decision Table
 * 
 * @author THANHND
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "decisionTableBean")
@ViewScoped
public class DecisionTableBean extends BaseController implements Serializable {

	private static final String EMPTY_VALUE = "-";

	// List decision table
	private List<DecisionTable> decisionTables;
	private Category category;
	private String formType = "";

	// Decision table
	private DecisionTable decisionTable;
	private List<Normalizer> normalizers;
	private List<ColumnDt> columnDts;
	private List<RowDt> rowDts;
	private List<ColumnModel> columns;
	private List<ColumnModel> _columns;

	private List<ComboboxModel> comboboxModels;

	// Decision table cache
	private List<ColumnDt> _columnDts;
	private ColumnDt columnDt_Selected;

	private DecisionTableDAO decisionTableDAO;
	private CategoryDAO categoryDAO;
	private ColumnDtDAO columnDtDAO;
	private NormalizerDAO normalizerDAO;
	private RateTableDAO rateTableDAO;
	private Boolean editable;

	private DecisionTable selectedDecisionContext;

	private HashMap<Long, List<NormValue>> normValuesMap;

	private List<Category> categories;

	@PostConstruct
	public void init() {
		this.decisionTables = new ArrayList<DecisionTable>();
		this.decisionTable = new DecisionTable();
		this.selectedDecisionContext = new DecisionTable();
		this.category = new Category();
		this.normalizers = new ArrayList<Normalizer>();
		this.columnDts = new ArrayList<ColumnDt>();
		this.rowDts = new ArrayList<RowDt>();
		this.columns = new ArrayList<ColumnModel>();
		this._columns = new ArrayList<ColumnModel>();
		this.categories = new ArrayList<Category>();

		this.comboboxModels = new ArrayList<ComboboxModel>();

		this.normValuesMap = new HashMap<Long, List<NormValue>>();

		this.decisionTableDAO = new DecisionTableDAO();
		this.categoryDAO = new CategoryDAO();
		this.columnDtDAO = new ColumnDtDAO();
		this.normalizerDAO = new NormalizerDAO();
		this.rateTableDAO = new RateTableDAO();
	}

	private void createColumnDtsSavePoint() {
		try {
			_columnDts = cloneListColumnDt(columnDts);
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	private void loadColumnDtsSavePoint() {
		try {
			columnDts = cloneListColumnDt(_columnDts);
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	private void createColumnModelSavePoint() {
		try {
			_columns = cloneListColumnModel(columns);
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	private void loadColumnModelSavePoint() {
		try {
			columns = cloneListColumnModel(_columns);
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	// BUSINESS METHOD
	public void refreshDecisionTable(DecisionTable decisionTable) {
		formType = "form-decisiontable-detail";
		this.decisionTable = decisionTable.clone();
		this.category = categoryDAO.get(decisionTable.getCategoryId());
		this.normalizers = decisionTableDAO.findNormalizers(decisionTable);
		initColumns();
		createDynamicColumns();
		initNormValuesMap();
		initRows();
		createColumnDtsSavePoint();
		loadCategories();
		editable = false;
	}

	public void refreshCategories(Category parentCategory) {
		formType = "form-decisiontable-list";
		this.category = parentCategory;
		decisionTables = decisionTableDAO.findByCategoryId(category);
		clearFilter();
	}

	private void initColumns() {
		this.columnDts = decisionTableDAO.findColumns(decisionTable);
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

	private boolean createDynamicColumns() {
		columns.clear();
		for (ColumnDt columnDt : columnDts) {
			if (columnDt.getNormalizerId() == null) {
				loadColumnModelSavePoint();
				return false;
			}
			columns.add(new ColumnModel(columnDt.getColumnName(), columnDt.getNormalizerId()));
		}
		createColumnDtsSavePoint();
		createColumnModelSavePoint();
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

	public void addRowColumn() {
		columnDts.add(new ColumnDt());
	}

	public void deleteColumn(ColumnDt columnDt) {
		columnDts.remove(columnDt);
	}

	public void deleteRow(RowDt rowDt) {
		rowDts.remove(rowDt);
	}

	public void upRow(RowDt rowDt) {
		int index = rowDts.indexOf(rowDt);
		if (index > 0) {
			try {
				RowDt preRowDt = rowDts.get(index - 1).clone();
				rowDts.set(index - 1, rowDt.clone());
				rowDts.set(index, preRowDt.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void downRow(RowDt rowDt) {
		int index = rowDts.indexOf(rowDt);
		if (index < rowDts.size() - 1 && index > -1) {
			try {
				RowDt preRowDt = rowDts.get(index + 1).clone();
				rowDts.set(index + 1, rowDt.clone());
				rowDts.set(index, preRowDt.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void upColumn(ColumnDt columnDt) {
		int index = columnDts.indexOf(columnDt);
		if (index > 0) {
			try {
				ColumnDt preColumn = columnDts.get(index - 1).clone();
				columnDts.set(index - 1, columnDt.clone());
				columnDts.set(index, preColumn.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void downColumn(ColumnDt columnDt) {
		int index = columnDts.indexOf(columnDt);
		if (index < columnDts.size() - 1 && index > -1) {
			try {
				ColumnDt nextColumn = columnDts.get(index + 1).clone();
				columnDts.set(index + 1, columnDt.clone());
				columnDts.set(index, nextColumn.clone());
			} catch (CloneNotSupportedException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	public void applyChangeColumn() {
		if (createDynamicColumns()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgAddColumn').hide();");
			if (columnDts.size() == 0) {
				rowDts.clear();
			}
			updateValueRow();
		} else {
			super.showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "decisio_table.normalizer_id_not_null");
		}
	}

	private void updateValueRow() {
		for (RowDt rowDt : rowDts) {
			int longer = columnDts.size()
					- (rowDt.getValue() == null || rowDt.getValue().isEmpty() ? 0 : rowDt.getValue().split("/").length);
			StringBuffer bonus = new StringBuffer(rowDt.getValue());
			if (longer < 0) {
				for (int i = 0; i < -longer; i++) {
					if (bonus.lastIndexOf("/") > 0) {
						bonus = new StringBuffer(bonus.substring(0, bonus.lastIndexOf("/")));
					} else {
						bonus = new StringBuffer();
						break;
					}
				}
			} else if (longer > 0) {
				int columnSize = columnDts.size();
				for (int i = columnSize - longer; i < columnSize; i++) {
					List<NormValue> normValues = normValuesMap.get(columns.get(i).getNormalizerId());
					if (normValues.size() > 0) {
						if (bonus.length() > 0) {
							bonus.append("/" + normValues.get(0).getValueId());
						} else {
							bonus.append(normValues.get(0).getValueId());
						}
					} else {
						bonus.append(i == columnSize ? "-" : "-/");
					}
				}
			}
			rowDt.setValue(bonus.toString());
		}
	}

	public void cancelChangeColumn() {
		loadColumnDtsSavePoint();
		loadColumnModelSavePoint();
	}

	public void addRowDecition() {
		StringBuffer value = new StringBuffer("");
		for (int i = 0; i < columns.size(); i++) {
			List<NormValue> normValues = normValuesMap.get(columns.get(i).getNormalizerId());
			if (normValues.size() > 0) {
				value.append(i == columns.size() - 1 ? normValues.get(0).getValueId()
						: normValues.get(0).getValueId() + "/");
			} else {
				value.append(i == columns.size() - 1 ? "-" : "-/");
			}

		}
		rowDts.add(new RowDt(0, value.toString(), null));
	}

	public void applyChangeRow() {
		String msg = validateCombined();
		if (msg != null) {
			super.showNotification(FacesMessage.SEVERITY_WARN, super.readProperties("common.summary.warning"), msg);
			return;
		}
		if (validateDefaultValue() != null) {
			super.showNotification(FacesMessage.SEVERITY_WARN, super.readProperties("common.summary.error"),
					super.readProperties("decisontable.defaultValueError"));
			return;
		} else {
			List<DecisionTable> lstDT = decisionTableDAO.loadDecisionTableByDecisionTableName(
					decisionTable.getDecisionTableId(), decisionTable.getDecisionTableName());
			if (lstDT != null && lstDT.size() > 0) {
				this.showMessageWARN("common.save", this.readProperties("title.decisionTable"),
						this.readProperties("decisiontable.doubleName"));
				return;
			} else {
				if (columnDtDAO.saveColumnsOfDecision(columnDts, rowDts, decisionTable)) {
					Category cat = categoryDAO.get(decisionTable.getCategoryId());
					this.normalizers = decisionTableDAO.findNormalizers(decisionTable);
					super.getTreeOfferBean().updateTreeNode(decisionTable, cat, normalizers);
					this.showMessageINFO("common.save", " decision table ");
					editable = false;
				} else {
					this.showMessageWARN("common.save", " decision table ");
				}
			}
		}
	}

	private String validateDefaultValue() {
		if (rowDts == null || rowDts.size() == 0) {
			return null;
		}
		for (RowDt rowDt : rowDts) {
			if (rowDt.getDefaultValue()) {
				return null;
			}
		}
		return "fail";
	}

	public void cancelChangeRow() {
		refreshDecisionTable(decisionTable);
	}

	public void addNewDecisionTable() {
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.decisionTable"));
		DecisionTable decisionTable = new DecisionTable();
		decisionTable.setCategoryId(category.getCategoryId());
		refreshDecisionTable(decisionTable);
		editable = true;
	}

	public void onChangedNormValue() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String newValue = externalContext.getRequestParameterMap().get("newValue");
		Integer rowIndex = Integer.valueOf(externalContext.getRequestParameterMap().get("rowIndex"));
		int colIndex = Integer.valueOf(externalContext.getRequestParameterMap().get("colIndex"));

		RowDt rowDt = rowDts.get(rowIndex);

		String[] value = rowDt.getValue().split("/");
		if (colIndex < value.length) {
			value[colIndex] = newValue.isEmpty() ? EMPTY_VALUE : newValue;
			rowDt.setValue(String.join("/", value));
		} else {
			String extra = "";
			for (int i = value.length; i < colIndex; i++) {
				extra += EMPTY_VALUE + "/";
			}
			rowDt.setValue(String.join("/", value) + "/" + extra + newValue);
		}
	}

	private void showDialog() {
		super.openTreeOfferDialog(TreeType.OFFER_NORMALIZER, CategoryType.OFF_NOM_NORMALIZER_NAME, 0, false, null);
	}

	public void onDialogReturn(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Normalizer) {
			Normalizer normalizer = (Normalizer) obj;
			columnDt_Selected.setNormalizerId(normalizer.getNormalizerId());
			columnDt_Selected.setColumnName(normalizer.getNormalizerName());
		}
	}

	public void chosseNormalizer(ColumnDt columnDt) {
		columnDt_Selected = columnDt;
		showDialog();
	}

	public void editDecisionTable(DecisionTable decisionTable) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		// treeOfferBean.setDecisionTableProperties(false, null, decisionTable);
		treeOfferBean.setContentTitle(super.readProperties("title.decisionTable"));
		refreshDecisionTable(decisionTable);
		editable = true;
	}

	public void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('wgDecision')");
	}

	public void deleteDecisionTable(DecisionTable decisionTable) {
		if (rateTableDAO.countRateTableByDecisionTableId(decisionTable.getDecisionTableId()) > 0) {
			this.showMessageWARN("common.delete", this.readProperties("decisontable.name"),
					this.readProperties("decisontable.notDelete"));
		} else {
			if (rateTableDAO.countRateTableByDecisionTableId(decisionTable.getDecisionTableId()) > 0) {
				this.showMessageWARN("common.delete", this.readProperties("decisontable.name"),
						this.readProperties("decisontable.notDelete"));
			} else {
				try {
					for (int i = 0; i < decisionTables.size(); i++) {
						if (decisionTable.getDecisionTableId() == decisionTables.get(i).getDecisionTableId()) {
							decisionTables.remove(i);
							clearFilter();
							break;
						}
					}
					decisionTableDAO.deleteDecisionAndMapColumns(decisionTable);
					super.getTreeOfferBean().removeTreeNodeAll(decisionTable);
					this.showMessageINFO("common.delete", " decision table ");
				} catch (Exception e) {
					getLogger().warn(e.getMessage(), e);
					this.showMessageWARN("common.delete", " decision table ");
				}
			}
		}
	}

	public void cloneDecisionTable(DecisionTable decisionTable) {
		showDialogClone(decisionTable);
	}
	// Context Menu

	public void editDecisionContext(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		editable = true;
	}

	public void addNewDecisionContext(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		DecisionTable decisionTable = new DecisionTable();
		decisionTable.setCategoryId(this.decisionTable.getCategoryId());
		refreshDecisionTable(decisionTable);
		editable = true;
	}

	public void onDialogContextDecisionReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof DecisionTable) {
			DecisionTable decisionTable = (DecisionTable) event.getObject();
			Category category = categoryDAO.get(decisionTable.getCategoryId());
			List<Normalizer> lstNormalizer = decisionTableDAO.findNormalizers(decisionTable);
			super.getTreeOfferBean().updateTreeNode(decisionTable, category, lstNormalizer);
			// super.getTreeOfferBean().setDecisionTableProperties(false,
			// category, decisionTable);
			super.getTreeOfferBean().hideCategory();
			super.getTreeOfferBean().setContentTitle(super.readProperties("title.decisionTable"));
			refreshDecisionTable(decisionTable);
			showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		DecisionTable decisionTable = (DecisionTable) event.getTreeNode().getData();
		Object object = decisionTableDAO.upDownObjectInCatWithDomain(decisionTable, "index", isUp);
		if (object instanceof DecisionTable) {
			Category category = categoryDAO.get(decisionTable.getCategoryId());
			DecisionTable nextDecision = (DecisionTable) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(decisionTable, category);
			} else {
				treeOfferBean.moveDownTreeNode(decisionTable, category);
			}
			treeOfferBean.updateTreeNode(nextDecision, category, null);
			if (formType == "form-decisiontable-list" && category.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}
			super.showNotificationSuccsess();
		}
	}

	public void cloneDecisionContext(NodeSelectEvent nodeSelectEvent) {
		DecisionTable decisionTable = (DecisionTable) nodeSelectEvent.getTreeNode().getData();
		showDialogClone(decisionTable);
	}

	private void showDialogClone(DecisionTable decisionTable) {
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
		lstPara.add("decision_table;" + decisionTable.getDecisionTableId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("8");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void removeDecisionTableContext(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getParent().getData();
		if (object instanceof Category) {
			try {
				DecisionTable decisionTable = (DecisionTable) nodeSelectEvent.getTreeNode().getData();
				if (rateTableDAO.countRateTableByDecisionTableId(decisionTable.getDecisionTableId()) > 0) {
					this.showMessageWARN("common.delete", this.readProperties("decisontable.name"),
							this.readProperties("decisontable.notDelete"));
				} else {
					Category category = categoryDAO.get(decisionTable.getCategoryId());
					super.getTreeOfferBean().removeTreeNodeAll(decisionTable);
					decisionTableDAO.deleteDecisionAndMapColumns(decisionTable);
					for (int i = 0; i < decisionTables.size(); i++) {
						if (decisionTable.getDecisionTableId() == decisionTables.get(i).getDecisionTableId()) {
							decisionTables.remove(i);
							if (formType.equals("form-decisiontable-list")) {
								clearFilter();
							}
							break;
						}
					}
					if (formType.equals("form-decisiontable-detail")) {
						// super.getTreeOfferBean().setDecisionTableProperties(false,
						// category, new DecisionTable());
						super.getTreeOfferBean().hideCategory();
						super.getTreeOfferBean().setContentTitle(super.readProperties("title.decisionTable"));
						refreshDecisionTable(new DecisionTable());
					}
					super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				}
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
			}
		} else {
			if (object instanceof RateTable) {
				try {
					RateTable rateTable = (RateTable) object;
					DecisionTable decisionTable = (DecisionTable) nodeSelectEvent.getTreeNode().getData();
					rateTable.setDecisionTableId(null);
					rateTableDAO.saveOrUpdate(rateTable);
					super.getTreeOfferBean().removeTreeNode(decisionTable, rateTable);
					super.getTreeOfferBean().setRateTableProperties(false, null, rateTable, false, false);
					super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				} catch (Exception e) {
					getLogger().warn(e.getMessage(), e);
					super.showNotification(FacesMessage.SEVERITY_ERROR, "common.fail", "common.failEx");
				}
			}
		}

	}

	public void moveTo(NodeSelectEvent nodeSelectEvent) {
		Object object = nodeSelectEvent.getTreeNode().getData();
		if (object instanceof DecisionTable) {
			selectedDecisionContext = (DecisionTable) object;
			super.openTreeCategoryDialog(TreeType.OFFER_DECISION_TABLE, "folder", 0);
		}
	}

	public void onDialogCatMoveToReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof Category) {
			Category category = (Category) object;
			if (category.getCategoryId() != 0L) {
				selectedDecisionContext.setCategoryId(category.getCategoryId());
				if (decisionTableDAO.moveTo(selectedDecisionContext)) {
					List<Normalizer> normalizers = decisionTableDAO.findNormalizers(selectedDecisionContext);
					super.getTreeOfferBean().updateTreeNode(selectedDecisionContext, category, normalizers);

					if (selectedDecisionContext.getDecisionTableId() == this.decisionTable.getDecisionTableId()) {
						decisionTable.setCategoryId(selectedDecisionContext.getCategoryId());
					}

					showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				}
			}
		}
	}

	// End

	public void showDependenciesContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			DecisionTable item = (DecisionTable) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getDecisionTableId(), DecisionTable.class);
		}
	}

	// GET, SET

	public DecisionTable getDecisionTable() {
		return this.decisionTable;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public void setDecisionTable(DecisionTable decisionTable) {
		this.decisionTable = decisionTable;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void loadCategories() {
		categories = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_DT_DECISION_TABLE);
	}

	public List<Normalizer> getNormalizers() {
		return normalizers;
	}

	public void setNormalizers(List<Normalizer> normalizers) {
		this.normalizers = normalizers;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

	public HashMap<Long, List<NormValue>> getNormValuesMap() {
		return normValuesMap;
	}

	public void setNormValuesMap(HashMap<Long, List<NormValue>> normValuesMap) {
		this.normValuesMap = normValuesMap;
	}

	public List<RowDt> getRowDts() {
		return rowDts;
	}

	public void setRowDts(List<RowDt> rowDts) {
		this.rowDts = rowDts;
	}

	public List<DecisionTable> getDecisionTables() {
		return decisionTables;
	}

	public void setDecisionTables(List<DecisionTable> decisionTables) {
		this.decisionTables = decisionTables;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<ColumnDt> getColumnDts() {
		return columnDts;
	}

	public void setColumnDts(List<ColumnDt> columnDts) {
		this.columnDts = columnDts;
	}

	public List<ComboboxModel> getComboboxModels() {
		return comboboxModels;
	}

	public void setComboboxModels(List<ComboboxModel> comboboxModels) {
		this.comboboxModels = comboboxModels;
	}

	// Util

	public String getEMPTY_VALUE() {
		return EMPTY_VALUE;
	}

	private List<ColumnDt> cloneListColumnDt(List<ColumnDt> list) throws CloneNotSupportedException {
		List<ColumnDt> clone = new ArrayList<ColumnDt>();
		for (ColumnDt columnDt : list) {
			clone.add(columnDt.clone());
		}
		return clone;
	}

	private List<ColumnModel> cloneListColumnModel(List<ColumnModel> list) throws CloneNotSupportedException {
		List<ColumnModel> clone = new ArrayList<ColumnModel>();
		for (ColumnModel columnModel : list) {
			clone.add(columnModel.clone());
		}
		return clone;
	}

	public int getLengthStringArray(String[] strings) {
		return strings.length;
	}

	public String getNormalizerName(Long id) {
		Normalizer normalizer = normalizerDAO.get(id != null ? id : -1);
		return normalizer != null ? normalizer.getNodeName() : "";
	}

	public String getNormName(String value, Long normalizerId) {
		List<NormValue> normValues = normValuesMap.get(normalizerId);
		for (NormValue normValue : normValues) {
			if (!value.equals("") && value != null && !value.equals("-")
					&& normValue.getValueId() == Long.valueOf(value)) {
				return normValue.getValueName();
			}
		}
		// if (normValues != null && normValues.size() > 0) {
		// return normValues.get(0).getValueName();
		// } else {
		return "(" + readProperties("common.none") + ")";
		// }
	}

	public String getStringColorNormValue(String value, Long normalizerId) {
		List<NormValue> normValues = normValuesMap.get(normalizerId);
		if (normValues != null) {
			for (NormValue normValue : normValues) {
				if (value != null && !value.isEmpty() && !value.equals("-")
						&& normValue.getValueId() == CommonUtil.getLong(value)) {
					return "color: " + normValue.getColor() + "; background-color: " + normValue.getColorBG();
				}
			}
		}
		return "";
	}

	public void rowValidate() {
		String msg = validateCombined();
		if (msg != null) {
			super.showMessage(FacesMessage.SEVERITY_WARN, super.readProperties("common.summary.warning"), msg);
		} else {
			super.showMessage(FacesMessage.SEVERITY_INFO, super.readProperties("common.summary.info"),
					super.readProperties("decisontable.valid"));
		}
	}

	/**
	 * @author THANHND
	 * @return If all are okey then return null, opposite then return a string
	 *         is not null
	 */
	private String validateCombined() {
		int index = -1;
		for (RowDt rowDt : rowDts) {
			index++;
			if ((rowDt.getValue() == null || rowDt.getValue().isEmpty()) && columns.size() > 0) {
				return readProperties("decisontable.invalid") + " " + (index + 1);
			} else if (rowDt.getValue().split("/").length != columnDts.size()) {
				return readProperties("decisontable.invalid") + " " + (index + 1);
			} else if (rowDt.getValue().contains("-")) {
				return readProperties("decisontable.invalid") + " " + (index + 1);
			} else if (!validateExistOfValue(rowDt.getValue())) {
				return readProperties("decisontable.invalid") + " " + (index + 1);
			}
		}
		return null;
	}

	/**
	 * @author THANHND
	 * @param values
	 *            is string of value combined
	 * @return true if correct or opposite
	 */
	private boolean validateExistOfValue(String values) {
		String[] valueItem = values.split("/");
		for (int i = 0; i < valueItem.length; i++) {
			List<NormValue> normValues = normValuesMap.get(columnDts.get(i).getNormalizerId());
			boolean correct = false;
			for (int j = 0; j < normValues.size(); j++) {
				if (Long.valueOf(valueItem[i]).equals(normValues.get(j).getValueId())) {
					correct = true;
					break;
				}
			}
			if (!correct) {
				return false;
			}
		}
		return true;
	}

	public void onChangeCheckbox(RowDt rowDt) {
		rowDt.setDefaultValue(true);

		for (RowDt rowDtItem : rowDts) {
			if (rowDtItem != rowDt) {
				rowDtItem.setDefaultValue(false);
			}
		}
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

}

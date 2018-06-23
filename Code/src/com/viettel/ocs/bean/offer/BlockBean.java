package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.Normalizer;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.BlockRateTableMapDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.ObjectFieldDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.PcTypeDAO;
import com.viettel.ocs.dao.PriceComponentBlockMapDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.ObjectField;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PcTypeBlockMap;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.PriceComponentBlockMap;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "blockBean")
@ViewScoped
public class BlockBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4164396933438146087L;
	private Block blockUI;
	private List<SelectItem> listItemCategory;
	private List<Block> listBlockByCategory;
	private String formType = "";
	private String txtAffectedObject = "";
	private boolean isEditting;

	private CategoryDAO categoryDAO;
	private BlockDAO blockDAO;
	private SortPriceComponentDAO sortPriceComponentDAO;

	private List<SelectItem> listComboBlockType;

	private List<SelectItem> listComboAffectedObjectType;

	private List<SelectItem> listComboAffectedObject;
	
	private List<SortPriceComponent> blockFilters;

	private long componentType;

	private RateTable itemRateTable;
	private List<RateTable> listRateTable;
	private List<RateTable> listRateTableBasic;
	private List<RateTable> listRateTableDiscount;
	private List<RateTable> listRateTableCondition;

	private List<RateTable> listRateTableBasicOld;
	private List<RateTable> listRateTableDiscountOld;
	private List<RateTable> listRateTableConditionOld;

	private long categoryID;

	public BlockBean() {
		super();
	}

	@PostConstruct
	public void init() {
		this.isEditting = true;
		categoryDAO = new CategoryDAO();
		prepare();
		this.categoryID = 0l;
	}

	public void prepare() {
		blockUI = new Block();
		categoryDAO = new CategoryDAO();
		blockDAO = new BlockDAO();
		sortPriceComponentDAO = new SortPriceComponentDAO();
		listRateTable = new ArrayList<RateTable>();
		listRateTableBasic = new ArrayList<RateTable>();
		listRateTableDiscount = new ArrayList<RateTable>();
		listRateTableCondition = new ArrayList<RateTable>();
		listRateTableBasicOld = new ArrayList<RateTable>();
		listRateTableDiscountOld = new ArrayList<RateTable>();
		listRateTableConditionOld = new ArrayList<RateTable>();
		listComboAffectedObjectType = new ArrayList<>();
		blockFilters = new ArrayList<>();
		componentType = Normalizer.TypeRateTable.BASIC;
	}

	// prepare
	// Load Block type
	public List<SelectItem> loadComboBlockType() {
		listComboBlockType = new ArrayList<SelectItem>();
		listComboBlockType.add(new SelectItem(Normalizer.BlockType.CHARGE, Normalizer.BlockType.CHARGE_NAME));
		listComboBlockType.add(new SelectItem(Normalizer.BlockType.GRANT, Normalizer.BlockType.GRANT_NAME));
		listComboBlockType.add(new SelectItem(Normalizer.BlockType.SET, Normalizer.BlockType.SET_NAME));
		listComboBlockType.add(new SelectItem(Normalizer.BlockType.POLICY, Normalizer.BlockType.POLICY_NAME));
		return listComboBlockType;
	}

	// Load Block Affected Object Type
	public List<SelectItem> loadComboAffectedObjectType() {
		listComboAffectedObjectType = new ArrayList<SelectItem>();
		listComboAffectedObjectType.add(
				new SelectItem(Normalizer.BlockAffectedObjectType.NONE, Normalizer.BlockAffectedObjectType.NONE_NAME));
		listComboAffectedObjectType.add(
				new SelectItem(Normalizer.BlockAffectedObjectType.CUST, Normalizer.BlockAffectedObjectType.CUST_NAME));
		listComboAffectedObjectType.add(
				new SelectItem(Normalizer.BlockAffectedObjectType.SUB, Normalizer.BlockAffectedObjectType.SUB_NAME));
		listComboAffectedObjectType.add(new SelectItem(Normalizer.BlockAffectedObjectType.BALANCE,
				Normalizer.BlockAffectedObjectType.BALANCE_NAME));
		listComboAffectedObjectType.add(new SelectItem(Normalizer.BlockAffectedObjectType.ACMBAL,
				Normalizer.BlockAffectedObjectType.ACMBAL_NAME));
		listComboAffectedObjectType.add(new SelectItem(Normalizer.BlockAffectedObjectType.PARAMETER,
				Normalizer.BlockAffectedObjectType.PARAMETER_NAME));
		listComboAffectedObjectType.add(new SelectItem(Normalizer.BlockAffectedObjectType.GROUP,
				Normalizer.BlockAffectedObjectType.GROUP_NAME));
		listComboAffectedObjectType.add(new SelectItem(Normalizer.BlockAffectedObjectType.OFFER,
				Normalizer.BlockAffectedObjectType.OFFER_NAME));
		listComboAffectedObjectType.add(new SelectItem(Normalizer.BlockAffectedObjectType.OCSMSG,
				Normalizer.BlockAffectedObjectType.OCSMSG_NAME));
		return listComboAffectedObjectType;
	}

	public void changeAffectedObjectType() {
		setTxtAffectedObject("");
		blockUI.setAffectedValue(null);
	}

	// Load Block Affected Object
	public List<SelectItem> loadComboAffectedField(long AffectedObjectType) {
		listComboAffectedObject = new ArrayList<SelectItem>();
		if (AffectedObjectType != 0) {
			ObjectFieldDAO objectFieldDAO = new ObjectFieldDAO();
			List<ObjectField> listObject = objectFieldDAO.findObjectFieldChild(AffectedObjectType);
			if (listObject != null) {
				for (ObjectField objectField : listObject) {
					if (!objectField.getObjectFieldName().equalsIgnoreCase("availableAmount")
							|| blockUI.getBlockType() == 2) {
						listComboAffectedObject.add(
								new SelectItem(objectField.getObjectFieldName(), objectField.getObjectFieldName()));
					}
				}
			}
		}
		return listComboAffectedObject;
	}

	// load list category by type Block
	public List<SelectItem> loadCategory() {
		listItemCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_BLOCK_BLOCK);
		if (listItemCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listItemCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listItemCategory;
	}

	private void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('tblBlockWidgetVar')");
	}

	// load list list Block by categoryId
	public List<Block> loadBlockByCategory(long categoryId) {
		clearFilter();
		listBlockByCategory = new ArrayList<Block>();
		listBlockByCategory = blockDAO.findByCategoryId(categoryId);
		this.categoryID = categoryId;
		return listBlockByCategory;
	}

	// load list all Ratetable by Block
	public void loadRatetableByBlock(long blockId, long componentType) {
		if (componentType == Normalizer.TypeRateTable.BASIC) {
			listRateTableBasic = blockDAO.findRatetableByBlockIdType(blockId, componentType);
			listRateTableBasicOld.addAll(listRateTableBasic);
		} else if (componentType == Normalizer.TypeRateTable.DISCOUNT) {
			listRateTableDiscount = blockDAO.findRatetableByBlockIdType(blockId, componentType);
			listRateTableDiscountOld.addAll(listRateTableDiscount);
		} else if (componentType == Normalizer.TypeRateTable.CONDITION) {
			listRateTableCondition = blockDAO.findRatetableByBlockIdType(blockId, componentType);
			listRateTableConditionOld.addAll(listRateTableCondition);
		}
	}
	
	private void loadSPC() {
		
		blockFilters.clear();
		if (blockUI.getBlockFilterId() != null) {
			SortPriceComponent sortPriceComponent = sortPriceComponentDAO.get(blockUI.getBlockFilterId());
			if (sortPriceComponent != null) {
				blockFilters.add(sortPriceComponent);
			}
		}
	}

	public void changeNameAffectedObject(Block block) {
		if (block.getAffectedObjectType() > 0) {
			if (block.getAffectedObjectType() == Normalizer.BlockAffectedObjectType.BALANCE) {
				BalTypeDAO balTypeDAO = new BalTypeDAO();
				BalType balType = balTypeDAO.get(block.getAffectedValue());
				if (balType != null) {
					setTxtAffectedObject(balType.getBalTypeName());
				} else {
					setTxtAffectedObject("");
				}
			} else if (block.getAffectedObjectType() == Normalizer.BlockAffectedObjectType.ACMBAL) {
				BalTypeDAO balTypeDAO = new BalTypeDAO();
				BalType balType = balTypeDAO.get(block.getAffectedValue());
				if (balType != null) {
					setTxtAffectedObject(balType.getBalTypeName());
				} else {
					setTxtAffectedObject("");
				}
			} else if (block.getAffectedObjectType() == Normalizer.BlockAffectedObjectType.PARAMETER) {
				ParameterDAO parameterDAO = new ParameterDAO();
				Parameter parameter = parameterDAO.get(block.getAffectedValue());
				if (parameter != null) {
					setTxtAffectedObject(parameter.getParameterName());
				} else {
					setTxtAffectedObject("");
				}
			} else if (block.getAffectedObjectType() == Normalizer.BlockAffectedObjectType.OFFER) {
				OfferDAO offerDAO = new OfferDAO();
				Offer offer = offerDAO.get(block.getAffectedValue());
				if (offer != null) {
					setTxtAffectedObject(offer.getOfferName());
				} else {
					setTxtAffectedObject("");
				}
			}
		}
	}

	public void refeshBlock(Block block) {
		setFormType("detail-block");
		try {
			setBlockUI(block.clone());
			changeNameAffectedObject(block);
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}

		loadSPC();
		loadRatetableByBlock(block.getBlockId(), com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC);
		loadRatetableByBlock(block.getBlockId(), com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT);
		loadRatetableByBlock(block.getBlockId(), com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION);
	}

	// Action
	public void commandChooseObject(long affectedObjectType) {
		if (affectedObjectType == Normalizer.BlockAffectedObjectType.BALANCE) {
			showDialogBalance();
		} else if (affectedObjectType == Normalizer.BlockAffectedObjectType.ACMBAL) {
			showDialogACMBalance();
		} else if (affectedObjectType == Normalizer.BlockAffectedObjectType.PARAMETER) {
			showDialogParameter();
		} else if (affectedObjectType == Normalizer.BlockAffectedObjectType.OFFER) {
			showDialogOffer();
		}
	}

	public void commandNotChooseObject() {
		blockUI.setAffectedObjectType(null);
	}

	public void commandAddNew() {
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.block"));
		this.isEditting = true;
		prepare();
		blockUI.setCategoryId(categoryID);
		blockUI.setBlockType((long) Normalizer.BlockType.CHARGE);
		setFormType("detail-block");
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
	}

	public void commandEdit() {
		this.isEditting = false;
	}

	public void commandCancel() {
		setFormType("list-block-by-category");
		this.isEditting = true;
	}

	public void commandApply() {
		List<RateTable> listRateTable = new ArrayList<RateTable>();
		if (validateBlock()) {
			commandCleanTable();
			if (blockUI.getAffectedField() != null && blockUI.getAffectedField().equalsIgnoreCase("none")) {
				blockUI.setAffectedField(null);
			}
			blockDAO.saveListRatetableBlock(blockUI, listRateTableBasic, listRateTableBasicOld, listRateTableDiscount,
					listRateTableDiscountOld, listRateTableCondition, listRateTableConditionOld);
			listRateTableBasicOld.clear();
			listRateTableBasicOld.addAll(listRateTableBasic);
			listRateTableDiscountOld.clear();
			listRateTableDiscountOld.addAll(listRateTableDiscount);
			listRateTableConditionOld.clear();
			listRateTableConditionOld.addAll(listRateTableCondition);
			updateListAndTree(listRateTable);
			this.showMessageINFO("validate.saveSuccess", super.readProperties(""));
			this.isEditting = false;
		}
	}

	public void updateListAndTree(List<RateTable> listRateTable) {
		Category cat = categoryDAO.get(blockUI.getCategoryId());
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.updateTreeNodeBlock(blockUI, cat, listRateTableBasic, listRateTableDiscount,
				listRateTableCondition);
		this.isEditting = true;
	}

	public void commandAddNewRatetable() {
		RateTable itemNew = new RateTable();
		itemNew.setRateTableName("Choose Ratetable");
		showDialogRatetable(itemNew);
	}

	@ManagedProperty("#{rateTableBean}")
	private RateTableBean rateTableBean;

	public void setRateTableBean(RateTableBean rateTableBean) {
		this.rateTableBean = rateTableBean;
	}

	public void commandEditRatetable(RateTable item) {
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.rateTable"));
		super.getTreeOfferBean().setRateTableProperties(false, item.getCategoryId(), item, true, false);
	}

	public void commandUpOnMap(RateTable item) {
		switch ((int) getComponentType()) {
		case 1:
			int indexBasic = listRateTableBasic.indexOf(item);
			if (indexBasic > 0) {
				RateTable itemBefore = listRateTableBasic.get(indexBasic - 1);
				listRateTableBasic.set(indexBasic - 1, item);
				listRateTableBasic.set(indexBasic, itemBefore);
			}
			break;
		case 2:
			int indexDiscount = listRateTableDiscount.indexOf(item);
			if (indexDiscount > 0) {
				RateTable itemBefore = listRateTableDiscount.get(indexDiscount - 1);
				listRateTableDiscount.set(indexDiscount - 1, item);
				listRateTableDiscount.set(indexDiscount, itemBefore);
			}
			break;
		case 3:
			int indexCondition = listRateTableCondition.indexOf(item);
			if (indexCondition > 0) {
				RateTable itemBefore = listRateTableCondition.get(indexCondition - 1);
				listRateTableCondition.set(indexCondition - 1, item);
				listRateTableCondition.set(indexCondition, itemBefore);
			}
			break;
		}
	}

	public void commandDownOnMap(RateTable item) {
		switch ((int) getComponentType()) {
		case 1:
			int indexBasic = listRateTableBasic.indexOf(item);
			if (indexBasic < listRateTableBasic.size() && (listRateTableBasic.size() - 1) != indexBasic) {
				RateTable itemAfter = listRateTableBasic.get(indexBasic + 1);
				listRateTableBasic.set(indexBasic + 1, item);
				listRateTableBasic.set(indexBasic, itemAfter);
			}
			break;
		case 2:
			int indexDiscount = listRateTableDiscount.indexOf(item);
			if (indexDiscount < listRateTableDiscount.size() && (listRateTableDiscount.size() - 1) != indexDiscount) {
				RateTable itemAfter = listRateTableDiscount.get(indexDiscount + 1);
				listRateTableDiscount.set(indexDiscount + 1, item);
				listRateTableDiscount.set(indexDiscount, itemAfter);
			}
			break;
		case 3:
			int indexCondition = listRateTableCondition.indexOf(item);
			if (indexCondition < listRateTableCondition.size()
					&& (listRateTableCondition.size() - 1) != indexCondition) {
				RateTable itemAfter = listRateTableCondition.get(indexCondition + 1);
				listRateTableCondition.set(indexCondition + 1, item);
				listRateTableCondition.set(indexCondition, itemAfter);
			}
			break;
		}
	}

	public void commandDeleteOnMap(RateTable item) {
		switch ((int) getComponentType()) {
		case 1:
			listRateTableBasic.remove(item);
			break;
		case 2:
			listRateTableDiscount.remove(item);
			break;
		case 3:
			listRateTableCondition.remove(item);
			break;
		}
	}

	public void commandCleanTable() {
		// Clean Ratetable empty
		if (!listRateTableBasic.isEmpty()) {
			Iterator<RateTable> listNormValues = listRateTableBasic.iterator();
			while (listNormValues.hasNext()) {
				RateTable normValue = listNormValues.next();
				if (normValue.getRateTableId() <= 0) {
					listNormValues.remove();
				}
			}
		}
		if (!listRateTableDiscount.isEmpty()) {
			Iterator<RateTable> listNormValues = listRateTableCondition.iterator();
			while (listNormValues.hasNext()) {
				RateTable normValue = listNormValues.next();
				if (normValue.getRateTableId() <= 0) {
					listNormValues.remove();
				}
			}
		}
		if (!listRateTableCondition.isEmpty()) {
			Iterator<RateTable> listNormValues = listRateTableCondition.iterator();
			while (listNormValues.hasNext()) {
				RateTable normValue = listNormValues.next();
				if (normValue.getRateTableId() <= 0) {
					listNormValues.remove();
				}
			}
		}
	}

	public void changeTypeListRateTable(long type) {
		setComponentType(type);
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		switch (tabView.getActiveIndex()) {
		case 0:
			changeTypeListRateTable(Normalizer.TypeRateTable.BASIC);
			break;
		case 1:
			changeTypeListRateTable(Normalizer.TypeRateTable.DISCOUNT);
			break;
		case 2:
			changeTypeListRateTable(Normalizer.TypeRateTable.CONDITION);
			break;
		default:
			changeTypeListRateTable(Normalizer.TypeRateTable.BASIC);
			break;
		}
	}

	private boolean insert = false;

	public void showDialogRatetable(RateTable item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(listRateTableBasic != null) {
			for (RateTable rt : listRateTableBasic) {
				lstId.add(rt.getRateTableId());
			}	
		}else if (listRateTableDiscount != null) {
			for (RateTable rt : listRateTableDiscount) {
				lstId.add(rt.getRateTableId());
			}	
		}else if (listRateTableCondition != null) {
			for (RateTable rt : listRateTableCondition) {
				lstId.add(rt.getRateTableId());
			}
		}
		
		if (item == null || item.getRateTableId() <= 0){			
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, false, lstId);
		}
		itemRateTable = item;
		insert = false;
	}

	public void insertRatetableAt(RateTable item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(listRateTableBasic != null) {
			for (RateTable rt : listRateTableBasic) {
				lstId.add(rt.getRateTableId());
			}	
		}else if (listRateTableDiscount != null) {
			for (RateTable rt : listRateTableDiscount) {
				lstId.add(rt.getRateTableId());
			}	
		}else if (listRateTableCondition != null) {
			for (RateTable rt : listRateTableCondition) {
				lstId.add(rt.getRateTableId());
			}
		}
		
		if (item == null || item.getRateTableId() <= 0){			
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, false, lstId);
		}
		itemRateTable = item;
		insert = true;
	}

	public void showDialogBalance() {
		super.openTreeCommonDialog(TreeType.CATALOG_BALANCES, CategoryType.CTL_BL_BAL_TYPE_NAME, 0, false, null);
	}

	public void showDialogACMBalance() {
		super.openTreeCommonDialog(TreeType.CATALOG_BALANCE_ACC, CategoryType.CTL_BL_BAL_TYPE_ACC_NAME, 0, false, null);
	}

	public void showDialogOffer() {
		super.openTreeOfferDialog(TreeType.OFFER_TREE_NORMAL_ALL_DLG, CategoryType.OFF_OFFER_NAME, 0, false, null);
	}

	public void showDialogParameter() {
		super.openTreeCommonDialog(TreeType.CATALOG_PARAMETER, CategoryType.CTL_PARAMETER_NAME, 0, false, null);
	}

	public void onDialogReturnTable(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof RateTable) {
				RateTable rateTableChange = (RateTable) obj;
				switch ((int) getComponentType()) {
				case 1:
					if (!existedRTs(rateTableChange, listRateTableBasic)) {
						if (itemRateTable.getRateTableId() != 0L) {
							if (insert) {
								listRateTableBasic.add(listRateTableBasic.indexOf(itemRateTable) + 1, rateTableChange);
							} else {
								listRateTableBasic.set(listRateTableBasic.indexOf(itemRateTable), rateTableChange);
							}
						} else {
							listRateTableBasic.add(rateTableChange);
						}
					} else {
						if (itemRateTable.getRateTableId() == rateTableChange.getRateTableId() && !insert) {
							listRateTableBasic.set(listRateTableBasic.indexOf(itemRateTable), rateTableChange);
						} else {
							this.showMessageWARN("common.summary.warning",
									super.readProperties("common.objAlreadyExists"));
						}
					}
					break;
				case 2:
					if (!existedRTs(rateTableChange, listRateTableDiscount)) {
						if (itemRateTable.getRateTableId() != 0L) {
							if (insert) {
								listRateTableDiscount.add(listRateTableDiscount.indexOf(itemRateTable) + 1,
										rateTableChange);
							} else {
								listRateTableDiscount.set(listRateTableDiscount.indexOf(itemRateTable),
										rateTableChange);
							}
						} else {
							listRateTableDiscount.add(rateTableChange);
						}
					} else {
						if (itemRateTable.getRateTableId() == rateTableChange.getRateTableId() && !insert) {
							listRateTableDiscount.set(listRateTableDiscount.indexOf(itemRateTable), rateTableChange);
						} else {
							this.showMessageWARN("common.summary.warning",
									super.readProperties("common.objAlreadyExists"));
						}
					}

					break;
				case 3:
					if (!existedRTs(rateTableChange, listRateTableCondition)) {
						if (itemRateTable.getRateTableId() != 0L) {
							if (insert) {
								listRateTableCondition.add(listRateTableCondition.indexOf(itemRateTable) + 1,
										rateTableChange);
							} else {
								listRateTableCondition.set(listRateTableCondition.indexOf(itemRateTable),
										rateTableChange);
							}
						} else {
							listRateTableCondition.add(rateTableChange);
						}
					} else {
						if (itemRateTable.getRateTableId() == rateTableChange.getRateTableId() && !insert) {
							listRateTableCondition.set(listRateTableCondition.indexOf(itemRateTable), rateTableChange);
						} else {
							this.showMessageWARN("common.summary.warning",
									super.readProperties("common.objAlreadyExists"));
						}
					}
				default:
					break;
				}
			}
		}
	}

	private boolean existedRTs(RateTable rateTableChange, List<RateTable> rateTables) {
		for (RateTable rateTable : rateTables) {
			if (rateTable.getRateTableId() == rateTableChange.getRateTableId()) {
				return true;
			}
		}
		return false;
	}

	// SelectEvent import org.primefaces.event.SelectEvent
	public void onDialogReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof RateTable) {
				RateTable rateTableChange = (RateTable) obj;
				switch ((int) getComponentType()) {
				case 1:
					if (!existedRTs(rateTableChange, listRateTableBasic)) {
						if (itemRateTable.getRateTableId() != 0L) {
							listRateTableBasic.set(listRateTableBasic.indexOf(itemRateTable), rateTableChange);
						} else {
							listRateTableBasic.add(rateTableChange);
						}
					} else {
						if (itemRateTable.getRateTableId() == rateTableChange.getRateTableId()) {
							listRateTableBasic.set(listRateTableBasic.indexOf(itemRateTable), rateTableChange);
						} else {
							this.showMessageWARN("common.summary.warning",
									super.readProperties("common.objAlreadyExists"));
						}
					}
					break;
				case 2:
					if (!existedRTs(rateTableChange, listRateTableDiscount)) {
						if (itemRateTable.getRateTableId() != 0L) {
							listRateTableDiscount.set(listRateTableDiscount.indexOf(itemRateTable), rateTableChange);
						} else {
							listRateTableDiscount.add(rateTableChange);
						}
					} else {
						if (itemRateTable.getRateTableId() == rateTableChange.getRateTableId()) {
							listRateTableDiscount.set(listRateTableDiscount.indexOf(itemRateTable), rateTableChange);
						} else {
							this.showMessageWARN("common.summary.warning",
									super.readProperties("common.objAlreadyExists"));
						}
					}

					break;
				case 3:
					if (!existedRTs(rateTableChange, listRateTableCondition)) {
						if (itemRateTable.getRateTableId() != 0L) {
							listRateTableCondition.set(listRateTableCondition.indexOf(itemRateTable), rateTableChange);
						} else {
							listRateTableCondition.add(rateTableChange);
						}
					} else {
						if (itemRateTable.getRateTableId() == rateTableChange.getRateTableId()) {
							listRateTableCondition.set(listRateTableCondition.indexOf(itemRateTable), rateTableChange);
						} else {
							this.showMessageWARN("common.summary.warning",
									super.readProperties("common.objAlreadyExists"));
						}
					}
				default:
					break;
				}
			} else if (obj instanceof Block) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode((Block) obj, categoryDAO.get(((Block) obj).getCategoryId()), null);
				commandEditBlock((Block) obj);
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			}
		}
	}

	public void onDialogReturnChooseAffectedObject(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Offer) {
			Offer offer = (Offer) obj;
			blockUI.setAffectedValue(offer.getOfferId());
			setTxtAffectedObject(offer.getOfferName());
		} else if (obj instanceof Parameter) {
			Parameter parameter = (Parameter) obj;
			blockUI.setAffectedValue(parameter.getParameterId());
			setTxtAffectedObject(parameter.getParameterName());
		} else if (obj instanceof BalType) {
			BalType balType = (BalType) obj;
			if (balType.getIsAcm()) {
				blockUI.setAffectedValue(balType.getBalTypeId());
				setTxtAffectedObject(balType.getBalTypeName());
			} else {
				blockUI.setAffectedValue(balType.getBalTypeId());
				setTxtAffectedObject(balType.getBalTypeName());
			}
		}
	}


	public void chooseBlockFilter() {
		super.openTreeOfferDialog(TreeType.OFFER_SORT_PRICE_COMPONENT, readProperties("action.sort_price_component"), 0,
				false, null);
	}
	
	public void notChooseBlockFilter() {
		blockUI.setBlockFilterId(null);
		blockFilters.clear();
		RequestContext.getCurrentInstance().update("form-block-detail:slBlockFilter");
	}
	
	public void onDialogBlockFilterReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof SortPriceComponent) {
			SortPriceComponent sortPriceComponent = (SortPriceComponent) object;
			blockUI.setBlockFilterId(sortPriceComponent.getSortPriceComponentId());
			blockFilters.clear();
			blockFilters.add(sortPriceComponent);
			RequestContext.getCurrentInstance().update("form-block-detail:slBlockFilter");
		}
	}
	
	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_BLOCK, "Block", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.blockUI.setCategoryId(cate.getCategoryId());
			if (blockDAO.moveToCate(blockUI)) {
				blockDAO.saveOrUpdate(this.blockUI);
				this.isEditting = false;
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.blockUI, cate, null);
				this.showMessageINFO("common.moveCate", " Success ");
			}			
		}
	}

	// In table Category
	public void commandEditBlock(Block item) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.block"));
		this.isEditting = true;
		treeOfferBean.hideCategory();
		setFormType("detail-block");
		setBlockUI(item);
		loadRatetableByBlock(item.getBlockId(), com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC);
		loadRatetableByBlock(item.getBlockId(), com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT);
		loadRatetableByBlock(item.getBlockId(), com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION);
	}

	public void commandCloneBlock(Block item) {
		Block block = item;

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
		lstPara.add("block;" + block.getBlockId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("10");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandDeleteBlock(Block item) {
		// Delete Block and Map Block - Rate table
		PriceComponentBlockMapDAO componentBlockMapDAO = new PriceComponentBlockMapDAO();
		if (!componentBlockMapDAO.checkBlockInPC(item.getBlockId())) {
			BlockRateTableMapDAO blockRateTableMapDAO = new BlockRateTableMapDAO();
			blockRateTableMapDAO.deleteBlockRateTableMapByBlockId(item);
			loadBlockByCategory(item.getCategoryId());
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.removeTreeNodeAll(item);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.readProperties("common.delete"),
					this.readProperties("validate.deleteSuccess")));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("block.objectUser"));
		}
	}

	// In Tree
	public void commandAddNewBlockTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		this.isEditting = true;
		prepare();
		Block item = (Block) nodeSelectEvent.getTreeNode().getData();
		if (item != null) {
			blockUI.setCategoryId(item.getCategoryId());
		}
		setFormType("detail-block");
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
	}

	public void commandEditBlockTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		this.isEditting = true;
		treeOfferBean.hideCategory();
		setFormType("detail-block");
	}

	public void commandDeleteBlockTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			Block item = (Block) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			PriceComponentBlockMapDAO componentBlockMapDAO = new PriceComponentBlockMapDAO();
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (object instanceof Category) {
				// Delete Block and Map Block - Rate table
				if (!componentBlockMapDAO.checkBlockInPC(item.getBlockId())) {
					BlockRateTableMapDAO blockRateTableMapDAO = new BlockRateTableMapDAO();
					blockRateTableMapDAO.deleteBlockRateTableMapByBlockId(item);
					treeOfferBean.removeTreeNodeAll(item);
					// setFormType("list-block-by-category");
					setFormType("");
					this.isEditting = true;
					loadBlockByCategory(item.getCategoryId());
					super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("block.objectUser"));
				}
			} else if (object instanceof PriceComponent) {
				// Delete Map PC - Block
				PriceComponent priceComponent = (PriceComponent) object;
				PriceComponentBlockMap priceComponentBlockMap = componentBlockMapDAO
						.findPriceComponentBlock(priceComponent.getPriceComponentId(), item.getBlockId());
				if (priceComponentBlockMap != null) {
					componentBlockMapDAO.delete(priceComponentBlockMap);
					treeOfferBean.removeTreeNode(item, priceComponent);
					this.formType = "";
					treeOfferBean.setPriceComponentProperties(false, priceComponent.getCategoryId(), priceComponent,
							false, false);
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			} else if (object instanceof PcType) {
				
				PcType pcType = (PcType) object;
				PcTypeDAO pcDao = new PcTypeDAO();
				PcTypeBlockMap map = pcDao.findPcTypeBlockMap(pcType.getPcTypeId(), item.getBlockId());
				if(map != null) {
					pcDao.delete(map);
					treeOfferBean.removeTreeNode(item, pcType);
					this.formType = "";
					treeOfferBean.setPcTypeProperties(false, pcType.getCategoryId(), pcType, false);
				}
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
			}
		}
	}

	public boolean commandCheckNodeType(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			if (object instanceof Category) {
				return true;
			}
		}
		return false;
	}

	public void commandCloneBlockTreeOld(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		Block block = (Block) nodeSelectEvent.getTreeNode().getData();

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
		lstPara.add("block;" + block.getBlockId());
		mapPara.put("param", lstPara);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandCloneBlockTree() {
		Block block = this.blockUI;

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
		lstPara.add("block;" + block.getBlockId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("10");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Block item = (Block) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getBlockId(), Block.class);
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	private void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		Block block = (Block) event.getTreeNode().getData();
		Object object = blockDAO.upDownObjectInCatWithDomain(block, "index", isUp);
		if (object instanceof Block) {
			Category category = categoryDAO.get(block.getCategoryId());
			Block nextBlock = (Block) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(block, category);
			} else {
				treeOfferBean.moveDownTreeNode(block, category);
			}
			treeOfferBean.updateTreeNode(nextBlock, category, null);
			if (formType == "list-block-by-category" && category.getCategoryId() == categoryID) {
				loadBlockByCategory(categoryID);
			}
			super.showNotificationSuccsess();
		}
	}

	// CommandUp ContextMenu
	public void moveUpBlock(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Block item = (Block) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				moveUpDownInCat(nodeSelectEvent, true);
			} else if (objParent instanceof PriceComponent) {
				PriceComponent priceComponent = (PriceComponent) objParent;
				PriceComponentBlockMapDAO componentBlockMapDAO = new PriceComponentBlockMapDAO();
				List<PriceComponentBlockMap> lstPriceComponentBlockMap = componentBlockMapDAO
						.findPriceComponentBlockMapByPC(priceComponent.getPriceComponentId());

				if (!lstPriceComponentBlockMap.isEmpty() && lstPriceComponentBlockMap.size() > 1) {
					for (int i = 1; i < lstPriceComponentBlockMap.size(); i++) {
						if (lstPriceComponentBlockMap.get(i).getBlockId() == item.getBlockId()) {
							componentBlockMapDAO.saveMapPCBlock(lstPriceComponentBlockMap.get(i - 1),
									lstPriceComponentBlockMap.get(i));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveUpTreeNode(item, objParent);
							treeOfferBean.setPriceComponentProperties(false, priceComponent.getCategoryId(),
									priceComponent, false, false);
							this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
							break;
						}
					}
				}

			} else if (objParent instanceof PcType) {
				
				PcType pcType = (PcType) objParent;
				PcTypeDAO pcDao = new PcTypeDAO();
				if(pcDao.moveUpDownBlock(pcType, item, true)) {

					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveUpTreeNode(item, pcType);
					treeOfferBean.setPcTypeProperties(false, pcType.getCategoryId(), pcType, false);
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));	
				}				
			}
		}
	}

	// CommandDown ContextMenu
	public void moveDownBlock(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Block item = (Block) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				moveUpDownInCat(nodeSelectEvent, false);
			} else if (objParent instanceof PriceComponent) {
				PriceComponent priceComponent = (PriceComponent) objParent;
				PriceComponentBlockMapDAO componentBlockMapDAO = new PriceComponentBlockMapDAO();
				List<PriceComponentBlockMap> lstPriceComponentBlockMap = componentBlockMapDAO
						.findPriceComponentBlockMapByPC(priceComponent.getPriceComponentId());
				if (!lstPriceComponentBlockMap.isEmpty()) {
					for (int i = 0; i < lstPriceComponentBlockMap.size(); i++) {
						if (lstPriceComponentBlockMap.get(i).getBlockId() == item.getBlockId()
								&& i != (lstPriceComponentBlockMap.size() - 1)) {
							componentBlockMapDAO.saveMapPCBlock(lstPriceComponentBlockMap.get(i),
									lstPriceComponentBlockMap.get(i + 1));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveDownTreeNode(item, objParent);
							treeOfferBean.setPriceComponentProperties(false, priceComponent.getCategoryId(),
									priceComponent, false, false);
							this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
							break;
						}
					}
				}
			} else if (objParent instanceof PcType) {
				
				PcType pcType = (PcType) objParent;
				PcTypeDAO pcDao = new PcTypeDAO();
				if(pcDao.moveUpDownBlock(pcType, item, false)) {

					TreeOfferBean treeOfferBean = super.getTreeOfferBean();
					treeOfferBean.moveDownTreeNode(item, pcType);
					treeOfferBean.setPcTypeProperties(false, pcType.getCategoryId(), pcType, false);
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));	
				}				
			}
		}
	}

	// Add children
	int indexType = 0;

	public void addChildren(NodeSelectEvent nodeSelectEvent, long componentType) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		super.openTreeOfferDialog(TreeType.OFFER_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, 0, true, null);
		setComponentType(componentType);
		setIndexType(Integer.parseInt(String.valueOf(componentType - 1)));
		this.isEditting = true;
	}

	public void onNodeSelect(NodeSelectEvent nodeSelectEvent, long type) {
		Block block = (Block) nodeSelectEvent.getTreeNode().getData();
		if (block.getBlockId() != this.blockUI.getBlockId()) {
			super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		}
		this.isEditting = true;
		this.componentType = type;
	}

	// Validate
	private boolean validateBlock() {
		if (ValidateUtil.checkStringNullOrEmpty(blockUI.getBlockName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			return false;
		}
		if (blockDAO.checkName(blockUI, isEditting)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			return false;
		}
		if (listRateTableBasic.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("block.emptyList")));
			return false;
		}
		return true;
	}

	/** GET-SET **/
	public Block getBlockUI() {
		return blockUI;
	}

	public void setBlockUI(Block blockUI) {
		this.blockUI = blockUI;
	}

	public List<SelectItem> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<SelectItem> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<Block> getListBlockByCategory() {
		return listBlockByCategory;
	}

	public void setListBlockByCategory(List<Block> listBlockByCategory) {
		this.listBlockByCategory = listBlockByCategory;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public List<SelectItem> getListComboBlockType() {
		return listComboBlockType;
	}

	public void setListComboBlockType(List<SelectItem> listComboBlockType) {
		this.listComboBlockType = listComboBlockType;
	}

	public List<SelectItem> getListComboAffectedObjectType() {
		return listComboAffectedObjectType;
	}

	public void setListComboAffectedObjectType(List<SelectItem> listComboAffectedObjectType) {
		this.listComboAffectedObjectType = listComboAffectedObjectType;
	}

	public long getComponentType() {
		return componentType;
	}

	public void setComponentType(long componentType) {
		this.componentType = componentType;
	}

	public RateTable getItemRateTable() {
		return itemRateTable;
	}

	public void setItemRateTable(RateTable itemRateTable) {
		this.itemRateTable = itemRateTable;
	}

	public List<RateTable> getListRateTableBasic() {
		return listRateTableBasic;
	}

	public void setListRateTableBasic(List<RateTable> listRateTableBasic) {
		this.listRateTableBasic = listRateTableBasic;
	}

	public List<RateTable> getListRateTableDiscount() {
		return listRateTableDiscount;
	}

	public void setListRateTableDiscount(List<RateTable> listRateTableDiscount) {
		this.listRateTableDiscount = listRateTableDiscount;
	}

	public List<RateTable> getListRateTableCondition() {
		return listRateTableCondition;
	}

	public void setListRateTableCondition(List<RateTable> listRateTableCondition) {
		this.listRateTableCondition = listRateTableCondition;
	}

	public List<RateTable> getListRateTableBasicOld() {
		return listRateTableBasicOld;
	}

	public void setListRateTableBasicOld(List<RateTable> listRateTableBasicOld) {
		this.listRateTableBasicOld = listRateTableBasicOld;
	}

	public List<RateTable> getListRateTableDiscountOld() {
		return listRateTableDiscountOld;
	}

	public void setListRateTableDiscountOld(List<RateTable> listRateTableDiscountOld) {
		this.listRateTableDiscountOld = listRateTableDiscountOld;
	}

	public List<RateTable> getListRateTableConditionOld() {
		return listRateTableConditionOld;
	}

	public void setListRateTableConditionOld(List<RateTable> listRateTableConditionOld) {
		this.listRateTableConditionOld = listRateTableConditionOld;
	}

	public List<SelectItem> getListComboAffectedObject() {
		return listComboAffectedObject;
	}

	public void setListComboAffectedObject(List<SelectItem> listComboAffectedObject) {
		this.listComboAffectedObject = listComboAffectedObject;
	}

	public String getTxtAffectedObject() {
		return txtAffectedObject;
	}

	public void setTxtAffectedObject(String txtAffectedObject) {
		this.txtAffectedObject = txtAffectedObject;
	}

	public List<RateTable> getListRateTable() {
		return listRateTable;
	}

	public void setListRateTable(List<RateTable> listRateTable) {
		this.listRateTable = listRateTable;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public int getIndexType() {
		return indexType;
	}

	public void setIndexType(int indexType) {
		this.indexType = indexType;
	}

	public List<SortPriceComponent> getBlockFilters() {
		return blockFilters;
	}

	public void setBlockFilters(List<SortPriceComponent> blockFilters) {
		this.blockFilters = blockFilters;
	}

}

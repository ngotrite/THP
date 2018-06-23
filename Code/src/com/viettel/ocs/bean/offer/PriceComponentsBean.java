package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionPriceComponentMapDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.PcTypeDAO;
import com.viettel.ocs.dao.PriceComponentBlockMapDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionPriceComponentMap;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PcTypeBlockMap;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.util.ValidateUtil;

/**
 * @author Nampv
 * @since 28082016
 */

@ManagedBean(name = "priceComponentsBean", eager = true)
@ViewScoped
public class PriceComponentsBean extends BaseController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7463933442949071395L;
	private String formType;
	private PriceComponent priceComponent;
	private List<PriceComponent> lstPriceComponents;
	private List<Category> listCategory;
	private PriceComponentDAO priceComponentDAO;
	private boolean isEdit;
	private long categoryId;
	private String titleBlocks;
	private List<Block> lstSubBlocks;
	private BlockDAO blockDAO;
	private CategoryDAO categoryDAO;
	private boolean editPC;
	private Block block;
	private List<Category> categoriesOfPC;
	private List<PcType> pcTypes;
	private PcTypeDAO pcTypeDAO;
	
	public List<PcType> getPcTypes() {
		return pcTypes;
	}

	public void setPcTypes(List<PcType> pcTypes) {
		this.pcTypes = pcTypes;
	}

	public List<Category> getCategoriesOfPC() {
		return categoriesOfPC;
	}

	public void setCategoriesOfPC(List<Category> categoriesOfPC) {
		this.categoriesOfPC = categoriesOfPC;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public boolean isEditPC() {
		return editPC;
	}

	public void setEditPC(boolean editPC) {
		this.editPC = editPC;
	}

	public List<Block> getLstSubBlocks() {
		return lstSubBlocks;
	}

	public void setLstSubBlocks(List<Block> lstSubBlocks) {
		this.lstSubBlocks = lstSubBlocks;
	}

	public String getTitleBlocks() {
		return titleBlocks;
	}

	public void setTitleBlocks(String titleBlocks) {
		this.titleBlocks = titleBlocks;
	}

	public List<Category> getListCategory() {
		return listCategory;
	}

	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
	}

	public List<SelectItem> getListComboCategory() {
		return listComboCategory;
	}

	public void setListComboCategory(List<SelectItem> listComboCategory) {
		this.listComboCategory = listComboCategory;
	}

	private List<SelectItem> listComboCategory;

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public PriceComponent getPriceComponent() {
		return priceComponent;
	}

	public void setPriceComponent(PriceComponent priceComponent) {
		try {
			this.priceComponent = priceComponent.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	public List<PriceComponent> getLstPriceComponents() {
		return lstPriceComponents;
	}

	public void setLstPriceComponents(List<PriceComponent> lstPriceComponents) {
		this.lstPriceComponents = lstPriceComponents;
	}

	public PriceComponentsBean() {
		init();
	}

	private void init() {
		lstSubBlocks = new ArrayList<Block>();
		listComboCategory = new ArrayList<SelectItem>();
		priceComponent = new PriceComponent();
		lstPriceComponents = new ArrayList<PriceComponent>();
		priceComponentDAO = new PriceComponentDAO();
		titleBlocks = "CURRENT BLOCKS INFORMATION";
		blockDAO = new BlockDAO();
		categoryDAO = new CategoryDAO();
		block = new Block();
		setEditPC(false);
		categoriesOfPC = new ArrayList<Category>();
		pcTypes = new ArrayList<PcType>();
		pcTypeDAO = new PcTypeDAO();
		loadPCTypes();
	}
	
	public void loadPCTypes() {
		pcTypes.clear();
		if (priceComponent.getPcType() != null) {
			PcType pcType = pcTypeDAO.get(priceComponent.getPcType());
			if (pcType != null) {
				pcTypes.add(pcTypeDAO.get(pcType.getPcTypeId()));
			}
		}
	}
	
	public void choosePcType() {
		super.openTreeOfferDialog(TreeType.OFFER_PC_TYPE, readProperties("priceComponent.pcType"), 0, false, null);
	}
	
	public void notChoosePcType() {
		priceComponent.setPcType(null);
		pcTypes.clear();
		RequestContext.getCurrentInstance().update("form-pc-detail:slPCType");
	}
	
	public void onDialogPcTypeReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof PcType) {
			PcType pcType = (PcType) object;
			priceComponent.setPcType(pcType.getPcTypeId());
			pcTypes.clear();
			this.pcTypes.add(pcType);
			RequestContext.getCurrentInstance().update("form-pc-detail:slPCType");
		}
	}

	public List<PriceComponent> getListPriceComponentByCategoryId(Long categoryId) {
		setCategoryId(categoryId);
		//clearFilter();
		return lstPriceComponents = priceComponentDAO.loadPriceComponentByCategpryId(categoryId);
	}

	private void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('tblPCWidgetVar')");
	}

	public void loadCategoriesOfPC() {
		categoriesOfPC = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_PC_PRICE_COMPONENT);
	}
	
	public void getLoadComboListCategory() {
		listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_PC_PRICE_COMPONENT);
		if (listCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				if (category != null) {
					listComboCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
				}
			}
		}
	}

	// ************** SU KIEN LIST PC ****************
	public void btnAddNew() {
		priceComponent = new PriceComponent();
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.priceComponent"));
		setEdit(true);
		setEditPC(true);
		setFormType("form-pc-detail");
		getListBlockByPC();
		loadCategoriesOfPC();  
		//getLoadComboListCategory();
		loadPCTypes();

	}

	public void editPriceComponents(PriceComponent priceComponent) {
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.priceComponent"));
		setEdit(true);
		setEditPC(true);
		setPriceComponent(priceComponent);
		setFormType("form-pc-detail");
		getListBlockByPC();
		//getLoadComboListCategory();     
		loadCategoriesOfPC();
		setCategoryId(priceComponent.getCategoryId());
		loadPCTypes();
	}

	public void clonePriceComponents(PriceComponent priceComponent) {
		PriceComponent priceComponentClone = priceComponent;
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
		lstPara.add("pc;" + priceComponentClone.getPriceComponentId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("11");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void deletePriceComponents(PriceComponent item) {
		// Delete PC and Map PC - Block
		ActionPriceComponentMapDAO actionPriceComponentMapDAO = new ActionPriceComponentMapDAO();
		if (!actionPriceComponentMapDAO.checkPCInAction(item.getPriceComponentId())) {
			PriceComponentBlockMapDAO componentBlockMapDAO = new PriceComponentBlockMapDAO();
			componentBlockMapDAO.deletePCBlockByPCId(item);
			getListPriceComponentByCategoryId(item.getCategoryId());
			setFormType("category");
			this.editPC = true;
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.removeTreeNodeAll(item);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.readProperties("common.delete"),
					this.readProperties("title.priceComponent")));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("priceComponent.used"));
		}
	}

	public void btnAddNew(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		priceComponent = new PriceComponent();
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.priceComponent"));
		setEdit(true);
		setEditPC(false);
		setFormType("form-pc-detail");
		getListBlockByPC();
		//getLoadComboListCategory();      
		loadCategoriesOfPC();  
		loadPCTypes();

	}

	public void editPriceComponents(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		getListBlockByPC();
		//getLoadComboListCategory();       
		loadCategoriesOfPC();  
		loadPCTypes();
		setCategoryId(priceComponent.getCategoryId());
		treeOfferBean.hideCategory();
		setEdit(true);
		setEditPC(true);
		setPriceComponent(priceComponent);
		setFormType("form-pc-detail");
	}

	public void editChildPriceComponents(NodeSelectEvent nodeSelectEvent) {
		PriceComponent priceComponent = (PriceComponent) nodeSelectEvent.getTreeNode().getData();
		if (priceComponent.getPriceComponentId() != this.priceComponent.getPriceComponentId()) {
			super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
			super.getTreeOfferBean().hideCategory();
			super.getTreeOfferBean().setContentTitle(super.readProperties("title.priceComponent"));

			setPriceComponent(priceComponent);
			setFormType("form-pc-detail");
			// getListBlockByPC();
			// getLoadComboListCategory();
			// setCategoryId(priceComponent.getCategoryId());
		}
		setEdit(true);
		setEditPC(true);
	}

	public void clonePriceComponents(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		priceComponent.setPriceComponentId(0L);
		super.getTreeOfferBean().hideCategory();
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.priceComponent"));
		setEdit(true);
		setEditPC(true);
		setPriceComponent(priceComponent);
		setFormType("form-pc-detail");
		getListBlockByPC();
		loadCategoriesOfPC();  
		//getLoadComboListCategory();
		setCategoryId(priceComponent.getCategoryId());
	}

	public void deletePriceComponents(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			PriceComponent item = (PriceComponent) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			ActionPriceComponentMapDAO actionPriceComponentMapDAO = new ActionPriceComponentMapDAO();
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (object instanceof Category) {
				// Delete PC and Map PC - Block
				if (!actionPriceComponentMapDAO.checkPCInAction(item.getPriceComponentId())) {
					PriceComponentBlockMapDAO componentBlockMapDAO = new PriceComponentBlockMapDAO();
					componentBlockMapDAO.deletePCBlockByPCId(item);
					setFormType("");
					treeOfferBean.removeTreeNodeAll(item);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.readProperties("common.delete"), this.readProperties("title.priceComponent")));
				} else {
					this.showMessageWARN("common.summary.warning", super.readProperties("priceComponent.used"));
				}
			}
			if (object instanceof Action) {
				// Delete Map Action - PC
				Action action = (Action) object;
				ActionPriceComponentMap actionPriceComponentMap = actionPriceComponentMapDAO
						.findActionPriceComponentMap(action.getActionId(), item.getPriceComponentId());
				if (actionPriceComponentMap != null) {
					actionPriceComponentMapDAO.delete(actionPriceComponentMap);
					treeOfferBean.removeTreeNode(item, action);
				}
			}
		}
	}

	public void commandHideClonePCTree(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		setFormType("form-pc-detail");
	}

	public void commandClonePCTreeOld(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		PriceComponent priceComponent = (PriceComponent) nodeSelectEvent.getTreeNode().getData();

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
		lstPara.add("pc;" + priceComponent.getPriceComponentId());
		mapPara.put("param", lstPara);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void commandClonePCTree() {
		PriceComponent priceComponentClone = this.priceComponent;
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
		lstPara.add("pc;" + priceComponentClone.getPriceComponentId());
		mapPara.put("param", lstPara);
		List<String> posIndex = new ArrayList<>();
		posIndex.add("11");
		mapPara.put("index", posIndex);
		List<String> typeTreeClone = new ArrayList<>();
		typeTreeClone.add("1");
		mapPara.put("treeTypeClone", typeTreeClone);
		RequestContext.getCurrentInstance().openDialog("/pages/offer/context_offer/clone_common.xhtml", options,
				mapPara);
	}

	public void redirectChangeCate() {
		this.openTreeCategoryDialog(TreeType.OFFER_PRICE_COMPONENT, "PriceComponent", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.priceComponent.setCategoryId(cate.getCategoryId());
			if (priceComponentDAO.moveToCate(priceComponent)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.priceComponent, cate, null);
				this.editPC = false;
				this.showMessageINFO("common.moveCate", " Success ");
			}
		}
	}

	// ************** SU KIEN FORM PC DETAIL ******************
	public void getListBlockByPC() {
		lstSubBlocks = blockDAO.findBlockByPC(priceComponent.getPriceComponentId());
	}

	public void btnPCNew() {
		priceComponent = new PriceComponent();
		setEditPC(false);
	}

	public void btnPCSave() {
		priceComponent.setCategoryId(getCategoryId());
		if (validatePC()) {
			Category cat = categoryDAO.get(getCategoryId());
			Category catParent = categoryDAO.get(cat.getCategoryParentId());
			// if (lstSubBlocks.size() > 0) {
			// Save PC & Blocks
			priceComponentDAO.savePCWithBlock(priceComponent, lstSubBlocks);
			if (!isEditPC()) {
				lstPriceComponents.add(priceComponent);
			} else {
				for (int i = 0; i < lstPriceComponents.size(); i++) {
					if (priceComponent.getPriceComponentId() == lstPriceComponents.get(i).getPriceComponentId()) {
						lstPriceComponents.set(i, priceComponent);
					}
				}
			}
			// Update tree
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.updateTreeNode(priceComponent, cat, lstSubBlocks);
			setEditPC(true);
			setEdit(false);
			this.showMessageINFO("common.save", " Price Component ");
		} else
			this.showMessageERROR("common.save", " Price component ", " Price component name already exists ");
		return;
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	private void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		PriceComponent action = (PriceComponent) event.getTreeNode().getData();
		Object object = priceComponentDAO.upDownObjectInCatWithDomain(action, "index", isUp);
		if (object instanceof PriceComponent) {
			Category category = categoryDAO.get(action.getCategoryId());
			PriceComponent nextAction = (PriceComponent) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(action, category);
			} else {
				treeOfferBean.moveDownTreeNode(action, category);
			}
			treeOfferBean.updateTreeNode(nextAction, category, null);
			if (formType == "category" && category.getCategoryId() == categoryId) {
				getListPriceComponentByCategoryId(categoryId);
			}
			super.showNotificationSuccsess();
		}
	}

	public void moveUpPC(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			PriceComponent item = (PriceComponent) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				moveUpDownInCat(nodeSelectEvent, true);
			} else if (objParent instanceof Action) {
				Action action = (Action) objParent;
				ActionPriceComponentMapDAO actionPriceComponentMapDAO = new ActionPriceComponentMapDAO();
				List<ActionPriceComponentMap> lstActionPriceComponentMap = actionPriceComponentMapDAO
						.findActionPriceComponentMapByAction(action.getActionId());
				if (!lstActionPriceComponentMap.isEmpty() && lstActionPriceComponentMap.size() > 1) {
					for (int i = 1; i < lstActionPriceComponentMap.size(); i++) {
						if (lstActionPriceComponentMap.get(i).getPriceComponentId() == item.getPriceComponentId()) {
							actionPriceComponentMapDAO.saveMapActionPC(lstActionPriceComponentMap.get(i - 1),
									lstActionPriceComponentMap.get(i));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveUpTreeNode(item, objParent);
							treeOfferBean.setActionProperties(false, null, action, false, false);
							break;
						}
					}
					this.showMessageINFO("validate.upObjectSuccess", super.readProperties(""));
				}
			}
		}
	}

	public void moveDownPC(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			PriceComponent item = (PriceComponent) nodeSelectEvent.getTreeNode().getData();
			BaseEntity objParent = (BaseEntity) nodeSelectEvent.getTreeNode().getParent().getData();
			if (objParent instanceof Category) {
				moveUpDownInCat(nodeSelectEvent, false);
			} else if (objParent instanceof Action) {
				Action action = (Action) objParent;
				ActionPriceComponentMapDAO actionPriceComponentMapDAO = new ActionPriceComponentMapDAO();
				List<ActionPriceComponentMap> lstActionPriceComponentMap = actionPriceComponentMapDAO
						.findActionPriceComponentMapByAction(action.getActionId());
				if (!lstActionPriceComponentMap.isEmpty() && lstActionPriceComponentMap.size() > 1) {
					for (int i = 0; i < lstActionPriceComponentMap.size() - 1; i++) {
						if (lstActionPriceComponentMap.get(i).getPriceComponentId() == item.getPriceComponentId()) {
							actionPriceComponentMapDAO.saveMapActionPC(lstActionPriceComponentMap.get(i),
									lstActionPriceComponentMap.get(i + 1));
							TreeOfferBean treeOfferBean = super.getTreeOfferBean();
							treeOfferBean.moveDownTreeNode(item, objParent);
							treeOfferBean.setActionProperties(false, null, action, false, false);
							break;
						}
					}
					this.showMessageINFO("validate.downObjectSuccess", super.readProperties(""));
				}
			}
		}
	}

	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			PriceComponent item = (PriceComponent) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getPriceComponentId(), PriceComponent.class);
		}
	}

	public void btnPCCancel() {
		priceComponent = new PriceComponent();
		setEditPC(false);
	}

	private boolean insert = false;

	public void btnAddBlock(Block item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(lstSubBlocks != null) {
			for (Block bl : lstSubBlocks) {
				lstId.add(bl.getBlockId());
			}	
		}
		
		if (item == null || item.getBlockId() <= 0) {			
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("title.block"), 0, true,lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("title.block"), 0, false, lstId);
		}
		
		block = item;
		insert = false;
	}

	public void commandAddNewBL() {
		Block itemNew = new Block();
		btnAddBlock(itemNew);
		insert = false;
	}

	public void addBlockAt(Block item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if(lstSubBlocks != null) {
			for (Block bl : lstSubBlocks) {
				lstId.add(bl.getBlockId());
			}	
		}
		
		if (item == null || item.getBlockId() <= 0){			
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("title.block"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("title.block"), 0, false, lstId);
		}
		block = item;
		insert = true;
	}

	public void onDialogBLReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object obj : objArr) {
			if (obj instanceof Block) {
				Block blockChange = (Block) obj;
				if (!existBlock(blockChange)) {
					if (block.getBlockId() != 0L) {
						if (insert) {
							lstSubBlocks.add(lstSubBlocks.indexOf(block) + 1, blockChange);
						} else {
							lstSubBlocks.set(lstSubBlocks.indexOf(block), blockChange);
						}
					} else {
						lstSubBlocks.add(blockChange);
					}
				} else {
					if (block.getBlockId() == blockChange.getBlockId() && !insert) {
						lstSubBlocks.set(lstSubBlocks.indexOf(block), blockChange);
					} else {
						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
					}
				}
			}
		}
	}

	public void onDialogPCReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof PriceComponent) {
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.updateTreeNode((PriceComponent) object,
					categoryDAO.get(((PriceComponent) object).getCategoryId()), null);
			editPriceComponents((PriceComponent) object);
			this.isEdit = false;
			super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
		}
	}

	private boolean existBlock(Block objBlock) {
		for (Block block : lstSubBlocks) {
			if (block.getBlockId() == objBlock.getBlockId()) {
				return true;

			}
		}
		return false;
	}

	// Validate
	private boolean validatePC() {
		if (ValidateUtil.checkStringNullOrEmpty(priceComponent.getPriceComponentName())) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorDataValueName")));
			return false;
		}
		if (priceComponentDAO.checkName(priceComponent)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			return false;
		}
		return true;
	}

	// *************** SU KIEN LIST SUB BLOCK PARENT PC**************
	public void editBlock(Block block) {
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.block"));
		super.getTreeOfferBean().setBlockProperties(false, block.getCategoryId(), block, true, false);
	}

	public void cloneBlock(Block block) {
		block.setBlockId(0L);
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.block"));
		super.getTreeOfferBean().setBlockProperties(false, block.getCategoryId(), block, false, false);
	}

	public void deleteBlock(Block block) {
		if (block != null) {
			// blockDAO.delete(block);
//			blockDAO.deletePriceComponentBlockMap(block, priceComponent.getPriceComponentId());
			lstSubBlocks.remove(block);
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.removeTreeNodeAll(block);
			this.showMessageINFO("common.delete", " Price Component BlockMap ");
		}
	}

	public void commandUpOnMap(Block block) {
		int indexBlock = lstSubBlocks.indexOf(block);
		if (indexBlock > 0) {
			Block itemBefore = lstSubBlocks.get(indexBlock - 1);
			lstSubBlocks.set(indexBlock - 1, block);
			lstSubBlocks.set(indexBlock, itemBefore);
		}
	}

	public void commandDownOnMap(Block block) {
		int indexBlock = lstSubBlocks.indexOf(block);
		if (indexBlock < lstSubBlocks.size() && indexBlock != (lstSubBlocks.size() - 1)) {
			Block itemBefore = lstSubBlocks.get(indexBlock + 1);
			lstSubBlocks.set(indexBlock + 1, block);
			lstSubBlocks.set(indexBlock, itemBefore);
		}
	}
}

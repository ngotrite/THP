package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
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
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.PcTypeDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PcTypeBlockMap;
import com.viettel.ocs.entity.SortPriceComponent;

@SuppressWarnings("serial")
@ManagedBean(name = "pcTypeBean")
@ViewScoped
public class PcTypeBean extends BaseController implements Serializable {
	private PcType pcType;
	private List<PcType> pcTypes;
	private PcTypeDAO pcTypeDAO;
	private String formType = "";
	private Category category;
	private List<Category> categoriesOfPcType;
	private CategoryDAO categoryDAO;
	private List<Block> lstBlock;
	private BlockDAO blockDAO;
//	private Block block;
	private boolean isEditting;
	private List<PcTypeBlockMap> lstPcTypeBlockMap;
	private PcTypeBlockMap currentItem;
	private List<SelectItem> lstBlockGenType;
	
	private List<SortPriceComponent> sortPriceComponents;
	private SortPriceComponentDAO sortPriceComponentDAO;

	@PostConstruct
	public void init() {
		pcType = new PcType();
		pcTypes = new ArrayList<PcType>();
		pcTypeDAO = new PcTypeDAO();
		category = new Category();
		categoriesOfPcType = new ArrayList<Category>();
		categoryDAO = new CategoryDAO();
		lstBlock = new ArrayList<Block>();
		blockDAO = new BlockDAO();
//		block = new Block();
		isEditting = true;
		sortPriceComponents = new ArrayList<SortPriceComponent>();
		sortPriceComponentDAO = new SortPriceComponentDAO();
		lstPcTypeBlockMap = new ArrayList<PcTypeBlockMap>();
		
		lstBlockGenType = new ArrayList<>(); 
		lstBlockGenType.add(new SelectItem(1, "--None--"));
		lstBlockGenType.add(new SelectItem(2, "Accumulate"));
		lstBlockGenType.add(new SelectItem(3, "Discount"));
	}
	
	public void notChooseSortPC() {
		pcType.setFilterId(null);
		sortPriceComponents.clear();
		RequestContext.getCurrentInstance().update("form-pc-type-detail:slSortPriceComponent");
	}
	
	public void onDialogSortPCReturn(SelectEvent event) {
		Object object = event.getObject();
		if (object instanceof SortPriceComponent) {
			SortPriceComponent sortPriceComponent = (SortPriceComponent) object;
			pcType.setFilterId(sortPriceComponent.getSortPriceComponentId());
			sortPriceComponents.clear();
			this.sortPriceComponents.add(sortPriceComponent);
			RequestContext.getCurrentInstance().update("form-pc-type-detail:slSortPriceComponent");
		}
	}
	
	public void chooseSortPC() {
		super.openTreeOfferDialog(TreeType.OFFER_SORT_PRICE_COMPONENT, readProperties("action.sort_price_component"), 0,
				false, null);
	}
	
	private void loadSortPriceComponents() {
		sortPriceComponents.clear();
		if (pcType.getFilterId() != null) {
			SortPriceComponent sortPriceComponent = sortPriceComponentDAO.get(pcType.getFilterId());
			if (sortPriceComponent != null) {
				sortPriceComponents.add(sortPriceComponent);
			}
		}
	}

	public void savePcType() {
		if (validatePcType()) {
			if (pcTypeDAO.savePcType(pcType, lstPcTypeBlockMap)) {
				
				lstBlock = blockDAO.findBlockByPcType(pcType.getPcTypeId());
				
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				Category cat = categoryDAO.get(pcType.getCategoryId());
				treeOfferBean.updateTreeNode(pcType, cat, lstBlock);
				this.showMessageINFO("common.save", " PC Type ");
				this.isEditting = false;
			} else {
				this.showMessageWARN("common.save", " PC Type ");
			}
		}
	}

	private boolean validatePcType() {
		boolean result = true;
		if (pcTypeDAO.checkName(pcType)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("common.nameAlreadyExists")));
			result = false;
		}

		return result;
	}

	public void removeBlock(PcTypeBlockMap item) {
		
		lstPcTypeBlockMap.remove(item);
		
//		if (pcType != null && block != null) {
//			lstBlock.remove(block);
//			this.showMessageINFO("common.delete", " PCType Block Map");
//		}
	}

	public void editBlock(PcTypeBlockMap item) {
		Block block = blockDAO.get(item.getBlockId());
		if(block == null) 
			return;
		
		TreeOfferBean offerBean = getTreeOfferBean();
		offerBean.setContentTitle(super.readProperties("block"));
		offerBean.setBlockProperties(false, block.getCategoryId(), block, true, false);	
	}
	
	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			PcType item = (PcType) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getPcTypeId(), PcType.class);
		}
	}

	// command up
	public void commandUpOnMap(PcTypeBlockMap item) {
		
		int idx = lstPcTypeBlockMap.indexOf(item);
		if(idx > 0) {
			lstPcTypeBlockMap.remove(idx);
			lstPcTypeBlockMap.add(idx - 1, item);
		}
		
//		int indexCondition = lstBlock.indexOf(block);
//		if (indexCondition > 0) {
//			Block itemBefore = lstBlock.get(indexCondition - 1);
//			lstBlock.set(indexCondition - 1, block);
//			lstBlock.set(indexCondition, itemBefore);
//		}
	}

	// command down
	public void commandDownOnMap(PcTypeBlockMap item) {
		
		int idx = lstPcTypeBlockMap.indexOf(item);
		if(idx < lstPcTypeBlockMap.size() - 1) {
			lstPcTypeBlockMap.remove(idx);
			lstPcTypeBlockMap.add(idx + 1, item);
		}
		
//		int indexBasic = lstBlock.indexOf(item);
//		if (indexBasic < lstBlock.size() && lstBlock.size() != 1) {
//			Block itemAfter = lstBlock.get(indexBasic + 1);
//			lstBlock.set(indexBasic + 1, item);
//			lstBlock.set(indexBasic, itemAfter);
//		}
	}

	public void addBLAt(PcTypeBlockMap item) {
		
//		block = blockDAO.get(item.getBlockId());
//		if(block == null) 
//			return;
		
		currentItem = item;
		List<Long> lstId = new ArrayList<Long>();
		if (lstPcTypeBlockMap != null) {
			for (PcTypeBlockMap map : lstPcTypeBlockMap) {
				lstId.add(map.getBlockId());
			}
		}

		if (item == null || item.getBlockId() <= 0) {
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("block"), 0, true, lstId);
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("block"), 0, false, lstId);
		}
		
		insert = true;
	}

	private boolean insert = false;

	public void addNewBlock() {
		insert = false;
		chooseBL(null);
	}

	public void chooseBL(PcTypeBlockMap item) {
		
		List<Long> lstId = new ArrayList<Long>();
		if (lstPcTypeBlockMap != null) {
			for (PcTypeBlockMap map : lstPcTypeBlockMap) {
				lstId.add(map.getBlockId());
			}
		}

		if (item == null || item.getBlockId() <= 0) {
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("block"), 0, true, lstId);
//			block = new Block();
		} else {
			super.openTreeOfferDialog(TreeType.OFFER_BLOCK, readProperties("block"), 0, false, lstId);
//			block = this.getBlockByMapItem(item);
		}
		
		currentItem = item;
		insert = false;
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
				if (currentItem != null) {
					if (insert) {
						
						PcTypeBlockMap map = new PcTypeBlockMap(blockChange.getBlockId(), pcType.getPcTypeId());
						lstPcTypeBlockMap.add(lstPcTypeBlockMap.indexOf(currentItem) + 1, map);
					} else {
						currentItem.setBlockId(blockChange.getBlockId());
					}
				} else {
					
					PcTypeBlockMap map = new PcTypeBlockMap(blockChange.getBlockId(), pcType.getPcTypeId());
					lstPcTypeBlockMap.add(map);
				}
				
				lstBlock.add(blockChange);
				
//				if (!exitsBLs(blockChange)) {
//					if (block.getBlockId() != 0L) {
//						if (insert) {
//							lstBlock.add(lstBlock.indexOf(block) + 1, blockChange);
//						} else {
//							lstBlock.set(lstBlock.indexOf(block), blockChange);
//						}
//					} else {
//						lstBlock.add(blockChange);
//					}
//				} else {
//					if (block.getBlockId() == blockChange.getBlockId() && !insert) {
//						lstBlock.set(lstBlock.indexOf(block), blockChange);
//					} else {
//						this.showMessageWARN("common.summary.warning", super.readProperties("common.objAlreadyExists"));
//					}
//				}
			}
		}

	}

	private boolean exitsBLs(Block tableInput) {
		for (Block table : lstBlock) {
			if (table.getBlockId() == tableInput.getBlockId()) {
				return true;
			}
		}
		return false;
	}

	private void initBlock() {
		if (pcType != null && pcType.getPcTypeId() != 0L) {
			lstPcTypeBlockMap = pcTypeDAO.findPcTypeBlockMap(pcType.getPcTypeId());
			lstBlock = blockDAO.findBlockByPcType(pcType.getPcTypeId());			
		} else {
			lstPcTypeBlockMap = new ArrayList<>();
			lstBlock = new ArrayList<Block>();
		}			
	}

	public void refreshPcType(PcType pcType) {
		formType = "form-pc-type-detail";
		this.pcType = pcType;
		loadCategoriesOfPcType();
		initBlock();
		loadSortPriceComponents();
	}

	public void refreshCategories(Category category) {
		formType = "form-pc-type-list";
		this.category = category;
		loadPcTypeByCategory(category.getCategoryId());
		loadCategoriesOfPcType();
	}

	public void loadCategoriesOfPcType() {
		categoriesOfPcType = categoryDAO.findByTypeForSelectbox(CategoryType.OFF_PC_TYPE);
	}

	public List<PcType> loadPcTypeByCategory(long categoryId) {
		pcTypes = new ArrayList<PcType>();
		pcTypes = pcTypeDAO.findPCTByCategoryId(categoryId);
		return pcTypes;
	}

	public void addNewPcType() {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		PcType pcType = new PcType();
		pcType.setCategoryId(category.getCategoryId());
		treeOfferBean.setContentTitle(super.readProperties("pcType.title"));
		refreshPcType(pcType);
		this.isEditting = true;
	}

	public void editPcType(PcType pcType) {
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		treeOfferBean.setContentTitle(super.readProperties("pcType.title"));
		refreshPcType(pcType);
		this.isEditting = true;
	}

	public void removePcType(PcType pcType) {
		PriceComponentDAO pcDAO = new PriceComponentDAO();
		if (!pcDAO.checkPcTypeInPC(pcType.getPcTypeId())) {
			pcTypeDAO.delete(pcType);
			pcTypes.remove(pcType);
			setFormType("");
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.removeTreeNodeAll(pcType);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.readProperties("common.delete"),
					this.readProperties("pcType.title")));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("pcType.used"));
		}
	}
	
	//Context Menu
	
	public void addNewPcTypeCT(NodeSelectEvent nodeSelectEvent){
		addNewPcType();
	}
	
	public void editPcTypeCT(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		refreshPcType(pcType);
		this.isEditting = true;
	}
	
	public void removePcTypeCT(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			PcType item = (PcType) nodeSelectEvent.getTreeNode().getData();
			Object object = nodeSelectEvent.getTreeNode().getParent().getData();
			if (object instanceof Category) {
				removePcType(item);
			}

		}
	}
	
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		PcType pcType = (PcType) event.getTreeNode().getData();
		Object object = pcTypeDAO.upDownObjectInCatWithDomain(pcType, "posIdx", isUp);
		if (object instanceof PcType) {
			Category category = categoryDAO.get(pcType.getCategoryId());
			PcType nextPcType = (PcType) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(pcType, category);
			} else {
				treeOfferBean.moveDownTreeNode(pcType, category);
			}
			treeOfferBean.updateTreeNode(nextPcType, category, null);
			if (formType == "form-pc-type-list" && category.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(this.category);
			}
			super.showNotificationSuccsess();
		}
	}
	
	private Block getBlockByMapItem(PcTypeBlockMap map) {
		
		for(Block block : lstBlock) {
			if(map.getBlockId() != null && block.getBlockId() == map.getBlockId())
				return block;
		}
		
		return null;
	}
	
	public String getBlockName(PcTypeBlockMap map) {
		
		Block block = this.getBlockByMapItem(map);
		if(block != null)
			return block.getBlockName();
		
		return "";
	}
	
	public String getBlockDesc(PcTypeBlockMap map) {
		
		Block block = this.getBlockByMapItem(map);
		if(block != null)
			return block.getRemark();
		
		return "";
	}

	public PcType getPcType() {
		return pcType;
	}

	public void setPcType(PcType pcType) {
		this.pcType = pcType;
	}

	public List<PcType> getPcTypes() {
		return pcTypes;
	}

	public void setPcTypes(List<PcType> pcTypes) {
		this.pcTypes = pcTypes;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<Category> getCategoriesOfPcType() {
		return categoriesOfPcType;
	}

	public void setCategoriesOfPcType(List<Category> categoriesOfPcType) {
		this.categoriesOfPcType = categoriesOfPcType;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Block> getLstBlock() {
		return lstBlock;
	}

	public void setLstBlock(List<Block> lstBlock) {
		this.lstBlock = lstBlock;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<SortPriceComponent> getSortPriceComponents() {
		return sortPriceComponents;
	}

	public void setSortPriceComponents(List<SortPriceComponent> sortPriceComponents) {
		this.sortPriceComponents = sortPriceComponents;
	}

	public List<PcTypeBlockMap> getLstPcTypeBlockMap() {
		return lstPcTypeBlockMap;
	}

	public void setLstPcTypeBlockMap(List<PcTypeBlockMap> lstPcTypeBlockMap) {
		this.lstPcTypeBlockMap = lstPcTypeBlockMap;
	}

	public List<SelectItem> getLstBlockGenType() {
		return lstBlockGenType;
	}

	public void setLstBlockGenType(List<SelectItem> lstBlockGenType) {
		this.lstBlockGenType = lstBlockGenType;
	}
	
}

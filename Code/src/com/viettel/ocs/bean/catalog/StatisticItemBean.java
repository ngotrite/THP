package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.StatisticItemDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;

@SuppressWarnings("serial")
@ManagedBean(name = "statisticItemBean")
@ViewScoped
public class StatisticItemBean extends BaseController implements Serializable {
	private Category category;
	private String formType = "";
	private List<StatisticItem> statisticItems;

	private StatisticItem statisticItem;
	private StatisticItemDAO statisticItemDAO;
	private CategoryDAO categoryDAO;
	private boolean isEditting;

	@PostConstruct
	public void init() {
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.statisticItem = new StatisticItem();
		this.statisticItemDAO = new StatisticItemDAO();
		this.statisticItems = new ArrayList<StatisticItem>();
		this.isEditting = true;

	}

	public void refreshStatisticItem(StatisticItem statisticItem) {
		try {
			this.statisticItem = statisticItem.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("statistic-detail");
		LoadCategoriesOfUnitType();
		this.isEditting = true;
	}

	public void refreshCategories(Category category) {
		formType = "list-statistic";
		this.category = category;
		loadStatisticItemByCategory(category.getCategoryId());
	}

	// load list StatisticItem by Category
	public List<StatisticItem> loadStatisticItemByCategory(long categoryId) {
		statisticItems = new ArrayList<StatisticItem>();
		statisticItems = statisticItemDAO.findSIByCategoryId(categoryId);
		return statisticItems;
	}

	private List<Category> categoriesOfStatistic;

	private void LoadCategoriesOfUnitType() {
		categoriesOfStatistic = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_STATISTIC_ITEM);
	}

	public void addNewStatistic() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("statisticItem.title"));
		statisticItem = new StatisticItem();
		refreshStatisticItem(statisticItem);
		statisticItem.setCategoryId(category.getCategoryId());
		this.isEditting = false;
	}

	public void editStatistic(StatisticItem statisticItem) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("statisticItem.title"));
		treeCommonBean.hideAllCategoryComponent();
		refreshStatisticItem(statisticItem);
		this.isEditting = false;
	}

	public void deleteStatistic(StatisticItem item) {
		if (checkDelStatisticItem(item)) {
			try {
				statisticItemDAO.delete(item);
				statisticItems.remove(item);
				refreshCategories(category);
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				treeCommonBean.removeTreeNodeAll(item);
				this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
			} catch (Exception e) {
				throw e;
			}
		}

	}

	public boolean checkDelStatisticItem(StatisticItem statisticItem) {
		if (statisticItemDAO.checkStatisticItemDpdFomula(statisticItem) > 0) {
			this.showMessageWARN("", super.readProperties("validate.fieldUseIn"));
			return false;
		}
		return true;
	}

	public void saveStatistic() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(statisticItem.getCategoryId());
		if (validateStatisticItem()) {
			if (statisticItemDAO.saveStatistic(statisticItem)) {
				this.showMessageINFO("common.save", "Statistic Item");
				treeCommonBean.updateTreeNode(statisticItem, cat, null);
				this.isEditting = true;
			}
		} else {

		}
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		StatisticItem statisticItem = (StatisticItem) event.getTreeNode().getData();
		Object object = statisticItemDAO.upDownObjectInCatWithDomain(statisticItem, "pos_Idx", isUp);
		if (object instanceof StatisticItem) {
			Category category = categoryDAO.get(statisticItem.getCategoryId());
			StatisticItem nextStatisticItem = (StatisticItem) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(statisticItem);
			} else {
				treeCommonBean.moveDownTreeNode(statisticItem);
			}
			treeCommonBean.updateTreeNode(nextStatisticItem, category, null);
			if (formType == "list-statistic" && nextStatisticItem.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}

			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			StatisticItem item = (StatisticItem) nodeSelectEvent.getTreeNode().getData();
			editStatistic(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			StatisticItem item = (StatisticItem) nodeSelectEvent.getTreeNode().getData();
			deleteStatistic(item);
			loadStatisticItemByCategory(item.getCategoryId());
		}
	}

	// Validate
	private boolean validateStatisticItem() {
		boolean result = true;
		if (statisticItemDAO.checkName(statisticItem, statisticItem.getStatisticItemName())) {
			this.showMessageWARN("statisticItem.title", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	public List<Category> getCategoriesOfStatistic() {
		return categoriesOfStatistic;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<StatisticItem> getStatisticItems() {
		return statisticItems;
	}

	public void setStatisticItems(List<StatisticItem> statisticItems) {
		this.statisticItems = statisticItems;
	}

	public StatisticItem getStatisticItem() {
		return statisticItem;
	}

	public void setStatisticItem(StatisticItem statisticItem) {
		this.statisticItem = statisticItem;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}
}

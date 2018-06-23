package com.viettel.ocs.bean.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.PCCRuleDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.UnitType;

@ManagedBean(name = "pccRuleBean")
@ViewScoped
public class PCCRuleBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = 1;
	private PccRule pccRule;
	private PccRule pccRuleSafe;
	private List<Category> listItemCategory;
	private boolean isEditting;
	private PCCRuleDAO pccRuleDAO;
	private boolean showAddNew;
	private String formType;
	private String pccRuleTitle;
	private List<PccRule> lstPccRule;
	private CategoryDAO categoryDAO;
	private Long categoryID;

	public List<Category> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<Category> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public Long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Long categoryID) {
		this.categoryID = categoryID;
	}

	public List<PccRule> getLstPccRule() {
		return lstPccRule;
	}

	public void setLstPccRule(List<PccRule> lstPccRule) {
		this.lstPccRule = lstPccRule;
	}

	public String getPccRuleTitle() {
		return pccRuleTitle;
	}

	public void setPccRuleTitle(String pccRuleTitle) {
		this.pccRuleTitle = pccRuleTitle;
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

	public boolean isShowAddNew() {
		return showAddNew;
	}

	public void setShowAddNew(boolean showAddNew) {
		this.showAddNew = showAddNew;
	}

	public PccRule getPccRule() {
		return pccRule;
	}

	public void setPccRule(PccRule pccRule) {
		this.pccRule = pccRule;
	}

	public PCCRuleBean() {
		lstPccRule = new ArrayList<>();
		pccRule = new PccRule();
		pccRuleDAO = new PCCRuleDAO();
		categoryDAO = new CategoryDAO();
		loadCategory();
	}

	// ***************EVENT DETAIL***************
	public void btnSave() {
		if (pccRule != null) {
			if (validate()) {
				if (compareDate(pccRule.getActiveTime(), pccRule.getDeactiveTime())) {
					if (!checkDoublePccRuleName(pccRule)) {
						if (pccRule != null) {
//							if (pccRule.getPccRuleId() == 0L) {
//								pccRule.setIndex(pccRuleDAO.getMaxIndex() + 1L);
//							}
//							pccRuleDAO.saveOrUpdate(pccRule);
							pccRuleDAO.savePccRule(pccRule);
							this.showMessageINFO("common.save", " PCC Rule ");
							TreeCommonBean treeCommonBean = super.getTreeCommonBean();
							Category cat = categoryDAO.get(pccRule.getCategoryId());
							treeCommonBean.updateTreeNode(pccRule, cat, null);
							try {
								pccRuleSafe = pccRule.clone();
							} catch (CloneNotSupportedException e) {
								getLogger().warn(e.getMessage(), e);
							}
							isEditting = false;
						}
					} else {
						this.showMessageWARN("common.save", this.readProperties("title.PR"),
								this.readProperties("policy.pcc_basename"));
						return;
					}
				} else {
					super.showMessageWARN("common.save", this.readProperties("title.PR"),
							this.readProperties("policy.afterdate"));
					return;
				}
			}
		}
	}

	private boolean checkDoublePccRuleName(PccRule rule) {
		List<PccRule> lstPccRuleByPCCName = pccRuleDAO.loadPccRuleByPCCName(rule.getPccRuleId(), rule.getNodeName());
		if (lstPccRuleByPCCName != null && lstPccRuleByPCCName.size() > 0)
			return true;
		else
			return false;
	}

	private boolean validate() {
		if (pccRule != null) {
			if (pccRule.getPriority() < 0) {
				this.showMessageWARN("common.save", this.readProperties("policy.title_rule"),
						this.readProperties("policy.rule_priority"));
				return false;
			}
		}
		return true;
	}

	private boolean compareDate(Date efDate, Date exDate) {
		if (exDate != null) {
			if (exDate.after(efDate)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public void btnCancel() {
		showAddNew = false;
		setEditting(true);
		this.pccRule = pccRuleSafe;
	}

	// load combo for type Category
	public void loadCategory() {
		listItemCategory = new ArrayList<Category>();
		listItemCategory = categoryDAO.findByTypeForSelectbox(CategoryType.PLC_PR_PCC_RULE);
	}

	// ***************EVENT LIST PCCRULE***************
	public List<PccRule> getListPccRuleByCategoryId(Long catId) {
		return lstPccRule = pccRuleDAO.getPccRuleByCategoryId(catId);
	}

	private void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('vgPccRule')");
	}

	public void updateUI() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('vgPccRule').clearFilters()");
	}

	public void setDataByTreeNode(PccRule rule) {
		pccRule = rule;
		try {
			pccRuleSafe = rule.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	public void editPccRule(PccRule rule) {
		pccRule = rule;
		setDataAndViewDetail(rule);
		setEditting(true);
		getTreeCommonBean().setContentTitle(super.readProperties("title.PR"));
	}

	public void deletePccRule(PccRule rule) {

		if (pccRuleDAO.isDependency(rule.getPccRuleId())) {
			super.showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "dependencies.titleTreeParent");
			return;
		}

		if (rule != null) {
			for (int i = 0; i < lstPccRule.size(); i++) {
				if (rule.getPccRuleId() == lstPccRule.get(i).getPccRuleId()) {

					pccRuleDAO.delete(rule);
					lstPccRule.remove(i);
					TreeCommonBean treeCommonBean = super.getTreeCommonBean();
					Category cat = categoryDAO.get(rule.getCategoryId());
					treeCommonBean.removeTreeNode(rule, cat);
					updateUI();
					this.showMessageINFO("common.delete", super.readProperties("title.PR"));
					
					break;
				}
			}
		}
	}

	public void btnAddNewPccRule() {
		pccRule = new PccRule();
		pccRule.setCategoryId(categoryID);
		setDataAndViewDetail(pccRule);
		isEditting = true;
		getTreeCommonBean().setContentTitle(super.readProperties("title.PR"));
	}

	private void setDataAndViewDetail(PccRule rule) {
//		setFormType("detail");
//		getTreeCommonBean().setPccRuleProperties(false, rule.getCategoryId(), rule);
		getTreeCommonBean().hideAllCategoryComponent();
		setFormType("detail");
		setDataByTreeNode(rule);
		setEditting(false);
		loadCategory();
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		PccRule pccRule = (PccRule) event.getTreeNode().getData();
		Object object = pccRuleDAO.upDownObjectInCatWithDomain(pccRule, "index", isUp);
		if (object instanceof PccRule) {
			Category category = categoryDAO.get(pccRule.getCategoryId());
			PccRule nextPccRule = (PccRule) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(pccRule);
			} else {
				treeCommonBean.moveDownTreeNode(pccRule);
			}
			treeCommonBean.updateTreeNode(nextPccRule, category, null);
			if (formType == "category" && category.getCategoryId() == categoryID) {
				getListPccRuleByCategoryId(category.getCategoryId());
				clearFilter();
			}

			super.showNotificationSuccsess();
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			PccRule item = (PccRule) nodeSelectEvent.getTreeNode().getData();
			editPccRule(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			PccRule item = (PccRule) nodeSelectEvent.getTreeNode().getData();
			deletePccRule(item);
			getListPccRuleByCategoryId(item.getCategoryId());
			formType = "category";
		}
	}
}

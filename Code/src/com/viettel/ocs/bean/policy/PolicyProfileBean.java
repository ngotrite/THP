package com.viettel.ocs.bean.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.MonitorKeyDAO;
import com.viettel.ocs.dao.PCCRuleDAO;
import com.viettel.ocs.dao.ProfilePepDAO;
import com.viettel.ocs.dao.QosDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.MonitorKey;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.Qos;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.TriggerOcs;

@ManagedBean(name = "policyProfileBean")
@ViewScoped
public class PolicyProfileBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = 1L;

	private ProfilePep profilePep;
	private ProfilePep profilePepSafe;
	private List<ProfilePep> listProfilePep;
	private List<Category> listItemCategory;
	private List<SelectItem> listItemMonitorKey;
	private List<SelectItem> listItemQos;
	private boolean isEditting = false;
	private boolean showAddNew = false;
	private ProfilePepDAO profilePepDAO;
	private MonitorKeyDAO monitorKeyDAO;
	private QosDAO qosDAO;
	private PCCRuleDAO ruleDAO;
	private CategoryDAO categoryDAO;
	private String formType;
	private String policyProfileTitle;
	private List<PccRule> listPCCOfProfilePep;
	private PccRule pccRuleDlg;
	private PCCRuleDAO pccRuleDAO;
	private Long categoryID;

	public Long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Long categoryID) {
		this.categoryID = categoryID;
	}

	public PccRule getPccRuleDlg() {
		return pccRuleDlg;
	}

	public void setPccRuleDlg(PccRule pccRuleDlg) {
		this.pccRuleDlg = pccRuleDlg;
	}

	public List<PccRule> getListPCCOfProfilePep() {
		return listPCCOfProfilePep;
	}

	public void setListPCCOfProfilePep(List<PccRule> listPCCOfProfilePep) {
		this.listPCCOfProfilePep = listPCCOfProfilePep;
	}

	public String getPolicyProfileTitle() {
		return policyProfileTitle;
	}

	public void setPolicyProfileTitle(String policyProfileTitle) {
		this.policyProfileTitle = policyProfileTitle;
	}

	public List<ProfilePep> getListProfilePep() {
		return listProfilePep;
	}

	public void setListProfilePep(List<ProfilePep> listProfilePep) {
		this.listProfilePep = listProfilePep;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public ProfilePep getProfilePep() {
		return profilePep;
	}

	public void setProfilePep(ProfilePep profilePep) {
		this.profilePep = profilePep;
	}

	public List<Category> getListItemCategory() {
		return listItemCategory;
	}

	public void setListItemCategory(List<Category> listItemCategory) {
		this.listItemCategory = listItemCategory;
	}

	public List<SelectItem> getListItemMonitorKey() {
		return listItemMonitorKey;
	}

	public void setListItemMonitorKey(List<SelectItem> listMonitorKey) {
		this.listItemMonitorKey = listMonitorKey;
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

	@PostConstruct
	public void init() {
		this.isEditting = false;
		profilePep = new ProfilePep();
		profilePepDAO = new ProfilePepDAO();
		monitorKeyDAO = new MonitorKeyDAO();
		pccRuleDAO = new PCCRuleDAO();
		ruleDAO = new PCCRuleDAO();
		categoryDAO = new CategoryDAO();
		qosDAO = new QosDAO();
		loadCategory();
		listProfilePep = new ArrayList<>();
	}

	public void btnSave() {
		if (validate()) {
			if (!checkDoublePPName(profilePep)) {
				boolean check = profilePepDAO.saveProfilePepDetail(profilePep, listPCCOfProfilePep);
				if (check) {
					TreeCommonBean treeCommonBean = super.getTreeCommonBean();
					Category cat = categoryDAO.get(profilePep.getCategoryId());
					treeCommonBean.updateTreeNode(profilePep, cat, listPCCOfProfilePep);
					try {
						profilePepSafe = profilePep.clone();
					} catch (CloneNotSupportedException e) {
						getLogger().warn(e.getMessage(), e);
					}
					isEditting = false;
					this.showMessageINFO("common.save", this.readProperties("policy.title_profile"));
				} else
					this.showMessageWARN("common.save", this.readProperties("policy.title_profile"));
			} else {
				this.showMessageWARN("common.save", this.readProperties("policy.title_profile"),
						this.readProperties("policy.profile"));
				return;
			}
		}
	}

	private boolean checkDoublePPName(ProfilePep profilePep) {
		List<ProfilePep> lstPPByPPName = profilePepDAO.loadProfilePepByPPName(profilePep.getProfilePepId(),
				profilePep.getProfilePepName().trim());
		if (lstPPByPPName != null && lstPPByPPName.size() > 0)
			return true;
		else
			return false;
	}

	private boolean validate() {
		if (profilePep != null) {
			if (profilePep.getPriority() < 0) {
				this.showMessageWARN("common.save", this.readProperties("policy.title_profile"),
						this.readProperties("policy.rule_priority"));
				return false;
			}
		}
		return true;
	}

	public void btnNew() {
		init();
		profilePep = new ProfilePep();
		profilePep.setCategoryId(categoryID);
		setDataAndViewDetail(profilePep);
		getTreeCommonBean().setContentTitle(super.readProperties("title.PP"));
		isEditting = true;

	}

	private void setDataAndViewDetail(ProfilePep profilePep) {
//		setFormType("detail");
//		getTreeCommonBean().setPolicyProfileProperties(false, profilePep.getCategoryId(), profilePep);
		getTreeCommonBean().hideAllCategoryComponent();
		setFormType("detail");
		setDataByTreeNode(profilePep);
		findListPccRule(profilePep.getProfilePepId());
		loadCategory();
	}

	public void updateUI() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('vgPolicyProfile').clearFilters()");
	}

	public void btnCancel() {
		try {
			this.profilePep = profilePepSafe.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}

		showAddNew = false;
		setEditting(true);
	}

	// load combo for type Category
	public void loadCategory() {
		listItemCategory = new ArrayList<>();
		listItemCategory = categoryDAO.findByTypeForSelectbox(CategoryType.PLC_PPP_POLICY_PROFILE);
	}

	// load combo for type MonitorKey
	public List<SelectItem> loadMonitorKey() {
		listItemMonitorKey = new ArrayList<SelectItem>();
		List<MonitorKey> listMonitorKey = monitorKeyDAO.findAllWithoutDomain("");
		if (listItemMonitorKey != null && !listMonitorKey.isEmpty()) {
			for (MonitorKey monitorKey : listMonitorKey) {
				listItemMonitorKey.add(new SelectItem(monitorKey.getMonitorKeyId(),
						monitorKey.getMonitorKey() + "( " + monitorKey.getRemark() + " )"));
			}
		}
		return listItemMonitorKey;
	}

	public List<SelectItem> loadQos() {
		listItemQos = new ArrayList<SelectItem>();
		List<Qos> listQos = qosDAO.findAllWithoutDomain("");
		if (listItemQos != null && !listQos.isEmpty()) {
			for (Qos qos : listQos) {
				listItemQos.add(new SelectItem(qos.getQosId(), qos.getQosName()));
			}
		}
		return listItemQos;
	}

	// ***********EVENT CLICK TREE**************
	public List<ProfilePep> getListPolicyProfileBeanByCategoryId(Long catId) {
		return listProfilePep = profilePepDAO.getProfilePepByCategoryId(catId);
	}

	public void setDataByTreeNode(ProfilePep pep) {
		try {
			profilePep = pep.clone();
			profilePepSafe = pep.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	// **************EVENT LIST PP**************
	public void editPP(ProfilePep pep) {
		setDataAndViewDetail(pep);
		isEditting = true;
		this.getTreeCommonBean().setContentTitle(super.readProperties("title.PP"));
	}

	public void deletePP(ProfilePep pep) {
		List<PccRule> lstPccRule = ruleDAO.findPccRuleByPoliciProfile(pep.getProfilePepId());
		if (lstPccRule.size() > 0) {
			this.showMessageWARN("common.delete", this.readProperties("policy.title_rule_use"));
			return;
		} else {
			for (int i = 0; i < listProfilePep.size(); i++) {
				if (pep.getProfilePepId() == listProfilePep.get(i).getProfilePepId()) {
					listProfilePep.remove(i);
					profilePepDAO.delete(pep);
					TreeCommonBean treeCommonBean = super.getTreeCommonBean();
					Category cat = categoryDAO.get(pep.getCategoryId());
					treeCommonBean.removeTreeNode(pep, cat);
					updateUI();
					this.showMessageINFO("common.delete", this.readProperties("policy.title_profile"));
					break;
				}
			}
		}
	}

	// ***********EVENT LIST PCCRULE**************
	public void findListPccRule(Long ProfileID) {
		listPCCOfProfilePep = ruleDAO.findPccRuleByPoliciProfile(ProfileID);
		updatTablePcc();
	}

	public void editPccRule(PccRule rule) {
//		try {
//			pccRuleDlg = rule.clone();
//		} catch (CloneNotSupportedException e) {
//			getLogger().warn(e.getMessage(), e);
//		}
		
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setPccRuleProperties(false, rule.getCategoryId(), rule, true, false);
	}

	public void deletePccRule(PccRule rule) {
		if (rule != null) {
			for (int i = 0; i < listPCCOfProfilePep.size(); i++) {
				if (rule.getPccRuleId() == listPCCOfProfilePep.get(i).getPccRuleId()) {
					listPCCOfProfilePep.remove(i);
					updatTablePcc();
					break;
				}
			}
		}
	}

	public void choosePR() {
		try {
			List<Long> lstId = new ArrayList<Long>();
			if(listPCCOfProfilePep != null) {
				for (PccRule pr : listPCCOfProfilePep) {
					lstId.add(pr.getPccRuleId());
				}	
			}
			super.openTreeCommonDialog(TreeType.POLICY_PCC_RULE, readProperties("pccRule.title"), 0, true, lstId);
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
			super.showNotificationFail();
		}

	}

	public void onDialogPRReturn(SelectEvent event) {
		Object[] objArr = new Object[1];
		if (event.getObject() instanceof Object[]) {
			objArr = (Object[]) event.getObject();
		} else {
			objArr[0] = event.getObject();
		}

		for (Object object : objArr) {
			if (object instanceof PccRule) {
				PccRule pccRule = (PccRule) object;
				if (!exitsPRs(pccRule)) {
					listPCCOfProfilePep.add(pccRule);
					updatTablePcc();
				} else
					this.showMessageWARN("common.select", this.readProperties("policy.title_rule"),
							this.readProperties("policy.pcc"));
			}
		}
	}

	private boolean exitsPRs(PccRule pccRule) {
		for (PccRule rule : listPCCOfProfilePep) {
			if (rule.getPccRuleId() == pccRule.getPccRuleId()) {
				return true;
			}
		}
		return false;
	}

	private void updatTablePcc() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('vgPCC').clearFilters()");
	}

	// ****************EVENT DIALOG ***************
	public void btnSavePccRuleDlg() {
		if (pccRuleDAO.countPccRule(pccRuleDlg.getPccRuleId(), profilePep.getProfilePepId()) > 0) {
			this.showMessageWARN("common.edit", this.readProperties("policy.title_rule"),
					this.readProperties("policy.pccRule_use"));
			return;
		} else {
			if (validatePccRule()) {
				if (!checkDoublePccRuleName(pccRuleDlg)) {
					if (pccRuleDlg != null) {
						for (int i = 0; i < listPCCOfProfilePep.size(); i++) {
							if (listPCCOfProfilePep.get(i).getPccRuleId() == pccRuleDlg.getPccRuleId()) {
								listPCCOfProfilePep.remove(listPCCOfProfilePep.get(i));
								listPCCOfProfilePep.add(i, pccRuleDlg);
							}
						}
						pccRuleDAO.saveOrUpdate(pccRuleDlg);
						TreeCommonBean treeCommonBean = super.getTreeCommonBean();
						Category cat = categoryDAO.get(pccRuleDlg.getCategoryId());
						treeCommonBean.updateTreeNode(pccRuleDlg, cat, null);
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('dlgPccRule').hide();");
						updatTablePcc();
						this.showMessageINFO("common.save", super.readProperties("policy.title_rule"));
					}
				} else {
					this.showMessageWARN("common.save", this.readProperties("title.PR"),
							this.readProperties("policy.pcc_basename"));
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

	private boolean validatePccRule() {
		if (pccRuleDlg != null) {
			if (pccRuleDlg.getPriority() < 0) {
				this.showMessageWARN("common.save", this.readProperties("policy.title_rule"),
						this.readProperties("policy.rule_priority"));
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		ProfilePep profilePep = (ProfilePep) event.getTreeNode().getData();
		Object object = profilePepDAO.upDownObjectInCatWithDomain(profilePep, "index", isUp);
		if (object instanceof ProfilePep) {
			Category category = categoryDAO.get(profilePep.getCategoryId());
			ProfilePep nextProfilePep = (ProfilePep) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(profilePep);
			} else {
				treeCommonBean.moveDownTreeNode(profilePep);
			}
			treeCommonBean.updateTreeNode(nextProfilePep, category, null);
			if (formType == "category" && category.getCategoryId() == categoryID) {
				getListPolicyProfileBeanByCategoryId(category.getCategoryId());
				clearFilters();
			}

			super.showNotificationSuccsess();
		}
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			ProfilePep item = (ProfilePep) nodeSelectEvent.getTreeNode().getData();
			editPP(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
				ProfilePep item = (ProfilePep) nodeSelectEvent.getTreeNode().getData();
				getListPolicyProfileBeanByCategoryId(item.getCategoryId());
				deletePP(item);
				setFormType("category"); 
		}
	}

	private void clearFilters() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("clearFilter('vgPolicyProfile')");
	}
}

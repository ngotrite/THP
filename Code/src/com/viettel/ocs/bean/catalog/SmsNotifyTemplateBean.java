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
import com.viettel.ocs.dao.LangDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.SmsNotifyTemplateDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Lang;
import com.viettel.ocs.entity.SmsNotifyTemplate;
import com.viettel.ocs.entity.StatisticItem;

@SuppressWarnings("serial")
@ManagedBean(name = "smsNotiTemBean")
@ViewScoped
public class SmsNotifyTemplateBean extends BaseController implements Serializable {

	private Category category;
	private String formType = "";
	private List<SmsNotifyTemplate> smsNotiTems;

	private SmsNotifyTemplate smsNotiTem;
	private SmsNotifyTemplateDAO smsNotiTemDAO;
	private CategoryDAO categoryDAO;
	private boolean isEditting;

	private List<Lang> lstLang;

	@PostConstruct
	public void init() {
		this.category = new Category();
		this.categoryDAO = new CategoryDAO();
		this.smsNotiTem = new SmsNotifyTemplate();
		this.smsNotiTemDAO = new SmsNotifyTemplateDAO();
		this.smsNotiTems = new ArrayList<SmsNotifyTemplate>();
		this.isEditting = true;
		this.lstLang = new ArrayList<Lang>();
		this.categoriesOfSMS = new ArrayList<Category>();
	}
	
	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			SmsNotifyTemplate item = (SmsNotifyTemplate) nodeSelectEvent.getTreeNode().getData();
			editSMS(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			SmsNotifyTemplate item = (SmsNotifyTemplate) nodeSelectEvent.getTreeNode().getData();
			deleteSMS(item);
			loadSMSByCategory(item.getCategoryId());
		}
	}

	
	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		SmsNotifyTemplate sms = (SmsNotifyTemplate) event.getTreeNode().getData();
		Object object = smsNotiTemDAO.upDownObjectInCatWithDomain(sms, "posIndex", isUp);
		if (object instanceof SmsNotifyTemplate) {
			Category category = categoryDAO.get(sms.getCategoryId());
			SmsNotifyTemplate nextSMS = (SmsNotifyTemplate) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(sms);
			} else {
				treeCommonBean.moveDownTreeNode(sms);
			}
			treeCommonBean.updateTreeNode(nextSMS, category, null);
			if (formType == "list-sms" && nextSMS.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}

			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	// Validate
	private boolean validateSMS() {
		boolean result = true;
		if (smsNotiTemDAO.checkName(smsNotiTem, smsNotiTem.getName())) {
			this.showMessageWARN("smsNotifyTemplate.title", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	public void saveSMS() {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		Category cat = categoryDAO.get(smsNotiTem.getCategoryId());
		if (validateSMS()) {
			if (smsNotiTemDAO.saveSMS(smsNotiTem)) {
				this.showMessageINFO("common.save", "SMS Notify Template");
				treeCommonBean.updateTreeNode(smsNotiTem, cat, null);
				this.isEditting = true;
			}
		}
	}

	private void loadComboListLang() {
		LangDAO langDAO = new LangDAO();
		lstLang = langDAO.findAll("");
	}

	public void deleteSMS(SmsNotifyTemplate item) {
		try {
			smsNotiTemDAO.delete(item);
			smsNotiTems.remove(item);
			refreshCategories(category);
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			treeCommonBean.removeTreeNodeAll(item);
			this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
		} catch (Exception e) {
			throw e;
		}
	}

	public void editSMS(SmsNotifyTemplate smsNotifyTemplate) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("smsNotifyTemplate.title"));
		treeCommonBean.hideAllCategoryComponent();
		refreshSMS(smsNotifyTemplate);
		this.isEditting = false;
	}

	public void addNewSMS() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("smsNotifyTemplate.title"));
		smsNotiTem = new SmsNotifyTemplate();
		refreshSMS(smsNotiTem);
		smsNotiTem.setCategoryId(category.getCategoryId());
		this.isEditting = false;
	}

	public void refreshCategories(Category category) {
		formType = "list-sms";
		this.category = category;
		loadSMSByCategory(category.getCategoryId());
	}

	public void refreshSMS(SmsNotifyTemplate smsNotiTem) {
		try {
			this.smsNotiTem = smsNotiTem.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		setFormType("sms-detail");
		LoadCategoriesOfSMS();
		this.isEditting = true;
		loadComboListLang();
	}

	private List<Category> categoriesOfSMS;

	private void LoadCategoriesOfSMS() {
		categoriesOfSMS = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_SMS_NOTIFY_TEMPLATE);
	}

	public List<SmsNotifyTemplate> loadSMSByCategory(long categoryId) {
		smsNotiTems = new ArrayList<SmsNotifyTemplate>();
		smsNotiTems = smsNotiTemDAO.findSMSByCategoryId(categoryId);
		return smsNotiTems;
	}

	// *** GET-SET ***//

	public List<Category> getCategoriesOfSMS() {
		return categoriesOfSMS;
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

	public List<SmsNotifyTemplate> getSmsNotiTems() {
		return smsNotiTems;
	}

	public void setSmsNotiTems(List<SmsNotifyTemplate> smsNotiTems) {
		this.smsNotiTems = smsNotiTems;
	}

	public SmsNotifyTemplate getSmsNotiTem() {
		return smsNotiTem;
	}

	public void setSmsNotiTem(SmsNotifyTemplate smsNotiTem) {
		this.smsNotiTem = smsNotiTem;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<Lang> getLstLang() {
		return lstLang;
	}

	public void setLstLang(List<Lang> lstLang) {
		this.lstLang = lstLang;
	}

}

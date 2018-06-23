package com.viettel.ocs.bean.catalog;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.dao.CdrStdConfigDAO;
import com.viettel.ocs.entity.CdrStdConfig;
import com.viettel.ocs.entity.CdrStdIo;

@SuppressWarnings("serial")
@ManagedBean(name = "cdrStdConfigBean")
@ViewScoped
public class CdrStdConfigBean extends BaseController implements Serializable {
	private List<CdrStdConfig> lstCdrStdConfig;
	private CdrStdConfig cdrStdConfig;
	private CdrStdConfigDAO cdrStdConfigDAO;
	private Boolean isEdit;

	@PostConstruct
	public void init() {
		this.cdrStdConfig = new CdrStdConfig();
		this.cdrStdConfigDAO = new CdrStdConfigDAO();
		this.lstCdrStdConfig = new ArrayList<CdrStdConfig>();
		this.isEdit = false;
		loadCdrStdConfig();
	}
	
	public void addNewCdrStdConfig() {
		cdrStdConfig = new CdrStdConfig();
		this.isEdit = true;
	}
	
	public void applyCdrStdConfig() {
		if (validateCdrStdConfig()) {
			cdrStdConfigDAO.saveOrUpdate(cdrStdConfig);
			cdrStdConfig = new CdrStdConfig();
			this.isEdit = false;
			super.showNotificationSuccsess();
		}
	}
	
	private boolean validateCdrStdConfig() {
		boolean result = true;
//		if (cdrStdConfigDAO.checkCdrServiceId(cdrStdConfig, cdrStdConfig.getCdrServiceId())) {
//			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
//			result = false;
//		}
//
//		if (cdrStdConfigDAO.checkcdrTemplatedId(cdrStdConfig, cdrStdConfig.getCdrTemplatedId())) {
//			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
//			result = false;
//		}
		
		if (cdrStdConfigDAO.checkCdrStdConfigName(cdrStdConfig, cdrStdConfig.getCdrStdConfigName())) {
			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	public void btnCancel() {
		cdrStdConfig = new CdrStdConfig();
	}

	public void deleteCdrStdConfig(CdrStdConfig item) throws IOException {
		cdrStdConfigDAO.delete(item);
		cdrStdConfig = new CdrStdConfig();
		RequestContext.getCurrentInstance().execute("PF('wvCdrStdConfig').clearFilters();");
		loadCdrStdConfig();
		super.showNotificationSuccsess();
	}

	public void editCdrStdConfig(CdrStdConfig item) {
		this.cdrStdConfig = item;
		this.isEdit = true;
	}

	public List<CdrStdConfig> loadCdrStdConfig() {
		CdrStdConfigDAO cdrStdConfigDAO = new CdrStdConfigDAO();
		lstCdrStdConfig = cdrStdConfigDAO.findAllWithoutDomain("");
		return lstCdrStdConfig;
	}

	public List<CdrStdConfig> getLstCdrStdConfig() {
		return lstCdrStdConfig;
	}

	public void setLstCdrStdConfig(List<CdrStdConfig> lstCdrStdConfig) {
		this.lstCdrStdConfig = lstCdrStdConfig;
	}

	public CdrStdConfig getCdrStdConfig() {
		return cdrStdConfig;
	}

	public void setCdrStdConfig(CdrStdConfig cdrStdConfig) {
		this.cdrStdConfig = cdrStdConfig;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

}

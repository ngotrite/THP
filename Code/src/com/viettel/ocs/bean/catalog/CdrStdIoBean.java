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

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.dao.CdrStdIoDAO;
import com.viettel.ocs.entity.CdrStdIo;

@SuppressWarnings("serial")
@ManagedBean(name = "cdrStdIoBean")
@ViewScoped
public class CdrStdIoBean extends BaseController implements Serializable {
	private List<CdrStdIo> lstCdrStdIo;
	private CdrStdIo cdrStdIo;
	private CdrStdIoDAO cdrStdIoDAO;
	private Boolean isEdit;

	@PostConstruct
	public void init() {
		this.cdrStdIo = new CdrStdIo();
		this.cdrStdIoDAO = new CdrStdIoDAO();
		this.lstCdrStdIo = new ArrayList<CdrStdIo>();
		this.isEdit = false;
		loadCdrStdIo();
	}

	public void addNewCdrStdIo() {
		cdrStdIo = new CdrStdIo();
		this.isEdit = true;
	}

	public void applyCdrStdIo() {
		if (validateCdrStdIo()) {
			cdrStdIoDAO.saveOrUpdate(cdrStdIo);
			cdrStdIo = new CdrStdIo();
			this.isEdit = false;
			super.showNotificationSuccsess();
		}
	}

	private boolean validateCdrStdIo() {
		boolean result = true;
		if (cdrStdIoDAO.checkCdrStdConfigId(cdrStdIo, cdrStdIo.getCdrStdConfigId())) {
			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}

//		if (cdrStdIoDAO.checkPropertyId(cdrStdIo, cdrStdIo.getPropertyId())) {
//			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
//			result = false;
//		}
		return result;
	}

	public void btnCancel() {
		cdrStdIo = new CdrStdIo();
	}

	public void deleteCdrStdIo(CdrStdIo item) throws IOException {
		cdrStdIoDAO.delete(item);
		cdrStdIo = new CdrStdIo();
		RequestContext.getCurrentInstance().execute("PF('wvCdrStdIo').clearFilters();");
		loadCdrStdIo();
		super.showNotificationSuccsess();
	}
	
	public void editCdrStdIo(CdrStdIo item) {
		this.cdrStdIo = item;
		this.isEdit = true;
	}

	public List<CdrStdIo> loadCdrStdIo() {
		CdrStdIoDAO cdrStdIoDAO = new CdrStdIoDAO();
		lstCdrStdIo = cdrStdIoDAO.findAllWithoutDomain("");
		return lstCdrStdIo;
	}

	public List<CdrStdIo> getLstCdrStdIo() {
		return lstCdrStdIo;
	}

	public void setLstCdrStdIo(List<CdrStdIo> lstCdrStdIo) {
		this.lstCdrStdIo = lstCdrStdIo;
	}

	public CdrStdIo getCdrStdIo() {
		return cdrStdIo;
	}

	public void setCdrStdIo(CdrStdIo cdrStdIo) {
		this.cdrStdIo = cdrStdIo;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

}

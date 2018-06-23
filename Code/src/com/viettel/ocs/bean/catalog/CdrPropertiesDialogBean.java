package com.viettel.ocs.bean.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.CdrPropDAO;
import com.viettel.ocs.entity.CdrProp;

@ManagedBean(name = "cdrPropertiesDialogBean")
@ViewScoped
public class CdrPropertiesDialogBean extends BaseController {

	private List<CdrProp> selectedCdrProps;
	private List<CdrProp> cdrProps;
	private CdrPropDAO cdrPropDAO;
	private String selectMode = "multiple"; 
	private String selectedIds;

	public CdrPropertiesDialogBean() {
		init();
	}
	
	private void init() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		selectMode = params.get("selectMode");
		selectedIds = params.get("selectedIds");
		String[] ids = selectedIds.split(",");
		List<Long> lstSelectedID = new ArrayList<Long>();
		for(String id : ids) {
			lstSelectedID.add(Long.parseLong(id));
		}
		
		cdrPropDAO = new CdrPropDAO();
		cdrProps = cdrPropDAO.loadAllExclude(lstSelectedID);
	}

	public void onCancel() {
		RequestContext.getCurrentInstance().closeDialog(0);
	}

	public void onOk() {
		RequestContext.getCurrentInstance().closeDialog(selectedCdrProps);
	}

	public List<CdrProp> getCdrProps() {
		return cdrProps;
	}

	public void setCdrProps(List<CdrProp> cdrProps) {
		this.cdrProps = cdrProps;
	}

	public List<CdrProp> getSelectedCdrProps() {
		return selectedCdrProps;
	}

	public void setSelectedCdrProps(List<CdrProp> selectedCdrProps) {
		this.selectedCdrProps = selectedCdrProps;
	}

	public String getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(String selectMode) {
		this.selectMode = selectMode;
	}

}

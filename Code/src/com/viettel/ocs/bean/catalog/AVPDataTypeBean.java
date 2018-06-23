package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.AVPDataTypeDAO;
import com.viettel.ocs.dao.AVPDefineDAO;
import com.viettel.ocs.entity.AVPDataType;

@SuppressWarnings("serial")
@ManagedBean(name = "avpDataTypeBean")
@ViewScoped
public class AVPDataTypeBean extends BaseController implements Serializable {
	private List<AVPDataType> avpDataTypes;
	private AVPDataType avpDataType;
	private AVPDataTypeDAO avpDataTypeDAO;
	private Boolean isEdit;

	@PostConstruct
	public void init() {
		this.avpDataType = new AVPDataType();
		this.avpDataTypeDAO = new AVPDataTypeDAO();
		this.avpDataTypes = new ArrayList<AVPDataType>();
		this.isEdit = false;
	}

	public void addNewAVPDataType() {
		avpDataType = new AVPDataType();
		this.isEdit = true;
	}

	public List<AVPDataType> loadAvpDataTypes() {
		avpDataTypes = avpDataTypeDAO.findAllWithoutDomain("");
		return avpDataTypes;
	}

	public void applyAVPDataType() {
		if (validateAVPDataType()) {
			avpDataTypeDAO.saveOrUpdate(avpDataType);
			avpDataType = new AVPDataType();
			this.isEdit = false;
			super.showNotificationSuccsess();
		}else {
			
		}
	}

	// Validate
	private boolean validateAVPDataType() {
		boolean result = true;
		if (avpDataTypeDAO.checkName(avpDataType, avpDataType.getAvpDataTypeName())) {
			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	public void editAVPDataType(AVPDataType item) {
		this.avpDataType = item;
		this.isEdit = true;
	}

	public void deleteAVPDataType(AVPDataType item) {
		AVPDefineDAO avpDefineDAO = new AVPDefineDAO();
		if (!avpDefineDAO.checkAVPDataType(item.getAvpDataTypeId())) {
			avpDataTypeDAO.delete(item);
			super.showNotificationSuccsess();
			avpDataType = new AVPDataType();
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}

	}
	
	public void btnCancel() {
		this.avpDataType = new AVPDataType();
	}

	public List<AVPDataType> getAvpDataTypes() {
		return avpDataTypes;
	}

	public void setAvpDataTypes(List<AVPDataType> avpDataTypes) {
		this.avpDataTypes = avpDataTypes;
	}

	public AVPDataType getAvpDataType() {
		return avpDataType;
	}

	public void setAvpDataType(AVPDataType avpDataType) {
		this.avpDataType = avpDataType;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}
}

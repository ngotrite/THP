package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.AVPDataTypeDAO;
import com.viettel.ocs.dao.AVPDefineDAO;
import com.viettel.ocs.entity.AVPDataType;
import com.viettel.ocs.entity.AVPDefine;

@SuppressWarnings("serial")
@ManagedBean(name = "avpDefineBean")
@ViewScoped
public class AVPDefineBean extends BaseController implements Serializable {
	private List<AVPDefine> avpDefines;
	private AVPDefine avpDefine;
	private AVPDefineDAO avpDefineDAO;
	private Boolean isEdit;
	private List<AVPDataType> avpDataTypes;
	private List<SelectItem> lstInterface;

	@PostConstruct
	public void init() {
		this.avpDefine = new AVPDefine();
		this.avpDefineDAO = new AVPDefineDAO();
		this.avpDefines = new ArrayList<AVPDefine>();
		this.avpDataTypes = new ArrayList<AVPDataType>();
		this.isEdit = false;
		loadAvpDataTypes();
	}

	public List<SelectItem> loadComboInterface() {
		lstInterface = new ArrayList<SelectItem>();
		lstInterface.add(new SelectItem(com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gy,
				com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gy_NAME));
		lstInterface.add(new SelectItem(com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gx,
				com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gx_NAME));
		lstInterface.add(new SelectItem(com.viettel.ocs.constant.ContantsUtil.InterfaceType.Sy,
				com.viettel.ocs.constant.ContantsUtil.InterfaceType.Sy_NAME));
		lstInterface.add(new SelectItem(com.viettel.ocs.constant.ContantsUtil.InterfaceType.Rx,
				com.viettel.ocs.constant.ContantsUtil.InterfaceType.Rx_NAME));
		lstInterface.add(new SelectItem(com.viettel.ocs.constant.ContantsUtil.InterfaceType.COMMON,
				com.viettel.ocs.constant.ContantsUtil.InterfaceType.COMMON_NAME));
		return lstInterface;
	}

	public void addNewAVPDefine() {
		avpDefine = new AVPDefine();
		this.isEdit = true;
	}

	public List<AVPDefine> loadAvpDefines() {
		avpDefines = avpDefineDAO.findAllWithoutDomain("");
		return avpDefines;
	}

	public String getInterfaceName(Long avpDefineId) {
		if (avpDefineDAO.get(avpDefineId) != null && avpDefineDAO.get(avpDefineId).get_interface() != null) {
			if (avpDefineDAO.get(avpDefineId)
					.get_interface() == com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gy) {
				return com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gy_NAME;
			}
			if (avpDefineDAO.get(avpDefineId)
					.get_interface() == com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gx) {
				return com.viettel.ocs.constant.ContantsUtil.InterfaceType.Gx_NAME;
			}
			if (avpDefineDAO.get(avpDefineId)
					.get_interface() == com.viettel.ocs.constant.ContantsUtil.InterfaceType.Sy) {
				return com.viettel.ocs.constant.ContantsUtil.InterfaceType.Sy_NAME;
			}
			if (avpDefineDAO.get(avpDefineId)
					.get_interface() == com.viettel.ocs.constant.ContantsUtil.InterfaceType.Rx) {
				return com.viettel.ocs.constant.ContantsUtil.InterfaceType.Rx_NAME;
			}
			if (avpDefineDAO.get(avpDefineId)
					.get_interface() == com.viettel.ocs.constant.ContantsUtil.InterfaceType.COMMON) {
				return com.viettel.ocs.constant.ContantsUtil.InterfaceType.COMMON_NAME;
			}
		}

		return null;
	}

	public void loadAvpDataTypes() {
		AVPDataTypeDAO avpDataTypeDAO = new AVPDataTypeDAO();
		avpDataTypes = avpDataTypeDAO.findAllWithoutDomain("");
	}

	public void applyAVPDefine() {
		if (validateAVPDefine()) {
			avpDefineDAO.saveOrUpdate(avpDefine);
			avpDefine = new AVPDefine();
			this.isEdit = false;
			super.showNotificationSuccsess();
		}
	}

	// Validate
	private boolean validateAVPDefine() {
		boolean result = true;
		if (avpDefineDAO.checkName(avpDefine, avpDefine.getAvpDefineName())) {
			this.showMessageWARN("", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}
		return result;
	}

	public void editAVPDefine(AVPDefine item) {
		this.avpDefine = item;
		this.isEdit = true;
	}

	public void deleteAVPDefine(AVPDefine item) {
		avpDefineDAO.delete(item);
		avpDefine = new AVPDefine();
		super.showNotificationSuccsess();
	}

	public void btnCancel() {
		avpDefine = new AVPDefine();
	}

	public String getAVPTypeName(Long avpDataTypeId) {
		AVPDataTypeDAO avpDataTypeDAO = new AVPDataTypeDAO();
		return avpDataTypeDAO.get(avpDataTypeId).getAvpDataTypeName();
	}

	public List<AVPDefine> getAvpDefines() {
		return avpDefines;
	}

	public void setAvpDefines(List<AVPDefine> avpDefines) {
		this.avpDefines = avpDefines;
	}

	public AVPDefine getAvpDefine() {
		return avpDefine;
	}

	public void setAvpDefine(AVPDefine avpDefine) {
		this.avpDefine = avpDefine;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public List<AVPDataType> getAvpDataTypes() {
		return avpDataTypes;
	}

	public void setAvpDataTypes(List<AVPDataType> avpDataTypes) {
		this.avpDataTypes = avpDataTypes;
	}

}

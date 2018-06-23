package com.viettel.ocs.bean.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.MonitorKeyDAO;
import com.viettel.ocs.dao.ProfilePepDAO;
import com.viettel.ocs.entity.MonitorKey;
import com.viettel.ocs.entity.ProfilePep;

@ManagedBean(name = "monitorKeyBean")
@ViewScoped
public class MonitorKeyBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MonitorKey monitorKeys;
	private MonitorKey monitorKeysSafe;
	private List<MonitorKey> listMKey;
	private boolean isEditting;
	private boolean showAddNew;
	private MonitorKeyDAO mkDAO;
	private ProfilePepDAO pepDAO;
	private boolean checkNew;

	public MonitorKey getMonitorKeys() {
		return monitorKeys;
	}

	public void setMonitorKeys(MonitorKey monitorKeys) {
		this.monitorKeys = monitorKeys;
	}

	public List<MonitorKey> getListMKey() {
		return listMKey;
	}

	public void setListMKey(List<MonitorKey> listMKey) {
		this.listMKey = listMKey;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public MonitorKeyDAO getMkDAO() {
		return mkDAO;
	}

	public void setMkDAO(MonitorKeyDAO mkDAO) {
		this.mkDAO = mkDAO;
	}

	public boolean isShowAddNew() {
		return showAddNew;
	}

	public void setShowAddNew(boolean showAddNew) {
		this.showAddNew = showAddNew;
	}

	public MonitorKeyBean() {
		checkNew = false;
		showAddNew = true;
		this.isEditting = false;
		monitorKeys = new MonitorKey();
		mkDAO = new MonitorKeyDAO();
		pepDAO = new ProfilePepDAO();
		listMKey = new ArrayList<MonitorKey>();
		initRows();
	}

	private void initRows() {
		if (listMKey != null && listMKey.size() <= 0)
			listMKey = mkDAO.findAllWithoutDomain("");
	}

	public void btnSave() {
		if (validate()) {
			if (!checkDoubleMonitorKey(monitorKeys)) {
				mkDAO.saveOrUpdate(monitorKeys);
				boolean checkAdd = true;
				for (int i = 0; i < listMKey.size(); i++) {
					if (listMKey.get(i).getMonitorKeyId() == monitorKeys.getMonitorKeyId()) {
						listMKey.remove(listMKey.get(i));
						listMKey.add(i, monitorKeys);
						checkAdd = false;
					}
				}
				if (checkAdd)
					listMKey.add(monitorKeys);
				try {
					monitorKeysSafe = monitorKeys.clone();
				} catch (CloneNotSupportedException e) {
					getLogger().warn(e.getMessage(), e);
				}
				monitorKeys = new MonitorKey();
				isEditting = false;
				//clearFilter();
				this.showMessageINFO("common.save", this.readProperties("policy.monitor_keys"));
			} else {
				listMKey = mkDAO.findAllWithoutDomain("");
				this.showMessageWARN("common.save", this.readProperties("policy.monitor_keys"),
						this.readProperties("policy.errorMonitor"));
				return;
			}
		}
	}

	private boolean checkDoubleMonitorKey(MonitorKey monitorKey) {
		List<MonitorKey> lstPPByPPName = mkDAO.loadMonitorKeyByMonitorKey(monitorKey.getMonitorKeyId(),
				monitorKey.getMonitorKey());
		if (lstPPByPPName != null && lstPPByPPName.size() > 0)
			return true;
		else
			return false;
	}

	private boolean validate() {
		if (monitorKeys != null) {
			if (monitorKeys.getMonitorKey() < 0) {
				this.showMessageWARN("common.save", " Qos ", this.readProperties("policy.err_monitorKey"));
				return false;
			}
		}
		return true;
	}

	public void btnNew() {
		checkNew = true;
		isEditting = true;
		showAddNew = false;
		this.monitorKeys = new MonitorKey();
		
	}

	public void btnCancel() {
		this.monitorKeys = new MonitorKey();
		isEditting = false;
	}

	// EVENT LIST
	public void editMK(MonitorKey monitorKey) {
		this.monitorKeys = monitorKey;
		try {
			monitorKeysSafe = monitorKey.clone();
		} catch (CloneNotSupportedException e) {
			getLogger().warn(e.getMessage(), e);
		}
		isEditting = true;
		showAddNew = false;
		checkNew = false;
	}

	public void deleteMK(MonitorKey monitorKey) {
		isEditting = false;
		List<ProfilePep> lstPP = pepDAO.findProfilePepByMonitorID(monitorKey.getMonitorKeyId());
		if (lstPP.size() > 0) {
			this.showMessageWARN("common.delete", this.readProperties("policy.title_monitor_key"),this.readProperties("policy.title_monitor_key_use"));
			return;
		} else {
			for (int i = 0; i < listMKey.size(); i++) {
				if (monitorKey.getMonitorKeyId() == listMKey.get(i).getMonitorKeyId()) {
					listMKey.remove(i);
					mkDAO.delete(monitorKey);
					monitorKeys = new MonitorKey();
					this.monitorKeys = new MonitorKey();
					isEditting = false;
					clearFilter();
					//initRows();
					this.showMessageINFO("common.delete", this.readProperties("policy.title_monitor_key"));
					break;
				}
			}
		}
		this.monitorKeys = new MonitorKey();
	}

	private void clearFilter() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('wgMKey').clearFilters()");
	}
}

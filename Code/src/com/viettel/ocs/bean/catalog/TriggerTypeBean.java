package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.dao.TriggerTypeDAO;
import com.viettel.ocs.entity.TriggerType;




@ManagedBean(name = "triggerTypeBean", eager = true)
@ViewScoped
public class TriggerTypeBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private TriggerType triggerType = new TriggerType();
	
	TriggerTypeDAO triggerTypeDAO = new TriggerTypeDAO();
	
	public List<TriggerType> getTriggerTypeList() {

		return triggerTypeDAO.findAll("");
	}
	
	public void saveTriggerType() {

		triggerTypeDAO.saveOrUpdate(triggerType);
		getTriggerTypeList();
	}
	
	public void deleteTriggerType(TriggerType item) {
		triggerTypeDAO.delete(item);
		getTriggerTypeList();
	}
	
	
	public void editTriggerType(TriggerType item) {
		this.triggerType = item;

	}

	public void cancelTriggerType() {
		triggerType = new TriggerType();
	}

	
	
	public TriggerType getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}
	
	

}

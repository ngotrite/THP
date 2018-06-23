package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.SysDomainDAO;
import com.viettel.ocs.entity.SysDomain;

//@Component
@ManagedBean(name="domainBean")
@ViewScoped
public class DomainBean extends BaseController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219556076115932261L;
	private SysDomain SysDomain = new SysDomain();
	private List<SysDomain> listSysDomain;
	private boolean isEditing;
	
	// Spring DI
	//@Autowired
	private SysDomainDAO domainDao;
	
	public DomainBean() {
		
		domainDao = new SysDomainDAO();
		init();
		searchDomain();
	}
	
	private void init() {
		SysDomain = new SysDomain();
	}
	
	public void searchDomain() {
		
		listSysDomain = domainDao.findAll("");
	}
		
	public void btnNew() {
		
		init();
		isEditing = true;
	}

	public void btnCancel() {
		
		init();
		isEditing = false;
	}
	
	public void btnSave() {
		
		if(!validateSave())
			return;
	
		if(SysDomain.getId() > 0 ) {
			
			domainDao.update(SysDomain);
		} else {
			
			domainDao.save(SysDomain);
			listSysDomain.add(SysDomain);
		}
		
		btnCancel();
		this.showMessageINFO("common.save", " Domain ");
	}
	
	private boolean validateSave() {
		
		SysDomain checkObj = domainDao.findByName(SysDomain.getName(), SysDomain.getId());
		if(checkObj != null) {
			
			super.showMessageERROR("common.save", " Domain ", "common.duplicateName");
			return false;
		}
		
		return true;
	} 
	
	public void onRowSelect(SelectEvent event) {
//		sysRole = (SysDomain) event.getObject();
	}
	
	public void onRowEdit(SysDomain domain) {
		
		this.SysDomain = domain;
		isEditing = true;
	}
	
	public void onRowDelete(SysDomain domain) {
		
		if(this.SysDomain.getId() == domain.getId()) {
			btnCancel();
		}
		domainDao.delete(domain);
		listSysDomain.remove(domain);
	}
	
	public void commandClone (SysDomain sysDomain) throws CloneNotSupportedException{
		if (sysDomain != null) {
			SysDomain sysDomainCloned = domainDao.cloneSysDomain(sysDomain, "_Cloned");
			if (sysDomainCloned != null) {
				onRowEdit(sysDomainCloned);
				listSysDomain.add(sysDomainCloned);
				this.showMessageINFO("common.clone", " Domain");
			}
		}
	}
	
	/** GET SET**/
	public SysDomain getSysDomain() {
		return SysDomain;
	}

	public void setSysDomain(SysDomain SysDomain) {
		this.SysDomain = SysDomain;
	}
				
	public List<SysDomain> getListSysDomain() {
		return listSysDomain;
	}

	public void setListSysDomain(List<SysDomain> listSysDomain) {
		this.listSysDomain = listSysDomain;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}
}

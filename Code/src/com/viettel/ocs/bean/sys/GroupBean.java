package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.SysGroupDAO;
import com.viettel.ocs.dao.SysUserDAO;
import com.viettel.ocs.entity.SysGroup;
import com.viettel.ocs.entity.SysUser;

//@Component
@ManagedBean(name="groupBean")
@ViewScoped
public class GroupBean extends BaseController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219556076115932261L;
	private SysGroup sysGroup = new SysGroup();
	private List<SysGroup> listSysGroup;
	private List<SysUser> listSysUser;
	private boolean isEditing;
	
	// Spring DI
	//@Autowired
	private SysGroupDAO groupDao;
	private SysUserDAO userDao;
	
	public GroupBean() {
		
		groupDao = new SysGroupDAO();
		userDao = new SysUserDAO();
		init();
		searchGroup();
	}
	
	private void init() {
		
		sysGroup = new SysGroup();
		sysGroup.setIsActive(true);
		listSysUser = new ArrayList<>();
	}
	
	public void searchGroup() {
		
		listSysGroup = groupDao.findAll("");
	}
	
	public void searchUser() {
		
		listSysUser = userDao.findByGroup(sysGroup.getId());
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
			
		if(sysGroup.getId() > 0 ) {
			
			groupDao.saveOrUpdate(sysGroup, listSysUser);
		} else {
			
			groupDao.saveOrUpdate(sysGroup, listSysUser);
			listSysGroup.add(sysGroup);
		}
		
		btnCancel();
		this.showMessageINFO("common.save", " Group ");
	}
	
	private boolean validateSave() {
		
		SysGroup checkObj = groupDao.findByName(sysGroup.getName(), sysGroup.getId());
		if(checkObj != null) {
			
			super.showMessageERROR("common.save", " Group ", "common.duplicateName");
			return false;
		}
		
		return true;
	} 
	
	public void onRowSelect(SelectEvent event) {
//		sysRole = (SysGroup) event.getObject();
	}
	
	public void onRowEdit(SysGroup group) {
		
		this.sysGroup = group;
		this.searchUser();
		isEditing = true;
	}
	
	public void onRowDelete(SysGroup group) {
		
		if(this.sysGroup.getId() == group.getId()) {
			btnCancel();
		}
		groupDao.delete(group);
		listSysGroup.remove(group);
	}
	
	/** DIALOG - BEGIN **/
	private List<SysUser> listSysUserDlg;
	private List<SysUser> selectedUsers;
	public void btnShowSelectUserDlg() {
		
		listSysUserDlg = userDao.findAll("");
	}
	
	public void btnSelectUserDlg() {
		
		if(selectedUsers != null) {
			for(SysUser objUser: selectedUsers) {	
				
				boolean found = false;
				for(SysUser selected: listSysUser) {
					
					if(objUser.getId() == selected.getId()) {
						found = true;
						break;
					}
				}
				
				if(!found) {
					listSysUser.add(objUser);
				}
			}
		}
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgSelectUserOfGroup').hide();");
	}
	
	public void removeUser(SysUser objUser) {
		
		listSysUser.remove(objUser);
	}
		
	/** DIALOG - END **/
	
	/** GET SET**/
	public SysGroup getSysGroup() {
		return sysGroup;
	}

	public void setSysGroup(SysGroup sysGroup) {
		this.sysGroup = sysGroup;
	}
				
	public List<SysGroup> getListSysGroup() {
		return listSysGroup;
	}

	public void setListSysGroup(List<SysGroup> listSysGroup) {
		this.listSysGroup = listSysGroup;
	}

	public List<SysUser> getListSysUser() {
		return listSysUser;
	}

	public void setListSysUser(List<SysUser> listSysUser) {
		this.listSysUser = listSysUser;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public List<SysUser> getListSysUserDlg() {
		return listSysUserDlg;
	}

	public void setListSysUserDlg(List<SysUser> listSysUserDlg) {
		this.listSysUserDlg = listSysUserDlg;
	}

	public List<SysUser> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<SysUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}

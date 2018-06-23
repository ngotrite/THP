package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.component.picklist.PickList;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.Setting;
import com.viettel.ocs.dao.SysGroupDAO;
import com.viettel.ocs.dao.SysMenuDAO;
import com.viettel.ocs.dao.SysRoleDAO;
import com.viettel.ocs.dao.SysUserDAO;
import com.viettel.ocs.entity.SysGroup;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SysRole;
import com.viettel.ocs.entity.SysUser;

//@Component
@ManagedBean(name="roleBean")
@ViewScoped
public class RoleBean extends BaseController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219556076115932261L;
	private SysRole sysRole = new SysRole();
	private boolean isEditing;
	private List<SysRole> listSysRole;
	private List<SysGroup> listSysGroup;
	private List<SysUser> listSysUser;
	
	private List<SysMenu> lstSysMenuAll;
	private DualListModel<SysMenu> dlSysMenu;
		
	// Spring DI
	//@Autowired
	private SysRoleDAO roleDao;
	private SysMenuDAO menuDao;
	private SysUserDAO userDao;
	private SysGroupDAO groupDao;
	
	public RoleBean() {
		
		roleDao = new SysRoleDAO();
		menuDao = new SysMenuDAO();
		groupDao = new SysGroupDAO();
		userDao = new SysUserDAO();
				
		init();
		searchRole();
	}
	
	private void init() {
		
		sysRole = new SysRole();		
		sysRole.setIsActive(true);
		listSysUser = new ArrayList<>();
		listSysGroup = new ArrayList<>();
		
		searchMenu();
	}
	
	private void searchRole() {
		
		listSysRole = roleDao.findAll("");
	}
	
	private void searchMenu() {
				
		if(lstSysMenuAll == null)
			lstSysMenuAll = menuDao.findAll(true);
		
		List<SysMenu> lstTarget = menuDao.findByRole(sysRole.getId(), false, true);
		List<SysMenu> lstSource = new ArrayList<>();
		lstSource.addAll(lstSysMenuAll);
		dlSysMenu = new DualListModel<>(lstSource, lstTarget);
	}
	
	private void searchGroup() {
		
		listSysGroup = groupDao.findByRole(sysRole.getId());
	}
	
	private void searchUser() {
		
		listSysUser = userDao.findByRole(sysRole.getId());
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
	
		List<SysMenu> lstTarget = dlSysMenu.getTarget();		
		if(sysRole.getId() > 0 ) {
			
			roleDao.saveOrUpdate(sysRole, listSysUser, listSysGroup, lstTarget);			
		} else {
		
			roleDao.saveOrUpdate(sysRole, listSysUser, listSysGroup, lstTarget);
			listSysRole.add(sysRole);
		}
		
		btnCancel();
		this.showMessageINFO("common.save", " Role ");
	}
	
	private boolean validateSave() {
		
		SysRole checkObj = roleDao.findByName(sysRole.getName(), sysRole.getId());
		if(checkObj != null) {
			
			super.showMessageERROR("common.save", " Role ", "common.duplicateName");
			return false;
		}
		
		return true;
	}
	
	public void onMenuTransfer(TransferEvent event) {
        
		List<SysMenu> lstTransfer = (List<SysMenu>) event.getItems();
		if(lstTransfer.isEmpty())
			return;
		
		List<SysMenu> lstSource = dlSysMenu.getSource();
		List<SysMenu> lstTarget = dlSysMenu.getTarget();
		
		if(lstTarget.contains(lstTransfer.get(0))) {
			// Move xuoi, neu move con thi move ca cha
			
			for(SysMenu menuTransfer : lstTransfer) {
				
				Iterator<SysMenu> it = lstSource.iterator();
				while(it.hasNext()) {
					
					SysMenu menuSource = it.next();
					if(menuSource != menuTransfer && !lstTarget.contains(menuSource) 
							&& menuTransfer.getPath().contains("/" + menuSource.getId() + "/")) {
						
						lstTarget.add(menuSource);
					}
				}
			}
						
			List<SysMenu> lstTarget2 = new ArrayList<>();
			menuDao.sortTree(null, lstTarget, lstTarget2);			
			dlSysMenu.setTarget(lstTarget2);						
		} else {
			// Move nguoc thi chi xoa ben target
			
			for(SysMenu menuTransfer : lstTransfer) {
				
				Iterator<SysMenu> it = lstTarget.iterator();
				while(it.hasNext()) {
					
					SysMenu menuTarget = it.next();
					if(menuTarget != menuTransfer && menuTarget.getPath().contains("/" + menuTransfer.getId() + "/")) {
						
						it.remove();
					}
				}
			}
			
			List<SysMenu> lstTarget2 = new ArrayList<>();
			menuDao.sortTree(null, lstTarget, lstTarget2);			
			dlSysMenu.setTarget(lstTarget2);
		}
		
		lstSource.clear();
		lstSource.addAll(lstSysMenuAll);
    }
 
    public void onMenuSelect(SelectEvent event) {
        
    }
     
    public void onMenuUnselect(UnselectEvent event) {
        
    }
     
    public void onMenuReorder() {
        
    } 
	
	public void onRowSelect(SelectEvent event) {
//		sysRole = (SysRole) event.getObject();
	}
	
	public void onRowEdit(SysRole role) {
		
		this.sysRole = role;
		searchMenu();
		searchGroup();
		searchUser();		
		isEditing = true;
	}
	
	public void onRowDelete(SysRole role) {
		
		if(this.sysRole.getId() == role.getId()) {
			btnCancel();
		}
		roleDao.delete(role);
		listSysRole.remove(role);
	}
	
	/** DIALOG - BEGIN **/
	private List<SysUser> listSysUserDlg;
	private List<SysUser> selectedUsers;
	
	private List<SysGroup> listSysGroupDlg;
	private List<SysGroup> selectedGroups;
	
	/*--------------------- USER ---------------*/
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
		context.execute("PF('dlgSelectUserOfRole').hide();");
	}
	
	public void removeUser(SysUser objUser) {
		
		listSysUser.remove(objUser);
	}
	
	/*--------------------- GROUP ---------------*/
	public void btnShowSelectGroupDlg() {
		
		listSysGroupDlg = groupDao.findAll("");
	}
	
	public void btnSelectGroupDlg() {
		
		if(selectedGroups != null) {
			for(SysGroup objGroup : selectedGroups) {	
				
				boolean found = false;
				for(SysGroup selected: listSysGroup) {
					
					if(objGroup.getId() == selected.getId()) {
						found = true;
						break;
					}
				}
				
				if(!found) {
					listSysGroup.add(objGroup);
				}
			}
		}
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgSelectGroupOfRole').hide();");
	}
	
	public void removeGroup(SysGroup objGroup) {
		
		listSysGroup.remove(objGroup);
	}
	
	/** DIALOG - END **/
	
	/** GET SET**/
	public SysRole getSysRole() {
		return sysRole;
	}

	public void setSysRole(SysRole sysRole) {
		this.sysRole = sysRole;
	}

	public Map<String, String> getRoleTypeList() {
		return Setting.RoleType.listType;
	}
	
	public String getRoleTypeLabel(String value) {
		return Setting.RoleType.listType.get(value);
	}
		
	public List<SysRole> getListSysRole() {
		return listSysRole;
	}

	public void setListSysRole(List<SysRole> listSysRole) {
		this.listSysRole = listSysRole;
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

	public List<SysGroup> getListSysGroupDlg() {
		return listSysGroupDlg;
	}

	public void setListSysGroupDlg(List<SysGroup> listSysGroupDlg) {
		this.listSysGroupDlg = listSysGroupDlg;
	}

	public List<SysGroup> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<SysGroup> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public DualListModel<SysMenu> getDlSysMenu() {
		return dlSysMenu;
	}

	public void setDlSysMenu(DualListModel<SysMenu> dlSysMenu) {
		this.dlSysMenu = dlSysMenu;
	}
}

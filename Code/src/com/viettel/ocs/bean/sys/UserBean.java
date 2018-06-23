package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.dao.BaseDAO;
import com.viettel.ocs.dao.LangDAO;
import com.viettel.ocs.dao.SysRoleDAO;
import com.viettel.ocs.dao.SysUserDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Lang;
import com.viettel.ocs.entity.SysRole;
import com.viettel.ocs.entity.SysUser;
import com.viettel.ocs.util.CommonUtil;
import com.viettel.ocs.util.LocaleUtils;
import com.viettel.ocs.util.PasswordUtil;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "userBean")
@ViewScoped
public class UserBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7569728160086294584L;
	private SysUser sysUser = new SysUser();
	private String password;
	private List<SysUser> listSysUser;
	private List<SysRole> listSysRole;
	private List<Lang> listLang;
//	private long roleID;
	private boolean isEditing;

	private SysUserDAO userDao;

	public UserBean() {

		userDao = new SysUserDAO();
		init();
		searchUser();
	}

	public SysUser getSysUser() {
		return sysUser;
	}

	private void init() {

		sysUser = new SysUser();
		sysUser.setIsActive(true);
		searchRole();
		searchLang();
	}

	private void searchUser() {
		SysUserDAO baseDAO = new SysUserDAO();
		listSysUser = baseDAO.findAll("");
	}

	private void searchRole() {

		SysRoleDAO roleDao = new SysRoleDAO();
		listSysRole = roleDao.findAll("");
	}
	
	private void searchLang(){
		LangDAO langDAO = new LangDAO();
		listLang = langDAO.findAll("");
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

		if(!this.validateSave())
			return;
				
		if (sysUser.getId() > 0) {

			userDao.update(sysUser);
		} else {

			userDao.save(sysUser);
			listSysUser.add(sysUser);
		}

		btnCancel();
		this.showMessageINFO("common.save", " User ");
	}

	private boolean validateSave() {
		
		SysUser checkObj = userDao.findByUserName(sysUser.getUserName(), sysUser.getId());
		if(checkObj != null) {
			
			super.showMessageERROR("common.save", " User ", "common.duplicateName");
			return false;
		}
		
		if(sysUser.getId() <= 0) {
			// Neu them moi thi bat buoc validate password
			
			if(!PasswordUtil.validatePassword(password)) {
				
				super.showMessageERROR("common.save", " User ", "user.formatPassword");
				return false;
			} else {
				String salt = PasswordUtil.getRandomSalt();
				sysUser.setSalt(salt);
				sysUser.setPassword(PasswordUtil.generateHash(password, salt));
				sysUser.setLastChangePassword(Calendar.getInstance().getTime());
			}
		} else {
			// Neu update thi chi validate password khi doi password
			
			if(!CommonUtil.isEmpty(password)) {				
				if(!PasswordUtil.validatePassword(password)) {
					
					super.showMessageERROR("common.save", " User ", "user.formatPassword");
					return false;
				} else {
					String salt = PasswordUtil.getRandomSalt();
					sysUser.setSalt(salt);
					sysUser.setPassword(PasswordUtil.generateHash(password, salt));
					sysUser.setLastChangePassword(Calendar.getInstance().getTime());
				}
			} else {
				// Do nothing
			}
		}
		
		Date nowDate = new Date();
		if (sysUser.getExpireDate() != null && sysUser.getExpireDate().getDate() < nowDate.getDate()) {
			super.showMessageWARN("validate.checkDateNow", "");
			return false;
		}
		
		return true;
	}
	
	public String colorTheme(){
		String color = "";
		if(ValidateUtil.checkStringNullOrEmpty(sysUser.getTheme())){
			sysUser.setTheme(ContantsUtil.Theme.DEFAULT_THEME);
		}
		switch (sysUser.getTheme()) {
		case ContantsUtil.Theme.DEFAULT_THEME:
			color = ContantsUtil.Theme.DEFAULT_THEME_COLOR;
			break;
		case ContantsUtil.Theme.BLUE_THEME:
			color = ContantsUtil.Theme.BLUE_THEME_COLOR;
			break;
		case ContantsUtil.Theme.SEA_THEME:
			color = ContantsUtil.Theme.SEA_THEME_COLOR;
			break;
		case ContantsUtil.Theme.PURPLE_THEME:
			color = ContantsUtil.Theme.PURPLE_THEME_COLOR;
			break;
		case ContantsUtil.Theme.ORANGE_THEME:
			color = ContantsUtil.Theme.ORANGE_THEME_COLOR;
			break;
		default:
			break;
		}
		
		return color;
	}
	
	public void changeTheme(String theme){
		this.sysUser.setTheme(theme);
	}
		
	public void onRowSelect(SelectEvent event) {
		// sysUser = (SysUser) event.getObject();
		// roleID = sysUser.getRole().getId();
	}

	public void onRowEdit(SysUser user) {

		this.sysUser = user;
//		roleID = sysUser.getRole().getId();
		isEditing = true;
	}

	public void onRowDelete(SysUser user) {
		
		if (user.getId() != 0L && (user.getId() != SessionUtils.getUser().getId())) {
			if (this.sysUser.getId() == user.getId()) {
				btnCancel();
			}
			userDao.delete(user);
			listSysUser.remove(user);
			super.showNotificationSuccsess();
		}else {
			super.showMessageWARN("validate.accUsed", "");
		}

	}

	/** GET SET **/
	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<SysUser> getListSysUser() {
		return listSysUser;
	}

	public void setListSysUser(List<SysUser> listSysUser) {
		this.listSysUser = listSysUser;
	}

//	public long getRoleID() {
//		return roleID;
//	}
//
//	public void setRoleID(long roleID) {
//		this.roleID = roleID;
//	}

	public List<SysRole> getListSysRole() {
		return listSysRole;
	}

	public void setListSysRole(List<SysRole> listSysRole) {
		this.listSysRole = listSysRole;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public List<Lang> getListLang() {
		return listLang;
	}

	public void setListLang(List<Lang> listLang) {
		this.listLang = listLang;
	}
	
}

package com.viettel.ocs.bean.sys;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.dao.LangDAO;
import com.viettel.ocs.dao.SysRoleDAO;
import com.viettel.ocs.dao.SysUserDAO;
import com.viettel.ocs.entity.Lang;
import com.viettel.ocs.entity.SysRole;
import com.viettel.ocs.entity.SysUser;
import com.viettel.ocs.util.PasswordUtil;

@ManagedBean(name = "userProfileBean")
@SessionScoped
public class UserProfileBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = 9089358585644225640L;
	private SysUser user;
	private SysUser sysUserPass;
	private String password;
	private String newPassword;
	private String ReenterPassword;
	private String role;
	private SysRoleDAO sysRoleDAO;
private SysUserDAO sysUserDAO;
	private List<Lang> listLang;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getReenterPassword() {
		return ReenterPassword;
	}

	public void setReenterPassword(String reenterPassword) {
		ReenterPassword = reenterPassword;
	}

	public SysUser getUser() {
		return user;
	}

	public void setUser(SysUser user) {
		this.user = user;
	}

	public List<Lang> getListLang() {
		return listLang;
	}

	public void setListLang(List<Lang> listLang) {
		this.listLang = listLang;
	}

	@PostConstruct
	private void init() {
		user = new SysUser();
		sysUserPass = new SysUser();
		searchLang();
	}

	public void getUserByLogin() {
		user = SessionUtils.getUser();
	}

	private void searchLang(){
		LangDAO langDAO = new LangDAO();
		listLang = langDAO.findAll("");
	}
	
	public void btnSave() {
		SysUserDAO sysUserDAO = new SysUserDAO();
		sysUserDAO.saveOrUpdate(user);
		this.showMessageINFO("common.save", " User profile");
	}

	public void doChangePassword() {
		if (validateSavePass()) {
			
			SysUserDAO userDao = new SysUserDAO();
			
			sysUserPass = userDao.findByUserName(SessionUtils.getUserName(), 0);
			String salt = PasswordUtil.getRandomSalt();
			sysUserPass.setSalt(salt);
			sysUserPass.setPassword(PasswordUtil.generateHash(newPassword, salt));
			sysUserPass.setLastChangePassword(Calendar.getInstance().getTime());
			userDao.saveOrUpdate(sysUserPass);
			
			this.showMessageINFO("common.save", " Change Password ");
			HttpSession session = SessionUtils.getSession();
			session.invalidate();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest req = (HttpServletRequest) ec.getRequest();
			try {
				ec.redirect(req.getContextPath() + "/login.xhtml");
			} catch (IOException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	private boolean validateSavePass() {
		
		if(password == null || newPassword == null || ReenterPassword == null) {
			
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "changepass.enterAllPass");
			return false;
		}
		
		if(!newPassword.equals(ReenterPassword)) {
			
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "changepass.notMatch");
			return false;
		}
		

		if (!PasswordUtil.validatePassword(newPassword)) {
			super.showMessageWARN("common.save", " Password ", "user.formatPassword");
			return false;
		}
		
		SysUserDAO sysUserDAO = new SysUserDAO();
		SysUser sysUser = sysUserDAO.findByUserName(SessionUtils.getUserName(), 0);
		if (sysUser == null || !sysUser.getPassword().equalsIgnoreCase(PasswordUtil.generateHash(password, sysUser.getSalt()))) {
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "changepass.passwordincorrect");
			return false;
		}

		if (newPassword.equals(password)) {
			super.showMessageWARN("common.save", " error ", "user.passUsed");
			return false;
		}
		
		return true;
	}

}

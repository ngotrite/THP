package com.viettel.ocs.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.event.SelectEvent;

import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.dao.LangDAO;
import com.viettel.ocs.dao.SysDomainDAO;
import com.viettel.ocs.dao.SysMenuDAO;
import com.viettel.ocs.dao.SysUserDAO;
import com.viettel.ocs.entity.Lang;
import com.viettel.ocs.entity.SysDomain;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SysMenuTrl;
import com.viettel.ocs.entity.SysUser;
import com.viettel.ocs.util.CommonUtil;
import com.viettel.ocs.util.ConfigUtil;
import com.viettel.ocs.util.DatetimeUtil;
import com.viettel.ocs.util.PasswordUtil;

@ManagedBean(name = "userSession")
@ViewScoped
public class UserSession extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private Locale locale;
	private long domainId;
	private long domainIdTmp;
	private String userName;
	private String password;
	private List<SysDomain> lstDomain;
	private List<Lang> listLang;
	private SysUser sysUser;

	private SysUserDAO userDao;
	private SysMenuDAO menuDao;
	private SysDomainDAO domainDao;
	private List<SysMenu> lstRootMenu;
	private List<SysMenu> lstMenuOfUser;
	
	private boolean showChangePassword;
	private String newPassword;
	private String newPasswordConfirm;
	private String captchaText;
	private int loginCount;
	
	public UserSession() {

		userDao = new SysUserDAO();
		menuDao = new SysMenuDAO();
		domainDao = new SysDomainDAO();
		lstRootMenu = new ArrayList<>();
		lstMenuOfUser = new ArrayList<>();
		searchDomain();
		searchLang();
	}

	public void searchDomain() {

		lstDomain = domainDao.findAllActive(true);
		if(lstDomain.size() > 0){
			this.domainIdTmp = lstDomain.get(0).getId();
		}
	}

	private void searchLang(){
		LangDAO langDAO = new LangDAO();
		listLang = langDAO.findAll("");
	}
	
	private void updateMenuLang() {
		
		List<SysMenuTrl> lstTrl = menuDao.findTranslationsByLang(locale.getLanguage());
		for(SysMenuTrl trl : lstTrl) {
			for(SysMenu menu : lstMenuOfUser) {
				if(menu.getId() == trl.getMenuId()) {
					menu.setName(trl.getName());
					break;
				}
			}
		}
	}
	
	public void doLogin() throws IOException {
		
//		sysUser = userDao.login(userName, PasswordUtil.generateHash(password));
		lstRootMenu.clear();
		lstMenuOfUser.clear();
		sysUser = userDao.findByUserName(userName, 0);
		boolean validate = false;
		if(sysUser != null) {
			
			String hash = PasswordUtil.generateHash(password, sysUser.getSalt());
			if(hash != null && hash.equals(sysUser.getPassword()))
				validate = true;
		}
		
		if (validate) {
			
			int expireDays = Integer.parseInt(ConfigUtil.getCfg("expirePasswordDays"));
			if(sysUser.getLastChangePassword() == null 
					|| DatetimeUtil.getDaysBetween(sysUser.getLastChangePassword(), Calendar.getInstance().getTime()) > expireDays) {
				
				showChangePassword = true;
				return;
			}
			
			if (!CommonUtil.isEmpty(sysUser.getLanguague())) {
				locale = new Locale(sysUser.getLanguague());
			} else {
				locale = Locale.ENGLISH;
			}
			// Find menu not belong to user
			lstMenuOfUser = userDao.findAllMenuOfUser(sysUser.getId());
			updateMenuLang();
			
			List<SysMenu> lstMenuAll = menuDao.findAll(false);
			List<SysMenu> lstMenuRestrict = new ArrayList<SysMenu>();
			for (SysMenu menu : lstMenuAll) {

				boolean found = false;
				for (SysMenu menuAuth : lstMenuOfUser) {
					if (menu.getId() == menuAuth.getId()) {
						found = true;
						break;
					}
				}

				if (!found) {
					lstMenuRestrict.add(menu);
				}
			}

			// Build root menu nodes
			for (SysMenu menu : lstMenuOfUser) {

				if (menu.getParentId() == null)
					lstRootMenu.add(menu);
			}

			// Create new session 
			((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).invalidate();
			FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userSession", this);
			
			HttpSession session = SessionUtils.getSession();
			session.setAttribute(SessionUtils.SESSION_SYS_USER, sysUser);
			session.setAttribute(SessionUtils.SESSION_DOMAIN_ID, domainId);
			session.setAttribute(SessionUtils.SESSION_LOCALE, locale);
			session.setAttribute(SessionUtils.SESSION_MENUS_RESTRICT, lstMenuRestrict);

			redirectHomePage();

			// return "home";
			getLogger().info("LOGIN - " + userName);
			
			loginCount = 0;
		} else {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Login!", "Please Try Again!"));

			loginCount++;
		}
	}
	
	private void redirectHomePage() throws IOException {
		
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest req = (HttpServletRequest) ec.getRequest();
		ec.redirect(req.getContextPath() + "/home.xhtml?javax.faces.Token=" + getFacesToken());
	}
	
	public String getFacesToken() {
		return String.valueOf(SessionUtils.getSession().getAttribute("com.sun.faces.TOKEN"));
	}
	
	public void updateDomainIdTmp() throws IOException{
		this.domainIdTmp = this.domainId;
		
		HttpSession session = SessionUtils.getSession();
		session.setAttribute(SessionUtils.SESSION_DOMAIN_ID, domainIdTmp);

		redirectHomePage();		
	}
	
	public void fillBackDomainId(){
		this.domainId = this.domainIdTmp;
	}

	public void doLogout() throws IOException {

		HttpSession session = SessionUtils.getSession();
		session.invalidate();
		lstRootMenu.clear();
		lstMenuOfUser.clear();

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest req = (HttpServletRequest) ec.getRequest();
		ec.redirect(req.getContextPath() + "/login.xhtml");
	}
	
	public List<SysMenu> searchMenuAutoComplete(String query){
		
		List<SysMenu> lst = new ArrayList<SysMenu>();
		if(query == null)
			return lst;
		query = query.toLowerCase();
		
		for(SysMenu menu: lstMenuOfUser) {			
			if(menu.getName() != null && menu.getName().toLowerCase().contains(query)) {
				lst.add(menu);
			}
		}
		
		return lst;
	}
	
	public void onChoosingMenu(SelectEvent event) throws IOException {
		
		String url = String.valueOf(event.getObject());
		if(!CommonUtil.isEmpty(url)) {
			
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest req = (HttpServletRequest) ec.getRequest();
			ec.redirect(req.getContextPath() + url);	
		}
	}

	public String getDateFormat() {
		if (sysUser != null) {
			if (sysUser.getDateformat() != null) {
				return sysUser.getDateformat();
			}
		}
		return "yyyy-MM-dd";
	}

	public String getTimeFormat() {
		return getDateFormat() +" HH:mm:ss";
	}

	public void onDomainChange(ValueChangeEvent e) throws IOException {

		long newDomain = e.getNewValue() == null ? 0 : ((Long) e.getNewValue()).longValue();
		domainId = newDomain;
		HttpSession session = SessionUtils.getSession();
		session.setAttribute(SessionUtils.SESSION_DOMAIN_ID, domainId);

		redirectHomePage();
	}

	public void onSetLanguage(String lang) {

		setLanguage(lang);
	}


	public void doChangePassword() {
		
		SysUser sysUser = userDao.findByUserName(userName, 0);
		if (validateSavePass(sysUser)) {
						
			String salt = PasswordUtil.getRandomSalt();
			sysUser.setSalt(salt);
			sysUser.setPassword(PasswordUtil.generateHash(newPassword, salt));
			sysUser.setLastChangePassword(Calendar.getInstance().getTime());
			userDao.saveOrUpdate(sysUser);
			
			this.showMessageINFO("common.save", " Change Password ");
			this.showChangePassword = false;
		}
	}
	
	private boolean validateSavePass(SysUser sysUser) {
		
		if(password == null || newPassword == null || newPasswordConfirm == null) {
			
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "changepass.enterAllPass");
			return false;
		}
		
		if(!newPassword.equals(newPasswordConfirm)) {
			
			showNotification(FacesMessage.SEVERITY_WARN, "common.fail", "changepass.notMatch");
			return false;
		}

		if (!PasswordUtil.validatePassword(newPassword)) {
			super.showMessageWARN("common.save", " Password ", "user.formatPassword");
			return false;
		}
		
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
	
	/** GET - SET **/
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	public String getDisplayLanguage() {
		for(Lang lang : listLang) {
			if(locale.getLanguage().equals(lang.getLangCode())) {
				if(!CommonUtil.isEmpty(lang.getLangName())) {
					return lang.getLangName();
				}				
				return locale.getDisplayLanguage();
			}
		}
		return locale.getDisplayLanguage();
	}

	public void setLanguage(String language) {
		
		locale = new Locale(language);
		HttpSession session = SessionUtils.getSession();
		session.setAttribute(SessionUtils.SESSION_LOCALE, locale);
		
		updateMenuLang();		
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	}
	
	public String getBreadCumb() {
		
		UIViewRoot uiViewRoot =	FacesContext.getCurrentInstance().getViewRoot();
		Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String url = uiViewRoot.getViewId();
		if(params.get("treeType") != null) {
			url += "?treeType=" + params.get("treeType");
		}
		
		String retVal = "Home";
		SysMenu menu = menuDao.findByUrl(url);
		if(menu != null) {
			
			List<SysMenu> lstParent = new ArrayList<>();
			menuDao.findParentAll(menu, lstParent);
			for(int i = lstParent.size() - 1; i >= 0; i--) {
				
				SysMenu parent = lstParent.get(i);
				retVal += " | " + menuDao.getNameTrl(parent, locale.getLanguage());
			}
			  
			retVal += " | " + menuDao.getNameTrl(menu, locale.getLanguage());
		}
		
		return retVal;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {

		this.domainId = domainId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public List<SysDomain> getLstDomain() {
		return lstDomain;
	}

	public void setLstDomain(List<SysDomain> lstDomain) {
		this.lstDomain = lstDomain;
	}

	public List<SysMenu> getLstRootMenu() {
		return lstRootMenu;
	}

	public void setLstRootMenu(List<SysMenu> lstRootMenu) {
		this.lstRootMenu = lstRootMenu;
	}

	public long getDomainIdTmp() {
		return domainIdTmp;
	}

	public void setDomainIdTmp(long domainIdTmp) {
		this.domainIdTmp = domainIdTmp;
	}

	public List<Lang> getListLang() {
		return listLang;
	}

	public void setListLang(List<Lang> listLang) {
		this.listLang = listLang;
	}

	public boolean isShowChangePassword() {
		return showChangePassword;
	}

	public void setShowChangePassword(boolean showChangePassword) {
		this.showChangePassword = showChangePassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirm() {
		return newPasswordConfirm;
	}

	public void setNewPasswordConfirm(String newPasswordConfirm) {
		this.newPasswordConfirm = newPasswordConfirm;
	}

	public String getCaptchaText() {
		return captchaText;
	}

	public void setCaptchaText(String captchaText) {
		this.captchaText = captchaText;
	}

	public int getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}
	
}
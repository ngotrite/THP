package com.viettel.ocs.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.viettel.ocs.db.ExcludeFieldJson;

@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2744751907880820211L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "user_name", nullable = false, unique = true)
	private String userName;

	@ExcludeFieldJson
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Transient
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "remark")
	private String remark;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "theme")
	private String theme;
	
	@ExcludeFieldJson
	@Column(name = "salt")
	private String salt;

	// @ManyToOne
	// @JoinColumn(name="role_id", nullable=false)
	// @ExcludeFieldJson
	// private SysRole role;

	@Column(name = "expire_date")
	private Date expireDate;

	@Column(name = "domain_id")
	private Long domainId;

	@Column(name = "languague")
	private String languague;

	@Column(name = "dateformat")
	private String dateformat;
	
	@Column(name = "last_change_password")
	private Date lastChangePassword;

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {

		if (firstName == null && lastName == null) {
			return "";
		} else if (firstName == null) {
			return lastName;
		} else if (lastName == null) {
			return firstName;
		} else {
			return this.firstName + " " + this.lastName;
		}
	}

	public void setFullName(String fullName) {
		// this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	// public SysRole getRole() {
	// return role;
	// }
	//
	// public void setRole(SysRole role) {
	// this.role = role;
	// }

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

	@Override
	public SysUser clone() throws CloneNotSupportedException {
		return (SysUser) super.clone();
	}

	public String getLanguague() {
		return languague;
	}

	public void setLanguague(String languague) {
		this.languague = languague;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getLastChangePassword() {
		return lastChangePassword;
	}

	public void setLastChangePassword(Date lastChangePassword) {
		this.lastChangePassword = lastChangePassword;
	}
}

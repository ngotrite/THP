package com.viettel.ocs.entity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.viettel.ocs.db.ExcludeFieldJson;

@Entity
@Table(name="sys_role")
public class SysRole extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5748320798053404000L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="name", nullable=false, unique=true)
	private String name;
	
	@Column(name="role_type")
	private String roleType;
	
	@Column(name="description")
	private String description;
	
	@Column(name="is_active")
	private Boolean isActive;
		
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
//	public Set<SysUser> getUsers() {
//		return users;
//	}
//
//	public void setUsers(Set<SysUser> users) {
//		this.users = users;
//	}
	

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

}

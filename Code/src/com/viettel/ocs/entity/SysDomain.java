package com.viettel.ocs.entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="sys_domain")
public class SysDomain extends BaseEntity implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9177628522822246323L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="name", nullable=false)
	private String name;
	
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
	
	@Override
	public SysDomain clone() throws CloneNotSupportedException {
		return (SysDomain) super.clone();
	}

}

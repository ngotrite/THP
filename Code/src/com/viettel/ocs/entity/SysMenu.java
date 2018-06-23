package com.viettel.ocs.entity;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="sys_menu")
public class SysMenu extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5748320798053404000L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="parent_id")
	private Long parentId;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="path", nullable=false)
	private String path;
	
	@Column(name="url")
	private String url;
	
	@Column(name="description")
	private String description;
	
	@Column(name="is_active")
	private Boolean isActive;
	
	@Column(name="pos_index")
	private Long posIndex;
	
	@Column(name="css_class")
	private String cssClass;
	
	@Transient
	private List<SysMenu> subMenus;
		
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
	
	public Long getPosIndex() {
		return posIndex;
	}

	public void setPosIndex(Long posIndex) {
		this.posIndex = posIndex;
	}

	public List<SysMenu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<SysMenu> subMenus) {
		this.subMenus = subMenus;
	}	

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	@Override
	@Transient
	public String getNodeName() {
		return this.name;
	}
}

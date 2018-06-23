package com.viettel.ocs.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "system_conf")
public class SystemConfig extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5748320798053404000L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "sys_key", nullable = false)
	private String key;
	@Column(name = "sys_value", nullable = false)
	private String value;
	@Column(name = "extra")
	private String extra;
	@Column(name = "description")
	private String description;
	@Column(name = "category_id")
	private Long categoryId;
	@Column(name = "POS_INDEX", length = 20)
	private Long index;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	@Override
	@Transient
	public String getNodeName() {
		return this.key + " - " + this.value;
	}

}

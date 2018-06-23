package com.viettel.ocs.entity;

// Generated Aug 10, 2016 7:34:41 PM by Hibernate Tools 3.2.1.GA
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * StateType generated by hbm2java
 */
@Entity
@Table(name = "state_groups")
public class StateGroup extends BaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "state_group_id")
	private long stateGroupId;
	@Basic(optional = false)
	@Column(name = "state_group_name")
	private String stateGroupName;
	@Basic(optional = false)
	@Column(name = "state_group_desc")
	private String stateGroupDesc;

	@Column(name = "DOMAIN_ID")
	private Long domainId;

	@Column(name = "POS_INDEX")
	private Long index;

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	@Column(name = "CATEGORY_ID")
	private long categoryId;

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	@Transient
	public String getNodeName() {
		return stateGroupName;
	}

	public StateGroup() {
	}

	public StateGroup(long stateGroupId) {
		this.stateGroupId = stateGroupId;
	}

	public StateGroup(long stateGroupId, String stateGroupName, String stateGroupDesc) {
		this.stateGroupId = stateGroupId;
		this.stateGroupName = stateGroupName;
		this.stateGroupDesc = stateGroupDesc;
	}

	public long getStateGroupId() {
		return stateGroupId;
	}

	public void setStateGroupId(long stateGroupId) {
		this.stateGroupId = stateGroupId;
	}

	public String getStateGroupName() {
		return stateGroupName;
	}

	public void setStateGroupName(String stateGroupName) {
		this.stateGroupName = stateGroupName;
	}

	public String getStateGroupDesc() {
		return stateGroupDesc;
	}

	public void setStateGroupDesc(String stateGroupDesc) {
		this.stateGroupDesc = stateGroupDesc;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

}
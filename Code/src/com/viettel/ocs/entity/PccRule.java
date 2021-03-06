package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * PccRule generated by hbm2java
 */
@Entity
@Table(name = "pcc_rule")
public class PccRule extends BaseEntity implements java.io.Serializable, Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return pccBaseName;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private long pccRuleId;
	private String pccBaseName;
	private Long precedence;
	private Date activeTime;
	private Date deactiveTime;
	private Long type;
	private String remark;
	private Long categoryId;
	private Long priority;
	private Long index;

	public PccRule() {
	}

	public PccRule(String pccBaseName, Long precedence, Date activeTime, Date deactiveTime, Long type, String remark,
			Long categoryId) {
		this.pccBaseName = pccBaseName;
		this.precedence = precedence;
		this.activeTime = activeTime;
		this.deactiveTime = deactiveTime;
		this.type = type;
		this.remark = remark;
		this.categoryId = categoryId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "PCC_RULE_ID", unique = true, nullable = false)
	public long getPccRuleId() {
		return this.pccRuleId;
	}

	public void setPccRuleId(long pccRuleId) {
		this.pccRuleId = pccRuleId;
	}

	@Column(name = "PCC_BASE_NAME", length = 30)
	public String getPccBaseName() {
		return this.pccBaseName;
	}

	public void setPccBaseName(String pccBaseName) {
		this.pccBaseName = pccBaseName;
	}

	@Column(name = "PRECEDENCE")
	public Long getPrecedence() {
		return this.precedence;
	}

	public void setPrecedence(Long precedence) {
		this.precedence = precedence;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVE_TIME", length = 19)
	public Date getActiveTime() {
		return this.activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DEACTIVE_TIME", length = 19)
	public Date getDeactiveTime() {
		return this.deactiveTime;
	}

	public void setDeactiveTime(Date deactiveTime) {
		this.deactiveTime = deactiveTime;
	}

	@Column(name = "TYPE")
	public Long getType() {
		return this.type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "CATEGORY_ID")
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "PRIORITY")
	public Long getPriority() {
		return this.priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	@Column(name = "POS_INDEX", length = 20)
	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	@Override
	public PccRule clone() throws CloneNotSupportedException {
		return (PccRule) super.clone();
	}

}

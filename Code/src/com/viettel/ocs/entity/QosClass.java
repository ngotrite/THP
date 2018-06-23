package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "qos_class")
public class QosClass extends BaseEntity implements java.io.Serializable {

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

	private long qci;
	private String resourceType;
	private Long priority;
	private Long delayBudget;
	private Long errorLost;
	private String name;

	public QosClass() {
	}

	public QosClass(long qci, String resourceType, Long priority,
			Long delayBudget, Long errorLost, String name) {
		this.qci = qci;
		this.resourceType = resourceType;
		this.priority = priority;
		this.delayBudget = delayBudget;
		this.errorLost = errorLost;
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "QCI", unique = true, nullable = false)
	public long getQci() {
		return qci;
	}

	public void setQci(long qci) {
		this.qci = qci;
	}

	@Column(name = "RESOURCE_TYPE")
	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	@Column(name = "PRIORITY")
	public Long getPriority() {
		return priority;
	}
	public void setPriority(Long priority) {
		this.priority = priority;
	}
	@Column(name = "DELAY_BUDGET")
	public Long getDelayBudget() {
		return delayBudget;
	}

	public void setDelayBudget(Long delayBudget) {
		this.delayBudget = delayBudget;
	}
	@Column(name = "ERROR_LOST")
	public Long getErrorLost() {
		return errorLost;
	}

	public void setErrorLost(Long errorLost) {
		this.errorLost = errorLost;
	}
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

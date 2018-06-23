package com.viettel.ocs.entity;
// Generated Sep 2, 2016 4:54:08 PM by Hibernate Tools 3.2.1.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ThresholdTriggerMap generated by hbm2java
 */
@Entity
@Table(name = "threshold_trigger_map")
public class ThresholdTriggerMap extends BaseEntity implements java.io.Serializable {

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

	private Long domainId;

	@Column(name = "DOMAIN_ID")
	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	private long thresholdTriggerMapId;
	private Long thresholdId;
	private Long triggerOcsId;

	public ThresholdTriggerMap() {
	}

	public ThresholdTriggerMap(Long thresholdId, Long triggerOcsId) {
		this.thresholdId = thresholdId;
		this.triggerOcsId = triggerOcsId;
	}

	public ThresholdTriggerMap(Long thresholdId, Long triggerOcsId, Long domainId) {
		this.thresholdId = thresholdId;
		this.triggerOcsId = triggerOcsId;
		this.domainId = domainId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "THRESHOLD_TRIGGER_MAP_ID", unique = true, nullable = false)
	public long getThresholdTriggerMapId() {
		return this.thresholdTriggerMapId;
	}

	public void setThresholdTriggerMapId(long thresholdTriggerMapId) {
		this.thresholdTriggerMapId = thresholdTriggerMapId;
	}

	@Column(name = "THRESHOLD_ID")
	public Long getThresholdId() {
		return this.thresholdId;
	}

	public void setThresholdId(Long thresholdId) {
		this.thresholdId = thresholdId;
	}

	@Column(name = "TRIGGER_OCS_ID")
	public Long getTriggerOcsId() {
		return this.triggerOcsId;
	}

	public void setTriggerOcsId(Long triggerOcsId) {
		this.triggerOcsId = triggerOcsId;
	}

}

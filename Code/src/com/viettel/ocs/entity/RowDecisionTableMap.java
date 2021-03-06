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
 * RowDecisionTableMap generated by hbm2java
 */
@Entity
@Table(name = "row_decision_table_map")
public class RowDecisionTableMap extends BaseEntity implements java.io.Serializable, Cloneable {

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

	private long rowDecisionTableMapId;
	private Long decisionTableId;
	private Long rowDtId;

	public RowDecisionTableMap() {
	}

	public RowDecisionTableMap(Long decisionTableId, Long rowDtId) {
		this.decisionTableId = decisionTableId;
		this.rowDtId = rowDtId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "ROW_DECISION_TABLE_MAP_ID", unique = true, nullable = false)
	public long getRowDecisionTableMapId() {
		return this.rowDecisionTableMapId;
	}

	public void setRowDecisionTableMapId(long rowDecisionTableMapId) {
		this.rowDecisionTableMapId = rowDecisionTableMapId;
	}

	@Column(name = "DECISION_TABLE_ID")
	public Long getDecisionTableId() {
		return this.decisionTableId;
	}

	public void setDecisionTableId(Long decisionTableId) {
		this.decisionTableId = decisionTableId;
	}

	@Column(name = "ROW_DT_ID")
	public Long getRowDtId() {
		return this.rowDtId;
	}

	public void setRowDtId(Long rowDtId) {
		this.rowDtId = rowDtId;
	}

	@Override
	public RowDecisionTableMap clone() throws CloneNotSupportedException {
		return (RowDecisionTableMap) super.clone();
	}

}

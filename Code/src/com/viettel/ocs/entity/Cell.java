package com.viettel.ocs.entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CELL")
public class Cell extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2397408954887405739L;
	private long cellId;
	private String cellCode;
	private String cellName;
	private Date cellUpdateTime;
    
    private Long domainId;
	@Column(name="DOMAIN_ID")
    public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public Cell() {

	}

	public Cell(long cellId) {
		this.cellId = cellId;
	}

	public Cell(long cellId, String cellCode, String cellName, Date cellUpdateTime) {
		this.cellId = cellId;
		this.cellCode = cellCode;
		this.cellName = cellName;
		this.cellUpdateTime = cellUpdateTime;
	}

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "CELL_ID", unique = true, nullable = false)
	public long getCellId() {
		return cellId;
	}

	public void setCellId(long cellId) {
		this.cellId = cellId;
	}

	@Column(name = "CELL_CODE", nullable = false)
	public String getCellCode() {
		return cellCode;
	}

	public void setCellCode(String cellCode) {
		this.cellCode = cellCode;
	}

	@Column(name = "CELL_NAME", nullable = false)
	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	@Column(name = "CELL_UPDATE_TIME", nullable = false)
	public Date getCellUpdateTime() {
		return cellUpdateTime;
	}

	public void setCellUpdateTime(Date cellUpdateTime) {
		this.cellUpdateTime = cellUpdateTime;
	}

	@Override
	@Transient
	public String getNodeName() {
		return cellName;
	}
}

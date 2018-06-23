package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "formula_error_code")
public class FormulaErrorCode extends BaseEntity implements java.io.Serializable{

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	
	private long formulaErrorCodeId;
	private Long errorCode;
	private String description;
	private Long type;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "FORMULA_ERROR_CODE_ID", unique = true, nullable = false)
	public long getFormulaErrorCodeId() {
		return formulaErrorCodeId;
	}

	public void setFormulaErrorCodeId(long formulaErrorCodeId) {
		this.formulaErrorCodeId = formulaErrorCodeId;
	}

	@Column(name = "ERROR_CODE")
	public Long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Long errorCode) {
		this.errorCode = errorCode;
	}
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@Column(name = "TYPE")
	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

}

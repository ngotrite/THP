package com.viettel.ocs.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "formula_unit_converter")
public class FormulaUnitConverter extends BaseEntity implements Serializable {
	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	
	private long unitConverterId;
	private String unitConverterName;
	private String unitConverterDesc;
	
	
	@Id
	//@GeneratedValue(strategy = IDENTITY)
	@Column(name = "UNIT_CONVERTER_ID", unique = true, nullable = false)
	public long getUnitConverterId() {
		return unitConverterId;
	}
	public void setUnitConverterId(long unitConverterId) {
		this.unitConverterId = unitConverterId;
	}
	
	
	@Column(name = "UNIT_CONVERTER_NAME")
	public String getUnitConverterName() {
		return unitConverterName;
	}
	public void setUnitConverterName(String unitConverterName) {
		this.unitConverterName = unitConverterName;
	}
	
	
	@Column(name = "UNIT_CONVERTER_DESC")
	public String getUnitConverterDesc() {
		return unitConverterDesc;
	}
	public void setUnitConverterDesc(String unitConverterDesc) {
		this.unitConverterDesc = unitConverterDesc;
	}
	
	
}

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
 * NormValue generated by hbm2java
 */
@Entity
@Table(name = "norm_value")
public class NormValue extends BaseEntity implements java.io.Serializable, Cloneable {

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

	private long normValueId;
	private String valueName;
	private Long valueId;
	private String description;
	private Long normValueIndex;
	private String color;
	private String colorBG;

	public NormValue() {
	}

	public NormValue(String valueName, Long valueId, String description, Long normValueIndex, String color,
			String colorBG) {
		this.valueName = valueName;
		this.valueId = valueId;
		this.description = description;
		this.normValueIndex = normValueIndex;
		this.color = color;
		this.colorBG = colorBG;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "NORM_VALUE_ID", unique = true, nullable = false)
	public long getNormValueId() {
		return this.normValueId;
	}

	public void setNormValueId(long normValueId) {
		this.normValueId = normValueId;
	}

	@Column(name = "VALUE_NAME", length = 65535)
	public String getValueName() {
		return this.valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	@Column(name = "VALUE_ID")
	public Long getValueId() {
		return this.valueId;
	}

	public void setValueId(Long valueId) {
		this.valueId = valueId;
	}

	@Column(name = "DESCRIPTION", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "NORM_VALUE_INDEX")
	public Long getNormValueIndex() {
		return this.normValueIndex;
	}

	public void setNormValueIndex(Long normValueIndex) {
		this.normValueIndex = normValueIndex;
	}

	@Column(name = "COLOR", length = 80)
	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Column(name = "COLORBG", length = 80)
	public String getColorBG() {
		return colorBG;
	}

	public void setColorBG(String colorBG) {
		this.colorBG = colorBG;
	}

	@Override
	public NormValue clone() throws CloneNotSupportedException {
		return (NormValue) super.clone();
	}

}

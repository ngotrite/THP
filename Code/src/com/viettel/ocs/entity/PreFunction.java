package com.viettel.ocs.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "pre_function")
public class PreFunction extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy =IDENTITY)
	@Column(name = "pre_function_id")
	private long preFunctionId;
	@Column(name = "function_name")
	private String functionName;
	@Column(name = "number_param")
	private int numberParam;
	
	public long getPreFunctionId() {
		return preFunctionId;
	}



	public void setPreFunctionId(long preFunctionId) {
		this.preFunctionId = preFunctionId;
	}



	public String getFunctionName() {
		return functionName;
	}



	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}



	public int getNumberParam() {
		return numberParam;
	}



	public void setNumberParam(int numberParam) {
		this.numberParam = numberParam;
	}



	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
}

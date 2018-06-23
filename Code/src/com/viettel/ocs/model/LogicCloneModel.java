package com.viettel.ocs.model;

import java.io.Serializable;

import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry.FallbackJavaTypeDescriptor;

@SuppressWarnings("serial")
public class LogicCloneModel implements Serializable {
	private boolean offer;
	private boolean action;
	private boolean pc;
	private boolean block;
	private boolean rateTable;
	private boolean dt;
	private boolean normalizer;
	private boolean spc;
	private boolean dr;

	public LogicCloneModel() {
		super();
		this.offer = true;
		this.action = false;
		this.pc = false;
		this.block = false;
		this.rateTable = false;
		this.dt = false;
		this.normalizer = false;
	}

	public LogicCloneModel(boolean offer, boolean action, boolean pc, boolean block, boolean rateTable, boolean dt) {
		super();
		this.offer = offer;
		this.action = action;
		this.pc = pc;
		this.block = block;
		this.rateTable = rateTable;
		this.dt = dt;
		this.normalizer = false;
	}

	public LogicCloneModel(boolean offer, boolean action, boolean pc, boolean block, boolean rateTable, boolean dt,
			boolean normalizer) {
		super();
		this.offer = offer;
		this.action = action;
		this.pc = pc;
		this.block = block;
		this.rateTable = rateTable;
		this.dt = dt;
		this.normalizer = normalizer;
	}

	public LogicCloneModel(boolean spc, boolean rateTable, boolean dt, boolean normalizer) {
		super();
		this.spc = spc;
		this.rateTable = rateTable;
		this.dt = dt;
		this.normalizer = normalizer;
	}
	
	public void LogicCloneModelDr(boolean dr, boolean rateTable, boolean dt, boolean normalizer) {
		this.dr = dr;
		this.rateTable = rateTable;
		this.dt = dt;
		this.normalizer = normalizer;
	}

	public boolean isOffer() {
		return offer;
	}

	public void setOffer(boolean offer) {
		this.offer = offer;
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public boolean isPc() {
		return pc;
	}

	public void setPc(boolean pc) {
		this.pc = pc;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public boolean isRateTable() {
		return rateTable;
	}

	public void setRateTable(boolean rateTable) {
		this.rateTable = rateTable;
	}

	public boolean isDt() {
		return dt;
	}

	public void setDt(boolean dt) {
		this.dt = dt;
	}

	public boolean isNormalizer() {
		return normalizer;
	}

	public void setNormalizer(boolean normalizer) {
		this.normalizer = normalizer;
	}

	public boolean isSpc() {
		return spc;
	}

	public void setSpc(boolean spc) {
		this.spc = spc;
	}

	public boolean isDr() {
		return dr;
	}

	public void setDr(boolean dr) {
		this.dr = dr;
	}
	
	

}

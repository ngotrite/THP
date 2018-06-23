package com.viettel.ocs.model;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.PcTypeBlockMap;

@SuppressWarnings("serial")
public class PcTypeBlockMapModel implements Serializable{
	private PcTypeBlockMap pcTypeBlockMap;
	private Block block;
	
	public PcTypeBlockMapModel(PcTypeBlockMap pcTypeBlockMap, Block block) {
		super();
		this.pcTypeBlockMap = pcTypeBlockMap;
		this.block = block;
	}

	public PcTypeBlockMapModel() {
		super();
	}

	public PcTypeBlockMap getPcTypeBlockMap() {
		return pcTypeBlockMap;
	}
	public void setPcTypeBlockMap(PcTypeBlockMap pcTypeBlockMap) {
		this.pcTypeBlockMap = pcTypeBlockMap;
	}
	
	public Block getBlock() {
		return block;
	}
	public void setBlock(Block block) {
		this.block = block;
	}	
}

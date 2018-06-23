package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "pc_type_block_map")
public class PcTypeBlockMap extends BaseEntity implements Serializable {
	
	public PcTypeBlockMap() {
		super();
	}

	public PcTypeBlockMap(long pcTypeBlockMapId, Long pcTypeId, Long blockId, Long blockGenType,
			boolean isReplaceIfExist, Long blockIndex) {
		super();
		this.pcTypeBlockMapId = pcTypeBlockMapId;
		this.pcTypeId = pcTypeId;
		this.blockId = blockId;
		this.blockGenType = blockGenType;
		this.isReplaceIfExist = isReplaceIfExist;
		this.blockIndex = blockIndex;
	}

	public PcTypeBlockMap(Long blockId, Long pcTypeId) {
		super();
		this.blockId = blockId;
		this.pcTypeId = pcTypeId;
	}

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}

	private long pcTypeBlockMapId;
	private Long pcTypeId;
	private Long blockId;
	private Long blockGenType;
	private boolean isReplaceIfExist;
	private Long blockIndex;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "PC_TYPE_BLOCK_MAP_ID", unique = true, nullable = false)
	public long getPcTypeBlockMapId() {
		return pcTypeBlockMapId;
	}

	public void setPcTypeBlockMapId(long pcTypeBlockMapId) {
		this.pcTypeBlockMapId = pcTypeBlockMapId;
	}

	@Column(name = "PC_TYPE_ID")
	public Long getPcTypeId() {
		return pcTypeId;
	}

	public void setPcTypeId(Long pcTypeId) {
		this.pcTypeId = pcTypeId;
	}

	@Column(name = "BLOCK_ID")
	public Long getBlockId() {
		return blockId;
	}

	public void setBlockId(Long blockId) {
		this.blockId = blockId;
	}

	@Column(name = "BLOCK_GEN_TYPE")
	public Long getBlockGenType() {
		return blockGenType;
	}

	public void setBlockGenType(Long blockGenType) {
		this.blockGenType = blockGenType;
	}

	@Column(name = "IS_REPLACE_IF_EXIST")
	public boolean isReplaceIfExist() {
		return isReplaceIfExist;
	}

	public void setReplaceIfExist(boolean isReplaceIfExist) {
		this.isReplaceIfExist = isReplaceIfExist;
	}

	@Column(name = "BLOCK_INDEX")
	public Long getBlockIndex() {
		return blockIndex;
	}

	public void setBlockIndex(Long blockIndex) {
		this.blockIndex = blockIndex;
	}

}

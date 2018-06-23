package com.viettel.ocs.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="share_type"
)
public class ShareType extends BaseEntity implements Serializable{

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	

	private long shareTypeId;
	private String shareTypeName;
	
	
	
	public ShareType() {
	}
	public ShareType(long shareTypeId, String shareTypeName) {
		super();
		this.setShareTypeId(shareTypeId);
		this.setShareTypeName(shareTypeName);
	}
	
    @Id @GeneratedValue(strategy=IDENTITY)
    
   @Column(name="SHARE_TYPE_ID", unique=true, nullable=false)
	public long getShareTypeId() {
		return shareTypeId;
	}
	public void setShareTypeId(long shareTypeId) {
		this.shareTypeId = shareTypeId;
	}
	
	
	 @Column(name="SHARE_TYPE_NAME")
	public String getShareTypeName() {
		return shareTypeName;
	}
	public void setShareTypeName(String shareTypeName) {
		this.shareTypeName = shareTypeName;
	}
	
	
}

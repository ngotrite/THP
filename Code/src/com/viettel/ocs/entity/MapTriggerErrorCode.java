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
 * MapTriggerErrorCode generated by hbm2java
 */
@Entity
@Table(name="map_trigger_error_code"
)
public class MapTriggerErrorCode extends BaseEntity  implements java.io.Serializable {

	@Override
	@Transient
	public String getNodeName() {
		return null;
	}
	private Long domainId;
	@Column(name="DOMAIN_ID")
    public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
     private long mapTriggerErrorCodeId;
     private long triggerOcsId;
     private String errorCode;

    public MapTriggerErrorCode() {
    }

    public MapTriggerErrorCode(long triggerOcsId, String errorCode) {
       this.triggerOcsId = triggerOcsId;
       this.errorCode = errorCode;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="MAP_TRIGGER_ERROR_CODE_ID", unique=true, nullable=false)
    public long getMapTriggerErrorCodeId() {
        return this.mapTriggerErrorCodeId;
    }
    
    public void setMapTriggerErrorCodeId(long mapTriggerErrorCodeId) {
        this.mapTriggerErrorCodeId = mapTriggerErrorCodeId;
    }
    
    @Column(name="TRIGGER_OCS_ID", nullable=false)
    public long getTriggerOcsId() {
        return this.triggerOcsId;
    }
    
    public void setTriggerOcsId(long triggerOcsId) {
        this.triggerOcsId = triggerOcsId;
    }
    
    @Column(name="ERROR_CODE", nullable=false, length=50)
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }




}


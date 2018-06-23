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
 * NormalizerNormValueMap generated by hbm2java
 */
@Entity
@Table(name="normalizer_norm_value_map"
)
public class NormalizerNormValueMap extends BaseEntity  implements java.io.Serializable {

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
     private long normalizerNormValueMapId;
     private Long normalizerId;
     private Long normValueId;

    public NormalizerNormValueMap() {
    }

    public NormalizerNormValueMap(Long normalizerId, Long normValueId) {
       this.normalizerId = normalizerId;
       this.normValueId = normValueId;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="NORMALIZER_NORM_VALUE_MAP_ID", unique=true, nullable=false)
    public long getNormalizerNormValueMapId() {
        return this.normalizerNormValueMapId;
    }
    
    public void setNormalizerNormValueMapId(long normalizerNormValueMapId) {
        this.normalizerNormValueMapId = normalizerNormValueMapId;
    }
    
    @Column(name="NORMALIZER_ID")
    public Long getNormalizerId() {
        return this.normalizerId;
    }
    
    public void setNormalizerId(Long normalizerId) {
        this.normalizerId = normalizerId;
    }
    
    @Column(name="NORM_VALUE_ID")
    public Long getNormValueId() {
        return this.normValueId;
    }
    
    public void setNormValueId(Long normValueId) {
        this.normValueId = normValueId;
    }




}



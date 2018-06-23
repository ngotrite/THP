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
 * ZoneMap generated by hbm2java
 */
@Entity
@Table(name="zone_map"
)
public class ZoneMap extends BaseEntity  implements java.io.Serializable, Cloneable {

	@Override
	@Transient
	public String getNodeName() {
		return this.zoneMapName;
	}
	private Long domainId;
	@Column(name="DOMAIN_ID")
    public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
     private long zoneMapId;
     private String zoneMapName;
     private String remark;
     private Long categoryId;
     private Long index;

    public ZoneMap() {
    }

    public ZoneMap(String zoneMapName, String remark) {
       this.zoneMapName = zoneMapName;
       this.remark = remark;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="ZONE_MAP_ID", unique=true, nullable=false)
    public long getZoneMapId() {
        return this.zoneMapId;
    }
    
    public void setZoneMapId(long zoneMapId) {
        this.zoneMapId = zoneMapId;
    }
    
    @Column(name="ZONE_MAP_NAME", length=200)
    public String getZoneMapName() {
        return this.zoneMapName;
    }
    
    public void setZoneMapName(String zoneMapName) {
        this.zoneMapName = zoneMapName;
    }
    
    @Column(name="REMARK", length=250)
    public String getRemark() {
        return this.remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="CATEGORY_ID")
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	@Column(name = "POS_INDEX", length = 20)
	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}
	@Override
	public ZoneMap clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (ZoneMap) super.clone();
	}
	
	

}



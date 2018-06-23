package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.dao.BaseDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.dao.ZoneDAO;
import com.viettel.ocs.dao.ZoneMapDAO;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneMap;

//@ManagedBean(name = "zoneBean")
@ViewScoped
public class ZoneBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Zone zone = new Zone();
	private List<Zone> listZoneByZoneMap;
	ZoneDAO zoneDAO = new ZoneDAO();

	private int isEnable;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isEnable = ContantsUtil.Status.ENABLE;

	}

	public void btnAddNew() {
		this.isEnable = ContantsUtil.Status.DISABLE;
		cancelZone();
	}

	public List<Zone> getZoneList() {
		return zoneDAO.findAll("");
	}

	// load list list Zone by ZoneMap
	public List<Zone> loadZoneByCategory(long zoneMapId) {
		listZoneByZoneMap = new ArrayList<Zone>();
		listZoneByZoneMap = zoneDAO.findZoneByConditions(zoneMapId);
		return listZoneByZoneMap;
	}

	public void saveZone() {
		if (zone.getZoneId() == 0L) {
			listZoneByZoneMap.add(zone);
		}
		zoneDAO.saveOrUpdate(zone);

	}

	public void deleteZone(Zone item) {
		zoneDAO.delete(item);
		listZoneByZoneMap.remove(item);
	}

	public void editZone(Zone item) {
		this.zone = item;

	}

	public void cancelZone() {
		zone = new Zone();
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public int getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(int isEnable) {
		this.isEnable = isEnable;
	}

}

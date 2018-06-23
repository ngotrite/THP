package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.dao.BaseDAO;
import com.viettel.ocs.dao.GeoNetZoneDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.dao.ZoneDAO;
import com.viettel.ocs.entity.GeoNetZone;
import com.viettel.ocs.entity.Zone;

//@ManagedBean(name = "geoNetBean")
@ViewScoped
public class GeoNetBean extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private GeoNetZone geoNet = new GeoNetZone();
	private List<GeoNetZone> listGeoNetByGeoHome;
	GeoNetZoneDAO geoNetDAO = new GeoNetZoneDAO();

	private int isEnable;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isEnable = ContantsUtil.Status.ENABLE;

	}

	public void btnAddNew() {
		this.isEnable = ContantsUtil.Status.DISABLE;
		cancelGeoNet();
	}

	public List<GeoNetZone> getGeoNetList() {
		return geoNetDAO.findAll("");
	}

	// load list GeoNet by GeoHome
	public List<GeoNetZone> loadGeoNetByGeoHome(long geoHomeZoneId) {
		listGeoNetByGeoHome = new ArrayList<GeoNetZone>();
		listGeoNetByGeoHome = geoNetDAO.findGeoNetZoneByConditions(geoHomeZoneId);
		return listGeoNetByGeoHome;
	}

	public void saveGeoNet() {

		geoNetDAO.saveOrUpdate(geoNet);

		if (geoNet.getGeoNetZoneId() == 0L) {
			listGeoNetByGeoHome.add(geoNet);
		}
		geoNetDAO.saveOrUpdate(geoNet);
	}

	public void deleteGeoNet(GeoNetZone item) {
		geoNetDAO.delete(item);
		listGeoNetByGeoHome.remove(item);
	}

	public void editNetHome(GeoNetZone item) {
		this.geoNet = item;

	}

	public void cancelGeoNet() {
		geoNet = new GeoNetZone();
	}

	public GeoNetZone getGeoNet() {
		return geoNet;
	}

	public void setGeoNet(GeoNetZone geoNet) {
		this.geoNet = geoNet;
	}

	public int getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(int isEnable) {
		this.isEnable = isEnable;
	}

}

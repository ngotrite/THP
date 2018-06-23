package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.dao.BaseDAO;
import com.viettel.ocs.dao.CellDAO;
import com.viettel.ocs.entity.Cell;


@ManagedBean(name = "cellBean")
@ViewScoped
public class CellBean implements Serializable {

	private Cell cell = new Cell();
	CellDAO cellDAO = new CellDAO();

	private int isEnable;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isEnable = ContantsUtil.Status.ENABLE;

	}

	public void btnAddNew() {
		this.isEnable = ContantsUtil.Status.DISABLE;
		cancelCell();
	}

	public List<Cell> getCellList() {

		return cellDAO.findAll("");
	}

	public void saveCell() {

		cellDAO.saveOrUpdate(cell);
		getCellList();

	}

	public void deleteCell(Cell item) {
		cellDAO.delete(item);
		getCellList();
	}

	public void editCell(Cell item) {
		this.cell = item;

	}

	public void cancelCell() {
		cell = new Cell();
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public int getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(int isEnable) {
		this.isEnable = isEnable;
	}
	
	

}

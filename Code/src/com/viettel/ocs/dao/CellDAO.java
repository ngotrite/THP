package com.viettel.ocs.dao;

import java.io.Serializable;

import com.viettel.ocs.entity.Cell;

public class CellDAO extends BaseDAO<Cell> implements Serializable {

	@Override
	protected Class<Cell> getEntityClass() {
		return Cell.class;
	}

}

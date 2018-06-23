package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.List;

import com.viettel.ocs.entity.Lang;

@SuppressWarnings("serial")
public class LangDAO extends BaseDAO<Lang> implements Serializable{
	@Override
	protected Class<Lang> getEntityClass() {
		return Lang.class;
	}
	
	public List<Lang> findAll(String orders) {
		return super.findAllWithoutDomain(orders);
	}
}

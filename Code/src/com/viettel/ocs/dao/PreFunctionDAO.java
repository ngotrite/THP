package com.viettel.ocs.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.AuditLog;
import com.viettel.ocs.entity.PreFunction;
import com.viettel.ocs.entity.SysUser;

/***
 * 
 * @author huannn
 *
 */
public class PreFunctionDAO extends BaseDAO<PreFunction> implements Serializable {

	@Override
	protected Class<PreFunction> getEntityClass() {
		return PreFunction.class;
	}
	
	@Override
	public List<PreFunction> findAll(String orders) {
		// TODO Auto-generated method stub
		return super.findAllWithoutDomain(orders);
	}
}

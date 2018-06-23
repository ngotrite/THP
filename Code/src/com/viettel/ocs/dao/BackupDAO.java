package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;

public class BackupDAO implements Serializable {
	
	public BackupDAO() {
		// TODO Auto-generated constructor stub
	}
	
	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}
	
	public List<String> getAllTable(String tblSchema) {
		List<String> rs = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		StringBuffer buffer = new StringBuffer();
		Session session = HibernateUtil.getOpenSession();
		try {
			
			Query query = session.createNativeQuery("SELECT Table_Name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = ? AND table_type = 'BASE TABLE'");
			query.setParameter(1, tblSchema);
			rs = query.getResultList();
			return rs;	
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}		
	}

	public List getAllData(String tblName) {
		List<Object[]> rs = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		Session session = HibernateUtil.getOpenSession();
		try {

			Query query = session.createNativeQuery("SELECT * FROM " + tblName); // Phai cong string, ko truyen tham so dc
			rs = query.getResultList();
			return rs;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public int getCountColumn(String tblName, String tblSchema) {
		
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<?> query = session.createNativeQuery("SELECT count(*) FROM information_schema.columns WHERE table_name = ? AND table_schema = ?");
			query.setParameter(1, tblName);
			query.setParameter(2, tblSchema);
			String rs = String.valueOf(query.getSingleResult());
			return Integer.parseInt(rs);
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public String getSQLCreateTable(String tblName) {
		String buffer = new String();
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<?> query = session.createNativeQuery("SHOW CREATE TABLE `" + tblName + "`"); // Phai cong string, ko truyen tham so dc
			Object[] objects = (Object[]) query.getSingleResult();
			if (objects != null) {
				buffer = objects[1].toString();
			}
			return buffer;
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}		
	}

	public String getCurrentSchemaName() {
		Session session = HibernateUtil.getOpenSession();
		try {

			Query<?> query = session.createNativeQuery("SELECT DATABASE()");
			return (String) query.getSingleResult();
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

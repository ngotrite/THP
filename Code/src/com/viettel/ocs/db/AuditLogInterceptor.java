package com.viettel.ocs.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;

import com.viettel.ocs.context.SessionUtils;
import com.viettel.ocs.entity.AuditLog;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.util.JsonConvertUtils;

/***
 * 
 * @author Nampv
 *
 */
public class AuditLogInterceptor extends EmptyInterceptor {

	private Set inserts = new HashSet();
	private Set updates = new HashSet();
	private Set deletes = new HashSet();
	
	private static final String NEW = "NEW";
	private static final String UPDATE = "UPDATE";
	private static final String DELETE = "DELETE";
	
	private final static Logger logger = Logger.getLogger(AuditLogInterceptor.class);

	// Called when you save an object, the object is not save into database yet
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
			throws CallbackException {

		if (entity instanceof BaseEntity && !(entity instanceof AuditLog)) {
			inserts.add(entity);
		}
		return false;

	}

	// Called when you update an object, the object is not update into database yet.
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) throws CallbackException {

		if (entity instanceof BaseEntity && !(entity instanceof AuditLog)) {
			updates.add(entity);
		}
		return false;

	}

	// Called when you delete an object, the object is not delete into database yet.
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

		if (entity instanceof BaseEntity && !(entity instanceof AuditLog)) {
			deletes.add(entity);
		}
	}

	// called before commit into database
	public void preFlush(Iterator iterator) {

	}

	// called after committed into database
	public void postFlush(Iterator iterator) {

		try {

			for (Iterator it = inserts.iterator(); it.hasNext();) {

				BaseEntity entity = (BaseEntity) it.next();
				LogIt(NEW, entity);
			}

			for (Iterator it = updates.iterator(); it.hasNext();) {

				BaseEntity entity = (BaseEntity) it.next();
				LogIt(UPDATE, entity);
			}

			for (Iterator it = deletes.iterator(); it.hasNext();) {

				BaseEntity entity = (BaseEntity) it.next();
				LogIt(DELETE, entity);
			}

		} finally {
			inserts.clear();
			updates.clear();
			deletes.clear();
		}
	}

	public void LogIt(String action, BaseEntity entity) {

		Session tempSession = HibernateUtil.getOpenSessionNoInterceptor();
//		tempSession.beginTransaction();
		try {
			
			String userName = SessionUtils.getUserName();
			long domain = SessionUtils.getDomainId();
			String ip = SessionUtils.getClientIpAddr(SessionUtils.getRequest());
			StringBuilder oldValue = new StringBuilder("");
			StringBuilder newValue = new StringBuilder("");
			
			boolean isChange = false;
			if(NEW.equals(action)) {
				
				newValue.append(JsonConvertUtils.convertObjectToJson(entity));
				isChange = true;
			} else if(UPDATE.equals(action)) {
				
				long id = entity.getID();
				oldValue.append("id:" + id + ";");
				newValue.append("id:" + id + ";");
				BaseEntity oldEntity = tempSession.get(entity.getClass(), id);
				if (oldEntity != null) {
					
					java.lang.reflect.Field[] lstFiel = oldEntity.getClass().getDeclaredFields();
					for (java.lang.reflect.Field field : lstFiel) {
						
						if(field.getAnnotation(ExcludeFieldJson.class) != null)
							continue;
						
						field.setAccessible(true);
						try {
							if (field.get(oldEntity) != null && !field.get(oldEntity).equals(field.get(entity))) {
								oldValue.append(field.getName() + ": " + field.get(oldEntity) + "; ");
								newValue.append(field.getName() + ": " + field.get(entity) + "; ");
								isChange = true;
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							logger.warn(e.getMessage());
						}
					}
				}
			} else if(DELETE.equals(action)) {
				
				oldValue.append(JsonConvertUtils.convertObjectToJson(entity));
				isChange = true;
			}
			

			if(isChange) {
				AuditLog auditRecord = new AuditLog(userName, action, oldValue.toString(), newValue.toString(),
						new Timestamp(Calendar.getInstance().getTimeInMillis()), entity.getID(),
						entity.getClass().getName().replace("com.viettel.ocs.entity.", ""), ip, domain);
				tempSession.save(auditRecord);	
			}			
			
//			String insertStr = "INSERT INTO AuditLog(USER_NAME, ACTION, OLD_DETAIL, NEW_DETAIL, CREATED_DATE, ENTITY_ID, ENTITY_NAME, IP, DOMAIN_ID) ";
//			insertStr += " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";			
//			Query insertQuery = tempSession.createNativeQuery(insertStr);
//			insertQuery.setParameter(1, userName);
//			insertQuery.setParameter(2, action);
//			insertQuery.setParameter(3, oldValue.toString());
//			insertQuery.setParameter(4, newValue.toString());
//			insertQuery.setParameter(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));
//			insertQuery.setParameter(6, entity.getID());
//			insertQuery.setParameter(7, entity.getClass().getName().replace("com.viettel.ocs.entity.", ""));
//			insertQuery.setParameter(8, ip);
//			insertQuery.setParameter(9, domain);
//			insertQuery.executeUpdate();
//			tempSession.getTransaction().commit();// khong dung commit vi se goi lap ham postFlush
		} catch (Exception e) {
			logger.warn(e.getMessage());
//			tempSession.getTransaction().rollback();
		} finally {
			tempSession.close();
		}
	}
}

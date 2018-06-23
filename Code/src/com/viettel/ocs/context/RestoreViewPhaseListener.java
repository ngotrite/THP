package com.viettel.ocs.context;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.viettel.ocs.db.HibernateUtil;

public class RestoreViewPhaseListener implements PhaseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9062165433967413151L;

	public void beforePhase(PhaseEvent event) {

//		Session session = HibernateUtil.getCurrentSession();
//		Transaction trx = session.getTransaction();
//		if(!trx.isActive())
//			trx.begin();
	}
	
	public void afterPhase(PhaseEvent event) {
	}
	
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}
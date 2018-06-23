package com.viettel.ocs.context;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.viettel.ocs.db.HibernateUtil;

public class RenderResponsePhaseListener implements PhaseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8870665443652996751L;

	public void beforePhase(PhaseEvent event) {

	}
	
	public void afterPhase(PhaseEvent event) {
	}
	
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
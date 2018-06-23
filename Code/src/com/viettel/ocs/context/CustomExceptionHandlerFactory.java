package com.viettel.ocs.context;

import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.log4j.Logger;

import com.viettel.ocs.bean.BaseController;

public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ExceptionHandlerFactory parent;
	public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
		this.parent = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		ExceptionHandler result = new CustomExceptionHandler(parent.getExceptionHandler());
		return result;
	}
}

class CustomExceptionHandler extends ExceptionHandlerWrapper {
	
	private final static Logger logger = Logger.getLogger(CustomExceptionHandler.class);

	private ExceptionHandler wrapped;
	public CustomExceptionHandler(ExceptionHandler exception) {
		this.wrapped = exception;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {

		final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
		while (i.hasNext()) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

			// get the exception from context
			Throwable t = context.getException();

			final FacesContext fc = FacesContext.getCurrentInstance();
			final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
			final NavigationHandler nav = fc.getApplication().getNavigationHandler();

			// here you do what ever you want with exception
			try {

				// log error ?
				// log.log(Level.SEVERE, "Critical Exception!", t);

				// redirect error page
//				requestMap.put("exceptionMessage", t.getMessage());
//				nav.handleNavigation(fc, null, "/error");
//				fc.renderResponse();

				if(t.getMessage() != null && t.getMessage().contains("ConstraintViolationException")) {
					
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", 
							new BaseController().readProperties("common.deleteConstraint"));
					FacesContext.getCurrentInstance().addMessage(null, message);
					logger.warn("CONSTRAINT WARNING: " + t.getMessage());
				} else {
					
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", t.getMessage());
					FacesContext.getCurrentInstance().addMessage(null, message);
					logger.error("EXCEPTION: ", t);
				}				
			} finally {
				// remove it from queue
				i.remove();
			}
		}
		// parent hanle
		getWrapped().handle();
	}
}
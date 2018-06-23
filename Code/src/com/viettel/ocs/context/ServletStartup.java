
package com.viettel.ocs.context;

import javax.servlet.http.HttpServlet;

import com.viettel.ocs.db.HibernateUtil;

/**
 * 
 * @author huannn
 *
 * Startup all service that will be run when deploy
 */
public class ServletStartup extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4139945909777710750L;

	public ServletStartup() {
		
		HibernateUtil.initSessionFactory();
	 }
}

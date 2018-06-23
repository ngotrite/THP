package com.viettel.ocs.bean.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.ProfilePepDAO;
import com.viettel.ocs.dao.QosClassDAO;
import com.viettel.ocs.dao.QosDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.Qos;
import com.viettel.ocs.entity.QosClass;

@ManagedBean(name = "qosBean")
@ViewScoped
public class QoSBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Qos qos;
	private List<SelectItem> listQCI;
	private List<Qos> rowDts;
	private boolean isEditting = false;
	private QosDAO qosDAO;
	private QosClassDAO classDAO;

	public List<Qos> getRowDts() {
		return rowDts;
	}

	public void setRowDts(List<Qos> rowDts) {
		this.rowDts = rowDts;
	}

	public Qos getQos() {
		return qos;
	}

	public void setQos(Qos qos) {
		this.qos = qos;
	}

	public List<SelectItem> getListQCI() {
		return listQCI;
	}

	public void setListQCI(List<SelectItem> listQCI) {
		this.listQCI = listQCI;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public QosDAO getQosDAO() {
		return qosDAO;
	}

	public void setQosDAO(QosDAO qosDAO) {
		this.qosDAO = qosDAO;
	}

	// Init
	@PostConstruct
	public void init() {
		listQCI = new ArrayList<SelectItem>();
		this.isEditting = false;
		qos = new Qos();
		qosDAO = new QosDAO();
		classDAO = new QosClassDAO();
		initRows();
	}

	private void initRows() {
		rowDts = new ArrayList<>();
		rowDts = qosDAO.findAllWithoutDomain("");
		loadQCI();
		//updatTableQos();
	}

	// load combo for type Category
	public List<SelectItem> loadQCI() {
		List<QosClass> lstQosClass = classDAO.findAllQosClass();
		listQCI = new ArrayList<>();
		if (lstQosClass != null) {
			for (QosClass qosClass : lstQosClass) {
				if (qosClass != null) {
					listQCI.add(new SelectItem(qosClass.getQci(),
							qosClass.getQci() + ":" + qosClass.getResourceType() + ": " + qosClass.getName()));
				}
			}
		}
		return listQCI;
	}

	public void btnSave() {
		if (validate()) {
			if (!checkDoubleQosName(qos)) {
				if (isEditting) {
					qosDAO.saveOrUpdate(qos);
				} else {
					qosDAO.save(qos);
				}
				initRows();
				qos = new Qos();
				isEditting = false;
				this.showMessageINFO("common.save", " Qos ");
			} else {
				initRows();
				this.showNotification(FacesMessage.SEVERITY_WARN, " Qos ", this.readProperties("policy.errqosname"));
				return;
			}

		} else
			initRows();
		return;
	}

	private boolean checkDoubleQosName(Qos qos) {
		List<Qos> lstQosByQosName = qosDAO.loadProfilePepByPPName(qos.getQosId(), qos.getQosName());
		if (lstQosByQosName != null && lstQosByQosName.size() > 0)
			return true;
		else
			return false;
	}

	private boolean validate() {
		if (qos != null) {
			if (qos.getMbrul() < 0) {
				this.showMessageWARN("common.save", " Qos ", this.readProperties("policy.qos_MBRUL"));
				return false;
			}
			if (qos.getGbrul() < 0) {
				this.showMessageWARN("common.save", " Qos ", this.readProperties("policy.qos_GBRU"));
				return false;
			}

			if (qos.getMbrdl() < 0) {
				this.showMessageWARN("common.save", " Qos ", this.readProperties("policy.qos_GBRDL"));
				return false;
			}
			if (qos.getGbrdl() < 0) {
				this.showMessageWARN("common.save", " Qos ", this.readProperties("qos_MBRDL"));
				return false;
			}
//			if (qos.getMbrul() < qos.getGbrul()) {
//				this.showMessageWARN("common.save", " Qos ", this.readProperties("policy.qos_GBRUL_MBRUL"));
//				return false;
//			}
//			if (qos.getMbrdl() < qos.getGbrdl()) {
//				this.showMessageWARN("common.save", " Qos ", this.readProperties("policy.qos_GBRDL_MBRDL"));
//				return false;
//			}
		}
		return true;
	}

	public void btnNew() {
		isEditting = true;
		qos = new Qos();
	}

	public void btnCancel() {
		init();
		isEditting = false;
		qos = new Qos();
	}

	public void editQos(Qos qos) {
		this.qos = qos;
		isEditting = true;
	}

	public void deleteQos(Qos qos) {
		ProfilePepDAO dao = new ProfilePepDAO();
		int countPP = dao.countProfilePep(qos.getQosId());
		if (countPP > 0) {
			this.showMessageWARN("common.delete", super.readProperties("policy.qos"),this.readProperties("policy.qos_use"));
			return;
		} else {
			for (int i = 0; i < rowDts.size(); i++) {
				if (qos.getQosId() == rowDts.get(i).getQosId()) {
					rowDts.remove(i);
					qosDAO.delete(qos);
					isEditting  = false;
					this.qos = new Qos();
					updatTableQos();
					this.showMessageINFO("common.delete", super.readProperties("policy.qos"));
					break;
				}
			}
		}
	}

	private void updatTableQos() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('wgQos').clearFilters()");
	}
}

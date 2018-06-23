package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.offer.ActionBean;
import com.viettel.ocs.bean.offer.ActionTypeBean;
import com.viettel.ocs.bean.offer.BlockBean;
import com.viettel.ocs.bean.offer.DecisionTableBean;
import com.viettel.ocs.bean.offer.DynamicReservesBean;
import com.viettel.ocs.bean.offer.EventBean;
import com.viettel.ocs.bean.offer.NormalizerBean;
import com.viettel.ocs.bean.offer.OfferBean;
import com.viettel.ocs.bean.offer.OfferPackageBean;
import com.viettel.ocs.bean.offer.PriceComponentsBean;
import com.viettel.ocs.bean.offer.RateTableBean;
import com.viettel.ocs.bean.offer.SortPriceComponentBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.EventDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.OfferPackageDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Offer;

@ManagedBean(name="popupOfferBean")
@ViewScoped
public class PopupOfferBean extends BaseController implements Serializable{
	
	@ManagedProperty("#{offerBean}")
	private OfferBean offerBean;

	public void setOfferBean(OfferBean offerBean) {
		this.offerBean = offerBean;
	}

	@ManagedProperty("#{actionBean}")
	private ActionBean actionBean;

	public void setActionBean(ActionBean actionBean) {
		this.actionBean = actionBean;
	}

	@ManagedProperty("#{offerPackageBean}")
	private OfferPackageBean offerPackageBean;

	public void setOfferPackageBean(OfferPackageBean offerPackageBean) {
		this.offerPackageBean = offerPackageBean;
	}

	@ManagedProperty("#{priceComponentsBean}")
	private PriceComponentsBean priceComponentsBean;

	public void setPriceComponentsBean(PriceComponentsBean priceComponentsBean) {
		this.priceComponentsBean = priceComponentsBean;
	}

	@ManagedProperty("#{blockBean}")
	private BlockBean blockBean;

	public void setBlockBean(BlockBean blockBean) {
		this.blockBean = blockBean;
	}

	@ManagedProperty("#{rateTableBean}")
	private RateTableBean rateTableBean;

	public void setRateTableBean(RateTableBean rateTableBean) {
		this.rateTableBean = rateTableBean;
	}

	@ManagedProperty("#{decisionTableBean}")
	private DecisionTableBean decisionTableBean;

	public void setDecisionTableBean(DecisionTableBean decisionTableBean) {
		this.decisionTableBean = decisionTableBean;
	}

	@ManagedProperty("#{normalizerBean}")
	private NormalizerBean normalizerBean;

	public void setNormalizerBean(NormalizerBean normalizerBean) {
		this.normalizerBean = normalizerBean;
	}

	@ManagedProperty("#{actionTypeBean}")
	private ActionTypeBean actionTypeBean;

	public void setActionTypeBean(ActionTypeBean actionTypeBean) {
		this.actionTypeBean = actionTypeBean;
	}

	@ManagedProperty("#{dynamicReservesBean}")
	private DynamicReservesBean dynamicReservesBean;

	public void setDynamicReservesBean(DynamicReservesBean dynamicReservesBean) {
		this.dynamicReservesBean = dynamicReservesBean;
	}

	@ManagedProperty("#{eventBean}")
	private EventBean eventBean;

	public void setEventBean(EventBean eventBean) {
		this.eventBean = eventBean;
	}

	@ManagedProperty("#{sortPriceComponentBean}")
	private SortPriceComponentBean sortPriceComponentBean;

	public void setSortPriceComponentBean(SortPriceComponentBean sortPriceComponentBean) {
		this.sortPriceComponentBean = sortPriceComponentBean;
	}
	
	private OfferDAO offerDao;
	private OfferPackageDAO offerPackageDAO;
	private ActionDAO actionDao;
	private EventDAO eventDAO;
	private PriceComponentDAO pcDao;
	private RateTableDAO rateTableDAO;
	private DecisionTableDAO decisionTableDAO;
	private ActionTypeDAO actionTypeDAO;
	private BlockDAO blockDAO;
	private NormalizerDAO normalizerDAO;
	private DynamicReserveDAO dynamicReserveDAO;
	private SortPriceComponentDAO sortPriceComponentDAO;
	
	private String className;
	private Long objID;
	public PopupOfferBean() {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String treeType = params.get("treeType");
		if (treeType.contains(";")) {
			String[] parts = treeType.split(";");
			objID = Long.parseLong(parts[0]);
			className = parts[1];
		} else
			return;
		
		offerDao = new OfferDAO();
		offerPackageDAO = new OfferPackageDAO();
		actionDao = new ActionDAO();
		pcDao = new PriceComponentDAO();
		rateTableDAO = new RateTableDAO();
		decisionTableDAO = new DecisionTableDAO();
		actionTypeDAO = new ActionTypeDAO();
		eventDAO = new EventDAO();
		blockDAO = new BlockDAO();
		normalizerDAO = new NormalizerDAO();
		dynamicReserveDAO = new DynamicReserveDAO();
		sortPriceComponentDAO = new SortPriceComponentDAO();		
	}
	
	@PostConstruct
	public void init() {
		
		if(Offer.class.getName().equals(className)) {
			
			Offer offer = offerDao.get(objID);
			offerBean.refreshOfferVersionPage(offer, null);	
		}
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Long getObjID() {
		return objID;
	}

	public void setObjID(Long objID) {
		this.objID = objID;
	}
}

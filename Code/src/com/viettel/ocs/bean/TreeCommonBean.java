package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.bean.catalog.BalTypeBean;
import com.viettel.ocs.bean.catalog.BillingCycleTypeBean;
import com.viettel.ocs.bean.catalog.CdrGenFileNameBean;
import com.viettel.ocs.bean.catalog.CdrProcessConfigBean;
import com.viettel.ocs.bean.catalog.CdrServiceBean;
import com.viettel.ocs.bean.catalog.CdrTemplateBean;
import com.viettel.ocs.bean.catalog.CssUssdResponseBean;
import com.viettel.ocs.bean.catalog.GeoHomeBean;
import com.viettel.ocs.bean.catalog.MapAcmbalBalBean;
import com.viettel.ocs.bean.catalog.MapSharebalBalBean;
import com.viettel.ocs.bean.catalog.ParameterBean;
import com.viettel.ocs.bean.catalog.ReserveInfoBean;
import com.viettel.ocs.bean.catalog.ServiceBean;
import com.viettel.ocs.bean.catalog.SmsNotifyTemplateBean;
import com.viettel.ocs.bean.catalog.StateSetBean;
import com.viettel.ocs.bean.catalog.StatisticItemBean;
import com.viettel.ocs.bean.catalog.TriggerDestinationBean;
import com.viettel.ocs.bean.catalog.TriggerMsgBean;
import com.viettel.ocs.bean.catalog.TriggerOcsBean;
import com.viettel.ocs.bean.catalog.UnitTypeBean;
import com.viettel.ocs.bean.catalog.ZoneMapBean;
import com.viettel.ocs.bean.policy.PCCRuleBean;
import com.viettel.ocs.bean.policy.PolicyProfileBean;
import com.viettel.ocs.bean.sys.SystemConfigBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeNodeType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.BillingCycleTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrGenFilenameDAO;
import com.viettel.ocs.dao.CdrProcessConfigDAO;
import com.viettel.ocs.dao.CdrServiceDAO;
import com.viettel.ocs.dao.CdrTemplateDAO;
import com.viettel.ocs.dao.CssUssdResponseDAO;
import com.viettel.ocs.dao.GeoHomeZoneDAO;
import com.viettel.ocs.dao.MapAcmbalBalDAO;
import com.viettel.ocs.dao.MapSharebalBalDAO;
import com.viettel.ocs.dao.PCCRuleDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.ProfilePepDAO;
import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.dao.ServiceDAO;
import com.viettel.ocs.dao.SmsNotifyTemplateDAO;
import com.viettel.ocs.dao.StateGroupDAO;
import com.viettel.ocs.dao.StateTypeDAO;
import com.viettel.ocs.dao.StatisticItemDAO;
import com.viettel.ocs.dao.SystemConfigDAO;
import com.viettel.ocs.dao.ThresholdDAO;
import com.viettel.ocs.dao.TriggerDestinationDAO;
import com.viettel.ocs.dao.TriggerMsgDAO;
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.dao.TriggerTypeDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.dao.ZoneDAO;
import com.viettel.ocs.dao.ZoneMapDAO;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrGenFilename;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.CssUssdResponse;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.MapAcmbalBal;
import com.viettel.ocs.entity.MapSharebalBal;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.Service;
import com.viettel.ocs.entity.SmsNotifyTemplate;
import com.viettel.ocs.entity.StateGroup;
import com.viettel.ocs.entity.StateType;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.SystemConfig;
import com.viettel.ocs.entity.Threshold;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.entity.TriggerMsg;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.TriggerType;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneMap;
import com.viettel.ocs.model.OcsTreeNode;
import com.viettel.ocs.util.CommonUtil;

@ManagedBean(name = "treeCommonBean")
@ViewScoped
public class TreeCommonBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4432210493238596849L;
	private ParameterDAO parameterDAO;
	private UnitTypeDAO unitTypeDao;
	private ZoneMapDAO zoneMapDao;
	private StateGroupDAO stateGroupDAO;
	private StateTypeDAO stateTypeDAO;
	private BalTypeDAO balTypeDAO;
	private ThresholdDAO thresholdDAO;
	private ReserveInfoDAO reserveInfoDAO;
	private ZoneDAO zoneDAO;
	private GeoHomeZoneDAO geoHomeDAO;
	private TriggerDestinationDAO triggerDesDAO;
	private TriggerMsgDAO triggerMsgDAO;
	private BillingCycleTypeDAO billingCycleTypeDAO;
	private ProfilePepDAO profilePepDAO;
	private PCCRuleDAO pccRuleDAO;
	private TriggerOcsDAO triggerOcsDAO;
	private TriggerTypeDAO triggerTypeDAO;
	private ServiceDAO serviceDAO;
	private String formType;
	private String treeType;
	private boolean showBtnCatNew;
	private String contentTitle;
	private MapSharebalBalDAO mapShareBalDAO;
	private MapAcmbalBalDAO mapAcmBalDAO;
	private CdrServiceDAO cdrServiceDAO;
	private CdrTemplateDAO cdrTemplateDAO;
	private CdrGenFilenameDAO cdrGenFilenameDAO;
	private CdrProcessConfigDAO cdrProcessConfigDAO;
	private SystemConfigDAO systemConfigDAO;
	private StatisticItemDAO statisticItemDAO; 
	private SmsNotifyTemplateDAO smsNotiTemDAO;
	private CssUssdResponseDAO cssUssdResponseDAO;

	private TreeNode root;
	private TreeNode selectedNode;
	private Map<Long, TreeNode> mapCatFirstNode = new HashMap<Long, TreeNode>();
	private List<Long> lstCatID = new ArrayList<Long>();
	private Map<Long, TreeNode> mapCatNode = new HashMap<Long, TreeNode>();
	// private Map<String, TreeNode> mapAllNode = new HashMap<String,
	// TreeNode>();
	private Map<String, List<TreeNode>> mapListNode = new HashMap<>();
	private String txtTreeSearch;
	private BaseEntity currentEntity;

	// Parameter
	@ManagedProperty("#{parameterBean}")
	private ParameterBean parameterBean;

	public void setParameterBean(ParameterBean parameterBean) {
		this.parameterBean = parameterBean;
	}

	// triggerOcsBean
	@ManagedProperty("#{triggerOcsBean}")
	private TriggerOcsBean triggerOcsBean;

	public void setTriggerOcsBean(TriggerOcsBean triggerOcsBean) {
		this.triggerOcsBean = triggerOcsBean;
	}

	// Trigger Destination
	@ManagedProperty("#{triggerMsgBean}")
	private TriggerMsgBean triggerMsgBean;

	public void setTriggerMsgBean(TriggerMsgBean triggerMsgBean) {
		this.triggerMsgBean = triggerMsgBean;
	}

	// Unit Type
	@ManagedProperty("#{unitBean}")
	private UnitTypeBean unitBean;

	public void setUnitBean(UnitTypeBean unitBean) {
		this.unitBean = unitBean;
	}

	// Zone Map
	@ManagedProperty("#{zoneMapBean}")
	private ZoneMapBean zoneMapBean;

	public void setZoneMapBean(ZoneMapBean zoneMapBean) {
		this.zoneMapBean = zoneMapBean;
	}

	// Bal Type
	@ManagedProperty("#{balTypeBean}")
	private BalTypeBean balTypeBean;

	public void setBalTypeBean(BalTypeBean balTypeBean) {
		this.balTypeBean = balTypeBean;
	}

	// Geo Home Zone
	@ManagedProperty("#{geoBean}")
	private GeoHomeBean geoBean;

	public void setGeoBean(GeoHomeBean geoBean) {
		this.geoBean = geoBean;
	}

	@ManagedProperty("#{stateSetBean}")
	private StateSetBean stateSetBean;

	public void setStateSetBean(StateSetBean stateSetBean) {
		this.stateSetBean = stateSetBean;
	}

	// Billing Cycle
	@ManagedProperty("#{billingCycleTypeBean}")
	private BillingCycleTypeBean billingCycleTypeBean;

	public void setBillingCycleTypeBean(BillingCycleTypeBean billingCycleTypeBean) {
		this.billingCycleTypeBean = billingCycleTypeBean;
	}

	// Trigger Destination
	@ManagedProperty("#{triggerDesBean}")
	private TriggerDestinationBean triggerDesBean;

	public void setTriggerDesBean(TriggerDestinationBean triggerDesBean) {
		this.triggerDesBean = triggerDesBean;
	}

	// Trigger Destination
	@ManagedProperty("#{serviceBean}")
	private ServiceBean serviceBean;

	public void setServiceBean(ServiceBean serviceBean) {
		this.serviceBean = serviceBean;
	}

	// Map Share Bal
	@ManagedProperty("#{mapShareBalBean}")
	private MapSharebalBalBean mapShareBalBean;

	public void setMapShareBalBean(MapSharebalBalBean mapShareBalBean) {
		this.mapShareBalBean = mapShareBalBean;
	}

	// Map Acm Bal
	@ManagedProperty("#{mapAcmBalBean}")
	private MapAcmbalBalBean mapAcmBalBean;

	public void setMapAcmBalBean(MapAcmbalBalBean mapAcmBalBean) {
		this.mapAcmBalBean = mapAcmBalBean;
	}

	// Cdr service
	@ManagedProperty("#{cdrServiceBean}")
	private CdrServiceBean cdrServiceBean;

	public void setCdrServiceBean(CdrServiceBean cdrServiceBean) {
		this.cdrServiceBean = cdrServiceBean;
	}

	// Cdr Template
	@ManagedProperty("#{cdrTemplateBean}")
	private CdrTemplateBean cdrTemplateBean;

	public void setCdrTemplateBean(CdrTemplateBean cdrTemplateBean) {
		this.cdrTemplateBean = cdrTemplateBean;
	}

	// cdrGenFileNameBean
	@ManagedProperty("#{cdrGenFileNameBean}")
	private CdrGenFileNameBean cdrGenFileNameBean;

	public void setCdrGenFileNameBean(CdrGenFileNameBean cdrGenFileNameBean) {
		this.cdrGenFileNameBean = cdrGenFileNameBean;
	}

	// cdr Process Config Bean
	@ManagedProperty("#{cdrProcessConfigBean}")
	private CdrProcessConfigBean cdrProcessConfigBean;

	public void setCdrProcessConfigBean(CdrProcessConfigBean cdrProcessConfigBean) {
		this.cdrProcessConfigBean = cdrProcessConfigBean;
	}

	// System Config Bean
	@ManagedProperty("#{systemConfigBean}")
	private SystemConfigBean systemConfigBean;

	public void setSystemConfigBean(SystemConfigBean systemConfigBean) {
		this.systemConfigBean = systemConfigBean;
	}
	
	// Statistic Item Bean
	@ManagedProperty("#{statisticItemBean}")
	private StatisticItemBean statisticItemBean;
	
	public void setStatisticItemBean(StatisticItemBean statisticItemBean) {
		this.statisticItemBean = statisticItemBean;
	}
	
	@ManagedProperty("#{smsNotiTemBean}")
	private SmsNotifyTemplateBean smsNotiTemBean;

	public void setSmsNotiTemBean(SmsNotifyTemplateBean smsNotiTemBean) {
		this.smsNotiTemBean = smsNotiTemBean;
	}
	
	@ManagedProperty("#{cssUssdResponseBean}")
	private CssUssdResponseBean cssUssdResponseBean;
	
	public void setCssUssdResponseBean(CssUssdResponseBean cssUssdResponseBean) {
		this.cssUssdResponseBean = cssUssdResponseBean;
	}

	public TreeCommonBean() {
		defaulData();
		catDao = new CategoryDAO();
		parameterDAO = new ParameterDAO();
		unitTypeDao = new UnitTypeDAO();
		zoneMapDao = new ZoneMapDAO();
		stateGroupDAO = new StateGroupDAO();
		stateTypeDAO = new StateTypeDAO();
		balTypeDAO = new BalTypeDAO();
		thresholdDAO = new ThresholdDAO();
		reserveInfoDAO = new ReserveInfoDAO();
		billingCycleTypeDAO = new BillingCycleTypeDAO();
		triggerOcsDAO = new TriggerOcsDAO();
		triggerTypeDAO = new TriggerTypeDAO();
		serviceDAO = new ServiceDAO();

		zoneDAO = new ZoneDAO();
		geoHomeDAO = new GeoHomeZoneDAO();
		triggerDesDAO = new TriggerDestinationDAO();
		triggerMsgDAO = new TriggerMsgDAO();

		profilePepDAO = new ProfilePepDAO();
		pccRuleDAO = new PCCRuleDAO();

		mapShareBalDAO = new MapSharebalBalDAO();
		mapAcmBalDAO = new MapAcmbalBalDAO();

		cdrServiceDAO = new CdrServiceDAO();
		cdrTemplateDAO = new CdrTemplateDAO();
		cdrGenFilenameDAO = new CdrGenFilenameDAO();
		cdrProcessConfigDAO = new CdrProcessConfigDAO();

		systemConfigDAO = new SystemConfigDAO();
		statisticItemDAO = new StatisticItemDAO();
		smsNotiTemDAO = new SmsNotifyTemplateDAO();
		cssUssdResponseDAO = new CssUssdResponseDAO();

		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if (viewId.endsWith("parameter.xhtml")) {
			treeType = TreeType.CATALOG_PARAMETER;
		} else if (viewId.endsWith("unittype.xhtml")) {
			treeType = TreeType.CATALOG_UNIT_TYPE;
		} else if (viewId.endsWith("zonemap.xhtml")) {
			treeType = TreeType.CATALOG_ZONE_DATA;
		} else if (viewId.endsWith("reserveInfo.xhtml")) {
			treeType = TreeType.CATALOG_RESERVE_INFO;
		} else if (viewId.endsWith("stateset.xhtml")) {
			treeType = TreeType.CATALOG_STATE_SET;
		} else if (viewId.endsWith("baltype.xhtml")) {
			treeType = TreeType.CATALOG_BALANCE;
		} else if (viewId.endsWith("geohomezone.xhtml")) {
			treeType = TreeType.CATALOG_GEO_HOME;
		} else if (viewId.endsWith("triggerdestination.xhtml")) {
			treeType = TreeType.CATALOG_TRIGGER_DESTINATION;
		} else if (viewId.endsWith("triggermsg.xhtml")) {
			treeType = TreeType.CATALOG_TRIGGER_MESSAGE;
		} else if (viewId.endsWith("billingcycle.xhtml")) {
			treeType = TreeType.CATALOG_BILLING_CYCLE;
		} else if (viewId.endsWith("triggerocs.xhtml")) {
			treeType = TreeType.CATALOG_TRIGGER_OCS;
		} else if (viewId.endsWith("services.xhtml")) {
			treeType = TreeType.CATALOG_SERVICE;
		} else if (viewId.endsWith("cdr_service.xhtml")) {
			treeType = TreeType.CATALOG_CDR_SERVICE;
		} else if (viewId.endsWith("cdr_template.xhtml")) {
			treeType = TreeType.CATALOG_CDR_TEMPLATE;
		} else if (viewId.endsWith("cdr_gen_fileName.xhtml")) {
			treeType = TreeType.CATALOG_CDR_GEN_FILENAME;
		} else if (viewId.endsWith("cdr_process_config.xhtml")) {
			treeType = TreeType.CATALOG_CDR_PROCESS_CONFIG;
		} else if (viewId.endsWith("system_config.xhtml")) {
			treeType = TreeType.SYS_SYS_CONFIG;
		} else if (viewId.endsWith("statistic_item.xhtml")) {
			treeType = TreeType.CATALOG_STATISTIC_ITEM;
		} else if (viewId.endsWith("sms_notify_template.xhtml")) {
			treeType = TreeType.CATALOG_SMS_NOTIFY_TEMPLATE;
		} else if (viewId.endsWith("css_ussd_response.xhtml")) {
			treeType = TreeType.CATALOG_CSS_USSD_RESPONSE;
		}
		buildTree(treeType);
		initCategoryType();
	}

	public void onload() {
		// Temporary not work when use jsf template

		if (!FacesContext.getCurrentInstance().isPostback()) {
			buildTree(treeType);
			initCategoryType();
		}
	}

	/************ BUILD TREE - BEGIN *****************/

	private void buildTree(String treeType) {
		
		if (treeType == null)
			return;

		lstCatID.clear();
		mapCatNode.clear();
		mapListNode.clear();
		
		root = new OcsTreeNode();

		switch (treeType) {
		case TreeType.CATALOG_PARAMETER:
			buildTreeParameter();
			break;
		case TreeType.CATALOG_UNIT_TYPE:
			buildTreeUnitType();
			break;
		case TreeType.CATALOG_ZONE_DATA:
			buildTreeZoneData();
			break;
		case TreeType.CATALOG_STATE_SET:
			buildTreeStateSet();
			break;
		case TreeType.CATALOG_BALANCE:
			buildTreeBalance();
			break;
		case TreeType.CATALOG_GEO_HOME:
			buildTreeGeoHome();
			break;
		case TreeType.CATALOG_RESERVE_INFO:
			buildTreeReserveInfo();
			break;
		case TreeType.CATALOG_TRIGGER_DESTINATION:
			buildTreeTriggerDes();
			break;
		case TreeType.CATALOG_TRIGGER_MESSAGE:
			buildTreeTriggerMsg();
			break;
		case TreeType.CATALOG_BILLING_CYCLE:
			buildTreeBillingCycle();
			break;
		case TreeType.POLICY_POLICY_PROFILE:
			buildTreePolicyProfile();
			break;
		case TreeType.POLICY_PCC_RULE:
			buildTreePccrule();
			break;
		case TreeType.CATALOG_TRIGGER_OCS:
			buildTreeTriggerOcs();
			break;
		case TreeType.CATALOG_SERVICE:
			buildTreeService();
			break;
		case TreeType.CATALOG_CDR_SERVICE:
			buildTreeCDRService();
			break;
		case TreeType.CATALOG_CDR_TEMPLATE:
			buildTreeCDRTemplate();
			break;
		case TreeType.CATALOG_CDR_GEN_FILENAME:
			buildTreeCDRGenFileName();
			break;
		case TreeType.CATALOG_CDR_PROCESS_CONFIG:
			buildTreeCDRProcessConfig();
			break;
		case TreeType.SYS_SYS_CONFIG:
			buildTreeSystemConfig();
			break;
		case TreeType.CATALOG_STATISTIC_ITEM:
			buildTreeStatisticItem();
			break;
		case TreeType.CATALOG_SMS_NOTIFY_TEMPLATE:
			buildTreeSmsNotifyTemplate();
			break;
		case TreeType.CATALOG_CSS_USSD_RESPONSE:
			buildTreeCssUssdResponse();
			break;
		default:
			break;
		}
	}

	// Cuongvv
	public void buildTreeParameter() {
		/** PARAMETER */
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Parameter belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<Parameter> lstParameter = parameterDAO.findByConditions(cols, operators, values, "index");
		for (Parameter parameter : lstParameter) {
						
			if(!isFoundNode(parameter))
				continue;
			
			TreeNode catNode = mapCatNode.get(parameter.getCategoryId());
			if (catNode != null) {
				TreeNode parameterNode = new OcsTreeNode(parameter, catNode);
				settingNewTreeNode(parameter, parameterNode);
			}
		}
	}

	public void buildTreeStateSet() {
		/** State Set */
		// Build children categories
		buildTreeByCatType(treeType, root);
		// Add State Set belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<StateGroup> lsStateGroups = stateGroupDAO.findByConditions(cols, operators, values, "index");
		List<StateType> lsStateTypes = stateTypeDAO.findByConditions(cols, operators, values, "index ASC");
		for (TreeNode treeNode : root.getChildren()) {
			Category category = (Category) treeNode.getData();
			if (category.getCategoryType() == CategoryType.CTL_STATEGROUP) {
				for (StateGroup stateGroup : lsStateGroups) {
					
					if(!isFoundNode(stateGroup))
						continue;
					
					TreeNode catNode = mapCatNode.get(stateGroup.getCategoryId());
					if (catNode != null) {
						TreeNode stateGroupNode = new OcsTreeNode(stateGroup, catNode);
						stateGroupNode.setType(TreeNodeType.CAT_STATE_GROUP);
						settingNewTreeNode(stateGroup, stateGroupNode);
					}
				}
			} else if (category.getCategoryType() == CategoryType.CTL_STATETYPE) {
				for (StateType stateType : lsStateTypes) {				
					
					if(!isFoundNode(stateType))
						continue;
					
					TreeNode catNode = mapCatNode.get(stateType.getCategoryId());
					if (catNode != null) {
						TreeNode stateTypeNode = new OcsTreeNode(stateType, catNode);
						stateTypeNode.setType(TreeNodeType.CAT_STATE_TYPE);
						settingNewTreeNode(stateType, stateTypeNode);
					}
				}
			}
		}
	}

	// Build Tree Billing Cycle
	public void buildTreeBillingCycle() {
		/** Billing Cycle */
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Parameter belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<BillingCycleType> lstBillingCycle = billingCycleTypeDAO.findByConditions(cols, operators, values, "index");
		for (BillingCycleType billingCycleType : lstBillingCycle) {
			
			if(!isFoundNode(billingCycleType))
				continue;
			
			TreeNode catNode = mapCatNode.get(billingCycleType.getCategoryId());
			if (catNode != null) {
				TreeNode billingCycleTypeNode = new OcsTreeNode(billingCycleType, catNode);
				settingNewTreeNode(billingCycleType, billingCycleTypeNode);
			}
		}
	}

	// / --------------
	/**** UNIT TYPE - TENT ****/
	public void buildTreeUnitType() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Unit Type belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<UnitType> lstUnitType = unitTypeDao.findByConditions(cols, operators, values, "index ASC");
		for (UnitType unitType : lstUnitType) {
			
			if(!isFoundNode(unitType))
				continue;
			
			TreeNode catNode = mapCatNode.get(unitType.getCategoryId());
			if (catNode != null) {
				TreeNode unitTypeMapNode = new OcsTreeNode(unitType, catNode);
				settingNewTreeNode(unitType, unitTypeMapNode);
			}
		}
	}

	/**** ZONE DATA - TENT ****/
	public void buildTreeZoneData() {

		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Zone Data belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<ZoneMap> lstZoneMap = zoneMapDao.findByConditions(cols, operators, values, "index");
		for (ZoneMap zoneMap : lstZoneMap) {
			
			if(!isFoundNode(zoneMap))
				continue;
			
			TreeNode catNode = mapCatNode.get(zoneMap.getCategoryId());
			if (catNode != null) {
				TreeNode zoneMapNode = new OcsTreeNode(zoneMap, catNode);
				settingNewTreeNode(zoneMap, zoneMapNode);
				List<Zone> lstZone = zoneDAO.findZoneByConditions(zoneMap.getZoneMapId());
				for (Zone zone : lstZone) {
					TreeNode zoneNode = new OcsTreeNode(zone, zoneMapNode);
					settingNewTreeNode(zone, zoneNode);
				}
			}
		}
	}

	/**** BALANCES - TENT ****/
	public void buildTreeBalance() {

		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add BalType belong to Category

		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };

		List<BalType> lstBalType = balTypeDAO.findByConditions(cols, operators, values, "index");

		for (BalType balType : lstBalType) {
			
			if(!isFoundNode(balType))
				continue;
			
			TreeNode catNode = mapCatNode.get(balType.getCategoryId());
			if (catNode != null) {
				TreeNode balTypeNode = new OcsTreeNode(balType, catNode);
				settingNewTreeNode(balType, balTypeNode);
				List<Threshold> lstThreshold = balTypeDAO.findThresholdByBalType(balType.getBalTypeId());
				for (Threshold threshold : lstThreshold) {
					TreeNode thresholdNode = new OcsTreeNode(threshold, balTypeNode);
					settingNewTreeNode(threshold, thresholdNode);
					List<TriggerOcs> lsTTriggerOcs = thresholdDAO.findTriggerOcsByTH(threshold.getThresholdId());
					for (TriggerOcs triggerOcs : lsTTriggerOcs) {
						TreeNode triggerOcsNode = new OcsTreeNode(triggerOcs, thresholdNode);
						settingNewTreeNode(triggerOcs, triggerOcsNode);
					}
				}
			}
		}

		List<MapSharebalBal> lstMapShareBal = mapShareBalDAO.findByConditions(cols, operators, values, "index");

		for (MapSharebalBal mapShareBal : lstMapShareBal) {
			
			if(!isFoundNode(mapShareBal))
				continue;
			
			TreeNode catNode = mapCatNode.get(mapShareBal.getCategoryId());
			if (catNode != null) {
				TreeNode balTypeNode = new OcsTreeNode(mapShareBal, catNode);
				settingNewTreeNode(mapShareBal, balTypeNode);
			}
		}

		List<MapAcmbalBal> lstMapAcmBal = mapAcmBalDAO.findByConditions(cols, operators, values, "index");

		for (MapAcmbalBal mapAcmBal : lstMapAcmBal) {
			
			if(!isFoundNode(mapAcmBal))
				continue;
			
			TreeNode catNode = mapCatNode.get(mapAcmBal.getCategoryId());
			if (catNode != null) {
				TreeNode mapAcmBalNode = new OcsTreeNode(mapAcmBal, catNode);
				settingNewTreeNode(mapAcmBal, mapAcmBalNode);
			}
		}

	}

	/**** GEO HOME ZONE - TENT ****/
	public void buildTreeGeoHome() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Unit Type belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<GeoHomeZone> lstGeoHome = geoHomeDAO.findByConditions(cols, operators, values, "index");
		for (GeoHomeZone geoHome : lstGeoHome) {
			
			if(!isFoundNode(geoHome))
				continue;
			
			TreeNode catNode = mapCatNode.get(geoHome.getCategoryId());
			if (catNode != null) {
				TreeNode geoHomeNode = new OcsTreeNode(geoHome, catNode);
				settingNewTreeNode(geoHome, geoHomeNode);

			}
		}
	}

	/**** TRIGGER DESTINATION - CUONGVV ****/
	public void buildTreeTriggerDes() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Trigger Destination belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<TriggerDestination> lstTriggerDes = triggerDesDAO.findByConditions(cols, operators, values, "index");
		for (TriggerDestination triggerDes : lstTriggerDes) {
			
			if(!isFoundNode(triggerDes))
				continue;
			
			TreeNode catNode = mapCatNode.get(triggerDes.getCategoryId());
			if (catNode != null) {
				TreeNode triggerDesNode = new OcsTreeNode(triggerDes, catNode);
				settingNewTreeNode(triggerDes, triggerDesNode);
			}
		}
	}

	/**** TRIGGER MESSAGE - CUONGVV ****/
	public void buildTreeTriggerMsg() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Trigger Destination belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<TriggerMsg> lstTriggerMsg = triggerMsgDAO.findByConditions(cols, operators, values, "index");
		for (TriggerMsg triggerMsgMap : lstTriggerMsg) {
			
			if(!isFoundNode(triggerMsgMap))
				continue;
			
			TreeNode catNode = mapCatNode.get(triggerMsgMap.getCategoryId());
			if (catNode != null) {

				TreeNode triggerMsgMapNode = new OcsTreeNode(triggerMsgMap, catNode);
				settingNewTreeNode(triggerMsgMap, triggerMsgMapNode);
			}
		}
	}

	public void buildTreeTriggerOcs() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Trigger Destination belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };

		for (TreeNode treeNode : root.getChildren()) {
			Category category = (Category) treeNode.getData();
			if (category.getCategoryType() == CategoryType.CTL_TO_TRIGGER_OCS) {
				List<TriggerOcs> lsTriggerOcs = triggerOcsDAO.findByConditions(cols, operators, values, "index");
				for (TriggerOcs triggerOcs : lsTriggerOcs) {
					
					if(!isFoundNode(triggerOcs))
						continue;
					
					TreeNode catNode = mapCatNode.get(triggerOcs.getCategoryId());
					if (catNode != null) {
						TreeNode triggerOcsNode = new OcsTreeNode(triggerOcs, catNode);
						settingNewTreeNode(triggerOcs, triggerOcsNode);
					}
				}
			} else if (category.getCategoryType() == CategoryType.CTL_TT_TRIGGER_TYPE) {
				List<TriggerType> lsTriggerType = triggerTypeDAO.findByConditions(cols, operators, values, "index");
				for (TriggerType triggerType : lsTriggerType) {
					
					if(!isFoundNode(triggerType))
						continue;
					
					TreeNode catNode = mapCatNode.get(triggerType.getCategoryId());
					if (catNode != null) {
						TreeNode triggerTypeNode = new OcsTreeNode(triggerType, catNode);
						settingNewTreeNode(triggerType, triggerTypeNode);
					}
				}
			}
		}
	}

	public void buildTreeService() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add Trigger Destination belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<Service> lstService = serviceDAO.findByConditions(cols, operators, values, "index ASC");
		for (Service service : lstService) {
			
			if(!isFoundNode(service))
				continue;
			
			TreeNode catNode = mapCatNode.get(service.getCategoryId());
			if (catNode != null) {
				TreeNode serviceNode = new OcsTreeNode(service, catNode);
				settingNewTreeNode(service, serviceNode);
			}
		}
	}

	// build tree CDR Service
	public void buildTreeCDRService() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<CdrService> lstCdrService = cdrServiceDAO.findCdrServiceByConditions(lstCatID);
		for (CdrService cdrService : lstCdrService) {
			
			if(!isFoundNode(cdrService))
				continue;
			
			TreeNode catNode = mapCatNode.get(cdrService.getCategoryId());
			if (catNode != null) {
				TreeNode cdrServiceNode = new OcsTreeNode(cdrService, catNode);
				settingNewTreeNode(cdrService, cdrServiceNode);
			}
		}
	}

	// build tree CDR Service
	public void buildTreeCDRTemplate() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<CdrTemplate> lstCdrTemplate = cdrTemplateDAO.findCdrTemplateByConditions(lstCatID);
		for (CdrTemplate cdrTemplate : lstCdrTemplate) {
			
			if(!isFoundNode(cdrTemplate))
				continue;
			
			TreeNode catNode = mapCatNode.get(cdrTemplate.getCategoryId());
			if (catNode != null) {
				TreeNode cdrTemplateNode = new OcsTreeNode(cdrTemplate, catNode);
				settingNewTreeNode(cdrTemplate, cdrTemplateNode);
			}
		}
	}

	// build tree CDR Gen file name
	public void buildTreeCDRGenFileName() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<CdrGenFilename> lstCdrGenFilename = cdrGenFilenameDAO.findCdrGenFilenameByConditions(lstCatID);
		for (CdrGenFilename cdrGenFilename : lstCdrGenFilename) {
			
			if(!isFoundNode(cdrGenFilename))
				continue;
			
			TreeNode catNode = mapCatNode.get(cdrGenFilename.getCategoryId());
			if (catNode != null) {
				TreeNode cdrTemplateNode = new OcsTreeNode(cdrGenFilename, catNode);
				settingNewTreeNode(cdrGenFilename, cdrTemplateNode);
			}
		}
	}

	// build tree CDR ProcessConfig
	public void buildTreeCDRProcessConfig() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<CdrProcessConfig> lstCdrProcessConfig = cdrProcessConfigDAO.findCdrProcessConfigByConditions(lstCatID);
		for (CdrProcessConfig cdrProcessConfig : lstCdrProcessConfig) {
			
			if(!isFoundNode(cdrProcessConfig))
				continue;
			
			TreeNode catNode = mapCatNode.get(cdrProcessConfig.getCategoryId());
			if (catNode != null) {
				TreeNode cdrTemplateNode = new OcsTreeNode(cdrProcessConfig, catNode);
				settingNewTreeNode(cdrProcessConfig, cdrTemplateNode);
			}
		}
	}

	public void buildTreeSystemConfig() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<SystemConfig> lstSystemConfig = systemConfigDAO.findByListCat(lstCatID);
		for (SystemConfig systemConfig : lstSystemConfig) {
			
			if(!isFoundNode(systemConfig))
				continue;
			
			TreeNode catNode = mapCatNode.get(systemConfig.getCategoryId());
			if (catNode != null) {
				TreeNode systemConfigNode = new OcsTreeNode(systemConfig, catNode);
				settingNewTreeNode(systemConfig, systemConfigNode);
			}
		}
	}
	
	private void buildTreeStatisticItem() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<StatisticItem> lstStatisticItem = statisticItemDAO.findByListCat(lstCatID);
		for (StatisticItem statisticItem : lstStatisticItem) {
			
			if(!isFoundNode(statisticItem))
				continue;
			
			TreeNode catNode = mapCatNode.get(statisticItem.getCategoryId());
			if (catNode != null) {
				TreeNode statisticItemNode = new OcsTreeNode(statisticItem, catNode);
				settingNewTreeNode(statisticItem, statisticItemNode);
			}
		}
	}
	
	public void buildTreeSmsNotifyTemplate() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<SmsNotifyTemplate> lstSmsNotifyTemplate = smsNotiTemDAO.findByListCat(lstCatID);
		for (SmsNotifyTemplate smsNotifyTemplate : lstSmsNotifyTemplate) {
			
			if(!isFoundNode(smsNotifyTemplate))
				continue;
			
			TreeNode catNode = mapCatNode.get(smsNotifyTemplate .getCategoryId());
			if (catNode != null) {
				TreeNode smsNotifyTemplateNode = new OcsTreeNode(smsNotifyTemplate , catNode);
				settingNewTreeNode(smsNotifyTemplate , smsNotifyTemplateNode);
			}
		}
	}
	

	public void buildTreeCssUssdResponse() {
		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<CssUssdResponse> lstCssUssdResponse = cssUssdResponseDAO.findByListCat(lstCatID);
		for (CssUssdResponse cssUssdResponse : lstCssUssdResponse) {
			
			if(!isFoundNode(cssUssdResponse))
				continue;
			
			TreeNode catNode = mapCatNode.get(cssUssdResponse.getCategoryId());
			if (catNode != null) {
				TreeNode cssUssdResponseNode = new OcsTreeNode(cssUssdResponse, catNode);
				settingNewTreeNode(cssUssdResponse, cssUssdResponseNode);
			}
		}
	}

	/**
	 * Nampv 22/08/2016
	 * 
	 */
	private void buildTreeReserveInfo() {

		// Build children categories
		buildTreeByCatType(treeType, root);

		// Add reserveInfo to Category children
		List<ReserveInfo> lstReserveInfos = reserveInfoDAO.findReserveInfoByConditions(lstCatID);
		for (ReserveInfo reserveinfo : lstReserveInfos) {
			
			if(!isFoundNode(reserveinfo))
				continue;
			
			TreeNode catNode = mapCatNode.get(reserveinfo.getCategoryId());
			if (catNode != null) {
				TreeNode reserveinfoNode = new OcsTreeNode(reserveinfo, catNode);
				settingNewTreeNode(reserveinfo, reserveinfoNode);
			}
		}
	}

	private void buildTreePolicyProfile() {
		// Build children categories
		buildTreeByCatType(treeType, root);
		// Add reserveInfo to Category children
		List<ProfilePep> lstProfilePeps = profilePepDAO.findProfilePepByCategoryId(lstCatID);
		for (ProfilePep profilePep : lstProfilePeps) {
			
			if(!isFoundNode(profilePep))
				continue;
			
			TreeNode catNode = mapCatNode.get(profilePep.getCategoryId());
			if (catNode != null) {
				TreeNode profilePepNode = new OcsTreeNode(profilePep, catNode);
				settingNewTreeNode(profilePep, profilePepNode);
				List<PccRule> lstPccRules = pccRuleDAO.findPccRuleByPoliciProfile(profilePep.getProfilePepId());
				for (PccRule pccRule : lstPccRules) {
					TreeNode pccRuleNode = new OcsTreeNode(pccRule, profilePepNode);
					settingNewTreeNode(pccRule, pccRuleNode);
				}
			}
		}
	}

	private void buildTreePccrule() {
		// Build children categories
		buildTreeByCatType(treeType, root);
		// Add reserveInfo to Category children
		List<PccRule> lstPccRules = pccRuleDAO.findPccRuleByCategoryId(lstCatID);
		for (PccRule pccRule : lstPccRules) {
			
			if(!isFoundNode(pccRule))
				continue;
			
			TreeNode catNode = mapCatNode.get(pccRule.getCategoryId());
			if (catNode != null) {
				TreeNode ruleNode = new OcsTreeNode(pccRule, catNode);
				settingNewTreeNode(pccRule, ruleNode);
			}
		}
	}

	private void buildTreeByCatType(String treeType, TreeNode root) {

		Map<Long, String> mapType = CategoryType.getCatTypesByTreeType(treeType);
		Iterator<Long> it = mapType.keySet().iterator();
		while (it.hasNext()) {

			Long catType = it.next();
			Category cat = new Category();
			cat.setTreeType(treeType);
			cat.setCategoryName(mapType.get(catType));
			cat.setCategoryType(catType);
			cat.setCategoryId(0);

			TreeNode node = new OcsTreeNode(cat, root);
			node.setExpanded(true);
			mapCatFirstNode.put(catType, node);
			buildTreeByCat(catType, node);
		}
	}

	private void buildTreeByCat(Long catType, TreeNode rootCatType) {
		List<Category> lstCat = new ArrayList<>();
		if (catType == CategoryType.SYS_SYS_CONFIG) {
			lstCat = catDao.findByTypeWithoutDomain(catType);
		} else {
			lstCat = catDao.findByType(catType);
		}
		List<TreeNode> lstNodeNew = new ArrayList<TreeNode>();
		List<TreeNode> lstNodeNotAdded = new ArrayList<TreeNode>();

		for (Category cat : lstCat) {

			if (cat.getCategoryParentId() == null || cat.getCategoryParentId() == 0) {

				TreeNode node = new OcsTreeNode(cat, rootCatType);
				lstNodeNew.add(node);
			} else {

				boolean isFound = false;
				for (TreeNode parentNode : lstNodeNew) {
					if (cat.getCategoryParentId() == ((Category) parentNode.getData()).getCategoryId()) {

						TreeNode node = new OcsTreeNode(cat, parentNode);
						lstNodeNew.add(node);
						isFound = true;
						break;
					}
				}

				if (!isFound) {

					TreeNode node = new OcsTreeNode(cat, null);
					lstNodeNotAdded.add(node);
					lstNodeNew.add(node);
				}
			}
		}

		for (TreeNode node : lstNodeNotAdded) {
			for (TreeNode nodeAdded : lstNodeNew) {

				if (((Category) node.getData()).getCategoryParentId() == ((Category) nodeAdded.getData())
						.getCategoryId()) {

					node.setParent(nodeAdded);
					nodeAdded.getChildren().add(node);
					break;
				}
			}
		}

		for (TreeNode node : lstNodeNew) {

			Category cat = (Category) node.getData();
//			node.setExpanded(true);
			node.setType(TreeNodeType.CATEGORY);
			lstCatID.add(cat.getCategoryId());
			mapCatNode.put(cat.getCategoryId(), node);
			getListNodeByObject(cat).add(node);
		}
	}

	private void removeTreeNode(TreeNode parentNode, BaseEntity objEntity) {

		List<TreeNode> lstChildren = parentNode.getChildren();
		if (lstChildren == null || lstChildren.isEmpty())
			return;

		for (TreeNode childNode : lstChildren) {

			BaseEntity data = (BaseEntity) childNode.getData();
			if (objEntity.getUniqueKey().equals(data.getUniqueKey())) {

				parentNode.getChildren().remove(childNode);
				childNode.setParent(null);
				return;
			}

			removeTreeNode(childNode, objEntity);
		}
	}

	public void removeTreeNodeAll(BaseEntity objEntity) {

		List<TreeNode> lstNode = getListNodeByObject(objEntity);
		Iterator<TreeNode> it = lstNode.iterator();
		while (it.hasNext()) {

			TreeNode node = it.next();
			TreeNode parentNode = node.getParent();
			parentNode.getChildren().remove(node);
			node.setParent(null);

			it.remove();
		}
	}

	/************ BUILD TREE - END *****************/

	/************ EVENT - BEGIN ********************/


	public void onSearchTree() {
		
		buildTree(treeType);
		if(!CommonUtil.isEmpty(txtTreeSearch)) {
			setExpandedRecursively(root, true, true);
			removeEmptyCatNode(root);	
		}		
	}
	
	private boolean isFoundNode(BaseEntity objEntity) {
		
		if(CommonUtil.isEmpty(txtTreeSearch))
			return true;
		
		if(objEntity.getNodeName() != null && objEntity.getNodeName().toLowerCase().contains(txtTreeSearch.toLowerCase()))
			return true;
		
		return false;
	}
	
	private boolean removeEmptyCatNode(TreeNode treeNode) {
		
		if(treeNode != root && !(treeNode.getData() instanceof Category))
			return false;
		
		List<TreeNode> lstChildrenNode = treeNode.getChildren();
		int countChildren = lstChildrenNode.size();
		int countChildrenRemoved = 0;
		
		Iterator<TreeNode> it = lstChildrenNode.iterator();
		while(it.hasNext()) {
			
			TreeNode childNode = it.next();
			if(removeEmptyCatNode(childNode)) {
				
//				childNode.setExpanded(false);
//				countChildrenRemoved++;
				
				Category childCat = (Category)childNode.getData();
				if(childCat.getCategoryId() > 0) {
					it.remove();
//					lstCatID.remove(childCat.getCategoryId());
//					mapCatNode.remove(childCat.getCategoryId());
					countChildrenRemoved++;	
				}				
			}
		}
		
		if(countChildren == countChildrenRemoved)
			return true;
		else
			return false;
	}
	
	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {

	}

	public void onNodeCollapse(NodeCollapseEvent nodeCollapseEvent) {

		TreeNode node = nodeCollapseEvent.getTreeNode();
		node.setExpanded(false);
	}

	private NodeSelectEvent nodeSelectEvent;

	public NodeSelectEvent getNodeSelectEvent() {
		return nodeSelectEvent;
	}

	public void setNodeSelectEvent(NodeSelectEvent nodeSelectEvent) {
		this.nodeSelectEvent = nodeSelectEvent;
	}

	public void onNodeSelectContext(NodeSelectEvent event) {
		nodeSelectEvent = event;
	}

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {

		TreeNode treeNode = nodeSlectedEvent.getTreeNode();
		currentEntity = (BaseEntity) treeNode.getData();
		if (currentEntity == null)
			return;

		if (currentEntity instanceof Category) {
			// Category
			setContentTitle(super.readProperties("title.category"));
			category = (Category) currentEntity;
			setSelectCategoryNode(category);
			selectedCatType = category.getCategoryType();
		} else {
			hideAllCategoryComponent();

			if (currentEntity instanceof Parameter) {

				setContentTitle(super.readProperties("parameter.title"));
				setParameterProperties(false, ((Parameter) currentEntity).getCategoryId(), ((Parameter) currentEntity), false);
			} else if (currentEntity instanceof ReserveInfo) {

				setContentTitle(super.readProperties("title.ReserveInfo"));
				setReserveInfoProperties(false, ((ReserveInfo) currentEntity).getCategoryId(), ((ReserveInfo) currentEntity), false);
			} else if (currentEntity instanceof TriggerOcs) {

				setContentTitle(super.readProperties("triggerOcs.title"));
				setTriggerOcsProperties(false, ((TriggerOcs) currentEntity).getCategoryId(),
						((TriggerOcs) currentEntity), false);
			} else if (currentEntity instanceof TriggerType) {

				setContentTitle(super.readProperties("triggerType.title"));
				setTriggerTypeProperties(false, ((TriggerType) currentEntity).getCategoryId(),
						((TriggerType) currentEntity), false);
			} else if (currentEntity instanceof TriggerDestination) {

				setContentTitle(super.readProperties("triggerDes.title"));
				setTriggerDesProperties(false, ((TriggerDestination) currentEntity).getCategoryId(),
						((TriggerDestination) currentEntity), false);
			} else if (currentEntity instanceof Zone) {

				setContentTitle(super.readProperties("zone.title"));
				setZoneProperties(false, ((Zone) currentEntity).getZoneMapId(), ((Zone) currentEntity));
			} else if (currentEntity instanceof GeoHomeZone) {

				setContentTitle(super.readProperties("geohome.title"));
				setGeoHomeProperties(false, ((GeoHomeZone) currentEntity).getCategoryId(),
						((GeoHomeZone) currentEntity), false);
			} else if (currentEntity instanceof ZoneMap) {

				setContentTitle(super.readProperties("zonemap.title"));
				setZoneMapProperties(false, ((ZoneMap) currentEntity).getCategoryId(), ((ZoneMap) currentEntity), false);
			} else if (currentEntity instanceof BillingCycleType) {

				setContentTitle(super.readProperties("billingcycleType.title"));
				setBillingcycleProperties(false, ((BillingCycleType) currentEntity).getCategoryId(),
						((BillingCycleType) currentEntity), false);
			} else if (currentEntity instanceof UnitType) {

				setContentTitle(super.readProperties("unittype.title"));
				setUnitTypeProperties(false, ((UnitType) currentEntity).getCategoryId(), ((UnitType) currentEntity), false);
			} else if (currentEntity instanceof StateType) {

				setContentTitle(super.readProperties("stateset.title"));
				setStateTypeProperties(false, ((StateType) currentEntity).getCategoryId(), ((StateType) currentEntity), false);
			} else if (currentEntity instanceof StateGroup) {

				setContentTitle(super.readProperties("stateset.stateGroup"));
				setStateGroupProperties(false, ((StateGroup) currentEntity).getCategoryId(),
						((StateGroup) currentEntity), false);
			} else if (currentEntity instanceof PccRule) {

				setContentTitle(super.readProperties("title.PR"));
				setPccRuleProperties(false, ((PccRule) currentEntity).getCategoryId(), ((PccRule) currentEntity), false, false);
			} else if (currentEntity instanceof ProfilePep) {

				setContentTitle(super.readProperties("title.PEPP"));
				setPolicyProfileProperties(false, ((ProfilePep) currentEntity).getCategoryId(),
						((ProfilePep) currentEntity), false);
			} else if (currentEntity instanceof BalType) {

				setContentTitle(super.readProperties("baltype.title"));
				setBalTypeProperties(false, ((BalType) currentEntity).getCategoryId(), ((BalType) currentEntity), false);
			} else if (currentEntity instanceof TriggerMsg) {

				setContentTitle(super.readProperties("triggerMsg.title"));
				setTriggerMsgProperties(false, ((TriggerMsg) currentEntity).getCategoryId(),
						((TriggerMsg) currentEntity), false);
			} else if (currentEntity instanceof Service) {

				setContentTitle(super.readProperties("service.title"));
				setServiceProperties(false, ((Service) currentEntity).getCategoryId(), ((Service) currentEntity), false);
			} else if (currentEntity instanceof MapSharebalBal) {

				setContentTitle(super.readProperties("title.MapShareBal"));
				setMapShareBalProperties(false, ((MapSharebalBal) currentEntity).getCategoryId(),
						((MapSharebalBal) currentEntity), false);
			} else if (currentEntity instanceof MapAcmbalBal) {

				setContentTitle(super.readProperties("title.MapAcmBal"));
				setMapAcmBalProperties(false, ((MapAcmbalBal) currentEntity).getCategoryId(),
						((MapAcmbalBal) currentEntity), false);
			} else if (currentEntity instanceof CdrService) {
				setContentTitle(super.readProperties("title.CdrService"));
				setCdrServiceProperties(false, ((CdrService) currentEntity).getCategoryId(),
						((CdrService) currentEntity), false);
			} else if (currentEntity instanceof CdrTemplate) {
				setContentTitle(super.readProperties("title.CdrTemplate"));
				setCdrTemplateProperties(false, ((CdrTemplate) currentEntity).getCategoryId(),
						((CdrTemplate) currentEntity), false);
			} else if (currentEntity instanceof CdrGenFilename) {
				setContentTitle(super.readProperties("title.CdrGenFileName"));
				setCdrGenFileNameProperties(false, ((CdrGenFilename) currentEntity).getCategoryId(),
						((CdrGenFilename) currentEntity), false);
			} else if (currentEntity instanceof CdrProcessConfig) {
				setContentTitle(super.readProperties("title.CdrProcessConfig"));
				setCdrProcessConfigProperties(false, ((CdrProcessConfig) currentEntity).getCategoryId(),
						((CdrProcessConfig) currentEntity), false);
			} else if (currentEntity instanceof SystemConfig) {
				setContentTitle(super.readProperties("title.SystemConfig"));
				setSystemConfigProperties(false, ((SystemConfig) currentEntity).getCategoryId(),
						((SystemConfig) currentEntity), false);
			} else if (currentEntity instanceof Threshold) {
				setPage("baltype");
				setContentTitle(super.readProperties("baltype.title"));
				Threshold threshold = (Threshold) nodeSlectedEvent.getTreeNode().getData();
				balTypeBean.refreshThresHold(threshold);
				mapShareBalBean.setFormType("");
				mapAcmBalBean.setFormType("");
			} else if (currentEntity instanceof StatisticItem) {
				setContentTitle(super.readProperties("statisticItem.title"));
				setStatisticItemProperties(false, ((StatisticItem) currentEntity).getCategoryId(),
						((StatisticItem) currentEntity), false);
			} else if (currentEntity instanceof SmsNotifyTemplate) {
				setContentTitle(super.readProperties("smsNotifyTemplate.title"));
				setSmsNotifyTemplateProperties(false, ((SmsNotifyTemplate) currentEntity).getCategoryId(),
						((SmsNotifyTemplate) currentEntity), false);
			} else if (currentEntity instanceof CssUssdResponse) {
				setContentTitle(super.readProperties("cssUssdResponse.title"));
				setCssUssdResponseProperties(false, ((CssUssdResponse) currentEntity).getCategoryId(),
						((CssUssdResponse) currentEntity), false);
			}
		}
	}

	// Nampv setSelectCategoryNode khi click node category
	private void setSelectCategoryNode(Category cat) {

		long catId = cat.getCategoryId();
		if (catId <= 0) {
			// Select node ngoai cung

			setPage("hiden");
			setFormType("cat-list");
			loadListCategory(cat.getCategoryType());
			setShowBtnCatNew(true);

		} else {
			// Select node con
			setFormType("cat-form");
			categoryParentId = cat.getCategoryParentId() == null ? 0 : cat.getCategoryParentId().longValue();
			setCategory(cat);
			searchCatSub(catId);
			setCategoryTitle("Sub-Categories of " + cat.getCategoryName());
			isEditing = true;
			searchCatParent(cat.getCategoryType(), cat.getCategoryId());

			switch (treeType) {
			case TreeType.CATALOG_PARAMETER:
				setParameterProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_RESERVE_INFO:
				setReserveInfoProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_TRIGGER_OCS:
				if (cat.getCategoryType() == CategoryType.CTL_TO_TRIGGER_OCS) {
					setTriggerOcsProperties(true, catId, null, false);
				}
				if (cat.getCategoryType() == CategoryType.CTL_TT_TRIGGER_TYPE) {
					setTriggerTypeProperties(true, catId, null, false);
				}
				break;

			case TreeType.CATALOG_TRIGGER_MESSAGE:
				setTriggerMsgProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_TRIGGER_DESTINATION:
				setTriggerDesProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_SERVICE:
				setServiceProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_CDR:
				// onSelectCDR(treeNode);
				break;

			case TreeType.CATALOG_ZONE_DATA:
				setZoneMapProperties(true, catId, null, false);
				// setZoneProperties(true, zoneMapId, null);
				break;

			case TreeType.CATALOG_GEO_HOME:
				setGeoHomeProperties(true, catId, null, false);

				break;

			case TreeType.CATALOG_BILLING_CYCLE:
				setBillingcycleProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_UNIT_TYPE:
				setUnitTypeProperties(true, catId, null, false);
				break;

			case TreeType.CATALOG_STATE_SET:
				if (cat.getCategoryType() == CategoryType.CTL_STATEGROUP) {
					setStateGroupProperties(true, catId, null, false);
				} else if (cat.getCategoryType() == CategoryType.CTL_STATETYPE) {
					setStateTypeProperties(true, catId, null, false);
				}
				break;
			case TreeType.POLICY_PCC_RULE:
				setPccRuleProperties(true, catId, null, false, false);
				break;
			case TreeType.POLICY_POLICY_PROFILE:
				setPolicyProfileProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_BALANCE:
				if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE) {
					setBalTypeProperties(true, catId, null, false);
				}
				if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACC) {
					setBalTypeProperties(true, catId, null, false);
				}
				if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACCOUNT_MAPPING) {
					setMapShareBalProperties(true, catId, null, false);
				}
				if (cat.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACM_MAPPING) {
					setMapAcmBalProperties(true, catId, null, false);
				}
				break;
			case TreeType.CATALOG_CDR_SERVICE:
				setCdrServiceProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_CDR_TEMPLATE:
				setCdrTemplateProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_CDR_GEN_FILENAME:
				setCdrGenFileNameProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_CDR_PROCESS_CONFIG:
				setCdrProcessConfigProperties(true, catId, null, false);
				break;
			case TreeType.SYS_SYS_CONFIG:
				setSystemConfigProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_STATISTIC_ITEM:
				setStatisticItemProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_SMS_NOTIFY_TEMPLATE:
				setSmsNotifyTemplateProperties(true, catId, null, false);
				break;
			case TreeType.CATALOG_CSS_USSD_RESPONSE:
				setCssUssdResponseProperties(true, catId, null, false);
				break;
			default:
				break;

			}
		}
	}

	// Nampv setPropertyDetail khi click node con cuoi cung
	public void hideAllCategoryComponent() {
		setFormType("hide-all-cat");
	}

	@ManagedProperty("#{policyProfileBean}")
	private PolicyProfileBean policyProfileBean;

	public void setPolicyProfileBean(PolicyProfileBean policyProfileBean) {
		this.policyProfileBean = policyProfileBean;
	}

	@ManagedProperty("#{pccRuleBean}")
	private PCCRuleBean pccRuleBean;

	public void setPccRuleBean(PCCRuleBean pccRuleBean) {
		this.pccRuleBean = pccRuleBean;
	}

	@ManagedProperty("#{reserveBean}")
	private ReserveInfoBean reserveInfoBean;

	public void setReserveInfoBean(ReserveInfoBean reserveInfoBean) {
		this.reserveInfoBean = reserveInfoBean;
	}

	/**
	 * Nampv setPropertyOfReserveInfo
	 */
	private void setReserveInfoProperties(boolean selectCat, Long categoryId, ReserveInfo reserveInfo, boolean createNew) {

		if(createNew) {
			reserveInfoBean.setCategoryID(categoryId);
			reserveInfoBean.commandAddNew();
		} else if (selectCat) {
			reserveInfoBean.setFormType("list-reserveinfo-by-category");
			reserveInfoBean.loadReserveInfoByCategory(categoryId);
			reserveInfoBean.setEditting(true);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.rVClearFilter').click();");
		} else {
			hideAllCategoryComponent();
			reserveInfoBean.setFormType("detail-reserveinfo");
			reserveInfoBean.setReserveInfoUI(reserveInfo);
			reserveInfoBean.setEditting(true);
			reserveInfoBean.setApply(true);
			reserveInfoBean.loadCategoriesOfRI();
			hideAllCategoryComponent();
		}
	}

	public void setUnitTypeProperties(boolean selectCat, Long categoryId, UnitType unitType, boolean createNew) {
		if(createNew) {
			unitBean.setCategory(category);
			unitBean.commandAddNewUnitType();
		} else if (selectCat) {
			unitBean.refreshCategories(category);
		} else {
			unitBean.refreshUnitType(unitType);
			hideAllCategoryComponent();
		}
	}

	public void setBalTypeProperties(boolean selectCat, Long categoryId, BalType balType, boolean createNew) {
		setPage("baltype");
		if(createNew) {
			balTypeBean.setCategory(category);
			balTypeBean.commandAddNewBalType();
		} else if (selectCat) {
			balTypeBean.refreshCategories(category);
		} else {
			balTypeBean.refreshBalType(balType);
			hideAllCategoryComponent();
		}
		mapShareBalBean.setFormType("");
		mapAcmBalBean.setFormType("");
	}

	private void setMapShareBalProperties(boolean selectCat, Long categoryId, MapSharebalBal mapShareBal, boolean createNew) {
		setPage("baltype");
		if(createNew) {
			mapShareBalBean.setCategory(category);
			mapShareBalBean.commandAddNewMapShareBal();
		} else if (selectCat) {
			mapShareBalBean.refreshCategoriesofMapShare(category);
		} else {
			mapShareBalBean.refreshMapShareBal(mapShareBal);
			hideAllCategoryComponent();
		}
		balTypeBean.setFormType("");
		mapAcmBalBean.setFormType("");
	}

	public void setCdrServiceProperties(boolean selectCat, Long categoryId, CdrService cdrService, boolean createNew) {
		if(createNew) {
			cdrServiceBean.setCategoryId(categoryId);
			cdrServiceBean.btnAddNew();
		} else if (selectCat) {
			cdrServiceBean.setConditionFormType("category");
			cdrServiceBean.getListCdrServiceByCategoryId(categoryId, 1);
			cdrServiceBean.setCategoryId(categoryId);
			super.resetDataTable("form-category:ID_RoleTable");
		} else {
			hideAllCategoryComponent();
			setContentTitle(super.readProperties("title.CdrService"));
			cdrServiceBean.getLoadComboListCategory();
			cdrServiceBean.setConditionFormType("detail");
			cdrServiceBean.setDataByTreeNode(cdrService);
			cdrServiceBean.setCategoryId(cdrService.getCategoryId());
		}
	}

	public void setCdrTemplateProperties(boolean selectCat, Long categoryId, CdrTemplate cdrTemplate, boolean createNew) {
		if(createNew) {
			cdrTemplateBean.setCategoryId(categoryId);
			cdrTemplateBean.addNewTemplate();
		} else if (selectCat) {
			cdrTemplateBean.setFormType("category");
			cdrTemplateBean.getListCdrTemplateByCategoryId(categoryId);
			cdrTemplateBean.setCategoryId(categoryId);
			super.resetDataTable("form-template-category:tableCdrTemplate");
		} else {
			hideAllCategoryComponent();
			setContentTitle(super.readProperties("title.CdrTemplate"));
			cdrTemplateBean.setFormType("detail");
			cdrTemplateBean.setDataByTreeNode(cdrTemplate);
			cdrTemplateBean.setCategoryId(cdrTemplate.getCategoryId());
			cdrTemplateBean.setEditting(false);
			cdrTemplateBean.getLoadComboListCategory();
		}
	}

	public void setCdrGenFileNameProperties(boolean selectCat, Long categoryId, CdrGenFilename cdrGenFilename, boolean createNew) {
		if(createNew) {
			cdrGenFileNameBean.setCategoryId(categoryId);
			cdrGenFileNameBean.addNewGenFileName();
		} else if (selectCat) {
			cdrGenFileNameBean.setFormType("category");
			cdrGenFileNameBean.getListCdrGenFileNameByCategoryId(categoryId, 1);
			cdrGenFileNameBean.setCategoryId(categoryId);
			super.resetDataTable("form-template-category:tableCdrGenFileName");
		} else {
			hideAllCategoryComponent();
			setContentTitle(super.readProperties("title.CdrGenFileName"));
			cdrGenFileNameBean.setFormType("detail");
			cdrGenFileNameBean.setDataByTreeNode(cdrGenFilename);
			cdrGenFileNameBean.setCategoryId(cdrGenFilename.getCategoryId());
			cdrGenFileNameBean.setEditting(false);
		}
	}

	public void setCdrProcessConfigProperties(boolean selectCat, Long categoryId, CdrProcessConfig cdrProcessConfig, boolean createNew) {
		if(createNew) {
			cdrProcessConfigBean.setCategoryId(categoryId);
			cdrProcessConfigBean.addNewProcessConfig();
		} else if (selectCat) {
			cdrProcessConfigBean.setFormType("category");
			cdrProcessConfigBean.getListCdrProcessConfigByCategoryId(categoryId, 1);
			cdrProcessConfigBean.setCategoryId(categoryId);
			super.resetDataTable("form-ProcessConfig-category:tableCdrProcessConfig");
		} else {
			hideAllCategoryComponent();
			setContentTitle(super.readProperties("title.CdrProcessConfig"));
			cdrProcessConfigBean.setFormType("detail");
			cdrProcessConfigBean.setDataByTreeNode(cdrProcessConfig);
			cdrProcessConfigBean.setCategoryId(cdrProcessConfig.getCategoryId());
			cdrProcessConfigBean.setEditting(false);
		}
	}

	private void setMapAcmBalProperties(boolean selectCat, Long categoryId, MapAcmbalBal mapAcmBal, boolean createNew) {
		setPage("baltype");
		if(createNew) {
			mapAcmBalBean.setCategory(category);
			mapAcmBalBean.commandAddNewMapAcmBal();
		} else if (selectCat) {
			mapAcmBalBean.refreshCategoriesofMapAcm(category);
		} else {
			mapAcmBalBean.refreshMapAcmBal(mapAcmBal);
			hideAllCategoryComponent();
		}
		mapShareBalBean.setFormType("");
		balTypeBean.setFormType("");
	}

	private void setZoneMapProperties(boolean selectCat, Long categoryId, ZoneMap zoneMap, boolean createNew) {
		if(createNew) {
			zoneMapBean.setCategory(category);;
			zoneMapBean.commandAddNewZoneMap();
		} else if (selectCat) {
			zoneMapBean.refreshCategories(category);
		} else {
			zoneMapBean.refreshZoneMap(zoneMap);
			hideAllCategoryComponent();
		}
	}

	private void setZoneProperties(boolean selectCat, Long zoneMapId, Zone zone) {
		if (!selectCat) {
			zoneMapBean.refreshZone(zone);
			zoneMapBean.setApply(true);
			hideAllCategoryComponent();
		}
	}

	private void setTriggerDesProperties(boolean selectCat, Long categoryId, TriggerDestination triggerDestination, boolean createNew) {
		if(createNew) {
			triggerDesBean.setCategoryID(categoryId);
			triggerDesBean.commandAddNewTriggerDes();
		} else if (selectCat) {
			triggerDesBean.setFormType("list-triggerdes-by-category");
			triggerDesBean.loadTriggerDesByCategory(categoryId);
		} else {
			triggerDesBean.refreshTriggerDes(triggerDestination);
			triggerDesBean.setEditting(true);
			triggerDesBean.setApply(true);
			triggerDesBean.loadCategoriesOfTD();
			hideAllCategoryComponent();
		}
	}

	private void setGeoHomeProperties(boolean selectCat, Long categoryId, GeoHomeZone geoHomeZone, boolean createNew) {
		if(createNew) {
			geoBean.setCategory(category);
			geoBean.commandAddNewGeoHome();
		} else if (selectCat) {
			geoBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("geohome.title"));
			geoBean.refreshGeoHome(geoHomeZone);
			hideAllCategoryComponent();
		}
	}

	public void setParameterProperties(boolean selectCat, Long categoryId, Parameter parameter, boolean createNew) {
		if(createNew) {			
			parameterBean.setCategoryID(categoryId);
			parameterBean.commandAddNew();
		} else if (selectCat) {
			parameterBean.setFormType("list-parameter-by-category");
			parameterBean.loadParameterByCategory(categoryId);
			parameterBean.setEditting(true);
		} else {
			parameterBean.setFormType("detail-parameter");
			parameterBean.setParameterUI(parameter);
			parameterBean.setEditting(true);
			parameterBean.setApply(true);
			parameterBean.loadCategoriesOfPara();
			hideAllCategoryComponent();
		}
	}

	private void setStateTypeProperties(boolean selectCat, Long categoryId, StateType stateType, boolean createNew) {
		if(createNew) {
			stateSetBean.setCategoryID(categoryId);
			stateSetBean.commandAddNew();
		} else if (selectCat) {
			stateSetBean.setFormType("list-statetype-by-category");
			stateSetBean.loadStateTypeByCategory(category.getCategoryId());
			stateSetBean.setEditting(true);
			stateSetBean.setEdittingStateGroup(true);
		} else {
			stateSetBean.setFormType("detail-statetype");
			stateSetBean.setStateTypeUI(stateType);
			stateSetBean.setEditting(true);
			stateSetBean.setApply(true);
			stateSetBean.setEdittingStateGroup(true);
			hideAllCategoryComponent();
		}
	}

	private void setStateGroupProperties(boolean selectCat, Long categoryId, StateGroup stateGroup, boolean createNew) {
		if(createNew) {
			stateSetBean.setCategoryID(categoryId);
			stateSetBean.commandAddNewStateGroup();
		} else if (selectCat) {
			stateSetBean.setFormType("list-stategroup-by-category");
			stateSetBean.loadStateGroupByCategory(category.getCategoryId());
			stateSetBean.setEditting(true);
			stateSetBean.setEdittingStateGroup(true);
//			RequestContext requestContext = RequestContext.getCurrentInstance();
//			requestContext.execute("$('.stateGroupTableClearFilter').click();");
		} else {
			stateSetBean.setFormType("detail-stategroup");
			stateSetBean.setStateGroupUI(stateGroup);
			stateSetBean.setEditting(true);
			stateSetBean.setApply(true);
			stateSetBean.setEdittingStateGroup(true);
			hideAllCategoryComponent();
		}
	}

	private void setTriggerOcsProperties(boolean selectCat, Long categoryId, TriggerOcs triggerOcs, boolean createNew) {
		setPage("trigger_ocs");
		if(createNew) {
			triggerOcsBean.setCategoryID(categoryId);;
			triggerOcsBean.commandAddNewTriggerOcs();
		} else if (selectCat) {
			triggerOcsBean.setFormType("list-trigger-ocs");
			triggerOcsBean.loadTriggerOcsByCategory(categoryId);
			triggerOcsBean.setEditting(true);
			triggerOcsBean.setEdittingTriggerType(true);
		} else {
			triggerOcsBean.setFormType("detail-trigger-ocs");
			triggerOcsBean.setTriggerOcsUI(triggerOcs);
			triggerOcsBean.setEditting(true);
			triggerOcsBean.setApply(true);
			triggerOcsBean.setEdittingTriggerType(true);
			triggerOcsBean.loadCategoriesOfTO();
			hideAllCategoryComponent();
		}
	}

	private void setTriggerTypeProperties(boolean selectCat, Long categoryId, TriggerType triggerType, boolean createNew) {
		setPage("trigger_ocs");
		if(createNew) {
			triggerOcsBean.setCategoryID(categoryId);;
			triggerOcsBean.commandAddNewTriggerType();
		} else if (selectCat) {
			triggerOcsBean.setFormType("list-trigger-type");
			triggerOcsBean.loadTriggerTypeByCategory(categoryId);
			triggerOcsBean.setEditting(true);
			triggerOcsBean.setEdittingTriggerType(true);
		} else {
			triggerOcsBean.setFormType("detail-trigger-type");
			triggerOcsBean.setTriggerTypeUI(triggerType);
			triggerOcsBean.setEditting(true);
			triggerOcsBean.setApply(true);
			triggerOcsBean.setEdittingTriggerType(true);
			triggerOcsBean.loadCategoriesOfTT();
			hideAllCategoryComponent();
		}
	}

	private void setTriggerMsgProperties(boolean selectCat, Long categoryId, TriggerMsg triggerMsg, boolean createNew) {
		setPage("trigger_msg");
		if(createNew) {
			triggerMsgBean.setCategoryID(categoryId);
			triggerMsgBean.commandAddNewTriggerMsg();
		} else if (selectCat) {
			triggerMsgBean.setFormType("list-trigger-msg");
			triggerMsgBean.loadTriggerMsgByCategory(category.getCategoryId());
			triggerMsgBean.setEditting(true);
		} else {
			triggerMsgBean.setFormType("detail-trigger-msg");
			triggerMsgBean.setTriggerMsgUI(triggerMsg);
			triggerMsgBean.setEditting(true);
			triggerMsgBean.setApply(true);
			triggerMsgBean.loadCategoriesOfTM();
			hideAllCategoryComponent();
		}
	}

	private void setServiceProperties(boolean selectCat, Long categoryId, Service service, boolean createNew) {
		if(createNew) {
			serviceBean.setCategoryID(categoryId);
			serviceBean.commandAddNew();
		} else if (selectCat) {
			serviceBean.setFormType("list-service");
			serviceBean.loadServiceByCategory(category.getCategoryId());
			serviceBean.setEditting(true);
		} else {
			serviceBean.setFormType("detail-service");
			serviceBean.setServiceUI(service);
			serviceBean.setEditting(true);
			serviceBean.setApply(true);
			serviceBean.loadCategoriesOfService();
			hideAllCategoryComponent();
		}
	}

	private void setBillingcycleProperties(boolean selectCat, Long categoryId, BillingCycleType billingCycleType, boolean createNew) {
		if(createNew) {
			billingCycleTypeBean.setCategoryID(categoryId);
			billingCycleTypeBean.commandAddNew();
		} else if (selectCat) {
			billingCycleTypeBean.setFormType("list-billing-cycle-type-by-category");
			billingCycleTypeBean.loadBillingCycleTypeByCategory(categoryId);
			billingCycleTypeBean.setEditting(true);
			// RequestContext requestContext =
			// RequestContext.getCurrentInstance();
			// requestContext.execute("$('.billingCycleCateTableClearFilter').click();");
		} else {
			billingCycleTypeBean.setFormType("detail-billing-cycle-type");
			billingCycleTypeBean.setBillingCycleTypeUI(billingCycleType);
			billingCycleTypeBean.setEditting(true);
			billingCycleTypeBean.setApply(true);
			billingCycleTypeBean.loadListBillingCycleDB(billingCycleType.getBillingCycleTypeId());
//			billingCycleTypeBean.loadCalcUnit(); 
			billingCycleTypeBean.loadCalcUnitDB();
			billingCycleTypeBean.resetDataTable();
			hideAllCategoryComponent();
		}
	}

	public void setPolicyProfileProperties(boolean selectCat, Long categoryId, ProfilePep profilePep, boolean createNew) {
		setPage("policy_profile");
		if(createNew) {
			policyProfileBean.setCategoryID(categoryId);
			policyProfileBean.btnNew();
		} else if (selectCat) {
			policyProfileBean.setFormType("category");
			policyProfileBean.getListPolicyProfileBeanByCategoryId(categoryId);
			policyProfileBean.setPolicyProfileTitle(super.readProperties("title.ProfilePep"));
			policyProfileBean.updateUI();
			policyProfileBean.setCategoryID(categoryId);
		} else {
			hideAllCategoryComponent();
			policyProfileBean.setFormType("detail");
			policyProfileBean.setDataByTreeNode(profilePep);
			policyProfileBean.setEditting(false);
			policyProfileBean.findListPccRule(profilePep.getProfilePepId());
			policyProfileBean.loadCategory();
		}
	}

	public void setPccRuleProperties(boolean selectCat, Long categoryId, PccRule pccRule, boolean parentEdit, boolean createNew) {
		setPage("pcc_rule");
		if(createNew) {
			pccRuleBean.setCategoryID(categoryId);
			pccRuleBean.btnAddNewPccRule();
		} else if (selectCat) {
			pccRuleBean.setFormType("category");
			pccRuleBean.getListPccRuleByCategoryId(categoryId);
			pccRuleBean.setPccRuleTitle(super.readProperties("title.PccRule"));
			pccRuleBean.updateUI();
			pccRuleBean.setCategoryID(categoryId);
		} else {
			hideAllCategoryComponent();
			pccRuleBean.setFormType("detail");
			pccRuleBean.setDataByTreeNode(pccRule);
			pccRuleBean.setEditting(false);
			if(parentEdit) {
				pccRuleBean.setEditting(true);
			}
			pccRuleBean.loadCategory();			
		}
	}
	

	private void setSystemConfigProperties(boolean selectCat, Long categoryId, SystemConfig systemConfig, boolean createNew) {

		if(createNew) {
			systemConfigBean.setCategoryID(categoryId);
			systemConfigBean.btnNew();
		} else {
			systemConfigBean.refreshSysConfig(categoryId, systemConfig);	
		}
	}
	
	private void setStatisticItemProperties(boolean selectCat, Long categoryId, StatisticItem statisticItem, boolean createNew) {
		if(createNew) {
			statisticItemBean.setCategory(category);
			statisticItemBean.addNewStatistic();
		} else if (selectCat) {
			statisticItemBean.refreshCategories(category);
		} else {
			statisticItemBean.refreshStatisticItem(statisticItem);
			hideAllCategoryComponent();
		}
	}
	
	private void setSmsNotifyTemplateProperties(boolean selectCat, Long categoryId, SmsNotifyTemplate smsNotifyTemplate, boolean createNew) {
		if(createNew) {
			smsNotiTemBean.setCategory(category);
			smsNotiTemBean.addNewSMS();
		} else if (selectCat) {
			smsNotiTemBean.refreshCategories(category);
		} else {
			smsNotiTemBean.refreshSMS(smsNotifyTemplate);
			hideAllCategoryComponent();
		}
	}
	
	private void setCssUssdResponseProperties(boolean selectCat, Long categoryId, CssUssdResponse cssUssdResponse, boolean createNew) {
		if (createNew) {
			cssUssdResponseBean.setCategory(category);
			cssUssdResponseBean.addNewCSS();
		} else if (selectCat) {
			cssUssdResponseBean.refreshCategories(category);
		} else {
			cssUssdResponseBean.refreshCSS(cssUssdResponse);
			hideAllCategoryComponent();
		}
	}

	public void collapseAll() {

		setExpandedRecursively(root, false, false);
	}

	public void expandAll() {

		setExpandedRecursively(root, true, false);
	}

	private void setExpandedRecursively(final TreeNode node, final boolean expanded, boolean catOnly) {

		if (node.getChildren() == null)
			return;
		
		if(catOnly) {
			
			if(node == root || node.getData() instanceof Category) {

				for (final TreeNode child : node.getChildren()) {
					setExpandedRecursively(child, expanded, catOnly);
				}
				
				if(expanded) {
					node.setExpanded(expanded);	
				} else {
					// Chi collapse toi cat co Id > 0
					if(node.getData() instanceof Category && ((Category)node.getData()).getCategoryId() > 0) {
						node.setExpanded(expanded);
					}		
				}
			}			
		} else {

			for (final TreeNode child : node.getChildren()) {
				setExpandedRecursively(child, expanded, catOnly);
			}
			node.setExpanded(expanded);			
		}
	}

	public boolean removeTreeNode(BaseEntity objEntity, BaseEntity parentEntity) {
		TreeNode currentNode = this.getTreeNodeByObject(objEntity, parentEntity);
		if (currentNode == null)
			return false;

		TreeNode parentNode = currentNode.getParent();
		if (parentNode != null) {

			parentNode.getChildren().remove(currentNode);
			currentNode.setParent(null);
			getListNodeByObject(objEntity).remove(currentNode);
			return true;
		}

		return false;
	}

	private TreeNode getTreeNodeByObject(BaseEntity obj, BaseEntity objParent) {
		if (obj == null || objParent == null)
			return null;

		List<TreeNode> lstObjNode = mapListNode.get(obj.getUniqueKey());
		List<TreeNode> lstParentNode = mapListNode.get(objParent.getUniqueKey());
		if (lstObjNode == null || lstParentNode == null)
			return null;

		for (TreeNode parentNode : lstParentNode) {

			List<TreeNode> lstChildOfParent = parentNode.getChildren();
			for (TreeNode node : lstChildOfParent) {
				for (TreeNode objNode : lstObjNode) {

					if (objNode == node)
						return objNode;
				}
			}
		}

		return null;
	}

	private List<TreeNode> getListNodeByObject(BaseEntity objEntity) {

		List<TreeNode> lst = mapListNode.get(objEntity.getUniqueKey());
		if (lst == null) {
			lst = new ArrayList<>();
			mapListNode.put(objEntity.getUniqueKey(), lst);
		}
		return lst;
	}

	public void updateTreeNode(BaseEntity obj, Category objCategory, List lstChildObject) {
		this.setCatNodeParent(obj, objCategory);
		this.updateTreeNode(obj, lstChildObject);
	}

	private void setCatNodeParent(BaseEntity obj, Category objCategory) {

		if (objCategory == null)
			return;

		TreeNode catNode = mapCatNode.get(objCategory.getCategoryId());
		if (catNode == null)
			return;

		TreeNode nodeCurrent = null;
		List<TreeNode> lstNode = this.getListNodeByObject(obj);
		for (TreeNode node : lstNode) {
			if (node.getParent().getData() instanceof Category) {

				nodeCurrent = node;
				break;
			}
		}

		if (nodeCurrent != null) {

			BaseEntity nodeObj = (BaseEntity) nodeCurrent.getData();
			if (nodeObj != obj)
				HibernateUtil.copyEntityProperties(BaseEntity.class, obj, nodeObj, true);

			TreeNode oldParentNode = nodeCurrent.getParent();
			if (oldParentNode != catNode) {

				oldParentNode.getChildren().remove(nodeCurrent);
				nodeCurrent.setParent(catNode);
				if (catNode != null)
					catNode.getChildren().add(nodeCurrent);
			}
		} else {

			TreeNode newNode = new OcsTreeNode(obj, catNode);
			settingNewTreeNode(obj, newNode);
		}
	}

	private void updateTreeNode(BaseEntity obj, List lstObjChildren) {

		List<TreeNode> lstTreeNode = this.getListNodeByObject(obj);
		for (TreeNode nodeParent : lstTreeNode) {

			BaseEntity objNode = (BaseEntity) nodeParent.getData();
			HibernateUtil.copyEntityProperties(obj, objNode);

			if (lstObjChildren == null || lstObjChildren.size() <= 0)
				continue;

			// Tim nhung childnode ko thuoc trong list con
			List<TreeNode> lstCurrentChildren = nodeParent.getChildren();
			List<TreeNode> lstChildren2Remove = new ArrayList<TreeNode>();
			for (TreeNode childNode : lstCurrentChildren) {

				boolean found = false;
				BaseEntity childNodeEntity = (BaseEntity) childNode.getData();
				for (int i = 0; i < lstObjChildren.size(); i++) {

					BaseEntity objChild = (BaseEntity) lstObjChildren.get(i);
					if (objChild.getUniqueKey().equals(childNodeEntity.getUniqueKey())) {

						HibernateUtil.copyEntityProperties(objChild, childNodeEntity);
						found = true;
						break;
					}
				}

				if (!found) {
					lstChildren2Remove.add(childNode);
				}
			}

			// Remove nhung child ko thuoc list con
			for (TreeNode childRemove : lstChildren2Remove) {

				lstCurrentChildren.remove(childRemove);
				childRemove.setParent(null);
				getListNodeByObject((BaseEntity) childRemove.getData()).remove(childRemove);
			}

			// Add cac con moi vao node
			for (int i = 0; i < lstObjChildren.size(); i++) {

				BaseEntity objChild = (BaseEntity) lstObjChildren.get(i);
				if (getTreeNodeByObject(objChild, obj) == null) {

					TreeNode newChildNode = new OcsTreeNode(objChild, nodeParent);
					settingNewTreeNode(objChild, newChildNode);
				}
			}
		}
	}

	private void settingNewTreeNode(BaseEntity objEntity, TreeNode node) {
		getListNodeByObject(objEntity).add(node);
		setTypeAndDumpNode(objEntity, node);
	}

	private void setTypeAndDumpNode(BaseEntity objEntity, TreeNode node) {
		if (objEntity instanceof Service) {
			node.setType(TreeNodeType.CAT_SERVICE);
		} else if (objEntity instanceof Parameter) {
			node.setType(TreeNodeType.CAT_PARAMETER);
		} else if (objEntity instanceof BillingCycleType) {
			node.setType(TreeNodeType.CAT_BILLING_CYCLE);
		} else if (objEntity instanceof UnitType) {
			node.setType(TreeNodeType.CAT_UNIT_TYPE);
		} else if (objEntity instanceof BalType) {
			node.setType(TreeNodeType.CAT_BAL_TYPE);
		} else if (objEntity instanceof StateGroup) {
			node.setType(TreeNodeType.CAT_STATE_GROUP);
		} else if (objEntity instanceof StateType) {
			node.setType(TreeNodeType.CAT_STATE_TYPE);
		} else if (objEntity instanceof ReserveInfo) {
			node.setType(TreeNodeType.CAT_RESERVE_INFO);
		} else if (objEntity instanceof TriggerOcs) {
			node.setType(TreeNodeType.CAT_TRIGGER_OCS);
		} else if (objEntity instanceof TriggerDestination) {
			node.setType(TreeNodeType.CAT_TRIGGER_DESTINATION);
		} else if (objEntity instanceof TriggerMsg) {
			node.setType(TreeNodeType.CAT_TRIGGER_MSG);
		} else if (objEntity instanceof GeoHomeZone) {
			node.setType(TreeNodeType.CAT_GEO_HOME_ZONE);
		} else if (objEntity instanceof TriggerType) {
			node.setType(TreeNodeType.CAT_TRIGGER_TYPE);
		} else if (objEntity instanceof ZoneMap) {
			node.setType(TreeNodeType.CAT_ZONE_MAP);
		} else if (objEntity instanceof MapSharebalBal) {
			node.setType(TreeNodeType.CAT_MAP_SHARE_BAL);
		} else if (objEntity instanceof MapAcmbalBal) {
			node.setType(TreeNodeType.CAT_MAP_ACM_BAL);
		} else if (objEntity instanceof CdrService) {
			node.setType(TreeNodeType.CAT_CDR_SERVICE);
		} else if (objEntity instanceof CdrTemplate) {
			node.setType(TreeNodeType.CAT_CDR_TEMP);
		} else if (objEntity instanceof CdrGenFilename) {
			node.setType(TreeNodeType.CAT_CDR_GEN_FILENAME);
		} else if (objEntity instanceof CdrProcessConfig) {
			node.setType(TreeNodeType.CAT_CDR_PROCESS_CFG);
		} else if (objEntity instanceof ProfilePep) {
			node.setType(TreeNodeType.POLICY_PEP_PROFILE);
		} else if (objEntity instanceof PccRule) {
			node.setType(TreeNodeType.POLICY_PCC_RULE);
		} else if (objEntity instanceof SystemConfig) {
			node.setType(TreeNodeType.SETTING_SYS_COMFIG);
		} else if (objEntity instanceof StatisticItem) {
			node.setType(TreeNodeType.CAT_STATISTIC_ITEM);
		} else if (objEntity instanceof SmsNotifyTemplate) {
			node.setType(TreeNodeType.CAT_SMS_NOTIFY_TEMPLATE);
		} else if (objEntity instanceof CssUssdResponse) {
			node.setType(TreeNodeType.CAT_CSS_USSD_RESPONSE);
		}
	}

	public void addChildCatContext(NodeSelectEvent nodeSelectEvent) {
		
		this.onNodeSelect(nodeSelectEvent);
		btnCatShowDlg(null);
	}
	
	public void addChildObjectContext(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent.getTreeNode().getData() instanceof Category) {

			category = (Category) nodeSelectEvent.getTreeNode().getData();
			if (category.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE || category.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACC) {
				
				setBalTypeProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACCOUNT_MAPPING) {
				
				setMapShareBalProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.CTL_BL_BAL_TYPE_ACM_MAPPING) {
				
				setMapAcmBalProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_PARAMETER) {
				
				setParameterProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_UT_RESERVE_INFO) {
				
				setReserveInfoProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_TO_TRIGGER_OCS) {
				
				setTriggerOcsProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_TT_TRIGGER_TYPE) {
				
				setTriggerTypeProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_TD_TRIGGER_DESTINATION) {
				
				setTriggerDesProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_TM_TRIGGER_MESSAGE) {
				
				setTriggerMsgProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_SERVICE) {
				
				setServiceProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_CDR_SERVICE) {
				
				setCdrServiceProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_CDR_TEMPLATE) {
				
				setCdrTemplateProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_CDR_GEN_FILENAME) {
				
				setCdrGenFileNameProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_CDR_PROCESS_CONFIG) {
				
				setCdrProcessConfigProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_ZD_ZONE_DATA) {
				
				setZoneMapProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_GHZ_GEO_HOME_ZONE) {
				
				setGeoHomeProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_BILLING_CYCLE) {
				
				setBillingcycleProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_UT_UNIT_TYPE) {
				
				setUnitTypeProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_STATETYPE) {
				
				setStateTypeProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_STATEGROUP) {
				
				setStateGroupProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.PLC_PPP_POLICY_PROFILE) {
				
				setPolicyProfileProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.PLC_PR_PCC_RULE) {
				
				setPccRuleProperties(false, category.getCategoryId(), null, false, true);
			} else if(category.getCategoryType() == CategoryType.SYS_SYS_CONFIG) {
				
				setSystemConfigProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_STATISTIC_ITEM) {
				
				setStatisticItemProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_SMS_NOTIFY_TEMPLATE) {
				
				setSmsNotifyTemplateProperties(false, category.getCategoryId(), null, true);
			} else if(category.getCategoryType() == CategoryType.CTL_CSS_USSD_RESPONSE) {
				
				setCssUssdResponseProperties(false, category.getCategoryId(), null, true);
			}
		}	
	}

	public void removeCatContext(NodeSelectEvent nodeSelectEvent) {
		
		Category cat = (Category) nodeSelectEvent.getTreeNode().getData();
		deleteCategory(cat);
	}
	
	public void moveUpCategory() {

		if (selectedNode != null) {

			List<TreeNode> lstChildOfParent = selectedNode.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(selectedNode);
			if (idx > 0) {

				Category cat = (Category) selectedNode.getData();
				if (catDao.moveUpDown(true, cat)) {

					lstChildOfParent.remove(idx);
					lstChildOfParent.add(idx - 1, selectedNode);
				}
			}
		}
	}

	public void moveDownCategory() {

		if (selectedNode != null) {

			List<TreeNode> lstChildOfParent = selectedNode.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(selectedNode);
			if (idx < lstChildOfParent.size() - 1) {

				Category cat = (Category) selectedNode.getData();
				if (catDao.moveUpDown(false, cat)) {

					lstChildOfParent.remove(idx);
					lstChildOfParent.add(idx + 1, selectedNode);
				}
			}
		}
	}

	public void moveUpTreeNode(BaseEntity objEntity) {

		List<TreeNode> lstNode = getListNodeByObject(objEntity);
		for (TreeNode node : lstNode) {

			List<TreeNode> lstChildOfParent = node.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(node);
			if (idx > 0) {

				lstChildOfParent.remove(idx);
				lstChildOfParent.add(idx - 1, node);
			}
		}
	}

	public void moveDownTreeNode(BaseEntity objEntity) {

		List<TreeNode> lstNode = getListNodeByObject(objEntity);
		for (TreeNode node : lstNode) {

			List<TreeNode> lstChildOfParent = node.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(node);
			if (idx < lstChildOfParent.size() - 1) {

				lstChildOfParent.remove(idx);
				lstChildOfParent.add(idx + 1, node);
			}
		}
	}

	/************ EVENT - END *****************/

	/************ CATEGORY - BEGIN ************/

	private Category category;
	private Long categoryParentId;
	private Long categoryFirst;
	private List<Category> listCatParent;
	private List<Category> listAllCategory;
	private List<Category> listCatSub;
	private boolean isEditing;
	private String categoryTitle;
	private CategoryDAO catDao;
	private Category catParentDump;
	private Long selectedCatType;
	private List<SelectItem> listCatType;

	public void defaulData() {
		catParentDump = new Category();
		catParentDump.setCategoryName("");
		listCatParent = new ArrayList<Category>();
		listCatSub = new ArrayList<Category>();
		listAllCategory = new ArrayList<Category>();
		catDao = new CategoryDAO();
	}

	private int itemParentCategory;

	public int getItemParentCategory() {
		return itemParentCategory;
	}

	public void setItemParentCategory(int itemParentCategory) {
		this.itemParentCategory = itemParentCategory;
	}

	// lay danh sach category con thuoc category cha
	private void searchCatSub(Long categoryId) {
		listCatSub = catDao.loadListCategoryByParentId(categoryId);
	}

	// lay sanh sach category khong phai la chinh no de lam cha
	private void searchCatParent(Long catType, long catId) {

		listCatParent = catDao.findByTypeAndNotEqualForSelectbox(catType, catId);
		listCatParent.add(0, catParentDump);
	}

	private void initCategory() {
		Category currentCat = category;
		category = new Category();
		category.setTreeType(treeType);
		categoryParentId = 0L;
		formType = "cat-form";
		if (currentCat != null) {
			selectedCatType = currentCat.getCategoryType();
			category.setCategoryType(selectedCatType);
		}
		searchCatParent(selectedCatType, category.getCategoryId());
	}

	private void initCategoryType() {
		listCatType = new ArrayList<>();
		Map<Long, String> mapCatType = CategoryType.getCatTypesByTreeType(treeType);
		Iterator<Long> it = mapCatType.keySet().iterator();
		SelectItem item;
		while (it.hasNext()) {
			long catType = it.next();
			item = new SelectItem(catType, mapCatType.get(catType));
			listCatType.add(item);
		}
	}

	public void btnCatNew() {
		initCategory();
		isEditing = true;
	}

	public void btnCatCancel() {
		initCategory();
		isEditing = false;
	}

	public void btnCatSave() {
		doSaveCategory(category, categoryParentId);
	}

	private boolean doSaveCategory(Category objCat, Long catParentId) {
		objCat.setCategoryType(selectedCatType);
		if (catParentId > 0)
			objCat.setCategoryParentId(catParentId);
		else
			objCat.setCategoryParentId(null);

		List<Category> lstCheck = catDao.findDuplicateName(objCat);
		if (lstCheck != null && !lstCheck.isEmpty()) {
			super.showMessageERROR("common.save", " Category ", "common.duplicateName");
			return false;
		}

		if (objCat.getCategoryId() > 0) {
			// Edit

			if (catParentId == objCat.getCategoryId() || catDao.isContainInTree(catParentId, objCat.getCategoryId())) {
				this.showMessageWARN("common.save", " Category ", "cat.saveWarnRecursive");
				return false;
			}

			catDao.saveOrUpdate(objCat);
			if (catParentId <= 0) {
				// Do nothing
			} else {

				if (catParentId != this.category.getCategoryId()) {
					listCatSub.remove(objCat);
				}
			}

			// Update treenode
			TreeNode parentNode = mapCatNode.get(catParentId);
			if (parentNode == null)
				parentNode = mapCatFirstNode.get(objCat.getCategoryType());

			TreeNode node = mapCatNode.get(objCat.getCategoryId());
			Category nodeCat = (Category) node.getData();
			if (nodeCat != objCat)
				HibernateUtil.copyEntityProperties(Category.class, objCat, nodeCat, true);
			TreeNode oldParent = node.getParent();
			if (oldParent != parentNode) {

				if (oldParent != null)
					oldParent.getChildren().remove(node);
				node.setParent(parentNode);
				parentNode.getChildren().add(node);
			}
		} else {
			// New

			catDao.saveOrUpdate(objCat);
			if (catParentId <= 0) {

				listAllCategory.add(objCat);
			} else {

				if (catParentId == this.category.getCategoryId()) {
					listCatSub.add(objCat);
				}
			}

			// Add node vao tree
			TreeNode parentNode = mapCatNode.get(catParentId);
			if (parentNode == null)
				parentNode = mapCatFirstNode.get(objCat.getCategoryType());

			// Tim vi tri cua node category con cuoi cung cua parentNode de add vao
			List<TreeNode> lstChildren = parentNode.getChildren();
			int idxLastChild = 0;
			for(TreeNode node : lstChildren) {
				if(node.getData() instanceof Category) {
					idxLastChild++;
				} else {
					break;
				}
			}			
			TreeNode node = new OcsTreeNode(objCat, parentNode);
			node.setType(TreeNodeType.CATEGORY);
			parentNode.getChildren().remove(node);
			parentNode.getChildren().add(idxLastChild, node);
			
			lstCatID.add(objCat.getCategoryId());
			mapCatNode.put(objCat.getCategoryId(), node);
			getListNodeByObject(objCat).add(node);
		}
		// btnCatCancel();
		this.showMessageINFO("common.save", " Category ");
		return true;
	}

	public void editCategory(Category cat) {
		category = cat;
		setCategoryParentId(cat.getCategoryParentId());
		setEditing(true);
		searchCatParent(category.getCategoryType(), category.getCategoryId());
		setSelectCategoryNode(cat);
	}

	public void deleteCategory(Category category) {
		if (category != null) {
			catDao.delete(category);
			listCatSub.remove(category);
			listAllCategory.remove(category);
			TreeNode node = mapCatNode.remove(category.getCategoryId());
			if (node != null) {
				node.getParent().getChildren().remove(node);
				node.setParent(null);
			}

			this.showMessageINFO("common.delete", " Category ");
		}
	}

	// type: 1- su kien click node cha ;
	public List<Category> loadListCategory(Long categoryType) {
		listAllCategory = catDao.loadListCategoryByTypeNoParent(categoryType);
		return listAllCategory;
	}

	/********* CATEGORY - END ************/

	/********* CATEGORY DLG - BEGIN ************/
	private Category dlgCategory;
	private Long dlgCategoryParentId;
	private List<Category> listCatParentDlg;

	public void btnCatShowDlg(Category cat) {

		if (cat == null) {
			// New child cat

			dlgCategory = new Category();
			dlgCategory.setTreeType(treeType);
			dlgCategoryParentId = category.getCategoryId();
			dlgCategory.setCategoryType(selectedCatType);
		} else {
			// Edit child cat

			dlgCategory = cat;
			dlgCategoryParentId = cat.getCategoryParentId();
		}

		searchCatParentDlg(selectedCatType, dlgCategory.getCategoryId());
	}

	public void btnCatSaveDlg() {

		if (this.doSaveCategory(dlgCategory, dlgCategoryParentId)) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgCategory').hide();");
		}
	}

	private void searchCatParentDlg(Long catType, long catId) {

		if(catType == CategoryType.SYS_SYS_CONFIG) {
			listCatParentDlg = catDao.findByTypeAndNotEqualForSelectboxWithoutDomain(catType, catId);
		} else {
			listCatParentDlg = catDao.findByTypeAndNotEqualForSelectbox(catType, catId);	
		}
		
		listCatParentDlg.add(0, catParentDump);
	}

	/********* CATEGORY DLG - END ************/

	/********* GET SET - BEGIN ***********/
	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public Long getCategoryParentId() {
		return categoryParentId;
	}

	public void setCategoryParentId(Long categoryParentId) {
		this.categoryParentId = categoryParentId;
	}

	public List<Category> getListCatParent() {
		return listCatParent;
	}

	public void setListCatParent(List<Category> listParent) {
		this.listCatParent = listParent;
	}

	public List<Category> getListSubCategory() {
		return listCatSub;
	}

	public void setListSubCategory(List<Category> listSubCategory) {
		this.listCatSub = listSubCategory;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public String getCategoryTitle() {
		return categoryTitle;
	}

	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	public List<Category> getListAllCategory() {
		return listAllCategory;
	}

	public void setListAllCategory(List<Category> listAllCategory) {
		this.listAllCategory = listAllCategory;
	}

	public long getCategoryFirst() {
		return categoryFirst;
	}

	public void setCategoryFirst(long categoryFirst) {
		this.categoryFirst = categoryFirst;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<SelectItem> getListCatType() {
		return listCatType;
	}

	public void setListCatType(List<SelectItem> listCatType) {
		this.listCatType = listCatType;
	}

	public Long getSelectedCatType() {
		return selectedCatType;
	}

	public void setSelectedCatType(Long selectedCatType) {
		this.selectedCatType = selectedCatType;
	}

	public boolean isShowBtnCatNew() {
		return showBtnCatNew;
	}

	public void setShowBtnCatNew(boolean showBtnCatNew) {
		this.showBtnCatNew = showBtnCatNew;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	private String page;

	public Category getDlgCategory() {
		return dlgCategory;
	}

	public void setDlgCategory(Category dlgCategory) {
		this.dlgCategory = dlgCategory;
	}

	public Long getDlgCategoryParentId() {
		return dlgCategoryParentId;
	}

	public void setDlgCategoryParentId(Long dlgCategoryParentId) {
		this.dlgCategoryParentId = dlgCategoryParentId;
	}

	public List<Category> getListCatParentDlg() {
		return listCatParentDlg;
	}

	public void setListCatParentDlg(List<Category> listCatParentDlg) {
		this.listCatParentDlg = listCatParentDlg;
	}

	public CdrServiceDAO getCdrServiceDAO() {
		return cdrServiceDAO;
	}

	public void setCdrServiceDAO(CdrServiceDAO cdrServiceDAO) {
		this.cdrServiceDAO = cdrServiceDAO;
	}

	public CdrTemplateDAO getCdrTemplateDAO() {
		return cdrTemplateDAO;
	}

	public void setCdrTemplateDAO(CdrTemplateDAO cdrTemplateDAO) {
		this.cdrTemplateDAO = cdrTemplateDAO;
	}

	public CdrGenFilenameDAO getCdrGenFilenameDAO() {
		return cdrGenFilenameDAO;
	}

	public void setCdrGenFilenameDAO(CdrGenFilenameDAO cdrGenFilenameDAO) {
		this.cdrGenFilenameDAO = cdrGenFilenameDAO;
	}

	public CdrProcessConfigDAO getCdrProcessConfigDAO() {
		return cdrProcessConfigDAO;
	}

	public void setCdrProcessConfigDAO(CdrProcessConfigDAO cdrProcessConfigDAO) {
		this.cdrProcessConfigDAO = cdrProcessConfigDAO;
	}

	public String getTxtTreeSearch() {
		return txtTreeSearch;
	}

	public void setTxtTreeSearch(String txtTreeSearch) {
		this.txtTreeSearch = txtTreeSearch;
	}

	/****** GET SET - END *************/
}

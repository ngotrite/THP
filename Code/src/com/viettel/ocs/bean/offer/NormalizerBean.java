package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.Session;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.Normalizer.NormalizerType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BaseDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.GeoHomeZoneDAO;
import com.viettel.ocs.dao.GeoNetZoneDAO;
import com.viettel.ocs.dao.NestedObjectClassDAO;
import com.viettel.ocs.dao.NestedObjectDAO;
import com.viettel.ocs.dao.NomalizerNormParamMapDAO;
import com.viettel.ocs.dao.NormParamDAO;
import com.viettel.ocs.dao.NormValueDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.NormalizerNormValueMapDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.PreFunctionDAO;
import com.viettel.ocs.dao.ValueParam;
import com.viettel.ocs.dao.ZoneDAO;
import com.viettel.ocs.dao.ZoneMapDAO;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.GeoNetZone;
import com.viettel.ocs.entity.NestedObject;
import com.viettel.ocs.entity.NestedObjectClass;
import com.viettel.ocs.entity.NomalizerNormParamMap;
import com.viettel.ocs.entity.NormParam;
import com.viettel.ocs.entity.NormValue;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.NormalizerNormValueMap;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PreFunction;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneMap;
import com.viettel.ocs.model.OcsTreeNode;
import com.viettel.ocs.util.CommonUtil;
import com.viettel.ocs.util.DatetimeUtil;

@ManagedBean(name = "normalizerBean")
@ViewScoped
public class NormalizerBean extends BaseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6691764833614265337L;

	public static String baseChar = "abcdefghijklmlopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	private Normalizer normalizer;
	private NormalizerNormValueMap normVallueMap;
	private NormValue normValue;
	private NormParam normParam;
	private NomalizerNormParamMap normParamMap;
	private tmpInputFieldTable tmpInputField;
	private NestedObject nestedObjectTree;
	private NormValue normValueTmpToChooseColor;
	private tableNormValueParam tmpNormValueParamItem;
	private Boolean tmpNormValueParamItemIsStart;

	private List<Normalizer> listNormalizer;
	private List<Normalizer> listNormalizerByCate;
	private List<NormValue> listNormValueTmp;
	private List<NormValue> listNormValueToDelete;
	private List<NormParam> listNormParamToDelete;

	private List<tmpInputFieldTable> listInputFieldTable1;
	private List<tmpInputFieldTable> listInputFieldTable2;

	private List<tableNormValueParam> listTableNormValueParamForString;

	private List<preFunctionTable> listPreFunctionTable;
	private List<filterConditionTable> listFilterConditionTable;

	private List<NestedObject> listParentNestedObjectByChild;

	// private List<Category> listAllCategory;

	private List<Long> listObjectClassID = new ArrayList<Long>();
	private List<Long> listObjectID = new ArrayList<Long>();

	private NestedObject dumpEntity;

	private List<dateInWeek> listDateInWeekTMP;
	private List<tableTimeType> listTableTimeType;

	private Map<Long, PreFunction> mapPreFunction;

	private List<SelectItem> listNormType;
	private List<SelectItem> listNormState;
	private List<SelectItem> listNormCate;
	private List<SelectItem> listNormDefault;
	private List<SelectItem> listPreFunctions;
	private List<SelectItem> listNestedObject;
	private List<SelectItem> listNestedObjectClass;
	private List<SelectItem> listSelectNormValue;
	private List<SelectItem> listSelectItemCompare;
	private List<SelectItem> listDateNormDateType;
	private List<SelectItem> listZoneType;
	private List<SelectItem> listZoneID;
	private List<SelectItem> listHour;
	private List<SelectItem> listMinute;
	private List<SelectItem> listSecond;
	private List<SelectItem> listDay;

	private Integer startCharactor;
	private Integer endCharactor;
	private Integer selectedNormType;
	private Integer selectedNormState;
	private Long selectedNormCate;
	private Long selectedObjectClass;
	private Long selectedNormDefaultWithOutNormValue;
	private Long selectedNormDefaultInsingMatchType;
	private Long selectedTmpNestedObj;
	private Long selectedNormDefaultWithNormValue;
	private Long period;

	private String formtype;
	private String description;
	private String inputField1;
	private String inputField2;
	private String filterConditions;
	private String preFunctions;
	private String finalFilter;
	private String normName;

	private Boolean isEdit;
	private Boolean hasError;
	private Boolean isAvailableAmount;
	private Boolean isCurrentTimeUsing;
	private Boolean isStaticInput;

	private CategoryDAO catDao;
	private NestedObjectDAO nestedObjectDAO;
	private NestedObjectClassDAO nestedObjectClassDAO;
	private NormValueDAO normValueDAO;
	private NormParamDAO normParamDAO;
	private NormalizerNormValueMapDAO normValueMapDAO;
	private NomalizerNormParamMapDAO normParamMapDAO;
	private ParameterDAO parameterDAO;
	private NormalizerDAO normDAO;
	private GeoHomeZoneDAO geoHomeDAO;
	private GeoNetZoneDAO geoNetDAO;
	private ZoneDAO zoneDAO;
	private ZoneMapDAO zoneMapDAO;

	// Tree path
	private TreeNode root;
	private TreeNode treeNodeSelected;
	private String treeType;
	private int catType;
	private Map<Long, TreeNode> mapCatNode = new HashMap<Long, TreeNode>();
	private List<Long> lstAllCatID = new ArrayList<Long>();
	private Map<String, TreeNode> mapAllNode = new HashMap<String, TreeNode>();
	private Object objReturn;
	private String title;
	private String categoryTypeName;

	// SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	// SimpleDateFormat dfTimeType = new SimpleDateFormat("HH:mm:ss");
	// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss");

	private static final String NORM_DATE_FORMAT = "yyyy/MM/dd/HH/mm/ss";
	// private static final SimpleDateFormat df = new
	// SimpleDateFormat(NORM_DATE_FORMAT);
	private static final int NORM_DATE_PARAM_NONE = 1;
	private static final int NORM_DATE_PARAM_CURRENT_TIME = 2;
	private static final int NORM_DATE_PARAM_DELTA = 3;

	public NormalizerBean() {

		dumpEntity = new NestedObject();
	}

	@PostConstruct
	public void init() {
		this.selectedNormType = this.selectedNormState = 0;
		this.normalizer = new Normalizer();
		this.formtype = "";
		this.isEdit = true;
		this.listInputFieldTable1 = new ArrayList<>();
		this.listInputFieldTable2 = new ArrayList<>();
		this.listPreFunctionTable = new ArrayList<>();
		this.listNestedObjectClass = new ArrayList<>();
		this.listFilterConditionTable = new ArrayList<>();
		this.tmpInputField = new tmpInputFieldTable();
		this.listNestedObject = new ArrayList<>();
		this.listSelectNormValue = new ArrayList<>();
		this.listNormValue = new ArrayList<>();
		this.listDateNormDateType = new ArrayList<>();
		this.listZoneType = new ArrayList<>();
		this.listTableNormValueParamForString = new ArrayList<>();
		this.listZoneID = new ArrayList<>();
		this.listTableTimeType = new ArrayList<>();
		this.selectedNormDefaultWithOutNormValue = this.selectedNormDefaultInsingMatchType = (long) 0;
		this.isAvailableAmount = false;
		this.isCurrentTimeUsing = false;
		this.isStaticInput = false;
		this.listHour = new ArrayList<>();
		this.listMinute = new ArrayList<>();
		this.listSecond = new ArrayList<>();
		this.listDay = new ArrayList<>();
		this.tmpNormValueParamItem = new tableNormValueParam();
		this.tmpNormValueParamItemIsStart = false;
		this.normValueDAO = new NormValueDAO();
		this.normParamDAO = new NormParamDAO();
		this.normParamMapDAO = new NomalizerNormParamMapDAO();
		this.normValueMapDAO = new NormalizerNormValueMapDAO();
		this.normDAO = new NormalizerDAO();
		this.parameterDAO = new ParameterDAO();
		this.listNormalizerByCate = new ArrayList<>();
		this.listNormValueTmp = new ArrayList<>();
		this.listNormValueToDelete = new ArrayList<>();
		this.listNormParamToDelete = new ArrayList<>();
		this.period = 0l;
		this.startCharactor = this.endCharactor = null;
		this.normName = "";
		this.geoHomeDAO = new GeoHomeZoneDAO();
		this.setGeoNetDAO(new GeoNetZoneDAO());
		this.zoneDAO = new ZoneDAO();
		this.zoneMapDAO = new ZoneMapDAO();
		this.catDao = new CategoryDAO();

		initPreFunctions();
		loadCategory();
	}

	// parse select time to legend
	public Long parseSelectedTimeToLegend(long hour, long minute, long second) {
		long result = 0;

		result = hour * 3600 + minute * 60 + second;

		return result;
	}

	// parse legend to time
	public List<Long> parseLegendToLong(long legend) {
		List<Long> lstTime = new ArrayList<>();
		if (legend < 60) {
			lstTime.add(legend);
			lstTime.add((long) 0);
			lstTime.add((long) 0);
			return lstTime;
		}
		long second = legend % 60;
		lstTime.add(second);
		legend -= second;
		if ((legend / 60) < 60) {
			lstTime.add(legend / 60);
			lstTime.add((long) 0);
			return lstTime;
		}
		long minute = (legend / 60) % 60;
		lstTime.add(minute);
		legend -= minute * 60;
		if (legend < 3600) {
			lstTime.add((long) 0);
			return lstTime;
		}
		long hour = legend / 3600;
		lstTime.add(hour);
		return lstTime;
	}

	public void onchangeSomeFields() {

		switch (this.selectedNormType) {
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
			if (this.isCurrentTimeUsing) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:true");
				this.normalizer.setInputFileds("");
			} else {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:false");
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:
			if (this.isCurrentTimeUsing && this.isStaticInput) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:true;isStaticInput:true");
				this.normalizer.setInputFileds("");
			} else if (!this.isCurrentTimeUsing && this.isStaticInput) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:false;isStaticInput:true");
			} else if (this.isCurrentTimeUsing && !this.isStaticInput) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:true;isStaticInput:false");
				this.normalizer.setInputFileds("");
			} else {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:false;isStaticInput:false");
			}

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
			if (this.isAvailableAmount) {
				this.normalizer.setSpecialFileds("isAvailableAmount:true");
			} else {
				this.normalizer.setSpecialFileds("isAvailableAmount:false");
			}

			break;
		default:
			break;
		}
	}

	// change norm type
	public void onChangeNormType() {

		if (listTableNormValueParamForString != null)
			listTableNormValueParamForString.clear();
		if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER) {
			// Do nothing
		} else {

			if (listNormValue != null && listNormValue.size() > 0
					&& "true,false".contains(listNormValue.get(0).getValueName())) {
				// Clear khi list normvalue dang chua cac gia tri true,false fix
				// cung
				listNormValue.clear();
				if (listSelectNormValue != null)
					listSelectNormValue.clear();
			}
		}

		switch (this.selectedNormType) {

		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:
			addChildForTableNormValue();
			loadDataForSelectNormValue(this.listNormValueTmp);
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:
			addChildForTableNormValue();
			loadDataForSelectNormValue(this.listNormValueTmp);
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
			if (this.isCurrentTimeUsing) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:true");
				this.normalizer.setInputFileds("");
			} else {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:false");
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:
			if (this.isCurrentTimeUsing && this.isStaticInput) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:true;isStaticInput:true");
				this.normalizer.setInputFileds("");
			} else if (!this.isCurrentTimeUsing && this.isStaticInput) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:false;isStaticInput:true");
			} else if (this.isCurrentTimeUsing && !this.isStaticInput) {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:true;isStaticInput:false");
				this.normalizer.setInputFileds("");
			} else {
				this.normalizer.setSpecialFileds("isCurrentTimeUsing:false;isStaticInput:false");
			}

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
			if (this.isAvailableAmount) {
				this.normalizer.setSpecialFileds("isAvailableAmount:true");
			} else {
				this.normalizer.setSpecialFileds("isAvailableAmount:false");
			}

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:
			addChildForTableNormValue();
			loadDataForSelectNormValue(this.listNormValueTmp);
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:

			break;

		default:
			break;
		}
	}

	// build tree path
	public void createTree(tmpInputFieldTable item, String type) {
		// Map<String, String> params = FacesContext.getCurrentInstance()
		// .getExternalContext().getRequestParameterMap();
		// treeType = params.get("treeType");
		// if (treeType.contains(";")) {
		// String[] parts = treeType.split(";");
		// treeType = parts[0];
		// categoryTypeName = parts[1];
		// } else
		// return;
		// title = "Select a " + categoryTypeName;
		this.tmpInputField = item;
		catDao = new CategoryDAO();
		nestedObjectDAO = new NestedObjectDAO();
		parameterDAO = new ParameterDAO();
		buildTree(type);
	}

	private void buildTree(String treeType) {

		// if (treeType == null)
		// return;
		root = new DefaultTreeNode();
		this.treeType = treeType;
		switch (treeType) {
		case TreeType.OFFER_OBJECT:
			buildTreeObject();
			break;
		case TreeType.CATALOG_PARAMETER:
			buildTreeParameter();
			break;
		case TreeType.CATALOG_ZONE_DATA:
			buildTreeZoneData();
			break;
		case TreeType.CATALOG_ZONE_MAP:
			buildTreeZoneMAP();
			break;
		case TreeType.CATALOG_GEO_HOME:
			buildTreeGeoHome();
			break;
		case TreeType.CATALOG_GEO_NET:
			buildTreeGeoNet();
			break;
		default:
			break;
		}
	}

	private void buildTreeRootParent(String treeType, TreeNode root) {
		mapCatNode.clear();
		lstAllCatID.clear();
		mapAllNode.clear();
		Map<Long, String> mapType = CategoryType.getCatTypesByTreeType(treeType);
		Iterator<Long> it = mapType.keySet().iterator();
		while (it.hasNext()) {

			Long catType = it.next();
			Category cat = new Category();
			cat.setTreeType(treeType);
			cat.setCategoryName(mapType.get(catType));
			cat.setCategoryType(catType);
			cat.setCategoryId(0);

			TreeNode node = new DefaultTreeNode(cat, root);
			node.setExpanded(true);
			mapAllNode.put(CategoryType.getUniqueKey(catType), node);
			buildTreeByCat(catType, node);
		}
	}

	private void buildTreeByCat(Long catType, TreeNode rootCatType) {
		List<Category> lstCat = catDao.findByType(catType);
		List<TreeNode> lstNodeNew = new ArrayList<TreeNode>();
		List<TreeNode> lstNodeNotAdded = new ArrayList<TreeNode>();

		for (Category cat : lstCat) {

			if (cat.getCategoryParentId() == null || cat.getCategoryParentId() == 0) {

				TreeNode node = new DefaultTreeNode(cat, rootCatType);
				node.setExpanded(true);
				lstNodeNew.add(node);
			} else {

				boolean isFound = false;
				for (TreeNode parentNode : lstNodeNew) {
					if (cat.getCategoryParentId() == ((Category) parentNode.getData()).getCategoryId()) {
						TreeNode node = new DefaultTreeNode(cat, parentNode);
						node.setExpanded(true);
						lstNodeNew.add(node);
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					TreeNode node = new DefaultTreeNode(cat, null);
					node.setExpanded(true);
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
					node.setExpanded(true);
					break;
				}
			}
		}

		for (TreeNode node : lstNodeNew) {
			mapCatNode.put(((Category) node.getData()).getCategoryId(), node);
			lstAllCatID.add(((Category) node.getData()).getCategoryId());
			mapAllNode.put(((Category) node.getData()).getUniqueKey(), node);
		}
	}

	// *********BUILD NODE CHILDEN ********
	public void buildTreeObject() {
		nestedObjectClassDAO = new NestedObjectClassDAO();
		NestedObjectClass obj = nestedObjectClassDAO.get(100l);
		// buildTreeRootParent(treeType, root);
		root = new DefaultTreeNode("Objects", null);
		// root.setType(String.valueOf(CategoryType.OFF_OBJECT));
		TreeNode objectNode = new DefaultTreeNode(root.getType(), obj, root);
		objectNode.setExpanded(true);
		mapAllNode.put(obj.getUniqueKey(), objectNode);
		List<NestedObject> lstObjByParent = nestedObjectDAO.findObjectByObjParentNull();
		addNodeObject(objectNode, lstObjByParent);
	}

	// build node parameter
	public void buildTreeParameter() {
		// Build children categories
		buildTreeRootParent(treeType, root);
		// Add Parameter belong to Category

		List<Parameter> lstParameter = new ArrayList<Parameter>();
		if (selectedNormType.equals(NormalizerType.NUMBER_PARAMETER_NORMALIZER)) {
			lstParameter = parameterDAO.findParamByCatIdAndNotForTemp(lstAllCatID);
		} else {
			lstParameter = parameterDAO.findParamByCatId(lstAllCatID);
		}
		for (Parameter parameter : lstParameter) {
			TreeNode catNode = mapCatNode.get(parameter.getCategoryId());
			if (catNode != null) {
				TreeNode parameterNode = new DefaultTreeNode(catNode.getType(), parameter, catNode);
				parameterNode.setExpanded(true);
				mapAllNode.put(parameter.getUniqueKey(), parameterNode);
			}
		}
	}

	public void buildTreeGeoHome() {
		// Build children categories
		buildTreeRootParent(treeType, root);

		// Add Unit Type belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstAllCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<GeoHomeZone> lstGeoHome = geoHomeDAO.findByConditions(cols, operators, values, "");
		for (GeoHomeZone geoHome : lstGeoHome) {
			TreeNode catNode = mapCatNode.get(geoHome.getCategoryId());
			if (catNode != null) {

				TreeNode geoHomeNode = new DefaultTreeNode(catNode.getType(), geoHome, catNode);
				geoHomeNode.setExpanded(true);
				mapAllNode.put(geoHome.getUniqueKey(), geoHomeNode);

			}
		}
	}

	public void buildTreeGeoNet() {
		// Build children categories
		buildTreeRootParent(TreeType.CATALOG_GEO_HOME, root);

		// Add Unit Type belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstAllCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<GeoHomeZone> lstGeoHome = geoHomeDAO.findByConditions(cols, operators, values, "");
		for (GeoHomeZone geoHome : lstGeoHome) {
			TreeNode catNode = mapCatNode.get(geoHome.getCategoryId());
			if (catNode != null) {
				TreeNode geoHomeNode = new DefaultTreeNode(catNode.getType(), geoHome, catNode);
				geoHomeNode.setExpanded(true);
				mapAllNode.put(geoHome.getUniqueKey(), geoHomeNode);
				List<GeoNetZone> geoNetZones = geoNetDAO.findByGeoHomeZoneId(geoHome);
				for (GeoNetZone geoNetZone : geoNetZones) {
					new DefaultTreeNode(geoNetZone, geoHomeNode);
				}
			}
		}
	}

	public void buildTreeZoneData() {

		// Build children categories
		buildTreeRootParent(treeType, root);

		// Add Zone Data belong to Category
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.IN };
		List<Long> lstCatIDParam = new ArrayList<Long>();
		lstCatIDParam.addAll(lstAllCatID);
		if (lstCatIDParam.size() == 0)
			lstCatIDParam.add(-1L);
		Object[] values = { lstCatIDParam };
		List<ZoneMap> lstZoneMap = zoneMapDAO.findByConditions(cols, operators, values, "");
		for (ZoneMap zoneMap : lstZoneMap) {
			TreeNode catNode = mapCatNode.get(zoneMap.getCategoryId());
			if (catNode != null) {
				TreeNode zoneMapNode = new OcsTreeNode(zoneMap, catNode);
				zoneMapNode.setExpanded(true);
				List<Zone> lstZone = zoneDAO.findZoneByConditions(zoneMap.getZoneMapId());
				for (Zone zone : lstZone) {
					new OcsTreeNode(zone, zoneMapNode).setExpanded(true);
				}
			}
		}
	}

	public void buildTreeZoneMAP() {

		// Build children categories
		// root = new DefaultTreeNode("Objects", null);
		buildTreeRootParent(TreeType.CATALOG_ZONE_DATA, root);

		List<ZoneMap> lstZoneMap = zoneMapDAO.findAll("");
		for (ZoneMap zoneMap : lstZoneMap) {

			TreeNode catNode = mapCatNode.get(zoneMap.getCategoryId());
			if (catNode != null) {

				TreeNode zoneMapNode = new DefaultTreeNode(zoneMap, catNode);
				zoneMapNode.setExpanded(true);
				mapAllNode.put(zoneMap.getUniqueKey(), zoneMapNode);
			}
		}
	}

	private void addNodeObject(TreeNode parentNode, List<NestedObject> lstObjByParent) {
		for (NestedObject nestedObject : lstObjByParent) {
			TreeNode objectNode = new DefaultTreeNode(parentNode.getType(), nestedObject, parentNode);
			// objectNode.setExpanded(true);
			mapAllNode.put(nestedObject.getUniqueKey(), objectNode);

			int countChild = nestedObjectDAO.countChildObjectByObjId(nestedObject.getObjClassId());
			if (countChild > 0) {
				addDumpNode(objectNode);
			}

			// List<NestedObject> lstChildObjByParent =
			// nestedObjectDAO.findChildObjectByObjId(nestedObject.getObjClassId());
			// if(lstChildObjByParent.size() > 0){
			// objectNode.setExpanded(true);
			// addNodeObject(objectNode, lstChildObjByParent);
			// }
		}
	}

	private void addDumpNode(TreeNode parentNode) {
		TreeNode dumpNode = new DefaultTreeNode(dumpEntity, parentNode);
		dumpNode.setType(parentNode.getType());
	}

	private boolean removeDumpNode(TreeNode node) {
		List<TreeNode> lstChildren = node.getChildren();
		Iterator<TreeNode> it = lstChildren.iterator();
		while (it.hasNext()) {

			TreeNode childNode = it.next();
			if (childNode.getData() == dumpEntity) {

				childNode.setParent(null);
				it.remove();
				return true;
			}
		}

		return false;
	}

	// *********END***********************

	// ********EVENT NODE *********

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
		treeNodeSelected = nodeSlectedEvent.getTreeNode();
		if (treeNodeSelected != null) {
			objReturn = treeNodeSelected.getData();

			// if(treeNodeSelected.getData() instanceof NestedObject){
			// NestedObject nObj = (NestedObject) treeNodeSelected.getData();
			// if(treeNodeSelected.getChildCount() < 1){
			// List<NestedObject> lstChildObjByParent =
			// nestedObjectDAO.findChildObjectByObjId(nObj.getObjClassId());
			// if(lstChildObjByParent.size() > 0){
			// treeNodeSelected.setExpanded(true);
			// addNodeObject(treeNodeSelected, lstChildObjByParent);
			// }
			// }
			// }
		}
	}

	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {

		TreeNode treeNode = nodeExpandEvent.getTreeNode();
		if (treeNode.getData() instanceof NestedObject) {

			removeDumpNode(treeNode);

			NestedObject nObj = (NestedObject) treeNode.getData();
			if (treeNode.getChildCount() < 1) {
				List<NestedObject> lstChildObjByParent = nestedObjectDAO.findChildObjectByObjId(nObj.getObjClassId());
				if (lstChildObjByParent.size() > 0) {
					treeNode.setExpanded(true);
					addNodeObject(treeNode, lstChildObjByParent);
				}
			}
		}
	}

	public void onDialogReturn() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public void onDialogCreate() {
	}

	public void onDialogSelect() {

		if (objReturn == null || objReturn instanceof Category || objReturn instanceof NestedObjectClass
				|| (objReturn instanceof ZoneMap && this.tmpNormValueParamItem
						.getSelectedZoneType() != com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP)) {

			super.showMessageWARN("", "", "common.warnSelectObject");
			return;
		}

		String tmpPath = "";
		if (objReturn instanceof Parameter) {
			Parameter param = (Parameter) objReturn;
			if (this.listTableNormValueParamForString.size() > 0 && this.tmpNormValueParamItem != null) {
				if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER) {
					this.tmpNormValueParamItem.setSelectedParameterID(param.getParameterId());
				} else {
					if (this.tmpNormValueParamItemIsStart) {
						this.tmpNormValueParamItem.setStartValue(String.valueOf(param.getParameterId()));
					} else {
						this.tmpNormValueParamItem.setEndValue(String.valueOf(param.getParameterId()));
					}
				}
				this.listTableNormValueParamForString.set(indexOfItemTableNVP(this.tmpNormValueParamItem),
						this.tmpNormValueParamItem);
				onChangeNormValueInTableNVP(this.tmpNormValueParamItem);
			}
		} else if (objReturn instanceof NestedObject) {
			this.nestedObjectTree = (NestedObject) objReturn;
			this.listParentNestedObjectByChild = new ArrayList<>();
			this.listParentNestedObjectByChild.add(nestedObjectTree);
			if (nestedObjectTree.getParentClassId() > 0) {
				this.listParentNestedObjectByChild = processGetNestedObjByChild(nestedObjectTree,
						listParentNestedObjectByChild, treeNodeSelected);
			}

			if (this.listParentNestedObjectByChild.size() > 0) {
				if (checkPositionInListOfTmpInputField(tmpInputField) == 0) {
					List<tmpInputFieldTable> tmpInputFieldTableOnCreate = new ArrayList<>();
					boolean firstList = true;
					for (int i = (this.listParentNestedObjectByChild.size() - 1); i >= 0; i--) {
						nestedObjectDAO = new NestedObjectDAO();
						List<NestedObject> listObjSameLevel = new ArrayList<>();
						if (listParentNestedObjectByChild.get(i).getParentClassId() > 0) {
							listObjSameLevel = nestedObjectDAO
									.findObjectByObjParentID(listParentNestedObjectByChild.get(i).getParentClassId());
						} else {
							listObjSameLevel = nestedObjectDAO.findObjectByObjParentNull();
						}
						List<SelectItem> selectItemObj = new ArrayList<>();
						if (listObjSameLevel.size() > 0) {
							selectItemObj.add(new SelectItem((long) 0, "Choose a new item !"));
							for (NestedObject item : listObjSameLevel) {
								selectItemObj.add(new SelectItem(item.getObjId(), item.getObjName()));
							}
						}
						if (firstList) {
							tmpPath += listParentNestedObjectByChild.get(i).getObjName();
							tmpInputFieldTableOnCreate.add(new tmpInputFieldTable(1, tmpPath, "",
									listParentNestedObjectByChild.get(i), selectItemObj));
							firstList = false;
						} else {
							tmpInputFieldTableOnCreate.add(new tmpInputFieldTable(
									tmpInputFieldTableOnCreate.get(tmpInputFieldTableOnCreate.size() - 1).getIndex()
											+ 1,
									listParentNestedObjectByChild.get(i).getObjName(), "",
									listParentNestedObjectByChild.get(i), selectItemObj));
						}
					}
					if (checkTypeTableInListOfTmpInputField(tmpInputField) == 1) {
						this.listInputFieldTable1 = tmpInputFieldTableOnCreate;
						submitField01();
					} else if (checkTypeTableInListOfTmpInputField(tmpInputField) == 2) {
						this.listInputFieldTable2 = tmpInputFieldTableOnCreate;
						submitField02();
					}
				}

			}
		} else if (objReturn instanceof Zone) {
			Zone zone = (Zone) objReturn;

			if (this.listTableNormValueParamForString.size() > 0 && this.tmpNormValueParamItem != null
					&& this.tmpNormValueParamItem
							.getSelectedZoneType() == com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE) {

				this.tmpNormValueParamItem.setSelectedZoneId(zone.getZoneId());
				this.tmpNormValueParamItem.setSelectedZoneName(zone.getZoneName());
				this.listTableNormValueParamForString.set(indexOfItemTableNVP(this.tmpNormValueParamItem),
						this.tmpNormValueParamItem);
				onChangeNormValueInTableNVP(this.tmpNormValueParamItem);
			}

		} else if (objReturn instanceof ZoneMap) {
			ZoneMap zoneMap = (ZoneMap) objReturn;

			if (this.listTableNormValueParamForString.size() > 0 && this.tmpNormValueParamItem != null
					&& this.tmpNormValueParamItem
							.getSelectedZoneType() == com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP) {

				this.tmpNormValueParamItem.setSelectedZoneId(zoneMap.getZoneMapId());
				this.tmpNormValueParamItem.setSelectedZoneName(zoneMap.getZoneMapName());
				this.listTableNormValueParamForString.set(indexOfItemTableNVP(this.tmpNormValueParamItem),
						this.tmpNormValueParamItem);
				onChangeNormValueInTableNVP(this.tmpNormValueParamItem);
			}
		} else if (objReturn instanceof GeoHomeZone) {
			GeoHomeZone geoHome = (GeoHomeZone) objReturn;

			if (this.listTableNormValueParamForString.size() > 0 && this.tmpNormValueParamItem != null
					&& this.tmpNormValueParamItem
							.getSelectedZoneType() == com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_HOME_ZONE) {

				this.tmpNormValueParamItem.setSelectedZoneId(geoHome.getGeoHomeZoneId());
				this.tmpNormValueParamItem.setSelectedZoneName(geoHome.getGeoHomeZoneName());
				this.listTableNormValueParamForString.set(indexOfItemTableNVP(this.tmpNormValueParamItem),
						this.tmpNormValueParamItem);
				onChangeNormValueInTableNVP(this.tmpNormValueParamItem);
			}

		} else if (objReturn instanceof GeoNetZone) {
			GeoNetZone geoNet = (GeoNetZone) objReturn;

			if (this.listTableNormValueParamForString.size() > 0 && this.tmpNormValueParamItem != null
					&& this.tmpNormValueParamItem
							.getSelectedZoneType() == com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_NET_ZONE) {

				this.tmpNormValueParamItem.setSelectedZoneId(geoNet.getGeoNetZoneId());
				this.tmpNormValueParamItem.setSelectedZoneName(geoNet.getCellId().toString());
				this.listTableNormValueParamForString.set(indexOfItemTableNVP(this.tmpNormValueParamItem),
						this.tmpNormValueParamItem);
				onChangeNormValueInTableNVP(this.tmpNormValueParamItem);
			}
		} else {
			if (this.listTableNormValueParamForString.size() > 0 && this.tmpNormValueParamItem != null) {
				if (this.tmpNormValueParamItemIsStart) {
					this.tmpNormValueParamItem.setStartIsParam(false);
					this.tmpNormValueParamItem.setStartValue("");
				} else {
					this.tmpNormValueParamItem.setEndIsParam(false);
					this.tmpNormValueParamItem.setEndValue("");
				}
				this.listTableNormValueParamForString.set(indexOfItemTableNVP(this.tmpNormValueParamItem),
						this.tmpNormValueParamItem);
				onChangeNormValueInTableNVP(this.tmpNormValueParamItem);
			}
		}

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.dlgTreePath .ui-dialog-titlebar-close').click();");
	}

	public Integer checkPositionInListOfTmpInputField(tmpInputFieldTable tmp) {

		if (listInputFieldTable1.indexOf(tmp) != -1) {
			return listInputFieldTable1.indexOf(tmp);
		} else if (listInputFieldTable2.indexOf(tmp) != -1) {
			return listInputFieldTable2.indexOf(tmp);
		}

		return -1;
	}

	public Integer checkTypeTableInListOfTmpInputField(tmpInputFieldTable tmp) {

		if (listInputFieldTable1.indexOf(tmp) != -1) {
			return 1;
		} else if (listInputFieldTable2.indexOf(tmp) != -1) {
			return 2;
		}

		return -1;
	}

	public void fillDataForFilterObject(NestedObject obj) {
		this.listNestedObject = new ArrayList<>();
		if (obj != null) {
			if (obj.getIsAlist() == 1) {
				nestedObjectDAO = new NestedObjectDAO();
				List<NestedObject> listAllNestedObject = nestedObjectDAO.findChildObjectByObjId(obj.getObjClassId());
				if (listAllNestedObject.size() > 0) {
					for (NestedObject no : listAllNestedObject) {
						if (no.getIsAlist() == 0) {
							listNestedObject.add(new SelectItem(no.getObjName(), no.getObjName()));
						}
					}
				}
			}
		}
	}

	public List<NestedObject> processGetNestedObjByChild(NestedObject obj, List<NestedObject> lstObj, TreeNode node) {
		if (node != null && node.getParent() != null && node.getParent().getData() instanceof NestedObject) {
			try {
				NestedObject objParent = (NestedObject) node.getParent().getData();
				lstObj.add(objParent);
				TreeNode treeNodeCurrent = node.getParent();
				if (treeNodeCurrent.getParent() == null) {
					return lstObj;
				} else {
					processGetNestedObjByChild(objParent, lstObj, treeNodeCurrent);
				}
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				return lstObj;
			}
		}
		return lstObj;
	}

	// *********END*****************************************************************************************
	// change path child event
	public void changePathChildEvent(tmpInputFieldTable item) {
		if (item != null && this.selectedTmpNestedObj > 0) {
			nestedObjectDAO = new NestedObjectDAO();
			NestedObject no = nestedObjectDAO.get(selectedTmpNestedObj);
			if (item.path != no.getObjName()) {
				item.path = no.getObjName();
				item.filter = "";
				item.nestedObject = no;
				if (checkTypeTableInListOfTmpInputField(item) == 1) {
					this.listInputFieldTable1.set(checkPositionInListOfTmpInputField(item), item);
					if (checkPositionInListOfTmpInputField(item) < this.listInputFieldTable1.size() - 1) {
						for (int i = this.listInputFieldTable1.size() - 1; i >= checkPositionInListOfTmpInputField(item)
								+ 1; i--) {
							this.listInputFieldTable1.remove(i);
						}
					}
				} else if (checkTypeTableInListOfTmpInputField(item) == 2) {
					this.listInputFieldTable2.set(checkPositionInListOfTmpInputField(item), item);
					if (checkPositionInListOfTmpInputField(item) < this.listInputFieldTable2.size() - 1) {
						for (int i = this.listInputFieldTable2.size() - 1; i >= checkPositionInListOfTmpInputField(item)
								+ 1; i--) {
							this.listInputFieldTable2.remove(i);
						}
					}
				}
			}

		}
		submitField01();
		submitField02();
	}

	// btn Submit Field 1
	public void submitField01() {
		if (this.listInputFieldTable1.size() > 0) {
			String strInputField = "";
			for (tmpInputFieldTable tmp : listInputFieldTable1) {
				if (checkStringNullOrEmpty(tmp.getFilter())) {
					if (checkStringNullOrEmpty(strInputField)) {
						strInputField += tmp.getPath();
					} else {
						strInputField += "." + tmp.getPath();
					}
				} else {
					if (checkStringNullOrEmpty(strInputField)) {
						strInputField += tmp.getPath() + "{" + tmp.getFilter() + "}";
					} else {
						strInputField += "." + tmp.getPath() + "{" + tmp.getFilter() + "}";
					}
				}
			}
			this.inputField1 = strInputField;
		} else {
			this.inputField1 = "";
		}
	}

	// btn Submit Field 2
	public void submitField02() {
		if (this.listInputFieldTable2.size() > 0) {
			String strInputField = "";
			for (tmpInputFieldTable tmp : listInputFieldTable2) {
				if (checkStringNullOrEmpty(tmp.getFilter())) {
					if (checkStringNullOrEmpty(strInputField)) {
						strInputField += tmp.getPath();
					} else {
						strInputField += "." + tmp.getPath();
					}
				} else {
					if (checkStringNullOrEmpty(strInputField)) {
						strInputField += tmp.getPath() + "{" + tmp.getFilter() + "}";
					} else {
						strInputField += "." + tmp.getPath() + "{" + tmp.getFilter() + "}";
					}
				}
			}
			this.inputField2 = strInputField;
		} else {
			this.inputField2 = "";
		}
	}

	// Set data for edit
	public void setDataForEdit() {

		loadCategory();
		if (this.normalizer != null) {

			this.normalizer = normDAO.get(normalizer.getNormalizerId());
			this.normName = this.normalizer.getNormalizerName();
			this.selectedNormCate = this.normalizer.getCategoryId();
			this.selectedNormState = Integer.valueOf(this.normalizer.getState().toString());
			this.selectedNormType = Integer.valueOf(this.normalizer.getNormlizerType().toString());
			this.inputField1 = this.normalizer.getInputFileds();
			this.inputField2 = "";
			this.selectedNormDefaultWithNormValue = this.normalizer.getDefaultValue();
			this.listNormValue = new ArrayList<>();
			this.listNormValueTmp = new ArrayList<>();
			if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER
					|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER
					|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER) {
				// this.selectedNormDefaultWithNormValue =
				// this.normalizer.getDefaultValue();
				String inputField = this.normalizer.getInputFileds();
				if (inputField.indexOf("/") > 0) {
					this.inputField1 = inputField.substring(0, inputField.indexOf("/"));
					this.inputField2 = inputField.substring(inputField.indexOf("/") + 1);
				}

				// normValueMapDAO = new NormalizerNormValueMapDAO();
				List<NormalizerNormValueMap> normValueMap = normValueMapDAO
						.findMapByNormalizerId(this.normalizer.getNormalizerId());
				if (normValueMap != null && normValueMap.size() > 0) {
					// this.listNormValue = new ArrayList<>();
					// this.listNormValueTmp = new ArrayList<>();
					normValueDAO = new NormValueDAO();
					for (NormalizerNormValueMap nnvm : normValueMap) {
						this.listNormValue.add(normValueDAO.get(nnvm.getNormValueId()));
						this.listNormValueTmp.add(normValueDAO.get(nnvm.getNormValueId()));
					}
				}

				loadDataForSelectNormValue(this.listNormValue);
			}

			if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER
					|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER) {
				try {
					if (this.normalizer.getSpecialFileds().indexOf(";period:") > 0) {
						String specialField = this.normalizer.getSpecialFileds();
						this.period = Long.valueOf(
								specialField.substring(specialField.indexOf(";period:") + ";period:".length()));
					}
				} catch (Exception e) {
					// TODO: handle exception
					getLogger().warn(e.getMessage(), e);
				}
			}

			this.isCurrentTimeUsing = false;
			try {
				loadDataForInputField();
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.loadError"),
								this.readProperties("normalizer.errorLoadDataInputField")));
			}

			try {
				reloadDataForSpecialField();
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.loadError"),
								this.readProperties("normalizer.errorLoadDataSpecialField")));
			}

			if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER) {
				this.selectedNormDefaultInsingMatchType = this.normalizer.getDefaultValue();
			}

			if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER) {
				loadListParamIDdefault();
			}

			try {
				if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER
						|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER) {
					// normValueMapDAO = new NormalizerNormValueMapDAO();
					List<NormalizerNormValueMap> normValueMap = normValueMapDAO
							.findMapByNormalizerId(this.normalizer.getNormalizerId());
					if (normValueMap != null && normValueMap.size() > 0) {

						this.listNormValue = new ArrayList<>();
						this.listNormValueTmp = new ArrayList<>();
						normValueDAO = new NormValueDAO();
						for (NormalizerNormValueMap nnvm : normValueMap) {
							this.listNormValue.add(normValueDAO.get(nnvm.getNormValueId()));
							this.listNormValueTmp.add(normValueDAO.get(nnvm.getNormValueId()));
						}
					}
					loadDataForSelectNormValue(this.listNormValue);

					if (this.listTableNormValueParamForString == null)
						this.listTableNormValueParamForString = new ArrayList<>();
					else
						this.listTableNormValueParamForString.clear();

					List<NomalizerNormParamMap> normParamMap = normParamMapDAO
							.findMapByNormalizerId(this.normalizer.getNormalizerId());
					if (normParamMap != null && normParamMap.size() > 0) {
						normParamDAO = new NormParamDAO();
						for (NomalizerNormParamMap nnvm : normParamMap) {
							NormParam normParam = normParamDAO.get(nnvm.getNormParamId());
							for (NormValue nv : this.listNormValue) {
								if (nv.getNormValueIndex() == normParam.getNormParamIndex()) {
									int select = 0;
									String inputText = "";
									switch (this.selectedNormType) {

									case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:
										select = Integer.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";type:") + ";type:".length(),
												normParam.getConfigInput().length()));
										inputText = normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(":") + 1,
												normParam.getConfigInput().indexOf(";"));
										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), "", "", 0, 0, (long) 0, 0, 0,
												(long) 0, (long) 0, (long) 0, (long) 0, (long) 1, false, false));
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:
										Long paramId = 0l;
										String isParam = "";
										Integer paramCompareType = 0;
										Integer paramPriority = 0;
										if (this.normalizer.getSpecialFileds().equals("isUseParameter:true")) {
											paramId = Long.valueOf(
													normParam.getConfigInput().substring("parameterId:".length(),
															normParam.getConfigInput().indexOf(";isUsingParameter:")));
											isParam = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";isUsingParameter:")
															+ ";isUsingParameter:".length(),
													normParam.getConfigInput().indexOf(";comparisionType:"));
											paramCompareType = Integer.valueOf(normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";comparisionType:")
															+ ";comparisionType:".length(),
													normParam.getConfigInput().indexOf(";priority:")));
											paramPriority = Integer.valueOf(normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";priority:")
															+ ";priority:".length(),
													normParam.getConfigInput().length()));
											boolean isParamBol = false;
											if (isParam.equals("true")) {
												isParamBol = true;
											}
											this.listTableNormValueParamForString.add(new tableNormValueParam(nv,
													normParam, paramCompareType, inputText, nv.getValueId(), "", "", 0,
													0, 0l, 0, paramPriority, paramId, (long) 0, (long) 0, (long) 0,
													(long) 1, isParamBol, false));
										} else {
											paramId = Long
													.valueOf(normParam.getConfigInput().substring("value:".length(),
															normParam.getConfigInput().indexOf(";comparisionType:")));
											paramCompareType = Integer.valueOf(normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";comparisionType:")
															+ ";comparisionType:".length(),
													normParam.getConfigInput().length()));

											this.listTableNormValueParamForString.add(new tableNormValueParam(nv,
													normParam, paramCompareType, inputText, nv.getValueId(), "", "", 0,
													0, 0l, 0, paramPriority, paramId, (long) 0, (long) 0, (long) 0,
													(long) 1, false, false));
										}
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
										long day = Long.valueOf(normParam.getConfigInput().substring("day:".length(),
												normParam.getConfigInput().indexOf(";hour:")));
										long hour = Long.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";hour:") + ";hour:".length(),
												normParam.getConfigInput().indexOf(";min:")));
										long minute = Long.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";min:") + ";min:".length(),
												normParam.getConfigInput().indexOf(";sec:")));
										long second = Long.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";sec:") + ";sec:".length(),
												normParam.getConfigInput().length()));
										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), "", "", 0, 0, (long) 0, 0, 0,
												(long) 0, hour, minute, second, day, false, false));
										createOrUpdateTableTime();
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:
										String startValue = normParam.getConfigInput().substring("startValue:".length(),
												normParam.getConfigInput().indexOf(";startType:"));
										int selectedStartValue = Integer.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";startType:")
														+ ";startType:".length(),
												normParam.getConfigInput().indexOf(";endValue:")));
										String endValue = normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";endValue:")
														+ ";endValue:".length(),
												normParam.getConfigInput().indexOf(";endType:"));
										int selectedEndValue = Integer.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";endType:") + ";endType:".length(),
												normParam.getConfigInput().length()));

										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), startValue, endValue,
												selectedStartValue, selectedEndValue, (long) 0, 0, 0, (long) 0,
												(long) 0, (long) 0, (long) 0, (long) 1, false, false));
										// this.listTableNormValueParamForString.get(this.listTableNormValueParamForString.size()
										// -1).setStartValueStr(startValue);
										// this.listTableNormValueParamForString.get(this.listTableNormValueParamForString.size()
										// -1).setEndValueStr(endValue);
										if (selectedStartValue == NORM_DATE_PARAM_NONE) {
											Date startValueDate = DatetimeUtil.stringToDate(startValue,
													NORM_DATE_FORMAT);
											this.listTableNormValueParamForString
													.get(this.listTableNormValueParamForString.size() - 1)
													.setStartValueDate(startValueDate);
										} else if (selectedStartValue == NORM_DATE_PARAM_DELTA) {
											long startValueNumber = CommonUtil.getLong(startValue);
											this.listTableNormValueParamForString
													.get(this.listTableNormValueParamForString.size() - 1)
													.setStartValueNumber(startValueNumber);
										}

										if (selectedEndValue == NORM_DATE_PARAM_NONE) {
											Date endValueDate = DatetimeUtil.stringToDate(endValue, NORM_DATE_FORMAT);
											this.listTableNormValueParamForString
													.get(this.listTableNormValueParamForString.size() - 1)
													.setEndValueDate(endValueDate);
										} else if (selectedEndValue == NORM_DATE_PARAM_DELTA) {
											long endValueNumber = CommonUtil.getLong(endValue);
											this.listTableNormValueParamForString
													.get(this.listTableNormValueParamForString.size() - 1)
													.setEndValueNumber(endValueNumber);
										}

										// Long startValueLong =
										// Long.valueOf(startValue) == null ?
										// 0l:Long.valueOf(startValue);
										// Long endValueLong =
										// Long.valueOf(endValue) == null ?
										// 0l:Long.valueOf(endValue);
										// if(selectedStartValue == 1){
										// startValue = df.format(new
										// Date(startValueLong));
										// }
										//
										// if(selectedEndValue == 1){
										// endValue = df.format(new
										// Date(endValueLong));
										// }
										//
										// long dayGreater = (endValueLong -
										// startValueLong) / 3600000;
										//
										//
										// if(selectedStartValue == 3 &&
										// selectedEndValue == 3){
										// startValue = "0";
										// endValue =
										// String.valueOf(dayGreater);
										// }
										//
										// if(selectedStartValue == 3 &&
										// selectedEndValue != 3){
										// startValue = "0";
										// }
										//
										// if(selectedStartValue != 3 &&
										// selectedEndValue == 3){
										// endValue =
										// String.valueOf(dayGreater);
										// }
										//
										// this.listTableNormValueParamForString.add(new
										// tableNormValueParam(
										// nv, normParam, select, inputText,
										// nv.getValueId(), startValue,
										// endValue, selectedStartValue,
										// selectedEndValue, (long)0, 0
										// ,0,(long) 0, (long)0, (long)0,
										// (long)0, (long)1, false, false
										// ));
										// this.listTableNormValueParamForString.get(this.listTableNormValueParamForString.size()
										// -1).setStartValueStr(df.format(new
										// Date(startValueLong)));
										// this.listTableNormValueParamForString.get(this.listTableNormValueParamForString.size()
										// -1).setStartValueDate(new
										// Date(startValueLong));
										// this.listTableNormValueParamForString.get(this.listTableNormValueParamForString.size()
										// -1).setEndValueStr(df.format(new
										// Date(endValueLong)));
										// this.listTableNormValueParamForString.get(this.listTableNormValueParamForString.size()
										// -1).setEndValueDate(new
										// Date(endValueLong));

										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:
										Boolean startIsParam = false;
										Boolean endIsParam = false;
										String startQuantity = "";
										String startIsParamStr = "";
										String endQuantity = "";
										String endIsParamStr = "";
										String priority = "0";
										if (normParam.getConfigInput().indexOf(";startIsParameter:") != -1) {
											startQuantity = normParam.getConfigInput().substring("start:".length(),
													normParam.getConfigInput().indexOf(";startIsParameter:"));
											startIsParamStr = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";startIsParameter:")
															+ ";startIsParameter:".length(),
													normParam.getConfigInput().indexOf(";end:"));
											endQuantity = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";end:") + ";end:".length(),
													normParam.getConfigInput().indexOf(";endIsParameter:"));
											endIsParamStr = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";endIsParameter:")
															+ ";endIsParameter:".length(),
													normParam.getConfigInput().indexOf(";priority:"));
											priority = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";priority:")
															+ ";priority:".length(),
													normParam.getConfigInput().length());
										} else {
											startQuantity = normParam.getConfigInput().substring("start:".length(),
													normParam.getConfigInput().indexOf(";end:"));
											endQuantity = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";end:") + ";end:".length(),
													normParam.getConfigInput().length());
										}
										if (startIsParamStr.equals("true")) {
											startIsParam = true;
										}

										if (endIsParamStr.equals("true")) {
											endIsParam = true;
										}

										if (startQuantity.equals("Double.MinValue")) {
											startQuantity = "";
										}

										if (endQuantity.equals("Double.MinValue")) {
											endQuantity = "";
										}
										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), startQuantity, endQuantity, 0, 0,
												(long) 0, 0, Integer.valueOf(priority), (long) 0, (long) 0, (long) 0,
												(long) 0, (long) 1, startIsParam, endIsParam));
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
										startIsParam = false;
										endIsParam = false;
										String startBalance = "";
										startIsParamStr = "";
										String endBalance = "";
										endIsParamStr = "";
										priority = "0";

										if (normParam.getConfigInput().indexOf(";startIsParameter:") != -1) {
											startBalance = normParam.getConfigInput().substring("start:".length(),
													normParam.getConfigInput().indexOf(";startIsParameter:"));
											startIsParamStr = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";startIsParameter:")
															+ ";startIsParameter:".length(),
													normParam.getConfigInput().indexOf(";end:"));
											endBalance = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";end:") + ";end:".length(),
													normParam.getConfigInput().indexOf(";endIsParameter:"));
											endIsParamStr = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";endIsParameter:")
															+ ";endIsParameter:".length(),
													normParam.getConfigInput().indexOf(";priority:"));
											priority = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";priority:")
															+ ";priority:".length(),
													normParam.getConfigInput().length());
										} else {
											startBalance = normParam.getConfigInput().substring("start:".length(),
													normParam.getConfigInput().indexOf(";end:"));
											endBalance = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";end:") + ";end:".length(),
													normParam.getConfigInput().length());
										}
										if (startIsParamStr.equals("true")) {
											startIsParam = true;
										}

										if (endIsParamStr.equals("true")) {
											endIsParam = true;
										}

										if (startBalance.equals("Double.MinValue")) {
											startBalance = "";
										}

										if (endBalance.equals("Double.MinValue")) {
											endBalance = "";
										}
										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), startBalance, endBalance, 0, 0,
												(long) 0, 0, Integer.valueOf(priority), (long) 0, (long) 0, (long) 0,
												(long) 0, (long) 1, startIsParam, endIsParam));
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:
										Long ZoneId = Long
												.valueOf(normParam.getConfigInput().substring("zoneValueId:".length(),
														normParam.getConfigInput().indexOf(";dataZoneType:")));
										Integer ZoneType = Integer.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";dataZoneType:")
														+ ";dataZoneType:".length(),
												normParam.getConfigInput().length()));
										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), "", "", 0, 0, ZoneId, ZoneType, 0,
												(long) 0, (long) 0, (long) 0, (long) 0, (long) 1, false, false));
										this.listTableNormValueParamForString
												.get(this.listTableNormValueParamForString.size() - 1)
												.setSelectedZoneName(getZoneNameByIdAndType(ZoneId, ZoneType));
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:
										startIsParam = false;
										endIsParam = false;
										String startAcmBalance = "";
										startIsParamStr = "";
										String endAcmBalance = "";
										endIsParamStr = "";
										priority = "0";

										if (normParam.getConfigInput().indexOf(";startIsParameter:") != -1) {
											startAcmBalance = normParam.getConfigInput().substring("start:".length(),
													normParam.getConfigInput().indexOf(";startIsParameter:"));
											startIsParamStr = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";startIsParameter:")
															+ ";startIsParameter:".length(),
													normParam.getConfigInput().indexOf(";end:"));
											endAcmBalance = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";end:") + ";end:".length(),
													normParam.getConfigInput().indexOf(";endIsParameter:"));
											endIsParamStr = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";endIsParameter:")
															+ ";endIsParameter:".length(),
													normParam.getConfigInput().indexOf(";priority:"));
											priority = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";priority:")
															+ ";priority:".length(),
													normParam.getConfigInput().length());
										} else {
											startAcmBalance = normParam.getConfigInput().substring("start:".length(),
													normParam.getConfigInput().indexOf(";end:"));
											endAcmBalance = normParam.getConfigInput().substring(
													normParam.getConfigInput().indexOf(";end:") + ";end:".length(),
													normParam.getConfigInput().length());
										}
										if (startIsParamStr.equals("true")) {
											startIsParam = true;
										}

										if (endIsParamStr.equals("true")) {
											endIsParam = true;
										}

										if (startAcmBalance.equals("Double.MinValue")) {
											startAcmBalance = "";
										}

										if (endAcmBalance.equals("Double.MinValue")) {
											endAcmBalance = "";
										}
										this.listTableNormValueParamForString.add(new tableNormValueParam(nv, normParam,
												select, inputText, nv.getValueId(), startAcmBalance, endAcmBalance, 0,
												0, (long) 0, 0, Integer.valueOf(priority), (long) 0, (long) 0, (long) 0,
												(long) 0, (long) 1, startIsParam, endIsParam));
										break;
									case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:
										paramId = Long
												.valueOf(normParam.getConfigInput().substring("parameterId:".length(),
														normParam.getConfigInput().indexOf(";comparisionType:")));
										paramCompareType = Integer.valueOf(normParam.getConfigInput().substring(
												normParam.getConfigInput().indexOf(";comparisionType:")
														+ ";comparisionType:".length(),
												normParam.getConfigInput().indexOf(";priority:")));
										paramPriority = Integer
												.valueOf(normParam.getConfigInput().substring(
														normParam.getConfigInput().indexOf(";priority:")
																+ ";priority:".length(),
														normParam.getConfigInput().length()));
										this.listTableNormValueParamForString
												.add(new tableNormValueParam(nv, normParam, paramCompareType, inputText,
														nv.getValueId(), "", "", 0, 0, 0l, 0, paramPriority, paramId,
														(long) 0, (long) 0, (long) 0, (long) 1, true, false));
										break;
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.loadError"),
								this.readProperties("normalizer.errorLoadDataNormValueParam")));
			}
		}
	}

	// get name zone
	public String getZoneNameByIdAndType(Long id, Integer type) {
		String name = "";
		try {
			switch (type) {
			case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE:
				name = zoneDAO.get(id).getZoneName();
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP:
				name = zoneMapDAO.get(id).getZoneMapName();
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_HOME_ZONE:
				name = geoHomeDAO.get(id).getGeoHomeZoneName();
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_NET_ZONE:
				name = geoNetDAO.get(id).getCellId().toString();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
		}
		return name;
	}

	// show dialog choosparam
	public void showDialogParameter() {
		super.openTreeCommonDialog(TreeType.CATALOG_PARAMETER, CategoryType.CTL_PARAMETER_NAME, 0, false, null);
	}

	public void showDialogParameterForNumberParam(tableNormValueParam item) {
		this.tmpNormValueParamItem = new tableNormValueParam();
		this.tmpNormValueParamItem = item;
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.show-dialog-param').click();");
	}

	public void showDialogZoneType(tableNormValueParam item) {
		this.tmpNormValueParamItem = new tableNormValueParam();
		this.tmpNormValueParamItem = item;
		RequestContext requestContext = RequestContext.getCurrentInstance();
		switch (item.selectedZoneType) {
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE:
			requestContext.execute("$('.show-dialog-zone').click();");
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP:
			requestContext.execute("$('.show-dialog-zonemap').click();");
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_HOME_ZONE:
			requestContext.execute("$('.show-dialog-geohome').click();");
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_NET_ZONE:
			requestContext.execute("$('.show-dialog-geonet').click();");
			break;
		default:
			break;
		}
	}

	public void showDialogParameterForStartValue(tableNormValueParam item) {
		if (item.getStartIsParam()) {
			this.tmpNormValueParamItem = new tableNormValueParam();
			this.tmpNormValueParamItem = item;
			this.tmpNormValueParamItemIsStart = true;
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.show-dialog-param').click();");
		} else {
			this.tmpNormValueParamItem = null;
		}

		item.setStartValue("");
//		item.setSelectedParameterID(-1L);
		onChangeNormValueInTableNVP(item);
	}

	public void showDialogParameterForEndValue(tableNormValueParam item) {
		if (item.getEndIsParam()) {
			this.tmpNormValueParamItem = item;
			this.tmpNormValueParamItemIsStart = false;
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.show-dialog-param').click();");
		} else {
			this.tmpNormValueParamItem = null;
		}

		item.setEndValue("");
		onChangeNormValueInTableNVP(item);
	}

	// index of item in table norm value param
	public Integer indexOfItemTableNVP(tableNormValueParam item) {
		Integer index = -1;
		if (this.listTableNormValueParamForString.size() > 0) {
			return this.listTableNormValueParamForString.indexOf(item);
		}
		return index;
	}

	// reaload datafor special field
	public void reloadDataForSpecialField() {
		if (this.normalizer != null && !checkStringNullOrEmpty(this.normalizer.getSpecialFileds())) {
			switch (this.normalizer.getNormlizerType().intValue()) {

			case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

				String start = normalizer.getSpecialFileds().substring("startCharacter:".length(),
						normalizer.getSpecialFileds().indexOf(";endCharacter:"));
				String end = normalizer.getSpecialFileds().substring(
						normalizer.getSpecialFileds().indexOf(";endCharacter:") + ";endCharacter:".length(),
						normalizer.getSpecialFileds().length());

				if (end.equals("Interger.MaxValue")) {
					end = null;
				}

				this.startCharactor = Integer.valueOf(start);
				this.endCharactor = end == null ? null : Integer.valueOf(end);
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
				String timeStatus = normalizer.getSpecialFileds().substring("isCurrentTimeUsing:".length(),
						normalizer.getSpecialFileds().length());
				if (timeStatus.equals("true")) {
					this.isCurrentTimeUsing = true;
				} else {
					this.isCurrentTimeUsing = false;
				}
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:
				String dateStatus = normalizer.getSpecialFileds().substring("isCurrentTimeUsing:".length(),
						normalizer.getSpecialFileds().indexOf(";isStaticInput:"));
				String isStaticInput = normalizer.getSpecialFileds().substring(
						normalizer.getSpecialFileds().indexOf(";isStaticInput:") + ";isStaticInput:".length(),
						normalizer.getSpecialFileds().length());
				if (dateStatus.equals("true")) {
					this.isCurrentTimeUsing = true;
				} else {
					this.isCurrentTimeUsing = false;
				}

				if (isStaticInput.equals("true")) {
					this.isStaticInput = true;
				} else {
					this.isStaticInput = false;
				}
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
				String isAvailableAmount = normalizer.getSpecialFileds().substring("isAvailableAmount:".length(),
						normalizer.getSpecialFileds().indexOf(";"));
				if (isAvailableAmount.equals("true")) {
					this.isAvailableAmount = true;
				} else {
					this.isAvailableAmount = false;
				}
				break;
			}
		}
	}

	// load data for inputfield

	public void loadDataForInputField() {
		if (!checkStringNullOrEmpty(this.inputField1)) {
			this.listInputFieldTable1 = new ArrayList<>();
			String[] path = this.inputField1.split(Pattern.quote("."));
			if (path.length > 0) {
				// nestedObjectClassDAO = new NestedObjectClassDAO();
				// NestedObjectClass nestedClass =
				// nestedObjectClassDAO.findNestedObjectClassByName(path[0]);
				// if(nestedClass != null){
				String path1 = path[0];
				String filter = "";
				if (path[0].indexOf("{") > 0) {
					filter = path[0].substring(path[0].indexOf("{") + 1, path[0].indexOf("}"));
					path1 = path[0].substring(0, path[0].indexOf("{"));
				}
				nestedObjectDAO = new NestedObjectDAO();
				NestedObject objectRoot = nestedObjectDAO.findNestedObjectByNameRoot(path1);
				if (objectRoot != null) {
					this.listInputFieldTable1.add(new tmpInputFieldTable(1, path1, filter.trim(), objectRoot, null));
					for (int i = 0; i < path.length; i++) {
						if (i > 0) {
							String pathi = path[i];
							filter = "";
							if (path[i].indexOf("{") > 0) {
								filter = path[i].substring(path[i].indexOf("{") + 1, path[i].indexOf("}"));
								pathi = path[i].substring(0, path[i].indexOf("{"));
							}
							NestedObject objectChild = nestedObjectDAO.findNestedObjectByNameAndParentID(pathi,
									this.listInputFieldTable1.get(this.listInputFieldTable1.size() - 1)
											.getNestedObject().getObjClassId());
							if (objectChild != null) {
								List<NestedObject> lstObjChild = nestedObjectDAO.findObjectByObjParentID(
										this.listInputFieldTable1.get(this.listInputFieldTable1.size() - 1)
												.getNestedObject().getParentClassId());
								List<SelectItem> selectItemObj = new ArrayList<>();
								if (lstObjChild.size() > 0) {
									selectItemObj.add(new SelectItem(0l, "Choose another child!"));
									for (NestedObject no : lstObjChild) {
										selectItemObj.add(new SelectItem(no.getObjId(), no.getObjName()));
									}
									this.listInputFieldTable1
											.add(new tmpInputFieldTable(
													this.listInputFieldTable1.get(this.listInputFieldTable1.size() - 1)
															.getIndex() + 1,
													pathi, filter, objectChild, selectItemObj));
								}
							}
						}
					}
				}
				// }
			}
		}

		if (!checkStringNullOrEmpty(this.inputField2)) {
			this.listInputFieldTable2 = new ArrayList<>();
			String[] path = this.inputField2.split(Pattern.quote("."));
			if (path.length > 0) {
				// nestedObjectClassDAO = new NestedObjectClassDAO();
				// NestedObjectClass nestedClass =
				// nestedObjectClassDAO.findNestedObjectClassByName(path[0]);
				// if(nestedClass != null){
				String path1 = path[0];
				String filter = "";
				if (path[0].indexOf("{") > 0) {
					filter = path[0].substring(path[0].indexOf("{") + 1, path[0].indexOf("}"));
					path1 = path[0].substring(0, path[0].indexOf("{"));
				}
				nestedObjectDAO = new NestedObjectDAO();
				NestedObject objectRoot = nestedObjectDAO.findNestedObjectByNameRoot(path1);
				if (objectRoot != null) {
					this.listInputFieldTable2.add(new tmpInputFieldTable(1, path1, filter.trim(), objectRoot, null));
					for (int i = 0; i < path.length; i++) {
						if (i > 0) {
							String pathi = path[i];
							filter = "";
							if (path[i].indexOf("{") > 0) {
								filter = path[i].substring(path[i].indexOf("{") + 1, path[i].indexOf("}"));
								pathi = path[i].substring(0, path[i].indexOf("{"));
							}
							NestedObject objectChild = nestedObjectDAO.findNestedObjectByNameAndParentID(pathi,
									this.listInputFieldTable2.get(this.listInputFieldTable2.size() - 1)
											.getNestedObject().getObjClassId());
							if (objectChild != null) {
								List<NestedObject> lstObjChild = nestedObjectDAO.findObjectByObjParentID(
										this.listInputFieldTable2.get(this.listInputFieldTable2.size() - 1)
												.getNestedObject().getParentClassId());
								List<SelectItem> selectItemObj = new ArrayList<>();
								if (lstObjChild.size() > 0) {
									selectItemObj.add(new SelectItem(0l, "Choose another child!"));
									for (NestedObject no : lstObjChild) {
										selectItemObj.add(new SelectItem(no.getObjId(), no.getObjName()));
									}
									this.listInputFieldTable2
											.add(new tmpInputFieldTable(
													this.listInputFieldTable2.get(this.listInputFieldTable2.size() - 1)
															.getIndex() + 1,
													pathi, filter, objectChild, selectItemObj));
								}
							}
						}
					}
				}
				// }
			}
		}
	}

	public int countCharInString(String str, String character) {
		String[] mangStr = null;
		for (int i = 0; i < str.length(); i++) {
			mangStr = str.split(str, str.indexOf(character));
		}
		return mangStr.length;
	}

	// set data for Normvaluetmp
	public void addDataForNormValueTmp(NormValue tmp) {
		if (tmp != null) {
			this.normValueTmpToChooseColor = tmp;
		}
	}

	// Choose color
	public void chooseColor(String color, String bgColor) {
		if (this.normValueTmpToChooseColor != null) {
			Integer indexNorm = this.listNormValueTmp.indexOf(this.normValueTmpToChooseColor);
			if (indexNorm >= 0) {
				this.listNormValueTmp.get(indexNorm).setColor(color);
				this.listNormValueTmp.get(indexNorm).setColorBG(bgColor);
			}
		}
	}

	// Save normalizer
	public void saveNormalizer() {
		if (checkValueBeforeSave()) {

			setValueForNormalizerBeforeSave();

			if (clearDeleteDataOfNormParam()) {

				if (this.normalizer.getDefaultValue() == null) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.errorValidateDefaultValue")));
				} else {
					Session session = HibernateUtil.getOpenSession();
					session.getTransaction().begin();
					boolean isNew = false;
					try {

						if (this.normalizer.getNormalizerId() <= 0) {
							// Normalizer lastItem =
							// normDAO.getLastNormalizer();
							// if (lastItem != null && lastItem.getPosIndex() !=
							// null) {
							// this.normalizer.setPosIndex(lastItem.getPosIndex()
							// + 1);
							// } else {
							// this.normalizer.setPosIndex(1l);
							// }

							isNew = true;
						}

						NormalizerDAO normalizerDAO = new NormalizerDAO();
						normalizerDAO.generateIndexByCat(this.normalizer, "posIndex", "normalizerId", session);
						session.saveOrUpdate(this.normalizer);
						if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER) {
							if (!saveNormValue(session)) {
								session.getTransaction().rollback();
								if (isNew) {
									this.normalizer.setNormalizerId(0);
								}
								return;
							}
							if (!saveNormParam(session)) {
								session.getTransaction().rollback();
								if (isNew) {
									this.normalizer.setNormalizerId(0);
								}
								return;
							}
						}
						if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER
								|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER) {
							if (!saveNormValue(session)) {
								session.getTransaction().rollback();
								if (isNew) {
									this.normalizer.setNormalizerId(0);
								}
								return;
							}
						}
						TreeOfferBean treeOfferBean = super.getTreeOfferBean();
						treeOfferBean.updateTreeNode(normalizer, catDao.get(selectedNormCate), null);
						this.showMessageINFO("common.save", " Normalizer ");
						this.isEdit = false;
						session.getTransaction().commit();
					} catch (Exception e) {
						getLogger().warn(e.getMessage(), e);
						session.getTransaction().rollback();
						if (isNew) {
							this.normalizer.setNormalizerId(0);
						}
						this.isEdit = true;
						throw e;
					} finally {
						session.close();
					}
				}
			} else {

			}
		}
	}

	// clear Data NormValue to del
	public Boolean clearDeleteDataOfNormParam() {
		boolean result = true;

		if (this.listNormParamToDelete.size() > 0) {
			for (NormParam np : this.listNormParamToDelete) {
				if (!normParamMapDAO.deleteNormParamItemAndMap(np)) {
					result = false;
					break;
				}
			}

			if (!result) {
				this.listNormParamToDelete = new ArrayList<>();
			}
		}

		return result;
	}

	// Set Data for Object Normalizer before save
	public void setValueForNormalizerBeforeSave() {
		NormalizerDAO normDao = new NormalizerDAO();
		this.normalizer.setCategoryId(selectedNormCate);
		this.normalizer.setState((long) selectedNormState);
		this.normalizer.setNormlizerType((long) selectedNormType);
		// this.normalizer.setInputFileds(buildInputfieldForSave());
		if (!isCurrentTimeUsing) {
			this.normalizer.setInputFileds(buildInputfieldForSave());
		} else {
			this.normalizer.setInputFileds("");
		}
		this.normalizer.setDomainId(normDao.getDomainId());
		this.normalizer.setNormalizerName(this.normName);
		if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER) {
			if (this.listTableNormValueParamForString.size() > 0) {
				this.normalizer.setSpecialFileds("isUseParameter:false");
				for (tableNormValueParam nv : this.listTableNormValueParamForString) {
					if (nv.getStartIsParam() || nv.getEndIsParam()) {
						this.normalizer.setSpecialFileds("isUseParameter:true");
						break;
					}
				}
			}
		}
		if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER) {
			if (this.listTableNormValueParamForString.size() > 0) {
				String specialfield = this.normalizer.getSpecialFileds();
				if(specialfield == null)
					specialfield = "";
				if(specialfield.contains("isUseParameter:false")) {
					// Do Nothing
				} else if (specialfield.contains("isUseParameter:true")) {
					specialfield = specialfield.replace("isUseParameter:true", "isUseParameter:false");
				} else {
					if(specialfield.trim().length() > 0)
						specialfield += ";isUseParameter:false";
					else
						specialfield += "isUseParameter:false";
				}
				
				if(specialfield.indexOf(";period:") > 0)
					specialfield = specialfield.substring(0, specialfield.indexOf(";period:"));				
					
				this.normalizer.setSpecialFileds(specialfield);
				for (tableNormValueParam nv : this.listTableNormValueParamForString) {
					if (nv.getStartIsParam() || nv.getEndIsParam()) {
						specialfield = specialfield.replace("isUseParameter:false", "isUseParameter:true");
						this.normalizer.setSpecialFileds(specialfield);
						break;
					}
				}
			}
		}

		if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER) {
			if (this.listTableNormValueParamForString.size() > 0) {
				if (this.period != 0) {
					String specialfield = this.normalizer.getSpecialFileds();
					this.normalizer.setSpecialFileds(specialfield + ";" + "period:" + String.valueOf(this.period));
				}
			}
		}

		if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER) {
			this.normalizer.setDefaultValue(this.selectedNormDefaultWithNormValue);
		}
	}

	// Check require value input beforeSave
	public Boolean checkValueBeforeSave() {
		Boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (checkStringNullOrEmpty(this.normName)) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateName")));
			return false;
		}

		if (normDAO.checkFieldIsExist("normalizerName", this.normName, this.normalizer)) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateNameIsExist")));
			return false;
		}

		if (this.normalizer.getStartDate() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateStartDate")));
			return false;
		}
		if (!this.isCurrentTimeUsing) {
			if (checkStringNullOrEmpty(this.inputField1)) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorValidateInputfield1")));
				return false;
			}
		}

		if (this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER
				&& this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER
				&& this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER) {

			if (!checkNormValueFillAllToTableNormValueParam()) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorValidateNotFillAllValue")));
				return false;
			}

			if (!checkDataOfNormValueParam()) {
//				context.addMessage(null,
//						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
//								this.readProperties("normalizer.errorConfigInput")));
				return false;
			} else {
				switch (this.selectedNormType) {
				case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:
					if (!checkIsUsedParamInTableNVP(1)) {
						if (this.listTableNormValueParamForString.size() > 0) {
							for (tableNormValueParam tb : this.listTableNormValueParamForString) {
								String configInputTmp = tb.getNormParam().getConfigInput();
								if (configInputTmp.indexOf("parameterId:") != -1) {
									Long paramId = Long.valueOf(configInputTmp.substring("parameterId:".length(),
											configInputTmp.indexOf(";isUsingParameter:")));
									Integer paramCompareType = Integer.valueOf(configInputTmp.substring(
											configInputTmp.indexOf(";comparisionType:") + ";comparisionType:".length(),
											configInputTmp.indexOf(";priority:")));
									tb.getNormParam().setConfigInput(
											"value:" + paramId + ";comparisionType:" + paramCompareType);
								}
							}
						}
					}
					break;
				case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:
					if (!checkIsUsedParamInTableNVP(2)) {
						if (this.listTableNormValueParamForString.size() > 0) {
							for (tableNormValueParam tb : this.listTableNormValueParamForString) {
								String configInputTmp = tb.getNormParam().getConfigInput();
								if (configInputTmp.indexOf(";startIsParameter:") != -1) {
									String startValue = configInputTmp.substring("start:".length(),
											configInputTmp.indexOf(";startIsParameter:"));
									String endValue = configInputTmp.substring(
											configInputTmp.indexOf(";end:") + ";end:".length(),
											configInputTmp.indexOf(";endIsParameter:"));

									tb.getNormParam().setConfigInput("start:" + startValue + ";end:" + endValue);
								}
							}
						}
					}
					break;
				case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
					if (!checkIsUsedParamInTableNVP(2)) {
						if (this.listTableNormValueParamForString.size() > 0) {
							for (tableNormValueParam tb : this.listTableNormValueParamForString) {
								String configInputTmp = tb.getNormParam().getConfigInput();
								if (configInputTmp.indexOf(";startIsParameter:") != -1) {
									String startValue = configInputTmp.substring("start:".length(),
											configInputTmp.indexOf(";startIsParameter:"));
									String endValue = configInputTmp.substring(
											configInputTmp.indexOf(";end:") + ";end:".length(),
											configInputTmp.indexOf(";endIsParameter:"));

									tb.getNormParam().setConfigInput("start:" + startValue + ";end:" + endValue);
								}
							}
						}
					}
					break;
				case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:
					if (!checkIsUsedParamInTableNVP(2)) {
						if (this.listTableNormValueParamForString.size() > 0) {
							for (tableNormValueParam tb : this.listTableNormValueParamForString) {
								String configInputTmp = tb.getNormParam().getConfigInput();
								if (configInputTmp.indexOf(";startIsParameter:") != -1) {
									String startValue = configInputTmp.substring("start:".length(),
											configInputTmp.indexOf(";startIsParameter:"));
									String endValue = configInputTmp.substring(
											configInputTmp.indexOf(";end:") + ";end:".length(),
											configInputTmp.indexOf(";endIsParameter:"));

									tb.getNormParam().setConfigInput("start:" + startValue + ";end:" + endValue);
								}
							}
						}
					}
					break;

				default:
					break;
				}
			}

		}

		switch (this.selectedNormType) {

		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:
			if (checkStringNullOrEmpty(this.inputField2)) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorValidateInputfield1")));
				return false;
			}

			if (this.startCharactor != null && this.endCharactor != null && this.startCharactor > this.endCharactor) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorStartEndCharacter")));
				return false;
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:
			if (checkStringNullOrEmpty(this.inputField2)) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorValidateInputfield2")));
				return false;
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:

			break;

		default:
			break;
		}

		return result;
	}

	public String selectedZoneName(Integer zoneType) {
		String name = "";

		switch (zoneType) {
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE:
			name = "ZONE";
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP:
			name = "ZONE MAP";
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_NET_ZONE:
			name = "GEO NET ZONE";
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_HOME_ZONE:
			name = "GEO HOME ZONE";
			break;
		default:
			break;
		}

		return name;
	}

	public Boolean checkDataOfNormValueParam() {
		boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (this.listTableNormValueParamForString.size() > 0) {
			for (tableNormValueParam vp : this.listTableNormValueParamForString) {
				if (vp.getNormParam() != null && !checkStringNullOrEmpty(vp.getNormParam().getConfigInput())) {
					int select = 0;
					String inputText = "";
					String configInput = vp.getNormParam().getConfigInput();
					try {
						switch (this.selectedNormType) {

						case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:
							select = Integer.valueOf(configInput.substring(
									configInput.indexOf(";type:") + ";type:".length(), configInput.length()));
							inputText = configInput.substring(configInput.indexOf(":") + 1, configInput.indexOf(";"));

							if (checkStringNullOrEmpty(inputText)) {
								context.addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_WARN,
												this.readProperties("normalizer.validateError"),
												this.readProperties("normalizer.stringInputNull")));
								return false;
							}

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:
							try {
								if (this.normalizer != null && this.normalizer.getNormalizerId() == 0) {
									Long paramId = Long.valueOf(configInput.substring("parameterId:".length(),
											configInput.indexOf(";isUsingParameter:")));
									String isParam = configInput.substring(
											configInput.indexOf(";isUsingParameter:") + ";isUsingParameter:".length(),
											configInput.indexOf(";comparisionType:"));
									Integer paramCompareType = Integer.valueOf(configInput.substring(
											configInput.indexOf(";comparisionType:") + ";comparisionType:".length(),
											configInput.indexOf(";priority:")));
									Integer paramPriority = Integer.valueOf(configInput.substring(
											configInput.indexOf(";priority:") + ";priority:".length(),
											configInput.length()));
									boolean isParamBol = false;
									if (isParam.equals("true")) {
										isParamBol = true;
									}

									if (paramId < 0) {
										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.stringInputNull")));
										return false;
									}
								} else if (this.normalizer != null && this.normalizer.getNormalizerId() > 0) {
									if (configInput.indexOf("parameterId:") != -1) {
										Long paramId = Long.valueOf(configInput.substring("parameterId:".length(),
												configInput.indexOf(";isUsingParameter:")));
										String isParam = configInput.substring(
												configInput.indexOf(";isUsingParameter:")
														+ ";isUsingParameter:".length(),
												configInput.indexOf(";comparisionType:"));
										Integer paramCompareType = Integer.valueOf(configInput.substring(
												configInput.indexOf(";comparisionType:") + ";comparisionType:".length(),
												configInput.indexOf(";priority:")));
										Integer paramPriority = Integer.valueOf(configInput.substring(
												configInput.indexOf(";priority:") + ";priority:".length(),
												configInput.length()));
										boolean isParamBol = false;
										if (isParam.equals("true")) {
											isParamBol = true;
										}

										if (paramId < 0) {
											context.addMessage(null,
													new FacesMessage(FacesMessage.SEVERITY_WARN,
															this.readProperties("normalizer.validateError"),
															this.readProperties("normalizer.stringInputNull")));
											return false;
										}
									} else {
										Long paramId = Long.valueOf(configInput.substring("value:".length(),
												configInput.indexOf(";comparisionType:")));
										Integer paramCompareType = Integer.valueOf(configInput.substring(
												configInput.indexOf(";comparisionType:") + ";comparisionType:".length(),
												configInput.length()));

										if (paramId < 0) {
											context.addMessage(null,
													new FacesMessage(FacesMessage.SEVERITY_WARN,
															this.readProperties("normalizer.validateError"),
															this.readProperties("normalizer.stringInputNull")));
											return false;
										}
									}
								}
							} catch (Exception e) {
								getLogger().warn(e.getMessage(), e);
								context.addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_WARN,
												this.readProperties("normalizer.validateError"),
												this.readProperties("normalizer.stringInputError")));
								return false;
							}
							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:
							String startValue = configInput.substring("startValue:".length(),
									configInput.indexOf(";startType:"));
							int selectedStartValue = Integer.valueOf(
									configInput.substring(configInput.indexOf(";startType:") + ";startType:".length(),
											configInput.indexOf(";endValue:")));
							String endValue = configInput.substring(
									configInput.indexOf(";endValue:") + ";endValue:".length(),
									configInput.indexOf(";endType:"));
							int selectedEndValue = Integer.valueOf(configInput.substring(
									configInput.indexOf(";endType:") + ";endType:".length(), configInput.length()));

							if (checkStringNullOrEmpty(startValue) || checkStringNullOrEmpty(endValue)) {
								context.addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_WARN,
												this.readProperties("normalizer.validateError"),
												this.readProperties("normalizer.stringInputNull")));
								return false;
							}

							if (selectedStartValue == NORM_DATE_PARAM_DELTA
									&& selectedEndValue == NORM_DATE_PARAM_DELTA) {

								long startValueNumber = CommonUtil.getLong(startValue);
								long endValueNumber = CommonUtil.getLong(endValue);
								if (startValueNumber > endValueNumber) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.errorStartEndValue")));
									return false;
								}
							} else if (selectedStartValue == NORM_DATE_PARAM_DELTA) {

								long startValueNumber = CommonUtil.getLong(startValue);
								if (startValueNumber <= 0) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.errorStartValueNegative")));
									return false;
								}
							} else if (selectedEndValue == NORM_DATE_PARAM_DELTA) {

								long endValueNumber = CommonUtil.getLong(endValue);
								if (endValueNumber <= 0) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.errorEndValueNegative")));
									return false;
								}
							} else {

								if (selectedStartValue == NORM_DATE_PARAM_NONE
										&& selectedEndValue == NORM_DATE_PARAM_NONE) {

									Date startValueDate = DatetimeUtil.stringToDate(startValue, NORM_DATE_FORMAT);
									Date endValueDate = DatetimeUtil.stringToDate(endValue, NORM_DATE_FORMAT);
									if (startValueDate.compareTo(endValueDate) > 0) {
										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							}

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:
							Boolean startIsParam = false;
							Boolean endIsParam = false;

							String startQuantity = "";
							String startIsParamStr = "";
							String endQuantity = "";
							String endIsParamStr = "";
							String priority = "0";

							if (configInput.indexOf(";startIsParameter:") != -1) {

								startQuantity = configInput.substring("start:".length(),
										configInput.indexOf(";startIsParameter:"));
								startIsParamStr = configInput.substring(
										configInput.indexOf(";startIsParameter:") + ";startIsParameter:".length(),
										configInput.indexOf(";end:"));
								endQuantity = configInput.substring(configInput.indexOf(";end:") + ";end:".length(),
										configInput.indexOf(";endIsParameter:"));
								endIsParamStr = configInput.substring(
										configInput.indexOf(";endIsParameter:") + ";endIsParameter:".length(),
										configInput.indexOf(";priority:"));
								priority = configInput.substring(
										configInput.indexOf(";priority:") + ";priority:".length(),
										configInput.length());

								if (checkStringNullOrEmpty(startQuantity) || checkStringNullOrEmpty(endQuantity)) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if ((startIsParamStr.equals("true") && startQuantity.equals("0"))
										|| (endIsParamStr.equals("true") && endQuantity.equals("0"))) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if (startIsParamStr.equals("false") && endIsParamStr.equals("false")) {
									if (CommonUtil.getInt(startQuantity) > CommonUtil.getInt(endQuantity)) {

										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							} else {

								startQuantity = configInput.substring("start:".length(), configInput.indexOf(";end:"));
								endQuantity = configInput.substring(configInput.indexOf(";end:") + ";end:".length(),
										configInput.length());

								if (checkStringNullOrEmpty(startQuantity) || checkStringNullOrEmpty(endQuantity)) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if (configInput.indexOf(";startIsParameter:") < 0
										&& configInput.indexOf(";endIsParameter:") < 0) {
									if (CommonUtil.getInt(startQuantity) > CommonUtil.getInt(endQuantity)) {

										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							}

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
							startIsParam = false;
							endIsParam = false;

							String startBalance = "";
							startIsParamStr = "";
							String endBalance = "";
							endIsParamStr = "";
							priority = "0";

							if (configInput.indexOf(";startIsParameter:") != -1) {

								startBalance = configInput.substring("start:".length(),
										configInput.indexOf(";startIsParameter:"));
								startIsParamStr = configInput.substring(
										configInput.indexOf(";startIsParameter:") + ";startIsParameter:".length(),
										configInput.indexOf(";end:"));
								endBalance = configInput.substring(configInput.indexOf(";end:") + ";end:".length(),
										configInput.indexOf(";endIsParameter:"));
								endIsParamStr = configInput.substring(
										configInput.indexOf(";endIsParameter:") + ";endIsParameter:".length(),
										configInput.indexOf(";priority:"));
								priority = configInput.substring(
										configInput.indexOf(";priority:") + ";priority:".length(),
										configInput.length());

								if (checkStringNullOrEmpty(startBalance) || checkStringNullOrEmpty(endBalance)) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if ((startIsParamStr.equals("true") && startBalance.equals("0"))
										|| (endIsParamStr.equals("true") && endBalance.equals("0"))) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if (startIsParamStr.equals("false") && endIsParamStr.equals("false")) {
									if (CommonUtil.getInt(startBalance) > CommonUtil.getInt(endBalance)) {

										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							} else {
								startBalance = configInput.substring("start:".length(), configInput.indexOf(";end:"));
								endBalance = configInput.substring(configInput.indexOf(";end:") + ";end:".length(),
										configInput.length());

								if (checkStringNullOrEmpty(startBalance) || checkStringNullOrEmpty(endBalance)) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if (configInput.indexOf(";startIsParameter:") < 0
										&& configInput.indexOf(";endIsParameter:") < 0) {
									if (CommonUtil.getInt(startBalance) > CommonUtil.getInt(endBalance)) {

										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							}
							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:
							Long ZoneId = Long.valueOf(configInput.substring("zoneValueId:".length(),
									configInput.indexOf(";dataZoneType:")));
							// Integer ZoneType =
							// Integer.valueOf(configInput.substring(configInput.indexOf(";dataZoneType:")
							// +
							// ";dataZoneType:".length(),configInput.length()));

							if (ZoneId <= 0) {
								context.addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_WARN,
												this.readProperties("normalizer.validateError"),
												this.readProperties("normalizer.stringInputNull")));
								return false;
							}

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:
							startIsParam = false;
							endIsParam = false;

							String startAcmBalance = "";
							startIsParamStr = "";
							String endAcmBalance = "";
							endIsParamStr = "";
							priority = "0";

							if (configInput.indexOf(";startIsParameter:") != -1) {

								startAcmBalance = configInput.substring("start:".length(),
										configInput.indexOf(";startIsParameter:"));
								startIsParamStr = configInput.substring(
										configInput.indexOf(";startIsParameter:") + ";startIsParameter:".length(),
										configInput.indexOf(";end:"));
								endAcmBalance = configInput.substring(configInput.indexOf(";end:") + ";end:".length(),
										configInput.indexOf(";endIsParameter:"));
								endIsParamStr = configInput.substring(
										configInput.indexOf(";endIsParameter:") + ";endIsParameter:".length(),
										configInput.indexOf(";priority:"));
								priority = configInput.substring(
										configInput.indexOf(";priority:") + ";priority:".length(),
										configInput.length());

								if (checkStringNullOrEmpty(startAcmBalance) || checkStringNullOrEmpty(endAcmBalance)) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if ((startIsParamStr.equals("true") && startAcmBalance.equals("0"))
										|| (endIsParamStr.equals("true") && endAcmBalance.equals("0"))) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if (startIsParamStr.equals("false") && endIsParamStr.equals("false")) {
									if (CommonUtil.getInt(startAcmBalance) > CommonUtil.getInt(endAcmBalance)) {

										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							} else {

								startAcmBalance = configInput.substring("start:".length(),
										configInput.indexOf(";end:"));
								endAcmBalance = configInput.substring(configInput.indexOf(";end:") + ";end:".length(),
										configInput.length());

								if (checkStringNullOrEmpty(startAcmBalance) || checkStringNullOrEmpty(endAcmBalance)) {
									context.addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_WARN,
													this.readProperties("normalizer.validateError"),
													this.readProperties("normalizer.stringInputError")));
									return false;
								}

								if (configInput.indexOf(";startIsParameter:") < 0
										&& configInput.indexOf(";endIsParameter:") < 0) {
									if (CommonUtil.getInt(startAcmBalance) > CommonUtil.getInt(endAcmBalance)) {

										context.addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_WARN,
														this.readProperties("normalizer.validateError"),
														this.readProperties("normalizer.errorStartEndValue")));
										return false;
									}
								}
							}

							break;
						case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:
							Long paramId = 0l;
							try {
								paramId = Long.valueOf(configInput.substring("parameterId:".length(),
										configInput.indexOf(";comparisionType:")));
								Integer paramCompareType = Integer.valueOf(configInput.substring(
										configInput.indexOf(";comparisionType:") + ";comparisionType:".length(),
										configInput.indexOf(";priority:")));
								Integer paramPriority = Integer.valueOf(
										configInput.substring(configInput.indexOf(";priority:") + ";priority:".length(),
												configInput.length()));
							} catch (Exception e) {
								// TODO: handle exception
								getLogger().warn(e.getMessage(), e);
								paramId = 0l;
							}

							if (paramId < 1) {
								context.addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_WARN,
												this.readProperties("normalizer.validateError"),
												this.readProperties("normalizer.stringInputNull")));
								return false;
							}

							break;
						}
					} catch (Exception e) {
						getLogger().warn(e.getMessage(), e);
						result = false;
						break;
					}
				} else {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}
		
		if(!result) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorConfigInput")));
		}

		return result;
	}

	public Boolean checkNormValueFillAllToTableNormValueParam() {
		boolean result = true;

		if (this.listNormValue.size() > 0 && this.listTableNormValueParamForString.size() > 0) {
			for (NormValue nv : this.listNormValue) {
				boolean isSet = false;
				if (nv.getValueId() == this.selectedNormDefaultWithNormValue) {
					isSet = true;
				} else {
					for (tableNormValueParam tNVP : this.listTableNormValueParamForString) {
						if (nv.getValueId() == tNVP.getSelectedNormValue()) {
							isSet = true;
							break;
						}
					}
				}
				if (!isSet) {
					return false;
				}
			}
		} else {
			return false;
		}

		return result;
	}

	// build inputfield for save
	public String buildInputfieldForSave() {
		if (!checkStringNullOrEmpty(inputField1) && !checkStringNullOrEmpty(inputField2)) {
			return inputField1 + "/" + inputField2;
		} else if (!checkStringNullOrEmpty(inputField1)) {
			return inputField1;
		} else if (!checkStringNullOrEmpty(inputField2)) {
			return inputField2;
		}
		return "";
	}

	// load list all Normalizer
	public List<Normalizer> loadAllNormalizer() {
		listNormalizer = new ArrayList<Normalizer>();
		NormalizerDAO normalizerDAO = new NormalizerDAO();
		listNormalizer = normalizerDAO.findAll("");
		return listNormalizer;
	}

	// load list Normalizer by cate
	public List<Normalizer> loadNormalizerByCate(long cateID) {

		NormalizerDAO normalizerDAO = new NormalizerDAO();
		listNormalizerByCate = normalizerDAO.findNormalByConditions(cateID);
		this.selectedNormCate = cateID;
		// loadCategory();
		return listNormalizerByCate;
	}

	// load Edit from cat
	public void commandEditTable(Normalizer item) {
		if (item != null) {
			init();
			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			treeOfferBean.hideCategory();
			treeOfferBean.setContentTitle(super.readProperties("title.normalizer"));
			this.isEdit = true;
			setFormtype("normDetail");
			this.normalizer = item;
			setDataForEdit();
		}
	}

	// remove Normalizer from cat
	public void commandRemoveTable(Normalizer item) {
		if (item != null) {
			NormalizerDAO normDao = new NormalizerDAO();
			if (normDao.deleteNormalizer(item)) {
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.removeTreeNodeAll(item);
				if (this.listNormalizerByCate.size() > 0) {
					for (Normalizer e : this.listNormalizerByCate) {
						if (e.getNormalizerId() == item.getNormalizerId()) {
							item = e;
						}
					}
					this.listNormalizerByCate.remove(item);
				}

				Category category = catDao.get(item.getCategoryId());
				treeOfferBean.setSelectCategoryNode(category);
				this.showMessageINFO("common.delete", " Normalizer Success");
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						this.readProperties("cdrService.deleteError"), this.readProperties("cdrService.recordInUsed")));
			}
		}
	}

	// btn add new norm
	public void commandAddNew() {
		this.isEdit = true;
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		init();
		this.inputField1 = this.inputField2 = "";
		this.normalizer.setStartDate(new Date());
		setFormtype("normDetail");
		super.getTreeOfferBean().setContentTitle(super.readProperties("title.normalizer"));
	}

	public void commandAddNew(NodeSelectEvent nodeSelectEvent) {
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		this.isEdit = true;
		TreeOfferBean treeOfferBean = super.getTreeOfferBean();
		treeOfferBean.hideCategory();
		init();
		this.inputField1 = this.inputField2 = "";
		this.normalizer.setStartDate(new Date());
		setFormtype("normDetail");
	}

	// changeCate
	public void changeCategory() {
		if (this.normalizer != null && this.normalizer.getCategoryId() != null) {
			saveNormalizer();
			setFormtype("normDetail");
		}
	}

	public void redirectChangeCate() {
		// if(nodeSelectEvent != null){
		// if(nodeSelectEvent.getTreeNode().getData() instanceof Normalizer){
		// this.normalizer = (Normalizer)
		// nodeSelectEvent.getTreeNode().getData();
		// }
		// }
		// super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		// if(this.normalizer != null && this.normalizer.getCategoryId() !=
		// null){
		// this.formtype = "normChangeCate";
		// }
		this.openTreeCategoryDialog(TreeType.OFFER_NORMALIZER, "Normalizer", 0);
	}

	public void onDialogReturnCategory(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Category) {
			Category cate = (Category) obj;
			this.normalizer.setCategoryId(cate.getCategoryId());
			if (normDAO.moveCatNormalizer(this.normalizer)) {
				selectedNormCate = cate.getCategoryId();
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(this.normalizer, cate, null);
				this.showMessageINFO("common.moveCate", " Success ");
			}
		}
	}

	// remove normalizer from context
	public void commandRemoveNormContextMenu(NodeSelectEvent nodeSelectEvent) {
		Normalizer norm = (Normalizer) nodeSelectEvent.getTreeNode().getData();
		Object parent = nodeSelectEvent.getTreeNode().getParent().getData();

		if (parent instanceof Category) {
			commandRemoveTable(norm);
		} else if (parent instanceof DecisionTable) {
			DecisionTableDAO decisionTableDAO = new DecisionTableDAO();
			if (decisionTableDAO.removeColumnByNormalizerAndDT((DecisionTable) parent, norm)) {
				super.showNotification(FacesMessage.SEVERITY_INFO, "common.success", "");
				super.getTreeOfferBean().removeTreeNode(norm, (DecisionTable) parent);
			}
		}
	}

	public void commandCheckDependencies(NodeSelectEvent nodeSelectEvent) {
		if (nodeSelectEvent != null) {
			Normalizer item = (Normalizer) nodeSelectEvent.getTreeNode().getData();
			showDependencies(item.getNormalizerId(), Normalizer.class);
		}
	}

	// edit norm
	public void editContext(NodeSelectEvent nodeSelectEvent) {
		init();
		super.getTreeOfferBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent.getTreeNode().getData() instanceof Normalizer) {
			this.normName = ((Normalizer) nodeSelectEvent.getTreeNode().getData()).getNormalizerName();
			this.isEdit = true;
		}
	}

	// Clone Norm
	public void commandCloneNormContextMenu(NodeSelectEvent nodeSelectEvent) {
		Normalizer item = (Normalizer) nodeSelectEvent.getTreeNode().getData();
		if (item != null) {
			this.normalizer = normDAO.cloneNormalizer(item);
			if (this.normalizer != null) {
				loadNormalizerByCate(item.getCategoryId());
				this.normName = this.normalizer.getNormalizerName();
				catDao = new CategoryDAO();
				commandEditTable(this.normalizer);
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(normalizer, catDao.get(selectedNormCate), null);
				this.showMessageINFO("common.clone", " Normalizer ");
			}
		}
	}

	/**
	 * 
	 * @param isUp
	 *            true is up
	 */
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		Normalizer normalizer = (Normalizer) event.getTreeNode().getData();
		Object object = normDAO.upDownObjectInCatWithDomain(normalizer, "posIndex", isUp);
		if (object instanceof Normalizer) {
			Category category = catDao.get(normalizer.getCategoryId());
			Normalizer nextNormalizer = (Normalizer) object;

			TreeOfferBean treeOfferBean = super.getTreeOfferBean();
			if (isUp) {
				treeOfferBean.moveUpTreeNode(normalizer, category);
			} else {
				treeOfferBean.moveDownTreeNode(normalizer, category);
			}
			if (formtype == "normByCat" && category.getCategoryId() == nextNormalizer.getCategoryId()) {
				treeOfferBean.setNormalizerProperties(true, normalizer.getCategoryId(), nextNormalizer, false);
			}
			treeOfferBean.updateTreeNode(nextNormalizer, category, null);
			super.showNotificationSuccsess();
		}
	}

	// clone in cate

	public void commandCloneNormByCategory(Normalizer item) {
		if (item != null) {
			this.normalizer = normDAO.cloneNormalizer(item);
			if (this.normalizer != null) {
				this.normName = this.normalizer.getNormalizerName();
				loadNormalizerByCate(item.getCategoryId());
				commandEditTable(this.normalizer);
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.updateTreeNode(normalizer, catDao.get(selectedNormCate), null);
				this.showMessageINFO("common.clone", " Normalizer ");
			}
		}
	}

	public void addRowPath() {

	}

	// btn add child for inputfieldtable 1
	public void addChildIPF1() {
		if (this.listInputFieldTable1.size() > 0) {
			if (!checkStringNullOrEmpty(listInputFieldTable1.get(listInputFieldTable1.size() - 1).getPath())) {
				tmpInputFieldTable tmpLastItem = this.listInputFieldTable1.get(listInputFieldTable1.size() - 1);
				nestedObjectDAO = new NestedObjectDAO();
				List<NestedObject> lstObjChild = nestedObjectDAO
						.findObjectByObjParentID(tmpLastItem.getNestedObject().getObjClassId());
				List<SelectItem> selectItemObj = new ArrayList<>();
				if (lstObjChild.size() > 0) {
					for (NestedObject no : lstObjChild) {
						selectItemObj.add(new SelectItem(no.getObjId(), no.getObjName()));
					}
					this.listInputFieldTable1.add(new tmpInputFieldTable(tmpLastItem.getIndex() + 1,
							lstObjChild.get(0).getObjName(), "", lstObjChild.get(0), selectItemObj));
				} else {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.notHaveChild")));
				}
			}
		} else {

			this.listInputFieldTable1.add(new tmpInputFieldTable(1, "", "", new NestedObject(), null));
		}
		submitField01();
	}

	// btn remove child added in inputfieldtable 1
	public void removeChildIPF1(tmpInputFieldTable item) {
		if (this.listInputFieldTable1.size() > 0) {
			int indexItem = this.listInputFieldTable1.indexOf(item);
			if (indexItem < this.listInputFieldTable1.size() - 1) {
				for (int i = this.listInputFieldTable1.size() - 1; i >= indexItem; i--) {
					this.listInputFieldTable1.remove(i);
				}
			} else {
				this.listInputFieldTable1.remove(item);
			}
		}
		submitField01();
	}

	// btn add child for inputfieldtable 2
	public void addChildIPF2() {
		if (this.listInputFieldTable2.size() > 0) {
			if (!checkStringNullOrEmpty(listInputFieldTable2.get(listInputFieldTable2.size() - 1).getPath())) {
				tmpInputFieldTable tmpLastItem = this.listInputFieldTable2.get(listInputFieldTable2.size() - 1);
				nestedObjectDAO = new NestedObjectDAO();
				List<NestedObject> lstObjChild = nestedObjectDAO
						.findObjectByObjParentID(tmpLastItem.getNestedObject().getObjClassId());
				List<SelectItem> selectItemObj = new ArrayList<>();
				if (lstObjChild.size() > 0) {
					for (NestedObject no : lstObjChild) {
						selectItemObj.add(new SelectItem(no.getObjId(), no.getObjName()));
					}
					this.listInputFieldTable2.add(new tmpInputFieldTable(tmpLastItem.getIndex() + 1,
							lstObjChild.get(0).getObjName(), "", lstObjChild.get(0), selectItemObj));
				} else {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.notHaveChild")));
				}
			}
		} else {

			this.listInputFieldTable2.add(new tmpInputFieldTable(1, "", "", new NestedObject(), null));
		}
		submitField02();
	}

	// btn remove child added in inputfieldtable 2
	public void removeChildIPF2(tmpInputFieldTable item) {
		if (this.listInputFieldTable2.size() > 0) {
			int indexItem = this.listInputFieldTable2.indexOf(item);
			if (indexItem < this.listInputFieldTable2.size() - 1) {
				for (int i = this.listInputFieldTable2.size() - 1; i >= indexItem; i--) {
					this.listInputFieldTable2.remove(i);
				}
			} else {
				this.listInputFieldTable2.remove(item);
			}
		}
		submitField02();
	}

	// add current data on edit to tmp inputfield
	public void fillDataToTmpInputField(tmpInputFieldTable item) {
		if (item != null) {
			this.tmpInputField = item;
			this.filterConditions = "";
			this.preFunctions = "";
			if (this.listFilterConditionTable != null)
				this.listFilterConditionTable.clear();
			if (this.listPreFunctionTable != null)
				this.listPreFunctionTable.clear();
			fillDataForFilterObject(item.nestedObject);
			if (!checkStringNullOrEmpty(item.filter)) {
				reloadDataForFilterPanel(item);
			}
		}
		submitField01();
		submitField02();
	}

	public void reloadDataForFilterPanel(tmpInputFieldTable item) {
		String filterPanelStr = item.getFilter().trim();
		String[] arrConditionFilter = filterPanelStr.split(";");
		for (int i = 0; i < arrConditionFilter.length; i++) {
			if (checkIsFilterPreFunction(arrConditionFilter[i])) {
				this.preFunctions = arrConditionFilter[i];
				String[] arrPreFunction = arrConditionFilter[i].split(":");
				this.listPreFunctionTable = new ArrayList<>();
				for (int j = 0; j < arrPreFunction.length; j++) {

					String functionName = arrPreFunction[j].substring(0, arrPreFunction[j].indexOf("("));
					Iterator<Long> it = mapPreFunction.keySet().iterator();
					while (it.hasNext()) {

						Long id = it.next();
						PreFunction function = mapPreFunction.get(id);
						String functionName1 = function.getFunctionName();
						functionName1 = functionName1.substring(0, functionName1.indexOf("("));
						if (functionName.equals(functionName1)) {

							int index = 1;
							if (j > 0) {
								index = this.listPreFunctionTable.get(j - 1).getIndex() + 1;
							}

							String paramString1 = "";
							String paramString2 = "";
							String paramString3 = "";
							String paramString4 = "";
							String paramString5 = "";

							String paramStringValue = arrPreFunction[j].substring(arrPreFunction[j].indexOf("(") + 1,
									arrPreFunction[j].indexOf(")"));
							String[] paramString = paramStringValue.split(",");

							if (paramString.length <= 1) {

								paramString1 = paramString[0];
							} else if (paramString.length == 2) {

								paramString1 = paramString[0];
								paramString2 = paramString[1];
							} else if (paramString.length == 3) {

								paramString1 = paramString[0];
								paramString2 = paramString[1];
								paramString3 = paramString[2];
							} else if (paramString.length == 4) {

								paramString1 = paramString[0];
								paramString2 = paramString[1];
								paramString3 = paramString[2];
								paramString4 = paramString[3];
							} else if (paramString.length == 5) {

								paramString1 = paramString[0];
								paramString2 = paramString[1];
								paramString3 = paramString[2];
								paramString4 = paramString[3];
								paramString5 = paramString[4];
							}

							this.listPreFunctionTable.add(new preFunctionTable(index, function.getPreFunctionId(),
									paramString1, paramString2, paramString3, paramString4, paramString5));
							break;
						}
					}

					// switch(arrPreFunction[j].substring(0,
					// arrPreFunction[j].indexOf("("))){
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.PREFIX_CLASS_NAME:
					// int index = 1;
					// int functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.PREFIX;
					// String paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// String paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX;
					// paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING;
					// paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(","));
					// paramString2 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf(",")
					// + 1, arrPreFunction[j].indexOf(")"));
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE;
					// paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE;
					// paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					//
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID;
					// paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS;
					// String paramStringValue =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// String[] paramString = paramStringValue.split(",");
					// String paramString3 = "";
					// if(paramString.length == 3){
					// paramString1 = paramString[0];
					// paramString2 = paramString[1];
					// paramString3 = paramString[2];
					// } else {
					// paramString1 = paramString2 = paramString3 = "";
					// }
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, paramString3, "",
					// ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR;
					// paramString1 = "";
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// case
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE_CLASS_NAME:
					// index = 1;
					// functionID =
					// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE;
					// paramString1 =
					// arrPreFunction[j].substring(arrPreFunction[j].indexOf("(")
					// + 1, arrPreFunction[j].indexOf(")"));
					// paramString2 = "";
					// if(j > 0){
					// index = this.listPreFunctionTable.get(j -1).getIndex() +
					// 1;
					// }
					// this.listPreFunctionTable.add(new preFunctionTable(index,
					// functionID, paramString1, paramString2, "", "", ""));
					// break;
					// default:
					// break;
					// }
				}
			} else {
				this.filterConditions = arrConditionFilter[i];
				this.listFilterConditionTable = new ArrayList<>();
				if (!checkStringNullOrEmpty(this.filterConditions)) {
					String[] arrCondition = arrConditionFilter[i].split("&");
					for (int j = 0; j < arrCondition.length; j++) {
						if (this.listNestedObject.size() > 0) {
							for (SelectItem no : listNestedObject) {
								if (no.getLabel().equals(arrCondition[j].substring(0, arrCondition[j].indexOf("=")))) {
									int index = 1;
									if (j > 0) {
										index = this.listFilterConditionTable.get(j - 1).getIndex() + 1;
									}
									String filterValue = arrCondition[j].substring(arrCondition[j].indexOf("=") + 1,
											arrCondition[j].length());
									this.listFilterConditionTable
											.add(new filterConditionTable(index, no.getLabel(), filterValue));
								}
							}
						}
					}
				}
			}
		}
	}

	public Boolean checkIsFilterPreFunction(String str) {

		Iterator<Long> it = mapPreFunction.keySet().iterator();
		while (it.hasNext()) {

			Long id = it.next();
			PreFunction function = mapPreFunction.get(id);
			String functionName = function.getFunctionName();
			functionName = functionName.substring(0, functionName.indexOf("("));
			if (str.indexOf(functionName) > -1) {
				return true;
			}
		}

		return false;

		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.PREFIX_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// if(str.indexOf(com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE_CLASS_NAME)
		// > -1){
		// return true;
		// }
		//
		// return false;
	}

	// tmp Table for InputField
	public class tmpInputFieldTable {
		public NestedObject nestedObject;
		public Integer index;
		public String path;
		public String filter;
		public List<SelectItem> listObjectForChoose;

		public tmpInputFieldTable() {

		}

		public tmpInputFieldTable(Integer index, String path, String filter, NestedObject nestedObject,
				List<SelectItem> listObjectForChoose) {
			super();
			this.index = index;
			this.path = path;
			this.filter = filter;
			this.nestedObject = nestedObject;
			this.listObjectForChoose = listObjectForChoose;
		}

		public NestedObject getNestedObject() {
			return nestedObject;
		}

		public void setNestedObject(NestedObject nestedObject) {
			this.nestedObject = nestedObject;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public List<SelectItem> getListObjectForChoose() {
			return listObjectForChoose;
		}

		public void setListObjectForChoose(List<SelectItem> listObjectForChoose) {
			this.listObjectForChoose = listObjectForChoose;
		}

	}

	// btn apply all filter
	public void applyAllFilter() {

		// validate
		boolean fillFullParam = true;
		for (preFunctionTable entity : this.listPreFunctionTable) {

			PreFunction function = mapPreFunction.get(entity.getFunctionID());
			if (function.getNumberParam() == 1) {
				if (checkStringNullOrEmpty(entity.paramString1)) {
					fillFullParam = false;
					break;
				}
			} else if (function.getNumberParam() == 2) {
				if (checkStringNullOrEmpty(entity.paramString1) || checkStringNullOrEmpty(entity.paramString2)) {
					fillFullParam = false;
					break;
				}
			} else if (function.getNumberParam() == 3) {
				if (checkStringNullOrEmpty(entity.paramString1) || checkStringNullOrEmpty(entity.paramString2)
						|| checkStringNullOrEmpty(entity.paramString3)) {
					fillFullParam = false;
					break;
				}
			} else if (function.getNumberParam() == 4) {
				if (checkStringNullOrEmpty(entity.paramString1) || checkStringNullOrEmpty(entity.paramString2)
						|| checkStringNullOrEmpty(entity.paramString3) || checkStringNullOrEmpty(entity.paramString4)) {
					fillFullParam = false;
					break;
				}
			} else if (function.getNumberParam() == 5) {
				if (checkStringNullOrEmpty(entity.paramString1) || checkStringNullOrEmpty(entity.paramString2)
						|| checkStringNullOrEmpty(entity.paramString3) || checkStringNullOrEmpty(entity.paramString4)
						|| checkStringNullOrEmpty(entity.paramString5)) {
					fillFullParam = false;
					break;
				}
			}
		}

		if (!fillFullParam) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateFunctionParamEmpty")));
			return;
		}

		this.finalFilter = "";
		if (!checkStringNullOrEmpty(this.preFunctions) && !checkStringNullOrEmpty(this.filterConditions)) {
			this.finalFilter = this.filterConditions + ";" + this.preFunctions;
		} else if (checkStringNullOrEmpty(this.preFunctions) && !checkStringNullOrEmpty(this.filterConditions)) {
			this.finalFilter = this.filterConditions;
		} else if (!checkStringNullOrEmpty(this.preFunctions) && checkStringNullOrEmpty(this.filterConditions)) {
			this.finalFilter = ";" + this.preFunctions;
		}
		this.tmpInputField.setFilter(this.finalFilter);
		if (this.listInputFieldTable1.indexOf(this.tmpInputField) >= 0) {
			this.listInputFieldTable1.set(this.listInputFieldTable1.indexOf(this.tmpInputField), this.tmpInputField);
		} else if (this.listInputFieldTable2.indexOf(this.tmpInputField) >= 0) {
			this.listInputFieldTable2.set(this.listInputFieldTable2.indexOf(this.tmpInputField), this.tmpInputField);
		}
		this.listFilterConditionTable = new ArrayList<>();
		this.listPreFunctionTable = new ArrayList<>();
		this.filterConditions = this.preFunctions = "";
		submitField01();
		submitField02();

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.ui-dialog-titlebar-close').click();");
	}

	// btn add child for FilterCondition
	public void addChildFilterCondition() {
		if (this.listNestedObject.size() > 0) {
			if (this.listFilterConditionTable.size() > 0) {
				int lastIndex = this.listFilterConditionTable.get(listFilterConditionTable.size() - 1).getIndex();
				this.listFilterConditionTable.add(new filterConditionTable(lastIndex + 1, "", "0"));
			} else {
				this.listFilterConditionTable.add(new filterConditionTable(1, "", "0"));
			}
		}
		createFilterConditionsField();
	}

	// btn remove child added in FilterCondition
	public void removeChildFilterCondition(filterConditionTable item) {
		if (this.listFilterConditionTable.size() > 0) {
			this.listFilterConditionTable.remove(item);
			createFilterConditionsField();
		}
	}

	// Create value for filterCondition
	public void createFilterConditionsField() {
		this.filterConditions = "";
		if (this.listFilterConditionTable.size() > 0) {
			for (filterConditionTable item : this.listFilterConditionTable) {
				if (item.getFilterFieldName().isEmpty()) {
					item.setFilterFieldName(listNestedObject.get(0).getValue().toString());
				}

				String filterValue = "";
				if (checkStringNullOrEmpty(item.getFilterValue().toString().trim())) {
					filterValue = "0";
				} else {
					filterValue = item.getFilterValue().toString().trim();
				}
				if (this.filterConditions.equals("")) {

					this.filterConditions += (item.getFilterFieldName() + "=" + item.getFilterValue().toString());
				} else {
					this.filterConditions += ("&" + item.getFilterFieldName() + "=" + item.getFilterValue().toString());
				}
			}
		}
	}

	// Filter Condition table
	public class filterConditionTable {
		public Integer index;
		public String filterFieldName;
		public String filterValue;

		public filterConditionTable() {
			super();
		}

		public filterConditionTable(Integer index, String filterFieldName, String filterValue) {
			super();
			this.index = index;
			this.filterFieldName = filterFieldName;
			this.filterValue = filterValue;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public String getFilterFieldName() {
			return filterFieldName;
		}

		public void setFilterFieldName(String filterFieldName) {
			this.filterFieldName = filterFieldName;
		}

		public String getFilterValue() {
			return filterValue;
		}

		public void setFilterValue(String filterValue) {
			this.filterValue = filterValue;
		}

	}

	// btn add child for preFunctionTable
	public void addChildPreFunction() {
		if (this.listPreFunctionTable.size() > 0) {
			int lastIndex = this.listPreFunctionTable.get(listPreFunctionTable.size() - 1).getIndex();
			this.listPreFunctionTable.add(new preFunctionTable(lastIndex + 1, 1L, "", "", "", "", ""));
		} else {
			this.listPreFunctionTable.add(new preFunctionTable(1, 1L, "", "", "", "", ""));
		}

		createPreFunctionField();
	}

	// btn remove child added in preFunctionTable
	public void removeChildPreFunction(preFunctionTable item) {
		if (this.listPreFunctionTable.size() > 0) {
			this.listPreFunctionTable.remove(item);
		}

		createPreFunctionField();
	}

	// Pre-Function table
	public class preFunctionTable {
		public Integer index;
		public Long functionID;
		public Integer numberParam;
		public String paramString1;
		public String paramString2;
		public String paramString3;
		public String paramString4;
		public String paramString5;

		public preFunctionTable() {
			super();
		}

		public preFunctionTable(Integer index, Long functionID, String paramString1, String paramString2,
				String paramString3, String paramString4, String paramString5) {
			super();
			this.index = index;
			this.functionID = functionID;
			this.paramString1 = paramString1;
			this.paramString2 = paramString2;
			this.paramString3 = paramString3;
			this.paramString4 = paramString4;
			this.paramString5 = paramString5;
		}

		public Integer getNumberParam() {
			return numberParam;
		}

		public void setNumberParam(Integer numberParam) {
			this.numberParam = numberParam;
		}

		public String getParamString4() {
			return paramString4;
		}

		public void setParamString4(String paramString4) {
			this.paramString4 = paramString4;
		}

		public String getParamString5() {
			return paramString5;
		}

		public void setParamString5(String paramString5) {
			this.paramString5 = paramString5;
		}

		public String getParamString3() {
			return paramString3;
		}

		public void setParamString3(String paramString3) {
			this.paramString3 = paramString3;
		}

		public Long getFunctionID() {
			return functionID;
		}

		public void setFunctionID(Long functionID) {
			this.functionID = functionID;
		}

		public String getParamString2() {
			return paramString2;
		}

		public void setParamString2(String paramString2) {
			this.paramString2 = paramString2;
		}

		public String getParamString1() {
			return paramString1;
		}

		public void setParamString1(String paramString1) {
			this.paramString1 = paramString1;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

	}

	public void createPreFunctionField() {

		this.preFunctions = "";
		for (preFunctionTable entity : this.listPreFunctionTable) {
			PreFunction function = mapPreFunction.get(entity.getFunctionID());
			if (function == null)
				continue;

			String functionName = function.getFunctionName();
			functionName = functionName.substring(0, functionName.indexOf("("));

			if (function.getNumberParam() <= 0) {

				if ("".equals(preFunctions)) {
					preFunctions += functionName + "()";
				} else {
					preFunctions += ":" + functionName + "()";
				}
			} else if (function.getNumberParam() == 1) {

				if ("".equals(preFunctions)) {
					preFunctions += functionName + "(" + entity.paramString1 + ")";
				} else {
					preFunctions += ":" + functionName + "(" + entity.paramString1 + ")";
				}
			} else if (function.getNumberParam() == 2) {

				if ("".equals(preFunctions)) {
					preFunctions += functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ")";
				} else {
					preFunctions += ":" + functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ")";
				}
			} else if (function.getNumberParam() == 3) {

				if ("".equals(preFunctions)) {
					preFunctions += functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ","
							+ entity.paramString3 + ")";
				} else {
					preFunctions += ":" + functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ","
							+ entity.paramString3 + ")";
				}
			} else if (function.getNumberParam() == 4) {

				if ("".equals(preFunctions)) {
					preFunctions += functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ","
							+ entity.paramString3 + "," + entity.paramString4 + ")";
				} else {
					preFunctions += ":" + functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ","
							+ entity.paramString3 + "," + entity.paramString4 + ")";
				}
			} else if (function.getNumberParam() == 5) {

				if ("".equals(preFunctions)) {
					preFunctions += functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ","
							+ entity.paramString3 + "," + entity.paramString4 + "," + entity.paramString5 + ")";
				} else {
					preFunctions += ":" + functionName + "(" + entity.paramString1 + "," + entity.paramString2 + ","
							+ entity.paramString3 + "," + entity.paramString4 + "," + entity.paramString5 + ")";
				}
			}
		}

		// this.preFunctions = "";
		// if (this.listPreFunctionTable.size() > 0) {
		// for (preFunctionTable entity : this.listPreFunctionTable) {
		// int type = checkParamRender(entity.getFunctionID());
		// if (type == 0) {
		// switch (entity.getFunctionID()) {
		// case com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN_NAME);
		// }
		// break;
		// case com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST_NAME;
		// } else {
		// this.preFunctions += (":" +
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST_NAME;
		// } else {
		// this.preFunctions += (":" +
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH_NAME);
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR_NAME;
		// } else {
		// this.preFunctions += (":"
		// +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR_NAME);
		// }
		// break;
		// default:
		// break;
		// }
		// } else if (type == 1) {
		// switch (entity.getFunctionID()) {
		// case com.viettel.ocs.constant.Normalizer.preFunction.PREFIX:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions += "prefix(" + entity.getParamString1() + ")";
		// } else {
		// this.preFunctions += (":" + "prefix(" + entity.getParamString1() +
		// ")");
		// }
		// break;
		// case com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions += "suffix(" + entity.getParamString1() + ")";
		// } else {
		// this.preFunctions += (":" + "suffix(" + entity.getParamString1() +
		// ")");
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions += "containsStateType(" + entity.getParamString1()
		// + ")";
		// } else {
		// this.preFunctions += (":" + "containsStateType(" +
		// entity.getParamString1() + ")");
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions += "getAcmBalByBillingCycle(" +
		// entity.getParamString1() + ")";
		// } else {
		// this.preFunctions += (":" + "getAcmBalByBillingCycle(" +
		// entity.getParamString1() + ")");
		// }
		// break;
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_CLASS_NAME
		// + "(" + entity.getParamString1() + ")";
		// } else {
		// this.preFunctions += (":" +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_CLASS_NAME
		// + "(" + entity.getParamString1() + ")");
		// }
		// break;
		// case com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE_CLASS_NAME +
		// "(" + entity.getParamString1() + ")";
		// } else {
		// this.preFunctions += (":" +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE_CLASS_NAME +
		// "(" + entity.getParamString1() + ")");
		// }
		// break;
		// default:
		// break;
		// }
		// } else if (type == 2) {
		// switch (entity.getFunctionID()) {
		// case com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions += "subString(" + entity.paramString1 + "," +
		// entity.paramString2 + ")";
		// } else {
		// this.preFunctions += (":" + "subString(" + entity.paramString1 + ","
		// + entity.paramString2
		// + ")");
		// }
		// break;
		// default:
		// break;
		// }
		// } else if (type == 3){
		// switch (entity.getFunctionID()) {
		// case
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS:
		// if (this.preFunctions.equals("")) {
		// this.preFunctions +=
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_CLASS_NAME
		// + "(" + entity.paramString1 + "," + entity.paramString2 + "," +
		// entity.paramString3 + ")";
		// } else {
		// this.preFunctions += (":" +
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_CLASS_NAME
		// + "(" + entity.paramString1 + "," + entity.paramString2 + "," +
		// entity.paramString3 + ")");
		// }
		// break;
		// default:
		// break;
		// }
		// }
		// }
		// }
	}

	public int getNumberParam(long functionID) {

		if (mapPreFunction.get(functionID) != null) {
			return mapPreFunction.get(functionID).getNumberParam();
		}

		return 0;
	}

	public Integer checkParamRender(Integer functionID) {
		/*
		 * result = 0 khong hien thi result = 1 hien thi 1 field result = 2 hien
		 * thi 2 field
		 */
		int result = 0;
		// if ((functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR)) {
		// result = 0;
		// } else if ((functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.PREFIX)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID)
		// || (functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE)) {
		// result = 1;
		// } else if ((functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING)) {
		// result = 2;
		// } else if ((functionID ==
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS))
		// {
		// result = 3;
		// }
		return result;
	}

	public Boolean checkStringNullOrEmpty(String str) {

		if (str == null) {
			return true;
		}

		if (str.isEmpty()) {
			return true;
		}

		return false;
	}

	public void showDialogNestedObject(tmpInputFieldTable item) {
		// Map<String, Object> options = new HashMap<String, Object>();
		// options.put("modal", true);
		// options.put("widgetVar", "dlgTree");
		// options.put("width", 500);
		// options.put("height", 450);
		// options.put("resizable", false);
		// options.put("contentWidth", "100%");
		// options.put("contentHeight", "100%");
		// Map<String, List<String>> mapPara = new HashMap<String,
		// List<String>>();
		// List<String> lstPara = new ArrayList<String>();
		// lstPara.add(TreeType.OFFER_OBJECT + ";" +
		// CategoryType.OFF_OBJECT_NAME);
		// mapPara.put("treeType", lstPara);
		// RequestContext.getCurrentInstance().openDialog("/pages/treecommondialog",
		// options, mapPara);

		super.openTreeOfferDialog(TreeType.OFFER_OBJECT, CategoryType.OFF_OBJECT_NAME, 0, false, null);
		this.tmpInputField = item;

	}

	// SelectEvent import org.primefaces.event.SelectEvent
	public void onDialogNestedObjectReturn(SelectEvent event) {
		Object obj = event.getObject();
		if (obj.getClass().equals(NestedObject.class)) {
			// this.itemRateTableOnCell = (RateTable) obj;
			// listBlockRateTableMap.get(listBlockRateTableMap.indexOf(blockRateTableMap))
			// .setRateTableId(itemRateTableOnCell.getRateTableId());
		}
	}

	public void createSpecialFieldSM() {
		String startChar = "";
		String endChar = "";

		if (this.startCharactor == null || this.endCharactor == null) {
			if (this.startCharactor == null || this.startCharactor < 0) {
				startChar = "0";
			} else {
				startChar = this.startCharactor.toString();
			}

			if (this.endCharactor == null) {
				endChar = "Interger.MaxValue";
			} else {
				endChar = this.endCharactor.toString();
			}
		} else if (this.startCharactor != null && this.endCharactor != null) {
			if (this.startCharactor > this.endCharactor) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorStartEndCharacter")));
				this.normalizer.setSpecialFileds("");
			} else {
				startChar = this.startCharactor.toString();
				endChar = this.endCharactor.toString();
			}
		}

		if (startChar.isEmpty() || endChar.isEmpty()) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					this.readProperties("normalizer.validateError"), this.readProperties("normalizer.parseDataError")));
			this.normalizer.setSpecialFileds("");
		} else {
			this.normalizer.setSpecialFileds("startCharacter:" + startChar + ";" + "endCharacter:" + endChar);
		}
	}

	// ****************************** Add and remove ListTableNormValueParam for
	// String
	// **********************************************************************
	public void addChildForListTableNVPstring() {
		if (this.listNormValue.size() > 0) {
			
			NormValue normDefault = new NormValue();
			if (this.selectedNormDefaultWithNormValue != null) {
				for (NormValue nv : this.listNormValue) {
					if (nv.getValueId() == this.selectedNormDefaultWithNormValue) {
						normDefault = nv;
					}
				}
			} else {
				normDefault = this.listNormValue.get(0);
			}
			
			if (this.listTableNormValueParamForString.size() > 0) {
				tableNormValueParam lastItem = this.listTableNormValueParamForString
						.get(this.listTableNormValueParamForString.size() - 1);
				if (lastItem.getNormParam() != null && lastItem.getNormValue() != null) {
					this.listTableNormValueParamForString.add(
							new tableNormValueParam(normDefault, new NormParam("", normDefault.getNormValueIndex()),
									com.viettel.ocs.constant.Normalizer.StringNormCompareType.EXACT, "",
									this.selectedNormDefaultWithNormValue, "", "", 1, 1, null, 0, 0,
									(long) 0, (long) 0, (long) 0, (long) 0, (long) 1, false, false));
					if (this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER
							&& this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER
							&& this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER) {
						onChangeNormValueInTableNVP(this.listTableNormValueParamForString
								.get(this.listTableNormValueParamForString.size() - 1));
					}
				}
			} else {
				this.listTableNormValueParamForString
						.add(new tableNormValueParam(normDefault, new NormParam("", normDefault.getNormValueIndex()),
								com.viettel.ocs.constant.Normalizer.StringNormCompareType.EXACT, "",
								this.selectedNormDefaultWithNormValue, "", "", 1, 1, null, 0, 0,
								(long) 0, (long) 0, (long) 0, (long) 0, (long) 1, false, false));
				if (this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER
						&& this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER
						&& this.selectedNormType != com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER) {
					onChangeNormValueInTableNVP(this.listTableNormValueParamForString.get(0));
				}
			}
		} else {

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "",
					this.readProperties("normalizer.warnAddNormValue")));
		}
	}

	public String loadNameOfSelectedParam(Long paramID) {
		String name = "Click here to set parameter";
		Parameter param = parameterDAO.get(paramID);
		if (param != null) {
			name = param.getParameterName();
		}

		return name;
	}

	public String loadNameOfSelectedParam(tableNormValueParam item) {
		String name = "Click here to set parameter";

		if (item.getStartIsParam()) {
			Long paramId = 0l;
			try {
				paramId = Long.valueOf(item.getSelectedParameterID());
			} catch (Exception e) {
				getLogger().warn(e.getMessage(), e);
				paramId = 0l;
			}
			
			Parameter param = parameterDAO.get(paramId);
			if (param != null) {
				name = param.getParameterName();
			}
		} else {
			name = item.getSelectedParameterID() == null? "" : item.getSelectedParameterID().toString();
		}

		return name;
	}

	public String loadNameOfSelectedParam(tableNormValueParam item, boolean isStart) {
		String name = "";
		if (isStart) {
			if (item.getStartIsParam()) {
				Long paramId = 0l;
				try {
					paramId = Long.valueOf(item.getStartValue());
				} catch (Exception e) {
					getLogger().warn(e.getMessage(), e);
					paramId = 0l;
				}
				
				Parameter param = parameterDAO.get(paramId);
				if (param != null) {
					name = param.getParameterName();
				}
			} else {
				name = item.getStartValue();
			}

		} else {
			if (item.getEndIsParam()) {
				Long paramId = 0l;
				try {
					paramId = Long.valueOf(item.getEndValue());
				} catch (Exception e) {
					getLogger().warn(e.getMessage(), e);
					paramId = 0l;
				}

				Parameter param = parameterDAO.get(paramId);
				if (param != null) {
					name = param.getParameterName();
				}
			} else {
				name = item.getEndValue();
			}
		}

		return name;
	}

	public void removeChildForListTableNVPstring(tableNormValueParam item) {
		if (item != null) {
			if (item.getNormParam() != null && item.getNormParam().getNormParamId() > 0) {
				this.listNormParamToDelete.add(item.getNormParam());
			}
			this.listTableNormValueParamForString.remove(item);
			if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER) {
				createOrUpdateTableTime();
			}
		}
	}

	public void addTimeParam(tableNormValueParam item) {

		addChildForListTableNVPstring();
		tableNormValueParam addItem = listTableNormValueParamForString.get(listTableNormValueParamForString.size() - 1);
		int currentIdx = listTableNormValueParamForString.indexOf(item);
		listTableNormValueParamForString.remove(addItem);
		listTableNormValueParamForString.add(currentIdx + 1, addItem);
	}

	public void moveUpTimeParam(tableNormValueParam item) {

		int currentIdx = listTableNormValueParamForString.indexOf(item);
		if (currentIdx > 0) {

			listTableNormValueParamForString.remove(currentIdx);
			listTableNormValueParamForString.add(currentIdx - 1, item);
		}
	}

	public void moveDownTimeParam(tableNormValueParam item) {

		int currentIdx = listTableNormValueParamForString.indexOf(item);
		if (currentIdx < listTableNormValueParamForString.size() - 1) {

			listTableNormValueParamForString.remove(currentIdx);
			listTableNormValueParamForString.add(currentIdx + 1, item);
		}
	}

	// set data for normvalue onchange select one menu in tableNormValueParam
	public void onChangeNormValueInTableNVP(tableNormValueParam item) {
		if (item != null) {
			if (item.normValue != null) {
				if (this.listNormValue.size() > 0) {
					Integer indexOfItem = this.listTableNormValueParamForString.indexOf(item);
					for (NormValue no : this.listNormValue) {
						if (no.getValueId() == item.selectedNormValue) {
							item.normValue = no;
							item.normParam.setNormParamIndex(no.getNormValueIndex());
							item.normParam.setConfigInput(createConfigInput(item));
							this.listTableNormValueParamForString.set(indexOfItem, item);
							if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER) {
								createOrUpdateTableTime();
							}
						}
					}
				}
			}
		}
	}

	// Create ConfigInput for String type
	public String createConfigInput(tableNormValueParam item) {
		String configInput = "";
		String paramValueStr = item.getParamValueStr();
		switch (this.selectedNormType) {

		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:
			if (checkStringNullOrEmpty(paramValueStr)) {
				paramValueStr = "";
			}
			configInput = "inputStr:" + paramValueStr + ";type:" + item.getSelectedCompare().toString();
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:
			String isParam = ";isUsingParameter:false";
			if (item.getStartIsParam()) {
				isParam = ";isUsingParameter:true";
			}
			configInput = "parameterId:" + item.getSelectedParameterID() + isParam + ";comparisionType:"
					+ item.getSelectedCompare().toString() + ";priority:" + item.getPriority();
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
			configInput = "day:" + item.getSelectedDay() + ";hour:" + item.getSelectedHour() + ";min:"
					+ item.getSelectedMinute() + ";sec:" + item.getSelectedSecond();
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:

			if (item.getSelectedStartValue() == NORM_DATE_PARAM_NONE) {
				String startValue = DatetimeUtil.dateToString(item.startValueDate, NORM_DATE_FORMAT);
				item.setStartValue(startValue);
			} else if (item.getSelectedStartValue() == NORM_DATE_PARAM_CURRENT_TIME) {
				item.setStartValue("none");
			} else if (item.getSelectedStartValue() == NORM_DATE_PARAM_DELTA) {
				String startValue = String.valueOf(item.startValueNumber);
				item.setStartValue(startValue);
			}

			if (item.getSelectedEndValue() == NORM_DATE_PARAM_NONE) {
				String endValue = DatetimeUtil.dateToString(item.endValueDate, NORM_DATE_FORMAT);
				item.setEndValue(endValue);
			} else if (item.getSelectedEndValue() == NORM_DATE_PARAM_CURRENT_TIME) {
				item.setEndValue("none");
			} else if (item.getSelectedEndValue() == NORM_DATE_PARAM_DELTA) {
				String endValue = String.valueOf(item.endValueNumber);
				item.setEndValue(endValue);
			}

			configInput = "startValue:" + item.getStartValue() + ";startType:" + item.getSelectedStartValue()
					+ ";endValue:" + item.getEndValue() + ";endType:" + item.getSelectedEndValue();
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:
			String startVal = "";
			String endVal = "";
			String startIsParam = "true";
			String endIsParam = "true";

			if (!item.getStartIsParam()) {
				startIsParam = "false";
			}

			if (!item.getEndIsParam()) {
				endIsParam = "false";
			}

			if (checkStringNullOrEmpty(item.getStartValue())) {
				startVal = "Double.MinValue";
			} else {
				startVal = item.getStartValue();
			}

			if (checkStringNullOrEmpty(item.getEndValue())) {
				startVal = "Double.MaxValue";
			} else {
				endVal = item.getEndValue();
			}

			configInput = "start:" + startVal + ";startIsParameter:" + startIsParam + ";end:" + endVal
					+ ";endIsParameter:" + endIsParam + ";priority:" + item.getPriority();

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:
			startVal = "";
			endVal = "";
			startIsParam = "true";
			endIsParam = "true";

			if (!item.getStartIsParam()) {
				startIsParam = "false";
			}

			if (!item.getEndIsParam()) {
				endIsParam = "false";
			}

			if (checkStringNullOrEmpty(item.getStartValue())) {
				startVal = "Double.MinValue";
			} else {
				startVal = item.getStartValue();
			}

			if (checkStringNullOrEmpty(item.getEndValue())) {
				startVal = "Double.MaxValue";
			} else {
				endVal = item.getEndValue();
			}

			configInput = "start:" + startVal + ";startIsParameter:" + startIsParam + ";end:" + endVal
					+ ";endIsParameter:" + endIsParam + ";priority:" + item.getPriority();

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:
			configInput = "zoneValueId:" + item.getSelectedZoneId() + ";dataZoneType:" + item.getSelectedZoneType();
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:
			startVal = "";
			endVal = "";
			startIsParam = "true";
			endIsParam = "true";

			if (!item.getStartIsParam()) {
				startIsParam = "false";
			}

			if (!item.getEndIsParam()) {
				endIsParam = "false";
			}

			if (checkStringNullOrEmpty(item.getStartValue())) {
				startVal = "Double.MinValue";
			} else {
				startVal = item.getStartValue();
			}

			if (checkStringNullOrEmpty(item.getEndValue())) {
				startVal = "Double.MaxValue";
			} else {
				endVal = item.getEndValue();
			}

			configInput = "start:" + startVal + ";startIsParameter:" + startIsParam + ";end:" + endVal
					+ ";endIsParameter:" + endIsParam + ";priority:" + item.getPriority();

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:
			configInput = "parameterId:" + item.getSelectedParameterID() + ";comparisionType:"
					+ item.getSelectedCompare().toString() + ";priority:" + item.getPriority();
			break;

		default:
			break;
		}
		return configInput;
	}

	// validate config input in tableNormValueParam
	public void validateChildForListTableNVPstring() {
		if (this.listTableNormValueParamForString.size() > 0) {
			for (tableNormValueParam nvp : this.listTableNormValueParamForString) {
				if (nvp.getNormParam().getConfigInput().length() < 17) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.configInputNotValid") + " "
											+ nvp.getNormParam().getConfigInput()));
				}
			}
		}
	}

	// load data for select item norm value
	public void loadDataForSelectNormValue(List<NormValue> lstNormValue) {
		this.listSelectNormValue = new ArrayList<>();
		if (lstNormValue.size() > 0) {
			boolean checkDefaultExist = false;
			for (NormValue no : lstNormValue) {
				listSelectNormValue.add(new SelectItem(no.getValueId(), no.getValueName()));
				if (this.selectedNormDefaultWithNormValue != null) {
					if (no.getValueId().equals(this.selectedNormDefaultWithNormValue)) {
						checkDefaultExist = true;
					}
				}
				if (this.listTableNormValueParamForString.size() > 0) {
					for (tableNormValueParam nvp : this.listTableNormValueParamForString) {
						if (no.getNormValueIndex() == nvp.getNormValue().getNormValueIndex()) {
							nvp.setNormValue(no);
							nvp.setSelectedNormValue(no.getValueId());
						}
					}
				}
			}
			if (!checkDefaultExist) {
				this.selectedNormDefaultWithNormValue = (Long) listSelectNormValue.get(0).getValue();
			}
		}
	}

	// Change data in list NormValue
	public Boolean checkLineInTableNormValue(NormValue item) {
		boolean result = false;
		if (item != null) {
			FacesContext context = FacesContext.getCurrentInstance();
			if (item.getValueId() == null || item.getValueId() < 1) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorDataValueID")));
				result = true;
			} else {
				if (this.listNormValueTmp.size() > 1) {
					int recordIndex = this.listNormValueTmp.indexOf(item);
					for (int i = 0; i < this.listNormValueTmp.size(); i++) {
						if (i != recordIndex) {
							if (this.listNormValueTmp.get(i).getValueId() == item.getValueId()) {
								context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
										this.readProperties("normalizer.validateError"),
										this.readProperties("normalizer.errorDataValueIDDuplicate")
												+ this.readProperties("normalizer.valueID") + "=" + item.getValueId()
												+ " " + this.readProperties("normalizer.and")
												+ this.readProperties("normalizer.valueName") + "="
												+ item.getValueName()));
								result = true;
							}
						}
					}
				}
			}

			if (item.getNormValueIndex() == null || item.getNormValueIndex() < 1) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorDataValueIndex")));
				result = true;
			} else {
				if (this.listNormValueTmp.size() > 1) {
					int recordIndex = this.listNormValueTmp.indexOf(item);
					for (int i = 0; i < this.listNormValueTmp.size(); i++) {
						if (i != recordIndex) {
							if (this.listNormValueTmp.get(i).getNormValueIndex() == item.getNormValueIndex()) {
								context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
										this.readProperties("normalizer.validateError"),
										this.readProperties("normalizer.errorDataValueIndexDuplicate")
												+ this.readProperties("normalizer.valueIndex") + "=" + item.getValueId()
												+ " " + this.readProperties("normalizer.and")
												+ this.readProperties("normalizer.valueName") + "="
												+ item.getValueName()));
								result = true;
							}
						}
					}
				}
			}

			if (checkStringNullOrEmpty(item.getValueName())) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
								this.readProperties("normalizer.errorDataValueName")));
				result = true;
			}
		}
		return result;
	}

	// add Child for Table Norm Value
	public void addChildForTableNormValue() {
		if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER
				|| this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER) {
			if (this.listNormValueTmp.size() == 0) {
				this.listNormValueTmp.add(new NormValue("false", (long) 0, "false", (long) 0, "#000000", "#FFFFFF"));
				this.listNormValueTmp.add(new NormValue("true", (long) 1, "true", (long) 1, "#000000", "#FFFFFF"));
				this.selectedNormDefaultWithNormValue = (long) 0;
				this.listNormValue = this.listNormValueTmp;
			}
		} else {
			if (this.listNormValueTmp.size() > 0) {
				NormValue lastItem = this.listNormValueTmp.get(this.listNormValueTmp.size() - 1);
				if (lastItem.getValueId() >= 0 && lastItem.getNormValueIndex() > 0
						&& !checkStringNullOrEmpty(lastItem.getValueName())) {
					this.listNormValueTmp.add(new NormValue("", lastItem.getValueId() + 1, "",
							lastItem.getNormValueIndex() + 1, "#000000", "#FFFFFF"));
				} else {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.errorValidateAddNewNormValue")));
				}
			} else {
				this.listNormValueTmp.add(new NormValue("", (long) 1, "", (long) 1, "#000000", "#FFFFFF"));
			}
		}
	}

	// save norm value to DB
	public void saveNormValueToDB() {
		if (this.listNormValue.size() > 0) {
			if (checkNormValueBeforeSave(this.listNormValue)) {
				if (this.selectedNormDefaultWithNormValue != null) {
					this.normalizer.setDefaultValue(this.selectedNormDefaultWithNormValue);
				} else {
					this.normalizer.setDefaultValue(this.listNormValue.get(0).getValueId());
				}
				saveNormalizer();
			}
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateAddNewNormValueEmpty")));
		}
	}

	public void saveNormValueTMP() {
		if (this.listNormValueTmp.size() > 0) {
			if (checkNormValueBeforeSave(this.listNormValueTmp)) {
				this.listNormValue = this.listNormValueTmp;
				if (this.selectedNormDefaultWithNormValue != null) {
					if (checkDefaultValueInList(this.listNormValue, this.selectedNormDefaultWithNormValue)) {
						this.normalizer.setDefaultValue(this.selectedNormDefaultWithNormValue);
					} else {
						this.normalizer.setDefaultValue(this.listNormValue.get(0).getValueId());
					}
				} else {
					this.normalizer.setDefaultValue(this.listNormValue.get(0).getValueId());
				}
				loadDataForSelectNormValue(this.listNormValue);
				if (this.listNormValueToDelete.size() > 0) {
					if (normDAO.deleteNormValueInEditNormalizer(this.listNormValueToDelete,
							this.normalizer.getNormalizerId())) {
						this.showMessageINFO("common.update", " Normalizer Value Success");
						this.listNormValueToDelete = new ArrayList<>();
					}
				} else {
					this.showMessageINFO("common.update", " Normalizer Value Success");
				}

				this.listNormValueToDelete = new ArrayList<>();

				// update timetable
				if (this.selectedNormType == com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER
						&& listTableTimeType != null) {
					for (tableTimeType timeType : listTableTimeType) {
						List<dateInWeek> lstDateInWeek = timeType.getLstDateInWeek();
						if (lstDateInWeek != null) {
							for (dateInWeek dw : lstDateInWeek) {
								for (NormValue normValue : listNormValue) {
									if (normValue.getValueName() != null
											&& normValue.getValueName().equals(dw.getValueName())) {

										dw.setColorBG(normValue.getColorBG());
										dw.setColor(normValue.getColor());
									}
								}
							}
						}
					}
				}

				// close luon
				closeNormValueTMP();
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("$('.ui-dialog-titlebar-close').click();");
			}
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateAddNewNormValueEmpty")));
		}
	}

	public void closeNormValueTMP() {
		this.listNormValueTmp = this.listNormValue;
		loadDataForSelectNormValue(this.listNormValue);
		this.listNormValueToDelete = new ArrayList<>();
	}

	public Boolean checkDefaultValueInList(List<NormValue> lstNormValue, long defaultId) {
		boolean result = false;

		if (lstNormValue.size() > 0) {
			for (int i = 0; i < lstNormValue.size(); i++) {
				if (lstNormValue.get(i).getValueId() == defaultId) {
					result = true;
					break;
				}
			}

		}

		return result;
	}

	// check normalizer value before save
	public Boolean checkNormValueBeforeSave(List<NormValue> lstNormValue) {
		boolean result = true;
		if (lstNormValue.size() > 0) {
			for (int i = 0; i < lstNormValue.size(); i++) {
				for (int j = 0; j < lstNormValue.size(); j++) {
					if (i != j) {
						if (lstNormValue.get(i).getValueId() == lstNormValue.get(j).getValueId()) {
							FacesContext context = FacesContext.getCurrentInstance();
							context.addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN,
											this.readProperties("normalizer.validateError"),
											this.readProperties("normalizer.errorDataValueIDDuplicate")));
							return false;
						}
						if (lstNormValue.get(i).getNormValueIndex() == lstNormValue.get(j).getNormValueIndex()) {
							FacesContext context = FacesContext.getCurrentInstance();
							context.addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN,
											this.readProperties("normalizer.validateError"),
											this.readProperties("normalizer.errorDataValueIndexDuplicate")));
							return false;
						}
						if (lstNormValue.get(i).getValueName().equals(lstNormValue.get(j).getValueName())) {
							FacesContext context = FacesContext.getCurrentInstance();
							context.addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN,
											this.readProperties("normalizer.validateError"),
											this.readProperties("normalizer.errorDataValueNameDuplicate")));
							return false;
						}
					}
				}

				if (checkStringNullOrEmpty(lstNormValue.get(i).getValueName())) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.errorDataValueName")));
					return false;
				}

			}
		}

		return result;
	}

	public Boolean saveNormValue(Session session) {
		if (this.listNormValue.size() > 0) {
			if (this.selectedNormDefaultWithNormValue != null) {
				this.normalizer.setDefaultValue(this.selectedNormDefaultWithNormValue);
			} else {
				this.normalizer.setDefaultValue(this.listNormValue.get(0).getValueId());
			}
			if (this.normalizer.getNormalizerId() > 0) {
				// normValueDAO = new NormValueDAO();
				for (NormValue nv : this.listNormValue) {
					if (nv.getDomainId() == null || nv.getDomainId() <= 0)
						nv.setDomainId(normValueDAO.getDomainId());
					session.saveOrUpdate(nv);
					if (nv.getNormValueId() > 0) {
						// normValueMapDAO = new NormalizerNormValueMapDAO();
						NormalizerNormValueMap normValueMap = normValueMapDAO.findMapByNormalizerIdAndValueId(
								this.normalizer.getNormalizerId(), nv.getNormValueId());
						if (normValueMap == null) {
							normValueMap = new NormalizerNormValueMap(this.normalizer.getNormalizerId(),
									nv.getNormValueId());
							normValueMap.setDomainId(normValueMapDAO.getDomainId());
							session.save(normValueMap);
						}
					}
				}
			}
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorValidateAddNewNormValueEmpty")));
			return false;
		}

		return true;
	}

	// saveNormParam to DB
	public Boolean saveNormParam_bak() {
		if (this.listTableNormValueParamForString.size() > 0) {
			if (checkNormParamBeforeSave()) {
				if (this.normalizer.getNormalizerId() > 0) {
					normParamDAO = new NormParamDAO();

					for (tableNormValueParam tnvp : this.listTableNormValueParamForString) {
						NormParam np = tnvp.getNormParam();
						normParamDAO.saveOrUpdate(np);
						if (np.getNormParamId() > 0) {
							normParamMapDAO = new NomalizerNormParamMapDAO();
							NomalizerNormParamMap normParamMap = normParamMapDAO.findMapByNormalizerIdAndValueId(
									this.normalizer.getNormalizerId(), np.getNormParamId());
							if (normParamMap == null) {
								normParamMapDAO.save(new NomalizerNormParamMap(this.normalizer.getNormalizerId(),
										np.getNormParamId()));
							}
						}
					}
				}
			} else {
				return false;
			}
		}

		return true;
	}

	public Boolean saveNormParam(Session session) {
		if (this.listTableNormValueParamForString.size() > 0) {
			if (checkNormParamBeforeSave()) {
				if (this.normalizer.getNormalizerId() > 0) {
					// normParamDAO = new NormParamDAO();
					boolean isParam = false;
					for (tableNormValueParam tnvp : this.listTableNormValueParamForString) {
						NormParam np = tnvp.getNormParam();
						isParam = np.getConfigInput().contains("isUsingParameter:true")
								|| np.getConfigInput().contains("startIsParameter:true")
								|| np.getConfigInput().contains("endIsParameter:true");
						if (isParam) {
							break;
						}
					}

					for (tableNormValueParam tnvp : this.listTableNormValueParamForString) {
						NormParam np = tnvp.getNormParam();
						if (isParam && normalizer.getNormlizerType()
								.equals(Long.valueOf(NormalizerType.NUMBER_NORMALIZER))) {
							if (!np.getConfigInput().contains("isUsingParameter:")) {
								String configInput = np.getConfigInput();
								np.setConfigInput("parameterId:" + getValueInConfigInput("value", configInput)
										+ ";isUsingParameter:false;comparisionType:"
										+ getValueInConfigInput("comparisionType", configInput) + ";priority:"
										+ tnvp.getPriority());
							}
						}

						if (isParam && (normalizer.getNormlizerType()
								.equals(Long.valueOf(NormalizerType.QUANTITY_NORMALIZER))
								|| normalizer.getNormlizerType().equals(Long.valueOf(NormalizerType.BALANCE_NORMALIZER))
								|| normalizer.getNormlizerType()
										.equals(Long.valueOf(NormalizerType.ACMBALANCE_NORMALIZER)))) {
							if (!np.getConfigInput().contains("startIsParameter:")) {
								String configInput = np.getConfigInput();
								np.setConfigInput("start:" + getValueInConfigInput("start", configInput)
										+ ";startIsParameter:false;end:" + getValueInConfigInput("end", configInput)
										+ ";endIsParameter:false;priority:" + tnvp.getPriority());
							}
						}
						if (np.getDomainId() == null || np.getDomainId() <= 0)
							np.setDomainId(normParamMapDAO.getDomainId());
						session.saveOrUpdate(np);
						if (np.getNormParamId() > 0) {
							// normParamMapDAO = new NomalizerNormParamMapDAO();
							NomalizerNormParamMap normParamMap = normParamMapDAO.findMapByNormalizerIdAndValueId(
									this.normalizer.getNormalizerId(), np.getNormParamId());
							if (normParamMap == null) {
								normParamMap = new NomalizerNormParamMap(this.normalizer.getNormalizerId(),
										np.getNormParamId());
								normParamMap.setDomainId(normParamMapDAO.getDomainId());
								session.save(normParamMap);
							}
						}
					}
				}
			} else {
				return false;
			}
		}

		return true;

	}

	public String getValueInConfigInput(String key, String configInput) {
		String[] items = configInput.split(";");
		for (String item : items) {
			if (item.contains(key + ":")) {
				return item.replace(key + ":", "");
			}
		}
		return null;
	}

	// checkNormParam Before save
	public Boolean checkNormParamBeforeSave() {
		boolean result = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (this.listTableNormValueParamForString.size() > 0) {
			switch (this.selectedNormType) {

			case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:
				for (tableNormValueParam tNVP : this.listTableNormValueParamForString) {
					if (checkStringNullOrEmpty(tNVP.getParamValueStr())) {
						context.addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_WARN,
										this.readProperties("normalizer.validateError"),
										this.readProperties("normalizer.errorTableNormValueNormParamStr")));
						result = false;
						break;
					}
				}

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:
				Boolean[] checkDaySetZeroHour = { false, false, false, false, false, false, false };
				Boolean checkDaySetEndDayHour = false;
				for (tableNormValueParam tNVP : this.listTableNormValueParamForString) {
					for (long i = 1; i < 8; i++) {
						if (tNVP.getSelectedDay() == i && tNVP.getSelectedHour() == 0 && tNVP.getSelectedMinute() == 0
								&& tNVP.getSelectedSecond() == 0) {
							checkDaySetZeroHour[(int) i - 1] = true;
						}
					}
					if (tNVP.getSelectedHour() == 23 && tNVP.getSelectedMinute() == 59
							&& tNVP.getSelectedSecond() == 59) {
						checkDaySetEndDayHour = true;
					}
				}

				if (checkDaySetEndDayHour) {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									this.readProperties("normalizer.validateError"),
									this.readProperties("normalizer.errorTableNormValueNormParamTimeTypeEnd")));
					result = false;
				}

				boolean checkTime = true;
				for (int i = 0; i < checkDaySetZeroHour.length; i++) {
					if (!checkDaySetZeroHour[i]) {
						checkTime = false;
						break;
					}
				}
				
				if(!checkTime) {
					context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							this.readProperties("normalizer.validateError"),
							this.readProperties("normalizer.errorTableNormValueNormParamTimeTypeAll")));
					result = false;					
				}

				for (int i = 0; i < this.listTableNormValueParamForString.size(); i++) {
					for (int j = 0; j < this.listTableNormValueParamForString.size(); j++) {
						if (i != j) {
							if (this.listTableNormValueParamForString.get(i)
									.getSelectedDay() == this.listTableNormValueParamForString.get(j).getSelectedDay()
									&& this.listTableNormValueParamForString.get(i)
											.getSelectedHour() == this.listTableNormValueParamForString.get(j)
													.getSelectedHour()
									&& this.listTableNormValueParamForString.get(i)
											.getSelectedMinute() == this.listTableNormValueParamForString.get(j)
													.getSelectedMinute()
									&& this.listTableNormValueParamForString.get(i)
											.getSelectedSecond() == this.listTableNormValueParamForString.get(j)
													.getSelectedSecond()) {
								context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
										this.readProperties("normalizer.validateError"),
										this.readProperties("normalizer.duplicateTime") + " Day:"
												+ getStringDay(this.listTableNormValueParamForString.get(i)
														.getSelectedDay().intValue())
												+ " Hour:"
												+ this.listTableNormValueParamForString.get(i).getSelectedHour()
												+ " Minute:"
												+ this.listTableNormValueParamForString.get(i).getSelectedMinute()
												+ " Second:"
												+ this.listTableNormValueParamForString.get(i).getSelectedSecond()));
								result = false;
								break;
							}
						}
					}
					if (!result) {
						break;
					}
				}

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:
				for (int i = 0; i < this.listTableNormValueParamForString.size(); i++) {
					if (this.listTableNormValueParamForString.get(i).getSelectedStartValue() == 2
							&& this.listTableNormValueParamForString.get(i).getSelectedEndValue() == 2) {
						context.addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_WARN,
										this.readProperties("normalizer.validateError"),
										this.readProperties("normalizer.chooseSameCurrentTime")));
						result = false;
						break;
					} else {
						
					}
				}
				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:

				break;
			case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:

				break;

			default:
				break;
			}
		}
		return result;
	}

	public Boolean checkIsUsedParamInTableNVP(Integer colUseCheck) {
		boolean result = false;

		if (this.listTableNormValueParamForString.size() > 0) {
			if (colUseCheck == 1) {
				for (tableNormValueParam nvp : this.listTableNormValueParamForString) {
					if (nvp.getStartIsParam()) {
						return true;
					}
				}
			} else if (colUseCheck == 2) {
				for (tableNormValueParam nvp : this.listTableNormValueParamForString) {
					if (nvp.getStartIsParam()) {
						return true;
					}

					if (nvp.getEndIsParam()) {
						return true;
					}
				}
			}
		}

		return result;
	}

	// delete norm value
	public void removeChildForTableNormValue(NormValue item) {
		if (item != null) {
			if (!checkIsUsedOfNormValue(item)) {
				// loadDataForSelectNormValue(this.listNormValueTmp);
				if (item.getNormValueId() != 0) {
					this.listNormValueToDelete.add(item);
				}
				this.listNormValueTmp.remove(item);
				// try{
				// if(item.getNormValueId() > 0){
				// normValueMapDAO = new NormalizerNormValueMapDAO();
				// normValueMapDAO.deleteMapByValueId(item.getNormValueId());
				// normValueDAO = new NormValueDAO();
				// normValueDAO.delete(item);
				// }
				//
				// } catch (Exception e) {
				// FacesContext context = FacesContext.getCurrentInstance();
				// context.addMessage(null, new
				// FacesMessage(FacesMessage.SEVERITY_WARN,
				// this.readProperties("normalizer.deleteError"),
				// this.readProperties("normalizer.tryAgainLater")));
				// }
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, this.readProperties("normalizer.deleteError"),
								this.readProperties("normalizer.normValueIsUsed")));
			}
		}
	}

	public Boolean checkIsUsedOfNormValue(NormValue item) {
		boolean result = false;

		if (this.listTableNormValueParamForString.size() > 0) {
			for (tableNormValueParam tb : this.listTableNormValueParamForString) {
				if (item.getValueId() == tb.normValue.getValueId()) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	public Boolean deleteReferenceOfNormValue(NormValue item) {
		boolean result = true;

		if (this.listTableNormValueParamForString.size() > 0) {
			for (tableNormValueParam tb : this.listTableNormValueParamForString) {
				if (item.getValueId() == tb.normValue.getValueId()) {
					try {
						if (tb.getNormParam() != null && tb.getNormParam().getNormParamId() > 0) {
							normParamMapDAO = new NomalizerNormParamMapDAO();
							normParamMapDAO.deleteParamMapByParamId(tb.getNormParam().getNormParamId());
							normParamDAO = new NormParamDAO();
							normParamDAO.delete(tb.getNormParam());
						}
						this.listTableNormValueParamForString.remove(tb);
					} catch (Exception e) {
						getLogger().warn(e.getMessage(), e);
						result = false;
					}
				}
			}
		}
		return result;
	}

	// ****************************** TableNormValueParam for String type
	// **********************************************************************
	public class tableNormValueParam {

		public NormValue normValue;
		public NormParam normParam;

		public String paramValueStr;
		public Integer selectedCompare;
		public Long selectedNormValue;

		public String startValue;
		public String endValue;

		// public String startValueStr;
		// public String endValueStr;

		public Date startValueDate;
		public Date endValueDate;

		public long startValueNumber;
		public long endValueNumber;

		public Boolean startIsParam;
		public Boolean endIsParam;

		public Integer selectedStartValue;
		public Integer selectedEndValue;

		public String selectedZoneName;
		public Long selectedZoneId;
		public Integer selectedZoneType;
		public Integer priority;
		public Long selectedParameterID;

		public Long selectedHour;
		public Long selectedMinute;
		public Long selectedSecond;
		public Long selectedDay;

		public tableNormValueParam() {
			super();
		}

		public tableNormValueParam(NormValue normValue, NormParam normParam, Integer selectedCompare,
				String paramValueStr, Long selectedNormValue, String startValue, String endValue,
				Integer selectedStartValue, Integer selectedEndValue, Long selectedZoneId, Integer selectedZoneType,
				Integer priority, Long selectedParameterID, Long selectedHour, Long selectedMinute, Long selectedSecond,
				Long selectedDay, Boolean startIsParam, Boolean endIsParam) {
			super();
			this.normValue = normValue;
			this.normParam = normParam;
			this.selectedCompare = selectedCompare;
			this.paramValueStr = paramValueStr;
			this.selectedNormValue = selectedNormValue;
			this.startValue = startValue;
			this.endValue = endValue;
			this.selectedStartValue = selectedStartValue;
			this.selectedEndValue = selectedEndValue;
			this.selectedZoneId = selectedZoneId;
			this.selectedZoneType = selectedZoneType;
			this.priority = priority;
			this.selectedParameterID = selectedParameterID;
			this.selectedHour = selectedHour;
			this.selectedMinute = selectedMinute;
			this.selectedSecond = selectedSecond;
			this.selectedDay = selectedDay;
			this.startIsParam = startIsParam;
			this.endIsParam = endIsParam;
		}

		public String getSelectedZoneName() {
			return selectedZoneName;
		}

		public void setSelectedZoneName(String selectedZoneName) {
			this.selectedZoneName = selectedZoneName;
		}

		public Date getStartValueDate() {
			return startValueDate;
		}

		public void setStartValueDate(Date startValueDate) {
			this.startValueDate = startValueDate;
		}

		public Date getEndValueDate() {
			return endValueDate;
		}

		public void setEndValueDate(Date endValueDate) {
			this.endValueDate = endValueDate;
		}

		// public String getStartValueStr() {
		// return startValueStr;
		// }
		//
		// public void setStartValueStr(String startValueStr) {
		// this.startValueStr = startValueStr;
		// }
		//
		// public String getEndValueStr() {
		// return endValueStr;
		// }
		//
		// public void setEndValueStr(String endValueStr) {
		// this.endValueStr = endValueStr;
		// }

		public Boolean getStartIsParam() {
			return startIsParam;
		}

		public long getStartValueNumber() {
			return startValueNumber;
		}

		public void setStartValueNumber(long startValueNumber) {
			this.startValueNumber = startValueNumber;
		}

		public long getEndValueNumber() {
			return endValueNumber;
		}

		public void setEndValueNumber(long endValueNumber) {
			this.endValueNumber = endValueNumber;
		}

		public void setStartIsParam(Boolean startIsParam) {
			this.startIsParam = startIsParam;
		}

		public Boolean getEndIsParam() {
			return endIsParam;
		}

		public void setEndIsParam(Boolean endIsParam) {
			this.endIsParam = endIsParam;
		}

		public Long getSelectedDay() {
			return selectedDay;
		}

		public void setSelectedDay(Long selectedDay) {
			this.selectedDay = selectedDay;
		}

		public Long getSelectedHour() {
			return selectedHour;
		}

		public void setSelectedHour(Long selectedHour) {
			this.selectedHour = selectedHour;
		}

		public Long getSelectedMinute() {
			return selectedMinute;
		}

		public void setSelectedMinute(Long selectedMinute) {
			this.selectedMinute = selectedMinute;
		}

		public Long getSelectedSecond() {
			return selectedSecond;
		}

		public void setSelectedSecond(Long selectedSecond) {
			this.selectedSecond = selectedSecond;
		}

		public Long getSelectedParameterID() {
			return selectedParameterID;
		}

		public void setSelectedParameterID(Long selectedParameterID) {
			this.selectedParameterID = selectedParameterID;
		}

		public Integer getPriority() {
			return priority;
		}

		public void setPriority(Integer priority) {
			this.priority = priority;
		}

		public Long getSelectedZoneId() {
			return selectedZoneId;
		}

		public void setSelectedZoneId(Long selectedZoneId) {
			this.selectedZoneId = selectedZoneId;
		}

		public Integer getSelectedZoneType() {
			return selectedZoneType;
		}

		public void setSelectedZoneType(Integer selectedZoneType) {
			this.selectedZoneType = selectedZoneType;
		}

		public String getStartValue() {
			return startValue;
		}

		public void setStartValue(String startValue) {
			this.startValue = startValue;
		}

		public String getEndValue() {
			return endValue;
		}

		public void setEndValue(String endValue) {
			this.endValue = endValue;
		}

		public Integer getSelectedStartValue() {
			return selectedStartValue;
		}

		public void setSelectedStartValue(Integer selectedStartValue) {
			this.selectedStartValue = selectedStartValue;
		}

		public Integer getSelectedEndValue() {
			return selectedEndValue;
		}

		public void setSelectedEndValue(Integer selectedEndValue) {
			this.selectedEndValue = selectedEndValue;
		}

		public Long getSelectedNormValue() {
			return selectedNormValue;
		}

		public void setSelectedNormValue(Long selectedNormValue) {
			this.selectedNormValue = selectedNormValue;
		}

		public NormValue getNormValue() {
			return normValue;
		}

		public void setNormValue(NormValue normValue) {
			this.normValue = normValue;
		}

		public NormParam getNormParam() {
			return normParam;
		}

		public void setNormParam(NormParam normParam) {
			this.normParam = normParam;
		}

		public Integer getSelectedCompare() {
			return selectedCompare;
		}

		public void setSelectedCompare(Integer selectedCompare) {
			this.selectedCompare = selectedCompare;
		}

		public String getParamValueStr() {
			return paramValueStr;
		}

		public void setParamValueStr(String paramValueStr) {
			this.paramValueStr = paramValueStr;
		}

	}

	public void onchangeZoneType(tableNormValueParam item) {

		if (item != null) {
			item.selectedZoneId = null;
			item.selectedZoneName = "";
			item.normParam.setConfigInput("zoneValueId:-1;dataZoneType:-1");
		}
	}

	// load listZoneID
	public void loadListZoneID(tableNormValueParam item) {
		this.listZoneID = new ArrayList<>();
		switch (item.getSelectedZoneType()) {
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE:
			ZoneDAO zoneDao = new ZoneDAO();
			List<Zone> lstZone = zoneDao.findAll("");
			if (lstZone != null && lstZone.size() > 0) {
				for (Zone zo : lstZone) {
					this.listZoneID.add(new SelectItem(zo.getZoneId(), zo.getZoneName()));
				}
				item.setSelectedZoneId(lstZone.get(0).getZoneId());
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP:
			ZoneMapDAO zoneMapDao = new ZoneMapDAO();
			List<ZoneMap> lstZoneMap = zoneMapDao.findAll("");
			if (lstZoneMap != null && lstZoneMap.size() > 0) {
				for (ZoneMap zm : lstZoneMap) {
					this.listZoneID.add(new SelectItem(zm.getZoneMapId(), zm.getZoneMapName()));
				}
				item.setSelectedZoneId(lstZoneMap.get(0).getZoneMapId());
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_NET_ZONE:
			GeoNetZoneDAO geoNetZoneDao = new GeoNetZoneDAO();
			List<GeoNetZone> lstGeoNetZone = geoNetZoneDao.findAll("");
			if (lstGeoNetZone != null && lstGeoNetZone.size() > 0) {
				for (GeoNetZone gnz : lstGeoNetZone) {
					this.listZoneID.add(new SelectItem(gnz.getGeoNetZoneId(), String.valueOf(gnz.getGeoNetZoneId())));
				}
				item.setSelectedZoneId(lstGeoNetZone.get(0).getGeoNetZoneId());
			}
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_HOME_ZONE:
			GeoHomeZoneDAO geoHomeZoneDao = new GeoHomeZoneDAO();
			List<GeoHomeZone> lstGeoHomeZone = geoHomeZoneDao.findAll("");
			if (lstGeoHomeZone != null && lstGeoHomeZone.size() > 0) {
				for (GeoHomeZone ghz : lstGeoHomeZone) {
					this.listZoneID.add(new SelectItem(ghz.getGeoHomeZoneId(), ghz.getGeoHomeZoneName()));
				}
				item.setSelectedZoneId(lstGeoHomeZone.get(0).getGeoHomeZoneId());
			}
			break;

		default:
			break;
		}
	}

	// load zone default
//	public Long loadListZoneIDdefault() {
//		long result = 0;
//		ZoneDAO zoneDao = new ZoneDAO();
//		List<Zone> lstZone = zoneDao.findAll("");
//		if (lstZone != null && lstZone.size() > 0) {
//			for (Zone zo : lstZone) {
//				this.listZoneID.add(new SelectItem(zo.getZoneId(), zo.getZoneName()));
//			}
//			result = lstZone.get(0).getZoneId();
//		}
//		return result;
//	}

	// load paramdefault ID
	public Long loadListParamIDdefault() {
		long result = 0;
		ParameterDAO paramDAO = new ParameterDAO();
		List<Parameter> lstParam = paramDAO.findAll("");
		if (lstParam != null && lstParam.size() > 0) {
			for (Parameter pr : lstParam) {
				this.listZoneID.add(new SelectItem(pr.getParameterId(), pr.getParameterName()));
			}
			result = lstParam.get(0).getParameterId();
		}
		return result;
	}

	// START ****Table for Time
	// type***************************************************************************************************************
	public class tableTimeType {
		public Long legend;
		public String timeStr;
		public List<dateInWeek> lstDateInWeek;

		public tableTimeType() {
			super();
		}

		public tableTimeType(Long legend, String timeStr, List<dateInWeek> lstDateInWeek) {
			super();
			this.legend = legend;
			this.lstDateInWeek = lstDateInWeek;
			this.timeStr = timeStr;
		}

		public Long getLegend() {
			return legend;
		}

		public void setLegend(Long legend) {
			this.legend = legend;
		}

		public List<dateInWeek> getLstDateInWeek() {
			return lstDateInWeek;
		}

		public void setLstDateInWeek(List<dateInWeek> lstDateInWeek) {
			this.lstDateInWeek = lstDateInWeek;
		}

		public String getTimeStr() {
			return timeStr;
		}

		public void setTimeStr(String timeStr) {
			this.timeStr = timeStr;
		}

	}

	public class dateInWeek {
		public int dateNum;
		public String color;
		public String colorBG;
		public String valueName;
		public NormParam normParam;

		public dateInWeek() {
			super();
		}

		public dateInWeek(int dateNum, String color, String colorBG, NormParam normParam, String valueName) {
			super();
			this.dateNum = dateNum;
			this.color = color;
			this.colorBG = colorBG;
			this.normParam = normParam;
			this.valueName = valueName;
		}

		public String getValueName() {
			return valueName;
		}

		public void setValueName(String valueName) {
			this.valueName = valueName;
		}

		public int getDateNum() {
			return dateNum;
		}

		public void setDateNum(int dateNum) {
			this.dateNum = dateNum;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getColorBG() {
			return colorBG;
		}

		public void setColorBG(String colorBG) {
			this.colorBG = colorBG;
		}

		public NormParam getNormParam() {
			return normParam;
		}

		public void setNormParam(NormParam normParam) {
			this.normParam = normParam;
		}

	}

	// create or update table time
	public void createOrUpdateTableTime() {
		if (this.listTableNormValueParamForString != null && this.listTableNormValueParamForString.size() > 0) {
			this.listTableTimeType = new ArrayList<>();
			for (tableNormValueParam e : this.listTableNormValueParamForString) {
				boolean isExist = false;
				long legend = parseSelectedTimeToLegend(e.getSelectedHour(), e.getSelectedMinute(),
						e.getSelectedSecond());
				for (tableTimeType tbtt : this.listTableTimeType) {
					if (tbtt.legend == legend) {
						for (dateInWeek diw : tbtt.getLstDateInWeek()) {
							if (diw.getDateNum() == e.getSelectedDay()) {
								diw.setColor(e.getNormValue().getColor());
								diw.setColorBG(e.getNormValue().getColorBG());
								diw.setNormParam(e.getNormParam());
								diw.setValueName(e.getNormValue().getValueName());
								isExist = true;
								break;
							}
						}
					}
				}
				if (!isExist) {
					this.listTableTimeType.add(new tableTimeType(legend,
							(e.getSelectedHour() < 10 ? "0" + e.getSelectedHour() : e.getSelectedHour()) + ":"
									+ (e.getSelectedMinute() < 10 ? "0" + e.getSelectedMinute() : e.getSelectedMinute())
									+ ":" + (e.getSelectedSecond() < 10 ? "0" + e.getSelectedSecond()
											: e.getSelectedSecond()),
							getListDateInWeekTMP()));
					tableTimeType lastItem = this.listTableTimeType.get(this.listTableTimeType.size() - 1);
					for (dateInWeek diw : lastItem.getLstDateInWeek()) {
						if (diw.getDateNum() == e.getSelectedDay()) {
							diw.setColor(e.getNormValue().getColor());
							diw.setColorBG(e.getNormValue().getColorBG());
							diw.setNormParam(e.getNormParam());
							diw.setValueName(e.getNormValue().getValueName());
							break;
						}
					}
				}
			}
		}
		resortTableTime();
	}

	// sort table time
	public void resortTableTime() {
		if (this.listTableTimeType.size() > 0) {
			Collections.sort(this.listTableTimeType, new Comparator<tableTimeType>() {
				public int compare(tableTimeType synchronizedListOne, tableTimeType synchronizedListTwo) {
					// use instanceof to verify the references are indeed of the
					// type in question
					return ((tableTimeType) synchronizedListOne).legend
							.compareTo(((tableTimeType) synchronizedListTwo).legend);
				}
			});
		}
		fillColorTableTime();
	}

	// fill color for table time
	public void fillColorTableTime() {
		if (this.listTableTimeType.size() > 0) {
			String[] tmpColor = { "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000" };
			String[] tmpColorBG = { "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF" };
			// for(int i = this.listTableTimeType.size() - 1; i >= 0; i-- ){
			for (int i = 0; i < this.listTableTimeType.size(); i++) {
				for (int j = 0; j < 7; j++) {
					if (this.listTableTimeType.get(i).lstDateInWeek.get(j).getNormParam().getConfigInput() != null) {
						tmpColor[j] = this.listTableTimeType.get(i).lstDateInWeek.get(j).getColor();
						tmpColorBG[j] = this.listTableTimeType.get(i).lstDateInWeek.get(j).getColorBG();
					} else {
						this.listTableTimeType.get(i).lstDateInWeek.get(j).setColor(tmpColor[j]);
						this.listTableTimeType.get(i).lstDateInWeek.get(j).setColorBG(tmpColorBG[j]);
						this.listTableTimeType.get(i).lstDateInWeek.get(j).setValueName("-");
					}
				}
			}
		}
	}
	// END ****Table for Time
	// type***************************************************************************************************************

	// GET SET
	public Normalizer getNormalizer() {
		return normalizer;
	}

	public void setNormalizer(Normalizer normalizer) {
		this.normalizer = normalizer;
	}

	public List<Normalizer> getListNormalizer() {
		return listNormalizer;
	}

	public void setListNormalizer(List<Normalizer> listNormalizer) {
		this.listNormalizer = listNormalizer;
	}

	public List<Normalizer> getListNormalizerByCate() {
		return listNormalizerByCate;
	}

	public void setListNormalizerByCate(List<Normalizer> listNormalizerByCate) {
		this.listNormalizerByCate = listNormalizerByCate;
	}

	public List<SelectItem> getListNormType() {
		listNormType = new ArrayList<SelectItem>();
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER,
				this.readProperties("normalizer.STRING_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER,
				this.readProperties("normalizer.STRING_MATCH_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER,
				this.readProperties("normalizer.NUMBER_NORMALIZER")));
		listNormType
				.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER,
						this.readProperties("normalizer.CHECK_REGISTER_LIST_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER,
				this.readProperties("normalizer.TIME_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER,
				this.readProperties("normalizer.DATE_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER,
				this.readProperties("normalizer.QUANTITY_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER,
				this.readProperties("normalizer.BALANCE_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER,
				this.readProperties("normalizer.ZONE_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER,
				this.readProperties("normalizer.CHECK_IN_LIST_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER,
				this.readProperties("normalizer.ACMBALANCE_NORMALIZER")));
		listNormType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER,
				this.readProperties("normalizer.NUMBER_PARAMETER_NORMALIZER")));
		return listNormType;
	}

	public void setListNormType(List<SelectItem> listNormType) {
		this.listNormType = listNormType;
	}

	public List<SelectItem> getListNormState() {
		listNormState = new ArrayList<SelectItem>();
		listNormState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormState.STATIC_PER_EVENT,
				this.readProperties("normalizer.STATIC_PER_EVENT_STATE")));
		listNormState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormState.STATIC_PER_SESSION,
				this.readProperties("normalizer.STATIC_PER_SESSION_STATE")));
		listNormState.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormState.DYNAMIC,
				this.readProperties("normalizer.DYNAMIC_STATE")));
		return listNormState;
	}

	public void setListNormState(List<SelectItem> listNormState) {
		this.listNormState = listNormState;
	}

	public List<SelectItem> getListNormCate() {

		return listNormCate;
	}

	private void loadCategory() {

		if (listNormCate != null)
			listNormCate.clear();
		else
			listNormCate = new ArrayList<>();
		List<Category> lstCat = catDao.findByTypeForSelectbox(CategoryType.OFF_NOM_NORMALIZER);
		for (Category item : lstCat) {

			listNormCate.add(new SelectItem(item.getCategoryId(), item.getCategoryName()));
		}
	}

	public void setListNormCate(List<SelectItem> listNormCate) {
		this.listNormCate = listNormCate;
	}

	public Integer getSelectedNormType() {
		return selectedNormType;
	}

	public void setSelectedNormType(Integer selectedNormType) {
		this.selectedNormType = selectedNormType;
	}

	public Integer getSelectedNormState() {
		return selectedNormState;
	}

	public void setSelectedNormState(Integer selectedNormState) {
		this.selectedNormState = selectedNormState;
	}

	public Long getSelectedNormCate() {
		return selectedNormCate;
	}

	public void setSelectedNormCate(Long selectedNormCate) {
		this.selectedNormCate = selectedNormCate;
	}

	public List<SelectItem> getListNormDefault() {
		listNormDefault = new ArrayList<SelectItem>();
		switch (this.selectedNormType) {
		case 0:
			break;
		case 1:
			listNormDefault.add(new SelectItem((long) 0, this.readProperties("normalizer.equal")));
			listNormDefault.add(new SelectItem((long) 1, this.readProperties("normalizer.notEqual")));
			break;
		case 2:
			break;
		case 3:
			listNormDefault.add(new SelectItem((long) 0, this.readProperties("normalizer.notExist")));
			listNormDefault.add(new SelectItem((long) 1, this.readProperties("normalizer.exist")));
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			listNormDefault.add(new SelectItem((long) 0, this.readProperties("normalizer.notExist")));
			listNormDefault.add(new SelectItem((long) 1, this.readProperties("normalizer.exist")));
			break;
		case 10:
			break;
		case 11:
			break;
		}
		return listNormDefault;
	}

	public void setListNormDefault(List<SelectItem> listNormDefault) {
		this.listNormDefault = listNormDefault;
	}

	public String getFormtype() {
		return formtype;
	}

	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public NormalizerNormValueMap getNormVallueMap() {
		return normVallueMap;
	}

	public void setNormVallueMap(NormalizerNormValueMap normVallueMap) {
		this.normVallueMap = normVallueMap;
	}

	public NormValue getNormValue() {
		return normValue;
	}

	public void setNormValue(NormValue normValue) {
		this.normValue = normValue;
	}

	public NormParam getNormParam() {
		return normParam;
	}

	public void setNormParam(NormParam normParam) {
		this.normParam = normParam;
	}

	public NomalizerNormParamMap getNormParamMap() {
		return normParamMap;
	}

	public void setNormParamMap(NomalizerNormParamMap normParamMap) {
		this.normParamMap = normParamMap;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInputField1() {
		return inputField1;
	}

	public void setInputField1(String inputField1) {
		this.inputField1 = inputField1;
	}

	public String getInputField2() {
		return inputField2;
	}

	public void setInputField2(String inputField2) {
		this.inputField2 = inputField2;
	}

	public List<tmpInputFieldTable> getListInputFieldTable1() {
		return listInputFieldTable1;
	}

	public void setListInputFieldTable1(List<tmpInputFieldTable> listInputFieldTable1) {
		this.listInputFieldTable1 = listInputFieldTable1;
	}

	public List<tmpInputFieldTable> getListInputFieldTable2() {
		return listInputFieldTable2;
	}

	public void setListInputFieldTable2(List<tmpInputFieldTable> listInputFieldTable2) {
		this.listInputFieldTable2 = listInputFieldTable2;
	}

	public String getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(String filterConditions) {
		this.filterConditions = filterConditions;
	}

	public String getPreFunctions() {
		return preFunctions;
	}

	public void setPreFunctions(String preFunctions) {
		this.preFunctions = preFunctions;
	}

	private void initPreFunctions() {

		this.mapPreFunction = new HashMap<>();
		this.listPreFunctions = new ArrayList<>();
		PreFunctionDAO dao = new PreFunctionDAO();
		List<PreFunction> lstObjPreFunction = dao.findAll("functionName");
		for (PreFunction function : lstObjPreFunction) {
			mapPreFunction.put(function.getPreFunctionId(), function);
			listPreFunctions.add(new SelectItem(function.getPreFunctionId(), function.getFunctionName()));
		}
	}

	public List<SelectItem> getListPreFunctions() {
		// this.listPreFunctions = new ArrayList<>();
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.PREFIX,
		// com.viettel.ocs.constant.Normalizer.preFunction.PREFIX_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX,
		// com.viettel.ocs.constant.Normalizer.preFunction.SUFFIX_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING,
		// com.viettel.ocs.constant.Normalizer.preFunction.SUB_STRING_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN,
		// com.viettel.ocs.constant.Normalizer.preFunction.STANDARD_MSISDN_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_LIST_SIZE_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST,
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_LIST_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT,
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_FOR_OBJECT_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST,
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_LIST_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT,
		// com.viettel.ocs.constant.Normalizer.preFunction.VALIDATE_WITH_STATE_FOR_OBJECT_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER,
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAIN_CALLED_NUMBER_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME,
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_GROUP_NAME_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE,
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_STATE_TYPE_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ACMBAL_BY_BILLING_CYCLE_NAME));
		//
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER,
		// com.viettel.ocs.constant.Normalizer.preFunction.CONTAINS_CALLING_NUMBER_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_ZONE_STRING_BY_ZONEID_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_CELLS_FROM_LIST_CELL_STRING_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONES_FROM_LIST_ZONE_STRING_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_AVAILABLE_AMOUNT_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_CONSUME_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_TOTAL_VALUE_OF_PREVIOUS_ACMBALS_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_DAY_OF_MONTH_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_MONTH_OF_YEAR_NAME));
		// listPreFunctions.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE,
		// com.viettel.ocs.constant.Normalizer.preFunction.GET_ZONE_NAME));

		return listPreFunctions;
	}

	public void setListPreFunctions(List<SelectItem> listPreFunctions) {
		this.listPreFunctions = listPreFunctions;
	}

	public List<preFunctionTable> getListPreFunctionTable() {
		return listPreFunctionTable;
	}

	public void setListPreFunctionTable(List<preFunctionTable> listPreFunctionTable) {
		this.listPreFunctionTable = listPreFunctionTable;
	}

	public List<filterConditionTable> getListFilterConditionTable() {
		return listFilterConditionTable;
	}

	public void setListFilterConditionTable(List<filterConditionTable> listFilterConditionTable) {
		this.listFilterConditionTable = listFilterConditionTable;
	}

	public NestedObjectDAO getNestedObjectDAO() {
		return nestedObjectDAO;
	}

	public void setNestedObjectDAO(NestedObjectDAO nestedObjectDAO) {
		this.nestedObjectDAO = nestedObjectDAO;
	}

	public List<SelectItem> getListNestedObject() {
		return listNestedObject;
	}

	public void setListNestedObject(List<SelectItem> listNestedObject) {
		this.listNestedObject = listNestedObject;
	}

	public String getFinalFilter() {
		return finalFilter;
	}

	public void setFinalFilter(String finalFilter) {
		this.finalFilter = finalFilter;
	}

	public tmpInputFieldTable getTmpInputField() {
		return tmpInputField;
	}

	public void setTmpInputField(tmpInputFieldTable tmpInputField) {
		this.tmpInputField = tmpInputField;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public int getCatType() {
		return catType;
	}

	public void setCatType(int catType) {
		this.catType = catType;
	}

	public List<Long> getLstAllCatID() {
		return lstAllCatID;
	}

	public void setLstAllCatID(List<Long> lstAllCatID) {
		this.lstAllCatID = lstAllCatID;
	}

	public Map<Long, TreeNode> getMapCatNode() {
		return mapCatNode;
	}

	public void setMapCatNode(Map<Long, TreeNode> mapCatNode) {
		this.mapCatNode = mapCatNode;
	}

	public Map<String, TreeNode> getMapAllNode() {
		return mapAllNode;
	}

	public void setMapAllNode(Map<String, TreeNode> mapAllNode) {
		this.mapAllNode = mapAllNode;
	}

	public Object getObjReturn() {
		return objReturn;
	}

	public void setObjReturn(Object objReturn) {
		this.objReturn = objReturn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategoryTypeName() {
		return categoryTypeName;
	}

	public void setCategoryTypeName(String categoryTypeName) {
		this.categoryTypeName = categoryTypeName;
	}

	public TreeNode getTreeNodeSelected() {
		return treeNodeSelected;
	}

	public void setTreeNodeSelected(TreeNode treeNodeSelected) {
		this.treeNodeSelected = treeNodeSelected;
	}

	public Long getSelectedObjectClass() {
		return selectedObjectClass;
	}

	public void setSelectedObjectClass(Long selectedObjectClass) {
		this.selectedObjectClass = selectedObjectClass;
	}

	public List<SelectItem> getListNestedObjectClass() {
		return listNestedObjectClass;
	}

	public void setListNestedObjectClass(List<SelectItem> listNestedObjectClass) {
		this.listNestedObjectClass = listNestedObjectClass;
	}

	public NestedObjectClassDAO getNestedObjectClassDAO() {
		return nestedObjectClassDAO;
	}

	public void setNestedObjectClassDAO(NestedObjectClassDAO nestedObjectClassDAO) {
		this.nestedObjectClassDAO = nestedObjectClassDAO;
	}

	public List<Long> getListObjectClassID() {
		switch (this.selectedNormType) {

		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:

			break;

		default:
			break;
		}
		return listObjectClassID;
	}

	public void setListObjectClassID(List<Long> listObjectClassID) {
		this.listObjectClassID = listObjectClassID;
	}

	public List<Long> getListObjectID() {
		return listObjectID;
	}

	public void setListObjectID(List<Long> listObjectID) {
		this.listObjectID = listObjectID;
	}

	public NestedObject getNestedObjectTree() {
		return nestedObjectTree;
	}

	public void setNestedObjectTree(NestedObject nestedObjectTree) {
		this.nestedObjectTree = nestedObjectTree;
	}

	public List<NestedObject> getListParentNestedObjectByChild() {
		return listParentNestedObjectByChild;
	}

	public void setListParentNestedObjectByChild(List<NestedObject> listParentNestedObjectByChild) {
		this.listParentNestedObjectByChild = listParentNestedObjectByChild;
	}

	public Long getSelectedNormDefaultWithOutNormValue() {
		return selectedNormDefaultWithOutNormValue;
	}

	public void setSelectedNormDefaultWithOutNormValue(Long selectedNormDefaultWithOutNormValue) {
		this.selectedNormDefaultWithOutNormValue = selectedNormDefaultWithOutNormValue;
	}

	public Long getSelectedNormDefaultInsingMatchType() {
		return selectedNormDefaultInsingMatchType;
	}

	public void setSelectedNormDefaultInsingMatchType(Long selectedNormDefaultInsingMatchType) {
		this.selectedNormDefaultInsingMatchType = selectedNormDefaultInsingMatchType;
	}

	public Integer getStartCharactor() {
		return startCharactor;
	}

	public void setStartCharactor(Integer startCharactor) {
		this.startCharactor = startCharactor;
	}

	public Integer getEndCharactor() {
		return endCharactor;
	}

	public void setEndCharactor(Integer endCharactor) {
		this.endCharactor = endCharactor;
	}

	public NormValueDAO getNormValueDAO() {
		return normValueDAO;
	}

	public void setNormValueDAO(NormValueDAO normValueDAO) {
		this.normValueDAO = normValueDAO;
	}

	public NormParamDAO getNormParamDAO() {
		return normParamDAO;
	}

	public void setNormParamDAO(NormParamDAO normParamDAO) {
		this.normParamDAO = normParamDAO;
	}

	public NormalizerNormValueMapDAO getNormValueMapDAO() {
		return normValueMapDAO;
	}

	public void setNormValueMapDAO(NormalizerNormValueMapDAO normValueMapDAO) {
		this.normValueMapDAO = normValueMapDAO;
	}

	public NomalizerNormParamMapDAO getNormParamMapDAO() {
		return normParamMapDAO;
	}

	public void setNormParamMapDAO(NomalizerNormParamMapDAO normParamMapDAO) {
		this.normParamMapDAO = normParamMapDAO;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	private List<ValueParam> listValueParam;

	private NormValue defaulNValue;

	private List<NormValue> listNormValue;
	private List<NormParam> listNormParam;

	private String nameValue = "Choose NormValue";
	private String nameType = "Choose Type";

	// Load compare type
	public List<SelectItem> listCompareType() {
		List<SelectItem> listCompare = new ArrayList<SelectItem>();
		switch (this.selectedNormType) {

		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.EXACT,
					this.readProperties("normalizer.EXACT")));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.PREFIX,
					this.readProperties("normalizer.PREFIX")));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.SUFFIX,
					this.readProperties("normalizer.SUFFIX")));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.CONTAINS,
					this.readProperties("normalizer.CONTAINS")));
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL_VALUE));
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL_VALUE));
			listCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL_VALUE));
			break;

		default:
			break;
		}
		return listCompare;
	}

	// get Name NormValue
	public String nameNormValue(long idNormParam) {
		for (NormValue normValue : listNormValue) {
			if (idNormParam == normValue.getNormValueId()) {
				return normValue.getValueName();
			}
		}
		return nameValue;
	}

	// get Name Type
	public String nameCompareType(int idType) {
		for (SelectItem item : listCompareType()) {
			if (idType == (int) item.getValue()) {
				return item.getLabel();
			}
		}
		return nameType;
	}

	// get Default NormValue
	public NormValue defaultNormValue() {
		defaulNValue = new NormValue();
		for (NormValue normValue : listNormValue) {
			if (normValue.getValueId() == getNormalizer().getDefaultValue()) {
				this.defaulNValue = normValue;
			}
		}
		return defaulNValue;
	}
	// change Default

	// Load table
	public List<ValueParam> loadListValueParam() {
		// long normalizerid;
		listValueParam = new ArrayList<>();
		NormalizerDAO normalizerDAO = new NormalizerDAO();
		listValueParam = normalizerDAO.findListValueParamByConditions(getNormalizer().getNormalizerId());
		loadListNormValue();
		loadListNormParam();
		// defaultNormValue();
		convertParameterCompareTypeToTB();
		return listValueParam;
	}

	// convert Parameter CompareType To Table
	public void convertParameterCompareTypeToTB() {
		if (listValueParam != null && !listValueParam.isEmpty()) {
			for (ValueParam valueParam : listValueParam) {
				String config = valueParam.getNormParam().getConfigInput();

				String[] configArray = new String[2];
				configArray = config.split(";");

				String input = configArray[0].toString();
				String[] inputArray = new String[2];
				inputArray = input.split(":");

				if (inputArray[0].toString().equalsIgnoreCase("inputNumber")) {
					NormParam normParam = valueParam.getNormParam();
					normParam.setConfigInput(inputArray[1].toString());
					valueParam.setNormParam(normParam);
				}

				String type = configArray[1].toString();
				String[] typeArray = new String[2];
				typeArray = type.split(":");

				if (typeArray[0].toString().equalsIgnoreCase("comparisionType")) {
					if (typeArray[1].toString().contains(" ")) {
					} else {
						valueParam.setCompareType(Integer.parseInt(typeArray[1].toString()));
					}
				}
			}
		}
	}

	// Load list NormParam
	public List<NormParam> loadListNormParam() {
		listNormParam = new ArrayList<>();
		// Show list Id Norm Param by Normerlizer Id
		NormParamDAO normParamDAO = new NormParamDAO();
		listNormParam = normParamDAO.findNormParamByNormId(getNormalizer().getNormalizerId());
		return listNormParam;
	}

	public void commandAddNewValueParam() {
		if (listValueParam == null) {
			listValueParam = new ArrayList<>();
		}
		ValueParam valueParam = new ValueParam();
		valueParam.setNormValue(new NormValue());
		valueParam.setNormParam(new NormParam());
		valueParam.setCompareType(1);
		listValueParam.add(valueParam);
	}

	// In Dialog --------------------------
	// Load list NormValue
	public List<NormValue> loadListNormValue() {
		listNormValue = new ArrayList<>();
		// Show list Id Norm Value by Normerlizer Id
		NormValueDAO normValueDAO = new NormValueDAO();
		listNormValue = normValueDAO.findNormValueByNormId(getNormalizer().getNormalizerId());
		return listNormValue;
	}

	// Event in Dialog
	public void commandAddNewValue() {
		if (listNormValue == null) {
			listNormValue = new ArrayList<NormValue>();
		}
		NormValue newNormValue = new NormValue();
		listNormValue.add(newNormValue);
	}

	public void commandApplyListValue() {
		commandClearValue();
		NormValueDAO normValueDAO = new NormValueDAO();
		NormalizerNormValueMapDAO normalizerNormValueMapDAO = new NormalizerNormValueMapDAO();
		// Save Norm
		if (getNormalizer() == null) {
			NormalizerDAO normDAO = new NormalizerDAO();
			normDAO.saveOrUpdate(this.normalizer);
		}
		if (getNormalizer() != null && getNormalizer().getNormalizerId() > 0) {
			// Delete NormValue and Map by NormId
			normalizerNormValueMapDAO.deleteMapByNormalizerId(getNormalizer().getNormalizerId());
			// Save NormValue
			for (NormValue normValue : listNormValue) {
				normValue.setNormValueId(0);
				normValueDAO.saveOrUpdate(normValue);
				// Save MAP Value
				NormalizerNormValueMap valueMap = new NormalizerNormValueMap();
				valueMap.setNormalizerId(getNormalizer().getNormalizerId());
				valueMap.setNormValueId(normValue.getNormValueId());
				normalizerNormValueMapDAO.saveOrUpdate(valueMap);
			}
		}
	}

	public void commandCloseValue() {
		commandClearValue();
	}

	public void commandClearValue() {
		// Delete item in list vs ValueId null
		if (listNormValue.isEmpty()) {
			listNormValue = new ArrayList<NormValue>();
		} else {
			Iterator<NormValue> listNormValues = listNormValue.iterator();
			while (listNormValues.hasNext()) {
				NormValue normValue = listNormValues.next();
				if (normValue.getValueId() == null) {
					listNormValues.remove();
				}
			}
		}
	}

	// End in Dialog ------------------------------------
	public void commandSaveOrUpdateValueParam() {
		commandClearValue();
		NormParamDAO normParamDAO = new NormParamDAO();
		NomalizerNormParamMapDAO nomalizerNormParamMapDAO = new NomalizerNormParamMapDAO();
		// if new or update normalizer
		if (getNormalizer() != null && getNormalizer().getNormalizerId() > 0) {
			if (listValueParam != null && listValueParam.size() > 0) {
				// Delete NormParam and Map by NormId
				nomalizerNormParamMapDAO.deleteParamMapByNormalizerId(getNormalizer().getNormalizerId());
				for (ValueParam valueParam : listValueParam) {
					// Save Param
					NormParam normParam = valueParam.getNormParam();
					normParam.setDomainId(0l);
					String configInput = "inputNumber:" + normParam.getConfigInput() + ";comparisionType:"
							+ valueParam.getCompareType();
					normParam.setConfigInput(configInput);

					for (NormValue normValue : listNormValue) {
						if (valueParam.getNormValue().getNormValueId() == normValue.getNormValueId()) {
							normParam.setNormParamIndex(normValue.getNormValueIndex());
						}
					}
					normParamDAO.save(normParam);
					// Save Map param
					NomalizerNormParamMap nomalizerNormParamMap = new NomalizerNormParamMap();
					nomalizerNormParamMap.setNormalizerId(getNormalizer().getNormalizerId());
					nomalizerNormParamMap.setNormParamId(normParam.getNormParamId());
					nomalizerNormParamMapDAO.save(nomalizerNormParamMap);
				}
			}
		}
		// Update normal
		NormalizerDAO normalizerDAO = new NormalizerDAO();
		Normalizer normalizer = getNormalizer();
		// normalizer.setDefaultValue(defaulNValue.getValueId());
		normalizerDAO.update(normalizer);
		loadListValueParam();
	}

	// GetSet
	public List<NormValue> getListNormValue() {
		return listNormValue;
	}

	public void setListNormValue(List<NormValue> listNormValue) {
		this.listNormValue = listNormValue;
	}

	public List<ValueParam> getListValueParam() {
		return listValueParam;
	}

	public void setListValueParam(List<ValueParam> listValueParam) {
		this.listValueParam = listValueParam;
	}

	public String getNameValue() {
		return nameValue;
	}

	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}

	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameType) {
		this.nameType = nameType;
	}

	public List<NormParam> getListNormParam() {
		return listNormParam;
	}

	public void setListNormParam(List<NormParam> listNormParam) {
		this.listNormParam = listNormParam;
	}

	public NormValue getDefaulNValue() {
		return defaulNValue;
	}

	public void setDefaulNValue(NormValue defaulNValue) {
		this.defaulNValue = defaulNValue;
	}

	public Long getSelectedTmpNestedObj() {
		return selectedTmpNestedObj;
	}

	public void setSelectedTmpNestedObj(Long selectedTmpNestedObj) {
		this.selectedTmpNestedObj = selectedTmpNestedObj;
	}

	public List<tableNormValueParam> getListTableNormValueParamForString() {
		return listTableNormValueParamForString;
	}

	public void setListTableNormValueParamForString(List<tableNormValueParam> listTableNormValueParamForString) {
		this.listTableNormValueParamForString = listTableNormValueParamForString;
	}

	public List<SelectItem> getListSelectNormValue() {
		return listSelectNormValue;
	}

	public void setListSelectNormValue(List<SelectItem> listSelectNormValue) {
		this.listSelectNormValue = listSelectNormValue;
	}

	public List<SelectItem> getListSelectItemCompare() {
		this.listSelectItemCompare = new ArrayList<SelectItem>();
		switch (this.selectedNormType) {

		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_NORMALIZER:
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.EXACT,
					this.readProperties("normalizer.EXACT")));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.PREFIX,
					this.readProperties("normalizer.PREFIX")));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.SUFFIX,
					this.readProperties("normalizer.SUFFIX")));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.StringNormCompareType.CONTAINS,
					this.readProperties("normalizer.CONTAINS")));
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.STRING_MATCH_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_NORMALIZER:
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL_VALUE));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_VALUE));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_VALUE));
			listSelectItemCompare
					.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL,
							com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL_VALUE));
			listSelectItemCompare
					.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL,
							com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL_VALUE));
			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_REGISTER_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.TIME_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.DATE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.QUANTITY_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.BALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ZONE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.CHECK_IN_LIST_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.ACMBALANCE_NORMALIZER:

			break;
		case com.viettel.ocs.constant.Normalizer.NormalizerType.NUMBER_PARAMETER_NORMALIZER:
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.EQUAL_VALUE));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_VALUE));
			listSelectItemCompare.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS,
					com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_VALUE));
			listSelectItemCompare
					.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL,
							com.viettel.ocs.constant.Normalizer.NumberNormCompareType.GREATER_OR_EQUAL_VALUE));
			listSelectItemCompare
					.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL,
							com.viettel.ocs.constant.Normalizer.NumberNormCompareType.LESS_OR_EQUAL_VALUE));
			break;

		default:
			break;
		}
		return listSelectItemCompare;
	}

	public void setListSelectItemCompare(List<SelectItem> listSelectItemCompare) {
		this.listSelectItemCompare = listSelectItemCompare;
	}

	public Long getSelectedNormDefaultWithNormValue() {
		return selectedNormDefaultWithNormValue;
	}

	public void setSelectedNormDefaultWithNormValue(Long selectedNormDefaultWithNormValue) {
		this.selectedNormDefaultWithNormValue = selectedNormDefaultWithNormValue;
	}

	public NormValue getNormValueTmpToChooseColor() {
		return normValueTmpToChooseColor;
	}

	public void setNormValueTmpToChooseColor(NormValue normValueTmpToChooseColor) {
		this.normValueTmpToChooseColor = normValueTmpToChooseColor;
	}

	public Boolean getIsAvailableAmount() {
		return isAvailableAmount;
	}

	public void setIsAvailableAmount(Boolean isAvailableAmount) {
		this.isAvailableAmount = isAvailableAmount;
	}

	public List<SelectItem> getListDateNormDateType() {
		this.listDateNormDateType = new ArrayList<>();
		listDateNormDateType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.DateNormDateType.NONE,
				this.readProperties("normalizer.NONE")));
		listDateNormDateType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.DateNormDateType.CURRENT_TIME,
				this.readProperties("normalizer.CURRENT_TIME")));
		listDateNormDateType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.DateNormDateType.DELTA,
				this.readProperties("normalizer.DELTA")));
		return listDateNormDateType;
	}

	public void setListDateNormDateType(List<SelectItem> listDateNormDateType) {
		this.listDateNormDateType = listDateNormDateType;
	}

	public Boolean getIsCurrentTimeUsing() {
		return isCurrentTimeUsing;
	}

	public void setIsCurrentTimeUsing(Boolean isCurrentTimeUsing) {
		this.isCurrentTimeUsing = isCurrentTimeUsing;
	}

	public Boolean getIsStaticInput() {
		return isStaticInput;
	}

	public void setIsStaticInput(Boolean isStaticInput) {
		this.isStaticInput = isStaticInput;
	}

	public List<SelectItem> getListZoneType() {
		this.listZoneType = new ArrayList<>();
		listZoneType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE, "ZONE"));
		listZoneType.add(new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerZoneType.ZONE_MAP, "ZONE_MAP"));
		// listZoneType.add(new
		// SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_NET_ZONE,
		// "GEO_NET_ZONE"));
		listZoneType.add(
				new SelectItem(com.viettel.ocs.constant.Normalizer.NormalizerZoneType.GEO_HOME_ZONE, "GEO_HOME_ZONE"));
		return listZoneType;
	}

	public void setListZoneType(List<SelectItem> listZoneType) {
		this.listZoneType = listZoneType;
	}

	public List<SelectItem> getListZoneID() {
		return listZoneID;
	}

	public void setListZoneID(List<SelectItem> listZoneID) {
		this.listZoneID = listZoneID;
	}

	public List<dateInWeek> getListDateInWeekTMP() {
		this.listDateInWeekTMP = new ArrayList<>();
		this.listDateInWeekTMP.add(new dateInWeek(1, "#000000", "#FFFFFF", new NormParam(), ""));
		this.listDateInWeekTMP.add(new dateInWeek(2, "#000000", "#FFFFFF", new NormParam(), ""));
		this.listDateInWeekTMP.add(new dateInWeek(3, "#000000", "#FFFFFF", new NormParam(), ""));
		this.listDateInWeekTMP.add(new dateInWeek(4, "#000000", "#FFFFFF", new NormParam(), ""));
		this.listDateInWeekTMP.add(new dateInWeek(5, "#000000", "#FFFFFF", new NormParam(), ""));
		this.listDateInWeekTMP.add(new dateInWeek(6, "#000000", "#FFFFFF", new NormParam(), ""));
		this.listDateInWeekTMP.add(new dateInWeek(7, "#000000", "#FFFFFF", new NormParam(), ""));
		return listDateInWeekTMP;
	}

	public void setListDateInWeekTMP(List<dateInWeek> listDateInWeekTMP) {
		this.listDateInWeekTMP = listDateInWeekTMP;
	}

	public List<tableTimeType> getListTableTimeType() {
		return listTableTimeType;
	}

	public void setListTableTimeType(List<tableTimeType> listTableTimeType) {
		this.listTableTimeType = listTableTimeType;
	}

	public List<SelectItem> getListHour() {
		this.listHour = new ArrayList<>();
		for (Long i = 0l; i < 24; i++) {
			this.listHour.add(new SelectItem(i, i.toString()));
		}
		return listHour;
	}

	public void setListHour(List<SelectItem> listHour) {
		this.listHour = listHour;
	}

	public List<SelectItem> getListMinute() {
		this.listMinute = new ArrayList<>();
		for (Long i = 0l; i < 60; i++) {
			this.listMinute.add(new SelectItem(i, i.toString()));
		}
		return listMinute;
	}

	public void setListMinute(List<SelectItem> listMinute) {
		this.listMinute = listMinute;
	}

	public List<SelectItem> getListSecond() {
		this.listSecond = new ArrayList<>();
		for (Long i = 0l; i < 60; i++) {
			this.listSecond.add(new SelectItem(i, i.toString()));
		}
		return listSecond;
	}

	public void setListSecond(List<SelectItem> listSecond) {
		this.listSecond = listSecond;
	}

	public List<SelectItem> getListDay() {
		this.listDay = new ArrayList<>();
		this.listDay.add(new SelectItem((long) 1, "Sunday"));
		this.listDay.add(new SelectItem((long) 2, "Monday"));
		this.listDay.add(new SelectItem((long) 3, "Tuesday"));
		this.listDay.add(new SelectItem((long) 4, "Wednesday"));
		this.listDay.add(new SelectItem((long) 5, "Thursday"));
		this.listDay.add(new SelectItem((long) 6, "Friday"));
		this.listDay.add(new SelectItem((long) 7, "Satuday"));
		return listDay;
	}

	public String getStringDay(Integer day) {
		String dayStr = "";
		switch (day.intValue()) {
		case 1:
			dayStr = "Sunday";
			break;
		case 2:
			dayStr = "Monday";
			break;
		case 3:
			dayStr = "Tuesday";
			break;
		case 4:
			dayStr = "Wednesday";
			break;
		case 5:
			dayStr = "Thursday";
			break;
		case 6:
			dayStr = "Friday";
			break;
		case 7:
			dayStr = "Satuday";
			break;
		default:
			break;
		}

		return dayStr;
	}

	public void setListDay(List<SelectItem> listDay) {
		this.listDay = listDay;
	}

	public tableNormValueParam getTmpNormValueParamItem() {
		return tmpNormValueParamItem;
	}

	public void setTmpNormValueParamItem(tableNormValueParam tmpNormValueParamItem) {
		this.tmpNormValueParamItem = tmpNormValueParamItem;
	}

	public Boolean getTmpNormValueParamItemIsStart() {
		return tmpNormValueParamItemIsStart;
	}

	public void setTmpNormValueParamItemIsStart(Boolean tmpNormValueParamItemIsStart) {
		this.tmpNormValueParamItemIsStart = tmpNormValueParamItemIsStart;
	}

	public ParameterDAO getParameterDAO() {
		return parameterDAO;
	}

	public void setParameterDAO(ParameterDAO parameterDAO) {
		this.parameterDAO = parameterDAO;
	}

	public NormalizerDAO getNormDAO() {
		return normDAO;
	}

	public void setNormDAO(NormalizerDAO normDAO) {
		this.normDAO = normDAO;
	}

	public List<NormValue> getListNormValueTmp() {
		return listNormValueTmp;
	}

	public void setListNormValueTmp(List<NormValue> listNormValueTmp) {
		this.listNormValueTmp = listNormValueTmp;
	}

	public List<NormValue> getListNormValueToDelete() {
		return listNormValueToDelete;
	}

	public void setListNormValueToDelete(List<NormValue> listNormValueToDelete) {
		this.listNormValueToDelete = listNormValueToDelete;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public List<NormParam> getListNormParamToDelete() {
		return listNormParamToDelete;
	}

	public void setListNormParamToDelete(List<NormParam> listNormParamToDelete) {
		this.listNormParamToDelete = listNormParamToDelete;
	}

	public String getNormName() {
		return normName;
	}

	public void setNormName(String normName) {
		this.normName = normName;
	}

	public ZoneMapDAO getZoneMapDAO() {
		return zoneMapDAO;
	}

	public void setZoneMapDAO(ZoneMapDAO zoneMapDAO) {
		this.zoneMapDAO = zoneMapDAO;
	}

	public GeoNetZoneDAO getGeoNetDAO() {
		return geoNetDAO;
	}

	public void setGeoNetDAO(GeoNetZoneDAO geoNetDAO) {
		this.geoNetDAO = geoNetDAO;
	}

	// End
	// ---------------------------------------------------------------------------------------------------------------------------

}

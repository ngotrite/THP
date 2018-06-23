package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.TreeNodeType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.EventDAO;
import com.viettel.ocs.dao.NestedObjectDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.OfferPackageDAO;
import com.viettel.ocs.dao.PcTypeDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.RateTableDAO;
//import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.NestedObject;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;

@ManagedBean(name = "treeOfferDialogBean")
@ViewScoped
public class TreeOfferDialogBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = -8289000556477276894L;
	private TreeNode root;
	private RateTableDAO rateTableDAO;
	private DecisionTableDAO decisionTableDAO;
	private BlockDAO blockDAO;
	private CategoryDAO catDao;
	private NormalizerDAO normalizerDAO;
	private NestedObjectDAO objectDAO;
	private PriceComponentDAO pcDao;
	private DynamicReserveDAO dynamicReserveDao;
	private SortPriceComponentDAO sortPCDao;
	private ActionTypeDAO actionTypeDAO;
	private EventDAO eventDAO;
	private OfferPackageDAO offerPackageDAO;
	private ActionDAO actionDao;
	private OfferDAO offerDAO;
	private PcTypeDAO pcTypeDAO;
	private String treeType;
	private int catType;
	private Map<Long, TreeNode> mapCatNode = new HashMap<Long, TreeNode>();
	private List<Long> lstAllCatID = new ArrayList<Long>();
	private Map<String, TreeNode> mapAllNode = new HashMap<String, TreeNode>();
	private Object objReturn;
	private String title;
	private String categoryTypeName;

	public String getCategoryTypeName() {
		return categoryTypeName;
	}

	public void setCategoryTypeName(String categoryTypeName) {
		this.categoryTypeName = categoryTypeName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Object getObjReturn() {
		return objReturn;
	}

	public void setObjReturn(Object objReturn) {
		this.objReturn = objReturn;
	}

	private TreeNode treeNodeSelected;

	public TreeNode getTreeNodeSelected() {
		return treeNodeSelected;
	}

	public void setTreeNodeSelected(TreeNode treeNodeSelected) {
		this.treeNodeSelected = treeNodeSelected;
	}

	private boolean isMulti = false;
	private List<Long> lstSelectedID;
	private TreeNode[] selectedNodes;
	private Object[] objReturnArr;

	public boolean isMulti() {
		return isMulti;
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public Object[] getObjReturnArr() {
		return objReturnArr;
	}

	public void setObjReturnArr(Object[] objReturnArr) {
		this.objReturnArr = objReturnArr;
	}

	public int getCatType() {
		return catType;
	}

	public void setCatType(int catType) {
		this.catType = catType;
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

	public List<Long> getLstAllCatID() {
		return lstAllCatID;
	}

	public void setLstAllCatID(List<Long> lstAllCatID) {
		this.lstAllCatID = lstAllCatID;
	}

	public Map<String, TreeNode> getMapAllNode() {
		return mapAllNode;
	}

	public void setMapAllNode(Map<String, TreeNode> mapAllNode) {
		this.mapAllNode = mapAllNode;
	}

	public TreeOfferDialogBean() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		treeType = params.get("treeType");
		lstSelectedID = new ArrayList<Long>();
		if (treeType.contains(";")) {
			String[] parts = treeType.split(";");
			treeType = parts[0];
			categoryTypeName = parts[1];
			catType = Integer.parseInt(parts[2]);
			isMulti = Boolean.parseBoolean(parts[3]);
			String[] ids = parts[4].split(",");
			for(String id : ids) {
				lstSelectedID.add(Long.parseLong(id));
			}
		} else
			return;
		title = super.readProperties("common.select") + " " + categoryTypeName;
		catDao = new CategoryDAO();
		rateTableDAO = new RateTableDAO();
		blockDAO = new BlockDAO();
		normalizerDAO = new NormalizerDAO();
		decisionTableDAO = new DecisionTableDAO();
		objectDAO = new NestedObjectDAO();
		pcDao = new PriceComponentDAO();
		dynamicReserveDao = new DynamicReserveDAO();
		sortPCDao = new SortPriceComponentDAO();
		actionTypeDAO = new ActionTypeDAO();
		eventDAO = new EventDAO();
		offerPackageDAO = new OfferPackageDAO();
		actionDao = new ActionDAO();
		offerDAO = new OfferDAO();
		pcTypeDAO = new PcTypeDAO();
		buildTree(treeType);
	}

	private void buildTree(String treeType) {

		if (treeType == null)
			return;

		root = new DefaultTreeNode();

		switch (treeType) {
		case TreeType.OFFER_BLOCK:
			buildTreeBlock();
			break;
		case TreeType.OFFER_RATE_TABLE:
			buildTreeRateTable();
			break;
		case TreeType.OFFER_DECISION_TABLE:
			buildTreeDecisionTable();
			break;
		case TreeType.OFFER_NORMALIZER:
			buildTreeNormalizer();
			break;
		case TreeType.OFFER_OBJECT:
			buildTreeObject();
			break;
		case TreeType.OFFER_PRICE_COMPONENT:
			buildTreePriceComponent();
			break;
		case TreeType.OFFER_DYNAMIC_RESERVE:
			buildTreeDynamicReserve();
			break;
		case TreeType.OFFER_SORT_PRICE_COMPONENT:
			buildPriceComponentSort();
			break;
		case TreeType.OFFER_ACTION_TYPE:
			buildActionType();
			break;
		case TreeType.OFFER_ACTION:
			buildAction();
			break;
		case TreeType.OFFER_EVENT_EVENT:
			buildEvent();
			break;
		case TreeType.OFFER_PACKAGE:
			buildOfferPackage();
			break;
		case TreeType.OFFER_TREE_NORMAL_ALL_DLG:
			this.treeType = TreeType.OFFER_TREE_NORMAL_DLG;
			buildOfferSubcription("all");
			break;
		case TreeType.OFFER_TREE_NORMAL_DLG:
			buildOfferSubcription("normal");
			break;
		case TreeType.OFFER_TREE_SUBCRIPTION_MAIN:
			buildOfferSubcription("main");
			break;
		case TreeType.OFFER_PC_TYPE:
			buildTreePcType();
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
		List<Category> lstCat = new ArrayList<Category>();
		if (catType.equals(CategoryType.OFF_EVENT_EVENT) || catType.equals(CategoryType.OFF_EVENT_ACTION_TYPE)) {
			lstCat = catDao.findByTypeWithoutDomain(catType);
		} else {
			lstCat = catDao.findByType(catType);
		}
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

	private void buildTreeBlock() {
		buildTreeRootParent(treeType, root);

		List<Block> lstBlock = blockDAO.findByCategoriesWithinGenerated(lstAllCatID);
		for (Block block : lstBlock) {

			if(lstSelectedID.contains(block.getBlockId()))
				continue;

			TreeNode catNode = mapCatNode.get(block.getCategoryId());
			if (catNode != null) {
				TreeNode blockNode = new DefaultTreeNode(catNode.getType(), block, catNode);
				blockNode.setExpanded(true);
				mapAllNode.put(block.getUniqueKey(), blockNode);
			}
		}
	}

	private void buildTreeRateTable() {
		buildTreeRootParent(treeType, root);

		List<RateTable> lstRateTable = rateTableDAO.findByCatAndNotGend(lstAllCatID);
		for (RateTable rateTable : lstRateTable) {

			if(lstSelectedID.contains(rateTable.getRateTableId()))
				continue;

			TreeNode catNode = mapCatNode.get(rateTable.getCategoryId());
			if (catNode != null) {
				TreeNode rateTableNode = new DefaultTreeNode(catNode.getType(), rateTable, catNode);
				rateTableNode.setExpanded(true);
				mapAllNode.put(rateTable.getUniqueKey(), rateTableNode);
			}
		}
	}

	private void buildTreeDecisionTable() {
		buildTreeRootParent(treeType, root);

		List<DecisionTable> decisionTables = decisionTableDAO.findByListCategoryIdAndGened(lstAllCatID);
		for (DecisionTable decisionTable : decisionTables) {

			if(lstSelectedID.contains(decisionTable.getDecisionTableId()))
				continue;

			TreeNode catNode = mapCatNode.get(decisionTable.getCategoryId());
			if (catNode != null) {
				TreeNode decisionTableNode = new DefaultTreeNode(catNode.getType(), decisionTable, catNode);
				decisionTableNode.setExpanded(true);
				mapAllNode.put(decisionTable.getUniqueKey(), decisionTableNode);
			}
		}
	}

	private void buildTreeNormalizer() {
		buildTreeRootParent(treeType, root);
		List<Normalizer> lstNormalizer = normalizerDAO.findByListCategoryIdAndGened(lstAllCatID);
		for (Normalizer normalizer : lstNormalizer) {

			if(lstSelectedID.contains(normalizer.getNormalizerId()))
				continue;

			TreeNode catNode = mapCatNode.get(normalizer.getCategoryId());
			if (catNode != null) {
				TreeNode parameterNode = new DefaultTreeNode(catNode.getType(), normalizer, catNode);
				parameterNode.setExpanded(true);
				mapAllNode.put(normalizer.getUniqueKey(), parameterNode);
			}
		}
	}

	private void buildTreeObject() {

		root = new DefaultTreeNode("Objects", null);
		List<NestedObject> lstObjByParent = objectDAO.findObjectByObjParentNull();
		addNodeObject(root, lstObjByParent);
	}

	private void addNodeObject(TreeNode parentNode, List<NestedObject> lstObjByParent) {
		for (NestedObject nestedObject : lstObjByParent) {
			TreeNode objectNode = new DefaultTreeNode(parentNode.getType(), nestedObject, parentNode);
			objectNode.setExpanded(true);
			mapAllNode.put(nestedObject.getUniqueKey(), objectNode);
			List<NestedObject> lstChildObjByParent = objectDAO.findChildObjectByObjId(nestedObject.getObjId());
			if (lstChildObjByParent != null)
				addNodeObject(objectNode, lstChildObjByParent);
			else
				return;
		}
	}

	/**** OFFER_PRICE_COMPONENT ****/
	private void buildTreePriceComponent() {
		// Build children categories
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<PriceComponent> lstPC = pcDao.findPriceComponentByCategoryIdAndNotGened(lstAllCatID);
		for (PriceComponent pc : lstPC) {

			if(lstSelectedID.contains(pc.getPriceComponentId()))
				continue;

			TreeNode catNode = mapCatNode.get(pc.getCategoryId());
			if (catNode != null) {
				TreeNode pcNode = new DefaultTreeNode(catNode.getType(), pc, catNode);
				pcNode.setExpanded(true);
				mapAllNode.put(pc.getUniqueKey(), pcNode);
			}
		}
	}

	private void buildTreeDynamicReserve() {
		buildTreeRootParent(treeType, root);
		List<DynamicReserve> lstDynamicReserve = dynamicReserveDao
				.findDynamicReservetByCategoryIdAndNotGened(lstAllCatID);
		for (DynamicReserve dr : lstDynamicReserve) {

			if(lstSelectedID.contains(dr.getDynamicReserveId()))
				continue;

			TreeNode catNode = mapCatNode.get(dr.getCategoryId());
			if (catNode != null) {
				TreeNode drNode = new DefaultTreeNode(catNode.getType(), dr, catNode);
				drNode.setExpanded(true);
				mapAllNode.put(dr.getUniqueKey(), drNode);
			}
		}
	}

	private void buildPriceComponentSort() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<SortPriceComponent> lstSortPC = sortPCDao.findSortPriceComponentByCategoryIdAndNotGened(lstAllCatID);
		for (SortPriceComponent sortPC : lstSortPC) {

			if(lstSelectedID.contains(sortPC.getSortPriceComponentId()))
				continue;

			TreeNode catNode = mapCatNode.get(sortPC.getCategoryId());
			if (catNode != null) {
				TreeNode sortPCNode = new DefaultTreeNode(catNode.getType(), sortPC, catNode);
				sortPCNode.setExpanded(true);
				mapAllNode.put(sortPC.getUniqueKey(), sortPCNode);
			}
		}
	}

	private void buildActionType() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<ActionType> lstActionType = actionTypeDAO.findByListCategory(lstAllCatID);
		for (ActionType actionType : lstActionType) {

			if(lstSelectedID.contains(actionType.getActionTypeId()))
				continue;

			TreeNode catNode = mapCatNode.get(actionType.getCategoryId());
			if (catNode != null) {
				TreeNode actionTypeNode = new DefaultTreeNode(catNode.getType(), actionType, catNode);
				actionTypeNode.setExpanded(true);
				mapAllNode.put(actionType.getUniqueKey(), actionTypeNode);
			}
		}
	}

	private void buildEvent() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<Event> lstEvent = eventDAO.findByListCategory(lstAllCatID);
		for (Event event : lstEvent) {

			if(lstSelectedID.contains(event.getEventId()))
				continue;

			TreeNode catNode = mapCatNode.get(event.getCategoryId());
			if (catNode != null) {
				TreeNode eventNode = new DefaultTreeNode(catNode.getType(), event, catNode);
				eventNode.setExpanded(true);
				mapAllNode.put(event.getUniqueKey(), eventNode);
			}
		}
	}

	private void buildOfferPackage() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<OfferPackage> lstOfferPackage = offerPackageDAO.findListOfferPackageByCategoryId(lstAllCatID);
		for (OfferPackage offerPackage : lstOfferPackage) {

			if(lstSelectedID.contains(offerPackage.getOfferPkgId()))
				continue;
			
			if(catType == ContantsUtil.OfferPackage.offerPkgType.NORMAL
					&& (offerPackage.getOfferPkgType() == null || offerPackage.getOfferPkgType() != ContantsUtil.OfferPackage.offerPkgType.NORMAL)) {
				continue;
			}

			TreeNode catNode = mapCatNode.get(offerPackage.getCategoryId());
			if (catNode != null) {
				TreeNode eventNode = new DefaultTreeNode(catNode.getType(), offerPackage, catNode);
				eventNode.setExpanded(true);
				mapAllNode.put(offerPackage.getUniqueKey(), eventNode);
			}
		}
	}

	private void buildAction() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<Action> lstAction = actionDao.findActionsByCategoryIdAndNotGened(lstAllCatID);
		for (Action action : lstAction) {

			if(lstSelectedID.contains(action.getActionId()))
				continue;
			
			TreeNode catNode = mapCatNode.get(action.getCategoryId());
			if (catNode != null && action.getState() != null && action.getState()) {
				TreeNode eventNode = new DefaultTreeNode(catNode.getType(), action, catNode);
				eventNode.setExpanded(true);
				eventNode.setType(TreeNodeType.OFF_ACTION);
				mapAllNode.put(action.getUniqueKey(), eventNode);
			}
		}
	}

	private void buildOfferSubcription(String type) {
		// Build children categories
		buildTreeRootParent(treeType, root);

		// Build cac node fix cung
		Map<Long, String> mapType = CategoryType.getCatTypesByTreeType(treeType);
		Iterator<Long> it = mapType.keySet().iterator();
		while (it.hasNext()) {

			long catTypeParent = it.next();
			if (catTypeParent == CategoryType.OFF_OFFER_SUBSCRIPTION
					|| catTypeParent == CategoryType.OFF_OFFER_ONETIME) {

				Map<Long, String> mapTypeSub = null;
				if (type.equals("all")) {
					mapTypeSub = CategoryType.getCatTypeSubcription(catTypeParent);
				} else if (type.equals("normal")) {
					mapTypeSub = CategoryType.getCatTypeNormal(catTypeParent);
				} else if (type.equals("main")) {
					mapTypeSub = CategoryType.getCatTypeSubcriptionMain(catTypeParent);
				}

				Iterator<Long> it1 = mapTypeSub.keySet().iterator();
				while (it1.hasNext()) {

					long catTypeSub = it1.next();
					Category cat = new Category();
					cat.setTreeType(treeType);
					cat.setCategoryName(mapTypeSub.get(catTypeSub));
					cat.setCategoryType(catTypeSub);
					cat.setCategoryId(0);
					TreeNode node = new DefaultTreeNode(cat, mapAllNode.get(CategoryType.getUniqueKey(catTypeParent)));
					mapAllNode.put(CategoryType.getUniqueKey(catTypeSub), node);
					buildTreeByCat(catTypeSub, node);
				}
			}
		}

		// Add offer belong to Category
		List<Offer> lstOffer = offerDAO.findOfferByListCatId(lstAllCatID);
		for (Offer offer : lstOffer) {

			if (lstSelectedID.contains(offer.getOfferId()))
				continue;
			
			offer.setVersionName(true);
			TreeNode catNode = mapCatNode.get(offer.getCategoryId());
			if (catNode != null) {
				TreeNode offerNode = new DefaultTreeNode(offer, catNode);
				offerNode.setExpanded(true);
				mapAllNode.put(offer.getUniqueKey(), offerNode);
			}
		}
	}
	
	private void buildTreePcType() {
		buildTreeRootParent(treeType, root);
		List<PcType> lstPcType = pcTypeDAO.findPcTypeByCategoryId(lstAllCatID);
		for (PcType pct : lstPcType) {

			if (lstSelectedID.contains(pct.getPcTypeId()))
				continue;

			TreeNode catNode = mapCatNode.get(pct.getCategoryId());
			if (catNode != null) {
				TreeNode pctNode = new DefaultTreeNode(catNode.getType(), pct, catNode);
				pctNode.setExpanded(true);
				mapAllNode.put(pct.getUniqueKey(), pctNode);
			}
		}
	}

	// *********END***********************

	// ********EVENT NODE *********

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
		treeNodeSelected = nodeSlectedEvent.getTreeNode();
		if (treeNodeSelected != null) {
			objReturn = treeNodeSelected.getData();
			if (objReturn instanceof Normalizer) {

			}
		}
	}

	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {
	}

	public void onDialogReturn() {
		RequestContext.getCurrentInstance().closeDialog(null);

	}

	public void onDialogCreate() {
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgNew').show();");

	}

	public void onDialogSelect() {

		if (isMulti) {

			if (selectedNodes == null || selectedNodes.length < 0) {

				super.showMessageWARN("", "", "common.warnSelectObject");
				return;
			} else {

				List<Object> lst = new ArrayList<>();
				for (int i = 0; i < selectedNodes.length; i++) {

					TreeNode node = selectedNodes[i];
					if (node.getData() instanceof Category) {
						continue;
					} else {
						lst.add(node.getData());
					}
				}

				if (lst.size() <= 0) {
					super.showMessageWARN("", "", "common.warnSelectObject");
					return;
				} else {
					objReturnArr = new Object[lst.size()];
					lst.toArray(objReturnArr);
					RequestContext.getCurrentInstance().closeDialog(objReturnArr);
					return;
				}
			}
		} else {
			if (objReturn == null || objReturn instanceof Category) {

				super.showMessageWARN("", "", "common.warnSelectObject");
				return;
			}
			RequestContext.getCurrentInstance().closeDialog(objReturn);
		}
	}
	// *********END****************
}

package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.bean.offer.ActionBean;
import com.viettel.ocs.bean.offer.ActionTypeBean;
import com.viettel.ocs.bean.offer.BlockBean;
import com.viettel.ocs.bean.offer.DecisionTableBean;
import com.viettel.ocs.bean.offer.DynamicReservesBean;
import com.viettel.ocs.bean.offer.EventBean;
import com.viettel.ocs.bean.offer.NormalizerBean;
import com.viettel.ocs.bean.offer.OfferBean;
import com.viettel.ocs.bean.offer.OfferPackageBean;
import com.viettel.ocs.bean.offer.PcTypeBean;
import com.viettel.ocs.bean.offer.PriceComponentsBean;
import com.viettel.ocs.bean.offer.RateTableBean;
import com.viettel.ocs.bean.offer.SortPriceComponentBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil.OfferState;
import com.viettel.ocs.constant.TreeNodeType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.EventDAO;
import com.viettel.ocs.dao.GeneralObjectDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.OfferPackageDAO;
import com.viettel.ocs.dao.PcTypeDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionTableDump;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.GeneralObject;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.OfferDump;
import com.viettel.ocs.entity.OfferPackage;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.RateTableDump;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.model.OcsTreeNode;
import com.viettel.ocs.model.OcsTreeNodeComparator;
import com.viettel.ocs.util.CommonUtil;

@ManagedBean(name = "treeOfferBean")
@ViewScoped
public class TreeOfferBean extends BaseController implements Serializable {

	/**
	 * huannn
	 */
	private static final long serialVersionUID = -4432210493238596849L;
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
	private GeneralObjectDAO generalObjectDAO;
	private PcTypeDAO pcTypeDAO;
	
	private boolean fixParentCatNode;
	private boolean showBtnCatNew;
	private Boolean isShowClone;
	private String formType;
	private String treeType;
	private String contentPage;
	private String contentTitle;

	private TreeNode root;
	private TreeNode selectedNode;
	private Map<Long, TreeNode> mapCatFirstNode = new HashMap<Long, TreeNode>();
	private List<Long> lstCatID = new ArrayList<Long>();
	private Map<Long, TreeNode> mapCatNode = new HashMap<Long, TreeNode>();
	private Map<String, List<TreeNode>> mapListNode = new HashMap<>();
	private BaseEntity currentEntity;
	private BaseEntity dumpEntity;
	private String txtTreeSearch;
	private boolean expandTree;

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
	
	@ManagedProperty("#{pcTypeBean}")
	private PcTypeBean pcTypeBean;
	
	public void setPcTypeBean(PcTypeBean pcTypeBean) {
		this.pcTypeBean = pcTypeBean;
	}

	public TreeOfferBean() {

		catDao = new CategoryDAO();
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
		generalObjectDAO = new GeneralObjectDAO();
		pcTypeDAO = new PcTypeDAO();

		catParentDump = new Category();
		catParentDump.setCategoryName("");
		listCatParent = new ArrayList<Category>();
		listCatSub = new ArrayList<Category>();
		listAllCategory = new ArrayList<Category>();
		dumpEntity = new BaseEntity() {

			@Override
			public String getNodeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@PostConstruct
	public void init() {
		// Nothing to do now

		// Map<String,String> params =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		// String action = params.get("treeType");
		// onload();
	}

	public void onload() {
		// JSF set treeType here via viewParam

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
		case TreeType.OFFER_OFFER:
			buildOffer();
			break;
		case TreeType.OFFER_EVENT:
			buildEvent(true, null, null);
			buildActionType(true, null, null);
			break;
		case TreeType.OFFER_ACTION:
			buildAction(true, null, null);
			break;
		case TreeType.OFFER_DYNAMIC_RESERVE:
			buildDR(true, null, null);
			break;
		case TreeType.OFFER_SORT_PRICE_COMPONENT:
			buildSortPriceComponent(true, null, null);
			break;
		case TreeType.OFFER_POST_PROCESS:
			// todo
			break;
		case TreeType.OFFER_PRICE_COMPONENT:
			buildPC(true, null, null);
			break;
		case TreeType.OFFER_BLOCK:
			buildBlock(true, null, null);
			break;
		case TreeType.OFFER_RATE_TABLE:
			buildRT(true, null, null);
			break;
		case TreeType.OFFER_DECISION_TABLE:
			buildDT(true, null, null);
			break;
		case TreeType.OFFER_NORMALIZER:
			buildNorm(true, null, null);
			break;
		case TreeType.OFFER_PC_TYPE:
			buildPcType(true, null, null);
			break;

		default:
			break;
		}
	}

	private void buildOffer() {

		// Build children categories
		buildTreeByCatType(treeType, root);

		listCatOfferSubscription = new ArrayList<>();
		listCatOfferOneTime = new ArrayList<>();

		// Build cac node fix cung
		Map<Long, String> mapType = CategoryType.getCatTypesByTreeType(treeType);
		Iterator<Long> it = mapType.keySet().iterator();
		while (it.hasNext()) {

			long catTypeParent = it.next();
			if (catTypeParent == CategoryType.OFF_OFFER_SUBSCRIPTION
					|| catTypeParent == CategoryType.OFF_OFFER_ONETIME) {

				Map<Long, String> mapTypeSub = CategoryType.getCatTypeSub(catTypeParent);
				Iterator<Long> itSub = mapTypeSub.keySet().iterator();
				while (itSub.hasNext()) {

					long catTypeSub = itSub.next();
					Category cat = new Category();
					cat.setTreeType(treeType);
					cat.setCategoryName(mapTypeSub.get(catTypeSub));
					cat.setCategoryType(catTypeSub);
					cat.setCategoryId(0L);
					if (catTypeParent == CategoryType.OFF_OFFER_SUBSCRIPTION)
						listCatOfferSubscription.add(cat);
					else
						listCatOfferOneTime.add(cat);

					TreeNode node = new OcsTreeNode(cat, mapCatFirstNode.get(catTypeParent));
					node.setExpanded(true);
					mapCatFirstNode.put(catTypeSub, node);
					buildTreeByCat(catTypeSub, node);
				}
			}
		}

		// Add offer belong to Category
		List<List<Offer>> lstOfferGrouped = offerDao.getGroupListOfferByListCatId(lstCatID);
		for (List<Offer> lstOffer : lstOfferGrouped) {

			if (lstOffer.size() == 0)
				continue;

			Offer offer = lstOffer.get(0);
			TreeNode catNode = mapCatNode.get(offer.getCategoryId());
			if (catNode != null) {

				OfferDump offerDump = new OfferDump();
				offerDump.setCategoryId(offer.getCategoryId());
				offerDump.setOfferName(offer.getOfferName());
				offerDump.setOfferExternalId(offer.getOfferExternalId());
				
				if(!isFoundNode(offerDump))
					continue;

				TreeNode offerDumpNode = new OcsTreeNode(offerDump, catNode);
				offerDumpNode.setType(TreeNodeType.OFF_OFFER_GROUP);
				// offerDumpNode.setExpanded(true);
				for (Offer version : lstOffer) {

					TreeNode versionNode = new OcsTreeNode(version, offerDumpNode);
					settingNewTreeNode(version, versionNode);
				}
			}
		}

		// Add offer package belong to Category
		List<OfferPackage> offerPackages = offerPackageDAO.findListOfferPackageByCategoryId(lstCatID);
		for (OfferPackage offerPackage : offerPackages) {

			if(!isFoundNode(offerPackage))
				continue;

			TreeNode catNode = mapCatNode.get(offerPackage.getCategoryId());
			if (catNode != null) {

				TreeNode offerPackageNode = new OcsTreeNode(offerPackage, catNode);
				settingNewTreeNode(offerPackage, offerPackageNode);
			}
		}
	}

	private void buildOfferOfOfferPackage(OfferPackage offerPkg, TreeNode offerPkgNode) {

		if (offerPkg.getMainOfferId() != null && offerPkg.getMainOfferId() > 0) {

			Offer offer = offerDao.get(offerPkg.getMainOfferId());
			offer.setVersionName(true);
			TreeNode offerNode = new OcsTreeNode(offer, offerPkgNode);
			settingNewTreeNode(offer, offerNode);
		}

		List<Offer> lstOffer = offerDao.findListByOfferPackage(offerPkg.getOfferPkgId());
		for (Offer offer : lstOffer) {

			TreeNode offerNode = new OcsTreeNode(offer, offerPkgNode);
			offer.setVersionName(true);
			settingNewTreeNode(offer, offerNode);
		}
	}

	private void buildAction(boolean isBuildCat, Offer offer, TreeNode offerNode) {

		if (isBuildCat) {
			buildTreeByCatType(treeType, root);
			List<Action> lstAction = actionDao.findActionsByCategoryId(lstCatID);
			for (Action action : lstAction) {

				if(!isFoundNode(action))
					continue;
				
				TreeNode catNode = mapCatNode.get(action.getCategoryId());
				TreeNode actionNode = new OcsTreeNode(action, catNode);
				settingNewTreeNode(action, actionNode);
			}
		} else {

			List<Action> lstAction = actionDao.findActionByOffer(offer.getOfferId());
//			TreeNode dumpNode = createActionDumpNode(offerNode);
			TreeNode actionNode;

			for (Action action : lstAction) {
//				if (action.getActionType() != null) {
//					ActionType actionType = actionTypeDAO.get(action.getActionType());
//					if (actionType != null && actionType.getForProvisioning() != null && actionType.getForProvisioning()) {
//						actionNode = new OcsTreeNode(action, dumpNode);
//					} else {
//						actionNode = new OcsTreeNode(action, offerNode);
//					}
//				} else {
					actionNode = new OcsTreeNode(action, offerNode);
//				}
				settingNewTreeNode(action, actionNode);
			}
		}
	}

	private void buildPC(boolean isBuildCat, Action action, TreeNode actionNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<PriceComponent> lstPC = pcDao.findPriceComponentByCategoryId(lstCatID);
			for (PriceComponent pc : lstPC) {

				if(!isFoundNode(pc))
					continue;
				
				TreeNode catNode = mapCatNode.get(pc.getCategoryId());
				TreeNode pcNode = new OcsTreeNode(pc, catNode);
				settingNewTreeNode(pc, pcNode);
			}
		} else {

			List<PriceComponent> lstPC = pcDao.findPCByAction(action.getActionId());
			for (PriceComponent pc : lstPC) {

				TreeNode pcNode = new OcsTreeNode(pc, actionNode);
				settingNewTreeNode(pc, pcNode);
			}
		}
	}

	private void buildBlock(boolean isBuildCat, PriceComponent pc, TreeNode pcNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<Block> lstBlock = blockDAO.findByListCategory(lstCatID);
			for (Block block : lstBlock) {

				if(!isFoundNode(block))
					continue;
				
				TreeNode catNode = mapCatNode.get(block.getCategoryId());
				if (catNode != null) {

					TreeNode blockNode = new OcsTreeNode(block, catNode);
					settingNewTreeNode(block, blockNode);
				}
			}
		} else {

			List<Block> lstBlock = blockDAO.findBlockByPC(pc.getPriceComponentId());
			for (Block block : lstBlock) {

				TreeNode blockNode = new OcsTreeNode(block, pcNode);
				settingNewTreeNode(block, blockNode);
			}
		}
	}

	private void buildSortPriceComponent(boolean isBuildCat, Action action, TreeNode actionNode) {
		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<SortPriceComponent> lstSortPriceComponent = sortPriceComponentDAO
					.findSortPriceComponentByCategoryId(lstCatID);
			for (SortPriceComponent sortPriceComponent : lstSortPriceComponent) {

				if(!isFoundNode(sortPriceComponent))
					continue;
				
				TreeNode catNode = mapCatNode.get(sortPriceComponent.getCategoryId());
				TreeNode sortPriceComponentNode = new OcsTreeNode(sortPriceComponent, catNode);
				settingNewTreeNode(sortPriceComponent, sortPriceComponentNode);
			}
		} else {

			SortPriceComponent spc = sortPriceComponentDAO.get(action.getSortPriceComponentId());
			if (spc != null) {

				TreeNode spcNode = new OcsTreeNode(spc, actionNode);
				settingNewTreeNode(spc, spcNode);
			}
			
			SortPriceComponent spc2 = sortPriceComponentDAO.get(action.getPriorityFilterId());
			if (spc2 != null) {

				TreeNode spcNode = new OcsTreeNode(spc2, actionNode);
				settingNewTreeNode(spc2, spcNode);
			}
		}
	}

	private void buildRT(boolean isBuildCat, Block block, TreeNode blockNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<RateTable> lstRateTable = rateTableDAO.findByListCategory(lstCatID);
			for (RateTable rateTable : lstRateTable) {

				if(!isFoundNode(rateTable))
					continue;
				
				TreeNode catNode = mapCatNode.get(rateTable.getCategoryId());
				if (catNode != null) {

					TreeNode rateTableNode = new OcsTreeNode(rateTable, catNode);
					settingNewTreeNode(rateTable, rateTableNode);
				}
			}
		} else {

			List<RateTable> lstRateTableBasic = blockDAO.findRatetableByBlockIdType(block.getBlockId(),
					com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC);
			List<RateTable> lstRateTableDiscount = blockDAO.findRatetableByBlockIdType(block.getBlockId(),
					com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT);
			List<RateTable> lstRateTableCondition = blockDAO.findRatetableByBlockIdType(block.getBlockId(),
					com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION);

			// Basic RT
			if (lstRateTableBasic.size() > 0) {

				TreeNode rtDumpNode = createRTDumpNode(blockNode,
						com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC);
				for (RateTable rateTable : lstRateTableBasic) {

					TreeNode rateTableNode = new OcsTreeNode(rateTable, rtDumpNode);
					settingNewTreeNode(rateTable, rateTableNode);
				}
			}

			// Discount RT
			if (lstRateTableDiscount.size() > 0) {

				TreeNode rtDumpNode = createRTDumpNode(blockNode,
						com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT);
				for (RateTable rateTable : lstRateTableDiscount) {

					TreeNode rateTableNode = new OcsTreeNode(rateTable, rtDumpNode);
					settingNewTreeNode(rateTable, rateTableNode);
				}
			}

			// Condition RT
			if (lstRateTableCondition.size() > 0) {

				TreeNode rtDumpNode = createRTDumpNode(blockNode,
						com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION);
				for (RateTable rateTable : lstRateTableCondition) {

					TreeNode rateTableNode = new OcsTreeNode(rateTable, rtDumpNode);
					settingNewTreeNode(rateTable, rateTableNode);
				}
			}
		}
	}

	private TreeNode createRTDumpNode(TreeNode blockNode, long componentType) {

		RateTableDump rtDump = new RateTableDump();
		rtDump.setComponentType(componentType);
		rtDump.setBlockId(((Block) blockNode.getData()).getBlockId());
		if (com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC == componentType) {
			rtDump.setRateTableName("Basic");
		} else if (com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT == componentType) {
			rtDump.setRateTableName("Discount");
		} else if (com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION == componentType) {
			rtDump.setRateTableName("Condition");
		}

		TreeNode rtDumpNode = new OcsTreeNode(rtDump, blockNode);
		rtDumpNode.setType(TreeNodeType.OFF_DUMP_NODE);
		rtDumpNode.setExpanded(true);
		return rtDumpNode;
	}

	private TreeNode createActionDumpNode(TreeNode offerNode) {

		ActionTableDump actionTableDump = new ActionTableDump();
		actionTableDump.setOfferId(((Offer) offerNode.getData()).getOfferId());
		actionTableDump.setName("Provisioning");

		TreeNode rtDumpNode = new OcsTreeNode(actionTableDump, offerNode);
		rtDumpNode.setType(TreeNodeType.ACTION_DUMP_NODE);
		rtDumpNode.setExpanded(true);
		return rtDumpNode;
	}

	private void buildRTOfDR(boolean isBuildCat, DynamicReserve dynamicReserve, TreeNode blockNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<RateTable> lstRateTable = rateTableDAO.findByListCategory(lstCatID);
			for (RateTable rateTable : lstRateTable) {

				if(!isFoundNode(rateTable))
					continue;
				
				TreeNode catNode = mapCatNode.get(rateTable.getCategoryId());
				if (catNode != null) {

					TreeNode rateTableNode = new OcsTreeNode(rateTable, catNode);
					settingNewTreeNode(rateTable, rateTableNode);
				}
			}
		} else {

			List<RateTable> lstRateTable = rateTableDAO
					.findRateTableByDynamicReserve(dynamicReserve.getDynamicReserveId());
			for (RateTable rateTable : lstRateTable) {

				TreeNode rateTableNode = new OcsTreeNode(rateTable, blockNode);
				settingNewTreeNode(rateTable, rateTableNode);
			}
		}
	}

	private void buildRTOfSPC(boolean isBuildCat, SortPriceComponent sortPriceComponent, TreeNode blockNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<RateTable> lstRateTable = rateTableDAO.findByListCategory(lstCatID);
			for (RateTable rateTable : lstRateTable) {

				if(!isFoundNode(rateTable))
					continue;
				
				TreeNode catNode = mapCatNode.get(rateTable.getCategoryId());
				if (catNode != null) {

					TreeNode rateTableNode = new OcsTreeNode(rateTable, catNode);
					settingNewTreeNode(rateTable, rateTableNode);
				}
			}
		} else {

			List<RateTable> lstRateTable = rateTableDAO
					.findRateTableBySPC(sortPriceComponent.getSortPriceComponentId());
			for (RateTable rateTable : lstRateTable) {

				TreeNode rateTableNode = new OcsTreeNode(rateTable, blockNode);
				settingNewTreeNode(rateTable, rateTableNode);
			}
		}
	}

	private void buildDT(boolean isBuildCat, RateTable rateTable, TreeNode rateTableNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<DecisionTable> decisionTables = decisionTableDAO.findByListCategoryId(lstCatID);
			for (DecisionTable decisionTable : decisionTables) {

				TreeNode catNode = mapCatNode.get(decisionTable.getCategoryId());
				if (catNode != null) {

					if(!isFoundNode(decisionTable))
						continue;
					
					TreeNode decisionTableNode = new OcsTreeNode(decisionTable, catNode);
					settingNewTreeNode(decisionTable, decisionTableNode);
				}
			}
		} else {

			DecisionTable decisionTable = decisionTableDAO.get(rateTable.getDecisionTableId());
			if (decisionTable != null) {

				TreeNode decisionTableNode = new OcsTreeNode(decisionTable, rateTableNode);
				settingNewTreeNode(decisionTable, decisionTableNode);
			}
		}
	}

	private void buildNorm(boolean isBuildCat, DecisionTable decisionTable, TreeNode decisionTableNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<Normalizer> lstNormalizer = normalizerDAO.findByListCategory(lstCatID);
			for (Normalizer normalizer : lstNormalizer) {

				if(!isFoundNode(normalizer))
					continue;
				
				TreeNode catNode = mapCatNode.get(normalizer.getCategoryId());
				if (catNode != null) {
					TreeNode normNode = new OcsTreeNode(normalizer, catNode);
					settingNewTreeNode(normalizer, normNode);
				}
			}
		} else {

			List<Normalizer> lstNormalizer = decisionTableDAO.findNormalizers(decisionTable);
			for (Normalizer normalizer : lstNormalizer) {

				TreeNode normNode = new OcsTreeNode(normalizer, decisionTableNode);
				settingNewTreeNode(normalizer, normNode);
			}
		}
	}

	private void buildEvent(boolean isBuildCat, Offer offer, TreeNode offerNode) {
		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<Event> events = eventDAO.findByListCategory(lstCatID);

			for (Event event : events) {

				if(!isFoundNode(event))
					continue;
				
				TreeNode catNode = mapCatNode.get(event.getCategoryId());
				if (catNode != null) {
					TreeNode eventNode = new OcsTreeNode(event, catNode);
					settingNewTreeNode(event, eventNode);
				}
			}
		} else {
			// Do nothing now
		}
	}

	private void buildActionType(boolean isBuildCat, Event event, TreeNode offerNode) {
		if (isBuildCat) {

			List<ActionType> actionTypes = actionTypeDAO.findByListCategory(lstCatID);
			for (ActionType actionType : actionTypes) {

				if(!isFoundNode(actionType))
					continue;
				
				TreeNode catNode = mapCatNode.get(actionType.getCategoryId());
				if (catNode != null) {
					TreeNode actionTypeNode = new OcsTreeNode(actionType, catNode);
					settingNewTreeNode(actionType, actionTypeNode);
				}
			}
		} else {

			List<ActionType> actionTypes = actionTypeDAO.findActionTypesByEvent(event);
			for (ActionType actionType : actionTypes) {
				TreeNode actionTypeNode = new OcsTreeNode(actionType, offerNode);
				settingNewTreeNode(actionType, actionTypeNode);
			}
		}
	}

	private void buildDR(boolean isBuildCat, Action action, TreeNode actionNode) {
		if (isBuildCat) {
			buildTreeByCatType(treeType, root);
			List<DynamicReserve> lstDR = dynamicReserveDAO.findDynamicReservetByCategoryId(lstCatID);
			for (DynamicReserve dr : lstDR) {

				if(!isFoundNode(dr))
					continue;
				
				TreeNode catNode = mapCatNode.get(dr.getCategoryId());
				TreeNode drNode = new OcsTreeNode(dr, catNode);
				settingNewTreeNode(dr, drNode);
			}
		} else {

			DynamicReserve dr = dynamicReserveDAO.get(action.getDynamicReserveId());
			if (dr != null) {

				TreeNode drNode = new OcsTreeNode(dr, actionNode);
				settingNewTreeNode(dr, drNode);
			}
		}
	}
	
	private void buildPcType(boolean isBuildCat, PcType pcType, TreeNode treeNode) {
		if (isBuildCat) {
			buildTreeByCatType(treeType, root);
			List<PcType> lstPcType = pcTypeDAO.findPcTypeByCategoryId(lstCatID);
			for (PcType pc : lstPcType) {

				if(!isFoundNode(pc))
					continue;
				
				TreeNode catNode = mapCatNode.get(pc.getCategoryId());
				TreeNode pcNode = new OcsTreeNode(pc, catNode);
				settingNewTreeNode(pc, pcNode);
			}
		}
	}
	
	private void buildBlockOfPcType(boolean isBuildCat, PcType pcType, TreeNode treeNode) {

		if (isBuildCat) {

			buildTreeByCatType(treeType, root);

			List<Block> lstBlock = blockDAO.findByListCategory(lstCatID);
			for (Block block : lstBlock) {

				if(!isFoundNode(block))
					continue;
				
				TreeNode catNode = mapCatNode.get(block.getCategoryId());
				if (catNode != null) {

					TreeNode blNode = new OcsTreeNode(block, catNode);
					settingNewTreeNode(block, blNode);
				}
			}
		} else {
			List<Block> lstBlock = blockDAO.findBlockByPcType(pcType.getPcTypeId());

			for (Block block : lstBlock){
				TreeNode blNode = new OcsTreeNode(block, treeNode);
				settingNewTreeNode(block, blNode);
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
			// node.setType(TreeNodeType.CATEGORY);
			node.setExpanded(true);
			mapCatFirstNode.put(catType, node);
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

	public void removeTreeNode(BaseEntity objEntity, BaseEntity parentEntity) {

		if (objEntity == null || parentEntity == null)
			return;

		List<TreeNode> lst = this.getListNodeByObjectAndParent(objEntity, parentEntity);
		for (TreeNode currentNode : lst) {

			TreeNode parentNode = currentNode.getParent();
			parentNode.getChildren().remove(currentNode);
			currentNode.setParent(null);
			getListNodeByObject(objEntity).remove(currentNode);
		}
	}

	public void removeTreeNodeRateTableBlock(RateTable rt, Block block, long componentType) {

		if (rt == null || block == null)
			return;

		List<TreeNode> lstNode = getListNodeByObject(rt);
		List<TreeNode> lstNode2Remove = new ArrayList<TreeNode>();
		for (TreeNode rtNode : lstNode) {

			TreeNode rtDumpNode = rtNode.getParent();
			if (rtDumpNode.getData() instanceof RateTableDump) {

				RateTableDump objDump = (RateTableDump) rtDumpNode.getData();
				if (objDump.getComponentType() != componentType)
					continue;

				TreeNode blockNode = rtDumpNode.getParent();
				Block objBlock = (Block) blockNode.getData();
				if (objBlock.getBlockId() == block.getBlockId()) {

					if (rtDumpNode.getChildren().size() <= 1) {
						// Neu dump node chi co 1 RT thi remove dumpnode

						blockNode.getChildren().remove(rtDumpNode);
						rtDumpNode.setParent(null);
					} else {
						// Neu dump node co nhieu hon thi chi remove rt node

						rtDumpNode.getChildren().remove(rtNode);
						rtNode.setParent(null);
					}

					lstNode2Remove.add(rtNode);
				}
			}
		}

		lstNode.removeAll(lstNode2Remove);
	}

	public void removeTreeNodeAll(BaseEntity objEntity) {

		if (objEntity == null)
			return;

		List<TreeNode> lstNode = getListNodeByObject(objEntity);
		Iterator<TreeNode> it = lstNode.iterator();
		while (it.hasNext()) {

			TreeNode node = it.next();
			TreeNode parentNode = node.getParent();
			parentNode.getChildren().remove(node);
			node.setParent(null);

			if (objEntity instanceof Offer) {
				if (parentNode.getChildren().isEmpty()) {
					// Doi voi offer, neu xoa het version thi xoa dump node

					TreeNode grandParentNode = parentNode.getParent();
					grandParentNode.getChildren().remove(parentNode);
					parentNode.setParent(null);
				}
			}

			it.remove();
		}
	}

	private void removeTreeNodeRecursive(TreeNode parentNode, BaseEntity objEntity) {

		List<TreeNode> lstChildren = parentNode.getChildren();
		if (lstChildren == null || lstChildren.isEmpty())
			return;

		Iterator<TreeNode> it = lstChildren.iterator();
		while (it.hasNext()) {

			TreeNode childNode = it.next();
			BaseEntity data = (BaseEntity) childNode.getData();
			if (objEntity.getUniqueKey().equals(data.getUniqueKey())) {

				it.remove();
				childNode.setParent(null);
			} else {

				removeTreeNodeRecursive(childNode, objEntity);
			}
		}
	}

	public void switchOfferDeploy(List<Offer> lstOffer) {

		if (lstOffer == null || lstOffer.size() <= 1)
			return;

		Offer offer = lstOffer.get(0);
		List<TreeNode> lstNode = this.getListNodeByObject(offer);
		if (lstNode.isEmpty())
			return;

		TreeNode node = lstNode.get(0);
		TreeNode offerDumpNode = node.getParent();
		offerDumpNode.getChildren().clear();
		for (Offer version : lstOffer) {
			if (version.getState().equals(OfferState.REMOVED)) {
				continue;
			}
			this.getListNodeByObject(version).clear();
			TreeNode versionNode = new OcsTreeNode(version, offerDumpNode);
			settingNewTreeNode(version, versionNode);
		}
	}

	public void updateTreeNodeOffer(Offer offer, Category objCategory, List<? extends BaseEntity> lstChildObject,
			boolean updateVersionOnly) {

		setOfferDumpNodeParent(offer, objCategory, updateVersionOnly);

		List<TreeNode> lstTreeNode = this.getListNodeByObject(offer);
		for (TreeNode node : lstTreeNode) {

			BaseEntity objNode = (BaseEntity) node.getData();
			if (objNode != offer)
				HibernateUtil.copyEntityProperties(offer, objNode);
		}
		updateTreeNodeChildren(lstTreeNode, lstChildObject);
	}

	private void setOfferDumpNodeParent(Offer offer, Category objCategory, boolean updateVersionOnly) {
		TreeNode catNode = mapCatNode.get(objCategory.getCategoryId());
		if (catNode == null)
			return;

		TreeNode nodeCurrent = null;
		List<TreeNode> lstNode = this.getListNodeByObject(offer);
		if (lstNode.size() > 0)
			nodeCurrent = lstNode.get(0);

		if (nodeCurrent != null) {

			TreeNode dumpParentNode = nodeCurrent.getParent();
			if (updateVersionOnly) {

				// Neu cung cat thi bo qua
				OfferDump offerDump = (OfferDump) dumpParentNode.getData();
				if (offerDump.getCategoryId() == objCategory.getCategoryId())
					return;

				// Neu dump node chi co 1 con thi remove toan bo, nguoc lai
				// remove version
				if (dumpParentNode.getChildren().size() <= 1) {

					dumpParentNode.getParent().getChildren().remove(dumpParentNode);
					dumpParentNode.setParent(null);
				} else {

					dumpParentNode.getChildren().remove(nodeCurrent);
					nodeCurrent.setParent(null);
				}

				// Add vao node cat moi
				List<TreeNode> lstChildCatNode = catNode.getChildren();
				boolean found = false;
				for (TreeNode childOfDumpNode : lstChildCatNode) {
					if (childOfDumpNode.getData() instanceof OfferDump) {
						// Neu da co dump node thi add vao

						offerDump = (OfferDump) childOfDumpNode.getData();
						if (offer.equalWithDump(offerDump)) {

							childOfDumpNode.getChildren().add(nodeCurrent);
							nodeCurrent.setParent(childOfDumpNode);
							found = true;
							break;
						}
					}
				}

				if (!found) {
					// Neu chua co dump node thi tao moi

					offerDump = new OfferDump();
					offerDump.setCategoryId(offer.getCategoryId());
					offerDump.setOfferName(offer.getOfferName());
					offerDump.setOfferExternalId(offer.getOfferExternalId());

					TreeNode offerDumpNode = new OcsTreeNode(offerDump, catNode);
					offerDumpNode.setType(TreeNodeType.OFF_OFFER_GROUP);
					offerDumpNode.setExpanded(true);

					offerDumpNode.getChildren().add(nodeCurrent);
					nodeCurrent.setParent(offerDumpNode);
				}
			} else {

				OfferDump offerDump = (OfferDump) dumpParentNode.getData();
				offerDump.setOfferName(offer.getOfferName());
				offerDump.setOfferExternalId(offer.getOfferExternalId());
				offerDump.setCategoryId(offer.getCategoryId());
				if (dumpParentNode.getParent() != catNode) {

					dumpParentNode.getParent().getChildren().remove(dumpParentNode);
					catNode.getChildren().add(dumpParentNode);
					dumpParentNode.setParent(catNode);
				}
			}
		} else {

			List<TreeNode> lstChildCatNode = catNode.getChildren();
			boolean found = false;
			for (TreeNode childOfDumpNode : lstChildCatNode) {
				if (childOfDumpNode.getData() instanceof OfferDump) {
					// Neu da co dump node thi add vao

					OfferDump offerDump = (OfferDump) childOfDumpNode.getData();
					if (offer.equalWithDump(offerDump)) {

						TreeNode newNode = new OcsTreeNode(offer, childOfDumpNode);
						settingNewTreeNode(offer, newNode);
						found = true;
						break;
					}
				}
			}

			if (!found) {
				// Neu chua co dump node thi tao moi

				OfferDump offerDump = new OfferDump();
				offerDump.setCategoryId(offer.getCategoryId());
				offerDump.setOfferName(offer.getOfferName());
				offerDump.setOfferExternalId(offer.getOfferExternalId());

				TreeNode offerDumpNode = new OcsTreeNode(offerDump, catNode);
				offerDumpNode.setType(TreeNodeType.OFF_OFFER_GROUP);
				offerDumpNode.setExpanded(true);

				TreeNode versionNode = new OcsTreeNode(offer, offerDumpNode);
				settingNewTreeNode(offer, versionNode);
			}
		}
	}

	public void updateTreeNode(BaseEntity obj, Category objCategory, List<? extends BaseEntity> lstChildObject) {

		this.setCatNodeParent(obj, objCategory);

		List<TreeNode> lstTreeNode = this.getListNodeByObject(obj);
		for (TreeNode node : lstTreeNode) {

			BaseEntity objNode = (BaseEntity) node.getData();
			if (objNode != obj)
				HibernateUtil.copyEntityProperties(obj, objNode);
		}
		this.updateTreeNodeChildren(lstTreeNode, lstChildObject);
	}

	public void updateTreeNodeBlock(Block obj, Category objCategory, List<RateTable> lstRTBasic,
			List<RateTable> lstRTDiscount, List<RateTable> lstRTCondition) {

		this.setCatNodeParent(obj, objCategory);

		List<TreeNode> lstTreeNode = this.getListNodeByObject(obj);
		List<TreeNode> lstTreeNodeBasic = new ArrayList<TreeNode>();
		List<TreeNode> lstTreeNodeDiscount = new ArrayList<TreeNode>();
		List<TreeNode> lstTreeNodeCondition = new ArrayList<TreeNode>();
		for (TreeNode blockNode : lstTreeNode) {

			removeDumpNode(blockNode);
			List<TreeNode> childrenDumpNode = blockNode.getChildren();
			TreeNode dumpNodeBasic = null;
			TreeNode dumpNodeDiscount = null;
			TreeNode dumpNodeCondition = null;

			for (TreeNode dumpNode : childrenDumpNode) {

				RateTableDump rtDump = (RateTableDump) dumpNode.getData();
				if (rtDump == null)
					continue;

				if (rtDump.getComponentType() == com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC) {
					dumpNodeBasic = dumpNode;
				} else if (rtDump.getComponentType() == com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT) {
					dumpNodeDiscount = dumpNode;
				} else if (rtDump.getComponentType() == com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION) {
					dumpNodeCondition = dumpNode;
				}
			}

			// Dump basic
			if (lstRTBasic != null && lstRTBasic.size() > 0) {
				if (dumpNodeBasic != null) {
					lstTreeNodeBasic.add(dumpNodeBasic);
				} else {
					TreeNode rtDumpNode = createRTDumpNode(blockNode,
							com.viettel.ocs.constant.Normalizer.TypeRateTable.BASIC);
					lstTreeNodeBasic.add(rtDumpNode);
				}
			} else {
				if (dumpNodeBasic != null) {
					dumpNodeBasic.getParent().getChildren().remove(dumpNodeBasic);
					dumpNodeBasic.setParent(null);
				}
			}

			// Dump discount
			if (lstRTDiscount != null && lstRTDiscount.size() > 0) {
				if (dumpNodeDiscount != null) {
					lstTreeNodeDiscount.add(dumpNodeDiscount);
				} else {
					TreeNode rtDumpNode = createRTDumpNode(blockNode,
							com.viettel.ocs.constant.Normalizer.TypeRateTable.DISCOUNT);
					lstTreeNodeDiscount.add(rtDumpNode);
				}
			} else {
				if (dumpNodeDiscount != null) {
					dumpNodeDiscount.getParent().getChildren().remove(dumpNodeDiscount);
					dumpNodeDiscount.setParent(null);
				}
			}

			// Dump condition
			if (lstRTCondition != null && lstRTCondition.size() > 0) {
				if (dumpNodeCondition != null) {
					lstTreeNodeCondition.add(dumpNodeCondition);
				} else {
					TreeNode rtDumpNode = createRTDumpNode(blockNode,
							com.viettel.ocs.constant.Normalizer.TypeRateTable.CONDITION);
					lstTreeNodeCondition.add(rtDumpNode);
				}
			} else {
				if (dumpNodeCondition != null) {
					dumpNodeCondition.getParent().getChildren().remove(dumpNodeCondition);
					dumpNodeCondition.setParent(null);
				}
			}
		}

		this.updateTreeNodeChildren(lstTreeNodeBasic, lstRTBasic);
		this.updateTreeNodeChildren(lstTreeNodeDiscount, lstRTDiscount);
		this.updateTreeNodeChildren(lstTreeNodeCondition, lstRTCondition);
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

			BaseEntity objNode = (BaseEntity) nodeCurrent.getData();
			if (objNode != obj)
				HibernateUtil.copyEntityProperties(obj, objNode);

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

	private void updateTreeNodeChildren(List<TreeNode> lstTreeNode, List<? extends BaseEntity> lstObjChildren) {

		if (lstTreeNode == null)
			return;

		// List<TreeNode> lstTreeNode = this.getListNodeByObject(obj);
		for (TreeNode node : lstTreeNode) {

			// BaseEntity objNode = (BaseEntity) node.getData();
			// if (objNode != obj)
			// HibernateUtil.copyEntityProperties(obj, objNode);

			if (lstObjChildren == null)
				continue;

			// Tim nhung childnode ko thuoc trong list con de remove
			List<TreeNode> lstCurrentChildren = node.getChildren();
			List<TreeNode> lstChildren2Remove = new ArrayList<TreeNode>();
			for (TreeNode childNode : lstCurrentChildren) {

				boolean found = false;
				BaseEntity childNodeEntity = (BaseEntity) childNode.getData();
				for (BaseEntity objChild : lstObjChildren) {

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

			// Tim cac node con can add
			List<BaseEntity> lstObjChildren2Add = new ArrayList<BaseEntity>();
			for (BaseEntity objChild : lstObjChildren) {

				boolean found = false;
				for (TreeNode childNode : lstCurrentChildren) {

					BaseEntity childNodeEntity = (BaseEntity) childNode.getData();
					if (objChild.getUniqueKey().equals(childNodeEntity.getUniqueKey())) {

						HibernateUtil.copyEntityProperties(objChild, childNodeEntity);
						found = true;
						break;
					}
				}
				
				if(!found) {
					for(BaseEntity childAdd : lstObjChildren2Add) {
						if (objChild.getUniqueKey().equals(childAdd.getUniqueKey())) {
							
							found = true;
							break;
						}
					}
				}

				if (!found) {
					lstObjChildren2Add.add(objChild);
				}
			}

			// Add cac child co trong list nhung chua co tren tree
			for (BaseEntity objChild : lstObjChildren2Add) {

				TreeNode newChildNode = new OcsTreeNode(objChild, node);
				settingNewTreeNode(objChild, newChildNode);

				// if (objChild instanceof RateTable) {
				// buildDT(false, (RateTable) objChild, newChildNode);
				// }
			}

			// Sap xep lai node con
			int i = 0;
			for (BaseEntity objChild : lstObjChildren) {
				for (TreeNode childNode : lstCurrentChildren) {

					BaseEntity childNodeEntity = (BaseEntity) childNode.getData();
					if (objChild.getUniqueKey().equals(childNodeEntity.getUniqueKey())) {

						childNodeEntity.setTreePosIdx(i);
						break;
					}
				}

				i++;
			}
			this.sortChildren(node);
		}
	}

	private void updateTreeNode_bak(BaseEntity obj, List<? extends BaseEntity> lstObjChildren) {

		List<TreeNode> lstTreeNode = this.getListNodeByObject(obj);
		for (TreeNode node : lstTreeNode) {

			BaseEntity objNode = (BaseEntity) node.getData();
			if (objNode != obj)
				HibernateUtil.copyEntityProperties(obj, objNode);

			if (lstObjChildren == null || lstObjChildren.size() <= 0)
				continue;

			// Xoa cac node con hien tai
			List<TreeNode> lstCurrentChildren = node.getChildren();
			for (TreeNode childNode : lstCurrentChildren) {

				BaseEntity childNodeEntity = (BaseEntity) childNode.getData();
				getListNodeByObject(childNodeEntity).remove(childNode);
			}
			node.getChildren().clear();

			// Them moi cac node con
			for (BaseEntity objChild : lstObjChildren) {

				TreeNode newChildNode = new OcsTreeNode(objChild, node);
				settingNewTreeNode(objChild, newChildNode);
				// if (objChild instanceof RateTable) {
				// buildDT(false, (RateTable) objChild, newChildNode);
				// }
			}
		}
	}

	public void addChildCatContext(NodeSelectEvent nodeSelectEvent) {

		this.onNodeSelect(nodeSelectEvent);
		btnCatShowDlg(null);
	}

	public void addChildObjectContext(NodeSelectEvent nodeSelectEvent) {

		if (nodeSelectEvent.getTreeNode().getData() instanceof Category) {

			category = (Category) nodeSelectEvent.getTreeNode().getData();
			if (category.getCategoryType() == CategoryType.OFF_OFFER_PACKAGE) {

				setPackageProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.OFF_OFFER_TEMPLATE
					|| category.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN
					|| category.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
					|| category.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL) {

				setOfferProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.OFF_EVENT_EVENT) {
				
				setEventProperties(false, null, false, true);
			} else if (category.getCategoryType() == CategoryType.OFF_EVENT_ACTION_TYPE) {
				
				setActionTypeProperties(false, null, false, true);
			} else if (category.getCategoryType() == CategoryType.OFF_PC_TYPE) {
				
				setPcTypeProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.OFF_ACTION) {

				setActionProperties(false, category.getCategoryId(), null, false, true);
			} else if (category.getCategoryType() == CategoryType.OFF_DR_DYNAMIC_RESERVE) {

				setDynamicReserveProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.OFF_SPC_SORT_PRICE_COMPONENT) {

				setSortPriceCompornentProperties(false, category.getCategoryId(), null, true);
			} else if (category.getCategoryType() == CategoryType.OFF_PC_PRICE_COMPONENT) {

				setPriceComponentProperties(false, category.getCategoryId(), null, true, true);
			} else if (category.getCategoryType() == CategoryType.OFF_BLOCK_BLOCK) {

				setBlockProperties(false, category.getCategoryId(), null, false, true);
			} else if (category.getCategoryType() == CategoryType.OFF_RT_RATE_TABLE) {

				setRateTableProperties(false, category.getCategoryId(), null, false, true);
			} else if (category.getCategoryType() == CategoryType.OFF_DT_DECISION_TABLE) {

				setDecisionTableProperties(false, category, null, true);
			} else if (category.getCategoryType() == CategoryType.OFF_NOM_NORMALIZER) {

				setNormalizerProperties(false, category.getCategoryId(), null, true);
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

	public void moveUpTreeNode(BaseEntity objEntity, BaseEntity parentEntity) {

		if (objEntity == null || parentEntity == null)
			return;
		List<TreeNode> lstNode = this.getListNodeByObjectAndParent(objEntity, parentEntity);
		if (parentEntity instanceof RateTableDump) {
			lstNode = getListNodeByObject(objEntity);
		}

		for (TreeNode node : lstNode) {

			// Neu truyen vao la dump node ma node cha khong phai dump thi bo
			// qua
			if (parentEntity instanceof RateTableDump && !(node.getParent().getData() instanceof RateTableDump))
				continue;

			BaseEntity objNode = (BaseEntity) node.getData();
			HibernateUtil.copyEntityProperties(objEntity, objNode);

			List<TreeNode> lstChildOfParent = node.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(node);
			if (idx > 0) {

				lstChildOfParent.remove(idx);
				lstChildOfParent.add(idx - 1, node);
			}
		}
	}

	public void moveDownTreeNode(BaseEntity objEntity, BaseEntity parentEntity) {

		if (objEntity == null || parentEntity == null)
			return;
		List<TreeNode> lstNode = this.getListNodeByObjectAndParent(objEntity, parentEntity);
		if (parentEntity instanceof RateTableDump) {
			lstNode = getListNodeByObject(objEntity);
		}

		for (TreeNode node : lstNode) {

			// Neu truyen vao la dump node ma node cha khong phai dump thi bo
			// qua
			if (parentEntity instanceof RateTableDump && !(node.getParent().getData() instanceof RateTableDump))
				continue;

			BaseEntity objNode = (BaseEntity) node.getData();
			HibernateUtil.copyEntityProperties(objEntity, objNode);

			List<TreeNode> lstChildOfParent = node.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(node);
			if (idx < lstChildOfParent.size() - 1) {

				lstChildOfParent.remove(idx);
				lstChildOfParent.add(idx + 1, node);
			}
		}
	}

	private void sortChildren(TreeNode node) {

		List<TreeNode> lstCurrentChildren = node.getChildren();
		List<TreeNode> lstTemp = new ArrayList<>();
		lstTemp.addAll(lstCurrentChildren);
		Collections.sort(lstTemp, new OcsTreeNodeComparator());
		lstCurrentChildren.clear();
		lstCurrentChildren.addAll(lstTemp);
	}

	private List<TreeNode> getListNodeByObjectAndParent(BaseEntity obj, BaseEntity objParent) {

		List<TreeNode> lst = new ArrayList<>();
		if (obj == null || objParent == null)
			return lst;

		List<TreeNode> lstObjNode = getListNodeByObject(obj);
		List<TreeNode> lstParentNode = getListNodeByObject(objParent);
		if (lstObjNode == null || lstParentNode == null)
			return null;

		for (TreeNode parentNode : lstParentNode) {

			List<TreeNode> lstChildOfParent = parentNode.getChildren();
			for (TreeNode node : lstChildOfParent) {
				for (TreeNode objNode : lstObjNode) {

					if (objNode == node)
						lst.add(objNode);
				}
			}
		}

		return lst;
	}

	private List<TreeNode> getListNodeByObject(BaseEntity objEntity) {

		List<TreeNode> lst = mapListNode.get(objEntity.getUniqueKey());
		if (lst == null) {
			lst = new ArrayList<>();
			mapListNode.put(objEntity.getUniqueKey(), lst);
		}
		return lst;
	}

	private void settingNewTreeNode(BaseEntity objEntity, TreeNode node) {

		getListNodeByObject(objEntity).add(node);
		setTypeAndDumpNode(objEntity, node);
	}

	private void setTypeAndDumpNode(BaseEntity objEntity, TreeNode node) {

		if (objEntity instanceof OfferPackage) {

			node.setType(TreeNodeType.OFF_PACKAGE);
			OfferPackage offerPackage = (OfferPackage) objEntity;
			int countOffer = offerDao.countOfferByOfferPackage(offerPackage.getOfferPkgId());
			if ((offerPackage.getMainOfferId() != null && offerPackage.getMainOfferId() > 0) || countOffer > 0) {
				addDumpNode(TreeNodeType.OFF_OFFER, node);
			}
		} else if (objEntity instanceof Offer) {

			node.setType(TreeNodeType.OFF_OFFER);
			Offer offer = (Offer) objEntity;
			int countAction = actionDao.countActionByOffer(offer.getOfferId());
			if (countAction > 0) {
				addDumpNode(TreeNodeType.OFF_ACTION, node);
			}
		} else if (objEntity instanceof Action) {

			node.setType(TreeNodeType.OFF_ACTION);
			Action action = (Action) objEntity;
			int countPC = pcDao.countPCByAction(action.getActionId());
			if (action.getDynamicReserveId() != null || action.getSortPriceComponentId() != null
					|| action.getPriorityFilterId() != null || countPC > 0) {
				addDumpNode(TreeNodeType.OFF_PRICE_COMPONENT, node);
			}
		} else if (objEntity instanceof PriceComponent) {

			node.setType(TreeNodeType.OFF_PRICE_COMPONENT);
			PriceComponent pc = (PriceComponent) objEntity;
			int countBlock = blockDAO.countBlockByPC(pc.getPriceComponentId());
			if (countBlock > 0) {
				addDumpNode(TreeNodeType.OFF_BLOCK, node);
			}
		} else if (objEntity instanceof Block) {

			node.setType(TreeNodeType.OFF_BLOCK);
			Block block = (Block) objEntity;
			int countRT = rateTableDAO.countRateTableByBlock(block.getBlockId());
			if (countRT > 0) {
				addDumpNode(TreeNodeType.OFF_RATE_TABLE, node);
			}
		} else if (objEntity instanceof RateTable) {

			node.setType(TreeNodeType.OFF_RATE_TABLE);
			RateTable rt = (RateTable) objEntity;
			if (rt.getDecisionTableId() != null && rt.getDecisionTableId() > 0)
				buildDT(false, rt, node);
		} else if (objEntity instanceof DecisionTable) {

			node.setType(TreeNodeType.OFF_DECISION_TABLE);
			DecisionTable decisionTable = (DecisionTable) objEntity;
			int countNorm = decisionTableDAO.countNormalizers(decisionTable.getDecisionTableId());
			if (countNorm > 0) {
				addDumpNode(TreeNodeType.OFF_NORMALIZER, node);
			}
		} else if (objEntity instanceof DynamicReserve) {

			node.setType(TreeNodeType.OFF_DYNAMIC_RESERVE);
			DynamicReserve dr = (DynamicReserve) objEntity;
			int countRT = rateTableDAO.countRTByDynamicReserve(dr.getDynamicReserveId());
			if (countRT > 0) {
				addDumpNode(TreeNodeType.OFF_RATE_TABLE, node);
			}
		} else if (objEntity instanceof Event) {

			node.setType(TreeNodeType.OFF_EVENT);
			Event event = (Event) objEntity;
			int countAT = actionTypeDAO.countActionTypeByEvent(event.getEventId());
			if (countAT > 0) {
				addDumpNode(TreeNodeType.OFF_ACTIONTYPE, node);
			}
		} else if (objEntity instanceof SortPriceComponent) {

			node.setType(TreeNodeType.OFF_SORT_PRICE_COMPONENT);
			SortPriceComponent spc = (SortPriceComponent) objEntity;
			int countRatetable = rateTableDAO.countRateTableBySPC(spc.getSortPriceComponentId());
			if (countRatetable > 0) {
				addDumpNode(TreeNodeType.OFF_RATE_TABLE, node);
			}
		} else if (objEntity instanceof Normalizer) {

			node.setType(TreeNodeType.OFF_NORMALIZER);
		} else if (objEntity instanceof ActionType) {

			node.setType(TreeNodeType.OFF_ACTIONTYPE);
		}	else if (objEntity instanceof PcType) {

			node.setType(TreeNodeType.OFF_PC_TYPE);
			PcType pct = (PcType) objEntity;
			int countBlock = blockDAO.countBlockByPCT(pct.getPcTypeId());
			if (countBlock > 0) {
				addDumpNode(TreeNodeType.OFF_BLOCK, node);
			}
		}
	}

	/************ BUILD TREE - END *****************/

	/************ EVENT - BEGIN ********************/

	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {

		TreeNode node = nodeExpandEvent.getTreeNode();
		BaseEntity objEntity = (BaseEntity) node.getData();

		// Neu khong co node con la dump thi khong can add con
		if (!removeDumpNode(node))
			return;

		if (objEntity instanceof OfferPackage) {

			buildOfferOfOfferPackage((OfferPackage) objEntity, node);
		} else if (objEntity instanceof Offer) {

			buildAction(false, (Offer) objEntity, node);
		} else if (objEntity instanceof Action) {

			buildPC(false, (Action) objEntity, node);
			buildDR(false, (Action) objEntity, node);
			buildSortPriceComponent(false, (Action) objEntity, node);
		} else if (objEntity instanceof PriceComponent) {

			buildBlock(false, (PriceComponent) objEntity, node);
		} else if (objEntity instanceof Block) {

			buildRT(false, (Block) objEntity, node);
		} else if (objEntity instanceof DecisionTable) {

			buildNorm(false, (DecisionTable) objEntity, node);
		} else if (objEntity instanceof DynamicReserve) {

			buildRTOfDR(false, (DynamicReserve) objEntity, node);
		} else if (objEntity instanceof Event) {

			buildActionType(false, (Event) objEntity, node);
		} else if (objEntity instanceof SortPriceComponent) {

			buildRTOfSPC(false, (SortPriceComponent) objEntity, node);
		} else if (objEntity instanceof PcType) {
			
			buildBlockOfPcType(false, (PcType) objEntity, node);
		}
	}

	public void onNodeCollapse(NodeCollapseEvent nodeCollapseEvent) {

		TreeNode node = nodeCollapseEvent.getTreeNode();
		node.setExpanded(false);
	}

	private void addDumpNode(String treeNodeType, TreeNode parentNode) {
		TreeNode dumpNode = new OcsTreeNode(dumpEntity, parentNode);
		dumpNode.setType(treeNodeType);
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

	private NodeSelectEvent nodeSelectEvent;

	public NodeSelectEvent getNodeSelectEvent() {
		return nodeSelectEvent;
	}

	public void setNodeSelectEvent(NodeSelectEvent nodeSelectEvent) {
		this.nodeSelectEvent = nodeSelectEvent;
	}

	public void onNodeSelectContext(NodeSelectEvent event) {
		nodeSelectEvent = event;
		this.isShowClone = checkShowCloneMenu(event);
	}

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
		
		TreeNode treeNode = nodeSlectedEvent.getTreeNode();
		BaseEntity obj = (BaseEntity) treeNode.getData();
		processOnNodeSelect(obj);
	}
	
	public void processOnNodeSelect(BaseEntity selectedEntity) {

		currentEntity = selectedEntity;
		if (currentEntity == null)
			return;

		fixParentCatNode = false;
		if (currentEntity instanceof Category) {
			// Category

			setContentTitle(super.readProperties("title.category"));
			category = (Category) currentEntity;
			setSelectCategoryNode(category);
			selectedCatType = category.getCategoryType();
		} else {

			hideCategory();

			if (currentEntity instanceof Offer) {

				setOfferProperties(false, ((Offer) currentEntity).getCategoryId(), (Offer) currentEntity, false);
			} else if (currentEntity instanceof Event) {

				setEventProperties(false, (Event) currentEntity, false, false);
			}
			if (currentEntity instanceof ActionType) {

				setActionTypeProperties(false, (ActionType) currentEntity, false, false);
			} else if (currentEntity instanceof Action) {

				setActionProperties(false, null, (Action) currentEntity, false, false);
			} else if (currentEntity instanceof DynamicReserve) {

				setDynamicReserveProperties(false, ((DynamicReserve) currentEntity).getCategoryId(),
						((DynamicReserve) currentEntity), false);
			} else if (currentEntity instanceof SortPriceComponent) {

				setSortPriceCompornentProperties(false, ((SortPriceComponent) currentEntity).getCategoryId(),
						((SortPriceComponent) currentEntity), false);
			} else if (currentEntity instanceof PriceComponent) {

				setPriceComponentProperties(false, ((PriceComponent) currentEntity).getCategoryId(),
						((PriceComponent) currentEntity), false, false);
			} else if (currentEntity instanceof Block) {

				setBlockProperties(false, ((Block) currentEntity).getCategoryId(), ((Block) currentEntity), false,
						false);
			} else if (currentEntity instanceof RateTable) {

				setRateTableProperties(false, ((RateTable) currentEntity).getCategoryId(), ((RateTable) currentEntity),
						false, false);
			} else if (currentEntity instanceof DecisionTable) {

				setDecisionTableProperties(false, null, (DecisionTable) currentEntity, false);
			} else if (currentEntity instanceof Normalizer) {

				setNormalizerProperties(false, ((Normalizer) currentEntity).getCategoryId(), (Normalizer) currentEntity,
						false);
			} else if (currentEntity instanceof OfferPackage) {

				setPackageProperties(false, ((OfferPackage) currentEntity).getCategoryId(),
						(OfferPackage) currentEntity, false);
			} else if (currentEntity instanceof OfferDump) {

				setOfferGroupProperties(false, ((OfferDump) currentEntity).getCategoryId(), (OfferDump) currentEntity);
			} else if (currentEntity instanceof PcType) {
				
				setPcTypeProperties(false, ((PcType) currentEntity).getCategoryId(), ((PcType) currentEntity) , false);
			}
		}
	}

	// Nampv setSelectCategoryNode khi click node category
	public void setSelectCategoryNode(Category cat) {

		long catId = cat.getCategoryId();
		if (catId <= 0) {
			// Select node ngoai cung

			hideContentPage();
			setFormType("cat-list");
			if (CategoryType.OFF_OFFER_SUBSCRIPTION == cat.getCategoryType()
					|| CategoryType.OFF_OFFER_ONETIME == cat.getCategoryType()) {

				setShowBtnCatNew(false);
				if (CategoryType.OFF_OFFER_SUBSCRIPTION == cat.getCategoryType()) {

					fixParentCatNode = true;
					// listAllCategory = listCatOfferSubscription;

					List<Long> lstCatType = new ArrayList<>();
					lstCatType.add(CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN);
					lstCatType.add(CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL);
					lstCatType.add(CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED);
					loadListCategory(lstCatType);

				} else if (CategoryType.OFF_OFFER_ONETIME == cat.getCategoryType()) {

					fixParentCatNode = true;
					// listAllCategory = listCatOfferOneTime;

					List<Long> lstCatType = new ArrayList<>();
					lstCatType.add(CategoryType.OFF_OFFER_ONETIME_NORMAL);
					lstCatType.add(CategoryType.OFF_OFFER_ONETIME_COMPILED);
					loadListCategory(lstCatType);
				}
			} else {
				setShowBtnCatNew(true);
				loadListCategory(cat.getCategoryType());
			}
		} else {
			// Select node con

			// Set for category form
			setFormType("cat-form");
			categoryParentId = cat.getCategoryParentId();
			setCategory(cat);
			searchCatSub(catId);
			setCategoryTitle("Sub-Categories of " + cat.getCategoryName());
			isEditing = true;
			searchCatParent(cat.getCategoryType(), cat.getCategoryId());

			// Set for business bean form
			switch (treeType) {
			case TreeType.OFFER_OFFER:
				if (cat.getCategoryType() == CategoryType.OFF_OFFER_TEMPLATE
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_NORMAL
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_NORMAL
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_ONETIME_COMPILED
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_MAIN
						|| cat.getCategoryType() == CategoryType.OFF_OFFER_SUBSCRIPTION_COMPILED) {
					setOfferProperties(true, catId, null, false);
				} else if (cat.getCategoryType().equals(CategoryType.OFF_OFFER_PACKAGE)) {
					setPackageProperties(true, catId, null, false);
				}
				break;
			case TreeType.OFFER_EVENT:
				if (cat.getCategoryType().equals(CategoryType.OFF_EVENT_EVENT)) {
					setEventProperties(true, null, false, false);
				} else if (cat.getCategoryType().equals(CategoryType.OFF_EVENT_ACTION_TYPE)) {
					setActionTypeProperties(true, null, false, false);
				}
				break;
			case TreeType.OFFER_PC_TYPE:
				setPcTypeProperties(true, catId, null, false);
				break;
			case TreeType.OFFER_ACTION:
				setActionProperties(true, catId, null, false, false);
				break;
			case TreeType.OFFER_DYNAMIC_RESERVE:
				setDynamicReserveProperties(true, catId, null, false);
				break;
			case TreeType.OFFER_SORT_PRICE_COMPONENT:
				setSortPriceCompornentProperties(true, catId, null, false);
				break;
			case TreeType.OFFER_POST_PROCESS:
				// todo
				break;
			case TreeType.OFFER_PRICE_COMPONENT:
				setPriceComponentProperties(true, catId, null, false, false);
				break;
			case TreeType.OFFER_BLOCK:
				setBlockProperties(true, catId, null, false, false);
				break;
			case TreeType.OFFER_RATE_TABLE:
				setRateTableProperties(true, catId, null, false, false);
				break;
			case TreeType.OFFER_DECISION_TABLE:
				setDecisionTableProperties(true, category, null, false);
				break;
			case TreeType.OFFER_NORMALIZER:
				setNormalizerProperties(true, catId, null, false);
				break;
			default:
				break;
			}
		}
	}

	private void setOfferProperties(boolean selectCat, Long categoryId, Offer offer, boolean createNew) {
		setContentPage("page_offer");
		if(category == null)
			category = catDao.get(categoryId);
		if (createNew) {
			offerBean.setCategory(category);
			offerBean.btnAddNewFromCat();
		} else if(selectCat) {
			setContentTitle(super.readProperties("title.category"));
			offerBean.refreshOfferVersionPage(offer, this.category);
		} else {
			setContentTitle(super.readProperties("title.offer"));
			offerBean.refreshOfferVersionPage(offer, this.category);
		}
	}

	private void setOfferGroupProperties(boolean selectCat, Long categoryId, OfferDump offerDump) {
		setContentPage("page_offer");
		setContentTitle(super.readProperties("title.offer"));
		offerBean.refreshOfferGroup(offerDump);
	}

	public void setActionProperties(boolean selectCat, Object object, Action action, boolean parentEdit,
			boolean createNew) {
		setContentPage("page_action");
		if (createNew) {
			actionBean.setCategory(category);
			actionBean.addNewAction();
		} else if (selectCat) {
			setContentTitle(super.readProperties("title.category"));
			actionBean.refreshCategories(category);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.actionTableClearFilter').click();");
		} else {
			setContentTitle(super.readProperties("title.action"));
			actionBean.refreshAction(action);
			actionBean.setEdit(false);
		}

	}

	private void setPackageProperties(boolean selectCat, Object object, OfferPackage offerPackage, boolean createNew) {
		setContentPage("page_offerpackage");
		if (createNew) {
			offerPackageBean.setCategory(category);
			offerPackageBean.addNewOfferPackage();
		} else if (selectCat) {
			offerPackageBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("title.offer_package"));
			offerPackageBean.refreshOfferPackage(offerPackage);
		}
	}

	public void setPriceComponentProperties(boolean selectCat, Long categoryId, PriceComponent component,
			boolean parentEdit, boolean createNew) {

		setContentPage("page_priceComponent");
		if (createNew) {

			priceComponentsBean.setCategoryId(categoryId);
			priceComponentsBean.btnAddNew();
		} else if (selectCat) {
			priceComponentsBean.setFormType("category");
			priceComponentsBean.getListPriceComponentByCategoryId(categoryId);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.pcTableClearFilter').click();");
		} else {
			hideCategory();
			setContentTitle(super.readProperties("title.priceComponent"));
			priceComponentsBean.setEdit(false);
			priceComponentsBean.setEditPC(true);
			priceComponentsBean.setPriceComponent(component);
			priceComponentsBean.setFormType("form-pc-detail");
			priceComponentsBean.getListBlockByPC();
			priceComponentsBean.loadPCTypes();
			//priceComponentsBean.getLoadComboListCategory();
			priceComponentsBean.loadCategoriesOfPC();
			priceComponentsBean.setCategoryId(component.getCategoryId() == null ? 0 : component.getCategoryId());
			if (parentEdit == true) {
				priceComponentsBean.setEdit(true);
			}
		}
	}

	public void setBlockProperties(boolean selectCat, Long categoryId, Block block, boolean parentEdit,
			boolean createNew) {

		setContentPage("page_block");
		if (createNew) {

			blockBean.setCategoryID(categoryId);
			blockBean.commandAddNew();
		} else if (selectCat) {
			blockBean.setFormType("list-block-by-category");
			blockBean.loadBlockByCategory(categoryId);
			blockBean.setEditting(true);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.blockTableClearFilter').click();");
		} else {
			setContentTitle(super.readProperties("title.block"));
			blockBean.refeshBlock(block);
			blockBean.setEditting(false);
			if (parentEdit == true) {
				blockBean.setEditting(true);
			}
		}
	}

	public void setSortPriceCompornentProperties(boolean selectCat, Long categoryId, SortPriceComponent spc,
			boolean createNew) {

		setContentPage("page_sortPriceCompornent");
		if (createNew) {
			sortPriceComponentBean.setCategoryID(categoryId);
			sortPriceComponentBean.commandAddNew();
		} else if (selectCat) {
			sortPriceComponentBean.setFormType("list-sort-price-compornent-by-category");
			sortPriceComponentBean.loadListSortPriceComponentByCategory(categoryId);
			sortPriceComponentBean.setEditting(true);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.spcTableClearFilter').click();");
		} else {
			setContentTitle(super.readProperties("title.sortPriceComponent"));
			sortPriceComponentBean.refreshSPC(spc);
		}
	}

	public void setRateTableProperties(boolean selectCat, Long categoryId, RateTable rateTable, boolean parentEdit,
			boolean createNew) {

		setContentPage("page_rateTable");
		if (createNew) {
			rateTableBean.setCategory(category);
			rateTableBean.commandAddNew();
		} else if (selectCat) {
			rateTableBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("title.rateTable"));
			rateTableBean.refreshRateTable(rateTable);
			rateTableBean.setEditting(parentEdit);
		}
	}

	public void setDecisionTableProperties(boolean selectCat, Category cat, DecisionTable decisionTable,
			boolean createNew) {

		setContentPage("page_decisionTable");
		if (createNew) {

			decisionTableBean.setCategory(category);
			decisionTableBean.addNewDecisionTable();
		} else if (selectCat) {
			decisionTableBean.refreshCategories(category);
		} else {
			hideCategory();
			setContentTitle(super.readProperties("title.decisionTable"));
			decisionTableBean.refreshDecisionTable(decisionTable);
		}
	}

	public void setNormalizerProperties(boolean selectCat, Long categoryId, Normalizer normalizer, boolean createNew) {

		setContentPage("page_normalizer");
		if (createNew) {

			normalizerBean.commandAddNew();
			normalizerBean.getNormalizer().setCategoryId(categoryId);
			normalizerBean.setSelectedNormCate(categoryId);
		} else if (selectCat) {

			normalizerBean.setFormtype("normByCat");
			normalizerBean.setIsEdit(false);
			normalizerBean.loadNormalizerByCate(categoryId);
			if (normalizerBean.getListNormalizerByCate().size() == 0) {
				normalizerBean.setListNormalizerByCate(new ArrayList<>());
			}
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("$('.normTableClearFilter').click();");
		} else {

			setContentTitle(super.readProperties("title.normalizer"));
			normalizerBean.setFormtype("normDetail");
			normalizerBean.setNormalizer(normalizer);
			normalizerBean.setIsEdit(false);
			normalizerBean.setDataForEdit();
		}
	}

	public void setActionTypeProperties(boolean selectCat, ActionType actionType, boolean editing, boolean createNew) {

		setContentPage("page_actiontype");
		if(createNew) {
			actionTypeBean.setCategory(category);
			actionTypeBean.addNewActionType();
		} else if (selectCat) {
			actionTypeBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("title.actiontype"));
			actionTypeBean.refreshActionType(actionType, editing);
		}
	}

	public void setDynamicReserveProperties(boolean selectCat, Long categoryId, DynamicReserve dr, boolean createNew) {
		setContentPage("page_dynamicreserve");
		if (createNew) {
			dynamicReservesBean.setCategory(category);
			dynamicReservesBean.addNewDynamicReserve();
		} else if (selectCat) {
			dynamicReservesBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("title.dynamicReserve"));
			dynamicReservesBean.refreshDynamicReserve(dr);
			dynamicReservesBean.setEditting(false);
		}
	}

	public void setEventProperties(boolean selectCat, Event event, boolean editting, boolean createNew) {
		setContentPage("page_event");
		if(createNew) {
			setContentTitle(super.readProperties("title.event"));
			eventBean.setCategory(category);
			eventBean.addNewEvent();
		} else if (selectCat) {
			eventBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("title.event"));
			eventBean.refreshEvent(event, editting);
		}
	}
	
	public void setPcTypeProperties(boolean selectCat, Long categoryId ,PcType pcType, boolean createNew) {
		setContentPage("page_pcType");
		if(createNew) {
			setContentTitle(super.readProperties("pcType.title"));
			pcTypeBean.setCategory(category);
			pcTypeBean.addNewPcType();
		} else if (selectCat) {
			pcTypeBean.refreshCategories(category);
		} else {
			setContentTitle(super.readProperties("pcType.title"));
			pcTypeBean.refreshPcType(pcType);
			pcTypeBean.setEditting(false);
		}
	}
	
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

	public void onchangeExpandTree() {
		if(expandTree) {
			setExpandedRecursively(root, true, true);
		} else {
			setExpandedRecursively(root, false, true);
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

	public void hideCategory() {
		setFormType("hide-all-cat");
	}

	public void hideContentPage() {
		setContentPage("hide-content-page");
	}

	/************ EVENT - END *****************/

	/************ CATEGORY - BEGIN ************/

	private Category category;
	private Long categoryParentId;
	private List<Category> listCatParent;
	private List<Category> listAllCategory;
	private List<Category> listCatSub;
	private List<Category> listCatOfferSubscription;
	private List<Category> listCatOfferOneTime;
	private boolean isEditing;
	private String categoryTitle;
	private CategoryDAO catDao;
	private Category catParentDump;
	private List<SelectItem> listCatType;
	private Long selectedCatType;

	public List<SelectItem> getListCatType() {
		return listCatType;
	}

	public void setListCatType(List<SelectItem> listCatType) {
		this.listCatType = listCatType;
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
			categoryParentId = currentCat.getCategoryId();
		}
		searchCatParent(selectedCatType, category.getCategoryId());
	}

	private void initCategoryType() {

		listCatType = new ArrayList<>();
		Map<Long, String> mapCatType = CategoryType.getCatTypesByTreeType(treeType);
		Iterator<Long> it = mapCatType.keySet().iterator();
		SelectItem item;
		if (TreeType.OFFER_OFFER.equals(treeType)) {
			while (it.hasNext()) {

				long catType = it.next();
				if (catType == CategoryType.OFF_OFFER_SUBSCRIPTION || catType == CategoryType.OFF_OFFER_ONETIME) {

					SelectItemGroup itemGroup = new SelectItemGroup(mapCatType.get(catType));
					Map<Long, String> mapTypeSub = CategoryType.getCatTypeSub(catType);
					Iterator<Long> itSub = mapTypeSub.keySet().iterator();
					SelectItem[] selectItems = new SelectItem[mapTypeSub.size()];
					int i = 0;
					while (itSub.hasNext()) {

						long catTypeSub = itSub.next();
						item = new SelectItem(catTypeSub, mapTypeSub.get(catTypeSub));
						selectItems[i] = item;
						i++;
					}
					itemGroup.setSelectItems(selectItems);
					listCatType.add(itemGroup);
				} else {

					item = new SelectItem(catType, mapCatType.get(catType));
					listCatType.add(item);
				}
			}
		} else {
			while (it.hasNext()) {

				long catType = it.next();
				item = new SelectItem(catType, mapCatType.get(catType));
				listCatType.add(item);
			}
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

			// Tim vi tri cua node category con cuoi cung cua parentNode de add
			// vao
			List<TreeNode> lstChildren = parentNode.getChildren();
			int idxLastChild = 0;
			for (TreeNode node : lstChildren) {
				if (node.getData() instanceof Category) {
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

	// type: 1- su kien click node cha ;
	public List<Category> loadListCategory(Long categoryType) {

		listAllCategory = catDao.loadListCategoryByTypeNoParent(categoryType);
		return listAllCategory;
	}

	public List<Category> loadListCategory(List<Long> lstCatType) {

		listAllCategory = catDao.loadListCategoryByTypeNoParent(lstCatType);
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

		listCatParentDlg = catDao.findByTypeAndNotEqualForSelectbox(catType, catId);
		listCatParentDlg.add(0, catParentDump);
	}

	/********* CATEGORY DLG - END ************/

	/********* check show clone menu **********/

	public Boolean checkShowCloneMenu(NodeSelectEvent nodeSelectEvent) {
		boolean result = false;
		if (nodeSelectEvent != null) {
			try {
				if (nodeSelectEvent.getTreeNode().getParent().getData() instanceof Category) {
					result = true;
				}
			} catch (Exception e) {
				result = false;
			}
		} else {
			result = false;
		}
		// RequestContext context = RequestContext.getCurrentInstance();
		// context.update("treeCommon");
		return result;
	}

	/********* OBJECT SEARCH - BEGIN **********/
	
	private String txtSearchObjType;
	private Long txtSearchObjID;
	private String txtSearchObjName;
	private String txtSearchObjDesc;
	private boolean findDump;
	private List<GeneralObject> listSearchResult;
	
	public void onchangeSearchObjType() {
		
		listSearchResult = new ArrayList<>();
	}
	
	public void doSearchObject() {
		
		listSearchResult = generalObjectDAO.findByConditions(findDump, txtSearchObjType, txtSearchObjID, txtSearchObjName, txtSearchObjDesc);
	}
	
	public void onRowObjSelect(SelectEvent event) { 
		
		GeneralObject obj = (GeneralObject) event.getObject();
		BaseEntity actualObj = generalObjectDAO.findActualObject(obj);		
		processOnNodeSelect(actualObj);
	}
	
	public void deleteDumpObject(GeneralObject obj) { 
			
		BaseEntity actualObject = generalObjectDAO.deleteActualObject(obj);
		if(actualObject != null) {
			
			listSearchResult.remove(obj);
			this.removeTreeNodeAll(actualObject);
			this.showMessageINFO("common.delete", " Dump Object");
		}	
	}
	
	/********* OBJECT SEARCH - END **********/
	
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

	public String getContentPage() {
		return contentPage;
	}

	public void setContentPage(String page) {
		this.contentPage = page;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	public Long getSelectedCatType() {
		return selectedCatType;
	}

	public void setSelectedCatType(Long selectedCatType) {
		this.selectedCatType = selectedCatType;
	}

	public boolean isFixParentCatNode() {
		return fixParentCatNode;
	}

	public void setFixParentCatNode(boolean fixParentCatNode) {
		this.fixParentCatNode = fixParentCatNode;
	}

	public boolean isShowBtnCatNew() {
		return showBtnCatNew;
	}

	public void setShowBtnCatNew(boolean showBtnCatNew) {
		this.showBtnCatNew = showBtnCatNew;
	}

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

	public Boolean getIsShowClone() {
		return isShowClone;
	}

	public void setIsShowClone(Boolean isShowClone) {
		this.isShowClone = isShowClone;
	}

	public String getTxtSearchObjType() {
		return txtSearchObjType;
	}

	public void setTxtSearchObjType(String txtSearchObjType) {
		this.txtSearchObjType = txtSearchObjType;
	}

	public Long getTxtSearchObjID() {
		return txtSearchObjID;
	}

	public void setTxtSearchObjID(Long txtSearchObjID) {
		this.txtSearchObjID = txtSearchObjID;
	}

	public String getTxtSearchObjName() {
		return txtSearchObjName;
	}

	public void setTxtSearchObjName(String txtSearchObjName) {
		this.txtSearchObjName = txtSearchObjName;
	}

	public String getTxtSearchObjDesc() {
		return txtSearchObjDesc;
	}

	public void setTxtSearchObjDesc(String txtSearchObjDesc) {
		this.txtSearchObjDesc = txtSearchObjDesc;
	}

	public List<GeneralObject> getListSearchResult() {
		return listSearchResult;
	}

	public void setListSearchResult(List<GeneralObject> listSearchResult) {
		this.listSearchResult = listSearchResult;
	}

	public boolean isFindDump() {
		return findDump;
	}

	public void setFindDump(boolean findDump) {
		this.findDump = findDump;
	}

	public boolean isExpandTree() {
		return expandTree;
	}

	public void setExpandTree(boolean expandTree) {
		this.expandTree = expandTree;
	}

	public String getTxtTreeSearch() {
		return txtTreeSearch;
	}

	public void setTxtTreeSearch(String txtTreeSearch) {
		this.txtTreeSearch = txtTreeSearch;
	}
	

	/****** GET SET - END *************/
}

package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeNodeType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.FormulaDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.PcTypeDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.PcType;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.TriggerOcs;

@ManagedBean(name = "showDependenciesBean")
@ViewScoped
public class ShowDependenciesBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = 6630555912674495833L;
	private ActionDAO actionDAO;
	private OfferDAO offerDAO;
	private DynamicReserveDAO dynamicReserveDAO;
	private SortPriceComponentDAO sortPriceComponentDAO;
	private PcTypeDAO pcTypeDAO;
	private PriceComponentDAO priceComponentDAO;
	private BlockDAO blockDAO;
	private RateTableDAO rateTableDAO;
	private DecisionTableDAO decisionTableDAO;
	private NormalizerDAO normalizerDAO;
	private TriggerOcsDAO triggerOcsDAO;
	private FormulaDAO formulaDAO;
		
	private TreeNode rootParent;
	private TreeNode rootChilden;
	private Map<Long, TreeNode> mapCatNode;
	private String treeType;
	private String className;
	private Long objID;
	private String name;
	private String type;
	
	private List<Action> lstCheckAction;
	private List<RateTable> lstCheckRateTable;
	private List<PcType> lstCheckPcType;
	private List<PriceComponent> lstCheckPc;
	private List<Block> lstCheckBlock;

	public Long getObjID() {
		return objID;
	}

	public void setObjID(Long objID) {
		this.objID = objID;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public TreeNode getRootParent() {
		return rootParent;
	}

	public void setRootParent(TreeNode rootParent) {
		this.rootParent = rootParent;
	}

	public TreeNode getRootChilden() {
		return rootChilden;
	}

	public void setRootChilden(TreeNode rootChilden) {
		this.rootChilden = rootChilden;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ShowDependenciesBean() {
		actionDAO = new ActionDAO();
		offerDAO = new OfferDAO();
		dynamicReserveDAO = new DynamicReserveDAO();
		sortPriceComponentDAO = new SortPriceComponentDAO();
		pcTypeDAO = new PcTypeDAO();
		priceComponentDAO = new PriceComponentDAO();
		blockDAO = new BlockDAO();
		rateTableDAO = new RateTableDAO();
		decisionTableDAO = new DecisionTableDAO();
		normalizerDAO = new NormalizerDAO();
		triggerOcsDAO = new TriggerOcsDAO();
		formulaDAO = new FormulaDAO();
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		treeType = params.get("treeType");
		if (treeType.contains(";")) {
			String[] parts = treeType.split(";");
			objID = Long.parseLong(parts[0]);
			className = parts[1];
		} else
			return;
		rootParent = new DefaultTreeNode("Root", null);
		rootChilden = new DefaultTreeNode("Root", null);
		mapCatNode = new HashMap<>();
		
		lstCheckAction = new ArrayList<Action>();
		lstCheckRateTable = new ArrayList<RateTable>();
		lstCheckPcType = new ArrayList<PcType>();
		lstCheckPc = new ArrayList<PriceComponent>();
		lstCheckBlock = new ArrayList<Block>();
		
		// Build tree
		if(Offer.class.getName().equals(className)) {
						
			buildOffer(objID, rootParent);
		} else if(Action.class.getName().equals(className)) {
			
			buildAction(objID, rootParent);
		} else if(PriceComponent.class.getName().equals(className)) {
			
			buildPC(objID, rootParent);	
		} else if(Block.class.getName().equals(className)) {
			
			buildBlock(objID, rootParent);	
		} else if(RateTable.class.getName().equals(className)) {
			
			buildRT(objID, rootParent);	
		} else if(DecisionTable.class.getName().equals(className)) {
			
			buildDT(objID, rootParent);	
		} else if(Normalizer.class.getName().equals(className)) {
			
			buildNormalizer(objID, rootParent);	
		} else if(DynamicReserve.class.getName().equals(className)) {
			
			buildDR(objID, rootParent);	
		} else if(SortPriceComponent.class.getName().equals(className)) {
			
			buildSPC(objID, rootParent);	
		} else if(PcType.class.getName().equals(className)) {
			
			buildPCType(objID, rootParent);	
		} else if(TriggerOcs.class.getName().equals(className)) {
			
			buildTrigger(objID, rootParent);	
		}
		
		// Resort parent tree
		List<TreeNode> parentChildren = rootParent.getChildren();
		List<TreeNode> lstTemp = new ArrayList<>();
		lstTemp.addAll(parentChildren);
		parentChildren.clear();
		Collections.reverse(lstTemp);
		parentChildren.addAll(lstTemp);
	}

	private TreeNode buildTreeRoot(long catType, String catTypeName, TreeNode root) {
		
		if(mapCatNode.get(catType) != null)
			return mapCatNode.get(catType);
		
		Category cat = new Category();
		cat.setTreeType(treeType);		
		cat.setCategoryType(0l);
		cat.setCategoryName(catTypeName);
		TreeNode node = new DefaultTreeNode(cat, root);
		mapCatNode.put(catType, node);
		return node;
	}
	
	private void buildOffer(Long ID, TreeNode root) {
		
		Offer offer = offerDAO.get(ID);
		this.name = offer.getOfferName() + " - Version " + offer.getVersionInfo();
		this.type = "Offer";		 

		// build treeChildren
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(ID);		
		getListAction(false, lstID);
		
		List<Long> lstIDForDR = new ArrayList<Long>();
		List<Long> lstIDForSPC = new ArrayList<Long>();
		lstIDForDR.addAll(lstID);
		lstIDForSPC.addAll(lstID);
		getListDR(false, lstIDForDR);
		getListSPC(false, lstIDForSPC);
		
		getListPC(false, lstID);
		getListBlock(false, lstID);
		
		getListRateTableByDR(lstIDForDR);
		getListRateTableBySPC(lstIDForSPC);
		getListRateTable(false, lstID);
		
		lstID.addAll(lstIDForDR);
		lstID.addAll(lstIDForSPC);
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildAction(Long ID, TreeNode root) {
		
		Action action = actionDAO.get(ID);
		this.name = action.getActionName();
		this.type = "Action";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(ID);		
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(ID);
		
		List<Long> lstIDForDR = new ArrayList<Long>();
		List<Long> lstIDForSPC = new ArrayList<Long>();
		lstIDForDR.addAll(lstID);
		lstIDForSPC.addAll(lstID);
		getListDR(false, lstIDForDR);
		getListSPC(false, lstIDForSPC);
				
		getListPC(false, lstID);
		getListBlock(false, lstID);
		
		getListRateTableByDR(lstIDForDR);
		getListRateTableBySPC(lstIDForSPC);
		getListRateTable(false, lstID);
		
		lstID.addAll(lstIDForDR);
		lstID.addAll(lstIDForSPC);
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildDR(Long ID, TreeNode root) {
		
		DynamicReserve pc = dynamicReserveDAO.get(ID);
		this.name = pc.getDynamicReserveName();
		this.type = "Dynamic Reserve";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(-1L);
		lstID.add(ID);
		getListActionByDR(lstID);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);
		//getListBlock(false, lstID);
		getListRateTableByDR(lstID);
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildSPC(Long ID, TreeNode root) {
		
		SortPriceComponent pc = sortPriceComponentDAO.get(ID);
		this.name = pc.getSortPriceComponentName();
		this.type = "Sort Price Component";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(-1L);
		lstID.add(ID);
		
		List<Long> lstIDBlock = new ArrayList<Long>();
		getListBlockByPcTypeAndSPC(null, lstID, lstIDBlock);
		List<Long> lstIDPCType = new ArrayList<Long>();
		List<Long> lstIDPc = new ArrayList<Long>();
		getListPCType(true, null, null, lstID, lstIDPCType);
		getListPCByPcType(lstIDPCType, lstIDPc);
		getListActionBySPC(lstID);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);
				
//		getListRateTable(false, lstIDBlock);
		getListRateTableBySPC(lstID);
		lstID.addAll(lstIDBlock);
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildPCType(Long ID, TreeNode root) {
		
		PcType pc = pcTypeDAO.get(ID);
		this.name = pc.getPcTypeName();
		this.type = "Price Component Type";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
//		lstID.add(-1L);
//		lstID.add(ID);
//		getListActionBySPC(lstID);
//		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);
		List<Long> lstIDPc = new ArrayList<Long>();
		List<Long> lstIDBlock = new ArrayList<Long>();
		getListPCByPcType(lstID, lstIDPc);;
		getListAction(true, lstIDPc);
		getListOffer(true, lstIDPc);
		
		getListBlockByPcTypeAndSPC(lstID, null, lstIDBlock);
		
//		getListBlock(false, lstIDPc);
//		lstIDBlock.addAll(lstIDPc);
		
		getListRateTable(false, lstIDBlock);
		
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}

	private void buildPC(Long ID, TreeNode root) {
		
		PriceComponent pc = priceComponentDAO.get(ID);
		this.name = pc.getPriceComponentName();
		this.type = "Price Component";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(-1L);
		lstID.add(ID);
		getListAction(true, lstID);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);
		List<Long> lstIDPCType = new ArrayList<Long>();
		getListPCType(false, null, lstID, null, lstIDPCType);
		getListBlock(false, lstID);
		getListRateTable(false, lstID);
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildBlock(Long ID, TreeNode root) {

		Block block = blockDAO.get(ID);
		this.name = block.getBlockName();
		this.type = "Block";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(ID);
		getListPC(true, lstID);
		
		List<Long> lstIDForPCType = new ArrayList<Long>();
		lstIDForPCType.add(ID);
		List<Long> lstIDPCType = new ArrayList<Long>();
		getListPCType(true, lstIDForPCType, null, null, lstIDPCType);
		getListPCType(true, null, lstID, null, lstIDPCType);
		
		getListAction(true, lstID);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);
		getListRateTable(false, lstID);
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildRT(Long ID, TreeNode root) {

		RateTable rt = rateTableDAO.get(ID);
		this.name = rt.getNodeName();
		this.type = "RateTable";

		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(ID);
		getListBlock(true, lstID);
		
		List<Long> lstIDForPCType = new ArrayList<Long>();
		lstIDForPCType.addAll(lstID);
		List<Long> lstIDPCType = new ArrayList<Long>();
		getListPCType(true, lstIDForPCType, null, null, lstIDForPCType);
		
		getListPC(true, lstID);
		getListPCType(true, null, lstID, null, lstIDForPCType);

		List<Long> lstIDForSPC = new ArrayList<Long>();
		List<Long> lstIDForDR = new ArrayList<Long>();
		lstIDForSPC.add(ID);
		lstIDForDR.add(ID);		
		getListSPC(true, lstIDForSPC);
		getListDR(true, lstIDForDR);		
		
		getListActionByDR(lstIDForDR);
		getListActionBySPC(lstIDForSPC);
		getListAction(true, lstID);
		
		lstID.addAll(lstIDForDR);
		lstID.addAll(lstIDForSPC);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);		
		getListDecisionTable(false, lstID);
		getListNormalizers(false, lstID);
	}
	
	private void buildDT(Long ID, TreeNode root) {

		DecisionTable dt = decisionTableDAO.get(ID);
		this.name = dt.getDecisionTableName();
		this.type = "DecisionTable";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(-1L);
		lstID.add(ID);
		getListRateTable(true, lstID);
		
		List<Long> lstIDForSPC = new ArrayList<Long>();
		List<Long> lstIDForDR = new ArrayList<Long>();
		lstIDForSPC.addAll(lstID);
		lstIDForDR.addAll(lstID);		
				
		getListBlock(true, lstID);
				
		List<Long> lstIDForPCType = new ArrayList<Long>();
		lstIDForPCType.addAll(lstID);
		List<Long> lstIDPCType = new ArrayList<Long>();
		getListPCType(true, lstIDForPCType, null, null, lstIDPCType);
		
		getListPC(true, lstID);
		getListPCType(true, null, lstID, null, lstIDPCType);
		
		getListSPC(true, lstIDForSPC);
		getListDR(true, lstIDForDR);		
		
		getListActionByDR(lstIDForDR);
		getListActionBySPC(lstIDForSPC);
		getListAction(true, lstID);
		
		lstID.addAll(lstIDForDR);		
		lstID.addAll(lstIDForSPC);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);		
		getListNormalizers(false, lstID);
	}
	
	private void buildNormalizer(Long ID, TreeNode root) {

		Normalizer norm = normalizerDAO.get(ID);
		this.name = norm.getNormalizerName();
		this.type = "Normalizer";
		
		// Build treeParent
		List<Long> lstID = new ArrayList<Long>();
		lstID.add(-1L);
		lstID.add(ID);
		getListDecisionTable(true, lstID);
		getListRateTable(true, lstID);
		
		List<Long> lstIDForSPC = new ArrayList<Long>();
		List<Long> lstIDForDR = new ArrayList<Long>();
		lstIDForSPC.addAll(lstID);
		lstIDForDR.addAll(lstID);		
				
		getListBlock(true, lstID);
		
		List<Long> lstIDForPCType = new ArrayList<Long>();
		lstIDForPCType.addAll(lstID);
		List<Long> lstIDPCType = new ArrayList<Long>();
		getListPCType(true, lstIDForPCType, null, null, lstIDPCType);
		
		getListPC(true, lstID);
		getListPCType(true, null, lstID, null, lstIDPCType);
		
		getListSPC(true, lstIDForSPC);
		getListDR(true, lstIDForDR);		
		
		getListActionByDR(lstIDForDR);
		getListActionBySPC(lstIDForSPC);
		getListAction(true, lstID);
		
		lstID.addAll(lstIDForDR);		
		lstID.addAll(lstIDForSPC);
		getListOffer(true, lstID);
		
		// build treeChildren
	}

	private void buildTrigger(Long ID, TreeNode root) {

		TriggerOcs dt = triggerOcsDAO.get(ID);
		this.name = dt.getTriggerName();
		this.type = "TriggerOcs";
		
		// Build treeParent
		List<Formula> lstFormula = formulaDAO.findByTriggerOcs(ID);
		List<Long> lstID = new ArrayList<>();
		for(Formula formula: lstFormula) {
			lstID.add(formula.getFormulaId());
		}
		getListRateTableByFormula(lstID);
		
		List<Long> lstIDForSPC = new ArrayList<Long>();
		List<Long> lstIDForDR = new ArrayList<Long>();
		lstIDForSPC.addAll(lstID);
		lstIDForDR.addAll(lstID);		
				
		getListBlock(true, lstID);
		getListPC(true, lstID);
		
		getListSPC(true, lstIDForSPC);
		getListDR(true, lstIDForDR);		
		
		getListActionByDR(lstIDForDR);
		getListActionBySPC(lstIDForSPC);
		getListAction(true, lstID);
		
		lstID.addAll(lstIDForDR);		
		lstID.addAll(lstIDForSPC);
		getListOffer(true, lstID);
		
		// build treeChildren
		lstID.clear();
		lstID.add(-1L);
		lstID.add(ID);		
		getListNormalizers(false, lstID);
	}
	
	/********* GET LIST - BEGIN ***********/
	private void getListOffer(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<Offer> lstOffer = offerDAO.findOfferByActionId(lstId);
			lstId.clear();
			TreeNode nodeOffer = buildTreeRoot(CategoryType.OFF_OFFER_TEMPLATE, CategoryType.OFF_OFFER_NAME, rootParent);
			for (Offer offer : lstOffer) {
				offer.setVersionName(true);
				TreeNode offerNode = new DefaultTreeNode(offer, nodeOffer);
			}
		} else {
		}
	}

	private boolean checkExistAction(Action action) {
		
		for(Action act : lstCheckAction) {
			if(act.getActionId() == action.getActionId())
				return true;
		}
		
		return false;
	}

	private void getListAction(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<Action> lstAction = actionDAO.findActionByPC(lstId);
			TreeNode nodeAction = buildTreeRoot(CategoryType.OFF_ACTION, CategoryType.OFF_ACTION_NAME, rootParent);
			lstId.clear();
			for (Action action : lstAction) {
				
				if(checkExistAction(action))
					continue;
				else
					lstCheckAction.add(action);
				
				lstId.add(action.getActionId());
				TreeNode actionNode = new DefaultTreeNode(action, nodeAction);
				actionNode.setType(TreeNodeType.OFF_ACTION);
			}
		} else {
			List<Action> lstAction = actionDAO.findActionByListOffer(lstId);
			TreeNode nodeAction = buildTreeRoot(CategoryType.OFF_ACTION, CategoryType.OFF_ACTION_NAME, rootChilden);
			lstId.clear();
			for (Action action : lstAction) {
				
				if(checkExistAction(action))
					continue;
				else
					lstCheckAction.add(action);
				
				lstId.add(action.getActionId());
				TreeNode actionNode = new DefaultTreeNode(action, nodeAction);
				actionNode.setType(TreeNodeType.OFF_ACTION);
			}
		}
	}
	
	private void getListActionByDR(List<Long> lstId) {
//		if (isBuildTreeParent) {
			List<Action> lstAction = actionDAO.findActionByDR(lstId);
			TreeNode nodeAction = buildTreeRoot(CategoryType.OFF_ACTION, CategoryType.OFF_ACTION_NAME, rootParent);
			lstId.clear();
			for (Action action : lstAction) {

				if(checkExistAction(action))
					continue;
				else
					lstCheckAction.add(action);
				
				lstId.add(action.getActionId());
				TreeNode actionNode = new DefaultTreeNode(action, nodeAction);
				actionNode.setType(TreeNodeType.OFF_ACTION);
			}
//		} else {
//			List<Action> lstAction = actionDAO.findActionByListOffer(lstId);
//			TreeNode nodeAction = buildTreeRoot(CategoryType.OFF_ACTION, CategoryType.OFF_ACTION_NAME, rootChilden);
//			lstId.clear();
//			for (Action action : lstAction) {
//				lstId.add(action.getActionId());
//				TreeNode actionNode = new DefaultTreeNode(action, nodeAction);
//			}
//		}
	}
	
	private void getListActionBySPC(List<Long> lstId) {
//		if (isBuildTreeParent) {
			List<Action> lstAction = actionDAO.findActionBySPC(lstId);
			TreeNode nodeAction = buildTreeRoot(CategoryType.OFF_ACTION, CategoryType.OFF_ACTION_NAME, rootParent);
			lstId.clear();
			for (Action action : lstAction) {

				if(checkExistAction(action))
					continue;
				else
					lstCheckAction.add(action);
				
				lstId.add(action.getActionId());
				TreeNode actionNode = new DefaultTreeNode(action, nodeAction);
				actionNode.setType(TreeNodeType.OFF_ACTION);
			}
//		} else {
//			List<Action> lstAction = actionDAO.findActionByListOffer(lstId);
//			TreeNode nodeAction = buildTreeRoot(CategoryType.OFF_ACTION, CategoryType.OFF_ACTION_NAME, rootChilden);
//			lstId.clear();
//			for (Action action : lstAction) {
//				lstId.add(action.getActionId());
//				TreeNode actionNode = new DefaultTreeNode(action, nodeAction);
//			}
//		}
	}
	
	private void getListDR(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<DynamicReserve> lstDR = dynamicReserveDAO.findDRByListRT(lstId);
			TreeNode nodeDR = buildTreeRoot(CategoryType.OFF_DR_DYNAMIC_RESERVE, CategoryType.OFF_DR_DYNAMIC_RESERVE_NAME, rootParent);
			lstId.clear();
			for (DynamicReserve dr : lstDR) {
				lstId.add(dr.getDynamicReserveId());
				TreeNode drNode = new DefaultTreeNode(dr, nodeDR);
			}
		} else {
			List<DynamicReserve> lstDR = dynamicReserveDAO.findDRByListAction(lstId);
			TreeNode nodeDR = buildTreeRoot(CategoryType.OFF_DR_DYNAMIC_RESERVE, CategoryType.OFF_DR_DYNAMIC_RESERVE_NAME, rootChilden);
			lstId.clear();
			for (DynamicReserve dr : lstDR) {
				lstId.add(dr.getDynamicReserveId());
				TreeNode drNode = new DefaultTreeNode(dr, nodeDR);
			}
		}
	}
	
	private void getListSPC(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<SortPriceComponent> lstSPC = sortPriceComponentDAO.findSortPriceComponentByListRT(lstId);
			TreeNode nodeSPC = buildTreeRoot(CategoryType.OFF_SPC_SORT_PRICE_COMPONENT, CategoryType.OFF_SPC_SORT_PRICE_COMPONENT_NAME, rootParent);
			lstId.clear();
			for (SortPriceComponent spc : lstSPC) {
				lstId.add(spc.getSortPriceComponentId());
				TreeNode spcNode = new DefaultTreeNode(spc, nodeSPC);
			}
		} else {
			List<SortPriceComponent> lstSPC = sortPriceComponentDAO.findSPCByListAction(lstId);
			TreeNode nodeSPC = buildTreeRoot(CategoryType.OFF_SPC_SORT_PRICE_COMPONENT, CategoryType.OFF_SPC_SORT_PRICE_COMPONENT_NAME, rootChilden);
			lstId.clear();
			for (SortPriceComponent spc : lstSPC) {
				lstId.add(spc.getSortPriceComponentId());
				TreeNode spcNode = new DefaultTreeNode(spc, nodeSPC);
			}
		}
	}
	
	private boolean checkExistRateTable(RateTable rtCheck) {
		
		for(RateTable rt : lstCheckRateTable) {
			if(rt.getRateTableId() == rtCheck.getRateTableId())
				return true;
		}
		
		return false;
	}

	private void getListRateTableByDR(List<Long> lstId) {
//		if (isBuildTreeParent) {
//			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListDesId(lstId);
//			lstId.clear();
//			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootParent);
//			for (RateTable rateTable : lstRateTable) {
//				lstId.add(rateTable.getRateTableId());
//				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
//			}
//		} else {
			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListDR(lstId);
			lstId.clear();
			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootChilden);
			for (RateTable rateTable : lstRateTable) {
				
				if(checkExistRateTable(rateTable))
					continue;
				else
					lstCheckRateTable.add(rateTable);
				
				lstId.add(rateTable.getRateTableId());
				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
			}
//		}
	}

	private void getListRateTableBySPC(List<Long> lstId) {
//		if (isBuildTreeParent) {
//			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListDesId(lstId);
//			lstId.clear();
//			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootParent);
//			for (RateTable rateTable : lstRateTable) {
//				lstId.add(rateTable.getRateTableId());
//				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
//			}
//		} else {
			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListSPC(lstId);
			lstId.clear();
			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootChilden);
			for (RateTable rateTable : lstRateTable) {

				if(checkExistRateTable(rateTable))
					continue;
				else
					lstCheckRateTable.add(rateTable);
				
				lstId.add(rateTable.getRateTableId());
				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
			}
//		}
	}
	
	private void getListRateTableByFormula(List<Long> lstId) {
//		if (isBuildTreeParent) {
			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListFormula(lstId);
			lstId.clear();
			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootParent);
			for (RateTable rateTable : lstRateTable) {
				
				if(checkExistRateTable(rateTable))
					continue;
				else
					lstCheckRateTable.add(rateTable);
				
				lstId.add(rateTable.getRateTableId());
				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
			}
//		} else {
//			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListSPC(lstId);
//			lstId.clear();
//			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootChilden);
//			for (RateTable rateTable : lstRateTable) {
//
//				if(checkExistRateTable(rateTable))
//					continue;
//				else
//					lstCheckRateTable.add(rateTable);
//				
//				lstId.add(rateTable.getRateTableId());
//				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
//			}
////		}
	}
	
	private boolean checkExistPcType(PcType rtCheck) {
		
		for(PcType rt : lstCheckPcType) {
			if(rt.getPcTypeId() == rtCheck.getPcTypeId())
				return true;
		}
		
		return false;
	}
	
	private boolean checkExistPc(PriceComponent rtCheck) {
		
		for(PriceComponent rt : lstCheckPc) {
			if(rt.getPriceComponentId() == rtCheck.getPriceComponentId())
				return true;
		}
		
		return false;
	}
	
	private boolean checkExistBlock(Block rtCheck) {
		
		for(Block rt : lstCheckBlock) {
			if(rt.getBlockId() == rtCheck.getBlockId())
				return true;
		}
		
		return false;
	}
	
	private void getListPCType(boolean isBuildTreeParent, List<Long> lstBlockId, List<Long> lstPcId, List<Long> lstSPCId, List<Long> lstSPCTypeReturn) {
		if (isBuildTreeParent) {
			if(lstBlockId != null) {
				
				List<PcType> lstPC = pcTypeDAO.findPCTypeByListBlock(lstBlockId);
				TreeNode nodePC = buildTreeRoot(CategoryType.OFF_PC_TYPE, CategoryType.OFF_PC_TYPE_NAME, rootParent);
				for (PcType pc : lstPC) {
					
					if(checkExistPcType(pc))
						continue;
					else
						lstCheckPcType.add(pc);
					
					lstSPCTypeReturn.add(pc.getPcTypeId());
					TreeNode pcTypeNode = new DefaultTreeNode(pc, nodePC);
				}	
			}
			
			if(lstSPCId != null) {
				
				List<PcType> lstPC = pcTypeDAO.findPCTypeByListSPC(lstSPCId);
				TreeNode nodePC = buildTreeRoot(CategoryType.OFF_PC_TYPE, CategoryType.OFF_PC_TYPE_NAME, rootParent);
//				lstSPCId.clear();
				for (PcType pc : lstPC) {

					if(checkExistPcType(pc))
						continue;
					else
						lstCheckPcType.add(pc);
					
					lstSPCTypeReturn.add(pc.getPcTypeId());
					TreeNode pcTypeNode = new DefaultTreeNode(pc, nodePC);
				}	
			}
		} else {

			if (lstPcId != null) {
				
				List<PcType> lstPC = pcTypeDAO.findPCTypeByListPC(lstPcId);
				TreeNode nodePC = buildTreeRoot(CategoryType.OFF_PC_TYPE, CategoryType.OFF_PC_TYPE_NAME, rootChilden);
				for (PcType pc : lstPC) {
					
					if(checkExistPcType(pc))
						continue;
					else
						lstCheckPcType.add(pc);
					
					lstSPCTypeReturn.add(pc.getPcTypeId());
					TreeNode pcTypeNode = new DefaultTreeNode(pc, nodePC);
				}
			}					
		}
	}

	private void getListPCByPcType(List<Long> lstId, List<Long> lstIDPc) {
		
		List<PriceComponent> lstPC = priceComponentDAO.findPCByPcType(lstId);
		TreeNode nodePC = buildTreeRoot(CategoryType.OFF_PC_PRICE_COMPONENT, CategoryType.OFF_PC_PRICE_COMPONENT_NAME, rootParent);
		for (PriceComponent pc : lstPC) {
			
			if(checkExistPc(pc))
				continue;
			else
				lstCheckPc.add(pc);
			
			lstIDPc.add(pc.getPriceComponentId());
			TreeNode blockNode = new DefaultTreeNode(pc, nodePC);
		}
	}
	
	private void getListPC(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<PriceComponent> lstPC = priceComponentDAO.findPCByListBlock(lstId);
			TreeNode nodePC = buildTreeRoot(CategoryType.OFF_PC_PRICE_COMPONENT, CategoryType.OFF_PC_PRICE_COMPONENT_NAME, rootParent);
			lstId.clear();
			for (PriceComponent pc : lstPC) {
				
				if(checkExistPc(pc))
					continue;
				else
					lstCheckPc.add(pc);
				
				lstId.add(pc.getPriceComponentId());
				TreeNode blockNode = new DefaultTreeNode(pc, nodePC);
			}
		} else {
			List<PriceComponent> lstPC = priceComponentDAO.findPCByAction(lstId);
			TreeNode nodePC = buildTreeRoot(CategoryType.OFF_PC_PRICE_COMPONENT, CategoryType.OFF_PC_PRICE_COMPONENT_NAME, rootChilden);
			lstId.clear();
			for (PriceComponent pc : lstPC) {
				
				if(checkExistPc(pc))
					continue;
				else
					lstCheckPc.add(pc);
				
				lstId.add(pc.getPriceComponentId());
				TreeNode blockNode = new DefaultTreeNode(pc, nodePC);
			}
		}
	}
	
	private void getListBlockByPcTypeAndSPC(List<Long> lstId, List<Long> lstSPCId, List<Long> lstIDBlock) {
		
		if(lstId != null) {
			List<Block> lstBlock = blockDAO.findBlockByPcTypeList(lstId);
			TreeNode nodeBlock = buildTreeRoot(CategoryType.OFF_BLOCK_BLOCK, CategoryType.OFF_BLOCK_BLOCK_NAME, rootChilden);
			for (Block block : lstBlock) {
				
				if(checkExistBlock(block))
					continue;
				else
					lstCheckBlock.add(block);
				
				lstIDBlock.add(block.getBlockId());
				TreeNode blockNode = new DefaultTreeNode(block, nodeBlock);
			}	
		} else if (lstSPCId != null) {
			
			List<Block> lstBlock = blockDAO.findBlockBySPC(lstSPCId);
			TreeNode nodeBlock = buildTreeRoot(CategoryType.OFF_BLOCK_BLOCK, CategoryType.OFF_BLOCK_BLOCK_NAME, rootParent);
			for (Block block : lstBlock) {
				
				if(checkExistBlock(block))
					continue;
				else
					lstCheckBlock.add(block);
				
				lstIDBlock.add(block.getBlockId());
				TreeNode blockNode = new DefaultTreeNode(block, nodeBlock);
			}
		}
		
	}
	
	private void getListBlock(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<Block> lstBlock = blockDAO.findRatetableByListRateTableId(lstId);
			lstId.clear();
			TreeNode nodeBlock = buildTreeRoot(CategoryType.OFF_BLOCK_BLOCK, CategoryType.OFF_BLOCK_BLOCK_NAME, rootParent);
			for (Block block : lstBlock) {
				
				if(checkExistBlock(block))
					continue;
				else
					lstCheckBlock.add(block);
				
				lstId.add(block.getBlockId());
				TreeNode blockNode = new DefaultTreeNode(block, nodeBlock);
			}
		} else {
			List<Block> lstBlock = blockDAO.findBlockByListPC(lstId);
			lstId.clear();
			TreeNode nodeBlock = buildTreeRoot(CategoryType.OFF_BLOCK_BLOCK, CategoryType.OFF_BLOCK_BLOCK_NAME, rootChilden);
			for (Block block : lstBlock) {
				
				if(checkExistBlock(block))
					continue;
				else
					lstCheckBlock.add(block);
				
				lstId.add(block.getBlockId());
				TreeNode blockNode = new DefaultTreeNode(block, nodeBlock);
			}
		}
	}

	private void getListRateTable(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListDesId(lstId);
			lstId.clear();
			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootParent);
			for (RateTable rateTable : lstRateTable) {

				if(checkExistRateTable(rateTable))
					continue;
				else
					lstCheckRateTable.add(rateTable);
				
				lstId.add(rateTable.getRateTableId());
				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
			}
		} else {
			List<RateTable> lstRateTable = rateTableDAO.findRateTableByListBlock(lstId);
			lstId.clear();
			TreeNode nodeRateTable = buildTreeRoot(CategoryType.OFF_RT_RATE_TABLE, CategoryType.OFF_RT_RATE_TABLE_NAME, rootChilden);
			for (RateTable rateTable : lstRateTable) {

				if(checkExistRateTable(rateTable))
					continue;
				else
					lstCheckRateTable.add(rateTable);
				
				lstId.add(rateTable.getRateTableId());
				TreeNode rateTableNode = new DefaultTreeNode(rateTable, nodeRateTable);
			}
		}
	}

	private void getListDecisionTable(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			List<DecisionTable> lstDecisionTables = decisionTableDAO.findDecisionTableByListNormalizersId(lstId);
			lstId.clear();
			TreeNode nodeDecisionTable = buildTreeRoot(CategoryType.OFF_DT_DECISION_TABLE, CategoryType.OFF_DT_DECISION_TABLE_NAME, rootParent);
			for (DecisionTable decisionTable : lstDecisionTables) {
				lstId.add(decisionTable.getDecisionTableId());
				TreeNode decisionNode = new DefaultTreeNode(decisionTable, nodeDecisionTable);
			}
		} else {
			List<DecisionTable> lstDecisionTable = decisionTableDAO.findByListRateTableId(lstId);
			lstId.clear();
			TreeNode nodeDecisionTable = buildTreeRoot(CategoryType.OFF_DT_DECISION_TABLE, CategoryType.OFF_DT_DECISION_TABLE_NAME, rootChilden);
			for (DecisionTable decisionTable : lstDecisionTable) {
				lstId.add(decisionTable.getDecisionTableId());
				TreeNode decisionNode = new DefaultTreeNode(decisionTable, nodeDecisionTable);
			}
		}
	}

	private void getListNormalizers(boolean isBuildTreeParent, List<Long> lstId) {
		if (isBuildTreeParent) {
			TreeNode nodeNormalizers = buildTreeRoot(CategoryType.OFF_NOM_NORMALIZER, CategoryType.OFF_NOM_NORMALIZER_NAME, rootParent);
			// TODO
		} else {
			TreeNode nodeNormalizers = buildTreeRoot(CategoryType.OFF_NOM_NORMALIZER, CategoryType.OFF_NOM_NORMALIZER_NAME, rootChilden);
			List<Normalizer> lstNormalizers = normalizerDAO.findNormalizersByListDesId(lstId);
			lstId.clear();
			for (Normalizer normalizer : lstNormalizers) {
				lstId.add(normalizer.getNormalizerId());
				TreeNode normalizerNode = new DefaultTreeNode(normalizer, nodeNormalizers);
			}
		}
	}
	
	/********* GET LIST - END ***********/

	/*****************EVENT TREE*********************/
	public void btnJumpToObject() {
		
		super.showPopupOffer(100452L, Offer.class);
	}

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
	}

	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {
	}

	// ************EVENT TREE CHILDREN**************

	public void btnJumpToObjectChilden() {

	}

	public void onNodeSelectChilden(NodeSelectEvent nodeSlectedEvent) {
	}

	public void onNodeExpandChilden(NodeExpandEvent nodeExpandEvent) {
	}
}

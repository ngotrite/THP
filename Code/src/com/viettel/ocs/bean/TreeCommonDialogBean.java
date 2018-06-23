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
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.BillingCycleTypeDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrGenFilenameDAO;
import com.viettel.ocs.dao.CdrServiceDAO;
import com.viettel.ocs.dao.CdrTemplateDAO;
import com.viettel.ocs.dao.PCCRuleDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.dao.StatisticItemDAO;
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.entity.BalType;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrGenFilename;
import com.viettel.ocs.entity.CdrService;
import com.viettel.ocs.entity.CdrTemplate;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.StatisticItem;
import com.viettel.ocs.entity.TriggerOcs;

@ManagedBean(name = "treeCommonDialogBean")
@ViewScoped
public class TreeCommonDialogBean extends BaseController implements Serializable {
	private static final long serialVersionUID = -2567122270360514722L;
	/**
	 * @author Nampv 060916
	 */
	private String treeType;
	private long catType;
	private Map<Long, TreeNode> mapCatNode = new HashMap<Long, TreeNode>();
	private List<Long> lstAllCatID = new ArrayList<Long>();
	private Map<String, TreeNode> mapAllNode = new HashMap<String, TreeNode>();
	private Object objReturn;
	private TreeNode root;
	private TreeNode treeNodeSelected;
	private CategoryDAO catDao;
	private ParameterDAO parameterDAO;
	private BalTypeDAO balTypeDAO;
	private TriggerOcsDAO triggerOcsDAO;
	private ReserveInfoDAO reserveInfoDAO;
	private BillingCycleTypeDAO billingCycleTypeDAO;
	private PCCRuleDAO pccRuleDAO;
	private CdrServiceDAO cdrServiceDAO;
	private CdrTemplateDAO cdrTemplateDAO;
	private CdrGenFilenameDAO cdrGenFilenameDAO;
	private StatisticItemDAO statisticItemDAO;

	private String categoryTypeName;
	private String title;

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getTreeNodeSelected() {
		return treeNodeSelected;
	}

	public void setTreeNodeSelected(TreeNode treeNodeSelected) {
		this.treeNodeSelected = treeNodeSelected;
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

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public long getCatType() {
		return catType;
	}

	public void setCatType(long catType) {
		this.catType = catType;
	}

	public Object getObjReturn() {
		return objReturn;
	}

	public void setObjReturn(Object objReturn) {
		this.objReturn = objReturn;
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

	public TreeCommonDialogBean() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		treeType = params.get("treeType");
		lstSelectedID = new ArrayList<Long>();
		if (treeType.contains(";")) {
			String[] parts = treeType.split(";");
			treeType = parts[0];
			categoryTypeName = parts[1];
			catType = Long.parseLong(parts[2]);
			isMulti = Boolean.parseBoolean(parts[3]);
			String[] ids = parts[4].split(",");
			for(String id : ids) {
				lstSelectedID.add(Long.parseLong(id));
			}
		} else
			return;
		title = "Select a " + categoryTypeName;
		catDao = new CategoryDAO();
		parameterDAO = new ParameterDAO();
		balTypeDAO = new BalTypeDAO();
		triggerOcsDAO = new TriggerOcsDAO();
		reserveInfoDAO = new ReserveInfoDAO();
		billingCycleTypeDAO = new BillingCycleTypeDAO();
		pccRuleDAO = new PCCRuleDAO();
		cdrServiceDAO = new CdrServiceDAO();
		cdrTemplateDAO = new CdrTemplateDAO();
		cdrGenFilenameDAO = new CdrGenFilenameDAO();
		statisticItemDAO = new StatisticItemDAO();
		buildTree(treeType);
	}

	// ********EVENT NODE *********
	private void buildTree(String treeType) {

		if (treeType == null)
			return;

		root = new DefaultTreeNode();

		switch (treeType) {
		case TreeType.CATALOG_PARAMETER:
			buildTreeParameter(false);
			break;
		case TreeType.CATALOG_BALANCE_ACC:
			buildTreeBalance();
			break;
		case TreeType.CATALOG_BALANCES:
			buildTreeBalance();
			break;
		case TreeType.CATALOG_TRIGGER_OCS:
			buildTreeTriggerOcs();
			break;
		case TreeType.CATALOG_RESERVE_INFO:
			buildTreeReserveInfo();
			break;
		case TreeType.CATALOG_BILLING_CYCLE:
			buildBillingCycleType();
			break;
		case TreeType.POLICY_PCC_RULE:
			buildPolicyPccRule();
			break;
		case TreeType.CATALOG_CDR_SERVICE:
			buildCdrService();
			break;
		case TreeType.CATALOG_CDR_TEMPLATE:
			buildCdrTemplate();
			break;
		case TreeType.CATALOG_CDR_GEN_FILENAME:
			buildCdrGenFilename();
			break;
		case TreeType.CATALOG_STATISTIC_ITEM:
			buildStatisticItem();
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
		if (catType > 0) {
			mapType.clear();
			mapType.put(catType, categoryTypeName);
		}

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

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
		treeNodeSelected = nodeSlectedEvent.getTreeNode();
		if (treeNodeSelected != null) {
			objReturn = treeNodeSelected.getData();
		}
	}

	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {
	}

	public void onDialogReturn() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public void onDialogCreate() {
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

	public void buildTreeParameter(boolean isForTempOnly) {
		// Build children categories
		buildTreeRootParent(treeType, root);
		// Add Parameter belong to Category
		List<Parameter> lstParameter;
		if (isForTempOnly) {
			lstParameter = parameterDAO.findParamByCatIdOlyForTemp(lstAllCatID);
		} else {
			lstParameter = parameterDAO.findParamByCatId(lstAllCatID);
		}

		for (Parameter parameter : lstParameter) {
			
			if(lstSelectedID.contains(parameter.getParameterId()))
				continue;
			
			TreeNode catNode = mapCatNode.get(parameter.getCategoryId());
			if (catNode != null) {
				TreeNode parameterNode = new DefaultTreeNode(catNode.getType(), parameter, catNode);
				parameterNode.setExpanded(true);
				mapAllNode.put(parameter.getUniqueKey(), parameterNode);
			}
		}
	}

	public void reBuildTreeParamter(Boolean forTempOnly) {
		root.getChildren().clear();
		buildTreeParameter(forTempOnly);
	}

	public void buildTreeBalance() {
		// Build children categories
		buildTreeRootParent(treeType, root);
		List<BalType> lstBalType = balTypeDAO.findBalTypeByCatId(lstAllCatID);
		for (BalType balTypeMap : lstBalType) {

			if(lstSelectedID.contains(balTypeMap.getBalTypeId()))
				continue;
			
			TreeNode catNode = mapCatNode.get(balTypeMap.getCategoryId());
			if (catNode != null) {
				TreeNode balTypeMapNode = new DefaultTreeNode(catNode.getType(), balTypeMap, catNode);
				balTypeMapNode.setExpanded(true);
				mapAllNode.put(balTypeMap.getUniqueKey(), balTypeMapNode);
			}
		}

	}

	public void buildTreeTriggerOcs() {
		buildTreeRootParent(treeType, root);
		List<TriggerOcs> lstTriggerOcs = triggerOcsDAO.findTriggerOcsByListCatId(lstAllCatID);
		for (TriggerOcs triggerOcs : lstTriggerOcs) {

			if(lstSelectedID.contains(triggerOcs.getTriggerOcsId()))
				continue;
			
			TreeNode catNode = mapCatNode.get(triggerOcs.getCategoryId());
			if (catNode != null) {
				TreeNode triggerOcsNode = new DefaultTreeNode(catNode.getType(), triggerOcs, catNode);
				triggerOcsNode.setExpanded(true);
				mapAllNode.put(triggerOcs.getUniqueKey(), triggerOcsNode);
			}
		}
	}

	private void buildTreeReserveInfo() {
		// Build children categories
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<ReserveInfo> lstReserveInfos = reserveInfoDAO.findReserveInfoByConditions(lstAllCatID);
		for (ReserveInfo reserveinfo : lstReserveInfos) {

			if(lstSelectedID.contains(reserveinfo.getReserveInfoId()))
				continue;
			
			TreeNode catNode = mapCatNode.get(reserveinfo.getCategoryId());
			if (catNode != null) {
				TreeNode reserveinfoNode = new DefaultTreeNode(catNode.getType(), reserveinfo, catNode);
				reserveinfoNode.setExpanded(true);
				mapAllNode.put(reserveinfo.getUniqueKey(), reserveinfoNode);
			}
		}
	}

	private void buildBillingCycleType() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<BillingCycleType> lstBillingCycleType = billingCycleTypeDAO.findBillingCycleTypeByCategoryId(lstAllCatID);
		for (BillingCycleType billingCycleType : lstBillingCycleType) {

			if(lstSelectedID.contains(billingCycleType.getBillingCycleTypeId()))
				continue;

			TreeNode catNode = mapCatNode.get(billingCycleType.getCategoryId());
			if (catNode != null) {
				TreeNode billingCycleTypeNode = new DefaultTreeNode(catNode.getType(), billingCycleType, catNode);
				billingCycleTypeNode.setExpanded(true);
				mapAllNode.put(billingCycleType.getUniqueKey(), billingCycleTypeNode);
			}
		}
	}

	private void buildPolicyPccRule() {
		buildTreeRootParent(treeType, root);
		// Add reserveInfo to Category children
		List<PccRule> lstPccRule = pccRuleDAO.findPccRuleByCategoryId(lstAllCatID);
		for (PccRule pccRule : lstPccRule) {

			if(lstSelectedID.contains(pccRule.getPccRuleId()))
				continue;

			TreeNode catNode = mapCatNode.get(pccRule.getCategoryId());
			if (catNode != null) {
				TreeNode pccRuleNode = new DefaultTreeNode(catNode.getType(), pccRule, catNode);
				pccRuleNode.setExpanded(true);
				mapAllNode.put(pccRule.getUniqueKey(), pccRuleNode);
			}
		}
	}

	private void buildCdrService() {
		buildTreeRootParent(treeType, root);
		List<CdrService> lstCdrService = cdrServiceDAO.findCdrServiceByConditions(lstAllCatID);
		for (CdrService cdrService : lstCdrService) {

			if(lstSelectedID.contains(cdrService.getCdrServiceId()))
				continue;

			TreeNode catNode = mapCatNode.get(cdrService.getCategoryId());
			if (catNode != null) {
				TreeNode cdrServiceNode = new DefaultTreeNode(catNode.getType(), cdrService, catNode);
				cdrServiceNode.setExpanded(true);
				mapAllNode.put(cdrService.getUniqueKey(), cdrServiceNode);
			}
		}
	}

	private void buildCdrTemplate() {
		buildTreeRootParent(treeType, root);
		List<CdrTemplate> lstCdrTemplate = cdrTemplateDAO.findCdrTemplateByConditions(lstAllCatID);
		for (CdrTemplate cdrTemplate : lstCdrTemplate) {

			if(lstSelectedID.contains(cdrTemplate.getCdrTemplateId()))
				continue;
			
			TreeNode catNode = mapCatNode.get(cdrTemplate.getCategoryId());
			if (catNode != null) {
				TreeNode cdrTemplateNode = new DefaultTreeNode(catNode.getType(), cdrTemplate, catNode);
				cdrTemplateNode.setExpanded(true);
				mapAllNode.put(cdrTemplate.getUniqueKey(), cdrTemplateNode);
			}
		}
	}

	private void buildCdrGenFilename() {
		buildTreeRootParent(treeType, root);
		List<CdrGenFilename> lstCdrGenFilename = cdrGenFilenameDAO.findCdrGenFilenameByConditions(lstAllCatID);
		for (CdrGenFilename cdrGenFilename : lstCdrGenFilename) {

			if(lstSelectedID.contains(cdrGenFilename.getCdrGenFilenameId()))
				continue;

			TreeNode catNode = mapCatNode.get(cdrGenFilename.getCategoryId());
			if (catNode != null) {
				TreeNode cdrGenFilenameNode = new DefaultTreeNode(catNode.getType(), cdrGenFilename, catNode);
				cdrGenFilenameNode.setExpanded(true);
				mapAllNode.put(cdrGenFilename.getUniqueKey(), cdrGenFilenameNode);
			}
		}
	}
	
	private void buildStatisticItem() {
		buildTreeRootParent(treeType, root);
		List<StatisticItem> lstStatisticItem = statisticItemDAO.findByListCat(lstAllCatID);
		for (StatisticItem statisticItem : lstStatisticItem) {

			if(lstSelectedID.contains(statisticItem.getStatisticItemId()))
				continue;

			TreeNode catNode = mapCatNode.get(statisticItem.getCategoryId());
			if (catNode != null) {
				TreeNode statisticItemNode = new DefaultTreeNode(catNode.getType(), statisticItem, catNode);
				statisticItemNode.setExpanded(true);
				mapAllNode.put(statisticItem.getUniqueKey(), statisticItemNode);
			}
		}
	}

}

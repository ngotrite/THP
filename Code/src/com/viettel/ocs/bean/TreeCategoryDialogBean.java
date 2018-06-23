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
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.RateTableDAO;
//import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Normalizer;

@ManagedBean(name = "treeCategoryDialogBean")
@ViewScoped
public class TreeCategoryDialogBean extends BaseController implements
		Serializable {

	/**
	 * huannn
	 */
	private static final long serialVersionUID = 1L;
		
	private TreeNode root;
	private String treeType;
	private long catType;
	private Map<Long, TreeNode> mapCatNode = new HashMap<Long, TreeNode>();
	private List<Long> lstAllCatID = new ArrayList<Long>();
	private Map<String, TreeNode> mapAllNode = new HashMap<String, TreeNode>();
	private Category objReturn;
	private String title;
	private String categoryTypeName;
	
	private CategoryDAO catDao;

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

	public void setObjReturn(Category objReturn) {
		this.objReturn = objReturn;
	}

	public TreeNode getTreeNodeSelected() {
		return treeNodeSelected;
	}

	public void setTreeNodeSelected(TreeNode treeNodeSelected) {
		this.treeNodeSelected = treeNodeSelected;
	}

	private TreeNode treeNodeSelected;

	public long getCatType() {
		return catType;
	}

	public void setCatType(long catType) {
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

	public TreeCategoryDialogBean() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		treeType = params.get("treeType");
		if (treeType.contains(";")) {
			String[] parts = treeType.split(";");
			treeType = parts[0];
			categoryTypeName = parts[1];
			catType = Long.parseLong(parts[2]);
		} else
			return;
		title = super.readProperties("common.select") + " " + categoryTypeName;
		catDao = new CategoryDAO();
		buildTree(treeType);
	}

	private void buildTree(String treeType) {

		if (treeType == null)
			return;

		root = new DefaultTreeNode();
		buildTreeRootParent(treeType, root);
	}

	private void buildTreeRootParent(String treeType, TreeNode root) {
		mapCatNode.clear();
		lstAllCatID.clear();
		mapAllNode.clear();
		Map<Long, String> mapType = CategoryType.getCatTypesByTreeType(treeType);
		if(catType > 0) {
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

			TreeNode node = new DefaultTreeNode(cat,
					root);
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

			if (cat.getCategoryParentId() == null|| cat.getCategoryParentId() == 0) {

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
					TreeNode node = new DefaultTreeNode( cat, null);
					node.setExpanded(true);
					lstNodeNotAdded.add(node);
					lstNodeNew.add(node);
				}
			}
		}

		for (TreeNode node : lstNodeNotAdded) {
			for (TreeNode nodeAdded : lstNodeNew) {

				if (((Category) node.getData()).getCategoryParentId() == ((Category) nodeAdded.getData()).getCategoryId()) {
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
	
	// *********END***********************

	// ********EVENT NODE *********

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
		treeNodeSelected = nodeSlectedEvent.getTreeNode();
		if (treeNodeSelected != null) {
			objReturn = (Category) treeNodeSelected.getData();
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
		
		if (objReturn == null || objReturn.getCategoryId() <= 0) {

			super.showMessageWARN("", "", "common.warnSelectObject");
			return;
		}
		RequestContext.getCurrentInstance().closeDialog(objReturn);

	}
	// *********END****************
}

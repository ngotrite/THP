package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.LangDAO;
import com.viettel.ocs.dao.SysMenuDAO;
import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.entity.BaseEntity;
import com.viettel.ocs.entity.Lang;
import com.viettel.ocs.entity.SysMenu;
import com.viettel.ocs.entity.SysMenuTrl;
import com.viettel.ocs.util.CommonUtil;

//@Component
@ManagedBean(name="menuBean")
@ViewScoped
public class MenuBean extends BaseController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219556076115932261L;
	private SysMenu sysMenu = new SysMenu();
	private List<SysMenu> listSysMenu;
	private boolean isEditing;
	private long parentId;
	private List<SelectItem> listSelectItemParent;
	private List<SysMenuTrl> listTranslation;
	private List<Lang> listLang;
	
	// Spring DI
	//@Autowired
	private SysMenuDAO menuDao;
	private TreeNode root;
	private TreeNode dumpNode;
	private Map<Long, TreeNode> mapTreeNode;
	private TreeNode selectedNode;
	private static final String TREE_NODE_MENU = "TREE_NODE_MENU";
	private NodeSelectEvent nodeSelectEvent;
	
	public MenuBean() {
		
		menuDao = new SysMenuDAO();
		init();
	}
	
	private void init() {
		
		sysMenu = new SysMenu();
		sysMenu.setIsActive(true);
		parentId = 0;
		listTranslation = new ArrayList<>();
		buildTreeMenu();
		loadParentSelectbox();
		searchLang();
	}
			
	private void loadParentSelectbox() {
		
		if(listSelectItemParent == null)
			listSelectItemParent = new ArrayList<>();
		else
			listSelectItemParent.clear();
		
		listSelectItemParent.add(new SelectItem(0, ""));
		List<SysMenu> listMenuParent = menuDao.findNotEqualForSelectbox(sysMenu.getId());		
		for (SysMenu menu : listMenuParent) {
			
//			int level = StringUtils.countMatches(menu.getPath(), "/");
//			String name = StringUtils.repeat("---", level - 1) + " " + menu.getName();
			listSelectItemParent.add(new SelectItem(menu.getId(), menu.getName()));
		}
	}
	
	private void buildTreeMenu() {

		mapTreeNode = new HashMap<Long, TreeNode>();
		root = new DefaultTreeNode();
		SysMenu dumpMenuObj = new SysMenu();
		dumpMenuObj.setId(-1);
		dumpMenuObj.setName("Main menu");
		dumpNode = new DefaultTreeNode(dumpMenuObj, root);
		dumpNode.setExpanded(true);
		
		List<SysMenu> lstMenu = menuDao.findAll(false);
		List<TreeNode> lstNodeNew = new ArrayList<TreeNode>();
		List<TreeNode> lstNodeNotAdded = new ArrayList<TreeNode>();

		for (SysMenu menu : lstMenu) {

			if (menu.getParentId() == null || menu.getParentId() == 0) {

				TreeNode node = new DefaultTreeNode(menu, dumpNode);
				node.setType(TREE_NODE_MENU);
				//node.setExpanded(true);
				mapTreeNode.put(menu.getId(), node);
				lstNodeNew.add(node);
			} else {

				boolean isFound = false;
				for (TreeNode parentNode : lstNodeNew) {
					if (menu.getParentId() == ((SysMenu) parentNode.getData()).getId()) {

						TreeNode node = new DefaultTreeNode(menu, parentNode);
						node.setType(TREE_NODE_MENU);
						//node.setExpanded(true);
						mapTreeNode.put(menu.getId(), node);
						lstNodeNew.add(node);
						isFound = true;
						break;
					}
				}

				if (!isFound) {

					TreeNode node = new DefaultTreeNode(menu, null);
					node.setType(TREE_NODE_MENU);
					node.setExpanded(true);
					mapTreeNode.put(menu.getId(), node);
					lstNodeNotAdded.add(node);
					lstNodeNew.add(node);
				}
			}
		}

		for (TreeNode node : lstNodeNotAdded) {
			for (TreeNode nodeAdded : lstNodeNew) {

				if (((SysMenu) node.getData()).getParentId() == ((SysMenu) nodeAdded.getData()).getId()) {

					node.setParent(nodeAdded);
					nodeAdded.getChildren().add(node);
					break;
				}
			}
		}
	}
	
	private void searchChildrenMenu() {
		
		if(sysMenu == null)
			return;
		
		listSysMenu = menuDao.findChildren(sysMenu.getId());
	}
	
	private void searchTranslation() {
		
		if(sysMenu == null)
			return;
		
		listTranslation = menuDao.findTranslations(sysMenu.getId());
	}
	
	private void searchLang() {
				
		LangDAO langDAO = new LangDAO();
		listLang = langDAO.findAll("");
		Lang dump = new Lang();
		dump.setLangName("");
		listLang.add(0, dump);
	}
	
	public void addTranslation() {
		
		SysMenuTrl trl = new SysMenuTrl();
		listTranslation.add(trl);
	}
	
	public void removeTranslation(SysMenuTrl trl) {
		
		listTranslation.remove(trl);
	}
	
	public String getLangName(String langCode) {
		
		for(Lang lang : listLang) {
			if(lang.getLangCode() != null && lang.getLangCode().equals(langCode))
				return lang.getLangName();
		}
		return "";
	}
			
	public void btnNew() {
		
		init();
		isEditing = true;
	}
	
	public void btnCancel() {
		
		init();
		isEditing = false;
	}
	
	public void btnSave() {
	
		doSave(sysMenu, parentId);
	}
	
	private boolean doSave(SysMenu sysMenu, long parentId) {
		
		if(sysMenu.getId() < 0)
			return false;
		
		if(!validateSave())
			return false;
		
		if(parentId > 0) {
			sysMenu.setParentId(parentId);
		} else {			
			sysMenu.setParentId(null);
		}
			
		if(sysMenu.getId() > 0) {
			
			if (parentId == sysMenu.getId() || menuDao.isContainInTree(parentId, sysMenu.getId())) {
				this.showMessageWARN("common.save", " Menu ", "cat.saveWarnRecursive");
				return false;
			}
			
			menuDao.saveOrUpdate(sysMenu, listTranslation);
						
			TreeNode parentNode = mapTreeNode.get(parentId);
			if(parentNode == null)
				parentNode = dumpNode;
			TreeNode node = mapTreeNode.get(sysMenu.getId());
			
			SysMenu nodeSysMenu = (SysMenu) node.getData();
			if (nodeSysMenu != sysMenu)
				HibernateUtil.copyEntityProperties(sysMenu, nodeSysMenu);
			
			TreeNode oldParent = node.getParent();
			if (oldParent != parentNode) {

				if (oldParent != null)
					oldParent.getChildren().remove(node);
				node.setParent(parentNode);
				parentNode.getChildren().add(node);
			}
		} else {
			
			menuDao.saveOrUpdate(sysMenu, listTranslation);
			listSysMenu.add(sysMenu);
			
			// Add node vao tree
			TreeNode parentNode = mapTreeNode.get(parentId);
			if(parentNode == null)
				parentNode = dumpNode;

			TreeNode node = new DefaultTreeNode(sysMenu, parentNode);
			node.setType(TREE_NODE_MENU);
			mapTreeNode.put(sysMenu.getId(), node);
		}			
		
//		btnCancel();
		this.showMessageINFO("common.save", " Menu ");
		return true;
	}
	
	private boolean validateSave() {
		
		SysMenu checkObj = menuDao.findByName(sysMenu.getName(), sysMenu.getId());
		if(checkObj != null) {
			
			super.showMessageERROR("common.save", " Menu ", "common.duplicateName");
			return false;
		}
		
		for(Lang lang : listLang) {
			
			int count = 0;
			for(SysMenuTrl trl : listTranslation) {
				
				if(CommonUtil.isEmpty(trl.getLangCode()) || CommonUtil.isEmpty(trl.getName())) {
					
					super.showMessageERROR("common.save", " Menu ", "menu.trlEmpty");
					return false;
				}
					
				if(trl.getLangCode().equals(lang.getLangCode()))
					count++;
				
				if(count > 1) {
					
					super.showMessageERROR("common.save", " Menu ", "menu.trlLangDup");
					return false;
				}				
			}
		}		
		
		return true;
	} 
	
	/************ EVENT - BEGIN ********************/
	
	public void onNodeExpand(NodeExpandEvent nodeExpandEvent) {

	}
	
	public void onNodeCollapse(NodeCollapseEvent nodeCollapseEvent) {
        
		TreeNode node = nodeCollapseEvent.getTreeNode();
		node.setExpanded(false);
    }

	public void onNodeSelect(NodeSelectEvent nodeSlectedEvent) {
		
		TreeNode treeNode = nodeSlectedEvent.getTreeNode();
		if(treeNode == dumpNode)
			isEditing = false;
		else
			isEditing = true;
		
		sysMenu = (SysMenu) treeNode.getData();
		parentId = sysMenu.getParentId() == null? 0 : sysMenu.getParentId().longValue(); 
		searchChildrenMenu();
		searchTranslation();
		loadParentSelectbox();
	}
	
	public void onNodeSelectContext(NodeSelectEvent event) {
		nodeSelectEvent = event;
	}
	
	public void moveUpTreeNode() {

		if(selectedNode != null) {
			
			List<TreeNode> lstChildOfParent = selectedNode.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(selectedNode);
			if (idx > 0) {
				
				SysMenu menu = (SysMenu) selectedNode.getData();
				if(menuDao.moveUpDown(true, menu)) {
					
					lstChildOfParent.remove(idx);
					lstChildOfParent.add(idx - 1, selectedNode);	
				}				
			}
		}
	}

	public void moveDownTreeNode() {

		if(selectedNode != null) {

			List<TreeNode> lstChildOfParent = selectedNode.getParent().getChildren();
			int idx = lstChildOfParent.indexOf(selectedNode);
			if (idx < lstChildOfParent.size() - 1) {
				
				SysMenu menu = (SysMenu) selectedNode.getData();
				if(menuDao.moveUpDown(false, menu)) {
				
					lstChildOfParent.remove(idx);
					lstChildOfParent.add(idx + 1, selectedNode);
				}				
			}
		}
	}
	
	public void onRowEdit(SysMenu menu) {
		
//		this.sysMenu = menu;
//		isEditing = true;
	}
	
	public void onRowDelete(SysMenu menu) {
		
		if(this.sysMenu.getId() == menu.getId()) {
			btnCancel();
		}
		menuDao.delete(menu);
		listSysMenu.remove(menu);
		TreeNode node = mapTreeNode.remove(menu.getId());
		if (node != null) {
			node.getParent().getChildren().remove(node);
			node.setParent(null);
		}
		
		this.showMessageINFO("common.delete", " Menu ");
	}
	
	/************ EVENT - END ********************/
	
	/********* MENU DLG - BEGIN ************/
	private SysMenu sysMenuDlg;
	private Long parentIdDlg;
	private List<SelectItem> listSelectItemParentDlg;

	private void loadParentSelectboxDlg() {
		
		if(listSelectItemParentDlg == null)
			listSelectItemParentDlg = new ArrayList<>();
		else
			listSelectItemParentDlg.clear();
		
		listSelectItemParentDlg.add(new SelectItem(0, ""));
		List<SysMenu> listMenuParent = menuDao.findNotEqualForSelectbox(sysMenuDlg.getId());		
		for (SysMenu menu : listMenuParent) {
			
//			int level = StringUtils.countMatches(menu.getPath(), "/");
//			String name = StringUtils.repeat("---", level - 1) + " " + menu.getName();
			listSelectItemParentDlg.add(new SelectItem(menu.getId(), menu.getName()));
		}
	}

	public void btnShowDlg(SysMenu menu) {

		if (menu == null) {
			// New child cat

			sysMenuDlg = new SysMenu();
			sysMenuDlg.setIsActive(true);
			parentIdDlg = sysMenu.getId();
		} else {
			// Edit child cat

			sysMenuDlg = menu;
			parentIdDlg = menu.getParentId();
		}
		
		loadParentSelectboxDlg();
	}

	public void btnSaveDlg() {

		if (this.doSave(sysMenuDlg, parentIdDlg)) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgMenu').hide();");
		}
	}
	
	public void btnSaveAndNewDlg() {

		if (this.doSave(sysMenuDlg, parentIdDlg)) {
			
			btnShowDlg(null);
		}
	}

//	private void searchCatParentDlg(Long catType, long catId) {
//
//		listCatParentDlg = catDao.findByTypeAndNotEqual(catType, catId);
//		listCatParentDlg.add(0, catParentDump);
//	}

	/********* MENU DLG - END ************/
	
	/** GET SET**/
	public SysMenu getSysMenu() {
		return sysMenu;
	}

	public void setSysMenu(SysMenu sysMenu) {
		this.sysMenu = sysMenu;
	}
				
	public List<SysMenu> getListSysMenu() {
		return listSysMenu;
	}

	public void setListSysMenu(List<SysMenu> listSysMenu) {
		this.listSysMenu = listSysMenu;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public List<SelectItem> getListSelectItemParent() {
		return listSelectItemParent;
	}

	public void setListSelectItemParent(List<SelectItem> listSelectItemParent) {
		this.listSelectItemParent = listSelectItemParent;
	}

	public SysMenu getSysMenuDlg() {
		return sysMenuDlg;
	}

	public void setSysMenuDlg(SysMenu sysMenuDlg) {
		this.sysMenuDlg = sysMenuDlg;
	}

	public Long getParentIdDlg() {
		return parentIdDlg;
	}

	public void setParentIdDlg(Long parentIdDlg) {
		this.parentIdDlg = parentIdDlg;
	}

	public List<SelectItem> getListSelectItemParentDlg() {
		return listSelectItemParentDlg;
	}

	public void setListSelectItemParentDlg(List<SelectItem> listSelectItemParentDlg) {
		this.listSelectItemParentDlg = listSelectItemParentDlg;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public NodeSelectEvent getNodeSelectEvent() {
		return nodeSelectEvent;
	}

	public void setNodeSelectEvent(NodeSelectEvent nodeSelectEvent) {
		this.nodeSelectEvent = nodeSelectEvent;
	}

	public List<SysMenuTrl> getListTranslation() {
		return listTranslation;
	}

	public void setListTranslation(List<SysMenuTrl> listTranslation) {
		this.listTranslation = listTranslation;
	}

	public List<Lang> getListLang() {
		return listLang;
	}

	public void setListLang(List<Lang> listLang) {
		this.listLang = listLang;
	}
	
}

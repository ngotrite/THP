package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.TreeType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;

//@Component
@ManagedBean(name="categoryBean")
@ViewScoped
public class CategoryBean extends BaseController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1782504055252613230L;
	private String treeType;
	private Category category;
	private List<Category> listCategory;
	private List<Category> listParent;
	private List<Category> listParentById;
	private Long parentId;
	private Category parentDump;
	private boolean isEditing;
	
	private CategoryDAO catDao;
	
	public CategoryBean() {
		catDao = new CategoryDAO();
		parentDump = new Category();
		parentDump.setCategoryName("");
		isEditing = false;
		init();
		searchCategory();
	}
	
	private void init() {
		
		category = new Category();
		parentId = 0L;
		treeType = TreeType.OFFER_OFFER;
		searchParent();
	}
		
	public void searchCategory() {
		
//		listCategory = catDao.findAll();
	}
	
	public void searchParent() {
		
		String[] cols = {"categoryId"};
		Operator[] operators = {Operator.NOTEQ};
		Object[] values = {category.getCategoryId()};
		
		listParent = catDao.findByConditions(cols, operators, values, "");
		listParent.add(0, parentDump);
	}
	
	public String getParentName(int parentId) {
		
		String retVal = "";
		for(Category cat : listCategory) {
			if(cat.getCategoryId() == parentId) {
				retVal = cat.getCategoryName();
				break;
			}
		}
		
		return retVal;
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
	
		category.setTreeType(treeType);
		category.setCategoryParentId(parentId);
		
		if(category.getCategoryId() > 0) {
		
			catDao.update(category);
		} else {
			
			catDao.save(category);
			listCategory.add(category);
		}
		btnCancel();
	}	
	
	public void onRowSelect(SelectEvent event) {
//		category = (Category) event.getObject();
//		searchParent();
//		parentId = category.getCategoryParentId() == null? 0 : category.getCategoryParentId();
	}
	
	public void onRowEdit(Category cat) {
		
		this.category = cat;
		searchParent();
		parentId = category.getCategoryParentId() == null? 0 : category.getCategoryParentId();
		isEditing = true;
	}
	
	public void onRowDelete(Category cat) {
		
		if(this.category.getCategoryId() == cat.getCategoryId()) {
			btnCancel();
		}
		catDao.delete(cat);
		listCategory.remove(cat);
	}
	
	/** GET SET**/
	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public Category getParentDump() {
		return parentDump;
	}

	public void setParentDump(Category parentDump) {
		this.parentDump = parentDump;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	public List<Category> getListCategory() {
		return listCategory;
	}
	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
	}
		
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<Category> getListParent() {
		return listParent;
	}

	public void setListParent(List<Category> listParent) {
		this.listParent = listParent;
	}

	public Map<Long, String> getListType() {
//		return CategoryType.listType;
		return CategoryType.getCatTypesByTreeType(treeType);
	}
	
	public String getTypeLabel(Integer value) {
		return CategoryType.getCatTypesByTreeType(treeType).get(value);
	}

	
	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}
}

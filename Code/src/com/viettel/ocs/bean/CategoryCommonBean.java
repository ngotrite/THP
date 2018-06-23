package com.viettel.ocs.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.Category;

@ManagedBean(name = "categoryCommonBean", eager = true)
@ViewScoped
public class CategoryCommonBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 * @since 27082016
	 */

	// listParent : danh sach category cha - du lieu combobox
	// listCategory danh sach category con - du lieu table

	private static final long serialVersionUID = 1L;
	private Category category;
	private String treeType;
	private Long parentId;
	private List<Category> listParent;
	private List<Category> listCategory;
	private boolean isEditing;
	private String categoryTitle;
	private CategoryDAO catDao;
	private String formType;

	public CategoryCommonBean() {
		listParent = new ArrayList<Category>();
		listCategory = new ArrayList<Category>();
		catDao = new CategoryDAO();
	}

	@PostConstruct
	public void init() {
		parentId = 0L;
	}

	// ***** EVENT ADD NEW CATEGORY *****
	public Map<Long, String> getListTreeType() {
		if (treeType != null)
			return CategoryType.getCatTypesByTreeType(treeType);
		else
			return null;
	}

	// lay danh sach category con thuoc category cha
	public void searchCategory(Long categoryId) {

		listCategory = catDao.loadListCategoryByParentId(categoryId);
	}

	// lay sanh sach catefory khong phai la chinh no de lam cha
	public void searchParent() {
		String[] cols = { "categoryId" };
		Operator[] operators = { Operator.NOTEQ };
		Object[] values = { category.getCategoryId() };
		listParent = catDao.findByConditions(cols, operators, values,
				"categoryName");
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
	}

	public void editCategory(Category cat) {
		category = cat;
		setParentId(category.getCategoryParentId());
	}

	public void deleteCategory(Category category) {
		catDao.delete(category);
		searchCategory(category.getCategoryParentId());
		this.showMessageINFO("common.delete", " Category ");
	}

	// ******* END ************

	// ***** EVENT LIST SUBCATEGORY *****

	// type: 1- su kien click node cha ReserveInfo ;
	public List<Category> loadListCategory(Long categoryType, int type) {
		if (listCategory.size() <= 0 || type == 1)
			listCategory = catDao.loadListCategoryByType(categoryType);
		return listCategory;
	}

	public void deleteCategoryList(Category category) {
		catDao.delete(category);
		listCategory.remove(category);
	}

	public void editCategoryList(Category cat) {
		category = cat;
		setFormType("category");
		setDataByCategory(category);
	}

	private void setDataByCategory(Category cat) {
		setParentId(cat.getCategoryParentId());
		setCategory(cat);
		searchCategory(cat.getCategoryId());
		setCategoryTitle("Sub-Categories of " + cat.getCategoryName());
	}

	// ******* END ************

	// ******** GET SET **************
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

	public List<Category> getListCategory() {
		return listCategory;
	}

	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
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

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}
	// ****** END GET SET *************

}

package com.viettel.ocs.bean.offer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.entity.Category;

@ManagedBean
@ViewScoped
public class OfferCategoryCommonBean extends BaseController implements Serializable {

	private Category category;
	private List<Category> categories;
	private CategoryDAO categoryDAO;

	@PostConstruct
	public void init() {
		this.category = new Category();
		this.categories = new ArrayList<Category>();
		this.categoryDAO = new CategoryDAO();
	}

	// BUSINESS ACTION
	public void refresh(Category category) {
		this.category = category;
		this.categories = categoryDAO.findByTypeAndcategoryParentId(category.getCategoryType(),
				category.getCategoryId());
	}

	public void onRowEdit(Category category) {
		this.category = category;
	}

	public void onRowDelete(Category category) {
		this.categoryDAO.delete(category);
		this.categories.remove(category);
	}

	public void btnNew() {
		this.category = new Category();
		this.category.setCategoryParentId(0L);
	}

	// Start GET, SET
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	// END GET, SET
}

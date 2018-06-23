package com.viettel.ocs.bean.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.CdrType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.CdrPropDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.CdrProp;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "cdrPropertiesBean")
@ViewScoped
public class CdrPropertiesBean extends BaseController implements Serializable {
    private static final long serialVersionUID = 5740234148631254183L;    
    private int form = 0;
    private static final int FORM_DEFAULT =0;
    private static final int INSERT =1;
    private static final int EDIT =2;
    
    private Category category;
    private List<CdrProp> listCdrProp;    
    private CdrProp cdrProp;
    private CdrProp cdrPropSelected;
    private List<SelectItem> listDataType;

    private Long categoryId;
    private Long propId;
//    private Boolean isEdit;
    private CategoryDAO categoryDAO;
    private CdrPropDAO cdrPropDAO;

    @PostConstruct
    public void init() {
        initDAO();        
        clearData();
        initListObject();
    }
    
    public void initDAO(){
        categoryDAO = new CategoryDAO();
        cdrPropDAO = new CdrPropDAO();
        category = new Category();
    }

    public void initListObject() {        
        this.listDataType = new ArrayList<>();
        listDataType.add(new SelectItem(CdrType.STRING_TYPE, CdrType.STRING_TYPE));
        listDataType.add(new SelectItem(CdrType.LONG_TYPE, CdrType.LONG_TYPE));
        listDataType.add(new SelectItem(CdrType.INTEGER_TYPE, CdrType.INTEGER_TYPE));
        listDataType.add(new SelectItem(CdrType.DATE_TYPE, CdrType.DATE_TYPE));
    }

    public void clearData() {        
        this.cdrProp = new CdrProp();
        this.cdrPropSelected = new CdrProp();
        cdrProp.setDataType(CdrType.STRING_TYPE);
        listCdrProp = cdrPropDAO.findAll("cdrPropId");
        form = FORM_DEFAULT;
    }

    public void removeProperty(CdrProp item) {
//        CdrProp item = cloneObject(cdrPropSelected);
        if (cdrPropDAO.deleteCdrProp(item)) {            
            listCdrProp = cdrPropDAO.findAll("");
            clearData();            
            this.showMessageINFO("common.delete", " CDR Property");
            RequestContext.getCurrentInstance().execute("PF('cdrPropertiesTable').clearFilters();");

        } else {
            this.showMessageERROR("cdrService.deleteError", this.readProperties("cdrService.recordInUsed"));
        }
    }

    public CdrProp cloneObject(CdrProp cdrProp){
        CdrProp clone = new CdrProp();
        clone.setCdrPropId(cdrProp.getCdrPropId());
        clone.setPropName(cdrProp.getPropName());
        clone.setPropId(cdrProp.getPropId());
        clone.setSource(cdrProp.getSource());
        clone.setDataType(cdrProp.getDataType());
        clone.setParam(cdrProp.getParam());
        clone.setDefaultValue(cdrProp.getDefaultValue());
        clone.setDescription(cdrProp.getDescription());
        return clone;
    }
    public void editProperty(CdrProp item) {        
        this.cdrProp = cloneObject(item);
    }

    public void addNewProperty() {        
        clearData();
    }

    public void submitProperty() {
        if (validateProp()) {
            cdrPropDAO.saveOrUpdate(this.cdrProp);            
            this.showMessageINFO("common.saveOrUpdate", " CDR Property Success ");                        
            clearData();            
            RequestContext.getCurrentInstance().execute("PF('cdrPropertiesTable').clearFilters();");            
        }
    }

    public void onRowSelectCdrTemplate(){
        if (cdrPropSelected == null) {
            cdrProp = new CdrProp();
        } else {
            cdrProp = cloneObject(cdrPropSelected);            
        }
        form = FORM_DEFAULT;
    }
    public Boolean validateProp() {
        boolean result = true;        
        if (cdrPropDAO.checkFieldIsExist("propName", cdrProp.getPropName(), cdrProp)) {
            this.showMessageERROR("cdrProperties.propNameIsExist", "");
            result = false;
        }
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="GET SET">
    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public int getINSERT() {
        return INSERT;
    }

    public int getEDIT() {
        return EDIT;
    }

    public int getFORM_DEFAULT() {
        return FORM_DEFAULT;
    }
    
    
    
    public CdrProp getCdrPropSelected() {
        return cdrPropSelected;
    }
    
    public void setCdrPropSelected(CdrProp cdrPropSelected) {
        this.cdrPropSelected = cdrPropSelected;
    }
    
    
    public CdrProp getCdrProp() {
        return cdrProp;
    }
    
    public void setCdrProp(CdrProp cdrProp) {
        this.cdrProp = cdrProp;
    }
    
    public List<CdrProp> getListCdrProp() {
        
        return listCdrProp;
    }
    
    public void setListCdrProp(List<CdrProp> listCdrProp) {
        this.listCdrProp = listCdrProp;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public CategoryDAO getCategoryDAO() {
        return categoryDAO;
    }
    
    public void setCategoryDAO(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }
    
    public CdrPropDAO getCdrPropDAO() {
        return cdrPropDAO;
    }
    
    public void setCdrPropDAO(CdrPropDAO cdrPropDAO) {
        this.cdrPropDAO = cdrPropDAO;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<SelectItem> getListDataType() {
        return listDataType;
    }
    
    public void setListDataType(List<SelectItem> listDataType) {
        this.listDataType = listDataType;
    }
    
    public Long getPropId() {
        return propId;
    }
    
    public void setPropId(Long propId) {
        this.propId = propId;
    }    
//</editor-fold>
}

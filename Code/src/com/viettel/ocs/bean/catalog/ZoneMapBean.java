package com.viettel.ocs.bean.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.NormParamDAO;
import com.viettel.ocs.dao.ZoneDAO;
import com.viettel.ocs.dao.ZoneDataDAO;
import com.viettel.ocs.dao.ZoneMapDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.Zone;
import com.viettel.ocs.entity.ZoneData;
import com.viettel.ocs.entity.ZoneMap;
import com.viettel.ocs.util.CommonUtil;
import com.viettel.ocs.util.DatetimeUtil;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "zoneMapBean")
@ViewScoped
public class ZoneMapBean extends BaseController implements Serializable {
	private static final long serialVersionUID = 1L;
	private String formType = "";
	private ZoneMap zoneMap;
	private List<ZoneMap> listZoneMapByCategory;
	private ZoneMapDAO zoneMapDAO;

	// Category
	private Category category;
	private List<SelectItem> listComboCategory;
	private CategoryDAO categoryDAO;

	// Zone
	private Zone zone;
	private List<Zone> listZoneByZoneMap;
	private ZoneDAO zoneDAO;
	private boolean isEditting;
	private List<ZoneMap> listComboZoneMap;
	private boolean isApply;

	// Zone Data
	private ZoneData zoneData;
	private List<ZoneData> listZoneDataByZone;
	private ZoneDataDAO zoneDataDAO;
	private List<ZoneData> listZDSelection;

	private UploadedFile file;
	private UploadedFile fileZDT;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		this.category = new Category();
		this.zoneMap = new ZoneMap();
		this.zoneMapDAO = new ZoneMapDAO();
		this.categoryDAO = new CategoryDAO();
		this.zoneDAO = new ZoneDAO();
		this.zone = new Zone();
		this.listZoneMapByCategory = new ArrayList<ZoneMap>();
		this.listZoneByZoneMap = new ArrayList<Zone>();
		this.zoneDataDAO = new ZoneDataDAO();
		this.listComboZoneMap = new ArrayList<ZoneMap>();
		this.listZoneDataByZone = new ArrayList<ZoneData>();
		this.zoneData = new ZoneData();
		this.listZDSelection = new ArrayList<ZoneData>();
	}

	public void refreshCategories(Category category) {
		formType = "list-zonemap-by-category";
		this.category = category;
		loadZoneMapByCategory(category.getCategoryId());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("$('.zoneMapClearFilter').click();");
	}

	public void refreshZone(Zone zone) {
		this.formType = "zone-detail";
		try {
			this.zone = zone.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		super.resetDataTable("form-zone:DTZoneData");
		loadZoneDTByZone(zone.getZoneId());
		loadComboListZoneMap();
	}

	public void refreshZoneMap(ZoneMap zoneMap) {
		formType = ("zonemap-detail");
		try {
			this.zoneMap = zoneMap.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		loadZoneByZoneMap(zoneMap.getZoneMapId());
		// RequestContext requestContext = RequestContext.getCurrentInstance();
		// requestContext.execute("$('.zoneClearFilter').click();");
		LoadCategoriesOfZoneMap();
		this.isApply = true;
		super.resetDataTable("form-zonemap-detail:DTZone");
	}
	
	public void changeVisible() {
		this.isEditting = false;
	}

	public void commandAddNewZoneMap() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("zonemap.title"));
		ZoneMap zoneMap = new ZoneMap();
		zoneMap.setCategoryId(category.getCategoryId());
		refreshZoneMap(zoneMap);
		this.isApply = false;
	}

	public void commandEditZoneMap(ZoneMap zoneMap) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("zonemap.title"));
		refreshZoneMap(zoneMap);
		this.isApply = false;

	}

	public void commandCloneZoneMap(ZoneMap zoneMap) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		refreshZoneMap(zoneMap);
		zoneMap.setZoneMapId(0L);
	}

	public void commandDeleteZoneMap(ZoneMap item) {
		this.isEditting = true;
		NormParamDAO normParamDAO = new NormParamDAO();
		if (!zoneDAO.checkZoneByZM(item.getZoneMapId()) && !normParamDAO.checkZoneMapInNP(item.getZoneMapId())) {
			try {
				zoneMapDAO.delete(item);
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				Category cat = categoryDAO.get(item.getCategoryId());
				treeCommonBean.removeTreeNodeAll(item);
				refreshCategories(cat);
				this.showMessageINFO("common.delete", " Zone Map ");
			} catch (Exception e) {
				throw e;
			}
		} else {
			this.showMessageWARN("common.delete", super.readProperties("validate.fieldUseIn"));
		}
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		ZoneMap zoneMap = (ZoneMap) event.getTreeNode().getData();
		Object object = zoneMapDAO.upDownObjectInCatWithDomain(zoneMap, "index", isUp);
		if (object instanceof ZoneMap) {
			Category category = categoryDAO.get(zoneMap.getCategoryId());
			ZoneMap nextZoneMap = (ZoneMap) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(zoneMap);
			} else {
				treeCommonBean.moveDownTreeNode(zoneMap);
			}
			treeCommonBean.updateTreeNode(nextZoneMap, category, null);
			if (formType == "list-zonemap-by-category"
					&& nextZoneMap.getCategoryId() == this.category.getCategoryId()) {
				refreshCategories(category);
			}
			super.showNotificationSuccsess();
		} else {
//			super.showNotificationFail();
		}
	}

	// Edit ContextMenu
	public void editContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			ZoneMap item = (ZoneMap) nodeSelectEvent.getTreeNode().getData();
			commandEditZoneMap(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			ZoneMap item = (ZoneMap) nodeSelectEvent.getTreeNode().getData();
			commandDeleteZoneMap(item);
			loadZoneMapByCategory(item.getCategoryId());
		}
	}

	public void saveZoneMap() {
		if (checkValidListZone()) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(zoneMap.getCategoryId());
			if (validateZoneMap()) {

				// if (zoneMap.getZoneMapId() == 0L) {
				// zoneMap.setIndex(zoneMapDAO.getMaxIndex() + 1);
				// }

				// if (isEditting) {
				// zoneMapDAO.saveZoneAndZomeMap(zoneMap, listZoneByZoneMap);
				// } else {
				// zoneMapDAO.save(zoneMap);
				// listZoneMapByCategory.add(zoneMap);
				// }
				// treeCommonBean.updateTreeNode(zoneMap, cat,
				// listZoneByZoneMap);
				// this.showMessageINFO("common.save", " Zone Map ");

				if (zoneMapDAO.saveZoneAndZomeMap(zoneMap, listZoneByZoneMap)) {
					treeCommonBean.updateTreeNode(zoneMap, cat, listZoneByZoneMap);
					this.showMessageINFO("common.save", " Zone Map ");
					this.isApply = true;
				}

			}

		}

	}

	// check name isExist
	public Boolean checkValidListZone() {
		boolean result = true;

		if (this.listZoneByZoneMap.size() > 0) {
			List<String> nameDuplicate = new ArrayList<>();
			List<String> codeDuplicate = new ArrayList<>();
			for (int i = 0; i < this.listZoneByZoneMap.size(); i++) {
				for (int j = 0; j < this.listZoneByZoneMap.size(); j++) {
					if (i != j) {

						if (ValidateUtil.checkStringNullOrEmpty(this.listZoneByZoneMap.get(i).getZoneName())) {
							this.showMessageWARN("Zone", super.readProperties("validate.checkValueNameNull"));
							return false;
						}

						if (this.listZoneByZoneMap.get(i).getZoneName()
								.equals(this.listZoneByZoneMap.get(j).getZoneName())) {
							nameDuplicate.add(this.listZoneByZoneMap.get(i).getZoneName());
							result = false;
						}

						if (!ValidateUtil.checkStringNullOrEmpty(this.listZoneByZoneMap.get(i).getZoneCode())) {
							if (this.listZoneByZoneMap.get(i).getZoneCode()
									.equals(this.listZoneByZoneMap.get(j).getZoneCode())) {
								codeDuplicate.add(this.listZoneByZoneMap.get(i).getZoneCode());
								result = false;
							}
						}
					}
				}
			}

			if (nameDuplicate.size() > 0) {
				String listNameDuplicate = "";
				for (int i = 0; i < nameDuplicate.size(); i++) {
					if (listNameDuplicate.isEmpty()) {
						listNameDuplicate += nameDuplicate.get(i);
					} else {
						if (listNameDuplicate.indexOf(nameDuplicate.get(i)) == -1)
							listNameDuplicate += "," + nameDuplicate.get(i);
					}
				}

				this.showMessageWARN("Zone Name",
						super.readProperties("validate.checkValueNameExist") + " " + listNameDuplicate);

			}

			if (codeDuplicate.size() > 0) {
				String listCodeDuplicate = "";
				for (int i = 0; i < codeDuplicate.size(); i++) {
					if (listCodeDuplicate.isEmpty()) {
						listCodeDuplicate += codeDuplicate.get(i);
					} else {
						if (listCodeDuplicate.indexOf(codeDuplicate.get(i)) == -1)
							listCodeDuplicate += "," + codeDuplicate.get(i);
					}
				}

				this.showMessageWARN("Zone Code",
						super.readProperties("validate.checkValueNameExist") + " " + listCodeDuplicate);
			}
		}

		return result;
	}

	// Validate
	private boolean validateZoneMap() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(zoneMap.getZoneMapName())) {
			this.showMessageWARN("Zone Map", super.readProperties("validate.checkValueNameNull"));
			result = false;

		}
		if (zoneMapDAO.checkName(zoneMap, zoneMap.getZoneMapName())) {
			this.showMessageWARN("Zone Map", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}

		return result;
	}

	public void cancelZoneMap() {
		refreshCategories(category);
		Category category = new Category();

	}

	// load list list Zone Map by Category
	public List<ZoneMap> loadZoneMapByCategory(long categoryId) {
		listZoneMapByCategory = new ArrayList<ZoneMap>();
		listZoneMapByCategory = zoneMapDAO.findZMByConditions(categoryId);
		return listZoneMapByCategory;
	}

	public List<SelectItem> loadComboListCategory() {
		listComboCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_ZD_ZONE_DATA);
		if (listComboCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listComboCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listComboCategory;
	}

	private List<Category> categoriesOfZoneMap;

	private void LoadCategoriesOfZoneMap() {
		categoriesOfZoneMap = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_ZD_ZONE_DATA);
	}

	public List<Category> getCategoriesOfZoneMap() {
		return categoriesOfZoneMap;
	}

	// Zone

	public void commandAddNewZone() {
		Zone zone = new Zone();
		if (zoneMap.getZoneMapId() == 0L) {
			this.showMessageWARN("", super.readProperties("validate.zoneAddNew"));
		} else {
			zone.setZoneMapId(zoneMap.getZoneMapId());
			refreshZone(zone);
		}
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.hideAllCategoryComponent();
		treeCommonBean.setContentTitle(super.readProperties("zone.title"));
		this.isApply = false;

	}

	// load list list Zone by ZoneMap
	public List<Zone> loadZoneByZoneMap(long zoneMapId) {
		listZoneByZoneMap = new ArrayList<Zone>();
		listZoneByZoneMap = zoneDAO.findZoneByConditions(zoneMapId);
		return listZoneByZoneMap;
	}

	// Load list combo ZoneMap========================
	private void loadComboListZoneMap() {
		ZoneMapDAO zoneMapDAO = new ZoneMapDAO();
		listComboZoneMap = zoneMapDAO.findAll("");
	}

	public void saveZone() {
		if (checkValidListZD()) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			zoneMap = zoneMapDAO.get(zone.getZoneMapId());
			Category cat = categoryDAO.get(zoneMap.getCategoryId());
			if (validateZone()) {
				zoneDAO.saveZoneDataAndZone(zone, listZoneDataByZone);
				loadZoneByZoneMap(zoneMap.getZoneMapId());
				treeCommonBean.updateTreeNode(zoneMap, cat, listZoneByZoneMap);
				refreshZone(zone);
				treeCommonBean.updateTreeNode(zone, null, null);
				this.showMessageINFO("common.save", " Zone ");
			}
			this.isApply = true;
		}
	}

	// Validate
	private boolean validateZone() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(zone.getZoneName())) {
			this.showMessageWARN("Zone", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}
		if (zoneDAO.checkName(zone, zone.getZoneName(), zoneMap.getZoneMapId())) {
			this.showMessageWARN("Zone", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}

		return result;
	}

	public void commandDeleteZone(Zone item) {

		this.isEditting = true;

		ZoneDataDAO zoneDataDAO = new ZoneDataDAO();
		NormParamDAO normParamDAO = new NormParamDAO();
		if (!zoneDataDAO.checkZDByZone(item.getZoneId()) && !normParamDAO.checkZoneInNP(item.getZoneId())) {
			zoneDAO.delete(item);
			listZoneByZoneMap.remove(item);
			refreshZoneMap(zoneMap);
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			ZoneMap zoneMap = zoneMapDAO.get(item.getZoneMapId());
			treeCommonBean.removeTreeNode(item, zoneMap);
			this.isApply = false;
			this.showMessageINFO("Zone", super.readProperties("validate.deleteSuccess"));
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}

	}

	public void commandEditZone(Zone zone) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("zone.title"));
		refreshZone(zone);
		this.isApply = false;
	}

	public void cancelZone() {
		ZoneMap zoneMap = new ZoneMap();
		zoneMap.setZoneMapId(zone.getZoneMapId());
		refreshCategories(category);
	}

	public void commandDeleteZoneData(ZoneData item) {
		listZoneDataByZone.remove(item);
		zoneDataDAO.delete(item);
		loadZoneDTByZone(zone.getZoneId());
		RequestContext context = RequestContext.getCurrentInstance();
		this.showMessageINFO("common.delete", " Zone Data ");
	}

	// **** Zone Data ****//
	// Show Dialog Zone Data

	public void showDialogZD(ZoneData zoneData) {
		if (zoneData == null) {
			zoneData = new ZoneData();
			zoneData.setUpdateDate(new Date());
		}
		this.zoneData = zoneData;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgZoneDataWR').show();");

	}

	public void cmdApplyZoneDT() {
		if (validateZoneData()) {
			zoneData.setUpdateDate(new Date());
			zoneData.setZoneId(zone.getZoneId());
			zoneDataDAO.saveOrUpdate(zoneData);
			boolean checkNew = true;
			for (int i = 0; i < listZoneDataByZone.size(); i++) {
				if (zoneData.getZoneDataId() == (listZoneDataByZone.get(i).getZoneDataId())) {
					listZoneDataByZone.set(i, zoneData);
					checkNew = false;
					break;
				}
			}
			if (checkNew) {
				listZoneDataByZone.add(zoneData);
			}
			this.showMessageINFO("common.save", " Zone Data");
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgZoneDataWR').hide();");
		}

	}

	// Validate
	private boolean validateZoneData() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(zoneData.getZoneDataValue())) {
			this.showMessageWARN("Zone Data Value", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}
		if (zoneDataDAO.checkValue(zoneData, zoneData.getZoneDataValue())) {
			this.showMessageWARN("Zone Data Value", super.readProperties("validate.checkValueZDExist"));
			result = true;
		}
		return result;
	}

	public List<ZoneData> loadZoneDTByZone(long zoneId) {
		listZoneDataByZone = new ArrayList<ZoneData>();
		listZoneDataByZone = zoneDataDAO.findZoneDataByConditions(zoneId);
		return listZoneDataByZone;
	}

	public void selectEvent(AjaxBehaviorEvent event) {

	}

	public boolean activeButton() {
		if (listZDSelection != null && listZDSelection.size() > 0 && isApply == false) {
			return true;
		}
		return false;
	}

	public void commandRemoveZD() {
		if (zoneDataDAO.delListZD(listZDSelection)) {
			listZDSelection.clear();
			activeButton();
			loadZoneDTByZone(zone.getZoneId());
			this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
		}
	}

	// Export Zone

	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		Row row = sheet.getRow(0);
		Cell cell00 = row.createCell(0);
		cell00.setCellValue(this.readProperties("zone.exportTitle"));
		CellStyle styleHeader = wb.createCellStyle();
		Font boldHeader = wb.createFont();
		boldHeader.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldHeader.setFontHeightInPoints((short) 12);
		boldHeader.setFontName("Times New Roman");
		styleHeader.setAlignment(CellStyle.ALIGN_CENTER);
		styleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styleHeader.setFont(boldHeader);
		styleHeader.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cell00.setCellStyle(styleHeader);
		sheet.shiftRows(1, sheet.getLastRowNum(), 1);

		HSSFRow header = sheet.getRow(2);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setWrapText(true);
		Font bold = wb.createFont();
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		bold.setFontHeightInPoints((short) 10);
		bold.setFontName("Times New Roman");
		cellStyle.setFont(bold);
		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
		for (int colNum = 0; colNum < header.getLastCellNum(); colNum++)
			wb.getSheetAt(0).autoSizeColumn(colNum);
	}

	// Upload Zone
	public void uploadExel(FileUploadEvent event) throws IOException {
		file = event.getFile();
		String excelFilePath = file.getFileName();
		if(excelFilePath == null || !excelFilePath.endsWith(".xls")) {
			this.showMessageWARN("Import ", super.readProperties("validate.importFormat"));
		}
		
		readZoneFromExcelFile(file);		
	}

	@SuppressWarnings("deprecation")
	private String getCellValue(Cell cell) {
		String cellValue;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			cellValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			cellValue = cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
				cellValue = dateFormat.format(cell.getDateCellValue());
			} else {
				cellValue = Double.toString((long) cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_BLANK:
			cellValue = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellValue = Boolean.toString(cell.getBooleanCellValue());
			break;
		default:
			cellValue = "";
		}
		return cellValue;
	}

	// Import Zone

	public List<Zone> readZoneFromExcelFile(UploadedFile file) {
		
		Workbook workbook = null;
		try {

			workbook = new HSSFWorkbook(file.getInputstream());
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			List<String> nameDuplicate = new ArrayList<>();
			List<String> codeDuplicate = new ArrayList<>();
			int count = 0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() == 2 || nextRow.getRowNum() == 1 || nextRow.getRowNum() == 0) {
					continue;
				}
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				Zone zone = new Zone();
				boolean hasError = false;
				if (!hasError) {
					while (cellIterator.hasNext()) {
						Cell nextCell = cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();

						switch (columnIndex) {

						case 1:
							if (checkIsExistZone("zoneName", (String) getCellValue(nextCell))) {
								nameDuplicate.add((String) getCellValue(nextCell));
								hasError = true;
							} else {
								zone.setZoneName((String) getCellValue(nextCell));
								hasError = false;
							}
							break;
						case 2:
							if (checkIsExistZone("zoneCode", (String) getCellValue(nextCell))) {
								codeDuplicate.add((String) getCellValue(nextCell));
								hasError = true;
							} else {
								zone.setZoneCode((String) getCellValue(nextCell));
								hasError = false;
							}
							break;
						case 3:
							zone.setRemark((String) getCellValue(nextCell));
							break;
						}

					}
				}

				if (!hasError) {
					listZoneByZoneMap.add(zone);
					count++;
				}
			}

			if (nameDuplicate.size() > 0) {
				String listNameDuplicate = "";
				for (int i = 0; i < nameDuplicate.size(); i++) {
					if (listNameDuplicate.isEmpty()) {
						listNameDuplicate += nameDuplicate.get(i);
					} else {
						listNameDuplicate += "," + nameDuplicate.get(i);
					}
				}
				this.showMessageWARN("Zone Name",
						super.readProperties("your inport file") + " has " + count + " success record and "
								+ nameDuplicate.size() + " duplicate record with name : " + listNameDuplicate);
			}

			if (codeDuplicate.size() > 0) {
				String listCodeDuplicate = "";
				for (int i = 0; i < codeDuplicate.size(); i++) {
					if (listCodeDuplicate.isEmpty()) {
						listCodeDuplicate += codeDuplicate.get(i);
					} else {
						listCodeDuplicate += "," + codeDuplicate.get(i);
					}
				}
				this.showMessageWARN("Zone Code",
						super.readProperties("your inport file") + " has " + count + " success record and "
								+ codeDuplicate.size() + " duplicate record with name : " + listCodeDuplicate);
			}			
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
		} finally {
			try {
				if(workbook != null)
					workbook.close();
			} catch (IOException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
		
		return listZoneByZoneMap;
	}

	public Boolean checkIsExistZone(String column, String value) {
		boolean result = false;

		if (column.equals("zoneName")) {
			if (this.listZoneByZoneMap.size() > 0) {
				for (Zone z : this.listZoneByZoneMap) {
					if (z.getZoneName().equals(value)) {
						result = true;
						break;
					}
				}
			}

			if (!result) {
				if (zoneDAO.checkFieldIsExist(column, value, new Zone())) {
					result = true;
				}
			}
		}

		if (column.equals("zoneCode")) {
			if (this.listZoneByZoneMap.size() > 0) {
				for (Zone z : this.listZoneByZoneMap) {
					if (z.getZoneCode().equals(value)) {
						result = true;
						break;
					}
				}
			}

			if (!result) {
				if (zoneDAO.checkFieldIsExist(column, value, new Zone())) {
					result = true;
				}
			}
		}

		return result;
	}

	// Export Zone Data
	
	public String getExportZoneName() {
		
		return CommonUtil.toUnSign(zone.getZoneName()) + "-" + DatetimeUtil.dateToString(new Date(), "yyyy-dd-mm:HHmmss");
	}

	public void postProcessXLSZDT(Object document) {
		// SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
		Row row = sheet.getRow(0);
		Cell cell00 = row.createCell(0);
		cell00.setCellValue(this.readProperties("zonedata.list"));
		CellStyle styleHeader = wb.createCellStyle();
		Font boldHeader = wb.createFont();
		boldHeader.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldHeader.setFontHeightInPoints((short) 12);
		boldHeader.setFontName("Times New Roman");
		styleHeader.setAlignment(CellStyle.ALIGN_CENTER);
		styleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styleHeader.setFont(boldHeader);
		styleHeader.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cell00.setCellStyle(styleHeader);
		sheet.shiftRows(1, sheet.getLastRowNum(), 1);

		HSSFRow header = sheet.getRow(2);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setWrapText(true);
		Font bold = wb.createFont();
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		bold.setFontHeightInPoints((short) 10);
		bold.setFontName("Times New Roman");
		cellStyle.setFont(bold);
		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
		for (int colNum = 0; colNum < header.getLastCellNum(); colNum++)
			wb.getSheetAt(0).autoSizeColumn(colNum);
	}

	// Upload ZoneData
	public void uploadExelZDT(FileUploadEvent event) throws IOException {
		fileZDT = event.getFile();
		String excelFilePath = fileZDT.getFileName();
		if(excelFilePath == null || !excelFilePath.endsWith(".xls")) {
			this.showMessageWARN("Import ", super.readProperties("validate.importFormat"));
		}

		readZoneFromExcelFileZDT(file);
	}

	// Import ZoneData

	public void readZoneFromExcelFileZDT(UploadedFile file) {
		
		Workbook workbook = null;
		try {
			
			workbook = new HSSFWorkbook(fileZDT.getInputstream());
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			int count = 0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() == 2 || nextRow.getRowNum() == 1 || nextRow.getRowNum() == 0) {
					continue;
				}
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				ZoneData zoneData = new ZoneData();

				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					int columnIndex = nextCell.getColumnIndex();

					switch (columnIndex) {
					case 1:
						zoneData.setZoneDataValue((String) getCellValue(nextCell));
						break;
					}

				}
				listZoneDataByZone.add(zoneData);
				zoneMapDAO.saveZoneAndZomeMap(zoneMap, listZoneByZoneMap);
				count++;
			}

			if (count > 0) {
				this.showMessageWARN("Zone Data", super.readProperties("your inport file") + " has " + count
						+ " success record " + "please click apply finish");
			}
		} catch (Exception e) {
			// TODO: handle exception
			getLogger().warn(e.getMessage(), e);
		} finally {
			try {
				if(workbook != null)
					workbook.close();
			} catch (IOException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
	}

	// check name isExist
	public Boolean checkValidListZD() {

		boolean result = true;

		if (this.listZoneDataByZone.size() > 0) {
			for (int i = 0; i < this.listZoneDataByZone.size(); i++) {
				for (int j = 0; j < this.listZoneDataByZone.size(); j++) {
					if (i != j) {

						if (this.listZoneDataByZone.get(i).getZoneDataValue() == null) {
							this.showMessageWARN("Zone Data Value", super.readProperties("validate.fieldNull"));
							return false;

						}
					}
				}

			}
		}
		return result;
	}

	private StreamedContent fileTemEX;

	public void fileDownloadView() {
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream("/resources/themes/temexcel/zoneimporttem.xls");
		fileTemEX = new DefaultStreamedContent(stream, "office/xls", "zoneimporttem.xls");
	}

	public StreamedContent getFileTemEX() {
		return fileTemEX;
	}

	/** GET SET **/

	public ZoneMap getZoneMap() {
		return zoneMap;
	}

	public void setZoneMap(ZoneMap zoneMap) {
		this.zoneMap = zoneMap;
	}

	public List<ZoneMap> getListZoneMapByCategory() {
		return listZoneMapByCategory;
	}

	public void setListZoneMapByCategory(List<ZoneMap> listZoneMapByCategory) {
		this.listZoneMapByCategory = listZoneMapByCategory;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public boolean isEditting() {
		return isEditting;
	}

	public void setEditting(boolean isEditting) {
		this.isEditting = isEditting;
	}

	public List<SelectItem> getListComboCategory() {
		return listComboCategory;
	}

	public void setListComboCategory(List<SelectItem> listComboCategory) {
		this.listComboCategory = listComboCategory;
	}

	public List<Zone> getListZoneByZoneMap() {
		return listZoneByZoneMap;
	}

	public void setListZoneByZoneMap(List<Zone> listZoneByZoneMap) {
		this.listZoneByZoneMap = listZoneByZoneMap;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public ZoneMapDAO getZoneMapDAO() {
		return zoneMapDAO;
	}

	public void setZoneMapDAO(ZoneMapDAO zoneMapDAO) {
		this.zoneMapDAO = zoneMapDAO;
	}

	public ZoneDAO getZoneDAO() {
		return zoneDAO;
	}

	public void setZoneDAO(ZoneDAO zoneDAO) {
		this.zoneDAO = zoneDAO;
	}

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public ZoneData getZoneData() {
		return zoneData;
	}

	public void setZoneData(ZoneData zoneData) {
		this.zoneData = zoneData;
	}

	public ZoneDataDAO getZoneDataDAO() {
		return zoneDataDAO;
	}

	public void setZoneDataDAO(ZoneDataDAO zoneDataDAO) {
		this.zoneDataDAO = zoneDataDAO;
	}

	public List<ZoneData> getListZoneDataByZone() {
		return listZoneDataByZone;
	}

	public void setListZoneDataByZone(List<ZoneData> listZoneDataByZone) {
		this.listZoneDataByZone = listZoneDataByZone;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public UploadedFile getFileZDT() {
		return fileZDT;
	}

	public void setFileZDT(UploadedFile fileZDT) {
		this.fileZDT = fileZDT;
	}

	public List<ZoneMap> getListComboZoneMap() {
		return listComboZoneMap;
	}

	public void setListComboZoneMap(List<ZoneMap> listComboZoneMap) {
		this.listComboZoneMap = listComboZoneMap;
	}

	public List<ZoneData> getListZDSelection() {
		return listZDSelection;
	}

	public void setListZDSelection(List<ZoneData> listZDSelection) {
		this.listZDSelection = listZDSelection;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

}

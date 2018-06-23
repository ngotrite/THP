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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeCommonBean;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.GeoHomeZoneDAO;
import com.viettel.ocs.dao.GeoNetZoneDAO;
import com.viettel.ocs.dao.NormParamDAO;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.GeoNetZone;
import com.viettel.ocs.util.ValidateUtil;

@SuppressWarnings("serial")
@ManagedBean(name = "geoBean")
@ViewScoped
public class GeoHomeBean extends BaseController implements Serializable {
	private String formType = "";
	private GeoHomeZone geoHome;
	private List<GeoHomeZone> listGeoHomeByCategory;
	private GeoHomeZoneDAO geoHomeDAO;
	private List<SelectItem> listComboCategory;
	private List<SelectItem> listGeoHomeZoneType;

	private GeoNetZone geoNet;;
	private List<GeoNetZone> listGeoNetByGeoHome;
	private GeoNetZoneDAO geoNetDAO;

	private List<GeoNetZone> listGeoNetSelection;

	private Category category;
	private boolean isEditting;
	private boolean isApply;
	private CategoryDAO categoryDAO;
	private UploadedFile file;

	// Init -----------------------------------------------------
	@PostConstruct
	public void init() {
		this.isApply = true;
		this.isEditting = true;
		this.category = new Category();
		this.geoHome = new GeoHomeZone();
		this.geoHomeDAO = new GeoHomeZoneDAO();
		this.categoryDAO = new CategoryDAO();
		this.listGeoHomeByCategory = new ArrayList<GeoHomeZone>();

		this.geoNet = new GeoNetZone();
		this.geoNetDAO = new GeoNetZoneDAO();
		this.listGeoNetByGeoHome = new ArrayList<GeoNetZone>();
		this.listGeoNetSelection = new ArrayList<GeoNetZone>();
	}

	public void refreshGeoHome(GeoHomeZone geoHome) {
		try {
			this.geoHome = geoHome.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			getLogger().warn(e.getMessage(), e);
		}
		this.formType = "geohome-detail";
		// this.listGeoNetByGeoHome = geoNetDAO.findByGeoHomeZoneId(geoHome);
		loadGeoNetByGeoHome(geoHome.getGeoHomeZoneId());
		// RequestContext requestContext = RequestContext.getCurrentInstance();
		// requestContext.execute("$('.geoNetClearFilter').click();");
		LoadCategoriesOfGeoHome();
		this.isApply = true;
		super.resetDataTable("form-geohome:DTGeoNetTable");
	}

	public void refreshCategories(Category category) {
		formType = "list-geohome-by-category";
		this.category = category;
		// this.listGeoHomeByCategory =
		// geoHomeDAO.findGHZByConditions(category.getCategoryId());
		loadGeoHomeByCategory(category.getCategoryId());
		super.resetDataTable("form-geohome-by-cat:DTGeoHome");
	}

	public void refressGeoNet(GeoNetZone geoNet) {
		TreeCommonBean treeCommonBean = super.getTreeCommonBean();
		treeCommonBean.setContentTitle(super.readProperties("geoNet.title"));
		this.formType = "geonet-detail";
		this.geoNet = geoNet;
	}

	public void changeVisible() {
		this.isEditting = false;
	}

	// Context Menu
	public void moveUpDownInCat(NodeSelectEvent event, boolean isUp) {
		GeoHomeZone geoHome = (GeoHomeZone) event.getTreeNode().getData();
		Object object = geoHomeDAO.upDownObjectInCatWithDomain(geoHome, "index", isUp);
		if (object instanceof GeoHomeZone) {
			Category category = categoryDAO.get(geoHome.getCategoryId());
			GeoHomeZone nextGeoHome = (GeoHomeZone) object;

			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			if (isUp) {
				treeCommonBean.moveUpTreeNode(geoHome);
			} else {
				treeCommonBean.moveDownTreeNode(geoHome);
			}
			treeCommonBean.updateTreeNode(nextGeoHome, category, null);
			if (formType == "list-geohome-by-category"
					&& nextGeoHome.getCategoryId() == this.category.getCategoryId()) {
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
			GeoHomeZone item = (GeoHomeZone) nodeSelectEvent.getTreeNode().getData();
			commandEditGeoHome(item);
		}
	}

	// Remove ContextMenu
	public void removeContextMenu(NodeSelectEvent nodeSelectEvent) {
		super.getTreeCommonBean().onNodeSelect(nodeSelectEvent);
		if (nodeSelectEvent != null) {
			GeoHomeZone item = (GeoHomeZone) nodeSelectEvent.getTreeNode().getData();
			commandDeleteGeoHome(item);
			loadGeoHomeByCategory(item.getCategoryId());
		}
	}

	public void commandAddNewGeoHome() {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("geohome.title"));
		geoHome = new GeoHomeZone();
		refreshGeoHome(geoHome);
		geoHome.setCategoryId(category.getCategoryId());
		this.isApply = false;
	}

	public void commandEditGeoHome(GeoHomeZone geoHomeZone) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		super.getTreeCommonBean().setContentTitle(super.readProperties("geohome.title"));
		refreshGeoHome(geoHomeZone);
		this.isApply = false;
	}

	public void commandCloneGeoHome(GeoHomeZone geoHomeZone) {
		super.getTreeCommonBean().hideAllCategoryComponent();
		refreshGeoHome(geoHomeZone);
		geoHomeZone.setGeoHomeZoneId(0L);
	}

	public void commandDeleteGeoHome(GeoHomeZone item) {
		this.isEditting = true;
		GeoNetZoneDAO geoNetZoneDAO = new GeoNetZoneDAO();
		NormParamDAO normParamDAO = new NormParamDAO();
		if (!geoNetZoneDAO.checkGNInGH(item.getGeoHomeZoneId()) && !normParamDAO.checkGeoHomeInNP(item.getGeoHomeZoneId())) {
			try {
				geoHomeDAO.delete(item);
				listGeoHomeByCategory.remove(item);
				refreshCategories(category);
				TreeCommonBean treeCommonBean = super.getTreeCommonBean();
				Category cat = categoryDAO.get(item.getCategoryId());
				treeCommonBean.removeTreeNodeAll(item);
				this.showMessageINFO("Geo Home Zone", super.readProperties("validate.deleteSuccess"));
			} catch (Exception e) {
				throw e;
			}
		} else {
			this.showMessageWARN("common.summary.warning", super.readProperties("validate.fieldUseIn"));
		}

	}

	public void btnAddGeoHome() {
		geoHome = new GeoHomeZone();

	}

	// load list list GeoHomeZone by Category
	public List<GeoHomeZone> loadGeoHomeByCategory(long categoryId) {
		listGeoHomeByCategory = new ArrayList<GeoHomeZone>();
		listGeoHomeByCategory = geoHomeDAO.findGHZByConditions(categoryId);
		return listGeoHomeByCategory;
	}

	public List<SelectItem> loadComboListCategory() {
		listComboCategory = new ArrayList<SelectItem>();
		CategoryDAO categoryDAO = new CategoryDAO();
		List<Category> listCategory = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_GHZ_GEO_HOME_ZONE);
		if (listComboCategory != null && !listCategory.isEmpty()) {
			for (Category category : listCategory) {
				listComboCategory.add(new SelectItem(category.getCategoryId(), category.getCategoryName()));
			}
		}
		return listComboCategory;
	}

	private List<Category> categoriesOfGeoHome;

	private void LoadCategoriesOfGeoHome() {
		categoriesOfGeoHome = categoryDAO.findByTypeForSelectbox(CategoryType.CTL_GHZ_GEO_HOME_ZONE);
	}

	public List<Category> getCategoriesOfGeoHome() {
		return categoriesOfGeoHome;
	}

	public List<SelectItem> loadComboGeoHomeZoneType() {
		listGeoHomeZoneType = new ArrayList<SelectItem>();
		listGeoHomeZoneType.add(new SelectItem(ContantsUtil.GeoHomeZoneType.DEFAULT_TYPE,
				ContantsUtil.GeoHomeZoneType.DEFAULT_TYPE_NAME));
		return listGeoHomeZoneType;
	}

	public void saveGeoHome() {
		if (checkValidListGeoNet()) {
			TreeCommonBean treeCommonBean = super.getTreeCommonBean();
			Category cat = categoryDAO.get(geoHome.getCategoryId());
			if (validateGeoHome()) {
				geoNet.setUpdateDate(new Date());
				geoHomeDAO.saveGeoNetAndGeoHome(geoHome, listGeoNetByGeoHome);
				treeCommonBean.updateTreeNode(geoHome, cat, null);
				this.showMessageINFO("common.save", " Geo Home Zone ");
			}
			this.isApply = true;
		}
	}

	// Validate

	private boolean validateGeoHome() {
		boolean result = true;
		if (ValidateUtil.checkStringNullOrEmpty(geoHome.getGeoHomeZoneName())) {
			this.showMessageWARN("Geo Home Zone", super.readProperties("validate.checkValueNameNull"));

			result = false;
		}
		if (geoHomeDAO.checkName(geoHome)) {
			this.showMessageWARN("Geo Home Zonee", super.readProperties("validate.checkValueNameExist"));
			result = false;
		}

		return result;
	}

	// check name isExist
	public Boolean checkValidListGeoNet() {

		boolean result = true;

		if (this.listGeoNetByGeoHome.size() > 0) {
			List<Long> cellIDDuplicate = new ArrayList<>();
			for (int i = 0; i < this.listGeoNetByGeoHome.size(); i++) {
				for (int j = 0; j < this.listGeoNetByGeoHome.size(); j++) {
					if (i != j) {

						if (this.listGeoNetByGeoHome.get(i).getCellId() <= 0) {
							this.showMessageWARN("Cell ID", super.readProperties("validate.checkValueLess"));
							return false;
						}
						if (this.listGeoNetByGeoHome.get(i).getCellId() == null) {
							this.showMessageWARN("Cell ID", super.readProperties("validate.checkValueNameNull"));
							return false;
						}
						if (this.listGeoNetByGeoHome.get(i).getCellId()
								.equals(this.listGeoNetByGeoHome.get(j).getCellId())) {
							cellIDDuplicate.add(this.listGeoNetByGeoHome.get(i).getCellId());
							result = false;
						}
					}
				}
			}

			if (cellIDDuplicate.size() > 0) {
				String listCellIDDuplicate = "";
				for (int i = 0; i < cellIDDuplicate.size(); i++) {
					if (listCellIDDuplicate.isEmpty()) {
						listCellIDDuplicate += cellIDDuplicate.get(i);
					} else {
						if (listCellIDDuplicate.indexOf(String.valueOf(cellIDDuplicate.get(i))) == -1)
							listCellIDDuplicate += "," + cellIDDuplicate.get(i);
					}
				}

				this.showMessageWARN("Cell ID",
						super.readProperties("validate.checkValueNameExist") + " " + listCellIDDuplicate);
			}
		}

		return result;
	}

	public void deleteGeoHome(GeoHomeZone item) {
		this.geoHome = item;
		listGeoHomeByCategory.remove(item);
		geoHomeDAO.delete(item);

	}

	public void editGeoHome(GeoHomeZone item) {
		this.geoHome = item;

	}

	public void cancelGeoHome() {
		refreshCategories(category);
		Category category = new Category();
	}

	// Geo Net Zone

	// load list UnitType by Category
	public List<GeoNetZone> loadGeoNetByGeoHome(long geoHomeZoneId) {
		listGeoNetByGeoHome = new ArrayList<GeoNetZone>();
		listGeoNetByGeoHome = geoNetDAO.findGeoNetZoneByConditions(geoHomeZoneId);
		return listGeoNetByGeoHome;
	}

	// public void commandAddNewGeoNet() {
	// geoNet = new GeoNetZone();
	//
	// if (geoHome.getGeoHomeZoneId() == 0L) {
	// this.showMessageWARN("", super.readProperties("validate.geoNetAddNew"));
	// } else {
	// geoNet.setGeoHomeZoneId(geoHome.getGeoHomeZoneId());
	// refressGeoNet(geoNet);
	// geoNet.setUpdateDate(new Date());
	// }
	//
	// TreeCommonBean treeCommonBean = super.getTreeCommonBean();
	// treeCommonBean.hideAllCategoryComponent();
	// treeCommonBean.setContentTitle(super.readProperties("geoNet.title"));
	// }

	public void showDialogGNZ(GeoNetZone geoNetZone) {
		if (geoNetZone == null) {
			geoNetZone = new GeoNetZone();
			geoNetZone.setUpdateDate(new Date());
		}
		this.geoNet = geoNetZone;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgGeoNetWR').show();");
	}

	// public void saveGeoNet() {
	// if (validateGeoNet()) {
	// if (geoNet.getGeoNetZoneId() == 0L) {
	// listGeoNetByGeoHome.add(geoNet);
	// }
	// geoNet.setUpdateDate(new Date());
	// geoNetDAO.saveOrUpdate(geoNet);
	// cancelGeoNet();
	// this.showMessageINFO("common.save", "Geo Net Zone ");
	// }
	// }

	public void cmdApplyGeoNet() {
		if (validateGeoNet()) {
			geoNet.setUpdateDate(new Date());
			geoNet.setGeoHomeZoneId(geoHome.getGeoHomeZoneId());
			geoNetDAO.saveOrUpdate(geoNet);
			boolean checkNew = true;
			for (int i = 0; i < listGeoNetByGeoHome.size(); i++) {
				if (geoNet.getGeoNetZoneId() == (listGeoNetByGeoHome.get(i).getGeoNetZoneId())) {
					listGeoNetByGeoHome.set(i, geoNet);
					checkNew = false;
					break;
				}
			}
			if (checkNew) {
				listGeoNetByGeoHome.add(geoNet);
			}
			this.showMessageINFO("common.save", "Geo Net Zone");
			RequestContext context = RequestContext.getCurrentInstance();
//			context.execute("$('.geoNetClearFilter').click();");
			context.execute("PF('dlgGeoNetWR').hide();");
		}
	}

	private boolean validateGeoNet() {
		boolean result = true;
		if (geoNet.getCellId() == null) {
			this.showMessageWARN("cell.cellId", super.readProperties("validate.checkValueNameNull"));
			result = false;
		}
		if (geoNet.getCellId() <= 0) {
			this.showMessageWARN("common.save", this.readProperties("cell.cellId"));
			result = false;
		}
		if (geoNet.getCellId() == null || geoNet.getCellId() == 0) {
			this.showMessageWARN("cell.cellId", super.readProperties("validate.fieldNull"));
			result = false;
		}
//		if (geoNetDAO.checkNameCellID(geoNet, geoNet.getCellId())) {
//			this.showMessageWARN("cell.cellId", super.readProperties("validate.checkValueNameExist"));
//			result = false;
//		}
		if (geoNetDAO.checkNameCellIDNew(geoNet, geoNet.getCellId(), geoHome.getGeoHomeZoneId())) {
			this.showMessageWARN("cell.cellId", super.readProperties("validate.checkValueExist"));
			result = false;
		}
		return result;
	}

	public void deleteGeoNet(GeoNetZone item) {
		geoNetDAO.delete(item);
		listGeoNetByGeoHome.remove(item);
		refreshGeoHome(geoHome);
		this.showMessageINFO("common.delete", " Geo Net Zone ");
	}

	// public void editNetHome(GeoNetZone item) {
	// this.geoNet = item;
	// super.getTreeCommonBean().setContentTitle(super.readProperties("geoNet.title"));
	// refressGeoNet(item);
	// }

	public void cancelGeoNet() {
		refreshGeoHome(geoHome);
		GeoHomeZone geoHome = new GeoHomeZone();
	}

	public void selectEvent(AjaxBehaviorEvent event) {

	}

	public boolean activeButton() {
		if (listGeoNetSelection != null && listGeoNetSelection.size() > 0 && isApply == false) {
			return true;
		}
		return false;
	}

	public void commandRemoveGeoNet() {
		if (geoNetDAO.delListGeoNet(listGeoNetSelection)) {
			listGeoNetSelection.clear();
			activeButton();
			refreshGeoHome(geoHome);
			loadGeoNetByGeoHome(geoHome.getGeoHomeZoneId());
			this.showMessageINFO("validate.deleteSuccess", super.readProperties(""));
		}
	}

	// Export GeoNetZone

	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
		Row row = sheet.getRow(0);
		Cell cell00 = row.createCell(0);
		cell00.setCellValue(this.readProperties("geoNet.list"));
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
		readGNFromExcelFile(file);
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

	// Import GeoNetZone

	public void readGNFromExcelFile(UploadedFile file) {
		
		Workbook workbook = null;
		try {
			
			workbook = new HSSFWorkbook(file.getInputstream());
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			List<Long> cellIDDuplicate = new ArrayList<>();
			int count = 0;
			while (iterator.hasNext()) {
				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() == 2 || nextRow.getRowNum() == 1 || nextRow.getRowNum() == 0) {
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					GeoNetZone geoNetZone = new GeoNetZone();
					boolean hasError = false;
					if (!hasError) {
						while (cellIterator.hasNext()) {
							Cell nextCell = cellIterator.next();
							int columnIndex = nextCell.getColumnIndex();

							switch (columnIndex) {

							case 1:
								// geoNetZone.setCellId(Long.valueOf(getCellValue(nextCell)));
								// break;

								if (checkIsExistGeoNet("cellId", (Double.valueOf(getCellValue(nextCell))).longValue())) {
									cellIDDuplicate.add((Double.valueOf(getCellValue(nextCell))).longValue());
									hasError = true;
								} else {
									if ((Double.valueOf(getCellValue(nextCell))).longValue() >= 0) {
										geoNetZone.setCellId((Double.valueOf(getCellValue(nextCell))).longValue());
										hasError = false;
									} else {
										cellIDDuplicate.add((Double.valueOf(getCellValue(nextCell))).longValue());
										hasError = true;
									}
								}
								break;
							}

						}
					}
					if (!hasError) {
						listGeoNetByGeoHome.add(geoNetZone);
						geoHomeDAO.saveGeoNetAndGeoHome(geoHome, listGeoNetByGeoHome);
						count++;
					}
				} catch (Exception e) {
					getLogger().warn(e.getMessage(), e);
					super.showNotificationFail();
					return;
				}
			}

			if (count == 0) {
				// super.showNotificationFail();
				super.showMessageWARN("validate.importFail", "");
				return;
			}

			if (cellIDDuplicate.size() > 0) {
				String listCellIDDuplicate = "";
				for (int i = 0; i < cellIDDuplicate.size(); i++) {
					if (listCellIDDuplicate.isEmpty()) {
						listCellIDDuplicate += cellIDDuplicate.get(i);
					} else {
						listCellIDDuplicate += "," + cellIDDuplicate.get(i);
					}
				}
				this.showMessageWARN("Cell ID",
						super.readProperties("your import file") + " has " + count + " success record and "
								+ cellIDDuplicate.size() + " duplicate record with name : " + listCellIDDuplicate);
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
	}

	public Boolean checkIsExistGeoNet(String column, Long value) {
		boolean result = false;

		if (column.equals("cellId")) {
			if (this.listGeoNetByGeoHome.size() > 0) {
				for (GeoNetZone z : this.listGeoNetByGeoHome) {
					if (z.getCellId().equals(value)) {
						result = true;
						break;
					}
				}
			}

			if (!result) {
				if (geoNetDAO.checkFieldIsExist(column, value, new GeoNetZone())) {
					result = true;
				}
			}
		}

		return result;
	}

	private StreamedContent fileTemEX;

	public void fileDownloadView() {
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream("/resources/themes/temexcel/geonetzoneimporttem.xls");
		fileTemEX = new DefaultStreamedContent(stream, "office/xls", "geonetzoneimporttem.xls");
	}

	public StreamedContent getFileTemEX() {
		return fileTemEX;
	}

	// GET SET
	public GeoHomeZone getGeoHome() {
		return geoHome;
	}

	public void setGeoHome(GeoHomeZone geoHome) {
		this.geoHome = geoHome;
	}

	public List<GeoHomeZone> getListGeoHomeByCategory() {
		return listGeoHomeByCategory;
	}

	public void setListGeoHomeByCategory(List<GeoHomeZone> listGeoHomeByCategory) {
		this.listGeoHomeByCategory = listGeoHomeByCategory;
	}

	public GeoNetZone getGeoNet() {
		return geoNet;
	}

	public void setGeoNet(GeoNetZone geoNet) {
		this.geoNet = geoNet;
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

	public List<GeoNetZone> getListGeoNetByGeoHome() {
		return listGeoNetByGeoHome;
	}

	public void setListGeoNetByGeoHome(List<GeoNetZone> listGeoNetByGeoHome) {
		this.listGeoNetByGeoHome = listGeoNetByGeoHome;
	}

	public List<SelectItem> getListGeoHomeZoneType() {
		return listGeoHomeZoneType;
	}

	public void setListGeoHomeZoneType(List<SelectItem> listGeoHomeZoneType) {
		this.listGeoHomeZoneType = listGeoHomeZoneType;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<GeoNetZone> getListGeoNetSelection() {
		return listGeoNetSelection;
	}

	public void setListGeoNetSelection(List<GeoNetZone> listGeoNetSelection) {
		this.listGeoNetSelection = listGeoNetSelection;
	}

	public boolean isApply() {
		return isApply;
	}

	public void setApply(boolean isApply) {
		this.isApply = isApply;
	}

}

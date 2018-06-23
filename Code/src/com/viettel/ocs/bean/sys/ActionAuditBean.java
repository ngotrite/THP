package com.viettel.ocs.bean.sys;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.CategoryType;
import com.viettel.ocs.dao.AuditLogDAO;
import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.SysUserDAO;
import com.viettel.ocs.entity.AuditLog;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.SysUser;

@ManagedBean(name = "actionAuditBean")
@ViewScoped
public class ActionAuditBean extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = -1075306272896227027L;
	private List<AuditLog> lstAuditLog = null;
	private List<SelectItem> lstSysUser = null;
	private AuditLogDAO auditLogDAO;
	private SysUserDAO sysUserDAO;
	private Date fromDate;
	private Date toDate;
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<AuditLog> getLstAuditLog() {
		return lstAuditLog;
	}

	public void setLstAuditLog(List<AuditLog> lstAuditLog) {
		this.lstAuditLog = lstAuditLog;
	}

	public ActionAuditBean() {
		auditLogDAO = new AuditLogDAO();
		sysUserDAO = new SysUserDAO();
		lstSysUser = new ArrayList<>();
		lstAuditLog = new ArrayList<>();
		toDate = new Date();
		fromDate = addDate(toDate, -30);
		loadData();
	}

	private void loadData() {
		lstAuditLog = auditLogDAO.findAuditLogByUserIdAndCreateDate(userName, fromDate, toDate);
	}

	public List<SelectItem> loadUser() {
		lstSysUser = new ArrayList<SelectItem>();
		List<SysUser> listUser = sysUserDAO.findAll("");
		if (listUser != null && !listUser.isEmpty()) {
			for (SysUser user : listUser) {
				lstSysUser.add(new SelectItem(user.getUserName(), user.getUserName()));
			}
		}
		lstSysUser.add(0, new SelectItem(null, this.readProperties("log.all")));
		return lstSysUser;
	}

	private boolean compareDate(Date date1, Date date2) {
		if (date1.after(date2)) {
			return true;
		}
		return false;

	}

	public long calculateDays(Date dateEarly, Date dateLater) {
		return (dateLater.getTime() - dateEarly.getTime()) / (24 * 60 * 60 * 1000);
	}

	public void btnSearch() {
		long day = calculateDays(fromDate, toDate);
		if (day > 30) {
			super.showMessageWARN("common.search", this.readProperties("log.title"),
					this.readProperties("log.error30day"));
		} else {
			if (!compareDate(fromDate, toDate)) {
				loadData();
			} else {
				super.showMessageWARN("common.search", this.readProperties("log.title"),
						this.readProperties("log.afterdate"));
			}
		}
	}

	private Date addDate(Date date, int day) {
		Calendar calendar = new GregorianCalendar(/* remember about timezone! */);
		calendar.setTime(toDate);
		calendar.add(Calendar.DATE, -30);
		return calendar.getTime();
	}

	public void postProcessXLS(Object document) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		HSSFWorkbook wb = (HSSFWorkbook) document;
		try {
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.shiftRows(0, sheet.getLastRowNum(), 1);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
			Row row = sheet.getRow(0);
			row.setHeight((short) 550);
			Cell cell00 = row.createCell(0);
			cell00.setCellValue(this.readProperties("log.exportTitle") + dateFormat.format(fromDate) + " "
					+ this.readProperties("log.exportToDate") + dateFormat.format(toDate));
			CellStyle styleHeader = wb.createCellStyle();
			Font boldHeader = wb.createFont();
			boldHeader.setBoldweight(Font.BOLDWEIGHT_BOLD);
			boldHeader.setFontHeightInPoints((short) 12);
			boldHeader.setFontName("Times New Roman");
			styleHeader.setAlignment(CellStyle.ALIGN_CENTER);
			styleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			styleHeader.setFont(boldHeader);
			styleHeader.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
			cell00.setCellStyle(styleHeader);
			sheet.shiftRows(1, sheet.getLastRowNum(), 1);

			HSSFRow header = sheet.getRow(2);
			header.setHeight((short) 300);
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
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				getLogger().warn(e.getMessage(), e);
			}
		}		
	}
}

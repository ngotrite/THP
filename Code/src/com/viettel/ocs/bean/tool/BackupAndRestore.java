package com.viettel.ocs.bean.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.dao.BackupDAO;
import com.viettel.ocs.util.ConfigUtil;
import com.viettel.ocs.util.LocaleUtils;

@ManagedBean(name = "backupAndRestore")
public class BackupAndRestore extends BaseController implements Serializable {

	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = 2287542113479037003L;
	private int sizeLimit = 1024 * 1024;

	private final static Logger logger = Logger.getLogger(BackupAndRestore.class);

	public int getSizeLimit() {
		return sizeLimit;
	}

	public void setSizeLimit(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public BackupAndRestore() {
		sizeLimit = sizeLimit * Integer.parseInt(this.readProperties("restore.sizeLimit"));
	}

//	public void btnUpload(FileUploadEvent event) {
//		try {
//			int type = 0; // 1: window; 2: lunix; 0: khác
//			if (isWindows()) {
//				type = 1;
//			} else if (isUnix()) {
//				type = 2;
//			}
//			String filePath = copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
//			RestoreDBFromSql(this.readProperties("restore.dumpExePath"), this.readProperties("restore.user"),
//					this.readProperties("restore.pass"), this.readProperties("restore.db"), filePath, type);
//			this.showMessageINFO("export.restore", this.readProperties("export.db"));
//		} catch (IOException e) {
//			getLogger().warn(e.getMessage(), e);
//		}
//	}

	public void btnBackup1() {
		try {

			logger.info("BACKUP DB - BEGIN");

			StringBuilder sb = new StringBuilder();
			StringBuffer buff = new StringBuffer();
			BackupDAO backupDAO = new BackupDAO();
			String default_schema_name = backupDAO.getCurrentSchemaName();

			List<String> rs = backupDAO.getAllTable(default_schema_name);
			for (int i = 0; i < rs.size(); i++) {
				
				String tblName = rs.get(i);
				logger.info("BACKUP TABLE: " + tblName);
				
				sb.append("\n");
				sb.append("-- ----------------------------\n").append("-- Table structure for `").append(tblName)
						.append("`\n-- ----------------------------\n");
				sb.append("DROP TABLE IF EXISTS `").append(tblName).append("`;\n");
				String rs2 = backupDAO.getSQLCreateTable(tblName);
				String crt = rs2 + ";";
				sb.append(crt).append("\n");
				sb.append("\n");
				sb.append("-- ----------------------------\n").append("-- Records for `").append(tblName)
						.append("`\n-- ----------------------------\n");
				List lstObject = backupDAO.getAllData(tblName);
				int colCount = backupDAO.getCountColumn(tblName, default_schema_name);
				for (Object obj : lstObject) {
					
					Object[] objArr;
					if(obj instanceof Object[])
						objArr = (Object[]) obj;
					else
						objArr = new Object[]{obj};
					
					if (colCount > 0) {
						sb.append("INSERT INTO ").append(tblName).append(" VALUES(");
						for (int j = 0; j < colCount; j++) {
							if (j > 0) {
								sb.append(",");
							}
							String s = "";
							try {
								s += "'";
								s += objArr[j].toString().equals("'") ? objArr[j].toString() + "'"
										: objArr[j].toString();
								s += "'";
							} catch (Exception e) {
								s = "NULL";
							}
							sb.append(s);
						}
						sb.append(");\n");
						buff.append(sb.toString());
						sb = new StringBuilder();
					}
				}
			}
			writeToFile(buff.toString());

			logger.info("BACKUP DB - SUCCESSFUL");
		} catch (Exception e) {
			super.showMessageWARN("common.export", this.readProperties("export.db"),
					this.readProperties("export.notsupport"));
			logger.warn("BACKUP DB - FAIL", e);
		}
	}

	public void writeToFile(String content) {
		try {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String basePath = ctx.getRealPath("/");
			String folderPath = basePath + "backuprestoredb";
			File f1 = new File(folderPath);
			f1.mkdir();
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
			Date now = new Date();
			String strFilename = dateFormat.format(now);
			String fileName = folderPath + "\\" + "backup_" + strFilename + ".sql";
			String fileNameDowload = "backup_" + strFilename + ".sql";
			File file = new File(fileName);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			out.write(content);
			out.flush();
			out.close();
			dowloadFile(fileName);
			delete(fileName);
		} catch (IOException e) {
			logger.warn("BACKUP DB FAIL: ", e);
		}
	}

	private void dowloadFile(String filePath) {
		if (filePath == null) {
			super.showMessageWARN("common.export", this.readProperties("export.db"),
					this.readProperties("export.erorr"));
			return;
		} else {
			File file = new File(filePath);
			FileInputStream input = null;
			try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				response.setContentType(getContentType(filePath));
				response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
				input = new FileInputStream(file);
				byte[] bytesBuffer = new byte[2048];
				int bytesRead;
				while ((bytesRead = input.read(bytesBuffer)) > 0) {
					response.getOutputStream().write(bytesBuffer, 0, bytesRead);
				}
				response.getOutputStream().flush();
				response.getOutputStream().close();
				facesContext.responseComplete();
				super.showMessageINFO("export.backup", this.readProperties("export.db"),
						this.readProperties("export.backupSuccesses"));
			} catch (IOException e) {
				logger.warn("BACKUP DB FAIL: ", e);
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (IOException e) {
					logger.warn("BACKUP DB: ", e);
				}
			}
		}
	}

	private void delete(String filePath) {
		try {

			File file = new File(filePath);
			if (file.delete()) {
			} else {
			}
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
		}
	}

	public String getContentType(String filePath) {
		return FacesContext.getCurrentInstance().getExternalContext().getMimeType(filePath);
	}

	private boolean isWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}

	private boolean isUnix() {
		return SystemUtils.IS_OS_LINUX;
	}

	public String copyFile(String fileName, InputStream in) {
		String filePath = null;
		OutputStream out = null;
		try {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String basePath = ctx.getRealPath("/");
			String folderPath = basePath + "backuprestoredb";
			File f1 = new File(folderPath);
			f1.mkdir();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
			String name = "restore_" + fmt.format(new Date()) + "_" + fileName;
			File file = new File(folderPath + "\\" + name);
			out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			filePath = folderPath + "\\" + name;
		} catch (IOException e) {
			logger.warn("Backup Error:", e);
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				getLogger().warn(e.getMessage(), e);
			}
		}
		return filePath;
	}

	private void RestoreDBFromSql(String dumpExePath, String user, String password, String database, String restorePath,
			int type) {
		try {
			String[] executeCmd = new String[] { dumpExePath, ConfigUtil.getCfg("default_schema"), "--user=" + user,
					"--password=" + password, "-e", "source " + restorePath };
			Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			int processComplete = runtimeProcess.waitFor();
		} catch (IOException | InterruptedException ex) {
			logger.warn("Restore Error:", ex);
		}

	}
}

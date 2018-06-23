package com.viettel.ocs.bean.offer;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.bean.TreeOfferBean;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.BlockDAO;

import com.viettel.ocs.dao.CategoryDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.Category;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.model.LogicCloneModel;
import com.viettel.ocs.util.ValidateUtil;

@ManagedBean(name = "cloneCommonBean")
@ViewScoped
public class CloneCommonBean extends BaseController {
	private LogicCloneModel treeCloneModel;
	private BlockDAO blockDAO;
	private PriceComponentDAO priceComponentDAO;
	private SortPriceComponentDAO sortPriceComponentDAO;
	private ActionDAO actionDAO;
	private RateTableDAO rateTableDAO;
	private DynamicReserveDAO dynamicReserveDAO;
	private DecisionTableDAO decisionTableDAO;
	private ActionTypeDAO actionTypeDAO ;

	private Object objectToClone;
	private String type;
	private Integer treeTypeClone;
	private String id;
	private int step = 0;
	private CategoryDAO cateDAO;
	private Integer positionIndex;

	@PostConstruct
	public void init() {
		this.treeCloneModel = new LogicCloneModel();
		this.blockDAO = new BlockDAO();
		this.priceComponentDAO = new PriceComponentDAO();
		this.sortPriceComponentDAO = new SortPriceComponentDAO();
		this.actionDAO = new ActionDAO();
		this.rateTableDAO = new RateTableDAO();
		this.dynamicReserveDAO = new DynamicReserveDAO();
		decisionTableDAO = new DecisionTableDAO();
		actionTypeDAO = new ActionTypeDAO();

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String param = params.get("param");
		String[] values = param.split(";");
		String posIndex = params.get("index");
		String typeTrClone = params.get("treeTypeClone");
		if (ValidateUtil.checkStringNullOrEmpty(posIndex)) {
			this.positionIndex = 12;
		} else {
			this.positionIndex = Integer.valueOf(posIndex);
		}
		if (!ValidateUtil.checkStringNullOrEmpty(typeTrClone)) {
			this.treeTypeClone = Integer.valueOf(typeTrClone);
		}
		cateDAO = new CategoryDAO();

		this.type = values[0];
		this.id = values[1];

		setCheckedDefault(type);
	}

	private void setCheckedDefault(String type) {
		switch (type) {
		case "action":
			treeCloneModel.setAction(true);
			break;
		case "spc":
			treeCloneModel.setSpc(true);
			break;
		case "pc":
			treeCloneModel.setPc(true);
			break;
		case "block":
			treeCloneModel.setBlock(true);
			break;
		case "rateTable":
			treeCloneModel.setRateTable(true);
			break;
		case "decision_table":
			treeCloneModel.setDt(true);
			break;
		case "norm":
			treeCloneModel.setNormalizer(true);
			break;
		case "dr":
			treeCloneModel.setDr(true);
			break;
		default:
			break;
		}
	}

	public void onChangeChecked(Boolean newValue, String node) {
		switch (node) {
		case "action":
			if (newValue) {
				treeCloneModel.setOffer(true);
			} else {
				treeCloneModel.setPc(false);
				treeCloneModel.setBlock(false);
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "pc":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
			} else {
				treeCloneModel.setBlock(false);
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "block":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
			} else {
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "rateTable":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
				treeCloneModel.setBlock(true);
			} else {
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "dt":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
				treeCloneModel.setBlock(true);
				treeCloneModel.setRateTable(true);
			} else {
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "norm":
			if (newValue) {
				treeCloneModel.setOffer(true);
				treeCloneModel.setAction(true);
				treeCloneModel.setPc(true);
				treeCloneModel.setBlock(true);
				treeCloneModel.setRateTable(true);
				treeCloneModel.setDt(true);
				treeCloneModel.setNormalizer(true);
			}
			break;

		default:
			break;
		}
	}

	public void onChangeCheckedSpc(Boolean newValue, String node) {
		switch (node) {
		case "spc":
			if (newValue) {

			} else {
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "rateTable":
			if (newValue) {
				treeCloneModel.setSpc(true);
			} else {
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "dt":
			if (newValue) {
				treeCloneModel.setSpc(true);
				treeCloneModel.setRateTable(true);
			} else {
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "norm":
			if (newValue) {
				treeCloneModel.setSpc(true);
				treeCloneModel.setRateTable(true);
				treeCloneModel.setDt(true);
				treeCloneModel.setNormalizer(true);
			}
			break;
		default:
			break;
		}
	}

	public void onChangeCheckedDr(Boolean newValue, String node) {
		switch (node) {
		case "dr":
			if (newValue) {
				
			} else {
				treeCloneModel.setRateTable(false);
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "rateTable":
			if (newValue) {
				treeCloneModel.setDr(true);
			} else {
				treeCloneModel.setDt(false);
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "dt":
			if (newValue) {
				treeCloneModel.setDr(true);
				treeCloneModel.setRateTable(true);
			} else {
				treeCloneModel.setNormalizer(false);
			}
			break;
		case "norm":
			if (newValue) {
				treeCloneModel.setDr(true);
				treeCloneModel.setRateTable(true);
				treeCloneModel.setDt(true);
				treeCloneModel.setNormalizer(true);
			}
			break;
		default:
			break;
		}
	}

	public void oncancel() {
		RequestContext.getCurrentInstance().closeDialog(0);
	}

	public void doClone() throws Exception {
		switch (type) {
		case "action":
			objectToClone = actionDAO.get(Long.valueOf(id));
			Action action;
			if ((action = actionDAO.cloneAction((Action) objectToClone, treeCloneModel.isPc(), treeCloneModel.isBlock(),
					treeCloneModel.isRateTable(), treeCloneModel.isDt(), treeCloneModel.isNormalizer(),
					"_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(action);
			}
			break;
		case "pc":
			objectToClone = priceComponentDAO.get(Long.valueOf(id));
			PriceComponent priceComponent;
			if ((priceComponent = priceComponentDAO.clonePC((PriceComponent) objectToClone, treeCloneModel.isBlock(),
					treeCloneModel.isRateTable(), treeCloneModel.isDt(), treeCloneModel.isNormalizer(),
					"_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(priceComponent);
			}
			break;
		case "block":
			objectToClone = blockDAO.get(Long.valueOf(id));
			Block block;
			if ((block = blockDAO.cloneBlock((Block) objectToClone, treeCloneModel.isRateTable(), treeCloneModel.isDt(),
					treeCloneModel.isNormalizer(), "_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(block);
			}
			break;
		case "rateTable":
			objectToClone = rateTableDAO.get(Long.valueOf(id));
			RateTable rateTable;
			if ((rateTable = rateTableDAO.cloneRateTable((RateTable) objectToClone, treeCloneModel.isDt(),
					treeCloneModel.isNormalizer(), "_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(rateTable);
			}
			break;
		case "decision_table":
			objectToClone = decisionTableDAO.get(Long.valueOf(id));
			DecisionTable decisionTable;
			if ((decisionTable = decisionTableDAO.cloneDT((DecisionTable) objectToClone, treeCloneModel.isNormalizer(),
					"_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(decisionTable);
			}
			break;
		case "actionType":
			objectToClone = actionTypeDAO.get(Long.valueOf(id));
			ActionType actionType;
			if ((actionType = actionTypeDAO.cloneAT((ActionType) objectToClone, treeCloneModel.isNormalizer(),
					"_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(actionType);
			}
			break;
		default:
			break;
		}
	}

	public void doCloneSPC() throws Exception {
		switch (type) {
		case "spc":
			objectToClone = sortPriceComponentDAO.get(Long.valueOf(id));
			SortPriceComponent sortPriceComponentClone;
			if ((sortPriceComponentClone = sortPriceComponentDAO.cloneSPC((SortPriceComponent) objectToClone,
					treeCloneModel.isRateTable(), treeCloneModel.isDt(), treeCloneModel.isNormalizer(),
					"_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(sortPriceComponentClone);
			}
			break;
		case "rateTable":
			objectToClone = rateTableDAO.get(Long.valueOf(id));
			RateTable rateTable;
			if ((rateTable = rateTableDAO.cloneRateTable((RateTable) objectToClone, treeCloneModel.isDt(),
					treeCloneModel.isNormalizer(), "_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(rateTable);

			}
			break;
		default:
			break;
		}
	}

	public void doCloneDr() throws Exception {
		switch (type) {
		case "dr":
			objectToClone = dynamicReserveDAO.get(Long.valueOf(id));

			DynamicReserve dynamicReserveClone;
			if ((dynamicReserveClone = dynamicReserveDAO.cloneDR((DynamicReserve) objectToClone,
					treeCloneModel.isRateTable(), treeCloneModel.isDt(), treeCloneModel.isNormalizer(),
					"_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(dynamicReserveClone);
			}

			break;
		case "rateTable":
			objectToClone = rateTableDAO.get(Long.valueOf(id));
			RateTable rtMaxIndex = rateTableDAO.findRateTableLastIndex();
			RateTable rateTable;
			if ((rateTable = rateTableDAO.cloneRateTable((RateTable) objectToClone, treeCloneModel.isDt(),
					treeCloneModel.isNormalizer(), "_clone")) != null) {
				RequestContext.getCurrentInstance().closeDialog(rateTable);
				Category cat = cateDAO.get(rtMaxIndex.getCategoryId());
				TreeOfferBean treeOfferBean = super.getTreeOfferBean();
				treeOfferBean.moveUpTreeNode(rtMaxIndex, cat);
			}
			break;
		default:
			break;
		}
	}

	// GET, SET
	public LogicCloneModel getTreeCloneModel() {
		return treeCloneModel;
	}

	public void setTreeCloneModel(LogicCloneModel treeCloneModel) {
		this.treeCloneModel = treeCloneModel;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public CategoryDAO getCateDAO() {
		return cateDAO;
	}

	public void setCateDAO(CategoryDAO cateDAO) {
		this.cateDAO = cateDAO;
	}

	public Integer getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(Integer positionIndex) {
		this.positionIndex = positionIndex;
	}

	public Integer getTreeTypeClone() {
		return treeTypeClone;
	}

	public void setTreeTypeClone(Integer treeTypeClone) {
		this.treeTypeClone = treeTypeClone;
	}

	public DynamicReserveDAO getDynamicReserveDAO() {
		return dynamicReserveDAO;
	}

	public void setDynamicReserveDAO(DynamicReserveDAO dynamicReserveDAO) {
		this.dynamicReserveDAO = dynamicReserveDAO;
	}

}

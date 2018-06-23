package com.viettel.ocs.bean.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.google.gson.Gson;
import com.viettel.ocs.bean.BaseController;
import com.viettel.ocs.constant.ContantsUtil;
import com.viettel.ocs.constant.ContantsUtil.OfferType;
import com.viettel.ocs.dao.ActionDAO;
import com.viettel.ocs.dao.ActionTypeDAO;
import com.viettel.ocs.dao.BalTypeDAO;
import com.viettel.ocs.dao.BillingCycleTypeDAO;
import com.viettel.ocs.dao.BlockDAO;
import com.viettel.ocs.dao.CdrProcessConfigDAO;
import com.viettel.ocs.dao.DecisionTableDAO;
import com.viettel.ocs.dao.DynamicReserveDAO;
import com.viettel.ocs.dao.EventDAO;
import com.viettel.ocs.dao.GeoHomeZoneDAO;
import com.viettel.ocs.dao.MapAcmbalBalDAO;
import com.viettel.ocs.dao.MonitorKeyDAO;
import com.viettel.ocs.dao.NormalizerDAO;
import com.viettel.ocs.dao.OfferDAO;
import com.viettel.ocs.dao.PCCRuleDAO;
import com.viettel.ocs.dao.ParameterDAO;
import com.viettel.ocs.dao.PriceComponentDAO;
import com.viettel.ocs.dao.ProfilePepDAO;
import com.viettel.ocs.dao.QosDAO;
import com.viettel.ocs.dao.RateTableDAO;
import com.viettel.ocs.dao.ReserveInfoDAO;
import com.viettel.ocs.dao.ServiceDAO;
import com.viettel.ocs.dao.SortPriceComponentDAO;
import com.viettel.ocs.dao.StateGroupDAO;
import com.viettel.ocs.dao.StateTypeDAO;
import com.viettel.ocs.dao.TriggerDestinationDAO;
import com.viettel.ocs.dao.TriggerMsgDAO;
import com.viettel.ocs.dao.TriggerOcsDAO;
import com.viettel.ocs.dao.UnitTypeDAO;
import com.viettel.ocs.dao.ZoneDataDAO;
import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.ActionType;
import com.viettel.ocs.entity.BillingCycleType;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.CdrProcessConfig;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Event;
import com.viettel.ocs.entity.GeoHomeZone;
import com.viettel.ocs.entity.MonitorKey;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.Offer;
import com.viettel.ocs.entity.Parameter;
import com.viettel.ocs.entity.PccRule;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.ProfilePep;
import com.viettel.ocs.entity.Qos;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.ReserveInfo;
import com.viettel.ocs.entity.Service;
import com.viettel.ocs.entity.SortPriceComponent;
import com.viettel.ocs.entity.StateGroup;
import com.viettel.ocs.entity.StateType;
import com.viettel.ocs.entity.TriggerDestination;
import com.viettel.ocs.entity.TriggerMsg;
import com.viettel.ocs.entity.TriggerOcs;
import com.viettel.ocs.entity.UnitType;
import com.viettel.ocs.entity.ZoneData;

@ManagedBean(name = "chartDataProvider")
@ViewScoped
public class ChartDataProvider extends BaseController implements Serializable {
	/**
	 * @author Nampv
	 */
	private static final long serialVersionUID = 6597639710961001461L;
	private String dataPieChart;
	private int offerAll;
	private OfferDAO dao;
	private String tilePieChart;
	private String categoriesBarChart;
	private String dataBarChart;
	private String tileBarChart;
	private String dataLineChart;
	private String tileLineChart;
	private List<dataList> dataListObj;
	private List<dataList> dataListObjCatalog;
	private String srtReplace = "com.viettel.ocs.entity.";

	public List<dataList> getDataListObjCatalog() {
		return dataListObjCatalog;
	}

	public void setDataListObjCatalog(List<dataList> dataListObjCatalog) {
		this.dataListObjCatalog = dataListObjCatalog;
	}

	public List<dataList> getDataListObj() {
		return dataListObj;
	}

	public void setDataListObj(List<dataList> dataListObj) {
		this.dataListObj = dataListObj;
	}

	public String getTileLineChart() {
		return tileLineChart;
	}

	public void setTileLineChart(String tileLineChart) {
		this.tileLineChart = tileLineChart;
	}

	public String getDataLineChart() {
		return dataLineChart;
	}

	public void setDataLineChart(String dataLineChart) {
		this.dataLineChart = dataLineChart;
	}

	public String getTileBarChart() {
		return tileBarChart;
	}

	public void setTileBarChart(String tileBarChart) {
		this.tileBarChart = tileBarChart;
	}

	public String getCategoriesBarChart() {
		return categoriesBarChart;
	}

	public void setCategoriesBarChart(String categoriesBarChart) {
		this.categoriesBarChart = categoriesBarChart;
	}

	public String getDataBarChart() {
		return dataBarChart;
	}

	public void setDataBarChart(String dataBarChart) {
		this.dataBarChart = dataBarChart;
	}

	public String getTilePieChart() {
		return tilePieChart;
	}

	public void setTilePieChart(String tilePieChart) {
		this.tilePieChart = tilePieChart;
	}

	public String getDataPieChart() {
		return dataPieChart;
	}

	public void setDataPieChart(String dataPieChart) {
		this.dataPieChart = dataPieChart;
	}

	public ChartDataProvider() {
		tilePieChart = this.readProperties("chart.title");
		tileBarChart = this.readProperties("chart.barTitle");
		tileLineChart = this.readProperties("chart.lineTitle");
		dao = new OfferDAO();
		getaAllOffer();
		retrievePieData();
		loadDataBarChart();
		loadDataLineChart();
		dataListObj = new ArrayList<>();
		dataListObjCatalog = new ArrayList<>();
		loadDataListObj();
		loadDataListObjCatalog();
	}

	private void loadDataListObj() {
		dataListObj.add(getOffer());
		dataListObj.add(getMainOffer());
		dataListObj.add(getNormalOffer());
		dataListObj.add(getTemplateOffer());
		dataListObj.add(getCompiledOffer());
		dataListObj.add(getEvent());
		dataListObj.add(getActionType());
		dataListObj.add(getAction());
		dataListObj.add(getDynamicReserve());
		dataListObj.add(getSortPC());
		dataListObj.add(getPC());
		dataListObj.add(getBlock());
		dataListObj.add(getRateTable());
		dataListObj.add(getDecisionTable());
		dataListObj.add(getNormalizer());
	}

	private void loadDataListObjCatalog() {
		dataListObjCatalog.add(getBalance());
		dataListObjCatalog.add(getAcmBalance());
		dataListObjCatalog.add(getParameter());
		dataListObjCatalog.add(countReserveInfo());
		dataListObjCatalog.add(countTriggerOcs());
//		dataListObjCatalog.add(countTriggerDestination());
//		dataListObjCatalog.add(countTriggerMsg());
		dataListObjCatalog.add(countService());
		dataListObjCatalog.add(countCdrProcessConfig());
		dataListObjCatalog.add(countZoneData());
		dataListObjCatalog.add(countGeoHomeZone());
		dataListObjCatalog.add(countBillingCycleType());
		dataListObjCatalog.add(countUnitType());
		dataListObjCatalog.add(countStateType());
//		dataListObjCatalog.add(countStateGroup());
		dataListObjCatalog.add(countProfilePep());
		dataListObjCatalog.add(countPCCRule());
		dataListObjCatalog.add(countMonitorKey());
		dataListObjCatalog.add(countQos());
	}

	private dataList getOffer() {
		return new dataList(Offer.class.getName().replace(srtReplace, ""), offerAll);
	}

	private dataList getMainOffer() {
		int count = dao.countOfferByType(OfferType.MAIN);
		return new dataList(OfferType.MAIN_NAME, count);
	}

	private dataList getNormalOffer() {
		int count = dao.countOfferByType(OfferType.NORMAL);
		return new dataList(OfferType.NORMAL_NAME, count);
	}

	private dataList getTemplateOffer() {
		int count = dao.countOfferByType(OfferType.TEMPLATE);
		return new dataList(OfferType.TEMPLATE_NAME, count);
	}

	private dataList getCompiledOffer() {
		int count = dao.countOfferByType(OfferType.COMPILED);
		return new dataList(OfferType.COMPILED_NAME, count);
	}

	private dataList getEvent() {
		EventDAO dao = new EventDAO();
		int count = dao.countEvent();
		return new dataList(Event.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getActionType() {
		ActionTypeDAO dao = new ActionTypeDAO();
		int count = dao.countActionType();
		return new dataList(ActionType.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getAction() {
		ActionDAO dao = new ActionDAO();
		int count = dao.countAction();
		return new dataList(Action.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getDynamicReserve() {
		DynamicReserveDAO dao = new DynamicReserveDAO();
		int count = dao.countDynamicReserve();
		return new dataList(DynamicReserve.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getSortPC() {
		SortPriceComponentDAO dao = new SortPriceComponentDAO();
		int count = dao.countSortPriceComponent();
		return new dataList(SortPriceComponent.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getPC() {
		PriceComponentDAO dao = new PriceComponentDAO();
		int count = dao.countPriceComponent();
		return new dataList(PriceComponent.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getBlock() {
		BlockDAO dao = new BlockDAO();
		int count = dao.countBlock();
		return new dataList(Block.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getRateTable() {
		RateTableDAO dao = new RateTableDAO();
		int count = dao.countRateTable();
		return new dataList(RateTable.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getDecisionTable() {
		DecisionTableDAO dao = new DecisionTableDAO();
		int count = dao.countDecisionTable();
		return new dataList(DecisionTable.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getNormalizer() {
		NormalizerDAO dao = new NormalizerDAO();
		int count = dao.countNormalizer();
		return new dataList(Normalizer.class.getName().replace(srtReplace, ""), count);
	}

	private dataList getBalance() {
		BalTypeDAO dao = new BalTypeDAO();
		int count = dao.countBalance();
		return new dataList("Balance", count);
	}

	private dataList getAcmBalance() {
		MapAcmbalBalDAO dao = new MapAcmbalBalDAO();
		int count = dao.countAcmBalance();
		return new dataList("Acm Balance", count);
	}

	private dataList getParameter() {
		ParameterDAO dao = new ParameterDAO();
		int count = dao.countParameter();
		return new dataList(Parameter.class.getName().replace(srtReplace, ""), count);
	}

	private dataList countReserveInfo() {
		ReserveInfoDAO dao = new ReserveInfoDAO();
		int count = dao.countReserveInfo();
		return new dataList(ReserveInfo.class.getName().replace(srtReplace, ""), count);
	}

	private dataList countTriggerOcs() {
		TriggerOcsDAO dao = new TriggerOcsDAO();
		int count = dao.countTriggerOcs();
		TriggerMsgDAO dao1 = new TriggerMsgDAO();
		int count1 = dao1.countTriggerMsg();
		TriggerDestinationDAO dao2 = new TriggerDestinationDAO();
		int count2 = dao2.countTriggerDestination();
		return new dataList("Trigger", count+count1+count2);
	}

	private dataList countTriggerDestination() {
		TriggerDestinationDAO dao = new TriggerDestinationDAO();
		int count = dao.countTriggerDestination();
		return new dataList(TriggerDestination.class.getName().replace(srtReplace, ""), count);
	}

	private dataList countTriggerMsg() {
		TriggerMsgDAO dao = new TriggerMsgDAO();
		int count = dao.countTriggerMsg();
		return new dataList(TriggerMsg.class.getName().replace(srtReplace, ""), count);
	}

	private dataList countService() {
		ServiceDAO dao = new ServiceDAO();
		int count = dao.countService();
		return new dataList(Service.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countCdrProcessConfig() {
		CdrProcessConfigDAO dao = new CdrProcessConfigDAO();
		int count = dao.countCdrProcessConfig();
		return new dataList(CdrProcessConfig.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countZoneData() {
		ZoneDataDAO dao = new ZoneDataDAO();
		int count = dao.countZoneData();
		return new dataList(ZoneData.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countGeoHomeZone() {
		GeoHomeZoneDAO dao = new GeoHomeZoneDAO();
		int count = dao.countGeoHomeZone();
		return new dataList(GeoHomeZone.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countBillingCycleType() {
		BillingCycleTypeDAO dao = new BillingCycleTypeDAO();
		int count = dao.countBillingCycleType();
		return new dataList(BillingCycleType.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countUnitType() {
		UnitTypeDAO dao = new UnitTypeDAO();
		int count = dao.countUnitType();
		return new dataList(UnitType.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countStateType() {
		StateTypeDAO dao = new StateTypeDAO();
		int count = dao.countStateType();
		StateGroupDAO dao1 = new StateGroupDAO();
		int count1 = dao1.countStateGroup();
		return new dataList("State Set", count+count1);
	}
	
	private dataList countStateGroup() {
		StateGroupDAO dao = new StateGroupDAO();
		int count = dao.countStateGroup();
		return new dataList(StateGroup.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countProfilePep() {
		ProfilePepDAO dao = new ProfilePepDAO();
		int count = dao.countProfilePep();
		return new dataList(ProfilePep.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countPCCRule() {
		PCCRuleDAO dao = new PCCRuleDAO();
		int count = dao.countPCCRule();
		return new dataList(PccRule.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countMonitorKey() {
		MonitorKeyDAO dao = new MonitorKeyDAO();
		int count = dao.countMonitorKey();
		return new dataList(MonitorKey.class.getName().replace(srtReplace, ""), count);
	}
	
	private dataList countQos() {
		QosDAO dao = new QosDAO();
		int count = dao.countQos();
		return new dataList(Qos.class.getName().replace(srtReplace, ""), count);
	}
	
	

	private void retrievePieData() {
		List<dataChart> lstOffer = new ArrayList<dataChart>();
		lstOffer.add(getOfferInActive());
		lstOffer.add(getOfferActive());
		lstOffer.add(getOfferSuspend());
		lstOffer.add(getOfferRemoved());
		lstOffer.add(getOfferTesting());
		dataPieChart = new Gson().toJson(lstOffer);
	}

	private void loadDataBarChart() {
		List<Integer> lstInt = new ArrayList<>();
		List<String> lstStr = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			String name = "Bar " + i;
			lstStr.add(name);
			lstInt.add(100 * i);
		}
		categoriesBarChart = new Gson().toJson(lstStr);
		dataBarChart = new Gson().toJson(lstInt);
	}

	private void loadDataLineChart() {
		List<dataLineChart> lstDataLineChart = new ArrayList<>();
		List<Integer> lstInt = new ArrayList<>();

		String name1 = "Subs";
		lstInt = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			lstInt.add(100 * i * 2);
		}
		lstDataLineChart.add(new dataLineChart(name1, lstInt));
		String name2 = "Cust";
		lstInt = new ArrayList<>();
		for (int i = 1; i < 13; i++) {
			lstInt.add((105 * (i - 1)) - (20 * i));
		}
		lstDataLineChart.add(new dataLineChart(name2, lstInt));
		dataLineChart = new Gson().toJson(lstDataLineChart);
	}

	private int getaAllOffer() {
		offerAll = dao.countAllOffer();
		return offerAll;
	}

	private dataChart getOfferInActive() {
		dataChart chart = null;
		int count = dao.countOfferByState(ContantsUtil.OfferState.IN_ACTIVE);
		double y = (Double.parseDouble(count + "") / Double.parseDouble(offerAll + "")) * 100;
		chart = new dataChart(ContantsUtil.OfferState.IN_ACTIVE_NAME + "(" + count + ")", roundToDecimals(y, 2));
		return chart;
	}

	private dataChart getOfferActive() {
		dataChart chart = null;
		int count = dao.countOfferByState(ContantsUtil.OfferState.ACTIVE);
		double y = (Double.parseDouble(count + "") / Double.parseDouble(offerAll + "")) * 100;
		chart = new dataChart(ContantsUtil.OfferState.ACTIVE_NAME + "(" + count + ")", roundToDecimals(y, 2));
		return chart;
	}

	private dataChart getOfferRemoved() {
		dataChart chart = null;
		int count = dao.countOfferByState(ContantsUtil.OfferState.REMOVED);
		double y = (Double.parseDouble(count + "") / Double.parseDouble(offerAll + "")) * 100;
		chart = new dataChart(ContantsUtil.OfferState.REMOVED_NAME + "(" + count + ")", roundToDecimals(y, 2));
		return chart;
	}

	private dataChart getOfferSuspend() {
		dataChart chart = null;
		int count = dao.countOfferByState(ContantsUtil.OfferState.SUSPEND);
		double y = (Double.parseDouble(count + "") / Double.parseDouble(offerAll + "")) * 100;
		chart = new dataChart(ContantsUtil.OfferState.SUSPEND_NAME + "(" + count + ")", roundToDecimals(y, 2));
		return chart;
	}

	private dataChart getOfferTesting() {
		dataChart chart = null;
		int count = dao.countOfferByState(ContantsUtil.OfferState.TESTING);
		double y = (Double.parseDouble(count + "") / Double.parseDouble(offerAll + "")) * 100;
		chart = new dataChart(ContantsUtil.OfferState.TESTING_NAME + "(" + count + ")", roundToDecimals(y, 2));
		return chart;
	}

	public double roundToDecimals(double d, int c) {
		int temp = (int) (d * Math.pow(10, c));
		return ((double) temp) / Math.pow(10, c);
	}

	public class dataChart {
		private String name;
		private double y;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public dataChart() {
		}

		public dataChart(String name, double y) {
			super();
			this.name = name;
			this.y = y;
		}

	}

	public class dataLineChart {
		private String name;
		private List<Integer> data;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Integer> getData() {
			return data;
		}

		public void setData(List<Integer> data) {
			this.data = data;
		}

		public dataLineChart() {
		}

		public dataLineChart(String name, List<Integer> data) {
			super();
			this.name = name;
			this.data = data;
		}

	}

	public class dataList {
		private String name;
		private int count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public dataList() {
		}

		public dataList(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}

	}
}

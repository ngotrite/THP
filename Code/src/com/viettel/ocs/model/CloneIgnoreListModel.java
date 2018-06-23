package com.viettel.ocs.model;

import java.util.HashMap;

import com.viettel.ocs.entity.Action;
import com.viettel.ocs.entity.Block;
import com.viettel.ocs.entity.DecisionTable;
import com.viettel.ocs.entity.DynamicReserve;
import com.viettel.ocs.entity.Normalizer;
import com.viettel.ocs.entity.PriceComponent;
import com.viettel.ocs.entity.RateTable;
import com.viettel.ocs.entity.SortPriceComponent;

public class CloneIgnoreListModel {
	private HashMap<Long, Long> actions;
	private HashMap<Long, Long> priceComponents;
	private HashMap<Long, Long> blocks;
	private HashMap<Long, Long> rateTables;
	private HashMap<Long, Long> decisionTables;
	private HashMap<Long, Long> normalizers;
	private HashMap<Long, Long> dynamicReserves;
	private HashMap<Long, Long> sortPCs;

	public CloneIgnoreListModel() {
		super();
		this.actions = new HashMap<Long, Long>();
		this.priceComponents = new HashMap<Long, Long>();
		this.blocks = new HashMap<Long, Long>();
		this.rateTables = new HashMap<Long, Long>();
		this.decisionTables = new HashMap<Long, Long>();
		this.normalizers = new HashMap<Long, Long>();
		this.dynamicReserves = new HashMap<Long, Long>();
		this.sortPCs = new HashMap<Long, Long>();
	}

	public HashMap<Long, Long> getActions() {
		return actions;
	}

	public void setActions(HashMap<Long, Long> actions) {
		this.actions = actions;
	}

	public HashMap<Long, Long> getPriceComponents() {
		return priceComponents;
	}

	public void setPriceComponents(HashMap<Long, Long> priceComponents) {
		this.priceComponents = priceComponents;
	}

	public HashMap<Long, Long> getBlocks() {
		return blocks;
	}

	public void setBlocks(HashMap<Long, Long> blocks) {
		this.blocks = blocks;
	}

	public HashMap<Long, Long> getRateTables() {
		return rateTables;
	}

	public void setRateTables(HashMap<Long, Long> rateTables) {
		this.rateTables = rateTables;
	}

	public HashMap<Long, Long> getDecisionTables() {
		return decisionTables;
	}

	public void setDecisionTables(HashMap<Long, Long> decisionTables) {
		this.decisionTables = decisionTables;
	}

	public HashMap<Long, Long> getNormalizers() {
		return normalizers;
	}

	public void setNormalizers(HashMap<Long, Long> normalizers) {
		this.normalizers = normalizers;
	}

	public HashMap<Long, Long> getDynamicReserves() {
		return dynamicReserves;
	}

	public void setDynamicReserves(HashMap<Long, Long> dynamicReserves) {
		this.dynamicReserves = dynamicReserves;
	}

	public HashMap<Long, Long> getSortPCs() {
		return sortPCs;
	}

	public void setSortPC(HashMap<Long, Long> sortPC) {
		this.sortPCs = sortPC;
	}

	public Long isIgnore(Object object) {
		if (object instanceof Action) {
			return existAction((Action) object);
		} else if (object instanceof PriceComponent) {
			return existPriceComponent((PriceComponent) object);
		} else if (object instanceof Block) {
			return existBlock((Block) object);
		} else if (object instanceof RateTable) {
			return existRateTable((RateTable) object);
		} else if (object instanceof DecisionTable) {
			return existDecisionTable((DecisionTable) object);
		} else if (object instanceof Normalizer) {
			return existNormalizer((Normalizer) object);
		} else if (object instanceof DynamicReserve) {
			return existDynamicReserve((DynamicReserve) object);
		} else if (object instanceof SortPriceComponent) {
			return existSortPC((SortPriceComponent) object);
		} else {
			return null;
		}
	}

	private Long existNormalizer(Normalizer normalizer) {
		return normalizers.get(normalizer.getNormalizerId());
	}

	private Long existDecisionTable(DecisionTable decisionTable) {
		return decisionTables.get(decisionTable.getDecisionTableId());
	}

	private Long existRateTable(RateTable rateTable) {
		return rateTables.get(rateTable.getRateTableId());
	}

	private Long existBlock(Block block) {
		return blocks.get(block.getBlockId());
	}

	private Long existAction(Action action) {
		return actions.get(action.getActionId());
	}

	private Long existPriceComponent(PriceComponent priceComponent) {
		return priceComponents.get(priceComponent.getPriceComponentId());
	}

	private Long existDynamicReserve(DynamicReserve dynamicReserve) {
		return dynamicReserves.get(dynamicReserve.getDynamicReserveId());
	}

	private Long existSortPC(SortPriceComponent sortPC) {
		return sortPCs.get(sortPC.getSortPriceComponentId());
	}

}

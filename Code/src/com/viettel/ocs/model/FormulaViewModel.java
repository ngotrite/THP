package com.viettel.ocs.model;

import java.io.Serializable;

import com.viettel.ocs.constant.Normalizer.FormulaType;
import com.viettel.ocs.entity.Formula;
import com.viettel.ocs.entity.UnitType;

@SuppressWarnings("serial")
public class FormulaViewModel implements Serializable {
	private Formula formula;
	private String recipe;
	private boolean applied;
	private Double pattern;

	private Boolean type;
	private Boolean a;
	private Boolean b;
	private Boolean per;

	private Long temType;
	private Long pramType;
	private Double temA;
	private Long pramA;
	private Double temB;
	private Long pramB;
	private Long temPer;
	private Long pramPer;

	private Long rowIndex;
	private UnitType unitType;

	public FormulaViewModel() {
		this.formula = new Formula();
		formula.setA(1L);
		formula.setB(0L);
		formula.setPer(1L);
		formula.setFormulaType((long) FormulaType.NORMAL_TYPE);

		this.type = false;
		this.a = false;
		this.b = false;
		this.per = false;

		this.temType = 0L;
		this.pramType = 0L;
		this.temA = 0D;
		this.pramA = 0L;
		this.temB = 0D;
		this.pramB = 0L;
		this.temPer = 0L;
		this.pramPer = 0L;

		this.applied = false;
		this.pattern = 1D;

	}

	private Double genPattern(Long pre) {
		String pattern = "1";
		for (int i = 0; i < pre; i++) {
			pattern += "0";
		}
		return Double.valueOf(pattern);
	}

	public FormulaViewModel(Formula formula, boolean applied, UnitType unitType) {
		this.formula = formula;
		this.applied = applied;
		this.unitType = unitType;
		if (unitType != null) {
			pattern = genPattern(unitType.getUnitPrecision());
		}
		cal();
	}

	public FormulaViewModel(Formula formula, Long rowIndex, UnitType unitType) {
		this.formula = formula;
		this.rowIndex = rowIndex;
		this.unitType = unitType;
		if (unitType != null) {
			pattern = genPattern(unitType.getUnitPrecision());
		}
		applied = true;
		cal();
	}

	public void cal() {
		if (formula.getTemplateBits() != null) {
			String value = Long.toBinaryString(formula.getTemplateBits());

			StringBuffer bonus = new StringBuffer("");
			for (int i = 0; i < 4 - value.length(); i++) {
				bonus.append("0");
			}
			value = bonus.toString() + value;

			this.type = value.charAt(0) == '1' ? true : false;
			this.a = value.charAt(1) == '1' ? true : false;
			this.b = value.charAt(2) == '1' ? true : false;
			this.per = value.charAt(3) == '1' ? true : false;

			if (type) {
				pramType = formula.getFormulaType();
			} else {
				temType = formula.getFormulaType();
			}
			if (a) {
				pramA = formula.getA();
			} else {
				if (unitType != null) {
					temA = formula.getA() / (double) unitType.getDisplayRate();
					temA = (double) (Math.round(temA * pattern) / pattern);
				} else {
					temA = formula.getA().doubleValue();
				}
			}
			if (b) {
				pramB = formula.getB();
			} else {
				if (unitType != null) {
					temB = formula.getB() / (double) unitType.getDisplayRate();
					temB = (double) Math.round(temB * pattern) / pattern;
				} else {
					temB = formula.getB().doubleValue();
				}

			}
			if (per) {
				pramPer = formula.getPer();
			} else {
				temPer = formula.getPer();
			}

		} else {
			this.per = false;
			this.b = false;
			this.a = false;
			this.type = false;
		}
	}

	public void doChooseValue() {
		setFormulaType();
		if (a) {
			formula.setA(pramA);
		} else {
			if (unitType != null) {
				formula.setA(Math.round((temA * unitType.getDisplayRate())));
			} else {
				formula.setA(temA.longValue());
			}
		}
		if (b) {
			formula.setB(pramB);
		} else {
			if (unitType != null) {
				formula.setB(Math.round((temB * unitType.getDisplayRate())));
			} else {
				formula.setB(temB.longValue());
			}
		}
		if (per) {
			formula.setPer(pramPer);
		} else {
			formula.setPer(temPer);
		}
	}

	public void setFormulaType() {
		if (type) {
			formula.setFormulaType(pramType);
		} else {
			formula.setFormulaType(temType);
		}
	}

	public void convertToTemplateBits() {
		StringBuffer value = new StringBuffer("");
		value.append(type ? "1" : "0");
		value.append(a ? "1" : "0");
		value.append(b ? "1" : "0");
		value.append(per ? "1" : "0");
		formula.setTemplateBits(Long.valueOf(value.toString(), 2));
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public String getRecipe() {
		if (formula.getA() != null && formula.getB() != null && formula.getPer() != null) {
			return this.formula.getA() + "x + " + this.formula.getB() + "; per = " + this.getPer();
		} else {
			return "";
		}
	}

	public void setRecipe(String recipe) {
		this.recipe = recipe;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public Boolean getA() {
		return a;
	}

	public void setA(Boolean a) {
		this.a = a;
	}

	public Boolean getB() {
		return b;
	}

	public void setB(Boolean b) {
		this.b = b;
	}

	public Boolean getPer() {
		return per;
	}

	public void setPer(Boolean per) {
		this.per = per;
	}

	public Long getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(Long rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Double getTemA() {
		return temA;
	}

	public void setTemA(Double temA) {
		this.temA = temA;
	}

	public Long getPramA() {
		return pramA;
	}

	public void setPramA(Long pramA) {
		this.pramA = pramA;
	}

	public Long getPramType() {
		return pramType;
	}

	public void setPramType(Long pramType) {
		this.pramType = pramType;
	}

	public Long getTemType() {
		return temType;
	}

	public void setTemType(Long temType) {
		this.temType = temType;
	}

	public Double getTemB() {
		return temB;
	}

	public void setTemB(Double temB) {
		this.temB = temB;
	}

	public Long getPramB() {
		return pramB;
	}

	public void setPramB(Long pramB) {
		this.pramB = pramB;
	}

	public Long getTemPer() {
		return temPer;
	}

	public void setTemPer(Long temPer) {
		this.temPer = temPer;
	}

	public Long getPramPer() {
		return pramPer;
	}

	public void setPramPer(Long pramPer) {
		this.pramPer = pramPer;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
		if (unitType != null) {
			pattern = genPattern(unitType.getUnitPrecision());
		} else {
			pattern = 1D;
		}
	}

	@Override
	public FormulaViewModel clone() throws CloneNotSupportedException {
		FormulaViewModel formulaViewModel = new FormulaViewModel(formula.clone(), true, this.unitType);
		formulaViewModel.setApplied(false);
		return formulaViewModel;
	}

}

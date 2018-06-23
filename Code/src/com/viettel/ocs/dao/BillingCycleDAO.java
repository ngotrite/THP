package com.viettel.ocs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.viettel.ocs.db.HibernateUtil;
import com.viettel.ocs.db.Operator;
import com.viettel.ocs.entity.BillingCycle;
import com.viettel.ocs.entity.BillingCycleType;

public class BillingCycleDAO extends BaseDAO<BillingCycle> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<BillingCycle> findBillingCycleByBillingCycleType(Long billingCycleTypeId) {
		List<BillingCycle> lst = null;
		String[] cols = { "billingCycleTypeId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { billingCycleTypeId, getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		return lst;
	}

	public boolean delListBillingCycle(List<BillingCycle> listBillingCycle) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		List<Long> listBillingCycleId = new ArrayList<Long>();
		if (listBillingCycle.size() > 0) {
			for (BillingCycle billingCycle : listBillingCycle) {
				listBillingCycleId.add(billingCycle.getBillingCycleId());
			}
		}
		try {
			StringBuffer hql = new StringBuffer("DELETE FROM BillingCycle b");
			hql.append(" WHERE b.domainId = :domainId");
			hql.append(" AND b.billingCycleId IN (:listBillingCycleId)");

			Query<?> query = session.createQuery(hql.toString());
			query.setParameter("domainId", getDomainId());
			query.setParameterList("listBillingCycleId", listBillingCycleId);
			query.executeUpdate();
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}

	public List<BillingCycle> findBillingCycleByBillingCycleTypeLimit10(Long billingCycleTypeId) {
		List<BillingCycle> lst = new ArrayList<BillingCycle>();
		String[] cols = { "billingCycleTypeId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { billingCycleTypeId, getDomainId() };
		lst = findByConditions(cols, operators, values, "billingCycleId DESC");
		return lst;
	}

	@Override
	protected Class<BillingCycle> getEntityClass() {
		return BillingCycle.class;
	}

	public int saveBillingCycleType(BillingCycleType billingCycleTypeUI, Date fromDate, Date toDate) {
		Session session = HibernateUtil.getOpenSession();
		session.getTransaction().begin();
		try {
			// Save Billing Cycle
			Calendar calendarFromDate = Calendar.getInstance();
			calendarFromDate.setTime(fromDate);
			Calendar calendarToDate = Calendar.getInstance();
			calendarToDate.setTime(toDate);
			if (calendarFromDate.getTime().before(calendarToDate.getTime())) {
				int checkLoop = 0;
				while (calendarFromDate.getTime().before(calendarToDate.getTime())) {
					BillingCycle billingCycle = new BillingCycle();
					billingCycle.setCycleBeginDate(calendarFromDate.getTime());
					switch (billingCycleTypeUI.getCalcUnitId().intValue()) {
					case 3:
						calendarFromDate.add(Calendar.DATE,
								Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						break;
					case 4:
						int isDayWeek = calendarFromDate.get(Calendar.DAY_OF_WEEK);
						if (isDayWeek != billingCycleTypeUI.getFromOfDay()) {
							int time = isDayWeek - billingCycleTypeUI.getFromOfDay();
							calendarFromDate.add(Calendar.DATE, (time > 0 ? 7 - time : -1 * time));
							billingCycle.setCycleBeginDate(calendarFromDate.getTime());
							calendarFromDate.add(Calendar.DATE,
									7 * Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						} else {
							calendarFromDate.add(Calendar.DATE,
									7 * Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						}
						break;
					case 5:
						int isMonday = calendarFromDate.get(Calendar.DAY_OF_WEEK);
						if (isMonday != 2) {
							if ((isMonday - 2) > 0) {
								calendarFromDate.add(Calendar.DATE, (7 - (isMonday - 2)));
							} else {
								calendarFromDate.add(Calendar.DATE, 1);
							}
							billingCycle.setCycleBeginDate(calendarFromDate.getTime());
							calendarFromDate.add(Calendar.DATE,
									7 * Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						} else {
							calendarFromDate.add(Calendar.DATE,
									7 * Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						}
						break;
					case 6:

						calendarFromDate = setDataBeginDate(calendarFromDate, billingCycleTypeUI.getFromOfDay());
						billingCycle.setCycleBeginDate(calendarFromDate.getTime());
						calendarFromDate.add(Calendar.MONTH,
								Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						int isDayMonthBegin = calendarFromDate.get(Calendar.DAY_OF_MONTH);
						int totalDayOfMonth = calendarFromDate.getActualMaximum(Calendar.DAY_OF_MONTH);

						if (isDayMonthBegin > billingCycleTypeUI.getFromOfDay()) {
							calendarFromDate.set(calendarFromDate.get(Calendar.YEAR),
									calendarFromDate.get(Calendar.MONTH), billingCycleTypeUI.getFromOfDay(),
									calendarFromDate.get(Calendar.HOUR), calendarFromDate.get(Calendar.MINUTE),
									calendarFromDate.get(Calendar.SECOND));
						} else {
							if (totalDayOfMonth < billingCycleTypeUI.getFromOfDay()) {
								calendarFromDate.set(calendarFromDate.get(Calendar.YEAR),
										calendarFromDate.get(Calendar.MONTH), totalDayOfMonth,
										calendarFromDate.get(Calendar.HOUR), calendarFromDate.get(Calendar.MINUTE),
										calendarFromDate.get(Calendar.SECOND));
							} else {
								calendarFromDate.set(calendarFromDate.get(Calendar.YEAR),
										calendarFromDate.get(Calendar.MONTH), billingCycleTypeUI.getFromOfDay(),
										calendarFromDate.get(Calendar.HOUR), calendarFromDate.get(Calendar.MINUTE),
										calendarFromDate.get(Calendar.SECOND));
							}
						}
						break;
					case 7:
						int isFirstMonth = calendarFromDate.get(Calendar.DAY_OF_MONTH);
						if (isFirstMonth != 1) {
							calendarFromDate.add(Calendar.MONTH, 1);
							calendarFromDate.add(Calendar.DATE, -(isFirstMonth - 1));
							billingCycle.setCycleBeginDate(calendarFromDate.getTime());
							calendarFromDate.add(Calendar.MONTH,
									Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						} else {
							calendarFromDate.add(Calendar.MONTH,
									Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						}
						break;
					case 8:
						int isFirstYear = calendarFromDate.get(Calendar.DAY_OF_YEAR);
						if (isFirstYear != 1) {
							calendarFromDate.add(Calendar.YEAR, 1);
							calendarFromDate.set(Calendar.MONTH, 0);
							calendarFromDate.set(Calendar.DAY_OF_MONTH, 1);
							billingCycle.setCycleBeginDate(calendarFromDate.getTime());
							calendarFromDate.add(Calendar.YEAR,
									Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
						} else {
							calendarFromDate.add(Calendar.YEAR,
									Integer.parseInt(String.valueOf(billingCycleTypeUI.getQuantity())));
							calendarFromDate.set(Calendar.MONTH, 0);
							calendarFromDate.set(Calendar.DAY_OF_MONTH, 1);
						}
						break;

					default:
						break;
					}
					if (calendarFromDate.getTime().before(calendarToDate.getTime())
							|| calendarFromDate.getTime().equals(calendarToDate.getTime())) {
						billingCycle.setCycleEndDate(calendarFromDate.getTime());
						billingCycle.setBillingCycleTypeId(billingCycleTypeUI.getBillingCycleTypeId());
						billingCycle.setState(0L);
						billingCycle.setDomainId(getDomainId());
						checkLoop += 1;
						session.saveOrUpdate(billingCycle);
					}
					if (checkLoop == 0) {
						return 2;
					}
				}
			} else

			{
				return 3;
			}
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			return 4;

		} finally {
			session.close();
		}
	}

	public Calendar setDataBeginDate(Calendar cal, int fromOfDay) {
		int isDayMonthBegin = cal.get(Calendar.DAY_OF_MONTH);
		int totalDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		if (isDayMonthBegin <= fromOfDay) {
			if (totalDayOfMonth <= fromOfDay) {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), totalDayOfMonth, cal.get(Calendar.HOUR),
						cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			} else {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), fromOfDay, cal.get(Calendar.HOUR),
						cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}
		} else {
			cal.add(Calendar.MONTH, 1);
			totalDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (totalDayOfMonth <= fromOfDay) {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), totalDayOfMonth, cal.get(Calendar.HOUR),
						cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			} else {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), fromOfDay, cal.get(Calendar.HOUR),
						cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}
		}

		return cal;
	}

	public boolean checkBillingCycle(BillingCycleType item) {
		List<BillingCycle> lst = null;
		String[] cols = { "billingCycleTypeId", "domainId" };
		Operator[] operators = { Operator.EQ, Operator.EQ };
		Object[] values = { item.getBillingCycleTypeId(), getDomainId() };
		lst = findByConditions(cols, operators, values, "");
		if (lst != null && lst.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean updateStatusBillingCycle(List<BillingCycle> listBillingCycleSelection, long billingCycleTypeId) {
		Session session = HibernateUtil.getOpenSession();
		List<BillingCycle> billingCycles = findBillingCycleByBillingCycleTypeLimit10(billingCycleTypeId);

		session.getTransaction().begin();
		try {
			long activeId = 0L;

			for (BillingCycle billingCycle : listBillingCycleSelection) {
				if (billingCycle.getState() == 0L) {
					activeId = billingCycle.getBillingCycleId();
					break;
				}
			}

			for (BillingCycle billingCycle : billingCycles) {
				if (billingCycle.getBillingCycleId() == activeId) {
					billingCycle.setState(1L);
					session.update(billingCycle);
				} else if (billingCycle.getState() == 1L) {
					billingCycle.setState(0L);
					session.update(billingCycle);
				}
			}

			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			session.getTransaction().rollback();
			getLogger().error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
	}
}

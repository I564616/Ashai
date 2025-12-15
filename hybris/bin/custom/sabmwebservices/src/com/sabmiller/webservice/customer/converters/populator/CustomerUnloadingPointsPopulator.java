/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;


import com.sabmiller.facades.b2bunit.data.UnloadingPoint;
import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerUnloadingPointsPopulator implements Populator<Customer, B2BUnitData>
{

	private static final Logger LOG = Logger.getLogger(CustomerUnloadingPointsPopulator.class);

	enum Day
	{

		MON("1"), TUE("2"), WED("3"), THU("4"), FRI("5"), SAT("6"), SUN("7");

		private Day(final String code)
		{
			this.code = code;
		}

		private final String code;
	}

	protected boolean isValid(final String time)
	{
		return !CustomerImportConstants.UNLOADING_POINT_INVALID_VALUE.getCode().equals(time);
	}

	protected void addIfValid(final Set<String> timings, final String time)
	{
		if (isValid(time))
		{
			timings.add(time);
		}
	}

	protected void populateDayTimesMapping(final Map<String, Set<String>> dayTimeMap, final Day day, final String... timings)
	{
		final Set<String> times = new HashSet<String>();
		for (final String eachTime : timings)
		{
			addIfValid(times, eachTime);
		}
		if (!times.isEmpty())
		{
			dayTimeMap.put(day.code, times);
		}
	}


	@Override
	public void populate(final Customer source, final B2BUnitData target) throws ConversionException
	{
		final List<UnloadingPoint> unloadingPoints = new ArrayList<UnloadingPoint>();


		for (final Customer.UnloadingPoint unloadingPointWs : ListUtils.emptyIfNull(source.getUnloadingPoint()))
		{
			LOG.debug("unloading code : " + unloadingPointWs.getUnldingPoint() + " , isDefault : "
					+ unloadingPointWs.getUnldingPointDefault());

			final Map<String, Set<String>> dayTimeMap = new HashMap<String, Set<String>>();

			populateDayTimesMapping(dayTimeMap, Day.SUN, unloadingPointWs.getGoodsReceiveHrSunAm1(),
					unloadingPointWs.getGoodsReceiveHrSunAm2(), unloadingPointWs.getGoodsReceiveHrSunPm1(),
					unloadingPointWs.getGoodsReceiveHrSunPm2());

			populateDayTimesMapping(dayTimeMap, Day.MON, unloadingPointWs.getGoodsReceiveHrsMonAm1(),
					unloadingPointWs.getGoodsReceiveHrsMonAm2(), unloadingPointWs.getGoodsReceiveHrsMonPm1(),
					unloadingPointWs.getGoodsReceiveHrsMonPm2());

			populateDayTimesMapping(dayTimeMap, Day.TUE, unloadingPointWs.getGoodsReceiveHrsTuesAm1(),
					unloadingPointWs.getGoodsReceiveHrsTuesAm2(), unloadingPointWs.getGoodsReceiveHrsTuesPm1(),
					unloadingPointWs.getGoodsReceiveHrsTuesPm2());

			populateDayTimesMapping(dayTimeMap, Day.WED, unloadingPointWs.getGoodsReceiveHrsWedAm1(),
					unloadingPointWs.getGoodsReceiveHrsWedAm2(), unloadingPointWs.getGoodsReceiveHrsWedPm1(),
					unloadingPointWs.getGoodsReceiveHrsWedPm2());

			populateDayTimesMapping(dayTimeMap, Day.THU, unloadingPointWs.getGoodsReceiveHrsThursAm1(),
					unloadingPointWs.getGoodsReceiveHrsThursAm2(), unloadingPointWs.getGoodsReceiveHrsThursPm1(),
					unloadingPointWs.getGoodsReceiveHrsThursPm2());

			populateDayTimesMapping(dayTimeMap, Day.FRI, unloadingPointWs.getGoodsReceiveHrsFriAm1(),
					unloadingPointWs.getGoodsReceiveHrsFriAm2(), unloadingPointWs.getGoodsReceiveHrsFriPm1(),
					unloadingPointWs.getGoodsReceiveHrsFriPm2());

			populateDayTimesMapping(dayTimeMap, Day.SAT, unloadingPointWs.getGoodsReceiveHrSatAm1(),
					unloadingPointWs.getGoodsReceiveHrSatAm2(), unloadingPointWs.getGoodsReceiveHrSatPm1(),
					unloadingPointWs.getGoodsReceiveHrSatPm2());

			final UnloadingPoint unloadingPoint = new UnloadingPoint();
			unloadingPoint.setCode(unloadingPointWs.getUnldingPoint());
			unloadingPoint.setMap(dayTimeMap);
			unloadingPoints.add(unloadingPoint);
			if (CustomerImportConstants.UNLOADING_POINT_DEFAULT_VALUE.getCode().equals(unloadingPointWs.getUnldingPointDefault()))
			{
				LOG.debug("Setting the Default Unloading point >>>>>>>>>>>>>");
				target.setDefaultUnloadingPoint(unloadingPoint);
			}
		}
		target.setUnloadingPoints(unloadingPoints);
	}
}

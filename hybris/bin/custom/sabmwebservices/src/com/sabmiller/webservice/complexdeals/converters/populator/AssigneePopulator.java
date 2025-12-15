/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.complexdeals.data.ComplexDealAssignmentData;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.webservice.complexdeals.DealCondition;
import com.sabmiller.webservice.complexdeals.DealCondition.Assignments;


/**
 * @author joshua.a.antony
 */
public class AssigneePopulator implements Populator<DealCondition, ComplexDealData>
{
	private static final Logger LOG = LoggerFactory.getLogger(AssigneePopulator.class);

	@Override
	public void populate(final DealCondition source, final ComplexDealData target) throws ConversionException
	{
		final List<ComplexDealAssignmentData> assignments = new ArrayList<ComplexDealAssignmentData>();
		for (final Assignments eachAssignment : CollectionUtils.emptyIfNull(source.getAssignments()))
		{
			final ComplexDealAssignmentData a = new ComplexDealAssignmentData();
			a.setBanner(eachAssignment.getBanner());
			a.setCustomer(eachAssignment.getCustomer());
			a.setCustomerGroup(eachAssignment.getCustomerGroup());
			a.setDistributionChannel(eachAssignment.getDistributionChannel());
			a.setDivision(eachAssignment.getDivision());
			a.setExclusion(BooleanUtils.toBoolean(eachAssignment.isExclusion()));
			a.setPlant(eachAssignment.getPlant());
			a.setPriceGroup(eachAssignment.getPriceGroup());
			a.setPrimaryBanner(eachAssignment.getPrimaryBanner());
			a.setSalesGroup(eachAssignment.getSalesGroup());
			a.setSalesOffice(eachAssignment.getSalesOffice());
			a.setSalesOrganization(eachAssignment.getSalesOrganization());
			a.setSequenceNumber(SabmStringUtils.toInt(eachAssignment.getSequenceNumber()) + "");
			a.setSubBanner(eachAssignment.getSubBanner());
			a.setSubChannel(eachAssignment.getSubChannel());

			assignments.add(a);

			//LOG.info("In populate(). Added assignment : [{}] to the list ", ReflectionToStringBuilder.toString(a));
		}

		target.setAssignments(assignments);
	}
}

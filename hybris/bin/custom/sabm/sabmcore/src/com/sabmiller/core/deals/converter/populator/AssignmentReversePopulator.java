/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.complexdeals.data.ComplexDealAssignmentData;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import org.apache.commons.collections4.CollectionUtils;


/**
 * @author joshua.a.antony
 *
 */
public class AssignmentReversePopulator implements Populator<ComplexDealData, DealModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(AssignmentReversePopulator.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ComplexDealData source, final DealModel target) throws ConversionException
	{
		final List<DealAssigneeModel> assigneesList = new ArrayList<>();
		Function<Void,DealAssigneeModel> dealAssigneeProvider = createDealAssigneeProvider(target);

		for (final ComplexDealAssignmentData eachAssignment : source.getAssignments())
		{
			final DealAssigneeModel assignee = dealAssigneeProvider.apply(null);
			assignee.setB2bUnit(eachAssignment.getCustomer());
			assignee.setBanner(eachAssignment.getBanner());
			assignee.setCustomerGroup(eachAssignment.getCustomerGroup());
			assignee.setDistributionChannel(eachAssignment.getDistributionChannel());
			assignee.setDivision(eachAssignment.getDivision());
			assignee.setPlant(eachAssignment.getPlant());
			assignee.setPriceGroup(eachAssignment.getPriceGroup());
			assignee.setPrimaryBanner(eachAssignment.getPrimaryBanner());
			assignee.setSalesGroup(eachAssignment.getSalesGroup());
			assignee.setSalesOffice(eachAssignment.getSalesOffice());
			assignee.setSalesOrg(eachAssignment.getSalesOrganization());
			assignee.setSubBanner(eachAssignment.getSubBanner());
			assignee.setSubChannel(eachAssignment.getSubChannel());
			assignee.setExclude(eachAssignment.isExclusion());

			modelService.save(assignee);

			assigneesList.add(assignee);
		}

		target.setAssignees(assigneesList);

		LOG.info("After populating, assignees : {},", ReflectionToStringBuilder.toString(assigneesList));
	}


	/**
	 * Reused DealAssignee instead of  creating a new one if it's possible.
	 * @param target
	 * @return
	 */
	protected Function<Void,DealAssigneeModel> createDealAssigneeProvider(final DealModel target){

		if(modelService.isNew(target) || CollectionUtils.isEmpty(target.getAssignees())){
			return (v)->modelService.create(DealAssigneeModel.class);
		}

		final Queue<DealAssigneeModel> queue = new LinkedList<>(target.getAssignees());

		return (v)->{
			final DealAssigneeModel dealAssignee = queue.poll();
			if(dealAssignee != null){
				return dealAssignee;
			}

			return modelService.create(DealAssigneeModel.class);
		};
	}

}

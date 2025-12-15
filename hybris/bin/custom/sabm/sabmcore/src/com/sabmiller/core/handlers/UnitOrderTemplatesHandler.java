/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.SabmOrderTemplateDao;


/**
 * The Class UnitOrderTemplatesHandler to handle the dynamic attribute "orderTemplates" of the @B2BUnitModel.
 */
public class UnitOrderTemplatesHandler implements DynamicAttributeHandler<List<SABMOrderTemplateModel>, B2BUnitModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(UnitOrderTemplatesHandler.class);

	/** The order template dao. */
	@Resource(name = "sabmOrderTemplateDao")
	SabmOrderTemplateDao orderTemplateDao;


	/**
	 * getter of dynamic attribute orderTemplates.
	 *
	 * @param b2bUnit
	 *           the unit to fetch the related SABMOrderTemplateModel List
	 * @return a List of @SABMOrderTemplateModel, empty if no mapping is found in the b2bUnit. The set is ordered by
	 *         ProductUOMMappingModel.qtyConversion
	 */
	@Override
	public List<SABMOrderTemplateModel> get(final B2BUnitModel b2bUnit)
	{
		LOG.debug("Searching order templates related to b2bUnit: {}", b2bUnit);
		//Fetching the order templates using the DAO. Return empty list if no template is found.
		return orderTemplateDao.findOrderTemplateByB2BUnit(b2bUnit);
	}

	/**
	 * setter of dynamic attribute unitList, throws exception because this is a dynamic attribute, only to fetch data.
	 *
	 * @param arg0
	 *           the arg0
	 * @param arg1
	 *           the arg1
	 */
	@Override
	public void set(final B2BUnitModel arg0, final List<SABMOrderTemplateModel> arg1)
	{
		throw new UnsupportedOperationException("Set of dynamic attribute 'orderTemplates' of B2BUnitModel is disabled!");
	}
}

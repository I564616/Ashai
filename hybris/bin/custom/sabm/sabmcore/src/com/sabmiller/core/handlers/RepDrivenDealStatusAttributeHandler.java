/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.dao.RepDrivenDealConditionStatusDao;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;


/**
 * The Class RepDrivenDealStatusAttributeHandler.
 *
 * @author a.d.esposito
 */
public class RepDrivenDealStatusAttributeHandler implements DynamicAttributeHandler<RepDrivenDealConditionStatusModel, DealModel>
{
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "repDrivenDealConditionStatusDao")
	private RepDrivenDealConditionStatusDao repDrivenDealConditionStatusDao;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public RepDrivenDealConditionStatusModel get(final DealModel deal)
	{

		final String b2bUnitId = sessionService.getAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA);
		if (StringUtils.isNotEmpty(b2bUnitId))
		{
			return repDrivenDealConditionStatusDao.getRepDrivenDealCondition(deal.getCode(), b2bUnitId);
		}

		return null;
	}

	/**
	 * setter of dynamic attribute unitList, throws exception because this is a dynamic attribute, only to fetch data.
	 *
	 */
	@Override
	public void set(final DealModel deal, final RepDrivenDealConditionStatusModel repDrivenDealStatus)
	{
		throw new UnsupportedOperationException("Setting of dynamic attribute 'repDrivenDealStatus' of DealModel is disabled!");
	}
}

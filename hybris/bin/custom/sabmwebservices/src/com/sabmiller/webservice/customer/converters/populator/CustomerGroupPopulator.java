/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.b2bunit.data.B2BUnitGroup;
import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.Common.CustERPRplctnReqCom;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerGroupPopulator implements Populator<Customer, B2BUnitData>
{

	private final Logger LOG = Logger.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Customer source, final B2BUnitData target)
	{
		if (source.getCommon() != null && source.getCommon().getCustERPRplctnReqCom() != null)
		{
			final B2BUnitGroup group = new B2BUnitGroup();
			final CustERPRplctnReqCom c = source.getCommon().getCustERPRplctnReqCom();
			group.setPrimaryGroupKey(c.getPrimaryGroupKey());
			group.setPrimaryGroupDescription(c.getPrimaryGroupDescription());
			group.setSubGroupKey(c.getSubGroupKey());
			group.setSubGroupDescription(c.getSubGroupDescription());
			group.setGroupKey(c.getGroupKey());
			group.setGroupDescription(c.getGroupDescription());
			group.setSubChannel(c.getSubChannel());
			group.setSubChannelDescription(c.getSubChannelDescription());
			target.setB2bUnitGroup(group);
			
			if (StringUtils.isNotBlank(c.getSalesVolume()))	{
				target.setAutoPayStatus(SabmCoreConstants.SAP_SALES_VOLUME_AUTOPAY_STATUS_MAP.get(c.getSalesVolume()));
			}
		}

		final B2BUnitGroup g = target.getB2bUnitGroup();
		LOG.debug("B2bUnitGroup : " + g);
		if (g != null)
		{
			LOG.debug("primaryGroupKey : " + g.getPrimaryGroupKey() + " , subGroupKey : " + g.getSubGroupKey()
					+ " , primaryGroupDes : " + g.getPrimaryGroupDescription() + " , subGroupDescription : "
					+ g.getSubGroupDescription() + " , groupKey : " + g.getGroupKey());
		}
	}

}

/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.acceleratorfacades.order.populators.AcceleratorConsignmentPopulator;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.util.SabmFeatureUtil;


/**
 * @author marc.f.l.bautista
 *
 */
public class SABMConsignmentPopulator extends AcceleratorConsignmentPopulator
{

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void populate(final ConsignmentModel source, final ConsignmentData target)
	{
		super.populate(source, target);
		if(asahiSiteUtil.isCub())
		{
   		boolean emailSentStatus = false;
   		if (BooleanUtils.isTrue(source.getDispatchNotifEmailSent()))
   		{
   			emailSentStatus = true;
   		}
   
   		target.setDispatchNotifEmailSent(emailSentStatus);
   
   		target.setStatus(sabmFeatureUtil.displayTrackConsignmentStatus(source.getStatus()));
		}

	}

}

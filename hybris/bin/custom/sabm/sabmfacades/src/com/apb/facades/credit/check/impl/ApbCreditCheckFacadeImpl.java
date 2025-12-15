package com.apb.facades.credit.check.impl;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.facades.credit.check.ApbCreditCheckFacade;
import com.apb.integration.credit.check.service.AsahiCreditCheckIntegrationService;
import com.apb.integration.data.ApbCreditCheckData;

import de.hybris.platform.servicelayer.model.ModelService;


public class ApbCreditCheckFacadeImpl implements ApbCreditCheckFacade
{
	@Resource(name = "asahiCreditCheckIntegrationService")
	private AsahiCreditCheckIntegrationService asahiCreditCheckIntegrationService;

	@Resource
	private ModelService modelService;

	@Override
	public boolean getCreditCheck(AsahiB2BUnitModel b2bUnit, double totalPrice)
	{
		boolean isBlockedCredit = Boolean.FALSE;
		boolean isBlockedUser = Boolean.FALSE;
		ApbCreditCheckData creditCheckData = asahiCreditCheckIntegrationService.getCreditCheck(b2bUnit.getAccountNum());
		if (creditCheckData != null)
		{
			if (creditCheckData.isIsBlocked()|| totalPrice > creditCheckData.getCreditRemaining())
			{
				isBlockedUser = Boolean.TRUE;
			}

			b2bUnit.setIsCreditBlock(isBlockedCredit);
			b2bUnit.setCreditRemaining(creditCheckData.getCreditRemaining());
			modelService.save(b2bUnit);
		}
		return isBlockedUser;

	}

}

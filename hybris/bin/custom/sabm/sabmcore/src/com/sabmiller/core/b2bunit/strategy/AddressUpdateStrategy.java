/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;

import jakarta.annotation.Resource;

import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;


/**
 * @author joshua.a.antony
 *
 */
public class AddressUpdateStrategy extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, AddressData, AddressModel, Populator<AddressData, AddressModel>>
{


	@Resource(name = "addressReversePopulator")
	private Populator<AddressData, AddressModel> addressReversePopulator;

	@Override
	protected AddressModel lookup(final B2BUnitModel model, final AddressData addressData)
	{

		if (model.getAddresses() != null)
		{
			for (final AddressModel addressModel : model.getAddresses())
			{
				if (partnerNumberMatches(addressModel, addressData))
				{
					return addressModel;
				}
			}
		}

		return null;
	}

	protected boolean partnerNumberMatches(final AddressModel addressModel, final AddressData addressData)
	{
		return addressModel.getPartnerNumber() != null && addressData.getPartnerNumber() != null
				&& addressModel.getPartnerNumber().equals(addressData.getPartnerNumber());
	}

	@Override
	protected Populator<AddressData, AddressModel> getRelatedEntityModelPopulator()
	{
		return addressReversePopulator;
	}

	@Override
	protected AddressModel createModel(final B2BUnitModel b2bUnitModel)
	{
		final AddressModel addressModel = getModelService().create(AddressModel.class);
		addressModel.setOwner(b2bUnitModel);
		return addressModel;
	}

}

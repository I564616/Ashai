package com.apb.facades.assistedservice.impl;

import de.hybris.platform.assistedservicefacades.impl.DefaultAssistedServiceFacade;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.assistedservice.AsahiAssistedServiceFacade;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;

/**
 * The class will override the quitAssistedServiceMode to remove the bonus products from the cart.
 */
public class AsahiAssistedServiceFacadeImpl extends DefaultAssistedServiceFacade implements AsahiAssistedServiceFacade
{

	private static final Logger LOG = LoggerFactory.getLogger(AsahiAssistedServiceFacadeImpl.class);

	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private SessionService sessionService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "sabmSearchRestrictionService")
	private SabmSearchRestrictionService sabmSearchRestrictionService;

	@Override
	public void logoutAssistedServiceAgent() throws AssistedServiceException
	{
		if (!asahiSiteUtil.isCub())
		{
			LOG.info("Removing bonus products from cart on asm logout");
			sabmCartFacade.removeBonusProductFromCart();
		}
		super.logoutAssistedServiceAgent();
	}

	@Override
	public void stopEmulateCustomer()
	{
		if (!asahiSiteUtil.isCub())
		{
			LOG.info("Removing bonus products from cart when asm end user session");
			sabmCartFacade.removeBonusProductFromCart();
		}
		super.stopEmulateCustomer();
	}

	@Override
	public void emulateCustomer(final String customerId, final String cartId, final String orderId) throws AssistedServiceException
	{
		if (!asahiSiteUtil.isCub())
		{
			final Set<String> disableSearchRestrictions = new HashSet<>();
			disableSearchRestrictions.add("Frontend_ProductExclusionRestriction");
			disableSearchRestrictions.add("b2border_restriction");

			sabmSearchRestrictionService.simulateSearchRestrictionDisabledInSession(disableSearchRestrictions);
		}
		super.emulateCustomer(customerId, cartId, orderId);

		if (!asahiSiteUtil.isCub())
		{
			final UserModel userModel = getUserService().getCurrentUser();
			if (userModel instanceof B2BCustomerModel)
			{
				final B2BCustomerModel custModel = (B2BCustomerModel) userModel;

				final B2BUnitModel defaultB2bUnit =  custModel.getDefaultB2BUnit();
				final Map<String, List<AsahiB2BUnitModel>> activeUnits = apbB2BUnitService.getUserActiveB2BUnits(userModel.getUid());
				final String currentSite = asahiSiteUtil.getCurrentSite().getUid();

				if (CollectionUtils.isEmpty(activeUnits.get(currentSite)))
				{
					/*
					 * if no active b2bunit associated with user, don't let him login
					 */
					throw new AssistedServiceException("No B2BUnit is assigned to the user : " + custModel.getUid());
				}

				sessionService.setAttribute("isDefaultUnitBelongsCurrentSite", Boolean.TRUE);
				if (defaultB2bUnit instanceof AsahiB2BUnitModel && ((AsahiB2BUnitModel)defaultB2bUnit).getDisabledUser().contains(userModel.getUid()))
				{
					/*
					 * SCP-2047 : If default unit is disabled, make next one as default
					 */
					this.sessionService.setAttribute("defaultUnitDisabled", Boolean.TRUE);
					custModel.setDefaultB2BUnit((B2BUnitModel) activeUnits.get(currentSite).get(0));
					getModelService().save(custModel);
					getModelService().refresh(custModel);
				}
				else if (defaultB2bUnit instanceof AsahiB2BUnitModel && !((AsahiB2BUnitModel)defaultB2bUnit).getCompanyCode().equalsIgnoreCase(currentSite))
				{
					sessionService.setAttribute("isDefaultUnitBelongsCurrentSite", Boolean.FALSE);
					if (activeUnits.get(currentSite).size() >= 1)
					{
						custModel.setDefaultB2BUnit((B2BUnitModel) activeUnits.get(currentSite).get(0));
						getModelService().save(custModel);
						getModelService().refresh(custModel);
					}
				}
				else if(!(defaultB2bUnit instanceof AsahiB2BUnitModel)) 
				{
					sessionService.setAttribute("isDefaultUnitBelongsCurrentSite", Boolean.FALSE);
					if (activeUnits.get(currentSite).size() >= 1)
					{
						custModel.setDefaultB2BUnit((B2BUnitModel) activeUnits.get(currentSite).get(0));
						getModelService().save(custModel);
						getModelService().refresh(custModel);
					}
				}
			}
		}
	}
}

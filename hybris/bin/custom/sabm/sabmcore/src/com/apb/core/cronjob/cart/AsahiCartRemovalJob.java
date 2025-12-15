package com.apb.core.cronjob.cart;

import de.hybris.platform.acceleratorservices.model.CartRemovalCronJobModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.order.dao.CommerceCartDao;
import de.hybris.platform.commerceservices.order.dao.SaveCartDao;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.apb.core.service.config.AsahiConfigurationService;


/**
 * <p>
 * Class which removes old unused carts
 * </p>
 */
public class AsahiCartRemovalJob extends AbstractJobPerformable<CartRemovalCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(AsahiCartRemovalJob.class);

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	private CommerceCartDao commerceCartDao;
	private SaveCartDao saveCartDao;
	private TimeService timeService;
	private UserService userService;
	private CommerceSaveCartService commerceSaveCartService;
	private ConfigurationService configurationService;

	private static final String DEFAULT_CART_MAX_AGE = "asahi.default.cart.max.age.";
	private static final String DEFAULT_ANONYMOUS_CART_MAX_AGE = "asahi.default.anonymous.cart.max.age.";

	@Override
	public PerformResult perform(final CartRemovalCronJobModel job)
	{
		try
		{
			for (final BaseSiteModel site : job.getSites())
			{
				int age = Integer.parseInt(this.asahiConfigurationService.getString(DEFAULT_CART_MAX_AGE + site.getUid(), "2419200"));

				if (site.getCartRemovalAge() != null)
				{
					age = site.getCartRemovalAge().intValue();
				}
				for (final CartModel oldCart : getCommerceCartDao().getCartsForRemovalForSiteAndUser(
						new DateTime(getTimeService().getCurrentTime()).minusSeconds(age).toDate(), site, null))
				{
					LOG.info("Before removing the old carts for reg user..");
					getModelService().remove(oldCart);
					LOG.info("Old carts removed successfully for reg user ");
				}

				age = Integer
						.parseInt(this.asahiConfigurationService.getString(DEFAULT_ANONYMOUS_CART_MAX_AGE + site.getUid(), "1209600"));

				if (site.getAnonymousCartRemovalAge() != null)
				{
					age = site.getAnonymousCartRemovalAge().intValue();
				}

				for (final CartModel oldCart : getCommerceCartDao().getCartsForRemovalForSiteAndUser(
						new DateTime(getTimeService().getCurrentTime()).minusSeconds(age).toDate(), site,
						getUserService().getAnonymousUser()))
				{
					LOG.info("Before removing the old carts for anonymous user..");
					getModelService().remove(oldCart);
					LOG.info("After removing the old carts for anonymous user..");
				}

				for (final CartModel cartToFlag : getSaveCartDao().getSavedCartsForRemovalForSite(site))
				{
					final CommerceSaveCartParameter parameters = new CommerceSaveCartParameter();
					parameters.setCart(cartToFlag);
					parameters.setEnableHooks(getConfigurationService().getConfiguration()
							.getBoolean(CommerceServicesConstants.FLAGFORDELETIONHOOK_ENABLED, true));

					getCommerceSaveCartService().flagForDeletion(parameters);
					LOG.info("After flag for deletion");
				}
			}
			LOG.info("Cart removed succesfully..");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during cart cleanup ", e);
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
	}

	protected CommerceCartDao getCommerceCartDao()
	{
		return commerceCartDao;
	}

	public void setCommerceCartDao(final CommerceCartDao commerceCartDao)
	{
		this.commerceCartDao = commerceCartDao;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public SaveCartDao getSaveCartDao()
	{
		return saveCartDao;
	}

	public void setSaveCartDao(final SaveCartDao saveCartDao)
	{
		this.saveCartDao = saveCartDao;
	}

	protected CommerceSaveCartService getCommerceSaveCartService()
	{
		return commerceSaveCartService;
	}

	public void setCommerceSaveCartService(final CommerceSaveCartService commerceSaveCartService)
	{
		this.commerceSaveCartService = commerceSaveCartService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}

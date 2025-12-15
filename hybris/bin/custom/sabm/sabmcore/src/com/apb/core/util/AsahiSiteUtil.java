package com.apb.core.util;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.BDECustomerModel;


/**
 * @author Pankaj.Gandhi
 *
 *         Class to provide utility functions throughout the site related to sga/apb cmssite and its associated config
 *         values
 */
public class AsahiSiteUtil
{

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private SessionService sessionService;

	@Resource
	private UserService userService;

	private static final String SGA_SITE_NAME = "sga";
	private static final String APB_SITE_NAME = "apb";
	private static final String CUB_SITE_NAME = "sabmStore";

	private static final String DEFAULT_CURRENCY = "AUD";
	private static final String DEFAULT_CURRENCY_SYMBOL = "$";

	private static final Long DEFAULT_MAX_QTY = 200L;
	/**
	 * fetching the current site object.
	 *
	 * @return the current site
	 */
	public CMSSiteModel getCurrentSite()
	{
		return cmsSiteService.getCurrentSite();
	}

	/**
	 * Checks if is sga.
	 *
	 * @return true, if is sga
	 */
	public boolean isSga()
	{
		return cmsSiteService.getCurrentSite().getUid().equalsIgnoreCase(SGA_SITE_NAME);
	}

	/**
	 * Checks if is apb.
	 *
	 * @return true, if is apb
	 */
	public boolean isApb()
	{
		return cmsSiteService.getCurrentSite().getUid().equalsIgnoreCase(APB_SITE_NAME);
	}

	/**
	 * Checks if is cub.
	 *
	 * @return true, if is cub
	 */
	public boolean isCub()
	{
		final String sabmSesValue = sessionService.getAttribute(SabmCoreConstants.SESSION_CUB_WEBSERVICES_ATTR);
		if(sabmSesValue!=null && sabmSesValue.equalsIgnoreCase(SabmCoreConstants.CUB_STORE)) {
		return true;
		}
		return null != cmsSiteService.getCurrentSite() ? cmsSiteService.getCurrentSite().getUid().equalsIgnoreCase(CUB_SITE_NAME):false;
	}

	/**
	 * Gets the apb max order qty.
	 *
	 * @return the apb max order qty
	 */
	public Long getApbGlobalMaxOrderQty()
	{
		Long qty = DEFAULT_MAX_QTY;
		if (isApb() && null != getCurrentSite().getMaxOrderQty() && getCurrentSite().getMaxOrderQty() > 0)
		{
			qty = Long.valueOf(getCurrentSite().getMaxOrderQty());
		}
		return qty;
	}

	/**
	 * Gets the sga max order qty.
	 *
	 * @return the sga max order qty
	 */
	public Long getSgaGlobalMaxOrderQty()
	{
		Long qty = DEFAULT_MAX_QTY;
		if (isSga() && null != getCurrentSite().getMaxOrderQty() && getCurrentSite().getMaxOrderQty() > 0)
		{
			qty = Long.valueOf(getCurrentSite().getMaxOrderQty());
		}
		return qty;
	}

	/**
	 * Get catalog based on site uid
	 *
	 * @param siteCode
	 * @return catalogId
	 */
	public String getCatalogId(final String siteCode)
	{
		return asahiConfigurationService.getString(siteCode.toLowerCase() + ApbCoreConstants.CATALOG_ID_KEY, StringUtils.EMPTY);
	}

	/**
	 * Get catalog version based on site uid
	 *
	 * @param siteCode
	 * @return catalogVersion
	 */
	public String getCatalogVersion(final String siteCode)
	{
		return asahiConfigurationService.getString(siteCode + ApbCoreConstants.CATALOG_VERSION_KEY, "Staged");
	}

	/**
	 * Get current session currency
	 *
	 * @return currency
	 */
	public String getCurrency()
	{
		final CurrencyModel cur = commonI18NService.getCurrentCurrency();
		return null != cur && StringUtils.isNotEmpty(cur.getIsocode()) ? cur.getIsocode() : DEFAULT_CURRENCY;
	}

	/**
	 * Get current session currency symbol
	 *
	 * @return currency
	 */
	public String getCurrencySymbol()
	{
		final CurrencyModel cur = commonI18NService.getCurrentCurrency();
		return null != cur && StringUtils.isNotEmpty(cur.getSymbol()) ? cur.getSymbol() : DEFAULT_CURRENCY_SYMBOL;
	}

	/**
	 * <p>
	 * This method will check for site based on siteId
	 * </p>
	 *
	 * @param siteId
	 * @return boolean value
	 */
	public boolean isSga(final String siteId)
	{
		return StringUtils.isNotEmpty(siteId) ? siteId.equalsIgnoreCase(SGA_SITE_NAME) : false;
	}

	/**
	 * This method will check the curent logged inuser is BDE customer (staff portal customer) and return boolean value.
	 *
	 * @return
	 */
	public boolean isBDECustomer()
	{
		return userService.getCurrentUser() instanceof BDECustomerModel;
	}
}

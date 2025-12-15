/**
 *
 */
package com.sabmiller.storefront.interceptors.beforeview;

import com.sabmiller.facades.util.SabmFeatureUtil;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.constants.SabmCoreConstants;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;



/**
 * @author xiaowu.a.zhang Add the additional data to the jsp. e.g: impersonate username
 * @date 16/06/2016
 */
public class AdditionalDataBeforeViewHandler implements BeforeViewHandler
{

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;
	
	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws Exception
	{
		modelAndView.addObject("impersonate", getImpersonateValue());
		
		String deliveryDatePackType = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE);
		modelAndView.addObject("deliveryDatePackType", deliveryDatePackType);
		modelAndView.addObject("isAutoPayEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.AUTOPAY));
	}

	/**
	 * @return return the impersonate user's name. if username is empty will return uid
	 */
	protected String getImpersonateValue()
	{
		final UserModel impersonateUser = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA);
		if (impersonateUser != null)
		{
			return StringUtils.isNotBlank(impersonateUser.getName()) ? impersonateUser.getName() : impersonateUser.getUid();
		}
		return "";
	}

}

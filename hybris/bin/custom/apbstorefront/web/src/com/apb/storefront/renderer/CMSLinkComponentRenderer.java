/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.storefront.renderer;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.acceleratorstorefrontcommons.tags.Functions;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.tag.common.core.UrlSupport;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.constant.ApbStoreFrontContants;


/**
 */
public class CMSLinkComponentRenderer implements CMSComponentRenderer<CMSLinkComponentModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CMSLinkComponentRenderer.class);
	
	private static final String CATEGORY_PATH_PATTERN = "/c/";

	protected static final PolicyFactory policy = new HtmlPolicyBuilder().allowStandardUrlProtocols()
			.allowElements("a", "span")
			.allowAttributes( "href", "style", "class", "title", "target", "download", "rel", "rev",
					"hreflang", "type", "text", "accesskey", "contenteditable", "contextmenu", "dir", "draggable",
					"dropzone", "hidden", "id", "lang", "spellcheck", "tabindex", "translate")
			.onElements("a")
			.allowAttributes("class")
			.onElements("span")
			.toFactory();


	private Converter<ProductModel, ProductData> productUrlConverter;
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private SessionService sessionService;
	
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	protected Converter<ProductModel, ProductData> getProductUrlConverter()
	{
		return productUrlConverter;
	}

	public void setProductUrlConverter(final Converter<ProductModel, ProductData> productUrlConverter)
	{
		this.productUrlConverter = productUrlConverter;
	}

	protected Converter<CategoryModel, CategoryData> getCategoryUrlConverter()
	{
		return categoryUrlConverter;
	}

	public void setCategoryUrlConverter(final Converter<CategoryModel, CategoryData> categoryUrlConverter)
	{
		this.categoryUrlConverter = categoryUrlConverter;
	}

	protected String getUrl(final CMSLinkComponentModel component)
	{
		// Call the function getUrlForCMSLinkComponent so that this code is only in one place
		return Functions.getUrlForCMSLinkComponent(component, getProductUrlConverter(), getCategoryUrlConverter());
	}

	@Override
	public void renderComponent(final PageContext pageContext, final CMSLinkComponentModel component) throws ServletException,
			IOException
	{
		try
		{
			/*
			 * If user role does not match with link access role, do nothing
			 */
			if(null != component.getAccessType() 
					&& !asahiCoreUtil.getCurrentUserAccessType().equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS)
					&& !component.getAccessType().toString().equalsIgnoreCase(asahiCoreUtil.getCurrentUserAccessType()))
				return;
			
			//Do not render component for category if excluded for the customer
			final String url = getUrl(component);
			boolean allowed = true;
			 if (asahiSiteUtil.isSga() 
					&& null != sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES) 
					&& CollectionUtils.isNotEmpty(sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES))) {
				
				Set<String> excludedCategories = sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES);
				allowed = checkIfCategoryAllowed(component, excludedCategories);
				if (!allowed) {
					return;
				}
			 }
			final String encodedUrl = UrlSupport.resolveUrl(url, null, pageContext);
			final String linkName = component.getLinkName();
			final StringBuilder html = new StringBuilder();
			/*
			 * with implemenation of multi account functionality, admin group will be assigned to the user at runtime
			 * So manual check is implemented to check if any link being displayed on the site belongs to admin group then we should 
			 * only display to admin user.
			 */
			final List<String> adminLinks = Arrays.asList(StringUtils.split(
					asahiConfigurationService.getString(ApbStoreFrontContants.ADMIN_RESTRICTED_LINKS + asahiSiteUtil.getCurrentSite().getUid().toLowerCase()
							,StringUtils.EMPTY), ","));
			
			if(!adminLinks.contains(linkName.toLowerCase()) || 
					(adminLinks.contains(linkName.toLowerCase()) && asahiCoreUtil.adminRightExists())) {
			
   			if (StringUtils.isNotBlank(linkName) && StringUtils.isBlank(encodedUrl))
   			{
   				// <span class="empty-nav-item">${component.linkName}</span>
   				html.append("<span class=\"empty-nav-item\">");
   				html.append(linkName);
   				html.append("</span>");
   			}
   			else
   			{
   				// <a href="${encodedUrl}" ${component.styleAttributes} title="${component.linkName}"
   				// ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_blank"'}>${component.linkName}</a>
   
   				html.append("<a href=\"");
   				html.append(encodedUrl);
   				html.append("\" ");
   
   				// Write additional attributes onto the link
   				if (component.getStyleAttributes() != null)
   				{
   					html.append(component.getStyleAttributes());
   				}
   
   				if (StringUtils.isNotBlank(linkName))
   				{
   					html.append(" title=\"");
   					html.append(linkName);
   					html.append("\" ");
   				}
   
   				if (component.getTarget() != null && !LinkTargets.SAMEWINDOW.equals(component.getTarget()))
   				{
   					html.append(" target=\"_blank\"");
   				}
   				html.append(">");
   				if (StringUtils.isNotBlank(linkName))
   				{
   					html.append(linkName);
   				}
   				html.append("</a>");
   			}
   
   			String sanitizedHTML = policy.sanitize(html.toString());
   			final JspWriter out = pageContext.getOut();
   			out.write(sanitizedHTML);
			}
		}
		catch (final JspException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("JspException occurred...", e);
			}
		}
	}

	/**
	 * @param excludedCategories 
	 * @return ifCategoryAllowed
	 */
	private boolean checkIfCategoryAllowed(CMSLinkComponentModel component, Set<String> excludedCategories)
	{
		
		if (null != component.getCategory())
		{
			if (excludedCategories.contains(component.getCategory().getCode())) {
				return false;
			}
		}

		// Try to get the URL from the component
		final String url = component.getUrl();
		if (url != null && !url.isEmpty() && url.contains(CATEGORY_PATH_PATTERN))
		{
			String[] pathSegments = StringUtils.split(url, "/");
			int pathSegmentsLength = pathSegments.length;
			String categoryCode = pathSegments[pathSegmentsLength-1];
			if (excludedCategories.contains(categoryCode)) {
				return false;
			}			
		}

		return true;
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.renderer;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.acceleratorservices.util.HtmlSanitizerPolicyProvider;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.util.Config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Value;
import de.hybris.platform.acceleratorcms.component.renderer.impl.CMSParagraphComponentRenderer;

/**
 * HC-289 Custom renderer to handle Anchor tag links
 */
public class SabmCMSParagraphComponentRenderer extends CMSParagraphComponentRenderer
{

    @Override
    public void renderComponent(final PageContext pageContext, final CMSParagraphComponentModel component)
            throws ServletException, IOException
    {
        // <div class="content">${content}</div>
        final JspWriter out = pageContext.getOut();

        out.write("<div class=\"content\">");

        final String content = component.getContent() == null ? StringUtils.EMPTY : component.getContent();


            out.write(content);

        out.write("</div>");
    }


}

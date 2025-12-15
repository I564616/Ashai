package com.apb.renderer;

import com.apb.core.util.AsahiAdhocCoreUtil;
import com.apb.model.ApbHtmlCMSComponentModel;
import de.hybris.platform.acceleratorcms.component.renderer.impl.GenericViewCMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.PageContext;
import java.io.IOException;

public class AddOnApbHtmlCMSComponentRenderer extends GenericViewCMSComponentRenderer {

    @Autowired
    private AsahiAdhocCoreUtil adhocCoreUtil;

    @Override
    public void renderComponent(final PageContext pageContext, final AbstractCMSComponentModel component) throws ServletException, IOException {
        final ApbHtmlCMSComponentModel cmsComponent = adhocCoreUtil.validateAndGetModel(component);
        if (cmsComponent != null) {
            super.renderComponent(pageContext, cmsComponent);
        }
    }
}


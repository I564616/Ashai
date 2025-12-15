/**
 *
 */
package com.apb.apbb2baccaddon.component.renderer;

import de.hybris.platform.acceleratorcms.model.components.ProductAddToCartComponentModel;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;

import com.apb.constants.Apbb2baccaddonConstants;


/**
 * @author I332443
 *
 */
public class ApbB2bAccAbstractComponentRenderer<C extends SimpleCMSComponentModel>
		extends DefaultAddOnCMSComponentRenderer<C>
{
	private static final String COMPONENT = "component";

	/*
	 * @Override protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component) {
	 * final Map<String, Object> model = super.getVariablesToExpose(pageContext, component); model.put(COMPONENT,
	 * component); return model; }
	 */

	@Override
	protected String getAddonUiExtensionName(final C component)
	{
		return Apbb2baccaddonConstants.EXTENSIONNAME;
	}
}

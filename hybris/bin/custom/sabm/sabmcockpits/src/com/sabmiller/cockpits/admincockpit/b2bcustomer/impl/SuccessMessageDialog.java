package com.sabmiller.cockpits.admincockpit.b2bcustomer.impl;

import de.hybris.platform.cockpit.wizards.impl.DefaultPage;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;


/**
 *
 */
public class SuccessMessageDialog extends DefaultPage
{

	/**
	 * @param height
	 */
	public void setHeight(final String height)
	{
		this.height = height;
	}

	/**
	 * @param width
	 */
	public void setWidth(final String width)
	{
		this.width = width;
	}

	@Override
	public void renderView(final Component parent)
	{
		parent.appendChild(new Label(""));
	}
}


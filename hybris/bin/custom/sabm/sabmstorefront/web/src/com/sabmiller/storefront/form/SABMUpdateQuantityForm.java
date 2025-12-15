/**
 *
 */
package com.sabmiller.storefront.form;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateQuantityForm;


/**
 * Form for validating update field on cart page.
 */
public class SABMUpdateQuantityForm extends UpdateQuantityForm
{

	/** The unit. */
	private String unit = "";

	/** The entry number. */
	private Long entryNumber;

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public String getUnit()
	{
		return unit;
	}

	/**
	 * Sets the unit.
	 *
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	/**
	 * Gets the entry number.
	 *
	 * @return the entryNumber
	 */
	public Long getEntryNumber()
	{
		return entryNumber;
	}

	/**
	 * Sets the entry number.
	 *
	 * @param entryNumber
	 *           the entryNumber to set
	 */
	public void setEntryNumber(final Long entryNumber)
	{
		this.entryNumber = entryNumber;
	}
}

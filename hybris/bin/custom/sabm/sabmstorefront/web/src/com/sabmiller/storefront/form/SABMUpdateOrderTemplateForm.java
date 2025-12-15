/**
 *
 */
package com.sabmiller.storefront.form;

import java.util.List;


/**
 * The Class SABMUpdateOrderTemplateForm.
 */
public class SABMUpdateOrderTemplateForm
{

	/** The code. */
	private String code;

	/** The name. */
	private String name;

	/** The entries. */
	private List<SABMUpdateQuantityForm> entries;

	/** The entryNumber. */
	private String entryNumber;

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	public List<SABMUpdateQuantityForm> getEntries()
	{
		return entries;
	}

	/**
	 * Sets the entries.
	 *
	 * @param entries
	 *           the entries to set
	 */
	public void setEntries(final List<SABMUpdateQuantityForm> entries)
	{
		this.entries = entries;
	}

	/**
	 * @return the entryNumber
	 */
	public String getEntryNumber()
	{
		return entryNumber;
	}

	/**
	 * @param entryNumber
	 *           the entryNumber to set
	 */
	public void setEntryNumber(final String entryNumber)
	{
		this.entryNumber = entryNumber;
	}


}

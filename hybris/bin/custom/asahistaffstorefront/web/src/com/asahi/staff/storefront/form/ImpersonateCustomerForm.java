/**
 *
 */
package com.asahi.staff.storefront.form;

/**
 * @author geoffry.d.heredia
 *
 */
public class ImpersonateCustomerForm
{
	private String uid;
	private String unit;
	private String landingPage;
	private String email;

	/**
	 * @return the landingPage
	 */
	public String getLandingPage()
	{
		return landingPage;
	}

	/**
	 * @param landingPage
	 *           the landingPage to set
	 */
	public void setLandingPage(final String landingPage)
	{
		this.landingPage = landingPage;
	}

	/**
	 * @return the uid
	 */
	public String getUid()
	{
		return uid;
	}

	/**
	 * @param uid
	 *           the uid to set
	 */
	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	/**
	 * @return the unit
	 */
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

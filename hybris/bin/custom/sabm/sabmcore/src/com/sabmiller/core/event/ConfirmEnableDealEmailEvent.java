/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.user.UserModel;

import java.io.Serial;
import java.util.List;


/**
 *
 */
public class ConfirmEnableDealEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	private String behaviourRequirements;
	private List<String> activatedDealTitles;
	private List<String> deactivatedDealTitles;
	private final UserModel fromUser;
	private List<String> toEmails;
	private List<String> ccEmails;
	private B2BUnitModel emailUnit;
	private String primaryAdminStatus;

	/**
	 * @return the primaryAdminStatus
	 */
	public String getPrimaryAdminStatus()
	{
		return primaryAdminStatus;
	}

	/**
	 * @param primaryAdminStatus
	 *           the primaryAdminStatus to set
	 */
	public void setPrimaryAdminStatus(final String primaryAdminStatus)
	{
		this.primaryAdminStatus = primaryAdminStatus;
	}

	/**
	 * @param behaviourRequirements
	 * @param activatedDealTitles
	 * @param deactivatedDealTitles
	 * @param fromUser
	 * @param ccEmails
	 * @param toEmails
	 * @param emailUnit
	 */
	public ConfirmEnableDealEmailEvent(final String behaviourRequirements, final List<String> activatedDealTitles,
			final List<String> deactivatedDealTitles, final UserModel fromUser, final List<String> toEmails,
			final List<String> ccEmails, final B2BUnitModel emailUnit, final String primaryAdminStatus)
	{
		super();
		this.behaviourRequirements = behaviourRequirements;
		this.activatedDealTitles = activatedDealTitles;
		this.deactivatedDealTitles = deactivatedDealTitles;
		this.fromUser = fromUser;
		this.toEmails = toEmails;
		this.ccEmails = ccEmails;
		this.emailUnit = emailUnit;
		this.primaryAdminStatus = primaryAdminStatus;
	}

	/**
	 * @return the behaviourRequirements
	 */
	public String getBehaviourRequirements()
	{
		return behaviourRequirements;
	}

	/**
	 * @param behaviourRequirements
	 *           the behaviourRequirements to set
	 */
	public void setBehaviourRequirements(final String behaviourRequirements)
	{
		this.behaviourRequirements = behaviourRequirements;
	}

	/**
	 * @return the activatedDealTitles
	 */
	public List<String> getActivatedDealTitles()
	{
		return activatedDealTitles;
	}

	/**
	 * @param activatedDealTitles
	 *           the activatedDealTitles to set
	 */
	public void setActivatedDealTitles(final List<String> activatedDealTitles)
	{
		this.activatedDealTitles = activatedDealTitles;
	}

	/**
	 * @return the deactivatedDealTitles
	 */
	public List<String> getDeactivatedDealTitles()
	{
		return deactivatedDealTitles;
	}

	/**
	 * @param deactivatedDealTitles
	 *           the deactivatedDealTitles to set
	 */
	public void setDeactivatedDealTitles(final List<String> deactivatedDealTitles)
	{
		this.deactivatedDealTitles = deactivatedDealTitles;
	}

	/**
	 * @return the fromUser
	 */
	public UserModel getFromUser()
	{
		return fromUser;
	}

	/**
	 * @return the ccEmails
	 */
	public List<String> getCcEmails()
	{
		return ccEmails;
	}

	/**
	 * @param ccEmails
	 *           the ccEmails to set
	 */
	public void setCcEmails(final List<String> ccEmails)
	{
		this.ccEmails = ccEmails;
	}

	/**
	 * @return the toEmails
	 */
	public List<String> getToEmails()
	{
		return toEmails;
	}

	/**
	 * @param toEmails
	 *           the toEmails to set
	 */
	public void setToEmails(final List<String> toEmails)
	{
		this.toEmails = toEmails;
	}

	/**
	 * @return the emailUnit
	 */
	public B2BUnitModel getEmailUnit()
	{
		return emailUnit;
	}

	/**
	 * @param emailUnit
	 *           the emailUnit to set
	 */
	public void setEmailUnit(final B2BUnitModel emailUnit)
	{
		this.emailUnit = emailUnit;
	}


}

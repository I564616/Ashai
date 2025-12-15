/**
 *
 */
package com.sabmiller.staff.storefront.form;

import java.util.List;

import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;


/**
 * @author xue.zeng
 *
 */
public class SABMChangesDealForm
{
	/* Customer Id */
	private String uid;

	/* Changes Deal */
	private List<RepDrivenDealConditionData> conditions;

	/* Option to save */
	private boolean saveChanges;

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
	 * @return the conditions
	 */
	public List<RepDrivenDealConditionData> getConditions()
	{
		return conditions;
	}

	/**
	 * @param conditions
	 *           the conditions to set
	 */
	public void setConditions(final List<RepDrivenDealConditionData> conditions)
	{
		this.conditions = conditions;
	}

	/**
	 * @return the saveChanges
	 */
	public boolean getSaveChanges()
	{
		return saveChanges;
	}

	/**
	 * @param saveChanges
	 *           the saveChanges to set
	 */
	public void setSaveChanges(final boolean saveChanges)
	{
		this.saveChanges = saveChanges;
	}
}

/**
 * 
 */
package com.sabmiller.storefront.form;

import java.util.List;

/**
 * @author marc.f.l.bautista
 *
 */
public class SABMUpdateRecommendationsForm
{
	/** The list of recommendations. */
	private List<SABMUpdateRecommendationForm> recommendations;

	/** The list of recommendation ids to delete. */
	private String recommendationsToDelete;
	
	/**
	 * @return the recommendations
	 */
	public List<SABMUpdateRecommendationForm> getRecommendations()
	{
		return recommendations;
	}

	/**
	 * @param recommendations the recommendations to set
	 */
	public void setRecommendations(final List<SABMUpdateRecommendationForm> recommendations)
	{
		this.recommendations = recommendations;
	}

	/**
	 * @return the recommendationsToDelete
	 */
	public String getRecommendationsToDelete()
	{
		return recommendationsToDelete;
	}

	/**
	 * @param recommendationsToDelete the recommendationsToDelete to set
	 */
	public void setRecommendationsToDelete(final String recommendationsToDelete)
	{
		this.recommendationsToDelete = recommendationsToDelete;
	}
	
}

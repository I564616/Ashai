package com.sabmiller.storefront.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.facades.deal.data.RecommendationDealJson;
import com.sabmiller.facades.recommendation.data.RecommendationData;


/**
 * Created by evariz.d.paragoso on 6/19/17.
 */
public class RecommendationUtil
{

	/**
	 * Separates the deals recommendations and the product recommendations
	 * @param recommendations
	 * @param model
	 */
	public static void separateRecommendations(List<RecommendationData> recommendations, Model model)
	{
		final List<RecommendationDealJson> deals = new ArrayList<>();
		List<RecommendationData> productRecommendation = new ArrayList<>();

		for (RecommendationData recommendation : recommendations)
		{
			if (recommendation.getRecommendationType() == RecommendationType.DEAL)
			{
				deals.add(recommendation.getRecommendationDealJson());
			}
			else
			{
				productRecommendation.add(recommendation);
			}
		}
		model.addAttribute("deals", deals);
		model.addAttribute("productRecommendation", productRecommendation);
	}
}

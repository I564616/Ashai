package com.sabmiller.storefront.form;

import java.util.List;

/**
 * Created by raul.b.abatol.jr on 14/06/2017.
 */
public class SABMAddToRecommendationForm {

    private String dealCode;

    private List<SABMProdToRecommendationForm> baseProducts;


    /**
     * @return the dealCode
     */
    public String getDealCode()
    {
        return dealCode;
    }

    /**
     * @param dealCode
     *           the dealCode to set
     */
    public void setDealCode(final String dealCode)
    {
        this.dealCode = dealCode;
    }

    /**
     * @return the baseProducts
     */
    public List<SABMProdToRecommendationForm> getBaseProducts()
    {
        return baseProducts;
    }

    /**
     * @param baseProducts
     *           the baseProducts to set
     */
    public void setBaseProducts(final List<SABMProdToRecommendationForm> baseProducts)
    {
        this.baseProducts = baseProducts;
    }
}

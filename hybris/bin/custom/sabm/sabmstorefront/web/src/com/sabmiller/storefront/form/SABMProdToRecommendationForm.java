package com.sabmiller.storefront.form;

/**
 * Created by raul.b.abatol.jr on 14/06/2017.
 */
public class SABMProdToRecommendationForm {

    private String unit = "";

    private String productCodePost;

    private Integer qty;

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

    /**
     * @return the productCodePost
     */
    public String getProductCodePost()
    {
        return productCodePost;
    }

    /**
     * @param productCodePost
     *           the productCodePost to set
     */
    public void setProductCodePost(final String productCodePost)
    {
        this.productCodePost = productCodePost;
    }

    /**
     * @return the dealCode
     */
    public Integer getQty()
    {
        return qty;
    }

    /**
     * @param qty
     *           the qty to set
     */
    public void setQty(final Integer qty)
    {
        this.qty = qty;
    }


}

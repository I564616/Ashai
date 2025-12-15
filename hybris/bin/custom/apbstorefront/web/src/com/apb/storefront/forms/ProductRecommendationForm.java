package com.apb.storefront.forms;

public class ProductRecommendationForm {

    private String unit = "";

    private String productCode;

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
     * @return the productCode
     */
    public String getProductCode()
    {
        return productCode;
    }

    /**
     * @param productCode
     *           the productCode to set
     */
    public void setProductCode(final String productCode)
    {
        this.productCode = productCode;
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

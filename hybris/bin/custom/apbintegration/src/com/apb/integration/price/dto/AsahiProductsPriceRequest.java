package com.apb.integration.price.dto;

import java.util.List;

/**
 * The Class AsahiProductsPriceRequest.
 */
public class AsahiProductsPriceRequest {

	/** The product. */
	private List<AsahiProductPriceRequest> product = null;

	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public List<AsahiProductPriceRequest> getProduct() {
		return product;
	}

	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(List<AsahiProductPriceRequest> product) {
		this.product = product;
	}
}

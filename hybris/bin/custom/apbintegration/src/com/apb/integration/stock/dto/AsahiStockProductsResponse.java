package com.apb.integration.stock.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AsahiStockProductsResponse {

	@JsonProperty("Product")
	private List<AsahiStockProductResponse> product = null;

	public List<AsahiStockProductResponse> getProduct() {
		return product;
	}

	public void setProduct(List<AsahiStockProductResponse> product) {
		this.product = product;
	}

}

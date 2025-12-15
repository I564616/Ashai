package com.apb.integration.stock.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AsahiStockProductRequest {

	@JsonProperty("ProductId")
	private List<String> productId = null;

	public List<String> getProductId() {
		return productId;
	}

	public void setProductId(List<String> productId) {
		this.productId = productId;
	}
}

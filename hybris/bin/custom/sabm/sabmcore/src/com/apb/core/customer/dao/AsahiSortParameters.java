package com.apb.core.customer.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import de.hybris.platform.servicelayer.internal.dao.SortParameters;

public class AsahiSortParameters extends SortParameters{
	private final Map<String, SortParameters.SortOrder> params = new LinkedHashMap<String, SortParameters.SortOrder>();
	
	public void addSortParameter(String paramName, SortParameters.SortOrder sortOrder) {
		this.params.put(paramName, sortOrder);
	}
	
	public Map<String, SortParameters.SortOrder> getSortParameters() {
		return this.params;
	}
	
	public boolean isEmpty() {
		return this.params.isEmpty();
	}
	
}

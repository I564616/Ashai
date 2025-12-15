package com.apb.integration.order.dto;

import java.util.List;

public class AsahiOrderLinesRequest {

	private List<AsahiLineRequest> line = null;

	public List<AsahiLineRequest> getLine() {
		return line;
	}

	public void setLine(List<AsahiLineRequest> line) {
		this.line = line;
	}
}
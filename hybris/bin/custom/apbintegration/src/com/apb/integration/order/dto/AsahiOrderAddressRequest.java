package com.apb.integration.order.dto;

public class AsahiOrderAddressRequest {

	/** The address type. */
	private String addressType;
	
	/** The backend record id. */
	private String backendRecordId;

	/**
	 * @return the addressType
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * @param addressType the addressType to set
	 */
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the backendRecordId
	 */
	public String getBackendRecordId() {
		return backendRecordId;
	}

	/**
	 * @param backendRecordId the backendRecordId to set
	 */
	public void setBackendRecordId(String backendRecordId) {
		this.backendRecordId = backendRecordId;
	}
}

package com.apb.integration.order.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "order")
public class AsahiOrderRequest
{

	private String externalOrderNumber;

	private String onlinePaymentReference;

	private String isPrepaid;

	private String customerReference;

	private String addressUID;

	private String custAccount;

	private String orderCreatedDateTime;

	private String requestedDeliveryDate;

	private String deliveryTimeTo;

	private String deliveryInstructions;

	private AsahiOrderLinesRequest orderLines;

	private String orderPlacedBy;

	private String isDeferredDelivery;

	private String deliveryTimeFrom;

	private String orderOriginId;

	private String onlinePaymentOtherRefs;

	private double onlinePaymentAmount;

	private double onlinePaymentSurcharge;

	private String deviceType;

	private String custOrderType;

	private String companyCode;
	
	private String addressType;
	
	private String division;
	
	private String distChannel;
	
	private String salesOrg;
	
	private List<AsahiOrderAddressRequest> orderAddress;

    private String CCTokenId;


    public String getCCTokenId() {
        return CCTokenId;
    }

    public void setCCTokenId(String CCTokenId) {
        this.CCTokenId = CCTokenId;
    }

	

	/**
	 * @return
	 */
	public String getDeviceType()
	{
		return deviceType;
	}

	/**
	 * @param deviceType
	 */
	public void setDeviceType(final String deviceType)
	{
		this.deviceType = deviceType;
	}

	/**
	 * @return
	 */
	public String getCustOrderType()
	{
		return custOrderType;
	}

	/**
	 * @param custOrderType
	 */
	public void setCustOrderType(final String custOrderType)
	{
		this.custOrderType = custOrderType;
	}

	/**
	 * @return
	 */
	public String getCompanyCode()
	{
		return companyCode;
	}

	/**
	 * @param companyCode
	 */
	public void setCompanyCode(final String companyCode)
	{
		this.companyCode = companyCode;
	}

	public String getExternalOrderNumber()
	{
		return externalOrderNumber;
	}

	public void setExternalOrderNumber(final String externalOrderNumber)
	{
		this.externalOrderNumber = externalOrderNumber;
	}

	public String getOnlinePaymentReference()
	{
		return onlinePaymentReference;
	}

	public void setOnlinePaymentReference(final String onlinePaymentReference)
	{
		this.onlinePaymentReference = onlinePaymentReference;
	}

	public String getIsPrepaid()
	{
		return isPrepaid;
	}

	public void setIsPrepaid(final String isPrepaid)
	{
		this.isPrepaid = isPrepaid;
	}

	public String getCustomerReference()
	{
		return customerReference;
	}

	public void setCustomerReference(final String customerReference)
	{
		this.customerReference = customerReference;
	}

	public String getAddressUID()
	{
		return addressUID;
	}

	public void setAddressUID(final String addressUID)
	{
		this.addressUID = addressUID;
	}

	public String getCustAccount()
	{
		return custAccount;
	}

	public void setCustAccount(final String custAccount)
	{
		this.custAccount = custAccount;
	}

	public String getOrderCreatedDateTime()
	{
		return orderCreatedDateTime;
	}

	public void setOrderCreatedDateTime(final String orderCreatedDateTime)
	{
		this.orderCreatedDateTime = orderCreatedDateTime;
	}

	public String getRequestedDeliveryDate()
	{
		return requestedDeliveryDate;
	}

	public void setRequestedDeliveryDate(final String requestedDeliveryDate)
	{
		this.requestedDeliveryDate = requestedDeliveryDate;
	}

	public String getDeliveryTimeTo()
	{
		return deliveryTimeTo;
	}

	public void setDeliveryTimeTo(final String deliveryTimeTo)
	{
		this.deliveryTimeTo = deliveryTimeTo;
	}

	public String getDeliveryInstructions()
	{
		return deliveryInstructions;
	}

	public void setDeliveryInstructions(final String deliveryInstructions)
	{
		this.deliveryInstructions = deliveryInstructions;
	}

	public AsahiOrderLinesRequest getOrderLines()
	{
		return orderLines;
	}

	public void setOrderLines(final AsahiOrderLinesRequest orderLines)
	{
		this.orderLines = orderLines;
	}

	public String getOrderPlacedBy()
	{
		return orderPlacedBy;
	}

	public void setOrderPlacedBy(final String orderPlacedBy)
	{
		this.orderPlacedBy = orderPlacedBy;
	}

	public String getIsDeferredDelivery()
	{
		return isDeferredDelivery;
	}

	public void setIsDeferredDelivery(final String isDeferredDelivery)
	{
		this.isDeferredDelivery = isDeferredDelivery;
	}

	public String getDeliveryTimeFrom()
	{
		return deliveryTimeFrom;
	}

	public void setDeliveryTimeFrom(final String deliveryTimeFrom)
	{
		this.deliveryTimeFrom = deliveryTimeFrom;
	}

	public String getOrderOriginId()
	{
		return orderOriginId;
	}

	public void setOrderOriginId(final String orderOriginId)
	{
		this.orderOriginId = orderOriginId;
	}

	public String getOnlinePaymentOtherRefs()
	{
		return onlinePaymentOtherRefs;
	}

	public void setOnlinePaymentOtherRefs(final String onlinePaymentOtherRefs)
	{
		this.onlinePaymentOtherRefs = onlinePaymentOtherRefs;
	}

	public double getOnlinePaymentAmount()
	{
		return onlinePaymentAmount;
	}

	public void setOnlinePaymentAmount(final double onlinePaymentAmount)
	{
		this.onlinePaymentAmount = onlinePaymentAmount;
	}

	public double getOnlinePaymentSurcharge()
	{
		return onlinePaymentSurcharge;
	}

	public void setOnlinePaymentSurcharge(final double onlinePaymentSurcharge)
	{
		this.onlinePaymentSurcharge = onlinePaymentSurcharge;
	}

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
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}

	/**
	 * @param division the division to set
	 */
	public void setDivision(String division) {
		this.division = division;
	}

	/**
	 * @return the distChannel
	 */
	public String getDistChannel() {
		return distChannel;
	}

	/**
	 * @param distChannel the distChannel to set
	 */
	public void setDistChannel(String distChannel) {
		this.distChannel = distChannel;
	}

	/**
	 * @return the salesOrg
	 */
	public String getSalesOrg() {
		return salesOrg;
	}

	/**
	 * @param salesOrg the salesOrg to set
	 */
	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}

	/**
	 * @return the orderAddress
	 */
	public List<AsahiOrderAddressRequest> getOrderAddress() {
		return orderAddress;
	}

	/**
	 * @param orderAddress the orderAddress to set
	 */
	public void setOrderAddress(List<AsahiOrderAddressRequest> orderAddress) {
		this.orderAddress = orderAddress;
	}
}

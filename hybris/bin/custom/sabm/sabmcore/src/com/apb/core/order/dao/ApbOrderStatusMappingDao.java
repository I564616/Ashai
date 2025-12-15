package com.apb.core.order.dao;

public interface ApbOrderStatusMappingDao
{

	String getOrderMapping(String backendStatusCode);

	String getDisplayOrderStatus(String statusCode, String companyCode);

}

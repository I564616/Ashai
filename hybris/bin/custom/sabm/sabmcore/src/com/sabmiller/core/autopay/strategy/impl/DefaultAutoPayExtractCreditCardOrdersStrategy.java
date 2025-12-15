package com.sabmiller.core.autopay.strategy.impl;

import de.hybris.platform.core.model.order.OrderModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sabmiller.core.autopay.strategy.SabmXlsFileGeneratorStrategy;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.integration.salesforce.SabmCSVUtils;


/**
 * @author marc.f.l.bautista
 *
 */
public class DefaultAutoPayExtractCreditCardOrdersStrategy implements SabmXlsFileGeneratorStrategy<OrderModel>
{
	private static final String SHEET_TITE = "CUB AutoPay Advantage Credit Card Orders";

	private static final String HEADER_PAYER_NUMBER = "Payer Number";
	private static final String HEADER_SOLD_TO_NUMBER = "Sold To Number";
	private static final String HEADER_ORDER_NUMBER = "Order Number";
	private static final String HEADER_AMOUNT = "Amount";


	/**
	 * Gets the file path for the XLSX file
	 *
	 * @return File
	 */
	@Override
	public File getFilePath()
	{
		return SabmCSVUtils.getFullPath(SabmCoreConstants.AUTOPAY_GENERATED_FILES_HYBRIS_FOLDER_ORDER_EXTRACT);
	}


	/**
	 * Gets the file name for the XLSX file
	 *
	 * @return String
	 */
	@Override
	public String getFileName()
	{
		return SabmCoreConstants.AUTOPAY_CREDIT_CARD_ORDERS_FILENAME;
	}


	/**
	 * Gets the workbook sheet title
	 *
	 * @return String
	 */
	@Override
	public String getSheetTitle()
	{
		return SHEET_TITE;
	}


	/**
	 * Gets header texts for the XLSX file
	 *
	 * @return List<String>
	 */
	@Override
	public List<String> getHeaderLine()
	{
		final List<String> headers = new ArrayList<>();
		headers.add(HEADER_PAYER_NUMBER);
		headers.add(HEADER_SOLD_TO_NUMBER);
		headers.add(HEADER_ORDER_NUMBER);
		headers.add(HEADER_AMOUNT);
		return headers;
	}


	/**
	 * Generates the XLSX file
	 *
	 * @param orders
	 * @return File
	 */
	@Override
	public File generateXlsxFile(final List<OrderModel> orders) throws Exception
	{
		return createExcelSheet(getOrdersData(orders));
	}


	/**
	 * Gets the list of order data
	 *
	 * @param orders
	 * @return List<List<String>>
	 */
	private List<List<Object>> getOrdersData(final List<OrderModel> orders)
	{
		final List<List<Object>> ordersData = new ArrayList<>();

		orders.stream().forEach(order -> {
			final List<Object> orderData = new ArrayList<>();
			orderData.add(order.getUnit().getPayerId());
			orderData.add(order.getUnit().getSoldto());
			orderData.add(order.getSapSalesOrderNumber());
			orderData.add(order.getTotalPrice());

			ordersData.add(orderData);
		});

		return ordersData;
	}

}
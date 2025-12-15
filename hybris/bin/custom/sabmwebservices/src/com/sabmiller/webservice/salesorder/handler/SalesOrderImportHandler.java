/**
 *
 */
package com.sabmiller.webservice.salesorder.handler;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sabmiller.facades.dataimport.response.SalesOrderDataImportResponse;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.SalesOrderImportRecordModel;
import com.sabmiller.webservice.response.SalesOrderImportResponse;
import com.sabmiller.webservice.salesorder.SalesOrder;


/**
 * Entry point for Order import from SAP to Hybris. This service can accept both Hyrbis and Non Hybris orders. For
 * orders that does not exist in the Hybris (non hybris orders), a new order will be created and persited in hybris.
 *
 * @author joshua.a.antony
 *
 */
public class SalesOrderImportHandler extends
		AbstractImportHandler<SalesOrder, SalesOrderImportResponse, SalesOrderImportRecordModel>
{

	@Resource(name = "b2bOrderFacade")
	private SABMOrderFacade orderFacade;

	@Resource(name = "salesOrderWsConverter")
	private Converter<SalesOrder, OrderData> saleOrderConverter;

	@Resource(name = "salesOrderImportRecordReverseConverter")
	private Converter<SalesOrderImportResponse, SalesOrderImportRecordModel> salesOrderImportRecordReverseConverter;


	@Override
	public SalesOrderImportResponse importEntity(final SalesOrder salesOrder)
	{
		final SalesOrderDataImportResponse response = orderFacade.persistOrder(saleOrderConverter.convert(salesOrder));

		final SalesOrderImportResponse salesOrderImportResponse = generateResponse(salesOrder, null, response.getExist());
		salesOrderImportResponse.setOrderId(response.getOrderId());
		salesOrderImportResponse.setSource(response.getSource());
		if (response.isFake())
		{
			salesOrderImportResponse.setStatus(DataImportStatusEnum.FAKE_SAP_REQUEST);
		}
		return salesOrderImportResponse;
	}

	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.SALES_ORDER_UPDATE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.webservice.importer.ImportResponseGenerator#generateResponse(java.lang.Object,
	 * java.lang.Exception, java.lang.Boolean)
	 */
	@Override
	public SalesOrderImportResponse generateResponse(final SalesOrder salesOrder, final Exception e, final Boolean entityExist)
	{
		final SalesOrderImportResponse response = new SalesOrderImportResponse();
		response.setSalesOrderNumber(salesOrder.getSalesOrderNumber());
		response.setCustomerId(salesOrder.getBuyerParty().getSoldTo());
		response.setError(e != null ? ExceptionUtils.getStackTrace(e) : null);
		response.setStatus(response.getError() != null ? DataImportStatusEnum.ERROR : DataImportStatusEnum.SUCCESS);
		if (entityExist != null)
		{
			response.setOperation(entityExist ? OperationEnum.UPDATE : OperationEnum.CREATE);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.AbstractImportHandler#getImportRecordReverseConverter()
	 */
	@Override
	public Converter<SalesOrderImportResponse, SalesOrderImportRecordModel> getImportRecordReverseConverter()
	{
		return salesOrderImportRecordReverseConverter;
	}
}

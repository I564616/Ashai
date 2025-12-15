/**
 *
 */
package com.sabmiller.webservice.dispatchinfo.handler;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sabmiller.facades.ordersplitting.ConsignmentFacade;
import com.sabmiller.webservice.dispatchinfo.Delivery;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.DispatchInfoImportRecordModel;
import com.sabmiller.webservice.response.DispatchInfoImportResponse;


/**
 * Entry point for the Delivery Dispatch info webservice. It is assumed that the Sales Order already exist in Hybris
 * prior to this service invocation. This service will be responsibility to setup consignments for the order.The status
 * received from the webservice are SHIPPED and RETURNED
 *
 * @author joshua.a.antony
 */
public class DispatchInfoImportHandler extends
		AbstractImportHandler<Delivery, DispatchInfoImportResponse, DispatchInfoImportRecordModel>
{

	@Resource(name = "sabConsignmentFacade")
	private ConsignmentFacade consignmentFacade;

	@Resource(name = "dispatchInfoConsignmentConverter")
	private Converter<Delivery, ConsignmentData> dispatchInfoConsignmentConverter;

	@Resource(name = "dispatchInfoImportRecordReverseConverter")
	private Converter<DispatchInfoImportResponse, DispatchInfoImportRecordModel> dispatchInfoImportRecordReverseConverter;


	@Override
	public DispatchInfoImportResponse importEntity(final Delivery delivery)
	{
		final ConsignmentData consignmentData = dispatchInfoConsignmentConverter.convert(delivery);
		consignmentFacade.processDispatchConsignment(consignmentData);

		return generateResponse(delivery, null, null);
	}

	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.DELIVERY_DISPATCH;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.ImportResponseGenerator#generateResponse(java.lang.Object,
	 * java.lang.Exception, java.lang.Boolean)
	 */
	@Override
	public DispatchInfoImportResponse generateResponse(final Delivery delivery, final Exception e, final Boolean entityExist)
	{
		final DispatchInfoImportResponse response = new DispatchInfoImportResponse();
		response.setDeliveryNumber(delivery.getDeliveryNumber());
		response.setSalesOrderNumber(delivery.getItem().get(0).getSalesOrderReference().getSalesOrderNumber());
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
	public Converter<DispatchInfoImportResponse, DispatchInfoImportRecordModel> getImportRecordReverseConverter()
	{
		return dispatchInfoImportRecordReverseConverter;
	}
}

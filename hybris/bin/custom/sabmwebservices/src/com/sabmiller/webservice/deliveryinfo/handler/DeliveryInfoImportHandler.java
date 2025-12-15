/**
 *
 */
package com.sabmiller.webservice.deliveryinfo.handler;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sabmiller.facades.ordersplitting.ConsignmentFacade;
import com.sabmiller.webservice.deliveryinfo.Delivery;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.DeliveryInfoImportRecordModel;
import com.sabmiller.webservice.response.DeliveryInfoImportResponse;


/**
 * Entry point for the Delivery info webservice. It is assumed that the Sales Order already exist in Hybris prior to
 * this service invocation. This service will be responsibility to setup consignments for the order.The status received
 * from the webservice are PROCESSING and CANCELLED
 *
 * @author joshua.a.antony
 */
public class DeliveryInfoImportHandler extends
		AbstractImportHandler<Delivery, DeliveryInfoImportResponse, DeliveryInfoImportRecordModel>
{

	@Resource(name = "sabConsignmentFacade")
	private ConsignmentFacade consignmentFacade;

	@Resource(name = "deliveryInfoConsignmentConverter")
	private Converter<Delivery, ConsignmentData> deliveryInfoConsignmentConverter;

	@Resource(name = "deliveryInfoImportRecordReverseConverter")
	private Converter<DeliveryInfoImportResponse, DeliveryInfoImportRecordModel> deliveryInfoImportRecordReverseConverter;


	@Override
	public DeliveryInfoImportResponse importEntity(final Delivery deliveryInfo)
	{
		final ConsignmentData consignmentData = deliveryInfoConsignmentConverter.convert(deliveryInfo);
		consignmentFacade.processDeliveryConsignment(consignmentData);

		return generateResponse(deliveryInfo, null, null);
	}

	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.DELIVERY_INFO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.ImportResponseGenerator#generateResponse(java.lang.Object,
	 * java.lang.Exception, java.lang.Boolean)
	 */
	@Override
	public DeliveryInfoImportResponse generateResponse(final Delivery deliveryInfo, final Exception e, final Boolean entityExist)
	{
		final DeliveryInfoImportResponse response = new DeliveryInfoImportResponse();
		response.setDeliveryNumber(deliveryInfo.getDeliveryNumber());
		if (deliveryInfo.getItem().get(0).getSalesOrderReference() != null)
		{
			response.setSalesOrderNumber(deliveryInfo.getItem().get(0).getSalesOrderReference().getSalesOrderNumber());
		}
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
	public Converter<DeliveryInfoImportResponse, DeliveryInfoImportRecordModel> getImportRecordReverseConverter()
	{
		return deliveryInfoImportRecordReverseConverter;
	}
}

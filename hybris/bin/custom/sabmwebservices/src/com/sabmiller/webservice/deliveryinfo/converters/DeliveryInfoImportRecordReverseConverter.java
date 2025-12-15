/**
 *
 */
package com.sabmiller.webservice.deliveryinfo.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.webservice.model.DeliveryInfoImportRecordModel;
import com.sabmiller.webservice.response.DeliveryInfoImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public class DeliveryInfoImportRecordReverseConverter implements
		Converter<DeliveryInfoImportResponse, DeliveryInfoImportRecordModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(DeliveryInfoImportRecordReverseConverter.class.getName());

	@Resource(name = "modelService")
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public DeliveryInfoImportRecordModel convert(final DeliveryInfoImportResponse source) throws ConversionException
	{
		final DeliveryInfoImportRecordModel model = modelService.create(DeliveryInfoImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public DeliveryInfoImportRecordModel convert(final DeliveryInfoImportResponse source,
			final DeliveryInfoImportRecordModel target) throws ConversionException
	{
		target.setSalesOrderNumber(source.getSalesOrderNumber());
		target.setDeliveryNumber(source.getDeliveryNumber());
		target.setDeliveryActionCode(source.getDeliveryActionCode());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());

		LOG.debug("salesOrderNumber : " + target.getSalesOrderNumber() + " , deliveryNumber : " + target.getDeliveryNumber()
				+ " , deliveryActionCode : " + target.getDeliveryActionCode() + " , error : " + target.getError() + " , operation : "
				+ target.getOperation() + " , status : " + target.getStatus());

		return target;
	}

}

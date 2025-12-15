/**
 *
 */
package com.sabmiller.webservice.dispatchinfo.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.webservice.model.DispatchInfoImportRecordModel;
import com.sabmiller.webservice.response.DispatchInfoImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public class DispatchInfoImportRecordReverseConverter implements
		Converter<DispatchInfoImportResponse, DispatchInfoImportRecordModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(DispatchInfoImportRecordReverseConverter.class.getName());

	@Resource(name = "modelService")
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public DispatchInfoImportRecordModel convert(final DispatchInfoImportResponse source) throws ConversionException
	{
		final DispatchInfoImportRecordModel model = modelService.create(DispatchInfoImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public DispatchInfoImportRecordModel convert(final DispatchInfoImportResponse source,
			final DispatchInfoImportRecordModel target) throws ConversionException
	{
		target.setSalesOrderNumber(source.getSalesOrderNumber());
		target.setDeliveryNumber(source.getDeliveryNumber());
		//target.setDeliveryActionCode(source.getDeliveryActionCode());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());

		LOG.debug("salesOrderNumber : " + target.getSalesOrderNumber() + " , deliveryNumber : " + target.getDeliveryNumber()
				+ " , deliveryActionCode : " + target.getDeliveryActionCode() + " , error : " + target.getError() + " , operation : "
				+ target.getOperation() + " , status : " + target.getStatus());

		return target;
	}

}

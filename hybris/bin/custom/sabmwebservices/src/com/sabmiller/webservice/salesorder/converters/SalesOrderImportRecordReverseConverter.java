/**
 *
 */
package com.sabmiller.webservice.salesorder.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.webservice.model.SalesOrderImportRecordModel;
import com.sabmiller.webservice.response.SalesOrderImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public class SalesOrderImportRecordReverseConverter implements Converter<SalesOrderImportResponse, SalesOrderImportRecordModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(SalesOrderImportRecordReverseConverter.class.getName());

	@Resource(name = "modelService")
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public SalesOrderImportRecordModel convert(final SalesOrderImportResponse source) throws ConversionException
	{
		final SalesOrderImportRecordModel model = modelService.create(SalesOrderImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public SalesOrderImportRecordModel convert(final SalesOrderImportResponse source, final SalesOrderImportRecordModel target)
			throws ConversionException
	{
		target.setSalesOrderNumber(source.getSalesOrderNumber());
		target.setOrderId(source.getOrderId());
		target.setCustomerId(source.getCustomerId());
		target.setSource(source.getSource());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());

		LOG.debug("salesOrderNumber : " + target.getSalesOrderNumber() + " , customerId : " + target.getCustomerId()
				+ " , error : " + target.getError() + " , operation : " + target.getOperation() + " , status : " + target.getStatus());

		return target;
	}

}

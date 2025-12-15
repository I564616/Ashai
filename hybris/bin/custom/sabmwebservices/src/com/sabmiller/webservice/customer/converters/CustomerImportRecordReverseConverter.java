/**
 *
 */
package com.sabmiller.webservice.customer.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.webservice.model.CustomerImportRecordModel;
import com.sabmiller.webservice.response.CustomerImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerImportRecordReverseConverter implements Converter<CustomerImportResponse, CustomerImportRecordModel>
{

	private final Logger LOG = Logger.getLogger(this.getClass());

	@Resource(name = "modelService")
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public CustomerImportRecordModel convert(final CustomerImportResponse source) throws ConversionException
	{
		final CustomerImportRecordModel model = modelService.create(CustomerImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CustomerImportRecordModel convert(final CustomerImportResponse source, final CustomerImportRecordModel target)
			throws ConversionException
	{
		target.setCustomerId(source.getCustomerId());
		target.setCustomerType(source.getCustomerType());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());

		LOG.info("customerId : " + target.getCustomerId() + " , customerType : " + target.getCustomerType() + " , error : "
				+ target.getError() + " , operation : " + target.getOperation() + " , status : " + target.getStatus());

		return target;
	}


}

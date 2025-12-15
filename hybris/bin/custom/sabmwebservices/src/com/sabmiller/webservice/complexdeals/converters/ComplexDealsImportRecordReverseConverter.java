/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.webservice.model.ComplexDealImportRecordModel;
import com.sabmiller.webservice.response.ComplexDealImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public class ComplexDealsImportRecordReverseConverter
		implements Converter<ComplexDealImportResponse, ComplexDealImportRecordModel>
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
	public ComplexDealImportRecordModel convert(final ComplexDealImportResponse source) throws ConversionException
	{
		final ComplexDealImportRecordModel model = modelService.create(ComplexDealImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ComplexDealImportRecordModel convert(final ComplexDealImportResponse source, final ComplexDealImportRecordModel target)
			throws ConversionException
	{
		target.setCode(source.getCode());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("code : " + target.getCode() + " , error : " + target.getError() + " , operation : " + target.getOperation()
					+ " , status : " + target.getStatus());

		}
		return target;
	}


}

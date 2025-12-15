/**
 *
 */
package com.sabmiller.webservice.complexdeals.handler;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.webservice.importer.ContextSupportImportHandler;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.dataimport.response.DataImportResponse;
import com.sabmiller.facades.deal.SABMDealsFacade;
import com.sabmiller.webservice.complexdeals.DealCondition;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.ComplexDealImportRecordModel;
import com.sabmiller.webservice.response.ComplexDealImportResponse;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Entry point for Deal Conditions Import from SAP to Hybris.This service is resposible for creating/updating deals
 * information.
 *
 * @author joshua.a.antony
 *
 */
public class ComplexDealsImportHandler extends
		AbstractImportHandler<DealCondition, ComplexDealImportResponse, ComplexDealImportRecordModel> implements ContextSupportImportHandler<DealCondition,ComplexDealImportResponse, DealsService.ImportContext>
{

	@Resource(name = "dealsFacade")
	private SABMDealsFacade dealsFacade;

	@Resource(name = "complexDealsWsConverter")
	private Converter<DealCondition, ComplexDealData> complexDealConverter;

	@Resource(name = "complexDealsImportRecordReverseConverter")
	private Converter<ComplexDealImportResponse, ComplexDealImportRecordModel> complexDealImportRecordReverseConverter;

	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.DEAL;
	}


	@Override
	public ComplexDealImportResponse importEntity(final DealCondition dealCondition)
	{
		//DO NOTHING
		return null;
	}


	@Override
	public ComplexDealImportResponse generateResponse(final DealCondition deal, final Exception e, final Boolean entityExist)
	{
		final ComplexDealImportResponse importResponse = new ComplexDealImportResponse();
		importResponse.setError(e != null ? ExceptionUtils.getStackTrace(e) : null);
		importResponse.setStatus(importResponse.getError() != null ? DataImportStatusEnum.ERROR : DataImportStatusEnum.SUCCESS);
		importResponse.setCode(deal.getConditionNumber());
		if (entityExist != null)
		{
			importResponse.setOperation(entityExist ? OperationEnum.UPDATE : OperationEnum.CREATE);
		}

		return importResponse;
	}

	@Override
	public Converter<ComplexDealImportResponse, ComplexDealImportRecordModel> getImportRecordReverseConverter()
	{
		return complexDealImportRecordReverseConverter;
	}

	@Override
	public ComplexDealImportResponse importEntity(DealCondition dealCondition, DealsService.ImportContext importContext) {
		final ComplexDealData complexDealsData = complexDealConverter.convert(dealCondition);
		final DataImportResponse importResponse = dealsFacade.importComplexDeal(complexDealsData,importContext);
		return generateResponse(dealCondition, null, importResponse.getExist());
	}

	@Override
	public DealsService.ImportContext createImportContext() {
		return dealsFacade.createImportContext();
	}


}

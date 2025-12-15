/**
 *
 */
package com.sabmiller.webservice.customer.handler;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.webservice.customer.helper.DealAssigneeRecalculationStrategy;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.CustomerImportRecordModel;
import com.sabmiller.webservice.response.CustomerImportResponse;


/**
 * Entry point for the Customer(B2BUnit) Import from SAP to Hybris. Customer in SAP is nothing but Organization
 * (B2BUnit). This service will be responsible to create ZALB and ZADP customers. If Primary Admin is supplied, the user
 * will also be created and associated to ZADP. The major information related to B2BUnit like Unloading Points, Carrier,
 * Address, Plant, Sales Org Data are available as part of this import.
 *
 * @author joshua.a.antony
 *
 */
public class CustomerImportHandler extends AbstractImportHandler<Customer, CustomerImportResponse, CustomerImportRecordModel>
{

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	@Resource(name = "customerWsConverter")
	private Converter<Customer, B2BUnitData> b2bUnitConverter;

	@Resource(name = "customerImportRecordReverseConverter")
	private Converter<CustomerImportResponse, CustomerImportRecordModel> customerImportRecordReverseConverter;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "dealAssigneeRecalculationStrategy")
	private DealAssigneeRecalculationStrategy dealAssigneeRecalculationStrategy;

	@Resource(name = "sabmB2BUnitService")
	private SabmB2BUnitService sabmB2BUnitService;

	@Override
	public CustomerImportResponse importEntity(final Customer customer)
	{
		final B2BUnitData b2bUnitData = b2bUnitConverter.convert(customer);
		final B2BUnitModel unit =  sabmB2BUnitService.getUnitForUid(b2bUnitData.getUid());

		final boolean entityExist = unit != null;
		boolean requiresDealAssigneeRecalculation = true;

		if (entityExist)
		{
			requiresDealAssigneeRecalculation = dealAssigneeRecalculationStrategy.requiresDealAssigneeRecalculation(unit,customer);
			b2bUnitFacade.updateB2BUnit(b2bUnitData,unit);
		}
		else
		{
			b2bUnitFacade.createB2BUnit(b2bUnitData);
		}

		if(requiresDealAssigneeRecalculation){
				dealAssigneeRecalculationStrategy.recalculateDealAssignees();
		}

		return generateResponse(customer, null, entityExist);
	}

	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.CUSTOMER;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.ImportResponseGenerator#generateResponse(java.lang.Object,
	 * java.lang.Exception, java.lang.Boolean)
	 */
	@Override
	public CustomerImportResponse generateResponse(final Customer customer, final Exception e, final Boolean entityExist)
	{
		final CustomerImportResponse response = new CustomerImportResponse();
		response.setCustomerId(customer.getID().getValue());
		response.setCustomerType(customer.getAccountGroup());
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
	public Converter<CustomerImportResponse, CustomerImportRecordModel> getImportRecordReverseConverter()
	{
		return customerImportRecordReverseConverter;
	}

}

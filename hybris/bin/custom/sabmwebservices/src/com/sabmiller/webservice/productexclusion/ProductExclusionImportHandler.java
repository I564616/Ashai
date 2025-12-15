/**
 *
 */
package com.sabmiller.webservice.productexclusion;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.regioncache.CacheController;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.customer.ProductExclusionData;
import com.sabmiller.facades.customer.ProductExclusionImportData;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.ProductExclusionImportRecordModel;
import com.sabmiller.webservice.response.ProductExclusionImportResponse;


/**
 * The Class ProductExclusionImportHandler.
 */
public class ProductExclusionImportHandler
		extends AbstractImportHandler<ProductExclusionResponse, ProductExclusionImportResponse, ProductExclusionImportRecordModel>
{

	/** The customer facade. */
	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade customerFacade;


	/** The jdbc template, used to run raw sql. */
	@Resource
	private JdbcTemplate jdbcTemplate;

	/** The cache controller. */
	@Resource(name = "defaultCacheController")
	private CacheController cacheController;

	/** The product exclusion import record reverse converter. */
	@Resource(name = "productExclusionImportRecordReverseConverter")
	private Converter<ProductExclusionImportResponse, ProductExclusionImportRecordModel> productExclusionImportRecordReverseConverter;

	/** The cup reverse converter. */
	@Resource(name = "productExclusionWsConverter")
	private Converter<ProductExclusionResponse, ProductExclusionImportData> productExclReverseConverter;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;







	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.AbstractImportHandler#getEntityType()
	 */
	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.PRODUCT_EXCLUSION;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.ImportResponseGenerator#generateResponse(java.lang.Object,
	 * java.lang.Exception, java.lang.Boolean)
	 */

	private ProductExclusionImportResponse generateResponse(final ProductExclusionResponse entity, final Exception e,
			final Boolean entityExist, final String errors)
	{
		final ProductExclusionImportResponse response = new ProductExclusionImportResponse();
		response.setCustomerId(entity.getProductExclusionHeader().getCustomer());
		//response.setProduct(entity.getMaterial());
		response.setOperation(OperationEnum.CREATE);

		if (e != null || StringUtils.isNotBlank(errors))
		{
			if (e != null)
			{
				response.setError(errors + "=====Exception::" + ExceptionUtils.getStackTrace(e));
			}
			else
			{
				response.setError(errors);
			}


			response.setStatus(DataImportStatusEnum.ERROR);
		}
		else
		{
			response.setStatus(DataImportStatusEnum.SUCCESS);
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.AbstractImportHandler#getImportRecordReverseConverter()
	 */
	@Override
	public Converter<ProductExclusionImportResponse, ProductExclusionImportRecordModel> getImportRecordReverseConverter()
	{
		return productExclusionImportRecordReverseConverter;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.AbstractImportHandler#importEntity(java.lang.Object)
	 */
	@Override
	public ProductExclusionImportResponse importEntity(final ProductExclusionResponse entity)
	{
		String errors = "";

		try
		{
			B2BUnitModel unitModel = null;
			final String customerId = entity.getProductExclusionHeader() != null ? entity.getProductExclusionHeader().getCustomer()
					: "";
			if (StringUtils.isNotEmpty(customerId))
			{
				try
				{
					//Getting the B2BUnit using by uid, in case of exception setting null to the target
					unitModel = b2bUnitService.getUnitForUid(customerId);
				}
				catch (final AmbiguousIdentifierException e)
				{
					LOG.error("Customer not sent correctly from SAP for product exclusions");
				}
			}
			if (unitModel != null)
			{
				LOG.debug("Product Exclusions import started for the customer" + customerId);

				final ProductExclusionImportData productExclImportData = productExclReverseConverter.convert(entity);
				final Set<ProductExclusionData> dataList = productExclImportData.getProductExclusionDataList();

				customerFacade.createProductExclusion(dataList, unitModel);
				errors = productExclImportData.getProcessingErrors();

				LOG.debug("Product Exclusions import ended for the customer" + customerId);

			}
			else
			{
				errors = "Customer not found for product exclusion import";
				LOG.error(errors);
			}
		}
		catch (final Exception e)
		{
			LOG.error("ERROR while loading exclusion product" + e.getMessage());
			return generateResponse(entity, e, null, errors);
		}
		return generateResponse(entity, null, null, errors);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.importer.ImportResponseGenerator#generateResponse(java.lang.Object,
	 * java.lang.Exception, java.lang.Boolean)
	 */
	@Override
	public ProductExclusionImportResponse generateResponse(final ProductExclusionResponse entity, final Exception e,
			final Boolean entityExist)
	{
		// YTODO Auto-generated method stub
		return generateResponse(entity, e, null, null);
	}

}

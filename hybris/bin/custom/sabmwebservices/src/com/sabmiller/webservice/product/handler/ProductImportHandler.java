/**
 *
 */
package com.sabmiller.webservice.product.handler;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sabmiller.facades.product.SabmProductFacade;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.importer.DataImportValidationException;
import com.sabmiller.webservice.model.ProductImportRecordModel;
import com.sabmiller.webservice.product.Material;
import com.sabmiller.webservice.response.ProductImportResponse;


/**
 * Entry point for Product Import from SAP to Hybris.This service is resposible for creating/updating product
 * information. It should be noted that all the operations on the product are performed only on the staging catalog. It
 * is assumed that there is a manual process of synching data from stating to online catalog.
 *
 * @author joshua.a.antony
 *
 */
public class ProductImportHandler extends AbstractImportHandler<Material, ProductImportResponse, ProductImportRecordModel>
{

	@Resource(name = "productFacade")
	private SabmProductFacade productFacade;

	@Resource(name = "productWsConverter")
	private Converter<Material, ProductData> productConverter;

	@Resource(name = "productImportResponseReverseConverter")
	private Converter<ProductImportResponse, ProductImportRecordModel> productImportResponseReverseConverter;

	@Override
	public EntityTypeEnum getEntityType()
	{
		return EntityTypeEnum.PRODUCT;
	}


	@Override
	public ProductImportResponse importEntity(final Material material)
	{
		boolean productExist = false;
		try
		{
			final ProductData productData = productConverter.convert(material);
			productExist = productFacade.productExistInOfflineCatalog(productData.getInternalId());
			productFacade.saveProduct(productData);
		}

		catch (final DataImportValidationException e)
		{
			LOG.info("EAN not available in request:" + e.getMessage());
			return generateResponse(material, e, Boolean.valueOf(productExist));
		}

		LOG.info("EAN available in request");
		return generateResponse(material, null, Boolean.valueOf(productExist));

	}


	@Override
	public ProductImportResponse generateResponse(final Material material, final Exception e, final Boolean entityExist)
	{
		final ProductImportResponse importResponse = new ProductImportResponse();
		importResponse.setError(e != null ? ExceptionUtils.getStackTrace(e) : null);
		//INC0607523: Product Import Failures Error status update on webservice log entry
		//importResponse.setStatus(importResponse.getError() != null ? DataImportStatusEnum.SUCCESS : DataImportStatusEnum.SUCCESS);
		importResponse.setStatus(importResponse.getError() != null ? DataImportStatusEnum.ERROR : DataImportStatusEnum.SUCCESS);
		importResponse.setMaterialId(material.getInternalID().getValue());
		if (entityExist != null)
		{
			importResponse.setOperation(entityExist ? OperationEnum.UPDATE : OperationEnum.CREATE);
		}
		if (material.getGeneralData() != null)
		{
			importResponse.setHierarchy(material.getGeneralData().get(0).getProductHierarchy());
		}

		return importResponse;
	}

	@Override
	public Converter<ProductImportResponse, ProductImportRecordModel> getImportRecordReverseConverter()
	{
		return productImportResponseReverseConverter;
	}


}

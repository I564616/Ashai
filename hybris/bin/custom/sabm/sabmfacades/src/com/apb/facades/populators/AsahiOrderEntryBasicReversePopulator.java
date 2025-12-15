package com.apb.facades.populators;

import jakarta.annotation.Resource;

import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

public class AsahiOrderEntryBasicReversePopulator implements Populator<OrderEntryData, AbstractOrderEntryModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(AsahiOrderEntryBasicReversePopulator.class.getName());
	/** The Constant CODE_COMPANY_CATALOG_ID. */
	private static final String CODE_COMPANY_CATALOG_ID = ".company.catalog.id";
	
	/** The Constant CATALOG_VERSION. */
	private static final String CATALOG_VERSION = "Online";
	
	/** The product service. */
	@Resource(name = "apbProductReferenceService")
	private ApbProductReferenceService apbProductReferenceService;
	
	/** The catalog version service. */
	@Resource(name="catalogVersionService")
	private CatalogVersionService catalogVersionService;
	
	/** The entry number key generator. */
	@Resource(name="entryNumberKeyGenerator")
	private PersistentKeyGenerator entryNumberKeyGenerator;
	
	/** The asahi configuration service. */
	@Resource(name="asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The product service. */
	@Resource(name="productService")
	private ProductService productService;
	
	/** The model service. */
	@Resource(name="modelService")
	private ModelService modelService;
	
	public void populate(OrderEntryData source, AbstractOrderEntryModel target)
			throws ConversionException 
	{
		if(null != target && null != source.getProduct())
		{
			updateLineEntry(source, target);
		}
		
	}
	
	protected void updateLineEntry(OrderEntryData source, AbstractOrderEntryModel target) 
	{
		StringBuilder catalogConfigKeyWithCompanyCode = new StringBuilder(source.getCompanyCode()).append(CODE_COMPANY_CATALOG_ID);
		ProductModel productModel = this.apbProductReferenceService.getProductForCode(this.catalogVersionService.getCatalogVersion(this.asahiConfigurationService.getString(catalogConfigKeyWithCompanyCode.toString(), "apbProductCatalog"), CATALOG_VERSION), source.getProduct().getCode());
		if(null != productModel)
		{
			this.populatingBasicEntryAttributes(source, target);
			
			//populating Product attributes
			this.populateProductAndUnit(source, target, productModel);
			this.modelService.save(target);
		}		
	}

	/**
	 * Populating basic entry attributes.
	 *
	 * @param source the source
	 * @param target the target
	 */
	private void populatingBasicEntryAttributes(OrderEntryData source,
			AbstractOrderEntryModel target) 
	{
		target.setEntryNumber(Integer.parseInt(this.entryNumberKeyGenerator.generate().toString()));
		target.setLineNum(source.getLineNum()!=null?source.getLineNum():target.getLineNum());
		target.setBackendUid(source.getBackendUid()!=null?source.getBackendUid():target.getBackendUid());	
		target.setQuantity(source.getQuantity()!=null?source.getQuantity():target.getQuantity());
		target.setPickinglistQty(source.getPickinglistQty()!=null?source.getPickinglistQty():target.getPickinglistQty());
		target.setInventoryTransId(source.getInventoryTransId()!=null?source.getInventoryTransId():target.getInventoryTransId());
		if(null!=source.getIsBonusStock()){
			target.setIsBonusStock(source.getIsBonusStock());
		}
	}
		
	/**
	 * Populate product and unit.
	 *
	 * @param source the source
	 * @param target the target
	 * @param productModel the product model
	 */
	private void populateProductAndUnit(OrderEntryData source,
			AbstractOrderEntryModel target, ProductModel productModel) 
	{
		if(null != source.getProduct())
		{	
			target.setProduct(productModel);
			target.setQuantity(source.getQuantity());
			if(null != productModel.getUnit()){
				target.setUnit(productModel.getUnit());
			}
			else
			{
				try {
					target.setUnit(this.productService.getOrderableUnit(productModel));
				}
				catch(ModelNotFoundException e){
					LOG.info("No orderable unit found for this product"+productModel.getCode());
				}			
			}
		}
	}
}

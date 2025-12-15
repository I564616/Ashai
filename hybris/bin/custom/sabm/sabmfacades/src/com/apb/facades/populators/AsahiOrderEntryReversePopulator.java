/*
 * 
 */
package com.apb.facades.populators;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.enums.TaxType;
import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.TaxValue;

/**
 * The Class AsahiOrderEntryReversePopulator.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiOrderEntryReversePopulator extends AsahiOrderEntryBasicReversePopulator implements Populator<OrderEntryData, AbstractOrderEntryModel>{
	
	/** The Constant CODE_COMPANY_CATALOG_ID. */
	private static final String CODE_COMPANY_CATALOG_ID = ".company.catalog.id";
	
	/** The Constant CATALOG_VERSION. */
	private static final String CATALOG_VERSION = "Online";
	
	/** The Constant PRODUCT_CODE_FOR_WET. */
	public static final String PRODUCT_CODE_FOR_WET = "product.code.for.wet.apb";
	
	/** The Constant STATUS_INVOICE_GENERATED. */
	private static final String STATUS_INVOICE_GENERATED = "50";
	
	/** The Constant DEFAULT_COMPANY_CODE. */
	private static final String DEFAULT_COMPANY_CODE = "apb";
	
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

	/**
	 * Populate.
	 *
	 * @param source the source
	 * @param target the target
	 * @throws ConversionException the conversion exception
	 */
	public void populate(OrderEntryData source, AbstractOrderEntryModel target)
			throws ConversionException 
	{
		if(null!=source.getProduct())
		{
			this.updateLineEntry(source, target);
		}
	}

	/**
	 * Update line entry.
	 *
	 * @param source the source
	 * @param target the target
	 */
	protected void updateLineEntry(OrderEntryData source, AbstractOrderEntryModel target) 
	{
		StringBuilder catalogConfigKeyWithCompanyCode = new StringBuilder(source.getCompanyCode()).append(CODE_COMPANY_CATALOG_ID);
		
		if(null != source.getWetItem() && !source.getWetItem().isEmpty() && null != source.getOrderId())
		{
			//Getting Order Entry based on ItemId and OrderId
			ProductModel productModel = apbProductReferenceService.getProductForCode(this.catalogVersionService.getCatalogVersion(this.asahiConfigurationService.getString(catalogConfigKeyWithCompanyCode.toString(), "apbProductCatalog"), CATALOG_VERSION),source.getWetItem());

			List<OrderEntryModel> entries = apbProductReferenceService.getOrderEntryByProductAndOrderId(productModel.getPk().toString(),source.getOrderId());
			if(entries.size() == 1)
			{
				OrderEntryModel entry = entries.get(0);
				//populating gst and wet
				this.updatingGSTAndWET(source, entry);
				
				this.modelService.save(entry);
			}
			else if(entries.size() > 1)
			{
				entries.stream().forEach(entry -> entry.setWetNotIncluded(true));
			}

		}
		else
		{
			//populating invoiced quantity and entry status
			if(STATUS_INVOICE_GENERATED.equalsIgnoreCase(source.getOrderStatus()) && DEFAULT_COMPANY_CODE.equalsIgnoreCase(source.getCompanyCode())){
				populatingInvoicedQtyAndStatus(source, target);
			}
			if(!DEFAULT_COMPANY_CODE.equalsIgnoreCase(source.getCompanyCode())){
				if(STATUS_INVOICE_GENERATED.equalsIgnoreCase(source.getOrderStatus())){
					target.setStatus(OrderEntryStatus.SUPPLIED);
				}else{
					target.setStatus(OrderEntryStatus.NOTSUPPLIED);
				}
			}
			if(null != source.getIsBonusStock()) {
				target.setIsBonusStock(source.getIsBonusStock());
			}
			
			super.updateLineEntry(source, target);
			
			//populating price values
			this.populatingPrices(source, target);
			
			//populating gst
			this.populatingGST(source, target);
			
			this.modelService.save(target);
		}
		if(null!=target){
			target.setCdl(source.getCdl());
			this.modelService.save(target);
		}
	}


	/**
	 * Populating prices.
	 *
	 * @param source the source
	 * @param target the target
	 */
	private void populatingPrices(OrderEntryData source, AbstractOrderEntryModel target) 
	{
		target.setNetUnitPrice(source.getNetUnitPrice()!=null?source.getNetUnitPrice():target.getNetUnitPrice());
		target.setBasePrice(source.getNetUnitPrice()!=null?source.getNetUnitPrice():target.getNetUnitPrice());
		target.setNetLineOrderAmount(source.getNetLineOrderAmount()!=null?source.getNetLineOrderAmount():target.getNetLineOrderAmount());
		target.setTotalPrice(source.getNetLineInvoiceAmount()!=null?source.getNetLineInvoiceAmount():target.getNetLineInvoiceAmount());
		target.setNetLineInvoiceAmount(source.getNetLineInvoiceAmount()!=null?source.getNetLineInvoiceAmount():target.getNetLineInvoiceAmount());		
	}

	/**
	 * Populating invoiced qty and status.
	 *
	 * @param source the source
	 * @param target the target
	 */
	private void populatingInvoicedQtyAndStatus(OrderEntryData source, AbstractOrderEntryModel target) 
	{
		Integer invoicedQty = null != source.getInvoicedQty() ? source.getInvoicedQty() : 0;
		Long orderedQty = null != source.getQuantity() ? source.getQuantity() : 0L;
		target.setInvoicedQty(invoicedQty);
		
		if(orderedQty > 0 && invoicedQty == 0)
		{
			target.setStatus(OrderEntryStatus.NOTSUPPLIED);
			target.setInvoicedQty(invoicedQty);
			target.setTotalPrice(0D);
			target.setOrderEntryWET(0D);
		}
		else if(null != target.getStatus() && target.getStatus().equals(OrderEntryStatus.ADDED))
		{
			target.setStatus(invoicedQty < orderedQty ? OrderEntryStatus.ADDEDSHORT : OrderEntryStatus.ADDED);
		}
		else if(orderedQty > invoicedQty)
		{
			target.setStatus(null != target.getStatus() && target.getStatus().equals(OrderEntryStatus.UPDATED) ? OrderEntryStatus.UPDATEDSHORT :OrderEntryStatus.REDUCED);
		}
		else
		{
			target.setStatus(null != target.getStatus() && target.getStatus().equals(OrderEntryStatus.UPDATED) ? OrderEntryStatus.UPDATED : OrderEntryStatus.SUPPLIED);
		}
		
	}

	/**
	 * Populating GST and WET.
	 *
	 * @param source the source
	 * @param target the target
	 */
	private void updatingGSTAndWET(OrderEntryData source,
			AbstractOrderEntryModel target) {
		target.setOrderEntryGST(source.getOrderEntryGST()!=null?source.getOrderEntryGST():target.getOrderEntryGST());
		target.setOrderEntryWET(source.getNetLineInvoiceAmount()!=null?source.getNetLineInvoiceAmount():target.getNetLineInvoiceAmount());
		
		TaxValue gst = new TaxValue(TaxType.GST.getCode(), target.getOrderEntryGST(), true, source.getCurrencyIso());
		TaxValue wet = new TaxValue(TaxType.WET.getCode(), source.getNetLineInvoiceAmount()!=null?source.getNetLineInvoiceAmount():target.getNetLineInvoiceAmount(), 
				true, source.getCurrencyIso());
		List<TaxValue> taxList = new ArrayList<>();
		taxList.add(gst);
		taxList.add(wet);
		target.setTaxValues(taxList);
	}
	
	/**
	 * Populating GST.
	 *
	 * @param source the source
	 * @param target the target
	 */
	private void populatingGST(OrderEntryData source,
			AbstractOrderEntryModel target) 
	{
		target.setOrderEntryGST(source.getOrderEntryGST()!=null?source.getOrderEntryGST():target.getOrderEntryGST());
		TaxValue gst = new TaxValue(TaxType.GST.getCode(), source.getOrderEntryGST()!=null?source.getOrderEntryGST():target.getOrderEntryGST(), 
				true, null);
		List<TaxValue> taxList = new ArrayList<>();
		taxList.add(gst);
		target.setTaxValues(taxList);
	}
}
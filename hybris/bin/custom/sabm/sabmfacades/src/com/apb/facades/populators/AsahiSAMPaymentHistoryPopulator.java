package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.service.b2bunit.ApbB2BUnitService;

/**
 * The Class AsahiSAMPaymentHistReversePopulator.
 * 
 * @author Pankaj gandhi
 */
public class AsahiSAMPaymentHistoryPopulator implements Populator<AsahiSAMPaymentModel, AsahiSAMPaymentData>{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMPaymentHistoryPopulator.class);
	
	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "site.date.format.sga";
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The enumeration service. */
	@Resource(name="enumerationService")
	private EnumerationService enumerationService;
	
	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMInvoiceModel, AsahiSAMInvoiceData> asahiSAMInvoiceConverter;
	
	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;
	
	/** The model service. */
	@Resource
	private ModelService modelService;
	
	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	/**
	 * Populate.
	 *
	 * @param source the source
	 * @param target the target
	 * @throws ConversionException the conversion exception
	 */
	@Override
	public void populate(AsahiSAMPaymentModel source,
			AsahiSAMPaymentData target) throws ConversionException {
		
		if (null != source.getReceiptNumber()) {
			target.setReceiptNumber(source.getReceiptNumber());
		}
		if (null != source.getClrDocNumber()) {
			target.setClearingdocumentNumber(source.getClrDocNumber());
		}
		if (null != source.getAmount()) {
			target.setAmount(source.getAmount());
		}
		if (null != source.getPaymentReference()) {
			target.setPaymentReference(source.getPaymentReference());
		}
		if (null != source.getCustAccount().getAccountNum()) {
			target.setCustAccount(source.getCustAccount().getAccountNum());
		}
		
		if(source.getPaymentType() != null) {
			target.setPaymentType(enumerationService.getEnumerationName(source.getPaymentType()));
		}
		
		if(null != source.getTransactionDate()) {
			//Getting SGA Site Date Format
			SimpleDateFormat format = new SimpleDateFormat(this.asahiConfigurationService.getString(DATE_FORMAT, "dd/MM/yyyy"));
			target.setTransactionDate(format.format(source.getTransactionDate()).toString());
		}
		
		//populating invoice details
		if(!CollectionUtils.isEmpty(source.getSamInvoice())){
			this.populatingInvoices(source.getSamInvoice(),target);
		}
	}
	/**
	 * Populating invoices.
	 *
	 * @param invoiceModels the invoice data list
	 * @param target the target
	 */
	private void populatingInvoices(final Collection<AsahiSAMInvoiceModel> invoiceModels, final AsahiSAMPaymentData target) {
		
		final List<AsahiSAMInvoiceData> invoiceDataList = new ArrayList<>();
		
		for(AsahiSAMInvoiceModel invoiceModel : invoiceModels){
			final AsahiSAMInvoiceData invoiceData = new AsahiSAMInvoiceData();			
			invoiceDataList.add(getAsahiSAMInvoiceConverter().convert(invoiceModel,invoiceData));
		}
		target.setInvoice(invoiceDataList);
	}
	/**
	 * @return the asahiSAMInvoiceConverter
	 */
	public Converter<AsahiSAMInvoiceModel, AsahiSAMInvoiceData> getAsahiSAMInvoiceConverter() {
		return asahiSAMInvoiceConverter;
	}

	/**
	 * @param asahiSAMInvoiceConverter the asahiSAMInvoiceConverter to set
	 */
	public void setAsahiSAMInvoiceConverter(
			Converter<AsahiSAMInvoiceModel, AsahiSAMInvoiceData> asahiSAMInvoiceConverter) {
		this.asahiSAMInvoiceConverter = asahiSAMInvoiceConverter;
	}

}

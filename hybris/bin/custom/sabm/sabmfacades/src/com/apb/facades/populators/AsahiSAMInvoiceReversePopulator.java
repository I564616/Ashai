package com.apb.facades.populators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.enums.AsahiSAMDocumentType;
import com.sabmiller.core.enums.AsahiSAMInvoiceStatus;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.service.b2bunit.ApbB2BUnitService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * The Class AsahiSAMInvoiceReversePopulator.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiSAMInvoiceReversePopulator implements Populator<AsahiSAMInvoiceData, AsahiSAMInvoiceModel>{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMInvoiceReversePopulator.class);
	
	/** The Constant INVOICE_OPEN_STATUS. */
	private static final String INVOICE_OPEN_STATUS = "11";
	
	/** The Constant INVOICE_CLOSED_STATUS. */
	private static final String INVOICE_CLOSED_STATUS = "10";
	
	/** The Constant SAM_DOCUMENT_ECC_DATEPATTERN. */
	private static final String SAM_DOCUMENT_ECC_DATEPATTERN = "dd/MM/yyyy";
	
	/** The Constant SAM_DOCUMENT_HYBRIS_DATEPATTERN. */
	private static final String SAM_DOCUMENT_HYBRIS_DATEPATTERN = "yyyy-MM-dd HH:mm:ss";
		
	/** The enumeration service. */
	@Resource(name="enumerationService")
	private EnumerationService enumerationService;
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
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
	public void populate(AsahiSAMInvoiceData source, AsahiSAMInvoiceModel target)
			throws ConversionException {
		
		target.setCustAccount(this.apbB2BUnitService.getB2BUnitByAccountNumber(source.getCustAccount()));
		target.setSoldToName(source.getSoldToName());
		
		target.setDeliveryNumber(source.getDeliveryNumber());
		target.setDocumentNumber(source.getDocumentNumber());
		target.setLineNumber(source.getLineNumber());
		target.setPaymentMade(false);
		if(null != source.getDebitInvoiceRef())
			target.setDebitInvoiceRef(source.getDebitInvoiceRef());

		
		if(INVOICE_OPEN_STATUS.equalsIgnoreCase(source.getStatus())){
			target.setRemainingAmount(source.getInvoiceAmount());
		}
		if(INVOICE_CLOSED_STATUS.equalsIgnoreCase(source.getStatus())){
			target.setRemainingAmount(source.getInvoiceAmount());
			target.setPaidAmount(source.getInvoiceAmount());
		}
		
		target.setDocumentType(this.enumerationService.getEnumerationValue(AsahiSAMDocumentType.class,source.getInvoiceIndicator()));
		target.setStatus(this.enumerationService.getEnumerationValue(AsahiSAMInvoiceStatus.class,source.getStatus()));
		
		try
		{
			//Getting Site Date Format
			SimpleDateFormat sdf1 = new SimpleDateFormat(SAM_DOCUMENT_ECC_DATEPATTERN);
			SimpleDateFormat sdf2 = new SimpleDateFormat(SAM_DOCUMENT_HYBRIS_DATEPATTERN);
			
			String date;
			 
			if(null!=source.getInvoiceDate()){
				date = sdf2.format(sdf1.parse(source.getInvoiceDate()));
				target.setInvoiceDate(sdf2.parse(date));
			}
			if(null!=source.getInvoiceDueDate()){
				date = sdf2.format(sdf1.parse(source.getInvoiceDueDate()));
				target.setInvoiceDueDate(sdf2.parse(date));
			}
			if(null!=source.getDocumentPostedDate()){
				date = sdf2.format(sdf1.parse(source.getDocumentPostedDate()));
				target.setDocumentPostedDate(sdf2.parse(date));
			}
		}
		catch (ParseException exp)
		{
			logger.error("Parse Exception occured in AsahiSAMInvoiceReversePopulator" + exp.getMessage());
		}
	}

}

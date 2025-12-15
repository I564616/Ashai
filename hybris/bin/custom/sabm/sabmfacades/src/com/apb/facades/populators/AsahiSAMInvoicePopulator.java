package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.service.b2bunit.ApbB2BUnitService;


/**
 * The Class AsahiSAMInvoicePopulator.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMInvoicePopulator implements Populator<AsahiSAMInvoiceModel, AsahiSAMInvoiceData>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiSAMInvoicePopulator.class);

	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "site.date.format.sga";

	/** The Constant ASAHI_SAM_DOCUMENTTYPE_CREDITCODE. */
	private static final String ASAHI_SAM_DOCUMENTTYPE_CREDITCODE = "10";
	
	/** The Constant ASAHI_SAM_DOCUMENTTYPE_PARTIAL_PAYMENT. */
	private static final String ASAHI_SAM_DOCUMENTTYPE_PARTIAL_PAYMENT = "12";
	
	/** The Constant SAM_DOWNLOAD_LINK_ENABLE_DATE. */
	private static final String SAM_DOWNLOAD_LINK_ENABLE_DATE ="invoice.download.enable.link.date.sam.sga";
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private EnumerationService enumerationService;
	
	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	/**
	 * Populate.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 * @throws ConversionException
	 *            the conversion exception
	 */
	@Override
	public void populate(final AsahiSAMInvoiceModel source, final AsahiSAMInvoiceData target)
	{

		final SimpleDateFormat formatDDMMYYYY = new SimpleDateFormat(
				this.asahiConfigurationService.getString(DATE_FORMAT, "dd/MM/yyyy"));

		target.setDeliveryNumber(source.getDeliveryNumber());
		target.setDocumentNumber(source.getDocumentNumber());
		target.setPaymentMade(source.isPaymentMade());
		target.setLineNumber(source.getLineNumber());
		if(null != source.getDebitInvoiceRef())
			target.setDebitInvoiceRef(source.getDebitInvoiceRef());
		
		if (null != source.getDocumentType())
		{
			target.setDocumentType(enumerationService.getEnumerationName(source.getDocumentType()));

			if (ASAHI_SAM_DOCUMENTTYPE_CREDITCODE.equalsIgnoreCase(source.getDocumentType().getCode()))
			{
				target.setRemainingAmount(StringUtils.isNotEmpty(source.getRemainingAmount()) ? 
						("-").concat(source.getRemainingAmount()) : 
						StringUtils.EMPTY);
				target.setPaidAmount(StringUtils.isNotEmpty(source.getPaidAmount()) ? 
						("-").concat(source.getPaidAmount()) : 
						StringUtils.EMPTY);
			}else if(ASAHI_SAM_DOCUMENTTYPE_PARTIAL_PAYMENT.equalsIgnoreCase(source.getDocumentType().getCode()))
			{
				target.setRemainingAmount(StringUtils.isNotEmpty(source.getRemainingAmount()) ? 
						source.getRemainingAmount() : 
						StringUtils.EMPTY);
				target.setPaidAmount(StringUtils.isNotEmpty(source.getPaidAmount()) ? 
						source.getPaidAmount() : 
						StringUtils.EMPTY);
			}
			else
			{
				target.setRemainingAmount(StringUtils.isNotEmpty(source.getRemainingAmount()) ? 
						source.getRemainingAmount() : 
						StringUtils.EMPTY);
				target.setPaidAmount(StringUtils.isNotEmpty(source.getPaidAmount()) ? 
						source.getPaidAmount() : 
						StringUtils.EMPTY);
			}

		}
		if (null != source.getStatus())
		{
			target.setStatus(enumerationService.getEnumerationName(source.getStatus()));
		}
		if (null != source.getInvoiceDate())
		{
			//Getting Site Date Format
			target.setInvoiceDate(formatDDMMYYYY.format(source.getInvoiceDate()));
			
			try
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date linkEnableDate = formatter.parse(this.asahiConfigurationService.getString(SAM_DOWNLOAD_LINK_ENABLE_DATE, "10/10/2018"));
				Date invoiceDate = formatter.parse(target.getInvoiceDate());
				
				if(linkEnableDate.before(invoiceDate)){
					target.setEnableDownloadLink(true);
				}else{
					target.setEnableDownloadLink(false);
				}
				
			}catch (final ParseException e)
			{
				LOGGER.error("Parse Exception caught in converting date pattern" + e.getMessage());
			}
		}
		if (null != source.getInvoiceDueDate())
		{
			target.setInvoiceDueDate(formatDDMMYYYY.format(source.getInvoiceDueDate()));

			try
			{
				if (formatDDMMYYYY.parse(formatDDMMYYYY.format(source.getInvoiceDueDate()))
						.compareTo(formatDDMMYYYY.parse(formatDDMMYYYY.format(new Date()))) < 0)
				{
					target.setOverdue(true);
				}
			}
			catch (final ParseException e)
			{
				LOGGER.error("Parse Exception caught in converting date pattern" + e.getMessage());
			}
		}

		if(null!=source.getSoldToName()){
			target.setSoldToAccount(source.getSoldToName());
		}else{
			target.setSoldToAccount(source.getCustAccount().getLocName());
		}
	}
}

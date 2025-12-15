package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.enums.AsahiSAMPaymentType;
import com.sabmiller.core.model.AsahiSAMPaymentModel;


/**
 * The Class AsahiSAMPaymentHistReversePopulator.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMPaymentHistReversePopulator implements Populator<AsahiSAMInvoiceData, AsahiSAMPaymentModel>
{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMPaymentHistReversePopulator.class);

	/** The Constant SAM_PAYMENT_DOCUMENT_IDENTIFIER. */
	private static final String SAM_PAYMENT_DOCUMENT_IDENTIFIER = "sam.payment.document.identifier.sga";

	/** The Constant SAM_DOCUMENT_ECC_DATEPATTERN. */
	private static final String SAM_DOCUMENT_ECC_DATEPATTERN = "dd/MM/yyyy";

	/** The Constant SAM_DOCUMENT_HYBRIS_DATEPATTERN. */
	private static final String SAM_DOCUMENT_HYBRIS_DATEPATTERN = "dd-MM-yyyy";

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	/** The enumeration service. */
	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

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
	public void populate(final AsahiSAMInvoiceData source, final AsahiSAMPaymentModel target) throws ConversionException
	{

		if (null != source.getClearingdocumentNumber())
		{
			target.setClrDocNumber(source.getClearingdocumentNumber());
		}
		if (null != source.getReceiptNumber())
		{
			target.setReceiptNumber(source.getReceiptNumber());
		}

			try
			{
				//Getting SGA Site Date Format
				if (null != source.getTransactionDate())
				{
					final SimpleDateFormat sdf1 = new SimpleDateFormat(SAM_DOCUMENT_ECC_DATEPATTERN);
					final SimpleDateFormat sdf2 = new SimpleDateFormat(SAM_DOCUMENT_HYBRIS_DATEPATTERN);
					final String date = sdf2.format(sdf1.parse(source.getTransactionDate()));
					if (target.getTransactionDate() != null && target.getTransactionDate().before(sdf2.parse(date)))
					{
						handlePaymentHistoryBasedOnTxnDate(source, target, sdf2, date);
					}
					else if (target.getTransactionDate() == null)
					{
						handlePaymentHistoryBasedOnTxnDate(source, target, sdf2, date);
					}
				}
				else
				{
					handlePaymentHistoryBasedOnTxnDate(source, target, null, null);
				}
			}
			catch (final ParseException exp)
			{
				logger.error("Parse Exception occured in AsahiSAMPaymentHistReversePopulator" + exp.getMessage());
			}

		if (null != source.getCustAccount())
		{
			target.setCustAccount(this.apbB2BUnitService.getB2BUnitByAccountNumber(source.getCustAccount()));
		}
		if (null != source.getPaymentDocIdentifier())
		{
			target.setPaymentDocIdentifier(source.getPaymentDocIdentifier());
		}

	}

	private void handlePaymentHistoryBasedOnTxnDate(final AsahiSAMInvoiceData source, final AsahiSAMPaymentModel target,
			final SimpleDateFormat sdf2, final String date)
	{
		try
		{
			target.setPaymentReference(StringUtils.defaultString(source.getPaymentReference()));
			if (null != source.getPaymentType() && !source.getPaymentType().isEmpty())
			{
				target.setPaymentType(
						this.enumerationService.getEnumerationValue(AsahiSAMPaymentType.class, source.getPaymentType()));
			}
			if (this.asahiConfigurationService.getString(SAM_PAYMENT_DOCUMENT_IDENTIFIER, "X")
					.equalsIgnoreCase(source.getPaymentDocIdentifier()))
			{
				target.setAmount(source.getInvoiceAmount());
			}
			if (sdf2 != null && date != null)
			{
				target.setTransactionDate(sdf2.parse(date));
			}

		}
		catch (final ParseException exp)
		{
			logger.error("Parse Exception occured in AsahiSAMPaymentHistReversePopulator" + exp.getMessage());
		}

	}
}

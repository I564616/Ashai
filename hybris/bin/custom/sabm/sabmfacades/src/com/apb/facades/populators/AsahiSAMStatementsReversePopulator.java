package com.apb.facades.populators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiSAMStatementsModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMStatementData;
import com.apb.service.b2bunit.ApbB2BUnitService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * The Class AsahiSAMStatementsReversePopulator.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiSAMStatementsReversePopulator implements Populator<AsahiSAMStatementData, AsahiSAMStatementsModel>{

	/** The logger. */
	final Logger logger = LoggerFactory.getLogger(AsahiSAMStatementsReversePopulator.class);
	
	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "site.date.format.sga";
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;
	
	/** The model service. */
	@Resource
	private ModelService modelService;
	
	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> asahiSAMInvoiceReverseConverter;
	
	/**
	 * @return the asahiSAMInvoiceReverseConverter
	 */
	public Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> getAsahiSAMInvoiceReverseConverter() {
		return asahiSAMInvoiceReverseConverter;
	}

	/**
	 * @param asahiSAMInvoiceReverseConverter the asahiSAMInvoiceReverseConverter to set
	 */
	public void setAsahiSAMInvoiceReverseConverter(
			Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> asahiSAMInvoiceReverseConverter) {
		this.asahiSAMInvoiceReverseConverter = asahiSAMInvoiceReverseConverter;
	}

	/**
	 * Populate.
	 *
	 * @param source the source
	 * @param target the target
	 * @throws ConversionException the conversion exception
	 */
	@Override
	public void populate(AsahiSAMStatementData source,
			AsahiSAMStatementsModel target) throws ConversionException {
		
		target.setStatementBalance(source.getStatementBalance());
		target.setStatementNumber(source.getStatementNumber());
		target.setCustAccount(this.apbB2BUnitService.getB2BUnitByAccountNumber(source.getCustAccount()));
		try
		{
			//Getting SGA Site Date Format
			SimpleDateFormat format = new SimpleDateFormat(this.asahiConfigurationService.getString(DATE_FORMAT, "dd-MM-yyyy"));
			target.setStatementPeriod(format.parse(source.getStatementPeriod()));
		}
		catch (ParseException exp)
		{
			logger.error("Parse Exception occured in AsahiSAMStatementsReversePopulator" + exp.getMessage());
		}
		
		//populating invoice details
		if(!CollectionUtils.isEmpty(source.getInvoice())){
			this.populatingInvoices(source.getInvoice(),target);
		}
	}

	/**
	 * Populating invoices.
	 *
	 * @param invoiceDataList the invoice data list
	 * @param target the target
	 */
	private void populatingInvoices(List<AsahiSAMInvoiceData> invoiceDataList, AsahiSAMStatementsModel target) {
		
		List<AsahiSAMInvoiceModel> invoiceList = new ArrayList<AsahiSAMInvoiceModel>();
		
		for(AsahiSAMInvoiceData invoiceData : invoiceDataList){
			// Fetching Invoice based on document number
			AsahiSAMInvoiceModel existingInvoice = this.asahiSAMInvoiceService.getInvoiceByDocumentNumber(invoiceData.getDocumentNumber(),invoiceData.getLineNumber());
			/* Check if Invoice already exist in hybris if yes then update otherwise create new. */
			if(null!=existingInvoice){
				// update existing invoice
				// calling converter to populate the AsahiSAMInvoiceModel
				invoiceList.add(this.asahiSAMInvoiceReverseConverter.convert(invoiceData,existingInvoice));
				this.modelService.save(existingInvoice);
			}else{
				//create new Invoice in hybris
				AsahiSAMInvoiceModel newInvoice = this.modelService.create(AsahiSAMInvoiceModel.class);
				
				//calling converter to populate the AsahiSAMInvoiceModel
				invoiceList.add(this.asahiSAMInvoiceReverseConverter.convert(invoiceData,newInvoice));
				this.modelService.save(newInvoice);
			}
		}
		target.setSamInvoice(invoiceList);
	}

}

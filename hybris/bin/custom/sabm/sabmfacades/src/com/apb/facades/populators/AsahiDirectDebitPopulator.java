package com.apb.facades.populators;

import java.text.SimpleDateFormat;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiDirectDebitPaymentData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class AsahiDirectDebitPopulator implements Populator<AsahiSAMDirectDebitModel, AsahiDirectDebitData>{

	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "site.date.format.sga";
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private EnumerationService enumerationService;
	
	@Override
	public void populate(AsahiSAMDirectDebitModel source,
			AsahiDirectDebitData target) throws ConversionException {

		target.setCustAccount(source.getCustomer().getLocName());
		target.setCustAccountNum(source.getCustomer().getAccountNum());
		target.setPersonalName(source.getName());
		
		final SimpleDateFormat formatDDMMYYYY = new SimpleDateFormat(
				this.asahiConfigurationService.getString(DATE_FORMAT, "dd/MM/yyyy"));
		target.setDate(formatDDMMYYYY.format(source.getSignDate()));
		
		AsahiDirectDebitPaymentData directDebitPaymentData = new AsahiDirectDebitPaymentData();
		
		if(null!=source.getPaymentTransaction()){
			if(null!=source.getPaymentTransaction().getPaymentMode() && "BANK_ACCOUNT".equalsIgnoreCase(source.getPaymentTransaction().getPaymentMode().toString())){
				directDebitPaymentData.setAccountName(source.getPaymentTransaction().getAccountName());
				directDebitPaymentData.setAccountNum(source.getPaymentTransaction().getAccountNumber());
				directDebitPaymentData.setBsb(source.getPaymentTransaction().getBsb());
				if(null!=source.getPaymentTransaction().getRegion()){
					directDebitPaymentData.setRegion(source.getPaymentTransaction().getRegion().getName());
				}
				directDebitPaymentData.setSuburb(source.getPaymentTransaction().getSuburb());
				directDebitPaymentData.setTokenType(source.getPaymentTransaction().getPaymentMode().toString());
			}
			if(null!=source.getPaymentTransaction().getPaymentMode() && "DEBIT_CREDIT_CARD".equalsIgnoreCase(source.getPaymentTransaction().getPaymentMode().toString())){
				CreditCardPaymentInfoModel cardPaymentInfoModel = (CreditCardPaymentInfoModel) source.getPaymentTransaction().getInfo();
				if(null!=cardPaymentInfoModel){
					directDebitPaymentData.setCardNumber(cardPaymentInfoModel.getNumber());
					directDebitPaymentData.setCardExpiry(cardPaymentInfoModel.getValidToMonth());
					directDebitPaymentData.setCardType(cardPaymentInfoModel.getType().toString());
					if("master".equalsIgnoreCase(cardPaymentInfoModel.getType().toString())){
						directDebitPaymentData.setCardType("Mastercard");
					}
					directDebitPaymentData.setNameOnCard(cardPaymentInfoModel.getCcOwner());
				}
				directDebitPaymentData.setTokenType(source.getPaymentTransaction().getPaymentMode().toString());
			}
			directDebitPaymentData.setToken(source.getPaymentTransaction().getRequestToken());
			target.setDirectDebitPaymentData(directDebitPaymentData);
		}
		
	}

}

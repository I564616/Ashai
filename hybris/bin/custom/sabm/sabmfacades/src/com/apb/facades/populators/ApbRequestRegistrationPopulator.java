package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;

import org.springframework.util.Assert;

import com.apb.core.model.ApbRequestRegisterEmailModel;
import com.apb.facades.register.data.ApbRequestRegisterData;


/**
 *
 */
public class ApbRequestRegistrationPopulator implements Populator<ApbRequestRegisterEmailModel, ApbRequestRegisterData>
{

	public void populate(final ApbRequestRegisterEmailModel source, final ApbRequestRegisterData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setRequestRefNumber(source.getReferenceNumber());
		target.setOutletName(source.getOutletName());
		target.setTradingName(source.getTraidingName());
		target.setCompanyName(source.getCompanyName());
		target.setStreetNumber(source.getStreetNumber());
		target.setStreetName(source.getStreetName());
		target.setStreetAbreviation(source.getStreetAvreviation());
		target.setUnitNoShopNo(source.getUnitNo());
		target.setLevel(source.getLevel());
		target.setSuburb(source.getSuburb());
		target.setStateInvoice(source.getStateInvoice());
		target.setPostcodeInvoice(source.getPostalCodeInvoice());
		target.setContactName(source.getContactName());
		target.setAlternateContact(source.getAlternateContact());
		target.setPhoneNoInvoice(source.getPhoneInvoice());
		//target.setCustomerType(source.getCustomerType());
		target.setAlternativePhoneNo(source.getAlternatePhoneInvoice());
		target.setWarehouseNo(source.getWarehouseNo());
		target.setEmailAddress(source.getEmailAddress());
		target.setAbn(source.getAbn());
		target.setLiquorLicense(source.getLiquorLicense());
		target.setAcn(source.getAcn());
		target.setSameasInvoiceAddress(source.getSameAsAddress());
		target.setShippingStreet(source.getShippingStreet());
		target.setShippingSuburb(source.getShippingSuburb());
		target.setStateDelivery(source.getStateDelivery());
		target.setPostcodeDelivery(source.getPostalCodeDelivery());
		target.setDeliveryInstructions(source.getDeliveryInstruction());
		target.setTrust(source.getTrust());
		target.setTypeofEntity(source.getTypeofEntity());
		target.setTypeofBusiness(source.getTypeofBusiness());
		target.setDateBusinessEstablished(source.getDateBusinessEst());
		target.setLicensedPremisesAddress(source.getLicensePermisesAddress());
		target.setLicensee(source.getLicense());
		target.setBannerGroup(source.getBannerGroup());
		target.setDateofBirth(source.getDobIndividual());
		target.setDateandExpiryofLiquorLicense(source.getDateExpiryLiquorLicense());
		target.setPurchasingOfficer(source.getPurchasingOfficer());
		target.setAccountsContact(source.getAccountContact());

		target.setName(source.getNameIndividual());
		target.setName(source.getName());
		target.setPosition(source.getPositionIndividual());
		target.setAddress(source.getAddressIndividual());
		target.setPhoneNo(source.getPhoneIndividual());
		target.setDateofBirth(source.getDobIndividual());

		target.setName1(source.getNameIndividual1());
		target.setPosition1(source.getPositionIndividual1());
		target.setAddress1(source.getAddressIndividual1());
		target.setPhoneNo1(source.getPhone1Individual1());
		target.setDateofBirth1(source.getDobIndividual1());

		target.setBankBranch(source.getBankBranch());
		target.setContact(source.getContactIndividual());
		target.setRequestTermsConditions(source.getTermsConditions());
		target.setTrustName(source.getTrustName());
		target.setTrustDeed(source.getTrustDeed());
		target.setTrustAbn(source.getTrustAbn());
		if (source.getUploadFile() != null && source.getUploadFile().getRealFileName() != null)
		{
			target.setPdfFileName(source.getUploadFile().getRealFileName());
		}
		target.setPhoneNoReference(source.getPhoneNoIndividual());
	}
}

package com.apb.storefront.util;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController.SelectOption;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.apb.facades.register.data.ApbRequestRegisterData;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.forms.ApbRequestRegisterForm;


/**
 *
 */
public class ApbReguestRegistrationUtil
{
	/**
	 * @param form
	 * @return apbRequestRegisterData
	 */
	public ApbRequestRegisterData setRequestRegistrationData(final ApbRequestRegisterForm form)
	{
		final ApbRequestRegisterData apbRequestRegisterData = new ApbRequestRegisterData();
		apbRequestRegisterData.setPdfFile(form.getPdfFile());
		apbRequestRegisterData.setOutletName(form.getOutletName());
		apbRequestRegisterData.setTradingName(form.getTradingName());
		apbRequestRegisterData.setCompanyName(form.getCompanyName());
		apbRequestRegisterData.setStreetNumber(form.getStreetNumber());
		apbRequestRegisterData.setStreetName(form.getStreetName());
		apbRequestRegisterData.setStreetAbreviation(form.getStreetAbreviation());
		apbRequestRegisterData.setUnitNoShopNo(form.getUnitNoShopNo());
		apbRequestRegisterData.setLevel(form.getLevel());
		apbRequestRegisterData.setSuburb(form.getSuburb());
		apbRequestRegisterData.setStateInvoice(form.getStateInvoice());
		apbRequestRegisterData.setPostcodeInvoice(form.getPostcodeInvoice());
		apbRequestRegisterData.setContactName(form.getContactName());
		apbRequestRegisterData.setAlternateContact(form.getAlternateContact());
		apbRequestRegisterData.setPhoneNoInvoice(form.getPhoneNoInvoice());
		apbRequestRegisterData.setCustomerType(form.getCustomerType());
		apbRequestRegisterData.setAlternativePhoneNo(form.getAlternativePhoneNo());
		apbRequestRegisterData.setWarehouseNo(form.getWarehouseNo());
		apbRequestRegisterData.setEmailAddress(form.getEmailAddress());
		apbRequestRegisterData.setAbn(form.getAbn());
		apbRequestRegisterData.setLiquorLicense(form.getLiquorLicense());
		apbRequestRegisterData.setAcn(form.getAcn());
		apbRequestRegisterData.setSameasInvoiceAddress(form.isSameasInvoiceAddress());
		apbRequestRegisterData.setShippingStreet(form.getShippingStreet());
		apbRequestRegisterData.setShippingSuburb(form.getShippingSuburb());
		apbRequestRegisterData.setStateDelivery(form.getStateDelivery());
		apbRequestRegisterData.setPostcodeDelivery(form.getPostcodeDelivery());
		apbRequestRegisterData.setDeliveryInstructions(form.getDeliveryInstructions());
		apbRequestRegisterData.setApplicantCarry(form.isApplicantCarry());
		apbRequestRegisterData.setTypeofEntity(form.getTypeofEntity());
		apbRequestRegisterData.setTypeofBusiness(form.getTypeofBusiness());
		apbRequestRegisterData.setDateBusinessEstablished(form.getDateBusinessEstablished());
		apbRequestRegisterData.setLicensedPremisesAddress(form.getLicensedPremisesAddress());
		apbRequestRegisterData.setLicensee(form.getLicensee());
		apbRequestRegisterData.setBannerGroup(form.getBannerGroup());
		apbRequestRegisterData.setDateandExpiryofLiquorLicense(form.getDateandExpiryofLiquorLicense());
		apbRequestRegisterData.setPurchasingOfficer(form.getPurchasingOfficer());
		apbRequestRegisterData.setAccountsContact(form.getAccountsContact());
		apbRequestRegisterData.setName(form.getName());
		apbRequestRegisterData.setPosition(form.getPosition());
		apbRequestRegisterData.setAddress(form.getAddress());
		apbRequestRegisterData.setPhoneNo(form.getPhoneNo());
		apbRequestRegisterData.setDateofBirth(form.getDateofBirth());
		apbRequestRegisterData.setName1(form.getName1());
		apbRequestRegisterData.setPosition1(form.getPosition1());
		apbRequestRegisterData.setAddress1(form.getAddress());
		apbRequestRegisterData.setPhoneNo1(form.getPhoneNo1());
		apbRequestRegisterData.setDateofBirth1(form.getDateofBirth1());
		apbRequestRegisterData.setBankBranch(form.getBankBranch());
		apbRequestRegisterData.setContact(form.getContact());
		apbRequestRegisterData.setPhoneNoReference(form.getPhoneNoReference());
		apbRequestRegisterData.setRequestTermsConditions(form.isRequestTermsConditions());
		apbRequestRegisterData.setRequestCustomer(form.isRequestCustomerType());
		apbRequestRegisterData.setTrustName(form.getTrustName());
		apbRequestRegisterData.setTrustDeed(form.getTrustDeed());
		apbRequestRegisterData.setTrustAbn(form.getTrustAbn());
		apbRequestRegisterData.setAddAnother(form.isAddAnother());
		apbRequestRegisterData.setSameasDeliveryAddressLPA(form.isSameasDeliveryAddressLPA());
		return apbRequestRegisterData;
	}

	/**
	 * Create Option list from configuration db
	 *
	 * @param typeOfEntityProp
	 * @param entityList
	 */
	public void getOptionList(final String typeOfEntityProp, final List<SelectOption> entityList)
	{
		final String[] entity = typeOfEntityProp.split(",");
		if (entity.length > 0)
		{
			for (int i = 0; i < entity.length; i++)
			{
				final String entitytemp = entity[i];
				if (StringUtils.isNotEmpty(entitytemp))
				{
					final String[] entitTypeList = entitytemp.split(ApbStoreFrontContants.SPLIT_CHARACTER_SYMBOLE);
					for (int j = 1; j < entitTypeList.length; j++)
					{
						entityList.add(new SelectOption(entitTypeList[0], entitTypeList[1]));
					}
				}
			}
		}
	}
}

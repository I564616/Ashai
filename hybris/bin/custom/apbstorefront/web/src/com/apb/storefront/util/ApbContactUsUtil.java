package com.apb.storefront.util;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;

import java.util.UUID;

import jakarta.annotation.Resource;

import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.AsahiContactUsSaleRepData;
import com.apb.facades.contactust.data.ContactUsQueryTypeData;
import com.apb.storefront.forms.ApbContactUsForm;
import com.apb.facades.contactust.data.DeliveryDiscrepancyData;
import com.apb.facades.contactust.data.PriceDiscrepancyData;
import com.apb.storefront.forms.DiscrepancyForm;
import com.sabmiller.core.enums.AsahiEnquiryType;
import java.util.List;
import java.util.ArrayList;
import com.sabmiller.core.enums.AsahiEnquirySubType;

/**
 *
 */
public class ApbContactUsUtil
{
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	/**
	 * @param source
	 * @param target
	 */
	public void convert(final ApbContactUsData source, final ApbContactUsForm target)
	{
		target.setAccountNumber(source.getAccountNumber());
		target.setCompanyName(source.getCompanyName());
		target.setName(source.getName());
		target.setEmailAddress(source.getEmailAddress());
		target.setContactNumber(source.getContactNumber());
		target.setFurtherDetail(source.getFurtherDetail());
		target.setSubject(target.getSubject());
		target.setSubjectOther(target.getSubjectOther());
		target.setSubjectFlag(target.getSubjectFlag());
	}

	/**
	 * @param contactUsForm
	 * @return contactUsData
	 */
	public ApbContactUsData setContactUsData(final ApbContactUsForm contactUsForm)
	{
		final ApbContactUsData contactUsData = new ApbContactUsData();
		contactUsData.setAccountNumber(contactUsForm.getAccountNumber());
		contactUsData.setCompanyName(contactUsForm.getCompanyName());
		contactUsData.setName(contactUsForm.getName());
		contactUsData.setContactNumber(contactUsForm.getContactNumber());
		contactUsData.setEmailAddress(contactUsForm.getEmailAddress());
		contactUsForm.getSubject();
		final ContactUsQueryTypeData contactUsQueryTypeData = new ContactUsQueryTypeData();
		contactUsQueryTypeData.setCode(UUID.randomUUID().toString());
		contactUsQueryTypeData.setContactUsQueryType(contactUsForm.getSubject());
		contactUsQueryTypeData.setOtherContactUsQueryType(contactUsForm.getSubjectOther());
		contactUsData.setContactUsQueryType(contactUsQueryTypeData);
		contactUsData.setSubject(contactUsForm.getSubject());
		contactUsData.setSubjectOther(contactUsForm.getSubjectOther());
		contactUsData.setFurtherDetail(contactUsForm.getFurtherDetail());
		contactUsData.setPdfFile(contactUsForm.getPdfFile());
		setSalesRepContactUsData(contactUsForm, contactUsData);

		// new attributes added here
		contactUsData.setEnquiryType(contactUsForm.getEnquiryType());
		contactUsData.setEnquirySubType(contactUsForm.getEnquirySubType());
		contactUsData.setMessage(contactUsForm.getMessage());
		contactUsData.setAddInfo(contactUsForm.getAddInfo());
		contactUsData.setDeliveryNumber(contactUsForm.getDeliveryNumber());

		List<PriceDiscrepancyData> priceDiscrepancyDtos = new ArrayList<PriceDiscrepancyData>();
		List<DeliveryDiscrepancyData> deliveryDiscrepancyDtos = new ArrayList<DeliveryDiscrepancyData>();

		List<DiscrepancyForm> discrepancies = contactUsForm.getDiscrepancies();

		if(null != contactUsForm.getEnquiryType() && contactUsForm.getEnquiryType().equalsIgnoreCase(AsahiEnquiryType.INCORRECT_CHARGE.getCode())) {
			for (DiscrepancyForm discrepancy : discrepancies) {
				PriceDiscrepancyData pricedto = new PriceDiscrepancyData();
				pricedto.setMaterialNumber(discrepancy.getMaterialNumber());
				pricedto.setExpectedTotalPay(discrepancy.getExpectedTotalPay());
				pricedto.setAmtCharged(discrepancy.getAmtCharged());
				priceDiscrepancyDtos.add(pricedto);
			}
			contactUsData.setPriceDiscrepancyDTOs(priceDiscrepancyDtos);
		}

		if(null != contactUsForm.getEnquiryType() && contactUsForm.getEnquiryType().equalsIgnoreCase(AsahiEnquiryType.REPORT_DEL_ISSUE.getCode())) {
			for (DiscrepancyForm discrepancy : discrepancies) {
				DeliveryDiscrepancyData deliverydto = new DeliveryDiscrepancyData();
				// When enquiry sub type is Damaged Products or Incorrect products
				if(null != contactUsForm.getEnquirySubType() && contactUsForm.getEnquirySubType().equalsIgnoreCase(AsahiEnquirySubType.DAMAGED_PRODUCTS.getCode())) {

					deliverydto.setMaterialNumber(discrepancy.getMaterialNumber());
					deliverydto.setQtyWithDelIssue(discrepancy.getQtyWithDelIssue());
					deliveryDiscrepancyDtos.add(deliverydto);
				}
				
				// When enquiry sub type is wrong quantity
				if(null != contactUsForm.getEnquirySubType() && (contactUsForm.getEnquirySubType().equalsIgnoreCase(AsahiEnquirySubType.WRONG_QTY.getCode()) | contactUsForm.getEnquirySubType().equalsIgnoreCase(AsahiEnquirySubType.INCORRECT_PRODUCTS.getCode()))) {
					deliverydto.setMaterialNumber(discrepancy.getMaterialNumber());
					deliverydto.setQtyReceived(discrepancy.getQtyReceived());
					deliverydto.setExpectedQty(discrepancy.getExpectedQty());
					deliveryDiscrepancyDtos.add(deliverydto);
				}
			}
			contactUsData.setDeliveryDiscrepancyDTOs(deliveryDiscrepancyDtos);
		}

		return contactUsData;
	}

	/**
	 * @param contactUsForm
	 * @param contactUsData
	 */
	private void setSalesRepContactUsData(final ApbContactUsForm contactUsForm, final ApbContactUsData contactUsData)
	{
		final AsahiContactUsSaleRepData contactUsSaleRepData = new AsahiContactUsSaleRepData();
		contactUsSaleRepData.setName(contactUsForm.getAsahiSalesRepName());
		contactUsSaleRepData.setEmailAddress(contactUsForm.getAsahiSalesRepEmail());
		contactUsData.setAsahiContactUsSaleRepData(contactUsSaleRepData);
	}

	/**
	 * @return the userFacade
	 */
	public UserFacade getUserFacade()
	{
		return userFacade;
	}

	/**
	 * @param userFacade
	 *           the userFacade to set
	 */
	public void setUserFacade(final UserFacade userFacade)
	{
		this.userFacade = userFacade;
	}

	/**
	 * @return the i18NFacade
	 */
	public I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	/**
	 * @param i18nFacade
	 *           the i18NFacade to set
	 */
	public void setI18NFacade(final I18NFacade i18nFacade)
	{
		i18NFacade = i18nFacade;
	}

}

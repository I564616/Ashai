/**
 *
 */
package com.sabmiller.facades.businessenquiry.impl;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.businessenquiry.BusinessEnquiryService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.SABMEnquirySubType;
import com.sabmiller.core.enums.SABMEnquiryType;
import com.sabmiller.facades.businessenquiry.SabmBusinessEnquiryFacade;
import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmAutopayInquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmContactUsData;
import com.sabmiller.facades.businessenquiry.data.SabmDeliveryEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmDeliveryIssueData;
import com.sabmiller.facades.businessenquiry.data.SabmEmptyPalletPickupData;
import com.sabmiller.facades.businessenquiry.data.SabmGeneralInquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;
import com.sabmiller.facades.businessenquiry.data.SabmKegPickupData;
import com.sabmiller.facades.businessenquiry.data.SabmMyDetailsAndDeliverOptionsData;
import com.sabmiller.facades.businessenquiry.data.SabmPriceEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmProductEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmProductReturnData;
import com.sabmiller.facades.businessenquiry.data.SabmWebsiteErrorsData;
import com.sabmiller.facades.businessenquiry.data.SabmWebsiteInquiryFeedbackData;
import com.sabmiller.facades.generic.data.SABMEnquirySubTypeData;
import com.sabmiller.facades.businessenquiry.data.SabmOrderEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmUpdateExistingEnquiryData;
import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;
import com.sabmiller.sfmc.service.SabmSFMCService;




/**
 *
 */
public class SabmBusinessEnquiryFacadeImpl implements SabmBusinessEnquiryFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(SabmBusinessEnquiryFacadeImpl.class.getName());

	private BusinessEnquiryService businessEnquiryService;

	private Converter<AbstractBusinessEnquiryData, SabmDeliveryEnquiryData> sabmDeliveryEnquiryDataConverter;
	private Converter<AbstractBusinessEnquiryData, SabmKegPickupData> sabmKegPickupConverter;
	private Converter<AbstractBusinessEnquiryData, SabmMyDetailsAndDeliverOptionsData> sabmMyDetailsAndDeliverOptionsConverter;
	private Converter<AbstractBusinessEnquiryData, SabmEmptyPalletPickupData> sabmEmptyPalletPickupConverter;
	private Converter<AbstractBusinessEnquiryData, SabmWebsiteErrorsData> sabmWebsiteErrorsConverter;
	private Converter<AbstractBusinessEnquiryData, SabmGeneralInquiryData> sabmGeneralInquiryConverter;
	private Converter<AbstractBusinessEnquiryData, SabmWebsiteInquiryFeedbackData> sabmWebsiteInquiryFeedbackConverter;
	private Converter<AbstractBusinessEnquiryData, SabmProductReturnData> sabmProductReturnConverter;
	private Converter<AbstractBusinessEnquiryData, SabmKegIssueData> sabmKegIssueConverter;
	private Converter<AbstractBusinessEnquiryData, SabmDeliveryIssueData> sabmDeliveryIssueConverter;
	private Converter<AbstractBusinessEnquiryData, SabmPriceEnquiryData> sabmPriceEnquiryConverter;
	private Converter<AbstractBusinessEnquiryData, SabmProductEnquiryData> sabmProductEnquiryConverter;
	private Converter<AbstractBusinessEnquiryData, SabmAutopayInquiryData> sabmAutopayInquiryConverter;
	
	private Converter<AbstractBusinessEnquiryData, SabmOrderEnquiryData> sabmOrderEnquiryConverter;
	private Converter<AbstractBusinessEnquiryData, SabmUpdateExistingEnquiryData> sabmUpdateExistingEnquiryConverter;

	@Resource
	private Map<SABMEnquiryType, List<SABMEnquirySubType>> enquiryTypeToSubTypeMap;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "sabmSFMCService")
	private SabmSFMCService sabmSFMCService;


	/**
	 * Method to fetch enquiry subtype
	 * @param enquiryType
	 * @return
	 */
	@Override
	public List<SABMEnquirySubTypeData> fetchEnquirySubType(String enquiryType) {
		List<SABMEnquirySubTypeData> returnList = new ArrayList<>();
		final SABMEnquiryType enquiryTypeEnum = SABMEnquiryType.valueOf(enquiryType);
		if (StringUtils.isNotEmpty(enquiryType) && enquiryTypeToSubTypeMap.containsKey(enquiryTypeEnum)) {
			List<SABMEnquirySubType> enquiryTypeList = enquiryTypeToSubTypeMap.get(enquiryTypeEnum);
			enquiryTypeList.stream().forEach(en -> returnList.add(createNewEnquiryData(en)));

			return returnList;
		}
		return returnList;
	}

	private SABMEnquirySubTypeData createNewEnquiryData(SABMEnquirySubType subTypeEnum) {
		SABMEnquirySubTypeData data = new SABMEnquirySubTypeData();
		data.setCode(subTypeEnum.getCode());
		data.setName(enumerationService.getEnumerationName(subTypeEnum));
		return data;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.businessenquiry.SabmBusinessEnquiryFacade#sendEmailEnquiry(com.sabmiller.facades.
	 * businessenquiry.data.AbstractBusinessEnquiryData)
	 */
	@Override
	public void sendBusinessEnquiryEmail(final AbstractBusinessEnquiryData enquiry)
	{
		LOG.info("Sending business enquiry email...");
		AbstractBusinessEnquiryData businessEnquiry = null;
		final String requestType = enquiry.getRequestType();
		if (StringUtils.isBlank(requestType))
		{
			LOG.error("No request type was given in the form, not sending business email.");
		}
		switch (requestType)
		{
			case SabmCoreConstants.BUSINESS_ENQUIRY_DELIVERY:
				businessEnquiry = getDeliveryEnquiryData(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_RETURN:
				businessEnquiry = getProductReturnRequest(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_KEG:
				businessEnquiry = getKegIssue(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_DELIVERY_ISSUE:
				businessEnquiry = getDeliveryIssue(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PRICE:
				businessEnquiry = getPriceEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PRODUCT:
				businessEnquiry = getProductEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PICKUP:
				businessEnquiry = getKegPickupEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_UPDATE:
				businessEnquiry = getMyDetailsAndDeliverOptionsEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PALLET:
				businessEnquiry = getEmptyPalletPickupEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_WEBSITE:
				businessEnquiry = getWebsiteErrorsEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_GENERAL:
				businessEnquiry = getGeneralInquiryEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_WEBSITE_ENQ:
				businessEnquiry = getWebsiteInquiryFeedbackEnquiry(enquiry);
				break;
			// SABMC-1058 : Contact Us - Special Non-Business Enquiry Case
			case SabmCoreConstants.BUSINESS_ENQUIRY_CONTACT_US:
				businessEnquiry = getContactUsEnquiry(enquiry);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_AUTOPAY:
				businessEnquiry = getAutopayInquiryEnquiry(enquiry);
				break;
			case SabmCoreConstants.ORDER_ENQUIRY:
				businessEnquiry = getOrderEnquiry(enquiry);
				break;
			case SabmCoreConstants.UPDATE_EXISTING_ENQUIRY:
				businessEnquiry = getUpdateExistingEnquiry(enquiry);
				break;
			default:
				businessEnquiry = null;
				break;
		}

		if (businessEnquiry == null)
		{
			LOG.error("No matching business enquiry type was found for :[{}]", requestType);
			return;
		}

		getBusinessEnquiryService().sendEmail(businessEnquiry);
	}

	private SabmDeliveryEnquiryData getDeliveryEnquiryData(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmDeliveryEnquiryData deliveryEnquiry = new SabmDeliveryEnquiryData();
		getSabmDeliveryEnquiryDataConverter().convert(enquiry, deliveryEnquiry);

		return deliveryEnquiry;
	}

	private SabmKegPickupData getKegPickupEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmKegPickupData kegPickup = new SabmKegPickupData();
		getSabmKegPickupConverter().convert(enquiry, kegPickup);

		return kegPickup;
	}

	private SabmMyDetailsAndDeliverOptionsData getMyDetailsAndDeliverOptionsEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmMyDetailsAndDeliverOptionsData myDetailsAndDeliverOptions = new SabmMyDetailsAndDeliverOptionsData();
		getSabmMyDetailsAndDeliverOptionsConverter().convert(enquiry, myDetailsAndDeliverOptions);

		return myDetailsAndDeliverOptions;
	}

	private SabmEmptyPalletPickupData getEmptyPalletPickupEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmEmptyPalletPickupData emptyPalletPickup = new SabmEmptyPalletPickupData();
		getSabmEmptyPalletPickupConverter().convert(enquiry, emptyPalletPickup);

		return emptyPalletPickup;
	}

	private SabmWebsiteErrorsData getWebsiteErrorsEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmWebsiteErrorsData websiteErrors = new SabmWebsiteErrorsData();
		getSabmWebsiteErrorsConverter().convert(enquiry, websiteErrors);

		return websiteErrors;
	}

	private SabmGeneralInquiryData getGeneralInquiryEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmGeneralInquiryData generalInquiry = new SabmGeneralInquiryData();
		getSabmGeneralInquiryConverter().convert(enquiry, generalInquiry);

		return generalInquiry;
	}

	private SabmWebsiteInquiryFeedbackData getWebsiteInquiryFeedbackEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmWebsiteInquiryFeedbackData websiteInquiryFeedback = new SabmWebsiteInquiryFeedbackData();
		getSabmWebsiteInquiryFeedbackConverter().convert(enquiry, websiteInquiryFeedback);

		return websiteInquiryFeedback;
	}

	private SabmProductReturnData getProductReturnRequest(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmProductReturnData productReturn = new SabmProductReturnData();
		getSabmProductReturnConverter().convert(enquiry, productReturn);

		return productReturn;
	}

	private SabmKegIssueData getKegIssue(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmKegIssueData kegIssue = new SabmKegIssueData();
		getSabmKegIssueConverter().convert(enquiry, kegIssue);

		return kegIssue;
	}

	private SabmDeliveryIssueData getDeliveryIssue(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmDeliveryIssueData deliveryIssue = new SabmDeliveryIssueData();
		getSabmDeliveryIssueConverter().convert(enquiry, deliveryIssue);

		return deliveryIssue;
	}

	private SabmPriceEnquiryData getPriceEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmPriceEnquiryData priceEnquiry = new SabmPriceEnquiryData();
		getSabmPriceEnquiryConverter().convert(enquiry, priceEnquiry);

		return priceEnquiry;
	}

	private SabmProductEnquiryData getProductEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmProductEnquiryData productEnquiry = new SabmProductEnquiryData();
		getSabmProductEnquiryConverter().convert(enquiry, productEnquiry);

		return productEnquiry;
	}

	private SabmContactUsData getContactUsEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmContactUsData contactUsEnquiry = (SabmContactUsData) enquiry;
		return contactUsEnquiry;
	}

	private SabmAutopayInquiryData getAutopayInquiryEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmAutopayInquiryData autopayInquiry = new SabmAutopayInquiryData();
		sabmAutopayInquiryConverter.convert(enquiry, autopayInquiry);

		return autopayInquiry;
	}
	
	private SabmOrderEnquiryData getOrderEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmOrderEnquiryData orderEnquiry = new SabmOrderEnquiryData();
		sabmOrderEnquiryConverter.convert(enquiry, orderEnquiry);

		return orderEnquiry;
	}

	private SabmUpdateExistingEnquiryData getUpdateExistingEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		final SabmUpdateExistingEnquiryData updateExisitingEnquiry = new SabmUpdateExistingEnquiryData();
		sabmUpdateExistingEnquiryConverter.convert(enquiry, updateExisitingEnquiry);

		return updateExisitingEnquiry;
	}


	/**
	 * @return the businessEnquiryService
	 */
	public BusinessEnquiryService getBusinessEnquiryService()
	{
		return businessEnquiryService;
	}

	/**
	 * @param businessEnquiryService
	 *           the businessEnquiryService to set
	 */
	public void setBusinessEnquiryService(final BusinessEnquiryService businessEnquiryService)
	{
		this.businessEnquiryService = businessEnquiryService;
	}

	/**
	 * @return the sabmDeliveryEnquiryDataConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmDeliveryEnquiryData> getSabmDeliveryEnquiryDataConverter()
	{
		return sabmDeliveryEnquiryDataConverter;
	}

	/**
	 * @param sabmDeliveryEnquiryDataConverter
	 *           the sabmDeliveryEnquiryDataConverter to set
	 */
	public void setSabmDeliveryEnquiryDataConverter(
			final Converter<AbstractBusinessEnquiryData, SabmDeliveryEnquiryData> sabmDeliveryEnquiryDataConverter)
	{
		this.sabmDeliveryEnquiryDataConverter = sabmDeliveryEnquiryDataConverter;
	}

	/**
	 * @return the sabmMyDetailsAndDeliverOptionsConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmMyDetailsAndDeliverOptionsData> getSabmMyDetailsAndDeliverOptionsConverter()
	{
		return sabmMyDetailsAndDeliverOptionsConverter;
	}

	/**
	 * @param sabmMyDetailsAndDeliverOptionsConverter
	 *           the sabmMyDetailsAndDeliverOptionsConverter to set
	 */
	public void setSabmMyDetailsAndDeliverOptionsConverter(
			final Converter<AbstractBusinessEnquiryData, SabmMyDetailsAndDeliverOptionsData> sabmMyDetailsAndDeliverOptionsConverter)
	{
		this.sabmMyDetailsAndDeliverOptionsConverter = sabmMyDetailsAndDeliverOptionsConverter;
	}

	/**
	 * @return the sabmEmptyPalletPickupConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmEmptyPalletPickupData> getSabmEmptyPalletPickupConverter()
	{
		return sabmEmptyPalletPickupConverter;
	}

	/**
	 * @param sabmEmptyPalletPickupConverter
	 *           the sabmEmptyPalletPickupConverter to set
	 */
	public void setSabmEmptyPalletPickupConverter(
			final Converter<AbstractBusinessEnquiryData, SabmEmptyPalletPickupData> sabmEmptyPalletPickupConverter)
	{
		this.sabmEmptyPalletPickupConverter = sabmEmptyPalletPickupConverter;
	}

	/**
	 * @return the sabmWebsiteErrorsConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmWebsiteErrorsData> getSabmWebsiteErrorsConverter()
	{
		return sabmWebsiteErrorsConverter;
	}

	/**
	 * @param sabmWebsiteErrorsConverter
	 *           the sabmWebsiteErrorsConverter to set
	 */
	public void setSabmWebsiteErrorsConverter(
			final Converter<AbstractBusinessEnquiryData, SabmWebsiteErrorsData> sabmWebsiteErrorsConverter)
	{
		this.sabmWebsiteErrorsConverter = sabmWebsiteErrorsConverter;
	}

	/**
	 * @return the sabmGeneralInquiryConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmGeneralInquiryData> getSabmGeneralInquiryConverter()
	{
		return sabmGeneralInquiryConverter;
	}

	/**
	 * @param sabmGeneralInquiryConverter
	 *           the sabmGeneralInquiryConverter to set
	 */
	public void setSabmGeneralInquiryConverter(
			final Converter<AbstractBusinessEnquiryData, SabmGeneralInquiryData> sabmGeneralInquiryConverter)
	{
		this.sabmGeneralInquiryConverter = sabmGeneralInquiryConverter;
	}

	/**
	 * @return the sabmWebsiteInquiryFeedbackConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmWebsiteInquiryFeedbackData> getSabmWebsiteInquiryFeedbackConverter()
	{
		return sabmWebsiteInquiryFeedbackConverter;
	}

	/**
	 * @param sabmWebsiteInquiryFeedbackConverter
	 *           the sabmWebsiteInquiryFeedbackConverter to set
	 */
	public void setSabmWebsiteInquiryFeedbackConverter(
			final Converter<AbstractBusinessEnquiryData, SabmWebsiteInquiryFeedbackData> sabmWebsiteInquiryFeedbackConverter)
	{
		this.sabmWebsiteInquiryFeedbackConverter = sabmWebsiteInquiryFeedbackConverter;
	}

	/**
	 * @return the sabmKegPickupConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmKegPickupData> getSabmKegPickupConverter()
	{
		return sabmKegPickupConverter;
	}

	/**
	 * @param sabmKegPickupConverter
	 *           the sabmKegPickupConverter to set
	 */
	public void setSabmKegPickupConverter(final Converter<AbstractBusinessEnquiryData, SabmKegPickupData> sabmKegPickupConverter)
	{
		this.sabmKegPickupConverter = sabmKegPickupConverter;
	}

	/**
	 * @return the sabmProductReturnConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmProductReturnData> getSabmProductReturnConverter()
	{
		return sabmProductReturnConverter;
	}

	/**
	 * @param sabmProductReturnConverter
	 *           the sabmProductReturnConverter to set
	 */
	public void setSabmProductReturnConverter(
			final Converter<AbstractBusinessEnquiryData, SabmProductReturnData> sabmProductReturnConverter)
	{
		this.sabmProductReturnConverter = sabmProductReturnConverter;
	}

	/**
	 * @return the sabmKegIssueConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmKegIssueData> getSabmKegIssueConverter()
	{
		return sabmKegIssueConverter;
	}

	/**
	 * @param sabmKegIssueConverter
	 *           the sabmKegIssueConverter to set
	 */
	public void setSabmKegIssueConverter(final Converter<AbstractBusinessEnquiryData, SabmKegIssueData> sabmKegIssueConverter)
	{
		this.sabmKegIssueConverter = sabmKegIssueConverter;
	}

	/**
	 * @return the sabmDeliveryIssueConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmDeliveryIssueData> getSabmDeliveryIssueConverter()
	{
		return sabmDeliveryIssueConverter;
	}

	/**
	 * @param sabmDeliveryIssueConverter
	 *           the sabmDeliveryIssueConverter to set
	 */
	public void setSabmDeliveryIssueConverter(
			final Converter<AbstractBusinessEnquiryData, SabmDeliveryIssueData> sabmDeliveryIssueConverter)
	{
		this.sabmDeliveryIssueConverter = sabmDeliveryIssueConverter;
	}

	/**
	 * @return the sabmPriceEnquiryConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmPriceEnquiryData> getSabmPriceEnquiryConverter()
	{
		return sabmPriceEnquiryConverter;
	}

	/**
	 * @param sabmPriceEnquiryConverter
	 *           the sabmPriceEnquiryConverter to set
	 */
	public void setSabmPriceEnquiryConverter(
			final Converter<AbstractBusinessEnquiryData, SabmPriceEnquiryData> sabmPriceEnquiryConverter)
	{
		this.sabmPriceEnquiryConverter = sabmPriceEnquiryConverter;
	}

	/**
	 * @return the sabmProductEnquiryConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmProductEnquiryData> getSabmProductEnquiryConverter()
	{
		return sabmProductEnquiryConverter;
	}

	/**
	 * @param sabmProductEnquiryConverter
	 *           the sabmProductEnquiryConverter to set
	 */
	public void setSabmProductEnquiryConverter(
			final Converter<AbstractBusinessEnquiryData, SabmProductEnquiryData> sabmProductEnquiryConverter)
	{
		this.sabmProductEnquiryConverter = sabmProductEnquiryConverter;
	}

	/**
	 * @return the sabmAutopayInquiryConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmAutopayInquiryData> getSabmAutopayInquiryConverter()
	{
		return sabmAutopayInquiryConverter;
	}

	/**
	 * @param sabmAutopayInquiryConverter the sabmAutopayInquiryConverter to set
	 */
	public void setSabmAutopayInquiryConverter(
			Converter<AbstractBusinessEnquiryData, SabmAutopayInquiryData> sabmAutopayInquiryConverter)
	{
		this.sabmAutopayInquiryConverter = sabmAutopayInquiryConverter;
	}
	
	/**
	 * @return the sabmOrderEnquiryConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmOrderEnquiryData> getSabmOrderEnquiryConverter()
	{
		return sabmOrderEnquiryConverter;
	}

	/**
	 * @param sabmOrderEnquiryConverter
	 *           the sabmOrderEnquiryConverter to set
	 */
	public void setSabmOrderEnquiryConverter(
			final Converter<AbstractBusinessEnquiryData, SabmOrderEnquiryData> sabmOrderEnquiryConverter)
	{
		this.sabmOrderEnquiryConverter = sabmOrderEnquiryConverter;
	}

	/**
	 * @return the sabmUpdateExistingEnquiryConverter
	 */
	public Converter<AbstractBusinessEnquiryData, SabmUpdateExistingEnquiryData> getUpdateExistingEnquiryConverter()
	{
		return sabmUpdateExistingEnquiryConverter;
	}

	/**
	 * @param sabmUpdateExistingEnquiryConverter
	 *           the sabmUpdateExistingEnquiryConverter to set
	 */
	public void setSabmUpdateExistingEnquiryConverter(
			final Converter<AbstractBusinessEnquiryData, SabmUpdateExistingEnquiryData> sabmUpdateExistingEnquiryConverter)
	{
		this.sabmUpdateExistingEnquiryConverter = sabmUpdateExistingEnquiryConverter;
	}

	/**
	 * @param AbstractBusinessEnquiryData
	 *
	 */
	@Override
	public SFCompositeResponse createKegIssueWithSalesforce(AbstractBusinessEnquiryData businessEnquiryData) {
		final SabmKegIssueData sabmKegIssueData = new SabmKegIssueData();
		sabmKegIssueConverter.convert(businessEnquiryData, sabmKegIssueData);
		return businessEnquiryService.createKegIssueWithSalesforce(sabmKegIssueData);
	}

	public SFCompositeResponse buildResponse(String status, String message){
		return sabmSFMCService.buildResponse(status, message);
	}

}

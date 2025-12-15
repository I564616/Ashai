/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.sabm.core.model.BusinessEnquiryEmailProcessModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AutopayEnquiryModel;
import com.sabmiller.core.model.BusinessEnquiryModel;
import com.sabmiller.core.model.ContactUsModel;
import com.sabmiller.core.model.DeliveryEnquiryModel;
import com.sabmiller.core.model.DeliveryIssueEnquiryModel;
import com.sabmiller.core.model.EmptyPalletEnquiryModel;
import com.sabmiller.core.model.GeneralEnquiryModel;
import com.sabmiller.core.model.KegIssueEnquiryModel;
import com.sabmiller.core.model.KegPickupEnquiryModel;
import com.sabmiller.core.model.MyDetailsEnquiryModel;
import com.sabmiller.core.model.PriceEnquiryModel;
import com.sabmiller.core.model.ProductEnquiryModel;
import com.sabmiller.core.model.ProductReturnEnquiryModel;
import com.sabmiller.core.model.WebsiteEnquiryModel;
import com.sabmiller.core.model.WebsiteErrorsEnquiryModel;
import com.sabmiller.core.model.OrderEnquiryModel;
import com.sabmiller.core.model.UpdateExistingEnquiryModel;
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
import com.sabmiller.facades.businessenquiry.data.SabmOrderEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmUpdateExistingEnquiryData;


/**
 *
 */
public class BusinessEnquiryEmailContext extends CustomerEmailContext
{
	private String requestType;
	private String subjectRequestType;
	private AbstractBusinessEnquiryData businessEnquiryData;

	private static String EMAIL_DATE_FORMAT = "dd/MM/yyyy";

	private static String BUSINES_ENQUIRY_DELIVERY = "Delivery Enquiry";
	private static String BUSINES_ENQUIRY_PRODUCT_RETURN = "Product Return";
	private static String BUSINES_ENQUIRY_KEG_ISSUE = "Keg Issue";

	private static String BUSINES_ENQUIRY_DELIVERY_ISSUE = "Delivery Issue";
	private static String BUSINES_ENQUIRY_PRICE = "Price Enquiry";
	private static String BUSINES_ENQUIRY_PRODUCT = "Product Enquiry";

	private static String BUSINES_ENQUIRY_KEG_PICKUP = "Keg Pickup";
	private static String BUSINES_ENQUIRY_UPDATE = "Update my details and delivery options";
	private static String BUSINES_ENQUIRY_EMPTY_PALLET = "Empty Pallet Pickup";

	private static String BUSINES_ENQUIRY_WEBSITE_ERRORS = "Website Errors";
	private static String BUSINES_ENQUIRY_GENERAL = "General Enquiry";
	private static String BUSINES_ENQUIRY_WEBSITE_FEEDBACK = "Website Enquiries/Feedback";

	private static String BUSINES_ENQUIRY_AUTOPAY = "AutoPay ADVANTAGE Enquiry";
	
	private static String BUSINES_ENQUIRY_ORDER = "Order Enquiry";
	private static String BUSINES_ENQUIRY_UPDATE_EXISTING = "Update On Existing Enquiry";

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.facades.process.email.context.CustomerEmailContext#init(de.hybris.platform.commerceservices.model.
	 * process.StoreFrontCustomerProcessModel, de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel)
	 */
	@Override
	public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(storeFrontCustomerProcessModel, emailPageModel);

		if (storeFrontCustomerProcessModel instanceof BusinessEnquiryEmailProcessModel)
		{
			final BusinessEnquiryEmailProcessModel processModel = (BusinessEnquiryEmailProcessModel) storeFrontCustomerProcessModel;
			final String requestType = processModel.getEnquiry().getRequestType();
			setRequestType(requestType);
			setFields(processModel.getEnquiry(), requestType);
		}
	}

	private void setFields(final BusinessEnquiryModel enquiryModel, final String requestType)
	{
		final DateFormat df = new SimpleDateFormat(EMAIL_DATE_FORMAT);

		AbstractBusinessEnquiryData enquiry = null;
		switch (requestType)
		{
			case SabmCoreConstants.BUSINESS_ENQUIRY_DELIVERY:
				setSubjectRequestType(BUSINES_ENQUIRY_DELIVERY);
				enquiry = createDeliveryEnquiryData(enquiryModel, df);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_RETURN:
				setSubjectRequestType(BUSINES_ENQUIRY_PRODUCT_RETURN);
				enquiry = createProductReturnEnquiryData(enquiryModel, df);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_KEG:
				setSubjectRequestType(BUSINES_ENQUIRY_KEG_ISSUE);
				enquiry = createKegIssueEnquiryData(enquiryModel, df);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_DELIVERY_ISSUE:
				setSubjectRequestType(BUSINES_ENQUIRY_DELIVERY_ISSUE);
				enquiry = createDeliveryIssueEnquiryData(enquiryModel, df);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PRICE:
				setSubjectRequestType(BUSINES_ENQUIRY_PRICE);
				enquiry = createPriceEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PRODUCT:
				setSubjectRequestType(BUSINES_ENQUIRY_PRODUCT);
				enquiry = createProductEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PICKUP:
				setSubjectRequestType(BUSINES_ENQUIRY_KEG_PICKUP);
				enquiry = createKegPickupEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_UPDATE:
				setSubjectRequestType(BUSINES_ENQUIRY_UPDATE);
				enquiry = createUpdateEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PALLET:
				setSubjectRequestType(BUSINES_ENQUIRY_EMPTY_PALLET);
				enquiry = createEmptyPalletEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_WEBSITE:
				setSubjectRequestType(BUSINES_ENQUIRY_WEBSITE_ERRORS);
				enquiry = createWebsiteErrorsEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_GENERAL:
				setSubjectRequestType(BUSINES_ENQUIRY_GENERAL);
				enquiry = createGeneralEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_WEBSITE_ENQ:
				setSubjectRequestType(BUSINES_ENQUIRY_WEBSITE_FEEDBACK);
				enquiry = createWebsiteEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_CONTACT_US:
				setSubjectRequestType(BUSINES_ENQUIRY_GENERAL);
				enquiry = createContactUsData(enquiryModel);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_AUTOPAY:
				setSubjectRequestType(BUSINES_ENQUIRY_AUTOPAY);
				enquiry = createAutopayEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.ORDER_ENQUIRY:
				setSubjectRequestType(BUSINES_ENQUIRY_ORDER);
				enquiry = createOrderEnquiryData(enquiryModel);
				break;
			case SabmCoreConstants.UPDATE_EXISTING_ENQUIRY:
				setSubjectRequestType(BUSINES_ENQUIRY_UPDATE_EXISTING);
				enquiry = createUpdateExistingEnquiryData(enquiryModel);
				break;
			default:
				enquiry = null;
				break;
		}

		setBusinessEnquiryData(enquiry);
	}

	private void setCommonFields(final BusinessEnquiryModel enquiryModel, final AbstractBusinessEnquiryData businessEnquiryData)
	{
		businessEnquiryData.setName(enquiryModel.getName());
		businessEnquiryData.setBusinessUnit(enquiryModel.getBusinessUnit());
		businessEnquiryData.setPreferredContactMethod(enquiryModel.getPreferredContactMethod());
		businessEnquiryData.setEmailAddress(enquiryModel.getEmailAddress());
		businessEnquiryData.setRequestType(enquiryModel.getRequestType());
		businessEnquiryData.setPhoneNumber(enquiryModel.getPhoneNumber());
		businessEnquiryData.setUid(enquiryModel.getUid());
		businessEnquiryData.setPlantID(enquiryModel.getPlant());

	}

	private AbstractBusinessEnquiryData createDeliveryEnquiryData(final BusinessEnquiryModel enquiryModel, final DateFormat df)
	{
		final SabmDeliveryEnquiryData data = new SabmDeliveryEnquiryData();
		setCommonFields(enquiryModel, data);

		final DeliveryEnquiryModel model = (DeliveryEnquiryModel) enquiryModel;
		data.setOrderNumber(model.getOrderNumber());
		data.setOrderDate(model.getOrderDate());
		data.setExpectedDeliveryDate(model.getExpectedDeliveryDate());
		data.setOtherInformation(model.getOtherInformation());
		data.setOrderDateString(df.format(model.getOrderDate()));
		data.setExpectedDeliveryDateString(df.format(model.getExpectedDeliveryDate()));

		return data;
	}

	private AbstractBusinessEnquiryData createProductReturnEnquiryData(final BusinessEnquiryModel enquiryModel,
			final DateFormat df)
	{
		final SabmProductReturnData data = new SabmProductReturnData();
		setCommonFields(enquiryModel, data);

		final ProductReturnEnquiryModel model = (ProductReturnEnquiryModel) enquiryModel;
		data.setInvoiceNumber(model.getInvoiceNumber());
		data.setInvoiceDate(model.getInvoiceDate());
		data.setProductDescription(model.getProductDescription());
		data.setProductQuantity(model.getProductQuantity());
		data.setProductQuantityUOM(model.getProductQuantityUOM());
		data.setReturnReason(model.getReturnReason());
		data.setStockReturned(model.getStockReturned());
		data.setOtherInformation(model.getOtherInformation());
		data.setInvoiceDateString(df.format(model.getInvoiceDate()));

		return data;
	}

	private AbstractBusinessEnquiryData createKegIssueEnquiryData(final BusinessEnquiryModel enquiryModel, final DateFormat df)
	{
		final SabmKegIssueData data = new SabmKegIssueData();
		setCommonFields(enquiryModel, data);

		final KegIssueEnquiryModel model = (KegIssueEnquiryModel) enquiryModel;
		data.setKegBrand(model.getKegBrand());
		data.setKegNumber(model.getKegNumber());
		data.setBestBeforeDate(model.getBestBeforeDate());
		data.setKegProblem(model.getKegProblem());
		data.setBestBeforeDateString(df.format(model.getBestBeforeDate()));
        data.setPlantcode(model.getPlantcode());
        data.setTimecode(model.getTimecode());
		

		return data;
	}

	private AbstractBusinessEnquiryData createDeliveryIssueEnquiryData(final BusinessEnquiryModel enquiryModel,
			final DateFormat df)
	{
		final SabmDeliveryIssueData data = new SabmDeliveryIssueData();
		setCommonFields(enquiryModel, data);

		final DeliveryIssueEnquiryModel model = (DeliveryIssueEnquiryModel) enquiryModel;
		data.setInvoiceNumber(model.getInvoiceNumber());
		data.setInvoiceDate(model.getInvoiceDate());
		data.setProduct(model.getProduct());
		data.setQuantity(model.getQuantity());
		data.setQuantityUOM(model.getQuantityUOM());
		data.setDamageStock(model.getDamageStock());
		data.setDamagePremise(model.getDamagePremise());
		data.setKegsNotCollected(model.getKegsNotCollected());
		data.setPickingError(model.getPickingError());
		data.setDriverComplaint(model.getDriverComplaint());
		data.setNotAllItemsDelivered(model.getNotAllItemsDelivered());
		data.setOther(model.getOther());
		data.setOtherInformation(model.getOtherInformation());
		data.setInvoiceDateString(df.format(model.getInvoiceDate()));

		return data;
	}

	private AbstractBusinessEnquiryData createPriceEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmPriceEnquiryData data = new SabmPriceEnquiryData();
		setCommonFields(enquiryModel, data);

		final PriceEnquiryModel model = (PriceEnquiryModel) enquiryModel;
		data.setType(model.getType());
		data.setProduct(model.getProduct());
		data.setDiscountExpected(model.getDiscountExpected());
		data.setMinQuantity(model.getMinQuantity());
		data.setDiscountDisplayed(model.getDiscountDisplayed());
		data.setOtherInformation(model.getOtherInformation());

		return data;
	}

	private AbstractBusinessEnquiryData createProductEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmProductEnquiryData data = new SabmProductEnquiryData();
		setCommonFields(enquiryModel, data);

		final ProductEnquiryModel model = (ProductEnquiryModel) enquiryModel;
		data.setProduct(model.getProduct());
		data.setPromotionalStock(model.getPromotionalStock());
		data.setEnquiryInformation(model.getEnquiryInformation());

		return data;
	}

	private AbstractBusinessEnquiryData createKegPickupEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmKegPickupData data = new SabmKegPickupData();
		setCommonFields(enquiryModel, data);

		final KegPickupEnquiryModel model = (KegPickupEnquiryModel) enquiryModel;
		data.setNumberOfEmptyKegs(model.getNumberOfEmptyKegs());
		data.setNumberOfPartFullKegs(model.getNumberOfPartFullKegs());
		data.setOtherInformation(model.getOtherInformation());

		return data;
	}

	private AbstractBusinessEnquiryData createUpdateEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmMyDetailsAndDeliverOptionsData data = new SabmMyDetailsAndDeliverOptionsData();
		setCommonFields(enquiryModel, data);

		final MyDetailsEnquiryModel model = (MyDetailsEnquiryModel) enquiryModel;
		data.setChangeType(model.getChangeType());
		data.setCurrentDetails(model.getCurrentDetails());
		data.setNewDetails(model.getNewDetails());
		data.setOtherInformation(model.getOtherInformation());

		return data;
	}

	private AbstractBusinessEnquiryData createEmptyPalletEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmEmptyPalletPickupData data = new SabmEmptyPalletPickupData();
		setCommonFields(enquiryModel, data);

		final EmptyPalletEnquiryModel model = (EmptyPalletEnquiryModel) enquiryModel;
		data.setNumberOfEmptyPallets(model.getNumberOfEmptyPallets());
		data.setOtherInformation(model.getOtherInformation());

		return data;
	}

	private AbstractBusinessEnquiryData createWebsiteErrorsEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmWebsiteErrorsData data = new SabmWebsiteErrorsData();
		setCommonFields(enquiryModel, data);

		final WebsiteErrorsEnquiryModel model = (WebsiteErrorsEnquiryModel) enquiryModel;
		data.setIssueDescription(model.getIssueDescription());

		return data;
	}

	private AbstractBusinessEnquiryData createGeneralEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmGeneralInquiryData data = new SabmGeneralInquiryData();
		setCommonFields(enquiryModel, data);

		final GeneralEnquiryModel model = (GeneralEnquiryModel) enquiryModel;
		data.setInquiryMessage(model.getInquiryMessage());

		return data;
	}

	private AbstractBusinessEnquiryData createWebsiteEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmWebsiteInquiryFeedbackData data = new SabmWebsiteInquiryFeedbackData();
		setCommonFields(enquiryModel, data);

		final WebsiteEnquiryModel model = (WebsiteEnquiryModel) enquiryModel;
		data.setWebsiteEnquiryFeedback(model.getWebsiteEnquiryFeedback());

		return data;
	}

	private AbstractBusinessEnquiryData createContactUsData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmContactUsData data = new SabmContactUsData();
		setCommonFields(enquiryModel, data);

		final ContactUsModel model = (ContactUsModel) enquiryModel;
		data.setInquiryMessage(model.getInquiryMessage());

		return data;
	}

	private AbstractBusinessEnquiryData createAutopayEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmAutopayInquiryData data = new SabmAutopayInquiryData();
		setCommonFields(enquiryModel, data);

		final AutopayEnquiryModel model = (AutopayEnquiryModel) enquiryModel;
		data.setInquiryMessage(model.getInquiryMessage());

		return data;
	}
	
	private AbstractBusinessEnquiryData createOrderEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmOrderEnquiryData data = new SabmOrderEnquiryData();
		setCommonFields(enquiryModel, data);
		final OrderEnquiryModel model = (OrderEnquiryModel) enquiryModel;
		data.setOrderNumber(model.getOrderNumber());
		data.setYourMessage(model.getYourMessage());
		return data;
	}

	private AbstractBusinessEnquiryData createUpdateExistingEnquiryData(final BusinessEnquiryModel enquiryModel)
	{
		final SabmUpdateExistingEnquiryData data = new SabmUpdateExistingEnquiryData();
		setCommonFields(enquiryModel, data);
		final UpdateExistingEnquiryModel model = (UpdateExistingEnquiryModel) enquiryModel;
		data.setCaseNumber(model.getCaseNumber());
		data.setYourMessage(model.getYourMessage());
		return data;
	}


	/**
	 * @return the businessEnquiryData
	 */
	public AbstractBusinessEnquiryData getBusinessEnquiryData()
	{
		return businessEnquiryData;
	}

	/**
	 * @param businessEnquiryData
	 *           the businessEnquiryData to set
	 */
	public void setBusinessEnquiryData(final AbstractBusinessEnquiryData businessEnquiryData)
	{
		this.businessEnquiryData = businessEnquiryData;
	}

	/**
	 * @return the requestType
	 */
	public String getRequestType()
	{
		return requestType;
	}

	/**
	 * @param requestType
	 *           the requestType to set
	 */
	public void setRequestType(final String requestType)
	{
		this.requestType = requestType;
	}

	/**
	 * @return the subjectRequestType
	 */
	public String getSubjectRequestType()
	{
		return subjectRequestType;
	}

	/**
	 * @param subjectRequestType
	 *           the subjectRequestType to set
	 */
	public void setSubjectRequestType(final String subjectRequestType)
	{
		this.subjectRequestType = subjectRequestType;
	}
}

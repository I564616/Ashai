/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;

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
public class BusinessEnquiryEmailEventListener extends AbstractSiteEventListener<BusinessEnquiryEmailEvent>
{
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "userService")
	private UserService UserService;

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final BusinessEnquiryEmailEvent event)
	{
		final BusinessEnquiryEmailProcessModel businessEnquiryEmailProcessModel = (BusinessEnquiryEmailProcessModel) getBusinessProcessService()
				.createProcess("businessEnquiry-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						"businessEnquiryEmailProcess");
		businessEnquiryEmailProcessModel.setSite(event.getSite());
		businessEnquiryEmailProcessModel.setCustomer(event.getCustomer());
		businessEnquiryEmailProcessModel.setLanguage(event.getLanguage());
		businessEnquiryEmailProcessModel.setCurrency(event.getCurrency());
		businessEnquiryEmailProcessModel.setStore(event.getBaseStore());
		businessEnquiryEmailProcessModel.setToEmails(event.getToEmails());
		businessEnquiryEmailProcessModel.setCcEmails(event.getCcEmails());

		final BusinessEnquiryModel enquiry = generateEnquiryModel(event.getEnquiry(), event.getRequestType());
		businessEnquiryEmailProcessModel.setEnquiry(enquiry);

		getModelService().save(businessEnquiryEmailProcessModel);
		getBusinessProcessService().startProcess(businessEnquiryEmailProcessModel);
	}

	private BusinessEnquiryModel generateEnquiryModel(final AbstractBusinessEnquiryData enquiryData, final String requestType)
	{
		BusinessEnquiryModel enquiry = null;
		switch (requestType)
		{
			case SabmCoreConstants.BUSINESS_ENQUIRY_DELIVERY:
				enquiry = generateDeliveryEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_RETURN:
				enquiry = generateProductReturnEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_KEG:
				enquiry = generateKegIssueEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_DELIVERY_ISSUE:
				enquiry = generateDeliveryIssueEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PRICE:
				enquiry = generatePriceEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PRODUCT:
				enquiry = generateProductEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PICKUP:
				enquiry = generateKegPickupEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_UPDATE:
				enquiry = generateUpdateEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_PALLET:
				enquiry = generateEmptyPalletEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_WEBSITE:
				enquiry = generateWebsiteErrorsEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_GENERAL:
				enquiry = generateGeneralEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_WEBSITE_ENQ:
				enquiry = generateWebsiteInquiryEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_CONTACT_US:
				enquiry = generateContactUsModel(enquiryData);
				break;
			case SabmCoreConstants.BUSINESS_ENQUIRY_AUTOPAY:
				enquiry = generateAutopayEnquiryModel(enquiryData);
				break;
			case SabmCoreConstants.ORDER_ENQUIRY:
            enquiry = generateOrderEnquiryModel(enquiryData);
            break;
				case SabmCoreConstants.UPDATE_EXISTING_ENQUIRY:
            enquiry = generateUpdateExistingEnquiryModel(enquiryData);
            break;
			default:
				enquiry = null;
				break;
		}

		return enquiry;
	}


	private void supplyEnquiryCommonFieldValues(final AbstractBusinessEnquiryData enquiryData,
			final BusinessEnquiryModel enquiryModel)
	{
		enquiryModel.setName(enquiryData.getName());
		enquiryModel.setBusinessUnit(enquiryData.getBusinessUnit());
		enquiryModel.setRequestType(enquiryData.getRequestType());
		enquiryModel.setPreferredContactMethod(enquiryData.getPreferredContactMethod());
		enquiryModel.setEmailAddress(enquiryData.getEmailAddress());
		enquiryModel.setPhoneNumber(enquiryData.getPhoneNumber());
		final CustomerModel customer = (CustomerModel) UserService.getCurrentUser();
		if (customer != null && customer instanceof B2BCustomerModel)
		{
			B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
			if (b2bUnit == null)
			{
				b2bUnit = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
			}
			if (b2bUnit != null)
			{
				enquiryModel.setUid(b2bCommerceUnitService.getParentUnit().getUid());
				if (b2bUnit.getPlant() != null)
				{
					enquiryModel.setPlant(b2bCommerceUnitService.getParentUnit().getPlant().getPlantId());
				}
			}
		}
	}

	private BusinessEnquiryModel generateDeliveryEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmDeliveryEnquiryData data = (SabmDeliveryEnquiryData) enquiryData;

		final DeliveryEnquiryModel enquiry = getModelService().create(DeliveryEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setOrderNumber(data.getOrderNumber());
		enquiry.setOrderDate(data.getOrderDate());
		enquiry.setExpectedDeliveryDate(data.getExpectedDeliveryDate());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateProductReturnEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmProductReturnData data = (SabmProductReturnData) enquiryData;

		final ProductReturnEnquiryModel enquiry = getModelService().create(ProductReturnEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setInvoiceNumber(data.getInvoiceNumber());
		enquiry.setInvoiceDate(data.getInvoiceDate());
		enquiry.setProductDescription(data.getProductDescription());
		enquiry.setProductQuantity(data.getProductQuantity());
		enquiry.setProductQuantityUOM(data.getProductQuantityUOM());
		enquiry.setReturnReason(data.getReturnReason());
		enquiry.setStockReturned(data.getStockReturned());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateKegIssueEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmKegIssueData data = (SabmKegIssueData) enquiryData;

		final KegIssueEnquiryModel enquiry = getModelService().create(KegIssueEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setKegBrand(data.getKegBrand());
		enquiry.setKegNumber(data.getKegNumber());
		enquiry.setBestBeforeDate(data.getBestBeforeDate());
		enquiry.setKegProblem(data.getKegProblem());
        enquiry.setPlantcode(data.getPlantcode());
        enquiry.setTimecode(data.getTimecode());
		
		return enquiry;
	}

	private BusinessEnquiryModel generateDeliveryIssueEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmDeliveryIssueData data = (SabmDeliveryIssueData) enquiryData;

		final DeliveryIssueEnquiryModel enquiry = getModelService().create(DeliveryIssueEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setInvoiceNumber(data.getInvoiceNumber());
		enquiry.setInvoiceDate(data.getInvoiceDate());
		enquiry.setProduct(data.getProduct());
		enquiry.setQuantity(data.getQuantity());
		enquiry.setQuantityUOM(data.getQuantityUOM());
		enquiry.setDamageStock(data.getDamageStock());
		enquiry.setDamagePremise(data.getDamagePremise());
		enquiry.setKegsNotCollected(data.getKegsNotCollected());
		enquiry.setPickingError(data.getPickingError());
		enquiry.setDriverComplaint(data.getDriverComplaint());
		enquiry.setNotAllItemsDelivered(data.getNotAllItemsDelivered());
		enquiry.setOther(data.getOther());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generatePriceEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmPriceEnquiryData data = (SabmPriceEnquiryData) enquiryData;

		final PriceEnquiryModel enquiry = getModelService().create(PriceEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setType(data.getType());
		enquiry.setProduct(data.getProduct());
		enquiry.setDiscountExpected(data.getDiscountExpected());
		enquiry.setMinQuantity(data.getMinQuantity());
		enquiry.setDiscountDisplayed(data.getDiscountDisplayed());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateProductEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmProductEnquiryData data = (SabmProductEnquiryData) enquiryData;

		final ProductEnquiryModel enquiry = getModelService().create(ProductEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setProduct(data.getProduct());
		enquiry.setPromotionalStock(data.getPromotionalStock());
		enquiry.setEnquiryInformation(data.getEnquiryInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateKegPickupEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmKegPickupData data = (SabmKegPickupData) enquiryData;

		final KegPickupEnquiryModel enquiry = getModelService().create(KegPickupEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setNumberOfEmptyKegs(data.getNumberOfEmptyKegs());
		enquiry.setNumberOfPartFullKegs(data.getNumberOfPartFullKegs());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateUpdateEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmMyDetailsAndDeliverOptionsData data = (SabmMyDetailsAndDeliverOptionsData) enquiryData;

		final MyDetailsEnquiryModel enquiry = getModelService().create(MyDetailsEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setChangeType(data.getChangeType());
		enquiry.setCurrentDetails(data.getCurrentDetails());
		enquiry.setNewDetails(data.getNewDetails());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateEmptyPalletEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmEmptyPalletPickupData data = (SabmEmptyPalletPickupData) enquiryData;

		final EmptyPalletEnquiryModel enquiry = getModelService().create(EmptyPalletEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setNumberOfEmptyPallets(data.getNumberOfEmptyPallets());
		enquiry.setOtherInformation(data.getOtherInformation());

		return enquiry;
	}

	private BusinessEnquiryModel generateWebsiteErrorsEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmWebsiteErrorsData data = (SabmWebsiteErrorsData) enquiryData;

		final WebsiteErrorsEnquiryModel enquiry = getModelService().create(WebsiteErrorsEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setIssueDescription(data.getIssueDescription());

		return enquiry;
	}

	private BusinessEnquiryModel generateGeneralEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmGeneralInquiryData data = (SabmGeneralInquiryData) enquiryData;

		final GeneralEnquiryModel enquiry = getModelService().create(GeneralEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setInquiryMessage(data.getInquiryMessage());

		return enquiry;
	}

	private BusinessEnquiryModel generateContactUsModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmContactUsData data = (SabmContactUsData) enquiryData;

		final ContactUsModel enquiry = getModelService().create(ContactUsModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setInquiryMessage(data.getInquiryMessage());

		return enquiry;
	}

	private BusinessEnquiryModel generateWebsiteInquiryEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmWebsiteInquiryFeedbackData data = (SabmWebsiteInquiryFeedbackData) enquiryData;

		final WebsiteEnquiryModel enquiry = getModelService().create(WebsiteEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setWebsiteEnquiryFeedback(data.getWebsiteEnquiryFeedback());

		return enquiry;
	}

	private BusinessEnquiryModel generateAutopayEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
	{
		final SabmAutopayInquiryData data = (SabmAutopayInquiryData) enquiryData;

		final AutopayEnquiryModel enquiry = getModelService().create(AutopayEnquiryModel.class);
		supplyEnquiryCommonFieldValues(data, enquiry);

		// set values specific to this model
		enquiry.setInquiryMessage(data.getInquiryMessage());

		return enquiry;
	}
	
	private BusinessEnquiryModel generateOrderEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
   {
       final SabmOrderEnquiryData data = (SabmOrderEnquiryData) enquiryData;
       final OrderEnquiryModel enquiry = getModelService().create(OrderEnquiryModel.class);
       supplyEnquiryCommonFieldValues(data, enquiry);
       // set values specific to this model
       enquiry.setOrderNumber(data.getOrderNumber());
       enquiry.setYourMessage(data.getYourMessage());
       return enquiry;
   }

	private BusinessEnquiryModel generateUpdateExistingEnquiryModel(final AbstractBusinessEnquiryData enquiryData)
   {
       final SabmUpdateExistingEnquiryData data = (SabmUpdateExistingEnquiryData) enquiryData;
       final UpdateExistingEnquiryModel enquiry = getModelService().create(UpdateExistingEnquiryModel.class);
       supplyEnquiryCommonFieldValues(data, enquiry);
       // set values specific to this model
       enquiry.setCaseNumber(data.getCaseNumber());
       enquiry.setYourMessage(data.getYourMessage());
       return enquiry;
   }

	@Override
	protected boolean shouldHandleEvent(final BusinessEnquiryEmailEvent event)
	{
		return true;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}


	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}

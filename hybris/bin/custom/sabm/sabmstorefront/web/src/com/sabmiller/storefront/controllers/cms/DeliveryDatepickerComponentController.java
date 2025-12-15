/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.controllers.cms;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.servicelayer.session.SessionService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabm.core.model.cms.components.DeliveryDatepickerComponentModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.DeliveryModeType;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.delivery.data.B2bDeliveryDatesConfig;
import com.sabmiller.facades.delivery.data.DeliveryModePackTypeDeliveryDatesData;
import com.sabmiller.storefront.controllers.ControllerConstants;


/**
 * Controller for CMS DeliveryDatepickerComponent.
 */
@Controller("DeliveryDatepickerComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.DeliveryDatepickerComponentController)
public class DeliveryDatepickerComponentController extends AbstractCMSComponentController<DeliveryDatepickerComponentModel>
{

	/** The customer facade. */
	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;
	
	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final String SESSIONSERVICE_PLANT_CUTOFF_TIMEZONE_SUFFIX = "_PlantCutoffTimeZone";

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.storefront.controllers.cms.AbstractCMSComponentController#fillModel(javax.servlet.http.
	 * HttpServletRequest, org.springframework.ui.Model,
	 * de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
	 */
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final DeliveryDatepickerComponentModel component)
	{
		List<Long> enabledDeliveryDates = customerFacade.getDeliveryDates(true);
		Collections.sort(enabledDeliveryDates);
		model.addAttribute("enabledDates", enabledDeliveryDates);
		final Object dataObject = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		if (dataObject instanceof Date && cartFacade.hasSessionCart())
		{
			model.addAttribute("selectedDeliveryDate", sdf.format(dataObject));
			model.addAttribute("selectedDeliveryTimestamp", ((Date) dataObject).getTime());
			HashMap<String, String> cutofftimeMap = getCutOffTimeForDeliveryDate((Date) dataObject);
			if(null != cutofftimeMap) {
				if(null != cutofftimeMap.get(SabmCoreConstants.CUTOFFTIME)) {
					model.addAttribute(SabmCoreConstants.CUTOFFTIME, cutofftimeMap.get(SabmCoreConstants.CUTOFFTIME));
				}
				if (null != cutofftimeMap.get(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE)) {
					model.addAttribute(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE, cutofftimeMap.get(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE));
				}
			}
		}
		else
		{
			final Date nextDeliveryDate = customerFacade.getNextDeliveryDateAndUpdateSession();

			model.addAttribute("selectedDeliveryDate", sdf.format(nextDeliveryDate));
			model.addAttribute("selectedDeliveryTimestamp", nextDeliveryDate.getTime());
			HashMap<String, String> cutofftimeMap = getCutOffTimeForDeliveryDate((Date) dataObject);
			if(null != cutofftimeMap) {
				if(null != cutofftimeMap.get(SabmCoreConstants.CUTOFFTIME)) {
					model.addAttribute(SabmCoreConstants.CUTOFFTIME, cutofftimeMap.get(SabmCoreConstants.CUTOFFTIME));
				}
				if (null != cutofftimeMap.get(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE)) {
					model.addAttribute(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE, cutofftimeMap.get(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE));
				}
			}
		}
		model.addAttribute("deliveryDatesConfigData", getUpdatedDeliveryDateConfig());
		model.addAttribute("publicHolidayData", customerFacade.addPublicHolidayData(b2bCommerceUnitService.getParentUnit()));
	}


	/**
	 * Get values of the calendar delivery mode, selected shipping carrier and dates based on values in cart
	 * @return
	 */
	@GetMapping(value = "/getUpdatedDeliveryDateConfig", produces = "application/json")
	public @ResponseBody B2bDeliveryDatesConfig getUpdatedDeliveryDateConfig()
	{
		//check if session has a cart
		//if there is cart - get defaults in cart and set to calendar
		//else get defaults in calendar and set it to cart

		B2bDeliveryDatesConfig deliveryDatesConfig = customerFacade.getDeliveryDatesConfig(b2bCommerceUnitService.getParentUnit());

		fallbackMethodForIncorrectData(deliveryDatesConfig);
		return deliveryDatesConfig;
	}

	/**
	 *  Fallback if no dates for the specified delivery mode and their is date for the other delivery mode - set the data packtype to the other delivery mode
	 * @param deliveryDatesConfig
	 */
	private void fallbackMethodForIncorrectData(final B2bDeliveryDatesConfig deliveryDatesConfig)
	{
		boolean isCustomerOwned = BooleanUtils.toBoolean(deliveryDatesConfig.getCustomerOwned());
		boolean isCustomerArranged = deliveryDatesConfig.getCustomerArrangedEnabled();
		boolean isCubArranged = deliveryDatesConfig.getCubArrangedEnabled();
		final List<DeliveryModePackTypeDeliveryDatesData> deliveryDatesData = deliveryDatesConfig.getDeliveryDatesData();

		if (isCustomerOwned && !isCustomerArranged && isCubArranged)
		{
			deliveryDatesData.forEach(d -> d.setMode(DeliveryModeType.CUSTOMER_DELIVERY));
		}
		else if (!isCustomerOwned && !isCubArranged && isCustomerArranged)
		{
			deliveryDatesData.forEach(d -> d.setMode(DeliveryModeType.CUB_DELIVERY));
		}
	}
	
	private HashMap<String, String> getCutOffTimeForDeliveryDate(Date deliveryDate){
      
		B2BUnitModel b2bUnit = null;

      String cutoffTime = null;
      String plantCutoffTimeZone = null;
      try
      {
          b2bUnit = b2bCommerceUnitService.getParentUnit();
      }
      catch (final Exception e)
      {
          LOG.error("unable to get the b2bunit");
      }
      if (b2bUnit == null)
      {
          b2bUnit = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
      }
      HashMap<String, String> returnCutOffMap = null;
      if(sessionService.getAttribute(b2bUnit.getUid()+deliveryDate) != null && StringUtils.isNotEmpty(sessionService.getAttribute(b2bUnit.getUid()+deliveryDate))
      		&& StringUtils.isNotEmpty(sessionService.getAttribute(b2bUnit.getUid()+deliveryDate+SESSIONSERVICE_PLANT_CUTOFF_TIMEZONE_SUFFIX))){
    	  cutoffTime =  sessionService.getAttribute(b2bUnit.getUid()+deliveryDate);
    	  returnCutOffMap = new HashMap<String, String>();
    	  returnCutOffMap.put(SabmCoreConstants.CUTOFFTIME, cutoffTime);
    	  plantCutoffTimeZone = sessionService.getAttribute(b2bUnit.getUid()+deliveryDate+SESSIONSERVICE_PLANT_CUTOFF_TIMEZONE_SUFFIX);
    	  returnCutOffMap.put(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE, plantCutoffTimeZone);
      }else {
      	returnCutOffMap = deliveryDateCutOffService.getCutOffTimeforCalendarToDisplay(b2bUnit, deliveryDate); 
      	cutoffTime = returnCutOffMap.get(SabmCoreConstants.CUTOFFTIME);
	      sessionService.setAttribute(b2bUnit.getUid()+deliveryDate, cutoffTime);
	      if(StringUtils.isNotEmpty(returnCutOffMap.get(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE))) {
	      	sessionService.setAttribute(b2bUnit.getUid()+deliveryDate+SESSIONSERVICE_PLANT_CUTOFF_TIMEZONE_SUFFIX, returnCutOffMap.get(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE));
	      }  
      }	        
      
      final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
      
      try
      {
          if(cutoffTime != null && StringUtils.isNotEmpty(cutoffTime)) {
          	Date CutoffDate = df.parse(cutoffTime);
             df.applyPattern("EEEEE d MMM hh:mm aa");
             returnCutOffMap.replace(SabmCoreConstants.CUTOFFTIME, df.format(CutoffDate));
             return returnCutOffMap;
          }       	  	
         
      }
      catch (ParseException e)
      {
          LOG.error("Error occured while parsing cutoff time"+e);
      }
      
      return  null;
       
  }

}

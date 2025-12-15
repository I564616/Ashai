package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.core.model.order.OrderModel;

import jakarta.annotation.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.model.PlantCutOffModel;
import com.sabmiller.core.model.TimeZoneModel;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.order.data.TrackOrderData;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;

/**
 * Created by zhuo.a.jiang on 9/01/2018.
 */
@Controller
@Scope("tenant")
@RequestMapping("/trackorders")
public class TrackOrderPageController extends SabmAbstractPageController {


    /** The Constant Track_order_CMS_PAGE. */
    private static final String TRACKORDER_CMS_PAGE = "trackorder";


    @Resource(name = "simpleBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

    @Resource(name = "orderFacade")
    private SABMOrderFacade orderFacade;

    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;

 	@Resource(name = "sabmDeliveryDateCutOffService")
 	private SABMDeliveryDateCutOffService deliveryDateCutOffService;



    @GetMapping
    @RequireHardLogIn
    public String trackOrders(@RequestParam(value = "code", required = true) final String orderCode, final Model model) throws Exception,CMSItemNotFoundException
    {

        storeCmsPageInModel(model, getContentPageForLabelOrId(TRACKORDER_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRACKORDER_CMS_PAGE));

        model.addAttribute("pageType", SABMWebConstants.PageType.TRACKORDER.name());
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        List<Breadcrumb> breadcrumbs = resourceBreadcrumbBuilder.getBreadcrumbs("text.trackorder.header");
        model.addAttribute("breadcrumbs", breadcrumbs);

        OrderModel orderModel = orderFacade.getOrderBySapSalesOrderNumber(orderCode);

        if(orderModel  == null ){
            throw new Exception("Order not found");

        }

        // final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderModel.getCode());
        // model.addAttribute("orderData", orderDetails);


        final List<TrackOrderData> trackOrderData = orderFacade.getTrackOrderData(orderModel);
        setETAWindowPassed(trackOrderData);
        model.addAttribute("trackOrderData",trackOrderData);

        model.addAttribute("isTrackDeliveryOrderFeatureEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER));
        
        
        return getViewForPage(model);
    }
    
    /*If the actual time passes the estimated time of arrival show on the webpage then a notification message 
    should be shown 'Your order has been delayed, please wait for the estimated time of arrival to be updated."
    */ 
    private void setETAWindowPassed(List<TrackOrderData> trackOrderData){
   	 for(TrackOrderData consignmenTracktData:trackOrderData){
   		 ConsignmentData consignment = consignmenTracktData.getConsignment();
   		 consignmenTracktData.setETAPassed(false);
   		 if(!ConsignmentStatus.DELIVERED.equals(consignment.getStatus())){
   			  if(consignmenTracktData.getEndETA()!=null && new Date().after(consignmenTracktData.getEndETA())){
   				  consignmenTracktData.setETAPassed(true);
   				  }
   		 }
   	 }
   
    }


	
    

}

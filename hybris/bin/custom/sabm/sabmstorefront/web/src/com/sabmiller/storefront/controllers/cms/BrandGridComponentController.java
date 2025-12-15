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

import de.hybris.platform.acceleratorcms.model.components.SimpleBannerComponentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabm.core.model.cms.components.BrandGridComponentModel;
import de.hybris.platform.b2b.services.B2BUnitService;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMCMSUserGroupRestrictionModel;
import com.sabmiller.storefront.controllers.ControllerConstants;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controller for CMS BestsellerComponent.
 */
@Controller("BrandGridComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.BrandGridComponent)
public class BrandGridComponentController extends AbstractCMSComponentController<BrandGridComponentModel>
{
	
	private static final Logger LOG = LoggerFactory.getLogger(BrandGridComponentController.class.getName());
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "b2bUnitService")
	protected B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;
	
	@Resource(name = "sessionService")
	private SessionService sessionService;
	
	
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final BrandGridComponentModel component)
	{
		List<SimpleBannerComponentModel> filterBrands = new ArrayList<SimpleBannerComponentModel>();
		List<SimpleBannerComponentModel> brands = component.getBrands();
		
		for(SimpleBannerComponentModel brand : CollectionUtils.emptyIfNull(brands)){
			for(AbstractRestrictionModel restriction : CollectionUtils.emptyIfNull(brand.getRestrictions())){
				if(isBrandRestricted(restriction)){
					filterBrands.add(brand);
				}
			}
			if(CollectionUtils.isEmpty(brand.getRestrictions())){
				filterBrands.add(brand);
			}
		}		
		
		model.addAttribute("filterBrands", filterBrands);
		
	}
	
	protected boolean isBrandRestricted(AbstractRestrictionModel restriction){
		
		boolean evaluation = true;
		
      /*final UserModel currentUserModel = this.userService.getCurrentUser();
      B2BCustomerModel b2bCustomerModel = null;
      if (currentUserModel instanceof B2BCustomerModel)
      {
      	b2bCustomerModel = (B2BCustomerModel) currentUserModel;
      }
         	 
      final B2BUnitModel parent = b2bUnitService.getParent(b2bCustomerModel);
      if (parent != null)
      {         
      	return evaluateB2BUnit(restriction, parent);         
      }*/
      
      B2BUnitModel b2bUnit = null;

		try
		{
			b2bUnit = b2bCommerceUnitService.getParentUnit();
			
			if (b2bUnit == null)
			{
				b2bUnit = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
			}			
			
			if (b2bUnit != null)
	      {         
	      	return evaluateB2BUnit(restriction, b2bUnit);         
	      }
		}
		catch (final Exception e)
		{
			LOG.error("unable to get the b2bunit",e);
		}		
      
      return evaluation;
		
	}
	
	private boolean evaluateB2BUnit(final AbstractRestrictionModel restrictionModel,
 			final B2BUnitModel parent)
 	{
		Collection<UserGroupModel> groups =null;
		boolean thisEvaluation = false;
		if(restrictionModel instanceof  SABMCMSUserGroupRestrictionModel){
			groups = ((SABMCMSUserGroupRestrictionModel)restrictionModel).getUserGroups();
			thisEvaluation = ((SABMCMSUserGroupRestrictionModel)restrictionModel).getInverse().booleanValue();
			
      }else if(restrictionModel instanceof CMSUserGroupRestrictionModel){
      	groups = ((CMSUserGroupRestrictionModel)restrictionModel).getUserGroups();
      }
		
 		if (groups == null || groups.isEmpty())
 		{
 			return true;
 		}
 		
 		
 		for (final UserGroupModel group : groups)
 		{
 			B2BUnitModel b2bUnitGroup = null;
 			if (group instanceof B2BUnitModel)
 			{
 				b2bUnitGroup = (B2BUnitModel) group;
 				LOG.info("BrandGridComponentController : evaluateB2BUnit : parent = "+parent.getUid());
 				LOG.info("BrandGridComponentController : evaluateB2BUnit : b2bUnitGroup = "+b2bUnitGroup.getUid());
 				if ((b2bUnitGroup.getAccountGroup().equalsIgnoreCase("ZALB")
 						|| b2bUnitGroup.getAccountGroup().equalsIgnoreCase("ZADP"))
 						&& b2bUnitGroup.getAccountGroup().equalsIgnoreCase(parent.getAccountGroup()) && b2bUnitGroup.getUid().equals(parent.getUid()))
 				{
 					if(thisEvaluation){ 						
 						return false;
 					}else{ 						
 						return true;
 					} 					
 				}
 			}
 		}
 		if(thisEvaluation){
				return true;
		}

 		return thisEvaluation;
 	}
}

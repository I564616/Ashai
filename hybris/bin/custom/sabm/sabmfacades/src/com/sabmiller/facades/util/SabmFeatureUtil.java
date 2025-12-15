package com.sabmiller.facades.util;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.PlantCutOffModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SabmPilotConditionModel;

/**
 * Created by zhuo.a.jiang on 18/01/2018.
 */
public class SabmFeatureUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SabmFeatureUtil.class);

    @Resource(name = "sabmConfigurationService")
    private SabmConfigurationService sabmConfigurationService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "b2bCommerceUnitService")
    private B2BCommerceUnitService b2bCommerceUnitService;

    @Resource(name = "sessionService")
    private SessionService sessionService;

	private static final String ADMIN_USER="b2badmingroup";

	private static final String ORDER_ONLY="b2bordercustomer";


	/**
	 *  This method checks if feature is enabled for current UNIT and current USER.
	 *  should be replace by  boolean isFeatureEnabledForUnit(final String featureName, final B2BUnitModel unit)
	 * @param featureName
	 * @return boolean
	 */
	@Deprecated
	public boolean isFeatureEnabled(final String featureName) {

		UserModel currentUser = userService.getCurrentUser();
		// Show Track order feature for BDEs and for customer only if isTrackMyDeliveryEnabledForCustomers is true in SABM Configuration
		if (StringUtils.equalsIgnoreCase(SabmcommonsConstants.TRACK_DELIVERY_ORDER, featureName))	{
			if (currentUser != null && currentUser instanceof B2BCustomerModel &&
					isUSerGroupValidForTMD(userService.getAllUserGroupsForUser(currentUser)))	{
				if (sabmConfigurationService.isTrackMyDeliveryEnabledForCustomers())	{
					return isCustomerEligibleForFunction(b2bCommerceUnitService.getParentUnit(), featureName);
				}
			}
			
			// BDE user or system Admin - allow to view the feature
			else if (null != sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA))	{
				return true;
			}
		}
		// Show AutoPay Advantage feature for customer only if isAutoPayEnabled is true in SABM Configuration
		if (StringUtils.equalsIgnoreCase(SabmcommonsConstants.AUTOPAY, featureName)) {
			if (userService.getCurrentUser() != null && userService.getCurrentUser() instanceof B2BCustomerModel)	{
				if (sabmConfigurationService.isAutoPayEnabled()) {
					return isCustomerEligibleForFunction(b2bCommerceUnitService.getParentUnit(), featureName);
				}
			}
		}

		if (StringUtils.equalsIgnoreCase(SabmcommonsConstants.INVOICEDISCREPANY, featureName)) {
			if (sabmConfigurationService.isInvoiceDiscrepancyEnabled()) {
				return isCustomerEligibleForFunction(b2bCommerceUnitService.getParentUnit(), featureName);
			}
		}
		
		return false;
	}

	private boolean isUSerGroupValidForTMD(Set<UserGroupModel> userGroupList)
	{
		if (CollectionUtils.isNotEmpty(userGroupList))
		{
			return userGroupList.stream().
					filter(userGroup -> userGroup.getUid().equals(ADMIN_USER) || userGroup.getUid().equals(ORDER_ONLY)).findAny().isPresent();
		}
		return false;
	}
    

	/**
	 *  This method checks if feature is enabled for the specified unit.
	 *  
	 * @param featureName
	 * @return boolean
	 */
	public boolean isFeatureEnabledForUnit(final String featureName, final B2BUnitModel unit) {

		if (unit != null) {
			// Show Track order feature for BDEs and for customer only if isTrackMyDeliveryEnabledForCustomers is true in SABM Configuration
			if (StringUtils.equalsIgnoreCase(SabmcommonsConstants.TRACK_DELIVERY_ORDER, featureName)) {
				if (sabmConfigurationService.isTrackMyDeliveryEnabledForCustomers()) {
					return isCustomerEligibleForFunction(unit, featureName);
				}
			}
			
			// Show AutoPay Advantage feature for customer only if isAutoPayEnabled is true in SABM Configuration
			if (StringUtils.equalsIgnoreCase(SabmcommonsConstants.AUTOPAY, featureName)) {
				if (sabmConfigurationService.isAutoPayEnabled()) {
					return isCustomerEligibleForFunction(unit, featureName);
				}
			}

			if (StringUtils.equalsIgnoreCase(SabmcommonsConstants.INVOICEDISCREPANY, featureName)) {
				if (sabmConfigurationService.isInvoiceDiscrepancyEnabled()) {
					return isCustomerEligibleForFunction(unit, featureName);
				}
			}


		}
		
		return false;
	}

	
	public boolean isCustomerEligibleForFunction(final B2BUnitModel b2bUnitModel, final String featureName) {
 		boolean result = false;
 		
 		final List<B2BUnitModel> list = sabmConfigurationService.getB2BUnitsForPilotFunction(featureName);
 		final List<SabmPilotConditionModel> pilotConditions = sabmConfigurationService.getPilotConditions(featureName);
 		
 		if (Objects.isNull(list) && Objects.isNull(pilotConditions)) {
 			LOG.debug("pilot function " + featureName + " list is empty for {}", b2bUnitModel.getUid());
 			return true;
 		}
 		
 		result = validateB2BUnitForPilotFunction(list, b2bUnitModel);
 		if (!result && CollectionUtils.isNotEmpty(pilotConditions)) {
 			result = validateConditonsForPilotFunction(pilotConditions, b2bUnitModel.getPlant());
 		}
 		LOG.debug(featureName +" is: {} for B2BUnit {}", result, b2bUnitModel.getUid());


 		
 		return result;
 	}
    
	boolean validateB2BUnitForPilotFunction(final List<B2BUnitModel> list,B2BUnitModel b2bUnitModel) {
		return list != null ? list.contains(b2bUnitModel) : false ;
	}

	boolean validateConditonsForPilotFunction(final List<SabmPilotConditionModel> list,PlantModel plant)
	{
		boolean returnFlag = false;
		Optional<SabmPilotConditionModel> plantConditionMatched = list.stream().filter(condition -> condition.getPlant().equals(plant)).findFirst();
		if (plantConditionMatched.isPresent())
		{
			SabmPilotConditionModel condition = plantConditionMatched.get();
			List<String> shippingConditionList = condition.getShippingConditionList().stream().map(i -> i.getCode()).collect(Collectors.toList());
			Optional<PlantCutOffModel> shippingConditionMatched = plant.getCutOffs().stream().filter(cutoff -> shippingConditionList
					.contains(cutoff.getShippingCondition())).findFirst();
			return shippingConditionMatched.isPresent();
		}
		return returnFlag;
	}

	/**
	 * @param statusDisplay
	 */
	public String displayTrackOrderStatus(final String statusDisplay)
	{
		final String orderStatus = StringUtils.capitalize(statusDisplay);

		if (!isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER))
		{
			if (StringUtils.equalsIgnoreCase(statusDisplay, OrderStatus.INTRANSIT.toString())
					|| StringUtils.equalsIgnoreCase(statusDisplay, OrderStatus.NOTDELIVERED.toString())
					|| StringUtils.equalsIgnoreCase(statusDisplay, OrderStatus.COMPLETED.toString())
					|| StringUtils.equalsIgnoreCase(statusDisplay, OrderStatus.PARTIALDELIVERED.toString()))
			{
				return StringUtils.capitalize(OrderStatus.DISPATCHED.toString().toLowerCase());
			}
		}

		return orderStatus;
	}

	/**
	 * @param status
	 * @return
	 */
	public ConsignmentStatus displayTrackConsignmentStatus(final ConsignmentStatus status)
	{

		if (!isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER))
		{
			if (ConsignmentStatus.INTRANSIT.equals(status) || ConsignmentStatus.NOTDELIVERED.equals(status)
					|| ConsignmentStatus.DELIVERED.equals(status))
			{
				return ConsignmentStatus.SHIPPED;
			}
		}

		return status;
	}

}

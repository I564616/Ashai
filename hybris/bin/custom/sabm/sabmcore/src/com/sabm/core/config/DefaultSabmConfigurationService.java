/**
 *
 */
package com.sabm.core.config;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.PilotFuntionType;
import com.sabmiller.core.model.SabmConfigurationModel;
import com.sabmiller.core.model.SabmPilotConditionModel;

/**
 * @author joshua.a.antony
 *
 */
public class DefaultSabmConfigurationService implements SabmConfigurationService
{

	@Resource(name = "sabmConfigurationDao")
	private GenericDao<SabmConfigurationModel> genericDao;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public Double getPartialDealThreshold()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return configuration.get(0).getPartialDealThreshold();
			}
		}
		return null;
	}

	@Override
	public List<String> getDepositApplicableStates()
	{

		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return configuration.get(0).getDepsoitStates();
			}
		}
		return null;

	}

	@Override
	public boolean isLowStockFlagEnforced()
	{
		if (sessionService.getAttribute(SabmCoreConstants.SABMCONFIGURATION_LOWSTOCK_FLAG) == null)
		{
			sessionService.setAttribute(SabmCoreConstants.SABMCONFIGURATION_LOWSTOCK_FLAG, getLowStockFlagFromDB());
		}
		return sessionService.getAttribute(SabmCoreConstants.SABMCONFIGURATION_LOWSTOCK_FLAG);
	}

	private boolean getLowStockFlagFromDB()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return configuration.get(0).getEnforceLowStock();
			}
		}
		return false;
	}


	@Override
	public List<B2BUnitModel> getB2BUnitsForPilotFunction(final String functionName){
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				final Map<PilotFuntionType,List<B2BUnitModel>> map = configuration.get(0).getPilotFunctions();

				return map.get(PilotFuntionType.valueOf(functionName));
			}
		}
		return null;
	}

	@Override
	public List<SabmPilotConditionModel> getPilotConditions(final String functionName){

		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				final Map<PilotFuntionType,List<SabmPilotConditionModel>> map = configuration.get(0).getPilotFeatureToConditions();

				return map.get(PilotFuntionType.valueOf(functionName));
			}
		}
		return null;
	}



	@Override
	public boolean isTrackMyDeliveryEnabledForCustomers()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				final Boolean enableTrackMyDelivery = configuration.get(0).getEnableTrackMyDeliveryForCustomers();
				return enableTrackMyDelivery != null ? enableTrackMyDelivery.booleanValue() : false;
			}
		}
		return false;
	}


	@Override
	public boolean logRetrieverRequest(){
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return configuration.get(0).getLogRetrieverRequest();
			}
		}
		return false;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabm.core.config.SabmConfigurationService#isEnableProductExclusion()
	 */
	@Override
	public boolean isEnableProductExclusion()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return configuration.get(0).getEnableProductExclusion();
			}
		}
		return false;
	}
	
	@Override
	public boolean isEnablePricingBOGOFDeals()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return configuration.get(0).getEnablePricingBOGOFDeals();
			}
		}
		return false;
	}



	@Override
	public boolean isBdeOrderingEnabled()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				final Boolean enableBdeOrdering = configuration.get(0).getEnableBdeOrdering();
				return enableBdeOrdering != null ? enableBdeOrdering.booleanValue() : true;
			}
		}
		return false;
	}

	
	@Override
	public boolean isAutoPayEnabled() {
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null) {
			if (configuration.size() > 1) {
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1) {
				return BooleanUtils.toBooleanDefaultIfNull(configuration.get(0).getEnableAutoPay(), true);
			}
		}
		return false;
	}

	@Override
	public boolean isInvoiceDiscrepancyEnabled()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				final Boolean enableInvoiceDiscrepancy = configuration.get(0).getEnableInvoiceDiscrepancy();
				return enableInvoiceDiscrepancy != null ? enableInvoiceDiscrepancy.booleanValue() : false;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabm.core.config.SabmConfigurationService#getValidSapProductStatus()
	 */
	@Override
	public List<String> getValidSapProductStatus()
	{
		final List<SabmConfigurationModel> configuration = genericDao.find();
		if (configuration != null)
		{
			if (configuration.size() > 1)
			{
				throw new AmbiguousIdentifierException(
						"More than one Configuration found!!! At max 1 configuration per applciation is required");
			}
			if (configuration.size() == 1)
			{
				return CollectionUtils.isNotEmpty(configuration.get(0).getSapProductAvailibilityStatuses())
						? configuration.get(0).getSapProductAvailibilityStatuses() : Collections.emptyList();

			}
		}

		return Collections.emptyList();
	}

	
}

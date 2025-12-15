package com.apb.facades.user.impl;


import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.daos.RegionDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.apb.core.model.KegReturnSizeModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.facades.product.data.AsahiRoleData;
import com.apb.facades.user.ApbUserFacade;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.enums.AsahiRole;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.PlanogramModel;
import com.sabmiller.core.order.dao.DefaultSabmOrderDao;
/**
 * @author C5252631
 *
 *         DefaultApbUserFacade implementation of {@link ApbUserFacadeImpl}
 */
public class ApbUserFacadeImpl extends DefaultUserFacade implements ApbUserFacade
{

	private static final String DEFAULT_SORT_CODE = Config.getString("b2bcommerce.defaultSortCode", "byActiveDscName");


	private static final String ASAHI_CODE = "ASAHI";

	private static final long FOUR_WEEKS = 2419200;

	private static final Logger LOG = LoggerFactory.getLogger(ApbUserFacadeImpl.class);



	@Autowired
	private EnumerationService enumerationService;

	@Autowired
	private RegionDao regionDao;

	private Converter<AddressModel, AddressData> apbB2bAddressConverter;

	@Autowired
	private CMSSiteService cmsSiteService;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private ApbCustomerAccountService apbCustomerAccountService;

	private Converter<KegReturnSizeModel, KegSizeData> kegSizeConverter;

	@Resource(name = "apbCheckoutFacade")
	private APBCheckoutFacade apbCheckoutFacade;
	/** The asahi site util. */
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	/** The order dao. */
	@Resource(name = "orderDao")
	DefaultSabmOrderDao orderDao;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	private Converter<PlanogramModel, PlanogramData> planogramConverter;


	/**
	 * set Asahi Role
	 */
	public List<AsahiRoleData> getAsahiRole()
	{
		final List<AsahiRoleData> asahiRoleDataList = new LinkedList<AsahiRoleData>();
		final List<AsahiRole> roleList = enumerationService.getEnumerationValues(AsahiRole.class);
		for (final AsahiRole asahiRole : roleList)
		{
			final AsahiRoleData asahiRoleData = new AsahiRoleData();
			if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.OWNER.toString()))
			{
				asahiRoleData.setName("Owner");
			}
			else if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.AREAMANAGER.toString()))
			{
				asahiRoleData.setName("Area Manager");
			}
			else if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.VENUEOUTLETMANAGER.toString()))
			{
				asahiRoleData.setName("Venue / Outlet Manager");
			}
			else if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.OTHER.toString()))
			{
				asahiRoleData.setName("Other");
			}
			asahiRoleData.setCode(asahiRole.getCode());
			asahiRoleDataList.add(asahiRoleData);
		}
		return asahiRoleDataList;
	}

	/**
	 * Get States based on country
	 */
	@Override
	public List<RegionData> getStates(final CMSSiteModel cmsSiteModel)
	{
		final List<RegionData> regionList = new ArrayList<>();
		final List<BaseStoreModel> storeList = cmsSiteModel.getStores();
		if (CollectionUtils.isNotEmpty(storeList))
		{
			final Collection<CountryModel> countryCollection = storeList.get(0).getDeliveryCountries();
			countryCollection.forEach(item -> {
				regionDao.findRegionsByCountry(item).stream().filter(region -> region.getBusinessCode().equalsIgnoreCase(ASAHI_CODE))
						.forEach(region -> {
							final RegionData regionData = new RegionData();
							regionData.setCode(region.getIsocode());
							regionData.setName(region.getName());
							regionList.add(regionData);
						});
			});
		}
		return regionList;
	}

	@Override
	public List<AddressData> getB2BUnitAddressesForUser(final UserModel currentUser)
	{
		final List<AddressData> addressDataList = new ArrayList<>();
		String defaultAddressid = null;
		final List<AddressModel> addressModelList = apbCustomerAccountService.getB2BUnitAddressesForUser(currentUser, true);
		if (CollectionUtils.isNotEmpty(addressModelList))
		{
			for (final AddressModel addressModel : addressModelList)
			{
				final AddressData addressData = getApbB2bAddressConverter().convert(addressModel);
				if (addressData.isDefaultAddress())
				{
					defaultAddressid = addressData.getId();
				}
				addressDataList.add(addressData);
			}
		}
		if (asahiSiteUtil.isApb() && null != defaultAddressid)
		{
			final List<AddressData> deliveryAddresses = placeDefaultAddressOnTop(addressDataList, defaultAddressid);
			return deliveryAddresses;
		}

		return addressDataList;
	}

	/**
	 * Place default address on top.
	 *
	 * @param deliveryAddresses
	 *           the delivery addresses
	 * @param defaultAddressId
	 *           the default address id
	 * @return the list
	 */
	private List<AddressData> placeDefaultAddressOnTop(final List<? extends AddressData> deliveryAddresses,
			final String defaultAddressId)
	{
		final List<AddressData> addresses = new ArrayList<>(deliveryAddresses);
		if (null != defaultAddressId && CollectionUtils.isNotEmpty(deliveryAddresses))
		{
			final Optional<AddressData> defaultAddress = addresses.stream()
					.filter(address -> address.getId().equals(defaultAddressId)).findFirst();
			if (defaultAddress.isPresent())
			{
				final AddressData defaultAddressData = defaultAddress.get();
				final int index = deliveryAddresses.indexOf(defaultAddressData);
				if (index > 0)
				{
					addresses.remove(index);
					addresses.add(0, defaultAddressData);
				}
			}

		}
		return addresses;
	}

	@Override
	public List<KegSizeData> getKegSizes(final CMSSiteModel currentSite)
	{
		final List<KegReturnSizeModel> kegReturnSizeModelList = apbCustomerAccountService.getKegSizes(currentSite);
		final List<KegSizeData> kegSizeDataList = new LinkedList<>();
		if (CollectionUtils.isNotEmpty(kegReturnSizeModelList))
		{
			for (final KegReturnSizeModel kegReturnSizeModel : kegReturnSizeModelList)
			{
				final KegSizeData kegSizeData = getKegSizeConverter().convert(kegReturnSizeModel);
				kegSizeDataList.add(kegSizeData);
			}
		}
		return kegSizeDataList;
	}

	@Override
	public UserModel getCurrentUser()
	{
		return getUserService().getCurrentUser();
	}

	@Override
	public List<CCPaymentInfoData> getCCPaymentInfos(final boolean saved)
	{
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		final List<CreditCardPaymentInfoModel> creditCards = getCustomerAccountService().getCreditCardPaymentInfos(currentCustomer,
				saved);
		final List<CCPaymentInfoData> ccPaymentInfos = new ArrayList<CCPaymentInfoData>();
		PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
		if (null == defaultPaymentInfoModel && CollectionUtils.isNotEmpty(creditCards))
		{
			defaultPaymentInfoModel = creditCards.get(0);
		}
		for (final CreditCardPaymentInfoModel ccPaymentInfoModel : creditCards)
		{
			final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
			if (ccPaymentInfoModel.equals(defaultPaymentInfoModel))
			{
				paymentInfoData.setDefaultPaymentInfo(true);
				ccPaymentInfos.add(0, paymentInfoData);
			}
			else
			{
				ccPaymentInfos.add(paymentInfoData);
			}
		}
		return ccPaymentInfos;
	}

	@Override
	public SearchPageData<CustomerData> sortCustomers(final SearchPageData<CustomerData> searchPageData)
	{
		if (null != searchPageData.getPagination() && searchPageData.getPagination().getSort().equalsIgnoreCase(DEFAULT_SORT_CODE))
		{
			searchPageData.getResults().sort(
					Comparator.comparing(CustomerData::isActive).reversed().thenComparing(CustomerData::getName));
		}
		return searchPageData;
	}

	/**
	 * @return the kegSizeConverter
	 */
	public Converter<KegReturnSizeModel, KegSizeData> getKegSizeConverter()
	{
		return kegSizeConverter;
	}

	/**
	 * @param kegSizeConverter
	 *           the kegSizeConverter to set
	 */
	public void setKegSizeConverter(final Converter<KegReturnSizeModel, KegSizeData> kegSizeConverter)
	{
		this.kegSizeConverter = kegSizeConverter;
	}

	/**
	 * @return the apbB2bAddressConverter
	 */
	public Converter<AddressModel, AddressData> getApbB2bAddressConverter()
	{
		return apbB2bAddressConverter;
	}

	/**
	 * @param apbB2bAddressConverter
	 *           the apbB2bAddressConverter to set
	 */
	public void setApbB2bAddressConverter(final Converter<AddressModel, AddressData> apbB2bAddressConverter)
	{
		this.apbB2bAddressConverter = apbB2bAddressConverter;
	}

	@Override
	public boolean removeCustomerFromB2bUnit(final String userId,  final AsahiB2BUnitModel currentUnit)
	{

		boolean isSuccess = false;
		try
		{
			Assert.notNull(userId, "must not be null");
			Assert.notNull(currentUnit, "must not be null");
			final UserModel user = getUserService().getUserForUID(userId);
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			isSuccess = apbCustomerAccountService.removeCustomerFromUnit(customer, currentUnit);
			if (isSuccess)
			{
				apbCustomerAccountService.sendCustomerProfileUpdatedNoticeEmail(customer, currentUnit);
			}
		}
		catch (final Exception e)
		{
			LOG.error("can not remove this customer: " + userId + " from unit '" + currentUnit + "'", e);
		}
		return isSuccess;
	}

	@Override
	public boolean isUserEligibleToReceiveWelcomeEmail(final String userId, final String siteUid)
	{
		try
		{
			final UserModel user = getUserService().getUserForUID(userId);
			final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
			final int orderCount = orderDao.fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(user, siteUid, currentUnit);
			if (orderCount < 1)
			{
				return true;
			}
			else
			{
				if (null == user.getLastLogin())
				{
					return true;
				}
				final long timePassedSinceLastUserLogin = Calendar.getInstance().getTime().getTime() - user.getLastLogin().getTime();
				final long timeInSeconds = TimeUnit.SECONDS.convert(timePassedSinceLastUserLogin, TimeUnit.MILLISECONDS);
				if (timeInSeconds > FOUR_WEEKS)
				{
					return true;
				}
			}
			return false;
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.warn("Cannot find user with uid [{}]'", userId);
		}
		return false;
	}

	@Override
	public boolean sendWelcomeEmail(final String userId)
	{
		try
		{
			final UserModel user = getUserService().getUserForUID(userId);
			return apbCustomerAccountService.sendWelcomeEmail(user);
		}

		catch (final UnknownIdentifierException ex)
		{
			LOG.error("Cannot find user with uid [{}]", userId);
			return false;
		}
		catch (final Exception ex)
		{
			LOG.error("Could not send WelcomeEmail to [{}]", userId);
			LOG.error("Exception occured sending welcomeemail" + ex.getMessage());
			return false;
		}

	}

	/**
	 * Gets the planograms associated with Customer Account.
	 *
	 * @return the planograms for B2Bunit
	 */
	@Override
	public List<PlanogramData> getPlanogramsForB2BUnit()
	{
		final List<PlanogramModel> planograms = apbB2BUnitService.getCurrentB2BUnit().getPlanograms();
		if (CollectionUtils.isNotEmpty(planograms))
		{
			return Converters.convertAll(planograms, getPlanogramConverter());
		}
		else
		{
			return Collections.emptyList();
		}
	}

	/**
	 * Gets the default planogram added at CL level.
	 *
	 * @return the default planogram
	 */
	@Override
	public List<PlanogramData> getDefaultPlanogram()
	{
		final List<String> catalogHierarchy = new ArrayList<String>(apbB2BUnitService.getCurrentB2BUnit().getCatalogHierarchy());
		final List<PlanogramModel> defaultPlanograms = apbCustomerAccountService.getDefaultPlanograms(catalogHierarchy);
		if (CollectionUtils.isNotEmpty(defaultPlanograms))
		{
			return Converters.convertAll(defaultPlanograms, getPlanogramConverter());
		}
		else
		{
			return Collections.emptyList();
		}
	}


	@Override
	public boolean savePlanogram(final PlanogramData data)
	{
		try
		{
			final boolean success = apbCustomerAccountService.savePlanogram(data);
			return success;
		}
		catch (final Exception ex)
		{
			LOG.error("Could not save planogram for the customer" + ex.getStackTrace());
			return Boolean.FALSE;
		}
	}


	/**
	 * @return the planogramConverter
	 */
	public Converter<PlanogramModel, PlanogramData> getPlanogramConverter()
	{
		return planogramConverter;
	}

	/**
	 * @param planogramConverter
	 *           the planogramConverter to set
	 */
	public void setPlanogramConverter(final Converter<PlanogramModel, PlanogramData> planogramConverter)
	{
		this.planogramConverter = planogramConverter;
	}

	@Override
	public void removePlanogram(final String code)
	{
		apbCustomerAccountService.removePlanogram(code);

	}

	@Override
	public void removeAllPlanograms()
	{
		apbCustomerAccountService.removeAllPlanogramsForCurrentB2BUnit();

	}


}

package com.apb.service.b2bunit.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.event.AsahiCustomerNotifyEvent;
import com.apb.core.model.AccountGroupsModel;
import com.apb.core.model.AccountTypeModel;
import com.apb.core.model.BannerGroupsModel;
import com.apb.core.model.ChannelModel;
import com.apb.core.model.LicenceClassModel;
import com.apb.core.model.LicenseTypesModel;
import com.apb.core.model.SubChannelModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.dao.b2bunit.ApbB2BUnitDao;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.notification.service.NotificationService;


/**
 * The Interface ApbB2BUnitServiceImpl.
 *
 * Kuldeep.Singh1
 */
public class ApbB2BUnitServiceImpl extends DefaultB2BUnitService implements ApbB2BUnitService
{

	/** The apb B 2 B unit dao. */
	@Resource(name = "apbB2BUnitDao")
	private ApbB2BUnitDao apbB2BUnitDao;

	@Resource
	UserService userService;

	/** The type service. */
	@Resource
	private TypeService typeService;

	/** The event service. */
	@Resource(name = "eventService")
	private EventService eventService;

	/** The common I 18 N service. */
	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The base store service. */
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	/** The base site service. */
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	/**
	 * Gets the apb B 2 B unit by abn.
	 *
	 * @param abnNumber
	 *           the abn number
	 * @return the apb B 2 B unit by abn
	 */
	@Override
	public AsahiB2BUnitModel getApbB2BUnitByAbn(final String abnNumber)
	{
		return this.apbB2BUnitDao.getApbB2BUnitByAbn(abnNumber);
	}

	/**
	 * Gets the license types for code.
	 *
	 * @param code
	 *           the code
	 * @return the license types for code
	 */
	@Override
	public LicenseTypesModel getLicenseTypesForCode(final String code)
	{
		return this.apbB2BUnitDao.getLicenseTypesForCode(code);
	}

	/**
	 * Gets the sub channel for code.
	 *
	 * @param code
	 *           the code
	 * @return the sub channel for code
	 */
	@Override
	public SubChannelModel getSubChannelForCode(final String code)
	{
		return this.apbB2BUnitDao.getSubChannelForCode(code);
	}

	/**
	 * Gets the licence class for code.
	 *
	 * @param code
	 *           the code
	 * @return the licence class for code
	 */
	@Override
	public LicenceClassModel getLicenceClassForCode(final String code)
	{
		return this.apbB2BUnitDao.getLicenceClassForCode(code);
	}

	/**
	 * Gets the channel for code.
	 *
	 * @param code
	 *           the code
	 * @return the channel for code
	 */
	@Override
	public ChannelModel getChannelForCode(final String code)
	{
		return this.apbB2BUnitDao.getChannelForCode(code);
	}

	/**
	 * Gets the banner groups for code.
	 *
	 * @param code
	 *           the codethe license types for code
	 * @return the banner groups for code
	 */
	@Override
	public BannerGroupsModel getBannerGroupsForCode(final String code)
	{
		return this.apbB2BUnitDao.getBannerGroupsForCode(code);
	}

	/**
	 * Gets the account type for code.
	 *
	 * @param code
	 *           the code
	 * @return the license types for cod
	 */
	@Override
	public AccountTypeModel getAccountTypeForCode(final String code)
	{
		return this.apbB2BUnitDao.getAccountTypeForCode(code);
	}

	/**
	 * Gets the account groups for code.
	 *
	 * @param code
	 *           the code
	 * @return the account groups for code
	 */
	@Override
	public AccountGroupsModel getAccountGroupsForCode(final String code)
	{
		return this.apbB2BUnitDao.getAccountGroupsForCode(code);
	}

	/**
	 * Gets the apb B 2 B unit.
	 *
	 * @param abpAccountNo
	 *           the abp account no
	 * @param abnNumber
	 *           the abn number
	 * @return the apb B 2 B unit
	 */
	@Override
	public AsahiB2BUnitModel getApbB2BUnit(final String abpAccountNo, final String abnNumber)
	{
		return this.apbB2BUnitDao.getApbB2BUnit(abpAccountNo, abnNumber);

	}

	/**
	 * Find liquor license.
	 *
	 * @param liquorLicense
	 *           the liquor license
	 * @return the asahi B 2 B unit model
	 */
	@Override
	public AsahiB2BUnitModel findLiquorLicense(final String liquorLicense)
	{
		return this.apbB2BUnitDao.findLiquorLicense(liquorLicense);
	}

	/**
	 * Gets the warehouse for code.
	 *
	 * @param code
	 *           the code
	 * @return the warehouse for code
	 */
	@Override
	public WarehouseModel getwarehouseForCode(final String code)
	{
		return this.apbB2BUnitDao.getwarehouseForCode(code);
	}

	/**
	 * Gets the address for address record ID.
	 *
	 * @param id
	 *           the id
	 * @return the address for address record ID
	 */
	@Override
	public AddressModel getAddressForAddressRecordID(final String id, final B2BUnitModel b2bUnit)
	{
		return this.apbB2BUnitDao.getAddressForAddressRecordID(id, b2bUnit);
	}

	/**
	 * Gets the b 2 B unit by backend ID.
	 *
	 * @param customerRecId
	 *           the customer rec id
	 * @return the b 2 B unit by backend ID
	 */
	@Override
	public B2BUnitModel getB2BUnitByBackendID(final String customerRecId)
	{
		return this.apbB2BUnitDao.getB2BUnitByBackendID(customerRecId);
	}

	/**
	 * Gets the b 2 B unit by account number.
	 *
	 * @param accNum
	 *           the account number
	 * @return the b 2 B unit by account number
	 */
	@Override
	public AsahiB2BUnitModel getB2BUnitByAccountNumber(final String accountNumber)
	{
		return this.apbB2BUnitDao.getB2BUnitByAccountNumber(accountNumber);
	}

	@Override
	public String getAccNumForCurrentB2BUnit()
	{
		final AsahiB2BUnitModel asahiB2BUnitModel = getCurrentB2BUnit();
		return null != asahiB2BUnitModel && null != asahiB2BUnitModel.getAccountNum() ? asahiB2BUnitModel.getAccountNum() : null;
	}

	@Override
	public AsahiB2BUnitModel getCurrentB2BUnit()
	{

		final UserModel model = getUserService().getCurrentUser();
		boolean unitDisabled = false;
		if (null != model && model instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCust = (B2BCustomerModel) model;
			if (b2bCust.getDefaultB2BUnit() instanceof AsahiB2BUnitModel) {
				final AsahiB2BUnitModel unit = (AsahiB2BUnitModel) b2bCust.getDefaultB2BUnit();
				final Collection<String> disabledUser = unit.getDisabledUser();
				unitDisabled = CollectionUtils.isNotEmpty(disabledUser) ? disabledUser.contains(b2bCust.getUid()) : false;
				return unitDisabled ? null : unit;
			}
		}
		return null;
	}

	/**
	 * Gets the address by record id.
	 *
	 * @param addressId
	 *           the address id
	 * @return the address by record id
	 */
	@Override
	public AddressModel getAddressByRecordId(final String addressId)
	{
		return this.apbB2BUnitDao.getAddressByRecordId(addressId);
	}

	/*
	 * (non-Javadoc) This method is used to generate Asahi NotificationEmail
	 *
	 * @see com.apb.core.email.ApbEmailGenerationService#generateAsahiNotifyEmail(com.sabmiller.core.model.AsahiB2BUnitModel)
	 */
	@Override
	public void generateAsahiNotifyProcess(final AsahiB2BUnitModel asahiB2BUnitModel, final AsahiB2BUnitData b2bUnitData)
	{
		NotificationType notificationType = null;
		final String notifyType = b2bUnitData.getNotifyType();

		//Check if public holiday notifications are opted-in by the current user
		if (notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.NO_DELIVERY, "NODEL"))){
			notificationType = NotificationType.PUBLIC_HOLIDAY_NO_DELIVERY;
		}
		if (notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.ALT_CALLDAY_DELIVERY, "ALTCALL"))){
			notificationType = NotificationType.PUBLIC_HOLIDAY_ALT_CALL_DELIVERY;
		}
		if (notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.ALT_DELDATE_DELIVERY, "ALTDEL"))){
			notificationType = NotificationType.PUBLIC_HOLIDAY_ALT_DELIVERY;
		}

		if (StringUtils.isNotEmpty(notifyType) && null != b2bUnitData)
		{
			final Set<PrincipalModel> memberList = asahiB2BUnitModel.getMembers();
			for (final PrincipalModel member : memberList)
			{

				if (member instanceof B2BCustomerModel && BooleanUtils.isTrue(((B2BCustomerModel) member).getActive())
						&& !(CollectionUtils.isNotEmpty(asahiB2BUnitModel.getDisabledUser())
								&& asahiB2BUnitModel.getDisabledUser().contains(member.getUid()))
						&& (((B2BCustomerModel) member).getDisableEmailNotification() == null
								|| !((B2BCustomerModel) member).getDisableEmailNotification()))
				{
					if (notificationService.getEmailPreferenceForNotificationType(notificationType,(B2BCustomerModel) member, asahiB2BUnitModel)) {
						eventService.publishEvent(initializeEvent(new AsahiCustomerNotifyEvent(), b2bUnitData, member));
					}
				}
			}
		}
	}

	protected AbstractEvent initializeEvent(final AsahiCustomerNotifyEvent event, final AsahiB2BUnitData b2bUnitData,
			final PrincipalModel member)
	{
		final String notifyType = b2bUnitData.getNotifyType();
		event.setBaseStore(baseStoreService.getCurrentBaseStore());
		event.setSite(baseSiteService.getCurrentBaseSite());
		event.setLanguage(commonI18NService.getCurrentLanguage());
		event.setCurrency(commonI18NService.getCurrentCurrency());
		event.setNotifyType(b2bUnitData.getNotifyType());
		event.setHoliday(b2bUnitData.getHolidayText());
		event.setCutOffDate(b2bUnitData.getCutoffDate());
		if (StringUtils.isNotEmpty(notifyType)
				&& notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.NO_DELIVERY, "NODEL")))
		{
			event.setDeliveryDate(b2bUnitData.getHolidayDate());
		}
		else
		{
			event.setDeliveryDate(b2bUnitData.getNewDeliveryDate());
		}
		event.setCustomer((B2BCustomerModel) member);
		return event;
	}

	@Override
	public Map<String, List<AsahiB2BUnitModel>> getUserActiveB2BUnits(final String userId)
	{
		final UserModel customer = getUserService().getUser(userId);
		final List<AsahiB2BUnitModel> allActiveUnits = new ArrayList<>();
		List<AsahiB2BUnitModel> apbB2bUnits = new ArrayList<>();
		final Map<String, List<AsahiB2BUnitModel>> siteUnitsMap = new HashMap<>();

		if (customer instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
			b2bCustomer.getAllgroups().forEach(
					group -> {
						if (group instanceof AsahiB2BUnitModel)
						{
							final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) group;
							if (CollectionUtils.isEmpty(b2bUnit.getDisabledUser())
									|| (!b2bUnit.getDisabledUser().contains(customer.getUid())))
							{
								allActiveUnits.add(b2bUnit);
							}
						}
					});
		}
		apbB2bUnits = allActiveUnits
				.stream()
				.filter(
						b2bUnit -> StringUtils.isNotBlank(b2bUnit.getCompanyCode()) && b2bUnit.getCompanyCode().equalsIgnoreCase("apb"))
				.collect(Collectors.toList());
		allActiveUnits.removeAll(apbB2bUnits);
		siteUnitsMap.put("apb", apbB2bUnits);
		siteUnitsMap.put("sga", allActiveUnits);
		return siteUnitsMap;
	}

	@Override
	public AsahiSAMAccessModel createSamAccess(final String samAccess, final B2BCustomerModel customer, final String defaultUnitId)
	{
		final AsahiSAMAccessModel accessModel = this.getModelService().create(AsahiSAMAccessModel.class);
		accessModel.setB2bCustomer(customer);
		final AsahiB2BUnitModel defaultB2BUnit = apbB2BUnitDao.getB2BUnitByUID(defaultUnitId);
		accessModel.setPayer(defaultB2BUnit.getPayerAccount());
		accessModel.setParentAccount(defaultB2BUnit);
		if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS))
		{
			accessModel.setOrderAccess(Boolean.FALSE);
			accessModel.setPayAccess(Boolean.TRUE);
			accessModel.setPendingApproval(Boolean.TRUE);
			accessModel.setRequestDate(new Date());
			getModelService().save(accessModel);
			return accessModel;
		}
		else if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS))
		{
			accessModel.setOrderAccess(Boolean.TRUE);
			accessModel.setPayAccess(Boolean.TRUE);
			accessModel.setPendingApproval(Boolean.TRUE);
			accessModel.setRequestDate(new Date());
			getModelService().save(accessModel);
			return accessModel;
		}
		return null;
	}

	@Override
	public AsahiSAMAccessModel updateUserSamAccess(final B2BCustomerModel customer, final String defaultUnitId,
			final String samAccess)
	{
		final AsahiB2BUnitModel defaultB2BUnit = apbB2BUnitDao.getB2BUnitByUID(defaultUnitId);
		AsahiSAMAccessModel accessModel = apbB2BUnitDao.getAccessModel(customer, defaultB2BUnit.getPayerAccount());
		Boolean isPayAccessRequired = Boolean.FALSE;
		//Case 1 : If Access model does not exists...The user has created will Order Access and requested for Pay Access,email will Trigger.
		//Case 2 : If Access Model exists... and user does not have Pay access..email will trigger.
		//Case 3 : If customer already has Pay Access OR Awaiting For Pay Access...Email will not triggered again..
		if ((samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS) || samAccess
				.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS))
				&& ((null == accessModel) || (null != accessModel && (accessModel.isApprovalDenied() || !accessModel.isPayAccess()))))
		{
			isPayAccessRequired = Boolean.TRUE;
		}

		accessModel = updateAccessAttribute(customer, accessModel, samAccess, defaultB2BUnit);
		if (isPayAccessRequired)
		{
			customerAccountService.updateAndNotifyPayAccess(customer, accessModel, ApbCoreConstants.PAYER_ACCESS_SUPERUSER_REQUEST);
		}
		return accessModel;
	}

	@Override
	public AsahiSAMAccessModel getSamAccessModel(final String customerUid, final String payerUnit)
	{
		final UserModel userModel = getUserService().getUserForUID(customerUid);
		if (userModel instanceof B2BCustomerModel)
		{
			final AsahiB2BUnitModel payerUnitModel = apbB2BUnitDao.getB2BUnitByUID(payerUnit);
			final B2BCustomerModel customer = (B2BCustomerModel) userModel;
			return apbB2BUnitDao.getAccessModel(customer, payerUnitModel);
		}
		return null;
	}

	/**
	 * The method will update the SAM access if the access requested by the user from the homepage event.
	 *
	 * @param customer
	 * @param accessModel
	 * @param samAccess
	 * @param defaultB2BUnit
	 * @return AsahiSAMAccessModel
	 */
	@Override
	public AsahiSAMAccessModel updateSamAccessByUser(final B2BCustomerModel customer, final AsahiB2BUnitModel defaultB2BUnit,
			final AsahiSAMAccessModel accessModel, final String samAccess)
	{
		if (samAccess.equalsIgnoreCase(ApbCoreConstants.UPDATE_ORDER_ACCESS))
		{
			accessModel.setOrderAccess(Boolean.TRUE);
			getModelService().save(accessModel);
			return accessModel;
		}

		return updateAccessAttribute(customer, accessModel, samAccess, defaultB2BUnit);
	}

	/**
	 * The method will update the SAM access attribute based on the required access
	 *
	 * @param customer
	 * @param accessModel
	 * @param samAccess
	 * @param defaultB2BUnit
	 * @return AsahiSAMAccessModel
	 */
	private AsahiSAMAccessModel updateAccessAttribute(final B2BCustomerModel customer, AsahiSAMAccessModel accessModel,
			final String samAccess, final AsahiB2BUnitModel defaultB2BUnit)
	{
		Boolean approvalRequired = Boolean.TRUE;
		if (null != accessModel)
		{
			/*
			 * If user previously had pay access, then approval not required
			 */
			if (accessModel.isPayAccess() && !accessModel.isPendingApproval())
			{
				approvalRequired = Boolean.FALSE;
			}

			if (null != defaultB2BUnit.getPayerAccount())
			{
				accessModel.setPayer(defaultB2BUnit.getPayerAccount());
			}
			if (samAccess.equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS))
			{
				accessModel.setOrderAccess(Boolean.TRUE);
				accessModel.setPayAccess(Boolean.FALSE);
				accessModel.setPendingApproval(Boolean.FALSE);
				accessModel.setApprovalDenied(Boolean.FALSE);
			}
			else if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS))
			{
				accessModel.setOrderAccess(Boolean.FALSE);
				accessModel.setPayAccess(Boolean.TRUE);
				accessModel.setPendingApproval(approvalRequired);
				accessModel.setApprovalDenied(Boolean.FALSE);
			}
			else if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS))
			{
				accessModel.setOrderAccess(Boolean.TRUE);
				accessModel.setPayAccess(Boolean.TRUE);
				accessModel.setPendingApproval(approvalRequired);
				accessModel.setApprovalDenied(Boolean.FALSE);
			}
			getModelService().save(accessModel);
		}
		else
		{
			accessModel = this.createSamAccess(samAccess, customer, defaultB2BUnit.getPayerAccount().getUid());
		}
		return accessModel;
	}


	@Override
	public String getSamAccessTypeForCustomer(final B2BCustomerModel customer) {
		String accessType = null;
			final Collection<AsahiSAMAccessModel> samAccessList = customer.getSamAccess();

			if (CollectionUtils.isNotEmpty(samAccessList))
			{
				final B2BUnitModel payer =((AsahiB2BUnitModel)customer.getDefaultB2BUnit()).getPayerAccount();

				for (final AsahiSAMAccessModel accessModel : samAccessList)
				{
					if(accessModel.getPayer()!=null && accessModel.getPayer().equals(payer))
					{
						if (accessModel.isPayAccess() && accessModel.isOrderAccess()) {
							accessType = ApbCoreConstants.PAY_AND_ORDER_ACCESS;
						} else if (accessModel.isPayAccess()) {
							accessType = ApbCoreConstants.PAY_ACCESS;
						}
						else if(accessModel.isOrderAccess())
						{
							accessType = ApbCoreConstants.ORDER_ACCESS;
						}
						break;
					}

				}

			}

			return accessType;
	}
}

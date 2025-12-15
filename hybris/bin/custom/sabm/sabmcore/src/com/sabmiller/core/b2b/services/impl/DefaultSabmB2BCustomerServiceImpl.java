/**
 *
 */
package com.sabmiller.core.b2b.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.apb.core.util.AsahiSiteUtil;
import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.dao.SabmB2BCustomerDao;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerImportedModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.SABMCMSUserGroupRestrictionModel;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.model.SabmMessageModel;
import com.sabmiller.core.model.SabmUserMessagesStatusModel;
import com.sabmiller.facades.user.NotificationData;


/**
 * DefaultSabmB2BCustomerServiceImpl
 */
public class DefaultSabmB2BCustomerServiceImpl implements SabmB2BCustomerService
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BCustomerServiceImpl.class);

	@Resource(name = "sabmB2BCustomerDao")
	private SabmB2BCustomerDao sabmB2BCustomerDao;

	@Resource(name = "sabmNotificationDao")
	private GenericDao<SABMNotificationModel> sabmNotificationDao;


	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "sabmMessageUserGroupRestrictionEvaluator")
	private CMSRestrictionEvaluator<SABMCMSUserGroupRestrictionModel> sabmMessageUserGroupRestrictionEvaluator;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	List<String> cubUserGroups = Arrays.asList("b2bordercustomer", "b2binvoicecustomer", "b2bassistantgroup");


	private static final String B2BORDERCUSTOMER = "b2bordercustomer";
	private static final String B2BADMINGROUP = "b2badmingroup";

	private static final String DELETED_CUSTOMER_GROUP = "deletedcustomergroup";

	private static final String B2BASSISTANTGROUP = "b2bassistantgroup";


	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public List<String> getOtherCustomerByUnitAndGroups(final B2BUnitModel b2bUnit, final String customerUid)
	{
		final List<String> unit = new ArrayList<String>();
		unit.add(b2bUnit.getUid());
		final List<String> groups = new ArrayList<String>();
		groups.add(B2BADMINGROUP);
		groups.add(B2BORDERCUSTOMER);
		final List<String> otherCustomerUidList = new ArrayList<String>();
		//Get the Customer by unit and groups
		final List<B2BCustomerModel> b2BCustomerModelList = sabmB2BCustomerDao.getCustomerByUnits(unit, groups);
		if (CollectionUtils.isNotEmpty(b2BCustomerModelList))
		{
			for (final B2BCustomerModel model : b2BCustomerModelList)
			{
				//To get rid of the current customerUid
				if (customerUid.equals(model.getUid()))
				{
					continue;
				}
				if ((CollectionUtils.isEmpty(b2bUnit.getCubDisabledUsers())
						|| !b2bUnit.getCubDisabledUsers().contains(model.getUid())) && BooleanUtils.isTrue(model.getActive()))
				{
					otherCustomerUidList.add(model.getUid());
				}

			}
		}
		return otherCustomerUidList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BCustomerService#getUsersByGroups(de.hybris.platform.b2b.model.
	 * B2BCustomerModel)
	 */
	@Override
	public List<B2BCustomerModel> getUsersByGroups(final B2BCustomerModel b2bCustomerModel)
	{
		final List<String> unitList = new ArrayList<String>();
		//find the unit from B2BCustomerModel's group
		for (final PrincipalGroupModel model : b2bCustomerModel.getGroups())
		{
//			if (model instanceof AsahiB2BUnitModel)
//			{
//				continue;
//			}

			if (model instanceof B2BUnitModel)
			{
				unitList.add(((B2BUnitModel) model).getUid());

			}
		}
		final List<String> groupList = new ArrayList<String>();
		groupList.add(B2BADMINGROUP);
		groupList.add(B2BASSISTANTGROUP);
		//Get the Customer by unit and groups
		final List<B2BCustomerModel> b2BCustomerModelList = sabmB2BCustomerDao.getCustomerByUnits(unitList, groupList);

		return CollectionUtils.isNotEmpty(b2BCustomerModelList) ? b2BCustomerModelList : Collections.<B2BCustomerModel> emptyList();
	}

	/**
	 * Get the Customer by uids
	 *
	 * @param orUids
	 *           the b2b unit ids
	 * @param andUids
	 *           the b2b unit ids
	 * @return List<B2BCustomerModel> the Customers
	 */
	@Override
	public List<B2BCustomerModel> getCustomerForUpdateProfile(final List<String> orUids, final List<String> andUids)
	{
		return sabmB2BCustomerDao.getCustomerByUnits(getZADPUnitIdFromUids(orUids), andUids);

	}

	/**
	 * check is the unit belong ZADP
	 *
	 * @param uids
	 *           the b2b unit in the customer
	 * @return List<String> the unit contain the ZADP
	 */
	protected List<String> getZADPUnitIdFromUids(final List<String> uids)
	{
		final Set<String> newUids = new HashSet<>();
		newUids.addAll(uids);
		uids.clear();
		uids.addAll(newUids);
		for (final String uid : uids)
		{
			final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(uid);
			if (b2bUnitModel != null && isBranch(b2bUnitModel) && StringUtils.isNotEmpty(b2bUnitModel.getPayerId()))
			{
				final B2BUnitModel topLevelB2BUnit = b2bUnitService.findTopLevelB2BUnit(b2bUnitModel.getPayerId());
				if (topLevelB2BUnit != null)
				{
					newUids.add(topLevelB2BUnit.getUid());
				}
			}
		}
		return new ArrayList<String>(newUids);
	}

	/**
	 * check is the unit belong ZADP
	 *
	 * @param b2bUnit
	 *           the b2b unit model
	 * @return boolean
	 */
	protected boolean isTopLevelB2BUnit(final B2BUnitModel b2bUnit)
	{
		return b2bUnit != null && SabmCoreConstants.ZADP.equals(b2bUnit.getAccountGroup());
	}

	/**
	 * check is the unit belong ZALB
	 *
	 * @param b2bUnit
	 *           the b2b unit model
	 * @return boolean
	 */
	protected boolean isBranch(final B2BUnitModel b2bUnit)
	{
		return b2bUnit != null && SabmCoreConstants.ZALB.equals(b2bUnit.getAccountGroup());
	}


	/*
	 *
	 * delete the user**
	 *
	 * @param customer the user need to be change uid**
	 *
	 * @param newUid the new newUid
	 *
	 * @return CustomerModel the changed uid user
	 */

	@Override
	public B2BCustomerModel deleteCustomer(final B2BCustomerModel customer)
	{
		// the group for the deleted customer
		final B2BUnitModel deletedCustomerGroup = b2bUnitService.getUnitForUid(DELETED_CUSTOMER_GROUP);
		if (deletedCustomerGroup == null)
		{
			return null;
		}

		//final B2BCustomerModel newCustomer = changeCustomerUid(customer, newUid);

		updateDisabledUsersList(customer);
		/*
		 * final Set<PrincipalGroupModel> newgroups = Sets.newHashSet(customer.getGroups());
		 * newgroups.add(deletedCustomerGroup); customer.setDefaultB2BUnit(deletedCustomerGroup);
		 * customer.setGroups(newgroups);
		 */
		//customer.setLoginDisabled(Boolean.TRUE);
		//customer.setActive(Boolean.FALSE);
		customer.setOnboardWithWelcomeEmail(Boolean.FALSE);
		customer.setWelcomeEmailStatus(Boolean.FALSE);
		//customer.setEncodedPassword(null);
		customer.setOrderLimit(null);
		//customer.setDefaultShipmentAddress(null);
		//customer.setDefaultPaymentAddress(null);
		//customer.setAddresses(Collections.emptyList());
		//customer.setCreatedBy(null);
		//customer.setName(null);
		/*
		 * customer.setFirstName(null); customer.setLastName(null);
		 */
		if (customer.getFirstName() == null || customer.getLastName() == null)
		{
			customer.setFirstName("Deleted");
			customer.setLastName("User");
		}

		customer.setToken(null);

		modelService.save(customer);
		modelService.refresh(customer);

		final List<SABMNotificationModel> sabmNotificationList = sabmNotificationDao
				.find(Collections.singletonMap("user", customer));

		if(sabmNotificationList != null && sabmNotificationList.size()>0) {
			final List<SABMNotificationPrefModel> sabmNotificationPrefModels = new ArrayList<SABMNotificationPrefModel>();
			for(final SABMNotificationModel notification : sabmNotificationList) {
				final List<SABMNotificationPrefModel> tempNotificationPrefs = notification.getNotificationPreferences();
				for (final SABMNotificationPrefModel model : ListUtils.emptyIfNull(tempNotificationPrefs)) {
					sabmNotificationPrefModels.add(model);
				}
			}
			modelService.removeAll(sabmNotificationPrefModels);
			modelService.removeAll(sabmNotificationList);
			LOG.info("Removing SABMNotification for Deleted Users");
		}

		return customer;
	}

	/**
	 * @param customer
	 */
	private void updateDisabledUsersList(final B2BCustomerModel customer)
	{
		final Set<PrincipalGroupModel> principalGroups = Sets.newHashSet(customer.getGroups());
		final List<PrincipalGroupModel> cubB2bUnitModels = new ArrayList<PrincipalGroupModel>();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if(principalGroup instanceof UserGroupModel && cubUserGroups.contains(principalGroup.getUid())) {
				cubB2bUnitModels.add(principalGroup);
			}
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
				cubB2bUnitModels.add(b2bUnit);
				if (!CollectionUtils.isEmpty(b2bUnit.getCubDisabledUsers())
						&& (b2bUnit.getCubDisabledUsers().contains(customer.getUid())))
				{
					final List<String> modifiedList = new ArrayList<>(b2bUnit.getCubDisabledUsers());
					modifiedList.remove(customer.getUid());
					b2bUnit.setCubDisabledUsers(modifiedList);
					modelService.save(b2bUnit);
					modelService.refresh(b2bUnit);
					customer.setModifiedtime(new Date());
					modelService.save(customer);
					modelService.refresh(customer);
				}
			}
		}
		principalGroups.removeAll(cubB2bUnitModels);
		final B2BUnitModel deletedCustomerGroup = b2bUnitService.getUnitForUid(DELETED_CUSTOMER_GROUP);
		principalGroups.add(deletedCustomerGroup);
		customer.setDefaultB2BUnit(deletedCustomerGroup);
		customer.setGroups(principalGroups);

		modelService.save(customer);
		modelService.refresh(customer);
	}

	/**
	 * change the user's uid
	 *
	 * @param customer
	 *           the user need to be change uid
	 *
	 * @param newUid
	 *           the new uid
	 *
	 * @return CustomerModel the changed uid user
	 */
	@Override
	public B2BCustomerModel changeCustomerUid(final B2BCustomerModel customer, final String newUid)
	{
		Assert.hasText(newUid, "The field [newEmail] cannot be empty");
		Assert.notNull(customer, "The field [customer] cannot be empty");

		customer.setOriginalUid(newUid);
		customer.setUid(newUid);
		modelService.save(customer);
		modelService.refresh(customer);
		return customer;
	}

	/**
	 * get the user with similar uid
	 *
	 * @param uid
	 *           the uid
	 *
	 * @return List<B2BCustomerModel>
	 */
	@Override
	public List<B2BCustomerModel> getSimilarB2BCustomer(final String uid)
	{
		return sabmB2BCustomerDao.getSimilarB2BCustomer(uid);
	}


	@Override
	public List<B2BCustomerModel> searchB2BCustomerByEmail(final String email)
	{
		return sabmB2BCustomerDao.searchB2BCustomerByEmail(email);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BCustomerService#getBDECustomer(java.lang.String)
	 */
	@Override
	public BDECustomerModel getBDECustomer(final String uid)
	{
		return sabmB2BCustomerDao.getBDECustomer(uid);
	}

	@Override
	public BDECustomerImportedModel getBDECustomerImported(final String uid)
	{
		return sabmB2BCustomerDao.getBDECustomerImported(uid);
	}


	@Override
	public List<BDECustomerImportedModel> getBDECustomerImportedAll(){
		return sabmB2BCustomerDao.getBDECustomerImportedAll();
	}


	@Override
	public List<B2BCustomerModel> searchB2BDeletedCustomerByEmail(final String email)
	{
		return sabmB2BCustomerDao.searchB2BDeletedCustomerByEmail(email);
	}

	@Override
	public List<B2BCustomerModel> searchCustomerByEmail(final String email)
	{
		final List<B2BCustomerModel> customers = new ArrayList<B2BCustomerModel>();
		final List<B2BCustomerModel> searchB2BCustomerByEmail = sabmB2BCustomerDao.searchB2BCustomerByEmail(email);
		final List<B2BCustomerModel> searchB2BDeletedCustomerByEmail = sabmB2BCustomerDao.searchB2BDeletedCustomerByEmail(email);
		customers.addAll(searchB2BCustomerByEmail);
		customers.removeAll(searchB2BDeletedCustomerByEmail);
		return customers;
	}

	@Override
	public List<B2BCustomerModel> searchCustomerByExactEmail(final String email)
	{
		final List<B2BCustomerModel> customers = new ArrayList<B2BCustomerModel>();
		final List<B2BCustomerModel> searchB2BCustomerByEmail = sabmB2BCustomerDao.searchB2BCustomerByEmail(email);
		final List<B2BCustomerModel> searchB2BDeletedCustomerByEmail = sabmB2BCustomerDao.searchB2BDeletedCustomerByEmail(email);
		customers.addAll(searchB2BCustomerByEmail);
		customers.removeAll(searchB2BDeletedCustomerByEmail);
		return customers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BCustomerService#getUnreadSiteNotification()
	 */
	@Override
	public List<NotificationData> getUnreadSiteNotification(final UserModel user)
	{
		B2BCustomerModel b2bCustomer = null;
		if (user instanceof B2BCustomerModel)
		{
			b2bCustomer = (B2BCustomerModel) user;
		}

		final B2BUnitModel currentUserB2BUnit = b2bUnitService.getParent(b2bCustomer);

		final List<SabmMessageModel> messages = sabmB2BCustomerDao.getUserUnreadNotifications(currentUserB2BUnit, user.getUid());
		final List<NotificationData> notificationDataList = new ArrayList<>();
		if (messages != null)
		{
			for (final SabmMessageModel message : messages)
			{
				if (isMessageNotRestricted(message, currentUserB2BUnit))
				{
					final NotificationData notificationData = new NotificationData();
					notificationData.setCode(message.getCode());
					notificationData.setText(message.getText());
					notificationDataList.add(notificationData);
				}
			}
		}
		return notificationDataList;
	}

	private Boolean isMessageNotRestricted(final SabmMessageModel message, final B2BUnitModel b2bUnit)
	{
		Boolean messageIsNotRestricted = true;

		final List<SABMCMSUserGroupRestrictionModel> messageRestrictions = message.getRestrictions();

		if (CollectionUtils.isEmpty(messageRestrictions))
		{
			// if there are no restrictions and no b2bUnit specified in the message then message is shown to all users
			if (message.getB2bUnit() == null)
			{
				return messageIsNotRestricted;
			}
			else
			{
				// if there are no restrictions and there is a b2bUnit specified in the message and current user's b2bUnit matches it
				// then message is shown to all users of that b2bUnit
				if (message.getB2bUnit().equals(b2bUnit))
				{
					return messageIsNotRestricted;
				}
			}
		}

		final List<SABMCMSUserGroupRestrictionModel> sortedMessageRestrictions = getSortedMessageRestrictions(messageRestrictions);

		for (final AbstractRestrictionModel restriction : sortedMessageRestrictions)
		{
			if (restriction instanceof SABMCMSUserGroupRestrictionModel)
			{
				final SABMCMSUserGroupRestrictionModel sabmUserGroupRestriction = (SABMCMSUserGroupRestrictionModel) restriction;
				messageIsNotRestricted = sabmMessageUserGroupRestrictionEvaluator.evaluate(sabmUserGroupRestriction, null);

				// message is restricted if at least 1 exclude (Inverse = true) restriction restricts the message
				if (sabmUserGroupRestriction.getInverse() && !messageIsNotRestricted)
				{
					return messageIsNotRestricted; // message is restricted
				}

				// message is not restricted if at least 1 include (Inverse flag = false) restriction does not restrict the message
				if (!sabmUserGroupRestriction.getInverse() && messageIsNotRestricted)
				{
					return messageIsNotRestricted; // message is not restricted
				}
			}
		}

		return messageIsNotRestricted;
	}

	// Sort the message restrictions - those ones with Inverse flag = true should be first in the list
	private List<SABMCMSUserGroupRestrictionModel> getSortedMessageRestrictions(
			final List<SABMCMSUserGroupRestrictionModel> messageRestrictions)
	{
		if (messageRestrictions.size() > 1)
		{
			final List<SABMCMSUserGroupRestrictionModel> sortedMessageRestrictions = new ArrayList<SABMCMSUserGroupRestrictionModel>(
					messageRestrictions);

			Collections.sort(sortedMessageRestrictions, (restriction1, restriction2) -> {
				if (BooleanUtils.isTrue(restriction2.getInverse()) && BooleanUtils.isFalse(restriction1.getInverse()))
				{
					return 1;
				}
				if (BooleanUtils.isFalse(restriction2.getInverse()) && BooleanUtils.isTrue(restriction1.getInverse()))
				{
					return -1;
				}
				return 0;
			});

			return sortedMessageRestrictions;
		}

		return messageRestrictions;
	}

	@Override
	public void markSiteNotificationAsRead(final String userId, final String messageCode)
	{
		try
		{
			final SabmUserMessagesStatusModel model = modelService.create(SabmUserMessagesStatusModel.class);
			model.setMessageCode(messageCode);
			model.setUserId(userId);
			modelService.save(model);
		}
		catch (final Exception e)
		{
			LOG.error("Error while saving the site notification message as read");
		}
	}

	@Override
	public List<SabmUserMessagesStatusModel> getAllUserMessageEntries(final String messageCode)
	{
		return sabmB2BCustomerDao.getAllUserMessageEntries(messageCode);
	}

	/**
	 * @param user
	 * @return
	 */
	@Override
	public boolean isRegistrationAllowed(final UserModel user, final String b2bUnitId)
	{
		if ((asahiSiteUtil.isSga() && checkIfAsahiPortalAccountsExists(user, b2bUnitId))
				|| (asahiSiteUtil.isApb() && checkIfAsahiPortalAccountsExists(user, b2bUnitId))
				|| (asahiSiteUtil.isCub() && checkIfCUBAccountExists(user, SabmCoreConstants.CUB_STORE)))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public boolean checkIfUserRegisteredForOtherSites(final UserModel user, final boolean createUser)
	{
		if (asahiSiteUtil.isSga())
		{
			final PrincipalGroupModel b2bunit = user.getGroups().stream().filter(group -> group instanceof B2BUnitModel
							&& ((SabmCoreConstants.APB_STORE.equalsIgnoreCase(((B2BUnitModel) group).getCompanyUid()))
							|| ((B2BUnitModel) group).getUid().startsWith("5")
							|| ((SabmCoreConstants.CUB_STORE.equalsIgnoreCase(((B2BUnitModel) group).getCompanyUid())))))
					.findAny()
					.orElse(null);
			return null != b2bunit ? true : false;
		}

		if (asahiSiteUtil.isApb())
		{
			final PrincipalGroupModel b2bunit = user.getGroups().stream()
					.filter(group -> group instanceof B2BUnitModel && ((SabmCoreConstants.ALB_STORE
							.equalsIgnoreCase(((B2BUnitModel) group).getCompanyUid()))
							|| ((B2BUnitModel) group).getUid().startsWith("01")
							|| ((((B2BUnitModel) group).getCompanyUid().equalsIgnoreCase(SabmCoreConstants.CUB_STORE)))))
					.findAny().orElse(null);
			return null != b2bunit ? true : false;
		}

		if (asahiSiteUtil.isCub())
		{
			final PrincipalGroupModel b2bunit = user.getGroups().stream()
					.filter(group -> group instanceof B2BUnitModel
							&& ((SabmCoreConstants.APB_STORE.equalsIgnoreCase(((B2BUnitModel) group).getCompanyUid()))
							|| ((B2BUnitModel) group).getUid().startsWith("5")
							|| ((SabmCoreConstants.ALB_STORE.equalsIgnoreCase(((B2BUnitModel) group).getCompanyUid())))
							|| ((B2BUnitModel) group).getUid().startsWith("01")))
					.findAny().orElse(null);
			return null != b2bunit && (!createUser || !isUserRegisteredForCurrentSite(user)) ? true : false;
		}
		return false;

	}



	/**
	 * @param user
	 * @param site
	 * @return
	 */
	private boolean isUserRegisteredForCurrentSite(final UserModel user)
	{
		if (asahiSiteUtil.isSga())
		{
			final PrincipalGroupModel b2bunit = user.getGroups().stream()
					.filter(group -> group instanceof AsahiB2BUnitModel
							&& ((SabmCoreConstants.ALB_STORE.equalsIgnoreCase(((AsahiB2BUnitModel) group).getCompanyCode()))
							|| ((AsahiB2BUnitModel) group).getUid().startsWith("01")))
					.findAny().orElse(null);
			return null != b2bunit ? true : false;
		}

		if (asahiSiteUtil.isApb())
		{
			final PrincipalGroupModel b2bunit = user.getGroups().stream()
					.filter(group -> group instanceof AsahiB2BUnitModel
							&& ((SabmCoreConstants.APB_STORE.equalsIgnoreCase(((AsahiB2BUnitModel) group).getCompanyCode()))
							|| ((AsahiB2BUnitModel) group).getUid().startsWith("5")))
					.findAny().orElse(null);
			return null != b2bunit ? true : false;
		}

		if (asahiSiteUtil.isCub())
		{
			final PrincipalGroupModel b2bunit = user.getGroups().stream()
					.filter(group -> group instanceof B2BUnitModel
							&& (!(SabmCoreConstants.DELETEDCUSTOMERGROUP.equalsIgnoreCase(group.getUid()))
							&& SabmCoreConstants.CUB_STORE.equalsIgnoreCase(((B2BUnitModel) group).getCompanyUid())))
					.findAny().orElse(null);
			return null != b2bunit ? true : false;
		}
		return false;
	}

	/**
	 * @param user
	 * @param string
	 * @return
	 */
	private boolean checkIfCUBAccountExists(final UserModel user, final String companyCode)
	{
		final PrincipalGroupModel principal = user.getGroups().stream().filter(group -> (group instanceof B2BUnitModel
						&& !(group instanceof AsahiB2BUnitModel) && ((B2BUnitModel) group).getCompanyUid().equalsIgnoreCase(companyCode)
						&& !(SabmCoreConstants.DELETEDCUSTOMERGROUP.equalsIgnoreCase(group.getUid()))))
				.findAny().orElse(null);
		return null != principal ? true : false;
	}

	/**
	 * @param user
	 * @param selfRegister
	 * @param string
	 * @return
	 */
	private boolean checkIfAsahiPortalAccountsExists(final UserModel user, final String b2bUnitId)
	{
		if(null != user.getGroups()) {
			final PrincipalGroupModel principal = user.getGroups().stream()
					.filter(group -> group instanceof AsahiB2BUnitModel
							&& (null != b2bUnitId && b2bUnitId.equals(group.getUid())))
					.findAny().orElse(null);
			return null != principal ? true : false;
		}


		return false;
	}

	@Override
	public List<B2BCustomerModel> getALBCustomersToSFList()
	{
		return sabmB2BCustomerDao.getModifiedALBCustomersForSF();
	}

	@Override
	public List<B2BCustomerModel> getAllALBCustomersToSFList()
	{
		return sabmB2BCustomerDao.getAllALBCustomersForSF();
	}

}

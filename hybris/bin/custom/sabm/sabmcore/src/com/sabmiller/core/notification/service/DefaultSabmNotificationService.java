package com.sabmiller.core.notification.service;


import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.service.impl.DefaultNotificationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.utils.SabmTimeZoneUtils;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.NotificationTimeUnit;
import com.sabmiller.core.event.DealNotificationEmailEvent;
import com.sabmiller.core.event.OrderCutoffNotificationEmailEvent;
import com.sabmiller.core.event.OrderDeliveredNotificationEmailEvent;
import com.sabmiller.core.event.OrderDispatchNotificationEmailEvent;
import com.sabmiller.core.event.OrderNextInQueueDeliveryNotificationEmailEvent;
import com.sabmiller.core.event.OrderUnableToDeliverNotificationEmailEvent;
import com.sabmiller.core.event.TrackOrderETAChangesNotificationEmailEvent;
import com.sabmiller.core.event.TrackOrderETANotificationEmailEvent;
import com.sabmiller.core.event.TrackOrderTimePassesETANotificationEmailEvent;
import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.AsahiNotificationPrefModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.notification.dao.SabmNotificationDao;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.sfmc.context.SABMOrderCutOffSMSContextData;
import com.sabmiller.facades.sfmc.context.SABMOrderDispatchedSMSContextData;
import com.sabmiller.facades.sfmc.context.SABMTmdDeliveredSMSContextData;
import com.sabmiller.facades.sfmc.context.SABMTmdExceedETASMSContextData;
import com.sabmiller.facades.sfmc.context.SABMTmdInTransitSMSContextData;
import com.sabmiller.facades.sfmc.context.SABMTmdNextInQueueSMSContextData;
import com.sabmiller.facades.sfmc.context.SABMTmdPassETASMSContextData;
import com.sabmiller.sfmc.enums.SFMCRequestSMSTemplate;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.pojo.SFMCRequestTo;
import com.sabmiller.sfmc.service.SabmSFMCService;

/**
 * Created by raul.b.abatol.jr on 04/07/2017.
 */
public class DefaultSabmNotificationService extends DefaultNotificationService  implements NotificationService {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmNotificationService.class);


    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "b2bUnitService")
    private SabmB2BUnitService b2bUnitService;

    @Resource(name = "notificationDao")
    private SabmNotificationDao notificationDao;

    @Resource(name = "modelService")
    private ModelService modelService;


    @Resource(name = "sabmDeliveryDateCutOffService")
    private SABMDeliveryDateCutOffService deliveryDateCutOffService;

    @Resource(name = "b2bOrderService")
    private SabmB2BOrderService b2bOrderService;

    @Resource(name = "b2bCommerceUnitService")
    private B2BCommerceUnitService b2bCommerceUnitService;

    @Resource(name = "eventService")
    private EventService eventService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource(name = "dealsService")
    private DealsService dealsService;

    @Resource(name = "orderConverter")
    private Converter<OrderModel, OrderData> orderConverter;


    @Resource(name = "sabmSFMCService")
    private SabmSFMCService sabmSFMCService;

    @Resource(name = "sabmOrderCutOffSMSRequestConverter")
    private AbstractPopulatingConverter sabmOrderCutOffSMSRequestConverter;

    @Resource(name = "sabmOrderDispatchedSMSRequestConverter")
    private AbstractPopulatingConverter sabmOrderDispatchedSMSRequestConverter;

    @Resource(name = "sabmTmdPassETASMSRequestConverter")
    private AbstractPopulatingConverter sabmTmdPassETASMSRequestConverter;

    @Resource(name = "sabmTmdExceedETASMSRequestConverter")
    private AbstractPopulatingConverter sabmTmdExceedETASMSRequestConverter;

    @Resource(name = "sabmTmdNextInQueueSMSRequestConverter")
    private AbstractPopulatingConverter sabmTmdNextInQueueSMSRequestConverter;

    @Resource(name = "sabmTmdInTransitSMSRequestConverter")
    private AbstractPopulatingConverter sabmTmdInTransitSMSRequestConverter;

    @Resource(name = "sabmTmdDeliveredSMSRequestConverter")
    private AbstractPopulatingConverter sabmTmdDeliveredSMSRequestConverter;

    private static final String DATE_PATTERN = "HH.mm";

    private static final String DATE_PATTERN_SMS = "HH:mm";

    @Resource
    private SabmTimeZoneUtils sabmTimeZoneUtils;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    private Map<String, String> salesforceEventMap;

    /**
	 * @return the salesforceEventMap
	 */
	public Map<String, String> getSalesforceEventMap()
	{
		return salesforceEventMap;
	}

	/**
	 * @param salesforceEventMap the salesforceEventMap to set
	 */
	public void setSalesforceEventMap(final Map<String, String> salesforceEventMap)
	{
		this.salesforceEventMap = salesforceEventMap;
	}

    /*extra check to see if user is defined in restricted user list, if restriction list is empty, means it's no restriction.
               this is important to avoid sending email or sms from non-production env to real customer .
           */
    final String restrictionUserIds = Config.getString("sabm.sfmc.restriction.userid", "");
    List<String> restrictedUsers = Arrays.asList(StringUtils.split(restrictionUserIds.trim(), '|'));


    public SABMNotificationModel getNotificationForUserB2BUnit(final UserModel currentUser, final B2BUnitModel selectedB2BUnit) {
		/*
		   This method will only return one notification per user, if a user has multiple notifications, it fetch the first one in list.
		 */
        final List<SABMNotificationModel> notifications = notificationDao.getNotificationForUser((B2BCustomerModel) currentUser,
                selectedB2BUnit);
        return CollectionUtils.isNotEmpty(notifications) ? notifications.get(0) : null;
    }

    public AsahiNotificationModel getNotificationForAsahiUserB2BUnit(final UserModel currentUser, final B2BUnitModel selectedB2BUnit) {
 		/*
 		   This method will only return one notification per user, if a user has multiple notifications, it fetch the first one in list.
 		 */
         final List<AsahiNotificationModel> notifications = notificationDao.getNotificationForAsahiUser((B2BCustomerModel) currentUser,
                 selectedB2BUnit);
         return CollectionUtils.isNotEmpty(notifications) ? notifications.get(0) : null;
     }


    @Override
    public SABMNotificationModel getNotificationByID(final String id) {
        return notificationDao.getNotificationByID(id);
    }

    @Override
    public List<SABMNotificationModel> getNotifications(final NotificationType notificationType,
                                                        final Boolean notificationTypeEnabled) {
        return notificationDao.getNotifications(notificationType, notificationTypeEnabled);
    }

    @Override
    public List<SABMNotificationModel> getNotificationsForUnit(final NotificationType notificationType,
                                                               final Boolean notificationTypeEnabled, final B2BUnitModel b2bUnit) {
        return notificationDao.getNotificationForUnit(notificationType, notificationTypeEnabled, b2bUnit);
    }

    @Override
    public void updateLastSendDateOfNotificationTypeDeliveryMode(final String notificationID,
                                                                 final NotificationType notificationType, final String notificationDeliveryMode, final Date lastSendDate) {
        final SABMNotificationModel notification = getNotificationByID(notificationID);
        if (notification != null) {
            for (final SABMNotificationPrefModel notificationPref : notification.getNotificationPreferences()) {
                if (notificationPref.getNotificationType().equals(notificationType)) {
                    if (notificationPref.getEmailEnabled() || notificationPref.getSmsEnabled()) {
                        switch (notificationDeliveryMode) {
                            case "Email":
                                notificationPref.setEmailLastSendDate(lastSendDate);
                                modelService.save(notificationPref);
                                modelService.refresh(notification);

                            case "SMS":
                                notificationPref.setSmsLastSendDate(lastSendDate);
                                modelService.save(notificationPref);
                                modelService.refresh(notification);
                        }
                    }

                    break;
                }
            }
        }
    }

    @Override
    public void sendNotifications(final NotificationType notificationType) throws Exception {
        // get all Enabled notification settings for the notification type specified (ORDER, DELIVERY, tmdPassETA or DEAL)
        final List<SABMNotificationModel> notifications = this.getNotifications(notificationType, true);

        LOG.debug("Number of notifications = [{}]",
                notifications != null && CollectionUtils.isNotEmpty(notifications) ? notifications.size() : 0);

        /*extra check to see if user is defined in restricted user list, if restriction list is empty, means it's no restriction.
            this is important to avoid sending email or sms from non-production env to real customer .
        */
        final String restrictionUserIds = Config.getString("sabm.sfmc.restriction.userid", "");
        final List<String> restrictedUsers = Arrays.asList(StringUtils.split(restrictionUserIds.trim(),'|'));


        for (final SABMNotificationModel notification : notifications) {
            LOG.debug("User UID = [{}]", notification.getUser().getUid());

            boolean allowToReceiveNotification = false ;

            if(restrictedUsers.isEmpty()) {
                allowToReceiveNotification= true;
            }

            if(!restrictedUsers.isEmpty()){
                    if(restrictedUsers.contains(notification.getUser().getUid())){
                        allowToReceiveNotification= true;
                }
            }

				if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
				{
					allowToReceiveNotification = false;
				}

            // check if user is existing and active
            if (userService.isUserExisting(notification.getUser().getUid()) && notification.getUser().getActive() && allowToReceiveNotification) {
                LOG.debug("User UID = [{}] is existing and active | notificationType = [{}] | notiticationPK = [{}]", notification.getUser().getUid(),
                        notificationType, notification.getPk().toString());

                switch (notificationType) {
                    case ORDER:
                        sendOrderCutoffEmailOrSms(notificationType, notification);
                        break;

//                    case DELIVERY:
//                        sendOrderDispatchEmailOrSms(notificationType, notification);
//                        break;

//                    case UPDATE_FOR_ETA:
//                        sendTrackOrderTimePassesETAEmailOrSms(notificationType, notification);
//                        break;

                    case DEAL:
                        sendDealNotifEmail(notificationType, notification);
                }
            }
        }
    }


    private void sendDealNotifEmail(final NotificationType notificationType, final SABMNotificationModel notification) {
        for (final SABMNotificationPrefModel notificationPref : notification.getNotificationPreferences()) {
            if (notificationPref.getNotificationType().equals(notificationType)) {

                final String serverTimeInBaseStoreTZ = deliveryDateCutOffService
                        .getServerTimeInBaseStoreTimeZone(notification.getB2bUnit(),true);

                final DateTimeFormatter dateTimeParser = DateTimeFormatter.ofPattern(SabmDateUtils.DATE_PATTERN);
                if (dateTimeParser != null) {
                    final LocalDateTime serverTime = dateTimeParser.parse(serverTimeInBaseStoreTZ, LocalDateTime::from);
                    if (serverTime != null && serverTime.getDayOfWeek() != null) {
                        LOG.debug("Day of Week from server time: " + serverTime.getDayOfWeek());
                        LOG.debug("Day of Week from server time int value: " + serverTime.getDayOfWeek().getValue());

                        LOG.debug("Notification opted Day: " + notificationPref.getEmailOptedDay());
                        final int day = serverTime.getDayOfWeek().getValue() + 1;
                        if (day == notificationPref.getEmailOptedDay() && isDealsPresent(notification.getB2bUnit())) {

                            LOG.debug("Sending deal email");
                            eventService.publishEvent(
                                    initializeEvent(new DealNotificationEmailEvent(), notification.getUser(), notification.getB2bUnit()));

                            try {
                                final Date newEmailLastSendDate = DateUtils.truncate(
                                        SabmDateUtils.getDate(serverTimeInBaseStoreTZ, SabmDateUtils.DATE_PATTERN), Calendar.DATE);

                                this.updateLastSendDateOfNotificationTypeDeliveryMode(notification.getPk().toString(),
                                        notificationPref.getNotificationType(), "Email", newEmailLastSendDate);
                            } catch (final ParseException e) {
                                LOG.error("sendDealNotifEmail: ParseException occurred for serverTimeInBaseStoreTZ [{}]: [{}]",
                                        serverTimeInBaseStoreTZ, e);
                            }
                        }
                        break;
                    }

                }

            }
        }
    }

    /**
     * Trigger by Order CutOff Cronjob
     * @param notificationType
     * @param notification
     * @throws Exception
     */
    private void sendOrderCutoffEmailOrSms(final NotificationType notificationType, final SABMNotificationModel notification) throws Exception {
   	// check if no order placed for next delivery date
       final Date nextAvailableDeliveryDate = getSafeNextAvailableDeliveryDate(notification.getB2bUnit());

       LOG.debug("sendOrderCutoffEmail: B2bUnit = [{}] | nextAvailableDeliveryDate = [{}]", notification.getB2bUnit().getUid(),
               nextAvailableDeliveryDate);


       final Date truncatedNextAvailableDeliveryDate = DateUtils.truncate(nextAvailableDeliveryDate, Calendar.DATE);

       LOG.debug("sendOrderCutoffEmail: B2bUnit = [{}] | truncatedNextAvailableDeliveryDate = [{}]", notification.getB2bUnit().getUid(),
               truncatedNextAvailableDeliveryDate);

       final List<OrderModel> orderResults = b2bOrderService.getB2BUnitOrdersByDeliveryDate(notification.getB2bUnit(),
               truncatedNextAvailableDeliveryDate);

       if (LOG.isDebugEnabled())
       {
           LOG.debug("sendOrderCutoffEmailOrSms: B2bUnit = [{}] | orderResults = [{}]", notification.getB2bUnit().getUid(),
                   orderResults != null && CollectionUtils.isNotEmpty(orderResults) ? orderResults.size() : 0);
       }

       if (CollectionUtils.isEmpty(orderResults)) {
           for (final SABMNotificationPrefModel notificationPref : notification.getNotificationPreferences()) {
               LOG.debug("sendOrderCutoffEmail: B2bUnit = [{}] | User UID = [{}] | notificationPref.NotificationType = [{}]",
                       notification.getB2bUnit().getUid(), notification.getUser().getUid(), notificationPref.getNotificationType().toString());

               if (notificationPref.getNotificationType().equals(notificationType)) {
                   LOG.debug("sendOrderCutoff: B2bUnit = [{}] | User UID = [{}] | notificationPref.EmailEnabled = [{}] | notificationPref.SmsEnabled = [{}]",
                           notification.getB2bUnit().getUid(), notification.getUser().getUid(), notificationPref.getEmailEnabled().toString(), notificationPref.getSmsEnabled().toString());


                   // check if server time in base store timezone now is equal to user's preferred notification time duration before cutoff time
                   final String serverTimeInBaseStoreTZ = deliveryDateCutOffService
                           .getServerTimeInBaseStoreTimeZone(notification.getB2bUnit(),true);

                   final String cutoffDateTime = deliveryDateCutOffService.getCutOffTime(notification.getB2bUnit(),nextAvailableDeliveryDate);

                   //check email subscription

                   if (notificationPref.getEmailEnabled()) {

                       final Integer emailDurationInMinutes = getNotificationDurationInMinutes(
                               notificationPref.getEmailOptedTimeUnit(), notificationPref.getEmailOptedTime());

                       final Date emailLastSendDate = notificationPref.getEmailLastSendDate();

                       LOG.debug(
                               "sendOrderCutoffEmail: B2bUnit = [{}] | User UID = [{}] | emailDurationInMinutes = [{}] | serverTimeInBaseStoreTZ = [{}] | cutoffDateTime = [{}] |  emailLastSendDate = [{}] ",
                               notification.getB2bUnit().getUid(), notification.getUser().getUid(), emailDurationInMinutes,
                               serverTimeInBaseStoreTZ, cutoffDateTime,
                               SabmDateUtils.toString(emailLastSendDate, Config.getString("sabm.site.date.pattern", "dd/MM/yyyy")));
                       final boolean isOKToSendTheNotification = isOKToSendTheNotification(emailDurationInMinutes, serverTimeInBaseStoreTZ, cutoffDateTime,
                               emailLastSendDate);

                       LOG.debug("sendOrderCutoffEmail: B2bUnit = [{}] | User UID = [{}] status for to send email notification is: [{}]",
                               notification.getB2bUnit().getUid(), notification.getUser().getUid(),isOKToSendTheNotification);

                       if (isOKToSendTheNotification) {

                           eventService.publishEvent(initializeEvent(new OrderCutoffNotificationEmailEvent(), notification.getUser(),
                                   notification.getPk().toString(), notificationPref.getNotificationType(), cutoffDateTime,
                                   SabmDateUtils.toString(truncatedNextAvailableDeliveryDate,
                                           Config.getString("sabm.site.date.pattern", "dd/MM/yyyy")),
                                   serverTimeInBaseStoreTZ));
                       }
                   }

                   //check sms subscription
                   if (notificationPref.getSmsEnabled()) {

                       final Integer smsDurationInMinutes = getNotificationDurationInMinutes(
                               notificationPref.getSmsOptedTimeUnit(), notificationPref.getSmsOptedTime());

                       final Date smsLastSendDate = notificationPref.getSmsLastSendDate();

                       LOG.debug(
                               "sendOrderCutoffSms: B2bUnit = [{}] | User UID = [{}] | smsDurationInMinutes = [{}] | serverTimeInBaseStoreTZ = [{}] | cutoffDateTime = [{}] |  smsLastSendDate = [{}] ",
                               notification.getB2bUnit().getUid(), notification.getUser().getUid(), smsDurationInMinutes,
                               serverTimeInBaseStoreTZ, cutoffDateTime,
                               SabmDateUtils.toString(smsLastSendDate, Config.getString("sabm.site.date.pattern", "dd/MM/yyyy")));


                        final boolean isOKToSendTheNotification = isOKToSendTheNotification(smsDurationInMinutes, serverTimeInBaseStoreTZ, cutoffDateTime,
                                smsLastSendDate);

                        LOG.debug("sendOrderCutoffEmail: B2bUnit = [{}] | User UID = [{}] status for to send sms notification is: [{}]",
                               notification.getB2bUnit().getUid(), notification.getUser().getUid(),isOKToSendTheNotification);

                        if (isOKToSendTheNotification) {

                           // send sms via sfmc

                           final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                           final List<SFMCRequestTo> toListSMS = new ArrayList<>();

                           final Map map = new HashMap<>();
                           map.put("accountNumber", notification.getB2bUnit().getUid());

                            // make sure cutoffDateTime is not empty in any cases, otherwise , sfmc will reject
                            if (StringUtils.isEmpty(cutoffDateTime) || Objects.isNull(smsDurationInMinutes)){
                                LOG.error("cutOffTime or smsDurationInMinutes can not be empty for sfmc order cutoff sms integration for user: [{}]" ,notification.getUser());
                                continue;
                            }
                           map.put("cutOffPreference", smsDurationInMinutes);
                           map.put("cutOffTime", cutoffDateTime);


                           final SABMOrderCutOffSMSContextData orderCutOffSMSContextData = (SABMOrderCutOffSMSContextData) sabmOrderCutOffSMSRequestConverter
                                   .convert(map);

                           //send SMS via SFMC
                           final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                           sfmcRequestTo.setDynamicData(orderCutOffSMSContextData);
                           sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.ORDERCUTOFFSMS.getCode()));

                           // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                           if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())){
                               LOG.error("Mobile number can not be empty for sfmc order cutoff sms integration for user: [{}]" ,notification.getUser());
                               continue;
                           }
                           sfmcRequestTo.setTo(SabmStringUtils
                                   .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                           sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                           toListSMS.add(sfmcRequestTo);

                           sfmcRequestSMS.setInitiatorEmail(notification.getUser().getUid());
                           sfmcRequestSMS.setToList(toListSMS);
                           sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.ORDERCUTOFFSMS.getCode());


                           final boolean result = sendSMS(sfmcRequestSMS);

                           // if no error return from sfmc
                           if (result) {
                               Date newSmsLastSendDate = null;
                               try {
                                   newSmsLastSendDate = SabmDateUtils.getOnlyDate(serverTimeInBaseStoreTZ);
                               } catch (final ParseException e) {
                                   LOG.error(e.getMessage());
                               }

                               updateLastSendDateOfNotificationTypeDeliveryMode(notification.getPk().toString(),
                                       notificationPref.getNotificationType(), "SMS", newSmsLastSendDate);

                           }
                       }

                   }
                   break;
               }

           }
       }

    }



    /**
     * Triggered by Delivery Cronjob DeliveryNotificationsJob
     * @param notificationType
     */
    @Override
    public void sendOrderDispatchEmailOrSms(final NotificationType notificationType) {

        // return all orders which status are in dispachted
        final List<OrderModel> orderResults = b2bOrderService.getOrdersByOrderStatus(OrderStatus.DISPATCHED);

        if (LOG.isDebugEnabled()) {
            LOG.debug("inside sendOrderDispatchEmailOrSms");
            orderResults.stream().forEach(order -> {
                LOG.debug("Order code: [{}] and Order Unit: [{}],", order.getSapSalesOrderNumber(), order.getUnit().getUid());
            });

        }
        if (CollectionUtils.isNotEmpty(orderResults)) {
            for (final OrderModel order : orderResults) {

                final List<SABMNotificationModel> notifications = this.getNotificationsForUnit(notificationType, true, order.getUnit());

                LOG.debug("Number of notifications = [{}]",
                        notifications != null && CollectionUtils.isNotEmpty(notifications) ? notifications.size() : 0);


                // start send sms via sfmc - construct SFMCRequest
                final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                final List<SFMCRequestTo> toListSMS = new ArrayList<>();
                final Map map = new HashMap<>();
                map.put("accountNumber", order.getUnit().getUid());
                map.put("orderNumber", order.getSapSalesOrderNumber());

                final SABMOrderDispatchedSMSContextData orderDispatchedSMSContextData = (SABMOrderDispatchedSMSContextData) sabmOrderDispatchedSMSRequestConverter
                        .convert(map);
                sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.ORDERDISPATCHED.getCode());
                sfmcRequestSMS.setInitiatorEmail(order.getUser().getUid());

                // end send sms via sfmc - construct SFMCRequest


                for (final SABMNotificationModel notification : notifications) {
                    LOG.debug("User UID = [{}]", notification.getUser().getUid());


                    for (final SABMNotificationPrefModel notificationPref : notification.getNotificationPreferences()) {
                        if (notificationPref.getNotificationType().equals(notificationType)) {

                            boolean allowToReceiveNotification = false;

                            if (restrictedUsers.isEmpty()) {
                                allowToReceiveNotification = true;
                            }

                            if (!restrictedUsers.isEmpty()) {
                                if (restrictedUsers.contains(notification.getUser().getUid())) {
                                    allowToReceiveNotification = true;
                                }
                            }
									 if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
									 {
										 allowToReceiveNotification = false;
									 }


                            // check if user is existing and active
                            if (userService.isUserExisting(notification.getUser().getUid()) && notification.getUser().getActive() && allowToReceiveNotification) {
                                LOG.debug("User UID = [{}] is existing and active | notificationType = [{}] | notiticationPK = [{}]", notification.getUser().getUid(),
                                        notificationType, notification.getPk().toString());


                                if (BooleanUtils.isNotTrue(order.getDispatchNotifEmailSent()) && notificationPref.getEmailEnabled()) {
                                    eventService.publishEvent(initializeEvent(new OrderDispatchNotificationEmailEvent(), notification.getB2bUnit(),
                                            notification.getUser(), order));
                                }
                                if (BooleanUtils.isNotTrue(order.getDispatchNotifSmsSent()) && notificationPref.getSmsEnabled()) {
                                    // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                                    if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())) {
                                        LOG.error("Mobile number can not be empty for sfmc dispatched sms integration for user: [{}] | order: [{}]", notification.getUser(), order.getSapSalesOrderNumber());
                                        continue;
                                    }


                                    final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                                    sfmcRequestTo.setDynamicData(orderDispatchedSMSContextData);
                                    sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.ORDERDISPATCHED.getCode()));
                                    sfmcRequestTo.setTo(SabmStringUtils
                                            .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                                    sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                                    toListSMS.add(sfmcRequestTo);


                                }
                            }
                            break;
                        }
                    }
                }
                sfmcRequestSMS.setToList(toListSMS);
                // send sms
                final boolean smsResult = sendSMS(sfmcRequestSMS);


                // set  email/sms send flag to true


                order.setDispatchNotifEmailSent(Boolean.TRUE);
                for (final ConsignmentModel consigment : order.getConsignments()) {
                    if (ConsignmentStatus.SHIPPED.equals(consigment.getStatus()) && BooleanUtils.isNotTrue(consigment.getDispatchNotifEmailSent())) {
                        consigment.setDispatchNotifEmailSent(true);
                        modelService.save(consigment);
                    }
                }

                // if no error return from sfmc
                if (smsResult) {
                    order.setDispatchNotifSmsSent(Boolean.TRUE);
                    for (final ConsignmentModel consigment : order.getConsignments()) {
                        if (ConsignmentStatus.SHIPPED.equals(consigment.getStatus()) && BooleanUtils.isNotTrue(consigment.getDispatchNotifSmsSent())) {
                            consigment.setDispatchNotifSmsSent(true);
                            modelService.save(consigment);
                        }
                    }


                }
                modelService.save(order);


            }
        }
    }

    /**
     * Trigger by trackOrderTimePassesETANotificationsCronJob
     * @param notificationType

     */
    @Override
    public void sendTrackOrderTimePassesETAEmailOrSms(final NotificationType notificationType) {

        // return all orders which status are in INTRANSIT
        final Set<OrderModel> orderResults = b2bOrderService.getB2BUnitOrdersTimePassesETA(ConsignmentStatus.INTRANSIT);

        if (LOG.isDebugEnabled()) {
            LOG.debug("inside sendTrackOrderTimePassesETAEmailOrSms");
            orderResults.stream().forEach(order -> {
                LOG.debug("Order code: [{}] and Order Unit: [{}],", order.getSapSalesOrderNumber(), order.getUnit().getUid());
            });

        }
        if (CollectionUtils.isNotEmpty(orderResults)) {
            for (final OrderModel order : orderResults) {

                final List<SABMNotificationModel> notifications = this.getNotificationsForUnit(notificationType, true, order.getUnit());

                LOG.debug("Number of notifications = [{}]",
                        notifications != null && CollectionUtils.isNotEmpty(notifications) ? notifications.size() : 0);

                // start send sms via sfmc - construct SFMCRequest
                final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                final List<SFMCRequestTo> toListSMS = new ArrayList<>();
                final Map map = new HashMap<>();
                map.put("accountNumber", order.getUnit().getUid());
                map.put("orderNumber", order.getSapSalesOrderNumber());

                final SABMTmdPassETASMSContextData passETASMSContextData = (SABMTmdPassETASMSContextData) sabmTmdPassETASMSRequestConverter
                        .convert(map);
                sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.TMDPASSETA.getCode());
                sfmcRequestSMS.setInitiatorEmail(order.getUser().getUid());

                // end send sms via sfmc - construct SFMCRequest

                for (final SABMNotificationModel notification : notifications) {
                    LOG.debug("User UID = [{}]", notification.getUser().getUid());


                    for (final SABMNotificationPrefModel notificationPref : notification.getNotificationPreferences()) {
                        if (notificationPref.getNotificationType().equals(notificationType)) {

                            boolean allowToReceiveNotification = false;

                            if (restrictedUsers.isEmpty()) {
                                allowToReceiveNotification = true;
                            }

                            if (!restrictedUsers.isEmpty()) {
                                if (restrictedUsers.contains(notification.getUser().getUid())) {
                                    allowToReceiveNotification = true;
                                }
                            }

									 if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
									 {
										 allowToReceiveNotification = false;
									 }

                            // check if user is existing and active
                            if (userService.isUserExisting(notification.getUser().getUid()) && notification.getUser().getActive() && allowToReceiveNotification) {
                                LOG.debug("User UID = [{}] is existing and active | notificationType = [{}] | notiticationPK = [{}]", notification.getUser().getUid(),
                                        notificationType, notification.getPk().toString());


                                if (BooleanUtils.isNotTrue(order.getTrackOrderTimePassesETANotifEmailSent()) && notificationPref.getEmailEnabled()) {
                                    eventService.publishEvent(initializeEvent(new TrackOrderTimePassesETANotificationEmailEvent(),
                                            notification.getB2bUnit(),
                                            notification.getUser(), order));
                                }
                                if (BooleanUtils.isNotTrue(order.getTrackOrderTimePassesETANotifSmsSent()) && notificationPref.getSmsEnabled()) {
                                    LOG.debug("inside sms for tmd pass ETA");

                                    // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                                    if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())) {
                                        LOG.error("Mobile number can not be empty for sfmc tmd pass ETA sms integration for user: [{}] | order: [{}]" ,notification.getUser(),order.getSapSalesOrderNumber());
                                        continue;
                                    }


                                    final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                                    sfmcRequestTo.setDynamicData(passETASMSContextData);
                                    sfmcRequestTo.setTo(SabmStringUtils
                                            .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                                    sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                                    sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.TMDPASSETA.getCode()));
                                    toListSMS.add(sfmcRequestTo);


                                }
                            }
                            break;
                        }
                    }
                }
                sfmcRequestSMS.setToList(toListSMS);
                // send sms
                final boolean smsResult = sendSMS(sfmcRequestSMS);


                // set  email/sms send flag to true


                order.setTrackOrderTimePassesETANotifEmailSent(Boolean.TRUE);


                // if no error return from sfmc
                if (smsResult) {
                    order.setTrackOrderTimePassesETANotifSmsSent(Boolean.TRUE);


                }
                modelService.save(order);


            }
        }
    }


    private Integer getNotificationDurationInMinutes(final NotificationTimeUnit theNotificationTimeUnit,
                                                     final Integer theNotificationTimeDuration) {
        switch (theNotificationTimeUnit) {
            case HOUR:
            case HOURS:
                return Long.valueOf(TimeUnit.HOURS.toMinutes(theNotificationTimeDuration)).intValue();
            case DAY:
            case DAYS:

                return Long.valueOf(TimeUnit.DAYS.toMinutes(theNotificationTimeDuration)).intValue();

            default:
                return theNotificationTimeDuration;
        }
    }

    public Boolean isOKToSendTheNotification(final Integer theNotificationDurationInMinutes,
                                              final String theServerTimeInBaseStoreTZ, final String theCutoffDateTime, final Date lastSendDate) throws Exception {
        try {
            final DateTimeFormatter dateTimeParser = DateTimeFormatter.ofPattern(SabmDateUtils.DATE_PATTERN);
            final LocalDateTime serverTimeInBaseStoreTZ = dateTimeParser.parse(theServerTimeInBaseStoreTZ, LocalDateTime::from);
            final LocalDateTime cutOffDateTime = dateTimeParser.parse(theCutoffDateTime, LocalDateTime::from);

            final long serverTimeInBaseStoreTZandcutOffDateTimeDiffInMinutes = Duration
                    .between(serverTimeInBaseStoreTZ, cutOffDateTime).toMinutes();

            LOG.debug(
                    "isOKToSendTheNotification: serverTimeInBaseStoreTZandcutOffDateTimeDiffInMinutes = [{}] | theNotificationDurationInMinutes = [{}] | lastSendDate = [{}]",
                    serverTimeInBaseStoreTZandcutOffDateTimeDiffInMinutes, theNotificationDurationInMinutes,lastSendDate);

            // Note: Cannot check for exactly the user's preferred duration since cronjob is not to be run every minute but every 5 or 10 minutes.
            // So, check if time difference is greater than or equal to 0 and less than or equal to preferred duration in addition to checking if
            // lastSendDate is null or before server time in base store timezone
            if ((serverTimeInBaseStoreTZandcutOffDateTimeDiffInMinutes >= 0
                    && serverTimeInBaseStoreTZandcutOffDateTimeDiffInMinutes <= theNotificationDurationInMinutes)
                    && (lastSendDate == null || DateUtils.truncate(lastSendDate, Calendar.DATE).before(DateUtils
                    .truncate(SabmDateUtils.getDate(theServerTimeInBaseStoreTZ, SabmDateUtils.DATE_PATTERN), Calendar.DATE)))) {
                return true;
            }
        } catch (final Exception e) {
            LOG.error("isOKToSendTheNotification: ParseException occurred for theServerTimeInBaseStoreTZ [{}]: [{}]",
                    theServerTimeInBaseStoreTZ, e);
            throw e;
        }
        return false;
    }

    // NOTE: THIS IS THE METHOD TO CALL IF CRONJOB IS TO BE RUN EVERY MINUTE INSTEAD OF THE isOKToSendTheNotification METHOD
    private Boolean isNotificationDurationBeforeCutoffTime(final Integer theNotificationDurationInMinutes,
                                                           final String theServerTimeInBaseStoreTZ, final String theCutoffDateTime) {
        final DateTimeFormatter dateTimeParser = DateTimeFormatter.ofPattern(SabmDateUtils.DATE_PATTERN);
        final LocalDateTime serverTimeInBaseStoreTZ = dateTimeParser.parse(theServerTimeInBaseStoreTZ, LocalDateTime::from);
        final LocalDateTime cutOffDateTime = dateTimeParser.parse(theCutoffDateTime, LocalDateTime::from);

        return serverTimeInBaseStoreTZ.compareTo(cutOffDateTime.minusMinutes(theNotificationDurationInMinutes)) == 0;
    }

    private AbstractCommerceUserEvent<BaseSiteModel> initializeEvent(final AbstractCommerceUserEvent<BaseSiteModel> event,
                                                                     final B2BCustomerModel user) {
        event.setSite(baseSiteService.getBaseSiteForUID("sabmStore"));
        event.setCustomer(user);
        event.setLanguage(commonI18NService.getLanguage("en"));
        event.setCurrency(commonI18NService.getCurrency("AUD"));
        event.setBaseStore(baseStoreService.getBaseStoreForUid("sabmStore"));
        return event;
    }

    protected OrderCutoffNotificationEmailEvent initializeEvent(final OrderCutoffNotificationEmailEvent event,
                                                                final B2BCustomerModel user, final String notificationID, final NotificationType notificationType,
                                                                final String cutoffDateTime, final String deliveryDate, final String serverTimeInBaseStoreTZ) {
        initializeEvent(event, user);

        event.setNotificationID(notificationID);
        event.setNotificationType(notificationType);
        event.setCutoffDateTime(cutoffDateTime);
        event.setDeliveryDate(deliveryDate);
        event.setServerTimeInBaseStoreTZ(serverTimeInBaseStoreTZ);

        return event;
    }


    protected TrackOrderTimePassesETANotificationEmailEvent initializeEvent(
            final TrackOrderTimePassesETANotificationEmailEvent event, final B2BUnitModel b2bUnit, final B2BCustomerModel user,
            final OrderModel order) {
        initializeEvent(event, user);

        event.setB2bUnit(b2bUnit);
        event.setOrderCode(order.getSapSalesOrderNumber());

        return event;
    }

    protected OrderDispatchNotificationEmailEvent initializeEvent(final OrderDispatchNotificationEmailEvent event,
                                                                  final B2BUnitModel b2bUnit, final B2BCustomerModel user, final OrderModel order) {
        initializeEvent(event, user);

        event.setB2bUnit(b2bUnit);
        event.setOrder(order);

        return event;
    }

    protected DealNotificationEmailEvent initializeEvent(final DealNotificationEmailEvent event, final B2BCustomerModel user,
                                                         final B2BUnitModel b2BUnitModel) {

        initializeEvent(event, user);
        event.setB2bUnit(b2BUnitModel);

        return event;
    }

    protected TrackOrderETANotificationEmailEvent initializeEvent(
            final TrackOrderETANotificationEmailEvent event, final B2BUnitModel b2bUnit, final B2BCustomerModel user,
            final AbstractOrderModel order) {
        initializeEvent(event, user);
        event.setB2bUnit(b2bUnit);
        event.setOrder(order);
        return event;
    }

    @Override
    public Date getSafeNextAvailableDeliveryDate(final B2BUnitModel b2bUnit) {
   	 Date deliveryDay = null;
       final Set<Date> enabledCalendarDates = deliveryDateCutOffService.enabledCalendarDates(b2bUnit);



       if (CollectionUtils.isNotEmpty(enabledCalendarDates)) {
           final List<Date> sortedList = new ArrayList<>(enabledCalendarDates);
           Collections.sort(sortedList);

           deliveryDay = sortedList.get(0);
       } else {
           final Calendar cal = Calendar.getInstance();
           cal.setTime(new Date());
           cal.add(Calendar.DAY_OF_YEAR, 1);

           deliveryDay = cal.getTime();
       }
		   return SabmDateUtils.getOnlyDate(deliveryDay);
    }

    public boolean isDealsPresent(final B2BUnitModel unitModel) {
        boolean dealsPresent = false;
        final List<DealModel> deals = dealsService.getDeals(unitModel, new Date(), forNextPeriodDate(new Date()));

        // To determine the valid deals
        final List<DealModel> dealsFiltered = dealsService.getValidationDeals(getSafeNextAvailableDeliveryDate(unitModel), deals,
                false);

        if (CollectionUtils.isNotEmpty(dealsFiltered)) {
            final List<List<DealModel>> composedDeals = dealsService.composeComplexFreeProducts(dealsFiltered);
            if (CollectionUtils.isNotEmpty(composedDeals)) {
                LOG.debug("Deal is not empty: first deal" + composedDeals.get(0).get(0).getCode());
                dealsPresent = true;
            }
        }
        return dealsPresent;
    }

    protected Date forNextPeriodDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, Config.getInt("deal.valid.next.default.day", 14));
        return cal.getTime();
    }


    /*
    both for email and sms
     *  Order Delivered status, Trigger by Retriver request "DefaultConsignmentFacade.updateConsignmentStatusFromRetriever( when ConsignmentStatus.DELIVERED)"
     * @param consignmentModel
     */
    @Override
    public void sendOrderDeliveredNotification(final ConsignmentModel consignmentModel) {


        final List<SABMNotificationModel> notifications = getNotificationsForUnit(NotificationType.DELIVERED, true,
                consignmentModel.getOrder().getUnit());


        for (final SABMNotificationModel notification : notifications) {
            for (final SABMNotificationPrefModel pref : notification.getNotificationPreferences()) {
                if (NotificationType.DELIVERED.equals(pref.getNotificationType()) && pref.getNotificationTypeEnabled()) {
                    if (notification.getUser() != null && notification.getUser() instanceof B2BCustomerModel && notification.getUser().getActive()
								  && !(SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser())))
						  {

                        if (pref.getEmailEnabled()) {
                            final OrderDeliveredNotificationEmailEvent event = new OrderDeliveredNotificationEmailEvent();
                            initializeEvent(event, notification.getUser());
                            event.setSignature(consignmentModel.getSignature());
                            event.setOrder(consignmentModel.getOrder());
                            String timeStamp = null;
                            final String timeZone = consignmentModel.getOrder() != null && consignmentModel.getOrder().getUnit() != null ?
                                    sabmTimeZoneUtils.getPlantTimeZone(consignmentModel.getOrder().getUnit()) :
                                    sabmTimeZoneUtils.getBaseStoreTimeZone();
                            if (consignmentModel.getConsignmentDeliveredDate() != null) {
                                timeStamp = SabmDateUtils
                                        .extractDateString(null, consignmentModel.getConsignmentDeliveredDate(), TimeZone.getTimeZone(timeZone),false);
                            } else {
                                timeStamp = deliveryDateCutOffService.getServerTimeInBaseStoreTimeZone(consignmentModel.getOrder().getUnit(),false);
                            }
                            event.setTimeStamp(timeStamp);
                            final StringBuffer deliveryAddress = new StringBuffer(" ");
                            final B2BUnitModel b2bUnit = consignmentModel.getOrder().getUnit();
                            final Collection<AddressModel> addresses = b2bUnit.getAddresses();

                            for (final AddressModel address : addresses) {
                                if (address != null && address.getShippingAddress()) {
                                    deliveryAddress.append(address.getStreetnumber() != null ? address.getStreetnumber() : "").append(" ");
                                    deliveryAddress.append(address.getStreetname() != null ? address.getStreetname() : "").append(" ");
                                    deliveryAddress.append(address.getTown() != null ? address.getTown() : "").append(" ");
                                    deliveryAddress.append(address.getRegion() != null ? address.getRegion().getName() : "").append(" ");
                                    deliveryAddress.append(address.getPostalcode() != null ? address.getPostalcode() : "").append(" ");

                                }
                            }
                            event.setDeliveryAddress(deliveryAddress.toString());
                            eventService.publishEvent(event);
                        }
                        if (pref.getSmsEnabled()) {
                            // send sms via sfmc

                            final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                            final List<SFMCRequestTo> toListSMS = new ArrayList<>();

                            final Map map = new HashMap<>();
                            map.put("accountNumber", notification.getB2bUnit().getUid());
                            map.put("orderNumber", consignmentModel.getOrder().getSapSalesOrderNumber());


                            final SABMTmdDeliveredSMSContextData tmdDeliveredSMSContextData = (SABMTmdDeliveredSMSContextData) sabmTmdDeliveredSMSRequestConverter
                                    .convert(map);

                            //send SMS via SFMC
                            final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                            sfmcRequestTo.setDynamicData(tmdDeliveredSMSContextData);
                            sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.TMDDELIVERED.getCode()));

                            // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                            if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())){
                                LOG.error("Mobile number can not be empty for sfmc tmd delivered sms integration for user: [{}] | order: [{}]" ,notification.getUser(), consignmentModel.getOrder().getSapSalesOrderNumber());
                                continue;
                            }

                            sfmcRequestTo.setTo(SabmStringUtils
                                    .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                            sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                            toListSMS.add(sfmcRequestTo);

                            sfmcRequestSMS.setInitiatorEmail(notification.getUser().getUid());
                            sfmcRequestSMS.setToList(toListSMS);
                            sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.TMDDELIVERED.getCode());
                            final boolean result = sendSMS(sfmcRequestSMS);

                            if (!result) {
                                LOG.error("Sms send fail for Track my delivery Delivered for order: {}", consignmentModel.getOrder().getSapSalesOrderNumber());
                            }

                        }
                    }
                    break;
                }

            }
        }
    }


    @Override
    public void sendOrderUnableToDeliverNotification(final ConsignmentModel consignmentModel) {
        final Set<B2BCustomerModel> users = b2bUnitUsersOptedForNotification(consignmentModel, NotificationType.UPDATE_FOR_ETA);
        for (final B2BCustomerModel user : users) {

            final OrderUnableToDeliverNotificationEmailEvent event = new OrderUnableToDeliverNotificationEmailEvent();
            initializeEvent(event, user);
            event.setOrderCode(consignmentModel.getOrder().getSapSalesOrderNumber());
            eventService.publishEvent(event);
        }

        //TODO send sms


    }

    /**
     * Order Next in Queue status, Trigger by Retriver request "DefaultConsignmentFacade.updateConsignmentStatusFromRetriever( when !isConsignmentAlreadyNextInQueue)"
     * @param consignmentModel
     */
    @Override
    public void sendOrderNextInQueueDeliveryNotification(final ConsignmentModel consignmentModel) {
        if (isTimeStampNotInPast(consignmentModel)) {

            final List<SABMNotificationModel> notifications = getNotificationsForUnit(NotificationType.NEXT_IN_QUEUE, true,
                    consignmentModel.getOrder().getUnit());
            final Date startETA = getStartETAForConsignment(consignmentModel);
            final Date endETA = getEndETAForConsignment(consignmentModel);
				final String methodName = "sendOrderNextInQueueDeliveryNotification";
            for (final SABMNotificationModel notification : notifications) {

					boolean allowToReceiveNotification = true;
					if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
					{
						allowToReceiveNotification = false;
					}
                for (final SABMNotificationPrefModel pref : notification.getNotificationPreferences()) {
                    if (NotificationType.NEXT_IN_QUEUE.equals(pref.getNotificationType()) && pref.getNotificationTypeEnabled()) {
							  if (notification.getUser() != null && notification.getUser() instanceof B2BCustomerModel
									  && notification.getUser().getActive() && allowToReceiveNotification
									  && allowToSendNotification(consignmentModel, methodName))
							  {

                            if (pref.getEmailEnabled()) {
                                final OrderNextInQueueDeliveryNotificationEmailEvent event = new OrderNextInQueueDeliveryNotificationEmailEvent();
                                initializeEvent(event, notification.getUser());
                                event.setOrder(consignmentModel.getOrder());

                                if (Objects.nonNull(startETA) && Objects.nonNull(endETA)) {
                                    event.setStartETA(SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                            (consignmentModel.getOrder().getUnit())), startETA, DATE_PATTERN));
                                    event.setEndETA(SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                            (consignmentModel.getOrder().getUnit())), endETA, DATE_PATTERN));
                                }
                                eventService.publishEvent(event);
                            }
                            if (pref.getSmsEnabled()) {
                                // send sms via sfmc

                                final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                                final List<SFMCRequestTo> toListSMS = new ArrayList<>();

                                final Map map = new HashMap<>();
                                map.put("accountNumber", notification.getB2bUnit().getUid());
                                map.put("orderNumber", consignmentModel.getOrder().getSapSalesOrderNumber());

                                map.put("ETA1", SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                        (consignmentModel.getOrder().getUnit())), startETA, DATE_PATTERN_SMS));
                                map.put("ETA2", SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                        (consignmentModel.getOrder().getUnit())), endETA, DATE_PATTERN_SMS));

                                final SABMTmdNextInQueueSMSContextData tmdNextInQueueSMSContextData = (SABMTmdNextInQueueSMSContextData) sabmTmdNextInQueueSMSRequestConverter
                                        .convert(map);

                                //send SMS via SFMC
                                final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                                sfmcRequestTo.setDynamicData(tmdNextInQueueSMSContextData);
                                sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.TMDNEXTINQUEUE.getCode()));

                                // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                                if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())){
                                    LOG.error("Mobile number can not be empty for sfmc tmd next in queue sms integration for user: [{}] | order: [{}]" ,notification.getUser(), consignmentModel.getOrder().getSapSalesOrderNumber());
                                    continue;
                                }

                                sfmcRequestTo.setTo(SabmStringUtils
                                        .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                                sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                                toListSMS.add(sfmcRequestTo);

                                sfmcRequestSMS.setInitiatorEmail(notification.getUser().getUid());
                                sfmcRequestSMS.setToList(toListSMS);
                                sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.TMDNEXTINQUEUE.getCode());
                                final boolean result = sendSMS(sfmcRequestSMS);

                                if (!result) {
                                    LOG.error("Sms send fail for Track my delivery next in queue for order: {}", consignmentModel.getOrder().getSapSalesOrderNumber());
                                }

                            }
                        }
                        break;
                    }

                }
            }
        }
    }

    /**
	  * @param methodName
	  * @param consignmentModel.get
	  * @return
	  */
	 private boolean allowToSendNotification(final ConsignmentModel consignmentModel, final String methodName)
	 {

		 LOG.info("If start date is after current date");
		 final Date currentDate = new Date();
		 final Date estimatedDate = consignmentModel.getEstimatedArrivedTime();
		 if (estimatedDate != null)
		 {
			 LOG.info("printing the start time in allowToSendNotification before condition========" + estimatedDate
				 + "=====If the estimated arrived time is after current date for delivery number =====" + consignmentModel.getCode()
				 + "printing new date===" + currentDate);

		 LOG.info("printing the method name========" + methodName);
		 if (consignmentModel.getEstimatedArrivedTime().before(currentDate))
		 {
			 final OrderModel order = (OrderModel) consignmentModel.getOrder();
			 order.setTrackOrderTimePassesETANotifEmailSent(true);
			 try
			 {
				 modelService.save(order);
			 }
			 catch (final ModelSavingException ex)
			 {
				 LOG.error("Error while saving order model with ETA passes flag to TRUE", ex);
			 }

			 return false;
		 }
	 }
		 return true;
	 }

	 /**
	  * Exceed ETA more than 2 hours, Trigger by Retriver request
	  * "DefaultConsignmentFacade.updateConsignmentStatusFromRetriever"
	  *
	  * @param consignmentModel
	  */
    @Override
    public void sendOrderETAChangesNotification(final ConsignmentModel consignmentModel) {
        if (isTimeStampNotInPast(consignmentModel)) {

            final List<SABMNotificationModel> notifications = getNotificationsForUnit(NotificationType.UPDATE_FOR_ETA, true,
                    consignmentModel.getOrder().getUnit());
				final String methodName = "sendOrderETAChangesNotification";
            for (final SABMNotificationModel notification : notifications) {

					boolean allowToReceiveNotification = true;
					if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
					{
						allowToReceiveNotification = false;
					}
                for (final SABMNotificationPrefModel pref : notification.getNotificationPreferences()) {
                    if (NotificationType.UPDATE_FOR_ETA.equals(pref.getNotificationType()) && pref.getNotificationTypeEnabled()) {
							  if (notification.getUser() != null && notification.getUser() instanceof B2BCustomerModel
									  && notification.getUser().getActive() && allowToReceiveNotification
									  && allowToSendNotification(consignmentModel, methodName))
							  {

                            if (pref.getEmailEnabled()) {
                                final TrackOrderETAChangesNotificationEmailEvent event = new TrackOrderETAChangesNotificationEmailEvent();
                                initializeEvent(event, notification.getUser());
                                event.setOrderCode(consignmentModel.getOrder().getSapSalesOrderNumber());
                                eventService.publishEvent(event);
                            }
                            if (pref.getSmsEnabled()) {
                                // send sms via sfmc

                                final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                                final List<SFMCRequestTo> toListSMS = new ArrayList<>();

                                final Map map = new HashMap<>();
                                map.put("accountNumber", notification.getB2bUnit().getUid());
                                map.put("orderNumber", consignmentModel.getOrder().getSapSalesOrderNumber());

                                final SABMTmdExceedETASMSContextData tmdExceedETASMSContextData = (SABMTmdExceedETASMSContextData) sabmTmdExceedETASMSRequestConverter
                                        .convert(map);

                                //send SMS via SFMC
                                final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                                sfmcRequestTo.setDynamicData(tmdExceedETASMSContextData);
                                sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.TMDEXCEEDETA.getCode()));

                                // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                                if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())){
                                    LOG.error("Mobile number can not be empty for sfmc tmd exceed ETA 2h sms integration for user: [{}] | order: [{}]" ,notification.getUser(), consignmentModel.getOrder().getSapSalesOrderNumber());
                                    continue;
                                }

                                sfmcRequestTo.setTo(SabmStringUtils
                                        .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                                sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                                toListSMS.add(sfmcRequestTo);

                                sfmcRequestSMS.setInitiatorEmail(notification.getUser().getUid());
                                sfmcRequestSMS.setToList(toListSMS);
                                sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.TMDEXCEEDETA.getCode());
                                final boolean result = sendSMS(sfmcRequestSMS);

                                if (!result) {
                                    LOG.error("Sms send fail for Track my delivery Exceed 2 hours for order: {}", consignmentModel.getOrder().getSapSalesOrderNumber());
                                }

                            }
                        }
                        break;
                    }

                }
            }

        }
    }

    /**
     *  Order in Transit status, Trigger by Retriver request "DefaultConsignmentFacade.updateConsignmentStatusFromRetriever( when isFirstTransitStatusChange)"
     * @param consignmentModel
     */
    @Override
    public void sendOrderETANotification(final ConsignmentModel consignmentModel) {

        if (isTimeStampNotInPast(consignmentModel)) {

            final List<SABMNotificationModel> notifications = getNotificationsForUnit(NotificationType.INTRANSIT, true,
                    consignmentModel.getOrder().getUnit());
            final Date startETA = getStartETAForConsignment(consignmentModel);
            final Date endETA = getEndETAForConsignment(consignmentModel);
				final String methodName = "sendOrderETANotification";
            for (final SABMNotificationModel notification : notifications) {

					boolean allowToReceiveNotification = true;
					if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
					{
						allowToReceiveNotification = false;
					}
                for (final SABMNotificationPrefModel pref : notification.getNotificationPreferences()) {
                    if (NotificationType.INTRANSIT.equals(pref.getNotificationType()) && pref.getNotificationTypeEnabled()) {
							  if (notification.getUser() != null && notification.getUser() instanceof B2BCustomerModel
									  && notification.getUser().getActive() && allowToReceiveNotification
									  && allowToSendNotification(consignmentModel, methodName))
							  {

                            if (pref.getEmailEnabled()) {
                                final TrackOrderETANotificationEmailEvent event = new TrackOrderETANotificationEmailEvent();
                                initializeEvent(event, notification.getUser());
                                event.setOrder(consignmentModel.getOrder());

                                if (Objects.nonNull(startETA) && Objects.nonNull(endETA)) {
                                    event.setStartETA(SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                            (consignmentModel.getOrder().getUnit())), startETA, DATE_PATTERN));
                                    event.setEndETA(SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                            (consignmentModel.getOrder().getUnit())), endETA, DATE_PATTERN));
                                }
                                eventService.publishEvent(event);
                            }
                            if (pref.getSmsEnabled()) {
                                // send sms via sfmc

                                final SFMCRequest sfmcRequestSMS = new SFMCRequest();
                                final List<SFMCRequestTo> toListSMS = new ArrayList<>();

                                final Map map = new HashMap<>();
                                map.put("accountNumber", notification.getB2bUnit().getUid());
                                map.put("orderNumber", consignmentModel.getOrder().getSapSalesOrderNumber());
                                map.put("ETA1", SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                        (consignmentModel.getOrder().getUnit())), startETA, DATE_PATTERN_SMS));
                                map.put("ETA2", SabmDateUtils.getTimeInTimeZone(TimeZone.getTimeZone(sabmTimeZoneUtils.getPlantTimeZone
                                        (consignmentModel.getOrder().getUnit())), endETA, DATE_PATTERN_SMS));


                                final SABMTmdInTransitSMSContextData tmdInTransitSMSContextData = (SABMTmdInTransitSMSContextData) sabmTmdInTransitSMSRequestConverter
                                        .convert(map);

                                //send SMS via SFMC
                                final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                                sfmcRequestTo.setDynamicData(tmdInTransitSMSContextData);
                                sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.TMDINTRANSIT.getCode()));

                                // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                                if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())){
                                    LOG.error("Mobile number can not be empty for sfmc tmd in Transit sms integration for user: [{}] | order: [{}]" ,notification.getUser(), consignmentModel.getOrder().getSapSalesOrderNumber());
                                    continue;
                                }

                                sfmcRequestTo.setTo(SabmStringUtils
                                        .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                                sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                                toListSMS.add(sfmcRequestTo);

                                sfmcRequestSMS.setInitiatorEmail(notification.getUser().getUid());
                                sfmcRequestSMS.setToList(toListSMS);
                                sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.TMDINTRANSIT.getCode());
                                final boolean result = sendSMS(sfmcRequestSMS);

                                if (!result) {
                                    LOG.error("Sms send fail for Track my delivery In Transit for order: {}", consignmentModel.getOrder().getSapSalesOrderNumber());
                                }

                            }
                        }
                        break;
                    }

                }
            }
        }
    }

    private boolean isTimeStampNotInPast(final ConsignmentModel consignmentModel) {
		 final Date estimatedDate = consignmentModel.getEstimatedArrivedTime();
		 if (estimatedDate != null)
		 {
		 LOG.info("printing the start time in isTimeStampNotInPast========"
				 + estimatedDate
				 + "=====If the estimated arrived time is before current date for delivery number =====" + consignmentModel.getCode()
				 + "printing new date===" + new Date());
		 }
        if (consignmentModel.getEstimatedArrivedTime().before(new Date())) {

            final OrderModel order = (OrderModel) consignmentModel.getOrder();
            order.setTrackOrderTimePassesETANotifEmailSent(true);
            try {
                modelService.save(order);
            } catch (final ModelSavingException ex) {
                LOG.error("Error while saving order model with ETA passes flag to TRUE", ex);
            }
            return false;
        }
        return true;
    }

    protected Date getStartETAForConsignment(final ConsignmentModel consignmentModel) {
        Date roundedDate = null;
        Date returnDate = null;
        if (consignmentModel.getEstimatedArrivedTime() != null) {
            roundedDate = SabmDateUtils.roundDateToNearestQuarterHour(consignmentModel.getEstimatedArrivedTime());
            if (BooleanUtils.isTrue(consignmentModel.getInTransitNextInQueue())) {
                returnDate = SabmDateUtils.minusMinutes(roundedDate,
                        Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", "")));
            } else {
                returnDate = SabmDateUtils
                        .minusMinutes(roundedDate, Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes", "")));
            }
        }
        return returnDate;
    }

    protected Date getEndETAForConsignment(final ConsignmentModel consignmentModel) {
        Date roundedDate = null;
        Date returnDate = null;
        if (consignmentModel.getEstimatedArrivedTime() != null) {
            roundedDate = SabmDateUtils.roundDateToNearestQuarterHour(consignmentModel.getEstimatedArrivedTime());
            if (BooleanUtils.isTrue(consignmentModel.getInTransitNextInQueue())) {

                returnDate = SabmDateUtils.plusMinutes(roundedDate,
                        Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", "")));
            } else {
                returnDate = SabmDateUtils
                        .plusMinutes(roundedDate, Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes", "")));
            }
        }
        return returnDate;
    }


    /**
     * @param consignmentModel
     * @param notificationType
     * Method is NOT used any more
     */
    @Deprecated
    private Set<B2BCustomerModel> b2bUnitUsersOptedForNotification(final ConsignmentModel consignmentModel, final NotificationType notificationType) {
        final List<SABMNotificationModel> notifications = getNotificationsForUnit(notificationType, true,
                consignmentModel.getOrder().getUnit());

        final Set<B2BCustomerModel> users = new HashSet<>();
        for (final SABMNotificationModel notification : notifications) {

			  boolean allowToReceiveNotification = true;
			  if (SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
			  {
				  allowToReceiveNotification = false;
			  }
            for (final SABMNotificationPrefModel pref : notification.getNotificationPreferences()) {
                if (notificationType.equals(pref.getNotificationType()) && pref.getNotificationTypeEnabled()
                        && pref.getEmailEnabled()) {
						 if (notification.getUser() != null && notification.getUser() instanceof B2BCustomerModel
								 && notification.getUser().getActive() && allowToReceiveNotification)
						 {
                        users.add(notification.getUser());
                    }
                }
            }
        }
        return users;
    }


    private boolean sendSMS(final SFMCRequest sfmcRequestSMS) {

        final boolean success = false;

        if(CollectionUtils.isEmpty(sfmcRequestSMS.getToList())){
            return success;
        }

        try {
            sabmSFMCService.sendSMS(sfmcRequestSMS);
        } catch (final SFMCClientException e) {
            LOG.error(e.getMessage());
            return success;
        } catch (final SFMCRequestPayloadException e) {
            LOG.error(e.getMessage());
            return success;
        } catch (final SFMCRequestKeyNotFoundException e) {
            LOG.error(e.getMessage());
            return success;
        } catch (final SFMCEmptySubscribersException e) {
            LOG.error(e.getMessage());
            return success;
        }
        return true;
    }

    private String getSubscriberKeySuffixForSMFC() {

        if("prod".equalsIgnoreCase(configurationService.getConfiguration().getString("envType", "prod"))){
            return StringUtils.EMPTY;
        }
		else
		{
			return StringUtils.EMPTY;
		}


    }



	@Override
	public boolean getEmailPreferenceForNotificationType(final NotificationType notificationType, final B2BCustomerModel member,
			final B2BUnitModel asahiB2BUnitModel)
	{
		final AsahiNotificationModel notification = getNotificationForAsahiUserB2BUnit(member, asahiB2BUnitModel);

		if (null != notification) {
   		final List<AsahiNotificationPrefModel> preference = notification.getAsahiNotificationPreferences().stream()
   				.filter(pref -> pref.getNotificationType().equals(notificationType)).collect(Collectors.toList());
   		if (null != preference)
   		{
   			return preference.get(0).getEmailEnabled();
   		}
   		else
   		{
   			return true;
   		}
		} else {
			return true;
		}

	}


	/**
	 * @param customerModel
	 * @return
	 */
	@Override
	public List<SABMNotificationModel> getNotificationForAllUnits(final B2BCustomerModel customerModel)
	{
		return notificationDao.getNotificationForAllUnits(customerModel);
	}


}

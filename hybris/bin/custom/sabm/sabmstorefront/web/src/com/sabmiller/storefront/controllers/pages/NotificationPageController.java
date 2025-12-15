package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.enums.NotificationTimeUnit;
import com.sabmiller.facades.customer.impl.DefaultSABMCustomerFacade;
import com.sabmiller.facades.notification.SABMNotificationFacade;
import com.sabmiller.facades.notifications.data.NotificationPreferenceData;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.storefront.form.SABMNotificationForm;
import com.sabmiller.storefront.form.SABMNotificationForms;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;

/**
 * Created by evariz.d.paragoso on 6/30/17.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/your-notifications")
public class NotificationPageController extends SabmAbstractPageController
{

	/** The Constant NOTIFICATIONS_CMS_PAGE. */
	private static final String NOTIFICATIONS_CMS_PAGE = "notificationsPage";

	@Resource(name = "notificationFacade")
	private SABMNotificationFacade notificationFacade;


	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;


	@Resource(name = "sabmMobileNumberValidator")
	private Validator sabmMobileNumberValidator;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;


	/**
	 * Gets all Notifications for current user and display in page
	 * @param model
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	@RequireHardLogIn
	public String getNotifications(final Model model) throws CMSItemNotFoundException
	{
		final ContentPageModel notificationsCMSPage = getContentPageForLabelOrId(NOTIFICATIONS_CMS_PAGE);
		storeCmsPageInModel(model, notificationsCMSPage);
		setUpMetaDataForContentPage(model, notificationsCMSPage);
		model.addAttribute("notification", notificationFacade.getUserNotification());
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.notification.header"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("isTrackDeliveryOrderFeatureEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER));
		model.addAttribute("isInvoiceDiscrepancyEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY));

		return getViewForPage(model);
	}

	/**
	 * save the notification for current user
	 * @param model
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping(value = "/save", produces = "application/json")
	@RequireHardLogIn
	@ResponseBody
	public Boolean saveNotification(@RequestBody @Valid final SABMNotificationForms forms, final Model model,final BindingResult bindingErrors) throws CMSItemNotFoundException
	{



		//validate mobile number , if it's valid, store against person detail.

		if(StringUtils.isNotEmpty(forms.getMobileNumber())) {
			sabmMobileNumberValidator.validate(forms, bindingErrors);

			if (bindingErrors.hasErrors()) {
				return false;
			}
		}



		List<SABMNotificationForm> list = forms.getForms();
		List<NotificationPreferenceData> notifPreferences = new ArrayList<NotificationPreferenceData>();

		for( SABMNotificationForm notificationForm : list){
			NotificationPreferenceData prefData = new NotificationPreferenceData();
			prefData.setNotificationTypeEnabled(notificationForm.getNotificationEnabled());
			prefData.setNotificationType(notificationForm.getNotificationType());
			prefData.setEmailEnabled(notificationForm.getEmailEnabled());
			prefData.setSmsEnabled(notificationForm.getSmsEnabled());
			if(notificationForm.getEmailEnabled() && StringUtils.isNotEmpty(notificationForm.getEmailDuration())){
				final String[] emailDurationArray = notificationForm.getEmailDuration().split("-");
				if (emailDurationArray.length > 1)
				{
					prefData.setEmailDuration(Integer.valueOf(emailDurationArray[0]));
					prefData.setEmailDurationTimeUnit(emailDurationArray[1]);
				}
				else
				{
					prefData.setEmailDuration(getDay(notificationForm.getEmailDuration()));
				}
				prefData.setDuration(prefData.getEmailDuration()+"-"+prefData.getEmailDurationTimeUnit());
			}

			if(notificationForm.getSmsEnabled() && StringUtils.isEmpty(forms.getMobileNumber())){

				bindingErrors.rejectValue("mobileNumber", "address.phone.invalid ");

				if (bindingErrors.hasErrors()) {
					return false;
				}
			}
			else if(StringUtils.isNotEmpty(notificationForm.getSmsDuration())){
				final String[] smsDurationArray = notificationForm.getSmsDuration().split("-");
				if (smsDurationArray.length > 1)
				{
					prefData.setSmsDuration(Integer.valueOf(smsDurationArray[0]));
					prefData.setSmsDurationTimeUnit(smsDurationArray[1]);
				}
				else
				{
					prefData.setSmsDuration(getDay(notificationForm.getSmsDuration()));
				}
				prefData.setDuration(prefData.getSmsDuration()+"-"+prefData.getSmsDurationTimeUnit());
			}


			notifPreferences.add(prefData);

		}
		notificationFacade.saveNotification(notifPreferences,forms.getMobileNumber());
		return true;


	}

	public NotificationTimeUnit getTimeUnit(final String time)
	{
		return NotificationTimeUnit.valueOf(time.toUpperCase());
	}

	public Integer getDay(final String dayString)
	{
		Integer day = 0;
		if (StringUtils.isNotEmpty(dayString) && !dayString.equals("0")) {
			day =  DayOfWeek.valueOf(StringUtils.upperCase(dayString)).getValue() + 1;
		}
		return day;
	}


}

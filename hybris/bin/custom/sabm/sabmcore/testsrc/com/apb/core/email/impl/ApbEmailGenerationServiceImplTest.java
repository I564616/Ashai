/**
 *
 */
package com.apb.core.email.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.model.process.ApbContactUsEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerWelcomeEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.SgaProfileUpdatedNoticeProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.customer.dao.AsahiCustomerAccountDao;
import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.ApbEmailConfigurationUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;
import com.sabmiller.core.notification.service.NotificationService;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApbEmailGenerationServiceImplTest
{
	@Spy
	@InjectMocks
	private final ApbEmailGenerationServiceImpl apbEmailGenerationServiceImpl = new ApbEmailGenerationServiceImpl();

	@Mock
	private EmailService emailService;
	@Mock
	private AsahiConfigurationService asahiConfigurationService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;
	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private UserService userService;
	@Mock
	protected BaseStoreService baseStoreService;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;
	@Mock
	private AsahiCustomerAccountDao asahiCustomerAccountDao;
	@Mock
	private ApbEmailConfigurationUtil apbEmailConfigurationUtil;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private NotificationService notificationService;
	@Mock
	private EmailAddressModel fromAddress, emailAddress, emailModel, toEmail;
	@Mock
	private CMSSiteModel cmsSite;
	@Mock
	private ApbContactUsEmailProcessModel apbContactUsEmailProcessModel;
	@Mock
	private ContactUsQueryEmailModel contactUsQueryEmail;
	@Mock
	private EmailMessageModel emailMessageModel, toEmailMsg;
	@Mock
	private B2BUnitModel currentB2BUnit;
	@Mock
	private CustomerModel customer;
	@Mock
	private B2BCustomerModel userModel;
	@Mock
	private OrderProcessModel orderProcessModel;
	@Mock
	private AsahiCustomerWelcomeEmailProcessModel customerWelcomeEmailProcess;
	@Mock
	private SgaProfileUpdatedNoticeProcessModel sgaProfileUpdatedNoticeProcessModel;
	@Mock
	private AsahiDealChangeEmailProcessModel asahiDealChangeEmailProcessModel;
	@Mock
	private OrderModel order;
	@Before
	public void setup() {
		when(cmsSiteService.getCurrentSite()).thenReturn(cmsSite);
		when(cmsSite.getUid()).thenReturn("siteId");
		when(emailService.getOrCreateEmailAddressForEmail("configFromAddress", "displayName")).thenReturn(fromAddress);
		when(asahiConfigurationService.getString("contactus.from.emailid.siteId", "")).thenReturn("configFromAddress");
		when(asahiConfigurationService.getString("contactus.from.email.name.siteId", "")).thenReturn("displayName");
		when(apbContactUsEmailProcessModel.getContactUsQueryEmail()).thenReturn(contactUsQueryEmail);
		when(emailService.createEmailMessage(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(emailMessageModel);
		when(orderProcessModel.getOrder()).thenReturn(order);
		when(customer.getContactEmail()).thenReturn("customerEmail");
	}

	@Test
	public void apbContactUsEmailProcessModelSgaTest() {
		when(asahiSiteUtil.isSga()).thenReturn(true);
		when(asahiConfigurationService.getBoolean("sga.contactus.update.available", false)).thenReturn(true);
		when(emailService.getOrCreateEmailAddressForEmail("configFromAddress", "displayName")).thenReturn(fromAddress);
		when(apbContactUsEmailProcessModel.getCustomer()).thenReturn(userModel);
		when(userModel.getDefaultB2BUnit()).thenReturn(currentB2BUnit);
		when(userModel.getUid()).thenReturn("userId");
		Mockito.lenient().when(userModel.getName()).thenReturn("userName");
		final EmailMessageModel returnObject = apbEmailGenerationServiceImpl.createSuperEmailMessage("emailSubject", "emailBody",
				emailModel, apbContactUsEmailProcessModel, "replyToAddress", "emailPage");
		assertEquals(emailMessageModel, returnObject);
	}

	@Test
	public void apbContactUsEmailProcessModelApbTest()
	{
		when(asahiSiteUtil.isApb()).thenReturn(true);
		when(emailService.getOrCreateEmailAddressForEmail("configFromAddress", "displayName")).thenReturn(fromAddress);
		when(asahiConfigurationService.getString(ApbCoreConstants.CONTACT_US_TO_EMAIL + "siteId", ""))
				.thenReturn("sendCompanyDetailsEmail");
		final EmailMessageModel returnObject = apbEmailGenerationServiceImpl.createSuperEmailMessage("emailSubject", "emailBody",
				emailModel, apbContactUsEmailProcessModel, "replyToAddress", "emailPage");
				assertEquals(emailMessageModel, returnObject);
	}

	@Test
	public void orderProcessModelBDETest()
	{
		Mockito.lenient().when(order.getUser()).thenReturn(userModel);
		when(order.getBdeOrder()).thenReturn(true);
		when(orderProcessModel.getToEmails()).thenReturn(Collections.singletonList("toEmail"));
		when(userService.getUserForUID("toEmail")).thenReturn(userModel);
		when(asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION_FROM_EMAIL + "siteId", ""))
				.thenReturn("configFromAddress");
		when(asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION_EMAIL_NAME + "siteId", ""))
				.thenReturn("displayName");
		final EmailMessageModel returnObject = apbEmailGenerationServiceImpl.createSuperEmailMessage("emailSubject", "emailBody",
				emailModel, orderProcessModel, "replyToAddress", "emailPage");
		assertEquals(emailMessageModel, returnObject);
	}

	@Test
	public void asahiCustomerWelcomeEmailProcessModelTest() {
		when(apbEmailConfigurationUtil.getSubject(customerWelcomeEmailProcess)).thenReturn("emailSubject");
		when(customerWelcomeEmailProcess.getCustomer()).thenReturn(customer);
		when(asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_WELCOME_FROM_EMAIL + "siteId", ""))
				.thenReturn("configFromAddress");
		when(asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_WELCOME_EMAIL_NAME + "siteId", ""))
				.thenReturn("displayName");
		final EmailMessageModel returnObject = apbEmailGenerationServiceImpl.createSuperEmailMessage("emailSubject", "emailBody",
				emailModel, customerWelcomeEmailProcess, "replyToAddress", "emailPage");
		assertEquals(emailMessageModel, returnObject);
	}

	@Test
	public void sgaProfileUpdatedNoticeProcessModelTest()
	{
		when(apbEmailConfigurationUtil.getSubject(sgaProfileUpdatedNoticeProcessModel)).thenReturn("emailSubject");
		when(sgaProfileUpdatedNoticeProcessModel.getCustomer()).thenReturn(customer);
		when(asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_PROFILE_UPDATE_FROM_EMAIL + "siteId", ""))
				.thenReturn("configFromAddress");
		when(asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_PROFILE_UPDATE_EMAIL_NAME + "siteId", ""))
				.thenReturn("displayName");
		final EmailMessageModel returnObject = apbEmailGenerationServiceImpl.createSuperEmailMessage("emailSubject", "emailBody",
				emailModel, sgaProfileUpdatedNoticeProcessModel, "replyToAddress", "emailPage");
		assertEquals(emailMessageModel, returnObject);
	}

	@Test
	public void asahiDealChangeEmailProcessModelTest()
	{
		when(asahiDealChangeEmailProcessModel.getToEmails()).thenReturn(Collections.singletonList("toEmail"));
		when(userService.getUserForUID("toEmail")).thenReturn(userModel);
		when(asahiDealChangeEmailProcessModel.getSite()).thenReturn(cmsSite);
		when(asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DEAL_UPDATE_FROM_EMAIL + "siteId", ""))
				.thenReturn("configFromAddress");
		when(apbEmailConfigurationUtil.getSubject(asahiDealChangeEmailProcessModel)).thenReturn("emailSubject");
		when(asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DEAL_UPDATE_FROM_EMAIL + "siteId", ""))
				.thenReturn("configFromAddress");
		when(asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DEAL_UPDATE_EMAIL_NAME + "siteId", ""))
				.thenReturn("displayName");
		final EmailMessageModel returnObject = apbEmailGenerationServiceImpl.createSuperEmailMessage("emailSubject", "emailBody",
				emailModel, asahiDealChangeEmailProcessModel, "replyToAddress", "emailPage");
		assertEquals(emailMessageModel, returnObject);
	}

}

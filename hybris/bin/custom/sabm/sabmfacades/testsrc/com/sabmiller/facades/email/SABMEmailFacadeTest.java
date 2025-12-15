/**
 *
 */
package com.sabmiller.facades.email;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.mail.EmailConfig;
import com.sabmiller.facades.email.impl.SABMEmailFacadeImpl;


/**
 * @author xiaowu.a.zhang
 * @data 2016-01-08
 */
@UnitTest
public class SABMEmailFacadeTest
{
	private SABMEmailFacadeImpl sabmEmailFacadeImpl;

	@Mock
	private SystemEmailService emailService;
	@Mock
	private UserService userService;
	@Mock
	private EmailConfig emailConfig;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private SabmB2BUnitService b2bUnitService;

	private final String serviceKey = "serviceKey";
	private final String serviceType = "serviceType";
	private final String text = "text";
	private final String mailFrom = "MailFrom";
	private final String toEmailAddress = "toEmailAddress";
	private final String customerUid = "customerUid";
	private final String subject = serviceType + " " + SABMEmailFacadeImpl.CUSTOMER_ID_TITLE + customerUid;
	private final String b2bUnitUid = "b2bUnitUid";
	private final String topLevelName = "topLevelName";
	private final String topLevelUid = "topLevelUid";
	private final String subject1 = "subject";
	private final String message = "message";
	@Mock
	private List<String> messages;
	private SystemEmailMessageModel systemEmailMessageModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sabmEmailFacadeImpl = new SABMEmailFacadeImpl();
		sabmEmailFacadeImpl.setB2bUnitService(b2bUnitService);
		sabmEmailFacadeImpl.setConfigurationService(configurationService);
		sabmEmailFacadeImpl.setEmailConfig(emailConfig);
		sabmEmailFacadeImpl.setEmailService(emailService);
		sabmEmailFacadeImpl.setUserService(userService);

		final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		final B2BUnitModel topB2bUnit = mock(B2BUnitModel.class);
		systemEmailMessageModel = mock(SystemEmailMessageModel.class);

		given(b2bCustomerModel.getUid()).willReturn(customerUid);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(SABMEmailFacadeImpl.SERVICE_REQUEST_EMAIL_HEAD + serviceKey)).willReturn(toEmailAddress);
		given(userService.getCurrentUser()).willReturn(b2bCustomerModel);
		given(emailConfig.getMailFrom()).willReturn(mailFrom);

		given(b2bUnit.getPayerId()).willReturn("payerId");
		given(b2bUnit.getUid()).willReturn(b2bUnitUid);
		given(b2bUnitService.getParent(b2bCustomerModel)).willReturn(b2bUnit);

		given(topB2bUnit.getName()).willReturn(topLevelName);
		given(topB2bUnit.getUid()).willReturn(topLevelUid);
		given(b2bUnitService.findTopLevelB2BUnit(b2bUnit.getPayerId())).willReturn(topB2bUnit);


		messages = Arrays.asList(SABMEmailFacadeImpl.ACCOUNT_NAME_TITLE + topLevelName,
				SABMEmailFacadeImpl.ACCOUNT_NUMBER_TITLE + topLevelUid, SABMEmailFacadeImpl.BUSINESS_UNIT_TITLE + b2bUnitUid,
				SABMEmailFacadeImpl.CUSTOMER_ID_TITLE + customerUid, SABMEmailFacadeImpl.SERVICE_REQUEST_TYPE + serviceType, text);

		given(systemEmailMessageModel.getSubject()).willReturn(subject);
		given(emailService.constructSystemEmail(mailFrom, toEmailAddress, SABMEmailFacadeImpl.EMAIL_DISPLAY, subject, messages,
				null)).willReturn(systemEmailMessageModel);
		given(emailService.send(systemEmailMessageModel)).willReturn(true);
	}

	@Test
	public void testSendServiceRequestEmail()
	{
		final SystemEmailMessageModel sMessageModel = sabmEmailFacadeImpl.sendServiceRequestEmail(serviceKey, serviceType, text);
		Assert.assertEquals(subject, sMessageModel.getSubject());
	}

	@Test
	public void testSendContactUsEmail()
	{
		//final SystemEmailMessageModel sMessageModel = sabmEmailFacadeImpl.sendContactUsEmail(subject1, message);
		//Assert.assertEquals(subject, sMessageModel.getSubject());
	}

}

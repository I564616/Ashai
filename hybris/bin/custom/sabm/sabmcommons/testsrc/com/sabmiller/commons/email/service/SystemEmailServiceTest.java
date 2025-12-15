/**
 *
 */
package com.sabmiller.commons.email.service;

import com.sabmiller.commons.email.service.impl.SystemEmailServiceImpl;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailService;
import de.hybris.platform.acceleratorservices.email.strategy.EmailAddressFetchStrategy;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail2.jakarta.HtmlEmail;
import org.apache.commons.mail2.core.EmailException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 *
 */
@UnitTest
public class SystemEmailServiceTest
{
	private SystemEmailServiceImpl emailService;

	@Mock
	private EmailAddressDao emailAddressDao; //NOPMD
	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private EmailAddressFetchStrategy emailAddressFetchStrategy;
	@Mock
	private CatalogService catalogService;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private HtmlEmail email;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		emailService = new SystemEmailServiceImpl()
		{
			@Override
			protected HtmlEmail getPerConfiguredEmail() throws EmailException
			{
				return email;
			}

			@Override
			protected void validateEmailAddress(final String address, final String type)
			{
				// empty
			}
		};
		emailService.setMediaService(mediaService);
		emailService.setModelService(modelService);
		emailService.setConfigurationService(configurationService);
		emailService.setEmailAttachmentsMediaFolderName("EmailAttachments");
		emailService.setEmailAddressFetchStrategy(emailAddressFetchStrategy);
		emailService.setCatalogService(catalogService);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(catalogService.getDefaultCatalog()).willReturn(catalogModel);
		given(catalogModel.getActiveCatalogVersion()).willReturn(catalogVersionModel);
		given(Boolean.valueOf(configuration.getBoolean(DefaultEmailService.EMAILSERVICE_SEND_ENABLED_CONFIG_KEY, true)))
				.willReturn(Boolean.TRUE);
		given(configuration.getString(SystemEmailServiceImpl.EMAIL_BODY_END_OF_LINE_KEY,
				SystemEmailServiceImpl.EMAIL_BODY_END_OF_LINE)).willReturn("\n");
	}

	@Test
	public void testCreateEmailMessage()
	{
		final EmailAddressModel toAddress = mock(EmailAddressModel.class);
		final EmailAddressModel fromAddress = mock(EmailAddressModel.class);
		final SystemEmailMessageModel emailMessageModel = mock(SystemEmailMessageModel.class);

		given(modelService.create(SystemEmailMessageModel.class)).willReturn(emailMessageModel);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(Integer.valueOf(
				configuration.getInt(DefaultEmailService.EMAIL_BODY_MAX_LENGTH_KEY, DefaultEmailService.EMAIL_BODY_MAX_LENGTH)))
						.willReturn(Integer.valueOf(4000));



		final SystemEmailMessageModel message = emailService.createEmailMessage(Collections.singletonList(toAddress), null, null,
				fromAddress, "reply@hybris.com", "subject", "body", null);

		verify(modelService, times(1)).create(SystemEmailMessageModel.class);
		Assert.assertEquals(emailMessageModel, message);
	}

	@Test
	public void testConstructSystemEmail()
	{
		final EmailAddressModel toAddressModel = mock(EmailAddressModel.class);
		final EmailAddressModel fromAddressModel = mock(EmailAddressModel.class);
		final SystemEmailMessageModel emailMessageModel = mock(SystemEmailMessageModel.class);

		final String fromAddress = "fromAddress";
		final String toAddress = "toAddress";
		final String displayName = "displayName";
		final String subject = "subject";

		given(emailAddressFetchStrategy.fetch(fromAddress, displayName)).willReturn(fromAddressModel);
		given(emailAddressFetchStrategy.fetch(toAddress, displayName)).willReturn(toAddressModel);
		final List<String> messages = Arrays.asList("message1", "message2", "message3");

		given(emailMessageModel.getBody()).willReturn("message1\nmessage2\nmessage3\n");
		given(modelService.create(SystemEmailMessageModel.class)).willReturn(emailMessageModel);
		given(Integer.valueOf(
				configuration.getInt(DefaultEmailService.EMAIL_BODY_MAX_LENGTH_KEY, DefaultEmailService.EMAIL_BODY_MAX_LENGTH)))
						.willReturn(Integer.valueOf(4000));
		final SystemEmailMessageModel message = emailService.constructSystemEmail(fromAddress, toAddress, displayName, subject,
				messages, null);
		verify(modelService, times(1)).create(SystemEmailMessageModel.class);
		Assert.assertEquals(emailMessageModel.getBody(), message.getBody());
	}
}

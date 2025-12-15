/**
 *
 */
package com.apb.facades.process.email.context;

import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerWelcomeEmailProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.service.config.AsahiConfigurationService;



/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class AsahiCustomerWelcomeEmailContextTest
{

	@InjectMocks
	private final AsahiCustomerWelcomeEmailContext asahiCustomerWelcomeEmailContext = new AsahiCustomerWelcomeEmailContext();

	@Mock
	private Converter<UserModel, CustomerData> customerConverter;

	@Mock
	private CMSSiteService cmsSiteService;

	@Mock
	private AsahiConfigurationService asahiConfigurationService;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		asahiCustomerWelcomeEmailContext.setCustomerConverter(customerConverter);
	}

	@Test
	public void testInit()
	{
		final AsahiCustomerWelcomeEmailProcessModel businessProcessModel = Mockito
				.mock(AsahiCustomerWelcomeEmailProcessModel.class);
		Mockito.when(businessProcessModel.getCode()).thenReturn("AsahiCustomerWelcomeEmailProcess");
		Mockito.when(businessProcessModel.getOrderAccess()).thenReturn(Boolean.TRUE);
		Mockito.when(businessProcessModel.getPayAccess()).thenReturn(Boolean.TRUE);
		Mockito.when(businessProcessModel.getPayerEmail()).thenReturn("Test@payer.com");
		Mockito.when(businessProcessModel.getCustomerAccountName()).thenReturn("Test Account");
		businessProcessModel.setLanguage(Mockito.mock(LanguageModel.class));
		final EmailPageModel emailPageModel = Mockito.mock(EmailPageModel.class);
		Mockito.when(emailPageModel.getUid()).thenReturn("asahiCustomerWelcomeEmail");
		final CMSSiteModel cmssite = Mockito.mock(CMSSiteModel.class);
		Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(cmssite);
		Mockito.when(cmssite.getUid()).thenReturn("sga");
		asahiCustomerWelcomeEmailContext.init(businessProcessModel, emailPageModel);
		Mockito.verify(asahiConfigurationService, times(2)).getString(Mockito.anyString(), Mockito.anyString());
	}

}

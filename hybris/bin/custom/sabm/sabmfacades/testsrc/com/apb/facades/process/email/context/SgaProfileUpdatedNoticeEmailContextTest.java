/**
 *
 */
package com.apb.facades.process.email.context;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.model.process.SgaProfileUpdatedNoticeProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class SgaProfileUpdatedNoticeEmailContextTest
{

	@InjectMocks
	final private SgaProfileUpdatedNoticeEmailContext context = new SgaProfileUpdatedNoticeEmailContext();

	@Mock
	private UrlEncoderService urlEncoderService;

	@Mock
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Mock
	private CustomerEmailResolutionService customerEmailResolutionService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		context.setUrlEncoderService(urlEncoderService);
		context.setSiteBaseUrlResolutionService(siteBaseUrlResolutionService);
		context.setCustomerEmailResolutionService(customerEmailResolutionService);
	}

	@Test
	public void testPopulate()
	{

		final SgaProfileUpdatedNoticeProcessModel businessProcessModel = Mockito.mock(SgaProfileUpdatedNoticeProcessModel.class);
		Mockito.when(businessProcessModel.getCode()).thenReturn("SgaProfileUpdatedNoticeProcess");
		final AsahiB2BUnitModel b2bunit = Mockito.mock(AsahiB2BUnitModel.class);
		final B2BCustomerModel customer = Mockito.mock(B2BCustomerModel.class);
		Mockito.when(businessProcessModel.getAsahiB2bUnit()).thenReturn(b2bunit);
		Mockito.when(businessProcessModel.getCustomer()).thenReturn(customer);
		Mockito.when(customer.getName()).thenReturn("Test@payer.com");
		Mockito.when(b2bunit.getUid()).thenReturn("TestAccount");
		Mockito.when(b2bunit.getName()).thenReturn("Test Account");
		businessProcessModel.setLanguage(Mockito.mock(LanguageModel.class));
		final EmailPageModel emailPageModel = Mockito.mock(EmailPageModel.class);
		Mockito.when(emailPageModel.getUid()).thenReturn("asahiCustomerWelcomeEmail");
		final CMSSiteModel cmssite = Mockito.mock(CMSSiteModel.class);
		Mockito.when(businessProcessModel.getSite()).thenReturn(cmssite);
		context.init(businessProcessModel, emailPageModel);
		Assert.assertEquals(cmssite, context.getBaseSite());
		Assert.assertEquals(customer, context.getCustomer(businessProcessModel));

	}
}

/**
 *
 */
package com.apb.facades.process.email.context;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.facades.deal.data.AsahiDealData;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.AsahiDealModel;


/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class AsahiDealsChangeNotificationEmailContextTest
{

	@InjectMocks
	private final AsahiDealsChangeNotificationEmailContext asahiDealsChangeNoticeEmailContext = new AsahiDealsChangeNotificationEmailContext();

	@Mock
	protected DealsService dealsService;

	@Mock
	private Converter<AsahiDealModel, AsahiDealData> asahiDealDataConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInit()
	{

		final AsahiDealChangeEmailProcessModel businessProcessModel = Mockito.mock(AsahiDealChangeEmailProcessModel.class);
		Mockito.when(businessProcessModel.getCode()).thenReturn("AsahiDealChangeEmailProcess");
		Mockito.when(businessProcessModel.getUserDisplayName()).thenReturn("Test Customer");
		Mockito.when(businessProcessModel.getRemovedDeals()).thenReturn(Arrays.asList("deal001"));
		Mockito.when(businessProcessModel.getAdditionalDealDetails()).thenReturn("New Deals are available");
		Mockito.when(businessProcessModel.getActivatedDeals()).thenReturn(Arrays.asList("deal002"));
		Mockito.when(businessProcessModel.getToEmails()).thenReturn(Arrays.asList("testcustomer@test.com"));
		final EmailPageModel emailPageModel = Mockito.mock(EmailPageModel.class);
		Mockito.when(emailPageModel.getUid()).thenReturn("asahiDealsChangeEmail");
		asahiDealsChangeNoticeEmailContext.init(businessProcessModel, emailPageModel);
		Mockito.verify(dealsService).getSGADealsForCode(Arrays.asList("deal001"));
		Mockito.verify(dealsService).getSGADealsForCode(Arrays.asList("deal002"));
	}
}

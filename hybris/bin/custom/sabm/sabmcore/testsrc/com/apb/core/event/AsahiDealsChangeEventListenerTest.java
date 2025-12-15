/**
 *
 */
package com.apb.core.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.services.ApbCustomerAccountService;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiDealsChangeEventListenerTest
{
	@Spy
	@InjectMocks
	private final AsahiDealsChangeEventListener asahiDealsChangeEventListener = new AsahiDealsChangeEventListener();

	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private ApbCustomerAccountService customerAccountService;
	@Mock
	private UserModel userModel;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private LanguageModel lang;
	@Mock
	private CurrencyModel currency;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private AsahiDealsChangeEvent event;
	@Mock
	private AsahiDealChangeEmailProcessModel asahiDealChangeEmailProcessModel;
	@Before
	public void setup()
	{
		when(event.getCustomerEmailIds()).thenReturn(Collections.singletonList("customerMailId"));
		when(event.getSite()).thenReturn(baseSite);
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(asahiDealChangeEmailProcessModel);
		when(customerAccountService.getUserByUid("customerMailId")).thenReturn(userModel);
		when(event.getActivatedDeals()).thenReturn(Collections.singletonList("Activated Deals"));
		when(event.getRemovedDeals()).thenReturn(Collections.singletonList("Removed Deals"));
		when(event.getAdditionalDealDetails()).thenReturn("additional Deals");
		when(event.getBaseStore()).thenReturn(baseStore);
		when(event.getLanguage()).thenReturn(lang);
		when(event.getCurrency()).thenReturn(currency);
		doNothing().when(modelService).save(asahiDealChangeEmailProcessModel);
		doNothing().when(businessProcessService).startProcess(asahiDealChangeEmailProcessModel);
	}

	@Test
	public void onSiteEventTest()
	{
		asahiDealsChangeEventListener.onSiteEvent(event);
		Mockito.verify(businessProcessService).startProcess(asahiDealChangeEmailProcessModel);
	}
}

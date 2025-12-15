/**
 *
 */
package com.apb.core.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiPasswordResetEmailEventListenerTest
{
	@Spy
	@InjectMocks
	private final AsahiPasswordResetEmailEventListener asahiPasswordResetEmailEventListener = new AsahiPasswordResetEmailEventListener();
	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private AsahiPasswordResetEmailEvent event;
	@Mock
	private ForgottenPasswordProcessModel asahiPasswordResetEmailProcessModel;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private LanguageModel lang;
	@Mock
	private CurrencyModel currency;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private B2BCustomerModel b2bCustomerModel;

	@Before
	public void setup() {
		when(event.getSite()).thenReturn(baseSite);
		when(event.getLanguage()).thenReturn(lang);
		Mockito.lenient().when(event.getCurrency()).thenReturn(currency);
		when(event.getBaseStore()).thenReturn(baseStore);
		when(baseStore.getDefaultCurrency()).thenReturn(currency);
		when(event.getToken()).thenReturn("token");
		when(event.getOrderAccess()).thenReturn(true);
		when(event.getPayAccess()).thenReturn(true);
		when(event.getPayerEmail()).thenReturn("payerMail");
		when(event.getCustomerAccountName()).thenReturn("customer Account Name");
		when(event.getCustomer()).thenReturn(b2bCustomerModel);
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(asahiPasswordResetEmailProcessModel);
		doNothing().when(modelService).save(Mockito.any(ForgottenPasswordProcessModel.class));
		doNothing().when(businessProcessService).startProcess(Mockito.any(ForgottenPasswordProcessModel.class));
	}

	@Test
	public void onSiteEventTest()
	{
		asahiPasswordResetEmailEventListener.onSiteEvent(event);
		Mockito.verify(businessProcessService).startProcess(asahiPasswordResetEmailProcessModel);
	}

}

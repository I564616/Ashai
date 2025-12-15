/**
 *
 */
package com.apb.core.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerWelcomeEmailProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
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
public class AsahiCustomerWelcomeEmailEventListenerTest
{
	@Spy
	@InjectMocks
	private final AsahiCustomerWelcomeEmailEventListener asahiCustomerWelcomeEmailEventListener = new AsahiCustomerWelcomeEmailEventListener();

	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private CustomerModel customer;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private LanguageModel lang;
	@Mock
	private CurrencyModel currency;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private AsahiCustomerWelcomeEmailEvent event;
	@Mock
	private AsahiCustomerWelcomeEmailProcessModel asahiCustomerWelcomeEmailProcessModel;

	@Before
	public void setup()
	{
		when(event.getSite()).thenReturn(baseSite);
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(asahiCustomerWelcomeEmailProcessModel);
		when(event.getBaseStore()).thenReturn(baseStore);
		when(event.getLanguage()).thenReturn(lang);
		Mockito.lenient().when(event.getCurrency()).thenReturn(currency);
		when(event.getCustomer()).thenReturn(customer);
		when(baseStore.getDefaultCurrency()).thenReturn(currency);
		when(event.getOrderAccess()).thenReturn(true);
		when(event.getPayAccess()).thenReturn(true);
		when(event.getCustomerAccountName()).thenReturn("Cust Acc Name");
		doNothing().when(modelService).save(asahiCustomerWelcomeEmailProcessModel);
		doNothing().when(businessProcessService).startProcess(asahiCustomerWelcomeEmailProcessModel);

	}

	@Test
	public void onSiteEventTest()
	{
		asahiCustomerWelcomeEmailEventListener.onSiteEvent(event);
		Mockito.verify(businessProcessService).startProcess(asahiCustomerWelcomeEmailProcessModel);
	}
}

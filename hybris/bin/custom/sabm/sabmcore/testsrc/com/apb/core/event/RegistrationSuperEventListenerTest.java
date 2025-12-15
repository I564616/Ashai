/**
 *
 */
package com.apb.core.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontSuperCustomerProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
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

import com.apb.core.model.ApbEmailModel;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;

/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RegistrationSuperEventListenerTest
{
	@Spy
	@InjectMocks
	private final RegistrationSuperEventListener registrationSuperEventListener = new RegistrationSuperEventListener();

	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private SuperRegisterEvent registerEvent;
	@Mock
	private B2BCustomerModel b2bCustomerModel;
	@Mock
	private AsahiB2BUnitModel unit;
	@Mock
	private StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private LanguageModel lang;
	@Mock
	private CurrencyModel currency;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private ApbEmailModel apbEmail;

	@Before
	public void setup() {
		when(registerEvent.getSite()).thenReturn(baseSite);
		when(registerEvent.getLanguage()).thenReturn(lang);
		when(registerEvent.getCurrency()).thenReturn(currency);
		when(registerEvent.getBaseStore()).thenReturn(baseStore);
		when(registerEvent.getApbEmail()).thenReturn(apbEmail);
		when(registerEvent.getCustomer()).thenReturn(b2bCustomerModel);
		Mockito.lenient().when(b2bCustomerModel.getGroups()).thenReturn(Collections.singleton(unit));
		when(b2bCustomerModel.getUid()).thenReturn("customerId");
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(storeFrontSuperCustomerProcessModel);
		doNothing().when(modelService).save(Mockito.any(StoreFrontSuperCustomerProcessModel.class));
		doNothing().when(businessProcessService).startProcess(Mockito.any(StoreFrontSuperCustomerProcessModel.class));
	}

	@Test
	public void onSiteEventSGATest()
	{
		when(asahiSiteUtil.isSga()).thenReturn(true);
		when(registerEvent.getAsahiUnits()).thenReturn(Collections.singleton(unit));
		registrationSuperEventListener.onSiteEvent(registerEvent);
		Mockito.verify(businessProcessService).startProcess(Mockito.any(StoreFrontSuperCustomerProcessModel.class));
	}

	@Test
	public void onSiteEventTest() {
		when(asahiSiteUtil.isSga()).thenReturn(false);
		registrationSuperEventListener.onSiteEvent(registerEvent);
		Mockito.verify(businessProcessService).startProcess(Mockito.any(StoreFrontSuperCustomerProcessModel.class));
	}
}

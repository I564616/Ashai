/**
 *
 */
package com.apb.facades.order.converters.populator;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.util.AsahiAdhocCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.BDECustomerModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApbOrderHistoryPopulatorTest
{
	@Spy
	@InjectMocks
	private final ApbOrderHistoryPopulator apbOrderHistoryPopulator = new ApbOrderHistoryPopulator();

	@Mock
	private OrderModel source;
	@Mock
	private OrderHistoryData target;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private BDECustomerModel user;
	@Mock
	private SessionService sessionService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private AsahiAdhocCoreUtil adhocCoreUtil;
	private static final String ASAHI_USER_TIMEOFFSET_COOKIE = "asahiUserTimeOffsetCookie";

	@Test
	public void getUserNameTest()
	{
		when(asahiSiteUtil.isCub()).thenReturn(false);
		when(asahiSiteUtil.isSga()).thenReturn(true);
		Mockito.lenient().when(asahiSiteUtil.isApb()).thenReturn(false);
		when(source.getPlacedBy()).thenReturn(null);
		when(source.getUser()).thenReturn(user);
		when(user.getName()).thenReturn("Test User");
		when(source.getDate()).thenReturn(new Date());
		when(sessionService.getAttribute(ASAHI_USER_TIMEOFFSET_COOKIE)).thenReturn("timeZone");
		when(adhocCoreUtil.getOrderDateInUserTimeZone("timeZone", source.getDate())).thenReturn("offSet_Time");
		when(source.getTotalPrice()).thenReturn(null);
		apbOrderHistoryPopulator.populate(source, target);
		Mockito.verify(target).setFirstName("Rep: Test User");
	}

}

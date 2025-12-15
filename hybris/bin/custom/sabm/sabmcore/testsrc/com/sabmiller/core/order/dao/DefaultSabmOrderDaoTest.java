/**
 *
 */
package com.sabmiller.core.order.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSabmOrderDaoTest
{
	@Spy
	@InjectMocks
	private final DefaultSabmOrderDao sabmOrderDao = new DefaultSabmOrderDao();

	@Mock
	UserModel user;
	@Mock
	AsahiB2BUnitModel currentUnit;
	@Mock
	FlexibleSearchService flexibleSearchService;

	@Mock
	private OrderModel order;
	@Mock
	private SearchResult<Object> result;

	@Test
	public void fetchOnlineOrderCountBasedOnUserB2BUnitAndSiteTest()
	{
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(result);
		when(result.getResult()).thenReturn(Collections.singletonList(order));

		assertEquals(1, sabmOrderDao.fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(user, "siteUid", currentUnit));

	}
}

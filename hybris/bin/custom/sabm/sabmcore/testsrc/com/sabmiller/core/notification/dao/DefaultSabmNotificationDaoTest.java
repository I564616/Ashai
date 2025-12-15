/**
 *
 */
package com.sabmiller.core.notification.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.AsahiNotificationModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSabmNotificationDaoTest
{
	@Spy
	@InjectMocks
	private final DefaultSabmNotificationDao sabmNotificationDao = new DefaultSabmNotificationDao();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SearchResult<Object> result;
	@Mock
	private AsahiNotificationModel notification;

	@Mock
	private B2BCustomerModel customerModel;
	@Mock
	private B2BUnitModel b2BUnitModel;
	@Test
	public void getNotificationForAsahiUserTest()
	{
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(result);
		when(result.getResult()).thenReturn(Collections.singletonList(notification));
		final List<AsahiNotificationModel> searchResult = sabmNotificationDao.getNotificationForAsahiUser(customerModel,
				b2BUnitModel);
		assertEquals(1, searchResult.size());
	}
}

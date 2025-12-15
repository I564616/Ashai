/**
 *
 */
package com.apb.core.order.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.model.OrderStatusMappingModel;
import com.apb.core.order.dao.impl.ApbOrderStatusMappingDaoImpl;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApbOrderStatusMappingDaoImplTest
{
	@Spy
	@InjectMocks
	private final ApbOrderStatusMappingDaoImpl apbOrderStatusDao = new ApbOrderStatusMappingDaoImpl();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private FlexibleSearchQuery query;
	@Mock
	private SearchResult<Object> result;
	@Mock
	private OrderStatusMappingModel orderStatusMapping;

	@Before
	public void setup()
	{
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(result);
		when(result.getResult()).thenReturn(Collections.singletonList(orderStatusMapping));
		when(orderStatusMapping.getApbDisplayStatus()).thenReturn("APB Display Status");
		when(orderStatusMapping.getDisplayStatus()).thenReturn("Display Status");
	}

	@Test
	public void APBDisplayOrderStatusTest()
	{
		assertEquals("APB Display Status", apbOrderStatusDao.getDisplayOrderStatus("statusCode", "apb"));

	}

	@Test
	public void displayOrderStatusTest()
	{
		assertEquals("Display Status", apbOrderStatusDao.getDisplayOrderStatus("statusCode", "sga"));
	}
}

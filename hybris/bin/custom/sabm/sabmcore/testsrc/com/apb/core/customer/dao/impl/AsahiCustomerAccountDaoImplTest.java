/**
 *
 */
package com.apb.core.customer.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
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

import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.PlanogramModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiCustomerAccountDaoImplTest
{
	@Spy
	@InjectMocks
	private final AsahiCustomerAccountDaoImpl asahiCustomerAccountDaoImpl = new AsahiCustomerAccountDaoImpl();

	@Mock
	private UserModel user;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private SearchResult<Object> result;
	@Mock
	private AsahiCatalogProductMappingModel productMapping;
	@Mock
	private PlanogramModel planogram;
	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setup()
	{
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(result);

	}

	@Test
	public void getUserByUidTest()
	{
		when(result.getResult()).thenReturn(Collections.singletonList(user));
		assertEquals(user, asahiCustomerAccountDaoImpl.getUserByUid("userID"));

	}

	@Test
	public void findCatalogHierarchyDataTest()
	{
		when(result.getResult()).thenReturn(Collections.singletonList(productMapping));
		assertEquals(1, asahiCustomerAccountDaoImpl.findCatalogHierarchyData(Collections.singletonList("categoryID")).size());
	}

	@Test
	public void fetchPlanogramByCodeTest()
	{
		when(result.getResult()).thenReturn(Collections.singletonList(planogram));
		assertEquals(planogram, asahiCustomerAccountDaoImpl.fetchPlanogramByCode("code"));
	}
}

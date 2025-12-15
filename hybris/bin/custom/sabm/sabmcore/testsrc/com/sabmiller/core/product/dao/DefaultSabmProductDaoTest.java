/**
 *
 */
package com.sabmiller.core.product.dao;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 *
 */
@UnitTest
public class DefaultSabmProductDaoTest
{

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SabmConfigurationService sabmConfigurationService;

	private DefaultSabmProductDao defaultSabmProductDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSabmProductDao = new DefaultSabmProductDao(ProductModel._TYPECODE);
		defaultSabmProductDao.setFlexibleSearchService(flexibleSearchService);
		defaultSabmProductDao.setSabmConfigurationService(sabmConfigurationService);
	}

	@Test
	public void testGetProductByLevel2()
	{
		final List<Object> resList = new ArrayList<Object>();

		resList.add(createResult("testName1", PK.fromLong(2)));
		resList.add(createResult("testName2", PK.fromLong(3)));

		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);

		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);

		final List<SABMAlcoholVariantProductMaterialModel> similarCustomers = defaultSabmProductDao
				.getProductByLevel2("MockLevel2");

		Assert.assertEquals(2, similarCustomers.size());

	}

	protected List<SABMAlcoholVariantProductMaterialModel> createResult(final String name, final PK pk)
	{
		final List<SABMAlcoholVariantProductMaterialModel> products = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		final SABMAlcoholVariantProductMaterialModel product = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		given(product.getPk()).willReturn(pk);
		given(product.getName()).willReturn(name);
		return products;
	}

	@Test
	public void testGetProductByHierarchy()
	{
		final List<Object> resList = new ArrayList<Object>();
		resList.add(createProduct("X6"));
		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);

		final List<String> sapAvailablityStatuses = new ArrayList<String>();
		sapAvailablityStatuses.add("X6");
		given(sabmConfigurationService.getValidSapProductStatus()).willReturn(sapAvailablityStatuses);

		final List<SABMAlcoholVariantProductMaterialModel> similarProducts = defaultSabmProductDao.getProductByHierarchy(null, null,
				null, null, null, null);

		Assert.assertEquals(1, similarProducts.size());
	}

	protected ProductModel createProduct(final String sapAvailability)
	{
		final SABMAlcoholVariantProductMaterialModel product = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);

		final SABMAlcoholVariantProductEANModel EAN = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		given(EAN.getSapAvailabilityStatus()).willReturn(SAPAvailabilityStatus.valueOf(sapAvailability));

		given(product.getBaseProduct()).willReturn(EAN);

		return product;

	}

	@Test
	public void testGetOrderEntryForCustomerRule()
	{
		final OrderEntryModel entryModel = Mockito.mock(OrderEntryModel.class);
		final List<Object> resList = new ArrayList<Object>();
		resList.add(entryModel);
		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);
		final List<OrderEntryModel> entryModels = defaultSabmProductDao.getOrderEntryForCustomerRule(null, null, null);
		Assert.assertEquals(1, entryModels.size());
	}

	@Test
	public void testGetOrderEntryForGlobalRule()
	{
		final OrderEntryModel entryModel = Mockito.mock(OrderEntryModel.class);
		final List<Object> resList = new ArrayList<Object>();
		resList.add(entryModel);
		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);
		final List<OrderEntryModel> entryModels = defaultSabmProductDao.getOrderEntryForGlobalRule(null, null);
		Assert.assertEquals(1, entryModels.size());
	}

	@Test
	public void testGetOrderEntryForPlantRule()
	{
		final OrderEntryModel entryModel = Mockito.mock(OrderEntryModel.class);
		final List<Object> resList = new ArrayList<Object>();
		resList.add(entryModel);
		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);
		final List<OrderEntryModel> entryModels = defaultSabmProductDao.getOrderEntryForPlantRule(null, null, null);
		Assert.assertEquals(1, entryModels.size());
	}

}


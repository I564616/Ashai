/**
 *
 */
package com.sabmiller.core.b2b.dao;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.PK;
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


/**
 * DefaultSabmB2BCustomerDaoTest
 */
@UnitTest
public class DefaultSabmB2BCustomerDaoTest
{

	@Mock
	private FlexibleSearchService flexibleSearchService;

	private DefaultSabmB2BCustomerDaoImpl sabmB2BCustomerDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmB2BCustomerDao = new DefaultSabmB2BCustomerDaoImpl();
		sabmB2BCustomerDao.setFlexibleSearchService(flexibleSearchService);
	}


	@Test
	public void test()
	{

		final List<Object> resList = new ArrayList<Object>();

		resList.add(createResult("test1@hybris.com", "testName1", PK.fromLong(2)));
		resList.add(createResult("test2@hybris.com", "testName2", PK.fromLong(3)));

		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);

		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);
		final List<String> andUids = new ArrayList<>();
		andUids.add("b2badmingroup");
		andUids.add("b2bassistantgroup");

		final List<String> orUids = new ArrayList<>();
		orUids.add("testUid1");

		final List<B2BCustomerModel> allcustomerModels = sabmB2BCustomerDao.getCustomerByUnits(orUids, andUids);

		Assert.assertEquals(2, allcustomerModels.size());

	}


	protected List<B2BCustomerModel> createResult(final String uid, final String name, final PK pk)
	{
		final List<B2BCustomerModel> ret = new ArrayList<B2BCustomerModel>();
		final B2BCustomerModel b2bCustomer = Mockito.mock(B2BCustomerModel.class);
		given(b2bCustomer.getPk()).willReturn(pk);
		given(b2bCustomer.getName()).willReturn(name);
		given(b2bCustomer.getUid()).willReturn(uid);
		return ret;
	}
}

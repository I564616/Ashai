/**
 *
 */
package com.sabmiller.core.b2b.dao;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.model.OrderTemplateModel;
import com.sabmiller.core.cart.dao.DefaultSabmCommerceCartDao;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author GQ485VQ
 *
 */
@UnitTest
public class DefaultSabmCommerceCartDaoTest
{
	@InjectMocks
	DefaultSabmCommerceCartDao defaultSabmCommerceCartDao;

	private static final String MOCK_UNIT_ID = "MockUnitId";
	private static final String MOCK_UNIT_ID_1 = "MockUnitId1";

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAllSavedCartForB2BUnitTest()
	{
		final AsahiB2BUnitModel unit = new AsahiB2BUnitModel();
		unit.setUid(MOCK_UNIT_ID);
		final AsahiB2BUnitModel unit1 = new AsahiB2BUnitModel();
		unit.setUid(MOCK_UNIT_ID_1);
		final List<Object> resList = new ArrayList<Object>();

		resList.add(createResult(PK.fromLong(2), unit));
		resList.add(createResult(PK.fromLong(3), unit1));

		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);

		final List<OrderTemplateModel> orderTemplateList = defaultSabmCommerceCartDao.getAllSavedCartForB2BUnit(unit);

		Assert.assertEquals(2, orderTemplateList.size());
	}

	protected List<OrderTemplateModel> createResult(final PK pk, final AsahiB2BUnitModel unit)
	{
		final List<OrderTemplateModel> ret = new ArrayList<OrderTemplateModel>();
		final OrderTemplateModel orderTemplate = Mockito.mock(OrderTemplateModel.class);
		given(orderTemplate.getPk()).willReturn(pk);
		given(orderTemplate.getB2bUnit()).willReturn(unit);
		return ret;
	}

}

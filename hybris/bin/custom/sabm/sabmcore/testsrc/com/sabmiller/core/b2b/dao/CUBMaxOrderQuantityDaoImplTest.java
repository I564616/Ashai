/**
 *
 */
package com.sabmiller.core.b2b.dao;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
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

import com.sabmiller.core.model.MaxOrderQtyModel;


/**
 * @author GQ485VQ
 *
 */
@UnitTest
public class CUBMaxOrderQuantityDaoImplTest
{
	@InjectMocks
	CUBMaxOrderQuantityDaoImpl cubMaxOrderQuantityDao;

	private static final String MOCK_PRODUCT_CODE = "MockId";
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
	public void getCUBMaxOrderQuantityForProductCodeTest()
	{
		final List<Object> resList = new ArrayList<Object>();
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid(MOCK_UNIT_ID);
		resList.add(createResult("1234", 5, PK.fromLong(2), unit));
		resList.add(createResult("12345", 6, PK.fromLong(3), unit));

		final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);

		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);

		final List<MaxOrderQtyModel> maxOrderQtyList = cubMaxOrderQuantityDao
				.getCUBMaxOrderQuantityForProductCode(MOCK_PRODUCT_CODE);

		Assert.assertEquals(2, maxOrderQtyList.size());
	}

	protected List<MaxOrderQtyModel> createResult(final String code, final Integer maxQty, final PK pk, final B2BUnitModel unit)
	{
		final List<MaxOrderQtyModel> ret = new ArrayList<MaxOrderQtyModel>();
		final MaxOrderQtyModel maxOrderQty = Mockito.mock(MaxOrderQtyModel.class);
		given(maxOrderQty.getPk()).willReturn(pk);
		given(maxOrderQty.getB2bunit()).willReturn(unit);
		given(maxOrderQty.getMaxOrderQty()).willReturn(maxQty);
		given(maxOrderQty.getCode()).willReturn(code);
		return ret;
	}

}

/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.List;

import jakarta.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.sabmiller.core.deals.SabmProductSampleDataTest;


/**
 * @author joshua.a.antony
 *
 */
@IntegrationTest
public class DefaultSabmB2BUnitDaoTest extends SabmProductSampleDataTest
{
	@Resource(name = "b2bUnitDao")
	private SabmB2BUnitDao dao;


	@Test
	public void testGenerateSearchQuery()
	{
		final SearchB2BUnitQueryParam.Builder builder = new SearchB2BUnitQueryParam.Builder();
		//		builder.banner(test).customer(test).customerGroup(test).distributionChannel(test).division(test).plant(test)
		//				.priceGroup(test).primaryBanner(test).salesGroup(test).salesOffice(test).salesOrgId(test).subBanner(test);

		final SearchB2BUnitQueryParam queryParam = builder.customer(b2bUnitId).build();
		final List<B2BUnitModel> b2bUnits = dao.searchB2BUnit(queryParam);
		Assert.assertEquals(1, b2bUnits.size());
	}
}

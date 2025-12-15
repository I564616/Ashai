/**
 *
 */
package com.sabmiller.core.cdlvalue.dao;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabm.core.model.CDLValueModel;


/**
 * @author GQ485VQ
 *
 */
public class DefaultSabmCDLValueDaoTest
{
	@InjectMocks
	DefaultSabmCDLValueDao defaultSabmCDLValueDao;

	private static final String LOCATION = "MockLocation";
	private static final String CONTAINER_TYPE = "MockContainer";

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getCDLValueModelTest()
	{
		final Optional<CDLValueModel> cdlModel = createResult(LOCATION, CONTAINER_TYPE, PK.fromLong(2));

		final SearchResult<Object> res = new SearchResultImpl<Object>(Arrays.asList(cdlModel), 1, 0, 0);

		when(flexibleSearchService.searchUnique(Mockito.any(FlexibleSearchQuery.class))).thenReturn(res);

		final Optional<CDLValueModel> cdlModel1 = defaultSabmCDLValueDao.getCDLValueModel(LOCATION, CONTAINER_TYPE);

		Assert.assertNotNull(cdlModel1);
	}

	protected Optional<CDLValueModel> createResult(final String location, final String containerType, final PK pk)
	{

		final CDLValueModel cdlValueModel = Mockito.mock(CDLValueModel.class);
		given(cdlValueModel.getPk()).willReturn(pk);
		given(cdlValueModel.getLocation()).willReturn(location);
		given(cdlValueModel.getContainerType()).willReturn(containerType);
		return Optional.of(cdlValueModel);
	}

}

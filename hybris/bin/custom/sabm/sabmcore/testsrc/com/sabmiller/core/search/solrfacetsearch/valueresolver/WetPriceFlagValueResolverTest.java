/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch.valueresolver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractLocalizedValueResolverTest;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.search.solrfacetsearch.provider.impl.WetPriceFlagValueResolver;


/**
 * @author Ranjith.Karuvachery
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class WetPriceFlagValueResolverTest extends AbstractLocalizedValueResolverTest
{

	@InjectMocks
	private WetPriceFlagValueResolver valueResolver;

	@Before
	public void setUp()
	{
		valueResolver = new WetPriceFlagValueResolver();
		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());
	}

	@Test
	public void testResolveWET() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);

		when(product.getWetEligible()).thenReturn(true);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, true, null);
	}

}

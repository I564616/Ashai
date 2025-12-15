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
import com.sabmiller.core.search.solrfacetsearch.provider.impl.CDLCheckLevel4ValueResolver;


/**
 * @author Ranjith.Karuvachery
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CDLCheckLevel4ValueResolverTest extends AbstractLocalizedValueResolverTest
{

	@InjectMocks
	private CDLCheckLevel4ValueResolver valueResolver;

	@Before
	public void setUp()
	{
		valueResolver = new CDLCheckLevel4ValueResolver();
		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());
	}

	@Test
	public void testResolveCDL() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);

		when(product.getLevel4()).thenReturn("C");

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, "C", null);
	}

}

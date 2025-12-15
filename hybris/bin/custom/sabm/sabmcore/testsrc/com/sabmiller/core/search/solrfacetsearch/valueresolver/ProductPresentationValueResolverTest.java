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
import com.sabmiller.core.search.solrfacetsearch.provider.impl.ProductPresentationValueResolver;


/**
 * @author Ranjith.Karuvachery
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProductPresentationValueResolverTest extends AbstractLocalizedValueResolverTest
{

	@InjectMocks
	private ProductPresentationValueResolver valueResolver;

	@Before
	public void setUp()
	{
		valueResolver = new ProductPresentationValueResolver();
		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());
	}

	@Test
	public void testResolveProductPresentation() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);

		when(product.getPresentation()).thenReturn("12*3");

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, "12*3", null);
	}

}

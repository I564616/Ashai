/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch.valueresolver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractLocalizedValueResolverTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.b2b.dao.CUBMaxOrderQuantityDao;
import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.search.solrfacetsearch.provider.impl.MaxOrderQuantityValueResolver;


/**
 * @author Ranjith.Karuvachery
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class MaxOrderQuantityValueResolverTest extends AbstractLocalizedValueResolverTest
{
	private static final String CUSTOMER_MAX_ORDER_QTY = "customerMaxOrderQty";
	private static final String PLANT_MAX_ORDER_QTY = "plantMaxOrderQty";
	private static final String GLOBAL_MAX_ORDER_QTY = "globalMaxOrderQty";
	private static final String INDEX_PROPERTY_DELIMETER = "_";
	@Mock
	private CUBMaxOrderQuantityDao cubMaxOrderQuantityDao;
	@InjectMocks
	private MaxOrderQuantityValueResolver valueResolver = valueResolver = new MaxOrderQuantityValueResolver();

	@Before
	public void setUp()
	{

		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());
	}

	@Test
	public void testResolveMaxOrderQty() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		indexedProperty.setName(CUSTOMER_MAX_ORDER_QTY);
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);

		final List<MaxOrderQtyModel> maxOrderQtyModels = new ArrayList<MaxOrderQtyModel>();
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		maxOrderQtyModels.add(maxOrderQtyModel);
		when(maxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.CUSTOMER_RULE);
		when(maxOrderQtyModel.getB2bunit()).thenReturn(Mockito.mock(B2BUnitModel.class));
		when(maxOrderQtyModel.getB2bunit().getUid()).thenReturn("12345");
		when(maxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(true);
		when(maxOrderQtyModel.getDefaultAvgMaxOrderQty()).thenReturn(3);
		when(maxOrderQtyModel.getStartDate()).thenReturn(new Date());
		when(maxOrderQtyModel.getEndDate()).thenReturn(new Date());
		when(product.getCode()).thenReturn("1234567");
		//when(indexedProperty.getName()).thenReturn(CUSTOMER_MAX_ORDER_QTY);
		when(cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode("1234567")).thenReturn(maxOrderQtyModels);

		final List<String> propertyValue = new ArrayList<String>();
		propertyValue.add(new StringBuilder().append("12345").append(INDEX_PROPERTY_DELIMETER).append("3")
				.append(INDEX_PROPERTY_DELIMETER).append(new Date()).append(INDEX_PROPERTY_DELIMETER).append(new Date()).toString());

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, propertyValue, null);
	}

}

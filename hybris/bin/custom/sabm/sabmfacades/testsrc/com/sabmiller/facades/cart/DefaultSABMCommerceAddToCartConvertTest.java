/**
 *
 */
package com.sabmiller.facades.cart;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.UnitModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.facades.convert.SABMCommerceAddToCartConvert;


/**
 * DefaultSabCommerceAddToCartConvertTest
 *
 * @author yaopeng
 *
 */
@UnitTest
public class DefaultSABMCommerceAddToCartConvertTest
{

	private SABMCommerceAddToCartConvert sabCommerceAddToCartConvert;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabCommerceAddToCartConvert = new SABMCommerceAddToCartConvert();
	}

	@Test
	public void testSabCommerceAddToCart() throws CommerceCartModificationException
	{
		final SABMAlcoholVariantProductEANModel sab = mock(SABMAlcoholVariantProductEANModel.class);
		final PK pk = PK.parse("123568");
		given(sab.getPk()).willReturn(pk);
		final List<ProductUOMMappingModel> uomMappingList = new ArrayList<ProductUOMMappingModel>();
		final ProductUOMMappingModel uomModel = mock(ProductUOMMappingModel.class);

		final UnitModel unitModel1 = mock(UnitModel.class);
		final UnitModel unitModel2 = mock(UnitModel.class);
		final PK pk1 = PK.parse("99999");
		given(unitModel1.getPk()).willReturn(pk1);
		final PK pk2 = PK.parse("88888");
		given(unitModel2.getPk()).willReturn(pk2);
		given(unitModel1.getCode()).willReturn("layer");
		given(unitModel1.getName()).willReturn("Layer");
		given(unitModel2.getCode()).willReturn("case");
		given(unitModel2.getName()).willReturn("Case");
		given(uomModel.getFromUnit()).willReturn(unitModel1);
		given(uomModel.getToUnit()).willReturn(unitModel2);
		given(uomModel.getQtyConversion()).willReturn(Double.valueOf(12));
		uomMappingList.add(uomModel);
		given(sab.getUomMappings()).willReturn(uomMappingList);

		given(sab.getUnit()).willReturn(unitModel2);


		final CommerceCartParameter parameters = new CommerceCartParameter();
		parameters.setQuantity(5);
		parameters.setUnit(unitModel1);
		parameters.setProduct(sab);

		// Obtain the new quantity of products to cart
		//old quantity is 5 and qtyConversion is 12  so the new quantity is 60
		sabCommerceAddToCartConvert.beforeAddToCart(parameters);
		Assert.assertEquals(60, parameters.getQuantity());
		//The unit in the parameter has to be restored with the product one
		Assert.assertEquals(parameters.getProduct().getUnit(), parameters.getUnit());

	}
}

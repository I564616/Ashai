/**
 *
 */
package com.sabmiller.core.product;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SabmPriceRowServiceTest
{
	private static final String PRODUCT_CODE = "eanVariant02";
	private static final String PRODUCT_CODE_NULL = null;
	private static final String PRODUCT_CODE_UNKNOWN = "Unknown";

	@Mock
	private ProductService productService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private UserService userService;

	@Mock
	private SabmPriceRowDao priceRowDao;

	private DefaultSabmPriceRowService priceRowService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		priceRowService = new DefaultSabmPriceRowService();
		priceRowService.setCommonI18NService(commonI18NService);
		priceRowService.setProductService(productService);
		priceRowService.setUserService(userService);
		priceRowService.setSabmPriceRowDao(priceRowDao);
	}

	@Test
	public void testGetPriceRowByProduct()
	{
		final ProductModel mockProductModel = Mockito.mock(ProductModel.class);
		Mockito.when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(mockProductModel);

		final UserModel mockUser = Mockito.mock(UserModel.class);
		Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);

		final CurrencyModel mockCurrency = Mockito.mock(CurrencyModel.class);
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(mockCurrency);

		final UnitModel mockUnit = Mockito.mock(UnitModel.class);
		Mockito.when(mockProductModel.getUnit()).thenReturn(mockUnit);

		final PriceRowModel priceRow = new PriceRowModel();
		priceRow.setCurrency(mockCurrency);
		priceRow.setUnit(mockUnit);

		Mockito.when(priceRowDao.getPriceRowByProduct(mockUser, mockCurrency, mockProductModel)).thenReturn(priceRow);

		final PriceRowModel priceRowResult = priceRowService.getPriceRowByProduct(mockProductModel);
		Assert.assertNotNull(priceRow);
		Assert.assertNull(priceRowResult);
	}

	@Test
	public void testThrowsIllegalArgumentExceptionWhenCodeIsNull()
	{
		try
		{
			priceRowService.getPriceRowByProduct(PRODUCT_CODE_NULL);
			Assert.fail("Should throw IllegalArgumentException because code is null");
		}
		catch (final IllegalArgumentException ex)
		{
			//OK
		}
		catch (final Exception e)
		{
			Assert.fail("Should throw IllegalArgumentException but got " + e);
		}
	}

	@Test
	public void testThrowsIllegalArgumentExceptionWhenCodeNotFound()
	{
		//when
		try
		{
			priceRowService.getPriceRowByProduct(PRODUCT_CODE_UNKNOWN);
			Assert.fail("Should throw IllegalArgumentException because product not found");

		}
		catch (final IllegalArgumentException ex)
		{
			//OK
		}
	}
}

/**
 *
 */
package com.sabmiller.core.product;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.sabmiller.core.deals.SabmProductSampleDataTest;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;


/**
 * @author joshua.a.antony
 *
 */
@IntegrationTest
public class DefaultSabmPriceRowDaoTest extends SabmProductSampleDataTest
{

	@Resource(name = "sabmPriceRowDao")
	private SabmPriceRowDao priceRowDao;

	@Resource
	private ModelService modelService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	private static final String customerId = "0000794396";
	private SABMAlcoholVariantProductEANModel cupProductModel1;

	private final Date deliveryDate = new Date();
	private UserModel user4 = null;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		user4 = new UserModel();
		user4.setUid("test@tes.com");
		modelService.save(user4);

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(SABM_PRODUCT_CATALOG,
				CatalogManager.ONLINE_VERSION);
		cupProductModel1 = createMockProducts(catalogVersionModel, "prd123");

	}

	@Test
	public void testGetPriceRow()
	{
		final UserPriceGroup ug = UserPriceGroup.valueOf(customerId);
		modelService.save(ug);
		final PriceRowModel priceRowModel = modelService.create(PriceRowModel.class);
		priceRowModel.setBasePrice(10.15d);
		priceRowModel.setCurrency(currencyModel);
		priceRowModel.setPrice(8d);
		priceRowModel.setUg(ug);
		//priceRowModel.setProduct(cupProductModel1);
		priceRowModel.setUnit(unitService.getUnitForCode("pieces"));
		priceRowModel.setProductId("prd123EAN");
		priceRowModel.setUnitFactor(1);
		priceRowModel.setStartTime(deliveryDate);
		//priceRowModel.setUser(user4);
		modelService.save(priceRowModel);
		final PriceRowModel model = priceRowDao.getPriceRow(UserPriceGroup.valueOf(customerId), cupProductModel1, deliveryDate);
		assertEquals(Double.valueOf(10.15), model.getBasePrice());
		assertEquals("AUD", model.getCurrency().getIsocode());
		assertEquals(Double.valueOf(8), model.getPrice());
		assertEquals(Integer.valueOf(1), model.getUnitFactor());
		assertEquals("pieces", model.getUnit().getCode());
	}

	@Test
	public void testGetPriceRowByProduct()
	{
		final PriceRowModel priceRowModel = modelService.create(PriceRowModel.class);
		priceRowModel.setBasePrice(10.15d);
		priceRowModel.setCurrency(currencyModel);
		priceRowModel.setPrice(8d);
		//priceRowModel.setProduct(cupProductModel1);
		priceRowModel.setProductId("prd123EAN");
		priceRowModel.setUnit(unitService.getUnitForCode("pieces"));
		priceRowModel.setUnitFactor(1);
		priceRowModel.setUser(user4);
		modelService.save(priceRowModel);
		final PriceRowModel model = priceRowDao.getPriceRowByProduct(user4, currencyModel, cupProductModel1);
		assertEquals(Double.valueOf(10.15), model.getBasePrice());
		assertEquals("AUD", model.getCurrency().getIsocode());
		assertEquals(Double.valueOf(8), model.getPrice());
		assertEquals(Integer.valueOf(1), model.getUnitFactor());
		assertEquals("pieces", model.getUnit().getCode());
	}
}

/**
 *
 */
package com.sabmiller.core.b2b.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sabmiller.core.deals.SabmProductSampleDataTest;
import com.sabmiller.core.enums.LastUpdatedEntityType;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmPriceRowService;


/**
 * @author joshua.a.antony
 *
 */
@UnitTest
public class DefaultSabmB2BUnitServiceTest extends SabmProductSampleDataTest
{
	protected final String cupProduct1 = "000000000000092354";
	protected final String cupProduct2 = "000000000000087448";
	protected final String cupProduct3 = "000000000000087447";
	protected final String cupProduct4 = "000000000000087500";
	protected final String cupProduct5 = "000000000000087531";
	protected final String cupProduct6 = "000000000000090358";
	protected final String cupProduct7 = "000000000000090798";
	protected final String cupProduct8 = "000000000000090344";

	protected SABMAlcoholVariantProductEANModel cupProductModel1 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel2 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel3 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel4 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel5 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel6 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel7 = null;
	protected SABMAlcoholVariantProductEANModel cupProductModel8 = null;


	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "priceRowService")
	private SabmPriceRowService priceRowService;

	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(SABM_PRODUCT_CATALOG,
				CatalogManager.ONLINE_VERSION);
		cupProductModel1 = createMockProducts(catalogVersionModel, cupProduct1);
		cupProductModel2 = createMockProducts(catalogVersionModel, cupProduct2);
		cupProductModel3 = createMockProducts(catalogVersionModel, cupProduct3);
		cupProductModel4 = createMockProducts(catalogVersionModel, cupProduct4);
		cupProductModel5 = createMockProducts(catalogVersionModel, cupProduct5);
		cupProductModel6 = createMockProducts(catalogVersionModel, cupProduct6);
		cupProductModel7 = createMockProducts(catalogVersionModel, cupProduct7);
		cupProductModel8 = createMockProducts(catalogVersionModel, cupProduct8);

	}

	@Test
	public void testIsBOGOFDealsObsolete()
	{
		assertTrue(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));

		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.BOGOF, new Date(), new Date());
		assertFalse(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.BOGOF, cal.getTime(), new Date());
		assertTrue(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -22);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.BOGOF, cal.getTime(), new Date());
		assertFalse(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.BOGOF, cal.getTime(), new Date());
		assertTrue(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));
	}

	@Test
	public void testIsDiscountDealsObsolete()
	{
		assertTrue(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));

		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.PRICING_DISCOUNT, new Date(), new Date());
		assertFalse(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.PRICING_DISCOUNT, cal.getTime(), new Date());
		assertTrue(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -22);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.PRICING_DISCOUNT, cal.getTime(), new Date());
		assertFalse(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.PRICING_DISCOUNT, cal.getTime(), new Date());
		assertTrue(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));

	}

	@Test
	public void testCupObsolete()
	{
		assertTrue(b2bUnitService.isCUPObsolete(companyModel, new Date()));

		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.CUP, new Date(), new Date());
		assertFalse(b2bUnitService.isCUPObsolete(companyModel, new Date()));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.CUP, cal.getTime(), new Date());
		assertTrue(b2bUnitService.isCUPObsolete(companyModel, new Date()));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -22);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.CUP, cal.getTime(), new Date());
		assertFalse(b2bUnitService.isCUPObsolete(companyModel, new Date()));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		saveLastUpdatedEntity(companyModel, LastUpdatedEntityType.CUP, cal.getTime(), new Date());
		assertTrue(b2bUnitService.isCUPObsolete(companyModel, new Date()));

	}

	/**
	 * This has been tested with SOAP-UI mock service running on local which produces response =>>>
	 * <n0:CustomerUnitPricingResponse xmlns:n0="urn:gl.sabmiller.com:com:ecc:mastdata" xmlns:prx=
	 * "urn:sap.com:proxy:GC7:/1SAI/TAS28D24D0B28995612CB5E:701:2013/05/24"> <CustomerUnitPricingResponseHeader>
	 * <Customer>0000794396</Customer> <SalesOrganisation>7001</SalesOrganisation>
	 *
	 * </CustomerUnitPricingResponseHeader> <CustomerUnitPricingResponseItem> <MaterialID>000000000000092354</MaterialID>
	 * <SaleUnit>1</SaleUnit> <UnitOfMeasure>KAR</UnitOfMeasure> <BasePrice>10.00</BasePrice>
	 * <CustUnitPrice>10.00</CustUnitPrice> <Unit>AUD</Unit> <ValidFrom>2014-02-21</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000087448</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>CS</UnitOfMeasure> <BasePrice>100.00</BasePrice> <CustUnitPrice>100.00</CustUnitPrice>
	 * <Unit>AUD</Unit> <ValidFrom>2015-07-24</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000087447</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>CS</UnitOfMeasure> <BasePrice>10.00</BasePrice> <CustUnitPrice>10.00</CustUnitPrice>
	 * <Unit>AUD</Unit> <ValidFrom>2014-02-21</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000087500</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>CS</UnitOfMeasure> <BasePrice>20.00</BasePrice> <CustUnitPrice>20.00</CustUnitPrice>
	 * <Unit>AUD</Unit> <ValidFrom>2015-07-19</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000087531</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>CS</UnitOfMeasure> <BasePrice>40.00</BasePrice> <CustUnitPrice>38.20</CustUnitPrice>
	 * <Unit>AUD</Unit> <ValidFrom>2015-07-19</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000090358</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>KAR</UnitOfMeasure> <BasePrice>100.00</BasePrice> <CustUnitPrice>60.70</CustUnitPrice>
	 * <Unit>AUD</Unit> <ValidFrom>2015-07-19</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000090798</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>CS</UnitOfMeasure> <BasePrice>1.00</BasePrice> <CustUnitPrice>1.20</CustUnitPrice> <Unit>AUD</Unit>
	 * <ValidFrom>2015-07-19</ValidFrom> <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem>
	 * <CustomerUnitPricingResponseItem> <MaterialID>000000000000090344</MaterialID> <SaleUnit>1</SaleUnit>
	 * <UnitOfMeasure>KEG</UnitOfMeasure> <BasePrice>5.00</BasePrice> <CustUnitPrice>5.00</CustUnitPrice>
	 * <Unit>AUD</Unit> <ValidFrom>2015-07-19</ValidFrom>
	 * <ValidTo>9999-12-31</ValidTo> </CustomerUnitPricingResponseItem> </n0:CustomerUnitPricingResponse>
	 */
	@Test
	public void testRefreshCup()
	{
		//b2bUnitService.importFromCUPResponse(companyModel);

		final PriceRowModel model1 = priceRowService.getPriceRow(companyModel.getUid(), cupProduct1);
		final PriceRowModel model2 = priceRowService.getPriceRow(companyModel.getUid(), cupProductModel2);
		final PriceRowModel model3 = priceRowService.getPriceRow(companyModel.getUid(), cupProductModel3);
		final PriceRowModel model4 = priceRowService.getPriceRow(companyModel.getUid(), cupProduct4);
		final PriceRowModel model5 = priceRowService.getPriceRow(companyModel.getUid(), cupProductModel5);
		final PriceRowModel model6 = priceRowService.getPriceRow(companyModel.getUid(), cupProductModel6);
		final PriceRowModel model7 = priceRowService.getPriceRow(companyModel.getUid(), cupProductModel7);
		final PriceRowModel model8 = priceRowService.getPriceRow(companyModel.getUid(), cupProductModel8);

		assertNotNull(model1);
		assertNotNull(model2);
		assertNotNull(model3);
		assertNotNull(model4);
		assertNotNull(model5);
		assertNotNull(model6);
		assertNotNull(model7);
		assertNotNull(model8);

		assertEquals(Double.valueOf(10.00), model1.getBasePrice());
		assertEquals(Double.valueOf(10.00), model1.getPrice());
		assertEquals("KAR", model1.getUnit().getCode());

		assertEquals(Double.valueOf(40.00), model5.getBasePrice());
		assertEquals(Double.valueOf(38.20), model5.getPrice());
		assertEquals("CS", model5.getUnit().getCode());

		assertEquals(Double.valueOf(100.00), model6.getBasePrice());
		assertEquals(Double.valueOf(60.70), model6.getPrice());
		assertEquals("KAR", model6.getUnit().getCode());

		//Finally verify that the CUP is marked as obsolete
		assertFalse(b2bUnitService.isCUPObsolete(companyModel, new Date()));
	}


	@Test
	public void testMarkDiscountDealsAsRefreshed()
	{
		b2bUnitService.markDiscountDealsAsRefreshed(companyModel, new Date());
		assertFalse(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));
	}


	@Test
	public void testMarkBogofDealsAsRefreshed()
	{
		b2bUnitService.markBOGOFDealsAsRefreshed(companyModel, new Date());
		assertFalse(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));
	}

	private void saveLastUpdatedEntity(final B2BUnitModel b2bUnitModel, final LastUpdatedEntityType entityType, final Date date,
			final Date deliveryDate)
	{
		final LastUpdateTimeEntityModel lastUpdateTimeEntityModel = findLastUpdatedEntity(b2bUnitModel, entityType);
		if (lastUpdateTimeEntityModel != null)
		{
			lastUpdateTimeEntityModel.setLastUpdateTime(date);
			lastUpdateTimeEntityModel.setDeliveryDate(deliveryDate);
			getModelService().save(lastUpdateTimeEntityModel);
		}
		else
		{
			final LastUpdateTimeEntityModel newModel = getModelService().create(LastUpdateTimeEntityModel.class);
			newModel.setEntityType(entityType);
			newModel.setLastUpdateTime(date);
			newModel.setDeliveryDate(deliveryDate);
			getModelService().save(newModel);
			final List<LastUpdateTimeEntityModel> existingEntities = b2bUnitModel.getLastUpdateTimeEntities();

			final List<LastUpdateTimeEntityModel> lastUpdatedEntityList = new ArrayList<LastUpdateTimeEntityModel>();
			for (final LastUpdateTimeEntityModel model : ListUtils.emptyIfNull(existingEntities))
			{
				lastUpdatedEntityList.add(model);
			}

			lastUpdatedEntityList.add(newModel);
			b2bUnitModel.setLastUpdateTimeEntities(lastUpdatedEntityList);
			getModelService().save(b2bUnitModel);
		}
	}

	private LastUpdateTimeEntityModel findLastUpdatedEntity(final B2BUnitModel b2bUnitModel,
			final LastUpdatedEntityType lastUpdatedEntityType)
	{
		for (final LastUpdateTimeEntityModel model : ListUtils.emptyIfNull(b2bUnitModel.getLastUpdateTimeEntities()))
		{
			if (lastUpdatedEntityType.equals(model.getEntityType()))
			{
				return model;
			}
		}
		return null;
	}


	//	@SuppressWarnings("boxing")
	//	@Test
	//	public void updateDefaultUnit()
	//	{
	//		final B2BCustomerModel mockUser = Mockito.mock(B2BCustomerModel.class);
	//		Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
	//		final B2BUnitModel defualtB2bunit = Mockito.mock(B2BUnitModel.class);
	//		given(defualtB2bunit.getUid()).willReturn("default");
	//		given(mockUser.getDefaultB2BUnit()).willReturn(defualtB2bunit);
	//		b2bUnitService.updateDefaultCustomerUnit("previous");
	//		assertEquals("previous", mockUser.getDefaultB2BUnit());
	//	}


}

/**
 *
 */
package com.sabmiller.core.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.dao.CUBMaxOrderQuantityDao;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.enums.LifecycleStatusType;
import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.dao.SabmProductDao;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;


/**
 * JUnit for {@link DefaultSabmProductService}
 *
 * @author joshua.a.antony
 */
@UnitTest
public class DefaultSabmProductServiceTest
{

	@InjectMocks
	private final DefaultSabmProductService sabmProductService = new DefaultSabmProductService();
	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;
	@Mock
	private SabmProductDao sabmProductDao;
	@Mock
	private ProductDao productDao;
	@Mock
	ServicesUtil servicesUtil;
	@Mock
	private CUBMaxOrderQuantityDao cubMaxOrderQuantityDao;
	@Mock(name = "cartService")
	private SABMCartService sabmCartService;
	@Mock
	private UserService userService;
	private static final String CUB_SITE = "sabmStore";
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmProductService.setProductDao(productDao);
		sabmProductService.setValidSapAvailStatusList(Arrays.asList(new SAPAvailabilityStatus[]
		{ SAPAvailabilityStatus.X6, SAPAvailabilityStatus.X7, SAPAvailabilityStatus.X8 }));
	}


	@Test
	public void testIsProductVisible()
	{
		//Positive Conditions
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.PREVIEW);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X6);
		assertTrue(sabmProductService.isProductVisible(product));

		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		assertTrue(sabmProductService.isProductVisible(product));

		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.OBSOLETE);
		assertTrue(sabmProductService.isProductVisible(product));

		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X7);
		assertTrue(sabmProductService.isProductVisible(product));

		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X8);
		assertTrue(sabmProductService.isProductVisible(product));

		//NPE check
		Mockito.when(product.getLifecycleStatus()).thenReturn(null);
		Mockito.when(product.getApprovalStatus()).thenReturn(null);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(null);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid SAP Availability Status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.PREVIEW);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X9);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid Approval Status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.PREVIEW);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.CHECK);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X8);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid Lifecycle status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.NOT_LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X6);
		assertFalse(sabmProductService.isProductVisible(product));
	}

	@Test
	public void testIsProductSearchable()
	{
		//Positive Conditions
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X6);
		assertTrue(sabmProductService.isProductVisible(product));


		//NPE check
		Mockito.when(product.getLifecycleStatus()).thenReturn(null);
		Mockito.when(product.getApprovalStatus()).thenReturn(null);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(null);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid SAP Availability Status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X5);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid Approval Status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.CHECK);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X8);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid Lifecycle status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.NOT_LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X7);
		assertFalse(sabmProductService.isProductVisible(product));
	}

	@Test
	public void testIsProductPurchasable()
	{
		//Positive Conditions
		final SABMAlcoholVariantProductEANModel product = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X6);
		assertTrue(sabmProductService.isProductVisible(product));


		//NPE check
		Mockito.when(product.getLifecycleStatus()).thenReturn(null);
		Mockito.when(product.getApprovalStatus()).thenReturn(null);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(null);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid SAP Availability Status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X5);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid Approval Status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.CHECK);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X8);
		assertFalse(sabmProductService.isProductVisible(product));

		//Invalid Lifecycle status
		Mockito.when(product.getLifecycleStatus()).thenReturn(LifecycleStatusType.NOT_LIVE);
		Mockito.when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		Mockito.when(product.getSapAvailabilityStatus()).thenReturn(SAPAvailabilityStatus.X7);
		assertFalse(sabmProductService.isProductVisible(product));
	}

	@Test
	public void testGetAverageQuantityForCustomerRule()
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final List<CMSSiteModel> cmsSiteModels = new ArrayList<CMSSiteModel>();
		final CMSSiteModel cmsSiteModel = new CMSSiteModel();
		cmsSiteModel.setUid(CUB_SITE);
		cmsSiteModels.add(cmsSiteModel);
		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		final CatalogModel catalogModel = Mockito.mock(CatalogModel.class);
		final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = Mockito
				.mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		final List<OrderEntryModel> entryModels = new ArrayList<OrderEntryModel>();
		final OrderEntryModel entryModel = new OrderEntryModel();
		final OrderEntryModel entryModel2 = new OrderEntryModel();
		entryModel.setQuantity(3L);
		entryModel2.setQuantity(3L);
		entryModels.add(entryModel);
		entryModels.add(entryModel2);
		final B2BUnitModel b2bUnitModel = Mockito.mock(B2BUnitModel.class);
		Mockito.when(cmsSiteService.getSites()).thenReturn(cmsSiteModels);
		Mockito.when(catalogVersionDeterminationStrategy.onlineCatalogVersion()).thenReturn(catalogVersionModel);
		Mockito.when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		Mockito.when(catalogModel.getId()).thenReturn("abcd");
		Mockito.when(catalogVersionModel.getVersion()).thenReturn("Online");
		Mockito.when(maxOrderQtyModel.getProduct()).thenReturn("12345");
		Mockito.when(productDao.findProductsByCode(catalogVersionModel, "12345"))
				.thenReturn(Arrays.asList(sabmAlcoholVariantProductEANModel));
		Mockito.when(sabmAlcoholVariantProductEANModel.getLeadSku()).thenReturn(sabmAlcoholVariantProductMaterialModel);
		Mockito.when(maxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.CUSTOMER_RULE);
		Mockito.when(maxOrderQtyModel.getB2bunit()).thenReturn(b2bUnitModel);
		Mockito
				.when(sabmProductDao.getOrderEntryForCustomerRule(sabmAlcoholVariantProductMaterialModel, b2bUnitModel, cmsSiteModel))
				.thenReturn(entryModels);

		final int avergaQty = sabmProductService.getAverageQuantity(maxOrderQtyModel);

		assertEquals(3, avergaQty);

	}

	@Test
	public void testGetAverageQuantityForPlantRule()
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final List<CMSSiteModel> cmsSiteModels = new ArrayList<CMSSiteModel>();
		final CMSSiteModel cmsSiteModel = new CMSSiteModel();
		cmsSiteModel.setUid(CUB_SITE);
		cmsSiteModels.add(cmsSiteModel);
		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		final CatalogModel catalogModel = Mockito.mock(CatalogModel.class);
		final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = Mockito
				.mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = Mockito
				.mock(SABMAlcoholVariantProductMaterialModel.class);
		final List<OrderEntryModel> entryModels = new ArrayList<OrderEntryModel>();
		final OrderEntryModel entryModel = new OrderEntryModel();
		final OrderEntryModel entryModel2 = new OrderEntryModel();
		entryModel.setQuantity(3L);
		entryModel2.setQuantity(3L);
		entryModels.add(entryModel);
		entryModels.add(entryModel2);
		final PlantModel plantModel = Mockito.mock(PlantModel.class);
		Mockito.when(cmsSiteService.getSites()).thenReturn(cmsSiteModels);
		Mockito.when(catalogVersionDeterminationStrategy.onlineCatalogVersion()).thenReturn(catalogVersionModel);
		Mockito.when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		Mockito.when(catalogModel.getId()).thenReturn("abcd");
		Mockito.when(catalogVersionModel.getVersion()).thenReturn("Online");
		Mockito.when(maxOrderQtyModel.getProduct()).thenReturn("12345");
		Mockito.when(productDao.findProductsByCode(catalogVersionModel, "12345"))
				.thenReturn(Arrays.asList(sabmAlcoholVariantProductEANModel));
		Mockito.when(sabmAlcoholVariantProductEANModel.getLeadSku()).thenReturn(sabmAlcoholVariantProductMaterialModel);
		Mockito.when(maxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.PLANT_RULE);
		Mockito.when(maxOrderQtyModel.getPlant()).thenReturn(plantModel);
		Mockito
				.when(sabmProductDao.getOrderEntryForPlantRule(sabmAlcoholVariantProductMaterialModel, plantModel, cmsSiteModel))
				.thenReturn(entryModels);

		final int avergaQty = sabmProductService.getAverageQuantity(maxOrderQtyModel);

		assertEquals(3, avergaQty);

	}

	@Test
	public void testGetAverageQuantityForGlobalRule()
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final List<CMSSiteModel> cmsSiteModels = new ArrayList<CMSSiteModel>();
		final CMSSiteModel cmsSiteModel = new CMSSiteModel();
		cmsSiteModel.setUid(CUB_SITE);
		cmsSiteModels.add(cmsSiteModel);
		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		final CatalogModel catalogModel = Mockito.mock(CatalogModel.class);
		final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = Mockito
				.mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = Mockito
				.mock(SABMAlcoholVariantProductMaterialModel.class);
		final List<OrderEntryModel> entryModels = new ArrayList<OrderEntryModel>();
		final OrderEntryModel entryModel = new OrderEntryModel();
		final OrderEntryModel entryModel2 = new OrderEntryModel();
		entryModel.setQuantity(3L);
		entryModel2.setQuantity(3L);
		entryModels.add(entryModel);
		entryModels.add(entryModel2);
		Mockito.when(cmsSiteService.getSites()).thenReturn(cmsSiteModels);
		Mockito.when(catalogVersionDeterminationStrategy.onlineCatalogVersion()).thenReturn(catalogVersionModel);
		Mockito.when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		Mockito.when(catalogModel.getId()).thenReturn("abcd");
		Mockito.when(catalogVersionModel.getVersion()).thenReturn("Online");
		Mockito.when(maxOrderQtyModel.getProduct()).thenReturn("12345");
		Mockito.when(productDao.findProductsByCode(catalogVersionModel, "12345"))
				.thenReturn(Arrays.asList(sabmAlcoholVariantProductEANModel));
		Mockito.when(sabmAlcoholVariantProductEANModel.getLeadSku()).thenReturn(sabmAlcoholVariantProductMaterialModel);
		Mockito.when(maxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.GLOBAL_RULE);
		Mockito
				.when(sabmProductDao.getOrderEntryForGlobalRule(sabmAlcoholVariantProductMaterialModel, cmsSiteModel))
				.thenReturn(entryModels);

		final int avergaQty = sabmProductService.getAverageQuantity(maxOrderQtyModel);

		assertEquals(3, avergaQty);

	}

	@Test
	public void testGetCustomerMaxOrderQuantity()
	{
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final List<MaxOrderQtyModel> maxOrderQtyModels = new ArrayList<MaxOrderQtyModel>();
		final B2BUnitModel unitModel = Mockito.mock(B2BUnitModel.class);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final B2BCustomerModel currentUserModel = Mockito.mock(B2BCustomerModel.class);
		final MaxOrderQtyModel customerMaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		maxOrderQtyModels.add(customerMaxOrderQtyModel);

		Mockito.when(customerMaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.CUSTOMER_RULE);
		Mockito.when(customerMaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(customerMaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(false);
		Mockito.when(customerMaxOrderQtyModel.getMaxOrderQty()).thenReturn(5);

		Mockito.when(productModel.getCode()).thenReturn("123456");
		Mockito.when(cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode("123456")).thenReturn(maxOrderQtyModels);
		Mockito.when(sabmCartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUserModel);
		Mockito.when(cartModel.getRequestedDeliveryDate()).thenReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		Mockito.when(currentUserModel.getDefaultB2BUnit()).thenReturn(unitModel);

		final MaxOrderQtyModel finalMaxQty = sabmProductService.getMaxOrderQuantity(productModel);

		assertEquals(customerMaxOrderQtyModel, finalMaxQty);
		assertEquals(Integer.valueOf(5), finalMaxQty.getMaxOrderQty());

	}

	@Test
	public void testGetCustomerDefaultMaxOrderQuantity()
	{
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final List<MaxOrderQtyModel> maxOrderQtyModels = new ArrayList<MaxOrderQtyModel>();
		final B2BUnitModel unitModel = Mockito.mock(B2BUnitModel.class);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final B2BCustomerModel currentUserModel = Mockito.mock(B2BCustomerModel.class);
		final MaxOrderQtyModel customerMaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		maxOrderQtyModels.add(customerMaxOrderQtyModel);

		Mockito.when(customerMaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.CUSTOMER_RULE);
		Mockito.when(customerMaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(customerMaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(true);
		Mockito.when(customerMaxOrderQtyModel.getDefaultAvgMaxOrderQty()).thenReturn(8);

		Mockito.when(productModel.getCode()).thenReturn("123456");
		Mockito.when(cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode("123456")).thenReturn(maxOrderQtyModels);
		Mockito.when(sabmCartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUserModel);
		Mockito.when(cartModel.getRequestedDeliveryDate()).thenReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		Mockito.when(currentUserModel.getDefaultB2BUnit()).thenReturn(unitModel);


		final MaxOrderQtyModel finalMaxQty = sabmProductService.getMaxOrderQuantity(productModel);

		assertEquals(customerMaxOrderQtyModel, finalMaxQty);
		assertEquals(Integer.valueOf(8), finalMaxQty.getDefaultAvgMaxOrderQty());

	}

	@Test
	public void testGetPlantMaxOrderQuantity()
	{
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final List<MaxOrderQtyModel> maxOrderQtyModels = new ArrayList<MaxOrderQtyModel>();
		final B2BUnitModel unitModel = Mockito.mock(B2BUnitModel.class);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final B2BCustomerModel currentUserModel = Mockito.mock(B2BCustomerModel.class);
		final MaxOrderQtyModel customerMaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final MaxOrderQtyModel plantmaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final PlantModel plantModel = Mockito.mock(PlantModel.class);
		maxOrderQtyModels.add(customerMaxOrderQtyModel);
		maxOrderQtyModels.add(plantmaxOrderQtyModel);

		Mockito.when(customerMaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.CUSTOMER_RULE);
		Mockito.when(customerMaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(customerMaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(false);
		Mockito.when(customerMaxOrderQtyModel.getMaxOrderQty()).thenReturn(0);

		Mockito.when(plantmaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.PLANT_RULE);
		Mockito.when(plantmaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(plantmaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(plantmaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(plantmaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(false);
		Mockito.when(plantmaxOrderQtyModel.getMaxOrderQty()).thenReturn(5);

		Mockito.when(productModel.getCode()).thenReturn("123456");
		Mockito.when(cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode("123456")).thenReturn(maxOrderQtyModels);
		Mockito.when(sabmCartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUserModel);
		Mockito.when(cartModel.getRequestedDeliveryDate()).thenReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		Mockito.when(currentUserModel.getDefaultB2BUnit()).thenReturn(unitModel);
		Mockito.when(unitModel.getPlant()).thenReturn(plantModel);
		Mockito.when(plantmaxOrderQtyModel.getPlant()).thenReturn(plantModel);


		assertEquals(plantmaxOrderQtyModel, sabmProductService.getMaxOrderQuantity(productModel));

	}

	@Test
	public void testGetGlobalMaxOrderQuantity()
	{
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final List<MaxOrderQtyModel> maxOrderQtyModels = new ArrayList<MaxOrderQtyModel>();
		final B2BUnitModel unitModel = Mockito.mock(B2BUnitModel.class);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final B2BCustomerModel currentUserModel = Mockito.mock(B2BCustomerModel.class);
		final MaxOrderQtyModel customerMaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final MaxOrderQtyModel plantmaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final MaxOrderQtyModel globalMaxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);
		final PlantModel plantModel = Mockito.mock(PlantModel.class);
		maxOrderQtyModels.add(customerMaxOrderQtyModel);
		maxOrderQtyModels.add(plantmaxOrderQtyModel);
		maxOrderQtyModels.add(globalMaxOrderQtyModel);

		Mockito.when(customerMaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.CUSTOMER_RULE);
		Mockito.when(customerMaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(customerMaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(customerMaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(false);
		Mockito.when(customerMaxOrderQtyModel.getMaxOrderQty()).thenReturn(0);

		Mockito.when(plantmaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.PLANT_RULE);
		Mockito.when(plantmaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(plantmaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(plantmaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(plantmaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(false);
		Mockito.when(plantmaxOrderQtyModel.getMaxOrderQty()).thenReturn(0);
		Mockito.when(unitModel.getPlant()).thenReturn(plantModel);
		Mockito.when(plantmaxOrderQtyModel.getPlant()).thenReturn(plantModel);

		Mockito.when(globalMaxOrderQtyModel.getRuleType()).thenReturn(MaxOrderQtyRuleType.GLOBAL_RULE);
		Mockito.when(globalMaxOrderQtyModel.getStartDate()).thenReturn(new Date());
		Mockito.when(globalMaxOrderQtyModel.getEndDate()).thenReturn(new Date());
		Mockito.when(globalMaxOrderQtyModel.getB2bunit()).thenReturn(unitModel);
		Mockito.when(globalMaxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled()).thenReturn(false);
		Mockito.when(globalMaxOrderQtyModel.getMaxOrderQty()).thenReturn(5);

		Mockito.when(productModel.getCode()).thenReturn("123456");
		Mockito.when(cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode("123456")).thenReturn(maxOrderQtyModels);
		Mockito.when(sabmCartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUserModel);
		Mockito.when(cartModel.getRequestedDeliveryDate()).thenReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		Mockito.when(currentUserModel.getDefaultB2BUnit()).thenReturn(unitModel);


		assertEquals(globalMaxOrderQtyModel, sabmProductService.getMaxOrderQuantity(productModel));

	}
}

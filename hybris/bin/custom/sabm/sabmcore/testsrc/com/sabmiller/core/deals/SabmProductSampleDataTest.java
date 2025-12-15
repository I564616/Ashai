/**
 *
 */
package com.sabmiller.core.deals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.impl.AbstractCatalogTest;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import com.sabmiller.core.enums.AlcoholCategoryAttribute;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.enums.DealVisibilityEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.model.SalesDataModel;

@IntegrationTest
public class SabmProductSampleDataTest extends AbstractCatalogTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private UnitService unitService;

	@Resource
	protected CatalogVersionService catalogVersionService;

	@Resource(name = "typeService")
	private TypeService typeService;

	@Resource
	private WarehouseService warehouseService;

	protected B2BUnitModel companyModel;

	protected CurrencyModel currencyModel;

	protected final Calendar fromCal = Calendar.getInstance();
	protected final Calendar specificCal = Calendar.getInstance();
	protected final Calendar toCal = Calendar.getInstance();
	protected final String dcp1 = "111";
	protected final String dcp2 = "1111";
	protected final String dcp3 = "11111";
	protected final String dcp4 = "111111";
	protected final String dealBenefitProduct = "222";
	protected final String brand1 = "CARLTON";
	protected final String brand2 = "VB";

	protected final String dealCode1 = "1";
	protected final String dealCode2 = "2";
	protected final String dealCode3 = "3";
	protected final String dealCode4 = "4";
	protected final String dealCode5 = "5";

	protected final String b2bUnitId = "0000794396";

	protected static final String SABM_PRODUCT_CATALOG = "sabmProductCatalog";


	@Resource(name = "userService")
	protected UserService userService;


	@Override
	public void setUp() throws Exception
	{
		//super.setUp();

		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/sabmfulfilmentprocess/test/testBasics.csv", "windows-1252");
		importCsv("/sabmfulfilmentprocess/test/testCatalog.csv", "windows-1252");

		userService.setCurrentUser(userService.getUserForUID("admin"));

		final CatalogModel catalogModel = modelService.create(CatalogModel.class);
		catalogModel.setName(SABM_PRODUCT_CATALOG);
		catalogModel.setId(SABM_PRODUCT_CATALOG);
		modelService.save(catalogModel);

		final CatalogVersionModel cvm = modelService.create(CatalogVersionModel.class);
		cvm.setCatalog(catalogModel);
		cvm.setVersion(CatalogManager.ONLINE_VERSION);
		modelService.save(cvm);


		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(SABM_PRODUCT_CATALOG, CatalogManager.ONLINE_VERSION);

		fromCal.add(Calendar.DATE, -7);
		toCal.add(Calendar.DATE, 1);
		specificCal.add(Calendar.DATE, -6);

		companyModel = new B2BUnitModel();
		companyModel.setUid(b2bUnitId);
		companyModel.setPayerId("0000794396");
		companyModel.setAccountGroup("ZADP");
		companyModel.setName("Test Org");
		setAddress(companyModel);
		setMembers(companyModel);
		modelService.save(companyModel);

		final SalesDataModel salesDataModel = modelService.create(SalesDataModel.class);
		salesDataModel.setDefaultDeliveryPlant("PL1");
		salesDataModel.setDeletionIndicator("D123");
		salesDataModel.setDistributionChannel("Ch124");
		salesDataModel.setDivision("DIV124");
		salesDataModel.setSalesOrgId("SO123");

		companyModel.setSalesData(salesDataModel);
		modelService.save(companyModel);

		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.add(createDealModel(dealCode1, companyModel, dcp1, true));
		deals.add(createDealModel(dealCode2, companyModel, dcp2, false));
		deals.add(createDealModel(dealCode3, companyModel, dcp3, true));
		deals.add(createDealModel(dealCode4, companyModel, dcp4, false));
		deals.add(createDealModel(dealCode5, companyModel, dcp1, true));

		companyModel.setDeals(deals);
		modelService.save(companyModel);

		final UserPriceGroup ug = UserPriceGroup.valueOf(companyModel.getUid());
		modelService.save(ug);

		currencyModel = modelService.create(CurrencyModel.class);
		currencyModel.setIsocode("AUD");
		currencyModel.setSymbol("AUD");
		currencyModel.setName("Australian Dollar");
		modelService.save(currencyModel);


		final BaseStoreModel store = modelService.create(BaseStoreModel.class);
		store.setDefaultCurrency(currencyModel);
		store.setUid("sabmStore");
		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("default");
		final VendorModel vendor = new VendorModel();
		vendor.setCode("default");
		warehouse.setVendor(vendor);
		store.setWarehouses(Arrays.asList(warehouse));
		modelService.save(store);

		final SABMAlcoholProductModel p1Model = new SABMAlcoholProductModel();
		p1Model.setBrand(brand1);
		p1Model.setCode(dcp1);
		p1Model.setAbv("12.4");
		p1Model.setCatalogVersion(catalogVersionModel);
		p1Model.setCategoryAttribute(AlcoholCategoryAttribute.BEER);
		p1Model.setCategoryVariety("v1");
		p1Model.setSubBrand("subbrand1");
		modelService.save(p1Model);

		final SABMAlcoholProductModel p2Model = new SABMAlcoholProductModel();
		p2Model.setBrand(brand2);
		p2Model.setCode(dcp2);
		p2Model.setAbv("12.4");
		p2Model.setCatalogVersion(catalogVersionModel);
		p2Model.setCategoryAttribute(AlcoholCategoryAttribute.BEER);
		p2Model.setCategoryVariety("v1");
		p2Model.setSubBrand("subbrand1");
		modelService.save(p2Model);


		catalogVersionService.setSessionCatalogVersions(Arrays.asList(new CatalogVersionModel[]
		{ catalogVersionModel }));
		userService.setCurrentUser(userService.getAnonymousUser());
	}

	protected SABMAlcoholVariantProductEANModel createMockProducts(final CatalogVersionModel catalogVersionModel,
			final String code)
	{
		final SABMAlcoholProductModel alcProdModel = modelService.create(SABMAlcoholProductModel.class);
		alcProdModel.setCode(code + "ALC");
		alcProdModel.setAbv("12.4");
		alcProdModel.setBrand(brand2);
		alcProdModel.setCatalogVersion(catalogVersionModel);
		alcProdModel.setCategoryAttribute(AlcoholCategoryAttribute.BEER);
		alcProdModel.setCategoryVariety("v1");
		alcProdModel
				.setVariantType((VariantTypeModel) typeService.getComposedTypeForCode(SABMAlcoholVariantProductEANModel._TYPECODE));
		alcProdModel.setSubBrand("subbrand1");
		modelService.save(alcProdModel);

		final SABMAlcoholVariantProductEANModel eanModel = modelService.create(SABMAlcoholVariantProductEANModel.class);
		eanModel.setCode(code + "EAN");
		eanModel.setContainer("Bottle");
		eanModel.setUnit(unitService.getUnitForCode("pieces"));
		eanModel.setCatalogVersion(catalogVersionModel);
		eanModel.setBaseProduct(alcProdModel);
		eanModel.setVariantType(
				(VariantTypeModel) typeService.getComposedTypeForCode(SABMAlcoholVariantProductMaterialModel._TYPECODE));
		modelService.save(eanModel);

		final SABMAlcoholVariantProductMaterialModel materialModel = modelService
				.create(SABMAlcoholVariantProductMaterialModel.class);
		materialModel.setCode(code);
		materialModel.setCatalogVersion(catalogVersionModel);
		materialModel.setBaseProduct(eanModel);
		materialModel.setContainer("Bottle");
		modelService.save(materialModel);

		return eanModel;
	}


	protected DealModel createDealModel(final String code, final B2BUnitModel companyModel, final String dealConditionProduct,
			final boolean inStore)
	{
		final DealModel dealModel = new DealModel();
		dealModel.setCode(code);
		dealModel.setDealType(DealTypeEnum.BOGOF);
		dealModel.setDealVisibility(DealVisibilityEnum.PUBLIC);
		dealModel.setValidFrom(fromCal.getTime());
		dealModel.setValidTo(toCal.getTime());
		dealModel.setB2bUnit(companyModel);
		dealModel.setInStore(Boolean.valueOf(inStore));

		modelService.save(dealModel);

		final ProductDealConditionModel conditionModel = new ProductDealConditionModel();
		conditionModel.setMinQty(3);
		conditionModel.setProductCode(dealConditionProduct);

		final FreeGoodsDealBenefitModel benefitModel = new FreeGoodsDealBenefitModel();
		benefitModel.setProductCode(dealBenefitProduct);
		benefitModel.setQuantity(1);

		final DealConditionGroupModel conditionGroupModel = new DealConditionGroupModel();
		conditionGroupModel.setDealConditions(Arrays.<AbstractDealConditionModel> asList(new ProductDealConditionModel[]
		{ conditionModel }));
		conditionGroupModel.setDealBenefits(Arrays.<AbstractDealBenefitModel> asList(new FreeGoodsDealBenefitModel[]
		{ benefitModel }));

		modelService.save(conditionModel);
		modelService.save(benefitModel);
		modelService.save(conditionGroupModel);

		dealModel.setConditionGroup(conditionGroupModel);
		modelService.save(dealModel);

		return dealModel;
	}

	protected void setAddress(final B2BUnitModel companyModel)
	{
		final CountryModel country = modelService.create(CountryModel.class);
		country.setIsocode("AU");
		country.setName("Australia");

		final RegionModel region = modelService.create(RegionModel.class);
		region.setIsocode("VIC");
		region.setCountry(country);
		region.setName("Victoria");
		region.setBusinessCode("CUB");
		final AddressModel address = getModelService().create(AddressModel.class);
		address.setFirstname(companyModel.getName());
		address.setLastname(companyModel.getName());
		address.setLine1("250 Elizabeth Street");
		address.setTown("Melbourne");
		address.setPostalcode("3000");
		address.setCountry(country);
		address.setRegion(region);
		address.setShippingAddress(Boolean.TRUE);
		address.setContactAddress(Boolean.TRUE);
		address.setOwner(companyModel);

		modelService.saveAll(country, region, address);
	}

	protected void setMembers(final B2BUnitModel companyModel)
	{
		final UserModel userModel = modelService.create(UserModel.class);
		userModel.setName("Joshua Antony");
		userModel.setPrimaryAdmin(Boolean.TRUE);
		userModel.setUid("joshua.a.antony@accenture.com");
		modelService.save(userModel);

		final Set<PrincipalModel> users = new HashSet<PrincipalModel>();
		users.add(userModel);

		companyModel.setMembers(users);

	}

	public ModelService getModelService()
	{
		return modelService;
	}
}

/**
 *
 */
package com.sabmiller.core.deals.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertTrue;
import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.deals.SabmProductSampleDataTest;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;


/**
 * Integration test cases for DealsService
 *
 * @author joshua.a.antony
 */
@IntegrationTest
public class DealsServiceTest extends SabmProductSampleDataTest
{
	@Resource
	private DealsService dealsService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	private final Date today = new Date();

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}



	/**
	 * If there are no deals for company, make sure that deal is created and associated with the B2BUnit
	 *
	 * This has been tested by running SOAP-UI service in local box with response :
	 * <ns0:PricingBOGOFDealsResponse xmlns:ns0="urn:gl.sabmiller.com:com:ecc:mastdata">
	 * <PricingBOGOFDealsResponseHeader> <Customer>1234</Customer>
	 * <SalesOrganisation>123</SalesOrganisation> </PricingBOGOFDealsResponseHeader>
	 *
	 * <PricingBOGOFDealsResponseItem> <ConditionType>DDY00</ConditionType> <Material>DYNJ089202</Material>
	 * <MinimumQuantity>5</MinimumQuantity> <FreeGoodsQty>1</FreeGoodsQty> <UnitOfMeasure>CS</UnitOfMeasure>
	 * <AdditionalQtyFreeGoods>2</AdditionalQtyFreeGoods> <AdditionalUnitOfMeasure>CS</AdditionalUnitOfMeasure>
	 * <Rule></Rule> <AdditionalMaterial>DYND80000</AdditionalMaterial> <ValidFrom>2015-10-09</ValidFrom>
	 * <ValidTo>2015-11-10</ValidTo> </PricingBOGOFDealsResponseItem> </ns0:PricingBOGOFDealsResponse>
	 */
	@Test
	public void testRefreshBogofDealsIfNoDealsExist()
	{
		//Clear out the existing deals created by the setup method
		companyModel.setDeals(new ArrayList<DealModel>());
		getModelService().save(companyModel);

		//dealsService.refreshDeals(companyModel);

		assertBogofDealsOutcome(0);
	}

	/**
	 * If other deals already exist for the company, check if the newly added deal sticks to the end
	 *
	 * This has been tested by running SOAP-UI service in local box with response :
	 * <ns0:PricingBOGOFDealsResponse xmlns:ns0="urn:gl.sabmiller.com:com:ecc:mastdata">
	 * <PricingBOGOFDealsResponseHeader> <Customer>1234</Customer>
	 * <SalesOrganisation>123</SalesOrganisation> </PricingBOGOFDealsResponseHeader>
	 *
	 * <PricingBOGOFDealsResponseItem> <ConditionType>DDY00</ConditionType> <Material>DYNJ089202</Material>
	 * <MinimumQuantity>5</MinimumQuantity> <FreeGoodsQty>1</FreeGoodsQty> <UnitOfMeasure>CS</UnitOfMeasure>
	 * <AdditionalQtyFreeGoods>2</AdditionalQtyFreeGoods> <AdditionalUnitOfMeasure>CS</AdditionalUnitOfMeasure>
	 * <Rule></Rule> <AdditionalMaterial>DYND80000</AdditionalMaterial> <ValidFrom>2015-10-09</ValidFrom>
	 * <ValidTo>2015-11-10</ValidTo> </PricingBOGOFDealsResponseItem> </ns0:PricingBOGOFDealsResponse>
	 */
	@Test
	public void testRefreshBogofDealsIfDealsAlreadyExist()
	{
		//dealsService.refreshDeals(companyModel);

		assertBogofDealsOutcome(5);
	}

	private void assertBogofDealsOutcome(final int dealSize)
	{
		assertEquals(dealSize, companyModel.getDeals().size());

		final DealModel newlyAddedDeal = companyModel.getDeals().get(0);
		assertEquals(DealTypeEnum.BOGOF, newlyAddedDeal.getDealType());

		//Test the deal conditions
		final ProductDealConditionModel productDealConditionModel = (ProductDealConditionModel) companyModel.getDeals()
				.get(dealSize - 1).getConditionGroup().getDealConditions().get(0);
		assertEquals("111", productDealConditionModel.getProductCode());
		assertEquals(Integer.valueOf(3), productDealConditionModel.getMinQty());
		assertNull(productDealConditionModel.getQuantity());

		//Test the deal benefits
		final FreeGoodsDealBenefitModel freeGoodDealBenefitModel = (FreeGoodsDealBenefitModel) companyModel.getDeals()
				.get(dealSize - 1).getConditionGroup().getDealBenefits().get(0);
		assertEquals("222", freeGoodDealBenefitModel.getProductCode());
		assertEquals(Integer.valueOf(1), freeGoodDealBenefitModel.getQuantity());

		//Finally verify that the BOGOF is marked as obsolete
		assertTrue(b2bUnitService.isBOGOFDealsObsolete(companyModel, new Date()));
	}

	/**
	 * This has been tested by running SOAP-UI service in local box with response :
	 * <PricingDiscountConditionsResponse> <PricingDiscountConditionsHeader> <Customer>1234</Customer>
	 * <SalesOrganisation>123</SalesOrganisation> </PricingDiscountConditionsHeader>
	 *
	 * <PricingDiscountConditionsItem> <Material>DYNJ089202</Material> <ConditionType>DD00</ConditionType>
	 * <Amount>20</Amount> <Unit>%</Unit> <SaleUnit>1</SaleUnit> <UnitOfMeasure>CS</UnitOfMeasure>
	 * <MinimunQuantity>3</MinimunQuantity> <UnitOfMeasure2>CS</UnitOfMeasure2> <ValidFrom>01/11/2015</ValidFrom>
	 * <ValidTo>02/12/2015</ValidTo>
	 * <CalcType>CT</CalcType> </PricingDiscountConditionsItem> </PricingDiscountConditionsResponse>
	 */
	@Test
	public void testRefreshDiscountDeals()
	{
		//Clear out the existing deals created by the setup method
		companyModel.setDeals(new ArrayList<DealModel>());
		getModelService().save(companyModel);

		//dealsService.refreshDeals(companyModel);

		assertEquals(0, companyModel.getDeals().size());

		final DealModel newlyAddedDeal = companyModel.getDeals().get(0);
		assertEquals(DealTypeEnum.DISCOUNT, newlyAddedDeal.getDealType());

		//Test the deal conditions
		final ProductDealConditionModel productDealConditionModel = (ProductDealConditionModel) companyModel.getDeals().get(0)
				.getConditionGroup().getDealConditions().get(0);
		assertEquals("DYNJ089202", productDealConditionModel.getProductCode());
		assertEquals(Integer.valueOf(3), productDealConditionModel.getQuantity());
		assertNull(productDealConditionModel.getMinQty()); //No min quantity trigger for discount deals (applicable only for BOGOF)

		//Test the deal benefits
		final DiscountDealBenefitModel discountDealBenefitModel = (DiscountDealBenefitModel) companyModel.getDeals().get(0)
				.getConditionGroup().getDealBenefits().get(0);
		assertEquals(Double.valueOf(20), discountDealBenefitModel.getAmount());
		assertEquals(Integer.valueOf(1), discountDealBenefitModel.getSaleUnit());
		assertFalse(discountDealBenefitModel.getCurrency());


		//Finally verify that the Discount deal is marked as obsolete
		assertFalse(b2bUnitService.isDiscountDealsObsolete(companyModel, new Date()));
	}

	@Test
	public void testComposeComplexFreeProducts()
	{
		final List<DealModel> dealModels = new ArrayList<DealModel>();
		final DealModel dealModel1 = new DealModel();
		final DealModel dealModel2 = new DealModel();
		final DealModel dealModel3 = new DealModel();
		final DealModel dealModel4 = new DealModel();
		final DealModel dealModel5 = new DealModel();
		final DealModel dealModel6 = new DealModel();

		final List<AbstractDealBenefitModel> freeGoodsDealBenefitModels1 = new ArrayList<AbstractDealBenefitModel>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefit1 = mock(FreeGoodsDealBenefitModel.class);
		freeGoodsDealBenefitModels1.add(freeGoodsDealBenefit1);
		dealModel1.setCode("dealCode1");
		dealModel1.setTriggerHash("triggerHash1");
		dealModel1.setDealType(DealTypeEnum.COMPLEX);
		final DealConditionGroupModel dealConditionGroupModel1 = new DealConditionGroupModel();
		dealConditionGroupModel1.setDealBenefits(freeGoodsDealBenefitModels1);
		dealModel1.setConditionGroup(dealConditionGroupModel1);
		dealModels.add(dealModel1);

		final List<AbstractDealBenefitModel> freeGoodsDealBenefitModels2 = new ArrayList<AbstractDealBenefitModel>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefit2 = mock(FreeGoodsDealBenefitModel.class);
		freeGoodsDealBenefitModels2.add(freeGoodsDealBenefit2);
		dealModel2.setCode("dealCode2");
		dealModel2.setTriggerHash("triggerHash1");
		dealModel2.setDealType(DealTypeEnum.COMPLEX);
		final DealConditionGroupModel dealConditionGroupModel2 = new DealConditionGroupModel();
		dealConditionGroupModel2.setDealBenefits(freeGoodsDealBenefitModels2);
		dealModel2.setConditionGroup(dealConditionGroupModel2);
		dealModels.add(dealModel2);

		final List<AbstractDealBenefitModel> freeGoodsDealBenefitModels3 = new ArrayList<AbstractDealBenefitModel>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefit3 = mock(FreeGoodsDealBenefitModel.class);
		freeGoodsDealBenefitModels3.add(freeGoodsDealBenefit3);
		dealModel3.setCode("dealCode3");
		dealModel3.setTriggerHash("triggerHash2");
		dealModel3.setDealType(DealTypeEnum.COMPLEX);
		final DealConditionGroupModel dealConditionGroupModel3 = new DealConditionGroupModel();
		dealConditionGroupModel3.setDealBenefits(freeGoodsDealBenefitModels3);
		dealModel3.setConditionGroup(dealConditionGroupModel3);
		dealModels.add(dealModel3);

		final List<AbstractDealBenefitModel> freeGoodsDealBenefitModels4 = new ArrayList<AbstractDealBenefitModel>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefit4 = mock(FreeGoodsDealBenefitModel.class);
		freeGoodsDealBenefitModels4.add(freeGoodsDealBenefit4);
		dealModel4.setCode("dealCode4");
		dealModel4.setTriggerHash("triggerHash2");
		dealModel4.setDealType(DealTypeEnum.COMPLEX);
		final DealConditionGroupModel dealConditionGroupModel4 = new DealConditionGroupModel();
		dealConditionGroupModel4.setDealBenefits(freeGoodsDealBenefitModels4);
		dealModel4.setConditionGroup(dealConditionGroupModel4);
		dealModels.add(dealModel4);

		final List<AbstractDealBenefitModel> freeGoodsDealBenefitModels5 = new ArrayList<AbstractDealBenefitModel>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefit5 = mock(FreeGoodsDealBenefitModel.class);
		freeGoodsDealBenefitModels5.add(freeGoodsDealBenefit5);
		dealModel5.setCode("dealCode5");
		dealModel5.setTriggerHash("triggerHash3");
		dealModel5.setDealType(DealTypeEnum.COMPLEX);
		final DealConditionGroupModel dealConditionGroupModel5 = new DealConditionGroupModel();
		dealConditionGroupModel5.setDealBenefits(freeGoodsDealBenefitModels5);
		dealModel5.setConditionGroup(dealConditionGroupModel5);
		dealModels.add(dealModel5);

		dealModel6.setCode("dealCode6");
		dealModel6.setTriggerHash("triggerHash3");
		dealModel6.setDealType(DealTypeEnum.COMPLEX);
		dealModels.add(dealModel6);


		//final List<DealModel> composedDeals = dealsService.composeComplexFreeProducts(dealModels);
		//Assert.assertEquals(3, composedDeals.size());
		//Assert.assertEquals("dealCode1", composedDeals.get(0).getCode());
		//Assert.assertEquals(2, composedDeals.get(1).getConditionGroup().getDealBenefits().size());

		//		for (final DealModel dealModel : composedDeals)
		//		{
		//			System.out.println(dealModel.getCode() + "  " + (dealModel.getConditionGroup().getDealBenefits() == null ? 0
		//					: dealModel.getConditionGroup().getDealBenefits().size()));
		//		}

	}
}

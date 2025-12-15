/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.MinQtyDealConditionModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.facades.deal.data.DealData;


/**
 * SABMDealPopulatorTest
 *
 * @author xiaowu.a.zhang
 * @data 2015-12-04
 */
@UnitTest
public class SABMDealPopulatorTest
{
	private SABMDealPopulator sabmDealPopulator;
	@Mock
	private SABMAlcoholProductPopulator sabmAlcoholProductPopulator;
	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;
	@Mock
	private ProductService productService;

	@Mock
	private ProductFacade productFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sabmDealPopulator = new SABMDealPopulator();
		sabmDealPopulator.setProductService(productService);
		sabmDealPopulator.setProductFacade(productFacade);
		sabmDealPopulator.setSabmAlcoholProductPopulator(sabmAlcoholProductPopulator);
	}

	@Test
	public void testPopulator()
	{
		final DealModel dealModel = mock(DealModel.class);
		final DealConditionGroupModel dealConditionGroupModel = mock(DealConditionGroupModel.class);
		final ProductDealConditionModel productDealConditionModel = mock(ProductDealConditionModel.class);
		final MinQtyDealConditionModel minQtyDealConditionModel = mock(MinQtyDealConditionModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel = mock(FreeGoodsDealBenefitModel.class);
		final DiscountDealBenefitModel discountDealBenefitModel = mock(DiscountDealBenefitModel.class);
		final UnitModel unitModel1 = mock(UnitModel.class);
		final UnitModel unitModel2 = mock(UnitModel.class);
		final UnitModel unitModel3 = mock(UnitModel.class);
		final List<AbstractDealConditionModel> conditionModels = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitModels = new ArrayList<AbstractDealBenefitModel>();

		given(unitModel1.getCode()).willReturn("unitCode1");
		given(unitModel1.getName()).willReturn("unitName1");
		given(unitModel2.getCode()).willReturn("unitCode2");
		given(unitModel2.getName()).willReturn("unitName2");
		given(unitModel3.getCode()).willReturn("unitCode3");
		given(unitModel3.getName()).willReturn("unitName3");
		given(productDealConditionModel.getMinQty()).willReturn(Integer.valueOf(5));
		given(productDealConditionModel.getUnit()).willReturn(unitModel1);
		given(minQtyDealConditionModel.getMinQty()).willReturn(Integer.valueOf(6));
		given(minQtyDealConditionModel.getUnit()).willReturn(unitModel2);
		given(freeGoodsDealBenefitModel.getQuantity()).willReturn(Integer.valueOf(7));
		given(freeGoodsDealBenefitModel.getUnit()).willReturn(unitModel3);
		given(discountDealBenefitModel.getCurrency()).willReturn(Boolean.valueOf(true));
		given(discountDealBenefitModel.getAmount()).willReturn(Double.valueOf(10));
		given(discountDealBenefitModel.getSaleUnit()).willReturn(Integer.valueOf(3));
		given(discountDealBenefitModel.getUnit()).willReturn(unitModel1);
		conditionModels.add(productDealConditionModel);
		conditionModels.add(minQtyDealConditionModel);
		benefitModels.add(freeGoodsDealBenefitModel);
		benefitModels.add(discountDealBenefitModel);
		given(dealConditionGroupModel.getDealConditions()).willReturn(conditionModels);
		given(dealConditionGroupModel.getDealBenefits()).willReturn(benefitModels);
		given(dealModel.getConditionGroup()).willReturn(dealConditionGroupModel);

		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date date = null;

		try
		{
			date = sdf.parse("12-04-2015");
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}

		calendar.setTime(date);

		final Date validFrom = calendar.getTime();
		calendar.add(Calendar.MONTH, 12);
		final Date validTo = calendar.getTime();

		final PK pk = PK.parse("12345");
		given(dealModel.getPk()).willReturn(pk);
		given(dealModel.getValidFrom()).willReturn(validFrom);
		given(dealModel.getValidTo()).willReturn(validTo);
		given(dealModel.getDealType()).willReturn(DealTypeEnum.DISCOUNT);

		final DealData dealData = new DealData();
		sabmDealPopulator.populate(dealModel, dealData);
		Assert.assertEquals("DISCOUNT", dealData.getDealType());
		Assert.assertEquals("04/12/2015", dealData.getValidFrom());
		Assert.assertEquals("04/12/2016", dealData.getValidTo());
		Assert.assertEquals("PRODUCTCONDITION", dealData.getDealConditionGroupData().getDealConditions().get(0).getConditionType());
		Assert.assertEquals(Integer.valueOf(5), dealData.getDealConditionGroupData().getDealConditions().get(0).getMinQty());
		Assert.assertEquals("unitCode1", dealData.getDealConditionGroupData().getDealConditions().get(0).getUnit().getCode());
		Assert.assertEquals("unitName1", dealData.getDealConditionGroupData().getDealConditions().get(0).getUnit().getName());
		Assert.assertEquals("MINQTYCONDITION", dealData.getDealConditionGroupData().getDealConditions().get(1).getConditionType());
		Assert.assertEquals(Integer.valueOf(6), dealData.getDealConditionGroupData().getDealConditions().get(1).getMinQty());
		Assert.assertEquals("unitCode2", dealData.getDealConditionGroupData().getDealConditions().get(1).getUnit().getCode());
		Assert.assertEquals("unitName2", dealData.getDealConditionGroupData().getDealConditions().get(1).getUnit().getName());
		Assert.assertEquals("FREEGOODSBENEFIT", dealData.getDealConditionGroupData().getDealBenefits().get(0).getBenefitType());
		Assert.assertEquals(Integer.valueOf(7), dealData.getDealConditionGroupData().getDealBenefits().get(0).getQuantity());
		Assert.assertEquals("unitCode3", dealData.getDealConditionGroupData().getDealBenefits().get(0).getUnit().getCode());
		Assert.assertEquals("unitName3", dealData.getDealConditionGroupData().getDealBenefits().get(0).getUnit().getName());
		Assert.assertEquals("DISCOUNTBENEFIT", dealData.getDealConditionGroupData().getDealBenefits().get(1).getBenefitType());
		Assert.assertEquals(Boolean.valueOf(true), dealData.getDealConditionGroupData().getDealBenefits().get(1).getCurrency());
		Assert.assertEquals(Double.valueOf(10), dealData.getDealConditionGroupData().getDealBenefits().get(1).getAmount());
		Assert.assertEquals(Integer.valueOf(3), dealData.getDealConditionGroupData().getDealBenefits().get(1).getSaleUnit());
		Assert.assertEquals("unitCode1", dealData.getDealConditionGroupData().getDealBenefits().get(1).getUnit().getCode());
		Assert.assertEquals("unitName1", dealData.getDealConditionGroupData().getDealBenefits().get(1).getUnit().getName());
	}
}

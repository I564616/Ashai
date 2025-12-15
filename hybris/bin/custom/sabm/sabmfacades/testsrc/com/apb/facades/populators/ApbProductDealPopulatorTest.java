/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.model.ApbProductModel;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;


/**
 * @author Ranjith.Karuvachery
 *
 */
public class ApbProductDealPopulatorTest
{


	@InjectMocks
	private final ApbProductDealPopulator apbProductDealPopulator = new ApbProductDealPopulator();

	@Mock
	private AsahiCoreUtil asahiCoreUtil;
	@Mock
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock
	private SabmB2BUnitService b2bUnitService;

	@Mock
	private UserService userService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(asahiCoreUtil.isNAPUser()).thenReturn(false);
	}

	@Test
	public void testPopulate()
	{
		final B2BCustomerModel customerModel = Mockito.mock(B2BCustomerModel.class);
		final B2BUnitModel b2BUnitModel = Mockito.mock(B2BUnitModel.class);
		final ProductData productData = new ProductData();
		productData.setCode("123456");
		final List<AsahiDealData> asahiDealsInfo = new ArrayList<AsahiDealData>();
		final AsahiDealData asahiDealData = Mockito.mock(AsahiDealData.class);
		final ApbProductModel productModel = Mockito.mock(ApbProductModel.class);
		asahiDealsInfo.add(asahiDealData);
		Mockito.when(asahiDealData.getTitle()).thenReturn("Buy 2 ABC and get 1 DEF");
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(true);
		Mockito.when(userService.getCurrentUser()).thenReturn(customerModel);
		Mockito.when(b2bUnitService.getParent(customerModel)).thenReturn(b2BUnitModel);
		Mockito.when(sabmDealsSearchFacade.getSGADealsDataForProductAndUnit("123456", b2BUnitModel)).thenReturn(asahiDealsInfo);

		apbProductDealPopulator.populate(productModel, productData);

		Assert.assertEquals("Buy 2 ABC and get 1 DEF", productData.getDealsTitle().get(0));
	}

}

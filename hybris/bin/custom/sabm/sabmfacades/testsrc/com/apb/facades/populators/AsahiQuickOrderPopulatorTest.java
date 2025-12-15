/**
 *
 */
package com.apb.facades.populators;

import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.services.cart.ApbProductStockInCartEntryService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.facades.product.data.PackageSizeData;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;


/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class AsahiQuickOrderPopulatorTest
{

	@InjectMocks
	private final AsahiQuickOrderPopulator asahiQuickOrderPopulator = new AsahiQuickOrderPopulator();

	@Mock
	private Converter<PackageSizeModel, PackageSizeData> apbPackageSizeConverter;

	@Mock
	private Converter<MediaModel, ImageData> imageConverter;

	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock
	private ApbProductStockInCartEntryService apbProductStockInCartEntryService;

	@Mock
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Mock
	private ApbB2BUnitService apbB2BUnitService;

	@Mock
	private ApbStockPopulator<ProductModel, StockData> apbStockPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		asahiQuickOrderPopulator.setApbPackageSizeConverter(apbPackageSizeConverter);
		asahiQuickOrderPopulator.setImageConverter(imageConverter);
		asahiQuickOrderPopulator.setProductModelUrlResolver(productModelUrlResolver);
		Mockito.when(asahiSiteUtil.getSgaGlobalMaxOrderQty()).thenReturn(Long.valueOf("50"));
		Mockito.when(apbProductStockInCartEntryService.getProductQtyFromCart(Mockito.anyString())).thenReturn(Long.valueOf("10"));
		final AsahiDealData dealData = new AsahiDealData();
		dealData.setTitle("Buy 2 X get 1 X FREE");
		Mockito.when(sabmDealsSearchFacade.getSGADealsDataForProductAndUnit(Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(dealData));
	}

	@Test
	public void testPopulate()
	{
		final OrderModel order = Mockito.mock(OrderModel.class);
		Mockito.when(order.getCreationtime()).thenReturn(new Date());
		final OrderEntryModel orderEntry = Mockito.mock(OrderEntryModel.class);
		Mockito.when(orderEntry.getCreationtime()).thenReturn(new Date());
		Mockito.when(orderEntry.getIsBonusStock()).thenReturn(Boolean.FALSE);
		Mockito.when(orderEntry.getQuantity()).thenReturn(Long.valueOf("1"));
		final ApbProductModel product = Mockito.mock(ApbProductModel.class);
		Mockito.when(orderEntry.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("1000000");
		Mockito.when(product.getName()).thenReturn("Lipton Ice Tea");
		final BrandModel brand = Mockito.mock(BrandModel.class);
		final PackageSizeModel packageSize = Mockito.mock(PackageSizeModel.class);
		final UnitVolumeModel portalUnitVolumeModel = Mockito.mock(UnitVolumeModel.class);
		Mockito.when(product.getBrand()).thenReturn(brand);
		Mockito.when(product.getPackageSize()).thenReturn(packageSize);
		Mockito.when(product.getPortalUnitVolume()).thenReturn(portalUnitVolumeModel);
		Mockito.when(order.getCode()).thenReturn("ocode");
		Mockito.when(order.getEntries()).thenReturn(Arrays.asList(orderEntry));
		final AsahiQuickOrderData quickOrderData = new AsahiQuickOrderData();
		asahiQuickOrderPopulator.populate(Arrays.asList(order), quickOrderData);
		Assert.assertNotNull(quickOrderData.getEntries());
		Mockito.verify(apbB2BUnitService, times(1)).getCurrentB2BUnit();
	}
}

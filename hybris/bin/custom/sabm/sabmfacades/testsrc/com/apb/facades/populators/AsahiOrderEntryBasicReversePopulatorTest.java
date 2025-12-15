/**
 *
 */
package com.apb.facades.populators;

import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;


/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class AsahiOrderEntryBasicReversePopulatorTest
{

	@InjectMocks
	private final AsahiOrderEntryBasicReversePopulator asahiOrderEntryBasicReversePopulator = new AsahiOrderEntryBasicReversePopulator();

	@Mock
	private ApbProductReferenceService apbProductReferenceService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private PersistentKeyGenerator entryNumberKeyGenerator;

	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	@Mock
	private ProductService productService;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final OrderEntryData source = new OrderEntryData();
		source.setCompanyCode("sga");
		source.setQuantity(Long.valueOf("1"));
		source.setBackendUid("orderCode");
		source.setLineNum("1");
		source.setIsBonusStock(Boolean.TRUE);
		source.setInventoryTransId("inventoryId");
		source.setPickinglistQty(Integer.valueOf(1));
		final ProductData product = new ProductData();
		product.setCode("pcode");
		source.setProduct(product);
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		Mockito.when(productModel.getUnit()).thenReturn(Mockito.mock(UnitModel.class));
		Mockito.when(apbProductReferenceService.getProductForCode(Mockito.any(), Mockito.anyString()))
				.thenReturn(productModel);
		Mockito.when(entryNumberKeyGenerator.generate()).thenReturn(UUID.randomUUID());
		Mockito.when(entryNumberKeyGenerator.generate().toString()).thenReturn("1");
		asahiOrderEntryBasicReversePopulator.populate(source, new AbstractOrderEntryModel());
		Mockito.verify(modelService, times(1)).save(Mockito.any());
	}
}

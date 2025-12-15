/**
 *
 */
package com.apb.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.OrderTemplateData;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.cart.impl.AsahiSaveCartFacadeImpl;
import com.sabmiller.core.cart.dao.DefaultSabmCommerceCartDao;
import com.sabmiller.core.cart.service.SABMB2BCommerceCartService;
import com.sabmiller.core.model.AsahiB2BUnitModel;

/***
 * 
 * @author Ranjith.Karuvachery
 *
 */


@UnitTest
public class AsahiSaveCartFacadeImplTest
{

	final private static String QUICK_ORDER_TEMPLETE_PREFIX = "Quick Order";
	@InjectMocks
	private AsahiSaveCartFacadeImpl asahiSaveCartFacadeImpl = new AsahiSaveCartFacadeImpl();

	@Mock(name = "defaultSabmB2BCommerceCartService")
	private SABMB2BCommerceCartService sabMB2BCommerceCartService;

	@Mock
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	
	@Mock
	private UserService userService;
	
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private Converter<OrderTemplateModel, OrderTemplateData> asahiOrderTemplateConverter;
	
	@Mock(name = "defaultSabmCommerceCartDao")
	private DefaultSabmCommerceCartDao defaultSabmCommerceCartDao;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		asahiSaveCartFacadeImpl.setUserService(userService);
		asahiSaveCartFacadeImpl.setAsahiOrderTemplateConverter(asahiOrderTemplateConverter);
		asahiSaveCartFacadeImpl.setModelService(modelService);;
	}

	
	@Test
	public void testGetAllSavedCartsForCurrentUserB2BUnit() {
		final B2BCustomerModel customer = Mockito.mock(B2BCustomerModel.class);
		final AsahiB2BUnitModel b2bUnitModel = Mockito.mock(AsahiB2BUnitModel.class);
		final List<OrderTemplateModel> orderTemplateModels = new ArrayList<OrderTemplateModel>();
		final List<OrderTemplateData> orderTemplateData = new ArrayList<>();
		final OrderTemplateModel orderTemplateModel = Mockito.mock(OrderTemplateModel.class);
		orderTemplateModels.add(orderTemplateModel);
		final OrderTemplateData orderTempData = Mockito.mock(OrderTemplateData.class);
		orderTemplateData.add(orderTempData);
		
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getDefaultB2BUnit()).thenReturn(b2bUnitModel);
		when(orderTemplateModel.getName()).thenReturn("abc");
		when(asahiSiteUtil.isSga()).thenReturn(false);
		when(sabMB2BCommerceCartService.getAllSavedCartForB2BUnit(b2bUnitModel)).thenReturn(orderTemplateModels);
		when(asahiOrderTemplateConverter.convert(orderTemplateModel)).thenReturn(orderTempData);
		
		assertEquals(orderTemplateData, asahiSaveCartFacadeImpl.getAllSavedCartsForCurrentUserB2BUnit());
	}
	
	@Test
	public void testSaveOrderTemplate() {
		final B2BCustomerModel customer = Mockito.mock(B2BCustomerModel.class);
		final AsahiB2BUnitModel b2bUnitModel = Mockito.mock(AsahiB2BUnitModel.class);
		final OrderTemplateModel orderTemplateModel = Mockito.mock(OrderTemplateModel.class);
		final List<OrderTemplateEntryModel> entries = new ArrayList<OrderTemplateEntryModel>();
		final PK pk = PK.parse("12345678");
		OrderTemplateEntryModel orderTemplateEntryModel = Mockito.mock(OrderTemplateEntryModel.class);
		entries.add(orderTemplateEntryModel);
		
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getDefaultB2BUnit()).thenReturn(b2bUnitModel);
		when(defaultSabmCommerceCartDao.getOrderTemplateForCodeAndB2BUnit(QUICK_ORDER_TEMPLETE_PREFIX, b2bUnitModel)).thenReturn(orderTemplateModel);
		when(orderTemplateModel.getTemplateEntry()).thenReturn(entries);
		when(orderTemplateEntryModel.getPk()).thenReturn(pk);
		when(orderTemplateEntryModel.getTotalPrice()).thenReturn(20D);
		
		assertTrue(asahiSaveCartFacadeImpl.saveOrderTemplate(QUICK_ORDER_TEMPLETE_PREFIX, Map.ofEntries(Map.entry(pk.toString(),5L))));
	}
}

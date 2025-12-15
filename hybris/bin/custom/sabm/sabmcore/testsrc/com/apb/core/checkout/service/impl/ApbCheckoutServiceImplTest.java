/**
 *
 */
package com.apb.core.checkout.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.bdeordering.BDEOrderEmailForm;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApbCheckoutServiceImplTest
{
	@Spy
	@InjectMocks
	private final ApbCheckoutServiceImpl apbCheckoutServiceImpl = new ApbCheckoutServiceImpl();

	@Mock
	private AsahiB2BUnitModel b2bUnitModel;
	@Mock
	private CartService cartService;
	@Mock
	private B2BCustomerModel member;
	@Mock
	private CartModel cartModel;
	@Mock
	private GenericDao<EmployeeModel> employeeDao;
	@Mock
	private EmployeeModel employee;
	@Mock
	private BdeOrderDetailsForm bdeCheckoutForm;
	@Mock
	private BDEOrderEmailForm customer, user;
	@Mock
	private UserService userService;
	@Mock
	private ModelService modelService;
	@Mock
	private BDECustomerModel bdeCustomer;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Test
	public void getCustomerEmailIdsTest()
	{
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartModel.getUnit()).thenReturn(b2bUnitModel);
		when(b2bUnitModel.getMembers()).thenReturn(Collections.singleton(member));
		Mockito.lenient().when(member.getUid()).thenReturn("memberId");
		when(cartModel.getBdeOrderCustomerEmails()).thenReturn(Collections.singletonList("bdeCustomerId"));
		assertEquals(1, apbCheckoutServiceImpl.getCustomerEmailIds().size());
	}

	@Test
	public void searchBDEByNameTest()
	{
		when(employeeDao.find(Collections.singletonMap("name", "bdeName"))).thenReturn(Collections.singletonList(employee));
		assertEquals(employee, apbCheckoutServiceImpl.searchBDEByName("bdeName"));
	}

	@Test
	public void searchBDEByNameNullTest()
	{
		when(employeeDao.find(Collections.singletonMap("name", "bdeName"))).thenReturn(Collections.emptyList());
		assertNull(apbCheckoutServiceImpl.searchBDEByName("bdeName"));
	}

	@Test
	public void saveBDEOrderDetailsTest()
	{
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(bdeCheckoutForm.getCustomers()).thenReturn(Collections.singletonList(customer));
		when(customer.getEmail()).thenReturn("customerMail");
		when(bdeCheckoutForm.getUsers()).thenReturn(Collections.singletonList(user));
		when(user.getEmail()).thenReturn("userMail");
		when(userService.getUserForUID("userMail")).thenReturn(employee);
		when(employee.getName()).thenReturn("EmployeeName");
		when(bdeCheckoutForm.getEmailText()).thenReturn("Email Text");
		doNothing().when(modelService).save(cartModel);
		doNothing().when(modelService).refresh(cartModel);
		apbCheckoutServiceImpl.saveBDEOrderDetails(bdeCheckoutForm);
		Mockito.verify(modelService).save(cartModel);
	}

	@Test
	public void setDeviceTypeTest()
	{
		when(asahiSiteUtil.isBDECustomer()).thenReturn(true);
		apbCheckoutServiceImpl.setDeviceType(cartModel);
		Mockito.verify(cartModel).setDeviceType("StaffPortal");
	}
}

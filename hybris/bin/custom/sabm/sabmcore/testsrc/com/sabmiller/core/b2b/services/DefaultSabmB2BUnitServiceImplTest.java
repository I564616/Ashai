/**
 *
 */
package com.sabmiller.core.b2b.services;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.dao.SabmB2BUnitDao;

/**
 * DefaultSabmB2BUnitServiceImplTest
 */
@UnitTest
public class DefaultSabmB2BUnitServiceImplTest {
    @Mock
    @Resource(name = "b2bUnitDao")
    private transient SabmB2BUnitDao b2bUnitDao;

    @Mock
    private UserService userService;

	 @Mock
	 private B2BCustomerService b2bCustomerService;
	 @Mock
	 private B2BUnitModel b2bUnit1, b2bUnit2;
	 @Mock
	 private CustomerModel customer;
	 @Mock
	 private ModelService modelService;
	 @Mock
	 private UserModel user;

    @InjectMocks
    private final SabmB2BUnitService b2bUnitService = new DefaultSabmB2BUnitService();
    ;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        //b2bUnitService.setUserService(userService);
    }

    @Test
    public void testfindPrimaryAdminStatus() {
        final B2BCustomerModel primaryAdmin = Mockito.mock(B2BCustomerModel.class);
        given(primaryAdmin.getActive()).willReturn(Boolean.TRUE);
        given(primaryAdmin.getPrimaryAdmin()).willReturn(Boolean.TRUE);
        given(primaryAdmin.getUid()).willReturn("123@123.com");
        given(primaryAdmin.getEncodedPassword()).willReturn("1235678");
        final B2BUnitModel b2bunit = Mockito.mock(B2BUnitModel.class);
        final Set<PrincipalModel> prin = new HashSet<PrincipalModel>();
        prin.add(primaryAdmin);
        given(b2bunit.getMembers()).willReturn(prin);

        given(b2bUnitDao.findTopLevelB2BUnit("1234")).willReturn(b2bunit);
        given(userService.getUserGroupForUID("1234", B2BUnitModel.class)).willReturn(b2bunit);

		  Assert.assertEquals("active", b2bUnitService.findPrimaryAdminStatus("1234"));
    }

    @Test
    public void testGetCustomersWithInvoicePermissionWithPrimaryAdminInList() {

        final B2BCustomerModel primaryAdmin = Mockito.mock(B2BCustomerModel.class);
        Mockito.when(primaryAdmin.getActive()).thenReturn(Boolean.TRUE);

        Mockito.when(primaryAdmin.getPrimaryAdmin()).thenReturn(Boolean.FALSE);

        final B2BUnitModel b2bunit = Mockito.mock(B2BUnitModel.class);

        final List<B2BCustomerModel> list = b2bUnitService.getCustomersWithInvoicePermission(b2bunit);

        assertFalse(list.contains(primaryAdmin));

    }

	 @Test
	 public void getActiveB2BUnitModelsByCustomerTest()
	 {
		 when(b2bCustomerService.getUserForUID("uid")).thenReturn(customer);
		 when(customer.getGroups()).thenReturn(Collections.singleton(b2bUnit1));
		 when(b2bUnit1.getActive()).thenReturn(true);
		 when(b2bUnit1.getCubDisabledUsers()).thenReturn(Collections.singletonList("customerid1"));
		 assertEquals(1, b2bUnitService.getActiveB2BUnitModelsByCustomer("uid").size());
	 }

	 @Test
	 public void removeCustomerFromUnitTest()
	 {
		 when(userService.getUserForUID("customerId")).thenReturn(user);
		 when(userService.getUserGroupForUID("unitId", B2BUnitModel.class)).thenReturn(b2bUnit2);
		 when(b2bUnit2.getCubDisabledUsers()).thenReturn(Collections.singletonList("id1"));

		 b2bUnitService.removeCustomerFromUnit("unitId", "customerId");
		 Mockito.verify(modelService).save(b2bUnit2);
	 }



}

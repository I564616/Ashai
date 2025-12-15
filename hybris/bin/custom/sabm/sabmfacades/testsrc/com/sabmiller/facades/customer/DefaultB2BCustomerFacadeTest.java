/**
 *
 */
package com.sabmiller.facades.customer;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.services.DefaultSabmB2BUnitService;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.customer.impl.DefaultSABMCustomerFacade;


/**
 *
 */
@UnitTest
public class DefaultB2BCustomerFacadeTest
{
    @Mock
    private UserService userService;

    @InjectMocks
    private final DefaultSABMCustomerFacade sabMCustomerFacade = new DefaultSABMCustomerFacade();

    @InjectMocks
    private final DefaultSabmB2BUnitService b2bUnitService = new DefaultSabmB2BUnitService();

    @Mock
    private ModelService modelService;
    @Mock
    private Populator<CustomerModel, CustomerJson> sabmCustomerStatesPopulator;
    @Mock
    private B2BCommerceUnitService b2bCommerceUnitService;
    @Mock
    private Converter<CustomerModel, CustomerJson> customerJsonConverter;
    @Mock
    private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;
    @Mock
    private B2BCustomerService b2bCustomerService;


    @SuppressWarnings("deprecation")
    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        b2bUnitService.setModelService(modelService);
        sabMCustomerFacade.setB2bUnitService(b2bUnitService);
        sabMCustomerFacade.setModelService(modelService);
        b2bUnitService.setUserService(userService);
        sabMCustomerFacade.setUserService(userService);
        sabMCustomerFacade.setSabmCustomerStatesPopulator(sabmCustomerStatesPopulator);
        sabMCustomerFacade.setB2bCommerceUnitService(b2bCommerceUnitService);
        sabMCustomerFacade.setCustomerJsonConverter(customerJsonConverter);
        sabMCustomerFacade.setSabmDeliveryDateCutOffService(sabmDeliveryDateCutOffService);
    }

    @SuppressWarnings("boxing")
    @Test
    public void updateRemenberUnit()
    {
        final B2BCustomerModel customer = new B2BCustomerModel();
        customer.setPreviousB2bUnit(false);
        b2bUnitService.updateRemenberUnitFlag(true, customer);
        Assert.assertEquals(true, customer.getPreviousB2bUnit());
    }

    @SuppressWarnings("boxing")
    @Test
    public void setDefaultUnit()
    {
        final B2BCustomerModel customer = new B2BCustomerModel();
        final B2BUnitModel unit2 = new B2BUnitModel();
        final B2BUnitModel unit1 = new B2BUnitModel();
        unit1.setUid("unit1");
        unit2.setUid("unit2");
        customer.setDefaultB2BUnit(unit1);
        final Set<PrincipalGroupModel> units = new HashSet<PrincipalGroupModel>();
        units.add(unit1);
        units.add(unit2);
        customer.setGroups(units);
        given(userService.getCurrentUser()).willReturn(customer);
        b2bUnitService.updateDefaultCustomerUnit("unit2", customer);
        Assert.assertEquals("unit1", customer.getDefaultB2BUnit().getUid());

    }

    @SuppressWarnings("boxing")
    @Test
    public void changeDefaultUnit()
    {
        final B2BCustomerModel customer = new B2BCustomerModel();
        final B2BUnitModel unit2 = new B2BUnitModel();
        final B2BUnitModel unit1 = new B2BUnitModel();
        unit1.setUid("unit1");
        unit2.setUid("unit2");
        customer.setDefaultB2BUnit(unit1);
        customer.setPreviousB2bUnit(true);
        final Set<PrincipalGroupModel> units = new HashSet<PrincipalGroupModel>();
        units.add(unit1);
        units.add(unit2);
        customer.setGroups(units);
        given(userService.getCurrentUser()).willReturn(customer);
        final SessionService sessionService = Mockito.mock(SessionService.class);
        given(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT)).willReturn(unit1);
        b2bUnitService.setSessionService(sessionService);
        sabMCustomerFacade.updateDefaultCustomerUnit("unit2");
        Assert.assertEquals("unit2", customer.getDefaultB2BUnit().getUid());
        Assert.assertEquals(false, customer.getPreviousB2bUnit());
    }

    @Test
    public void testGetCurrentCustomerJsonStates()
    {
        final B2BCustomerModel customer = new B2BCustomerModel();
        given(userService.getCurrentUser()).willReturn(customer);
        final CustomerJson customerJson = sabMCustomerFacade.getCurrentCustomerJsonStates();
        Assert.assertNotNull(customerJson);
    }

    @Test
    public void testGetCustomerJsonByUid()
    {
        final CustomerJson json = new CustomerJson();
        final String uid2 = "adam.gilchrist@testsample123.com";
        json.setEmail("adam.gilchrist@testsample123.com");
        final B2BCustomerModel customer = new B2BCustomerModel();
        given(customerJsonConverter.convert(customer)).willReturn(json);
        given(b2bCustomerService.getUserForUID(uid2)).willReturn(customer);
        final CustomerJson customerJson2 = sabMCustomerFacade.getCustomerJsonByUid(uid2);
        Assert.assertNotNull(customerJson2);

    }

    @Test
    public void testEnabledCalendarDates()
    {
        final Set<Date> dates = new HashSet<Date>();
        given(sabmDeliveryDateCutOffService.enabledCalendarDates()).willReturn(dates);
    }
}

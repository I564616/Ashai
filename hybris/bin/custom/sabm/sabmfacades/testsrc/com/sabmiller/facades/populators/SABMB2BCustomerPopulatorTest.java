/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * SABMB2BCustomerPopulatorTest
 *
 * @author xiaowu.a.zhang
 * @data 2015-12-25
 */
@UnitTest
public class SABMB2BCustomerPopulatorTest
{
	
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private SABMB2BUnitPopulator sabmB2BUnitPopulator;
	@InjectMocks
	private SABMB2BCustomerPopulator sabmb2bCustomerPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		//sabmb2bCustomerPopulator = new SABMB2BCustomerPopulator();
		sabmb2bCustomerPopulator.setB2bUnitService(b2bUnitService);
		sabmb2bCustomerPopulator.setSabmB2BUnitPopulator(sabmB2BUnitPopulator);
	}

	@Test
	public void testPopulator()
	{
		final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final PrincipalGroupModel principalGroupModel = mock(PrincipalGroupModel.class);

		final B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
		final Integer orderLimit = Integer.valueOf(1000);
		final Boolean receiveUpdates = Boolean.valueOf("false");
		final Boolean receiveUpdatesForSms = Boolean.valueOf("false");
		final BaseSiteModel baseSite = mock(BaseSiteModel.class);

		final PK pk = PK.parse("123457");
		given(principalGroupModel.getPk()).willReturn(pk);
		final Set<PrincipalGroupModel> principalGroupModels = new HashSet<PrincipalGroupModel>();
		principalGroupModels.add(principalGroupModel);
		given(principalGroupModel.getUid()).willReturn("b2badmingroup");

		given(b2bCustomerModel.getOrderLimit()).willReturn(orderLimit);
		given(b2bCustomerModel.getReceiveUpdates()).willReturn(receiveUpdates);
		given(b2bCustomerModel.getReceiveUpdatesForSms()).willReturn(receiveUpdatesForSms);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(baseSiteService.getCurrentBaseSite().getUid()).willReturn(SabmCoreConstants.CUB_STORE);

		given(b2bCustomerModel.getGroups()).willReturn(principalGroupModels);
		given(customerModel.getName()).willReturn("adminName");
		given(customerModel.getContactEmail()).willReturn("adminEmail@163.com");
		given(b2bUnitModel.getContact()).willReturn(customerModel);
		given(b2bUnitService.getParent(b2bCustomerModel)).willReturn(b2bUnitModel);
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomerModel)).willReturn(null);

		final CustomerData customerData = new CustomerData();
		sabmb2bCustomerPopulator.populate(b2bCustomerModel, customerData);

		Assert.assertEquals(orderLimit, customerData.getOrderLimit());
		Assert.assertEquals(receiveUpdates, customerData.getReceiveUpdates());
		Assert.assertEquals(receiveUpdatesForSms, customerData.getReceiveUpdatesForSms());

	}
}

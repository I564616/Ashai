/**
 *
 */
package com.sabmiller.facades.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.customer.B2BUnitJson;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.RegionJson;


/**
 * @author xue.zeng
 *
 */
public class SABMCustomerCurrentB2BUnitPopulatorTest
{
	@InjectMocks
	private SABMCustomerCurrentB2BUnitPopulator sabmCustomerCurrentB2BUnitPopulator;

	@Mock
	private UserService userService;
	@Mock
	private SabmB2BUnitService b2bUnitService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final String uid = "123456789";
		final String IsoCode = "AW";

		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		final Set<PrincipalGroupModel> principalGroups = Sets.newConcurrentHashSet();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setUid(uid);
		principalGroups.add(b2bUnit);
		b2bCustomer.setGroups(principalGroups);

		final CustomerJson customerJson = new CustomerJson();
		final List<RegionJson> jsons = Lists.newArrayList();
		final RegionJson json = new RegionJson();
		final List<B2BUnitJson> b2bunits = Lists.newArrayList();
		final B2BUnitJson b2bUnitJson = new B2BUnitJson();
		b2bUnitJson.setCode(uid);
		b2bunits.add(b2bUnitJson);
		json.setIsocode(IsoCode);
		json.setB2bunits(b2bunits);
		jsons.add(json);
		customerJson.setStates(jsons);

		final AddressModel address = new AddressModel();
		final RegionModel region = new RegionModel();
		region.setIsocode(IsoCode);
		address.setRegion(region);

		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitService.getContactAddressFormB2BUnit(b2bUnit)).willReturn(address);
		sabmCustomerCurrentB2BUnitPopulator.populate(b2bCustomer, customerJson);
		assertEquals(1, customerJson.getStates().get(0).getB2bunits().size());

		final RegionModel region1 = mock(RegionModel.class);
		given(region1.getIsocode()).willReturn(IsoCode);
		given(region1.getName()).willReturn("testRegion1");
		address.setRegion(region1);
		b2bUnit.setUid("test");
		sabmCustomerCurrentB2BUnitPopulator.populate(b2bCustomer, customerJson);
		assertEquals(2, customerJson.getStates().get(0).getB2bunits().size());


		final RegionModel region2 = mock(RegionModel.class);
		given(region2.getIsocode()).willReturn(null);
		given(region2.getName()).willReturn("testRegion2");
		address.setRegion(region2);
		sabmCustomerCurrentB2BUnitPopulator.populate(b2bCustomer, customerJson);
		assertEquals(2, customerJson.getStates().get(0).getB2bunits().size());
	}
}

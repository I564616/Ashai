/**
 *
 */
package com.sabmiller.facades.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
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
import com.sabmiller.facades.customer.CustomerJson;


/**
 * @author xue.zeng
 *
 */
public class SABMCustomerStatesPopulatorTest
{
	@InjectMocks
	private SABMCustomerStatesPopulator customerStatesPopulator;

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
		final String isocode = "ACT";

		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		final String uid = "adam.gilchrist@testsample123.com";
		final Set<PrincipalGroupModel> currentPrincipalGroups = Sets.newConcurrentHashSet();
		final B2BUnitModel currentB2Bunit = new B2BUnitModel();
		currentB2Bunit.setUid(uid);
		currentPrincipalGroups.add(currentB2Bunit);
		b2bCustomer.setGroups(currentPrincipalGroups);
		
		final RegionModel region1 = mock(RegionModel.class);
		given(region1.getIsocode()).willReturn(isocode);
		given(region1.getName()).willReturn("testRegion");
		
		final AddressModel address1 = new AddressModel();
		address1.setStreetname("123");
		final Collection<AddressModel> addresses1 = Lists.newArrayList();
		addresses1.add(address1);
		currentB2Bunit.setAddresses(addresses1);
		currentB2Bunit.setActive(true);
		currentB2Bunit.setUid(uid);
		currentB2Bunit.setName("adam.gilchrist");
		address1.setRegion(region1);

		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(null);
		given(b2bUnitService.getContactAddressFormB2BUnit(currentB2Bunit)).willReturn(address1);
		final CustomerJson customerJson = new CustomerJson();
		customerStatesPopulator.populate(b2bCustomer, customerJson);
		assertEquals(null, customerJson.getEmail());

		final List<B2BUnitModel> topB2BUnits = Lists.newArrayList();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		topB2BUnits.add(b2bUnit);
		given(b2bUnitService.findCustomerTopLevelUnit(b2bCustomer)).willReturn(topB2BUnits);
		customerStatesPopulator.populate(b2bCustomer, customerJson);
		assertEquals(null, customerJson.getEmail());

		final CountryModel country = new CountryModel();
		country.setIsocode("isocode");
		b2bUnit.setCountry(country);

		final B2BUnitModel subB2BUnit = new B2BUnitModel();
		final Set<PrincipalModel> setPrincipal = Sets.newConcurrentHashSet();
		setPrincipal.add(subB2BUnit);
		b2bUnit.setMembers(setPrincipal);
		subB2BUnit.setAddresses(null);
		customerStatesPopulator.populate(b2bCustomer, customerJson);
		assertEquals(null, customerJson.getEmail());


		final List<RegionModel> preRegions = new ArrayList<RegionModel>();
		final RegionModel region = mock(RegionModel.class);
		given(region.getIsocode()).willReturn(isocode);
		given(region.getName()).willReturn("testRegion");
		preRegions.add(region);
		country.setRegions(preRegions);
		customerStatesPopulator.populate(b2bCustomer, customerJson);
		assertEquals(null, customerJson.getEmail());

		
		final AddressModel address = new AddressModel();
		address.setStreetname("123");
		final Collection<AddressModel> addresses = Lists.newArrayList();
		addresses.add(address);
		subB2BUnit.setAddresses(addresses);
		subB2BUnit.setActive(true);
		subB2BUnit.setUid(uid);
		subB2BUnit.setName("adam.gilchrist");
		address.setRegion(region);

		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(b2bUnit);
		given(b2bUnitService.getContactAddressFormB2BUnit(subB2BUnit)).willReturn(address);
		customerStatesPopulator.populate(b2bCustomer, customerJson);
		assertEquals(1, customerJson.getStates().size());
		assertEquals(true, customerJson.getStates().get(0).getB2bunits().get(0).isActive());
	}
}

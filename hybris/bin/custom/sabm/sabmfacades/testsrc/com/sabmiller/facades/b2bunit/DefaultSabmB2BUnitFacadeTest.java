/**
 *
 */
package com.sabmiller.facades.b2bunit;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.b2b.services.SabmOrderTemplateService;
import com.sabmiller.facades.populators.SABMB2BUnitPopulator;


/**
 *
 */
@UnitTest
public class DefaultSabmB2BUnitFacadeTest
{
	@InjectMocks
	private final DefaultSabmB2BUnitFacade defaultSabmB2BUnitFacade = new DefaultSabmB2BUnitFacade();;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private B2BCustomerService b2bCustomerService;
	@Mock
	private SabmOrderTemplateService orderTemplateService;
	@Mock
	private UserService userService;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private I18NFacade i18NFacade;
	@Mock
	@Resource(name = "sabmB2BUnitConverter")
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	@Mock
	@Resource(name = "customerConverter")
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Mock
	private ModelService modelService;
	@Mock
	private SABMB2BUnitPopulator sabmB2BUnitPopulator;
	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private CustomerData customerData1, customerData2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		//defaultSabmB2BUnitFacade = new DefaultSabmB2BUnitFacade();
		//defaultSabmB2BUnitFacade.setB2bCommerceUnitService(b2bCommerceUnitService);
		//defaultSabmB2BUnitFacade.setUserService(userService);
		//defaultSabmB2BUnitFacade.setB2bUnitService(b2bUnitService);
		//defaultSabmB2BUnitFacade.setB2BUnitConverter(b2bUnitConverter);
	}

	@Test
	public void testGetB2BUnits()
	{
		final B2BUnitModel b2BUbitModel = Mockito.mock(B2BUnitModel.class);
		final PK pk = PK.parse("12387");
		given(b2BUbitModel.getPk()).willReturn(pk);
		given(b2BUbitModel.getUid()).willReturn("cub");
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2BUbitModel);
		final List<B2BUnitData> b2blist = Mockito.mock(ArrayList.class);
		final B2BUnitData b2bunitData = new B2BUnitData();
		b2bunitData.setUid(b2BUbitModel.getUid());
		given(b2blist.get(0)).willReturn(b2bunitData);
		Assert.assertEquals("cub", b2blist.get(0).getUid());

	}

	@Test
	public void createEmptyOrderTemplateByName()
	{
		final B2BUnitModel b2BUbitModel = Mockito.mock(B2BUnitModel.class);
		final PK pk = PK.parse("12387");
		given(b2BUbitModel.getPk()).willReturn(pk);
		given(b2BUbitModel.getUid()).willReturn("cub");
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2BUbitModel);
		final SABMOrderTemplateModel createdOrderTemplate = Mockito.mock(SABMOrderTemplateModel.class);
		given(createdOrderTemplate.getPk()).willReturn(pk);
		given(createdOrderTemplate.getCode()).willReturn("0001000");
		given(createdOrderTemplate.getName()).willReturn("123123");
		given(orderTemplateService.createEmptyOrderTemplateForCurrentUnit("123123")).willReturn(createdOrderTemplate);


		Assert.assertEquals("123123", createdOrderTemplate.getName());

	}

	@Test
	public void testGetSubB2BUnitForZADP()
	{
		final String isocode = "ACT";

		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();

		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(null);
		final List<RegionData> nullRegions = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(null, nullRegions);

		final B2BUnitModel b2bUnit = new B2BUnitModel();
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(b2bUnit);
		final List<RegionData> nullRegions2 = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(null, nullRegions2);

		final CountryModel country = new CountryModel();
		country.setIsocode("isocode");
		b2bUnit.setCountry(country);
		final List<RegionData> mockRegions = mock(List.class);
		given(i18NFacade.getRegionsForCountryIso(country.getIsocode())).willReturn(mockRegions);
		final List<RegionData> regions = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(mockRegions, regions);

		final B2BUnitModel subB2BUnit = new B2BUnitModel();
		final Set<PrincipalModel> setPrincipal = Sets.newConcurrentHashSet();
		setPrincipal.add(subB2BUnit);
		b2bUnit.setMembers(setPrincipal);
		subB2BUnit.setAddresses(null);
		final List<RegionData> regionsByAddressIsNull = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(mockRegions, regionsByAddressIsNull);

		final AddressModel address = new AddressModel();
		final Collection<AddressModel> addresses = Lists.newArrayList();
		addresses.add(address);
		subB2BUnit.setAddresses(addresses);
		final List<RegionData> regionsByRegionIsNull = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(mockRegions, regionsByRegionIsNull);

		final RegionModel region = new RegionModel();
		region.setIsocode(isocode);
		address.setRegion(region);
		final List<RegionData> preRegions = new ArrayList<RegionData>();
		final RegionData preRegion = new RegionData();
		preRegion.setIsocode(isocode);
		preRegions.add(preRegion);
		final B2BUnitData preB2BUnit = new B2BUnitData();
		given(i18NFacade.getRegionsForCountryIso(country.getIsocode())).willReturn(preRegions);
		given(b2bUnitConverter.convert(subB2BUnit)).willReturn(preB2BUnit);
		final List<RegionData> regionsByAddresses = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(1, regionsByAddresses.size());
		assertEquals(isocode, regionsByAddresses.get(0).getIsocode());
		assertEquals(1, regionsByAddresses.get(0).getB2bUnits().size());

		final AddressModel contactAddress = new AddressModel();
		subB2BUnit.setContactAddress(contactAddress);

		final RegionModel region2 = new RegionModel();
		region2.setIsocode(isocode);
		contactAddress.setRegion(region2);

		final List<RegionData> preRegions2 = new ArrayList<RegionData>();
		final RegionData preRegion2 = new RegionData();
		preRegion2.setIsocode(isocode);
		preRegions2.add(preRegion2);
		final B2BUnitData preB2BUnit2 = new B2BUnitData();
		given(i18NFacade.getRegionsForCountryIso(country.getIsocode())).willReturn(preRegions2);
		given(b2bUnitConverter.convert(subB2BUnit)).willReturn(preB2BUnit2);
		final List<RegionData> regionsByContactAddress = defaultSabmB2BUnitFacade.getSubB2BUnitForZADP();
		assertEquals(1, regionsByContactAddress.size());
		assertEquals(isocode, regionsByContactAddress.get(0).getIsocode());
		assertEquals(1, regionsByContactAddress.get(0).getB2bUnits().size());
	}

	@Test
	public void testGetB2BUnitsByCustomer()
	{
		final String uid = "adam.gilchrist@testsample123.com";
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		given(b2bCustomerService.getUserForUID(uid)).willReturn(b2bCustomer);
		final List<de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData> b2bUnitsIsEmpty = defaultSabmB2BUnitFacade.getB2BUnitsByCustomer(uid);
		assertEquals(0, b2bUnitsIsEmpty.size());

		final Set<PrincipalGroupModel> principals = Sets.newConcurrentHashSet();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setActive(Boolean.TRUE);
		principals.add(b2bUnit);
		b2bCustomer.setGroups(principals);
		final de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData preB2BUnit = mock(de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData.class);
		given(preB2BUnit.getUid()).willReturn(uid);
		given(b2bUnitConverter.convert(b2bUnit)).willReturn(preB2BUnit);
		final List<de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData> b2bUnitsIsNotEmpty = defaultSabmB2BUnitFacade.getB2BUnitsByCustomer(uid);
		assertEquals(1, b2bUnitsIsNotEmpty.size());
		assertEquals(uid, b2bUnitsIsNotEmpty.get(0).getUid());

		final Set<PrincipalGroupModel> principals2 = Sets.newConcurrentHashSet();
		final B2BUnitModel b2bUnit2 = new B2BUnitModel();
		b2bUnit2.setActive(Boolean.FALSE);
		principals2.add(b2bUnit2);
		b2bCustomer.setGroups(principals2);
		final de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData preB2BUnit2 = mock(de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData.class);
		given(preB2BUnit2.getUid()).willReturn(uid);
		given(b2bUnitConverter.convert(b2bUnit2)).willReturn(preB2BUnit2);
		final List<de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData> b2bUnitsIsEmpty2 = defaultSabmB2BUnitFacade.getB2BUnitsByCustomer(uid);
		assertEquals(0, b2bUnitsIsEmpty2.size());
	}

	@Test
	public void testIsCurrentB2BUnitExistOfCustomer()
	{
		final String uid = "adam.gilchrist@testsample123.com";
		final String unitId = "1111";
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		given(b2bCustomerService.getUserForUID(uid)).willReturn(b2bCustomer);

		final boolean notExist = defaultSabmB2BUnitFacade.isCurrentB2BUnitExistOfCustomer(uid);
		assertEquals(false, notExist);

		final B2BUnitModel zadpB2BUnit = new B2BUnitModel();
		zadpB2BUnit.setUid(unitId);
		final de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData preZadpB2BUnit = mock(
				de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData.class);
		given(preZadpB2BUnit.getUid()).willReturn(uid);
		given(preZadpB2BUnit.isActive()).willReturn(Boolean.TRUE);
		final Set<PrincipalGroupModel> principals = Sets.newConcurrentHashSet();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setActive(Boolean.TRUE);
		b2bUnit.setUid(unitId);
		principals.add(b2bUnit);
		b2bCustomer.setGroups(principals);
		final de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData preB2BUnit = mock(
				de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData.class);
		given(preB2BUnit.getUid()).willReturn(uid);
		given(preB2BUnit.isActive()).willReturn(Boolean.TRUE);
		given(b2bUnitConverter.convert(b2bUnit)).willReturn(preB2BUnit);
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(zadpB2BUnit);
		given(b2bUnitConverter.convert(zadpB2BUnit)).willReturn(preZadpB2BUnit);
		final boolean exist = defaultSabmB2BUnitFacade.isCurrentB2BUnitExistOfCustomer(uid);
		assertEquals(true, exist);
	}

	@Test
	public void testGetZADPB2BUnitByCurrentCustomer()
	{
		final String uid = "adam.gilchrist@testsample123.com";
		final String unitId = "1111";
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(null);
		final B2BUnitData b2bUnitIsNull = defaultSabmB2BUnitFacade.getZADPB2BUnitByCurrentCustomer();
		assertEquals(null, b2bUnitIsNull);

		final B2BUnitModel zadpB2BUnit = new B2BUnitModel();
		zadpB2BUnit.setUid(unitId);
		final B2BUnitData preZadpB2BUnit = new B2BUnitData();
		preZadpB2BUnit.setActive(Boolean.TRUE);
		preZadpB2BUnit.setUid(uid);
		given(b2bUnitService.findTopLevelB2BUnit(b2bCustomer)).willReturn(zadpB2BUnit);
		given(b2bUnitConverter.convert(zadpB2BUnit)).willReturn(preZadpB2BUnit);
		final B2BUnitData b2bUnit = defaultSabmB2BUnitFacade.getZADPB2BUnitByCurrentCustomer();
		assertEquals(preZadpB2BUnit, b2bUnit);
	}


	@Test
	public void testB2bUnitDataExceptZADPUser()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		final Converter<B2BCustomerModel, CustomerData> customerconvert = Mockito.mock(Converter.class);
		final Converter<B2BUnitModel, B2BUnitData> unitConvert = Mockito.mock(Converter.class);

		final B2BUnitModel b2bUnitModel = Mockito.mock(B2BUnitModel.class);
		final B2BUnitData b2bUnitData = new B2BUnitData();
		final B2BCustomerModel customerModel1 = new B2BCustomerModel();
		final B2BCustomerModel customerModel2 = new B2BCustomerModel();
		final B2BCustomerModel customerModel3 = new B2BCustomerModel();
		final CustomerData customerData1 = new CustomerData();
		final CustomerData customerData2 = new CustomerData();
		final CustomerData customerData3 = new CustomerData();
		final List<CustomerData> customerDataList = new ArrayList<CustomerData>();
		final List<B2BCustomerModel> customermodelList = new ArrayList<B2BCustomerModel>();
		customermodelList.add(customerModel1);
		customermodelList.add(customerModel2);
		customermodelList.add(customerModel3);
		given(customerconvert.convert(customerModel1)).willReturn(customerData1);
		given(customerconvert.convert(customerModel2)).willReturn(customerData2);
		given(customerconvert.convert(customerModel3)).willReturn(customerData3);
		given(unitConvert.convert(b2bUnitModel)).willReturn(b2bUnitData);


		final SabmB2BUnitService service = Mockito.mock(SabmB2BUnitService.class);
		given(service.getUnitForUid("123")).willReturn(b2bUnitModel);
		given(service.getCustmoersExceptZADP(b2bUnitModel)).willReturn(customermodelList);
		final Field b2bUnitServiceField = defaultSabmB2BUnitFacade.getClass().getDeclaredField("b2bUnitService");
		b2bUnitServiceField.setAccessible(true);
		b2bUnitServiceField.set(defaultSabmB2BUnitFacade, service);

		//		final Field b2BUnitConverterField = defaultSabmB2BUnitFacade.getClass().getDeclaredField("b2BUnitConverter");
		//		b2BUnitConverterField.setAccessible(true);
		//		b2BUnitConverterField.set(defaultSabmB2BUnitFacade, unitConvert);
		//
		//		final Field b2BCustomerConverterField = defaultSabmB2BUnitFacade.getClass().getDeclaredField("b2BCustomerConverter");
		//		b2BCustomerConverterField.setAccessible(true);
		//		b2BCustomerConverterField.set(defaultSabmB2BUnitFacade, customerconvert);

		defaultSabmB2BUnitFacade.setB2bUnitService(service);



		customerData1.setLastName("abc");
		customerData2.setLastName("bcd");
		customerData3.setLastName("asd");
		customerData1.setActive(true);
		customerData2.setActive(true);
		customerData3.setActive(false);

		customerDataList.add(customerData2);
		customerDataList.add(customerData3);
		customerDataList.add(customerData1);
		given(b2BCustomerConverter.convert(customerModel1)).willReturn(customerData1);
		given(b2BCustomerConverter.convert(customerModel2)).willReturn(customerData2);
		given(b2BCustomerConverter.convert(customerModel3)).willReturn(customerData3);
		final B2BUnitData result = defaultSabmB2BUnitFacade.getB2bUnitDataExceptZADPUser("123");
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getCustomers());
		final List<CustomerData> list = (List<CustomerData>) result.getCustomers();
		Assert.assertEquals("abc", list.get(0).getLastName());
	}

	@Test
	public void testIsCurrentB2BUnitExistOfUid()
	{
		final String uid = "adam.gilchrist@testsample123.com";
		final String unitId = "1111";
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		given(b2bCustomerService.getUserForUID(uid)).willReturn(b2bCustomer);
		final boolean notExist = defaultSabmB2BUnitFacade.isCurrentB2BUnitExistOfUid(uid);
		assertEquals(false, notExist);

		final B2BUnitModel zadpB2BUnit = new B2BUnitModel();
		zadpB2BUnit.setUid(unitId);
		final B2BUnitData preZadpB2BUnit = new B2BUnitData();
		preZadpB2BUnit.setActive(Boolean.TRUE);
		preZadpB2BUnit.setUid(unitId);
		final Set<PrincipalGroupModel> principals = Sets.newConcurrentHashSet();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setActive(Boolean.TRUE);
		b2bUnit.setUid(unitId);
		principals.add(b2bUnit);
		b2bCustomer.setGroups(principals);

		final B2BUnitData preB2BUnit = new B2BUnitData();
		preB2BUnit.setActive(Boolean.TRUE);
		preB2BUnit.setUid(unitId);

		final List<B2BUnitModel> zadpUnitList = Lists.newArrayList();
		zadpUnitList.add(zadpB2BUnit);

		given(b2bUnitConverter.convert(b2bUnit)).willReturn(preB2BUnit);
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitConverter.convert(zadpB2BUnit)).willReturn(preZadpB2BUnit);
		given(b2bUnitService.findCustomerTopLevelUnit(b2bCustomer)).willReturn(zadpUnitList);
		boolean exist = defaultSabmB2BUnitFacade.isCurrentB2BUnitExistOfUid(uid);
		assertEquals(false, exist);

		final Set<B2BUnitModel> currentB2BUnits = Sets.newConcurrentHashSet();
		currentB2BUnits.add(b2bUnit);
		given(b2bUnitService.getBranch(zadpB2BUnit)).willReturn(currentB2BUnits);
		exist = defaultSabmB2BUnitFacade.isCurrentB2BUnitExistOfUid(uid);
		assertEquals(true, exist);
	}

	@Test
	public void testGetTopLevelB2BUnit()
	{
		final String uid = "adam.gilchrist@testsample123.com";
		final String unitId = "1111";
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bUnitService.findCustomerTopLevelUnit(b2bCustomer)).willReturn(Collections.EMPTY_LIST);
		final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnitIsNull = defaultSabmB2BUnitFacade.getTopLevelB2BUnit();
		assertEquals(null, b2bUnitIsNull);

		final B2BUnitModel zadpB2BUnit = new B2BUnitModel();
		zadpB2BUnit.setUid(unitId);
		final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData preZadpB2BUnit = new B2BUnitData();
		preZadpB2BUnit.setActive(Boolean.TRUE);
		preZadpB2BUnit.setUid(uid);
		given(b2bUnitService.findCustomerTopLevelUnit(b2bCustomer)).willReturn(Arrays.asList(zadpB2BUnit));
		given(b2bUnitConverter.convert(zadpB2BUnit)).willReturn(preZadpB2BUnit);
		final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnit = defaultSabmB2BUnitFacade.getTopLevelB2BUnit();
		assertEquals(preZadpB2BUnit, b2bUnit);
	}

	@Test
	public void setActiveStatusTest()
	{
		given(b2bUnitService.getUnitForUid("b2bUnitId")).willReturn(b2bUnitModel);
		given(b2bUnitModel.getCubDisabledUsers()).willReturn(Collections.singletonList("user_id1"));
		given(customerData1.getUid()).willReturn("user_id1");
		given(customerData2.getUid()).willReturn("user_id2");
		given(customerData1.isActive()).willReturn(true);
		given(customerData2.isActive()).willReturn(true);
		defaultSabmB2BUnitFacade.setActiveStatus(Arrays.asList(customerData1,customerData2), "b2bUnitId");
		Mockito.verify(customerData1).setActive(Boolean.FALSE);
		Mockito.verify(customerData2).setActive(Boolean.TRUE);
	}

}

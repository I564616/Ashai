/**
 *
 */
package com.sabmiller.facades.cart;


import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;
import com.sabmiller.facades.constants.SabmFacadesConstants;
import com.sabmiller.facades.customer.B2BUnitJson;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.PermissionsJson;
import com.sabmiller.facades.customer.RegionJson;
import com.sabmiller.facades.customer.SABMCreateUserFormData;
import com.sabmiller.facades.customer.impl.DefaultSABMCustomerFacade;
/**
 * DefaultSABMCustomerFacadeTest
 *
 * @author tom.minwen.wang
 *
 */


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultSABMCustomerFacadeTest
{
	private static final String TEST_FIRSTNAME = "firstName";
	private static final String TEST_LASTNAME = "surName";
	private static final String TEST_EMAIL = "email@qq.com";
	private static final String TEST_BUSINESSUNITID = "Test Sample B2B Melbourne";
	private static final String TEST_USERROLE = "admin";
	private static final String TEST_CANPLACEORDER = "place-orders";
	private static final String TEST_ORDERLIMIT = "1000";

	@InjectMocks
	private final DefaultSABMCustomerFacade defaultSABMCustomerFacade = new DefaultSABMCustomerFacade();

	@Mock
	private UserService userService;
	@Mock
	private UserModel user;
	@Mock
	private ModelService mockModelService;
	@Mock
	private AbstractPopulatingConverter<UserModel, CustomerData> customerConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private StoreSessionFacade storeSessionFacade;
	@Mock
	private CartService cartService;
	@Mock
	private UserFacade userFacade;
	@Mock
	private SessionService sessionService;
	@Mock
	private OrderFacade orderFacade;
	@Mock
	CustomerModel customerModel;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private CustomerNameStrategy customerNameStrategy;
	@Mock
	private CustomerGroupFacade customerGroupFacade;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;
	@Mock
	private DealsService dealsService;
	@Mock
	private SabmB2BCustomerService sabmB2BCustomerService;

	private SABMCreateUserFormData createUserFormData;

	@Mock
	private B2BCustomerService b2bCustomerService;

	@Mock
	private ApbCustomerAccountService apbCustomerAccountService;

	@Mock
	private ApbB2BUnitService apbB2BUnitService;
	@Mock
	private Converter<CustomerModel, CustomerJson> customerJsonConverter;

	@Mock
	private Converter<CsTicketModel, ApbContactUsData> asahiEnquiryConverter;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock
	private AsahiCoreUtil asahiCoreUtil;

	@Mock
	private SabmSearchRestrictionService sabmSearchRestrictionService;
	@Mock
	private B2BCustomerModel b2bCustomerModel;
	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private CustomerJson customerJson;
	@Mock
	private RegionJson regionJson;
	@Mock
	private B2BUnitJson b2bUnitJson;
	@Mock
	private PermissionsJson permissionJson;
	@Mock
	private CustomerData customerData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSABMCustomerFacade.setUserService(userService);
		defaultSABMCustomerFacade.setModelService(mockModelService);
		defaultSABMCustomerFacade.setCustomerConverter(customerConverter);
		defaultSABMCustomerFacade.setCommonI18NService(commonI18NService);
		defaultSABMCustomerFacade.setStoreSessionFacade(storeSessionFacade);
		defaultSABMCustomerFacade.setCartService(cartService);
		defaultSABMCustomerFacade.setUserFacade(userFacade);
		defaultSABMCustomerFacade.setSessionService(sessionService);
		defaultSABMCustomerFacade.setOrderFacade(orderFacade);
		defaultSABMCustomerFacade.setB2bUnitService(b2bUnitService);
		defaultSABMCustomerFacade.setCustomerNameStrategy(customerNameStrategy);
		defaultSABMCustomerFacade.setCustomerGroupFacade(customerGroupFacade);
		defaultSABMCustomerFacade.setB2bCommerceUnitService(b2bCommerceUnitService);

		defaultSABMCustomerFacade.setSabmDeliveryDateCutOffService(sabmDeliveryDateCutOffService);
		defaultSABMCustomerFacade.setDealsService(dealsService);
		defaultSABMCustomerFacade.setApbCustomerAccountService(apbCustomerAccountService);
		defaultSABMCustomerFacade.setApbB2BUnitService(apbB2BUnitService);
		defaultSABMCustomerFacade.setAsahiEnquiryConverter(asahiEnquiryConverter);

		createUserFormData = new SABMCreateUserFormData();

		createUserFormData.setFirstName(TEST_FIRSTNAME);
		createUserFormData.setSurName(TEST_LASTNAME);
		//createUserFormData.setBusinessUnit(TEST_BUSINESSUNITID);
		createUserFormData.setCanPlaceOrder(TEST_CANPLACEORDER);
		createUserFormData.setEmail(TEST_EMAIL);
		//createUserFormData.setUserRole(TEST_USERROLE);
		createUserFormData.setOrderLimit(TEST_ORDERLIMIT);

		final List<String> b2bUnits = new ArrayList<String>();
		b2bUnits.add("bid");
		createUserFormData.setB2bUnits(b2bUnits);

		final PK pk = PK.parse("1234567");
		given(customerModel.getPk()).willReturn(pk);
		given(userService.getCurrentUser()).willReturn(customerModel);


		final CurrencyData defaultCurrencyData = new CurrencyData();
		defaultCurrencyData.setIsocode("USD");

		final LanguageData defaultLanguageData = new LanguageData();
		defaultLanguageData.setIsocode("en");

		given(storeSessionFacade.getDefaultCurrency()).willReturn(defaultCurrencyData);
		given(storeSessionFacade.getDefaultLanguage()).willReturn(defaultLanguageData);

	}

	@Test
	public void testLoginSuccess() throws CommerceCartModificationException
	{

		final CustomerData customerData = new CustomerData();
		final CurrencyData userCurrencyData = new CurrencyData();
		userCurrencyData.setIsocode("PLN");
		customerData.setCurrency(userCurrencyData);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("DE");
		final Collection<CurrencyData> currencies = new ArrayList<CurrencyData>();
		currencies.add(currencyData);

		final LanguageData userLanguageData = new LanguageData();
		userLanguageData.setIsocode("PL");
		customerData.setLanguage(userLanguageData);
		final LanguageData languageData = new LanguageData();
		languageData.setIsocode("DE");
		final Collection<LanguageData> languages = new ArrayList<LanguageData>();
		languages.add(languageData);

		given(customerConverter.convert(customerModel)).willReturn(customerData);
		given(storeSessionFacade.getAllCurrencies()).willReturn(currencies);
		given(storeSessionFacade.getAllLanguages()).willReturn(languages);


		final CartModel sessionCart = mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(sessionCart);
		given(sessionCart.getOrderSimulationStatus()).willReturn(OrderSimulationStatus.NEED_CALCULATION);
		final Date nextAvailableDayDelivery = null;
		final B2BUnitModel b2bUnitModel = Mockito.mock(B2BUnitModel.class);
		given(sabmDeliveryDateCutOffService.getSafeNextAvailableDeliveryDate()).willReturn(nextAvailableDayDelivery);
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2bUnitModel);
		given(asahiSiteUtil.isCub()).willReturn(true);
		given(userService.getCurrentUser()).willReturn(customerModel);

		defaultSABMCustomerFacade.loginSuccess();

		Assert.assertEquals(OrderSimulationStatus.NEED_CALCULATION, sessionCart.getOrderSimulationStatus());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSaveUserIllegalArgumentExceptionr()
	{
		final CustomerJson customerJson = new CustomerJson();
		defaultSABMCustomerFacade.saveUser(customerJson);

		customerJson.setFirstName("Adam");
		defaultSABMCustomerFacade.saveUser(customerJson);

		customerJson.setSurName("Gilchrist");
		defaultSABMCustomerFacade.saveUser(customerJson);

		customerJson.setFirstName(null);
		customerJson.setSurName(null);
		customerJson.setEmail("adam.gilchrist@testsample123.com");
		defaultSABMCustomerFacade.saveUser(customerJson);
	}

	@Test
	public void testSaveUser()
	{
		final CustomerJson customerJson = new CustomerJson();
		customerJson.setFirstName("Adam");
		customerJson.setSurName("Gilchrist");
		customerJson.setEmail("adam.gilchrist@testsample123.com");

		final List<B2BUnitJson> b2bUnitJsons = Lists.newArrayList();
		final B2BUnitJson b2bUnitJson = new B2BUnitJson();
		b2bUnitJson.setSelected(Boolean.FALSE.booleanValue());
		b2bUnitJsons.add(b2bUnitJson);

		final List<RegionJson> regionJsons = Lists.newArrayList();
		final RegionJson regionJson = new RegionJson();
		regionJson.setB2bunits(b2bUnitJsons);
		regionJsons.add(regionJson);

		customerJson.setStates(regionJsons);

		Assert.assertNull(defaultSABMCustomerFacade.saveUser(customerJson));

		b2bUnitJson.setSelected(Boolean.TRUE.booleanValue());
		customerJson.setExists(Boolean.FALSE.booleanValue());
		final PermissionsJson permissionsJson = new PermissionsJson();
		permissionsJson.setOrderLimit(1234);
		permissionsJson.setOrders(Boolean.TRUE.booleanValue());
		customerJson.setPermissions(permissionsJson);

		final B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final B2BCustomerModel currentUser = mock(B2BCustomerModel.class);
		final CustomerData customerData = mock(CustomerData.class);
		Mockito.when(asahiCoreUtil.checkIfUserExists(Mockito.anyString())).thenReturn(null);
		given(mockModelService.create(B2BCustomerModel.class)).willReturn(customerModel);
		given(customerNameStrategy.getName("Adam", "Gilchrist")).willReturn("Adam Gilchrist");
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(userService.getCurrentUser()).willReturn(currentUser);
		//given(b2bCommerceUnitService.getCustomerForUid("adam.gilchrist@testsample123.com")).willReturn(customerModel);
		given(b2bCustomerService.getUserForUID("adam.gilchrist@testsample123.com")).willReturn(customerModel);
		given(customerConverter.convert(customerModel)).willReturn(customerData);
		Assert.assertNotNull(defaultSABMCustomerFacade.saveUser(customerJson));
	}

	@Test
	public void testSaveExistingUser()
	{
		final CustomerJson customerJson = new CustomerJson();
		customerJson.setFirstName("Adam");
		customerJson.setSurName("Gilchrist");
		customerJson.setEmail("adam.gilchrist@testsample123.com");

		final List<B2BUnitJson> b2bUnitJsons = Lists.newArrayList();
		final B2BUnitJson b2bUnitJson = new B2BUnitJson();
		b2bUnitJson.setSelected(Boolean.FALSE.booleanValue());
		b2bUnitJsons.add(b2bUnitJson);

		final List<RegionJson> regionJsons = Lists.newArrayList();
		final RegionJson regionJson = new RegionJson();
		regionJson.setB2bunits(b2bUnitJsons);
		regionJsons.add(regionJson);

		customerJson.setStates(regionJsons);

		Assert.assertNull(defaultSABMCustomerFacade.saveUser(customerJson));

		b2bUnitJson.setSelected(Boolean.TRUE.booleanValue());
		customerJson.setExists(Boolean.FALSE.booleanValue());
		final PermissionsJson permissionsJson = new PermissionsJson();
		permissionsJson.setOrderLimit(1234);
		permissionsJson.setOrders(Boolean.TRUE.booleanValue());
		customerJson.setPermissions(permissionsJson);

		final B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final B2BCustomerModel currentUser = mock(B2BCustomerModel.class);
		final CustomerData customerData = mock(CustomerData.class);
		Mockito.when(asahiCoreUtil.checkIfUserExists(Mockito.anyString())).thenReturn(customerModel);
		Mockito.when(sabmB2BCustomerService.isRegistrationAllowed(Mockito.any(), Mockito.anyString()))
				.thenReturn(Boolean.TRUE);
		given(mockModelService.create(B2BCustomerModel.class)).willReturn(customerModel);
		given(customerNameStrategy.getName("Adam", "Gilchrist")).willReturn("Adam Gilchrist");
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(userService.getCurrentUser()).willReturn(currentUser);
		//given(b2bCommerceUnitService.getCustomerForUid("adam.gilchrist@testsample123.com")).willReturn(customerModel);
		given(b2bCustomerService.getUserForUID("adam.gilchrist@testsample123.com")).willReturn(customerModel);
		given(customerConverter.convert(customerModel)).willReturn(customerData);
		Assert.assertNotNull(defaultSABMCustomerFacade.saveUser(customerJson));
	}

	@Test
	public void testEditUser()
	{
		final B2BCustomerModel model = Mockito.mock(B2BCustomerModel.class);
		final PK pk = PK.parse("123457");
		given(model.getPk()).willReturn(pk);
		final CustomerData customerData = new CustomerData();
		final B2BUnitModel b2bUnitModel = Mockito.mock(B2BUnitModel.class);

		//given(b2bCommerceUnitService.getCustomerForUid(TEST_EMAIL)).willReturn(model);
		given(b2bCustomerService.getUserForUID(TEST_EMAIL)).willReturn(model);
		customerData.setFirstName(TEST_FIRSTNAME);
		customerData.setLastName(TEST_LASTNAME);
		customerData.setName(customerNameStrategy.getName(TEST_FIRSTNAME, TEST_LASTNAME));
		customerData.setEmail(TEST_EMAIL);
		customerData.setUid(TEST_EMAIL);
		customerData.setActive(Boolean.TRUE);

		given(mockModelService.create(B2BCustomerModel.class)).willReturn(model);
		given(b2bUnitService.getUnitForUid(TEST_BUSINESSUNITID)).willReturn(b2bUnitModel);
		given(customerConverter.convert(model)).willReturn(customerData);
		//		model.setCustomerID(UUID.randomUUID().toString());
		if (StringUtils.isNotEmpty(TEST_ORDERLIMIT) && SabmFacadesConstants.USER_ROLE_STAFF_USERS.equals(TEST_USERROLE))
		{
			model.setOrderLimit(Integer.valueOf(TEST_ORDERLIMIT));
		}

		customerGroupFacade.addUserToCustomerGroup(B2BConstants.B2BADMINGROUP, TEST_EMAIL);

		//Assert.assertEquals(customerData, defaultSABMCustomerFacade.editUser(createUserFormData));
	}

	@Test

	public void getUserForUpdateProfile()
	{
		final List<String> andUids = new ArrayList<>();
		andUids.add("b2badmingroup");
		andUids.add("b2bassistantgroup");

		final List<String> orUids = new ArrayList<>();
		orUids.add("testUid1");

		final List<B2BCustomerModel> b2bCustomers = new ArrayList<>();
		final B2BCustomerModel b2bCustomer1 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel b2bCustomer2 = Mockito.mock(B2BCustomerModel.class);
		b2bCustomers.add(b2bCustomer1);
		b2bCustomers.add(b2bCustomer2);
		given(sabmB2BCustomerService.getCustomerForUpdateProfile(orUids, andUids)).willReturn(b2bCustomers);

		final CustomerData customerData1 = Mockito.mock(CustomerData.class);
		final CustomerData customerData2 = Mockito.mock(CustomerData.class);
		given(customerConverter.convert(b2bCustomer1)).willReturn(customerData1);
		given(customerConverter.convert(b2bCustomer2)).willReturn(customerData2);
		given(asahiSiteUtil.isCub()).willReturn(true);
		given(userService.getCurrentUser()).willReturn(customerModel);

		final List<CustomerData> customerDatas = defaultSABMCustomerFacade.getUserForUpdateProfile(orUids);
		Assert.assertEquals(2, customerDatas.size());
		Assert.assertEquals(customerData1, customerDatas.get(0));
		Assert.assertEquals(customerData2, customerDatas.get(1));
	}


	public void testDeleteUser()
	{
		final String uid = "mockUid";
		final String newUid = uid + "(2)";
		final B2BCustomerModel user = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel similarUser = Mockito.mock(B2BCustomerModel.class);
		final List<B2BCustomerModel> similaruUers = new ArrayList<>();
		similaruUers.add(similarUser);

		given(sabmB2BCustomerService.getSimilarB2BCustomer(uid + "(%")).willReturn(similaruUers);
		given(userService.getUserForUID(uid)).willReturn(user);

		final CustomerData customerData = new CustomerData();
		given(customerConverter.convert(user)).willReturn(customerData);

		given(sabmB2BCustomerService.deleteCustomer(user)).willReturn(user);
		final CustomerData resultCustomer1 = defaultSABMCustomerFacade.deleteUser(uid);
		Assert.assertNotNull(resultCustomer1);


		given(sabmB2BCustomerService.deleteCustomer(user)).willReturn(null);
		final CustomerData resultCustomer2 = defaultSABMCustomerFacade.deleteUser(uid);
		Assert.assertNull(resultCustomer2);

		doThrow(new UnknownIdentifierException("")).when(userService).getUserForUID(uid);
		final CustomerData resultCustomer3 = defaultSABMCustomerFacade.deleteUser(uid);
		Assert.assertNull(resultCustomer3);

	}


	@Test
	public void testGetUsersByUserId()
	{

		final List<B2BCustomerModel> b2bCustomers = new ArrayList<>();
		final B2BCustomerModel b2bCustomer1 = Mockito.mock(B2BCustomerModel.class);
		given(b2bCustomer1.getUid()).willReturn("test1");
		final B2BCustomerModel b2bCustomer2 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel b2bCustomer3 = Mockito.mock(B2BCustomerModel.class);
		b2bCustomers.add(b2bCustomer2);
		b2bCustomers.add(b2bCustomer3);
		given(userService.getUserForUID("test1")).willReturn(b2bCustomer1);

		given(sabmB2BCustomerService.getUsersByGroups(b2bCustomer1)).willReturn(b2bCustomers);

		final CustomerData customerData1 = Mockito.mock(CustomerData.class);
		final CustomerData customerData2 = Mockito.mock(CustomerData.class);
		given(customerConverter.convert(b2bCustomer2)).willReturn(customerData1);
		given(customerConverter.convert(b2bCustomer3)).willReturn(customerData2);

		final List<CustomerData> customerData = defaultSABMCustomerFacade.getUsersByUserId("test1");
		Assert.assertEquals(2, customerData.size());

	}

	@Test
	public void testIsEmployeeUser()
	{
		final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
		final boolean isEmployee = defaultSABMCustomerFacade.isEmployeeUser(b2bCustomerModel);
		Assert.assertEquals(false, isEmployee);

		final EmployeeModel employeeModel = mock(EmployeeModel.class);
		final UserGroupModel employeeGroup = mock(UserGroupModel.class);
		given(userService.getUserGroupForUID(SabmFacadesConstants.B2BAPPROVERGROUP)).willReturn(employeeGroup);
		given(userService.isMemberOfGroup(employeeModel, employeeGroup)).willReturn(true);
		final boolean isEmployee2 = defaultSABMCustomerFacade.isEmployeeUser(employeeModel);
		Assert.assertEquals(true, isEmployee2);

	}

	@Test
	public void testGetAllEnquiries() throws ParseException
	{

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(0);
		pageableData.setCurrentPage(1);

		final AsahiB2BUnitModel b2bunit = Mockito.mock(AsahiB2BUnitModel.class);

		final List<CsTicketModel> myenquiries = new ArrayList<>();
		final CsTicketModel enquiry1 = Mockito.mock(CsTicketModel.class);
		final CsTicketModel enquiry2 = Mockito.mock(CsTicketModel.class);
		myenquiries.add(enquiry1);
		myenquiries.add(enquiry2);

		final SearchPageData<CsTicketModel> enquiries = Mockito.mock(SearchPageData.class);
		given(enquiries.getResults()).willReturn(myenquiries);


		given(apbCustomerAccountService.getAllEnquiries(b2bunit, pageableData, b2bunit.getCooDate())).willReturn(enquiries);
		given(apbB2BUnitService.getCurrentB2BUnit()).willReturn(b2bunit);


		final SearchPageData<ApbContactUsData> searchPageData = defaultSABMCustomerFacade.getAllEnquiries(pageableData);

		Assert.assertEquals(2, searchPageData.getResults().size());

	}

	/*
	 * @Test public void testGetCustomerCatalogRestrictedCategories() {
	 * Mockito.when(apbCustomerAccountService.getCustomerCatalogRestrictedCategories()).thenReturn(Sets.newLinkedHashSet(
	 * "cat1")); final Set<String> categories = defaultSABMCustomerFacade.getCustomerCatalogRestrictedCategories();
	 * Assert.assertEquals(categories.iterator().next(), "cat1"); }
	 */

	@Test
	public void saveActiveForCustomerTest()
	{
		when(b2bCustomerService.getUserForUID("customerId")).thenReturn(b2bCustomerModel);
		when(b2bUnitService.getUnitForUid("unitId")).thenReturn(b2bUnitModel);
		when(b2bUnitModel.getCubDisabledUsers()).thenReturn(Collections.singletonList("id1"));
	}

	@Test
	public void editUserTest() throws DuplicateUidException
	{
		when(customerJson.getFirstName()).thenReturn("firstName");
		when(customerJson.getSurName()).thenReturn("surname");
		when(customerJson.getEmail()).thenReturn("test@test.com");
		when(customerJson.getCurrentEmail()).thenReturn("test@test.com");
		when(customerJson.isActive()).thenReturn(true);
		when(customerJson.getStates()).thenReturn(Collections.singletonList(regionJson));
		when(regionJson.getB2bunits()).thenReturn(Collections.singletonList(b2bUnitJson));
		when(b2bCustomerService.getUserForUID("test@test.com")).thenReturn(b2bCustomerModel);
		when(customerJsonConverter.convert(b2bCustomerModel)).thenReturn(customerJson);
		when(userService.getCurrentUser()).thenReturn(b2bCustomerModel);
		when(customerJson.getPermissions()).thenReturn(permissionJson);
		when(permissionJson.isOrders()).thenReturn(false);
		when(b2bCustomerModel.getGroups()).thenReturn(Collections.singleton(b2bUnitModel));
		when(b2bUnitJson.isActive()).thenReturn(true);
		when(b2bUnitJson.isSelected()).thenReturn(true);
		when(b2bUnitJson.getCode()).thenReturn("unitId");
		when(b2bUnitService.getUnitForUid("unitId")).thenReturn(b2bUnitModel);
		when(b2bUnitModel.getCubDisabledUsers()).thenReturn(Collections.singletonList("cusrID1"));
		when(b2bCustomerModel.getUid()).thenReturn("test@test.com");
		when(customerConverter.convert(b2bCustomerModel)).thenReturn(customerData);
		when(b2bUnitModel.getUid()).thenReturn("unitId");
		assertNotNull(defaultSABMCustomerFacade.editUser(customerJson));
	}
}

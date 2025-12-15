/**
 *
 */
package com.apb.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.customer.dao.AsahiCustomerAccountDao;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.asahi.facades.planograms.PlanogramData;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.PlanogramModel;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ApbCustomerAccountServiceImplTest
{

   @InjectMocks
	private final ApbCustomerAccountServiceImpl customerAccountService = new ApbCustomerAccountServiceImpl();

	@Mock
	private UserService userService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SessionService sessionService;

	@Mock
	private EventService eventService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private B2BCustomerModel user;
	@Mock
	private SearchResult<Object> result;
	@Mock
	private AsahiB2BUnitModel b2bUnit;
	@Mock
	private AsahiCatalogProductMappingModel mapping;
	@Mock
	private CategoryModel category;
	@Mock
	private SecureTokenService secureTokenService;
	@Mock
	private ModelService modelService;
	@Mock
	private PrincipalModel member;
	@Mock
	private AsahiCustomerAccountDao asahiCustomerAccountDao;
	@Mock
	private PlanogramModel planogram;
	@Mock
	private PlanogramData data;
	@Mock
	private ApbB2BUnitService apbB2BUnitService;
	@Mock
	private BDECustomerModel bdeCustomer;
	@Mock
	private MultipartFile file;
	@Mock
	private InputStream inputStream;
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		customerAccountService.setUserService(userService);
		customerAccountService.setFlexibleSearchService(flexibleSearchService);
		customerAccountService.setEventService(eventService);
		customerAccountService.setBaseSiteService(baseSiteService);
		customerAccountService.setBaseStoreService(baseStoreService);
		customerAccountService.setCommonI18NService(commonI18NService);
	}

	@Test
	public void testCustomerCatalogRestrictedCategoriesInSession()
	{
		setCatalogHierarchyForCustomerAccount();
		Mockito.when(sessionService.getAttribute(Mockito.any())).thenReturn(null);
		customerAccountService.setRestrictedCategoriesInSession();
		Mockito.verify(sessionService).setAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES, Sets.newHashSet("cat1"));
	}

	@Test
	public void testRestrictedCategoriesInSessionOnAccountChange()
	{
		setCatalogHierarchyForCustomerAccount();
		Mockito.when(sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES)).thenReturn(Sets.newHashSet());
		Mockito.when(sessionService.getAttribute(ApbCoreConstants.EXCLUDED_CATEGORY_RECALCULATION)).thenReturn(Boolean.TRUE);
		customerAccountService.setRestrictedCategoriesInSession();
		Mockito.verify(sessionService).setAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES, Sets.newHashSet("cat1"));
}

	private List<CategoryModel> setCatalogHierarchyForCustomerAccount() {

		final B2BCustomerModel mockUser = Mockito.mock(B2BCustomerModel.class);
		Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
		final AsahiB2BUnitModel b2bUnit = Mockito.mock(AsahiB2BUnitModel.class);
		Mockito.when(mockUser.getDefaultB2BUnit()).thenReturn(b2bUnit);
		Mockito.when(b2bUnit.getCatalogHierarchy()).thenReturn(Sets.newHashSet("catalaogHierarchy1"));
		final SearchResult searchResults = Mockito.mock(SearchResult.class);
		final List<AsahiCatalogProductMappingModel> cataloghierarchy = new ArrayList<AsahiCatalogProductMappingModel>(1);
		cataloghierarchy.add(Mockito.mock(AsahiCatalogProductMappingModel.class));
		Mockito.when(searchResults.getResult()).thenReturn(cataloghierarchy);
		final List<CategoryModel> categories = Lists.newArrayList(Mockito.mock(CategoryModel.class));
		Mockito.when(categories.iterator().next().getCode()).thenReturn("cat1");
		Mockito.when(cataloghierarchy.iterator().next().getExcludedCategories()).thenReturn(categories);
		Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResults);
		return categories;
	}

	@Test
	public void testSendPasswordResetEmail() {

		final B2BCustomerModel mockUser = Mockito.mock(B2BCustomerModel.class);
		Mockito.when(mockUser.getToken()).thenReturn("testToken");
		final AsahiB2BUnitModel b2bUnit = Mockito.mock(AsahiB2BUnitModel.class);
		Mockito.when(mockUser.getDefaultB2BUnit()).thenReturn(b2bUnit);
		Mockito.when(b2bUnit.getPk()).thenReturn(PK.fromLong(2));
		final AsahiSAMAccessModel samAccess = new AsahiSAMAccessModel();
		samAccess.setOrderAccess(Boolean.TRUE);
		samAccess.setPayAccess(Boolean.TRUE);
		samAccess.setPayer(b2bUnit);
		Mockito.when(b2bUnit.getLocName()).thenReturn("Test B2BAccount");
		Mockito.when(b2bUnit.getEmailAddress()).thenReturn("test@test.com");
		Mockito.when(mockUser.getSamAccess()).thenReturn(Arrays.asList(samAccess));
		customerAccountService.sendPasswordResetEmail(mockUser);
		Mockito.verify(eventService, Mockito.times(1)).publishEvent(Mockito.any());
	}

	@Test
	public void setRestrictedCategoriesInSessionTest()
	{
		when(userService.getCurrentUser()).thenReturn(user);
		when(userService.isAnonymousUser(user)).thenReturn(false);
		when(sessionService.getAttribute(ApbCoreConstants.EXCLUDED_CATEGORY_RECALCULATION)).thenReturn(true);
		when(sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES))
				.thenReturn(Collections.singleton("category"));
		when(user.getDefaultB2BUnit()).thenReturn(b2bUnit);
		when(b2bUnit.getCatalogHierarchy()).thenReturn(Collections.singletonList("category"));
		when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(result);
		when(result.getResult()).thenReturn(Collections.singletonList(mapping));
		when(mapping.getExcludedCategories()).thenReturn(Collections.singletonList(category));
		when(category.getCode()).thenReturn("categoryCode");
		customerAccountService.setRestrictedCategoriesInSession();
		Mockito.verify(sessionService).setAttribute(Mockito.any(), Mockito.any());
	}

	@Test
	public void sendWelcomeEmailTest()
	{

		final B2BCustomerModel mockUser = Mockito.mock(B2BCustomerModel.class);
		Mockito.when(mockUser.getToken()).thenReturn("testToken");
		final AsahiB2BUnitModel b2bUnit = Mockito.mock(AsahiB2BUnitModel.class);
		Mockito.when(mockUser.getDefaultB2BUnit()).thenReturn(b2bUnit);
		Mockito.when(b2bUnit.getPk()).thenReturn(PK.fromLong(2));
		final AsahiSAMAccessModel samAccess = new AsahiSAMAccessModel();
		samAccess.setOrderAccess(Boolean.TRUE);
		samAccess.setPayAccess(Boolean.TRUE);
		samAccess.setPayer(b2bUnit);
		Mockito.when(b2bUnit.getLocName()).thenReturn("Test B2BAccount");
		Mockito.when(b2bUnit.getEmailAddress()).thenReturn("test@test.com");
		Mockito.when(mockUser.getSamAccess()).thenReturn(Arrays.asList(samAccess));
		customerAccountService.sendWelcomeEmail(mockUser);
		Mockito.verify(eventService, Mockito.times(1)).publishEvent(Mockito.any());

	}

	@Test
	public void removeCustomerFromUnitTest()
	{
		when(user.getDefaultB2BUnit()).thenReturn(b2bUnit);
		when(b2bUnit.getMembers()).thenReturn(Collections.singleton(member));
		assertEquals(true, customerAccountService.removeCustomerFromUnit(user, b2bUnit));
	}

	@Test
	public void sendCustomerProfileUpdatedNoticeEmailTest()
	{
		customerAccountService.sendCustomerProfileUpdatedNoticeEmail(user, b2bUnit);
		Mockito.verify(eventService).publishEvent(Mockito.any());
	}

	@Test
	public void getDefaultPlanogramsTest()
	{
		when(asahiCustomerAccountDao.findCatalogHierarchyData(Mockito.any())).thenReturn(Collections.singletonList(mapping));
		when(mapping.getDefaultPlanogram()).thenReturn(planogram);
		assertEquals(1, customerAccountService.getDefaultPlanograms(Collections.singletonList("catalogHirearchy")).size());
	}

	@Test
	public void savePlanogramTest() throws IOException
	{
		when(apbB2BUnitService.getCurrentB2BUnit()).thenReturn(b2bUnit);
		when(userService.getCurrentUser()).thenReturn(bdeCustomer);
		when(modelService.create(PlanogramModel.class)).thenReturn(planogram);
		when(data.getFile()).thenReturn(file);
		when(file.getSize()).thenReturn((long) 100000);
		when(file.getInputStream()).thenReturn(inputStream);
		when(b2bUnit.getPlanograms()).thenReturn(Collections.singletonList(planogram));
		assertEquals(true, customerAccountService.savePlanogram(data));
	}

	@Test
	public void removePlanogramTest()
	{
		when(asahiCustomerAccountDao.fetchPlanogramByCode("code")).thenReturn(planogram);
		customerAccountService.removePlanogram("code");
		Mockito.verify(modelService).remove(planogram);
	}

	@Test
	public void removeAllPlanogramsForCurrentB2BUnit()
	{
		when(apbB2BUnitService.getCurrentB2BUnit()).thenReturn(b2bUnit);
		when(b2bUnit.getPlanograms()).thenReturn(Collections.singletonList(planogram));
		customerAccountService.removeAllPlanogramsForCurrentB2BUnit();
		Mockito.verify(modelService).removeAll(Mockito.anyCollection());
	}

	@Test
	public void fetchPlanogramByCodeTest()
	{
		when(asahiCustomerAccountDao.fetchPlanogramByCode("code")).thenReturn(planogram);
		assertEquals(planogram, customerAccountService.fetchPlanogramByCode("code"));
	}

}

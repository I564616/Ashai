/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;
import de.hybris.platform.servicelayer.internal.dao.SortParameters.SortOrder;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.dao.SabmOrderTemplateDao;


/**
 * DefaultSabmOrderTemplateServiceTest
 *
 *
 */
@UnitTest
public class DefaultSabmOrderTemplateServiceTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private SabmOrderTemplateDao orderTemplateDao;
	@Mock
	private KeyGenerator keyGenerator;

	private final String templateName = "testTemplateName";

	@InjectMocks
	private final DefaultSabmOrderTemplateService sabmOrderTemplateService = new DefaultSabmOrderTemplateService();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateEmptyOrderTemplateForCurrentUnit()
	{
		final UserModel mockUser = Mockito.mock(UserModel.class);
		Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);

		final B2BUnitModel b2bUnit = Mockito.mock(B2BUnitModel.class);
		Mockito.when(b2bCommerceUnitService.getParentUnit()).thenReturn(b2bUnit);

		final CurrencyModel currency = Mockito.mock(CurrencyModel.class);
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final List<SABMOrderTemplateModel> mockList = new ArrayList<SABMOrderTemplateModel>();
		final SABMOrderTemplateModel mockedOrderTemplate = Mockito.mock(SABMOrderTemplateModel.class);
		Mockito.when(mockedOrderTemplate.getSequence()).thenReturn(Integer.valueOf(1));
		mockList.add(mockedOrderTemplate);

		final SortParameters sortParam = new SortParameters();
		sortParam.addSortParameter(SABMOrderTemplateModel.SEQUENCE, SortOrder.DESCENDING);
		final Map<String, Object> params = new HashMap<>();
		params.put("unit", b2bUnit);
		Mockito.when(orderTemplateDao.find(params, sortParam)).thenReturn(mockList);

		Mockito.when(keyGenerator.generate()).thenReturn("testCode");

		final SABMOrderTemplateModel orderTemplate = new SABMOrderTemplateModel();
		Mockito.when(modelService.create(SABMOrderTemplateModel.class)).thenReturn(orderTemplate);

		final SABMOrderTemplateModel sabmOrderTemplateModel = sabmOrderTemplateService
				.createEmptyOrderTemplateForCurrentUnit(templateName);

		Assert.assertEquals(Integer.valueOf(1), sabmOrderTemplateModel.getSequence());
		Assert.assertEquals(mockUser, sabmOrderTemplateModel.getUser());
		Assert.assertEquals(b2bUnit, sabmOrderTemplateModel.getUnit());
		Assert.assertEquals(currency, sabmOrderTemplateModel.getCurrency());
	}

}

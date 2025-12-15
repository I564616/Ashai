/**
 *
 */
package com.sabmiller.core.b2b.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.sabmiller.core.cart.dao.SabmCommerceCartDao;
import com.sabmiller.core.cart.service.impl.DefaultSABMCartService;
import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.MaxOrderQtyModel;


/**
 * @author GQ485VQ
 *
 */
@UnitTest
public class DefaultSABMCartServiceTest
{
	@InjectMocks
	DefaultSABMCartService defaultSABMCartService;

	private static final String PRODUCT_CODE = "MockProductId";
	private static final String SITE_ID = "sabmstore";
	private static final String MOCK_UNIT_ID = "MockUnitId";

	@Mock
	ProductModel productModel;

	private static final Object ORDERED_QTY = "orderedQty";
	@Mock
	CMSSiteService cmsSiteService;
	@Mock
	private SabmCommerceCartDao commerceCartDao;
	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		Mockito.when(asahiConfigurationService.getString(ApbCoreConstants.CUB_MAX_ORDER_QTY_RULE_DAYS,
				ApbCoreConstants.DEFAULT_MAX_ORDER_QTY_RULE_DAYS)).thenReturn("7");
		final CMSSiteModel cmsSiteModel = new CMSSiteModel();
		cmsSiteModel.setUid(SITE_ID);
	}

	@Test
	public void getOrderBasedProductMaxOrderQtyTest() throws ParseException
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";
		final MaxOrderQtyModel maxOrderQtyModel = new MaxOrderQtyModel();
		maxOrderQtyModel.setRuleType(MaxOrderQtyRuleType.CUSTOMER_RULE);
		final AsahiB2BUnitModel unit = new AsahiB2BUnitModel();
		unit.setUid(MOCK_UNIT_ID);
		maxOrderQtyModel.setB2bunit(unit);
		maxOrderQtyModel.setStartDate(formatter.parse(startdate_string));
		maxOrderQtyModel.setEndDate(formatter.parse(enddate_string));
		final Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01");
		final OrderEntryModel entry = new OrderEntryModel();
		entry.setEntryNumber(0);
		entry.setProduct(productModel);
		entry.setQuantity(2L);
		final List<OrderEntryModel> entries = new ArrayList<OrderEntryModel>();
		entries.add(entry);

		Mockito.when(commerceCartDao.getOrderEntriesForCustomerRule(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(entries);
		final Map<String, Object> orderedQty = defaultSABMCartService.getOrderBasedProductMaxOrderQty(productModel,
				maxOrderQtyModel, d1);
		Assert.assertEquals(2, orderedQty.get(ORDERED_QTY));
	}

	@Test
	public void getMaxOrderQtyStartAndEndDateTest() throws ParseException
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String distpacthDate_string = "01-06-2022";
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";

		final Date requestedDispatchDate = formatter.parse(distpacthDate_string);
		final Date startDate = formatter.parse(startdate_string);
		final Date endDate = formatter.parse(enddate_string);

		when(maxOrderQtyModel.getStartDate()).thenReturn(startDate);
		when(maxOrderQtyModel.getEndDate()).thenReturn(endDate);

		final Map<String, Date> maxOrderQtyDates = defaultSABMCartService.getMaxOrderQtyStartAndEndDate(maxOrderQtyModel,
				requestedDispatchDate);

		final Date resultEndDate = formatter.parse("06-06-2022");
		assertEquals(startDate, maxOrderQtyDates.get("startDate"));
		assertEquals(resultEndDate, maxOrderQtyDates.get("endDate"));
	}

	@Test
	public void getMaxOrderQtyStartAndEndDateTest2() throws ParseException
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String distpacthDate_string = "06-06-2022";
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";

		final Date requestedDispatchDate = formatter.parse(distpacthDate_string);
		final Date startDate = formatter.parse(startdate_string);
		final Date endDate = formatter.parse(enddate_string);

		when(maxOrderQtyModel.getStartDate()).thenReturn(startDate);
		when(maxOrderQtyModel.getEndDate()).thenReturn(endDate);

		final Map<String, Date> maxOrderQtyDates = defaultSABMCartService.getMaxOrderQtyStartAndEndDate(maxOrderQtyModel,
				requestedDispatchDate);
		final Date resultEndDate = formatter.parse("13-06-2022");
		assertEquals(requestedDispatchDate, maxOrderQtyDates.get("startDate"));
		assertEquals(resultEndDate, maxOrderQtyDates.get("endDate"));
	}

	@Test
	public void getMaxOrderQtyStartAndEndDateTest3() throws ParseException
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String distpacthDate_string = "09-06-2022";
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";

		final Date requestedDispatchDate = formatter.parse(distpacthDate_string);
		final Date startDate = formatter.parse(startdate_string);
		final Date endDate = formatter.parse(enddate_string);

		when(maxOrderQtyModel.getStartDate()).thenReturn(startDate);
		when(maxOrderQtyModel.getEndDate()).thenReturn(endDate);

		final Map<String, Date> maxOrderQtyDates = defaultSABMCartService.getMaxOrderQtyStartAndEndDate(maxOrderQtyModel,
				requestedDispatchDate);

		final Date resultStartDate = formatter.parse("06-06-2022");
		final Date resultEndDate = formatter.parse("13-06-2022");
		assertEquals(resultStartDate, maxOrderQtyDates.get("startDate"));
		assertEquals(resultEndDate, maxOrderQtyDates.get("endDate"));
	}

	@Test
	public void getMaxOrderQtyStartAndEndDateTest4() throws ParseException
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String distpacthDate_string = "14-06-2022";
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";

		final Date requestedDispatchDate = formatter.parse(distpacthDate_string);
		final Date startDate = formatter.parse(startdate_string);
		final Date endDate = formatter.parse(enddate_string);

		when(maxOrderQtyModel.getStartDate()).thenReturn(startDate);
		when(maxOrderQtyModel.getEndDate()).thenReturn(endDate);

		final Map<String, Date> maxOrderQtyDates = defaultSABMCartService.getMaxOrderQtyStartAndEndDate(maxOrderQtyModel,
				requestedDispatchDate);

		final Date resultStartDate = formatter.parse("13-06-2022");
		final Date resultEndDate = formatter.parse("16-06-2022");
		assertEquals(resultStartDate, maxOrderQtyDates.get("startDate"));
		assertEquals(resultEndDate, maxOrderQtyDates.get("endDate"));
	}

	@Test
	public void getMaxOrderQtyStartAndEndDateTest5() throws ParseException
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String distpacthDate_string = "15-06-2022";
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";

		final Date requestedDispatchDate = formatter.parse(distpacthDate_string);
		final Date startDate = formatter.parse(startdate_string);
		final Date endDate = formatter.parse(enddate_string);

		when(maxOrderQtyModel.getStartDate()).thenReturn(startDate);
		when(maxOrderQtyModel.getEndDate()).thenReturn(endDate);

		final Map<String, Date> maxOrderQtyDates = defaultSABMCartService.getMaxOrderQtyStartAndEndDate(maxOrderQtyModel,
				requestedDispatchDate);

		final Date resultStartDate = formatter.parse("13-06-2022");
		final Date resultEndDate = formatter.parse("16-06-2022");
		assertEquals(resultStartDate, maxOrderQtyDates.get("startDate"));
		assertEquals(resultEndDate, maxOrderQtyDates.get("endDate"));
	}

	@Test
	public void getMaxOrderQtyStartAndEndDateTest6() throws ParseException
	{
		final MaxOrderQtyModel maxOrderQtyModel = Mockito.mock(MaxOrderQtyModel.class);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		final String distpacthDate_string = "30-05-2022";
		final String startdate_string = "30-05-2022";
		final String enddate_string = "15-06-2022";

		final Date requestedDispatchDate = formatter.parse(distpacthDate_string);
		final Date startDate = formatter.parse(startdate_string);
		final Date endDate = formatter.parse(enddate_string);

		when(maxOrderQtyModel.getStartDate()).thenReturn(startDate);
		when(maxOrderQtyModel.getEndDate()).thenReturn(endDate);

		final Map<String, Date> maxOrderQtyDates = defaultSABMCartService.getMaxOrderQtyStartAndEndDate(maxOrderQtyModel,
				requestedDispatchDate);

		final Date resultStartDate = formatter.parse("30-05-2022");
		final Date resultEndDate = formatter.parse("06-06-2022");
		assertEquals(resultStartDate, maxOrderQtyDates.get("startDate"));
		assertEquals(resultEndDate, maxOrderQtyDates.get("endDate"));
	}


}

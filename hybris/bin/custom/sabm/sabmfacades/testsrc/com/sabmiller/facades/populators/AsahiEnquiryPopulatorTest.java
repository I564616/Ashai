/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.populators.AsahiEnquiryPopulator;
import com.sabmiller.core.enums.AsahiEnquirySubType;
import com.sabmiller.core.enums.AsahiEnquiryType;


/**
 * @author SN366VA
 *
 */
@UnitTest
public class AsahiEnquiryPopulatorTest
{

	private AsahiEnquiryPopulator asahiEnquiryPopulator;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock
	private EnumerationService enumerationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		asahiEnquiryPopulator = new AsahiEnquiryPopulator();

		asahiEnquiryPopulator.setAsahiSiteUtil(asahiSiteUtil);
		asahiEnquiryPopulator.setEnumerationService(enumerationService);

		when(asahiSiteUtil.isCub()).thenReturn(false);
		when(enumerationService.getEnumerationName(AsahiEnquiryType.WEBSITE_SUPPORT)).thenReturn("Website Support");
		when(enumerationService.getEnumerationName(AsahiEnquirySubType.LOGIN_ISSUE)).thenReturn("Login issue");
	}

	@Test
	public void testPopulator() {

		final CsTicketModel csticket = mock(CsTicketModel.class);

		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date date = null;

		try
		{
			date = sdf.parse("12-04-2021");
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}

		calendar.setTime(date);

		final Date dateplaced = calendar.getTime();

		given(csticket.getTicketID()).willReturn("CUS100075");
		given(csticket.getDatePlaced()).willReturn(dateplaced);
		given(csticket.getName()).willReturn("Test User");
		given(csticket.getEnquiryType()).willReturn(AsahiEnquiryType.WEBSITE_SUPPORT);
		given(csticket.getEnquirySubType()).willReturn(AsahiEnquirySubType.LOGIN_ISSUE);
		given(csticket.getContact()).willReturn("Asahi Contact Center");

		final ApbContactUsData contactusData = new ApbContactUsData();
		asahiEnquiryPopulator.populate(csticket, contactusData);

		Assert.assertEquals("CUS100075", contactusData.getRequestRefNumber());
		Assert.assertEquals(dateplaced, contactusData.getDatePlaced());
		Assert.assertEquals("Test User", contactusData.getName());
		Assert.assertEquals("Website Support", contactusData.getEnquiryType());
		Assert.assertEquals("Login issue", contactusData.getEnquirySubType());
		Assert.assertEquals("Asahi Contact Center", contactusData.getContact());


	}


}

/**
 *
 */
package com.apb.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.service.config.AsahiConfigurationService;

/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiDateUtilTest
{
	@Spy
	@InjectMocks
	private final AsahiDateUtil asahiDateUtil = new AsahiDateUtil();

	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	@Mock
	private Date date, date2;

	@Before
	public void setup()
	{
		when(date.getTime()).thenReturn((long) 1222222);
		when(date2.getTime()).thenReturn((long) 1222222);
	}

	@Test
	public void validateDateTest() {

		when(asahiConfigurationService.getInt("sam.access.expired.email.timeout", 72)).thenReturn(12);
		assertEquals(false, asahiDateUtil.validateDate(date));
	}

	@Test
	public void getDifferenceInDaysTest()
	{

		assertEquals("1", asahiDateUtil.getDifferenceInDays(date, date2));
	}

	@Test
	public void getDifferenceInDaysZeroTest()
	{
		when(date.getTime()).thenReturn((long) 1000000000);
		assertEquals("0", asahiDateUtil.getDifferenceInDays(date, date2));
	}
}

/**
 *
 */
package com.sabmiller.core.util;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * @author tom.minwen.wang
 *
 */
@UnitTest
public class SABMFormatterUtilTest
{

	private SABMFormatterUtils formatUtil;
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		given(configuration.getString("leadingzeros.format.regx", "")).willReturn("0?(\\d+)");
		given(configuration.getString("trailingzeros.format.regx", "")).willReturn("\\.0*$|(\\.\\d*?)0+$");
		given(configuration.getString("comma.number.format", "")).willReturn("#,###.00");
		given(configurationService.getConfiguration()).willReturn(configuration);
		formatUtil = new SABMFormatterUtils();
		formatUtil.setConfigurationService(configurationService);
	}

	@Test
	public void testAttributeValueFormatter()
	{

		//CommaNumberFormat
		Assert.assertEquals("41,000.00", formatUtil.getCommaNumberFormat("41000"));
		Assert.assertEquals("1,000", formatUtil.formatDimension("1000"));
		Assert.assertEquals("41000000.00012345R", formatUtil.getCommaNumberFormat("41000000.00012345R"));
		Assert.assertEquals("VICTORIA BITTER", formatUtil.getCommaNumberFormat("VICTORIA BITTER"));
		Assert.assertEquals("100.09", formatUtil.getCommaNumberFormat("100.09"));
		Assert.assertEquals("1,000.99", formatUtil.getCommaNumberFormat("1000.99"));



		// formatPackageConfiguration
		Assert.assertEquals("1X2", formatUtil.formatPackageConfiguration("01X02"));
		Assert.assertEquals("1X2", formatUtil.formatPackageConfiguration("01X02"));
		Assert.assertEquals("5X2X3", formatUtil.formatPackageConfiguration("05X02X03"));
		Assert.assertEquals("1x2", formatUtil.formatPackageConfiguration("01x02"));
		Assert.assertEquals("1*2", formatUtil.formatPackageConfiguration("01*02"));
		Assert.assertEquals("100x100", formatUtil.formatPackageConfiguration("100x100"));
		Assert.assertEquals("a*b", formatUtil.formatPackageConfiguration("a*b"));
		Assert.assertEquals("", formatUtil.formatPackageConfiguration(""));
		Assert.assertEquals("aaa", formatUtil.formatPackageConfiguration("aaa"));
		Assert.assertEquals("1_1", formatUtil.formatPackageConfiguration("01_01"));

		//example data of the productModel
		//productdescription -BR Carlton Blk Ale RK 49.5L 1x1
		//ABV 4,40%
		Assert.assertEquals("4.40%", formatUtil.formatABV("4,40%"));
		//Category
		Assert.assertEquals("Beer", formatUtil.toTitleCase("BEER"));
		//sub Category
		Assert.assertEquals("Traditional Regular", formatUtil.toTitleCase("TRADITIONAL REGULAR"));
		//brand
		Assert.assertEquals("Carlton Black Ale", formatUtil.toTitleCase("CARLTON BLACK ALE"));
		//pakcage Type -345 ML
		Assert.assertEquals("Bottle", formatUtil.toTitleCase("BOTTLE"));
		//size
		Assert.assertEquals("345 Ml", formatUtil.toTitleCase("345 ML"));
		Assert.assertEquals("50 L", formatUtil.toTitleCase("50 L"));
		//package configuration 04X06
		Assert.assertEquals("4X6", formatUtil.formatPackageConfiguration("04X06"));

		//unit of measurement
		Assert.assertEquals("Cas", formatUtil.toTitleCase("CAS"));
		//weight
		//length-410.000
		Assert.assertEquals("410", formatUtil.formatDimension("410.000"));
		//height -519.000
		Assert.assertEquals("1,519", formatUtil.formatDimension("1519.000"));
		//width -410.000
		Assert.assertEquals("410", formatUtil.formatDimension("410.000"));

	}
}

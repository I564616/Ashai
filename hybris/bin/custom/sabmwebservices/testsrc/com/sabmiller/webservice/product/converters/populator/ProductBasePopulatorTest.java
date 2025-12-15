/**
 *
 */
package com.sabmiller.webservice.product.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.webservice.product.Material;
import com.sabmiller.webservice.product.Material.GeneralData;
import com.sabmiller.webservice.product.Material.TaxClassification;
import com.sabmiller.webservice.product.MatlERPRplctnReqMsgMatlDesc;
import com.sabmiller.webservice.product.ProductInternalID;
import com.sabmiller.webservice.product.SHORTDescription;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductBasePopulatorTest
{
	@Spy
	@InjectMocks
	private final ProductBasePopulator productBasePopulator = new ProductBasePopulator();
	@Mock
	private Material material;
	@Mock
	private ProductData target;
	@Mock
	private ProductInternalID internalId;
	@Mock
	private MatlERPRplctnReqMsgMatlDesc description;
	@Mock
	private SHORTDescription shortDescription;
	@Mock
	private SabmUnitService unitService;
	@Mock
	private Logger LOG;
	@Mock
	private GeneralData data;
	@Mock
	private TaxClassification tc;


	@Before
	public void setUp()
	{
		when(material.getInternalID()).thenReturn(internalId);
		when(material.getDescription()).thenReturn(Collections.singletonList(description));
		when(description.getDescription()).thenReturn(shortDescription);
		when(shortDescription.getValue()).thenReturn("value");
		when(target.getEan()).thenReturn("EAN");
		when(material.getTaxClassification()).thenReturn(Collections.singletonList(tc));

	}

	@Test
	public void populateWetFlagTrueTest()
	{
		when(tc.getTaxClassificationMaterial()).thenReturn("1");
		when(tc.getTaxCategory()).thenReturn("Z9W0");
		productBasePopulator.populate(material, target);
		assertEquals(true, target.isWetEligible());

	}

	@Test
	public void populateWetFlagFalseTest()
	{
		when(tc.getTaxCategory()).thenReturn("Z8W0");
		when(tc.getTaxClassificationMaterial()).thenReturn("2");
		productBasePopulator.populate(material, target);
		assertEquals(false, target.isWetEligible());

	}
}

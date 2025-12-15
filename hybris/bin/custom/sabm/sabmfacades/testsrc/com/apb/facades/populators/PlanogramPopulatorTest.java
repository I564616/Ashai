/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.enums.PlanogramAssociationType;
import com.sabmiller.core.model.PlanogramModel;



/**
 * @author Saumya.Mittal1
 *
 */
public class PlanogramPopulatorTest
{


	@InjectMocks
	private final PlanogramPopulator planogramPopulator = new PlanogramPopulator();;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final PlanogramModel planogram = Mockito.mock(PlanogramModel.class);
		final PlanogramData target = new PlanogramData();
		Mockito.when(planogram.getCode()).thenReturn("pcode");
		Mockito.when(planogram.getAssociationtype()).thenReturn(PlanogramAssociationType.CUSTOMER_ACCOUNT);
		Mockito.when(planogram.getDocumentName()).thenReturn("docName");
		Mockito.when(planogram.getUploadedBy()).thenReturn("owner");
		final CatalogUnawareMediaModel media = Mockito.mock(CatalogUnawareMediaModel.class);
		Mockito.when(media.getCode()).thenReturn("mcode");
		Mockito.when(media.getDownloadURL()).thenReturn("mediaURL");
		Mockito.when(planogram.getMedia()).thenReturn(media);
		planogramPopulator.populate(planogram, target);
		Assert.assertEquals("pcode", target.getCode());
	}
}

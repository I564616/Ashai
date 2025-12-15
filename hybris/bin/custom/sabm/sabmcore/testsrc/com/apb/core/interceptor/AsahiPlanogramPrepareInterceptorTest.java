/**
 *
 */
package com.apb.core.interceptor;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.PlanogramModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiPlanogramPrepareInterceptorTest
{
	@Spy
	@InjectMocks
	private final AsahiPlanogramPrepareInterceptor asahiPlanogramPrepareInterceptor = new AsahiPlanogramPrepareInterceptor();

	@Mock
	private PlanogramModel model;

	@Mock
	private InterceptorContext ctx;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void onPrepareTest() throws InterceptorException
	{
		when(model.getUploadedBy()).thenReturn("uploaded by");
		when(model.getDocumentName()).thenReturn("documentName");
		asahiPlanogramPrepareInterceptor.onPrepare(model, ctx);
		Mockito.verify(model).setCode(Mockito.any());
	}

	@Test
	public void onPrepareBlankUploadByTest() throws InterceptorException
	{
		exception.expect(InterceptorException.class);
		exception.expectMessage("Please provide your name while uploading the default planogram");
		Mockito.lenient().when(model.getDocumentName()).thenReturn("documentName");
		asahiPlanogramPrepareInterceptor.onPrepare(model, ctx);
	}

	@Test
	public void onPrepareBlankDocumentNameTest() throws InterceptorException
	{
		exception.expect(InterceptorException.class);
		exception.expectMessage("Please provide a document name for the uploaded planogram");
		when(model.getUploadedBy()).thenReturn("uploaded by");
		asahiPlanogramPrepareInterceptor.onPrepare(model, ctx);
	}


}

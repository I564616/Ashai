/**
 *
 */
package com.sabmiller.core.customer.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * @author bonnie
 *
 */
@UnitTest
public class SABMUserAccessHistoryServiceTest
{
	@Mock
	private ModelService modelService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testStartCheckoutCountdown() throws Exception
	{

	}
}

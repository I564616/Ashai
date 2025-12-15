/**
 * 
 */
package com.apb.occ.v2.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;

import com.apb.occ.v2.validators.ApbProductWsDTOValidator;


/**
 * The Class ApbProductWsDTOValidatorTest.
 * 
 * @author Kuldeep.Singh1
 */
@UnitTest
public class ApbProductWsDTOValidatorTest
{

	/** The validator. */
	@InjectMocks
	private final ApbProductWsDTOValidator validator = new ApbProductWsDTOValidator();

	/** The errors. */
	@Mock
	Errors errors;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *            the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test product validator.
	 */
	@Test
	public void testProductValidatorWithSuccess()
	{
		final ProductWsDTO productWsDTO = new ProductWsDTO();
		productWsDTO.setCode("asahi_super_dry");
		productWsDTO.setName("asahi_super_dry");

		validator.validate(productWsDTO, errors);
		Assert.assertNotNull(productWsDTO.getCode());
		Assert.assertNotNull(productWsDTO.getName());
	}

	/**
	 * Test product validator with error.
	 */
	@Test
	public void testProductValidatorWithError()
	{
		final ProductWsDTO productWsDTO = new ProductWsDTO();
		productWsDTO.setName("asahi_super_dry");

		validator.validate(productWsDTO, errors);
		Assert.assertNull(productWsDTO.getCode());
		Assert.assertNotNull(productWsDTO.getName());
	}
}

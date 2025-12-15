/**
 * 
 */
package com.apb.occ.v2.validator;

import de.hybris.platform.asahiocc.dto.b2bunit.AbpB2BUnitWsDTO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;

import com.apb.occ.v2.validators.ApbB2BUnitWsDTOValidator;


/**
 * @author Kuldeep.Singh1
 * 
 */
public class ApbB2BUnitWsDTOValidatorTest
{

	/** The validator. */
	@InjectMocks
	private final ApbB2BUnitWsDTOValidator validator = new ApbB2BUnitWsDTOValidator();

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
	 * Test B 2 B unit ws DTO validator.
	 */
	@Test
	public void testB2BUnitWsDTOValidatorWithSuccess()
	{
		final AbpB2BUnitWsDTO b2BUnitWsDTO = new AbpB2BUnitWsDTO();
		b2BUnitWsDTO.setUid("211211");
		b2BUnitWsDTO.setName("asahi_super_dry");
		b2BUnitWsDTO.setAbnNumber("23432");
		b2BUnitWsDTO.setAccountNum("3222");

		validator.validate(b2BUnitWsDTO, errors);
		Assert.assertNotNull(b2BUnitWsDTO.getName());
		Assert.assertNotNull(b2BUnitWsDTO.getAbnNumber());
		Assert.assertNotNull(b2BUnitWsDTO.getAccountNum());
	}

	/**
	 * Test B 2 B unit ws DTO validator with error.
	 */
	@Test
	public void testB2BUnitWsDTOValidatorWithError()
	{
		final AbpB2BUnitWsDTO b2BUnitWsDTO = new AbpB2BUnitWsDTO();
		b2BUnitWsDTO.setAbnNumber("23432");
		b2BUnitWsDTO.setAccountNum("3222");

		validator.validate(b2BUnitWsDTO, errors);
		Assert.assertNull(b2BUnitWsDTO.getUid());
		Assert.assertNotNull(b2BUnitWsDTO.getAbnNumber());
	}

}

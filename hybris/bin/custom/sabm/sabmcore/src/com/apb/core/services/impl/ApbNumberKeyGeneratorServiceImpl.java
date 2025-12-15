package com.apb.core.services.impl;

import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.services.ApbNumberKeyGeneratorService;


/**
 * @author C5252631
 *
 *         ApbNumberKeyGeneratorServiceImpl implementation {@link ApbNumberKeyGeneratorService}
 *
 *         Generic Apb Code key generator
 *
 */
public class ApbNumberKeyGeneratorServiceImpl implements ApbNumberKeyGeneratorService
{
	private PersistentKeyGenerator keyGenerator;

	/**
	 * @return generateCode
	 */
	@Override
	public String generateCode(final String prefixCode)
	{
		final Object generatedValue = keyGenerator.generate();
		if (generatedValue instanceof String)
		{
			return prefixCode + (String) generatedValue;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @return the keyGenerator
	 */
	public PersistentKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	/**
	 * @param keyGenerator
	 *           the keyGenerator to set
	 */
	public void setKeyGenerator(final PersistentKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}
}

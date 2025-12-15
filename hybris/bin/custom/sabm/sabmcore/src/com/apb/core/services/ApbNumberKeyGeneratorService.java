package com.apb.core.services;

/**
 * The Interface ApbNumberKeyGeneratorService.
 */
@FunctionalInterface
public interface ApbNumberKeyGeneratorService
{

	/**
	 * @param keyGenerator
	 * @return
	 */
	String generateCode(String referencePrefixCode);
}

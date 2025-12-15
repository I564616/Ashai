package com.sabmiller.integration.strategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;


/**
 * The Interface CatalogVersionStrategy.
 */
public interface CatalogVersionStrategy
{

	/**
	 * Sets the session catalog version.
	 */
	void setSessionCatalogVersion();

	/**
	 * Sets the session catalog version by catalog.
	 *
	 * @param catalog
	 *           the new session catalog version by catalog
	 */
	void setSessionCatalogVersionByCatalog(String catalog);

	/**
	 * Sets the session catalog version by version.
	 *
	 * @param version
	 *           the new session catalog version by version
	 */
	void setSessionCatalogVersionByVersion(String version);

	/**
	 * Sets the session catalog version.
	 *
	 * @param catalog
	 *           the catalog
	 * @param version
	 *           the version
	 */
	void setSessionCatalogVersion(String catalog, String version);

	/**
	 * Gets the catalog version.
	 *
	 * @return the catalog version
	 */
	CatalogVersionModel getCatalogVersion();
}

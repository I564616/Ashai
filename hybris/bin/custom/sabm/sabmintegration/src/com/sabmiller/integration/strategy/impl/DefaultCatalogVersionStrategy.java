package com.sabmiller.integration.strategy.impl;


import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import jakarta.annotation.Resource;

import com.sabmiller.integration.strategy.CatalogVersionStrategy;


/**
 * The Class DefaultCatalogVersionStrategy.
 */
public class DefaultCatalogVersionStrategy implements CatalogVersionStrategy
{

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The catalog id. */
	private String catalogId;

	/** The version. */
	private String version;

	/** The catalog version. */
	private CatalogVersionModel catalogVersion;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.integration.strategy.CatalogVersionStrategy#setSessionCatalogVersionByCatalog(java.lang.String)
	 */
	@Override
	public void setSessionCatalogVersionByCatalog(final String catalog)
	{
		setSessionCatalogVersion(catalog, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.integration.strategy.CatalogVersionStrategy#setSessionCatalogVersionByVersion(java.lang.String)
	 */
	@Override
	public void setSessionCatalogVersionByVersion(final String version)
	{
		setSessionCatalogVersion(null, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.integration.strategy.CatalogVersionStrategy#setSessionCatalogVersion(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setSessionCatalogVersion(final String catalog, final String version)
	{
		catalogVersionService.setSessionCatalogVersion(catalog, version);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.strategy.CatalogVersionStrategy#getCatalogVersion()
	 */
	@Override
	public CatalogVersionModel getCatalogVersion()
	{
		if (catalogVersion == null)
		{
			catalogVersion = catalogVersionService.getCatalogVersion(catalogId, version);
		}

		return catalogVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.integration.strategy.CatalogVersionStrategy#setSessionCatalogVersion()
	 */
	@Override
	public void setSessionCatalogVersion()
	{
		setSessionCatalogVersion(catalogId, version);
	}

	/**
	 * Sets the catalog id.
	 *
	 * @param catalogId
	 *           the catalogId to set
	 */
	public void setCatalogId(final String catalogId)
	{
		this.catalogId = catalogId;
	}

	/**
	 * Sets the version.
	 *
	 * @param version
	 *           the version to set
	 */
	public void setVersion(final String version)
	{
		this.version = version;
	}

}

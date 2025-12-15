/**
 *
 */
package com.sabmiller.core.product.strategy;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import jakarta.annotation.Resource;


/**
 * @author joshua.a.antony
 *
 */
public class CatalogVersionDeterminationStrategy
{
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	public CatalogVersionModel offlineCatalogVersion()
	{
		return catalogVersionService.getCatalogVersion(getCatalogId(), CatalogManager.OFFLINE_VERSION);
	}

	public CatalogVersionModel onlineCatalogVersion()
	{
		return catalogVersionService.getCatalogVersion(getCatalogId(), CatalogManager.ONLINE_VERSION);
	}

	public CatalogVersionModel offlineContentCatalogVersion()
	{
		return catalogVersionService.getCatalogVersion(getContentCatalogId(), CatalogManager.OFFLINE_VERSION);
	}

	public CatalogVersionModel onlineContentCatalogVersion()
	{
		return catalogVersionService.getCatalogVersion(getContentCatalogId(), CatalogManager.ONLINE_VERSION);
	}

	public String getCatalogId()
	{
		return "sabmProductCatalog";
	}

	public String getContentCatalogId()
	{
		return "sabmContentCatalog";
	}

}

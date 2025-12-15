package com.sabmiller.facades.search.handler;

/**
 * Interface for any custom SOLR sort handlers.
 *
 * Created by wei.yang.ng on 25/07/2016.
 */
public interface SABMCustomSOLRSortHandler<SOURCE, TARGET>
{
	/**
	 * Handler method for bestseller type indexed properties.
	 *
	 * @param source	the source to copy from.
	 * @param target	the targe to copy to.
	 */
	void handleCustomSortProperty(SOURCE source, TARGET target);
}

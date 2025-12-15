/**
 *
 */
package com.sabmiller.webservice.cronjob;

import com.sabmiller.webservice.enums.EntityTypeEnum;


/**
 * Job to cleanup the product import records.
 *
 * @author joshua.a.antony
 */
public class ProductImportCleanupJobPerformable extends AbstractImportCleanupJob
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.webservice.cronjob.AbstractImportCleanupJob#entityType()
	 */
	@Override
	protected EntityTypeEnum entityType()
	{
		return EntityTypeEnum.PRODUCT;
	}

}

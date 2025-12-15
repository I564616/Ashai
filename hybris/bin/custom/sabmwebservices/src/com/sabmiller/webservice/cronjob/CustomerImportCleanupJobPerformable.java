/**
 *
 */
package com.sabmiller.webservice.cronjob;

import com.sabmiller.webservice.enums.EntityTypeEnum;


/**
 * Job to cleanup the customer import records.
 *
 * @author joshua.a.antony
 */
public class CustomerImportCleanupJobPerformable extends AbstractImportCleanupJob
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.webservice.cronjob.AbstractImportCleanupJob#entityType()
	 */
	@Override
	protected EntityTypeEnum entityType()
	{
		return EntityTypeEnum.CUSTOMER;
	}
}

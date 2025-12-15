/**
 *
 */
package com.sabmiller.webservice.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.model.ImportCleanupJobModel;
import com.sabmiller.webservice.model.ImportRecordModel;
import com.sabmiller.webservice.model.MasterImportModel;


/**
 * Job used to purge the import records. Every entity import (example : Customer, Product etc) is stored in the
 * database, i.e the actual request (XML/JSON) is persisted. This could result in exponential increase in the total
 * records in the import table (along with the clob which stores the actual request).
 *
 * This job primary responsibility is to purge the records that are older than X days, where X is configurable per
 * Entity (Product, Customer etc..). Thus, if we foresee that that are too many import for Products, X can have a lower
 * value for the product. Similarly a higher value can be set to Entity that are not imported frequently.
 *
 * @author joshua.a.antony
 */
public abstract class AbstractImportCleanupJob extends AbstractJobPerformable<ImportCleanupJobModel>
{
	private static final Logger LOG = Logger.getLogger(AbstractImportCleanupJob.class);

	private static final String IMPORT_CLEANUP_QUERY = "SELECT {" + MasterImportModel.PK + "} " + "FROM {"
			+ MasterImportModel._TYPECODE + "} WHERE {" + MasterImportModel.CREATIONTIME + "}<?date AND {" + MasterImportModel.ENTITY
			+ "}=?entity";

	private static final String CHILD_CLEANUP_QUERY = "SELECT {" + ImportRecordModel.PK + "} FROM {" + ImportRecordModel._TYPECODE
			+ "} Where {" + ImportRecordModel.MASTERRECORD + "} IN (?mis)";

	protected abstract EntityTypeEnum entityType();

	@Override
	public PerformResult perform(final ImportCleanupJobModel cronjobModel)
	{
		if (cronjobModel == null)
		{
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}

		final List<MasterImportModel> masterImportModels = searchMasterImportRecords(cronjobModel);
		if (masterImportModels != null && !masterImportModels.isEmpty())
		{
			LOG.info("Found " + masterImportModels.size() + " records  which are older than "
					+ (cronjobModel.getXDaysOld() + " days."));

			final boolean success = deleteImportRecords(masterImportModels);
			LOG.info("Finished deleting records");

			return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		LOG.info("No records found. Nothing to do!!!");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	/**
	 * Search for the Master Import records based on the date and the entity type. It is responsibility of the subclass
	 * to set the appropriate value for the entity.
	 */
	protected List<MasterImportModel> searchMasterImportRecords(final ImportCleanupJobModel cronjobModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1 * cronjobModel.getXDaysOld());
		params.put("date", cal.getTime());
		params.put("entity", entityType());

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(IMPORT_CLEANUP_QUERY, params);
		final SearchResult<MasterImportModel> searchResult = flexibleSearchService.search(fsq);
		return searchResult.getCount() > 0 ? searchResult.getResult() : null;
	}

	/**
	 * Delete the Master import record and related child entities. Note that the child entity is defined as 'partof' in
	 * the items.xml definition and hence when the Master import is removed, the related children will also be deleted -
	 * there is no specific code required to remove the children 1 by 1. Example : Assume that Customer webservice was
	 * invoked by providing a huge file consisting of 100 customer records. During the import process, the Master record
	 * will have 1 record(along with the XML/JSON) and the Customoer Import table will have 100 records. During the
	 * deletion process, the 1 record in the master along with 100 records in the customer would be deleted.
	 *
	 * SABMC-1897, as the above description ,it should be working. But the relation between the master and the child have
	 * some issues(in the sabmwebservice-items.xml). So it does not work now. It should be master contain the child
	 * entities, but the current situation is the child contain the master. So when we delete the master record the child
	 * will not be deleted. As the before description before and we have been in the production stage, we need to search
	 * the child entries by SQL and then remove the child record. The master record will be removed automatically.
	 */
	protected boolean deleteImportRecords(final List<MasterImportModel> masterImportModels)
	{
		final boolean success = deleteChildRecords(masterImportModels);
		//  This logic doesn't need any more. Because 'partof' is there.
		//		for (final MasterImportModel model : masterImportModels)
		//		{
		//			LOG.info("Removing MasterImportModel. entity : " + model.getEntity() + " , status : " + model.getStatus() + " , pk : "
		//					+ model.getPk() + ". This will also remove all the associated children of type ");
		//			try
		//			{
		//				modelService.remove(model);
		//			}
		//			catch (final ModelRemovalException e)
		//			{
		//				success = false;
		//				LOG.error("Error removing MasterImportModel with Id " + model.getPk(), e);
		//			}
		//		}
		return success;
	}

	/**
	 * Delete the child record base on the master import record
	 *
	 * @param masterImportModels
	 *           the Master Import Models
	 * @return boolean
	 */
	protected boolean deleteChildRecords(final List<MasterImportModel> masterImportModels)
	{
		boolean success = true;
		final List<ImportRecordModel> childRecords = searchChildImportRecords(masterImportModels);
		if (CollectionUtils.isNotEmpty(childRecords))
		{
			try
			{
				modelService.removeAll(childRecords);
			}
			catch (final ModelRemovalException e)
			{
				success = false;
				LOG.error("Error removing ImportRecordModel with Id " + childRecords, e);
			}
		}
		return success;
	}

	/**
	 * Search for the Child Import records based on the Master records.
	 */
	protected List<ImportRecordModel> searchChildImportRecords(final List<MasterImportModel> masterImportModels)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("mis", masterImportModels);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(CHILD_CLEANUP_QUERY, params);
		final SearchResult<ImportRecordModel> searchResult = flexibleSearchService.search(fsq);
		return searchResult.getCount() > 0 ? searchResult.getResult() : null;
	}
}

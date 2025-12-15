/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.initialdata.setup;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.initialdata.constants.SabmInitialDataConstants;




/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = SabmInitialDataConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(InitialDataSystemSetup.class);

	private static final String IMPORT_CORE_DATA = "importCoreData";
	private static final String IMPORT_SAMPLE_DATA = "importSampleData";
	private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";
	//add by SAB_IMPEX
	private static final String IMPORT_RELEASE_DATA = "importCMSData";
	private static final String IMPORT_IMAGE_THROUGH_IMPEX = "Import Image through Impex";
	private static final String IMPORT_IMAGE_THROUGH_JOB = "Import Image through Job";
	private static final String IMPORT_IMAGE_TITLE = "Import Product Image";

	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;
	private CronJobService cronJobService;


	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));
		//add by SAB_IMPEX
		params.add(createBooleanSystemSetupParameterWithField(IMPORT_RELEASE_DATA, IMPORT_IMAGE_TITLE, IMPORT_IMAGE_THROUGH_JOB,
				IMPORT_IMAGE_THROUGH_IMPEX, true));
		// Add more Parameters here as you require

		return params;
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		// Add Essential Data here as you require
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		/*
		 * Add import data for each site you have configured
		 */
		final List<ImportData> importData = new ArrayList<ImportData>();

		final ImportData sampleImportData = new ImportData();
		sampleImportData.setProductCatalogName("sabm");
		sampleImportData.setContentCatalogNames(Arrays.asList("sabm"));
		sampleImportData.setStoreNames(Arrays.asList("sabmStore"));
		importData.add(sampleImportData);

		LOG.info("Starting with CORE DATA IMPORT SERVICE");
		getCoreDataImportService().execute(this, context, importData);
		getEventService().publishEvent(new CoreDataImportedEvent(context, importData));

		importImpexFile(context, "/sabminitialdata/import/coredata/productCatalogs/sabmProductCatalog/categories.impex");

		importImpexFile(context,
				"/sabminitialdata/import/coredata/contentCatalogs/sabmContentCatalog/mediaconversion_formats.impex");
		importImpexFile(context, "/sabminitialdata/import/coredata/users/user-groups.impex");

		if (getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA))
		{
			LOG.info("Starting with Data Import For Sample Data");
			importImpexFile(context, "/sabminitialdata/import/sampledata/commerceorg/user-groups.impex");

			LOG.info("Starting with SAMPLE DATA IMPORT SERVICE");
			getSampleDataImportService().execute(this, context, importData);
			getEventService().publishEvent(new SampleDataImportedEvent(context, importData));

			importImpexFile(context, "/sabminitialdata/import/sampledata/productCatalogs/sabmProductCatalog/products-deals.impex");
			importImpexFile(context,
					"/sabminitialdata/import/sampledata/productCatalogs/sabmProductCatalog/core-product-range.impex");
			importImpexFile(context, "/sabminitialdata/import/sampledata/common/plant-calendar.impex");
			//fixing the initilaization issue
			importImpexFile(context, "/sabminitialdata/import/sampledata/common/cub_setup_importscript.impex");
			importImpexFile(context, "/sabminitialdata/import/sampledata/common/sample_customer_product.impex");
			importCMSData(context);
		}
	}

	/**
	 * Add for import the sample data for System
	 *
	 * @param context
	 */
	private void importCMSData(final SystemSetupContext context)
	{
		final String importProductMediaData = this.getStringSystemSetupParameter(context, IMPORT_RELEASE_DATA);

		LOG.debug("import media data using ()", importProductMediaData);

		if (StringUtils.trimToEmpty(importProductMediaData).equals(IMPORT_IMAGE_THROUGH_JOB))
		{
			final CronJobModel imageImportJob = getCronJobService().getCronJob("imageImportCronJob");
			getCronJobService().performCronJob(imageImportJob, true);

		}
		else if (StringUtils.trimToEmpty(importProductMediaData).equals(IMPORT_IMAGE_THROUGH_IMPEX))
		{
			importImpexFile(context, "/sabminitialdata/import/sampledata/common/simple-product-media.impex");
		}
		getSampleDataImportService().synchronizeProductCatalog(this, context, "sabm", true);
	}

	/**
	 * Helper method for creating a Boolean setup parameter.
	 *
	 * @param key
	 * @param label
	 * @param defaultValue
	 */
	public SystemSetupParameter createBooleanSystemSetupParameterWithField(final String key, final String label,
			final String displayLabelTrue, final String displayLabelFalse, final boolean defaultValue)
	{
		final SystemSetupParameter syncProductsParam = new SystemSetupParameter(key);
		syncProductsParam.setLabel(label);
		syncProductsParam.addValue(displayLabelTrue, defaultValue);
		syncProductsParam.addValue(displayLabelFalse, !defaultValue);
		return syncProductsParam;
	}

	/**
	 * Helper method for checking setting of a Boolean setup parameter.
	 *
	 * @param context
	 * @param key
	 * @return true if parameter is set to Yes
	 */
	public String getStringSystemSetupParameter(final SystemSetupContext context, final String key)
	{
		final String parameterValue = context.getParameter(context.getExtensionName() + "_" + key);
		if (!StringUtils.isEmpty(parameterValue))
		{
			return parameterValue;
		}
		else
		{
			// Have not been able to determine value from context, fallback to default value
			final boolean defaultValue = getDefaultValueForBooleanSystemSetupParameter(key);
			LOG.warn("Missing setup parameter for key [" + key + "], falling back to defined default [" + defaultValue + "]");
			return "";
		}
	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}

	/**
	 * @return the cronJobService
	 */
	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	/**
	 * @param cronJobService
	 *           the cronJobService to set
	 */
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}



}

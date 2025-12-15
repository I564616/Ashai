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
package com.sabmiller.core.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.util.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SABMManualImportProductExclusionService;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = SabmCoreConstants.EXTENSIONNAME)
public class CoreSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = LoggerFactory.getLogger(CoreSystemSetup.class);

	public static final String IMPORT_ACCESS_RIGHTS = "accessRights";


	@Resource(name = "sabmMManualImportProductExclusionService")
	private SABMManualImportProductExclusionService sabmMManualImportProductExclusionService;

	/**
	 * @return the sabmMManualImportProductExclusionService
	 */
	public SABMManualImportProductExclusionService getSabmMManualImportProductExclusionService()
	{
		return sabmMManualImportProductExclusionService;
	}

	/**
	 * @param sabmMManualImportProductExclusionService
	 *           the sabmMManualImportProductExclusionService to set
	 */
	public void setSabmMManualImportProductExclusionService(
			final SABMManualImportProductExclusionService sabmMManualImportProductExclusionService)
	{
		this.sabmMManualImportProductExclusionService = sabmMManualImportProductExclusionService;
	}

	/**
	 * This method will be called by system creator during initialization and system update. Be sure that this method can
	 * be called repeatedly.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		importImpexFile(context, "/sabmcore/import/common/essential-data.impex");
		importImpexFile(context, "/sabmcore/import/common/countries.impex");
		importImpexFile(context, "/sabmcore/import/common/delivery-modes.impex");

		importImpexFile(context, "/sabmcore/import/common/themes.impex");
		importImpexFile(context, "/sabmcore/import/common/user-groups.impex");
		importImpexFile(context, "/sabmcore/import/common/constraints.impex");

		importImpexFile(context, "/sabmcore/import/common/search_restrictions.impex");
	}

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_ACCESS_RIGHTS, "Import Users & Groups", true));
		params.add(createBooleanSystemSetupParameter("executeProductExclusion", "Import Product Exclusion", false));
		params.add(createBooleanSystemSetupParameter("masterimportlogcleanup", "Cleaning the Master Import Logs", false));

		return params;
	}

	/**
	 * This method will be called during the system initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final boolean importAccessRights = getBooleanSystemSetupParameter(context, IMPORT_ACCESS_RIGHTS);
		final boolean importMasterLog = getBooleanSystemSetupParameter(context, "masterimportlogcleanup");

		final List<String> extensionNames = getExtensionNames();

		if (importAccessRights && extensionNames.contains("cmscockpit"))
		{
			importImpexFile(context, "/sabmcore/import/cockpits/cmscockpit/cmscockpit-users.impex");
			importImpexFile(context, "/sabmcore/import/cockpits/cmscockpit/cmscockpit-access-rights.impex");
		}

		if (importAccessRights && extensionNames.contains("btgcockpit"))
		{
			importImpexFile(context, "/sabmcore/import/cockpits/cmscockpit/btgcockpit-users.impex");
			importImpexFile(context, "/sabmcore/import/cockpits/cmscockpit/btgcockpit-access-rights.impex");
		}

		if (importAccessRights && extensionNames.contains("productcockpit"))
		{
			importImpexFile(context, "/sabmcore/import/cockpits/productcockpit/productcockpit-users.impex");
			importImpexFile(context, "/sabmcore/import/cockpits/productcockpit/productcockpit-access-rights.impex");
			importImpexFile(context, "/sabmcore/import/cockpits/productcockpit/productcockpit-constraints.impex");
		}

		if (importAccessRights && extensionNames.contains("cscockpit"))
		{
			importImpexFile(context, "/sabmcore/import/cockpits/cscockpit/cscockpit-users.impex");
			importImpexFile(context, "/sabmcore/import/cockpits/cscockpit/cscockpit-access-rights.impex");
		}

		if (importAccessRights && extensionNames.contains("reportcockpit"))
		{
			importImpexFile(context, "/sabmcore/import/cockpits/reportcockpit/reportcockpit-users.impex");
			importImpexFile(context, "/sabmcore/import/cockpits/reportcockpit/reportcockpit-access-rights.impex");
		}
		/*
		 * if (extensionNames.contains("mcc")) { importImpexFile(context,
		 * "/sabmcore/import/common/mcc-sites-links.impex"); }
		 */

		final boolean importProductExclusion = getBooleanSystemSetupParameter(context, "executeProductExclusion");
		if (importProductExclusion)
		{
			try
			{
				File directory = new File("./");
				directory = new File(directory.getAbsolutePath());
				while (directory.getParent() != null)
				{
					if (directory.getPath().endsWith("hybris"))
					{
						break;
					}
					directory = new File(directory.getParent());
				}
				final String soureRawFilePath = Config.getString("core.import.productexclusion.sourcepath",
						"/bin/custom/sabm/sabmcore/resources/sabmcore/import/PE");
				final File soureRawFilefolder = new File(directory.getPath() + soureRawFilePath);
				if (soureRawFilefolder.exists())
				{
					sabmMManualImportProductExclusionService
							.generateImpexFilesForProductExclusion(directory.getPath() + soureRawFilePath);
					LOG.info("Completed Generating All Impex files");
				}

				final String relativeImpexFilesPath = Config.getString("core.import.productexclusion.destinationpath", "/impexfiles");
				final String impexFilesPath = soureRawFilePath + relativeImpexFilesPath;

				final File folder = new File(directory.getPath() + impexFilesPath);
				//System.out.println("folder==>" + folder.getPath());
				if (folder.exists())
				{
					final File[] listOfFiles = folder.listFiles();

					for (int i = 0; i < listOfFiles.length; i++)
					{
						final File file = listOfFiles[i];
						if (file.isFile() && (file.getName().endsWith(".impex") || file.getName().endsWith(".IMPEX")))
						{
							LOG.info("Starting Import File : " + file.getName());
							importImpexFile(context, "/sabmcore/import/PE" + relativeImpexFilesPath + "/" + file.getName());
							LOG.info("Finished Import File : " + file.getName());
							if (file.delete())
							{
								LOG.info("Deleted IMPEX File : " + file.getName());
							}
							else
							{
								LOG.info("Problem In Deleting IMPEX File : " + file.getName());
							}
						}
					}
					if (folder.listFiles().length <= 0)
					{
						if (folder.delete())
						{
							LOG.info("Deleted generated impex folder : " + folder.getName());
						}
						else
						{
							LOG.info("Problem in Deleting generated impex folder : " + folder.getName());
						}
					}
				}
			}
			catch (final Exception e)
			{
				LOG.info("Problem in importing Product Exclusion.");
			}
		}
		else
		{
			if (extensionNames.contains("mcc"))
			{
				importImpexFile(context, "/sabmcore/import/common/mcc-sites-links.impex");
			}
		}

		if (importMasterLog)
		{
			importImpexFile(context, "/sabmcore/import/common/sabmimportlogcleanupjob.impex");
		}
	}


	protected List<String> getExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}

	protected <T> T getBeanForName(final String name)
	{
		return (T) Registry.getApplicationContext().getBean(name);
	}
}

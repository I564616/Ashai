package com.apb.core.error.handler;

import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.exceptions.ModelInitializationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.DefaultImportService;
import de.hybris.platform.servicelayer.impex.impl.ImportCronJobResult;

import jakarta.annotation.Resource;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.service.config.AsahiConfigurationService;


public class AsahiDefaultImportService extends DefaultImportService
{
	private static final Logger LOG = LoggerFactory.getLogger(AsahiDefaultImportService.class);
	private static final String NODEID_STRING = "nodeId";
	private static final String NODEGROUP_STRING = "nodeGroup";
	private static final String ADMINNODES_STRING = "adminNodeGroup";

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Override
	public ImportResult importData(final ImportConfig config)
	{
		final ImpExImportCronJobModel cronJob = (ImpExImportCronJobModel) this.getModelService().create("ImpExImportCronJob");

		try
		{
			this.getModelService().initDefaults(cronJob);
		}
		catch (final ModelInitializationException var4)
		{
			throw new SystemException(var4);
		}

		if (NODEID_STRING
				.equalsIgnoreCase(this.asahiConfigurationService.getString("impex.import.node.executiontype.apb", NODEID_STRING)))
		{
			cronJob.setNodeID(Integer.parseInt(this.asahiConfigurationService.getString("impex.import.node.id.apb", "0")));
		}
		if (NODEGROUP_STRING
				.equalsIgnoreCase(this.asahiConfigurationService.getString("impex.import.node.executiontype.apb", NODEID_STRING)))
		{
			cronJob.setNodeGroup(this.asahiConfigurationService.getString("impex.import.node.group.apb", ADMINNODES_STRING));
		}

		this.configureCronJob(cronJob, config);
		this.getModelService().saveAll(new Object[]
		{ cronJob.getJob(), cronJob });
		this.importData(cronJob, config.isSynchronous(), config.isRemoveOnSuccess());

		if ("true".equalsIgnoreCase(this.asahiConfigurationService.getString("log.cronjob.enabled.apb", "false")))
		{
			LOG.error("-cronjob-stats- Code : " + cronJob.getCode() + ", NodeID : " + cronJob.getNodeID() + ", StartTime : "
					+ cronJob.getStartTime() + ", EndTime : " + cronJob.getEndTime() + ", Number of lines processed successfully : "
					+ cronJob.getValueCount() + ", Last successfully processed line : " + cronJob.getLastSuccessfulLine()
					+ ", Status : " + cronJob.getStatus().name() + ", Result : " + cronJob.getResult().name());
		}
		cronJob.setNodeID(Integer.parseInt(this.asahiConfigurationService.getString("impex.import.node.id.apb", "0")));
		return ((Item) this.getModelService().getSource(cronJob)).isAlive() ? new ImportCronJobResult(cronJob)
				: new ImportCronJobResult((ImpExImportCronJobModel) null);
	}
}

/**
 *
 */
package com.sabmiller.webservice.cronjob;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.dao.DealsDaoImpl;
import com.sabmiller.core.deals.services.DealsServiceImpl;
import com.sabmiller.core.model.DealModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.sabmiller.webservice.enums.EntityTypeEnum;

import jakarta.annotation.Resource;


/**
 * @author madhu.c.dasari
 *
 */
public class DataImportCronJob extends AbstractJobPerformable<CronJobModel>
{
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	final InputStream FILE = DataImportCronJob.class.getResourceAsStream("/sampledata/sampledata_MasterImport.csv");
	final static String URL = "http://sabmiller.local:9001/sabmwebservices/services/";


	@Resource(name="defaultDealsService")
	private DealsServiceImpl dealsService;

	@Resource(name="defaultDealsDao")
	private DealsDaoImpl dealsDao;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		final boolean shouldLoad = configurationService.getConfiguration().getBoolean(SabmCoreConstants.DATA_IMPORT_LOAD_MODE, true);

		if (shouldLoad)
		{
			customerImport();
			LOG.info("Succefully completed Sample Data Import Cronjob");
		}

		printValidComplexDeals();
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	private void printValidComplexDeals()
	{
		List<DealModel> complexDeals = dealsDao.getComplexDeals();
		List<DealModel> validDeals = new ArrayList<>();

		for (DealModel dealModel : complexDeals)
		{
			if (dealsService.isValidDeal(dealModel))
			{
				validDeals.add(dealModel);
			}
		}

		LOG.info("------------ Printing List of Valid Deals ------------");
		for (final DealModel dealModel : validDeals)
		{
			LOG.info("Complex Deal [{}] is valid.", dealModel.getCode());
		}

	}

	public void customerImport()
	{
		BufferedReader br = null;
		String line = "";
		final String cvsSplitBy = ",";
		try
		{
			br = new BufferedReader(new InputStreamReader(FILE));
			while ((line = br.readLine()) != null)
			{
				try
				{
					final RestTemplate restTemplate = new RestTemplate();

					// use comma as separator
					final String[] data = line.split(cvsSplitBy);

					if (data[1].equalsIgnoreCase(EntityTypeEnum.CUSTOMER.getCode()))
					{
						restTemplate.postForLocation(URL + "/customer", data[2]);
					}
					else if (data[1].equalsIgnoreCase(EntityTypeEnum.PRODUCT.getCode()))
					{
						restTemplate.postForLocation(URL + "/product", data[2]);
					}
					else if (data[1].equalsIgnoreCase(EntityTypeEnum.DEAL.getCode()))
					{
						restTemplate.postForLocation(URL + "/complexdeals", data[2]);
					}

					//Sleep time 1 sec to finish the data import of 1 entity
					Thread.sleep(1000);
				}
				catch (final Exception e)
				{
					LOG.error("Error occured while performig import. ", e);
				}

			}

		}
		catch (final FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

}

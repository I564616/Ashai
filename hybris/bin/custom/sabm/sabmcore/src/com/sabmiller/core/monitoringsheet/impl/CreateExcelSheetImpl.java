/**
 *
 */
package com.sabmiller.core.monitoringsheet.impl;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Resource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.core.monitoringsheet.CreateExcelSheet;


/**
 * @author praveenkumar.k.reddy
 *
 */
public class CreateExcelSheetImpl implements CreateExcelSheet
{

	private static final Logger LOG = LoggerFactory.getLogger(CreateExcelSheetImpl.class);

	@Resource(name = "emailService")
	private SystemEmailService emailService;

	final String directory = Config.getString("monitoring.report.file.creation.directory", "File creation directory");
	String filename = directory + File.separator + "DailyMonitoringSheet.csv";

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.monitoringsheet.CreateExcelSheet#CreateJobList(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public boolean createExcelSheet(final List<CronJobModel> cronJobModels, final List<List<?>> interfacedetails)
	{
		boolean status = false;
		LOG.info("########### Starting loop #########");

		// Create blank workbook
		final XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank sheet
		final XSSFSheet spreadsheet = workbook.createSheet(" Daily Monitoring Sheet ");
		// Create row object
		XSSFRow row;
		int key = 1;
		// This data needs to be written (Object[])
		final Map<Integer, List<Object>> monitoringsheet = new TreeMap<Integer, List<Object>>();

		monitoringsheet.put(key, new ArrayList<>(Arrays.asList("Area", "Job/Interface Name", "Last Time Stamp", "Job Frequency",
				"Monitoring Frequency", "Status", "Records Count", "Comments")));

		for (final CronJobModel cronJobModel : cronJobModels)
		{
			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			final String startDate = dateFormat.format(cronJobModel.getStartTime());

			for (final TriggerModel triggerModel : cronJobModel.getTriggers())
			{
				final String jobRunFrequency = jobFrequency(cronJobModel.getStartTime(), triggerModel.getCronExpression());

				final List<Object> job = new ArrayList<Object>();
				job.add("Hybris");
				job.add(cronJobModel.getCode());
				job.add(startDate);
				job.add(jobRunFrequency);
				job.add("Daily");
				job.add(cronJobModel.getStatus().toString() + "_" + cronJobModel.getResult().toString());
				job.add("");
				job.add(" ");
				key++;
				monitoringsheet.put(key, job);
			}

		}

		for (final List<?> interfaceData : interfacedetails)
		{

			final List<Object> interfaceInfo = new ArrayList<Object>();
			interfaceInfo.add("Hybris");
			interfaceInfo.add(interfaceData.get(0));
			interfaceInfo.add("NA");
			interfaceInfo.add("NA");
			interfaceInfo.add("Daily");
			interfaceInfo.add(interfaceData.get(1).toString());
			interfaceInfo.add(interfaceData.get(2).toString());
			interfaceInfo.add(" ");
			key++;
			monitoringsheet.put(key, interfaceInfo);
		}
		final Set<Integer> keyid = monitoringsheet.keySet();
		int rowid = 0;
		for (final int eachkey : keyid)
		{
			row = spreadsheet.createRow(rowid++);
			final List<Object> objectArr = monitoringsheet.get(eachkey);
			int cellid = 0;
			for (final Object obj : objectArr)
			{
				final Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}

		final File destination = new File(directory);
		destination.getParentFile().mkdirs();
		writeDir(destination);
		FileOutputStream out;
		try
		{
			out = new FileOutputStream(filename);
			workbook.write(out);
			out.close();
			status = true;
		}
		catch (final FileNotFoundException e)
		{
			LOG.error("File creation exception ", e);
		}
		catch (final IOException e)
		{
			LOG.error(" Workbook creation exception ", e);
		}
		LOG.info("Daily Monitoring Report Created Successfully..");
		return status;
	}

	String jobFrequency(final Date jobStartTime, final String expression)
	{
		String jobRunFrequency = null;
		try
		{
			if (expression != null)
			{
				final CronExpression cronExpression = new CronExpression(expression);

				LOG.info("Cron Expression " + cronExpression.getCronExpression());
				LOG.info("getNextValidTimeAfter " + cronExpression.getNextValidTimeAfter(jobStartTime));
				jobRunFrequency = cronExpression.getNextValidTimeAfter(jobStartTime).toString();
			}
			else
			{
				jobRunFrequency = "";
			}

		}
		catch (final ParseException exception)
		{
			exception.printStackTrace();
			LOG.error(" Parser exception ", exception);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			LOG.error(" Exception ", e);
		}

		return jobRunFrequency;
	}

	private void writeDir(final File f)
	{
		try
		{
			if (f.mkdirs())
			{
				LOG.info("Directory creadted for daily monitoring report");
			}
			else
			{
				LOG.info("Directory not creadted for daily monitoring report");
			}
		}
		catch (final Exception e)
		{

			LOG.error("Not able to created the directory ", e);
		}
	}

}


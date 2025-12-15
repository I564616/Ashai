package com.sabmiller.integration.salesforce;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SabmCSVFileGenerator
{

	private static final String CDM_EXPORT_CSV_DELIMITER = "cdm.export.csv.delimiter";

	private static final String CDM_EXPORT_CSV_QUOTE = "cdm.export.csv.quote";

	private static final String CDM_EXPORT_CSV_HEADER = "cdm.export.csv.headline";

	@Resource(name = "modelService")
	private ModelService modelService;


	private static final Logger LOG = LoggerFactory.getLogger(SabmCSVFileGenerator.class.getName());



	/**
	 * Write to CSV file
	 *
	 * @param results
	 * @return boolean
	 */
	public File writeToFile(final File directory, final String fileName, final String fileExt, final List<List<String>> results,
			final List<String> headers)
	{
		File tempFile = null;
		try
		{
			tempFile = File.createTempFile(fileName, fileExt, directory);
		}
		catch (final IOException e)
		{
			LOG.error("Error Creating Temp file " + e.getMessage(), e);
			return tempFile;
		}

		// Loop thru all result
		try (FileWriter writer = new FileWriter(tempFile);)
		{
			final int delimiterInt = Config.getInt(CDM_EXPORT_CSV_DELIMITER, 44);
			final int quoteInt = Config.getInt(CDM_EXPORT_CSV_QUOTE, 34);
			final char delimiter = (char) delimiterInt;
			final char quote = (char) quoteInt;

				SabmCSVUtils.writeLine(writer, headers, delimiter, quote);
			for (final List<String> result : results)
			{
				SabmCSVUtils.writeLine(writer, result, delimiter, quote);
			}
			writer.flush();
			writer.close();
		}
		catch (final IOException e)
		{
			LOG.error("Error Creating CSV file " + e.getMessage(), e);
			return tempFile;
		}



		return tempFile;
	}

	// Prepare result data types
	private List<Class> getResultClasses(final int numberOfFields)
	{
		final List<Class> resultClasses = new ArrayList<>();
		for (int i = 0; i < numberOfFields; i++)
		{
			resultClasses.add(String.class);
		}
		return resultClasses;
	}
}

package com.apb.core.error.handler;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.BatchDirectoryUtils;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public abstract class AsahiImpexRunnerTask extends AbstractImpexRunnerTask
{

	private static final Logger LOG = LoggerFactory.getLogger(AsahiImpexRunnerTask.class);

	private final String encoding = CSVConstants.HYBRIS_ENCODING;
	private static final String ERROR_FILE_PREFIX = "error_";
	private int linesToSkip;
	private final char fieldSeparator = ',';

	@Override
	public BatchHeader execute(final BatchHeader header) throws FileNotFoundException
	{
		Assert.notNull(header, "must not be null");
		Assert.notNull(header.getEncoding(), "must not be null");
		if (CollectionUtils.isNotEmpty(header.getTransformedFiles()))
		{
			final Session localSession = getSessionService().createNewSession();
			try
			{
				for (final File file : header.getTransformedFiles())
				{
					processFile(file, header.getEncoding());
				}
			}
			finally
			{
				getSessionService().closeSession(localSession);
			}
		}
		return header;
	}

	/**
	 * Process an impex file using the given encoding
	 *
	 * @param file
	 * @param encoding
	 * @throws FileNotFoundException
	 */
	@Override
	protected void processFile(final File file, final String encoding) throws FileNotFoundException
	{
		CSVReader csvReader = null;
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			final ImportConfig config = getImportConfig();
			if (config == null)
			{
				LOG.error(
						String.format("Import config not found. The file %s won't be imported.", file == null ? null : file.getName()));
				return;
			}
			final ImpExResource resource = new StreamBasedImpExResource(fis, encoding);
			config.setScript(resource);
			final ImportResult importResult = getImportService().importData(config);
			if (importResult.isError() && importResult.hasUnresolvedLines())
			{
				final String message = importResult.getUnresolvedLines().getPreview();
				csvReader = this.createCsvReader(file);
				this.writeErrorLine(file, csvReader, message);
				LOG.error(importResult.getUnresolvedLines().getPreview());
			}
			IOUtils.closeQuietly(fis);
			closeQuietly(csvReader);
		}
		catch (final UnsupportedEncodingException e)
		{
			IOUtils.closeQuietly(fis);
			closeQuietly(csvReader);
			LOG.error("Error has occured", e);
		}
	}

	/**
	 * Close quietly.
	 *
	 * @param csvReader
	 *           the csv reader
	 */
	protected void closeQuietly(final CSVReader csvReader)
	{
		if (csvReader != null)
		{
			try
			{
				csvReader.close();
			}
			catch (final IOException e)
			{
				LOG.warn("Could not close csvReader" + e);
			}
		}
	}

	/**
	 * Write error line.
	 *
	 * @param file
	 *           the file
	 * @param csvReader
	 *           the csv reader
	 * @param message
	 *           the message
	 * @return the prints the writer
	 * @throws UnsupportedEncodingException
	 *            the unsupported encoding exception
	 * @throws FileNotFoundException
	 *            the file not found exception
	 */
	protected PrintWriter writeErrorLine(final File file, final CSVReader csvReader, final String message)
			throws UnsupportedEncodingException, FileNotFoundException
	{
		FileOutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		PrintWriter result = null;

		try
		{
			outputStream = new FileOutputStream(getErrorFile(file));
			outputStreamWriter = new OutputStreamWriter(outputStream, encoding);
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			result = new PrintWriter(bufferedWriter);
			result.print(message);
			result.close();
			bufferedWriter.close();
			outputStreamWriter.close();
			outputStream.close();
		}
		catch (final Exception e)
		{
			LOG.error("Exception occured " + e.getMessage(), e);

			try
			{
				if (result != null)
				{
					result.close();
				}
				if (bufferedWriter != null)
				{
					bufferedWriter.close();
				}
				if (outputStreamWriter != null)
				{
					outputStreamWriter.close();
				}
				if (outputStream != null)
				{
					outputStream.close();
				}

			}
			catch (final IOException ex)
			{
				LOG.error("IOException occured while closing resource stream " + ex.getMessage(), ex);
			}
		}
		return result;
	}

	/**
	 * Gets the error file.
	 *
	 * @param file
	 *           the file
	 * @return the error file
	 */
	protected File getErrorFile(final File file)
	{
		return new File(BatchDirectoryUtils.getRelativeErrorDirectory(file), ERROR_FILE_PREFIX + file.getName());
	}

	/**
	 * Creates a CSV Reader
	 *
	 * @param file
	 * @return a initialised CSV reader
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	protected CSVReader createCsvReader(final File file) throws UnsupportedEncodingException, FileNotFoundException
	{
		final CSVReader csvReader = new CSVReader(file, encoding);
		csvReader.setLinesToSkip(linesToSkip);
		csvReader.setFieldSeparator(new char[]
		{ fieldSeparator });
		return csvReader;
	}
}

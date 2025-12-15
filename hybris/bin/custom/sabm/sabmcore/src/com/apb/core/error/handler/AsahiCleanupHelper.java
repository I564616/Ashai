package com.apb.core.error.handler;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.BatchDirectoryUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;


public class AsahiCleanupHelper extends CleanupHelper
{

	private static final Logger LOG = LoggerFactory.getLogger(AsahiCleanupHelper.class);
	private static final String DATE_SEPARATOR = "_";
	private String timeStampFormat;

	/**
	 * Performs the cleanup of given header
	 *
	 * @param header
	 *           to clean up
	 * @param error
	 *           flag indicating if there was an error
	 */
	@Override
	public void cleanup(final BatchHeader header, final boolean error)
	{
		if (header != null)
		{
			cleanupTransformedFiles(header);
			cleanupSourceFile(header, error);
		}
	}

	/**
	 * Deletes a file
	 *
	 * @param file
	 */
	@Override
	public void cleanupFile(final File file)
	{
		if (!file.delete())
		{
			LOG.warn("Could not delete " + file);
		}
	}

	/**
	 * Removes the transformed file
	 *
	 * @param header
	 * @param error
	 */
	@Override
	protected void cleanupSourceFile(final BatchHeader header, final boolean error)
	{
		if (header.getFile() != null)
		{
			final File movedFile = getDestFile(header.getFile(), error);
			if (!header.getFile().renameTo(movedFile))
			{
				LOG.warn("Could not move " + header.getFile() + " to " + movedFile);

			}
		}
	}

	/**
	 * Removes the transformed file
	 *
	 * @param header
	 */
	@Override
	protected void cleanupTransformedFiles(final BatchHeader header)
	{
		if (header.getTransformedFiles() != null)
		{
			for (final File file : header.getTransformedFiles())
			{
				cleanupFile(file);
			}
		}
	}

	/**
	 * Returns the destination location of the file
	 *
	 * @param file
	 * @param error
	 *           flag indicating if there was an error
	 * @return the destination file
	 */
	@Override
	protected File getDestFile(final File file, final boolean error)
	{
		final StringBuilder builder = new StringBuilder(file.getName());
		if (!StringUtils.isBlank(timeStampFormat))
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(timeStampFormat, Locale.getDefault());
			builder.append(DATE_SEPARATOR);
			builder.append(sdf.format(new Date()));
		}
		return new File(
				error ? BatchDirectoryUtils.getRelativeErrorDirectory(file) : BatchDirectoryUtils.getRelativeArchiveDirectory(file),
				builder.toString());
	}

	/**
	 * @param timeStampFormat
	 *           the timeStampFormat to set
	 */
	@Override
	public void setTimeStampFormat(final String timeStampFormat)
	{
		this.timeStampFormat = timeStampFormat;
	}

	/**
	 *
	 * @return timeStampFormat
	 */
	@Override
	protected String getTimeStampFormat()
	{
		return timeStampFormat;
	}
}

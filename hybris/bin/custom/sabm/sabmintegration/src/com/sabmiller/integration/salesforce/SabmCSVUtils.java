/**
 *
 */
package com.sabmiller.integration.salesforce;


import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.util.CSVReader;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SabmCSVUtils
{

	private static final char DEFAULT_SEPARATOR = ',';
	private static final String REGEX = "^([0-9]{0,10}\\.+[0-9]+|[0-9]{1,10})$";
	private static final String DECIMAL_REGEX = "^[1-9][0-9]*$";
	private static final Logger LOG = LoggerFactory.getLogger(SabmCSVUtils.class);
	private static final String PROP_TEMP_DIR = "temp.dir";
	private static final String CUST_NOTIFICATION_DIR = "notification.report.file.creation.directory";

	private SabmCSVUtils()
	{
		// ignore
	}

	public static void writeLine(final Writer w, final List<String> values, final char quote) throws IOException
	{
		writeLine(w, values, DEFAULT_SEPARATOR, quote);
	}

	// https://tools.ietf.org/html/rfc4180
	private static String followCVSformat(final String value)
	{
		String result = StringUtils.isNotBlank(value) ? value.trim() : StringUtils.EMPTY;

		if (result.matches(REGEX))
		{
			final boolean hasDecimal = result.contains(".") && result.split(Pattern.quote("."))[1].matches(DECIMAL_REGEX);
			if (!hasDecimal)
			{
				final DecimalFormat decimalFormat = new DecimalFormat("0.00");
				result = decimalFormat.format(Double.valueOf(result));
			}
		}

		return result;
	}

	public static void writeLine(final Writer w, final List<String> values, final char separators, final char customQuote)
			throws IOException
	{
		char sep = separators;
		boolean first = true;
		// default customQuote is empty
		if (sep == ' ')
		{
			sep = DEFAULT_SEPARATOR;
		}

		final StringBuilder sb = new StringBuilder();
		for (final String value : values)
		{
			if (!first)
			{
				sb.append(sep);
			}
			if (customQuote == ' ')
			{
				sb.append(value);
			}
			else
			{
				sb.append(customQuote).append(value).append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());
	}

	/** Zip the contents of the directory, and save it in the zipfile */
	public static void zipDirectory(final File d, final String zipfile, final String extensions) throws IOException
	{
		// Check that the directory is a directory, and get its contents

		if (!d.isDirectory())
		{
			throw new IllegalArgumentException("Compress: not a directory:  " + d);
		}

		final String[] entries = d.list();
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile)))
		{
			// Create a stream to compress data and write it to the Zipfile

			// Loop through all entries in the directory
			for (int i = 0; i < entries.length; i++)
			{
				final File f = new File(d, entries[i]);
				if (f.isDirectory() || (f.isFile() && !extensions.contains(FilenameUtils.getExtension(f.getName()))))
				{
					continue;
				}

				writeZipEntry(out, f);
			}
		}
		catch (final IOException e)
		{
			LOG.error("Got exception with zipDirectory:" + e);
			throw e;
		}
	}

	/**
	 * @param out
	 * @param f
	 */
	private static void writeZipEntry(final ZipOutputStream out, final File f) throws IOException
	{
		final byte[] buffer = new byte[4096]; // Create a buffer for copying
		int bytesRead;

		try (FileInputStream in = new FileInputStream(f))
		{
			final ZipEntry entry = new ZipEntry(f.getName());
			out.putNextEntry(entry);

			while ((bytesRead = in.read(buffer)) != -1)
			{
				// Copy bytes
				out.write(buffer, 0, bytesRead);
			}
			// remove file after zipping
			FileUtils.deleteQuietly(f);
		}
		catch (final IOException e)
		{
			LOG.error("Got exception with writeZipEntry: " + e);
			throw e;
		}
	}

	public static File getFullPath(final String filePath)
	{
		final String dataDir = Config.getString(PROP_TEMP_DIR, "/");
		final String directoryFullPath = dataDir + File.separator + filePath + File.separator;


		final File directory = new File(directoryFullPath);
		if (!directory.exists())
		{
			directory.mkdirs();
		}

		return directory;
	}

	public static File getNotificationFullPath(final String filePath)
	{
		final String notificationdataDir = Config.getString(CUST_NOTIFICATION_DIR, "/");
		final String notificationdirectoryFullPath = notificationdataDir + File.separator;

		final File directory = new File(notificationdirectoryFullPath);
		if (!directory.exists())
		{
			directory.mkdirs();
		}

		return directory;
	}


	private static void delete(final Path path, final BiConsumer<Path, Exception> e) {
		try {
			Files.delete(path);
		} catch (final IOException ex) {
			LOG.error("Failed to delete file.");
			e.accept(path, ex);
		}
	}

	public static void purgeOldFiles(final String purgeDir) {

		try {
			final Path path = Path.of(purgeDir);
			Files.list(path)
					.forEach(n -> {
						LOG.info("Deleting file: {}", n);
						delete(n, (t, u) -> LOG.error("Couldn't delete {0}{1}", t, u.getMessage()));
					});
			FileUtils.deleteDirectory(new File("path"));
		} catch (final IOException e) {
			LOG.error("Error occured during purging of archive files. archiveDir:[{}] ", purgeDir, e);
		}
	}

	public static void removeRecursive(final String purgeDir) throws IOException {
		final Path path = Path.of(purgeDir);
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
					throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
				// try to delete the file anyway, even if its attributes
				// could not be read, since delete-only access is
				// theoretically possible
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
				if (exc == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed; propagate exception
					throw exc;
				}
			}
		});
	}

	public static void addAddress(final List<String> unitData, final AddressModel address)
	{
		String streetName = "";
		String streetNumber = "";
		String town = "";
		String state = "";
		String postcode = "";

		if (address != null)
		{
			streetName = address.getStreetname();
			streetNumber = address.getStreetnumber();
			town = address.getTown();
			state = address.getRegion() != null ? address.getRegion().getName() : "";
			postcode = address.getPostalcode();
		}

		unitData.add(trimToEmpty(streetName));
		unitData.add(trimToEmpty(streetNumber));
		unitData.add(trimToEmpty(town));
		unitData.add(trimToEmpty(state));
		unitData.add(trimToEmpty(postcode));


	}


	public static String trimToEmpty(final String s)
	{

		if (StringUtils.isBlank(s) || StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(s), "null"))
		{
			return "";
		}
		return StringUtils.trimToEmpty(s);
	}


	public static void closeReaderQuietly(final CSVReader csvReader)
	{
		LOG.debug("Closing CSVReader");
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

}
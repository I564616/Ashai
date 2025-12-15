package com.apb.core.error.handler;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class AsahiImpexTransformerTask extends ImpexTransformerTask
{

	private static final Logger LOG = LoggerFactory.getLogger(AsahiImpexTransformerTask.class);

	private final String encoding = CSVConstants.HYBRIS_ENCODING;

	@Override
	public BatchHeader execute(final BatchHeader header) throws UnsupportedEncodingException, FileNotFoundException
	{
		Assert.notNull(header, "must not be null");
		Assert.notNull(header.getFile(), "must not be null");
		final File file = header.getFile();
		header.setEncoding(encoding);
		final List<ImpexConverter> converters = getConverters(file);
		int position = 1;
		for (final ImpexConverter converter : converters)
		{
			final File impexFile = getImpexFile(file, position++);
			//getFileContent(impexFile);
			if (convertFile(header, file, impexFile, converter))
			{
				//getFileContent(impexFile);
				header.addTransformedFile(impexFile);
			}
			else
			{
				getCleanupHelper().cleanupFile(impexFile);
			}
		}
		return header;
	}

	void getFileContent(final File file)
	{
		if (null != file)
		{
			try (Stream<String> stream = Files.lines(file.toPath()))
			{
				stream.forEach(System.out::println);
			}
			catch (final IOException e)
			{
				LOG.error("Error has occured", e);
			}
		}
		else
		{
			LOG.warn("File content not found!");
		}
	}


	@Override
	protected boolean convertFile(final BatchHeader header, final File file, final File impexFile, final ImpexConverter converter)
			throws UnsupportedEncodingException, FileNotFoundException
	{
		boolean result = false;
		CSVReader csvReader = null;
		PrintWriter writer = null;
		PrintWriter errorWriter = null;
		OutputStream impexOutputStream = null;

		try
		{
			csvReader = createCsvReader(file);
			impexOutputStream = new FileOutputStream(impexFile);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(impexOutputStream, getEncoding())));
			writer.println(getReplacedHeader(header, converter));
			while (csvReader.readNextLine())
			{
				final Map<Integer, String> row = csvReader.getLine();
				if (converter.filter(row))
				{
					try
					{
						writer.println(converter.convert(row, header.getSequenceId()));
						result = true;
					}
					catch (final IllegalArgumentException exc)
					{
						errorWriter = writeErrorLine(file, csvReader, errorWriter, exc);
						LOG.error("File convertion error ", exc);
					}
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(errorWriter);
			IOUtils.closeQuietly(impexOutputStream);
			closeQuietly(csvReader);
		}
		return result;
	}
}

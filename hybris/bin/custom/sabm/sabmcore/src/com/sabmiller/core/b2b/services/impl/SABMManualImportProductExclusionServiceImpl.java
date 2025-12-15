/**
 *
 */
package com.sabmiller.core.b2b.services.impl;



import de.hybris.platform.util.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SABMManualImportProductExclusionService;




/**
 * DefaultSabmB2BCustomerServiceImpl
 */
public class SABMManualImportProductExclusionServiceImpl implements SABMManualImportProductExclusionService
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BCustomerServiceImpl.class);
	String impexFilesPath = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SABMManualImportProductExclusionService#generateImpexFilesForProductExclusion(java
	 * .lang.String)
	 */
	@Override
	public void generateImpexFilesForProductExclusion(final String sourceFileFolder)
	{
		try
		{
			final File folder = new File(sourceFileFolder);
			if (folder.exists())
			{
				impexFilesPath = sourceFileFolder + Config.getString("core.import.productexclusion.destinationpath", "/impexfiles");
				final File impexFolder = new File(impexFilesPath);
				if (!impexFolder.exists())
				{
					if (impexFolder.mkdir())
					{
						LOG.info(impexFolder.getPath() + " Directory has created successfully");
					}
					else
					{
						LOG.info("Problem In Creating Directory");
					}
				}
				final File[] listOfFiles = folder.listFiles();

				for (int i = 0; i < listOfFiles.length; i++)
				{
					final File file = listOfFiles[i];
					if (file.isFile() && (file.getName().endsWith(".txt") || file.getName().endsWith(".TXT")))
					{
						convertToImpex(file);
						if (file.delete())
						{
							LOG.info("Deleted TXT Raw File : " + file.getName());
						}
						else
						{
							LOG.info("Problem In Deleting TXT Raw File : " + file.getName());
						}

					}
				}
			}
			else
			{
				LOG.info("Folder not Exists");
			}
		}
		catch (final Exception e)
		{
			LOG.info("Problem in Reading File");
		}

	}

	private void convertToImpex(final File file)
	{

		String fileName = null;
		final List<StringBuffer> finalimpexes = new ArrayList<StringBuffer>();
		try
		{
			fileName = file.getName();
			final FileReader fileReader = new FileReader(file);
			final BufferedReader bufferedReader = new BufferedReader(fileReader);

			int count = 1;
			for (String line; (line = bufferedReader.readLine()) != null;)
			{
				if (count <= 3)
				{
					count++;
					continue;
				}
				int columnCount = 1;
				final StringBuffer finalimpexline = new StringBuffer();

				for (int i = 0; i < line.length(); i++)
				{
					final int indexFirstPipeline = line.indexOf('|', i);
					if (indexFirstPipeline != -1 && indexFirstPipeline + 1 < line.length())
					{
						final int indexSecondPipeline = line.indexOf('|', indexFirstPipeline + 1);
						if (indexSecondPipeline != -1 && indexSecondPipeline < line.length())
						{
							String column = line.substring(indexFirstPipeline + 1, indexSecondPipeline);
							column = column.replaceAll("\\s+", "");

							switch (columnCount)
							{
								case 3:
									final StringBuffer tempCustomer = new StringBuffer(";");
									for (int k = 1; k <= 10 - column.length(); k++)
									{
										tempCustomer.append('0');
									}
									tempCustomer.append(column + ";");
									finalimpexline.append(tempCustomer);
									break;
								case 4:
									final StringBuffer tempProduct = new StringBuffer();
									for (int k = 1; k <= 18 - column.length(); k++)
									{
										tempProduct.append('0');
									}
									tempProduct.append(column + ";");
									finalimpexline.append(tempProduct);
									break;
								case 5:
									final StringBuffer tempFromDate = new StringBuffer();
									final StringBuffer tempcolumn = new StringBuffer(column);
									tempFromDate.append(tempcolumn.substring(tempcolumn.lastIndexOf(".") + 1));
									tempcolumn.delete(tempcolumn.lastIndexOf("."), tempcolumn.length());
									tempFromDate.append(tempcolumn.substring(tempcolumn.lastIndexOf(".") + 1));
									tempcolumn.delete(tempcolumn.lastIndexOf("."), tempcolumn.length());
									tempFromDate.append(tempcolumn);
									finalimpexline.append(tempFromDate + ";");
									break;
								case 6:

									final StringBuffer tempToDate = new StringBuffer();
									final StringBuffer tempToDatecolumn = new StringBuffer(column);
									tempToDate.append(tempToDatecolumn.substring(tempToDatecolumn.lastIndexOf(".") + 1));
									tempToDatecolumn.delete(tempToDatecolumn.lastIndexOf("."), tempToDatecolumn.length());
									tempToDate.append(tempToDatecolumn.substring(tempToDatecolumn.lastIndexOf(".") + 1));
									tempToDatecolumn.delete(tempToDatecolumn.lastIndexOf("."), tempToDatecolumn.length());
									tempToDate.append(tempToDatecolumn);
									finalimpexline.append(tempToDate + ";;");
									break;
							}
							i = indexSecondPipeline - 1;
							columnCount++;
						}

					}
				}
				finalimpexes.add(finalimpexline);
			}
			fileReader.close();
		}
		catch (final IOException e)
		{
			LOG.info("Problem in Reading from Raw file");
		}

		try
		{

			final String impexFile = impexFilesPath + "/" + fileName.substring(0, fileName.lastIndexOf('.')) + ".impex";
			final FileWriter fileWriter = new FileWriter(impexFile);
			final String impexHeader = "INSERT_UPDATE ProductExclusion;customer(uid)[unique=true];product[unique=true];validFrom[dateformat=yyyyMMdd];validTo[dateformat=yyyyMMdd];salesOrg";
			fileWriter.append(impexHeader);
			fileWriter.append("\n");
			for (final StringBuffer impex : finalimpexes)
			{
				fileWriter.append(impex);
				fileWriter.append("\n");

			}


			fileWriter.flush();
			fileWriter.close();
			LOG.info("File has generated==>" + fileName.substring(0, fileName.lastIndexOf('.')) + ".impex");

		}
		catch (final IOException e)
		{
			LOG.info("Problem in writing into impex file");
		}
	}




}

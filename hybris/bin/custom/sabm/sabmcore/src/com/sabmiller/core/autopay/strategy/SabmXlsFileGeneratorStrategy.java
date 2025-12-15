package com.sabmiller.core.autopay.strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author marc.f.l.bautista
 *
 */
public interface SabmXlsFileGeneratorStrategy<T>
{
	static final Logger LOG = LoggerFactory.getLogger(SabmXlsFileGeneratorStrategy.class);

	static final String XLSX_FILE_EXTENSION = ".xlsx";

	static final String DATE_PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_YYYYMMDDHHMMSS);


	/**
	 * Gets the file path for the XLSX file
	 *
	 * @return File
	 */
	File getFilePath();


	/**
	 * Gets the file name for the XLSX file
	 *
	 * @return String
	 */
	String getFileName();


	/**
	 * Gets the workbook sheet title
	 *
	 * @return String
	 */
	String getSheetTitle();


	/**
	 * Gets header texts for the XLSX file
	 *
	 * @return List<String>
	 */
	List<String> getHeaderLine();


	/**
	 * Generates the XLSX file
	 *
	 * @param dataSource
	 * @return File
	 */
	File generateXlsxFile(final List<T> dataSource) throws Exception;


	/**
	 * Creates the excel file in XLSX format
	 *
	 * @param dataSource
	 * @return File
	 */
	default File createExcelSheet(final List<List<Object>> dataSource) throws Exception
	{
		// Create blank workbook
		final XSSFWorkbook workbook = new XSSFWorkbook();
		// Create blank sheet
		final XSSFSheet spreadsheet = workbook.createSheet(getSheetTitle());
		// Declare row & cell
		XSSFRow row;
		XSSFCell cell;

		// Initialize the row & column numbers
		int rowid = 0;
		int cellid = 0;

		// Create the header row
		row = spreadsheet.createRow(rowid++);
		for (final String data : getHeaderLine())
		{
			cell = row.createCell(cellid++);
			cell.setCellValue(data);
		}

		// Create the record data rows
		for (final List<Object> record : dataSource)
		{
			row = spreadsheet.createRow(rowid++);
			cellid = 0;
			for (final Object data : record)
			{
				cell = row.createCell(cellid++);
				if (data instanceof Double)
				{
					cell.setCellValue((Double) data);
				}
				else
				{
					cell.setCellValue(Objects.nonNull(data) ? String.valueOf(data) : "");
				}
			}
		}

		// Create/save the workbook/file
		final File file = new File(getFilePath(), getFileName() + " " + sdf.format(new Date()) + XLSX_FILE_EXTENSION);
		FileOutputStream out;
		try
		{
			out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
		}
		catch (final FileNotFoundException e)
		{
			LOG.error("File creation exception", e);
			throw e;
		}
		catch (final IOException e)
		{
			LOG.error("Workbook creation exception", e);
			throw e;
		}
		catch (final Exception e)
		{
			throw e;
		}
		LOG.info(getFileName() + " created successfully.");

		return file;
	};
}
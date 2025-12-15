package com.apb.facades.sam.statement.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.statements.AsahiSAMStatementsService;
import com.apb.core.util.AsahiAdhocCoreUtil;
import com.apb.facades.sam.data.AsahiSAMStatementData;
import com.apb.facades.sam.data.AsahiSAMStatementMonthData;
import com.apb.facades.sam.data.AsahiSAMStatementPageData;
import com.apb.facades.sam.statement.AsahiSAMStatementFacade;
import com.apb.integration.data.AsahiStatementDownloadResponse;
import com.apb.integration.data.AsahiStatementDownloadResponseDTO;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMStatementsModel;


/**
 * The Class AsahiSAMStatementFacadeImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMStatementFacadeImpl implements AsahiSAMStatementFacade
{

	private static final Logger LOG = Logger.getLogger(AsahiSAMStatementFacadeImpl.class);
	private static final String STATEMENT_RESULT_CURRENT_YEAR = "current";
	private static final String STATEMENT_RESULT_LAST_YEAR = "last";
	private static final String STATEMENT_RESULT_PREVIOUS_YEAR = "previous";
	private static final String STATEMENT_FY_DISPLAY_CONSTANT = " / ";
	private static final SimpleDateFormat cofoDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMStatementData, AsahiSAMStatementsModel> asahiSAMStatementsReverseConverter;

	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMStatementsModel, AsahiSAMStatementData> asahiSAMStatementsConverter;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The asahi SAM statements service. */
	@Resource
	private AsahiSAMStatementsService asahiSAMStatementsService;

	@Resource
	private UserService userService;

	/** The asahi integration points service. */
	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

	private AsahiConfigurationService asahiConfigurationService;


    @Resource(name = "adhocCoreUtil")
    private AsahiAdhocCoreUtil adhocCoreUtil;

	/**
	 * Import statements.
	 *
	 * @param statementData
	 *           the statement data
	 */
	@Override
	public void importStatements(final AsahiSAMStatementData statementData)
	{

		// Fetching Statement based on document number
		AsahiSAMStatementsModel existingStatement = this.asahiSAMStatementsService.getStatementByNumber(statementData
				.getStatementNumber());
		/* Check if Statement already exist in hybris if yes then update otherwise create new. */
		if (null != existingStatement)
		{
			// update existing Statement
			// calling converter to populate the AsahiSAMStatementsModel
			existingStatement = this.asahiSAMStatementsReverseConverter.convert(statementData, existingStatement);
			// saving existing Invoice into hybris database
			this.modelService.save(existingStatement);
		}
		else
		{
			//create new Statement in hybris
			AsahiSAMStatementsModel newStatement = this.modelService.create(AsahiSAMStatementsModel.class);

			//calling converter to populate the AsahiSAMStatementsModel
			newStatement = this.asahiSAMStatementsReverseConverter.convert(statementData, newStatement);

			//saving new Statement into hybris database
			this.modelService.save(newStatement);
		}
	}

	/**
	 * Get statements data on page load.
	 *
	 * @return AsahiSAMStatementPageData the Statements details
	 */
    @Override
	 public AsahiSAMStatementPageData getStatements()
	 {
        final Map<String, Map<Integer, AsahiSAMStatementMonthData>> map = new HashMap<>();
        final AsahiSAMStatementPageData statementPageData = new AsahiSAMStatementPageData();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - getAsahiConfigurationService().getInt("sam.statement.download.enable.after.month", 0));
        //After month june enable statement download
        if (calendar.get(Calendar.MONTH) > Calendar.JUNE) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        }

		  final Calendar cofoDateCalendar = getCofoCalanderTime(cofoDateFormat);
		  final Boolean isWithinDateRange = verifyDateRange(cofoDateCalendar, calendar, 1);
		  final Boolean isExcluded = !isWithinDateRange;
        //Prepare the map holding the months data for FY...
		  map.put(STATEMENT_RESULT_CURRENT_YEAR, getFYMonthData(calendar.get(Calendar.YEAR) - 1, isWithinDateRange, isExcluded));
        statementPageData.setCurrentYear(getFYDisplayValue(calendar.get(Calendar.YEAR)));

		  map.put(STATEMENT_RESULT_LAST_YEAR,
				  getFYMonthData(calendar.get(Calendar.YEAR) - 2,
						  isWithinDateRange ? !isWithinDateRange : verifyDateRange(cofoDateCalendar, calendar, 2),
						  isWithinDateRange ? !isWithinDateRange : !verifyDateRange(cofoDateCalendar, calendar, 2)));
        statementPageData.setLastYear(getFYDisplayValue(calendar.get(Calendar.YEAR) - 1));

		  map.put(STATEMENT_RESULT_PREVIOUS_YEAR,
				  getFYMonthData(calendar.get(Calendar.YEAR) - 3,
						  isWithinDateRange ? !isWithinDateRange : verifyDateRange(cofoDateCalendar, calendar, 3),
						  isWithinDateRange ? !isWithinDateRange : !verifyDateRange(cofoDateCalendar, calendar, 3)));
        statementPageData.setPreviousYear(getFYDisplayValue(calendar.get(Calendar.YEAR) - 2));

        statementPageData.setMonths(map);

        return statementPageData;
    }

	/**
	 * @param cofoDateFormat
	 * @return
	 */
	private Calendar getCofoCalanderTime(final SimpleDateFormat cofoDateFormat)
	{
		final Calendar cofoDateCalendar = Calendar.getInstance();

		if (null != getCofoDate() && StringUtils.isNotBlank(getCofoDate()))
		{
			Date formattedCfDate = null;
			try
			{
				formattedCfDate = cofoDateFormat.parse(getCofoDate().replace("-", "/"));
				cofoDateCalendar.setTime(formattedCfDate);
			}
			catch (final ParseException e1)
			{
				// YTODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return cofoDateCalendar;
	}

	/**
	 * @return
	 */
	private String getCofoDate()
	{
		final UserModel user = this.userService.getCurrentUser();
		final B2BCustomerModel b2bCustModel = (B2BCustomerModel) user;
		final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) b2bCustModel.getDefaultB2BUnit();
		final String cofoDate = b2bUnit.getCooDate();
		return cofoDate;
	}

	/**
	 * @param calendar
	 * @param i
	 */
	private final Boolean verifyDateRange(final Calendar cofoDateCalendar, final Calendar calendar, final int i)
	{
		final Calendar calendarStart = Calendar.getInstance();
		calendarStart.set(calendar.get(Calendar.YEAR) - i, Calendar.JULY, 1);
		final Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.set(calendar.get(Calendar.YEAR) - (i - 1), Calendar.JUNE, 1);

		if ((cofoDateCalendar.after(calendarStart) && (cofoDateCalendar.before(calendarEnd)))
				|| (cofoDateCalendar.equals(calendarStart) || cofoDateCalendar.equals(calendarEnd)))
		{
			return true;
		}
		return false;
	}

	/**
	 * The Method will prepare the FY display variable on the statement page.
	 *
	 * @param year
	 *           the FY year
	 * @return formatted String
	 */
	private final String getFYDisplayValue(final int year)
	{
		return String.valueOf(year-1).substring(2) + STATEMENT_FY_DISPLAY_CONSTANT + String.valueOf(year).substring(2);

	}

	/**
	 * The Method will return the true/false for the statement download link.
	 *
	 * @return true/false
	 */
	private final int getStatementEnableDay()
	{

		int configuredDay = getAsahiConfigurationService().getInt("sam.statement.download.day.enable", 6);
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, configuredDay);

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		{
			configuredDay = configuredDay + 2;
		}
		else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		{
			configuredDay = configuredDay + 1;
		}
		return configuredDay;
	}

	/**
	 * The Method will prepare the Month and year display data for FY TODO:Move to populator
	 *
	 * @param year
	 *           the FY year
	 * @param isExcluded
	 * @param b
	 * @param cofoDateCalendar
	 * @return List<AsahiSAMStatMonthData>
	 */
	private final Map<Integer, AsahiSAMStatementMonthData> getFYMonthData(final int year, final Boolean isWithinDateRange,
			final Boolean isExcluded)
	{
        final int startYear = getAsahiConfigurationService().getInt("sam.statement.start.year", 2017) - 1;
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        final SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM");
        final Calendar currentDate = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();
        List<AsahiSAMStatementMonthData> months;
        AsahiSAMStatementMonthData month;
		  Boolean isvalid = false;

		  final Calendar cofoDateCalendar = getCofoCalanderTime(cofoDateFormat);


		  final Calendar calendarStart = Calendar.getInstance();
		  calendarStart.set(year, Calendar.JULY, 1);
		  final Calendar calendarEnd = Calendar.getInstance();
		  calendarEnd.set(calendar.get(Calendar.YEAR), Calendar.JUNE, 1);

		  if ((cofoDateCalendar.after(calendarStart) && (cofoDateCalendar.before(calendarEnd)))
				  || (cofoDateCalendar.equals(calendarStart) || cofoDateCalendar.equals(calendarEnd)))
		  {
			  isvalid = true;
		  }


        if (year >= startYear) {
            //current or last year
            if (currentDate.get(Calendar.YEAR) - year == 0 || currentDate.get(Calendar.YEAR) - year == 1)
				{
					currentDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH)
							- getAsahiConfigurationService().getInt("sam.statement.download.enable.after.month", 0));
				}

            months = new ArrayList<>();
            for (int counter = 0; counter <= 11; counter++) {
                month = new AsahiSAMStatementMonthData();
					 calendar.set(year, Calendar.JULY + counter, Calendar.DAY_OF_MONTH);
                if (calendar.get(Calendar.MONTH) == 11) {
                    month.setQueryYear(calendar.get(Calendar.YEAR) + 1);
                    month.setDisplayYear(calendar.get(Calendar.YEAR));
                    month.setCode(1);
                } else {
                    month.setQueryYear(calendar.get(Calendar.YEAR));
                    month.setDisplayYear(calendar.get(Calendar.YEAR));
                    month.setCode(calendar.get(Calendar.MONTH) + 2);
                }

                month.setName(displayFormat.format(calendar.getTime()));

                //Set the link disable or enable...
                try {
						 final Boolean isCurrentDate = calendar.before(currentDate);
						 final Boolean iscofoDate = calendar.after(cofoDateCalendar)
								 && calendar.before(currentDate);
						 final Boolean isStatementDay = calendar.equals(currentDate)
								 && currentDate.get(Calendar.DAY_OF_MONTH) >= getStatementEnableDay();
						 final Boolean isStatementDayForCofo = calendar.get(Calendar.MONTH) == cofoDateCalendar.get(Calendar.MONTH)
								 && calendar.get(Calendar.YEAR) == cofoDateCalendar.get(Calendar.YEAR);
						 if (null != getCofoDate() && StringUtils.isNotBlank(getCofoDate()))
						 {
							 if (!isvalid)
							 {
								 validateLink(sdf, currentDate, calendar, month, isCurrentDate, isStatementDayForCofo);
							 }
							 else
							 {
								 if (!isExcluded)
								 {
									 validateLink(sdf, cofoDateCalendar, calendar, month, iscofoDate, isStatementDayForCofo);
								 }
								 else
								 {
									 month.setLink(false);
								 }
							 }
						 }
						 else
						 {
							 validateLink(sdf, currentDate, calendar, month, isCurrentDate, isStatementDay);
						 }
                } catch (final Exception e) {
                    LOG.error("Unable to parse the date for statement" + e.getMessage());
					  }

                months.add(month);
            }
            if (CollectionUtils.isNotEmpty(months)) {
                Collections.reverse(months);
					 final Map<Integer, AsahiSAMStatementMonthData> monthMap = new LinkedHashMap<>();
                months.forEach(data -> monthMap.put(data.getCode(), data));
                return monthMap;
            }
        }
        return null;
    }

	/**
	 * @param sdf
	 * @param currentDate
	 * @param calendar
	 * @param month
	 * @param calendar
	 * @throws ParseException
	 */
	private void validateLink(final SimpleDateFormat sdf, final Calendar currentDate, final Calendar calendar,
			final AsahiSAMStatementMonthData month, final Boolean condition, final Boolean isStatement) throws ParseException
	{

		if (condition)
		{
			month.setLink(true);
		}
		else if (isStatement)
		{
			month.setLink(true);
		}
		else
		{
			month.setLink(false);
		}
	}

	/**
	 * @return the asahiSAMStatementsReverseConverter
	 */
	public Converter<AsahiSAMStatementData, AsahiSAMStatementsModel> getAsahiSAMStatementsReverseConverter()
	{
		return asahiSAMStatementsReverseConverter;
	}

	/**
	 * @param asahiSAMStatementsReverseConverter
	 *           the asahiSAMStatementsReverseConverter to set
	 */
	public void setAsahiSAMStatementsReverseConverter(
			final Converter<AsahiSAMStatementData, AsahiSAMStatementsModel> asahiSAMStatementsReverseConverter)
	{
		this.asahiSAMStatementsReverseConverter = asahiSAMStatementsReverseConverter;
	}

	/**
	 * @return
	 */
	public Converter<AsahiSAMStatementsModel, AsahiSAMStatementData> getAsahiSAMStatementsConverter()
	{
		return asahiSAMStatementsConverter;
	}

	/**
	 * @param asahiSAMStatementsConverter
	 */
	public void setAsahiSAMStatementsConverter(
			final Converter<AsahiSAMStatementsModel, AsahiSAMStatementData> asahiSAMStatementsConverter)
	{
		this.asahiSAMStatementsConverter = asahiSAMStatementsConverter;
	}

	/**
	 * Gets the invoice pdf.
	 *
	 * @return the invoice pdf
	 */
	@Override
	public AsahiStatementDownloadResponse getStatementPdf(final String statementMonth, final String statementYear)
	{
		final AsahiStatementDownloadResponseDTO resposne = this.asahiIntegrationPointsService.getStatementPdf(statementMonth,
				statementYear);
		if (null != resposne && null != resposne.getStatementDownloadResponse())
		{
			return resposne.getStatementDownloadResponse();
		}
		return null;
	}

    public AsahiConfigurationService getAsahiConfigurationService() {
        return asahiConfigurationService;
    }

    public void setAsahiConfigurationService(final AsahiConfigurationService asahiConfigurationService) {
        this.asahiConfigurationService = asahiConfigurationService;
    }
}

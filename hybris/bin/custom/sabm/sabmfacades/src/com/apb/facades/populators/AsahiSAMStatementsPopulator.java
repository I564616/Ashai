package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;

import java.text.SimpleDateFormat;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.AsahiSAMStatementsModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.sam.data.AsahiSAMStatementData;


/**
 * The Class AsahiSAMStatementsReversePopulator.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMStatementsPopulator implements Populator<AsahiSAMStatementsModel, AsahiSAMStatementData>
{

	/** The logger. */
	final Logger logger = LoggerFactory.getLogger(AsahiSAMStatementsPopulator.class);

	/** The Constant STATEMENT_DATE_FORMAT. */
	private static final String STATEMENT_DATE_FORMAT = "site.statement.date.format.sga";

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;


	/**
	 * Populate.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	@Override
	public void populate(final AsahiSAMStatementsModel source, final AsahiSAMStatementData target)
	{

		//Getting SGA Site Date Format
		final SimpleDateFormat format = new SimpleDateFormat(this.asahiConfigurationService.getString(STATEMENT_DATE_FORMAT,
				"MMMM yyyy"));

		target.setStatementBalance(source.getStatementBalance());
		target.setStatementNumber(source.getStatementNumber());
		target.setStatementPeriod(format.format(source.getStatementPeriod()));
	}
}

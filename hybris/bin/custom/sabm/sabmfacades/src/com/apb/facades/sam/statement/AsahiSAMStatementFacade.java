package com.apb.facades.sam.statement;

import com.apb.facades.sam.data.AsahiSAMStatementData;
import com.apb.facades.sam.data.AsahiSAMStatementPageData;
import com.apb.integration.data.AsahiStatementDownloadResponse;


/**
 * The Interface AsahiSAMStatementFacade.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMStatementFacade
{

	/**
	 * Import statements.
	 *
	 * @param statementData
	 *           the statement data
	 */
	void importStatements(AsahiSAMStatementData statementData);

	/**
	 * Get statements.
	 *
	 * @return AsahiSAMStatementPageData The Statements details
	 */
	AsahiSAMStatementPageData getStatements();

	/**
	 * Gets the statement pdf.
	 *
	 * @param statementMonth
	 *           the statement month
	 * @param statementYear
	 *           the statement year
	 * @return the statement pdf
	 */
	AsahiStatementDownloadResponse getStatementPdf(String statementMonth, String statementYear);
}

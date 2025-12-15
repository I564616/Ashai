package com.apb.core.service.sam.statements;

import com.sabmiller.core.model.AsahiSAMStatementsModel;


/**
 * The Interface AsahiSAMStatementsService.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMStatementsService
{

	/**
	 * Gets the statement by number.
	 *
	 * @param number
	 *           the number
	 * @return the invoice by document number
	 */
	AsahiSAMStatementsModel getStatementByNumber(String number);

}

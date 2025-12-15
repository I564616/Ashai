package com.apb.core.dao.sam.statements;

import com.sabmiller.core.model.AsahiSAMStatementsModel;


/**
 * The Interface AsahiSAMStatementsDao.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMStatementsDao
{

	/**
	 * Gets the invoice by document number.
	 *
	 * @param documentNumber
	 *           the document number
	 * @return the invoice by document number
	 */
	AsahiSAMStatementsModel getStatementByNumber(String number);

}

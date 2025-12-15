package com.apb.core.service.sam.statements.impl;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.apb.core.dao.sam.statements.AsahiSAMStatementsDao;
import com.sabmiller.core.model.AsahiSAMStatementsModel;
import com.apb.core.service.sam.statements.AsahiSAMStatementsService;


/**
 * The Class AsahiSAMStatementsServiceImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMStatementsServiceImpl implements AsahiSAMStatementsService
{

	private static final Logger LOG = Logger.getLogger(AsahiSAMStatementsServiceImpl.class);
	/** The asahi SAM statements dao. */
	@Resource
	private AsahiSAMStatementsDao asahiSAMStatementsDao;

	/**
	 * Gets the statement by number.
	 *
	 * @param number
	 *           the number
	 * @return the invoice by document number
	 */
	@Override
	public AsahiSAMStatementsModel getStatementByNumber(final String number)
	{
		return this.asahiSAMStatementsDao.getStatementByNumber(number);
	}

}

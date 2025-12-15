package com.sabmiller.core.email.service;

import java.io.File;

/**
 * @author marc.f.l.bautista
 *
 */
public interface SabmEmailService
{
	/**
	 * Sends an email with the XLSX file containing the list of orders paid using credit card
	 * 
	 * @param file
	 */
	void sendOrdersByCreditCardPaymentEmail(final File file)  throws Exception;

	void sendCreditAdjustmentEmailToSupportTeam(final File file,final String sftpDirectory) throws Exception;
}
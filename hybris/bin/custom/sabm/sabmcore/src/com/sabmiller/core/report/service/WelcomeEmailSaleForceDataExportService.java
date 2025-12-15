package com.sabmiller.core.report.service;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;

/**
 * Created by zhuo.a.jiang on 15/12/2017.
 */
public interface WelcomeEmailSaleForceDataExportService
{
	/**
	 * @param string
	 */
	void generateReport(String string) throws IOException, PGPException, NoSuchProviderException;
	
	boolean sendWelcomeEmail()throws SABMSFIntegrationException;

}

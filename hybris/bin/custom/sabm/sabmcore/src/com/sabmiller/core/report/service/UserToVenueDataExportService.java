package com.sabmiller.core.report.service;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.io.File;

import org.bouncycastle.openpgp.PGPException;

import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;

/**
 * Author: Himanshu.Kumar
 * Created as part of HybrisSVOC Project
 */
public interface UserToVenueDataExportService
{
	/**
	 * @param string
	 */
	void generateReport(String string) throws IOException, PGPException, NoSuchProviderException, Exception;

}

package com.sabmiller.core.report.service;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

/**
 * Created by zhuo.a.jiang on 15/12/2017.
 */
public interface VenueReportService {
    void generateReport(String strategy) throws IOException, PGPException, NoSuchProviderException, Exception;


}

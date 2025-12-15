package com.sabmiller.core.report.service;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

/**
 * Created by zhuo.a.jiang on 14/12/2017.
 */
public interface CustomerReportService {

    void generateReport(String strategy)  throws IOException, PGPException, NoSuchProviderException,Exception;

}

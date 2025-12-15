package com.sabmiller.core.report.service;

import java.io.IOException;
import java.security.NoSuchProviderException;
import org.bouncycastle.openpgp.PGPException;
/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public interface ProductReportService {
    void generateReport(String strategy)  throws IOException, PGPException, NoSuchProviderException,Exception;

}

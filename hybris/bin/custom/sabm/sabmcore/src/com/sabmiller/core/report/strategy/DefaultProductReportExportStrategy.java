package com.sabmiller.core.report.strategy;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public interface DefaultProductReportExportStrategy {
    /*
     get header for CSV file export
     */
    List<String> getHeaderLine(final List<String> headers);

    /*
     get a list of String for each Customer, each line/string represent one customer
     */
    List<List<String>> getProductReportData();

    /*
    upload File to SFTP
     */
    void uploadFileToSFTP()  throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException;
}

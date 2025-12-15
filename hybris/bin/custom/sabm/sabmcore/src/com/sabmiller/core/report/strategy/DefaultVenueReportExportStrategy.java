package com.sabmiller.core.report.strategy;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * Created by zhuo.a.jiang on 15/12/2017.
 */
public interface DefaultVenueReportExportStrategy {

    /*
     get header for CSV file export
     */
    List<String> getHeaderLine(final List<String> headers);

    /*
     get a list of String for each Customer, each line/string represent one customer
     */
    List<List<String>> getVenueReportData();

    /*
    upload File to SFTP
     */
    void uploadFileToSFTP()  throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException;

}

package com.sabmiller.core.report.strategy;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
/**
 * Author: Himanshu.Kumar
 * Created as part of HybrisSVOC Project
 */
public interface DefaultUserToVenueDataExportStrategy {
    /*
    get header for CSV file export
     */
    List<String> getHeaderLine(final List<String> headers);

    /*
     get a list of String for each Customer, each line/string represent one customer
     */
    List<List<String>> getUserToVenueReportData();

    /*
    upload File to SFTP
     */
    void uploadFileToSFTP()  throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException;

}
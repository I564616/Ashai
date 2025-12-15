/**
 *
 */
package com.sabmiller.commons.email.service;

import java.io.File;
import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


/**
 * @author Siddarth
 *
 */
public interface SabmSFTPService
{
	 File getCSVFile(String fileName) throws JSchException, SftpException, IOException;

	 void uploadCSVFile(final File file,final String sftpDirectory) throws JSchException, SftpException, IOException;
}

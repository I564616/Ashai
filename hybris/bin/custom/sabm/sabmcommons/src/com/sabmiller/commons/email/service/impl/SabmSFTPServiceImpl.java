/**
 *
 */
package com.sabmiller.commons.email.service.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;


/**
 * @author Siddarth
 *
 */
public class SabmSFTPServiceImpl implements SabmSFTPService
{
	private final String HOSTNAME = "sabm.sftp.hostname";
	private final String USERNAME = "sabm.sftp.username";
	private final String PASSWORD = "sabm.sftp.password";
	private final String SFTPDIRECTORY = "sabm.sftp.directory";
	private final String SFTPARCHIVEDIRECTORY = "sabm.sftp.archive.directory";
	private final static String SFTPCHANNEL = "sftp";
	private final String HOSTKEYCHECKINGRULE = "StrictHostKeyChecking";
	private final String FILETIMESTAMPFORMAT = "ddMMyyyy-HHmm'.csv'";
	private final static String PRIVATE_KEY = "sabm.sftp.privatekey.location";
	private final static String PASSPHRASE = "sabm.sftp.passphrase";
	private ConfigurationService configurationService;
	private static final Logger LOG = LoggerFactory.getLogger(SabmSFTPServiceImpl.class);

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public File getCSVFile(final String fileName) throws JSchException, SftpException, IOException
	{
		final JSch jsch = new JSch();
		final String privateKey = getConfigurationService().getConfiguration().getString(PRIVATE_KEY);
		if (StringUtils.isNotBlank(privateKey))
		{
				jsch.addIdentity(privateKey,
						getConfigurationService().getConfiguration().getString(PASSPHRASE));
		}

		final java.util.Properties config = new java.util.Properties();
		config.put(HOSTKEYCHECKINGRULE, "no");

		final Session session = jsch.getSession(getConfigurationService().getConfiguration().getString(USERNAME),
				getConfigurationService().getConfiguration().getString(HOSTNAME));
		session.setConfig(config);
		if (StringUtils.isBlank(privateKey))
		{
			session.setPassword(getConfigurationService().getConfiguration().getString(PASSWORD));
		}
		session.connect();
		LOG.info("Connecting to SFTP location:" + session.getHost());
		LOG.info("Retrieving File:" + fileName);
		final Channel channel = session.openChannel(SFTPCHANNEL);
		channel.connect();
		final String rootDirectory = getConfigurationService().getConfiguration().getString(SFTPDIRECTORY);
		final ChannelSftp sftpChannel = (ChannelSftp) channel;
		sftpChannel.cd(rootDirectory);

		//Copying contents to a TEMP file
		final File targetFile = File.createTempFile(fileName, ".tmp");
		FileUtils.copyInputStreamToFile(sftpChannel.get(fileName), targetFile);

		//Archive File
		final String fileTimeStamp = new SimpleDateFormat(FILETIMESTAMPFORMAT).format(new Date());
		sftpChannel.rename(rootDirectory + fileName,
				getConfigurationService().getConfiguration().getString(SFTPARCHIVEDIRECTORY) + fileTimeStamp);

		LOG.debug("Temp File details:" + targetFile.getAbsolutePath());

		sftpChannel.exit();
		channel.disconnect();
		session.disconnect();

		return targetFile;
	}


    public void uploadCSVFile(final File file,final String sftpDirectory) throws JSchException, SftpException, IOException
    {
        final JSch jsch = new JSch();
		  final String privateKey = getConfigurationService().getConfiguration().getString(PRIVATE_KEY);
		  if (StringUtils.isNotBlank(privateKey))
		  {
			  jsch.addIdentity(privateKey, getConfigurationService().getConfiguration().getString(PASSPHRASE));
		  }
        final java.util.Properties config = new java.util.Properties();
        config.put(HOSTKEYCHECKINGRULE, "no");

        final Session session = jsch.getSession(getConfigurationService().getConfiguration().getString(USERNAME),
                getConfigurationService().getConfiguration().getString(HOSTNAME));
        session.setConfig(config);
		  if (StringUtils.isBlank(privateKey))
		  {
			  session.setPassword(getConfigurationService().getConfiguration().getString(PASSWORD));
		  }

        session.connect();
        LOG.info("Connecting to SFTP location:" + session.getHost());
        LOG.info("Uploading File:" + file.getName() + " to folder: "+ sftpDirectory);
        final Channel channel = session.openChannel(SFTPCHANNEL);
        channel.connect();


        final ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(sftpDirectory);

        final FileInputStream fileInputStream = new FileInputStream(file);
        sftpChannel.put(fileInputStream,file.getName());

        fileInputStream.close();
        sftpChannel.exit();
        channel.disconnect();
        session.disconnect();


    }

}

package com.sabmiller.core.email.service.impl;

import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.email.service.SabmEmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author marc.f.l.bautista
 */
public class DefaultSabmEmailService implements SabmEmailService {
    @Resource(name = "emailService")
    private SystemEmailService systemEmailService;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "catalogVersionService")
    private CatalogVersionService catalogVersionService;

    @Resource(name = "sabmSFTPService")
    private SabmSFTPService sabmSFTPService;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmEmailService.class);

    /**
     * Sends an email with the XLSX file containing the list of orders paid using credit card
     *
     * @param file
     */
    @Override
    public void sendOrdersByCreditCardPaymentEmail(final File file) throws Exception {
        final String fromEmailAddress = configurationService.getConfiguration()
                .getString(SabmCoreConstants.AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_FROM);
        final String toEmailAddresses = configurationService.getConfiguration()
                .getString(SabmCoreConstants.AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_TO);
        final String subject = configurationService.getConfiguration()
                .getString(SabmCoreConstants.AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_SUBJECT);
        final String message = configurationService.getConfiguration()
                .getString(SabmCoreConstants.AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_MESSAGE);
        final List<String> messages = new ArrayList<>();
        messages.add(message);

        try {
            // Setting catalog in session for emailAttachment
            catalogVersionService.setSessionCatalogVersion(Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
                    Config.getString("email.attachment.default.catalog.version", "Staged"));

            // Creating attachment for the email with generated report file.
            final FileInputStream fis = new FileInputStream(file);
            final EmailAttachmentModel emailAttachment = systemEmailService
                    .createEmailAttachment(new DataInputStream(fis), file.getName(), "text/xlsx");

            SystemEmailMessageModel systemEmailMessageModel = null;
            if (toEmailAddresses.contains(",") || toEmailAddresses.contains(";")) {
                systemEmailMessageModel = systemEmailService.constructSystemEmailForMultipleRecepients(fromEmailAddress, toEmailAddresses,
                        SabmCoreConstants.AUTOPAY_EMAIL_FROM_NAME_SAP_CREDIT_TEAM, subject, messages,
                        Collections.singletonList(emailAttachment));
            } else {
                systemEmailMessageModel = systemEmailService
                        .constructSystemEmail(fromEmailAddress, toEmailAddresses, SabmCoreConstants.AUTOPAY_EMAIL_FROM_NAME_SAP_CREDIT_TEAM,
                                subject, messages, Collections.singletonList(emailAttachment));
            }

            systemEmailService.send(systemEmailMessageModel);
        } catch (Exception e) {
            throw e;
        }
    }

    public void sendCreditAdjustmentEmailToSupportTeam(final File file, final String sftpDirectory) throws Exception {

        final String fromEmail = Config.getString("creditadjustment.salessupport.email.fromEmail", null);
        final String emailName = Config.getString("creditadjustment.salessupport.email.displayName", null);
        final String toEmail = Config.getString("creditadjustment.salessupport.email.toEmail", null);
        final String toEmail2 = Config.getString("creditadjustment.salessupport.email.toEmail2", null);
        final String subject = Config.getString("creditadjustment.salessupport.email.subject", null);
        final boolean sftpUploadEnabled = Config.getBoolean("sabm.sftp.upload.enabled", false);

        String message = Config.getString("creditadjustment.salessupport.email.body", null);
        message = file.getName() + " " + message;

        final List<String> messages = new ArrayList<>();
        messages.add(message);

        try {
            // Setting catalog in session for emailAttachment
            catalogVersionService.setSessionCatalogVersion(Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
                    Config.getString("email.attachment.default.catalog.version", "Staged"));

            // Creating attachment for the email with generated report file.
            final FileInputStream fis = new FileInputStream(file);
            final EmailAttachmentModel emailAttachment = systemEmailService
                    .createEmailAttachment(new DataInputStream(fis), file.getName(), "text/csv");

            SystemEmailMessageModel systemEmailMessageModel = null;


			/*
            Attachment will be uploaded to sftp server location
			 */
            if (sftpUploadEnabled) {
                if (toEmail2.contains(",") || toEmail2.contains(";")) {
                    systemEmailMessageModel = systemEmailService
                            .constructSystemEmailForMultipleRecepients(fromEmail, toEmail2, emailName, subject, messages,
                                    null);
                } else {
                    systemEmailMessageModel = systemEmailService.constructSystemEmail(fromEmail, toEmail2, emailName, subject, messages, null);
                }


                //update CSV file to SFTP server

                sabmSFTPService.uploadCSVFile(file, sftpDirectory);

            }
			/*
			if sftpUploadEnabled = false, Attachment is within email
			 */
            else {
                if (toEmail.contains(",") || toEmail.contains(";")) {
                    systemEmailMessageModel = systemEmailService
                            .constructSystemEmailForMultipleRecepients(fromEmail, toEmail, emailName, subject, messages,
                                    Collections.singletonList(emailAttachment));
                } else {
                    systemEmailMessageModel = systemEmailService.constructSystemEmail(fromEmail, toEmail, emailName, subject, messages,
                            Collections.singletonList(emailAttachment));
                }

            }

            systemEmailService.send(systemEmailMessageModel);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw e;

        }
    }

}
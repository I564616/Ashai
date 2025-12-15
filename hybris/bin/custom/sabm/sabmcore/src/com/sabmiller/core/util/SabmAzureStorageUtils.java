
package com.sabmiller.core.util;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.CopyStatusType;
import com.sabmiller.core.constants.SabmCoreConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class SabmAzureStorageUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SabmAzureStorageUtils.class);
    private ConfigurationService configurationService;

    public BlobContainerClient getAzureBlobContainer(final String containerReference) {
        try {
            BlobContainerClient containerClient = getClient().getBlobContainerClient(containerReference);
            containerClient.createIfNotExists();
            return containerClient;
        } catch (BlobStorageException e) {
            LOG.error(String.format("Error returned from the service. Http code : %d and error code: %s", e.getStatusCode(), e.getErrorCode()));
        }
        return null;
    }

    public BlobClient getInputFile(final String filename, final BlobContainerClient containerClient) {
        try {
            BlobClient blobClient = containerClient.getBlobClient(filename);
            if(blobClient.exists()) {
                return moveFileToDirectory(blobClient, containerClient, SabmCoreConstants.RECOMMENDATION_PROCESSING_CONTAINER + filename);
            }
        } catch (BlobStorageException e) {
            LOG.error(String.format("Error retrieving file. Http code : %d and error code: %s", e.getStatusCode(), e.getErrorCode()));
        }
        return null;
    }

    public BlobClient moveFileToDirectory(final BlobClient blobClient, final BlobContainerClient containerClient, String newBlobName) {
        try {
            BlobClient blobCopyClient = containerClient.getBlobClient(newBlobName);
            blobCopyClient.beginCopy(blobClient.getBlobUrl(), null);
            CopyStatusType copyStatus = CopyStatusType.PENDING;
            while (copyStatus == CopyStatusType.PENDING) {
                Thread.sleep(1000);
                copyStatus = blobCopyClient.getProperties().getCopyStatus();
            }
            blobClient.delete();
            return blobCopyClient;
        } catch (BlobStorageException e) {
            LOG.error(String.format("Error retrieving file. Http code : %d and error code: %s", e.getStatusCode(), e.getErrorCode()));
        } catch (InterruptedException e) {
            LOG.error("Interrupted Exception encountered while copying file");
        }
        return null;
    }

    public boolean archiveFile(final BlobContainerClient containerClient, final File outputFile) {
        return writeToAzureStorage(containerClient, outputFile, SabmCoreConstants.RECOMMENDATION_ARCHIVE_CONTAINER + outputFile.getName());
    }

    public boolean writeToAzureStorage(final BlobContainerClient containerClient, final File outputFile, final String outputFileName) {
        try {
            containerClient.getBlobClient(outputFileName).uploadFromFile(outputFile.getAbsolutePath());
            return true;
        } catch (BlobStorageException e) {
            LOG.error(String.format("Error retrieving file. Http code : %d and error code: %s", e.getStatusCode(), e.getErrorCode()));
        }
        return false;
    }

    public BlobServiceClient getClient() {
        final String storageConnectionString = getConfigurationService().getConfiguration()
                .getString(SabmCoreConstants.AZURE_STORAGE_CONNECTION_STRING);
        return new BlobServiceClientBuilder().connectionString(storageConnectionString).buildClient();
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

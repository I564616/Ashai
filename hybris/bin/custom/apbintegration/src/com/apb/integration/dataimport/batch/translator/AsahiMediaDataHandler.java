
package com.apb.integration.dataimport.batch.translator;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.media.MediaDataHandler;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.media.exceptions.MediaStoreException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class AsahiMediaDataHandler implements MediaDataHandler {

    private static final Logger LOG = Logger.getLogger(AsahiMediaDataHandler.class);
    public static final String AZURE_STORAGE_CONNECTION_STRING = "azure.hotfolder.storage.account.connection-string";
    private boolean legacyMode = Config.getBoolean((String) "impex.legacy.mode", (boolean) true);
    private MediaService mediaService;
    private ModelService modelService;

    public void importData(Media media, String path) throws ImpExException {
        this.assureParameters(media, path);
        if (this.isAzureUrlForMedia(path)) {
            this.setMediaFromAzureUrl(path, media);
        } else {
            throw new ImpExException("Path '" + path + "' couldn't be resolved!");
        }
    }

    private void setMediaFromAzureUrl(String path, Media media) throws ImpExException {
        InputStream inputStream = null;
        String actualpath = this.removeControlPrefixFromPath("azureFile:", path);
        try {
            try {
                BlobContainerClient containerClient = this.getAzureBlobContainer(Config.getString("azure.hotfolder.storage.container.name", "hybris"));
                if (null == containerClient) {
                    LOG.error("Container not found.");
                } else {
                    BlobClient blobClient = containerClient.getBlobClient(actualpath);
                    inputStream = blobClient.openInputStream();
                    this.setStreamForMedia(media, inputStream);
                }
            } catch (MediaStoreException e) {
                throw new ImpExException((Throwable) e, e.getMessage(), 0);
            } catch (final BlobStorageException e) {
                LOG.error(String.format("Error returned from the service. Status code: %d, Error code: %s", e.getStatusCode(), e.getErrorCode()));
            }
        } catch (Exception ex) {
            IOUtils.closeQuietly(inputStream);
        }
        IOUtils.closeQuietly((InputStream) inputStream);
    }

    private boolean isAzureUrlForMedia(String path) {
        return path.startsWith("azureFile:");
    }

    private void assureParameters(Media media, String path) throws ImpExException {
        if (media == null) {
            throw new ImpExException("Assigned media couldn't be null!");
        }
        if (StringUtils.isEmpty((String) path)) {
            throw new ImpExException("Invalid path definition!");
        }
    }

    private BlobContainerClient getAzureBlobContainer(final String containerReference) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(Config.getString(AZURE_STORAGE_CONNECTION_STRING, ""))
                    .buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerReference);
            if (!containerClient.exists()) {
                containerClient.create();
            }
            return containerClient;
        } catch (final BlobStorageException e) {
            LOG.error(String.format("Error returned from the service. Status code: %d, Error code: %s", e.getStatusCode(), e.getErrorCode()));
        }
        return null;
    }

    public BlobServiceClient getClient() throws URISyntaxException, InvalidKeyException {
        final String storageConnectionString = Config.getString(AZURE_STORAGE_CONNECTION_STRING, "");
        return new BlobServiceClientBuilder().connectionString(storageConnectionString).buildClient();
    }

    private void setStreamForMedia(Media media, InputStream dataStream) {
        if (this.legacyMode) {
            media.setData(dataStream, media.getRealFileName(), media.getMime());
        } else {
            MediaModel mediaModel = (MediaModel) this.getModelService().get((Object) media);
            this.getMediaService().setStreamForMedia(mediaModel, dataStream, mediaModel.getRealFileName(), mediaModel.getMime());
        }
    }

    private String removeControlPrefixFromPath(String controlPrefix, String fullPath) {
        return fullPath.substring(controlPrefix.length(), fullPath.length()).trim();
    }

    protected void setLegacyMode(boolean legacyMode) {
        this.legacyMode = legacyMode;
    }

    MediaService getMediaService() {
        if (this.mediaService == null) {
            this.mediaService = (MediaService) Registry.getApplicationContext().getBean("mediaService", MediaService.class);
        }
        return this.mediaService;
    }

    ModelService getModelService() {
        if (this.modelService == null) {
            this.modelService = (ModelService) Registry.getApplicationContext().getBean("modelService", ModelService.class);
        }
        return this.modelService;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public String exportData(Media paramMedia) throws ImpExException {
        throw new JaloSystemException("Not implemented yet! -- Use de.hybris.platform.impex.jalo.cronjob.DefaultCronJobMediaDataHandler");
    }
}

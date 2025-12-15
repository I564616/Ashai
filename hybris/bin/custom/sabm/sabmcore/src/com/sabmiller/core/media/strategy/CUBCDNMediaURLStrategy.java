/**
 *
 */
package com.sabmiller.core.media.strategy;

import com.sabmiller.core.constants.SabmCoreConstants;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.url.MediaURLStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ramsatish.jagajyothi
 *
 */
public class CUBCDNMediaURLStrategy implements MediaURLStrategy {

    private static final String IMAGE_MIME = "image";
    
    private static final String SABMCONTENTCATAOG = "sabmContentCatalog";
   		 
    private static final String SABMPRODUCTCATAOG = "sabmProductCatalog";
   		 
    private MediaURLStrategy mediaURLStrategy;
    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.media.url.MediaURLStrategy#getUrlForMedia(de.hybris.platform.media.storage.
     * MediaStorageConfigService.MediaFolderConfig, de.hybris.platform.media.MediaSource)
     */
    @Override public String getUrlForMedia(final MediaFolderConfig config, final MediaSource media) {
        final String url = getMediaURLStrategy().getUrlForMedia(config, media);
        if(media!=null)
        {
      	  if(media.getSource() instanceof MediaModel)
      	  {
         	  CatalogVersionModel catalogVersion = ((MediaModel)media.getSource()).getCatalogVersion();
         	  
         	  if(catalogVersion!=null && !(catalogVersion.getCatalog().getId().equalsIgnoreCase(SABMCONTENTCATAOG) ||
         			  catalogVersion.getCatalog().getId().equalsIgnoreCase(SABMPRODUCTCATAOG)))
         	  {
         		  return url;
         	  }
      	  }
      	  if(media.getSource() instanceof CatalogUnawareMediaModel && StringUtils.containsIgnoreCase(media.getMime(), IMAGE_MIME))
      	  {
      		  return url;
      	  }
          
        }
        
        
        if(media != null && StringUtils.containsIgnoreCase(media.getMime(), IMAGE_MIME)) {
           return new StringBuffer()
                   .append(getConfigurationService().getConfiguration().getString(SabmCoreConstants.PROPERTIES.STATIC_HOST_PATH, ""))
                   .append(URIUtil.getFromPath(url)).toString();

       }
        
        return url;

    }

    protected MediaURLStrategy getMediaURLStrategy() {
        return mediaURLStrategy;
    }

    public void setMediaURLStrategy(MediaURLStrategy mediaURLStrategy) {
        this.mediaURLStrategy = mediaURLStrategy;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

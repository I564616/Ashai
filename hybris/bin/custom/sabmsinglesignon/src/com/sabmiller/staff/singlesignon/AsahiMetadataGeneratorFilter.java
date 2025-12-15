/**
 *
 */
package com.sabmiller.staff.singlesignon;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.metadata.MetadataMemoryProvider;


/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiMetadataGeneratorFilter //extends MetadataGeneratorFilter
{



	/**
	 * @param generator
	 */
	public AsahiMetadataGeneratorFilter(final MetadataGenerator generator)
	{
		//super(generator);
		// YTODO Auto-generated constructor stub
	}

	/**
	 * Verifies whether generation is needed and if so the metadata document is created and stored in metadata manager.
	 *
	 * @param request
	 *           request
	 * @throws jakarta.servlet.ServletException
	 *            error
	 */
	//@Override
	protected void processMetadataInitialization(final HttpServletRequest request) throws ServletException
	{

		// In case the hosted SP metadata weren't initialized, let's do it now
//		if (manager.getHostedSPName() == null)
//		{
//
//			synchronized (MetadataManager.class)
//			{
//
//				if (manager.getHostedSPName() == null)
//				{
//
//					try
//					{
//
//						log.info(
//								"No default metadata configured, generating with default values, please pre-configure metadata for production use");
//
//						// Defaults
//						final String alias = null;
//						String baseURL = getDefaultBaseURL(request);
//
//						// Use default baseURL if not set
//						if (generator.getEntityBaseURL() == null || request.getRequestURI().contains("backoffice"))
//						{
//							log.warn(
//									"Generated default entity base URL {} based on values in the first server request. Please set property entityBaseURL on MetadataGenerator bean to fixate the value.",
//									baseURL);
//							generator.setEntityBaseURL(baseURL);
//						}
//						else
//						{
//							baseURL = generator.getEntityBaseURL();
//						}
//
//						// Use default entityID if not set
//						if (generator.getEntityId() == null)
//						{
//							generator.setEntityId(getDefaultEntityID(baseURL, alias));
//						}
//
//						final EntityDescriptor descriptor = generator.generateMetadata();
//						final ExtendedMetadata extendedMetadata = generator.generateExtendedMetadata();
//
//						log.info("Created default metadata for system with entityID: " + descriptor.getEntityID());
//						final MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(descriptor);
//						memoryProvider.initialize();
//						final MetadataProvider metadataProvider = new ExtendedMetadataDelegate(memoryProvider, extendedMetadata);
//
//						manager.addMetadataProvider(metadataProvider);
//						manager.setHostedSPName(descriptor.getEntityID());
//						manager.refreshMetadata();
//
//					}
//					catch (final MetadataProviderException e)
//					{
//						log.error("Error generating system metadata", e);
//						throw new ServletException("Error generating system metadata", e);
//					}
//
//				}
//
//			}
//
//		}
	}
}

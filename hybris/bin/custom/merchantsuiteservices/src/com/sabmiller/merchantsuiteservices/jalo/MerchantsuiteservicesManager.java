package com.sabmiller.merchantsuiteservices.jalo;

import com.sabmiller.merchantsuiteservices.constants.MerchantsuiteservicesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class MerchantsuiteservicesManager extends GeneratedMerchantsuiteservicesManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( MerchantsuiteservicesManager.class.getName() );
	
	public static final MerchantsuiteservicesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MerchantsuiteservicesManager) em.getExtension(MerchantsuiteservicesConstants.EXTENSIONNAME);
	}
	
}

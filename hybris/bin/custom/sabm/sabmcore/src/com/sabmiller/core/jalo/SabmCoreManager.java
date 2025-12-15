package com.sabmiller.core.jalo;

import com.sabmiller.core.constants.SabmCoreConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SabmCoreManager extends GeneratedSabmCoreManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( SabmCoreManager.class.getName() );
	
	public static final SabmCoreManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SabmCoreManager) em.getExtension(SabmCoreConstants.EXTENSIONNAME);
	}
	
}

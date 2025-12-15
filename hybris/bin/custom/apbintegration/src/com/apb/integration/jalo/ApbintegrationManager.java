package com.apb.integration.jalo;

import com.apb.integration.constants.ApbintegrationConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class ApbintegrationManager extends GeneratedApbintegrationManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( ApbintegrationManager.class.getName() );
	
	public static final ApbintegrationManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ApbintegrationManager) em.getExtension(ApbintegrationConstants.EXTENSIONNAME);
	}
	
}

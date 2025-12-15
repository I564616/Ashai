package com.apb.jalo;

import com.apb.constants.ApbcmsaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class ApbcmsaddonManager extends GeneratedApbcmsaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( ApbcmsaddonManager.class.getName() );
	
	public static final ApbcmsaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ApbcmsaddonManager) em.getExtension(ApbcmsaddonConstants.EXTENSIONNAME);
	}
	
}

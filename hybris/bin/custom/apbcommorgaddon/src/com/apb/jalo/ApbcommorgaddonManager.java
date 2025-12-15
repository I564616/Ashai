package com.apb.jalo;

import com.apb.constants.ApbcommorgaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class ApbcommorgaddonManager extends GeneratedApbcommorgaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( ApbcommorgaddonManager.class.getName() );
	
	public static final ApbcommorgaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ApbcommorgaddonManager) em.getExtension(ApbcommorgaddonConstants.EXTENSIONNAME);
	}
	
}

package com.apb.occ.v2.jalo;

import com.apb.occ.v2.constants.ApboccaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class ApboccaddonManager extends GeneratedApboccaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( ApboccaddonManager.class.getName() );
	
	public static final ApboccaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ApboccaddonManager) em.getExtension(ApboccaddonConstants.EXTENSIONNAME);
	}
	
}

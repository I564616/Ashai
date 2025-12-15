package com.apb.jalo;

import com.apb.constants.Apbb2baccaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class Apbb2baccaddonManager extends GeneratedApbb2baccaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( Apbb2baccaddonManager.class.getName() );
	
	public static final Apbb2baccaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Apbb2baccaddonManager) em.getExtension(Apbb2baccaddonConstants.EXTENSIONNAME);
	}
	
}

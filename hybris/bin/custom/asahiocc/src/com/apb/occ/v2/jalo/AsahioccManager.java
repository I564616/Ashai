package com.apb.occ.v2.jalo;

import com.apb.occ.v2.constants.AsahioccConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class AsahioccManager extends GeneratedAsahioccManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( AsahioccManager.class.getName() );
	
	public static final AsahioccManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (AsahioccManager) em.getExtension(AsahioccConstants.EXTENSIONNAME);
	}
	
}

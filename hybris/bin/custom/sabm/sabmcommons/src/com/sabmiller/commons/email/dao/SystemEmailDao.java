/**
 *
 */
package com.sabmiller.commons.email.dao;

import com.sabmiller.commons.model.SystemEmailMessageModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;



/**
 *
 */
public interface SystemEmailDao extends Dao
{
	/**
	 * Finds all Unsent System Emails.
	 *
	 * @param sent
	 *           flag to search for System Emails.
	 * @return a list of System Emails
	 */
	List<SystemEmailMessageModel> findSystemEmailsBySentStatus(final boolean sent);
}

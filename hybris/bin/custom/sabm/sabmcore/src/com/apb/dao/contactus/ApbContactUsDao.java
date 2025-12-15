package com.apb.dao.contactus;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.List;

import com.apb.core.model.ContactUsQueryTypeModel;


/**
 * The Interface ApbContactUsDao.
 */
@FunctionalInterface
public interface ApbContactUsDao
{

	/**
	 * Gets the subject.
	 *
	 * @param cmsSite the cms site
	 * @return the subject
	 */
	List<ContactUsQueryTypeModel> getSubject(CMSSiteModel cmsSite);

}

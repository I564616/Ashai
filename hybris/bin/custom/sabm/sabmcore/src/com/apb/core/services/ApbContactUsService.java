package com.apb.core.services;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.List;

import com.apb.core.model.ContactUsQueryTypeModel;


/**
 *
 */
@FunctionalInterface
public interface ApbContactUsService
{
	/**
	 * @param cmsSite
	 * @return
	 */
	List<ContactUsQueryTypeModel> getSubject(CMSSiteModel cmsSite);

}

package com.apb.core.services.impl;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.model.ContactUsQueryTypeModel;
import com.apb.core.services.ApbContactUsService;
import com.apb.dao.contactus.ApbContactUsDao;


/**
 * @author c5252631
 *
 *         ApbContactUsServiceImpl implmentation of {@link ApbContactUsService}
 */
public class ApbContactUsServiceImpl implements ApbContactUsService
{
	@Autowired
	private ApbContactUsDao apbContactUsDao;

	@Override
	public List<ContactUsQueryTypeModel> getSubject(final CMSSiteModel cmsSite)
	{
		return apbContactUsDao.getSubject(cmsSite);
	}
}

package com.apb.facades.contactus;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.media.MediaIOException;

import java.io.IOException;
import java.util.List;

import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.ContactUsQueryTypeData;

/**
 *
 */
public interface ApbContactUsFacade
{

	/**
	 * @param currentSite
	 * @return
	 */
	List<ContactUsQueryTypeData> getSubject(CMSSiteModel currentSite);

	/**
	 * @param apbContactUsData
	 * @throws MediaIOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	String sendContactUsQueryEmail(ApbContactUsData apbContactUsData) throws MediaIOException, IllegalArgumentException, IOException;


	/**
	 * @param cmsSite
	 * @return
	 */
	String getDefaultContactUsSubjectCode(CMSSiteModel cmsSite);

}

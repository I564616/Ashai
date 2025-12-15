/**
 *
 */
package com.sabmiller.webservice;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.xml.validation.ValidationErrorHandler;
import org.springframework.xml.validation.XmlValidator;
import org.xml.sax.SAXParseException;

import com.sabmiller.core.constants.SabmCoreConstants;


/**
 *
 */
public class SecurityValidationFilter implements XmlValidator
{

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.xml.validation.XmlValidator#validate(javax.xml.transform.Source)
	 */

	private SessionService sessionService;

	@Override
	public SAXParseException[] validate(final Source paramSource) throws IOException
	{
		final Logger LOG = LoggerFactory.getLogger(this.getClass());
		getSessionService().setAttribute(SabmCoreConstants.SESSION_CUB_WEBSERVICES_ATTR, SabmCoreConstants.CUB_STORE);
		if (paramSource != null)
		{
			String result =null;
			try {
			final StringWriter writer = new StringWriter();
			final StreamResult streamResult = new StreamResult(writer);
			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer transformer = tf.newTransformer();
			transformer.transform(paramSource, streamResult);
			result = streamResult.toString();
			}
			catch (final Exception e) {
				LOG.error("Error in parsing XML:"+e);
			}
			if (StringUtils.containsIgnoreCase(result, Config.getString("webservice.payload.XXE", "DOCTYPE")))
			{
				final SAXParseException[] exceptions =
				{ new SAXParseException(result, null) };
				return exceptions;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.xml.validation.XmlValidator#validate(javax.xml.transform.Source,
	 * org.springframework.xml.validation.ValidationErrorHandler)
	 */
	@Override
	public SAXParseException[] validate(final Source paramSource, final ValidationErrorHandler paramValidationErrorHandler)
			throws IOException
	{
		return validate(paramSource);
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}



}

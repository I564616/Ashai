package com.apb.core.util;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.sap.security.core.server.csi.XSSEncoder;


public class ApbXSSEncoderUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ApbXSSEncoderUtil.class);

	private ApbXSSEncoderUtil() 
	{
	    //empty
	}
	
	public static String encodeValue(final String inputValue)
	{
		try
		{
			return XSSEncoder.encodeHTML(inputValue);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error("Error occured during encoding the input value", e);
		}
		return null;
	}
}

/**
 * 
 */
package com.apb.occ.v2.exception;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

import java.io.Serial;
import java.util.ArrayList;


/**
 * The Class AsahiWebServiceException.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiWebServiceException extends WebserviceException
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/** The Constant ERROE_CODE. */
	public static final String ERROE_CODE = "errorCode";

	/** The Constant ERROE_MESSAGE. */
	public static final String ERROE_MESSAGE = "errorMessage";

	/** The validation object. */
	protected ArrayList<Object> validationObject;

	/**
	 * Instantiates a new asahi web service exception.
	 * 
	 * @param validationObject
	 *           the validation object
	 */
	public AsahiWebServiceException(final ArrayList<Object> validationObject)
	{
		super("Validation error");
		this.validationObject = validationObject;
	}

	/**
	 * Gets the validation object.
	 * 
	 * @return the validationObject
	 */
	public ArrayList<Object> getValidationObject()
	{
		return validationObject;
	}

	/**
	 * Sets the validation object.
	 * 
	 * @param validationObject
	 *           the validationObject to set
	 */
	public void setValidationObject(final ArrayList<Object> validationObject)
	{
		this.validationObject = validationObject;
	}

	/**
	 * Instantiates a new asahi web service exception.
	 * 
	 * @param message
	 *           the message
	 */
	public AsahiWebServiceException(final String message)
	{
		super(message);
	}
	
	public AsahiWebServiceException()
	{
		// default constructor
		super("Validation Error");
	}

	/**
	 * Gets the subject type.
	 * 
	 * @return the subject type
	 */
	@Override
	public String getSubjectType()
	{
		return ERROE_MESSAGE;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Override
	public String getType()
	{
		return ERROE_CODE;
	}
}

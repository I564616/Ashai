package com.apb.core.payment.fz.models;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * The Payment response wrapper
 *
 * @param <T>
 *           the type (Purchase, Refund etc) of the response
 */
public class AsahiPaymentResponse<T>
{
	/**
	 * The response Code (HTTP status code)
	 */
	private int responseCode;
	/**
	 * The response body
	 */
	private String responseBody;
	/**
	 * The response headers
	 */
	private Map<String, List<String>> headers;
	/**
	 * indicates the response being successful
	 */
	private boolean successful;
	/**
	 * Any errors for the request
	 */
	private List<String> errors;
	/**
	 * The result T of the transaction
	 */
	private T result;
	/**
	 * Indicates if the transaction was test or not
	 */
	private boolean test;

	/**
	 * Initialises a new response
	 *
	 * @param rCode
	 *           the HTTP response code
	 * @param rBody
	 *           the response body/content
	 * @param hdrs
	 *           the response headers
	 */
	public AsahiPaymentResponse(final int rCode, final String rBody, final Map<String, List<String>> hdrs)
	{
		this.responseCode = rCode;
		this.responseBody = rBody;
		this.headers = hdrs;
	}

	/**
	 *
	 */
	public AsahiPaymentResponse()
	{
	}


	/**
	 * Parses the response into the class T provided in clazz
	 *
	 * @param clazz
	 *           the class for the receiving object
	 */
	public void parseResult(final Class<T> clazz)
	{
		final JsonParser parser = new JsonParser();
		final JsonObject responsePayload = (JsonObject) parser.parse(this.responseBody);
		final JsonElement wrapper = responsePayload.get("response");
		this.result = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(wrapper, clazz);

		final AsahiPaymentResponse r = new Gson().fromJson(this.responseBody, this.getClass());
		this.successful = r.successful;
		this.errors = r.errors;
	}

	public int getResponseCode()
	{
		return responseCode;
	}

	public void setResponseCode(final int responseCode)
	{
		this.responseCode = responseCode;
	}

	public String getResponseBody()
	{
		return responseBody;
	}

	public void setResponseBody(final String responseBody)
	{
		this.responseBody = responseBody;
	}

	public Map<String, List<String>> getHeaders()
	{
		return headers;
	}

	public void setHeaders(final Map<String, List<String>> headers)
	{
		this.headers = headers;
	}

	public boolean isSuccessful()
	{
		return successful;
	}

	public void setSuccessful(final boolean successful)
	{
		this.successful = successful;
	}

	public List<String> getErrors()
	{
		return errors;
	}

	public void setErrors(final List<String> errors)
	{
		this.errors = errors;
	}

	public T getResult()
	{
		return result;
	}

	public void setResult(final T result)
	{
		this.result = result;
	}

	public boolean isTest()
	{
		return test;
	}

	public void setTest(final boolean test)
	{
		this.test = test;
	}


}

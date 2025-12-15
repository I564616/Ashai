package com.apb.core.payment.fz;

/**
 * Provides context (authentication credentials etc) for connection to the Payment API
 */
public class AsahiPaymentGatewayContext
{
	/**
	 *
	 */
	private String username;
	/**
	 *
	 */
	private String token;
	/**
	 *
	 */
	private boolean sandbox;
	/**
	 *
	 */
	private String live_url;
	/**
	 *
	 */
	private String sandbox_url;

	private String siteId;
	
	private boolean directDebit;

	private String url;

	private Object payloadObject;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getPayloadObject() {
        return payloadObject;
    }

    public void setPayloadObject(Object payloadObject) {
        this.payloadObject = payloadObject;
    }

    /**
	 * @return the directDebit
	 */
	public boolean isDirectDebit() {
		return directDebit;
	}

	/**
	 * @param directDebit the directDebit to set
	 */
	public void setDirectDebit(boolean directDebit) {
		this.directDebit = directDebit;
	}

	/**
	 * @return site id
	 */
	public String getSiteId()
	{
		return siteId;
	}

	/**
	 * @param siteId
	 */
	public void setSiteId(final String siteId)
	{
		this.siteId = siteId;
	}

	/**
	 * @return
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username
	 */
	public void setUsername(final String username)
	{
		this.username = username;
	}

	/**
	 * @return
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token
	 */
	public void setToken(final String token)
	{
		this.token = token;
	}

	/**
	 * @return
	 */
	public boolean isSandbox()
	{
		return sandbox;
	}

	/**
	 * @param sandbox
	 */
	public void setSandbox(final boolean sandbox)
	{
		this.sandbox = sandbox;
	}

	/**
	 * @return
	 */
	public String getLive_url()
	{
		return live_url;
	}

	/**
	 * @param live_url
	 */
	public void setLive_url(final String live_url)
	{
		this.live_url = live_url;
	}

	/**
	 * @return
	 */
	public String getSandbox_url()
	{
		return sandbox_url;
	}

	/**
	 * @param sandbox_url
	 */
	public void setSandbox_url(final String sandbox_url)
	{
		this.sandbox_url = sandbox_url;
	}

	/**
	 * @param username
	 * @param token
	 * @param sandbox
	 */
	public AsahiPaymentGatewayContext(final String username, final String token, final boolean sandbox)
	{
		this.username = username;
		this.token = token;
		this.sandbox = sandbox;
	}

	/**
	*
	*/
	public AsahiPaymentGatewayContext()
	{
	}
}

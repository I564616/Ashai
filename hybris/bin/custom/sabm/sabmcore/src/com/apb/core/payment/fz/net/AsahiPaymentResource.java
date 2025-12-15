package com.apb.core.payment.fz.net;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;

import jakarta.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.exception.AsahiBusinessException;
import com.apb.core.payment.fz.AsahiPaymentBase;
import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.exceptions.AsahiPaymentApiException;
import com.apb.core.payment.fz.exceptions.AsahiPaymentNetworkException;
import com.apb.core.payment.fz.models.AsahiPaymentResponse;
import com.apb.core.service.config.AsahiConfigurationService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Provides the methods for accessing Fat Zebra resources via the API
 */
public abstract class AsahiPaymentResource extends AsahiPaymentBase
{
	protected static final Logger LOG = LoggerFactory.getLogger("AsahiPaymentResource");
	public static final String CHARSET = "UTF-8";
	public static final String CONTENT_TYPE = "application/json";
	protected static final String DNS_CACHE_TTL_PROPERTY_NAME = "networkaddress.cache.ttl";
	protected static final String PAYMENT_TIMEOUT = "asahi.default.credit.card.payment.timeout.";
	protected static final String PAYMENT_VERSION = "asahi.default.credit.card.payment.version.";
	protected static final String DEFAULT_PAYMENT_VERSION = "1.5";
	protected static final String DEFAULT_PAYMENT_TIMEOUT = "60";
	protected static final String PAYMENT_SANDBOX_URL = "asahi.payment.target.direct.token.payment.sandbox.url.";
	protected static final String DEFAULT_PAYMENT_SANDBOX_URL = "https://gateway.sandbox.fatzebra.com.au/v1.0/";
	protected static final String PAYMENT_LIVE_URL = "asahi.payment.target.direct.token.payment.live.url.";
	protected static final String DEFAULT_PAYMENT_LIVE_URL = "https://gateway.fatzebra.com.au/v1.0/";

	protected static String originalDNSCacheTTL = null;
	protected static boolean allowedToSetTTL = true;

	public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.setDateFormat("yyyy-MM-dd").excludeFieldsWithoutExposeAnnotation().create();

	@Resource(name = "asahiConfigurationService")
	protected AsahiConfigurationService asahiConfigurationService;

	@Autowired
	protected CMSSiteService cmsSiteService;

	@Autowired
    protected UserService userService;

	@Autowired
	private AsahiCreditCardPaymentProxy asahiCreditCardPaymentProxy;

	public static enum RequestType
	{
		GET, POST, DELETE
	}

	protected static String getResponseBody(final InputStream responseStream) throws IOException
	{
		//\A denotes the start of the stream boundary
		final Scanner s = new Scanner(responseStream, CHARSET);
		s.useDelimiter("\\A");

		final String rBody = s.next(); //

		s.close();
		responseStream.close();
		return rBody;
	}

	/**
	 * Encodes the username and token into a base64 value used for the Authorization header The username and token must
	 * be set in the FatZebra class.
	 *
	 * @return String base64 encoded values joined with a colon (:)
	 */
	private static String base64EncodedCredentials(final AsahiPaymentGatewayContext ctx)
	{
		return Base64.encodeBase64String(String.format("%s:%s", ctx.getUsername(), ctx.getToken()).getBytes());
	}

	/**
	 * Builds the headers for the request including the auth header and the user agent
	 *
	 * @return Map<String,String> map of headers (key/value pairs)
	 */
	private Map<String, String> getHeaders(final AsahiPaymentGatewayContext ctx)
	{
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept-Charset", CHARSET);

		headers.put("User-Agent",
				String.format("Fat Zebra v1 - Java %s",
						this.asahiConfigurationService.getString(PAYMENT_VERSION
								+ (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
								DEFAULT_PAYMENT_VERSION)));

		headers.put("Authorization", String.format("Basic %s", base64EncodedCredentials(ctx)));

		// debug headers
		final String[] propertyNames =
		{ "os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor" };
		final Map<String, String> propertyMap = new HashMap<String, String>();
		for (final String propertyName : propertyNames)
		{
			propertyMap.put(propertyName, System.getProperty(propertyName));
		}
		propertyMap.put("bindings.version",
				this.asahiConfigurationService.getString(
						PAYMENT_VERSION
								+ (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
						DEFAULT_PAYMENT_VERSION));
		propertyMap.put("lang", "Java");
		propertyMap.put("publisher", "Fat Zebra");
		headers.put("X-Client-User-Agent", GSON.toJson(propertyMap));
		LOG.info("##################JAVAVERSION########" + headers.get("X-Client-User-Agent"));
		return headers;
	}

	/**
	 * Builds a HTTP URL Connection for the API endpoint and sets up the headers, timeout values etc required
	 *
	 * @return HttpsURLConnection the connection Object
	 * @throws IOException
	 */
	private HttpsURLConnection createApiConnection(final String urlSuffix, final AsahiPaymentGatewayContext ctx)
			throws IOException {
        Map<String, String> gatewayDetailsMap = new LinkedHashMap<>();
	    URL gatewayUrl = null;

        if (userService.isAdmin(userService.getCurrentUser())) {
            gatewayDetailsMap.put("user", userService.getCurrentUser().getUid());
            if (ctx.isDirectDebit()) {
                LOG.info("----Connection to be made for DD Bank Account----");
                gatewayUrl = URI.create(urlSuffix).toURL();
            } else {
                LOG.info("----Connection to be made for CC payment----");
                StringBuilder paymentUrlBuilder = new StringBuilder();
                if (ctx.isSandbox()) {
                    paymentUrlBuilder.append(asahiConfigurationService.getConfiguration().getString("asahi.payment.target.direct.token.payment.sandbox.url." + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid())));
                    paymentUrlBuilder.append(urlSuffix);
                    gatewayDetailsMap.put("asahi.payment.target.direct.token.payment.sandbox.url." + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                            asahiConfigurationService.getConfiguration().getString("asahi.payment.target.direct.token.payment.sandbox.url." + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid())));
                    LOG.info("Sand box is enabled hence falling back to sand box url " + paymentUrlBuilder.toString());
                } else {
                    paymentUrlBuilder.append(asahiConfigurationService.getConfiguration().getString("asahi.payment.target.direct.token.payment.live.url." + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid())));
                    paymentUrlBuilder.append(urlSuffix);
                    gatewayDetailsMap.put("asahi.payment.target.direct.token.payment.live.url." + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()), asahiConfigurationService.getConfiguration().getString("asahi.payment.target.direct.token.payment.live.url." + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid())));
                    LOG.info("Sand box is disable hence going with live url " + paymentUrlBuilder.toString());
                }

                LOG.info("--- Configuration details before connection with new mechanism---");
                gatewayDetailsMap.entrySet().forEach(entry -> LOG.info(entry.getKey() + " === " + entry.getValue()));
                gatewayUrl = URI.create(paymentUrlBuilder.toString()).toURL();
            }
        } else {
            gatewayDetailsMap.put("user", userService.getCurrentUser().getUid());
            if (ctx.isDirectDebit()) {
                LOG.info("----Connection to be made for DD Bank Account----");
                gatewayUrl = URI.create(urlSuffix).toURL();
            } else {
                LOG.info("----Connection to be made for CC payment----");
                gatewayUrl = URI.create(getPaymentGatewayUrl(urlSuffix, ctx)).toURL();
            }
        }
            LOG.info(String.format("Final Gateway Url %s, fetched for user %s", gatewayUrl, userService.getCurrentUser().getUid()));

            final HttpsURLConnection conn = (HttpsURLConnection) gatewayUrl.openConnection();
            conn.setConnectTimeout(Integer.parseInt(this.asahiConfigurationService.getString(
                    PAYMENT_TIMEOUT
                            + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                    DEFAULT_PAYMENT_TIMEOUT)) * 1000);
            gatewayDetailsMap.put(PAYMENT_TIMEOUT + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                    this.asahiConfigurationService.getString(PAYMENT_TIMEOUT + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()), DEFAULT_PAYMENT_TIMEOUT));
            conn.setReadTimeout(Integer.parseInt(this.asahiConfigurationService.getString(
                    PAYMENT_TIMEOUT
                            + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                    DEFAULT_PAYMENT_TIMEOUT)) * 1000);
            gatewayDetailsMap.put(PAYMENT_TIMEOUT + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                    this.asahiConfigurationService.getString(PAYMENT_TIMEOUT + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()), DEFAULT_PAYMENT_TIMEOUT));
            conn.setUseCaches(false);
            for (final Map.Entry<String, String> header : getHeaders(ctx).entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
            LOG.info("---Configuration details after connection made---");
            gatewayDetailsMap.entrySet().forEach(entry -> LOG.info(entry.getKey() + " === " + entry.getValue()));
        return conn;
    }

	private String getPaymentGatewayUrl(final String urlSuffix, final AsahiPaymentGatewayContext ctx)
	{
        Map<String, String> gatewayDetailsMap = new LinkedHashMap<>();
		String paymentUrl;
		if (ctx.isSandbox())
		{
			paymentUrl = this.asahiConfigurationService.getString(
					PAYMENT_SANDBOX_URL
							+ (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
					null);
            gatewayDetailsMap.put(PAYMENT_SANDBOX_URL + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                    this.asahiConfigurationService.getString(PAYMENT_SANDBOX_URL + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),null));
            LOG.info("Sand box is enabled hence falling back to sand box url " + paymentUrl + urlSuffix);
		}
		else
		{
			paymentUrl = this.asahiConfigurationService.getString(
					PAYMENT_LIVE_URL
							+ (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
					null);
			gatewayDetailsMap.put(PAYMENT_LIVE_URL + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),
                    this.asahiConfigurationService.getString(PAYMENT_LIVE_URL + (null == cmsSiteService.getCurrentSite() ? ctx.getSiteId() : cmsSiteService.getCurrentSite().getUid()),null));
			LOG.info("Sand box is disable hence going with live url " + paymentUrl + urlSuffix);
		}
		if (StringUtils.isEmpty(paymentUrl))
		{
			throw new AsahiBusinessException("No configured url for making payment.");
		}


        LOG.info("--- Configuration details before connection with old mechanism---");
        gatewayDetailsMap.entrySet().forEach(entry -> LOG.info(entry.getKey() + " === " + entry.getValue()));

		return paymentUrl + urlSuffix;

	}
	
	protected HttpsURLConnection createGetConnection(final String url, final String query,
			final AsahiPaymentGatewayContext ctx) throws IOException
	{
		final String getURL = String.format("%s?%s", url, query);
		final HttpsURLConnection conn = createApiConnection(getURL, ctx);
		conn.setRequestMethod("GET");
		return conn;
	}

	protected HttpsURLConnection createDeleteConnection(final String url, final Object payloadObject,
			final AsahiPaymentGatewayContext ctx) throws IOException
	{
		final HttpsURLConnection conn = createApiConnection(url, ctx);
		conn.setDoOutput(true);
		conn.setRequestMethod("DELETE");
		conn.setRequestProperty("Content-Type", CONTENT_TYPE);
		OutputStream output = null;
		try
		{
			output = conn.getOutputStream();
			output.write(GSON.toJson(payloadObject).getBytes(CHARSET));
		}
		finally
		{
			if (output != null)
			{
				output.close();
			}
		}
		return conn;
	}

	protected <T> AsahiPaymentResponse<T> doRequest(final String url, final Object payload, final RequestType type,
			final Class<T> classType, final AsahiPaymentGatewayContext context)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		try
		{
			disableDnsCache();
			HttpsURLConnection conn;
			switch (type)
			{
				case POST:
					conn = asahiCreditCardPaymentProxy.createPostConnection(url, payload, context);
					break;

				case DELETE:
					conn = createDeleteConnection(url, payload, context);
					break;

				case GET:
				default:
					conn = createGetConnection(url, (String) payload, context);
					break;
			}
            if(Objects.isNull(conn)){
			    throw new AsahiPaymentNetworkException("There was an error while trying to make payment.", true);
            }
			final int rCode = conn.getResponseCode();

			String rBody;
			Map<String, List<String>> headers;
			if (rCode >= 200 && rCode < 300)
			{
				rBody = getResponseBody(conn.getInputStream());
			}
			else
			{
				rBody = getResponseBody(conn.getErrorStream());
			}
			LOG.info("Payment Response Body " + rBody.toString());
			headers = conn.getHeaderFields();
			final AsahiPaymentResponse<T> response = new AsahiPaymentResponse<T>(rCode, rBody, headers);


			response.parseResult(classType);

			return response;
		}
		catch (final java.net.UnknownHostException ex)
		{
			throw new AsahiPaymentNetworkException(String.format("Unable to resolve address for %s", ex.getMessage()), true, ex);
		}
		catch (final java.net.ConnectException ex)
		{
			throw new AsahiPaymentNetworkException(String.format("Unable to connect to Gateway: %s", ex.getMessage()), true, ex);
		}
		finally
		{
			enableDnsCache();
		}
	}

	protected static void disableDnsCache()
	{
		try
		{
			originalDNSCacheTTL = java.security.Security.getProperty(DNS_CACHE_TTL_PROPERTY_NAME);
			// disable DNS cache
			java.security.Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "0");
		}
		catch (final SecurityException se)
		{
			allowedToSetTTL = false;
			LOG.error("Security Exception " + se.getMessage(), se);
		}
	}

	protected static void enableDnsCache()
	{
		if (allowedToSetTTL)
		{
			if (originalDNSCacheTTL == null)
			{
				// value unspecified by implementation
				// DNS_CACHE_TTL_PROPERTY_NAME of -1 = cache forever
				java.security.Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
			}
			else
			{
				java.security.Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
			}
		}
	}

	protected HttpsURLConnection getConnection(final String url, final Object payloadObject, final AsahiPaymentGatewayContext ctx) throws IOException{
        final HttpsURLConnection conn = createApiConnection(url, ctx);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        OutputStream output = null;
        try
        {
            output = conn.getOutputStream();
            output.write(GSON.toJson(payloadObject).getBytes(CHARSET));
        }
        catch (final Exception e)
        {
            LOG.error("Error found while creating connection", e);
        }
        finally
        {
            if (output != null)
            {
                output.close();
            }
        }
        return conn;
    }
}

/**
 *
 */
package com.sabmiller.core.util;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.DealModel;


/**
 * @author joshua.a.antony
 *
 */
public class SabmUtils
{
	public static final String HOME = "Home";
	public static final String REFERER_KEY = "referer";

	private static final String[] URL_IDENTIFIERS =
	{ "c", "p" }; // Category and Product identifier in the URL
	private static final String SLASH = "/";
	private static final String UTF8_ENCODING = "UTF-8";


	private static final Logger LOG = LoggerFactory.getLogger(SabmUtils.class);

	public static List<String> getDealNumbers(final List<DealModel> deals)
	{
		final List<String> dealNumbers = new ArrayList<String>();
		for (final DealModel deal : CollectionUtils.emptyIfNull(deals))
		{
			dealNumbers.add(deal.getCode());
		}
		return dealNumbers;
	}

	public static Double sapToHybrisDouble(final String s)
	{
		String transformedStr = SabmStringUtils.stripLeadingZeroes(s);
		if (StringUtils.endsWith(s, "-"))
		{
			transformedStr = "-" + s.substring(0, s.length() - 1);
		}
		return SabmStringUtils.toNullSafeDouble(transformedStr);
	}

	/**
	 * Method that gets all the images in the order entry data
	 *
	 * @param entries
	 * @return
	 * @throws IOException
	 */
	public static void getImageUrl(final List<OrderEntryData> entries) throws IOException
	{
		final String THUMBNAIL = "thumbnail";
		final List<InputStream> imageUrlList = new ArrayList<>();

		for (final OrderEntryData entry : entries)
		{
			final ProductData product = entry.getProduct();
			if (CollectionUtils.isNotEmpty(product.getImages()))
			{
				for (final ImageData image : product.getImages())
				{
					if (THUMBNAIL.equals(image.getFormat()))//thumbnail or product
					{
						InputStream iostream = null;
						try
						{
							//final URL url = new URL(image.getUrl());
							final URL url = URI.create(image.getUrl().replaceAll("\\s", "+")).toURL();
							iostream = url.openStream();
						}
						catch (final Exception e)
						{
							LOG.error("error while fetching the images in order template pdf report");
						}
						entry.setImageStream(iostream);
						break;
					}
				}
			}
		}
	}

	/*
	 * This will return the request origin formatted for google tag manager.
	 *
	 * @param referer the request referer
	 *
	 * @return origin the request origin
	 */
	public static String getRequestOrigin(final String referer, final String initialOrigin)
	{
		if (referer == null)
		{
			return initialOrigin;
		}

		String URL = null;
		try
		{
			URL = URLDecoder.decode(referer, UTF8_ENCODING);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.info("Failed to decode URL ");
		}

		final String[] refs = URL.split(SLASH);
		final StringBuffer origin = new StringBuffer();
		origin.append(initialOrigin == null ? "" : initialOrigin);

		for (int i = StringUtils.isEmpty(refs[0]) ? 1 : 5; i < refs.length; i++)
		{
			if (ArrayUtils.contains(URL_IDENTIFIERS, refs[i]))
			{
				break;
			}
			origin.append(SLASH);
			origin.append(refs[i].substring(0, 1).toUpperCase() + refs[i].substring(1));
		}

		return origin.toString();
	}

	public static String getFormattedRequestOrigin(final String... requestOrigin)
	{
		final StringBuffer formattedRequestOrigin = new StringBuffer();

		for (int i = 0; i < requestOrigin.length; i++)
		{

			if (!StringUtils.isEmpty(requestOrigin[i]))
			{
				formattedRequestOrigin.append(requestOrigin[i].trim());

				if (i < (requestOrigin.length - 1))
				{
					formattedRequestOrigin.append(SLASH);
				}
			}

		}

		return formattedRequestOrigin.toString();
	}

	/**
	 * @return true / false based on customer present in disabledUsers list
	 */
	public static boolean isCustomerActiveForCUB(final B2BCustomerModel customer)
	{
		boolean isActive = true;
		if(BooleanUtils.isTrue(customer.getActive()))
		{
		for(final PrincipalGroupModel unit:customer.getGroups())
		{
			if(unit instanceof B2BUnitModel && ((B2BUnitModel) unit).getCompanyUid() != null 
					&& ((B2BUnitModel) unit).getCompanyUid().equals(SabmCoreConstants.CUB_STORE))
			{
				if(SabmCoreConstants.DELETEDCUSTOMERGROUP.equalsIgnoreCase(((B2BUnitModel) unit).getUid()))
				{
					return false;
				}
				if(((B2BUnitModel) unit).getCubDisabledUsers() != null)
				{
					final Collection<String> disabledUsers= ((B2BUnitModel) unit).getCubDisabledUsers();
					if(CollectionUtils.isNotEmpty(disabledUsers) && disabledUsers.contains(customer.getUid()))
					{
						isActive = false;
					}
					else
					{
						return true;
					}
				}
			}
		}
		}
		else {
			isActive = false;
		}
		return isActive;
	}

	public static boolean isUserDisabledForCUBAccount(final B2BUnitModel b2bunit, final UserModel user)
	{
		if (null == b2bunit || null == user)
		{
			return true;
		}
		if (null != b2bunit && CollectionUtils.isNotEmpty(b2bunit.getCubDisabledUsers())
				&& b2bunit.getCubDisabledUsers().contains(user.getUid()))
		{
			return true;
		}
		return false;

	}
	
	public static boolean isCustomerActiveForAccountCUB(final B2BUnitModel b2bunit, final UserModel user)
	{
		if (null == b2bunit || null == user)
		{
			return false;
		}
		if (null != b2bunit && CollectionUtils.isNotEmpty(b2bunit.getCubDisabledUsers())
				&& b2bunit.getCubDisabledUsers().contains(user.getUid()))
		{
			return false;
		}
		return true;

	}

}

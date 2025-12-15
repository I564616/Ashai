package com.sabmiller.facades.search.handler;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wei.yang.ng on 27/07/2016.
 */
public abstract class SABMAbstractCustomSOLRSortHandler
{
	protected SabmB2BUnitService b2bUnitService;

	/**
	 * Constructs the appropriate SOLR sort field name using the subchannel extracted from the B2BUnit currently in session and
	 * appending it to the given SOLR sort prekfix.
	 *
	 * @param	solrSortPrefix	the given SOLR sort prefix to append the session sub-channel code to.
	 *
	 * @return	the SOLR subchannel indexed property, null otherwise.
	 */
	protected String constructSolrSortFieldNameWithGivenSortPrefix(final String solrSortPrefix)
	{
		final B2BUnitModel b2bUnit = getB2bUnitService().getB2BUnitInCurrentSession();

		if (b2bUnit == null) return solrSortPrefix + StringUtils.EMPTY;

		String subChannel = getB2bUnitService().getSubChannelByB2BUnit(b2bUnit);

		if (StringUtils.isEmpty(subChannel)) subChannel = StringUtils.EMPTY;

		return solrSortPrefix + subChannel;
	}

	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}
}

package com.sabmiller.facades.search.translator.sort;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public abstract class SABMAbstractSolrSortFieldnameTranslator implements SABMSolrSortFieldnameTranslator {

    private SabmB2BUnitService b2bUnitService;

	@Resource(name = "solrIndexedPropertyDao")
	private GenericDao<SolrIndexedPropertyModel> solrIndexedPropertyDao;

    @Resource
    private UserService userService;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

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

        if (b2bUnit == null)
		{
            //To use default channel for backoffice adaptive search
            final UserModel currentUser = userService.getCurrentUser();
		    if(currentUser instanceof EmployeeModel)
            {
                return solrSortPrefix + configurationService.getConfiguration().getString("solr.default.bestseller.channel","");
            }
			return solrSortPrefix + StringUtils.EMPTY;
		}

        String subChannel = getB2bUnitService().getSubChannelByB2BUnit(b2bUnit);

        if (StringUtils.isEmpty(subChannel))
		{
			subChannel = StringUtils.EMPTY;
		}
		if (!StringUtils.isEmpty(subChannel))
		{
			final List<SolrIndexedPropertyModel> solrIndexedProperty = solrIndexedPropertyDao
					.find(Collections.singletonMap("name", solrSortPrefix + subChannel));
			if (solrIndexedProperty.isEmpty())
			{
				return null;
			}
		}

        return solrSortPrefix + subChannel;
    }

    public SabmB2BUnitService getB2bUnitService()
    {
        return b2bUnitService;
    }

    public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
    {
        this.b2bUnitService = b2bUnitService;
    }
}

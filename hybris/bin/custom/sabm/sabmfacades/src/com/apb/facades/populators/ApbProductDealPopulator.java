package com.apb.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.model.ApbProductModel;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;


public class ApbProductDealPopulator implements Populator<ProductModel, ProductData>
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbProductDealPopulator.class);
	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "userService")
	private UserService userService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		if(asahiSiteUtil.isSga() && source instanceof ApbProductModel)
		{
			final ApbProductModel productModel = (ApbProductModel) source;

			final UserModel userModel = userService.getCurrentUser();
			if (!asahiCoreUtil.isNAPUser() && userModel instanceof B2BCustomerModel)
			{
				final B2BUnitModel b2bUnitModel = b2bUnitService.getParent((B2BCustomerModel) userModel);
				final List<AsahiDealData> asahiDealsInfo = sabmDealsSearchFacade.getSGADealsDataForProductAndUnit(target.getCode(),
						b2bUnitModel);

				//To determine whether to exist deals by b2bunit,productCode,date
				if (CollectionUtils.isNotEmpty(asahiDealsInfo))
				{
					LOG.debug("The product: {}  for customer :{} exist deals", target.getCode(), b2bUnitModel);
					// If exist deals set the dealsFlag is true
					target.setDealsFlag(Boolean.TRUE);
					final List<String> dealTitles = asahiDealsInfo.stream().map(dealData -> dealData.getTitle())
							.collect(Collectors.toList());
					SabmStringUtils.getSortedDealTitles(dealTitles);
					target.setAsahiDealsInfo(asahiDealsInfo);
					target.setDealsTitle(dealTitles);
				}
				else
				{
					target.setDealsFlag(Boolean.FALSE);
				}
			}
		}
	}
}

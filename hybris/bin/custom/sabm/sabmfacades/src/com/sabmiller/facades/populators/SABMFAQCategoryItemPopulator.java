package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.sabm.core.model.cms.components.FAQCategoryItemComponentModel;
import com.sabm.core.model.cms.components.GenericTextContentItemModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMCMSUserGroupRestrictionModel;
import com.sabmiller.facades.generic.data.FAQCategoryItemData;
import com.sabmiller.facades.generic.data.GenericTextContentItemData;
/**
 * Created by philip.c.a.ferma on 5/23/18.
 */
public class SABMFAQCategoryItemPopulator implements Populator<FAQCategoryItemComponentModel, FAQCategoryItemData> {

	private static final Logger LOG = Logger.getLogger(SABMFAQCategoryItemPopulator.class);

    @Resource(name = "sabmGenericTextContentItemConverter")
    private Converter<GenericTextContentItemModel, GenericTextContentItemData> sabmGenericTextContentItemConverter;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "b2bUnitService")
	protected B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sessionService")
	private SessionService sessionService;



    @Override
    public void populate(final FAQCategoryItemComponentModel faqCategoryItemComponentModel, final FAQCategoryItemData faqCategoryItemData) throws ConversionException {
        faqCategoryItemData.setTitle(faqCategoryItemComponentModel.getTitle());
		final List<GenericTextContentItemData> genericTextContentItemDataList = new ArrayList<GenericTextContentItemData>();
		for (final GenericTextContentItemModel genericTextContentItem : faqCategoryItemComponentModel.getQuestionList())
		{

			for (final AbstractRestrictionModel restriction : CollectionUtils.emptyIfNull(genericTextContentItem.getRestrictions()))
			{
				if (isFaqQesRestricted(restriction))
				{
					genericTextContentItemDataList.add(sabmGenericTextContentItemConverter.convert(genericTextContentItem));
				}
			}
			if (CollectionUtils.isEmpty(genericTextContentItem.getRestrictions()))
			{
				genericTextContentItemDataList.add(sabmGenericTextContentItemConverter.convert(genericTextContentItem));

			}

		}


		faqCategoryItemData.setQuestionList(genericTextContentItemDataList);

	}

	protected boolean isFaqQesRestricted(final AbstractRestrictionModel restriction)
	{

		final boolean evaluation = true;

		B2BUnitModel b2bUnit = null;

		try
		{
			b2bUnit = b2bCommerceUnitService.getParentUnit();

			if (b2bUnit == null)
			{
				b2bUnit = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
			}

			if (b2bUnit != null)
			{
				return evaluateB2BUnit(restriction, b2bUnit);
			}
		}
		catch (final Exception e)
		{
			LOG.error("unable to get the b2bunit", e);
		}

		return evaluation;

	}

	private boolean evaluateB2BUnit(final AbstractRestrictionModel restrictionModel, final B2BUnitModel parent)
	{
		Collection<UserGroupModel> groups = null;
		boolean thisEvaluation = false;
		if (restrictionModel instanceof SABMCMSUserGroupRestrictionModel)
		{
			groups = ((SABMCMSUserGroupRestrictionModel) restrictionModel).getUserGroups();
			thisEvaluation = ((SABMCMSUserGroupRestrictionModel) restrictionModel).getInverse().booleanValue();

		}
		else if (restrictionModel instanceof CMSUserGroupRestrictionModel)
		{
			groups = ((CMSUserGroupRestrictionModel) restrictionModel).getUserGroups();
		}

		if (groups == null || groups.isEmpty())
		{
			return true;
		}


		for (final UserGroupModel group : groups)
		{
			B2BUnitModel b2bUnitGroup = null;
			if (group instanceof B2BUnitModel)
			{
				b2bUnitGroup = (B2BUnitModel) group;
				LOG.debug("SABMFAQCategoryItemPopulator : evaluateB2BUnit : parent = " + parent.getUid());
				LOG.debug("SABMFAQCategoryItemPopulator : evaluateB2BUnit : b2bUnitGroup = " + b2bUnitGroup.getUid());
				if ((b2bUnitGroup.getAccountGroup().equalsIgnoreCase("ZALB")
						|| b2bUnitGroup.getAccountGroup().equalsIgnoreCase("ZADP"))
						&& b2bUnitGroup.getAccountGroup().equalsIgnoreCase(parent.getAccountGroup())
						&& b2bUnitGroup.getUid().equals(parent.getUid()))
				{
					if (thisEvaluation)
					{
						return false;
					}
					else
					{
						return true;
					}
				}
			}
		}
		if (thisEvaluation)
		{
			return true;
		}

		return thisEvaluation;
	}
}

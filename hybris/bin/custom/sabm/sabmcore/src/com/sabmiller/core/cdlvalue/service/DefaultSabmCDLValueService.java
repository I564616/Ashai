/**
 *
 */
package com.sabmiller.core.cdlvalue.service;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.Resource;

import com.sabm.core.model.CDLValueModel;
import com.sabmiller.core.cdlvalue.dao.SabmCDLValueDao;


/**
 * @author EG588BU
 *
 */
public class DefaultSabmCDLValueService implements SabmCDLValueService
{
	@Resource(name = "userService")
	private UserService userService;


	@Resource
	private SabmCDLValueDao sabmCDLValueDao;
	@Override
	public BigDecimal getCDLPrice(final String cdlContainerType, final String presentation)
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		if (currentUser instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnit = ((B2BCustomerModel) currentUser).getDefaultB2BUnit();
			String regionCode = null;
			if (null != b2bUnit && null != b2bUnit.getDefaultShipTo())
			{
				final AddressModel address = b2bUnit.getDefaultShipTo();
				if (null != address.getRegion())
				{
					regionCode = address.getRegion().getIsocode();
				}
			}
			if (null != regionCode)
			{
				final Optional<CDLValueModel> cdlValueModel = sabmCDLValueDao.getCDLValueModel(regionCode, cdlContainerType);
				if (cdlValueModel.isPresent())
				{
					final BigDecimal cdlValue = BigDecimal.valueOf(cdlValueModel.get().getValue());
					final List<String> numberStringList = Arrays.asList(presentation.split("X|x"));
					BigDecimal quantity = BigDecimal.valueOf(1);
					try
					{
						for (final String numberString : numberStringList)
						{
							final BigDecimal number = new BigDecimal(numberString);
							quantity = quantity.multiply(number);
						}
						return (cdlValue.multiply(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);
					}
					catch (final NumberFormatException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}

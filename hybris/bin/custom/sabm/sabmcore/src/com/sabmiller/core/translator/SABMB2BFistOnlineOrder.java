/**
 *
 */
package com.sabmiller.core.translator;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.translators.SABMAbstracB2BCustomerTranslator;
import com.sabmiller.core.order.SabmB2BOrderService;


/**
 * @author ross.hengjun.zhu
 *
 */
public class SABMB2BFistOnlineOrder extends SABMAbstracB2BCustomerTranslator
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BFistOnlineOrder.class);

	private SabmB2BOrderService b2bOrderService;

	/**
	 * Performs translation of user groups for customer export.
	 *
	 * @param item
	 *           of B2BCustomer Jalo.
	 * @return translated list of user groups as a String.
	 */
	@Override
	public String performExport(final Item item) throws ImpExException
	{
		Date last = new Date(0L);
		if (Registry.getCoreApplicationContext().getBean("b2bOrderService") instanceof SabmB2BOrderService)
		{
			b2bOrderService = (SabmB2BOrderService) Registry.getCoreApplicationContext().getBean("b2bOrderService");
		}

		// Checking if everything is ok to perform the attribute translation
		if (isValidAndSetup(item) && b2bOrderService != null)
		{
			final UserModel user = getCustomer();
			final OrderModel lastOrder = b2bOrderService.getFirstOnlineOrderByCustomer(user);
			if (lastOrder != null)
			{
				last = lastOrder.getDate();
			}
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, last.toString());

		if (!last.equals(new Date(0L)))
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return simpleDateFormat.format(last);
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}
}

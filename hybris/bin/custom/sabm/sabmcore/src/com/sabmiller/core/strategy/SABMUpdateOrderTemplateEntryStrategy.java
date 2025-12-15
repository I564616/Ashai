/**
 *
 */
package com.sabmiller.core.strategy;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;


/**
 * @author xiaowu.a.zhang
 * @date 01/06/2016
 */

public interface SABMUpdateOrderTemplateEntryStrategy extends CommerceUpdateCartEntryStrategy
{
	/**
	 * Strategy for remove the order template entry SABMC-904
	 *
	 * @param cartModel
	 *           the cart Model
	 * @param entryToUpdate
	 *           the entry To Update
	 * @return CommerceCartModification
	 */
	CommerceCartModification removeEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate);
}

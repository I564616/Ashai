package com.apb.facades.cart;

import java.util.List;
import java.util.Map;

import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.OrderTemplateData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

/**
 * The Interface AsahiSaveCartFacade.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiSaveCartFacade extends SaveCartFacade{

	/**
	 * Gets the saved carts for current user B 2 B unit.
	 *
	 * @param pageableData the pageable data
	 * @param orderStatus the order status
	 * @return the saved carts for current user B 2 B unit
	 */
	SearchPageData<OrderTemplateData> getSavedCartsForCurrentUserB2BUnit(
			PageableData pageableData);
	
	/**
	 * Save order template.
	 *
	 * @param templateName the input parameters
	 * @return true, if successful
	 */
	public boolean saveOrderTemplate(final String templateName);

	/**
	 * Gets the order template for code and B 2 B unit.
	 *
	 * @param cartCode the cart code
	 * @return the order template for code and B 2 B unit
	 */
	OrderTemplateData getOrderTemplateForCodeAndB2BUnit(String cartCode,String sortCode);

	/**
	 * Delete order template for id.
	 *
	 * @param orderTemplateId the order template id
	 */
	void deleteOrderTemplateForId(String orderTemplateId);

	/**
	 * Delete all entries for order template.
	 *
	 * @param orderTemplateId the order template id
	 */
	void deleteAllEntriesForOrderTemplate(String orderTemplateId);

	/**
	 * Delete order template entry for PK.
	 *
	 * @param orderTemplateId the order template id
	 */
	void deleteOrderTemplateEntryForPK(String orderTemplateId);

	/**
	 * Reorder entries for order template.
	 *
	 * @param orderTemplateId the order template id
	 * @param keepCart the keep cart
	 */
	void reorderEntriesForOrderTemplate(String orderTemplateId, boolean keepCart);

	/**
	 * Reorder order template entries.
	 *
	 * @param templateCode the template code
	 * @param entryQtyMap the template entries
	 * @param keepCart the keep cart
	 */
	void reorderOrderTemplateEntries(String templateCode,
			Map<String, Long> entryQtyMap,boolean keepCart);

	boolean saveOrderTemplate(final String templateCode, final Map<String, Long> entryQtyMap);	
	
	List<OrderTemplateData> getAllSavedCartsForCurrentUserB2BUnit();
	
	boolean addProductToOrderTemplate(final String templateCode,final String productCode,final Long quantity,
			final boolean existingTemplate)throws DuplicateUidException;

}

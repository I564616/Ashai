/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.List;


/**
 * The Interface SabmOrderTemplateDao to provide all the search functionality about the @SABMOrderTemplateModel
 */
public interface SabmOrderTemplateDao extends GenericDao<SABMOrderTemplateModel>
{

	/**
	 * Find order template by b2bUnit.
	 *
	 * @param b2bUnit
	 *           the b2bUnit related to the order template to search.
	 * @return the list of order templates related to the b2bUnit ordered by sequence, or an empty list if no template is
	 *         found.
	 */
	List<SABMOrderTemplateModel> findOrderTemplateByB2BUnit(B2BUnitModel b2bUnit);
}

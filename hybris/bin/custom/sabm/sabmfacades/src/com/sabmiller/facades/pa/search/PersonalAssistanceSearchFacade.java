/**
 *
 */
package com.sabmiller.facades.pa.search;

import de.hybris.platform.b2b.model.B2BCustomerModel;

import java.util.List;

import com.sabmiller.facades.user.SearchB2bUnitData;


/**
 * @author dale.bryan.a.mercado
 *
 */
public interface PersonalAssistanceSearchFacade
{
	List<SearchB2bUnitData> searchB2BUnitByAccount(String accountNumber);

	List<SearchB2bUnitData> searchB2BUnitByCustomer(String customerNumber, String customerName);

	/**
	 * @param searchB2BCustomerByEmail
	 * @return
	 */
	List<SearchB2bUnitData> searchB2BUnitByUser(List<B2BCustomerModel> searchB2BCustomerByEmail);
}

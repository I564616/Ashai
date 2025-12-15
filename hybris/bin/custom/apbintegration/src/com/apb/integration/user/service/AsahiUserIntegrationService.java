package com.apb.integration.user.service;

import java.util.List;

import com.apb.integration.data.AsahiUserResponseData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

public interface AsahiUserIntegrationService {

	/**
	 * Send Users to Salesforce.
	 *
	 * @param users  customers
	 * @return the AsahiUserResponseData
	 */
	AsahiUserResponseData sendUsersToSalesforce(List<B2BCustomerModel> users);

	OrderModel getALBFirstWebOrder(UserModel user);

	OrderModel getALBLastWebOrder(UserModel user);

	OrderModel getALBLastOrder(UserModel user);

}

package com.apb.integration.users.dao;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

public interface AsahiUsersIntegrationDao {

	OrderModel getALBFirstWebOrder(UserModel user);

	OrderModel getALBLastWebOrder(UserModel user);

	OrderModel getALBLastOrder(UserModel user);
}

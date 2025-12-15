package com.sabmiller.core.order.dao;

import de.hybris.platform.acceleratorservices.order.dao.AcceleratorConsignmentDao;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.List;

/**
 * Created by zhuo.a.jiang on 5/02/2018.
 */
public interface SabmConsignmentDao extends AcceleratorConsignmentDao {

    List<ConsignmentModel> getConsignmentForStatus(final OrderModel order, final List<ConsignmentStatus> consignmentStatus) ;


    List<ConsignmentModel> getConsignmentForCode(final String code);


}

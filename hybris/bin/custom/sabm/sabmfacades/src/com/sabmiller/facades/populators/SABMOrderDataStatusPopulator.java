package com.sabmiller.facades.populators;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.facades.util.SabmFeatureUtil;

public class SABMOrderDataStatusPopulator implements Populator<OrderModel, OrderData> {

    final Map<ConsignmentStatus,Integer> consignmentToIndex = new HashMap<>();

    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;
    
    @Resource
 	private AsahiSiteUtil asahiSiteUtil;

    @Override
    public void populate(final OrderModel source, final OrderData target) throws ConversionException {
         if (BooleanUtils.isFalse(sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER)) 
         		&& (asahiSiteUtil.isCub()))
         {
             populateMap(consignmentToIndex);
             if (CollectionUtils.isNotEmpty(target.getConsignments()))
             {
                 List<ConsignmentStatus> statusList = target.getConsignments().stream().map(con -> con.getStatus()).collect(Collectors.toList());
                 ConsignmentStatus maxStatus = getMaxStatus(statusList,consignmentToIndex);
                 target.setStatusToDisplay(sabmFeatureUtil.displayTrackConsignmentStatus(maxStatus));
             }
         }
    }

    private ConsignmentStatus getMaxStatus (final List<ConsignmentStatus> statusList, final Map<ConsignmentStatus,Integer> consignmentStatusIntegerMap)
    {
        ConsignmentStatus returnStatus = null;

        for (ConsignmentStatus  status:statusList)
        {
            Integer max=0;
            if (consignmentStatusIntegerMap.containsKey(status))
            {
                if (consignmentStatusIntegerMap.get(status) >= max)
                {
                    max = consignmentStatusIntegerMap.get(status);
                    returnStatus = status;
                }
            }
        }
        return returnStatus;

    }

    private void populateMap(final Map<ConsignmentStatus,Integer> consignmentStatusIntegerMap)
    {

        consignmentStatusIntegerMap.put(ConsignmentStatus.CANCELLED,0);
        consignmentStatusIntegerMap.put(ConsignmentStatus.CREATED,1);
        consignmentStatusIntegerMap.put(ConsignmentStatus.PROCESSING,2);
        consignmentStatusIntegerMap.put(ConsignmentStatus.SHIPPED,3);
        consignmentStatusIntegerMap.put(ConsignmentStatus.INTRANSIT,4);
        consignmentStatusIntegerMap.put(ConsignmentStatus.DELIVERED,5);
        consignmentStatusIntegerMap.put(ConsignmentStatus.RETURNED,6);
        consignmentStatusIntegerMap.put(ConsignmentStatus.NOTDELIVERED,7);
    }
}

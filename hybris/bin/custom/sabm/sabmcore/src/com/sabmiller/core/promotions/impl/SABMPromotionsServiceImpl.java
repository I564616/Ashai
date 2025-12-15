package com.sabmiller.core.promotions.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.impl.SabmDummyCalculationServiceImpl;

public class SABMPromotionsServiceImpl extends DefaultPromotionsService {

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "calculationService")
	private CalculationService calculationService;

	@Resource(name = "sabmDummyCalculationService")
	private SabmDummyCalculationServiceImpl sabmDummyCalculationService;

    /**
     * This method is overridden to remove the saveIfModified statement as it's not compatible with CUB Business
     * @param source
     * @param target
     * @param onlyTransferAppliedPromotions
     */
    @Override public void transferPromotionsToOrder(AbstractOrderModel source, OrderModel target,boolean onlyTransferAppliedPromotions) {
   	 if(asahiSiteUtil.isCub())
   	 {
        getPromotionsManager().transferPromotionsToOrder(this.getOrder(source), this.getOrder(target), onlyTransferAppliedPromotions);
        List<ItemModel> toRefresh = CollectionUtils.isEmpty(target.getAllPromotionResults()) ? new ArrayList(1) : new ArrayList(target.getAllPromotionResults());
        toRefresh.add(target);
        refreshModifiedModelsAfter(toRefresh);
   	 }
   	 else
   	 {
   		 super.transferPromotionsToOrder(source,target,onlyTransferAppliedPromotions);
   	 }
    }

	@Override
	protected CalculationService getCalculationService()
	{
		if (baseStoreService.getCurrentBaseStore() != null && asahiSiteUtil.isCub())
		{
			return sabmDummyCalculationService;
		}
		return calculationService;
	}
}

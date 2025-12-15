package com.sabmiller.merchantsuiteservices.dao;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

public class WestpacOrderDao extends DefaultGenericDao<AbstractOrderModel> implements GenericDao<AbstractOrderModel>{
    public WestpacOrderDao() {
        super(AbstractOrderModel._TYPECODE);
    }

    public AbstractOrderModel getOrder(final String cartCode, final BaseStoreModel storeModel){
        final Map<String, Object> params = new HashMap<>();
        params.put(AbstractOrderModel.CARTCODE, cartCode);
        params.put(AbstractOrderModel.STORE, storeModel);
        final List<AbstractOrderModel> list = find(params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}

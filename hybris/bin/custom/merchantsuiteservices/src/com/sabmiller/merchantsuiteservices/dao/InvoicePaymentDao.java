package com.sabmiller.merchantsuiteservices.dao;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.model.InvoicePaymentModel;

public class InvoicePaymentDao extends DefaultGenericDao<InvoicePaymentModel> implements GenericDao<InvoicePaymentModel> {
    public InvoicePaymentDao() {
        super(InvoicePaymentModel._TYPECODE);
    }

    public InvoicePaymentModel getInvoice(final String invoiceTrackingNumber, final UserModel user){
        final Map<String, Object> params = new HashMap<>();
        params.put(InvoicePaymentModel.PAYMENTCODE, invoiceTrackingNumber);
        params.put(InvoicePaymentModel.USER, user);
        final List<InvoicePaymentModel> list = find(params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public InvoicePaymentModel getInvoice(final String invoiceTrackingNumber){
        final Map<String, Object> params = new HashMap<>();
        params.put(InvoicePaymentModel.PAYMENTCODE, invoiceTrackingNumber);
        final List<InvoicePaymentModel> list = find(params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}

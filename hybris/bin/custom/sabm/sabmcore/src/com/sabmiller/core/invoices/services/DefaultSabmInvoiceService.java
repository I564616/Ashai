package com.sabmiller.core.invoices.services;

import com.sabmiller.core.enums.InvoiceDiscrepancyRaisedFromEnum;
import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.invoices.dao.SabmInvoiceDao;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class DefaultSabmInvoiceService implements SabmInvoiceService {

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "invoiceDao")
    private SabmInvoiceDao invoiceDao;

    @Override
    public List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancyForB2BUnit(final List<B2BUnitModel> b2bUnits) {

        List<InvoiceDiscrepancyRequestModel> invoices  = new ArrayList<>();

        for(B2BUnitModel b2bUnit:b2bUnits ){
            invoices.addAll(invoiceDao.getRaisedInvoiceDiscrepancy(b2bUnit));
        }
        // only return request are raised from Hybris if requestId is not NULL
        return invoices != null ? invoices.stream().filter( model -> InvoiceDiscrepancyRaisedFromEnum.HYBRIS.equals(model.getRaisedFrom())).collect(Collectors.toList()) : Collections.emptyList();

    }


    @Override
    public List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancyForB2BUnitsAndForDateRange(final List<B2BUnitModel> b2bUnits,
            final Date dateFrom, final Date dateTo) {

        List<InvoiceDiscrepancyRequestModel> invoices  = new ArrayList<>();

        for(B2BUnitModel b2bUnit:b2bUnits ){
            invoices.addAll(invoiceDao.getRaisedInvoiceDiscrepancyByDateRange(b2bUnit, dateFrom, dateTo));
        }

        // only return request are raised from Hybris if requestId is not NULL
        return invoices != null ? invoices.stream().filter( model -> InvoiceDiscrepancyRaisedFromEnum.HYBRIS.equals(model.getRaisedFrom())).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(final String invoiceNumber,final String requestId ) {

        List<InvoiceDiscrepancyRequestModel>  invoices = invoiceDao.findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(invoiceNumber,requestId);


        return invoices;


    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumber(final String invoiceNumber) {
        return  invoiceDao.findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(invoiceNumber,null);

    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndSapInvoiceNumber(final String invoiceNumber, final String sapInvoiceNumber) {
        return  invoiceDao.findRaisedInvoiceDiscrepancyByInvoiceNumberAndSapInvoiceNumber(invoiceNumber,sapInvoiceNumber);

    }


    }

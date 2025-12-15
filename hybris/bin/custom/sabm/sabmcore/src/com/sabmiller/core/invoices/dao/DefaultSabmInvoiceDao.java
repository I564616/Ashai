package com.sabmiller.core.invoices.dao;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class DefaultSabmInvoiceDao extends AbstractItemDao implements SabmInvoiceDao {

    protected final static String SELECTCLAUSE =
            "SELECT {" + InvoiceDiscrepancyRequestModel.PK + "} FROM {" + InvoiceDiscrepancyRequestModel._TYPECODE + "} ";

    protected final static String ORDERBYCLAUSE = " ORDER BY {" + InvoiceDiscrepancyRequestModel.RAISEDDATE + "} DESC";

    protected final static String FIND_INVOICE_DISCREPANCY_REQUEST_FOR_B2BUNIT_ID =
            SELECTCLAUSE + "WHERE " + " {" + InvoiceDiscrepancyRequestModel.B2BUNIT + "}= ?b2bUnit " + ORDERBYCLAUSE;

    protected final static String FIND_INVOICE_DISCREPANCY_REQUEST_FOR_B2BUNIT_ID_BY_DATARANGE =
            SELECTCLAUSE + "WHERE " + " {" + InvoiceDiscrepancyRequestModel.B2BUNIT + "}= ?b2bUnit AND {"
                    + InvoiceDiscrepancyRequestModel.RAISEDDATE + "} >= ?dateFrom AND {" + InvoiceDiscrepancyRequestModel.RAISEDDATE
                    + "} <= ?dateTo " + ORDERBYCLAUSE;

    private static final String FIND_INVOICE_DISCREPANCY_REQUEST_BY_INVOICENUMBER =
            "SELECT {" + InvoiceDiscrepancyRequestModel.PK + "} FROM {" + InvoiceDiscrepancyRequestModel._TYPECODE + "} WHERE {"
                    + InvoiceDiscrepancyRequestModel.INVOICENUMBER + "} = ?invoiceNumber";

    private static final String FIND_INVOICE_DISCREPANCY_REQUEST_BY_INVOICENUMBER_AND_SAPINVOICENUMBER =
            "SELECT {" + InvoiceDiscrepancyRequestModel.PK + "} FROM {" + InvoiceDiscrepancyRequestModel._TYPECODE + "} WHERE {"
                    + InvoiceDiscrepancyRequestModel.INVOICENUMBER + "} = ?invoiceNumber AND {"
                    + InvoiceDiscrepancyRequestModel.SAPINVOICENUMBER + "} = ?sapInvoiceNumber ";

    private static final String FIND_INVOICE_DISCREPANCY_REQUEST_BY_INVOICENUMBER_AND_PK =
            "SELECT {" + InvoiceDiscrepancyRequestModel.PK + "} FROM {" + InvoiceDiscrepancyRequestModel._TYPECODE + "} WHERE {"
                    + InvoiceDiscrepancyRequestModel.INVOICENUMBER + "} = ?invoiceNumber AND {" + InvoiceDiscrepancyRequestModel.PK
                    + "}= ?requestId ";

    @Override
    public List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancy(final B2BUnitModel b2bUnit) {
        final Map<String, Object> params = new HashMap<>();
        params.put("b2bUnit", b2bUnit.getPk());

        return doSearch(FIND_INVOICE_DISCREPANCY_REQUEST_FOR_B2BUNIT_ID, params, InvoiceDiscrepancyRequestModel.class);
    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancyByDateRange(final B2BUnitModel b2bUnit, final Date dateFrom,
            final Date dateTo) {
        final Map<String, Object> params = new HashMap<>();
        params.put("b2bUnit", b2bUnit.getPk());
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);

        return doSearch(FIND_INVOICE_DISCREPANCY_REQUEST_FOR_B2BUNIT_ID_BY_DATARANGE, params, InvoiceDiscrepancyRequestModel.class);
    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndSapInvoiceNumber(final String invoiceNumber,
            final String sapInvoiceNumber) {
        final Map<String, Object> params = new HashMap<>();

        if (StringUtils.isNotEmpty(invoiceNumber) && StringUtils.isNotEmpty(sapInvoiceNumber)) {
            params.put("invoiceNumber", invoiceNumber);
            params.put("sapInvoiceNumber", sapInvoiceNumber);
            return doSearch(FIND_INVOICE_DISCREPANCY_REQUEST_BY_INVOICENUMBER_AND_SAPINVOICENUMBER, params,
                    InvoiceDiscrepancyRequestModel.class);
        }

        return Collections.EMPTY_LIST;

    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(final String invoiceNumber,
            final String requestId) {
        final Map<String, Object> params = new HashMap<>();
        params.put("invoiceNumber", invoiceNumber);
        if (StringUtils.isNotEmpty(requestId)) {
            params.put("requestId", requestId);
            return doSearch(FIND_INVOICE_DISCREPANCY_REQUEST_BY_INVOICENUMBER_AND_PK, params, InvoiceDiscrepancyRequestModel.class);
        } else {
            return doSearch(FIND_INVOICE_DISCREPANCY_REQUEST_BY_INVOICENUMBER, params, InvoiceDiscrepancyRequestModel.class);
        }

    }

    @Override
    public List<InvoiceDiscrepancyRequestModel> getAllRaisedInvoiceDiscrepancy() {
        final Map<String, Object> params = new HashMap<>();
        return doSearch(SELECTCLAUSE, null, InvoiceDiscrepancyRequestModel.class);
    }

    protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final Class<T> resultClass) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
        if (params != null) {
            fQuery.addQueryParameters(params);
        }

        fQuery.setResultClassList(Collections.singletonList(resultClass));
        final SearchResult<T> searchResult = search(fQuery);
        return searchResult.getResult();
    }

    @Override
    protected <T> SearchResult<T> search(final FlexibleSearchQuery searchQuery) {
        return this.flexibleSearchService.search(searchQuery);
    }
}

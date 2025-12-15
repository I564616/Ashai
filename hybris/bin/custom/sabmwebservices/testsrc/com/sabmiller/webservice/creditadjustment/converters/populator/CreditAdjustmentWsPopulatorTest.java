package com.sabmiller.webservice.creditadjustment.converters.populator;

import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.webservice.creditadjustment.CreditAdjustment;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@UnitTest
public class CreditAdjustmentWsPopulatorTest {

    CreditAdjustmentWsPopulator  creditAdjustmentWsPopulator;

    @Before
    public void setUp() throws Exception {

          creditAdjustmentWsPopulator = new CreditAdjustmentWsPopulator();
    }



    @Test
    public void testPopulateTrimMethod() {
        CreditAdjustment.Invoice source  = new CreditAdjustment.Invoice();
        SABMInvoiceDiscrepancyData sabmInvoiceDiscrepancyData = new SABMInvoiceDiscrepancyData();
        source.setInvoiceNumber("7620146565");
        source.setPurchaseOrderNumber("7502883596 | 8796224152017");
        source.setAmount("5.00");
        source.setStatus("Approved");
        source.setType("YSCR");
        source.setSoldTo("859342");
        source.setDueDate("16102018");


        StringUtils.trim("7502883596 | 8796224152017");

        creditAdjustmentWsPopulator.populate(source,sabmInvoiceDiscrepancyData);


        assertEquals(sabmInvoiceDiscrepancyData.getInvoiceNumber(),"7502883596");
        assertEquals(sabmInvoiceDiscrepancyData.getCreditAdjustmentRequestId(),"8796224152017");

    }
}
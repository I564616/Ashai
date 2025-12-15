/**
 *
 */
package com.sabmiller.facades.invoice;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.facades.customer.impl.DefaultSABMInvoiceFacade;
import com.sabmiller.merchantsuiteservices.dao.InvoicePaymentDao;

/**
 *
 */
@UnitTest
public class DefaultSABMInvoiceFacadeTest
{
	@InjectMocks
	private final DefaultSABMInvoiceFacade defaultSABMInvoiceFacade = new DefaultSABMInvoiceFacade();;

	@Mock
	private InvoicePaymentDao invoicePaymentDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testGetSurchargeAmtforInvoiceByTrackingNumber()
	{

		final List<PaymentTransactionEntryModel> entries = new ArrayList<PaymentTransactionEntryModel>();
		final PaymentTransactionEntryModel paymentTransactionEntryModel = mock(PaymentTransactionEntryModel.class);
		given(paymentTransactionEntryModel.getRequestId()).willReturn("INV001");
		given(paymentTransactionEntryModel.getType()).willReturn(PaymentTransactionType.SURCHARGE);
		given(paymentTransactionEntryModel.getAmount()).willReturn(BigDecimal.valueOf(5.0));
		entries.add(paymentTransactionEntryModel);

		final PaymentTransactionModel paymentTransactionModel = mock(PaymentTransactionModel.class);
		given(paymentTransactionModel.getEntries()).willReturn(entries);

		final InvoicePaymentModel invoicePaymentModel = mock(InvoicePaymentModel.class);
		final PK invoicepk = PK.parse("1234567");
		given(invoicePaymentModel.getPk()).willReturn(invoicepk);
		given(invoicePaymentModel.getTransaction()).willReturn(paymentTransactionModel);

		given(invoicePaymentDao.getInvoice("INV001")).willReturn(invoicePaymentModel);

		final double surcharge = defaultSABMInvoiceFacade.getSurchargeAmtforInvoiceByTrackingNumber("INV001");

		Assert.assertEquals("5.0", Double.toString(surcharge));


	}




}

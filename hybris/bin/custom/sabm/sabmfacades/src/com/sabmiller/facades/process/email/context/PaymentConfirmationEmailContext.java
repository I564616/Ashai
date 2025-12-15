/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabm.core.model.PaymentConfirmationEmailProcessModel;
import com.sabmiller.core.model.InvoicePaymentDetailModel;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.facades.invoice.SABMInvoiceData;
import com.sabmiller.merchantsuiteservices.data.InvoicePaymentData;

/**
 * The context of confirm enabled deals, need to be update in the future stories
 *
 */
public class PaymentConfirmationEmailContext extends AbstractEmailContext<PaymentConfirmationEmailProcessModel>
{

	private Converter<InvoicePaymentModel, InvoicePaymentData> invoicePaymentConverter;
	private List<SABMInvoiceData> invoicesData;
	private InvoicePaymentData invoicePaymentData;
	private Converter<UserModel, CustomerData> customerConverter;
	private CustomerData customerData;
	private String formatedAmountPaid;
	private String formatedMsf;
	private String totalWithMsf;

	@Override
	public void init(final PaymentConfirmationEmailProcessModel paymentConfirmationEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.init(paymentConfirmationEmailProcessModel, emailPageModel);
		getInvoices(paymentConfirmationEmailProcessModel);
		customerData = getCustomerConverter().convert(paymentConfirmationEmailProcessModel.getCustomer());
	}

	public void getInvoices(final PaymentConfirmationEmailProcessModel paymentConfirmationEmailProcessModel)
	{
		final InvoicePaymentModel invoicePayment = paymentConfirmationEmailProcessModel.getInvoicePayment();
		final List<InvoicePaymentDetailModel> invoicesDetail = invoicePayment.getInvoicesDetail();
		this.invoicePaymentData = getInvoicePaymentConverter().convert(invoicePayment);
		if (null == invoicePaymentData.getReceiptNumber())
		{
			this.invoicePaymentData.setReceiptNumber(StringUtils.EMPTY);
		}
		if (null != invoicePaymentData.getPaymentInfo() && null == invoicePaymentData.getPaymentInfo().getCardType())
		{
			this.invoicePaymentData.getPaymentInfo().setCardType(StringUtils.EMPTY);
		}
		this.formatedAmountPaid = (this.invoicePaymentData.getAmount() == null ? StringUtils.EMPTY
				: formatDecimal(this.invoicePaymentData.getAmount()));
		this.invoicesData = new ArrayList<>();
		BigDecimal msfValue = getMsf(invoicePayment);
		this.setFormatedMsf((msfValue == null ? StringUtils.EMPTY : formatDecimal(msfValue)));
		Double total = invoicePaymentData.getAmount().doubleValue();
		if (msfValue != null)
		{
			total = invoicePaymentData.getAmount().doubleValue() + msfValue.doubleValue();
		}
		this.setTotalWithMsf(formatDecimal(BigDecimal.valueOf(total)));
		for (final InvoicePaymentDetailModel invoiceDetail : invoicesDetail)
		{
			final SABMInvoiceData data = new SABMInvoiceData();
			data.setInvoiceNumber(invoiceDetail.getInvoiceNumber());
			data.setPurchaseOrderNumber(invoiceDetail.getPurchaseOrderNumber());
			data.setType(invoiceDetail.getType());
			data.setOpenAmount(invoiceDetail.getAmount());
			this.invoicesData.add(data);
		}

	}

	/**
	 * @param invoicePayment
	 * @return
	 */
	private BigDecimal getMsf(final InvoicePaymentModel invoicePayment)
	{
		BigDecimal msfValue = null;
		if (CollectionUtils.isNotEmpty(invoicePayment.getTransaction().getEntries()))
		{
			for (final PaymentTransactionEntryModel entry : invoicePayment.getTransaction().getEntries())
			{
				if (PaymentTransactionType.SURCHARGE.equals(entry.getType()))
				{
					msfValue = entry.getAmount();

				}
			}
		}
		return msfValue;
	}

	public String formatDecimal(final BigDecimal bigDecimal)
	{
		final DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setGroupingUsed(false);
		return decimalFormat.format(bigDecimal);
	}

	/**
	 * @return the invoicePaymentConverter
	 */
	public Converter<InvoicePaymentModel, InvoicePaymentData> getInvoicePaymentConverter()
	{
		return invoicePaymentConverter;
	}


	public void setInvoicePaymentConverter(final Converter<InvoicePaymentModel, InvoicePaymentData> invoicePaymentConverter)
	{
		this.invoicePaymentConverter = invoicePaymentConverter;
	}


	public List<SABMInvoiceData> getInvoicesData()
	{
		return invoicesData;
	}


	public void setInvoicesData(final List<SABMInvoiceData> invoicesData)
	{
		this.invoicesData = invoicesData;
	}


	public InvoicePaymentData getInvoicePaymentData()
	{
		return invoicePaymentData;
	}


	public void setInvoicePaymentData(final InvoicePaymentData invoicePaymentData)
	{
		this.invoicePaymentData = invoicePaymentData;
	}


	public Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}


	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}


	public CustomerData getCustomerData()
	{
		return customerData;
	}


	public void setCustomerData(final CustomerData customerData)
	{
		this.customerData = customerData;
	}


	public String getFormatedAmountPaid()
	{
		return formatedAmountPaid;
	}


	public void setFormatedAmountPaid(final String formatedAmountPaid)
	{
		this.formatedAmountPaid = formatedAmountPaid;
	}

	public String getFormatedMsf()
	{
		return formatedMsf;
	}

	public void setFormatedMsf(final String formatedMsf)
	{
		this.formatedMsf = formatedMsf;
	}

	public String getTotalWithMsf()
	{
		return totalWithMsf;
	}

	public void setTotalWithMsf(final String totalWithMsf)
	{
		this.totalWithMsf = totalWithMsf;
	}

	@Override
	protected BaseSiteModel getSite(final PaymentConfirmationEmailProcessModel paymentConfirmationEmailProcessModel)
	{
		return paymentConfirmationEmailProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final PaymentConfirmationEmailProcessModel paymentConfirmationEmailProcessModel)
	{
		return paymentConfirmationEmailProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final PaymentConfirmationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}
}

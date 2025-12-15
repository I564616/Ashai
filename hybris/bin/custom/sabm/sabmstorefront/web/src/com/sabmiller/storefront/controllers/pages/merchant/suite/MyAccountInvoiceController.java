package com.sabmiller.storefront.controllers.pages.merchant.suite;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;
import com.sabmiller.facades.customer.SABMInvoiceFacade;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteInvalidInvoiceDataException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMSurchargeCalculationException;
import com.sabmiller.merchantsuiteservices.facade.impl.SABMMerchantSuitePaymentFacadeImpl;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


@Controller
@RequestMapping(value = "/your-business/billing")
public class MyAccountInvoiceController extends SabmAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(MyAccountInvoiceController.class.getName());

	private static final String URL_INVOICE_CONFIRMATION="/your-business/billing/confirmation/";
	private static final String REDIRECT_URL_INVOICE_CONFIRMATION = REDIRECT_PREFIX + URL_INVOICE_CONFIRMATION;
	private static final String REDIRECT_URL_INVOICE =  "/your-business/billing";
	private static final String INVOICE_WAIT_CMS_PAGE = "InvoicePaymentWaitingPage";

	@Resource(name = "sabmInvoiceFacade")
	protected SABMInvoiceFacade invoiceFacade;

	@Resource
    private SABMMerchantSuitePaymentFacadeImpl sabmMerchantSuitePaymentFacade;

	@ResponseBody
	@GetMapping("/pay/waitJson/{trackingNumber}")
	public String waitJson(@PathVariable(value = "trackingNumber") final String trackingNumber, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{

		LOG.info("Checking of invoice is paid for ref : " + trackingNumber);
		if (sabmMerchantSuitePaymentFacade.isInvoicePaid(trackingNumber))
		{
			LOG.info("INVOICE PAID. Redirecting to Invoice Confirmation");
			redirectModel.addFlashAttribute("pageType", getPageType());
			return URL_INVOICE_CONFIRMATION + trackingNumber;
		}

		if (sabmMerchantSuitePaymentFacade.hasExceededInvoiceWaitTimeout(trackingNumber))
		{
			LOG.info("INVOICE NOT PAID. Redirecting to Timeout Page");
			return "/your-business/billing?timeoutError=" + trackingNumber;
		}
		return REDIRECT_URL_INVOICE + "?paymentError=true";
	}

	@GetMapping("/pay/wait/{trackingNumber}")
	public String wait(@PathVariable(value = "trackingNumber") final String trackingNumber, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		LOG.info("Redirect Request recieved for wait page for tracking : " + trackingNumber);
		model.addAttribute("trackingNumber", trackingNumber);
		storeCmsPageInModel(model, getContentPageForLabelOrId(INVOICE_WAIT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(INVOICE_WAIT_CMS_PAGE));
		redirectModel.addFlashAttribute("pageType", getPageType());
		return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentWaitPage;
	}

	/**
	 * Controller Method for Pay By EFT Option for Invoice Payment Page
	 * @param invoices
	 * @param amount
	 * @param currencyIso
	 * @param userAccountName
	 * @param userAccountNumber
	 * @param userAccountBSB
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@ResponseBody
	@PostMapping("/payByEFT")
	public String payByEFT(@RequestParam(value = "invoices", required = true) final String invoices,
			@RequestParam(value = "amount", required = true) final BigDecimal amount,
			@RequestParam(value = "currencyIso", required = true) final String currencyIso,
			@RequestParam(value = "userAccountName", required = false) final String userAccountName,
			@RequestParam(value = "userAccountNumber", required = false) final String userAccountNumber,
			@RequestParam(value = "userAccountBSB", required = false) final String userAccountBSB)
			throws CMSItemNotFoundException {
			//Splitting into different invoice numbers seperated  by comma
            final String[] tokens = invoices.split(",");
            final Set<String> invoiceList = Sets.newHashSet(tokens);
            try {
            	//creating new data object to hold bank account details
                SABMBankDetailsData bankDetailsData = new SABMBankDetailsData();
                bankDetailsData.setBsb(userAccountBSB);
                bankDetailsData.setAccountNumber(userAccountNumber);
                bankDetailsData.setAccountName(userAccountName);
                //process bank transactions using bank account data submitted by customer
                String trackingNumber = this.sabmMerchantSuitePaymentFacade.initiateInvoiceEFTxn(invoiceList, amount, bankDetailsData,currencyIso);
	            return "/your-business/billing/pay/wait/" + trackingNumber;
            }
            //return to different error pages created to cater different textual information shown to the customers.
            catch(SABMMerchantSuitePaymentErrorException e)
            {
                LOG.error("Error processing Invoice Payment: Merchant Suite Payment Exception", e);
                return REDIRECT_URL_INVOICE + "?" + e.getErrorType() + "=true";
            }
            catch (SABMMerchantSuiteTokenAPIException e) {
                LOG.error("Error processing Invoice Payment: Integration Exception", e);
                return REDIRECT_URL_INVOICE + "?tokenError=true";
            }
            catch (SABMMerchantSuiteInvalidInvoiceDataException | SABMMerchantSuiteConfigurationException | SABMMerchantSuiteMissingBankDetailsException e) {
                LOG.error("Error processing Invoice Payment: Data Exception", e);
                return REDIRECT_URL_INVOICE + "?paymentError=true";
            }
            catch (Exception e) {
                LOG.error("Error processing Invoice Payment: General Exception", e);
                return REDIRECT_URL_INVOICE + "?paymentError=true";
            }
    }

	/**
	 * Controller Method for Pay By Card Option for Invoice Payment Page
	 * @param invoices
	 * @param amount
	 * @param currencyIso
	 * @param cardType
	 * @return
	 */
	@ResponseBody
	@PostMapping("/payByCard")
	public SABMCreditCardTransactionData payByCard(@RequestParam(value = "invoices", required = true) final String invoices,
			@RequestParam(value = "amount", required = true) final BigDecimal amount,
			@RequestParam(value = "currencyIso", required = true) final String currencyIso,
			@RequestParam(value = "cardType", required = true) final String cardType) {
		LOG.info("Request to calculate surcharge for Invoice Transaction" + invoices +"for amount:" + amount);
		SABMCreditCardTransactionData creditCardTransactionData = null;
		//Splitting into different invoice numbers seperated  by comma
		final String[] tokens = invoices.split(",");
		final Set<String> invoiceList = Sets.newHashSet(tokens);
		try {
			//initiating invoice credit card transaction and generating auth key for the transaction
				creditCardTransactionData = this.sabmMerchantSuitePaymentFacade.initiateInvoiceCCTxn(invoiceList,amount,currencyIso,cardType);
				return creditCardTransactionData;
			}
			//return to different error pages created to cater different textual information shown to the customers.
			catch (SABMMerchantSuiteTokenAPIException e) {
				LOG.error("Error processing Invoice Payment: Integration Exception", e);
				creditCardTransactionData = new SABMCreditCardTransactionData();
				creditCardTransactionData.setError("tokenError=true" );
			}
			  catch (SABMMerchantSuiteInvalidInvoiceDataException | SABMSurchargeCalculationException | SABMMerchantSuiteConfigurationException | SABMMerchantSuiteMissingBankDetailsException e) {
				LOG.error("Error processing Invoice Payment: Data Exception", e);
				creditCardTransactionData = new SABMCreditCardTransactionData();
				creditCardTransactionData.setError("paymentError=true" );
			}
			catch (Exception e) {
				LOG.error("Error processing Invoice Payment: General Exception", e);
				creditCardTransactionData = new SABMCreditCardTransactionData();
				creditCardTransactionData.setError("paymentError=true" );
			}
		return creditCardTransactionData;
	}

	/**
	 * Controller method to handle payment response for CC payment redirect from Merchant Suite
	 *
	 * @param resultKey
	 * @param responseCode
	 * @param responseText
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/invoicePaymentResponse")
	public void response(@RequestParam(value = "ResultKey", required = true)final String resultKey,
			@RequestParam(value = "ResponseCode", required = true)final String responseCode,
			@RequestParam(value = "ResponseText", required = true)final String responseText,HttpServletResponse response)
			throws CMSItemNotFoundException, IOException {
		LOG.debug("MerchantSuite redirect received for invoice with result key [{}]", resultKey);
		try {
			//process payment response from merchant suite and redirect to waiting page.
			String invoiceNumber = this.sabmMerchantSuitePaymentFacade.processInvoiceAuthKeyCCTxn(resultKey);
			response.sendRedirect("/your-business/billing/pay/wait/" + invoiceNumber);
		}
		catch (SABMMerchantSuitePaymentErrorException e) {
			LOG.error("Error processing Invoice Payment: Payment Declined :" + e.getErrorType() , e);
			response.sendRedirect(REDIRECT_URL_INVOICE + "?" + (e.getErrorType()) + "=true");
		}
		catch (SABMMerchantSuiteConfigurationException | SABMMerchantSuiteAPIRequestInvalidException e) {
			LOG.error("Error processing Invoice Payment", e);
			response.sendRedirect(REDIRECT_URL_INVOICE + "?paymentError=true");
		}
	}

	@ResponseBody
	@PostMapping("/selectedinvoices")
	public void saveInvoicesSelection(final HttpServletRequest request,
			@RequestParam(value = "invoiceSelectedList", required = false) final String invoiceSelectedList)
	{
		if (StringUtils.isNotBlank(invoiceSelectedList))
		{
			request.getSession().setAttribute("invoiceSelectedList", invoiceSelectedList);
		}

	}

	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.MY_ACCOUNT_INVOICE.name();
	}

}

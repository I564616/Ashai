package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController.SelectOption;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Produces;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.exception.AsahiPaymentException;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.core.services.ApbNumberKeyGeneratorService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.card.payment.AsahiCreditCardTypeEnum;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.facades.sam.data.AsahiCaptureResponseData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade;
import com.apb.facades.user.ApbUserFacade;
import com.apb.integration.data.AsahiInvoiceDownloadResponse;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.checkout.form.AsahiPaymentDetailsForm;
import com.apb.storefront.forms.AsahiInvoiceHistoryForm;
import com.apb.storefront.sam.payment.form.AsahiSAMInvoiceForm;
import com.apb.storefront.sam.payment.form.AsahiSAMPaymentForm;
import com.apb.storefront.util.AsahiSAMPaymentIframeUrlUtil;
import de.hybris.platform.util.Base64;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Controller for Multi Account page
 */
@Controller
@RequestMapping(value = "/invoice")
public class AsahiSAMInvoicePageController extends ApbAbstractPageController
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AsahiSAMInvoicePageController.class);

	/** The Constant ASAHI_INVOICE_DETAIL_PAGE_ID. */
	private static final String ASAHI_INVOICE_DETAIL_PAGE_ID = "invoicedetail";

	/** The Constant INVOICE_APPLICATION_TYPE. */
	private static final String INVOICE_PDF_APPLICATION_TYPE = "application/pdf";

	/** The Constant INVOICE_PDF_CACHE_CONTROL. */
	private static final String INVOICE_PDF_CACHE_CONTROL = "must-revalidate, post-check=0, pre-check=0";

	/** The Constant BREADCRUMBS_ATTR. */
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";

	/** The Constant INVOICE_PDF_FILE_FORMAT. */
	private static final String INVOICE_PDF_FILE_FORMAT = ".pdf";

	private static final String ASAHI_PAYMENT_DETAIL_PAGE_ID = "paymentdetail";

	private static final String SAM_VISA_TYPE_LIST = "sam.payment.integration.visa.card.list";
	private static final String SAM_MASTER_TYPE_LIST = "sam.payment.integration.master.card.list";
	private static final String SAM_AMEX_TYPE_LIST = "sam.payment.integration.amex.card.list";

	private static final String MAX_CREDIT_CARDS_ALLOWED = "max.saved.cards.allowed";

	/**
	 * THE CONSTANT FOR AMEX CARD SURCHARGE
	 */
	private static final String SAM_CREDIT_SURCHARGE_FOR_AMEX = "sam.credit.surcharge.for.amex.card";
	/**
	 * THE CONSTANT FOR VISA CARD SURCHARGE
	 */
	private static final String SAM_CREDIT_SURCHARGE_FOR_VISA = "sam.credit.surcharge.for.visa.card";
	/**
	 * THE CONSTANT FOR MASTER CARD SURCHARGE
	 */
	private static final String SAM_CREDIT_SURCHARGE_FOR_MASTER = "sam.credit.surcharge.for.master.card";

	private static final String ADD_SURCHARGE = "isAddSurcharge";

	private static final String REDIRECT_TO_PAY_CONFIRM_PAGE = "/invoice/payment/paymentConfirmation";

	private static final String ASAHI_PAYMENT_CONFIRMATION_PAGE_ID = "paymentConfirmation";

	private static final Double ZERO_TOTAL_PAID_AMOUNT = 0.00;
	private static final int DEFAULT_ZERO_INVOICE = 0;
	private static final String SAM_MAKE_A_PAYMENT_BREADCRUMB = "sam.make.a.payment.breadcrumb";
	private static final String ADDITIONAL_CENT_TO_FATZEBRA = "additional.cent.in.ordertotal";


	private static final String REDIRECT_TO_SUBMIT_INVOICE = "/invoice/submitInvoice";

	private static final String REDIRECT_TO_IVOICE_PAGE = "/invoice";
	
	/** The Constant INVOICE_PAYMENT. */
	private static final String INVOICE_PAYMENT = "payment";
	
	/** The user service. */
	@Resource
	private UserService userService;

	/** The asahi SAM invoice facade. */
	@Resource
	private AsahiSAMInvoiceFacade asahiSAMInvoiceFacade;

	/** The invoice breadcrumb builder. */
	@Resource(name = "invoiceBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder invoiceBreadcrumbBuilder;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "asahiSAMPaymentIframeUrlUtil")
	private AsahiSAMPaymentIframeUrlUtil asahiSAMPaymentIframeUrlUtil;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "sabmCheckoutFacade")
	AcceleratorCheckoutFacade acceleratorCheckoutFacade;

	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource(name = "addressConverter")
	private Converter<AddressModel, AddressData> apbB2bAddressConverter;

	@Resource
	private ApbNumberKeyGeneratorService apbNumberKeyGeneratorService;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private SessionService sessionService;
	
	@Resource
 	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "configurationService")
    private ConfigurationService configurationService;
	
	
	
	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;

	/**
	 * Method to return the saved credit cards.
	 *
	 * @return
	 */
	@ModelAttribute("creditCards")
	public List<CCPaymentInfoData> getCreditCard()
	{
		return apbUserFacade.getCCPaymentInfos(true);
	}

	@GetMapping
	@RequireHardLogIn
	public String showInvoiceDetails(@RequestParam(value = "status", defaultValue = "open") final String status,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode, final String documentType,
			final String dueStatus, final String keyword, final Model model, final HttpServletRequest request,
			final HttpServletResponse response) throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		//check if the user has the SAM access...
		//if yes then display invoicing details..
		//If No Display the set up info...
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("pendingApproval",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("requestDenied",asahiCoreUtil.isSAMAccessDenied());
		model.addAttribute("isDirectDebitEnabled",asahiCoreUtil.isDirectDebitEnabled());
		model.addAttribute("accessType",accessType);
		model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
					&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
					asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
		
		if (!accessType.equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS))
		{
			asahiSAMInvoiceFacade.setSAMHeaderSessionAttributes(model);
			model.addAttribute("payAccess", true);
			final int pageSize = Integer
					.parseInt(asahiConfigurationService.getString(ApbCoreConstants.SAM_INVOICE_HISTORY_PAGESIZE, "10"));
			final PageableData pageableData = createPageableData(page, pageSize, null, showMode);
			model.addAttribute("invoiceDetails",
					asahiSAMInvoiceFacade.getSAMInvoiceList(status, pageableData, documentType, dueStatus, keyword));
		}

		if ((getContentPageForLabelOrId(ASAHI_INVOICE_DETAIL_PAGE_ID)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(ASAHI_INVOICE_DETAIL_PAGE_ID)).getBackgroundImage().getURL());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ASAHI_INVOICE_DETAIL_PAGE_ID));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ASAHI_INVOICE_DETAIL_PAGE_ID));
		updatePageTitle(model, getContentPageForLabelOrId(ASAHI_INVOICE_DETAIL_PAGE_ID));
		model.addAttribute(BREADCRUMBS_ATTR, invoiceBreadcrumbBuilder.getBreadcrumbs(null));
		if(null != asahiCoreUtil.getCurrentB2BCustomer()) {
		    final boolean disableDebitInvRef = null != asahiCoreUtil.getCurrentB2BCustomer().getDisableDebitInvRefPopup() ? asahiCoreUtil.getCurrentB2BCustomer().getDisableDebitInvRefPopup() : false;
            model.addAttribute("disableDebitInvRefPopup", disableDebitInvRef);
        }
		
		return getViewForPage(model);

	}

	@PostMapping(value = "/fetchInvoiceRecords", produces = "application/json")
	@RequireHardLogIn
	public String fetchPaymentRecords(final AsahiInvoiceHistoryForm invoiceHistoryForm, final Model model)
	{
		//check if the user has the SAM access...
		//if yes then display invoicing details..
		//If No Display the set up info...
		if (!sabmCustomerFacade.isSAMPayAccessEnable())
		{

			model.addAttribute("payAccess", false);
		}
		else
		{
			asahiSAMInvoiceFacade.setSAMHeaderSessionAttributes(model);
			model.addAttribute("payAccess", true);
			final int pageSize = Integer
					.parseInt(asahiConfigurationService.getString(ApbCoreConstants.SAM_INVOICE_HISTORY_PAGESIZE, "10"));
			final PageableData pageableData = createPageableData(invoiceHistoryForm.getPage(), pageSize, null, ShowMode.Page);
			model.addAttribute("invoiceDetails",
					asahiSAMInvoiceFacade.getSAMInvoiceList(invoiceHistoryForm.getStatus(), pageableData,
							invoiceHistoryForm.getDocumentType(), invoiceHistoryForm.getDueStatus(), invoiceHistoryForm.getKeyword()));
		}
		return "fragments/account/invoiceHistoryResponse";

	}


	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}

	/**
	 * Download document.
	 *
	 * @param request
	 *           the request
	 * @param response
	 *           the response
	 * @return
	 * @return the response entity
	 */
	@Produces("application/pdf")
	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadInvoice(@RequestParam(value = "documentNumber") final String documentNumber,@RequestParam(value = "lineNumber") final String lineNumber,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final AsahiInvoiceDownloadResponse pdfRes = this.asahiSAMInvoiceFacade.getInvoicePdf(documentNumber,lineNumber);

		if (null != pdfRes && (null == pdfRes.getErrorMessage() || pdfRes.getErrorMessage().isEmpty()))
		{
			try
			{
				final byte[] asBytes = Base64.decode(pdfRes.getPdfResponse());

				final HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType(INVOICE_PDF_APPLICATION_TYPE));
				headers.add("Access-Control-Allow-Origin", "*");
				headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
				headers.add("Access-Control-Allow-Headers", "Content-Type");
				headers.add("Content-Disposition", "filename=" + pdfRes.getFileName());
				headers.add("Cache-Control", INVOICE_PDF_CACHE_CONTROL);
				headers.add("Pragma", "no-cache");
				headers.add("Expires", "0");
				headers.setContentDispositionFormData(pdfRes.getFileName() + INVOICE_PDF_FILE_FORMAT, pdfRes.getFileName() + ".pdf");

				final ResponseEntity<byte[]> response1 = new ResponseEntity<byte[]>(asBytes, headers, HttpStatus.OK);

				return response1;
			}
			catch (final Exception ex)
			{
				LOG.info("Error has occured while downloading the pdf");
			}
		} else {
			try
			{
				request.setAttribute("invoiceDownloadError", documentNumber);
				request.getRequestDispatcher("/invoice").forward(request, response);
			}
			catch (Exception e)
			{
				LOG.info("Exception while downloading document");
			}
		}
		return new ResponseEntity("For Document " + documentNumber + " Invoice Not Found.", HttpStatus.OK);
	}

	/**
	 * @param asahiSamPaymentForm
	 * @param bindingResult
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @param erroMessage
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/submitInvoice", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String paySelectedInvoice(AsahiSAMPaymentForm asahiSamPaymentForm, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) && null != asahiSamPaymentForm)
		{
			if (CollectionUtils.isEmpty(asahiSamPaymentForm.getAsahiSamInvoiceForm()))
			{
				return REDIRECT_PREFIX + REDIRECT_TO_IVOICE_PAGE;
			}
			else
			{
				setSessionInvoicePaymentForm(asahiSamPaymentForm);
			}
		}

		if (HttpMethod.GET.name().equalsIgnoreCase(request.getMethod()))
		{
			final AsahiSAMPaymentForm sessionForm = getSessionInvoicePaymentForm();
			if (null != sessionForm)
			{
				asahiSamPaymentForm = sessionForm;
			}
			else
			{
				return REDIRECT_PREFIX + REDIRECT_TO_IVOICE_PAGE;
			}
		}
		asahiCoreUtil.setSessionInvoicePaySelected(Boolean.TRUE);
		asahiSamPaymentForm.setAsahiCreditCardType(AsahiCreditCardTypeEnum.VISA);
		populateCommonModelAttributes(model, asahiSamPaymentForm);
		return getViewForPage(model);

	}


	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/payment")
	@RequireHardLogIn
	public String showPaymentDetails(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		final AsahiSAMPaymentForm asahiSamPaymentForm;

		final UserModel user = userService.getCurrentUser();
		final B2BUnitModel b2bUnit = ((B2BCustomerModel)user).getDefaultB2BUnit();
		final AsahiB2BUnitModel payerAccountModel = ((AsahiB2BUnitModel)b2bUnit).getPayerAccount();
		asahiCoreUtil.setSessionInvoicePaySelected(Boolean.FALSE);
		asahiCoreUtil.removeSessionInvoicePaymentForm();

		final AsahiSAMPaymentData asahiSAMPaymentData = asahiSAMInvoiceFacade.getAllDueNowOpenInvoices();
		if (CollectionUtils.isEmpty(asahiSAMPaymentData.getInvoice()))
		{
			return REDIRECT_PREFIX + REDIRECT_TO_IVOICE_PAGE;
		}
		final String payerAccount = sessionService.getAttribute("payerAccountID");
		final Double bal = asahiSAMInvoiceFacade.calculateTotalAmount(asahiSAMPaymentData.getInvoice());
		final Double balWithPayment = this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_PAYMENT,
   				payerAccount, INVOICE_PAYMENT, null, null, payerAccountModel.getCooDate());
		
		asahiSAMPaymentData.setTotalAmount(bal-balWithPayment);


		if (!model.containsAttribute("asahiSamPaymentForm"))
		{
			asahiSamPaymentForm = new AsahiSAMPaymentForm();
		}
		else
		{
			asahiSamPaymentForm = (AsahiSAMPaymentForm) model.asMap().get("asahiSamPaymentForm");
		}
		asahiSamPaymentForm.setAsahiCreditCardType(AsahiCreditCardTypeEnum.VISA);

		populateAsahiSamPaymentForm(asahiSamPaymentForm, asahiSAMPaymentData);
		populateCommonModelAttributes(model, asahiSamPaymentForm);



		return getViewForPage(model);

	}

	/**
	 * Method to make Payment and redirect to payment confirmation page.
	 *
	 * @param asahiSamPaymentForm
	 * @param bindingResult
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return
	 * @throws CMSItemNotFoundException
	 * @throws AsahiPaymentException
	 */
	@PostMapping("/payment")
	@RequireHardLogIn
	public String makePayment(final AsahiSAMPaymentForm asahiSamPaymentForm, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request)
			throws CMSItemNotFoundException, AsahiPaymentException
	{
		AsahiCaptureResponseData asahiCaptureResponseData = null;

		if (CollectionUtils.isEmpty(asahiSamPaymentForm.getAsahiSamInvoiceForm()))
		{
			return REDIRECT_PREFIX + REDIRECT_TO_IVOICE_PAGE;
		}

		final AsahiSAMPaymentData asahiSAMPaymentData = createAsahiSAMPaymentData(asahiSamPaymentForm);

		String selectedCreditCardType = null;
		if (null != asahiSamPaymentForm.getAsahiCreditCardType())
		{
			selectedCreditCardType = asahiSamPaymentForm.getAsahiCreditCardType().toString().toUpperCase();
		}

		final List<CCPaymentInfoData> cardList = apbUserFacade.getCCPaymentInfos(true);
		if (selectedCreditCardType == null)
		{
			selectedCreditCardType = AsahiCreditCardTypeEnum.VISA.toString();
			if (CollectionUtils.isNotEmpty(cardList))
			{
				selectedCreditCardType = cardList.get(0).getCardType();
			}
		}

		if (null != asahiSamPaymentForm.getAsahiPaymentDetailsForm() && asahiSAMInvoiceFacade.isAddSurcharge()
				&& StringUtils.isNotEmpty(selectedCreditCardType)
				&& StringUtils.isNotEmpty(asahiSamPaymentForm.getAsahiPaymentDetailsForm().getCardTypeInfo())
				&& !selectedCreditCardType.equalsIgnoreCase(asahiSamPaymentForm.getAsahiPaymentDetailsForm().getCardTypeInfo()))
		{
			GlobalMessages.addErrorMessage(model, "asahi.payment.card.mismatch.message");
			if (asahiCoreUtil.getSessionIsInvoicePaySelected())
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"asahi.payment.card.mismatch.message");
				return REDIRECT_PREFIX + REDIRECT_TO_SUBMIT_INVOICE;
			}
			return showPaymentDetails(model, redirectModel);
		}

		/* payment integration start */

		/* create a PaymentData this will be used to handle further processing */
		final AsahiPaymentDetailsData asahiPaymentDetailsData = setAsahiPaymentDetailsData(
				asahiSamPaymentForm.getAsahiPaymentDetailsForm());

		//set the client IP Address
		asahiPaymentDetailsData.setCustomerIP(asahiSAMPaymentIframeUrlUtil.getClientIPAddress(request));

		updateCardAmountAndCardReference(asahiPaymentDetailsData, asahiSAMPaymentData);

		asahiPaymentDetailsData.setAsahiSAMPaymentData(asahiSAMPaymentData);

		if (asahiSamPaymentForm.isSaveCreditCard() && allowAddCart(cardList.size()))
		{
			try
			{
				final AsahiPaymentDetailsForm paymentDetailsForm = asahiSamPaymentForm.getAsahiPaymentDetailsForm();
				acceleratorCheckoutFacade.createPaymentSubscription(populatePaymentCardData(paymentDetailsForm));
			}
			catch (final Exception exp)
			{
				LOG.error("Card is not saved ", exp);
			}

		}

		if (asahiPaymentDetailsData.getTotalAmount() <= ZERO_TOTAL_PAID_AMOUNT)
		{
			asahiCaptureResponseData = createCaptureResponseData();

			redirectModel.addFlashAttribute("asahiCaptureResponse", asahiCaptureResponseData);
			return REDIRECT_PREFIX + REDIRECT_TO_PAY_CONFIRM_PAGE;
		}
		else
		{

			try
			{
				//make service request for payment
				asahiSAMInvoiceFacade.makeCreditCardPayment(asahiPaymentDetailsData);
			}

			catch (final AsahiPaymentException e)
			{
				return handlePaymentException(model, redirectModel, e);
			}
			catch (final Exception e)
			{
				return handlePaymentException(model, redirectModel, e);
			}

			//make a Separate  Capture Request start.

			try
			{
				asahiCaptureResponseData = asahiSAMInvoiceFacade.makePaymentCaptureRequest(asahiPaymentDetailsData);

			}
			catch (final Exception e)
			{
				return handlePaymentException(model, redirectModel, e);

			}

			//if payment is succcessful then redirect to payment confirmation page else stay back on the same page.
			if (null != asahiCaptureResponseData)
			{
				asahiCaptureResponseData.setInvoiceCount(asahiPaymentDetailsData.getAsahiSAMPaymentData().getInvoice().size());
				asahiCaptureResponseData
						.setTotalPaidAmount(String.format("%.2f", asahiPaymentDetailsData.getAsahiSAMPaymentData().getTotalAmount()));
				redirectModel.addFlashAttribute("asahiCaptureResponse", asahiCaptureResponseData);
				asahiCoreUtil.removeSessionInvoicePaymentForm();
				asahiCoreUtil.remvoveSessionInvoicePaySelected();
				//Send Invoice Payment to ECC
				if(null!=asahiPaymentDetailsData && null!=asahiPaymentDetailsData.getAsahiSAMPaymentData()){
					LOG.info("asahiPaymentDetailsData is null---");
					asahiPaymentDetailsData.getAsahiSAMPaymentData().setPaymentTransactionId(asahiCaptureResponseData.getPaymentReference());
					
					Date date = Calendar.getInstance().getTime();  
					DateFormat dateFormat = new SimpleDateFormat(ApbCoreConstants.SAM_DOCUMENT_HYBRIS_DATEPATTERN);
					String currentDate = dateFormat.format(date); 
					
					asahiPaymentDetailsData.getAsahiSAMPaymentData().setTransactionDate(currentDate);
					this.asahiSAMInvoiceFacade.sendInvoicePayment(asahiPaymentDetailsData.getAsahiSAMPaymentData());
					
					//Generate Payment Confirmation Email Process
					this.asahiSAMInvoiceFacade.generatePaymentConfirmationProcess(asahiCaptureResponseData, asahiPaymentDetailsData);
				}
				
				return REDIRECT_PREFIX + REDIRECT_TO_PAY_CONFIRM_PAGE;
			}
			else
			{
				LOG.error("Credit Card Payment could not be completed.");
				GlobalMessages.addErrorMessage(model, "asahi.payment.failed.message");
				if (asahiCoreUtil.getSessionIsInvoicePaySelected())
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"asahi.payment.failed.message");
					return REDIRECT_PREFIX + REDIRECT_TO_SUBMIT_INVOICE;
				}
				return showPaymentDetails(model, redirectModel);
			}
		}

		// capture request end
		/* payment integration end */

	}

	/**
	 * This Method will create SAMPaymentData.
	 *
	 * @param asahiSamPaymentForm
	 * @return
	 */
	private AsahiSAMPaymentData createAsahiSAMPaymentData(final AsahiSAMPaymentForm asahiSamPaymentForm)
	{
		final AsahiSAMPaymentData asahiSAMPaymentData = new AsahiSAMPaymentData();
		final List<AsahiSAMInvoiceData> invoiceList = new ArrayList<>();
		asahiSAMPaymentData.setPaymentReference(asahiSamPaymentForm.getSamPaymentReference());
		asahiSAMPaymentData.setPartialPaymentReason(asahiSamPaymentForm.getSamPaymentReason());
		asahiSAMPaymentData.setTotalAmount(Double.parseDouble(asahiSamPaymentForm.getTotalPayableAmount()));

		if (CollectionUtils.isNotEmpty(asahiSamPaymentForm.getAsahiSamInvoiceForm()))
		{
			for (final AsahiSAMInvoiceForm invoiceForm : asahiSamPaymentForm.getAsahiSamInvoiceForm())
			{
				final AsahiSAMInvoiceData invoiceData = new AsahiSAMInvoiceData();
				invoiceData.setDocumentNumber(invoiceForm.getDocNumber());
				invoiceData.setLineNumber(invoiceForm.getLineNumber());
				invoiceData.setDocumentType(invoiceForm.getDocumentType());
				invoiceData.setRemainingAmount(invoiceForm.getRemainingAmount());
				invoiceData.setTotalPaidAmount(invoiceForm.getPaidAmount());
				invoiceList.add(invoiceData);
			}
			asahiSAMPaymentData.setInvoice(invoiceList);
		}

		return asahiSAMPaymentData;
	}

	/**
	 * This will create a capture response for hybris reference
	 *
	 * @return
	 */
	private AsahiCaptureResponseData createCaptureResponseData()
	{
		AsahiCaptureResponseData asahiCaptureResponseData;
		asahiCaptureResponseData = new AsahiCaptureResponseData();
		asahiCaptureResponseData.setInvoiceCount(DEFAULT_ZERO_INVOICE);
		asahiCaptureResponseData.setTotalPaidAmount(ZERO_TOTAL_PAID_AMOUNT.toString());
		final String referencePrefixCode = asahiConfigurationService
				.getString(ApbCoreConstants.SAM_PAYMENT_HYBRIS_PREFIX_CODE + cmsSiteService.getCurrentSite().getUid(), "HY");

		asahiCaptureResponseData.setPaymentReference(apbNumberKeyGeneratorService.generateCode(referencePrefixCode));
		return asahiCaptureResponseData;
	}

	/**
	 * Payment Confirmation Page.
	 *
	 * @param model
	 * @param redirectAttributes
	 * @param asahiCaptureResponse
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/payment/paymentConfirmation")
	@RequireHardLogIn
	public String showPaymentConfirmation(final Model model, final RedirectAttributes redirectAttributes,
			@ModelAttribute("asahiCaptureResponse") final Object asahiCaptureResponse) throws CMSItemNotFoundException
	{
		model.addAttribute("asahiCaptureResponseData", asahiCaptureResponse);
		if (getContentPageForLabelOrId(ASAHI_PAYMENT_CONFIRMATION_PAGE_ID).getBackgroundImage() != null)
		{
			model.addAttribute("media",
					getContentPageForLabelOrId(ASAHI_PAYMENT_CONFIRMATION_PAGE_ID).getBackgroundImage().getURL());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ASAHI_PAYMENT_CONFIRMATION_PAGE_ID));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ASAHI_PAYMENT_CONFIRMATION_PAGE_ID));
		updatePageTitle(model, getContentPageForLabelOrId(ASAHI_PAYMENT_CONFIRMATION_PAGE_ID));


		if (null != asahiCaptureResponse && asahiCaptureResponse instanceof AsahiCaptureResponseData
				&& StringUtils.isNotEmpty(((AsahiCaptureResponseData) asahiCaptureResponse).getPaymentReference()))
		{
			return getViewForPage(model);
		}
		else
		{
			return FORWARD_PREFIX + "/404";
		}



	}

	private String handlePaymentException(final Model model, final RedirectAttributes redirectModel, final Exception exp)
			throws CMSItemNotFoundException
	{
		LOG.error("Credit Card Payment could not be completed.", exp);
		GlobalMessages.addErrorMessage(model, "asahi.payment.failed.message");
		if (asahiCoreUtil.getSessionIsInvoicePaySelected())
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "asahi.payment.failed.message",
					null);
			return REDIRECT_PREFIX + REDIRECT_TO_SUBMIT_INVOICE;
		}
		return showPaymentDetails(model, redirectModel);
	}


	/**
	 * Method to udpate total with surcharge
	 *
	 * @param redirectAttributes
	 * @param cardType
	 * @param totalAmount
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/updateSAMCreditSurcharge", produces = "application/json")
	@ResponseBody
	@RequireHardLogIn
	public String addCreditSurcharge(final RedirectAttributes redirectAttributes,
			@RequestParam(value = "cardType", required = false) final String cardType,
			@RequestParam(value = "totalAmount") final String totalAmount, final Model model)
	{
		final String totalFinalAmount = asahiSAMInvoiceFacade.updateTotalwithCreditSurcharge(cardType, totalAmount);
		return totalFinalAmount;
	}

	private void updateCardAmountAndCardReference(final AsahiPaymentDetailsData asahiPaymentDetailsData,
			final AsahiSAMPaymentData asahiSAMPaymentData)
	{

		final Double paymentAmount = asahiSAMPaymentData.getTotalAmount();
		LOG.info("SAM Order Amoount before " + paymentAmount); 	
		LOG.info("SAM INVOICE value with old code calculation:" + asahiSAMPaymentData.getTotalAmount().doubleValue() * 100);
		final BigDecimal totalAmountBD = new BigDecimal(String.format("%.2f", paymentAmount));
		final BigDecimal orderTotalAmtBigDecimal = totalAmountBD.setScale(2, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);
		final BigDecimal additionCent = configurationService.getConfiguration().getBigDecimal(ADDITIONAL_CENT_TO_FATZEBRA, BigDecimal.ZERO);
		final Double orderAmt = (orderTotalAmtBigDecimal.add(additionCent)).doubleValue();
		LOG.info("SAM INVOICE value after fix " + orderAmt);
		asahiPaymentDetailsData.setTotalAmount(orderAmt);


	}


	/**
	 * Method to populate common model attribtues.
	 *
	 * @param model
	 * @param asahiSamPaymentForm
	 * @throws CMSItemNotFoundException
	 */
	private void populateCommonModelAttributes(final Model model, final AsahiSAMPaymentForm asahiSamPaymentForm)
			throws CMSItemNotFoundException
	{
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("pendingApproval",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("requestDenied",asahiCoreUtil.isSAMAccessDenied());
		model.addAttribute("accessType",accessType);		
		model.addAttribute("asahiSamPaymentReasonList", populateAsahiSamPaymentReasons());
		model.addAttribute("asahiSamPaymentForm", asahiSamPaymentForm);
		model.addAttribute("maxNumberOfCards", getMaxSavedCardsAllowed());
		model.addAttribute("visaSurcharge", asahiConfigurationService.getString(SAM_CREDIT_SURCHARGE_FOR_VISA, "0"));
		model.addAttribute("masterSurcharge", asahiConfigurationService.getString(SAM_CREDIT_SURCHARGE_FOR_MASTER, "0"));
		model.addAttribute("amexSurcharge", asahiConfigurationService.getString(SAM_CREDIT_SURCHARGE_FOR_AMEX, "0"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute(ADD_SURCHARGE, asahiSAMInvoiceFacade.isAddSurcharge());
		model.addAttribute(BREADCRUMBS_ATTR,
				invoiceBreadcrumbBuilder.getBreadcrumbs(asahiConfigurationService.getString(SAM_MAKE_A_PAYMENT_BREADCRUMB, "")));

		if (getContentPageForLabelOrId(ASAHI_PAYMENT_DETAIL_PAGE_ID).getBackgroundImage() != null)
		{
			model.addAttribute("media", getContentPageForLabelOrId(ASAHI_PAYMENT_DETAIL_PAGE_ID).getBackgroundImage().getURL());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ASAHI_PAYMENT_DETAIL_PAGE_ID));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ASAHI_PAYMENT_DETAIL_PAGE_ID));
		updatePageTitle(model, getContentPageForLabelOrId(ASAHI_PAYMENT_DETAIL_PAGE_ID));

		model.addAttribute("iframePostUrl", asahiSAMPaymentIframeUrlUtil.getIframeUrl());

	}

	/**
	 * This method will populate AsahiSamPaymentForm
	 *
	 * @param asahiSamPaymentForm
	 * @param asahiSAMPaymentData
	 */
	private void populateAsahiSamPaymentForm(final AsahiSAMPaymentForm asahiSamPaymentForm,
			final AsahiSAMPaymentData asahiSAMPaymentData)
	{
		asahiSamPaymentForm.setInitialTotalAmount(String.format("%.2f", asahiSAMPaymentData.getTotalAmount()));
		asahiSamPaymentForm.setInvoices(asahiSAMPaymentData.getInvoice());
		final List<AsahiSAMInvoiceData> invoiceList = asahiSAMPaymentData.getInvoice();
		final List<AsahiSAMInvoiceForm> invoiceFormList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(invoiceList))
		{
			invoiceList.stream().forEach(invoice -> invoiceFormList.add(
					new AsahiSAMInvoiceForm(invoice.getDocumentNumber(), invoice.getRemainingAmount(), invoice.getDocumentType(),invoice.getLineNumber())));
		}
		asahiSamPaymentForm.setAsahiSamInvoiceForm(invoiceFormList);
		asahiSamPaymentForm.setTotalInvoiceCount(asahiSAMPaymentData.getInvoice().size());
	}

	/**
	 * Method will create data for payment reason drop down select box
	 *
	 * @return
	 */
	public List<SelectOption> populateAsahiSamPaymentReasons()
	{
		List<String> samPaymentReasons = new ArrayList<>();
		final List<SelectOption> selectBoxList = new ArrayList<SelectOption>();
		final String paymentReasons = asahiConfigurationService
				.getString("sam.payment.partial.payment.reasons." + cmsSiteService.getCurrentSite().getUid(), "");

		if (StringUtils.isNotEmpty(paymentReasons))
		{
			samPaymentReasons = Stream.of(paymentReasons.split(",")).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(samPaymentReasons))
		{
			for (final String data : samPaymentReasons)
			{

				selectBoxList.add(new SelectOption(data, data));

			}
		}
		return selectBoxList;
	}

	/**
	 * Method to check whether max allowed credits cards added or not.
	 *
	 * @param cardCount
	 * @return
	 *
	 */
	private boolean allowAddCart(final Integer cardCount)
	{
		return cardCount < getMaxSavedCardsAllowed() ? true : false;
	}

	/**
	 * @return
	 */
	private int getMaxSavedCardsAllowed()
	{
		return Integer.parseInt(asahiConfigurationService.getString(MAX_CREDIT_CARDS_ALLOWED, "3"));
	}

	/**
	 * @param asahiPaymentDetailsForm
	 * @return
	 *
	 * 		  Method to create payment details data.
	 */
	private AsahiPaymentDetailsData setAsahiPaymentDetailsData(final AsahiPaymentDetailsForm asahiPaymentDetailsForm)
	{
		final AsahiPaymentDetailsData asahiPaymentDetailsData = new AsahiPaymentDetailsData();
		asahiPaymentDetailsData.setCardExpiry(asahiPaymentDetailsForm.getCardExpiry());
		asahiPaymentDetailsData.setCardNumber(asahiPaymentDetailsForm.getCardNumber());
		asahiPaymentDetailsData.setTransactionMessage(asahiPaymentDetailsForm.getMessage());
		asahiPaymentDetailsData.setCardToken(asahiPaymentDetailsForm.getCardToken());
		asahiPaymentDetailsData.setCardTypeInfo(asahiPaymentDetailsForm.getCardTypeInfo());
		asahiPaymentDetailsData.setResponseCode(asahiPaymentDetailsForm.getResponseCode());
		asahiPaymentDetailsData.setSamPayment(true);
		//customer is added to the paymentDetailsData
	    asahiPaymentDetailsData.setCustomerNumber(asahiCoreUtil.getDefaultB2BUnit().getUid().substring(1));
		return asahiPaymentDetailsData;

	}

	/**
	 * @param paymentDetailsForm
	 * @return
	 */
	public CCPaymentInfoData populatePaymentCardData(final AsahiPaymentDetailsForm paymentDetailsForm)
	{
		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());

		final String expiryDate = paymentDetailsForm.getCardExpiry();
		String[] splitExpDate = new String[2];
		if (null != expiryDate)
		{
			splitExpDate = expiryDate.split("/");
		}
		paymentInfoData.setCardType(setCardType(paymentDetailsForm.getCardTypeInfo().toString()));
		paymentInfoData.setToken(paymentDetailsForm.getCardToken());
		paymentInfoData.setExpiryMonth(splitExpDate[0]);
		paymentInfoData.setExpiryYear(splitExpDate[1]);
		paymentInfoData.setAccountHolderName(paymentDetailsForm.getCardHolderName());
		paymentInfoData.setSaved(true);
		paymentInfoData.setBillingAddress(setBillingAddressForCard());
		return paymentInfoData;
	}

	private String setCardType(final String cardType)
	{
		final String visaCardTypes = asahiConfigurationService.getString(SAM_VISA_TYPE_LIST, "Visa,VISA");
		final List<String> visaTypes = new ArrayList<>(Arrays.asList(visaCardTypes.split(",")));
		if (visaTypes.contains(cardType))
		{
			return CreditCardType.VISA.getCode();
		}
		final String masterCardTypes = asahiConfigurationService.getString(SAM_MASTER_TYPE_LIST, "MasterCard");
		final List<String> masterTypes = new ArrayList<>(Arrays.asList(masterCardTypes.split(",")));
		if (masterTypes.contains(cardType))
		{
			return CreditCardType.MASTER.getCode();
		}
		final String amexCardTypes = asahiConfigurationService.getString(SAM_AMEX_TYPE_LIST, "Amex");
		final List<String> amexTypes = new ArrayList<>(Arrays.asList(amexCardTypes.split(",")));
		if (amexTypes.contains(cardType))
		{
			return CreditCardType.AMEX.getCode();
		}

		return null;
	}

	private AddressData setBillingAddressForCard()
	{
		final AsahiB2BUnitModel b2bUnit = apbB2BUnitService.getCurrentB2BUnit();
		AddressData billingAddress = null;
		if (null != b2bUnit)
		{
			if (null != b2bUnit.getBillingAddress())
			{
				billingAddress = apbB2bAddressConverter.convert(b2bUnit.getBillingAddress());
			}
			else if (CollectionUtils.isNotEmpty(b2bUnit.getAddresses()))
			{
				billingAddress = apbB2bAddressConverter.convert(b2bUnit.getAddresses().iterator().next());
			}
			else
			{
				@SuppressWarnings("unchecked")
				final List<AddressData> addresses = (List<AddressData>) acceleratorCheckoutFacade.getSupportedDeliveryAddresses(true);
				billingAddress = addresses.stream().filter(address -> address.isBillingAddress()).findFirst().orElse(null);
			}
		}
		if (null == billingAddress)
		{
			billingAddress = new AddressData();
			final CountryData country = new CountryData();
			country.setIsocode("AU");
			billingAddress.setCountry(country);
		}

		return billingAddress;

	}

	/**
	 * Set the payment invoice form in session.
	 *
	 * @param asahiSamPaymentForm
	 */
	private void setSessionInvoicePaymentForm(final AsahiSAMPaymentForm asahiSamPaymentForm)
	{

		sessionService.setAttribute(ApbCoreConstants.INVOICE_PAYMENT_FORM, asahiSamPaymentForm);
	}

	/**
	 * @return session inovicepaymentform
	 */
	private AsahiSAMPaymentForm getSessionInvoicePaymentForm()
	{
		AsahiSAMPaymentForm sessionForm = null;
		final Object sessionObject = sessionService.getAttribute(ApbCoreConstants.INVOICE_PAYMENT_FORM);

		if (null != sessionObject)
		{
			sessionForm = (AsahiSAMPaymentForm) sessionObject;
		}
		return sessionForm;
	}

    @PostMapping("/disableDebitInvRefPopup")
    @ResponseBody
    public String updateDebitInvRefPopup(){
	    if(null != asahiCoreUtil.getCurrentB2BCustomer()){
	        final B2BCustomerModel customer = asahiCoreUtil.getCurrentB2BCustomer();
	        customer.setDisableDebitInvRefPopup(true);
	        asahiCoreUtil.updateB2BCustomer(customer);
        }
        return String.format("popup attribute in db after save called : %s", asahiCoreUtil.getCurrentB2BCustomer().getDisableDebitInvRefPopup());
    }

	@Produces("application/pdf")
	@GetMapping("/delivery")
	public ResponseEntity<byte[]> getDeliveryInfo(@RequestParam(value = "deliveryNumber") final String deliveryNumber,final HttpServletRequest request, final HttpServletResponse response)
	{	
		// url changes to /delivery instead of /send
		// get doc no and line number from asahisaminvoice
		final AsahiSAMInvoiceModel existingInvoice = this.asahiSAMInvoiceService.getInvoiceByDeliveryNumber(deliveryNumber);

		if(null != existingInvoice) {
			String documentNumber = existingInvoice.getDocumentNumber();
			String lineNumber = existingInvoice.getLineNumber();

			final AsahiInvoiceDownloadResponse pdfRes = this.asahiSAMInvoiceFacade.getInvoicePdf(documentNumber, lineNumber);

			if (null != pdfRes && (null == pdfRes.getErrorMessage() || pdfRes.getErrorMessage().isEmpty())) {
				try {
					final byte[] asBytes = Base64.decode(pdfRes.getPdfResponse());

					final HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.parseMediaType(INVOICE_PDF_APPLICATION_TYPE));
					headers.add("Access-Control-Allow-Origin", "*");
					headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
					headers.add("Access-Control-Allow-Headers", "Content-Type");
					//headers.add("Content-Disposition", "form-data; name =\""+ pdfRes.getFileName() + "\" inline;filename=\"" + pdfRes.getFileName() + ".pdf\"");
					headers.add("Cache-Control", INVOICE_PDF_CACHE_CONTROL);
					headers.add("Pragma", "no-cache");
					headers.add("Expires", "0");
					//headers.setContentDispositionFormData(pdfRes.getFileName() + INVOICE_PDF_FILE_FORMAT, pdfRes.getFileName() + ".pdf");

					final ResponseEntity<byte[]> response1 = new ResponseEntity<byte[]>(asBytes, headers, HttpStatus.OK);

					return response1;
				} catch (final Exception ex) {
					LOG.info("Error has occured while downloading the pdf");
				}
			}

		}
			return new ResponseEntity("For Delivery " + deliveryNumber + " Invoice Not Found.", HttpStatus.OK);


	}
}

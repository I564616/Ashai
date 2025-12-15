package com.apb.storefront.controllers.pages;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiDirectDebitPaymentData;
import com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade;
import com.apb.facades.sam.payment.history.AsahiSAMPaymentHistoryFacade;
import com.apb.facades.user.ApbUserFacade;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.sam.payment.form.AsahiDirectDebitForm;
import com.apb.storefront.util.AsahiDDPaymentIframeUrlUtil;

import de.hybris.platform.acceleratorcms.component.slot.CMSPageSlotComponentService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSImageComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

/**
 * Controller for Direct Debit page
 */
@Controller
@RequestMapping(value = "/directdebit")
public class AsahiSAMDirectDebitPageController extends ApbAbstractPageController
{
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AsahiSAMDirectDebitPageController.class);
	
	/** The Constant REDIRECT_CONFIRMATION_PAGE. */
	private static final String REDIRECT_CONFIRMATION_PAGE = "redirect:/directdebit/confirmation/?accountName=";
	
	/** The Constant REDIRECT_DIRECT_DEBIT_PAGE. */
	private static final String REDIRECT_DIRECT_DEBIT_PAGE = "redirect:/directdebit";
	
	/** The Constant INVOICE_APPLICATION_TYPE. */
	private static final String INVOICE_PDF_APPLICATION_TYPE = "application/pdf";
	
	/** The Constant INVOICE_PDF_FILE_FORMAT. */
	private static final String INVOICE_PDF_FILE_FORMAT = ".pdf";
	
	/** The Constant INVOICE_PDF_CACHE_CONTROL. */
	private static final String INVOICE_PDF_CACHE_CONTROL = "must-revalidate, post-check=0, pre-check=0";
	
	/** The Constant DIRECT_DEBIT_PAGE_LABEL. */
	private static final String DIRECT_DEBIT_PAGE_LABEL = "directdebit";
		
	/** The Constant BREADCRUMBS_ATTR. */
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	
	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "site.date.format.sga";
	
	/** The Constant DIRECT_DEBIT_CONFIRMATION_PAGE. */
	private static final String DIRECT_DEBIT_CONFIRMATION_PAGE = "directDebitConfirmation";

	/** The Constant ENABLE_NEW_DIRECT_DEBIT. */
	private static final String ENABLE_NEW_DIRECT_DEBIT = "enable.new.direct.debit.sga";
	
	/** The CMS PageSlot Component Service. */
	@Resource(name = "cmsPageSlotComponentService")
	private CMSPageSlotComponentService cmsPageSlotComponentService;
	
	/** The Asahi Configuration Service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The CMS Site Service. */
	@Resource(name = "cmsSiteService")
	protected CMSSiteService cmsSiteService;
	
	/** The Media Service. */
	@Resource(name = "mediaService")
	private MediaService mediaService;
	
	@Resource
	private AsahiSAMInvoiceFacade asahiSAMInvoiceFacade;
	
	/** The invoice breadcrumb builder. */
	@Resource(name = "invoiceBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder invoiceBreadcrumbBuilder;
	
	/** The asahi SAM payment history facade. */
	@Resource
	private AsahiSAMPaymentHistoryFacade asahiSAMPaymentHistoryFacade;
	
	@Resource(name = "asahiDDPaymentIframeUrlUtil")
	private AsahiDDPaymentIframeUrlUtil asahiDDPaymentIframeUrlUtil;
	
	/** The common I 18 N service. */
	@Resource
	private CommonI18NService commonI18NService;
	
	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;
			
	@GetMapping
	@RequireHardLogIn
	public String showDirectDebit(final Model model,final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
	throws CMSItemNotFoundException
	{
		if ((getContentPageForLabelOrId(DIRECT_DEBIT_PAGE_LABEL)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(DIRECT_DEBIT_PAGE_LABEL)).getBackgroundImage().getURL());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(DIRECT_DEBIT_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(DIRECT_DEBIT_PAGE_LABEL));
		updatePageTitle(model, getContentPageForLabelOrId(DIRECT_DEBIT_PAGE_LABEL));
		model.addAttribute(BREADCRUMBS_ATTR, invoiceBreadcrumbBuilder.getBreadcrumbs(null));
		
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("pendingApproval",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("requestDenied",asahiCoreUtil.isSAMAccessDenied());
		model.addAttribute("accessType",accessType);
		
		boolean isNewDirectDebitEnable = this.asahiConfigurationService.getBoolean(ENABLE_NEW_DIRECT_DEBIT, true);
		
		if(isNewDirectDebitEnable){
			final SimpleDateFormat formatDDMMYYYY = new SimpleDateFormat(
					this.asahiConfigurationService.getString(DATE_FORMAT, "dd/MM/yyyy"));
			
			model.addAttribute("currentDate", formatDDMMYYYY.format(new Date()));
			model.addAttribute("regions", this.apbUserFacade.getStates(cmsSiteService.getCurrentSite()));
			model.addAttribute("asahiDirectDebitForm", new AsahiDirectDebitForm());
			model.addAttribute("iframePostUrl", this.asahiDDPaymentIframeUrlUtil.getIframeUrl());
			
			AsahiB2BUnitModel currentAccount = this.apbB2BUnitService.getCurrentB2BUnit();
			if(null!=currentAccount){
				AsahiDirectDebitData directDebit= this.asahiSAMPaymentHistoryFacade.getDirectDebitEntryForUser(currentAccount.getPk().toString());
				
				if(null==directDebit || null==directDebit.getDirectDebitPaymentData() || null==directDebit.getDirectDebitPaymentData().getToken()){
					model.addAttribute("enableDirectDebit", true);
				}else{
					model.addAttribute("directDebit", directDebit);
				}
			}
		}
		model.addAttribute("isNewDirectDebitEnable", isNewDirectDebitEnable);
		return getViewForPage(model);
	}
	
	@PostMapping("/submitDirectDebit")
	@RequireHardLogIn
	public String submitDirectDebit(AsahiDirectDebitForm asahiDirectDebitForm, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		AsahiDirectDebitData directDebit = this.asahiSAMPaymentHistoryFacade.saveDirectDebit(this.createDirectDebitData(asahiDirectDebitForm, new AsahiDirectDebitData()));
		
		if(directDebit.isError()){
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "sga.direct.debit.submit.error.message",
					null);
			return REDIRECT_DIRECT_DEBIT_PAGE;
		}
		request.getSession().setAttribute("accountName", directDebit.getCustAccount());
		return REDIRECT_CONFIRMATION_PAGE;
	}
	
	/**
	 * Direct debit confirmation page.
	 *
	 * @param accountName the account name
	 * @param model the model
	 * @param request the request
	 * @return the string
	 * @throws CMSItemNotFoundException the CMS item not found exception
	 */
	@GetMapping("/confirmation")
	@RequireHardLogIn
	public String directDebitConfirmationPage(final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException
	{
		if ((getContentPageForLabelOrId(DIRECT_DEBIT_CONFIRMATION_PAGE)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(DIRECT_DEBIT_CONFIRMATION_PAGE)).getBackgroundImage().getURL());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(DIRECT_DEBIT_CONFIRMATION_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(DIRECT_DEBIT_CONFIRMATION_PAGE));
		updatePageTitle(model, getContentPageForLabelOrId(DIRECT_DEBIT_CONFIRMATION_PAGE));
		
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("pendingApproval",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("requestDenied",asahiCoreUtil.isSAMAccessDenied());
		model.addAttribute("accessType",accessType);
		
		final Breadcrumb companyDetails = new Breadcrumb("#",
				getMessageSource().getMessage("direct.debit.confirmation.breadcrumb", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(companyDetails));
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		
		model.addAttribute("accountName", request.getSession().getAttribute("accountName"));

		return getViewForPage(model);
	}

	
	/**
	 * Creates the direct debit data.
	 *
	 * @param asahiDirectDebitForm the asahi direct debit form
	 * @param asahiDirectDebitData the asahi direct debit data
	 * @return 
	 */
	private AsahiDirectDebitData createDirectDebitData(AsahiDirectDebitForm asahiDirectDebitForm, AsahiDirectDebitData asahiDirectDebitData){
		asahiDirectDebitData.setDate(asahiDirectDebitForm.getCurrentDate());
		asahiDirectDebitData.setPersonalName(asahiDirectDebitForm.getPersonalName());
		
		if(null!=asahiDirectDebitForm.getAsahiDirectDebitPaymentForm()){
			AsahiDirectDebitPaymentData directDebitPaymentData = new AsahiDirectDebitPaymentData();
			
			if("BANK_ACCOUNT".equalsIgnoreCase(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getTokenType())){
				directDebitPaymentData.setAccountName(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getAccountName());
				directDebitPaymentData.setAccountNum(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getAccountNum());
				directDebitPaymentData.setBsb(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getBsb());
				directDebitPaymentData.setRegion(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getRegion());
				directDebitPaymentData.setSuburb(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getSuburb());
				directDebitPaymentData.setTokenType(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getTokenType());
			}else{
				directDebitPaymentData.setTokenType(asahiDirectDebitForm.getAsahiDirectDebitPaymentForm().getTokenType());
				directDebitPaymentData.setToken(asahiDirectDebitForm.getAsahiPaymentDetailsForm().getCardToken());
				directDebitPaymentData.setCardNumber(asahiDirectDebitForm.getAsahiPaymentDetailsForm().getCardNumber());
				directDebitPaymentData.setCardExpiry(asahiDirectDebitForm.getAsahiPaymentDetailsForm().getCardExpiry());
				directDebitPaymentData.setCardType(asahiDirectDebitForm.getAsahiPaymentDetailsForm().getCardTypeInfo());
				directDebitPaymentData.setNameOnCard(asahiDirectDebitForm.getAsahiPaymentDetailsForm().getCardHolderName());
			}
			
			asahiDirectDebitData.setDirectDebitPaymentData(directDebitPaymentData);
		}
		
		return asahiDirectDebitData;
	}
	
	/**
	 * Download direct debit form.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the response entity
	 */
	@GetMapping(value = "/download", produces = INVOICE_PDF_APPLICATION_TYPE)
	public ResponseEntity<byte[]> downloadDebitForm(HttpServletRequest request, HttpServletResponse response)
	{
		final AbstractCMSComponentModel component = cmsPageSlotComponentService.getComponentForId(asahiConfigurationService.getString(ApbCoreConstants.DIRECT_DEBIT_FORM_COMPONENT + cmsSiteService.getCurrentSite().getUid(), "directdebitformcomponent"));

		if(null!=component && component instanceof CMSImageComponentModel){
			MediaModel media = ((CMSImageComponentModel)component).getMedia();
				if(null!=media){
					try{
						
						InputStream inputStream = mediaService.getStreamFromMedia(media);
						byte[] asBytes = IOUtils.toByteArray(inputStream);
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.parseMediaType(INVOICE_PDF_APPLICATION_TYPE));
						String filename = media.getRealFileName();
						headers.setContentDispositionFormData(filename + INVOICE_PDF_FILE_FORMAT, filename);
						headers.setCacheControl(INVOICE_PDF_CACHE_CONTROL);
						ResponseEntity<byte[]> response1 = new ResponseEntity<byte[]>(
								asBytes, headers, HttpStatus.OK);
						return response1;
					}catch(Exception ex){
						LOG.info("Error has occured while downloading the pdf");
					}
				}
		}
		return null;
	}
	
	@Override
	protected String getViewForPage(final Model model)
	{
		asahiSAMInvoiceFacade.setSAMHeaderSessionAttributes(model);
		
		if (model.containsAttribute(CMS_PAGE_MODEL))
		{
			final AbstractPageModel page = (AbstractPageModel) model.asMap().get(CMS_PAGE_MODEL);
			if (page != null)
			{
				return getViewForPage(page);
			}
		}
		return null;
	}
	
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}
}

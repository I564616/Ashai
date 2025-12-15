package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade;
import com.apb.facades.sam.payment.history.impl.AsahiSAMPaymentHistoryFacadeImpl;
import com.apb.storefront.forms.AsahiPaymentHistoryForm;


/**
 * Controller for SAM payment history
 */
@Controller
@RequestMapping(value = "/paymentHistory")
public class AsahiSAMPaymentHistoryPageController extends ApbAbstractPageController
{
	
	private static final Logger LOG = Logger.getLogger(AsahiSAMPaymentHistoryPageController.class);
	
	private static final String PAYMENT_HISTORY_PAGE_LABEL = "paymentHistory";

	private static final String SAM_PAGE_SIZE_KEY = "sam.payment.history.pagination.page.size";
	
	@Resource
	private AsahiSAMPaymentHistoryFacadeImpl asahiSAMPaymentHistoryFacade;
	
	@Resource
	private ResourceBreadcrumbBuilder invoiceBreadcrumbBuilder;
	
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private AsahiSAMInvoiceFacade asahiSAMInvoiceFacade;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	/**
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return string
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	@RequireHardLogIn
	public String getPayementRecords(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("pendingApproval",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("requestDenied",asahiCoreUtil.isSAMAccessDenied());
		model.addAttribute("accessType",accessType);
		
		LOG.info("Getting the payment records");
		int pageSize = asahiConfigurationService.getInt(SAM_PAGE_SIZE_KEY, 10);
		final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);
		int totalRecordCount = asahiSAMPaymentHistoryFacade.getPaymentRecordsCount(null,null,null);
		model.addAttribute("totalRecordCount",totalRecordCount);
		List<AsahiSAMPaymentData> paymentDetails = new ArrayList<>();
		int totalPages = 1;
		
		paymentDetails = asahiSAMPaymentHistoryFacade.getPaymentRecords(pageableData,null,null,null);
		totalPages = Double.valueOf(Math.ceil((double)totalRecordCount/pageSize)).intValue();
			
		model.addAttribute("paymentDetails",paymentDetails );
		model.addAttribute("totalPages",totalPages);
		model.addAttribute("currentPageRecordCount", paymentDetails != null ? paymentDetails.size() : 0);
		
		final ContentPageModel pageModel = getContentPageForLabelOrId(PAYMENT_HISTORY_PAGE_LABEL);
		if ((getContentPageForLabelOrId(PAYMENT_HISTORY_PAGE_LABEL)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(PAYMENT_HISTORY_PAGE_LABEL)).getBackgroundImage().getURL());
		}
		final AsahiPaymentHistoryForm paymentHistoryForm = new AsahiPaymentHistoryForm();
		paymentHistoryForm.setPageSize(pageSize);
		paymentHistoryForm.setTotalRecords(totalRecordCount);
		
		model.addAttribute("paymentHistoryForm",paymentHistoryForm );
		storeCmsPageInModel(model, pageModel);
		setUpMetaDataForContentPage(model, pageModel);
		updatePageTitle(model, pageModel);
		model.addAttribute("breadcrumbs", invoiceBreadcrumbBuilder.getBreadcrumbs(null));
		asahiSAMInvoiceFacade.setSAMHeaderSessionAttributes(model);
		
		return getViewForPage(model);
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}
	
	/**
	 * @param paymentHistoryForm
	 * @param model
	 * @return string
	 */
	@PostMapping
	@RequireHardLogIn
	public String searchPaymentRecords(final AsahiPaymentHistoryForm paymentHistoryForm, 
			final Model model) throws CMSItemNotFoundException{
		final PageableData pageableData = createPageableData(Integer.valueOf(paymentHistoryForm.getPageNo()),
				asahiConfigurationService.getInt(SAM_PAGE_SIZE_KEY, 10), paymentHistoryForm.getSortAttribute(), ShowMode.Page);
		int totalRecordCount = asahiSAMPaymentHistoryFacade.getPaymentRecordsCount(paymentHistoryForm.getFromDate(), 
				paymentHistoryForm.getToDate(), paymentHistoryForm.getKeyword());
		model.addAttribute("totalRecordCount",totalRecordCount);
		List<AsahiSAMPaymentData> paymentDetails = new ArrayList<>();
		
		if(totalRecordCount>0) {
			paymentDetails = asahiSAMPaymentHistoryFacade.getPaymentRecords(pageableData,paymentHistoryForm.getFromDate(), paymentHistoryForm.getToDate(), paymentHistoryForm.getKeyword());
		}
		model.addAttribute("paymentDetails",paymentDetails );
		
		final ContentPageModel pageModel = getContentPageForLabelOrId(PAYMENT_HISTORY_PAGE_LABEL);
		if ((getContentPageForLabelOrId(PAYMENT_HISTORY_PAGE_LABEL)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(PAYMENT_HISTORY_PAGE_LABEL)).
					getBackgroundImage().getURL());
		}
		
		storeCmsPageInModel(model, pageModel);
		setUpMetaDataForContentPage(model, pageModel);
		updatePageTitle(model, pageModel);
		model.addAttribute("breadcrumbs", invoiceBreadcrumbBuilder.getBreadcrumbs(null));
		return getViewForPage(model);
	}
	
	/**
	 * @param paymentHistoryForm
	 * @param model
	 * @return json
	 */
	@PostMapping(value = "/fetchPaymentRecords", produces = "application/json")
	@RequireHardLogIn
	public String fetchPaymentRecords(final AsahiPaymentHistoryForm paymentHistoryForm, final Model model){
		final PageableData pageableData = createPageableData(Integer.valueOf(paymentHistoryForm.getPageNo()),
				asahiConfigurationService.getInt(SAM_PAGE_SIZE_KEY, 10), paymentHistoryForm.getSortAttribute(), ShowMode.Page);
		
		int totalRecordsCount = asahiSAMPaymentHistoryFacade.getPaymentRecordsCount(paymentHistoryForm.getFromDate()
				, paymentHistoryForm.getToDate(), paymentHistoryForm.getKeyword());
		List<AsahiSAMPaymentData> paymentDetails = new ArrayList<>();
		
		if(totalRecordsCount>0) {
			paymentDetails = asahiSAMPaymentHistoryFacade.getPaymentRecords(pageableData,paymentHistoryForm.getFromDate()
					, paymentHistoryForm.getToDate(), paymentHistoryForm.getKeyword());
		}
		
		paymentHistoryForm.setTotalRecords(totalRecordsCount);		
		paymentHistoryForm.setFromCount(paymentHistoryForm.getPageNo() * paymentHistoryForm.getPageSize());
		
		int toCount = paymentDetails.size() + (paymentHistoryForm.getPageNo() * paymentHistoryForm.getPageSize());
		paymentHistoryForm.setToCount(toCount);
		
		model.addAttribute("paymentHistoryForm",paymentHistoryForm);
		model.addAttribute("paymentDetails",paymentDetails);
		return "fragments/account/paymentHistoryResponse";
	}
}

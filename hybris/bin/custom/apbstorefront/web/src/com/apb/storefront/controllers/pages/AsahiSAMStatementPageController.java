package com.apb.storefront.controllers.pages;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.sam.data.AsahiSAMStatementMonthData;
import com.apb.facades.sam.data.AsahiSAMStatementPageData;
import com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade;
import com.apb.facades.sam.statement.AsahiSAMStatementFacade;
import com.apb.integration.data.AsahiStatementDownloadResponse;
import de.hybris.platform.util.Base64;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Produces;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * Controller for SAM Statement page
 */
@Controller
@RequestMapping(value = "/statement")
public class AsahiSAMStatementPageController extends ApbAbstractPageController
{
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AsahiSAMStatementPageController.class);
	
	/** The Constant ASAHI_STATEMENTS_PAGE_ID. */
	private static final String ASAHI_STATEMENTS_PAGE_ID = "statement";
	
	/** The Constant BREADCRUMBS_ATTR. */
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	
	/** The Constant STATEMENT_DATA_CONSTANT. */
	private static final String STATEMENT_DATA_CONSTANT = "statements";
	
	/** The Constant ASAHI_INVOICE_DETAIL_PAGE_ID. */
	private static final String ASAHI_INVOICE_DETAIL_PAGE_ID = "invoicedetail";
	
	/** The Constant INVOICE_APPLICATION_TYPE. */
	private static final String INVOICE_PDF_APPLICATION_TYPE = "application/pdf";
	
	/** The Constant INVOICE_PDF_CACHE_CONTROL. */
	private static final String INVOICE_PDF_CACHE_CONTROL = "must-revalidate, post-check=0, pre-check=0";

	/** The Constant INVOICE_PDF_FILE_FORMAT. */
	private static final String INVOICE_PDF_FILE_FORMAT = ".pdf";

	/** The asahi SAM statement facade. */
	@Resource
	private AsahiSAMStatementFacade asahiSAMStatementFacade;
	
	/** The SAM breadcrumb builder. */
	//TODO:Make Generic breadcrum builder for SAM 
	@Resource(name = "invoiceBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder invoiceBreadcrumbBuilder;
	
	@Resource
	private AsahiSAMInvoiceFacade asahiSAMInvoiceFacade;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Autowired
    private SessionService sessionService;
	
	@GetMapping
	@RequireHardLogIn
	public String showStatements(final Model model,final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException
	{

		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("pendingApproval",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("requestDenied",asahiCoreUtil.isSAMAccessDenied());
		model.addAttribute("accessType",accessType);
		
		//get the statements...
        final AsahiSAMStatementPageData samStatementPageData = asahiSAMStatementFacade.getStatements();
		model.addAttribute(STATEMENT_DATA_CONSTANT,samStatementPageData);
		sessionService.setAttribute(STATEMENT_DATA_CONSTANT, samStatementPageData);

		if ((getContentPageForLabelOrId(ASAHI_STATEMENTS_PAGE_ID)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(ASAHI_STATEMENTS_PAGE_ID)).getBackgroundImage().getURL());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ASAHI_STATEMENTS_PAGE_ID));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ASAHI_STATEMENTS_PAGE_ID));
		updatePageTitle(model, getContentPageForLabelOrId(ASAHI_STATEMENTS_PAGE_ID));
		model.addAttribute(BREADCRUMBS_ATTR, invoiceBreadcrumbBuilder.getBreadcrumbs(null));
		asahiSAMInvoiceFacade.setSAMHeaderSessionAttributes(model);
		
		return getViewForPage(model);

	}
	/**
	 * Download document.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the response entity
	 */
		@Produces("application/pdf")
		@GetMapping(value = "/download", produces = INVOICE_PDF_APPLICATION_TYPE)
		public ResponseEntity<byte[]> downloadStatement(@RequestParam(value = "statementMonth") final String statementMonth,
				@RequestParam(value = "statementYear") final String statementYear, @RequestParam(value = "FY") final String fy,
				final HttpServletRequest request, final HttpServletResponse response)
		{
			AsahiStatementDownloadResponse pdfRes = this.asahiSAMStatementFacade.getStatementPdf(statementMonth,statementYear);
			
			if(null!=pdfRes && (null==pdfRes.getErrorMessage() || pdfRes.getErrorMessage().isEmpty())){
				try{
					byte[] asBytes = Base64.decode(pdfRes.getPdfResponse());
					
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.parseMediaType(INVOICE_PDF_APPLICATION_TYPE));
				    headers.add("Access-Control-Allow-Origin", "*");
				    headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
				    headers.add("Access-Control-Allow-Headers", "Content-Type");
				    headers.add("Content-Disposition", "filename=" + pdfRes.getFileName());
				    headers.add("Cache-Control", INVOICE_PDF_CACHE_CONTROL);
				    headers.add("Pragma", "no-cache");
				    headers.add("Expires", "0");
				    headers.setContentDispositionFormData(pdfRes.getFileName() + INVOICE_PDF_FILE_FORMAT, pdfRes.getFileName() +".pdf");
				    
				    ResponseEntity<byte[]> statementResponse = new ResponseEntity<byte[]>(
				    		asBytes, headers, HttpStatus.OK);
				    return statementResponse;
				}catch(Exception ex){
					LOG.info("Error has occured while downloading the pdf");
				}
			}else {
				try
				{
                    final AsahiSAMStatementPageData samStatementPageData = sessionService.getAttribute(STATEMENT_DATA_CONSTANT);
                    final Map<Integer, AsahiSAMStatementMonthData> map = samStatementPageData.getMonths().get(fy);

                    final AsahiSAMStatementMonthData statementMonthData = map.get(Integer.parseInt(statementMonth));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
                    int month = statementMonth.equalsIgnoreCase("1") ? 12 : ((Integer.parseInt(statementMonth)) - 1);
                    String dateInString = "10-" + month + "-" + statementMonthData.getDisplayYear();
                    Date date = sdf.parse(dateInString);

                    final SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM");
                    request.setAttribute("statementDownloadError", displayFormat.format(date) + " " + statementMonthData.getDisplayYear());
                    request.getRequestDispatcher("/statement").forward(request, response);
                }
				catch (Exception e)
				{
					LOG.info("Exception while downloading statement");
				}
			}
			return new ResponseEntity("Statement Not Found.", HttpStatus.OK);
		}
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}
}

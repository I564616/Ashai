/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;

import jakarta.annotation.Resource;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;

import com.sabmiller.core.enums.SABMEnquiryType;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.b2bunit.DefaultSabmB2BUnitFacade;
import com.sabmiller.facades.businessenquiry.SabmBusinessEnquiryFacade;
import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmResponseEntity;

import com.sabmiller.facades.product.SabmProductFacade;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.validation.SabmAbstractBusinessEnquiryDataValidator;
import com.sabmiller.storefront.form.validation.SabmKegIssueValidator;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Controller
@RequestMapping(value = "/businessEnquiry")
public class BusinessEnquiryPageController extends SabmAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(BusinessEnquiryPageController.class.getName());

	@Resource
	private SabmBusinessEnquiryFacade sabmBusinessEnquiryFacade;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private SabmAbstractBusinessEnquiryDataValidator sabmAbstractBusinessEnquiryDataValidator;

	@Resource
	private SabmKegIssueValidator sabmKegIssueValidator;

	@Resource(name = "b2bCommerceUnitFacade")
	private DefaultSabmB2BUnitFacade sabmB2BUnitFacade;

	@Resource(name = "productFacade")
	private SabmProductFacade productFacade;

    // === Added for Mouldy Keg PDF ===
    @Resource
    private MediaService mediaService;

    @Resource
    private CatalogVersionService catalogVersionService;


	private static final String DELIVERY_TITLE = "text.support.request.type.delivery.enquiry.title";

	private static final String BUSINESS_ENQUIRY_PAGE = "businessEnquiry";

	private static final String BUSINESS_ENQUIRY_SENT_PAGE = "businessEnquirySent";

	private static final String CONTACT_US_PAGE_REDIRECT = "redirect:/serviceRequest";
	private static final String PRODUCT_TITLE = "text.support.request.type.product.enquiry.title";
	private static final String KEG_TITLE = "text.support.request.type.keg.enquiry.title";
	private static final String GENERAL_TITLE = "text.support.request.type.general.enquiry.title";
	private static final String DELIVERY_BREADCRUMB = "text.support.request.type.delivery.enquiry.breadcrumbs";
	private static final String ENQUIRY_SENT_BREADCRUMB = "text.support.request.type.business.enquiry.sent.breadcrumbs";
	private static final String KEG_BREADCRUMB = "text.support.request.type.keg.enquiry.breadcrumbs";
	private static final String PRODUCT_BREADCRUMB = "text.support.request.type.product.enquiry.breadcrumbs";
	private static final String GENERAL_BREADCRUMB = "text.support.request.type.general.enquiry.breadcrumbs";
	private static final String SUCCESS_STATUS = "success";
	private static final String FAILED_STATUS = "failed";

	@Resource(name = "supportBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder supportBreadcrumbBuilder;

	/**
	 *
	 * Contoller handler to set up the Contact Us page before it is rendered.
	 *
	 * @param model
	 * @return
	 */
	@RequireHardLogIn
	@GetMapping
    public String businessEnquiryFormPage(final Model model,
            @RequestParam(value = "enquiryType", required = false) final String enquiryType) throws CMSItemNotFoundException
	{
		if (StringUtils.isEmpty(enquiryType)) {
			return CONTACT_US_PAGE_REDIRECT;
		}
		if(enquiryType.equalsIgnoreCase("keg")) {
			List<SABMAlcoholVariantProductMaterialModel> kegMaterials = productFacade.getKegMaterials();
			List<SABMAlcoholVariantProductMaterialModel> filterKegMaterials = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
			for(SABMAlcoholVariantProductMaterialModel material : kegMaterials) {
				final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) material)
						.getBaseProduct();
				if (eanProduct.getPurchasable())
				{


					if (StringUtils.isNotEmpty(eanProduct.getSellingName()) && StringUtils.isNotEmpty(eanProduct.getPackConfiguration()))
					{
						material.setSellingName(eanProduct.getSellingName()+" "+eanProduct.getPackConfiguration());
						LOG.info("pack+selling by getname =" +material.getName());
					}
					else
					{
						material.setSellingName(eanProduct.getName());
						LOG.info("Common by getname =" +material.getName());
					}
					filterKegMaterials.add(material);

					Collator collator = Collator.getInstance(Locale.US);
	 				collator.setStrength(Collator.PRIMARY);
	 				filterKegMaterials.sort(Comparator.comparing(SABMAlcoholVariantProductMaterialModel::getSellingName, collator.reversed()));
					Collections.reverse(filterKegMaterials);


				}
			}
			LOG.info("TotalKegMaterials =" +filterKegMaterials.size());
			model.addAttribute("filterKegMaterials", filterKegMaterials);
			model.addAttribute("TotalKegMaterials", filterKegMaterials.size());
            try {
                CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("sabmContentCatalog", "Online");
                MediaModel media = mediaService.getMedia(catalogVersion, "how-to-fix-a-mouldy-keg");

                if (media != null) {
                    String pdfUrl = mediaService.getUrlForMedia(media);
                    model.addAttribute("mouldyKegPdfUrl", pdfUrl);
                    LOG.info("Mouldy Keg PDF found: {}", pdfUrl);
                } else {
                    LOG.warn("Mouldy Keg PDF media not found in sabmContentCatalog:Online");
                }
            } catch (Exception e) {
                LOG.error("Error fetching Mouldy Keg PDF media", e);
            }

		}

		final SABMEnquiryType enquiry = SABMEnquiryType.valueOf(enquiryType);
		//Check the facade layer for all b2bunits the current user belongs to.
		final String customerUid = getCustomerFacade().getCurrentCustomerUid();
		//SABMC- 1888
		final List<de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData> b2bUnits = sabmB2BUnitFacade.getBranchesForCustomer(customerUid);
		model.addAttribute("b2bUnits", b2bUnits);
        model.addAttribute("subTypeList", sabmBusinessEnquiryFacade.fetchEnquirySubType(enquiryType));
		//Add customer data to model
		final CustomerData customer = getCustomerFacade().getCurrentCustomer();
		model.addAttribute("customer", customer);
		model.addAttribute("enquiryType", enquiry);
		storeCmsPageInModel(model, getContentPageForLabelOrId(BUSINESS_ENQUIRY_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BUSINESS_ENQUIRY_PAGE));
		addBreadcrumbsToBusinessEnquiryPage(model, enquiry);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	/**
	 * Add dynamic breadcumbs to business enquiry page in respect to enquiry type
	 *
	 * @param model
	 * @param enquiry
	 */
	private void addBreadcrumbsToBusinessEnquiryPage(final Model model, final SABMEnquiryType enquiry) {
		if (enquiry.equals(SABMEnquiryType.DELIVERY)) {
			model.addAttribute("breadcrumbs", supportBreadcrumbBuilder.getBreadcrumbs(DELIVERY_BREADCRUMB));
		} else if (enquiry.equals(SABMEnquiryType.KEG)) {
			model.addAttribute("breadcrumbs", supportBreadcrumbBuilder.getBreadcrumbs(KEG_BREADCRUMB));
		} else if (enquiry.equals(SABMEnquiryType.PRODUCT)) {
			model.addAttribute("breadcrumbs", supportBreadcrumbBuilder.getBreadcrumbs(PRODUCT_BREADCRUMB));
		} else if (enquiry.equals(SABMEnquiryType.GENERAL)) {
			model.addAttribute("breadcrumbs", supportBreadcrumbBuilder.getBreadcrumbs(GENERAL_BREADCRUMB));
		}
	}

	/**
	 * Controller handlerto display business enquiry sent page
	 *
	 * @param model
	 * @return
	 */
	@RequireHardLogIn
	@GetMapping("/enquirySent")
	public String businessEnquirySentPage(final Model model,
			@RequestParam(value = "enquiryType", required = false) final String enquiryType) throws CMSItemNotFoundException {
		storeCmsPageInModel(model, getContentPageForLabelOrId(BUSINESS_ENQUIRY_SENT_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BUSINESS_ENQUIRY_SENT_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("breadcrumbs", supportBreadcrumbBuilder.getBreadcrumbs(ENQUIRY_SENT_BREADCRUMB));
		return getViewForPage(model);

	}

	/**
	 * Controller method  to send busines enquiry emails.
	 * @param enquiry
	 * @return
	 */
	@PostMapping(value = "/send", consumes = "application/json")
	public @ResponseBody ResponseEntity<SabmResponseEntity> sendEnquiry(@RequestBody final AbstractBusinessEnquiryData enquiry) {
		LOG.info("Sending business enquiry...");
		final SabmResponseEntity response = new SabmResponseEntity();
		final List<String> errors = new ArrayList<String>();
		sabmAbstractBusinessEnquiryDataValidator.validate(enquiry, errors);
		if (!errors.isEmpty()) {
			response.setBean(enquiry);
			response.setErrors(errors);
			response.setStatus("NOK");
			response.setRedirectUrl("/businessEnquiry");

			return new ResponseEntity<SabmResponseEntity>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		sabmBusinessEnquiryFacade.sendBusinessEnquiryEmail(enquiry);

		response.setRedirectUrl("/businessEnquiry/enquirySent");
		response.setStatus("OK");
		response.setBean(enquiry);
		return new ResponseEntity<SabmResponseEntity>(response, HttpStatus.OK);
	}

	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.BUSINESS_ENQUIRY.name();
	}


	@PostMapping("/createKegIssue")
	@ResponseBody
	@RequireHardLogIn
	public ResponseEntity<SFCompositeResponse> createKegIssue(@RequestBody final AbstractBusinessEnquiryData businessEnquiryData) {
		List<String> errors = new ArrayList<>();
		sabmKegIssueValidator.validate(businessEnquiryData, errors);

		if (!errors.isEmpty()) {
			final String errorMsg = String.join(", ", errors);
			LOG.error("Validation failed for Keg Issue creation: {}", errorMsg);
			return ResponseEntity.badRequest()
					.body(sabmBusinessEnquiryFacade.buildResponse(FAILED_STATUS, "Validation failed: " + errorMsg));
		}

		try {
			final SFCompositeResponse response = sabmBusinessEnquiryFacade.createKegIssueWithSalesforce(businessEnquiryData);

			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			if (response != null && SUCCESS_STATUS.equalsIgnoreCase(response.getStatus())) {
				status = HttpStatus.CREATED;
			}
			return ResponseEntity.status(status).body(response);

		} catch (Exception e) {
			LOG.error("Exception occured while creating Keg issue: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(sabmBusinessEnquiryFacade.buildResponse(FAILED_STATUS, "Unexpected error: " + e.getMessage()));
		}
	}
}

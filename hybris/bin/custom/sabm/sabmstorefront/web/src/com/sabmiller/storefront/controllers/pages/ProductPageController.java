/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.controllers.pages;

import com.google.common.collect.Maps;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.data.DealData;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.product.SabmProductFacade;
import com.sabmiller.storefront.controllers.ControllerConstants;
import de.hybris.platform.commercefacades.futurestock.FutureStockFacade;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ProductBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.FutureStockForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ReviewForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ReviewValidator;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.acceleratorstorefrontcommons.variants.VariantSortStrategy;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.*;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.Config;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.apb.core.util.AsahiCoreUtil;

import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


/**
 * Controller for product details page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/{path:.*}/p")
public class ProductPageController extends SabmAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ProductPageController.class);

	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String PRODUCT_CODE_PATH_VARIABLE_PATTERN = "/{productCode:.*}";
	private static final String REVIEWS_PATH_VARIABLE_PATTERN = "{numberOfReviews:.*}";

	private static final String FUTURE_STOCK_ENABLED = "storefront.products.futurestock.enabled";
	private static final String STOCK_SERVICE_UNAVAILABLE = "basket.page.viewFuture.unavailable";
	private static final String NOT_MULTISKU_ITEM_ERROR = "basket.page.viewFuture.not.multisku";
	private static final String ZOOM = "zoom";
	private static final String CATEGORY_IDENTIFIER = "c";
	private static final String HOME = "Home";
	private static final String SLASH = "/";
	private static final String UTF8_ENCODING = "UTF-8";
	private static final String REFERER_KEY = "referer";
	private static final String NOT_AVAILABLE = "NA";
	private static final String DEAL_SEPARATOR = " | ";

	@Resource(name = "productModelUrlResolver")
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource(name = "productFacade")
	private SabmProductFacade productFacade;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "productBreadcrumbBuilder")
	private ProductBreadcrumbBuilder productBreadcrumbBuilder;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "variantSortStrategy")
	private VariantSortStrategy variantSortStrategy;

	@Resource(name = "reviewValidator")
	private ReviewValidator reviewValidator;

	@Resource(name = "futureStockFacade")
	private FutureStockFacade futureStockFacade;

	@Resource(name = "b2bCommerceUnitFacade")
	SabmB2BCommerceUnitFacade b2bUnitFacade;
	
	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;
	
	@Resource
	private CommonI18NService commonI18NService;
	
	@Resource
	private ConfigurationService configurationService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN)
	public String productDetail(@PathVariable("productCode") final String productCode, final Model model,
			@RequestParam(value = "listName", required = false) final String listName, @RequestParam(value = "listOriginPos", required = false) final Integer listOriginPos, 
			final HttpServletRequest request, final HttpServletResponse response)
					throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		final String redirection = checkRequestUrl(request, response, productModelUrlResolver.resolve(productModel));
		if (StringUtils.isNotEmpty(redirection))
		{
			return redirection;
		}

		updatePageTitle(productModel, model);

		final List<ProductOption> extraOptions = Arrays.asList(ProductOption.VARIANT_MATRIX_BASE, ProductOption.VARIANT_MATRIX_URL,
				ProductOption.VARIANT_MATRIX_MEDIA);
		populateProductDetailForDisplay(productModel, model, request, extraOptions);

		model.addAttribute(new ReviewForm());
		final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(productCode, Arrays
				.asList(ProductReferenceTypeEnum.SIMILAR, ProductReferenceTypeEnum.ACCESSORIES, ProductReferenceTypeEnum.CROSSELLING),
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE), null);
		model.addAttribute("productReferences", productReferences);
		model.addAttribute("pageType", PageType.PRODUCT.name());
		model.addAttribute("futureStockEnabled", Boolean.valueOf(Config.getBoolean(FUTURE_STOCK_ENABLED, false)));
		model.addAttribute("requestOrigin", XSSFilterUtil.filter(listName));
		model.addAttribute("listOriginPos", listOriginPos);
		
		model.addAttribute("orderTemplates", b2bUnitFacade.getB2BUnitOrderTemplates());
		addDealsToModel((List<DealJson>)model.asMap().get("deals"), model);
		Collection<String> keywords=new ArrayList<String>();
		for(KeywordModel keywordModel:productModel.getKeywords()){
			keywords.add(keywordModel.getKeyword());
		}
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(keywords);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productModel.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUserForSite());
		return getViewForPage(model);
	}

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/orderForm")
	public String productOrderForm(@PathVariable("productCode") final String productCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		updatePageTitle(productModel, model);

		final List<ProductOption> extraOptions = Arrays.asList(ProductOption.VARIANT_MATRIX_BASE,
				ProductOption.VARIANT_MATRIX_PRICE, ProductOption.VARIANT_MATRIX_MEDIA, ProductOption.VARIANT_MATRIX_STOCK,
				ProductOption.URL);
		populateProductDetailForDisplay(productModel, model, request, extraOptions);

		if (!model.containsAttribute(WebConstants.MULTI_DIMENSIONAL_PRODUCT))
		{
			return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
		}

		return ControllerConstants.Views.Pages.Product.OrderForm;
	}

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/zoomImages")
	public String showZoomImages(@PathVariable("productCode") final String productCode,
			@RequestParam(value = "galleryPosition", required = false) final String galleryPosition, final Model model)
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		final ProductData productData = productFacade.getProductForOptions(productModel,
				Collections.singleton(ProductOption.GALLERY));
		final List<Map<String, ImageData>> images = getGalleryImages(productData);
		populateProductData(productData, model);
		if (galleryPosition != null)
		{
			try
			{
				model.addAttribute("zoomImageUrl", images.get(Integer.parseInt(galleryPosition)).get("zoom").getUrl());
			}
			catch (final IndexOutOfBoundsException | NumberFormatException ioebe)
			{
				model.addAttribute("zoomImageUrl", "");
				LOG.error("Failed get zoom  Image for product code [{}] ", productCode, ioebe);
			}
		}
		return ControllerConstants.Views.Fragments.Product.ZoomImagesPopup;
	}

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/quickView")
	public String showQuickView(@PathVariable("productCode") final String productCode, final Model model,
			final HttpServletRequest request)
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		final ProductData productData = productFacade.getProductForOptions(productModel,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.STOCK, ProductOption.REVIEW,
						ProductOption.VARIANT_FULL, ProductOption.DELIVERY_MODE_AVAILABILITY));

		sortVariantOptionData(productData);
		populateProductData(productData, model);
		getRequestContextData(request).setProduct(productModel);

		return ControllerConstants.Views.Fragments.Product.QuickViewPopup;
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/review", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String postReview(@PathVariable final String productCode, final ReviewForm form, final BindingResult result,
			final Model model, final HttpServletRequest request, final RedirectAttributes redirectAttrs)
					throws CMSItemNotFoundException
	{
		getReviewValidator().validate(form, result);

		final ProductModel productModel = productService.getProductForCode(productCode);

		if (result.hasErrors())
		{
			updatePageTitle(productModel, model);
			GlobalMessages.addErrorMessage(model, "review.general.error");
			model.addAttribute("showReviewForm", Boolean.TRUE);
			populateProductDetailForDisplay(productModel, model, request, Collections.<ProductOption> emptyList());
			storeCmsPageInModel(model, getPageForProduct(productModel));
			return getViewForPage(model);
		}

		final ReviewData review = new ReviewData();
		review.setHeadline(XSSFilterUtil.filter(form.getHeadline()));
		review.setComment(XSSFilterUtil.filter(form.getComment()));
		review.setRating(form.getRating());
		review.setAlias(XSSFilterUtil.filter(form.getAlias()));
		productFacade.postReview(productCode, review);
		GlobalMessages.addFlashMessage(redirectAttrs, GlobalMessages.CONF_MESSAGES_HOLDER, "review.confirmation.thank.you.title");

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
	}

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/reviewhtml/"
			+ REVIEWS_PATH_VARIABLE_PATTERN)
	public String reviewHtml(@PathVariable("productCode") final String productCode,
			@PathVariable("numberOfReviews") final String numberOfReviews, final Model model, final HttpServletRequest request)
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		final List<ReviewData> reviews;
		final ProductData productData = productFacade.getProductForOptions(productModel,
				Arrays.asList(ProductOption.BASIC, ProductOption.REVIEW));

		if ("all".equals(numberOfReviews))
		{
			reviews = productFacade.getReviews(productCode);
		}
		else
		{
			final int reviewCount = Math.min(Integer.parseInt(numberOfReviews),
					(productData.getNumberOfReviews() == null ? 0 : productData.getNumberOfReviews().intValue()));
			reviews = productFacade.getReviews(productCode, Integer.valueOf(reviewCount));
		}

		getRequestContextData(request).setProduct(productModel);
		model.addAttribute("reviews", reviews);
		model.addAttribute("reviewsTotal", productData.getNumberOfReviews());
		model.addAttribute(new ReviewForm());

		return ControllerConstants.Views.Fragments.Product.ReviewsTab;
	}

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/writeReview")
	public String writeReview(@PathVariable final String productCode, final Model model) throws CMSItemNotFoundException
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		model.addAttribute(new ReviewForm());
		setUpReviewPage(model, productModel);
		return ControllerConstants.Views.Pages.Product.WriteReview;
	}

	protected void setUpReviewPage(final Model model, final ProductModel productModel) throws CMSItemNotFoundException
	{
		Collection<String> keywords=new ArrayList<String>();
		for(KeywordModel keywordModel:productModel.getKeywords()){
			keywords.add(keywordModel.getKeyword());
		}
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(keywords);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productModel.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);
		storeCmsPageInModel(model, getPageForProduct(productModel));
		model.addAttribute("product", productFacade.getProductForOptions(productModel, Arrays.asList(ProductOption.BASIC)));
		updatePageTitle(productModel, model);
	}

	@PostMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/writeReview")
	public String writeReview(@PathVariable final String productCode, final ReviewForm form, final BindingResult result,
			final Model model, final HttpServletRequest request, final RedirectAttributes redirectAttrs)
					throws CMSItemNotFoundException
	{
		getReviewValidator().validate(form, result);

		final ProductModel productModel = productService.getProductForCode(productCode);

		if (result.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "review.general.error");
			populateProductDetailForDisplay(productModel, model, request, Collections.<ProductOption> emptyList());
			setUpReviewPage(model, productModel);
			return ControllerConstants.Views.Pages.Product.WriteReview;
		}

		final ReviewData review = new ReviewData();
		review.setHeadline(XSSFilterUtil.filter(form.getHeadline()));
		review.setComment(XSSFilterUtil.filter(form.getComment()));
		review.setRating(form.getRating());
		review.setAlias(XSSFilterUtil.filter(form.getAlias()));
		productFacade.postReview(productCode, review);
		GlobalMessages.addFlashMessage(redirectAttrs, GlobalMessages.CONF_MESSAGES_HOLDER, "review.confirmation.thank.you.title");

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
	}

	@GetMapping(PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/futureStock")
	public String productFutureStock(@PathVariable("productCode") final String productCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		final boolean futureStockEnabled = Config.getBoolean(FUTURE_STOCK_ENABLED, false);
		if (futureStockEnabled)
		{
			final List<FutureStockData> futureStockList = futureStockFacade.getFutureAvailability(productCode);
			if (futureStockList == null)
			{
				GlobalMessages.addErrorMessage(model, STOCK_SERVICE_UNAVAILABLE);
			}
			else if (futureStockList.isEmpty())
			{
				GlobalMessages.addInfoMessage(model, "product.product.details.future.nostock");
			}

			final ProductModel productModel = productService.getProductForCode(productCode);
			populateProductDetailForDisplay(productModel, model, request, Collections.<ProductOption> emptyList());
			model.addAttribute("futureStocks", futureStockList);

			return ControllerConstants.Views.Fragments.Product.FutureStockPopup;
		}
		else
		{
			return ControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}
	}

	@ResponseBody
	@PostMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/grid/skusFutureStock", produces = MediaType.APPLICATION_JSON_VALUE)
	public final Map<String, Object> productSkusFutureStock(final FutureStockForm form, final Model model)
	{
		final String productCode = form.getProductCode();
		final List<String> skus = form.getSkus();
		final boolean futureStockEnabled = Config.getBoolean(FUTURE_STOCK_ENABLED, false);

		Map<String, Object> result = new HashMap<>();
		if (futureStockEnabled && CollectionUtils.isNotEmpty(skus) && StringUtils.isNotBlank(productCode))
		{
			final Map<String, List<FutureStockData>> futureStockData = futureStockFacade
					.getFutureAvailabilityForSelectedVariants(productCode, skus);

			if (futureStockData == null)
			{
				// future availability service is down, we show this to the user
				result = Maps.newHashMap();
				final String errorMessage = getMessageSource().getMessage(NOT_MULTISKU_ITEM_ERROR, null,
						getI18nService().getCurrentLocale());
				result.put(NOT_MULTISKU_ITEM_ERROR, errorMessage);
			}
			else
			{
				for (final Map.Entry<String, List<FutureStockData>> entry : futureStockData.entrySet())
				{
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}

	@ExceptionHandler(UnknownIdentifierException.class)
	public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request)
	{
		request.setAttribute("message", exception.getMessage());
		return FORWARD_PREFIX + "/404";
	}

	/**
	 * This will return the request origin formatted for google tag manager.
	 *
	 * @param referer the request referer
	 * @return origin the request origin
	 * */
	private String getRequestOrigin(String referer) {
		String URL = null;
		try {
			URL = URLDecoder.decode( referer, UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			LOG.info("Failed to decode URL ");
		}

		String[] refs = URL.split(SLASH);
		StringBuffer origin = new StringBuffer();
		origin.append(HOME);

		for(int i =5; i < refs.length;i++){

			if(CATEGORY_IDENTIFIER.equals(refs[i])){
				break;
			}
			origin.append(SLASH);
			origin.append(refs[i].substring(0,1).toUpperCase() + refs[i].substring(1));
		}


		return origin.toString();
	}

	private void addDealsToModel(List<DealJson> deals, Model model){
		StringBuffer codeBuffer = new StringBuffer();
		StringBuffer fromBuffer = new StringBuffer();
		StringBuffer toBuffer = new StringBuffer();
		StringBuffer daysRemainingBuffer = new StringBuffer();
		int index = 1 ;
		if (CollectionUtils.isNotEmpty(deals)){
			for(DealJson deal : deals){
				codeBuffer.append(validateDealString(deal.getCode()));
				fromBuffer.append(validateDealDate(deal.getValidFrom()));
				toBuffer.append(validateDealDate(deal.getValidTo()));
				daysRemainingBuffer.append(validateDealString(getDaysRemaining(deal.getValidTo())));
				if (index < deals.size()){
					codeBuffer.append(DEAL_SEPARATOR);
					fromBuffer.append(DEAL_SEPARATOR);
					toBuffer.append(DEAL_SEPARATOR);
					daysRemainingBuffer.append(DEAL_SEPARATOR);
				}
			}
			model.addAttribute("deal", codeBuffer.toString());
			model.addAttribute("dealValidFrom", fromBuffer.toString());
			model.addAttribute("dealValidTo", toBuffer.toString());
			model.addAttribute("dealDaysRemaining", daysRemainingBuffer.toString());
		} else {
			model.addAttribute("deal", NOT_AVAILABLE);
			model.addAttribute("dealValidFrom", NOT_AVAILABLE);
			model.addAttribute("dealValidTo", NOT_AVAILABLE);
			model.addAttribute("dealDaysRemaining", NOT_AVAILABLE);
		}

	}

	private String validateDealDate(Long dateTime) {
		if(dateTime == null || dateTime == 0L){
			return NOT_AVAILABLE;
		}
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date(dateTime);
		return df.format(date);
	}

	private String validateDealString(String value){
		if(StringUtils.isEmpty(value)){
			return NOT_AVAILABLE;
		}
		return value;
	}

	private String getDaysRemaining(Long validToTime){
		if(validToTime == null || validToTime == 0L){
			return NOT_AVAILABLE;
		}
		Date today = new Date();
		LocalDate todayLocal = new LocalDate(today);
		LocalDate validToLocal = new LocalDate(new Date(validToTime));
		int daysRemaining = Days.daysBetween(todayLocal, validToLocal).getDays() + 1;
		if(daysRemaining < 0) {
			daysRemaining = 0;
		}
		return String.valueOf(daysRemaining);
	}

	protected void updatePageTitle(final ProductModel productModel, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveProductPageTitle(productModel));
	}

	protected void populateProductDetailForDisplay(final ProductModel productModel, final Model model,
			final HttpServletRequest request, final List<ProductOption> extraOptions) throws CMSItemNotFoundException
	{
		getRequestContextData(request).setProduct(productModel);

		final List<ProductOption> options = new ArrayList<>(Arrays.asList(ProductOption.VARIANT_FIRST_VARIANT, ProductOption.BASIC,
				ProductOption.URL, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY,
				ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
				ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE,
				ProductOption.DELIVERY_MODE_AVAILABILITY));

		options.addAll(extraOptions);

		final ProductData productData = productFacade.getProductForOptions(productModel, options);
		final boolean isNapUser=!asahiCoreUtil.isNAPUser();
		
		if(isNapUser)
		{
			List<DealJson> deals = productFacade.getDealsForProduct(productData.getCode());
			if(CollectionUtils.isNotEmpty(deals)) {
				SabmStringUtils.getSortedListDealJson(deals);
			}		
			model.addAttribute("deals", deals);
			productData.setDealsFlag(CollectionUtils.isNotEmpty(deals));
		}
		sortVariantOptionData(productData);
		storeCmsPageInModel(model, getPageForProduct(productModel));
		populateProductData(productData, model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, productBreadcrumbBuilder.getBreadcrumbs(productModel.getCode()));

		if (CollectionUtils.isNotEmpty(productData.getVariantMatrix()))
		{
			model.addAttribute(WebConstants.MULTI_DIMENSIONAL_PRODUCT,
					Boolean.valueOf(CollectionUtils.isNotEmpty(productData.getVariantMatrix())));
		}
	}

	protected void populateProductData(final ProductData productData, final Model model)
	{
		List<Map<String, ImageData>> galleryImages = getGalleryImages(productData);
		model.addAttribute("galleryImages", galleryImages);
		model.addAttribute("product", productData);    
		Collection<ImageData> videos = productData.getVideos();
		model.addAttribute("sortedGallery", sortGalleryImagesAndVideos(galleryImages, videos));	

	}
	
	/*
	 * protected ProductData rePopulateWETPrice(ProductData productData) { if(productData != null &&
	 * productData.isWetEligible() && productData.getPrice() != null && productData.getPrice().getValue() != null) {
	 * BigDecimal currentNetPrice = productData.getPrice().getValue(); BigDecimal wetPercentage =
	 * this.configurationService.getConfiguration().getBigDecimal(SabmCoreConstants.CUB_WET_PRICE_PERCENTAGE); BigDecimal
	 * netWithWETPrice = null != wetPercentage? currentNetPrice.multiply(wetPercentage):currentNetPrice; final
	 * CurrencyModel currency = commonI18NService.getCurrency(productData.getPrice().getCurrencyIso()); PriceData
	 * priceData = priceDataFactory.create(productData.getPrice().getPriceType(), netWithWETPrice, currency);
	 * productData.setPrice(priceData); } return productData; }
	 */

	protected void sortVariantOptionData(final ProductData productData)
	{
		if (CollectionUtils.isNotEmpty(productData.getBaseOptions()))
		{
			for (final BaseOptionData baseOptionData : productData.getBaseOptions())
			{
				if (CollectionUtils.isNotEmpty(baseOptionData.getOptions()))
				{
					Collections.sort(baseOptionData.getOptions(), variantSortStrategy);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(productData.getVariantOptions()))
		{
			Collections.sort(productData.getVariantOptions(), variantSortStrategy);
		}
	}

	
	protected List<Map<String, ImageData>> sortGalleryImagesAndVideos(List<Map<String, ImageData>> galleryData, Collection<ImageData> videos)
	{
		List<Map<String, ImageData>> sortedGallery = new ArrayList<>();
		List<Map<String, ImageData>> add2LastGallery = new ArrayList<>();
		sortedGallery.addAll(galleryData);

		//No videos to sort, so stop and return.
		if (videos == null) return sortedGallery;

		for (ImageData video:videos){
			if(null != video.getGalleryIndex()){
				Map<String, ImageData> video1 = new HashMap<>();
				video1.put(ZOOM, video);
				sortedGallery.add(video1);
			}
			else{
				Map<String, ImageData> video2 = new HashMap<>();
				video2.put(ZOOM, video);
				add2LastGallery.add(video2);
			}
			
		}
		
		Collections.sort(sortedGallery, new Comparator<Map<String, ImageData>>()
			{
				@Override
				public int compare(Map<String, ImageData> o1, Map<String, ImageData> o2)
				{					
						return o1.get(ZOOM).getGalleryIndex().compareTo(o2.get(ZOOM).getGalleryIndex());
					
				}
			});
		
		sortedGallery.addAll(add2LastGallery);
		return sortedGallery ;
	}
	
	
	protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData)
	{
		final List<Map<String, ImageData>> galleryImages = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(productData.getImages()))
		{
			final List<ImageData> images = new ArrayList<>();
			for (final ImageData image : productData.getImages())
			{
				if (ImageDataType.GALLERY.equals(image.getImageType()))
				{
					images.add(image);
				}
			}
			Collections.sort(images, new Comparator<ImageData>()
			{
				@Override
				public int compare(final ImageData image1, final ImageData image2)
				{
					return image1.getGalleryIndex().compareTo(image2.getGalleryIndex());
				}
			});

			if (CollectionUtils.isNotEmpty(images))
			{
				int currentIndex = images.get(0).getGalleryIndex().intValue();
				Map<String, ImageData> formats = new HashMap<String, ImageData>();
				for (final ImageData image : images)
				{
					if (currentIndex != image.getGalleryIndex().intValue())
					{
						galleryImages.add(formats);
						formats = new HashMap<>();
						currentIndex = image.getGalleryIndex().intValue();
					}
					formats.put(image.getFormat(), image);
				}
				if (!formats.isEmpty())
				{
					galleryImages.add(formats);
				}
			}
		}
		return galleryImages;
	}

	protected ReviewValidator getReviewValidator()
	{
		return reviewValidator;
	}

	protected AbstractPageModel getPageForProduct(final ProductModel product) throws CMSItemNotFoundException
	{
		return cmsPageService.getPageForProduct(product);
	}

	@ModelAttribute("cupdealsRefreshInProgress")
	public boolean isCupDealRefreshInProgress()
	{
		final SABMCustomerFacade cf = ((SABMCustomerFacade) getCustomerFacade());
		return cf.isDealRefreshInProgress() || cf.isCupRefreshInProgress();
	}
}

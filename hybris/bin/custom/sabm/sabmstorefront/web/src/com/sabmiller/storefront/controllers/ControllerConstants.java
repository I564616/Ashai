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
package com.sabmiller.storefront.controllers;

import com.sabm.core.model.cms.components.*;
import com.sabmiller.core.model.LinkParagraphComponentModel;
import de.hybris.platform.acceleratorcms.model.components.CartSuggestionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.CategoryFeatureComponentModel;
import de.hybris.platform.acceleratorcms.model.components.DynamicBannerComponentModel;
import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarCollectionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import de.hybris.platform.acceleratorcms.model.components.ProductFeatureComponentModel;
import de.hybris.platform.acceleratorcms.model.components.ProductReferencesComponentModel;
import de.hybris.platform.acceleratorcms.model.components.PurchasedCategorySuggestionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SubCategoryListComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.cms2lib.model.components.RotatingImagesComponentModel;


/**
 */
public interface ControllerConstants
{
	/**
	 * Class with action name constants
	 */
	interface Actions
	{
		interface Cms
		{
			String _Prefix = "/view/";
			String _Suffix = "Controller";

			/**
			 * Default CMS component controller
			 */
			String DefaultCMSComponent = _Prefix + "DefaultCMSComponentController";

			/**
			 * CMS components that have specific handlers
			 */
			String PurchasedCategorySuggestionComponent = _Prefix + PurchasedCategorySuggestionComponentModel._TYPECODE + _Suffix;
			String CartSuggestionComponent = _Prefix + CartSuggestionComponentModel._TYPECODE + _Suffix;
			String ProductReferencesComponent = _Prefix + ProductReferencesComponentModel._TYPECODE + _Suffix;
			String ProductCarouselComponent = _Prefix + ProductCarouselComponentModel._TYPECODE + _Suffix;

			String CUBPicksComponentController = _Prefix + CUBPicksComponentModel._TYPECODE + _Suffix;
			String NavigationBarCollectionComponent = _Prefix + NavigationBarCollectionComponentModel._TYPECODE + _Suffix;
			String DeliveryDatepickerComponentController = _Prefix + DeliveryDatepickerComponentModel._TYPECODE + _Suffix;

			String BestsellerComponent = _Prefix + BestsellerComponentModel._TYPECODE + _Suffix;

			String MiniCartComponent = _Prefix + MiniCartComponentModel._TYPECODE + _Suffix;
			String ProductFeatureComponent = _Prefix + ProductFeatureComponentModel._TYPECODE + _Suffix;
			String CategoryFeatureComponent = _Prefix + CategoryFeatureComponentModel._TYPECODE + _Suffix;
			String NavigationBarComponent = _Prefix + NavigationBarComponentModel._TYPECODE + _Suffix;
			String NavigationBarWithImageComponent = _Prefix + NavigationBarWithImageComponentModel._TYPECODE + _Suffix;
			String CMSLinkComponent = _Prefix + CMSLinkComponentModel._TYPECODE + _Suffix;
			String DynamicBannerComponent = _Prefix + DynamicBannerComponentModel._TYPECODE + _Suffix;
			String SubCategoryListComponent = _Prefix + SubCategoryListComponentModel._TYPECODE + _Suffix;
			String SimpleResponsiveBannerComponent = _Prefix + SimpleResponsiveBannerComponentModel._TYPECODE + _Suffix;
			
			String RecommendationsComponent = _Prefix + RecommendationsComponentModel._TYPECODE + _Suffix;
			String RecommendationsHeaderComponent = _Prefix + RecommendationsHeaderComponentModel._TYPECODE + _Suffix;
			String LiveChatComponent = _Prefix + LiveChatComponentModel._TYPECODE + _Suffix;

			String CMSLinkEnhancedComponent = _Prefix + CMSLinkEnhancedComponentModel._TYPECODE + _Suffix;
			String SABMHeroBannerComponent = _Prefix + SABMHeroBannerComponentModel._TYPECODE + _Suffix;
			String SABMTextImageBannerComponent = _Prefix + SABMTextImageBannerComponentModel._TYPECODE + _Suffix;
			String SABMInformationBoxComponent = _Prefix + SABMInformationBoxComponentModel._TYPECODE + _Suffix;
			String HeroBannerRotatingImagesComponent = _Prefix + HeroBannerRotatingImagesComponentModel._TYPECODE + _Suffix;
			String FAQComponent = _Prefix + FAQComponentModel._TYPECODE + _Suffix;
			
			String RotatingImagesComponent = _Prefix + RotatingImagesComponentModel._TYPECODE + _Suffix;

			String LinkParagraphComponent = _Prefix + LinkParagraphComponentModel._TYPECODE + _Suffix;
			String SmartRecommendationsComponent = _Prefix + SmartRecommendationsComponentModel._TYPECODE + _Suffix;
			String BrandGridComponent = _Prefix + BrandGridComponentModel._TYPECODE + _Suffix;
			String DealHeaderComponent = _Prefix + DealHeaderComponentModel._TYPECODE + _Suffix;
			String SupportHeaderComponent = _Prefix + SupportHeaderComponentModel._TYPECODE + _Suffix;
	
		}
	}

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Cms
		{
			String ComponentPrefix = "cms/";
		}

		interface Pages
		{
			interface Account
			{
				String AccountLoginPage = "pages/account/accountLoginPage";
				String AccountHomePage = "pages/account/accountHomePage";
				String AccountOrderHistoryPage = "pages/account/accountOrderHistoryPage";
				String AccountOrderDetailsPage = "pages/account/accountOrderDetailsPage";
				String AccountOrderTemplatePage = "pages/account/accountOrderTemplates";
				String AccountOrderTemplateDetailPage = "pages/account/accountOrderTemplateDetail";
				String AccountOrderTemplateDetailAjax = "pages/account/accountOrderTemplateDetailAjax";
				String AccountOrderTemplateList = "pages/account/accountOrderTemplateList";
				String AccountOrderPage = "pages/account/accountOrderPage";
				String AccountProfilePage = "pages/account/accountProfilePage";
				String AccountProfileEditPage = "pages/account/accountProfileEditPage";
				String AccountProfileEmailEditPage = "pages/account/accountProfileEmailEditPage";
				String AccountChangePasswordPage = "pages/account/accountChangePasswordPage";
				String AccountAddressBookPage = "pages/account/accountAddressBookPage";
				String AccountEditAddressPage = "pages/account/accountEditAddressPage";
				String AccountPaymentInfoPage = "pages/account/accountPaymentInfoPage";
				String AccountRegisterPage = "pages/account/accountRegisterPage";
				String AccountBillingPage = "pages/account/accountBillingPage";
				String AccountBillingConfirmationPage = "pages/services/paymentConfirmation";
				String AccountNewCustomerLogin = "pages/account/accountNewCustomerLogin";
				String AccountEditUserPage = "pages/account/accountEditUserPage";
				String AccountForgottenPasswordEmailSent = "pages/account/accountForgottenPasswordEmailSent";
				String LiveChatFeedBackPage = "pages/account/liveChatFeedback";

			}

			interface Checkout
			{
				String CheckoutRegisterPage = "pages/checkout/checkoutRegisterPage";
				String CheckoutConfirmationPage = "pages/checkout/checkoutConfirmationPage";
				String CheckoutLoginPage = "pages/checkout/checkoutLoginPage";
				String CheckoutPage = "pages/checkout/checkoutPage";
				String CheckoutPaymentWaitPage = "pages/checkout/checkoutPaymentWaitPage";
			}

			interface MultiStepCheckout
			{
				String AddEditDeliveryAddressPage = "pages/checkout/multi/addEditDeliveryAddressPage";
				String ChooseDeliveryMethodPage = "pages/checkout/multi/chooseDeliveryMethodPage";
				String ChoosePickupLocationPage = "pages/checkout/multi/choosePickupLocationPage";
				String AddPaymentMethodPage = "pages/checkout/multi/addPaymentMethodPage";
				String CheckoutSummaryPage = "pages/checkout/multi/checkoutSummaryPage";
				String HostedOrderPageErrorPage = "pages/checkout/multi/hostedOrderPageErrorPage";
				String HostedOrderPostPage = "pages/checkout/multi/hostedOrderPostPage";
				String SilentOrderPostPage = "pages/checkout/multi/silentOrderPostPage";
				String GiftWrapPage = "pages/checkout/multi/giftWrapPage";
			}

			interface Password
			{
				String PasswordResetChangePage = "pages/password/passwordResetChangePage";
				String PasswordResetRequest = "pages/password/passwordResetRequestPage";
				String PasswordResetRequestConfirmation = "pages/password/passwordResetRequestConfirmationPage";
				String ForgotPasswordRequestPage = "pages/password/forgotPasswordRequestPage";
			}

			interface Error
			{
				String ErrorNotFoundPage = "pages/error/errorNotFoundPage";
			}

			interface Cart
			{
				String CartPage = "pages/cart/cartPage";
				String CartTopOptionAjax = "pages/cart/cartTopOptionDeliveryAjax";
				String EmptyCartPage = "pages/cart/emptyCartPage";
			}

			interface StoreFinder
			{
				String StoreFinderSearchPage = "pages/storeFinder/storeFinderSearchPage";
				String StoreFinderDetailsPage = "pages/storeFinder/storeFinderDetailsPage";
				String StoreFinderViewMapPage = "pages/storeFinder/storeFinderViewMapPage";
			}

			interface Misc
			{
				String MiscRobotsPage = "pages/misc/miscRobotsPage";
				String MiscSiteMapPage = "pages/misc/miscSiteMapPage";
			}

			interface Guest
			{
				String GuestOrderPage = "pages/guest/guestOrderPage";
				String GuestOrderErrorPage = "pages/guest/guestOrderErrorPage";
			}

			interface Product
			{
				String WriteReview = "pages/product/writeReview";
				String OrderForm = "pages/product/productOrderFormPage";
			}

			interface Deals
			{
				String DealsPage = "pages/deal/dealPage";
			}

			interface Business
			{
				String BusinessUnitsPage = "pages/business/businessUnitsPage";
				String BusinessUnitDetailPage = "pages/business/businessUnitDetailPage";
			}

			interface User
			{
				String ShowTopUsersAjax = "pages/user/showTopUsersAjax";
			}

			interface GenericComponents {
				String THREE_COLUMNS_SABMTextImageBannerComponent = "cms/three_columns_sabmtextimagebannercomponent";
				String FOUR_COLUMNS_SABMTextImageBannerComponent = "cms/four_columns_sabmtextimagebannercomponent";
				String FIVE_COLUMNS_SABMTextImageBannerComponent = "cms/five_columns_sabmtextimagebannercomponent";
				String SABMHeroBannerComponent = "cms/sabmherobannercomponent";
				String SABMHeroBannerComponent_BACKGROUNDIMAGE = "cms/sabmherobannercomponent_backgroundimage";
				String SABMInformationBoxComponent = "cms/sabminformationboxcomponent";
				String SABMInformationBoxComponent_TAB = "cms/sabminformationboxcomponent_tab";
				String SABMInformationBoxComponent_SMALLBOX = "cms/sabminformationboxcomponent_smallbox";
			}

		}

		interface Fragments
		{
			interface Cart
			{
				String AddToCartPopup = "fragments/cart/addToCartPopup";
				String MiniCartPanel = "fragments/cart/miniCartPanel";
				String MiniCartErrorPanel = "fragments/cart/miniCartErrorPanel";
				String CartPopup = "fragments/cart/cartPopup";
				String ExpandGridInCart = "fragments/cart/expandGridInCart";
				String ViewCartPopup = "fragments/cart/viewCartPopup";
			}

			interface Account
			{
				String CountryAddressForm = "fragments/address/countryAddressForm";
			}

			interface Checkout
			{
				String TermsAndConditionsPopup = "fragments/checkout/termsAndConditionsPopup";
				String BillingAddressForm = "fragments/checkout/billingAddressForm";
				String ReadOnlyExpandedOrderForm = "fragments/checkout/readOnlyExpandedOrderForm";
				String AjaxCartItemsBody = "fragments/checkout/ajaxCartItemsBody";
			}

			interface Password
			{
				String PasswordResetRequestPopup = "fragments/password/passwordResetRequestPopup";
				String ForgotPasswordValidationMessage = "fragments/password/forgotPasswordValidationMessage";
			}

			interface Product
			{
				String FutureStockPopup = "fragments/product/futureStockPopup";
				String QuickViewPopup = "fragments/product/quickViewPopup";
				String ZoomImagesPopup = "fragments/product/zoomImagesPopup";
				String ReviewsTab = "fragments/product/reviewsTab";
				String StorePickupSearchResults = "fragments/product/storePickupSearchResults";
			}
			
			interface Recommendation
			{
				String AddToRecommendationPopup = "fragments/recommendation/addToRecommendationPopup";
			}
		}
	}
}


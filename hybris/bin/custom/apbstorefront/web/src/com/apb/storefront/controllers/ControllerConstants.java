/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.storefront.controllers;

import com.sabm.core.model.cms.components.AsahiRecommendationsComponentModel;
import com.sabm.core.model.cms.components.LiveChatComponentModel;

import de.hybris.platform.acceleratorcms.model.components.CMSTabParagraphContainerModel;
import de.hybris.platform.acceleratorcms.model.components.CartSuggestionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.CategoryFeatureComponentModel;
import de.hybris.platform.acceleratorcms.model.components.CategoryNavigationComponentModel;
import de.hybris.platform.acceleratorcms.model.components.DynamicBannerComponentModel;
import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import de.hybris.platform.acceleratorcms.model.components.ProductFeatureComponentModel;
import de.hybris.platform.acceleratorcms.model.components.ProductReferencesComponentModel;
import de.hybris.platform.acceleratorcms.model.components.PurchasedCategorySuggestionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SubCategoryListComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;


/**
 */
public interface ControllerConstants
{
	// Constant names cannot be changed due to their usage in dependant extensions, thus nosonar

	/**
	 * Class with action name constants
	 */
	interface Utils // NOSONAR
	{
		interface Payment // NOSONAR
		{
			String DefaultCustomerIP = "127.0.0.1"; // NOSONAR
		}
	}
	
	interface Actions
	{
		interface Cms // NOSONAR
		{
			String _Prefix = "/view/"; // NOSONAR
			String _Suffix = "Controller"; // NOSONAR

			/**
			 * Default CMS component controller
			 */
			String DefaultCMSComponent = _Prefix + "DefaultCMSComponentController"; // NOSONAR

			/**
			 * CMS components that have specific handlers
			 */
			String PurchasedCategorySuggestionComponent = _Prefix + PurchasedCategorySuggestionComponentModel._TYPECODE + _Suffix; // NOSONAR
			String CartSuggestionComponent = _Prefix + CartSuggestionComponentModel._TYPECODE + _Suffix; // NOSONAR
			String ProductReferencesComponent = _Prefix + ProductReferencesComponentModel._TYPECODE + _Suffix; // NOSONAR
			String ProductCarouselComponent = _Prefix + ProductCarouselComponentModel._TYPECODE + _Suffix; // NOSONAR
			String MiniCartComponent = _Prefix + MiniCartComponentModel._TYPECODE + _Suffix; // NOSONAR
			String ProductFeatureComponent = _Prefix + ProductFeatureComponentModel._TYPECODE + _Suffix; // NOSONAR
			String CategoryFeatureComponent = _Prefix + CategoryFeatureComponentModel._TYPECODE + _Suffix; // NOSONAR
			String NavigationBarComponent = _Prefix + NavigationBarComponentModel._TYPECODE + _Suffix; // NOSONAR
			String CMSLinkComponent = _Prefix + CMSLinkComponentModel._TYPECODE + _Suffix; // NOSONAR
			String DynamicBannerComponent = _Prefix + DynamicBannerComponentModel._TYPECODE + _Suffix; // NOSONAR
			String SubCategoryListComponent = _Prefix + SubCategoryListComponentModel._TYPECODE + _Suffix; // NOSONAR
			String SimpleResponsiveBannerComponent = _Prefix + SimpleResponsiveBannerComponentModel._TYPECODE + _Suffix; // NOSONAR
			String CMSTabParagraphContainer = _Prefix + CMSTabParagraphContainerModel._TYPECODE + _Suffix; // NOSONAR
			String CategoryNavigationComponent = _Prefix + CategoryNavigationComponentModel._TYPECODE + _Suffix; // NOSONAR
			String AsahiRecommendationsComponent = _Prefix + AsahiRecommendationsComponentModel._TYPECODE + _Suffix;
			String LiveChatComponent = _Prefix + LiveChatComponentModel._TYPECODE + _Suffix;

		}
	}

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Cms // NOSONAR
		{
			String ComponentPrefix = "cms/"; // NOSONAR
		}

		interface Pages
		{
			interface Account // NOSONAR
			{
				String AccountLoginPage = "pages/account/accountLoginPage"; // NOSONAR
				String AccountHomePage = "pages/account/accountHomePage"; // NOSONAR
				String AccountOrderHistoryPage = "pages/account/accountOrderHistoryPage"; // NOSONAR
				String AccountOrderPage = "pages/account/accountOrderPage"; // NOSONAR
				String AccountProfilePage = "pages/account/accountProfilePage"; // NOSONAR
				String AccountProfileEditPage = "pages/account/accountProfileEditPage"; // NOSONAR
				String AccountProfileEmailEditPage = "pages/account/accountProfileEmailEditPage"; // NOSONAR
				String AccountChangePasswordPage = "pages/account/accountChangePasswordPage"; // NOSONAR
				String AccountAddressBookPage = "pages/account/accountAddressBookPage"; // NOSONAR
				String AccountEditAddressPage = "pages/account/accountEditAddressPage"; // NOSONAR
				String AccountPaymentInfoPage = "pages/account/accountPaymentInfoPage"; // NOSONAR
				String AccountRegisterPage = "pages/account/accountRegisterPage"; // NOSONAR
				String AccountCompanyDetailsPage = "pages/account/accountCompanyDetailsPage"; // NOSONAR
				String ContactUsPage = "pages/account/contactUsPage"; // NOSONAR
				String ContactUsConfirmationPage = "pages/account/contactUsConfirmationPage"; //NOSONAR
				String KegReturnPage = "pages/account/kegReturnPage"; // NOSONAR
				String SavedCardsPage = "pages/account/savedCardsPage"; // NOSONAR
				String TermsAndLegalPage = "pages/account/termsAndLegalPage"; // NOSONAR
				String ConfirmationPage = "pages/account/confirmationPage";
				String AboutUsPage = "pages/account/aboutUsPage";
				String promotionWinnersPage = "pages/account/promotionWinnersPage";
				String DirectDebitConfirmationPage = "pages/account/directDebitConfirmationPage";
				String LiveChatFeedBackPage = "pages/account/liveChatFeedback";
			}

			interface Checkout // NOSONAR
			{
				String CheckoutRegisterPage = "pages/checkout/checkoutRegisterPage"; // NOSONAR
				String CheckoutConfirmationPage = "pages/checkout/checkoutConfirmationPage"; // NOSONAR
				String CheckoutLoginPage = "pages/checkout/checkoutLoginPage"; // NOSONAR
			}

			interface MultiStepCheckout // NOSONAR
			{
				String AddEditDeliveryAddressPage = "pages/checkout/multi/addEditDeliveryAddressPage"; // NOSONAR
				String ChooseDeliveryMethodPage = "pages/checkout/multi/chooseDeliveryMethodPage"; // NOSONAR
				String ChoosePickupLocationPage = "pages/checkout/multi/choosePickupLocationPage"; // NOSONAR
				String AddPaymentMethodPage = "pages/checkout/multi/addPaymentMethodPage"; // NOSONAR
				String CheckoutSummaryPage = "pages/checkout/multi/checkoutSummaryPage"; // NOSONAR
				String HostedOrderPageErrorPage = "pages/checkout/multi/hostedOrderPageErrorPage"; // NOSONAR
				String HostedOrderPostPage = "pages/checkout/multi/hostedOrderPostPage"; // NOSONAR
				String SilentOrderPostPage = "pages/checkout/multi/silentOrderPostPage"; // NOSONAR
				String GiftWrapPage = "pages/checkout/multi/giftWrapPage"; // NOSONAR
			}

			interface Password // NOSONAR
			{
				String PasswordResetChangePage = "pages/password/passwordResetChangePage"; // NOSONAR
				String PasswordResetRequest = "pages/password/passwordResetRequestPage"; // NOSONAR
				String PasswordResetRequestConfirmation = "pages/password/passwordResetRequestConfirmationPage"; // NOSONAR
				String PasswordChangedConfirmation = "pages/password/passwordChangedConfirmationPage"; // NOSONAR
			}

			interface Error // NOSONAR
			{
				String ErrorNotFoundPage = "pages/error/errorNotFoundPage"; // NOSONAR
			}

			interface Cart // NOSONAR
			{
				String CartPage = "pages/cart/cartPage"; // NOSONAR
				String EmptyCartPage = "pages/layout/contentLayout1Page"; // NOSONAR
			}

			interface StoreFinder // NOSONAR
			{
				String StoreFinderSearchPage = "pages/storeFinder/storeFinderSearchPage"; // NOSONAR
				String StoreFinderDetailsPage = "pages/storeFinder/storeFinderDetailsPage"; // NOSONAR
				String StoreFinderViewMapPage = "pages/storeFinder/storeFinderViewMapPage"; // NOSONAR
			}

			interface Misc // NOSONAR
			{
				String MiscRobotsPage = "pages/misc/miscRobotsPage"; // NOSONAR
				String MiscSiteMapPage = "pages/misc/miscSiteMapPage"; // NOSONAR
			}

			interface Guest // NOSONAR
			{ // NOSONAR
				String GuestOrderPage = "pages/guest/guestOrderPage"; // NOSONAR
				String GuestOrderErrorPage = "pages/guest/guestOrderErrorPage"; // NOSONAR
			}

			interface Product // NOSONAR
			{
				String WriteReview = "pages/product/writeReview"; // NOSONAR
				String OrderForm = "pages/product/productOrderFormPage"; // NOSONAR
			}

			interface QuickOrder // NOSONAR
			{
				String QuickOrderPage = "pages/quickOrder/quickOrderPage"; // NOSONAR
			}

			interface CSV // NOSONAR
			{
				String ImportCSVSavedCartPage = "pages/csv/importCSVSavedCartPage"; // NOSONAR
			}

			interface SingleStepCheckout // NOSONAR
			{
				String AddDeliveryDataPage = "pages/checkout/single/addDeliveryDataPage";
			}
			

			interface Recommendation //NOSONAR
			{
				String RepRecommendationPage = "pages/recommendation/repRecommendationPage"; // NOSONAR
			}
			
			interface Planogram //NOSONAR
			{
				String PlanogramPage = "pages/planogram/myPlanogram"; // NOSONAR
			}
			
		}

		interface Fragments
		{
			interface Cart // NOSONAR
			{
				String AddToCartPopup = "fragments/cart/addToCartPopup"; // NOSONAR
				String MiniCartPanel = "fragments/cart/miniCartPanel"; // NOSONAR
				String MiniCartErrorPanel = "fragments/cart/miniCartErrorPanel"; // NOSONAR
				String CartPopup = "fragments/cart/cartPopup"; // NOSONAR
				String ExpandGridInCart = "fragments/cart/expandGridInCart"; // NOSONAR
				boolean FETCH_DYNAMIC_PRICE_FOR_MINI_CART = false;
			}

			interface Account // NOSONAR
			{
				String CountryAddressForm = "fragments/address/countryAddressForm"; // NOSONAR
				String SavedCartRestorePopup = "fragments/account/savedCartRestorePopup"; // NOSONAR
			}

			interface Checkout // NOSONAR
			{
				String TermsAndConditionsPopup = "fragments/checkout/termsAndConditionsPopup"; // NOSONAR
				String BillingAddressForm = "fragments/checkout/billingAddressForm"; // NOSONAR
				String ReadOnlyExpandedOrderForm = "fragments/checkout/readOnlyExpandedOrderForm"; // NOSONAR
			}

			interface Password // NOSONAR
			{
				String PasswordResetRequestPopup = "fragments/password/passwordResetRequestPopup"; // NOSONAR
				String ForgotPasswordValidationMessage = "fragments/password/forgotPasswordValidationMessage"; // NOSONAR
			}

			interface Product // NOSONAR
			{
				String FutureStockPopup = "fragments/product/futureStockPopup"; // NOSONAR
				String QuickViewPopup = "fragments/product/quickViewPopup"; // NOSONAR
				String ZoomImagesPopup = "fragments/product/zoomImagesPopup"; // NOSONAR
				String ReviewsTab = "fragments/product/reviewsTab"; // NOSONAR
				String StorePickupSearchResults = "fragments/product/storePickupSearchResults"; // NOSONAR
			}

			interface Order //NOSONAR
			{
				String KeepProductsInCart = "fragments/history/keepProductInCartConfirmationPopup"; // NOSONAR
			}
			

			interface Recommendation //NOSONAR
			{
				String addToRecommendation = "fragments/recommendation/addToRecommendationPopup"; // NOSONAR
			}
			
		}

	}
}

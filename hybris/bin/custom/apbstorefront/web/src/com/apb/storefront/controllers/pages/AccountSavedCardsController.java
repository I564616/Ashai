package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.util.AsahiPaymentIframeUrlUtil;


@Controller
@RequestMapping("/my-account/saved-cards")
public class AccountSavedCardsController extends ApbAbstractPageController
{
	@Resource(name = "apbUserFacade")
	UserFacade userFacade;

	@Resource(name = "sabmCheckoutFacade")
	AcceleratorCheckoutFacade acceleratorCheckoutFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "apbPaymentUrlUtil")
	private AsahiPaymentIframeUrlUtil asahiPaymentIframeUrlUtil;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource
	UserService userService;

	@Resource(name = "addressConverter")
	Converter<AddressModel, AddressData> apbB2bAddressConverter;

	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	private static final String SAVED_CARDS_CMS_PAGE = "saved-cards";
	private static final String ADD_CARD_CMS_PAGE = "add-card";

	private static final String MAX_CREDIT_CARDS_ALLOWED = "max.saved.cards.allowed.";

	private static final String MY_ACCOUNT_SAVED_CARDS_URL = "/my-account/saved-cards";

	private static final String REDIRECT_SAVED_CARDS_URL = REDIRECT_PREFIX + MY_ACCOUNT_SAVED_CARDS_URL;

	private static final String REDIRECT_ADD_CARD_URL = REDIRECT_PREFIX + MY_ACCOUNT_SAVED_CARDS_URL + "/add";

	private static final String VISA_TYPE_LIST = "payment.integration.visa.card.list.";
	private static final String MASTER_TYPE_LIST = "payment.integration.master.card.list.";
	private static final String AMEX_TYPE_LIST = "payment.integration.amex.card.list.";

	private static final String SET_DEFAULT_CARD_MSG = "saved.card.set.as.default";
	private static final String REMOVE_CARD_MSG = "saved.card.remove.success";
	private static final String ADD_CARD_MSG = "saved.card.add.card.success";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String TEXT_SAVED_CARDS = "text.saved.cards";


	Logger LOG = LoggerFactory.getLogger(AccountSavedCardsController.class);

	@GetMapping
	@RequireHardLogIn
	public String getSavedCards(final RedirectAttributes redirectModel, final Model model) throws CMSItemNotFoundException
	{
		boolean allowAddCart = true;
		final List<CCPaymentInfoData> availableCards = userFacade.getCCPaymentInfos(true);
		if (CollectionUtils.isNotEmpty(availableCards))
		{
			Collections.sort(availableCards, Comparator.comparing((final CCPaymentInfoData cardEntry) -> cardEntry.getCardNumber()));
			final CCPaymentInfoDatas paymentInfoDataList = new CCPaymentInfoDatas();
			paymentInfoDataList.setPaymentInfos(availableCards);
			model.addAttribute("paymentInfo", paymentInfoDataList);
			allowAddCart = allowAddCart(availableCards.size());
		}
		if (availableCards.size() >= getMaxSavedCardsAllowed())
		{
			model.addAttribute("addCardWarning", true);
			model.addAttribute("maxAllowed", getMaxSavedCardsAllowed());
		}
		model.addAttribute("allowAddCart", allowAddCart);
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(TEXT_SAVED_CARDS));
		storeCmsPageInModel(model, getContentPageForLabelOrId(SAVED_CARDS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SAVED_CARDS_CMS_PAGE));
		return getViewForPage(model);
	}

	@PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireHardLogIn
	public String createCCItem(@RequestBody final CCPaymentInfoDatas cards, final HttpServletRequest request,
			final RedirectAttributes redirectModel, final Model model) throws CMSItemNotFoundException
	{
		final List<CCPaymentInfoData> availableCards = userFacade.getCCPaymentInfos(true);
		if (CollectionUtils.isNotEmpty(availableCards) && !allowAddCart(availableCards.size()))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "saved.card.max.limit.reached",
					new Object[]
					{ availableCards.size() });
			return REDIRECT_SAVED_CARDS_URL;
		}
		cards.getPaymentInfos().get(0).setBillingAddress(setBillingAddressForCard());
		cards.getPaymentInfos().get(0).setCardType(setAddressTypeForCard(cards.getPaymentInfos().get(0).getCardType()));
		final CCPaymentInfoData cardInfo = acceleratorCheckoutFacade.createPaymentSubscription(cards.getPaymentInfos().get(0));
		if (cards.getPaymentInfos().get(0).isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(cardInfo);
		}
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, ADD_CARD_MSG, new Object[]
		{ request.getContextPath() + MY_ACCOUNT_SAVED_CARDS_URL });
		return REDIRECT_ADD_CARD_URL;


	}

	@PostMapping("/default")
	@RequireHardLogIn
	public String setCardAsDefault(final CCPaymentInfoData cardInfo, final RedirectAttributes redirectModel, final Model model)
			throws CMSItemNotFoundException
	{
		userFacade.setDefaultPaymentInfo(cardInfo);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, SET_DEFAULT_CARD_MSG);
		return REDIRECT_SAVED_CARDS_URL;
	}

	@PostMapping("/remove")
	@RequireHardLogIn
	public String removePaymentInfo(@RequestParam(value = "id") final String id, final RedirectAttributes redirectModel,
			final Model model) throws CMSItemNotFoundException
	{
		userFacade.removeCCPaymentInfo(id);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, REMOVE_CARD_MSG);
		return REDIRECT_SAVED_CARDS_URL;
	}

	@GetMapping("/add")
	@RequireHardLogIn
	public String showAddCreditCardView(final RedirectAttributes redirectModel, final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("addCardUrl", asahiPaymentIframeUrlUtil.getIframeUrl());
		final List<CCPaymentInfoData> availableCards = userFacade.getCCPaymentInfos(true);
		if (CollectionUtils.isNotEmpty(availableCards))
		{
			model.addAttribute("allowAddCart", allowAddCart(availableCards.size()));
		}
		else
		{
			model.addAttribute("allowAddCart", true);
		}
		model.addAttribute("maxAllowed", getMaxSavedCardsAllowed());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_CARD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_CARD_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-account/saved-cards",
				getMessageSource().getMessage(TEXT_SAVED_CARDS, null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.add.cards", null, getI18nService().getCurrentLocale()), null));

		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
		return getViewForPage(model);
	}

	private boolean allowAddCart(final Integer cardCount)
	{
		return cardCount < getMaxSavedCardsAllowed() ? true : false;
	}

	private int getMaxSavedCardsAllowed()
	{
		return Integer.parseInt(
				asahiConfigurationService.getString(MAX_CREDIT_CARDS_ALLOWED + cmsSiteService.getCurrentSite().getUid(), "3"));
	}


	private String setAddressTypeForCard(final String cardType)
	{
		final String visaCardTypes = asahiConfigurationService.getString(VISA_TYPE_LIST + cmsSiteService.getCurrentSite().getUid(),
				"Visa");
		final List<String> visaTypes = new ArrayList<>(Arrays.asList(visaCardTypes.split(",")));
		if (visaTypes.contains(cardType))
		{
			return CreditCardType.VISA.getCode();
		}
		final String masterCardTypes = asahiConfigurationService
				.getString(MASTER_TYPE_LIST + cmsSiteService.getCurrentSite().getUid(), "MasterCard");
		final List<String> masterTypes = new ArrayList<>(Arrays.asList(masterCardTypes.split(",")));
		if (masterTypes.contains(cardType))
		{
			return CreditCardType.MASTER.getCode();
		}
		final String amexCardTypes = asahiConfigurationService.getString(AMEX_TYPE_LIST + cmsSiteService.getCurrentSite().getUid(),
				"AMEX");
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
				final List<AddressData> addresses = (List<AddressData>) acceleratorCheckoutFacade.getSupportedDeliveryAddresses(true);
				billingAddress = addresses.stream().filter(address -> address.isBillingAddress()).findFirst().orElse(null);
			}
		}
		if (null == billingAddress)
		{
			billingAddress = new AddressData();
			final CountryData country = new CountryData();
			country.setIsocode("US");
			billingAddress.setCountry(country);
		}

		return billingAddress;

	}

}

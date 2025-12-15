package com.apb.core.util;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.integration.constants.ApbintegrationConstants;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.product.SabmProductService;


/**
 * @author Pankaj.Gandhi
 *
 *         Class to provide utility functions throughout the site for inclusion/exclusion data
 */
public class AsahiCoreUtil
{
	private static final String CASES_OF = "case(s) of ";
	private static final String REGEX_SPACE = "\\s+";
	@Resource
	private SessionService sessionService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;

	@Autowired
    private ModelService modelService;
	 @Resource
	 private CMSSiteService cmsSiteService;

	private static final String B2B_NATIONAL_GROUP = "b2bNAPGroup";

	private static final String ASAHI_ACCOUNT = "@asahi.com.au";

	private static final Logger LOG = Logger.getLogger(AsahiCoreUtil.class);

	private static final String BUY = "BUY ";

	/**
	 * This method will be used to get the product from session inclusion list based on code
	 *
	 * @param productCode
	 *           - product identifier
	 * @return - AsahiProductInfo
	 */
	public AsahiProductInfo getProductFromSessionInclusionList(final String productCode)
	{

		AsahiProductInfo productData = null;
		final Map<String, AsahiProductInfo> inclusionList = getSessionInclusionMap();
		if (null != inclusionList && !inclusionList.isEmpty())
		{
			productData = inclusionList.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(productCode))
					.map(Map.Entry::getValue).findFirst().orElse(null);
		}

		return productData;
	}

	/**
	 * This method will return the price data of products ids passed in the parameter
	 *
	 * @param productIds
	 *           - list
	 * @return map of products
	 */
	public Map<String, AsahiProductInfo> getPriceMapFromSession(final Set<String> productIds)
	{
		final Map<String, AsahiProductInfo> inclusionList = getSessionInclusionMap();

		final Map<String, AsahiProductInfo> priceMap = new HashMap<>();

		if (null != inclusionList && !inclusionList.isEmpty())
		{
			productIds.stream().forEach(id -> {
				final AsahiProductInfo product = inclusionList.entrySet().stream()
						.filter(entry -> entry.getKey().equalsIgnoreCase(id)).map(Map.Entry::getValue).findFirst().orElse(null);
				if (null != product)
				{
					priceMap.put(id, product);
				}
				else
				{
					priceMap.put(id, null);
				}
			});
		}
		return priceMap;
	}

	/**
	 * This method will get map from session
	 *
	 * @return map of items
	 */
	public Map<String, AsahiProductInfo> getSessionInclusionMap()
	{
		return sessionService.getAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST);

	}

	/**
	 * This method will store map In session
	 *
	 * @param inclusionList
	 */
	public void setInclusionMapInSession(final Map<String, AsahiProductInfo> inclusionList)
	{
		sessionService.setAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST, inclusionList);
	}

	/**
	 * This method will delete map from session
	 */
	public void removeInclusionMapFromSession()
	{
		sessionService.removeAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST);
	}

	/**
	 * This method will return the map with configured values related to any particular API key passed as parameter e.g.
	 * API key passed as <b>integration.login.customer.account</b> configuration keys would be
	 * <b>integration.login.customer.account.connection.timeout.sga</b>
	 *
	 * @param apiKey
	 * @return configuration map
	 */
	public Map<String, String> getAPIConfiguration(final String apiKey)
	{
		final String currentSite = asahiSiteUtil.getCurrentSite().getUid();
		final Map<String, String> config = new HashMap<>();
		config.put(ApbintegrationConstants.URL,
				asahiConfigurationService.getString(apiKey + ".url." + currentSite, StringUtils.EMPTY));
		config.put(ApbintegrationConstants.CONNECTION_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.CONNECTION_REQUEST_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.request.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.CONNECTION_READ_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.read.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.REQUEST_CONTENT_TYPE, "json");
		config.putAll(addRestAPICommonConfig(currentSite));

		return config;
	}

	/**
	 * This method will add API common configurations in the map
	 *
	 * @param site
	 * @return map
	 */
	public Map<String, String> addRestAPICommonConfig(final String site)
	{
		final Map<String, String> config = new HashMap<>();
		config.put(ApbintegrationConstants.CLIENT_STORE_PASSWORD,
				asahiConfigurationService.getString("integration.certificate.clientStore.password." + site, "1649"));
		config.put(ApbintegrationConstants.TRUST_STORE_PASSWORD,
				asahiConfigurationService.getString("integration.certificate.trustStore.password." + site, "changeit"));
		config.put(ApbintegrationConstants.CLIENT_STORE_FILE,
				asahiConfigurationService.getString("integration.certificate.clientStore.filename." + site, "hybrisuser.p12"));
		config.put(ApbintegrationConstants.TRUST_STORE_FILE,
				asahiConfigurationService.getString("integration.certificate.trustStore.filename." + site, "keystore_Asahi.jks"));
		config.put(ApbintegrationConstants.CERTIFICATE_FILEPATH,
				asahiConfigurationService.getString("integration.certificate.filePath." + site, "C:/AsahiB2b"));
		return config;
	}

	/**
	 * This method sets the current user session value for credit block.
	 *
	 * @param b
	 */
	public void setSessionUserCreditBlock(final boolean b)
	{
		sessionService.setAttribute(ApbCoreConstants.IS_CUSTOMER_SESSION_CREDIT_BLOCK, b);
	}

	/**
	 * This method gets the credit block value of current customer
	 *
	 * @return boolean
	 */
	public boolean isSessionUserCreditBlock()
	{
		final Object obj = sessionService.getAttribute(ApbCoreConstants.IS_CUSTOMER_SESSION_CREDIT_BLOCK);
		boolean isCreditBlock = Boolean.FALSE;
		if (null != obj)
		{
			isCreditBlock = (boolean) obj;
		}

		return isCreditBlock;
	}

	/**
	 * Set product visibility flag in case of any error from ECC
	 *
	 * @param b
	 */
	public void setShowProductWithoutPrice(final boolean b)
	{
		sessionService.setAttribute(ApbCoreConstants.IS_SHOW_PRODUCT_WITHOUT_PRICE, b);
	}

	/**
	 * Allows customer to view product without price in case of ECC exception
	 *
	 * @return boolean value
	 */
	public boolean getShowProductWithoutPrice()
	{
		final Object obj = sessionService.getAttribute(ApbCoreConstants.IS_SHOW_PRODUCT_WITHOUT_PRICE);
		// Setting show product without price as true
		boolean showProduct = Boolean.TRUE;
		if (null != obj)
		{
			showProduct = (boolean) obj;
		}
		return showProduct;
	}

	/**
	 * Setting ApbCoreConstants.INCLUSION_CHECKOUT_FLAG when user clicks on proceed to checkout and ECC call is triggered
	 */

	public void setSessionCheckoutFlag(final boolean b)
	{
		sessionService.setAttribute(ApbCoreConstants.INCLUSION_CHECKOUT_FLAG, b);
	}

	/**
	 * Return the session checkout flag value. Value initializes when user comes to checkout page
	 *
	 * @return boolean value
	 */
	public boolean getSessionCheckoutFlag()
	{
		final Object sessionObject = sessionService.getAttribute(ApbCoreConstants.INCLUSION_CHECKOUT_FLAG);
		boolean checkoutFlag = Boolean.FALSE;
		if (null != sessionObject)
		{
			checkoutFlag = (boolean) sessionObject;
		}
		return checkoutFlag;
	}

	/**
	 * Method will remove checkout flag if user goes back to any other page without completing checkout.
	 */
	public void removeSessionCheckoutFlag()
	{
		sessionService.removeAttribute(ApbCoreConstants.INCLUSION_CHECKOUT_FLAG);
	}

	/**
	 * Setting ApbCoreConstants.PRODUCT_BLOCK_FLAG when users all products are excluded
	 *
	 * @param val
	 */
	public void setSessionProductBlock(final Boolean val)
	{
		sessionService.setAttribute(ApbCoreConstants.PRODUCT_BLOCK_FLAG, val);

	}

	/**
	 * Return the session product block flag value. Value initializes when users all products are excluded
	 *
	 * @return boolean value
	 */
	public boolean getSessionProductBlockFlag()
	{
		final Object sessionObject = sessionService.getAttribute(ApbCoreConstants.PRODUCT_BLOCK_FLAG);
		boolean productBlock = Boolean.FALSE;
		if (null != sessionObject)
		{
			productBlock = (boolean) sessionObject;
		}
		return productBlock;
	}

	/**
	 * Method will remove checkout flag if user goes back to any other page without completing checkout.
	 */
	public void removeSessionProductBlock()
	{
		sessionService.removeAttribute(ApbCoreConstants.PRODUCT_BLOCK_FLAG);
	}

	/**
	 * remove session attributes
	 */
	public void removeSessionAttributesBeforeCheckout()
	{
		sessionService.removeAttribute(ApbCoreConstants.IS_CUSTOMER_SESSION_CREDIT_BLOCK);
		//sessionService.removeAttribute(ApbCoreConstants.IS_SHOW_PRODUCT_WITHOUT_PRICE);
		sessionService.removeAttribute(ApbCoreConstants.INCLUSION_CHECKOUT_FLAG);
		sessionService.removeAttribute(ApbCoreConstants.PRODUCT_BLOCK_FLAG);

	}


	/**
	 * Returns true if non bonus product exists
	 *
	 * @param entries
	 * @return boolean
	 */
	public Boolean isNonBonusProductExist(final List<AbstractOrderEntryModel> entries)
	{
		if (CollectionUtils.isEmpty(entries))
		{
			return false;
		}
		final Optional<AbstractOrderEntryModel> orderEntry = entries.stream().filter(entry -> !entry.getIsBonusStock()).findFirst();
		return orderEntry.isPresent();
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	/**
	 * validates admin user access
	 *
	 * @return boolean
	 */
	public boolean adminRightExists()
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean adminRoleExists = Boolean.FALSE;
		if (null != auth)
		{
			final List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
			final GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_B2BADMINGROUP");

			if (updatedAuthorities.contains(adminAuthority))
			{
				adminRoleExists = Boolean.TRUE;
			}
		}
		return adminRoleExists;
	}

	/**
	 * Set's the header multi account link
	 *
	 * @param request
	 */
	public void setMultiAccountDisplayLink(final HttpServletRequest request)
	{
		final AsahiB2BUnitModel b2bUnit = apbB2BUnitService.getCurrentB2BUnit();

		request.getSession().setAttribute("defaultB2BUnitCode", null != b2bUnit ? b2bUnit.getAccountNum(): StringUtils.EMPTY);
		request.getSession().setAttribute("defaultB2BUnitName", null != b2bUnit ? b2bUnit.getLocName(): StringUtils.EMPTY);

		sessionService.setAttribute("payerAccountID",
				null != b2bUnit && b2bUnit.getPayerAccount() != null ? b2bUnit.getPayerAccount().getUid() : StringUtils.EMPTY);

		final String currentSite = asahiSiteUtil.getCurrentSite().getUid();
		final Map<String,List<AsahiB2BUnitModel>> siteUnitsMap = apbB2BUnitService.getUserActiveB2BUnits(userService.getCurrentUser().getUid());
		if (CollectionUtils.isNotEmpty(siteUnitsMap.get(currentSite)))
		{
			request.getSession().setAttribute("showUnitDetail", siteUnitsMap.get(currentSite).size() > 1);
		}
	}
	
	
	/**
	 * For C001,C00C,D00c payment terms of account the direct debit will be disabled
	 */
	public Boolean isDirectDebitEnabled()
	{
		AsahiB2BUnitModel payerAccount = null;
		final UserModel userModel = getUserService().getCurrentUser();
		final String disabledPaymentTerms = asahiConfigurationService.getConfiguration()
				.getString("direct.debit.diabled.payment.terms", "C001,C00C,D00C");
		if (userModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customerModel = ((B2BCustomerModel) userModel);

			final B2BUnitModel soldToAccount = customerModel.getDefaultB2BUnit();
			if (soldToAccount instanceof AsahiB2BUnitModel)
			{
				payerAccount = ((AsahiB2BUnitModel) soldToAccount).getPayerAccount();
			}

		}
		if (payerAccount != null)
		{
			final String[] disabledTerms = disabledPaymentTerms.split(",");
			for (final String disabledTerm : disabledTerms)
			{
				if (StringUtils.equalsIgnoreCase(payerAccount.getPaymentTerm(), disabledTerm.trim()))
				{
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * Add or remove user's SAM role in session dynamically
	 */
	public void addOrRemoveSAMAccess()
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final List<GrantedAuthority> currentAuthorities = new ArrayList<>(auth.getAuthorities());
		final List<HybrisEnumValue> roles = enumerationService.getEnumerationValues("UserAccessType");
		roles.stream().forEach(samRole -> {
			final GrantedAuthority samAccessAuthLevel = new SimpleGrantedAuthority("ROLE_" + samRole);
			currentAuthorities.remove(samAccessAuthLevel);
		});

		final String samAccess = getCurrentUserAccessType();
		if (StringUtils.isNotEmpty(samAccess))
		{
			final GrantedAuthority samAccessAuth = new SimpleGrantedAuthority("ROLE_" + samAccess);
			currentAuthorities.add(samAccessAuth);
			LOG.info("Adding sam access " + samAccess);
		}

		final Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(),
				currentAuthorities);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		LOG.info("Security context updated with new access : " + currentAuthorities);
	}

	/**
	 * @param userModel
	 * @return access type
	 */
	public String getCurrentUserAccessType()
	{
		String accessType = ApbCoreConstants.ORDER_ACCESS;

		final UserModel userModel = getUserService().getCurrentUser();

		if (userModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customerModel = ((B2BCustomerModel) userModel);
			if (CollectionUtils.isEmpty(customerModel.getSamAccess()))
			{
				return accessType;
			}
			final B2BUnitModel soldToAccount = customerModel.getDefaultB2BUnit();
			if (soldToAccount instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel payerAccount = ((AsahiB2BUnitModel) soldToAccount).getPayerAccount();

				if (null != payerAccount)
				{
					for (final AsahiSAMAccessModel samAccessModel : customerModel.getSamAccess())
					{
						if (null != samAccessModel.getPayer()
								&& payerAccount.getUid().equalsIgnoreCase(samAccessModel.getPayer().getUid())
								&& samAccessModel.isPayAccess() && samAccessModel.isOrderAccess())
						{
							accessType = ApbCoreConstants.PAY_AND_ORDER_ACCESS;
							break;
						}else if (null != samAccessModel.getPayer()
								&& payerAccount.getUid().equalsIgnoreCase(samAccessModel.getPayer().getUid())
								&& samAccessModel.isPayAccess())
						{
							accessType = ApbCoreConstants.PAY_ACCESS;
							break;
						}
					}
				}
			}
		}
		return accessType;
	}

	/**
	 * Fetch sam access status
	 * @return boolean
	 */
	public boolean isSAMAccessApprovalPending()
	{
		try
		{
			final UserModel userModel = getUserService().getCurrentUser();
			if(userModel instanceof B2BCustomerModel) {
				final B2BCustomerModel customerModel = (B2BCustomerModel) userModel;
   			final B2BUnitModel soldToAccount = customerModel.getDefaultB2BUnit();
   			if(null != soldToAccount && soldToAccount instanceof AsahiB2BUnitModel) {
   				final AsahiB2BUnitModel payerAccount = ((AsahiB2BUnitModel) soldToAccount).getPayerAccount();
   				for (final AsahiSAMAccessModel samAccessModel : customerModel.getSamAccess())
   				{
   					if (null != samAccessModel.getPayer() && null != payerAccount && payerAccount.getUid().equalsIgnoreCase(samAccessModel.getPayer().getUid()))
   					{
   						return samAccessModel.isPendingApproval();
   					}
   				}
   			}
			}
		}
		catch (final Exception ex)
		{
			LOG.error("Exception while getting user SAM access status : " + ex);
		}
		return Boolean.FALSE;
	}

	/**
	 * Fetch sam access status
	 * @return boolean
	 */
	public boolean isSAMAccessDenied()
	{
		try
		{
			final UserModel userModel = getUserService().getCurrentUser();
			if(userModel instanceof B2BCustomerModel) {
				final B2BCustomerModel customerModel = (B2BCustomerModel) userModel;
   			final B2BUnitModel soldToAccount = customerModel.getDefaultB2BUnit();
   			if(null != soldToAccount && soldToAccount instanceof AsahiB2BUnitModel) {
   				final AsahiB2BUnitModel payerAccount = ((AsahiB2BUnitModel) soldToAccount).getPayerAccount();
      			for (final AsahiSAMAccessModel samAccessModel : customerModel.getSamAccess())
      			{
      				if (null != samAccessModel.getPayer() && null != payerAccount && payerAccount.getUid().equalsIgnoreCase(samAccessModel.getPayer().getUid()))
      				{
      					return samAccessModel.isApprovalDenied();
      				}
      			}
   			}
   		}
		}
		catch (final Exception ex)
		{
			LOG.error("Exception while getting user SAM access denied status : " + ex);
		}
		return Boolean.FALSE;
	}
	/**
	 * @param val
	 */
	public void setSessionInvoicePaySelected(final Boolean val)
	{
		sessionService.setAttribute(ApbCoreConstants.INVOICE_PAY_SELECTED, val);

	}

	/**
	 * Method to check whether the pay selected attribtue is set in session or not.
	 *
	 * @return
	 */
	public boolean getSessionIsInvoicePaySelected()
	{
		final Object sessionObject = sessionService.getAttribute(ApbCoreConstants.INVOICE_PAY_SELECTED);
		boolean isPaySelected = Boolean.FALSE;
		if (null != sessionObject)
		{
			isPaySelected = (boolean) sessionObject;
		}
		return isPaySelected;
	}

	/**
	 * Remove the session invoice form
	 */
	public void removeSessionInvoicePaymentForm()
	{
		sessionService.removeAttribute(ApbCoreConstants.INVOICE_PAYMENT_FORM);

	}

	/**
	 * Remove the session isInvoicePaySelecte attribute
	 */
	public void remvoveSessionInvoicePaySelected()
	{
		sessionService.removeAttribute(ApbCoreConstants.INVOICE_PAY_SELECTED);
	}

	/**
	 * This Method will filter b2bunits based on current site.
	 *
	 * @param activeUnits
	 * @return
	 */
	public List<AsahiB2BUnitModel> getSiteBasedUnits(final List<AsahiB2BUnitModel> activeUnits)
	{
		List<AsahiB2BUnitModel> siteSpecificList = new ArrayList<>();
		if (asahiSiteUtil.isSga())
		{
			siteSpecificList = activeUnits.stream()
					.filter(unit -> (null != unit.getCompanyCode() && unit.getCompanyCode().equalsIgnoreCase("sga")))
					.collect(Collectors.toList());
		}
		else
		{
			siteSpecificList = activeUnits.stream()
					.filter(unit -> (null != unit.getCompanyCode() && unit.getCompanyCode().equalsIgnoreCase("apb")))
					.collect(Collectors.toList());
		}
		return siteSpecificList;
	}

	/**
	 * This Method will check if the cart has bonus entry or not.
	 *
	 * @param cartEntryDatas
	 * @return
	 */
	public boolean checkIfBonusEntryPresent(final List<OrderEntryData> cartEntryDatas)
	{

		final AtomicBoolean isBonusEntry = new AtomicBoolean(Boolean.FALSE);
		cartEntryDatas.stream().forEach(entry -> {
			if (null != entry.getIsBonusStock() && entry.getIsBonusStock())
			{
				isBonusEntry.set(Boolean.TRUE);
			}
		});
		return isBonusEntry.get();
	}

	/**
	 * Return current user's default b2bunit
	 * @return b2bunit
	 */
	public AsahiB2BUnitModel getDefaultB2BUnit() {
		final UserModel userModel = getUserService().getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customerModel = ((B2BCustomerModel) userModel);
			return (AsahiB2BUnitModel) customerModel.getDefaultB2BUnit();
		}
		return null;
	}

	/**
	 * validate if user can opt for pay access
	 * @return boolean
	 */
	public boolean validateForPayerAccess() {
		final AsahiB2BUnitModel defaultUnit = getDefaultB2BUnit();
		if(null != defaultUnit.getPayerAccount()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Gets the direct debit API configuration.
	 *
	 * @param apiKey the api key
	 * @return the direct debit API configuration
	 */
	public Map<String, String> getDirectDebitAPIConfiguration(
			final String apiKey) {
		final String currentSite = "sga";
		final Map<String, String> config = new HashMap<>();
		config.put(ApbintegrationConstants.URL,
				asahiConfigurationService.getString(apiKey + ".url." + currentSite, StringUtils.EMPTY));
		config.put(ApbintegrationConstants.CONNECTION_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.CONNECTION_REQUEST_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.request.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.CONNECTION_READ_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.read.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.REQUEST_CONTENT_TYPE, "json");
		config.putAll(addRestAPICommonConfig(currentSite));

		return config;
	}

	public B2BCustomerModel getCurrentB2BCustomer(){
	    if(getUserService().getCurrentUser() instanceof B2BCustomerModel){
	        return (B2BCustomerModel)getUserService().getCurrentUser();
        }
        return null;
    }

    public void updateB2BCustomer(final B2BCustomerModel customer){
	    modelService.save(customer);
    }

	 public boolean isNAPUserForSite()
	 {
		 if (asahiSiteUtil.isSga() || asahiSiteUtil.isCub())
		 {
			 return isNAPUser();
		 }
		 else
		 {
			 return false;
		 }
	 }

	 public boolean isNAPUser()
	 {
		 final UserModel userModel = getUserService().getCurrentUser();
		 if (userModel.getGroups().stream()
				 .filter(group -> StringUtils.equalsIgnoreCase(group.getUid(), B2B_NATIONAL_GROUP)).findFirst().isPresent())
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	 }

	 /**
		 * This method will store map In session
		 *
		 * @param inclusionList
		 */
		public boolean isCloseToCreditBlock(final Double percentageUsed)
		{
 			final Double creditblockthreshold = Double.valueOf(asahiConfigurationService.getInt("credit.close.block.start.percentage.sga", 80));

 			return creditblockthreshold.compareTo(percentageUsed)>0?false:true;
		}

	 /**
		 * This method will store map In session
		 *
		 * @param inclusionList
		 */
		public void setCreditInfoInSession(final Double creditLimit,final Double deltaToLimit,final Double percentageUsed,final boolean isCloseToCreditBlock)
		{

   			final Double onAccountDisabledthreshold = Double.valueOf(asahiConfigurationService.getInt("onaccount.disabled.percentage.sga", 100));

   			final boolean isOnAccountDisabled = onAccountDisabledthreshold.compareTo(percentageUsed)>0?false:true;

   			sessionService.setAttribute(ApbCoreConstants.IS_CLOSE_TO_CREDIT_BLCOK, isCloseToCreditBlock);
   			sessionService.setAttribute(ApbCoreConstants.IS_ON_ACCOUNT_DISABLED, isOnAccountDisabled);
   			sessionService.setAttribute(ApbCoreConstants.DELTA_TO_LIMIT, deltaToLimit);
   			sessionService.setAttribute(ApbCoreConstants.CREDIT_LIMIT, creditLimit);
		}

		 /**
		 * This method will store map In session
		 *
		 * @param inclusionList
		 */
		public void removeCreditInfoInSession()
		{

   			sessionService.removeAttribute(ApbCoreConstants.IS_CLOSE_TO_CREDIT_BLCOK);
   			sessionService.removeAttribute(ApbCoreConstants.IS_ON_ACCOUNT_DISABLED);
   			sessionService.removeAttribute(ApbCoreConstants.DELTA_TO_LIMIT);
   			sessionService.removeAttribute(ApbCoreConstants.CREDIT_LIMIT);
		}

	public String getAsahiDealTitle(final AsahiDealModel deal)
	{
		final String conditionProductCode = ((AsahiProductDealConditionModel) deal.getDealCondition()).getProductCode();
		final ApbProductModel conditionProduct = (ApbProductModel) productService.getProductForCodeSafe(conditionProductCode);
		if (StringUtils.isNotBlank(deal.getDealTitle()))
		{
			return (deal.getDealTitle());
		}
		else
		{
			final String benefitProductCode = ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getProductCode();
			final ApbProductModel benefitProduct = (ApbProductModel) productService.getProductForCodeSafe(benefitProductCode);
			final Integer conditionQty = ((AsahiProductDealConditionModel) deal.getDealCondition()).getQuantity();
			final Integer benefitQty = ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getQuantity();
			final String benefitProductPortalUnit = getPortalVolume(benefitProduct.getPortalUnitVolume());
			final String conditionProductPortalUnit = getPortalVolume(conditionProduct.getPortalUnitVolume());
			final String conditionProductBrandName = conditionProduct.getBrand() != null
					? (conditionProduct.getBrand().getName() != null ? conditionProduct.getBrand().getName() : "")
					: "";
			final String benefitProductBrandName = benefitProduct.getBrand() != null
					? (benefitProduct.getBrand().getName() != null ? benefitProduct.getBrand().getName() : "")
					: "";
			final StringBuilder dealTitle = new StringBuilder().append(BUY).append(conditionQty).append(" ")
					.append(CASES_OF).append(conditionProductPortalUnit).append(" ").append(conditionProductBrandName).append(" ")
					.append(conditionProduct.getName())
					.append(" and get ").append(benefitQty).append(" ")
					.append(CASES_OF).append(benefitProductPortalUnit).append(" ").append(benefitProductBrandName).append(" ")
					.append(benefitProduct.getName()).append(" ").append("free");
			return (dealTitle.toString());
		}
	}

	private String getPortalVolume(final UnitVolumeModel unitVolumeModel)
	{
		if (null != unitVolumeModel && StringUtils.isNotBlank(unitVolumeModel.getName()))
		{
			final List<String> volumeValues = Arrays.asList(unitVolumeModel.getName().trim().split(REGEX_SPACE));
			if (CollectionUtils.isNotEmpty(volumeValues))
			{
				return volumeValues.get(0);
			}
		}
		return "";
	}

	/**
	 * @param emailId
	 * @return
	 */
	public UserModel checkIfUserExists(final String emailId)
	{
		final UserModel user;
		try
		{
			user = customerAccountService.getUserByUid(emailId);
		}
		catch (final Exception ex)
		{
			return null;
		}
		return user;
	}

	/**
	 * @param newCustomer
	 * @param albStore
	 * @return
	 */
	public boolean checkIfUserHasMultipleUnits(final String email)
	{
		final UserModel newCustomer = checkIfUserExists(email);
		final String storeUid = cmsSiteService.getCurrentSite().getUid();
		int counter = 0;
		for (final PrincipalGroupModel group : newCustomer.getGroups())
		{
			if (group instanceof AsahiB2BUnitModel
					&& ((storeUid.equalsIgnoreCase(((AsahiB2BUnitModel) group).getCompanyCode()))
							|| (storeUid.equalsIgnoreCase(SabmCoreConstants.APB_STORE)
									&& ((AsahiB2BUnitModel) group).getUid().startsWith("5"))
							|| (storeUid.equalsIgnoreCase(SabmCoreConstants.ALB_STORE)
									&& ((AsahiB2BUnitModel) group).getUid().startsWith("01"))))
			{
				if (CollectionUtils.isEmpty(((AsahiB2BUnitModel) group).getDisabledUser())
						|| !((AsahiB2BUnitModel) group).getDisabledUser().contains(newCustomer.getUid()))
				{
					counter++;
				}
			}

		}
		if (counter > 1)
		{
			return true;
		}
		return false;
	}

	/**
	 * @param albB2BUnitModel
	 * @return
	 */
	public static String getAdminUsername(final AsahiB2BUnitModel b2bunit)
	{
		for (final UserModel user : CollectionUtils.emptyIfNull(b2bunit.getAdminUsers()))
		{
			if (StringUtils.isNotBlank(user.getName()) && !user.getUid().contains(SabmCoreConstants.ASAHIDIRECT)
					&& !user.getUid().contains(ASAHI_ACCOUNT))
			{
				return user.getName();
			}
		}
		return SabmCoreConstants.CUSTOMER_SUPPORT;
	}




}

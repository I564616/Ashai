/**
 *
 */
package com.sabmiller.facades.order.impl;

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.servicelayer.session.SessionService;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;

import com.google.common.collect.Lists;
import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.core.b2b.services.SabmB2BEmployeeService;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.cart.errors.exceptions.SalesOrderCreateException;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.cart.service.SabmCommerceCartService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.OrderMessageModel;
import com.sabmiller.core.order.SabmCommerceCheckoutService;
import com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService;
import com.sabmiller.facades.bdeordering.BDEOrderEmailForm;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;
import com.sabmiller.facades.order.CartStateException;
import com.sabmiller.facades.order.CutoffTimeoutException;
import com.sabmiller.facades.order.OrderMessageData;
import com.sabmiller.facades.order.SABMCheckoutFacade;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicBoolean;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.dto.CardInfo;
import com.apb.core.util.AsahiSiteUtil;



/**
 * DefaultSABMCheckoutFacade.
 *
 * @author yaopeng
 */
public class DefaultSABMCheckoutFacade extends DefaultAcceleratorCheckoutFacade implements SABMCheckoutFacade
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMCheckoutFacade.class.getName());

	/** The sabm sales order create service. */
	private SABMSalesOrderCreateService sabmSalesOrderCreateService;

	/** The commerce cart service. */
	private SabmCommerceCartService commerceCartService;

	/** The sabm delivery date cut off service. */
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;

	/** The cart service. */
	private SABMCartService cartService;

	/** The sabm commerce checkout service. */
	private SabmCommerceCheckoutService sabmCommerceCheckoutService;

	/** The system email service. */
	private SystemEmailService systemEmailService;

	/** The deals service. */
	@Resource(name = "dealsService")
	private DealsService dealsService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The configuration service. */
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;
	
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;
	
	@Resource(name = "sessionService")
	private SessionService sessionService;
	
	@Resource(name = "sabmB2BEmployeeService")
	private SabmB2BEmployeeService sabmB2BEmployeeService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/** The Constant ENABLE_CUTOFFTIME_CHECK. */
	private static final String ENABLE_CUTOFFTIME_CHECK = "cutofftime.verification.enabled";
	private DefaultCheckoutFacade defaultCheckoutFacade;


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#runOrderSimulate(boolean)
	 */
	@Override
	public void runOrderSimulate(final boolean forceRun) throws CalculationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cartService.getSessionCart());
		parameter.setRecalculate(forceRun);

		if (forceRun)
		{
			commerceCartService.recalculateCart(parameter);

		}
		else
		{
			parameter.setEnableHooks(true);
			commerceCartService.calculateCart(parameter);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#getSapCartChanges()
	 */
	@Override
	public List<OrderMessageData> getSapCartChanges()
	{
		if (cartService.hasSessionCart())
		{
			final List<OrderMessageModel> changes = commerceCartService.getSAPOrderSimulateChanges(cartService.getSessionCart());
			final List<OrderMessageData> resultChanges = Lists.newArrayListWithCapacity(changes.size());

			for (final OrderMessageModel model : changes)
			{
				final OrderMessageData data = new OrderMessageData();
				data.setCode(model.getCode());
				data.setArguments(model.getArugments());
				resultChanges.add(data);
			}
			return resultChanges;
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#hasSapCartChanges()
	 */
	@Override
	public boolean hasSapCartChanges()
	{
		return cartService.hasSessionCart() && commerceCartService.hasSAPOrderSimulateChanges(cartService.getSessionCart());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#prepareCartForCheckout()
	 */
	@Override
	public void prepareCartForCheckout() throws CutoffTimeoutException, CartStateException
	{
		super.prepareCartForCheckout();

		if(asahiSiteUtil.isCub())
		{
   		final CartModel cartModel = getCart();
   		validateCutoffForCheckout();
   
   		final CommerceCartParameter parameter = new CommerceCartParameter();
   		parameter.setCart(cartModel);
   
   		//run order simulate if necessary
   		final Calendar cal = Calendar.getInstance();
   		final Date now = cal.getTime();
   
   		cal.setTime(cartModel.getSalesOrderSimulateSyncDate());
   		cal.add(Calendar.MINUTE, Config.getInt("sales.order.simulate.expiry.mins", 0));
   		cal.add(Calendar.MINUTE, -Config.getInt("sabm.checkout.countdown", 5));
   		final Date orderSimulateExpiry = cal.getTime();
   
   		if (orderSimulateExpiry.after(now))
   		{
   			try
   			{
   				commerceCartService.recalculateCart(parameter);
   			}
   			catch (final CalculationException e)
   			{
   				throw new IllegalStateException("Error running order calculate", e);
   			}
   		}
   
   		if (commerceCartService.hasSAPOrderSimulateChanges(cartModel))
   		{
   			LOG.warn("Order simulate came back with changes, user must go back to cart page");
   			throw new CartStateException("Order simulate made changes to cart, user must be taken to cart page.");
   		}
   
   		//start checkout countdown
   		sabmCommerceCheckoutService.startCheckoutCountdown(cartModel);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#validateCartForCredictcardPayment()
	 */
	@Override
	public boolean validateCartForCredictcardPayment() throws CartStateException, IllegalStateException, CutoffTimeoutException
	{
		if (isCheckoutCountdownValid())
		{
			validateCutoffForCheckout();

			final CartModel cartModel = cartService.getSessionCart();
			sabmCommerceCheckoutService.clearPreviousPaymentAttempts(cartModel);

			//force run an order simulate
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setCart(cartModel);
			parameter.setRecalculate(true);

			try
			{
				commerceCartService.recalculateCart(parameter);
			}
			catch (final CalculationException e)
			{
				throw new IllegalStateException(e);
			}

			if (commerceCartService.hasSAPOrderSimulateChanges(parameter.getCart()))
			{
				throw new CartStateException("Order simulate came back with changes");
			}

			return true;
		}
		LOG.info("Checkout countdown expired");

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#validateCutoffForCheckout()
	 */
	@Override
	public void validateCutoffForCheckout() throws CutoffTimeoutException
	{
		final boolean isCutoffTimeCheckEnabled = configurationService.getConfiguration().getBoolean(ENABLE_CUTOFFTIME_CHECK, true);

		if (!isCutoffTimeCheckEnabled)
		{
			return;
		}

		if (!sabmDeliveryDateCutOffService.isValidDeliveryDate(getCart().getRequestedDeliveryDate()))
		{
			LOG.warn("Cutoff Time exceeded for cart. Marking cart for recalculation!");
			throw new CutoffTimeoutException("Cutoff exceeded");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#isCheckoutCountdownValid()
	 */
	@Override
	public boolean isCheckoutCountdownValid()
	{
		final CartModel cartModel = getCart();

		if (cartModel != null && cartModel.getCheckoutCountdown() != null)
		{
			final Date now = Calendar.getInstance().getTime();
			final boolean exceeded = now.after(cartModel.getCheckoutCountdown());
			if (!exceeded)
			{
				return true;
			}
			LOG.warn("Checkout timeout exceeded for cart [{}]", cartModel.getCode());
		}
		return false;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#getDeliveryAddress()
	 */
	@Override
	protected AddressData getDeliveryAddress()
	{
		if(asahiSiteUtil.isCub())
		{
   		final CartModel cart = getCart();
   		if (cart != null)
   		{
   			final AddressModel deliveryAddress = cart.getDeliveryAddress();
   			if (deliveryAddress != null)
   			{
   				return getAddressConverter().convert(deliveryAddress);
   			}
   		}
   		LOG.warn("Attribute AddressModel is null in CartModel");
   		return null;
		}
		else
		{
			return super.getDeliveryAddress();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#hasExceededWaitTimeout()
	 */
	@Override
	public boolean hasExceededWaitTimeout()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			final Calendar cal = Calendar.getInstance();
			final Date now = cal.getTime();
			cal.setTime(cartModel.getPaymentInfo().getCreationtime());
			cal.add(Calendar.MILLISECOND, Config.getInt("westpac.payment.wait.timeout", 120000));

			return now.after(cal.getTime());
		}
		return false;
	}

	/**
	 *
	 * @param cartModel
	 *           This Method is to remove the cart deal condition when the deal == null
	 *
	 */
	public void validateDealconditions(final AbstractOrderModel cartModel)
	{
		LOG.info("Validating Deals");
		try
		{
			if (cartModel.getComplexDealConditions() != null)
			{
				final List<CartDealConditionModel> toBeRemovecartDealConditions = new ArrayList<CartDealConditionModel>();
				final List<CartDealConditionModel> cartDealConditions = cartModel.getComplexDealConditions();
				for (final CartDealConditionModel cartDealCondition : cartDealConditions)
				{
					if (cartDealCondition != null && cartDealCondition.getDeal() == null)
					{
						toBeRemovecartDealConditions.add(cartDealCondition);
					}
				}
				if (!toBeRemovecartDealConditions.isEmpty())
				{
					//cartDealConditions.removeAll(toBeRemovecartDealConditions);
					//cartModel.setComplexDealConditions(cartDealConditions);
					//getModelService().save(cartModel);
					getModelService().removeAll(toBeRemovecartDealConditions);
					getModelService().refresh(cartModel);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("Deal Conditions {} are not valid in cart {}::", cartModel.getComplexDealConditions(), cartModel);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#placeOrder()
	 */
	@Override
	public OrderData placeOrder() throws InvalidCartException
	{
		if(asahiSiteUtil.isCub())
		{
   		final CartModel cartModel = getCart();
   
   		if (cartModel != null)
   		{
   			try
   			{
   
   				final UserModel currentUser = getCurrentUserForCheckout();
   				if (cartModel.getUser().equals(currentUser) || getCheckoutCustomerStrategy().isAnonymousCheckout())
   				{
   					//validateDealconditions(cartModel);
   					beforePlaceOrder(cartModel);
   
   					salesOrderCreate(cartModel);
   					//Fix for sap order number null while create order.
   					if (StringUtils.isNotBlank(cartModel.getSapSalesOrderNumber()))
   					{
   						LOG.info(this.getClass().getName() + "::placeOrder : Entered inside to create order in Hybris");
   						final OrderModel orderModel = placeOrder(cartModel);
   						orderModel.setDate(getStoreDate());
   						getModelService().save(orderModel);
                           afterPlaceOrder(cartModel, orderModel);
   						// Convert the order to an order data
   						if (orderModel != null)
   						{
   							return getOrderConverter().convert(orderModel);
   						}
   					}
   					else
   					{
   						LOG.error(this.getClass().getName() + ".placeOrder() : Sap Sales order number is Empty in Cart");
   
   					}
   				}
   			}
   			catch (final Exception e)
   			{
   				//only send email when CC payments
   				if (cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
   				{
   					final List<String> messages = Lists.newArrayList();
   					messages.add("Order place failed");
   					messages.add(" ");
   					messages.add("Customer: " + cartModel.getUser().getUid());
   					messages.add("Cart Number: " + cartModel.getCode());
   					messages.add("Payment Type: Credit card");
   					messages.add(
   							"Payment reference: " + ((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getPaymentReference());
   
   					systemEmailService.constructSystemEmail(Config.getString("sabm.email.order.from", ""),
   							Config.getString("sabm.email.order.to", ""), Config.getString("sabm.email.order.displayName", ""),
   							"Order failed for customer <" + cartModel.getUser().getUid() + "> but payment was captured.", messages,
   							null);
   
   				}
   				throw e;
   			}
   
   		}
   		return null;
		}
		else
		{
			return  super.placeOrder();
		}
	}

    protected void afterPlaceOrderForPostback(final CartModel cartModel, final OrderModel orderModel)
    {
        if (orderModel != null)
        {
            getModelService().remove(cartModel);
            getModelService().refresh(orderModel);
        }
    }


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#placeOrder()
	 */
	@Override
	public OrderData placeOrderForPostback(SABMMerchantSuiteTransactionProcessData data) throws InvalidCartException
	{
		if(StringUtils.isNotEmpty(data.getEmailAddress())){
		final UserModel user = userService.getUserForUID(data.getEmailAddress());
		final CartModel cartModel = commerceCartService.getCartForCodeAndUser(data.getReference2(), user);
		if (cartModel != null)
		{
			try
			{
				if (cartModel.getUser().equals(user))
				{
					final String sessionAttrUserId = Config.getString("session.attr.user.invoking.sap.service",
							"CURRENT_USER_SAP_INVOCATION");
					sessionService.setAttribute(sessionAttrUserId, user.getUid());
					//validateDealconditions(cartModel);
					beforePlaceOrder(cartModel);
					salesOrderCreate(cartModel);
					//Fix for sap order number null while create order.
					if (StringUtils.isNotBlank(cartModel.getSapSalesOrderNumber()))
					{
						LOG.info(this.getClass().getName() + ":: placeOrderForPostback : Webhook : Entered inside to create order in Hybris");
						final OrderModel orderModel = placeOrder(cartModel);
						afterPlaceOrderForPostback(cartModel, orderModel);
						// Convert the order to an order data
						if (orderModel != null)
						{
							return getOrderConverter().convert(orderModel);
						}
					}
					else
					{
						LOG.error(this.getClass().getName() + ":: placeOrderForPostback : Webhook : Sap Sales order number is Empty in Cart");

					}
				}
			}
			catch (final Exception e)
			{
				//only send email when CC payments
				if (cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
				{
					final List<String> messages = Lists.newArrayList();
					messages.add("Order place failed");
					messages.add(" ");
					messages.add("Customer: " + cartModel.getUser().getUid());
					messages.add("Cart Number: " + cartModel.getCode());
					messages.add("Payment Type: Credit card");
					messages.add(
							"Payment reference: " + ((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getPaymentReference());

					systemEmailService.constructSystemEmail(Config.getString("sabm.email.order.from", ""),
							Config.getString("sabm.email.order.to", ""), Config.getString("sabm.email.order.displayName", ""),
							"Order failed for customer <" + cartModel.getUser().getUid() + "> but payment was captured.", messages,
							null);

				}
				throw e;
			}

		}
		}
		return null;
	}


	/**
	 * Sales order create.
	 *
	 * @param cartModel
	 *           the cart model
	 * @throws InvalidCartException
	 *            the invalid cart exception
	 */
	private void salesOrderCreate(final CartModel cartModel) throws InvalidCartException
	{
		try
		{
			getSabmSalesOrderCreateService().createOrderInSAPForPostback(cartModel);
			if (BooleanUtils.toBoolean(cartModel.getOneOffDealApplied()))
			{
				dealsService.refreshOneOffDeals(b2bCommerceUnitService.getParentUnit());
			}
		}
		catch (final SalesOrderCreateException e)
		{
			LOG.error("SAP sales order create service issue", e);
			throw new InvalidCartException("SAP sales order create service issue", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#markCartForRecalculation()
	 */
	@Override
	public void markCartForRecalculation()
	{
		cartService.markCartForRecalculation();
	}

	/**
	 * Gets the sabm sales order create service.
	 *
	 * @return the sabm sales order create service
	 */
	public SABMSalesOrderCreateService getSabmSalesOrderCreateService()
	{
		return sabmSalesOrderCreateService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#setDeliveryAddress(java.lang.String, boolean)
	 */
	@Override
	public boolean setDeliveryAddress(final String addressId, final boolean defaultAddress)
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			//get AddressModel by addressId from the user selected address
			final AddressModel addressModel = getDeliveryAddressModelForCode(addressId);
			if (null == addressModel)
			{
				LOG.error("The addressId {} not found addressModel", addressId);
				return false;
			}
			//if defaultAddress is true then save defaultShipmentAddress to user
			if (defaultAddress)
			{
				final UserModel user = cartModel.getUser();
				if (null != user && user instanceof B2BCustomerModel)
				{
					final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
					sabmCommerceCheckoutService.updateDefaultAddress(addressModel, b2bCustomer);
				}
			}
			//save address to cart
			final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
			parameter.setCart(cartModel);
			parameter.setAddress(addressModel);
			return getCommerceCheckoutService().setDeliveryAddress(parameter);
		}
		return false;
	}

	/**
	 * Sets the sabm sales order create service.
	 *
	 * @param sabmSalesOrderCreateService
	 *           the new sabm sales order create service
	 */
	public void setSabmSalesOrderCreateService(final SABMSalesOrderCreateService sabmSalesOrderCreateService)
	{
		this.sabmSalesOrderCreateService = sabmSalesOrderCreateService;
	}

	/**
	 * Sets the cart service.
	 *
	 * @param cartService
	 *           the new cart service
	 */
	public void setCartService(final SABMCartService cartService)
	{
		super.setCartService(cartService);
		this.cartService = cartService;
	}

	/**
	 * Sets the commerce cart service.
	 *
	 * @param commerceCartService
	 *           the new commerce cart service
	 */
	public void setCommerceCartService(final SabmCommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	/**
	 * Sets the sabm delivery date cut off service.
	 *
	 * @param sabmDeliveryDateCutOffService
	 *           the new sabm delivery date cut off service
	 */
	public void setSabmDeliveryDateCutOffService(final SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService)
	{
		this.sabmDeliveryDateCutOffService = sabmDeliveryDateCutOffService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#setCommerceCheckoutService(de.hybris.platform.
	 * commerceservices.order.CommerceCheckoutService)
	 */
	@Override
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		//Required to make order place strategy
		super.setCommerceCheckoutService(commerceCheckoutService);
	}

	/**
	 * Sets the sabm commerce checkout service.
	 *
	 * @param sabmCommerceCheckoutService
	 *           the new sabm commerce checkout service
	 */
	public void setSabmCommerceCheckoutService(final SabmCommerceCheckoutService sabmCommerceCheckoutService)
	{
		this.sabmCommerceCheckoutService = sabmCommerceCheckoutService;
	}

	/**
	 * Sets the system email service.
	 *
	 * @param systemEmailService
	 *           the new system email service
	 */
	public void setSystemEmailService(final SystemEmailService systemEmailService)
	{
		this.systemEmailService = systemEmailService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMCheckoutFacade#saveBDEOrderingDetails()
	 */
	@Override
	public void saveBDEOrderingDetails(final BdeOrderDetailsForm form)
	{


		final CartModel cartModel = cartService.getSessionCart();

		String customerFirstName = null;
		final List<String> customerEmailIds = new ArrayList<>();
		for (final BDEOrderEmailForm customer : form.getCustomers())
		{
			customerEmailIds.add(customer.getEmail());
			customerFirstName = customer.getFirstName();
		}
		if(customerEmailIds.size() == 1 && customerFirstName != null && StringUtils.isNotEmpty(customerFirstName)) {
			
			cartModel.setBdeOrderCustomerFirstName(customerFirstName);					
		}

		final List<String> userEmailIds = new ArrayList<>();
		for (final BDEOrderEmailForm user : form.getUsers())
		{
			if (form.getUsers().size() == 1)
			{
				if(sabmB2BEmployeeService.searchBDEByUid(user.getEmail()) == null)
				{
					LOG.info("Email = " +user.getEmail());
					userEmailIds.add(user.getEmail());
				}
			}
			userEmailIds.add(user.getEmail());
		}
		cartModel.setBdeOrder(Boolean.TRUE);
		cartModel.setBdeOrderCustomerEmails(customerEmailIds);
		cartModel.setBdeOrderUserEmails(userEmailIds);
		cartModel.setBdeOrderEmailText(form.getEmailText());
		getModelService().save(cartModel);


	}
	
	private Date getStoreDate()
	{

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");


		TimeZone storeTimeZone = null;

		//Getting BaseStore timezone
		if (baseStore != null && baseStore.getTimeZone() != null)
		{
			storeTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
		}

		final Date now = new Date();
		final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();
		Date storeTime = null;
		if (storeTimeZone != null)
		{
			storeTime = new Date(now.getTime() - serverTimeZone.getOffset(now.getTime()) + storeTimeZone.getOffset(now.getTime()));
		}
		else
		{
			storeTime = now;
		}

		final Calendar storeDateTime = Calendar.getInstance();		
		storeDateTime.setTime(storeTime);

		return storeDateTime.getTime();

	}
	
	
	@Override
	public CCPaymentInfoData createPaymentSubscription(final CCPaymentInfoData paymentInfoData)
	{
		if(!asahiSiteUtil.isCub())
		{
		validateParameterNotNullStandardMessage("paymentInfoData", paymentInfoData);
		final AddressData billingAddressData = paymentInfoData.getBillingAddress();
		validateParameterNotNullStandardMessage("billingAddress", billingAddressData);

		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName(paymentInfoData.getAccountHolderName());
		cardInfo.setCardNumber(paymentInfoData.getCardNumber());
		final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(paymentInfoData.getCardType());
		cardInfo.setCardType(cardType == null ? null : cardType.getCode());
		cardInfo.setExpirationMonth(Integer.valueOf(paymentInfoData.getExpiryMonth()));
		cardInfo.setExpirationYear(Integer.valueOf(paymentInfoData.getExpiryYear()));
		cardInfo.setIssueNumber(paymentInfoData.getIssueNumber());
		cardInfo.setToken(paymentInfoData.getToken());

		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity(billingAddressData.getTown());
		billingInfo.setCountry(billingAddressData.getCountry() == null ? null : billingAddressData.getCountry().getIsocode());
		billingInfo.setFirstName(billingAddressData.getFirstName());
		billingInfo.setLastName(billingAddressData.getLastName());
		billingInfo.setEmail(billingAddressData.getEmail());
		billingInfo.setPhoneNumber(billingAddressData.getPhone());
		billingInfo.setPostalCode(billingAddressData.getPostalCode());
		billingInfo.setStreet1(billingAddressData.getLine1());
		billingInfo.setStreet2(billingAddressData.getLine2());
		final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService().createPaymentSubscription(
				getCurrentUserForCheckout(), cardInfo, billingInfo, billingAddressData.getTitleCode(), getPaymentProvider(),
				paymentInfoData.isSaved());
		return ccPaymentInfoModel == null ? null : getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
		}
		else
		{
			return super.createPaymentSubscription(paymentInfoData);
		}
	}

	@Override
	protected AddressModel createDeliveryAddressModel(final AddressData addressData, final CartModel cartModel)
	{
		if(!asahiSiteUtil.isCub())
		{
		final AddressModel addressModel = getModelService().create(AddressModel.class);
		addressModel.setOwner(cartModel);
		getAddressReversePopulator().populate(addressData, addressModel);
		addressModel.setAddressRecordid(addressData.getCode());
		return addressModel;
		}
		else
		{
			return super.createDeliveryAddressModel(addressData, cartModel);
		}
	}
	
	public static void validateParameterNotNullStandardMessage(final String parameter, final Object parameterValue)
	{
		validateParameterNotNull(parameterValue, "Parameter " + parameter + " can not be null");
	}
	
	public static void validateParameterNotNull(final Object parameter, final String nullMessage)
	{
		Preconditions.checkArgument(parameter != null, nullMessage);
	}
}

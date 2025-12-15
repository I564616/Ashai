/**
 *
 */
package com.sabmiller.core.salesordercreate.service.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.concurrent.Executors;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sabmiller.core.cart.errors.exceptions.SalesOrderCreateException;
import com.sabmiller.core.enums.B2BUnitStatus;
import com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.ordercreate.SalesOrderCreateRequestHandler;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.response.SalesOrderCreateResponse;


/**
 * The Class DefalutSABMSalesOrderCreateService.
 */
@Component
public class DefalutSABMSalesOrderCreateService implements SABMSalesOrderCreateService
{

	/** The sales order create rest handler. */
	private SalesOrderCreateRequestHandler salesOrderCreateRestHandler;

	/** The sales order create request converter. */
	private Converter<AbstractOrderModel, SalesOrderCreateRequest> salesOrderCreateRequestConverter;

	/** The model service. */
	private ModelService modelService;

	/** The ysdm request converter. */
	@Resource(name = "ysdmRequestConverter")
	private Converter<YSDMRequest, SalesOrderCreateRequest> ysdmRequestConverter;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "cartService")
    private CartService cartService;


	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefalutSABMSalesOrderCreateService.class.getName());


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService#createOrderInSAP(de.hybris.platform.core.
	 * model.order.AbstractOrderModel)
	 */
	@Override
	public void createOrderInSAP(final AbstractOrderModel cartModel) throws SalesOrderCreateException
	{

		try
		{
			LOG.info("Placing order [cart:{}] on SAP ", cartModel.getCode());

			SalesOrderCreateResponse response = null;
			if (Config.getBoolean("salesordercreate.service.call.enabled", true))
			{
				//Fix for duplicate order create in sap, which check for the session cart is already made a call.
				if (!cartService.getSessionCart().getCode().equals(sessionService.getAttribute("CartInSession")))
				{
					LOG.info(this.getClass().getName() +"::createOrderInSAP : Entered inside CartInSession If condition to create order in SAP");
					sessionService.setAttribute("CartInSession", cartService.getSessionCart().getCode());
					response = salesOrderCreateRestHandler.sendPostRequest(salesOrderCreateRequestConverter.convert(cartModel));
				}
			}
			else
			{ // mockup for local env.
				response = new SalesOrderCreateResponse();
				response.setSalesOrderNumber(cartModel.getCode());
			}
			//Check whether response and sap sales order number is not empty, if not cart model & b2bunit will update.
			updateSalesOrderNumber(cartModel, response);

		}
		catch (final SABMIntegrationException e)
		{
			sessionService.removeAttribute("CartInSession");
			throw new SalesOrderCreateException("SABMIntegrationException while calling sales order create", e);
		}
		catch (final Exception e)
		{
			sessionService.removeAttribute("CartInSession");
			throw new SalesOrderCreateException("Exception while calling sales order create", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService#createOrderInSAP(de.hybris.platform.core.
	 * model.order.AbstractOrderModel)
	 */
	@Override
	public void createOrderInSAPForPostback(final AbstractOrderModel cartModel) throws SalesOrderCreateException
	{

		try
		{
			LOG.info("Placing order [cart:{}] on SAP ", cartModel.getCode());

			SalesOrderCreateResponse response = null;
			//PRB0042256 - Fix for duplicate order create in sap, which check for the session cart is already made a call.
			LOG.info("cart code :" + cartModel.getCode() + "session code :" + sessionService.getAttribute("CartInSession"));
			if (!cartModel.getCode().equals(sessionService.getAttribute("CartInSession")))
			{
				if (Config.getBoolean("salesordercreate.service.call.enabled", true))
				{

					LOG.info(this.getClass().getName()
							+ "::createOrderInSAPForPostback : Entered inside CartInSession If condition to create order in SAP");
					sessionService.setAttribute("CartInSession", cartModel.getCode());
					response = salesOrderCreateRestHandler.sendPostRequest(salesOrderCreateRequestConverter.convert(cartModel));
				}
				else
				{ // mockup for local env.
					response = new SalesOrderCreateResponse();
					response.setSalesOrderNumber(cartModel.getCode());
				}
				//Check whether response and sap sales order number is not empty, if not cart model & b2bunit will update.
				updateSalesOrderNumber(cartModel, response);
			}

		}
		catch (final SABMIntegrationException e)
		{
			sessionService.removeAttribute("CartInSession");
			throw new SalesOrderCreateException("SABMIntegrationException while calling sales order create", e);
		}
		catch (final Exception e)
		{
			sessionService.removeAttribute("CartInSession");
			throw new SalesOrderCreateException("Exception while calling sales order create", e);
		}
	}



	/**
	 * Asynchronously invokes the SAP YSDM create order service.
	 *
	 * @param ysdmRequest
	 *           the ysdm request
	 */
	@Override
	public void createYSDMOrderInSAP(final YSDMRequest ysdmRequest)
	{
		final String userId = userService.getCurrentUser().getUid();
		Executors.newCachedThreadPool().execute(new Runnable()
		{
			@Override
			public void run()
			{
				onThreadExecution(userId);
				try
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("Sending YSDM request to SAP {} ", ReflectionToStringBuilder.toString(ysdmRequest));
					}
					salesOrderCreateRestHandler.sendPostRequest(ysdmRequestConverter.convert(ysdmRequest));
				}
				catch (final SABMIntegrationException e)
				{
					LOG.error("Exception occurred while creating YSDM Order ", e);
				}
			}
		});
	}

	/**
	 * On thread execution.
	 *
	 * @param userId
	 *           the user id
	 */
	private void onThreadExecution(final String userId)
	{
		Registry.activateMasterTenant();
		userService.setCurrentUser(userService.getUserForUID("integrationAdmin"));

		final String sessionAttrUserId = Config.getString("session.attr.user.invoking.sap.service", "CURRENT_USER_SAP_INVOCATION");
		sessionService.setAttribute(sessionAttrUserId, userId);
	}



	/**
	 * Update sales order number.
	 *
	 * Steps:: 1- check for response & sap sales order number is empty or null 2- If both are not empty or null, cart
	 * model & b2bunit model will be saved. 3-If both are empty , Exception will be thrown.
	 * @param cartModel
	 *           the cart model
	 * @param response
	 *           the response
	 * @throws SalesOrderCreateException
	 *            the sales order create exception
	 */
	private void updateSalesOrderNumber(final AbstractOrderModel cartModel, final SalesOrderCreateResponse response)
			throws SalesOrderCreateException
	{
		if (response != null && StringUtils.isNotBlank(response.getSalesOrderNumber()))
		{
			LOG.info(this.getClass().getName() +"::updateSalesOrderNumber : Entered inside to update Sap Sales OrderNumber ");
			cartModel.setSapSalesOrderNumber(response.getSalesOrderNumber());

			getModelService().save(cartModel);
			final B2BUnitModel b2bunit = cartModel.getUnit();
			if (!B2BUnitStatus.ORDER_PLACED.equals(b2bunit.getB2BUnitStatus()))
			{
				b2bunit.setB2BUnitStatus(B2BUnitStatus.ORDER_PLACED);
				getModelService().save(b2bunit);
			}
		}
		else
		{
			LOG.error("Sales order number not received after sales order create");
			throw new SalesOrderCreateException("Exception while calling sales order create");
		}

	}



	/**
	 * Gets the model service.
	 *
	 * @return the model service
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService
	 *           the new model service
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets the sales order create rest handler.
	 *
	 * @return the sales order create rest handler
	 */
	public SalesOrderCreateRequestHandler getSalesOrderCreateRestHandler()
	{
		return salesOrderCreateRestHandler;
	}

	/**
	 * Sets the sales order create rest handler.
	 *
	 * @param salesOrderCreateRestHandler
	 *           the new sales order create rest handler
	 */
	public void setSalesOrderCreateRestHandler(final SalesOrderCreateRequestHandler salesOrderCreateRestHandler)
	{
		this.salesOrderCreateRestHandler = salesOrderCreateRestHandler;
	}

	/**
	 * Gets the sales order create request converter.
	 *
	 * @return the sales order create request converter
	 */
	public Converter<AbstractOrderModel, SalesOrderCreateRequest> getSalesOrderCreateRequestConverter()
	{
		return salesOrderCreateRequestConverter;
	}

	/**
	 * Sets the sales order create request converter.
	 *
	 * @param salesOrderCreateRequestConverter
	 *           the sales order create request converter
	 */
	public void setSalesOrderCreateRequestConverter(
			final Converter<AbstractOrderModel, SalesOrderCreateRequest> salesOrderCreateRequestConverter)
	{
		this.salesOrderCreateRequestConverter = salesOrderCreateRequestConverter;
	}

	public CartService getCartService()
	{
	  return cartService;
	}

	public void setCartService(final CartService cartService)
	{
	  this.cartService = cartService;
	}



}

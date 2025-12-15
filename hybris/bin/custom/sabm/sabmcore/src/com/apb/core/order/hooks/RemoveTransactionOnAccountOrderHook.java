package com.apb.core.order.hooks;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import com.apb.core.util.AsahiSiteUtil;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

/**
 * Created by SiddharthaKLet on 4/17/2019.
 */
public class RemoveTransactionOnAccountOrderHook implements CommercePlaceOrderMethodHook {

    private static final Logger LOGGER = Logger.getLogger(RemoveTransactionOnAccountOrderHook.class);
    private CartService cartService;
    
    @Resource
 	 private AsahiSiteUtil asahiSiteUtil;

    @Override
    public void afterPlaceOrder(CommerceCheckoutParameter parameter, CommerceOrderResult orderModel) throws InvalidCartException {
        //no implementation
    }

    @Override
    public void beforePlaceOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {
   	 if(!asahiSiteUtil.isCub()) {
        CartModel cart = parameter.getCart();
        List<PaymentTransactionModel> paymentTransactions = cart.getPaymentTransactions();
        if(paymentTransactions.size() > 0 && !cart.getIsPrepaid()){
            LOGGER.info(String.format("Transaction model is going to be removed from cart %s as this is not prepaid order.", cart.getCode()));
            cart.setPaymentTransactions(new ArrayList<>());
            getCartService().setSessionCart(cart);
            parameter.setCart(cart);
        }
   	 }
    }

    @Override
    public void beforeSubmitOrder(CommerceCheckoutParameter parameter, CommerceOrderResult result) throws InvalidCartException {
        //no implementation
    }


    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

}
